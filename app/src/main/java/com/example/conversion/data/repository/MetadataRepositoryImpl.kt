package com.example.conversion.data.repository

import android.content.Context
import android.location.Location
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.ImageMetadata
import com.example.conversion.domain.repository.MetadataRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Implementation of MetadataRepository using Android ExifInterface API.
 * 
 * This implementation:
 * - Extracts EXIF metadata from image files
 * - Handles various image formats (JPEG, PNG, HEIF, WebP, DNG, CR2, NEF, etc.)
 * - Parses GPS coordinates, camera info, and technical photo data
 * - Handles missing or corrupted metadata gracefully
 * - Validates URIs and permissions before reading
 */
class MetadataRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : MetadataRepository {
    
    companion object {
        // Date format used in EXIF data
        private const val EXIF_DATE_FORMAT = "yyyy:MM:dd HH:mm:ss"
        
        // Supported image MIME types for metadata extraction
        private val SUPPORTED_MIME_TYPES = setOf(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/heif",
            "image/heic",
            "image/webp",
            "image/dng",
            "image/x-adobe-dng"
        )
    }
    
    /**
     * Extracts EXIF metadata from an image URI.
     * 
     * Process:
     * 1. Validate URI and check accessibility
     * 2. Open input stream from content resolver
     * 3. Create ExifInterface from input stream
     * 4. Extract all available EXIF tags
     * 5. Parse and format metadata values
     * 6. Return ImageMetadata object
     */
    override suspend fun extractMetadata(uri: Uri): Result<ImageMetadata> = withContext(Dispatchers.IO) {
        try {
            // Validate URI
            if (!isValidImageUri(uri)) {
                return@withContext Result.Error(
                    IllegalArgumentException("Invalid image URI or unsupported format")
                )
            }
            
            // Open input stream and create ExifInterface
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext Result.Error(
                    FileNotFoundException("Cannot open input stream for URI: $uri")
                )
            
            val exifInterface = try {
                ExifInterface(inputStream)
            } catch (e: IOException) {
                return@withContext Result.Error(
                    IOException("Failed to read EXIF data: ${e.message}", e)
                )
            } finally {
                inputStream.close()
            }
            
            // Extract metadata
            val metadata = extractAllMetadata(exifInterface)
            
            Result.Success(metadata)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Extracts metadata from multiple images.
     * Returns null for images that fail to extract.
     */
    override suspend fun extractMetadataForMultiple(uris: List<Uri>): Result<List<ImageMetadata?>> = 
        withContext(Dispatchers.IO) {
            try {
                val metadataList = uris.map { uri ->
                    when (val result = extractMetadata(uri)) {
                        is Result.Success -> result.data
                        else -> null
                    }
                }
                Result.Success(metadataList)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    
    /**
     * Checks if a file has EXIF metadata available.
     */
    override suspend fun hasMetadata(uri: Uri): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext Result.Success(false)
            
            val exifInterface = try {
                ExifInterface(inputStream)
            } catch (e: IOException) {
                return@withContext Result.Success(false)
            } finally {
                inputStream.close()
            }
            
            // Check if any meaningful EXIF data exists
            val hasData = exifInterface.getAttribute(ExifInterface.TAG_DATETIME) != null ||
                    exifInterface.getAttribute(ExifInterface.TAG_MAKE) != null ||
                    exifInterface.getAttribute(ExifInterface.TAG_MODEL) != null ||
                    exifInterface.latLong != null
            
            Result.Success(hasData)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Validates that a URI points to a valid image file.
     */
    override suspend fun validateImageUri(uri: Uri): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            Result.Success(isValidImageUri(uri))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Extracts all available EXIF metadata from ExifInterface.
     */
    private fun extractAllMetadata(exif: ExifInterface): ImageMetadata {
        return ImageMetadata(
            dateTaken = extractDateTaken(exif),
            location = extractLocation(exif),
            cameraModel = extractCameraModel(exif),
            dimensions = extractDimensions(exif),
            orientation = extractOrientation(exif),
            latitude = extractLatitude(exif),
            longitude = extractLongitude(exif),
            fNumber = extractFNumber(exif),
            exposureTime = extractExposureTime(exif),
            iso = extractISO(exif),
            focalLength = extractFocalLength(exif),
            flash = extractFlash(exif)
        )
    }
    
    /**
     * Extracts date/time when the photo was taken.
     */
    private fun extractDateTaken(exif: ExifInterface): Long? {
        val dateString = exif.getAttribute(ExifInterface.TAG_DATETIME)
            ?: exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
            ?: exif.getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED)
            ?: return null
        
        return try {
            val dateFormat = SimpleDateFormat(EXIF_DATE_FORMAT, Locale.US)
            dateFormat.parse(dateString)?.time
        } catch (e: ParseException) {
            null
        }
    }
    
    /**
     * Extracts GPS location as Location object.
     */
    private fun extractLocation(exif: ExifInterface): Location? {
        val latLong = exif.latLong ?: return null
        
        return Location("exif").apply {
            latitude = latLong[0]
            longitude = latLong[1]
            
            // Add altitude if available
            val altitude = exif.getAltitude(0.0)
            if (altitude != 0.0) {
                this.altitude = altitude
            }
        }
    }
    
    /**
     * Extracts camera make and model.
     */
    private fun extractCameraModel(exif: ExifInterface): String? {
        val make = exif.getAttribute(ExifInterface.TAG_MAKE)
        val model = exif.getAttribute(ExifInterface.TAG_MODEL)
        
        return when {
            make != null && model != null -> "$make $model"
            model != null -> model
            make != null -> make
            else -> null
        }
    }
    
    /**
     * Extracts image dimensions (width x height).
     */
    private fun extractDimensions(exif: ExifInterface): Pair<Int, Int>? {
        val width = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0)
        val height = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0)
        
        return if (width > 0 && height > 0) {
            Pair(width, height)
        } else {
            null
        }
    }
    
    /**
     * Extracts image orientation in degrees.
     */
    private fun extractOrientation(exif: ExifInterface): Int? {
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION, 
            ExifInterface.ORIENTATION_NORMAL
        )
        
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    }
    
