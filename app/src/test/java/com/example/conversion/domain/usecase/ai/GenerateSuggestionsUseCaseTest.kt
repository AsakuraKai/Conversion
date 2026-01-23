package com.example.conversion.domain.usecase.ai

import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.ImageLabel
import com.example.conversion.domain.repository.MLRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for GenerateSuggestionsUseCase.
 *
 * @author Kai (Backend/Core Features)
 * @since CHUNK 13 - AI-Powered Filename Suggestions
 */
class GenerateSuggestionsUseCaseTest {
    
    private lateinit var mlRepository: MLRepository
    private lateinit var useCase: GenerateSuggestionsUseCase
    
    @Before
    fun setup() {
        mlRepository = mockk()
        useCase = GenerateSuggestionsUseCase(mlRepository, Dispatchers.Unconfined)
    }
    
    @Test
    fun `invoke should return suggestions from repository`() = runTest {
        // Given
        val labels = listOf(
            ImageLabel("sunset", 0.9f),
            ImageLabel("beach", 0.85f)
        )
        val mockSuggestions = listOf("sunset_beach", "beach_sunset", "sunset")
        coEvery { 
            mlRepository.generateFilenameSuggestions(any(), any()) 
        } returns Result.Success(mockSuggestions)
        
        val params = GenerateSuggestionsUseCase.Params(
            labels = labels,
            maxSuggestions = 5
        )
        
        // When
        val result = useCase(params)
        
        // Then
        assertTrue(result is Result.Success)
        assertEquals(mockSuggestions, (result as Result.Success).data)
    }
    
    @Test
    fun `invoke should pass parameters to repository correctly`() = runTest {
        // Given
        val labels = listOf(ImageLabel("test", 0.8f))
        coEvery { 
            mlRepository.generateFilenameSuggestions(any(), any()) 
        } returns Result.Success(emptyList())
        
        val params = GenerateSuggestionsUseCase.Params(
            labels = labels,
            maxSuggestions = 3
        )
        
        // When
        useCase(params)
        
        // Then
        coVerify {
            mlRepository.generateFilenameSuggestions(
                labels = labels,
                maxSuggestions = 3
            )
        }
    }
    
