package com.example.conversion.domain.repository

import android.net.Uri
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.ImagePalette

/**
 * Repository interface for theme-related operations.
 * Provides methods for extracting color palettes from images.
 */
interface ThemeRepository {
    
    /**
     * Extracts a color palette from an image URI.
     * 
     * The image is loaded and analyzed to extract dominant, vibrant, and muted colors
     * suitable for creating dynamic theme colors.
     * 
     * @param imageUri Content URI of the image to analyze
     * @return Result containing ImagePalette on success, or error on failure
     * 
     * Possible errors:
     * - Image not found or invalid URI
     * - Insufficient permissions to read the image
     * - Image format not supported
     * - Failed to extract colors from the image
     */
    suspend fun extractPalette(imageUri: Uri): Result<ImagePalette>
}
