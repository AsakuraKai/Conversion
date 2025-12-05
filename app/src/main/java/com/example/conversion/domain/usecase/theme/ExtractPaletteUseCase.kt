package com.example.conversion.domain.usecase.theme

import android.net.Uri
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.ImagePalette
import com.example.conversion.domain.repository.ThemeRepository
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for extracting color palette from an image.
 * 
 * This use case analyzes an image and extracts a color palette containing
 * dominant, vibrant, and muted colors suitable for dynamic theming.
 * 
 * Usage:
 * ```
 * val result = extractPaletteUseCase(imageUri)
 * when (result) {
 *     is Result.Success -> applyTheme(result.data)
 *     is Result.Error -> showError(result.exception.message)
 * }
 * ```
 */
class ExtractPaletteUseCase @Inject constructor(
    private val themeRepository: ThemeRepository,
    dispatcher: CoroutineDispatcher
) : BaseUseCase<Uri, ImagePalette>(dispatcher) {
    
    /**
     * Extracts color palette from the given image URI.
     * 
     * @param params The content URI of the image to analyze
     * @return ImagePalette containing extracted colors
     * @throws Exception if image cannot be loaded or analyzed
     */
    override suspend fun execute(params: Uri): ImagePalette {
        return when (val result = themeRepository.extractPalette(params)) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
            is Result.Loading -> throw IllegalStateException("Unexpected Loading state")
        }
    }
}
