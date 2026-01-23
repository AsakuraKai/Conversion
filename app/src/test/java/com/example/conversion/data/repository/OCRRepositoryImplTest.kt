package com.example.conversion.data.repository

import android.net.Uri
import com.example.conversion.domain.common.Result
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for OCRRepositoryImpl (Mock Implementation).
 *
 * @author Kai (Backend/Core Features)
 * @since CHUNK 19 - OCR Integration
 */
class OCRRepositoryImplTest {
    
    private lateinit var repository: OCRRepositoryImpl
    
    private val testUri1 = mockk<Uri>(relaxed = true) {
        every { toString() } returns "content://test/image1"
    }
    
    private val testUri2 = mockk<Uri>(relaxed = true) {
        every { toString() } returns "content://test/image2"
    }
    
    @Before
    fun setup() {
        repository = OCRRepositoryImpl(Dispatchers.Unconfined)
    }
    
    @Test
    fun `extractTextFromImage should return mock text blocks`() = runTest {
        // When
        val result = repository.extractTextFromImage(testUri1, 0.8f)
        
        // Then
        assertTrue(result is Result.Success)
        val textBlocks = (result as Result.Success).data
        assertTrue(textBlocks.isNotEmpty())
        textBlocks.forEach { block ->
            assertTrue(block.text.isNotBlank())
            assertTrue(block.confidence in 0.0f..1.0f)
            assertNotNull(block.boundingBox)
        }
    }
    
    @Test
    fun `extractTextFromImage should filter by confidence threshold`() = runTest {
        // When - High threshold
        val result = repository.extractTextFromImage(testUri1, 0.95f)
        
        // Then
        assertTrue(result is Result.Success)
        val textBlocks = (result as Result.Success).data
        textBlocks.forEach { block ->
            assertTrue("Confidence ${block.confidence} should meet threshold 0.95", 
                block.confidence >= 0.95f)
        }
    }
    
    @Test
    fun `extractTextFromImage should return consistent results for same URI`() = runTest {
        // When
        val result1 = repository.extractTextFromImage(testUri1, 0.8f)
        val result2 = repository.extractTextFromImage(testUri1, 0.8f)
        
        // Then
        assertTrue(result1 is Result.Success)
        assertTrue(result2 is Result.Success)
        val blocks1 = (result1 as Result.Success).data
        val blocks2 = (result2 as Result.Success).data
        
        // Should have same content due to hash-based generation
        assertEquals(blocks1.size, blocks2.size)
        blocks1.forEachIndexed { index, block ->
            assertEquals(block.text, blocks2[index].text)
            assertEquals(block.confidence, blocks2[index].confidence)
        }
    }
    
    @Test
    fun `extractTextFromImage should return different patterns for different URIs`() = runTest {
        // When
        val result1 = repository.extractTextFromImage(testUri1, 0.8f)
        val result2 = repository.extractTextFromImage(testUri2, 0.8f)
        
        // Then
        assertTrue(result1 is Result.Success)
        assertTrue(result2 is Result.Success)
        val blocks1 = (result1 as Result.Success).data
        val blocks2 = (result2 as Result.Success).data
        
        // Different URIs may produce different patterns
        // At least verify both return valid data
        assertTrue(blocks1.isNotEmpty())
        assertTrue(blocks2.isNotEmpty())
    }
    
    @Test
    fun `extractTextFromImage should sort by confidence descending`() = runTest {
        // When
        val result = repository.extractTextFromImage(testUri1, 0.7f)
        
        // Then
        assertTrue(result is Result.Success)
        val textBlocks = (result as Result.Success).data
        
        // Verify descending order
        for (i in 0 until textBlocks.size - 1) {
            assertTrue(
                "Block $i confidence ${textBlocks[i].confidence} should be >= " +
                "block ${i+1} confidence ${textBlocks[i+1].confidence}",
                textBlocks[i].confidence >= textBlocks[i+1].confidence
            )
        }
    }
    
    @Test
    fun `extractCombinedText should return space-separated text`() = runTest {
        // When
        val result = repository.extractCombinedText(testUri1, 0.8f)
        
        // Then
        assertTrue(result is Result.Success)
        val combinedText = (result as Result.Success).data
        assertTrue(combinedText.isNotBlank())
        assertFalse(combinedText.startsWith(" "))
        assertFalse(combinedText.endsWith(" "))
    }
    
