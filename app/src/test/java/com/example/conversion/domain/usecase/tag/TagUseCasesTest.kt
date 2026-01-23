package com.example.conversion.domain.usecase.tag

import android.net.Uri
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.model.FileTag
import com.example.conversion.domain.repository.TagRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for Tag System use cases.
 * Tests all tag management operations.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TagUseCasesTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var tagRepository: TagRepository

    // Use cases
    private lateinit var createTagUseCase: CreateTagUseCase
    private lateinit var getTagsUseCase: GetTagsUseCase
    private lateinit var tagFileUseCase: TagFileUseCase
    private lateinit var searchByTagUseCase: SearchByTagUseCase
    private lateinit var deleteTagUseCase: DeleteTagUseCase
    private lateinit var getFileTagsUseCase: GetFileTagsUseCase
    private lateinit var untagFileUseCase: UntagFileUseCase

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
        tagRepository = mockk()

        // Initialize use cases
        createTagUseCase = CreateTagUseCase(tagRepository)
        getTagsUseCase = GetTagsUseCase(tagRepository)
        tagFileUseCase = TagFileUseCase(tagRepository)
        searchByTagUseCase = SearchByTagUseCase(tagRepository)
        deleteTagUseCase = DeleteTagUseCase(tagRepository)
        getFileTagsUseCase = GetFileTagsUseCase(tagRepository)
        untagFileUseCase = UntagFileUseCase(tagRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    // ==================== CreateTagUseCase Tests ====================

    @Test
    fun `create valid tag successfully`() = runTest {
        // Given
        coEvery { tagRepository.createTag(any()) } returns Result.Success(Unit)

        // When
        val result = createTagUseCase(sampleTag)

        // Then
        assertTrue("Should create tag successfully", result is Result.Success)
        coVerify(exactly = 1) { tagRepository.createTag(sampleTag) }
    }

    @Test
    fun `create tag with blank name fails`() = runTest {
        // Given: Tag with blank name
        val invalidTag = sampleTag.copy(name = "")

        // When
        val result = createTagUseCase(invalidTag)

        // Then
        assertTrue("Should fail validation", result is Result.Error)
        val error = result as Result.Error
        assertTrue("Error message mentions invalid tag", 
            error.message?.contains("Invalid tag") == true)
        coVerify(exactly = 0) { tagRepository.createTag(any()) }
    }

    @Test
    fun `create tag with invalid color fails`() = runTest {
        // Given: Tag with invalid color format
        val invalidTag = sampleTag.copy(color = "red")

        // When
        val result = createTagUseCase(invalidTag)

        // Then
        assertTrue("Should fail validation", result is Result.Error)
        coVerify(exactly = 0) { tagRepository.createTag(any()) }
    }

    @Test
    fun `create tag handles repository error`() = runTest {
        // Given
        val exception = IllegalStateException("Database error")
        coEvery { tagRepository.createTag(any()) } returns Result.Error(exception)

        // When
        val result = createTagUseCase(sampleTag)

        // Then
        assertTrue("Should return error", result is Result.Error)
        assertEquals("Error should match", exception, (result as Result.Error).exception)
    }

    // ==================== GetTagsUseCase Tests ====================

    @Test
    fun `get tags returns list successfully`() = runTest {
        // Given
        val tags = listOf(sampleTag, sampleTag.copy(id = "tag2", name = "Work"))
        coEvery { tagRepository.getTags() } returns Result.Success(tags)

        // When
        val result = getTagsUseCase()

        // Then
        assertTrue("Should return success", result is Result.Success)
        assertEquals("Should return 2 tags", 2, (result as Result.Success).data.size)
        coVerify(exactly = 1) { tagRepository.getTags() }
    }

    @Test
    fun `get tags returns empty list`() = runTest {
        // Given
        coEvery { tagRepository.getTags() } returns Result.Success(emptyList())

        // When
        val result = getTagsUseCase()

        // Then
        assertTrue("Should return success", result is Result.Success)
        assertTrue("Should return empty list", (result as Result.Success).data.isEmpty())
    }

    // ==================== TagFileUseCase Tests ====================

    @Test
    fun `tag file successfully`() = runTest {
        // Given
        coEvery { tagRepository.getTagById("tag1") } returns Result.Success(sampleTag)
        coEvery { tagRepository.tagFile(any(), any()) } returns Result.Success(Unit)

        // When
        val result = tagFileUseCase(sampleUri, "tag1")

        // Then
        assertTrue("Should tag file successfully", result is Result.Success)
        coVerify(exactly = 1) { tagRepository.getTagById("tag1") }
        coVerify(exactly = 1) { tagRepository.tagFile(sampleUri, "tag1") }
    }

    @Test
    fun `tag file with blank tag id fails`() = runTest {
        // When
        val result = tagFileUseCase(sampleUri, "")

        // Then
        assertTrue("Should fail validation", result is Result.Error)
        coVerify(exactly = 0) { tagRepository.getTagById(any()) }
        coVerify(exactly = 0) { tagRepository.tagFile(any(), any()) }
    }

    @Test
    fun `tag file with non-existent tag fails`() = runTest {
        // Given
        coEvery { tagRepository.getTagById("nonexistent") } returns Result.Success(null)

        // When
        val result = tagFileUseCase(sampleUri, "nonexistent")

        // Then
        assertTrue("Should fail", result is Result.Error)
        val error = result as Result.Error
        assertTrue("Error mentions tag does not exist", 
            error.message?.contains("does not exist") == true)
        coVerify(exactly = 0) { tagRepository.tagFile(any(), any()) }
    }

    // ==================== SearchByTagUseCase Tests ====================

    @Test
    fun `search by tag returns files successfully`() = runTest {
        // Given
        val files = listOf(
            FileItem(
                id = "1",
                uri = sampleUri,
                name = "photo.jpg",
                path = "/storage/photo.jpg",
                size = 1024L,
                mimeType = "image/jpeg",
                dateModified = System.currentTimeMillis(),
                thumbnailUri = sampleUri
            )
        )
        coEvery { tagRepository.getFilesByTag("tag1") } returns Result.Success(files)

        // When
        val result = searchByTagUseCase("tag1")

        // Then
        assertTrue("Should return success", result is Result.Success)
        assertEquals("Should return 1 file", 1, (result as Result.Success).data.size)
    }

    @Test
    fun `search by tag with blank id fails`() = runTest {
        // When
        val result = searchByTagUseCase("")

        // Then
        assertTrue("Should fail validation", result is Result.Error)
        coVerify(exactly = 0) { tagRepository.getFilesByTag(any()) }
    }

    // ==================== DeleteTagUseCase Tests ====================

    @Test
    fun `delete tag successfully`() = runTest {
        // Given
        coEvery { tagRepository.deleteTag("tag1") } returns Result.Success(Unit)

        // When
        val result = deleteTagUseCase("tag1")

        // Then
        assertTrue("Should delete successfully", result is Result.Success)
        coVerify(exactly = 1) { tagRepository.deleteTag("tag1") }
    }

    @Test
    fun `delete tag with blank id fails`() = runTest {
        // When
        val result = deleteTagUseCase("")

        // Then
        assertTrue("Should fail validation", result is Result.Error)
        coVerify(exactly = 0) { tagRepository.deleteTag(any()) }
    }

    // ==================== GetFileTagsUseCase Tests ====================

    @Test
    fun `get file tags returns tags successfully`() = runTest {
        // Given
        val tags = listOf(sampleTag)
        coEvery { tagRepository.getTagsForFile(sampleUri) } returns Result.Success(tags)

        // When
        val result = getFileTagsUseCase(sampleUri)

        // Then
        assertTrue("Should return success", result is Result.Success)
        assertEquals("Should return 1 tag", 1, (result as Result.Success).data.size)
    }

    @Test
    fun `get file tags returns empty list`() = runTest {
        // Given
        coEvery { tagRepository.getTagsForFile(sampleUri) } returns Result.Success(emptyList())

        // When
        val result = getFileTagsUseCase(sampleUri)

        // Then
        assertTrue("Should return success", result is Result.Success)
        assertTrue("Should return empty list", (result as Result.Success).data.isEmpty())
    }

    // ==================== UntagFileUseCase Tests ====================

    @Test
    fun `untag file successfully`() = runTest {
        // Given
        coEvery { tagRepository.untagFile(any(), any()) } returns Result.Success(Unit)

        // When
        val result = untagFileUseCase(sampleUri, "tag1")

        // Then
        assertTrue("Should untag successfully", result is Result.Success)
        coVerify(exactly = 1) { tagRepository.untagFile(sampleUri, "tag1") }
    }

    @Test
    fun `untag file with blank tag id fails`() = runTest {
        // When
        val result = untagFileUseCase(sampleUri, "")

        // Then
        assertTrue("Should fail validation", result is Result.Error)
        coVerify(exactly = 0) { tagRepository.untagFile(any(), any()) }
    }

    // ==================== FileTag Model Tests ====================

    @Test
    fun `file tag validation with valid data`() {
        // Given: Valid tag
        // When
        val isValid = sampleTag.isValid()

        // Then
        assertTrue("Should be valid", isValid)
    }

    @Test
    fun `file tag validation with blank name`() {
        // Given: Tag with blank name
        val invalidTag = sampleTag.copy(name = "")

        // When
        val isValid = invalidTag.isValid()

        // Then
        assertFalse("Should be invalid", isValid)
    }

    @Test
    fun `file tag validation with invalid color format`() {
        // Given: Tag with invalid color
        val invalidTag = sampleTag.copy(color = "blue")

        // When
        val isValid = invalidTag.isValid()

        // Then
        assertFalse("Should be invalid", isValid)
    }

    @Test
    fun `file tag validation with too long name`() {
        // Given: Tag with name exceeding max length
        val longName = "a".repeat(FileTag.MAX_NAME_LENGTH + 1)
        val invalidTag = sampleTag.copy(name = longName)

        // When
        val isValid = invalidTag.isValid()

        // Then
        assertFalse("Should be invalid", isValid)
    }

    @Test
    fun `predefined colors are all valid`() {
        // When/Then: Check all predefined colors
        FileTag.PREDEFINED_COLORS.forEach { color ->
            assertTrue("Color $color should be valid", 
                color.matches(Regex("^#[0-9A-Fa-f]{6}$")))
        }
    }
}
