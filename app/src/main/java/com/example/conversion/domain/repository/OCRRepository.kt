package com.example.conversion.domain.repository

import android.net.Uri
import com.example.conversion.domain.model.ExtractedText

/**
 * Repository interface for OCR (Optical Character Recognition) operations.
 * 
 * Handles text extraction from images using ML Kit Text Recognition API.
 * Provides methods for analyzing images and extracting text blocks with
 * confidence scores and bounding boxes.
 *
 * @author Kai (Backend/Core Features)
 * @since CHUNK 19 - OCR Integration
 */
interface OCRRepository {
    
    /**
     * Extracts text from an image using OCR.
     * 
     * Analyzes the image at the given URI and returns all detected text blocks.
     * Each text block includes the recognized text, confidence score, and
     * bounding box coordinates.
     *
     * @param imageUri URI of the image to analyze
     * @param confidenceThreshold Minimum confidence score for text blocks (default: 0.8)
     * @return Result containing list of extracted text blocks, or error if recognition fails
     *
     * @throws IllegalArgumentException if imageUri is invalid
     * @throws SecurityException if app lacks permission to read the image
     */
    suspend fun extractTextFromImage(
        imageUri: Uri,
        confidenceThreshold: Float = ExtractedText.DEFAULT_CONFIDENCE_THRESHOLD
    ): Result<List<ExtractedText>>
    
    /**
     * Extracts text and combines it into a single string.
     * 
     * Convenience method that extracts all text blocks and joins them
     * into a single string, separated by spaces. Useful for quick text
     * extraction without needing detailed bounding box information.
     *
     * @param imageUri URI of the image to analyze
     * @param confidenceThreshold Minimum confidence score for text blocks (default: 0.8)
     * @return Result containing combined text string, or error if recognition fails
     */
    suspend fun extractCombinedText(
        imageUri: Uri,
        confidenceThreshold: Float = ExtractedText.DEFAULT_CONFIDENCE_THRESHOLD
    ): Result<String>
}