    /**
     * Extracts GPS latitude.
     */
    private fun extractLatitude(exif: ExifInterface): Double? {
        return exif.latLong?.get(0)
    }
    
    /**
     * Extracts GPS longitude.
     */
    private fun extractLongitude(exif: ExifInterface): Double? {
        return exif.latLong?.get(1)
    }
    
    /**
     * Extracts f-number (aperture).
     */
    private fun extractFNumber(exif: ExifInterface): String? {
        val fNumber = exif.getAttributeDouble(ExifInterface.TAG_F_NUMBER, 0.0)
        return if (fNumber > 0.0) {
            "f/${String.format(Locale.US, "%.1f", fNumber)}"
        } else {
            null
        }
    }
    
    /**
     * Extracts exposure time.
     */
    private fun extractExposureTime(exif: ExifInterface): String? {
        val exposureTime = exif.getAttributeDouble(ExifInterface.TAG_EXPOSURE_TIME, 0.0)
        return if (exposureTime > 0.0) {
            if (exposureTime < 1.0) {
                // Format as fraction for fast shutter speeds
                val denominator = (1.0 / exposureTime).toInt()
                "1/$denominator"
            } else {
                // Format as decimal for slow shutter speeds
                String.format(Locale.US, "%.1fs", exposureTime)
            }
        } else {
            null
        }
    }
    
    /**
     * Extracts ISO speed rating.
     */
    private fun extractISO(exif: ExifInterface): String? {
        val iso = exif.getAttributeInt(ExifInterface.TAG_ISO_SPEED_RATINGS, 0)
        return if (iso > 0) {
            iso.toString()
        } else {
            null
        }
    }
    
    /**
     * Extracts focal length.
     */
    private fun extractFocalLength(exif: ExifInterface): String? {
        val focalLength = exif.getAttributeDouble(ExifInterface.TAG_FOCAL_LENGTH, 0.0)
        return if (focalLength > 0.0) {
            "${focalLength.toInt()}mm"
        } else {
            null
        }
    }
    
    /**
     * Extracts flash status.
     */
    private fun extractFlash(exif: ExifInterface): Boolean? {
        val flash = exif.getAttributeInt(ExifInterface.TAG_FLASH, -1)
        return if (flash >= 0) {
            // Flash fired if bit 0 is set
            (flash and 0x1) != 0
        } else {
            null
        }
    }
    
    /**
     * Validates if URI is a valid image URI.
     */
    private fun isValidImageUri(uri: Uri): Boolean {
        // Check URI scheme
        if (uri.scheme != "content" && uri.scheme != "file") {
            return false
        }
        
        // Check MIME type
        val mimeType = context.contentResolver.getType(uri)
        return mimeType != null && SUPPORTED_MIME_TYPES.contains(mimeType.lowercase(Locale.US))
    }
}
