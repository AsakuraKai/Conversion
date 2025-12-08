package com.example.conversion.data.repository

import android.graphics.Rect
import android.net.Uri
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.ExtractedText
import com.example.conversion.domain.repository.OCRRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock implementation of OCRRepository for development and testing.
 * 
 * **STRATEGIC IMPLEMENTATION:** This uses simulated OCR responses to unblock UI development
 * while real ML Kit Text Recognition integration is prepared. Provides realistic mock data
 * based on common document and image text patterns.
 * 
 * **Production Upgrade Required:** Replace with real ML Kit Text Recognition API integration.
 * 
 * See MOCK_IMPLEMENTATIONS.md for production implementation details.
 *
 * @property ioDispatcher Coroutine dispatcher for background operations
 *
 * @author Kai (Backend/Core Features)
 * @since CHUNK 19 - OCR Integration
 */
@Singleton
class OCRRepositoryImpl @Inject constructor(
    @com.example.conversion.di.IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : OCRRepository {
    
    /**
     * Mock text extraction returning simulated OCR results.
     * 
     * **MOCK BEHAVIOR:** Returns predefined text blocks based on URI hash to simulate
     * different document content. In production, this should use ML Kit Text Recognition.
     *
     * @param imageUri URI of the image to analyze
     * @param confidenceThreshold Minimum confidence score for text blocks
     * @return Result containing mock ExtractedText blocks
     */
    override suspend fun extractTextFromImage(
        imageUri: Uri,
        confidenceThreshold: Float
    ): com.example.conversion.domain.common.Result<List<ExtractedText>> = withContext(ioDispatcher) {
        try {
            // Simulate OCR processing delay (OCR is typically slower than image labeling)
            delay(800L)
            
            // Generate mock text blocks based on URI hash for consistent results
            val mockTextBlocks = generateMockTextBlocks(imageUri)
            
            // Filter by confidence threshold
            val filteredBlocks = mockTextBlocks
                .filter { it.confidence >= confidenceThreshold }
                .sortedByDescending { it.confidence }
            
            Result.Success(filteredBlocks)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Mock combined text extraction.
     * 
     * **MOCK BEHAVIOR:** Combines all extracted text blocks into a single string.
     * In production, ML Kit can return text in reading order.
     *
     * @param imageUri URI of the image to analyze
     * @param confidenceThreshold Minimum confidence score for text blocks
     * @return Result containing combined text string
     */
    override suspend fun extractCombinedText(
        imageUri: Uri,
        confidenceThreshold: Float
    ): com.example.conversion.domain.common.Result<String> = withContext(ioDispatcher) {
        when (val result = extractTextFromImage(imageUri, confidenceThreshold)) {
            is Result.Success -> {
                val combinedText = result.data
                    .joinToString(" ") { it.text }
                    .trim()
                Result.Success(combinedText)
            }
            is Result.Error -> Result.Error(result.exception)
            is Result.Loading -> Result.Error(IllegalStateException("Unexpected loading state"))
        }
    }
    
    /**
     * Generates mock text blocks based on URI characteristics.
     * Uses hash-based patterns to provide consistent results for testing.
     *
     * @param imageUri URI to generate mock data for
     * @return List of mock ExtractedText blocks
     */
    private fun generateMockTextBlocks(imageUri: Uri): List<ExtractedText> {
        // Use URI hash to deterministically select a pattern
        val hash = imageUri.toString().hashCode()
        val patternIndex = Math.abs(hash % 7)
        
        return when (patternIndex) {
            0 -> generateDocumentPattern()
            1 -> generateReceiptPattern()
            2 -> generateBusinessCardPattern()
            3 -> generateSignPattern()
            4 -> generateMenuPattern()
            5 -> generatePosterPattern()
            else -> generateMixedPattern()
        }
    }
    
    /**
     * Mock pattern: Formal document with title and body text.
     */
    private fun generateDocumentPattern(): List<ExtractedText> {
        return listOf(
            ExtractedText(
                text = "Annual Report 2024",
                confidence = 0.95f,
                boundingBox = Rect(100, 50, 900, 150),
                language = "en"
            ),
            ExtractedText(
                text = "Financial Summary",
                confidence = 0.92f,
                boundingBox = Rect(100, 200, 600, 250),
                language = "en"
            ),
            ExtractedText(
                text = "Q4 Results",
                confidence = 0.89f,
                boundingBox = Rect(100, 300, 400, 350),
                language = "en"
            ),
            ExtractedText(
                text = "December 2024",
                confidence = 0.88f,
                boundingBox = Rect(100, 400, 450, 450),
                language = "en"
            )
        )
    }
    
    /**
     * Mock pattern: Receipt with items and prices.
     */
    private fun generateReceiptPattern(): List<ExtractedText> {
        return listOf(
            ExtractedText(
                text = "Store Receipt",
                confidence = 0.93f,
                boundingBox = Rect(150, 100, 850, 180),
                language = "en"
            ),
            ExtractedText(
                text = "Coffee",
                confidence = 0.90f,
                boundingBox = Rect(100, 250, 400, 300),
                language = "en"
            ),
            ExtractedText(
                text = "\$4.50",
                confidence = 0.88f,
                boundingBox = Rect(700, 250, 900, 300),
                language = "en"
            ),
            ExtractedText(
                text = "Total",
                confidence = 0.91f,
                boundingBox = Rect(100, 500, 300, 550),
                language = "en"
            ),
            ExtractedText(
                text = "\$4.50",
                confidence = 0.89f,
                boundingBox = Rect(700, 500, 900, 550),
                language = "en"
            )
        )
    }
    
    /**
     * Mock pattern: Business card with name and contact info.
     */
    private fun generateBusinessCardPattern(): List<ExtractedText> {
        return listOf(
            ExtractedText(
                text = "John Smith",
                confidence = 0.94f,
                boundingBox = Rect(100, 150, 600, 220),
                language = "en"
            ),
            ExtractedText(
                text = "Software Engineer",
                confidence = 0.92f,
                boundingBox = Rect(100, 250, 700, 310),
                language = "en"
            ),
            ExtractedText(
                text = "john.smith@email.com",
                confidence = 0.87f,
                boundingBox = Rect(100, 400, 800, 450),
                language = "en"
            ),
            ExtractedText(
                text = "+1 555-0123",
                confidence = 0.85f,
                boundingBox = Rect(100, 500, 500, 550),
                language = "en"
            )
        )
    }
    
    /**
     * Mock pattern: Road sign or warning sign.
     */
    private fun generateSignPattern(): List<ExtractedText> {
        return listOf(
            ExtractedText(
                text = "STOP",
                confidence = 0.96f,
                boundingBox = Rect(300, 200, 700, 400),
                language = "en"
            ),
            ExtractedText(
                text = "ALL WAY",
                confidence = 0.90f,
                boundingBox = Rect(350, 450, 650, 520),
                language = "en"
            )
        )
    }
    
    /**
     * Mock pattern: Restaurant menu.
     */
    private fun generateMenuPattern(): List<ExtractedText> {
        return listOf(
            ExtractedText(
                text = "Menu",
                confidence = 0.94f,
                boundingBox = Rect(400, 50, 600, 120),
                language = "en"
            ),
            ExtractedText(
                text = "Appetizers",
                confidence = 0.91f,
                boundingBox = Rect(100, 200, 500, 260),
                language = "en"
            ),
            ExtractedText(
                text = "Salad",
                confidence = 0.89f,
                boundingBox = Rect(100, 300, 300, 350),
                language = "en"
            ),
            ExtractedText(
                text = "Soup",
                confidence = 0.88f,
                boundingBox = Rect(100, 380, 280, 430),
                language = "en"
            )
        )
    }
    
    /**
     * Mock pattern: Event poster or flyer.
     */
    private fun generatePosterPattern(): List<ExtractedText> {
        return listOf(
            ExtractedText(
                text = "SUMMER FESTIVAL",
                confidence = 0.95f,
                boundingBox = Rect(150, 100, 850, 200),
                language = "en"
            ),
            ExtractedText(
                text = "Live Music",
                confidence = 0.92f,
                boundingBox = Rect(200, 300, 600, 360),
                language = "en"
            ),
            ExtractedText(
                text = "Food Trucks",
                confidence = 0.90f,
                boundingBox = Rect(200, 400, 650, 460),
                language = "en"
            ),
            ExtractedText(
                text = "June 15-17",
                confidence = 0.88f,
                boundingBox = Rect(300, 550, 700, 610),
                language = "en"
            )
        )
    }
    
    /**
     * Mock pattern: Mixed text content (default fallback).
     */
    private fun generateMixedPattern(): List<ExtractedText> {
        return listOf(
            ExtractedText(
                text = "Important Notice",
                confidence = 0.93f,
                boundingBox = Rect(100, 100, 700, 180),
                language = "en"
            ),
            ExtractedText(
                text = "Please read carefully",
                confidence = 0.89f,
                boundingBox = Rect(100, 250, 800, 310),
                language = "en"
            ),
            ExtractedText(
                text = "Document ID 12345",
                confidence = 0.85f,
                boundingBox = Rect(100, 400, 650, 460),
                language = "en"
            )
        )
    }
}
