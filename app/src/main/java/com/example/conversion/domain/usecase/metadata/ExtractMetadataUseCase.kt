package com.example.conversion.domain.usecase.metadata

import android.net.Uri
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.ImageMetadata
import com.example.conversion.domain.repository.MetadataRepository
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for extracting EXIF metadata from image files.
 * 
 * This use case analyzes an image file and extracts embedded metadata such as:
 * - Date and time the photo was taken
 * - GPS location coordinates
 * - Camera model and settings
 * - Image dimensions and orientation
 * - Technical photo data (aperture, exposure, ISO, focal length)
 * 
 * Usage:
 * ```
 * val result = extractMetadataUseCase(imageUri)
 * when (result) {
 *     is Result.Success -> {
 *         val metadata = result.data
 *         println("Taken on: ${metadata.dateTaken}")
 *         println("Camera: ${metadata.cameraModel}")
 *         println("Location: ${metadata.getFormattedLocation()}")
 *     }
 *     is Result.Error -> showError(result.exception.message)
 * }
 * ```
 */
class ExtractMetadataUseCase @Inject constructor(
    private val metadataRepository: MetadataRepository,
    dispatcher: CoroutineDispatcher
) : BaseUseCase<Uri, ImageMetadata>(dispatcher) {
    
    /**
     * Extracts EXIF metadata from the given image URI.
     * 
     * @param params The content URI of the image to analyze
     * @return ImageMetadata containing extracted EXIF data
     * @throws Exception if image cannot be loaded or EXIF data cannot be read
     */
    override suspend fun execute(params: Uri): ImageMetadata {
        return when (val result = metadataRepository.extractMetadata(params)) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
            is Result.Loading -> throw IllegalStateException("Unexpected Loading state")
        }
    }
}
