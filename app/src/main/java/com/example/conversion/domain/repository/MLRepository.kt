package com.example.conversion.domain.repository

import android.net.Uri
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.ImageLabel

/**
 * Repository interface for Machine Learning operations.
 * 
 * Provides access to ML Kit and other AI services for image analysis
 * and intelligent filename suggestions.
 *
 * @author Kai (Backend/Core Features)
 * @since CHUNK 13 - AI-Powered Filename Suggestions
 */
interface MLRepository {
    
    /**
     * Analyzes an image and returns detected labels.
     * 
     * Uses ML Kit Image Labeling to identify objects, scenes, and activities
     * in the provided image. Results are filtered by the confidence threshold.
     *
     * @param imageUri URI of the image to analyze
     * @param confidenceThreshold Minimum confidence score for returned labels (default: 0.7)
     * @param maxResults Maximum number of labels to return (default: 10)
     * @return Result containing list of detected ImageLabels, or error if analysis fails
     */
    suspend fun analyzeImage(
        imageUri: Uri,
        confidenceThreshold: Float = ImageLabel.DEFAULT_CONFIDENCE_THRESHOLD,
        maxResults: Int = 10
    ): Result<List<ImageLabel>>
    
    /**
     * Generates intelligent filename suggestions based on image labels.
     * 
     * Combines detected labels to create meaningful, human-readable filenames.
     * Example: ["sunset", "beach", "ocean"] -> ["sunset_beach", "beach_sunset", "ocean_view"]
     *
     * @param labels List of detected image labels
     * @param maxSuggestions Maximum number of filename suggestions to generate (default: 5)
     * @return Result containing list of suggested filenames
     */
    suspend fun generateFilenameSuggestions(
        labels: List<ImageLabel>,
        maxSuggestions: Int = 5
    ): Result<List<String>>
    
    /**
     * Convenience method to analyze image and generate suggestions in one call.
     * 
     * @param imageUri URI of the image to analyze
     * @param confidenceThreshold Minimum confidence score for label detection
     * @param maxSuggestions Maximum number of filename suggestions
     * @return Result containing list of suggested filenames
     */
    suspend fun analyzeAndSuggest(
        imageUri: Uri,
        confidenceThreshold: Float = ImageLabel.DEFAULT_CONFIDENCE_THRESHOLD,
        maxSuggestions: Int = 5
    ): Result<List<String>> {
        return when (val labelsResult = analyzeImage(imageUri, confidenceThreshold)) {
            is Result.Success -> generateFilenameSuggestions(labelsResult.data, maxSuggestions)
            is Result.Error -> Result.Error(labelsResult.exception)
            is Result.Loading -> Result.Loading
        }
    }
}
