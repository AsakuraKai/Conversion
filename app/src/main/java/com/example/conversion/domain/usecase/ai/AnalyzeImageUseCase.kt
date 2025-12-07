package com.example.conversion.domain.usecase.ai

import android.net.Uri
import com.example.conversion.domain.model.ImageLabel
import com.example.conversion.domain.repository.MLRepository
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for analyzing images using ML Kit Image Labeling.
 * 
 * Takes an image URI and returns detected labels with confidence scores.
 * Useful for understanding image content and generating intelligent suggestions.
 *
 * @property mlRepository Repository providing ML operations
 * @property dispatcher Coroutine dispatcher for background execution
 *
 * @author Kai (Backend/Core Features)
 * @since CHUNK 13 - AI-Powered Filename Suggestions
 */
class AnalyzeImageUseCase @Inject constructor(
    private val mlRepository: MLRepository,
    @com.example.conversion.di.IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<AnalyzeImageUseCase.Params, List<ImageLabel>>(dispatcher) {
    
    /**
     * Parameters for image analysis.
     *
     * @property imageUri URI of the image to analyze
     * @property confidenceThreshold Minimum confidence score for labels (default: 0.7)
     * @property maxResults Maximum number of labels to return (default: 10)
     */
    data class Params(
        val imageUri: Uri,
        val confidenceThreshold: Float = ImageLabel.DEFAULT_CONFIDENCE_THRESHOLD,
        val maxResults: Int = 10
    ) {
        init {
            require(confidenceThreshold in 0.0f..1.0f) {
                "Confidence threshold must be between 0.0 and 1.0"
            }
            require(maxResults > 0) {
                "Max results must be greater than 0"
            }
        }
    }
    
    /**
     * Executes image analysis.
     * 
     * @param params Analysis parameters
     * @return List of detected ImageLabels sorted by confidence (highest first)
     * @throws Exception if image analysis fails
     */
    override suspend fun execute(params: Params): List<ImageLabel> {
        return when (val result = mlRepository.analyzeImage(
            imageUri = params.imageUri,
            confidenceThreshold = params.confidenceThreshold,
            maxResults = params.maxResults
        )) {
            is com.example.conversion.domain.common.Result.Success -> {
                // Sort by confidence descending
                result.data.sortedByDescending { it.confidence }
            }
            is com.example.conversion.domain.common.Result.Error -> {
                throw result.exception
            }
            is com.example.conversion.domain.common.Result.Loading -> {
                throw IllegalStateException("Unexpected Loading state")
            }
        }
    }
}
