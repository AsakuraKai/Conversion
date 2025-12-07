package com.example.conversion.domain.model

import android.graphics.Rect

/**
 * Represents text extracted from an image using OCR (Optical Character Recognition).
 * 
 * This model encapsulates information about text blocks detected in an image,
 * along with confidence scores and bounding box coordinates.
 *
 * @property text The extracted text content
 * @property confidence Confidence score ranging from 0.0 to 1.0
 * @property boundingBox Rectangle defining the position of the text in the image
 * @property language Optional detected language code (e.g., "en", "es", "fr")
 *
 * @author Kai (Backend/Core Features)
 * @since CHUNK 19 - OCR Integration
 */
data class ExtractedText(
    val text: String,
    val confidence: Float,
    val boundingBox: Rect,
    val language: String? = null
) {
    init {
        require(text.isNotBlank()) { "Extracted text cannot be blank" }
        require(confidence in 0.0f..1.0f) { "Confidence must be between 0.0 and 1.0, got $confidence" }
    }
    
    /**
     * Checks if this text block meets the minimum confidence threshold.
     * Default threshold is 0.8 (80% confidence) - higher than image labeling
     * due to OCR typically requiring higher accuracy for filename generation.
     *
     * @param threshold Minimum confidence score (default: 0.8)
     * @return true if confidence meets or exceeds the threshold
     */
    fun meetsThreshold(threshold: Float = DEFAULT_CONFIDENCE_THRESHOLD): Boolean {
        return confidence >= threshold
    }
    
    /**
     * Returns a formatted string representation for display purposes.
     * Example: "Hello World (92%)"
     */
    fun toDisplayString(): String {
        val percentageConfidence = (confidence * 100).toInt()
        return "$text ($percentageConfidence%)"
    }
    
    /**
     * Sanitizes the extracted text for use in filenames.
     * Removes illegal characters, replaces whitespace with underscores,
     * and limits length to a reasonable size.
     *
     * @param maxLength Maximum length for the sanitized text (default: 50)
     * @return Sanitized text suitable for filename use
     */
    fun toFilenameFragment(maxLength: Int = 50): String {
        return text
            .trim()
            .replace(Regex("[^a-zA-Z0-9\\s-]"), "") // Remove illegal characters
            .replace(Regex("\\s+"), "_") // Replace whitespace with underscores
            .take(maxLength) // Limit length
            .trim('_') // Remove leading/trailing underscores
    }
    
    companion object {
        /**
         * Default minimum confidence threshold for OCR text filtering.
         * Higher than image labeling (0.8 vs 0.7) due to accuracy requirements.
         */
        const val DEFAULT_CONFIDENCE_THRESHOLD = 0.8f
        
        /**
         * Minimum confidence threshold for high-accuracy scenarios.
         * Use when filename generation requires very reliable text.
         */
        const val HIGH_CONFIDENCE_THRESHOLD = 0.9f
    }
}
