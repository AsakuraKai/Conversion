package com.example.conversion.domain.usecase.ocr

import android.net.Uri
import com.example.conversion.domain.model.ExtractedText
import com.example.conversion.domain.repository.OCRRepository
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for extracting text from images using OCR (Optical Character Recognition).
 * 
 * Takes an image URI and returns detected text blocks with confidence scores
 * and bounding box information. Useful for understanding text content in images
 * and generating intelligent filename suggestions based on document content.
 *
 * @property ocrRepository Repository providing OCR operations
 * @property dispatcher Coroutine dispatcher for background execution
 *
 * @author Kai (Backend/Core Features)
 * @since CHUNK 19 - OCR Integration
 */
class ExtractTextFromImageUseCase @Inject constructor(
    private val ocrRepository: OCRRepository,
    @com.example.conversion.di.IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<ExtractTextFromImageUseCase.Params, List<ExtractedText>>(dispatcher) {
    
    /**
     * Parameters for text extraction.
     *
     * @property imageUri URI of the image to analyze
     * @property confidenceThreshold Minimum confidence score for text blocks (default: 0.8)
     * @property combineText If true, returns single text block with combined text (default: false)
     */
    data class Params(
        val imageUri: Uri,
        val confidenceThreshold: Float = ExtractedText.DEFAULT_CONFIDENCE_THRESHOLD,
        val combineText: Boolean = false
    ) {
        init {
            require(confidenceThreshold in 0.0f..1.0f) {
                "Confidence threshold must be between 0.0 and 1.0"
            }
        }
    }
    
    /**
     * Executes text extraction from the image.
     *
     * @param params Parameters containing image URI and extraction settings
     * @return List of extracted text blocks with confidence and bounding boxes
     */
    override suspend fun execute(params: Params): List<ExtractedText> {
        if (params.combineText) {
            // Extract and combine into single text block
            val result = ocrRepository.extractCombinedText(
                imageUri = params.imageUri,
                confidenceThreshold = params.confidenceThreshold
            )
            return when (result) {
                is com.example.conversion.domain.common.Result.Success -> listOf(
                    ExtractedText(
                        text = result.data,
                        confidence = 1.0f, // Combined text uses max confidence
                        boundingBox = android.graphics.Rect(0, 0, 0, 0), // Full image
                        language = null
                    )
                )
                is com.example.conversion.domain.common.Result.Error -> throw result.exception
                is com.example.conversion.domain.common.Result.Loading -> throw IllegalStateException("Unexpected loading state")
            }
        } else {
            // Extract individual text blocks
            val result = ocrRepository.extractTextFromImage(
                imageUri = params.imageUri,
                confidenceThreshold = params.confidenceThreshold
            )
            return when (result) {
                is com.example.conversion.domain.common.Result.Success -> result.data
                is com.example.conversion.domain.common.Result.Error -> throw result.exception
                is com.example.conversion.domain.common.Result.Loading -> throw IllegalStateException("Unexpected loading state")
            }
        }
    }
}
