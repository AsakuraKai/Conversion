package com.example.conversion.data.repository

import android.content.ContentResolver
import android.net.Uri
import com.example.conversion.data.source.local.MediaStoreDataSource
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.FileFilter
import com.example.conversion.domain.model.FileItem
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for MediaRepositoryImpl.
 * Tests MediaStore integration, error handling, and repository behavior.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MediaRepositoryImplTest {

    private lateinit var mediaStoreDataSource: MediaStoreDataSource
    private lateinit var contentResolver: ContentResolver
    private lateinit var repository: MediaRepositoryImpl
    private val testDispatcher = UnconfinedTestDispatcher()

    // Mock data
    private val mockFileItem = FileItem(
        id = 1L,
        uri = Uri.parse("content://media/external/images/1"),
        name = "test_image.jpg",
        path = "/storage/emulated/0/Pictures/test_image.jpg",
        size = 1024000L,
        mimeType = "image/jpeg",
        dateModified = System.currentTimeMillis(),
        thumbnailUri = null
    )

    @Before
    fun setup() {
        mediaStoreDataSource = mockk()
        contentResolver = mockk(relaxed = true)
        repository = MediaRepositoryImpl(
            mediaStoreDataSource = mediaStoreDataSource,
            contentResolver = contentResolver,
            ioDispatcher = testDispatcher
        )
    }

    @Test
    fun `getMediaFiles returns success with files when query succeeds`() = runTest {
        // Given
        val filter = FileFilter.DEFAULT
        val expectedFiles = listOf(mockFileItem)
        every { mediaStoreDataSource.queryMediaFiles(filter) } returns expectedFiles

        // When
        val result = repository.getMediaFiles(filter)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(expectedFiles, (result as Result.Success).data)
        verify { mediaStoreDataSource.queryMediaFiles(filter) }
    }

    @Test
    fun `getMediaFiles returns empty list when no files found`() = runTest {
        // Given
        val filter = FileFilter.DEFAULT
        every { mediaStoreDataSource.queryMediaFiles(filter) } returns emptyList()

        // When
        val result = repository.getMediaFiles(filter)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(0, (result as Result.Success).data.size)
    }

    @Test
    fun `getMediaFiles returns error when SecurityException thrown`() = runTest {
        // Given
        val filter = FileFilter.DEFAULT
        every { mediaStoreDataSource.queryMediaFiles(filter) } throws SecurityException("Permission denied")

        // When
        val result = repository.getMediaFiles(filter)

        // Then
        assertTrue(result is Result.Error)
        val error = result as Result.Error
        assertTrue(error.exception is SecurityException)
        assertTrue(error.message?.contains("Permission denied") == true)
    }

    @Test
    fun `getMediaFiles returns error when general exception thrown`() = runTest {
        // Given
        val filter = FileFilter.DEFAULT
        every { mediaStoreDataSource.queryMediaFiles(filter) } throws Exception("Database error")

        // When
        val result = repository.getMediaFiles(filter)

        // Then
        assertTrue(result is Result.Error)
        val error = result as Result.Error
        assertTrue(error.message?.contains("Failed to retrieve media files") == true)
    }

    @Test
    fun `getMediaFiles with images only filter`() = runTest {
        // Given
        val filter = FileFilter.IMAGES_ONLY
        val expectedFiles = listOf(mockFileItem)
        every { mediaStoreDataSource.queryMediaFiles(filter) } returns expectedFiles

        // When
        val result = repository.getMediaFiles(filter)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(expectedFiles, (result as Result.Success).data)
        verify { mediaStoreDataSource.queryMediaFiles(filter) }
    }

    @Test
    fun `getMediaFiles with videos only filter`() = runTest {
        // Given
        val videoFile = mockFileItem.copy(
            mimeType = "video/mp4",
            name = "test_video.mp4"
        )
        val filter = FileFilter.VIDEOS_ONLY
        every { mediaStoreDataSource.queryMediaFiles(filter) } returns listOf(videoFile)

        // When
        val result = repository.getMediaFiles(filter)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(1, (result as Result.Success).data.size)
        assertTrue((result.data.first().isVideo))
    }

    @Test
    fun `getMediaFiles with size filter`() = runTest {
        // Given
        val filter = FileFilter.DEFAULT.copy(
            minSize = 500000L,
            maxSize = 2000000L
        )
        val expectedFiles = listOf(mockFileItem)
        every { mediaStoreDataSource.queryMediaFiles(filter) } returns expectedFiles

        // When
        val result = repository.getMediaFiles(filter)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(expectedFiles, (result as Result.Success).data)
    }

    @Test
    fun `getFilesByFolder returns success with files`() = runTest {
        // Given
        val folderPath = "/storage/emulated/0/Pictures"
        val filter = FileFilter.DEFAULT.copy(folderPath = folderPath)
        val expectedFiles = listOf(mockFileItem)
        every { mediaStoreDataSource.queryMediaFiles(filter) } returns expectedFiles

        // When
        val result = repository.getFilesByFolder(folderPath)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(expectedFiles, (result as Result.Success).data)
    }

    @Test
    fun `getFilesByFolder returns error on SecurityException`() = runTest {
        // Given
        val folderPath = "/storage/emulated/0/Pictures"
        val filter = FileFilter.DEFAULT.copy(folderPath = folderPath)
        every { mediaStoreDataSource.queryMediaFiles(filter) } throws SecurityException()

        // When
        val result = repository.getFilesByFolder(folderPath)

        // Then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is SecurityException)
    }

    @Test
    fun `getFileByUri returns success with file when found`() = runTest {
        // Given
        val uriString = "content://media/external/images/1"
        every { mediaStoreDataSource.queryFileByUri(uriString) } returns mockFileItem

        // When
        val result = repository.getFileByUri(uriString)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(mockFileItem, (result as Result.Success).data)
    }

    @Test
    fun `getFileByUri returns success with null when not found`() = runTest {
        // Given
        val uriString = "content://media/external/images/999"
        every { mediaStoreDataSource.queryFileByUri(uriString) } returns null

        // When
        val result = repository.getFileByUri(uriString)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(null, (result as Result.Success).data)
    }

    @Test
    fun `getFileByUri returns error on exception`() = runTest {
        // Given
        val uriString = "invalid_uri"
        every { mediaStoreDataSource.queryFileByUri(uriString) } throws Exception("Invalid URI")

        // When
        val result = repository.getFileByUri(uriString)

        // Then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).message?.contains("Failed to retrieve file") == true)
    }

    @Test
    fun `getMediaFolders returns success with folder list`() = runTest {
        // Given
        val filter = FileFilter.DEFAULT
        val expectedFolders = listOf(
            "/storage/emulated/0/Pictures",
            "/storage/emulated/0/DCIM/Camera",
            "/storage/emulated/0/Download"
        )
        every { mediaStoreDataSource.queryMediaFolders(filter) } returns expectedFolders

        // When
        val result = repository.getMediaFolders(filter)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(expectedFolders, (result as Result.Success).data)
    }

    @Test
    fun `getMediaFolders returns empty list when no folders found`() = runTest {
        // Given
        val filter = FileFilter.DEFAULT
        every { mediaStoreDataSource.queryMediaFolders(filter) } returns emptyList()

        // When
        val result = repository.getMediaFolders(filter)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(0, (result as Result.Success).data.size)
    }

    @Test
    fun `getMediaFolders returns error on SecurityException`() = runTest {
        // Given
        val filter = FileFilter.DEFAULT
        every { mediaStoreDataSource.queryMediaFolders(filter) } throws SecurityException()

        // When
        val result = repository.getMediaFolders(filter)

        // Then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is SecurityException)
    }
}
