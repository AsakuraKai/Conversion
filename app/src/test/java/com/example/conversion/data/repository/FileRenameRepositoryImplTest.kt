package com.example.conversion.data.repository

import android.content.ContentResolver
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.example.conversion.domain.common.Result
import io.mockk.*
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for FileRenameRepositoryImpl.
 * Tests file rename operations with MediaStore API.
 */
class FileRenameRepositoryImplTest {

    private lateinit var contentResolver: ContentResolver
    private lateinit var repository: FileRenameRepositoryImpl
    
    private val testDispatcher = UnconfinedTestDispatcher()
    
    private val testUri = Uri.parse("content://media/external/images/1")
    private val testFilePath = "/storage/emulated/0/DCIM/IMG_001.jpg"

    @Before
    fun setup() {
        contentResolver = mockk(relaxed = true)
        repository = FileRenameRepositoryImpl(contentResolver, testDispatcher)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `renameFile returns success when update succeeds`() = runTest {
        // Given: ContentResolver update returns 1 (success)
        every { contentResolver.update(any(), any(), any(), any()) } returns 1

        // When: Rename file
        val result = repository.renameFile(testUri, "new_photo.jpg")

        // Then: Should return success
        assertTrue(result is Result.Success)
        assertEquals(testUri, (result as Result.Success).data)
        
        // Verify update was called with correct parameters
        verify {
            contentResolver.update(
                testUri,
                match { values ->
                    values.getAsString(MediaStore.MediaColumns.DISPLAY_NAME) == "new_photo.jpg"
                },
                null,
                null
            )
        }
    }

    @Test
    fun `renameFile returns error when update fails`() = runTest {
        // Given: ContentResolver update returns 0 (failure)
        every { contentResolver.update(any(), any(), any(), any()) } returns 0

        // When: Rename file
        val result = repository.renameFile(testUri, "new_photo.jpg")

        // Then: Should return error
        assertTrue(result is Result.Error)
        val error = (result as Result.Error).exception
        assertTrue(error.message?.contains("No rows updated") == true)
    }

    @Test
    fun `renameFile returns error on SecurityException`() = runTest {
        // Given: ContentResolver throws SecurityException
        every { 
            contentResolver.update(any(), any(), any(), any()) 
        } throws SecurityException("Permission denied")

        // When: Rename file
        val result = repository.renameFile(testUri, "new_photo.jpg")

        // Then: Should return error with permission message
        assertTrue(result is Result.Error)
        val error = (result as Result.Error).exception
        assertTrue(error.message?.contains("Permission denied") == true)
    }

    @Test
    fun `renameFile returns error on IllegalArgumentException`() = runTest {
        // Given: ContentResolver throws IllegalArgumentException
        every { 
            contentResolver.update(any(), any(), any(), any()) 
        } throws IllegalArgumentException("Invalid URI")

        // When: Rename file
        val result = repository.renameFile(testUri, "new_photo.jpg")

        // Then: Should return error with invalid URI message
        assertTrue(result is Result.Error)
        val error = (result as Result.Error).exception
        assertTrue(error.message?.contains("Invalid file URI or name") == true)
    }

    @Test
    fun `renameFile handles general exceptions`() = runTest {
        // Given: ContentResolver throws generic exception
        every { 
            contentResolver.update(any(), any(), any(), any()) 
        } throws RuntimeException("Unexpected error")

        // When: Rename file
        val result = repository.renameFile(testUri, "new_photo.jpg")

        // Then: Should return error
        assertTrue(result is Result.Error)
        val error = (result as Result.Error).exception
        assertTrue(error.message?.contains("Failed to rename file") == true)
    }

    @Test
    fun `checkNameConflict returns false when no conflict exists`() = runTest {
        // Given: Mock cursor for file path query
        val pathCursor = mockk<Cursor>(relaxed = true)
        every { pathCursor.moveToFirst() } returns true
        every { pathCursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA) } returns 0
        every { pathCursor.getString(0) } returns testFilePath
        
        // Mock cursor for conflict check query (empty result)
        val conflictCursor = mockk<Cursor>(relaxed = true)
        every { conflictCursor.count } returns 0
        
        // Setup query responses
        every { 
            contentResolver.query(
                testUri,
                arrayOf(MediaStore.MediaColumns.DATA),
                null,
                null,
                null
            )
        } returns pathCursor
        
        every {
            contentResolver.query(
                MediaStore.Files.getContentUri("external"),
                any(),
                any(),
                any(),
                null
            )
        } returns conflictCursor

        // When: Check for name conflict
        val hasConflict = repository.checkNameConflict(testUri, "new_photo.jpg")

        // Then: Should return false (no conflict)
        assertFalse(hasConflict)
        
        verify { pathCursor.close() }
        verify { conflictCursor.close() }
    }

    @Test
    fun `checkNameConflict returns true when conflict exists`() = runTest {
        // Given: Mock cursor for file path query
        val pathCursor = mockk<Cursor>(relaxed = true)
        every { pathCursor.moveToFirst() } returns true
        every { pathCursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA) } returns 0
        every { pathCursor.getString(0) } returns testFilePath
        
        // Mock cursor for conflict check query (found existing file)
        val conflictCursor = mockk<Cursor>(relaxed = true)
        every { conflictCursor.count } returns 1
        
        every { 
            contentResolver.query(
                testUri,
                arrayOf(MediaStore.MediaColumns.DATA),
                null,
                null,
                null
            )
        } returns pathCursor
        
        every {
            contentResolver.query(
                MediaStore.Files.getContentUri("external"),
                any(),
                any(),
                any(),
                null
            )
        } returns conflictCursor