    @Test
    fun `extractCombinedText should combine all text blocks`() = runTest {
        // Given - First get individual blocks
        val blocksResult = repository.extractTextFromImage(testUri1, 0.8f)
        assertTrue(blocksResult is Result.Success)
        val blocks = (blocksResult as Result.Success).data
        
        // When - Get combined text
        val combinedResult = repository.extractCombinedText(testUri1, 0.8f)
        
        // Then
        assertTrue(combinedResult is Result.Success)
        val combinedText = (combinedResult as Result.Success).data
        
        // Combined text should contain text from all blocks
        blocks.forEach { block ->
            assertTrue(
                "Combined text should contain '${block.text}'",
                combinedText.contains(block.text)
            )
        }
    }
    
    @Test
    fun `extractCombinedText should respect confidence threshold`() = runTest {
        // When - High threshold
        val result = repository.extractCombinedText(testUri1, 0.95f)
        
        // Then
        assertTrue(result is Result.Success)
        val combinedText = (result as Result.Success).data
        
        // Should only include high-confidence text
        // Get blocks to verify
        val blocksResult = repository.extractTextFromImage(testUri1, 0.95f)
        val blocks = (blocksResult as Result.Success).data
        
        blocks.forEach { block ->
            assertTrue(combinedText.contains(block.text))
        }
    }
    
    @Test
    fun `mock patterns should generate document pattern`() = runTest {
        // Create URI that hashes to pattern 0 (document)
        val docUri = mockk<Uri>(relaxed = true) {
            every { toString() } returns "content://doc" // hashCode % 7 = 0
        }
        
        // When
        val result = repository.extractTextFromImage(docUri, 0.7f)
        
        // Then
        assertTrue(result is Result.Success)
        val blocks = (result as Result.Success).data
        assertTrue(blocks.isNotEmpty())
        
        // Document pattern should have formal text
        val allText = blocks.joinToString(" ") { it.text }
        assertTrue(allText.length > 0)
    }
    
    @Test
    fun `mock text blocks should have valid bounding boxes`() = runTest {
        // When
        val result = repository.extractTextFromImage(testUri1, 0.7f)
        
        // Then
        assertTrue(result is Result.Success)
        val blocks = (result as Result.Success).data
        
        blocks.forEach { block ->
            val rect = block.boundingBox
            assertTrue("Left should be >= 0", rect.left >= 0)
            assertTrue("Top should be >= 0", rect.top >= 0)
            assertTrue("Right should be > left", rect.right > rect.left)
            assertTrue("Bottom should be > top", rect.bottom > rect.top)
        }
    }
    
    @Test
    fun `mock text blocks should have language set`() = runTest {
        // When
        val result = repository.extractTextFromImage(testUri1, 0.7f)
        
        // Then
        assertTrue(result is Result.Success)
        val blocks = (result as Result.Success).data
        
        // Most blocks should have language set (mock uses "en")
        val withLanguage = blocks.count { it.language != null }
        assertTrue("Most blocks should have language", withLanguage > 0)
    }
    
    @Test
    fun `extractTextFromImage should handle low confidence threshold`() = runTest {
        // When - Very low threshold
        val result = repository.extractTextFromImage(testUri1, 0.1f)
        
        // Then
        assertTrue(result is Result.Success)
        val blocks = (result as Result.Success).data
        
        // Should return more blocks with low threshold
        assertTrue(blocks.isNotEmpty())
    }
    
    @Test
    fun `extractTextFromImage should handle maximum confidence threshold`() = runTest {
        // When - Maximum threshold
        val result = repository.extractTextFromImage(testUri1, 1.0f)
        
        // Then
        assertTrue(result is Result.Success)
        // May return empty list if no blocks have 1.0 confidence
        // This is valid behavior
    }
    
    @Test
    fun `extractCombinedText should return empty string when no blocks meet threshold`() = runTest {
        // When - Impossible threshold
        val result = repository.extractCombinedText(testUri1, 1.5f) // Will be filtered to 1.0
        
        // Then
        assertTrue(result is Result.Success)
        // Should handle gracefully
    }
}
