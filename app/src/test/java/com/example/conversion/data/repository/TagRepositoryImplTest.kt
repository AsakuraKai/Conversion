package com.example.conversion.data.repository

import android.net.Uri
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.FileTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for TagRepositoryImpl.
 * Tests in-memory tag management functionality.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TagRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: TagRepositoryImpl

    // Sample data
    private val sampleTag = FileTag(
        id = "tag1",
        name = "Vacation",
        color = "#FF5722",
        createdAt = System.currentTimeMillis()
    )

    private val sampleUri = Uri.parse("content://media/external/images/media/123")

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = TagRepositoryImpl(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ==================== Create Tag Tests ====================

    @Test
    fun `create tag successfully`() = runTest {
        // When
        val result = repository.createTag(sampleTag)

        // Then
        assertTrue("Should create successfully", result is Result.Success)
        
        // Verify tag exists
        val getResult = repository.getTagById(sampleTag.id)
        assertTrue("Should retrieve tag", getResult is Result.Success)
        assertEquals("Should match created tag", sampleTag, (getResult as Result.Success).data)
    }

    @Test
    fun `create duplicate tag fails`() = runTest {
        // Given: Tag already exists
        repository.createTag(sampleTag)

        // When: Try to create again
        val result = repository.createTag(sampleTag)

        // Then
        assertTrue("Should fail", result is Result.Error)
        val error = result as Result.Error
        assertTrue("Error mentions already exists", 
            error.message?.contains("already exists") == true)
    }

    // ==================== Update Tag Tests ====================

    @Test
    fun `update existing tag successfully`() = runTest {
        // Given: Tag exists
        repository.createTag(sampleTag)

        // When: Update tag
        val updatedTag = sampleTag.copy(name = "Updated Name", color = "#2196F3")
        val result = repository.updateTag(updatedTag)

        // Then
        assertTrue("Should update successfully", result is Result.Success)
        
        val getResult = repository.getTagById(sampleTag.id)
        assertEquals("Should have updated name", "Updated Name", 
            (getResult as Result.Success).data?.name)
        assertEquals("Should have updated color", "#2196F3", getResult.data?.color)
    }

    @Test
    fun `update non-existent tag fails`() = runTest {
        // When: Update tag that doesn't exist
        val result = repository.updateTag(sampleTag)

        // Then
        assertTrue("Should fail", result is Result.Error)
        val error = result as Result.Error
        assertTrue("Error mentions does not exist", 
            error.message?.contains("does not exist") == true)
    }

    // ==================== Get Tags Tests ====================

    @Test
    fun `get all tags returns empty list initially`() = runTest {
        // When
        val result = repository.getTags()

        // Then
        assertTrue("Should succeed", result is Result.Success)
        assertTrue("Should be empty", (result as Result.Success).data.isEmpty())
    }

    @Test
    fun `get all tags returns sorted by creation date`() = runTest {
        // Given: Multiple tags
        val tag1 = sampleTag.copy(id = "tag1", createdAt = 1000L)
        val tag2 = sampleTag.copy(id = "tag2", createdAt = 2000L)
        val tag3 = sampleTag.copy(id = "tag3", createdAt = 1500L)
        
        repository.createTag(tag1)
        repository.createTag(tag2)
        repository.createTag(tag3)

        // When
        val result = repository.getTags()

        // Then
        assertTrue("Should succeed", result is Result.Success)
        val tags = (result as Result.Success).data
        assertEquals("Should have 3 tags", 3, tags.size)
        // Should be sorted descending by creation date
        assertEquals("First should be newest", "tag2", tags[0].id)
        assertEquals("Second should be middle", "tag3", tags[1].id)
        assertEquals("Third should be oldest", "tag1", tags[2].id)
    }

    // ==================== Get Tag By ID Tests ====================

    @Test
    fun `get tag by id returns tag`() = runTest {
        // Given
        repository.createTag(sampleTag)

        // When
        val result = repository.getTagById(sampleTag.id)

        // Then
        assertTrue("Should succeed", result is Result.Success)
        assertEquals("Should return correct tag", sampleTag, (result as Result.Success).data)
    }

    @Test
    fun `get tag by id returns null for non-existent tag`() = runTest {
        // When
        val result = repository.getTagById("nonexistent")

        // Then
        assertTrue("Should succeed", result is Result.Success)
        assertNull("Should return null", (result as Result.Success).data)
    }

    // ==================== Delete Tag Tests ====================

    @Test
    fun `delete tag successfully`() = runTest {
        // Given
        repository.createTag(sampleTag)

        // When
        val result = repository.deleteTag(sampleTag.id)

        // Then
        assertTrue("Should delete successfully", result is Result.Success)
        
        val getResult = repository.getTagById(sampleTag.id)
        assertNull("Tag should not exist", (getResult as Result.Success).data)
    }

    @Test
    fun `delete tag removes file associations`() = runTest {
        // Given: Tag applied to file
        repository.createTag(sampleTag)
        repository.tagFile(sampleUri, sampleTag.id)

        // When: Delete tag
        repository.deleteTag(sampleTag.id)

        // Then: File should have no tags
        val tagsResult = repository.getTagsForFile(sampleUri)
        assertTrue("File should have no tags", 
            (tagsResult as Result.Success).data.isEmpty())
    }

    // ==================== Observe Tags Tests ====================

    @Test
    fun `observe tags emits initial empty list`() = runTest {
        // When
        val tags = repository.observeTags().first()

        // Then
        assertTrue("Should emit empty list", tags.isEmpty())
    }

    @Test
    fun `observe tags emits updates on create`() = runTest {
        // When: Create tag
        repository.createTag(sampleTag)

        // Then: Should emit updated list
        val tags = repository.observeTags().first()
        assertEquals("Should have 1 tag", 1, tags.size)
        assertEquals("Should be the created tag", sampleTag, tags[0])
    }

    // ==================== Tag File Tests ====================

    @Test
    fun `tag file successfully`() = runTest {
        // Given: Tag exists
        repository.createTag(sampleTag)

        // When
        val result = repository.tagFile(sampleUri, sampleTag.id)

        // Then
        assertTrue("Should tag successfully", result is Result.Success)
        
        val tagsResult = repository.getTagsForFile(sampleUri)
        assertEquals("File should have 1 tag", 1, 
            (tagsResult as Result.Success).data.size)
    }

    @Test
    fun `tag file with non-existent tag fails`() = runTest {
        // When
        val result = repository.tagFile(sampleUri, "nonexistent")

        // Then
        assertTrue("Should fail", result is Result.Error)
    }

    @Test
    fun `tag file multiple times is idempotent`() = runTest {
        // Given
        repository.createTag(sampleTag)

        // When: Tag file multiple times
        repository.tagFile(sampleUri, sampleTag.id)
        repository.tagFile(sampleUri, sampleTag.id)

        // Then: Should only have one tag association
        val tagsResult = repository.getTagsForFile(sampleUri)
        assertEquals("Should still have 1 tag", 1, 
            (tagsResult as Result.Success).data.size)
    }

    // ==================== Untag File Tests ====================

    @Test
    fun `untag file successfully`() = runTest {
        // Given: File is tagged
        repository.createTag(sampleTag)
        repository.tagFile(sampleUri, sampleTag.id)

        // When
        val result = repository.untagFile(sampleUri, sampleTag.id)

        // Then
        assertTrue("Should untag successfully", result is Result.Success)
        
        val tagsResult = repository.getTagsForFile(sampleUri)
        assertTrue("File should have no tags", 
            (tagsResult as Result.Success).data.isEmpty())
    }

    @Test
    fun `untag file that is not tagged does nothing`() = runTest {
        // When: Untag file that was never tagged
        val result = repository.untagFile(sampleUri, "tag1")

        // Then: Should succeed without error
        assertTrue("Should succeed", result is Result.Success)
    }

    // ==================== Get Files By Tag Tests ====================

    @Test
    fun `get files by tag returns tagged files`() = runTest {
        // Given: Multiple files tagged
        val uri1 = Uri.parse("content://media/external/images/media/1")
        val uri2 = Uri.parse("content://media/external/images/media/2")
        
        repository.createTag(sampleTag)
        repository.tagFile(uri1, sampleTag.id)
        repository.tagFile(uri2, sampleTag.id)

        // When
        val result = repository.getFilesByTag(sampleTag.id)

        // Then
        assertTrue("Should succeed", result is Result.Success)
        val files = (result as Result.Success).data
        assertEquals("Should have 2 files", 2, files.size)
    }

    @Test
    fun `get files by tag returns empty for untagged tag`() = runTest {
        // Given: Tag exists but no files tagged
        repository.createTag(sampleTag)

        // When
        val result = repository.getFilesByTag(sampleTag.id)

        // Then
        assertTrue("Should succeed", result is Result.Success)
        assertTrue("Should return empty list", 
            (result as Result.Success).data.isEmpty())
    }

    // ==================== Get Tags For File Tests ====================

    @Test
    fun `get tags for file returns all tags`() = runTest {
        // Given: File has multiple tags
        val tag2 = sampleTag.copy(id = "tag2", name = "Work")
        repository.createTag(sampleTag)
        repository.createTag(tag2)
        repository.tagFile(sampleUri, sampleTag.id)
        repository.tagFile(sampleUri, tag2.id)

        // When
        val result = repository.getTagsForFile(sampleUri)

        // Then
        assertTrue("Should succeed", result is Result.Success)
        assertEquals("Should have 2 tags", 2, (result as Result.Success).data.size)
    }

    @Test
    fun `get tags for untagged file returns empty`() = runTest {
        // When
        val result = repository.getTagsForFile(sampleUri)

        // Then
        assertTrue("Should succeed", result is Result.Success)
        assertTrue("Should be empty", (result as Result.Success).data.isEmpty())
    }

    // ==================== Get Tagged File Tests ====================

    @Test
    fun `get tagged file returns file with tags`() = runTest {
        // Given: File is tagged
        repository.createTag(sampleTag)
        repository.tagFile(sampleUri, sampleTag.id)

        // When
        val result = repository.getTaggedFile(sampleUri)

        // Then
        assertTrue("Should succeed", result is Result.Success)
        val taggedFile = (result as Result.Success).data
        assertNotNull("Should return tagged file", taggedFile)
        assertEquals("Should have correct URI", sampleUri, taggedFile?.fileUri)
        assertEquals("Should have 1 tag", 1, taggedFile?.tags?.size)
    }

    @Test
    fun `get tagged file returns null for untagged file`() = runTest {
        // When
        val result = repository.getTaggedFile(sampleUri)

        // Then
        assertTrue("Should succeed", result is Result.Success)
        assertNull("Should return null", (result as Result.Success).data)
    }

    // ==================== Search Files By Tag Name Tests ====================

    @Test
    fun `search files by tag name finds matches`() = runTest {
        // Given: Tags and tagged files
        val vacationTag = sampleTag.copy(id = "tag1", name = "Vacation 2024")
        val workTag = sampleTag.copy(id = "tag2", name = "Work")
        
        repository.createTag(vacationTag)
        repository.createTag(workTag)
        
        val uri1 = Uri.parse("content://media/external/images/media/1")
        repository.tagFile(uri1, vacationTag.id)

        // When: Search for "vacation"
        val result = repository.searchFilesByTagName("vacation")

        // Then
        assertTrue("Should succeed", result is Result.Success)
        val files = (result as Result.Success).data
        assertEquals("Should find 1 file", 1, files.size)
        assertTrue("File should have vacation tag", 
            files[0].tags.any { it.name.contains("Vacation") })
    }

    @Test
    fun `search files by tag name is case insensitive`() = runTest {
        // Given
        repository.createTag(sampleTag.copy(name = "Vacation"))
        repository.tagFile(sampleUri, sampleTag.id)

        // When: Search with different case
        val result = repository.searchFilesByTagName("VACATION")

        // Then
        assertTrue("Should succeed", result is Result.Success)
        assertEquals("Should find 1 file", 1, (result as Result.Success).data.size)
    }
}
