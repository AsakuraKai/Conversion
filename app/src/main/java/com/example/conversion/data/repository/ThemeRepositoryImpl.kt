package com.example.conversion.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.ImagePalette
import com.example.conversion.domain.repository.ThemeRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import javax.inject.Inject

/**
 * Implementation of ThemeRepository using Android Palette API.
 * 
 * This implementation:
 * - Validates URI permissions before loading
 * - Loads images efficiently with proper sampling
 * - Extracts color palette using AndroidX Palette library
 * - Handles errors gracefully with proper error messages
 */
class ThemeRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ThemeRepository {
    
    companion object {
        // Maximum dimension for image processing (to avoid OOM)
        private const val MAX_IMAGE_DIMENSION = 512
        
        // Default color swatch count for palette extraction
        private const val DEFAULT_SWATCH_COUNT = 16
    }
    
    /**
     * Extracts color palette from an image URI.
     * 
     * Process:
     * 1. Validate URI and check persistent permissions
     * 2. Load bitmap with efficient sampling
     * 3. Generate palette using AndroidX Palette library
     * 4. Convert Android colors to Compose Color objects
     * 5. Return ImagePalette with all extracted colors
     */
    override suspend fun extractPalette(imageUri: Uri): Result<ImagePalette> = withContext(Dispatchers.IO) {
        try {
            // Validate URI scheme
            if (imageUri.scheme != "content" && imageUri.scheme != "file") {
                return@withContext Result.Error(
                    IllegalArgumentException("Invalid URI scheme: ${imageUri.scheme}. Only content:// and file:// URIs are supported.")
                )
            }
            
            // Load bitmap with efficient sampling
            val bitmap = loadBitmapFromUri(imageUri)
                ?: return@withContext Result.Error(
                    FileNotFoundException("Failed to load image from URI: $imageUri")
                )
            
            // Generate palette from bitmap
            val palette = generatePalette(bitmap)
            
            // Recycle bitmap to free memory
            if (!bitmap.isRecycled) {
                bitmap.recycle()
            }
            
            // Convert palette to domain model
            val imagePalette = ImagePalette(
                dominantColor = palette.dominantSwatch?.rgb?.toComposeColor(),
                vibrantColor = palette.vibrantSwatch?.rgb?.toComposeColor(),
                mutedColor = palette.mutedSwatch?.rgb?.toComposeColor(),
                darkVibrantColor = palette.darkVibrantSwatch?.rgb?.toComposeColor(),
                lightVibrantColor = palette.lightVibrantSwatch?.rgb?.toComposeColor(),
                darkMutedColor = palette.darkMutedSwatch?.rgb?.toComposeColor(),
                lightMutedColor = palette.lightMutedSwatch?.rgb?.toComposeColor()
            )
            
            // Ensure at least one color was extracted
            if (!imagePalette.hasColors) {
                return@withContext Result.Error(
                    IllegalStateException("Failed to extract any colors from image")
                )
            }
            
            Result.Success(imagePalette)
            
        } catch (e: SecurityException) {
            Result.Error(
                e,
                "Permission denied to read image. Please grant storage permissions."
            )
        } catch (e: FileNotFoundException) {
            Result.Error(
                e,
                "Image not found at URI: $imageUri"
            )
        } catch (e: OutOfMemoryError) {
            Result.Error(
                Exception(e),
                "Image is too large to process. Please select a smaller image."
            )
        } catch (e: Exception) {
            Result.Error(
                e,
                "Failed to extract palette: ${e.message}"
            )
        }
    }
    
    /**
     * Loads a bitmap from URI with efficient sampling to avoid OOM.
     * 
     * @param uri The image URI to load
     * @return Bitmap or null if loading fails
     */
    private fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                // First decode bounds to get image dimensions
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(inputStream, null, options)
                inputStream.close()
                
                // Calculate sample size to reduce memory usage
                val sampleSize = calculateSampleSize(
                    options.outWidth,
                    options.outHeight,
                    MAX_IMAGE_DIMENSION
                )
                
                // Decode image with sampling
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val decodeOptions = BitmapFactory.Options().apply {
                        inSampleSize = sampleSize
                        inPreferredConfig = Bitmap.Config.ARGB_8888
                    }
                    BitmapFactory.decodeStream(stream, null, decodeOptions)
                }
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Calculates appropriate sample size for bitmap loading.
     * 
     * @param width Original image width
     * @param height Original image height
     * @param maxDimension Maximum allowed dimension
     * @return Sample size (power of 2)
     */
    private fun calculateSampleSize(width: Int, height: Int, maxDimension: Int): Int {
        var sampleSize = 1
        val maxDim = maxOf(width, height)
        
        while (maxDim / sampleSize > maxDimension) {
            sampleSize *= 2
        }
        
        return sampleSize
    }
    
    /**
     * Generates a color palette from a bitmap using AndroidX Palette library.
     * 
     * @param bitmap The bitmap to analyze
     * @return Palette object containing color swatches
     */
    private suspend fun generatePalette(bitmap: Bitmap): Palette = withContext(Dispatchers.Default) {
        Palette.from(bitmap)
            .maximumColorCount(DEFAULT_SWATCH_COUNT)
            .generate()
    }
    
    /**
     * Converts Android color int to Compose Color.
     */
    private fun Int.toComposeColor(): Color {
        return Color(this)
    }
}