    @Test
    fun `invoke should return error when repository fails`() = runTest {
        // Given
        val labels = listOf(ImageLabel("test", 0.8f))
        val exception = Exception("Generation failed")
        coEvery { 
            mlRepository.generateFilenameSuggestions(any(), any()) 
        } returns Result.Error(exception)
        
        val params = GenerateSuggestionsUseCase.Params(labels)
        
        // When
        val result = useCase(params)
        
        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception, (result as Result.Error).exception)
    }
    
    @Test
    fun `invoke should filter single-label suggestions when allowSingleLabel is false`() = runTest {
        // Given
        val labels = listOf(
            ImageLabel("sunset", 0.9f),
            ImageLabel("beach", 0.85f)
        )
        val mockSuggestions = listOf(
            "sunset_beach",  // Has underscore
            "sunset",        // Single label
            "beach_sunset",  // Has underscore
            "beach"          // Single label
        )
        coEvery { 
            mlRepository.generateFilenameSuggestions(any(), any()) 
        } returns Result.Success(mockSuggestions)
        
        val params = GenerateSuggestionsUseCase.Params(
            labels = labels,
            maxSuggestions = 10,
            allowSingleLabel = false
        )
        
        // When
        val result = useCase(params)
        
        // Then
        assertTrue(result is Result.Success)
        val suggestions = (result as Result.Success).data
        assertEquals(2, suggestions.size)
        assertTrue(suggestions.all { it.contains("_") })
    }
    
    @Test
    fun `invoke should limit results to maxSuggestions`() = runTest {
        // Given
        val labels = listOf(ImageLabel("test", 0.8f))
        val mockSuggestions = listOf("s1", "s2", "s3", "s4", "s5", "s6", "s7")
        coEvery { 
            mlRepository.generateFilenameSuggestions(any(), any()) 
        } returns Result.Success(mockSuggestions)
        
        val params = GenerateSuggestionsUseCase.Params(
            labels = labels,
            maxSuggestions = 3
        )
        
        // When
        val result = useCase(params)
        
        // Then
        assertTrue(result is Result.Success)
        val suggestions = (result as Result.Success).data
        assertTrue(suggestions.size <= 3)
    }
    
    @Test
    fun `invoke should use default maxSuggestions when not specified`() = runTest {
        // Given
        val labels = listOf(ImageLabel("test", 0.8f))
        coEvery { 
            mlRepository.generateFilenameSuggestions(any(), any()) 
        } returns Result.Success(emptyList())
        
        val params = GenerateSuggestionsUseCase.Params(labels = labels)
        
        // When
        useCase(params)
        
        // Then
        coVerify {
            mlRepository.generateFilenameSuggestions(
                labels = labels,
                maxSuggestions = GenerateSuggestionsUseCase.DEFAULT_MAX_SUGGESTIONS
            )
        }
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `Params should reject empty labels list`() {
        GenerateSuggestionsUseCase.Params(
            labels = emptyList()
        )
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `Params should reject maxSuggestions of 0 or less`() {
        GenerateSuggestionsUseCase.Params(
            labels = listOf(ImageLabel("test", 0.8f)),
            maxSuggestions = 0
        )
    }
    
    @Test
    fun `invoke should handle repository returning empty list`() = runTest {
        // Given
        val labels = listOf(ImageLabel("test", 0.8f))
        coEvery { 
            mlRepository.generateFilenameSuggestions(any(), any()) 
        } returns Result.Success(emptyList())
        
        val params = GenerateSuggestionsUseCase.Params(labels)
        
        // When
        val result = useCase(params)
        
        // Then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data.isEmpty())
    }
    
    @Test
    fun `invoke should allow single-label suggestions by default`() = runTest {
        // Given
        val labels = listOf(ImageLabel("sunset", 0.9f))
        val mockSuggestions = listOf("sunset", "sunset_beach")
        coEvery { 
            mlRepository.generateFilenameSuggestions(any(), any()) 
        } returns Result.Success(mockSuggestions)
        
        val params = GenerateSuggestionsUseCase.Params(labels = labels)
        
        // When
        val result = useCase(params)
        
        // Then
        assertTrue(result is Result.Success)
        val suggestions = (result as Result.Success).data
        assertEquals(2, suggestions.size)
    }
    
    @Test
    fun `invoke should handle single label input`() = runTest {
        // Given
        val labels = listOf(ImageLabel("sunset", 0.95f))
        val mockSuggestions = listOf("sunset")
        coEvery { 
            mlRepository.generateFilenameSuggestions(any(), any()) 
        } returns Result.Success(mockSuggestions)
        
        val params = GenerateSuggestionsUseCase.Params(labels)
        
        // When
        val result = useCase(params)
        
        // Then
        assertTrue(result is Result.Success)
        assertFalse((result as Result.Success).data.isEmpty())
    }
    
    @Test
    fun `invoke should respect allowSingleLabel parameter`() = runTest {
        // Given
        val labels = listOf(
            ImageLabel("sunset", 0.9f),
            ImageLabel("beach", 0.85f)
        )
        val mockSuggestions = listOf("sunset", "beach", "sunset_beach")
        coEvery { 
            mlRepository.generateFilenameSuggestions(any(), any()) 
        } returns Result.Success(mockSuggestions)
        
        // When allowSingleLabel = true
        val paramsWithSingle = GenerateSuggestionsUseCase.Params(
            labels = labels,
            allowSingleLabel = true
        )
        val resultWithSingle = useCase(paramsWithSingle)
        
        // When allowSingleLabel = false
        val paramsWithoutSingle = GenerateSuggestionsUseCase.Params(
            labels = labels,
            allowSingleLabel = false
        )
        val resultWithoutSingle = useCase(paramsWithoutSingle)
        
        // Then
        assertTrue(resultWithSingle is Result.Success)
        assertTrue(resultWithoutSingle is Result.Success)
        
        val withSingle = (resultWithSingle as Result.Success).data
        val withoutSingle = (resultWithoutSingle as Result.Success).data
        
        assertTrue(withSingle.size > withoutSingle.size)
        assertTrue(withoutSingle.all { it.contains("_") })
    }
    
    @Test
    fun `invoke should handle multiple labels efficiently`() = runTest {
        // Given
        val labels = List(10) { index ->
            ImageLabel("label$index", 0.9f - (index * 0.05f))
        }
        coEvery { 
            mlRepository.generateFilenameSuggestions(any(), any()) 
        } returns Result.Success(listOf("combined"))
        
        val params = GenerateSuggestionsUseCase.Params(labels)
        
        // When
        val result = useCase(params)
        
        // Then
        assertTrue(result is Result.Success)
    }
}
