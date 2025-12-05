package com.example.conversion.data.repository

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.example.conversion.domain.common.Result
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.FileNotFoundException

/**
 * Unit tests for ThemeRepositoryImpl.
 * Tests palette extraction, bitmap loading, error handling, and edge cases.
 * 
 * Note: Some tests use mocking to simulate Palette API behavior since
 * actual palette extraction requires Android framework components.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ThemeRepositoryImplTest {
    
    private lateinit var context: Context
    private lateinit var contentResolver: ContentResolver
    private lateinit var repository: ThemeRepositoryImpl
    
    // Mock URIs
    private val validImageUri = Uri.parse("content://media/external/images/1")
    private val invalidUri = Uri.parse("invalid://uri")
    private val fileUri = Uri.parse("file:///storage/emulated/0/image.jpg")
    
    @Before
    fun setup() {
        context = mockk(relaxed = true)
        contentResolver = mockk(relaxed = true)
        every { context.contentResolver } returns contentResolver
        repository = ThemeRepositoryImpl(context)
    }
    
    @Test
    fun `extractPalette returns error for invalid URI scheme`() = runTest {
        // Given
        val httpUri = Uri.parse("http://example.com/image.jpg")
        
        // When
        val result = repository.extractPalette(httpUri)
        
        // Then
        assertTrue(result is Result.Error)
        val error = result as Result.Error
        assertTrue(error.exception is IllegalArgumentException)
        assertTrue(error.message?.contains("Invalid URI scheme") == true)
    }
    
    @Test
    fun `extractPalette returns error when image not found`() = runTest {
        // Given
        every { contentResolver.openInputStream(validImageUri) } returns null
        
        // When
        val result = repository.extractPalette(validImageUri)
        
        // Then
        assertTrue(result is Result.Error)
        val error = result as Result.Error
        assertTrue(error.exception is FileNotFoundException || 
                   error.message?.contains("Failed to load image") == true)
    }
    
    @Test
    fun `extractPalette returns error on SecurityException`() = runTest {
        // Given
        every { contentResolver.openInputStream(validImageUri) } throws SecurityException("Permission denied")
        
        // When
        val result = repository.extractPalette(validImageUri)
        
        // Then
        assertTrue(result is Result.Error)
        val error = result as Result.Error
        assertTrue(error.exception is SecurityException)
        assertTrue(error.message?.contains("Permission denied") == true)
    }
    
    @Test
    fun `extractPalette returns error on FileNotFoundException`() = runTest {
        // Given
        every { contentResolver.openInputStream(validImageUri) } throws FileNotFoundException("File not found")
        
        // When
        val result = repository.extractPalette(validImageUri)
        
        // Then
        assertTrue(result is Result.Error)
        val error = result as Result.Error
        assertTrue(error.exception is FileNotFoundException)
        assertTrue(error.message?.contains("Image not found") == true)
    }
    
    @Test
    fun `extractPalette handles file URI scheme`() = runTest {
        // Given
        every { contentResolver.openInputStream(fileUri) } returns null
        
        // When
        val result = repository.extractPalette(fileUri)
        
        // Then
        // Should attempt to process file:// URI (will fail with null stream, but validates scheme handling)
        assertTrue(result is Result.Error)
        // Error should not be about invalid scheme
        val error = result as Result.Error
        assertFalse(error.message?.contains("Invalid URI scheme") == true)
    }
    
    @Test
    fun `extractPalette handles content URI scheme`() = runTest {
        // Given
        every { contentResolver.openInputStream(validImageUri) } returns null
        
        // When
        val result = repository.extractPalette(validImageUri)
        
        // Then
        // Should attempt to process content:// URI (will fail with null stream, but validates scheme handling)
        assertTrue(result is Result.Error)
        // Error should not be about invalid scheme
        val error = result as Result.Error
        assertFalse(error.message?.contains("Invalid URI scheme") == true)
    }
    
    @Test
    fun `extractPalette verifies URI validation before processing`() = runTest {
        // Given - Various invalid URI schemes
        val testCases = listOf(
            Uri.parse("http://example.com/image.jpg"),
            Uri.parse("https://example.com/image.jpg"),
            Uri.parse("ftp://example.com/image.jpg"),
            Uri.parse("data:image/png;base64,...")
        )
        
        // When & Then
        testCases.forEach { uri ->
            val result = repository.extractPalette(uri)
            assertTrue("URI $uri should fail validation", result is Result.Error)
            val error = result as Result.Error
            assertTrue(
                "Error should mention invalid scheme for $uri",
                error.message?.contains("Invalid URI scheme") == true
            )
        }
    }
    
    @Test
    fun `extractPalette verifies valid URI schemes are accepted`() = runTest {
        // Given - Valid URI schemes (will fail at image loading, but pass validation)
        val testCases = listOf(
            Uri.parse("content://media/external/images/1"),
            Uri.parse("file:///storage/emulated/0/image.jpg")
        )
        
        every { contentResolver.openInputStream(any()) } returns null
        
        // When & Then
        testCases.forEach { uri ->
            val result = repository.extractPalette(uri)
            assertTrue("URI $uri should pass validation", result is Result.Error)
            val error = result as Result.Error
            // Should fail at loading, not at validation
            assertFalse(
                "Error should not mention invalid scheme for $uri",
                error.message?.contains("Invalid URI scheme") == true
            )
        }
    }
    
    @Test
    fun `extractPalette handles OutOfMemoryError gracefully`() = runTest {
        // Given
        every { contentResolver.openInputStream(validImageUri) } throws OutOfMemoryError("Image too large")
        
        // When
        val result = repository.extractPalette(validImageUri)
        
        // Then
        assertTrue(result is Result.Error)
        val error = result as Result.Error
        assertTrue(error.message?.contains("too large") == true)
    }
    
    @Test
    fun `extractPalette handles generic exceptions`() = runTest {
        // Given
        every { contentResolver.openInputStream(validImageUri) } throws RuntimeException("Unknown error")
        
        // When
        val result = repository.extractPalette(validImageUri)
        
        // Then
        assertTrue(result is Result.Error)
        val error = result as Result.Error
        assertTrue(error.message?.contains("Failed to extract palette") == true)
    }
    
    @Test
    fun `extractPalette verifies error messages are user-friendly`() = runTest {
        // Test various error scenarios have appropriate messages
        
        // Security exception
        every { contentResolver.openInputStream(validImageUri) } throws SecurityException()
        var result = repository.extractPalette(validImageUri)
        assertTrue((result as Result.Error).message?.contains("Permission") == true ||
                   (result as Result.Error).message?.contains("storage permissions") == true)
        
        // File not found
        every { contentResolver.openInputStream(validImageUri) } throws FileNotFoundException()
        result = repository.extractPalette(validImageUri)
        assertTrue((result as Result.Error).message?.contains("not found") == true)
        
        // Invalid scheme
        result = repository.extractPalette(Uri.parse("http://test.com/image.jpg"))
        assertTrue((result as Result.Error).message?.contains("Invalid URI") == true)
    }
}
