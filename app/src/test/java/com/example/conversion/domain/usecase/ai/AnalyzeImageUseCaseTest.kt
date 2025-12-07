package com.example.conversion.domain.usecase.ai

import android.net.Uri
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.ImageLabel
import com.example.conversion.domain.repository.MLRepository
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
 * Unit tests for AnalyzeImageUseCase.
 *
 * @author Kai (Backend/Core Features)
 * @since CHUNK 13 - AI-Powered Filename Suggestions
 */
class AnalyzeImageUseCaseTest {
    
    private lateinit var mlRepository: MLRepository
    private lateinit var useCase: AnalyzeImageUseCase
    
    private val testUri = mockk<Uri>(relaxed = true)
    
    @Before
    fun setup() {
        mlRepository = mockk()
        useCase = AnalyzeImageUseCase(mlRepository, Dispatchers.Unconfined)
    }
    
    @Test
    fun `invoke should return sorted labels by confidence descending`() = runTest {
        // Given
        val labels = listOf(
            ImageLabel("beach", 0.75f),
            ImageLabel("sunset", 0.92f),
            ImageLabel("ocean", 0.88f)
        )
        coEvery { 
            mlRepository.analyzeImage(any(), any(), any()) 
        } returns Result.Success(labels)
        
        val params = AnalyzeImageUseCase.Params(
            imageUri = testUri,
            confidenceThreshold = 0.7f,
            maxResults = 10
        )
        
        // When
        val result = useCase(params)
        
        // Then
        assertTrue(result is Result.Success)
        val sortedLabels = (result as Result.Success).data
        assertEquals(3, sortedLabels.size)
        assertEquals("sunset", sortedLabels[0].text) // 0.92
        assertEquals("ocean", sortedLabels[1].text)  // 0.88
        assertEquals("beach", sortedLabels[2].text)  // 0.75
    }
    
    @Test
    fun `invoke should pass parameters to repository correctly`() = runTest {
        // Given
        coEvery { 
            mlRepository.analyzeImage(any(), any(), any()) 
        } returns Result.Success(emptyList())
        
        val params = AnalyzeImageUseCase.Params(
            imageUri = testUri,
            confidenceThreshold = 0.8f,
            maxResults = 5
        )
        
        // When
        useCase(params)
        
        // Then
        coVerify {
            mlRepository.analyzeImage(
                imageUri = testUri,
                confidenceThreshold = 0.8f,
                maxResults = 5
            )
        }
    }
    
    @Test
    fun `invoke should return error when repository fails`() = runTest {
        // Given
        val exception = Exception("ML Kit failed")
        coEvery { 
            mlRepository.analyzeImage(any(), any(), any()) 
        } returns Result.Error(exception)
        
        val params = AnalyzeImageUseCase.Params(testUri)
        
        // When
        val result = useCase(params)
        
        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception, (result as Result.Error).exception)
    }
    
    @Test
    fun `invoke should handle empty labels list`() = runTest {
        // Given
        coEvery { 
            mlRepository.analyzeImage(any(), any(), any()) 
        } returns Result.Success(emptyList())
        
        val params = AnalyzeImageUseCase.Params(testUri)
        
        // When
        val result = useCase(params)
        
        // Then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data.isEmpty())
    }
    
    @Test
    fun `invoke should use default values when not specified`() = runTest {
        // Given
        coEvery { 
            mlRepository.analyzeImage(any(), any(), any()) 
        } returns Result.Success(emptyList())
        
        val params = AnalyzeImageUseCase.Params(imageUri = testUri)
        
        // When
        useCase(params)
        
        // Then
        coVerify {
            mlRepository.analyzeImage(
                imageUri = testUri,
                confidenceThreshold = ImageLabel.DEFAULT_CONFIDENCE_THRESHOLD,
                maxResults = 10
            )
        }
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `Params should reject confidence threshold below 0`() {
        AnalyzeImageUseCase.Params(
            imageUri = testUri,
            confidenceThreshold = -0.1f
        )
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `Params should reject confidence threshold above 1`() {
        AnalyzeImageUseCase.Params(
            imageUri = testUri,
            confidenceThreshold = 1.1f
        )
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `Params should reject maxResults of 0 or less`() {
        AnalyzeImageUseCase.Params(
            imageUri = testUri,
            maxResults = 0
        )
    }
    
    @Test
    fun `invoke should maintain label order when all have same confidence`() = runTest {
        // Given
        val labels = listOf(
            ImageLabel("label1", 0.8f),
            ImageLabel("label2", 0.8f),
            ImageLabel("label3", 0.8f)
        )
        coEvery { 
            mlRepository.analyzeImage(any(), any(), any()) 
        } returns Result.Success(labels)
        
        val params = AnalyzeImageUseCase.Params(testUri)
        
        // When
        val result = useCase(params)
        
        // Then
        assertTrue(result is Result.Success)
        assertEquals(3, (result as Result.Success).data.size)
    }
    
    @Test
    fun `invoke should handle single label correctly`() = runTest {
        // Given
        val labels = listOf(ImageLabel("sunset", 0.95f))
        coEvery { 
            mlRepository.analyzeImage(any(), any(), any()) 
        } returns Result.Success(labels)
        
        val params = AnalyzeImageUseCase.Params(testUri)
        
        // When
        val result = useCase(params)
        
        // Then
        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(1, data.size)
        assertEquals("sunset", data[0].text)
        assertEquals(0.95f, data[0].confidence)
    }
    
    @Test
    fun `invoke should sort labels with very close confidence values`() = runTest {
        // Given
        val labels = listOf(
            ImageLabel("label1", 0.8501f),
            ImageLabel("label2", 0.8500f),
            ImageLabel("label3", 0.8502f)
        )
        coEvery { 
            mlRepository.analyzeImage(any(), any(), any()) 
        } returns Result.Success(labels)
        
        val params = AnalyzeImageUseCase.Params(testUri)
        
        // When
        val result = useCase(params)
        
        // Then
        assertTrue(result is Result.Success)
        val sorted = (result as Result.Success).data
        assertTrue(sorted[0].confidence >= sorted[1].confidence)
        assertTrue(sorted[1].confidence >= sorted[2].confidence)
    }
}
