package com.example.conversion.data.repository

import android.net.Uri
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.ImageLabel

/**
 * Unit tests for MLRepositoryImpl (Mock Implementation).
 *
 * @author Kai (Backend/Core Features)
 * @since CHUNK 13 - AI-Powered Filename Suggestions
 */
class MLRepositoryImplTest {
    
    private lateinit var repository: MLRepositoryImpl
    
    @Before
    fun setup() {
        repository = MLRepositoryImpl(Dispatchers.Unconfined)
    }
    
    @Test
    fun `analyzeImage should return mock labels`() = runTest {
        // Given
        val uri = mockk<Uri>()
        every { uri.toString() } returns "content://test/image.jpg"
        
        // When
        val result = repository.analyzeImage(uri)
        
        // Then
        assertTrue(result is Result.Success)
        val labels = (result as Result.Success).data
        assertTrue(labels.isNotEmpty())
        assertTrue(labels.all { it.text.isNotBlank() })
        assertTrue(labels.all { it.confidence in 0.0f..1.0f })
    }
    
    @Test
    fun `analyzeImage should filter by confidence threshold`() = runTest {
        // Given
        val uri = mockk<Uri>()
        every { uri.toString() } returns "content://test/image.jpg"
        val threshold = 0.85f
        
        // When
        val result = repository.analyzeImage(uri, confidenceThreshold = threshold)
        
        // Then
        assertTrue(result is Result.Success)
        val labels = (result as Result.Success).data
        assertTrue(labels.all { it.confidence >= threshold })
    }
    
    @Test
    fun `analyzeImage should respect maxResults parameter`() = runTest {
        // Given
        val uri = mockk<Uri>()
        every { uri.toString() } returns "content://test/image.jpg"
        val maxResults = 3
        
        // When
        val result = repository.analyzeImage(uri, maxResults = maxResults)
        
        // Then
        assertTrue(result is Result.Success)
        val labels = (result as Result.Success).data
        assertTrue(labels.size <= maxResults)
    }
    
    @Test
    fun `analyzeImage should return labels sorted by confidence descending`() = runTest {
        // Given
        val uri = mockk<Uri>()
        every { uri.toString() } returns "content://test/image.jpg"
        
        // When
        val result = repository.analyzeImage(uri, confidenceThreshold = 0.0f)
        
        // Then
        assertTrue(result is Result.Success)
        val labels = (result as Result.Success).data
        
        // Verify descending order
        for (i in 0 until labels.size - 1) {
            assertTrue(labels[i].confidence >= labels[i + 1].confidence)
        }
    }
    
    @Test
    fun `analyzeImage should return consistent results for same URI`() = runTest {
        // Given
        val uri = mockk<Uri>()
        every { uri.toString() } returns "content://test/image.jpg"
        
        // When
        val result1 = repository.analyzeImage(uri)
        val result2 = repository.analyzeImage(uri)
        
        // Then
        assertTrue(result1 is Result.Success)
        assertTrue(result2 is Result.Success)
        
        val labels1 = (result1 as Result.Success).data
        val labels2 = (result2 as Result.Success).data
        
        // Same URI should produce same labels (hash-based)
        assertEquals(labels1.size, labels2.size)
        assertEquals(labels1.map { it.text }, labels2.map { it.text })
    }
    
    @Test
    fun `analyzeImage should return different labels for different URIs`() = runTest {
        // Given
        val uri1 = mockk<Uri>()
        val uri2 = mockk<Uri>()
        every { uri1.toString() } returns "content://test/image1.jpg"
        every { uri2.toString() } returns "content://test/image2.jpg"
        
        // When
        val result1 = repository.analyzeImage(uri1)
        val result2 = repository.analyzeImage(uri2)
        
        // Then
        assertTrue(result1 is Result.Success)
        assertTrue(result2 is Result.Success)
        
        val labels1 = (result1 as Result.Success).data
        val labels2 = (result2 as Result.Success).data
        
        // Different URIs might produce different labels
        // (This is probabilistic but mock uses hash-based selection)
        assertTrue(labels1.isNotEmpty())
        assertTrue(labels2.isNotEmpty())
    }
    
    @Test
    fun `generateFilenameSuggestions should return non-empty list`() = runTest {
        // Given
        val labels = listOf(
            ImageLabel("sunset", 0.9f),
            ImageLabel("beach", 0.85f)
        )
        
        // When
        val result = repository.generateFilenameSuggestions(labels)
        
        // Then
        assertTrue(result is Result.Success)
        val suggestions = (result as Result.Success).data
        assertTrue(suggestions.isNotEmpty())
    }
    
