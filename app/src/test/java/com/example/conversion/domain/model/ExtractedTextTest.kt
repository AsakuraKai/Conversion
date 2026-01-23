package com.example.conversion.domain.model

import android.graphics.Rect
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for ExtractedText domain model.
 *
 * @author Kai (Backend/Core Features)
 * @since CHUNK 19 - OCR Integration
 */
class ExtractedTextTest {
    
    @Test
    fun `create ExtractedText with valid parameters`() {
        // When
        val extractedText = ExtractedText(
            text = "Hello World",
            confidence = 0.95f,
            boundingBox = Rect(0, 0, 100, 50),
            language = "en"
        )
        
        // Then
        assertEquals("Hello World", extractedText.text)
        assertEquals(0.95f, extractedText.confidence)
        assertEquals("en", extractedText.language)
        assertFalse(extractedText.boundingBox.isEmpty)
    }
    
    @Test
    fun `create ExtractedText without language`() {
        // When
        val extractedText = ExtractedText(
            text = "Test",
            confidence = 0.9f,
            boundingBox = Rect(0, 0, 50, 30)
        )
        
        // Then
        assertEquals("Test", extractedText.text)
        assertEquals(null, extractedText.language)
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `create ExtractedText with blank text should throw`() {
        // When/Then
        ExtractedText(
            text = "",
            confidence = 0.9f,
            boundingBox = Rect(0, 0, 100, 50)
        )
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `create ExtractedText with whitespace text should throw`() {
        // When/Then
        ExtractedText(
            text = "   ",
            confidence = 0.9f,
            boundingBox = Rect(0, 0, 100, 50)
        )
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `create ExtractedText with negative confidence should throw`() {
        // When/Then
        ExtractedText(
            text = "Test",
            confidence = -0.1f,
            boundingBox = Rect(0, 0, 100, 50)
        )
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `create ExtractedText with confidence above 1 should throw`() {
        // When/Then
        ExtractedText(
            text = "Test",
            confidence = 1.1f,
            boundingBox = Rect(0, 0, 100, 50)
        )
    }
    
    @Test
    fun `create ExtractedText with minimum confidence`() {
        // When
        val extractedText = ExtractedText(
            text = "Test",
            confidence = 0.0f,
            boundingBox = Rect(0, 0, 100, 50)
        )
        
        // Then
        assertEquals(0.0f, extractedText.confidence)
    }
    
    @Test
    fun `create ExtractedText with maximum confidence`() {
        // When
        val extractedText = ExtractedText(
            text = "Test",
            confidence = 1.0f,
            boundingBox = Rect(0, 0, 100, 50)
        )
        
        // Then
        assertEquals(1.0f, extractedText.confidence)
    }
    
    @Test
    fun `meetsThreshold should return true when confidence meets threshold`() {
        // Given
        val extractedText = ExtractedText(
            text = "Test",
            confidence = 0.85f,
            boundingBox = Rect(0, 0, 100, 50)
        )
        
        // When/Then
        assertTrue(extractedText.meetsThreshold(0.8f))
        assertTrue(extractedText.meetsThreshold(0.85f))
    }
    
    @Test
    fun `meetsThreshold should return false when confidence below threshold`() {
        // Given
        val extractedText = ExtractedText(
            text = "Test",
            confidence = 0.75f,
            boundingBox = Rect(0, 0, 100, 50)
        )
        
        // When/Then
        assertFalse(extractedText.meetsThreshold(0.8f))
    }
    
    @Test
    fun `meetsThreshold should use default threshold`() {
        // Given
        val highConfidence = ExtractedText(
            text = "High",
            confidence = 0.9f,
            boundingBox = Rect(0, 0, 100, 50)
        )
        val lowConfidence = ExtractedText(
            text = "Low",
            confidence = 0.7f,
            boundingBox = Rect(0, 0, 100, 50)
        )
        
        // When/Then
        assertTrue(highConfidence.meetsThreshold())
        assertFalse(lowConfidence.meetsThreshold())
    }
    
    @Test
    fun `toDisplayString should format correctly`() {
        // Given
        val extractedText = ExtractedText(
            text = "Hello World",
            confidence = 0.92f,
            boundingBox = Rect(0, 0, 100, 50)
        )
        
        // When
        val displayString = extractedText.toDisplayString()
        
        // Then
        assertEquals("Hello World (92%)", displayString)
    }
    
    @Test
    fun `toDisplayString should round percentage correctly`() {
        // Given
        val extractedText = ExtractedText(
            text = "Test",
            confidence = 0.956f,
            boundingBox = Rect(0, 0, 100, 50)
        )
        
        // When
        val displayString = extractedText.toDisplayString()
        
        // Then
        assertEquals("Test (95%)", displayString)
    }
    
    @Test
    fun `toFilenameFragment should sanitize text for filenames`() {
        // Given
        val extractedText = ExtractedText(
            text = "Hello World!",
            confidence = 0.9f,
            boundingBox = Rect(0, 0, 100, 50)
        )
        
        // When
        val filename = extractedText.toFilenameFragment()
        
        // Then
        assertEquals("Hello_World", filename)
    }
    
    @Test
    fun `toFilenameFragment should remove illegal characters`() {
        // Given
        val extractedText = ExtractedText(
            text = "Test@#$%File",
            confidence = 0.9f,
            boundingBox = Rect(0, 0, 100, 50)
        )
        
        // When
        val filename = extractedText.toFilenameFragment()
        
        // Then
        assertEquals("TestFile", filename)
    }
    
    @Test
    fun `toFilenameFragment should replace multiple spaces with single underscore`() {
        // Given
        val extractedText = ExtractedText(
            text = "Hello    World",
            confidence = 0.9f,
            boundingBox = Rect(0, 0, 100, 50)
        )
        
        // When
        val filename = extractedText.toFilenameFragment()
        
        // Then
        assertEquals("Hello_World", filename)
    }
    
    @Test
    fun `toFilenameFragment should limit length`() {
        // Given
        val longText = "A".repeat(100)
        val extractedText = ExtractedText(
            text = longText,
            confidence = 0.9f,
            boundingBox = Rect(0, 0, 100, 50)
        )
        
        // When
        val filename = extractedText.toFilenameFragment(maxLength = 20)
        
        // Then
        assertEquals(20, filename.length)
    }
    
    @Test
    fun `toFilenameFragment should use default max length`() {
        // Given
        val longText = "A".repeat(100)
        val extractedText = ExtractedText(
            text = longText,
            confidence = 0.9f,
            boundingBox = Rect(0, 0, 100, 50)
        )
        
        // When
        val filename = extractedText.toFilenameFragment()
        
        // Then
        assertEquals(50, filename.length)
    }
    
    @Test
    fun `toFilenameFragment should trim leading and trailing underscores`() {
        // Given
        val extractedText = ExtractedText(
            text = " Test File ",
            confidence = 0.9f,
            boundingBox = Rect(0, 0, 100, 50)
        )
        
        // When
        val filename = extractedText.toFilenameFragment()
        
        // Then
        assertFalse(filename.startsWith("_"))
        assertFalse(filename.endsWith("_"))
        assertEquals("Test_File", filename)
    }
    
    @Test
    fun `toFilenameFragment should preserve hyphens`() {
        // Given
        val extractedText = ExtractedText(
            text = "Test-File-Name",
            confidence = 0.9f,
            boundingBox = Rect(0, 0, 100, 50)
        )
        
        // When
        val filename = extractedText.toFilenameFragment()
        
        // Then
        assertEquals("Test-File-Name", filename)
    }
    
    @Test
    fun `constants should have correct values`() {
        // Then
        assertEquals(0.8f, ExtractedText.DEFAULT_CONFIDENCE_THRESHOLD)
        assertEquals(0.9f, ExtractedText.HIGH_CONFIDENCE_THRESHOLD)
    }
    
    @Test
    fun `bounding box should be preserved`() {
        // Given
        val boundingBox = Rect(10, 20, 200, 100)
        val extractedText = ExtractedText(
            text = "Test",
            confidence = 0.9f,
            boundingBox = boundingBox
        )
        
        // When/Then
        assertEquals(10, extractedText.boundingBox.left)
        assertEquals(20, extractedText.boundingBox.top)
        assertEquals(200, extractedText.boundingBox.right)
        assertEquals(100, extractedText.boundingBox.bottom)
    }
}
