package com.example.conversion.domain.repository

import android.net.Uri
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.ImageMetadata

/**
 * Repository interface for extracting metadata from image files.
 * Abstracts EXIF data extraction and provides clean data access layer.
 */
interface MetadataRepository {
    /**
     * Extracts EXIF metadata from an image file.
     *
     * @param uri Content URI of the image file
     * @return Result containing ImageMetadata or error if extraction fails
     */
    suspend fun extractMetadata(uri: Uri): Result<ImageMetadata>

    /**
     * Extracts metadata from multiple image files.
     *
     * @param uris List of content URIs for image files
     * @return Result containing list of ImageMetadata (may include nulls for failed extractions)
     */
    suspend fun extractMetadataForMultiple(uris: List<Uri>): Result<List<ImageMetadata?>>

    /**
     * Checks if a file has EXIF metadata available.
     *
     * @param uri Content URI of the image file
     * @return Result containing true if metadata exists, false otherwise
     */
    suspend fun hasMetadata(uri: Uri): Result<Boolean>

    /**
     * Validates that a URI points to a valid image file with accessible EXIF data.
     *
     * @param uri Content URI to validate
     * @return Result containing true if valid, false otherwise
     */
    suspend fun validateImageUri(uri: Uri): Result<Boolean>
}