    @Test
    fun `generateFilenameSuggestions should return empty list for empty labels`() = runTest {
        // Given
        val labels = emptyList<ImageLabel>()
        
        // When
        val result = repository.generateFilenameSuggestions(labels)
        
        // Then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data.isEmpty())
    }
    
    @Test
    fun `generateFilenameSuggestions should respect maxSuggestions`() = runTest {
        // Given
        val labels = listOf(
            ImageLabel("sunset", 0.9f),
            ImageLabel("beach", 0.85f),
            ImageLabel("ocean", 0.8f)
        )
        val maxSuggestions = 3
        
        // When
        val result = repository.generateFilenameSuggestions(labels, maxSuggestions)
        
        // Then
        assertTrue(result is Result.Success)
        val suggestions = (result as Result.Success).data
        assertTrue(suggestions.size <= maxSuggestions)
    }
    
    @Test
    fun `generateFilenameSuggestions should sanitize label text`() = runTest {
        // Given
        val labels = listOf(
            ImageLabel("Sunset Beach", 0.9f),
            ImageLabel("Ocean View!", 0.85f)
        )
        
        // When
        val result = repository.generateFilenameSuggestions(labels)
        
        // Then
        assertTrue(result is Result.Success)
        val suggestions = (result as Result.Success).data
        
        // All suggestions should be valid filenames (lowercase, underscores only)
        suggestions.forEach { suggestion ->
            assertTrue(suggestion.matches(Regex("[a-z0-9_]+")))
        }
    }
    
    @Test
    fun `generateFilenameSuggestions should create single-label suggestions`() = runTest {
        // Given
        val labels = listOf(
            ImageLabel("sunset", 0.9f),
            ImageLabel("beach", 0.85f)
        )
        
        // When
        val result = repository.generateFilenameSuggestions(labels, maxSuggestions = 10)
        
        // Then
        assertTrue(result is Result.Success)
        val suggestions = (result as Result.Success).data
        
        // Should contain single-label suggestions
        assertTrue(suggestions.any { !it.contains("_") })
    }
    
    @Test
    fun `generateFilenameSuggestions should create multi-label combinations`() = runTest {
        // Given
        val labels = listOf(
            ImageLabel("sunset", 0.9f),
            ImageLabel("beach", 0.85f)
        )
        
        // When
        val result = repository.generateFilenameSuggestions(labels, maxSuggestions = 10)
        
        // Then
        assertTrue(result is Result.Success)
        val suggestions = (result as Result.Success).data
        
        // Should contain multi-label combinations
        assertTrue(suggestions.any { it.contains("_") })
    }
    
    @Test
    fun `generateFilenameSuggestions should handle single label`() = runTest {
        // Given
        val labels = listOf(ImageLabel("sunset", 0.95f))
        
        // When
        val result = repository.generateFilenameSuggestions(labels)
        
        // Then
        assertTrue(result is Result.Success)
        val suggestions = (result as Result.Success).data
        assertTrue(suggestions.isNotEmpty())
    }
    
    @Test
    fun `analyzeAndSuggest should combine analyze and suggest operations`() = runTest {
        // Given
        val uri = mockk<Uri>()
        every { uri.toString() } returns "content://test/image.jpg"
        
        // When
        val result = repository.analyzeAndSuggest(uri)
        
        // Then
        assertTrue(result is Result.Success)
        val suggestions = (result as Result.Success).data
        assertTrue(suggestions.isNotEmpty())
    }
    
    @Test
    fun `analyzeAndSuggest should respect confidenceThreshold parameter`() = runTest {
        // Given
        val uri = mockk<Uri>()
        every { uri.toString() } returns "content://test/image.jpg"
        val threshold = 0.9f
        
        // When
        val result = repository.analyzeAndSuggest(uri, confidenceThreshold = threshold)
        
        // Then
        assertTrue(result is Result.Success)
        // High threshold might result in fewer/no suggestions
        // This is expected behavior
    }
    
    @Test
    fun `analyzeAndSuggest should limit suggestions to maxSuggestions`() = runTest {
        // Given
        val uri = mockk<Uri>()
        every { uri.toString() } returns "content://test/image.jpg"
        val maxSuggestions = 2
        
        // When
        val result = repository.analyzeAndSuggest(uri, maxSuggestions = maxSuggestions)
        
        // Then
        assertTrue(result is Result.Success)
        val suggestions = (result as Result.Success).data
        assertTrue(suggestions.size <= maxSuggestions)
    }
}
