package com.example.conversion.data.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.example.conversion.domain.common.Result
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.FileNotFoundException

/**
 * Unit tests for MetadataRepositoryImpl.
 * Tests EXIF metadata extraction, error handling, and validation.
 * 
 * Note: These tests use mocking to simulate ExifInterface behavior since
 * actual EXIF extraction requires Android framework components and real image files.
 * For full integration tests with real images, use Android instrumented tests.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MetadataRepositoryImplTest {
    
    private lateinit var context: Context
    private lateinit var contentResolver: ContentResolver
    private lateinit var repository: MetadataRepositoryImpl
    
    // Mock URIs
    private val validImageUri = Uri.parse("content://media/external/images/1")
    private val invalidUri = Uri.parse("invalid://uri")
    private val fileUri = Uri.parse("file:///storage/emulated/0/image.jpg")
    
    @Before
    fun setup() {
        context = mockk(relaxed = true)
        contentResolver = mockk(relaxed = true)
        every { context.contentResolver } returns contentResolver
        repository = MetadataRepositoryImpl(context)
    }
    
    @Test
    fun `extractMetadata returns error for invalid URI scheme`() = runTest {
        // Given
        val httpUri = Uri.parse("http://example.com/image.jpg")
        every { contentResolver.getType(httpUri) } returns "image/jpeg"
        
        // When
        val result = repository.extractMetadata(httpUri)
        
        // Then
        assertTrue(result is Result.Error)
        val error = result as Result.Error
        assertTrue(
            error.exception is IllegalArgumentException ||
            error.message?.contains("Invalid") == true
        )
    }
    
    @Test
    fun `extractMetadata returns error when image not found`() = runTest {
        // Given
        every { contentResolver.getType(validImageUri) } returns "image/jpeg"
        every { contentResolver.openInputStream(validImageUri) } returns null
        
        // When
        val result = repository.extractMetadata(validImageUri)
        
        // Then
        assertTrue(result is Result.Error)
        val error = result as Result.Error
        assertTrue(
            error.exception is FileNotFoundException || 
            error.message?.contains("Cannot open input stream") == true
        )
    }
    
    @Test
    fun `extractMetadata returns error on SecurityException`() = runTest {
        // Given
        every { contentResolver.getType(validImageUri) } returns "image/jpeg"
        every { contentResolver.openInputStream(validImageUri) } throws SecurityException("Permission denied")
        
        // When
        val result = repository.extractMetadata(validImageUri)
        
        // Then
        assertTrue(result is Result.Error)
        val error = result as Result.Error
        assertTrue(error.exception is SecurityException)
    }
    
    @Test
    fun `extractMetadata returns error on FileNotFoundException`() = runTest {
        // Given
        every { contentResolver.getType(validImageUri) } returns "image/jpeg"
        every { contentResolver.openInputStream(validImageUri) } throws FileNotFoundException("File not found")
        
        // When
        val result = repository.extractMetadata(validImageUri)
        
        // Then
        assertTrue(result is Result.Error)
        val error = result as Result.Error
        assertTrue(error.exception is FileNotFoundException)
    }
    
    @Test
    fun `validateImageUri returns false for invalid URI scheme`() = runTest {
        // Given
        val httpUri = Uri.parse("http://example.com/image.jpg")
        every { contentResolver.getType(httpUri) } returns "image/jpeg"
        
        // When
        val result = repository.validateImageUri(httpUri)
        
        // Then
        assertTrue(result is Result.Success)
        assertFalse((result as Result.Success).data)
    }
    
    @Test
    fun `validateImageUri returns false for unsupported MIME type`() = runTest {
        // Given
        every { contentResolver.getType(validImageUri) } returns "video/mp4"
        
        // When
        val result = repository.validateImageUri(validImageUri)
        
        // Then
        assertTrue(result is Result.Success)
        assertFalse((result as Result.Success).data)
    }
    
    @Test
    fun `validateImageUri returns true for JPEG images`() = runTest {
        // Given
        every { contentResolver.getType(validImageUri) } returns "image/jpeg"
        
        // When
        val result = repository.validateImageUri(validImageUri)
        
        // Then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data)
    }
    
    @Test
    fun `validateImageUri returns true for PNG images`() = runTest {
        // Given
        every { contentResolver.getType(validImageUri) } returns "image/png"
        
        // When
        val result = repository.validateImageUri(validImageUri)
        
        // Then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data)
    }
    
    @Test
    fun `validateImageUri returns true for HEIF images`() = runTest {
        // Given
        every { contentResolver.getType(validImageUri) } returns "image/heif"
        
        // When
        val result = repository.validateImageUri(validImageUri)
        
        // Then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data)
    }
    
    @Test
    fun `validateImageUri returns true for WebP images`() = runTest {
        // Given
        every { contentResolver.getType(validImageUri) } returns "image/webp"
        
        // When
        val result = repository.validateImageUri(validImageUri)
        
        // Then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data)
    }
    
    @Test
    fun `validateImageUri returns true for DNG RAW images`() = runTest {
        // Given
        every { contentResolver.getType(validImageUri) } returns "image/dng"
        
        // When
        val result = repository.validateImageUri(validImageUri)
        
        // Then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data)
    }
    
    @Test
    fun `validateImageUri handles null MIME type`() = runTest {
        // Given
        every { contentResolver.getType(validImageUri) } returns null
        
        // When
        val result = repository.validateImageUri(validImageUri)
        
        // Then
        assertTrue(result is Result.Success)
        assertFalse((result as Result.Success).data)
    }
    
    @Test
    fun `extractMetadataForMultiple returns list with mixed results`() = runTest {
        // Given
        val uri1 = Uri.parse("content://media/external/images/1")
        val uri2 = Uri.parse("content://media/external/images/2")
        val uri3 = Uri.parse("content://media/external/images/3")
        val uris = listOf(uri1, uri2, uri3)
        
        every { contentResolver.getType(uri1) } returns "image/jpeg"
        every { contentResolver.getType(uri2) } returns "video/mp4" // Invalid
        every { contentResolver.getType(uri3) } returns "image/png"
        
        // When
        val result = repository.extractMetadataForMultiple(uris)
        
        // Then
        assertTrue(result is Result.Success)
        val metadataList = (result as Result.Success).data
        assertNotNull(metadataList)
        assertTrue(metadataList.size == 3)
    }
    
    @Test
    fun `extractMetadataForMultiple handles empty list`() = runTest {
        // Given
        val emptyList = emptyList<Uri>()
        
        // When
        val result = repository.extractMetadataForMultiple(emptyList)
        
        // Then
        assertTrue(result is Result.Success)
        val metadataList = (result as Result.Success).data
        assertTrue(metadataList.isEmpty())
    }
    
    @Test
    fun `hasMetadata returns false when file not found`() = runTest {
        // Given
        every { contentResolver.openInputStream(validImageUri) } returns null
        
        // When
        val result = repository.hasMetadata(validImageUri)
        
        // Then
        assertTrue(result is Result.Success)
        assertFalse((result as Result.Success).data)
    }
    
    @Test
    fun `hasMetadata returns error on exception`() = runTest {
        // Given
        every { contentResolver.openInputStream(validImageUri) } throws SecurityException("Permission denied")
        
        // When
        val result = repository.hasMetadata(validImageUri)
        
        // Then
        assertTrue(result is Result.Error)
    }
}