        // When: Check for name conflict
        val hasConflict = repository.checkNameConflict(testUri, "new_photo.jpg")

        // Then: Should return true (conflict exists)
        assertTrue(hasConflict)
    }

    @Test
    fun `checkNameConflict returns false on exception`() = runTest {
        // Given: Query throws exception
        every { 
            contentResolver.query(any(), any(), any(), any(), any()) 
        } throws RuntimeException("Query failed")

        // When: Check for name conflict
        val hasConflict = repository.checkNameConflict(testUri, "new_photo.jpg")

        // Then: Should return false (assume no conflict to allow rename attempt)
        assertFalse(hasConflict)
    }

    @Test
    fun `checkNameConflict handles null cursor gracefully`() = runTest {
        // Given: Query returns null cursor
        every { 
            contentResolver.query(any(), any(), any(), any(), any()) 
        } returns null

        // When: Check for name conflict
        val hasConflict = repository.checkNameConflict(testUri, "new_photo.jpg")

        // Then: Should return false
        assertFalse(hasConflict)
    }

    @Test
    fun `batchRenameFiles processes all files`() = runTest {
        // Given: Multiple rename pairs
        val uri1 = Uri.parse("content://media/external/images/1")
        val uri2 = Uri.parse("content://media/external/images/2")
        val uri3 = Uri.parse("content://media/external/images/3")
        
        val renamePairs = listOf(
            uri1 to "photo001.jpg",
            uri2 to "photo002.jpg",
            uri3 to "photo003.jpg"
        )
        
        // Mock successful updates for all
        every { contentResolver.update(any(), any(), any(), any()) } returns 1

        // When: Batch rename files
        val results = repository.batchRenameFiles(renamePairs)

        // Then: Should process all three files
        assertEquals(3, results.size)
        assertTrue(results[uri1] is Result.Success)
        assertTrue(results[uri2] is Result.Success)
        assertTrue(results[uri3] is Result.Success)
    }

    @Test
    fun `batchRenameFiles handles mixed success and failure`() = runTest {
        // Given: Multiple rename pairs with mixed results
        val uri1 = Uri.parse("content://media/external/images/1")
        val uri2 = Uri.parse("content://media/external/images/2")
        val uri3 = Uri.parse("content://media/external/images/3")
        
        val renamePairs = listOf(
            uri1 to "photo001.jpg",
            uri2 to "photo002.jpg",
            uri3 to "photo003.jpg"
        )
        
        // Mock: first succeeds, second fails, third succeeds
        every { contentResolver.update(uri1, any(), any(), any()) } returns 1
        every { contentResolver.update(uri2, any(), any(), any()) } returns 0
        every { contentResolver.update(uri3, any(), any(), any()) } returns 1

        // When: Batch rename files
        val results = repository.batchRenameFiles(renamePairs)

        // Then: Should have mixed results
        assertEquals(3, results.size)
        assertTrue(results[uri1] is Result.Success)
        assertTrue(results[uri2] is Result.Error)
        assertTrue(results[uri3] is Result.Success)
    }

    @Test
    fun `batchRenameFiles handles empty list`() = runTest {
        // Given: Empty rename pairs
        val renamePairs = emptyList<Pair<Uri, String>>()

        // When: Batch rename files
        val results = repository.batchRenameFiles(renamePairs)

        // Then: Should return empty results
        assertTrue(results.isEmpty())
    }

    @Test
    fun `batchRenameFiles continues on individual failures`() = runTest {
        // Given: Multiple files where middle one throws exception
        val uri1 = Uri.parse("content://media/external/images/1")
        val uri2 = Uri.parse("content://media/external/images/2")
        val uri3 = Uri.parse("content://media/external/images/3")
        
        val renamePairs = listOf(
            uri1 to "photo001.jpg",
            uri2 to "photo002.jpg",
            uri3 to "photo003.jpg"
        )
        
        // Mock: first succeeds, second throws exception, third succeeds
        every { contentResolver.update(uri1, any(), any(), any()) } returns 1
        every { contentResolver.update(uri2, any(), any(), any()) } throws SecurityException("Permission denied")
        every { contentResolver.update(uri3, any(), any(), any()) } returns 1

        // When: Batch rename files
        val results = repository.batchRenameFiles(renamePairs)

        // Then: Should process all files despite exception
        assertEquals(3, results.size)
        assertTrue(results[uri1] is Result.Success)
        assertTrue(results[uri2] is Result.Error)
        assertTrue(results[uri3] is Result.Success)
    }

    @Test
    fun `renameFile with special characters in filename`() = runTest {
        // Given: Filename with special characters
        val specialFilename = "photo_2024-12-01_test.jpg"
        every { contentResolver.update(any(), any(), any(), any()) } returns 1

        // When: Rename file
        val result = repository.renameFile(testUri, specialFilename)

        // Then: Should succeed
        assertTrue(result is Result.Success)
        verify {
            contentResolver.update(
                testUri,
                match { values ->
                    values.getAsString(MediaStore.MediaColumns.DISPLAY_NAME) == specialFilename
                },
                null,
                null
            )
        }
    }

    @Test
    fun `renameFile with unicode filename`() = runTest {
        // Given: Filename with unicode characters
        val unicodeFilename = "照片_001.jpg"
        every { contentResolver.update(any(), any(), any(), any()) } returns 1

        // When: Rename file
        val result = repository.renameFile(testUri, unicodeFilename)

        // Then: Should succeed
        assertTrue(result is Result.Success)
    }
}
