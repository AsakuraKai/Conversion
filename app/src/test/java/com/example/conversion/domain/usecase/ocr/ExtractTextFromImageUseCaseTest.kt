package com.example.conversion.domain.usecase.ocr

import android.graphics.Rect
import android.net.Uri
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.ExtractedText
import com.example.conversion.domain.repository.OCRRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for ExtractTextFromImageUseCase.
 *
 * @author Kai (Backend/Core Features)
 * @since CHUNK 19 - OCR Integration
 */
class ExtractTextFromImageUseCaseTest {
    
    private lateinit var ocrRepository: OCRRepository
    private lateinit var useCase: ExtractTextFromImageUseCase
    
    private val testUri = mockk<Uri>(relaxed = true)
    
    @Before
    fun setup() {
        ocrRepository = mockk()
        useCase = ExtractTextFromImageUseCase(ocrRepository, Dispatchers.Unconfined)
    }
    
    @Test
    fun `invoke should return text blocks from repository`() = runTest {
        // Given
        val textBlocks = listOf(
            ExtractedText("Hello World", 0.95f, Rect(0, 0, 100, 50)),
            ExtractedText("Test Document", 0.90f, Rect(0, 60, 120, 110))
        )
        coEvery { 
            ocrRepository.extractTextFromImage(any(), any()) 
        } returns Result.Success(textBlocks)
        
        val params = ExtractTextFromImageUseCase.Params(
            imageUri = testUri,
            confidenceThreshold = 0.8f
        )
        
        // When
        val result = useCase(params)
        
        // Then
        assertTrue(result is Result.Success)
        val extractedBlocks = (result as Result.Success).data
        assertEquals(2, extractedBlocks.size)
        assertEquals("Hello World", extractedBlocks[0].text)
        assertEquals("Test Document", extractedBlocks[1].text)
    }
    
    @Test
    fun `invoke should pass parameters to repository correctly`() = runTest {
        // Given
        coEvery { 
            ocrRepository.extractTextFromImage(any(), any()) 
        } returns Result.Success(emptyList())
        
        val params = ExtractTextFromImageUseCase.Params(
            imageUri = testUri,
            confidenceThreshold = 0.85f
        )
        
        // When
        useCase(params)
        
        // Then
        coVerify {
            ocrRepository.extractTextFromImage(
                imageUri = testUri,
                confidenceThreshold = 0.85f
            )
        }
    }
    
    @Test
    fun `invoke should return error when repository fails`() = runTest {
        // Given
        val exception = Exception("OCR failed")
        coEvery { 
            ocrRepository.extractTextFromImage(any(), any()) 
        } returns Result.Error(exception)
        
        val params = ExtractTextFromImageUseCase.Params(testUri)
        
        // When
        val result = useCase(params)
        
        // Then
        assertTrue(result is Result.Error)
        assertEquals("OCR failed", (result as Result.Error).exception.message)
    }
    
    @Test
    fun `invoke with combineText should return single text block`() = runTest {
        // Given
        val combinedText = "Hello World Test Document"
        coEvery { 
            ocrRepository.extractCombinedText(any(), any()) 
        } returns Result.Success(combinedText)
        
        val params = ExtractTextFromImageUseCase.Params(
            imageUri = testUri,
            confidenceThreshold = 0.8f,
            combineText = true
        )
        
        // When
        val result = useCase(params)
        
        // Then
        assertTrue(result is Result.Success)
        val extractedBlocks = (result as Result.Success).data
        assertEquals(1, extractedBlocks.size)
        assertEquals(combinedText, extractedBlocks[0].text)
        assertEquals(1.0f, extractedBlocks[0].confidence)
    }
    
    @Test
    fun `invoke with combineText should call extractCombinedText`() = runTest {
        // Given
        coEvery { 
            ocrRepository.extractCombinedText(any(), any()) 
        } returns Result.Success("Combined")
        
        val params = ExtractTextFromImageUseCase.Params(
            imageUri = testUri,
            combineText = true
        )
        
        // When
        useCase(params)
        
        // Then
        coVerify {
            ocrRepository.extractCombinedText(testUri, any())
        }
        coVerify(exactly = 0) {
            ocrRepository.extractTextFromImage(any(), any())
        }
    }
    
    @Test
    fun `invoke should use default confidence threshold`() = runTest {
        // Given
        coEvery { 
            ocrRepository.extractTextFromImage(any(), any()) 
        } returns Result.Success(emptyList())
        
        val params = ExtractTextFromImageUseCase.Params(testUri)
        
        // When
        useCase(params)
        
        // Then
        coVerify {
            ocrRepository.extractTextFromImage(
                testUri, 
                ExtractedText.DEFAULT_CONFIDENCE_THRESHOLD
            )
        }
    }
    
    @Test
    fun `Params should validate confidence threshold range`() {
        // When/Then - Valid thresholds
        ExtractTextFromImageUseCase.Params(testUri, 0.0f)
        ExtractTextFromImageUseCase.Params(testUri, 0.5f)
        ExtractTextFromImageUseCase.Params(testUri, 1.0f)
        
        // When/Then - Invalid threshold below 0
        try {
            ExtractTextFromImageUseCase.Params(testUri, -0.1f)
            assertTrue("Should throw exception for negative threshold", false)
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("between 0.0 and 1.0"))
        }
        
        // When/Then - Invalid threshold above 1
        try {
            ExtractTextFromImageUseCase.Params(testUri, 1.1f)
            assertTrue("Should throw exception for threshold > 1", false)
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("between 0.0 and 1.0"))
        }
    }
    
    @Test
    fun `invoke should handle empty text blocks gracefully`() = runTest {
        // Given
        coEvery { 
            ocrRepository.extractTextFromImage(any(), any()) 
        } returns Result.Success(emptyList())
        
        val params = ExtractTextFromImageUseCase.Params(testUri)
        
        // When
        val result = useCase(params)
        
        // Then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data.isEmpty())
    }
    
    @Test
    fun `invoke should preserve text block properties`() = runTest {
        // Given
        val boundingBox = Rect(10, 20, 200, 100)
        val textBlock = ExtractedText(
            text = "Test Text",
            confidence = 0.92f,
            boundingBox = boundingBox,
            language = "en"
        )
        coEvery { 
            ocrRepository.extractTextFromImage(any(), any()) 
        } returns Result.Success(listOf(textBlock))
        
        val params = ExtractTextFromImageUseCase.Params(testUri)
        
        // When
        val result = useCase(params)
        
        // Then
        assertTrue(result is Result.Success)
        val extractedBlock = (result as Result.Success).data.first()
        assertEquals("Test Text", extractedBlock.text)
        assertEquals(0.92f, extractedBlock.confidence)
        assertEquals(boundingBox, extractedBlock.boundingBox)
        assertEquals("en", extractedBlock.language)
    }
}
