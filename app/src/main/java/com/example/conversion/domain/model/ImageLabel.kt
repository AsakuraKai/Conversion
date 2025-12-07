package com.example.conversion.domain.model

/**
 * Represents a label detected in an image by ML Kit or similar AI services.
 * 
 * This model encapsulates information about objects, scenes, or activities
 * identified in an image, along with confidence scores and categorization.
 *
 * @property text The label text (e.g., "sunset", "beach", "mountain")
 * @property confidence Confidence score ranging from 0.0 to 1.0
 * @property category Optional category for the label (e.g., "nature", "activity", "object")
 *
 * @author Kai (Backend/Core Features)
 * @since CHUNK 13 - AI-Powered Filename Suggestions
 */
data class ImageLabel(
    val text: String,
    val confidence: Float,
    val category: String? = null
) {
    init {
        require(text.isNotBlank()) { "Label text cannot be blank" }
        require(confidence in 0.0f..1.0f) { "Confidence must be between 0.0 and 1.0, got $confidence" }
    }
    
    /**
     * Checks if this label meets the minimum confidence threshold.
     * Default threshold is 0.7 (70% confidence).
     *
     * @param threshold Minimum confidence score (default: 0.7)
     * @return true if confidence meets or exceeds the threshold
     */
    fun meetsThreshold(threshold: Float = 0.7f): Boolean {
        return confidence >= threshold
    }
    
    /**
     * Returns a formatted string representation for display purposes.
     * Example: "Sunset (85%)"
     */
    fun toDisplayString(): String {
        val percentageConfidence = (confidence * 100).toInt()
        return "$text ($percentageConfidence%)"
    }
    
    companion object {
        /**
         * Default minimum confidence threshold for label filtering.
         */
        const val DEFAULT_CONFIDENCE_THRESHOLD = 0.7f
        
        /**
         * High confidence threshold for critical operations.
         */
        const val HIGH_CONFIDENCE_THRESHOLD = 0.85f
    }
}
