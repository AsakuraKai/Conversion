package com.example.conversion.domain.usecase.sort

import android.net.Uri
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.model.SortStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for SortFilesUseCase.
 * Tests all sorting strategies with various file scenarios.
 */
class SortFilesUseCaseTest {

    private lateinit var useCase: SortFilesUseCase

    @Before
    fun setup() {
        useCase = SortFilesUseCase(Dispatchers.Unconfined)
    }

    // ========== NATURAL SORT TESTS ==========

    @Test
    fun `natural sort handles numeric filenames correctly`() = runTest {
        // Given: Files with numbers that should sort naturally
        val files = listOf(
            createFileItem(1L, "file1.jpg"),
            createFileItem(2L, "file10.jpg"),
            createFileItem(3L, "file2.jpg"),
            createFileItem(4L, "file20.jpg"),
            createFileItem(5L, "file100.jpg")
        )
        val params = SortFilesUseCase.Params(files, SortStrategy.NATURAL)

        // When: Sort naturally
        val result = useCase(params)

        // Then: Should be sorted as file1, file2, file10, file20, file100
        assertTrue("Result should be success", result is Result.Success)
        val sorted = result.getOrNull()!!
        assertEquals("file1.jpg", sorted[0].name)
        assertEquals("file2.jpg", sorted[1].name)
        assertEquals("file10.jpg", sorted[2].name)
        assertEquals("file20.jpg", sorted[3].name)
        assertEquals("file100.jpg", sorted[4].name)
    }

    @Test
    fun `natural sort handles padded numbers`() = runTest {
        // Given: Files with zero-padded numbers
        val files = listOf(
            createFileItem(1L, "img001.jpg"),
            createFileItem(2L, "img010.jpg"),
            createFileItem(3L, "img100.jpg"),
            createFileItem(4L, "img002.jpg")
        )
        val params = SortFilesUseCase.Params(files, SortStrategy.NATURAL)

        // When: Sort naturally
        val result = useCase(params)

        // Then: Should sort numerically
        val sorted = result.getOrNull()!!
        assertEquals("img001.jpg", sorted[0].name)
        assertEquals("img002.jpg", sorted[1].name)
        assertEquals("img010.jpg", sorted[2].name)
        assertEquals("img100.jpg", sorted[3].name)
    }

    @Test
    fun `natural sort handles mixed alphanumeric names`() = runTest {
        // Given: Complex filenames with multiple numeric parts
        val files = listOf(
            createFileItem(1L, "photo_2_final.jpg"),
            createFileItem(2L, "photo_10_draft.jpg"),
            createFileItem(3L, "photo_1_final.jpg"),
            createFileItem(4L, "photo_2_draft.jpg")
        )
        val params = SortFilesUseCase.Params(files, SortStrategy.NATURAL)

        // When: Sort naturally
        val result = useCase(params)

        // Then: Should sort by first number, then by text
        val sorted = result.getOrNull()!!
        assertEquals("photo_1_final.jpg", sorted[0].name)
        assertEquals("photo_2_draft.jpg", sorted[1].name)
        assertEquals("photo_2_final.jpg", sorted[2].name)
        assertEquals("photo_10_draft.jpg", sorted[3].name)
    }

    @Test
    fun `natural sort handles filenames without numbers`() = runTest {
        // Given: Files with only alphabetic names
        val files = listOf(
            createFileItem(1L, "zebra.jpg"),
            createFileItem(2L, "apple.jpg"),
            createFileItem(3L, "banana.jpg")
        )
        val params = SortFilesUseCase.Params(files, SortStrategy.NATURAL)

        // When: Sort naturally
        val result = useCase(params)

        // Then: Should sort alphabetically
        val sorted = result.getOrNull()!!
        assertEquals("apple.jpg", sorted[0].name)
        assertEquals("banana.jpg", sorted[1].name)
        assertEquals("zebra.jpg", sorted[2].name)
    }

    @Test
    fun `natural sort is case-insensitive`() = runTest {
        // Given: Files with different cases
        val files = listOf(
            createFileItem(1L, "File_B.jpg"),
            createFileItem(2L, "file_a.jpg"),
            createFileItem(3L, "FILE_C.jpg")
        )
        val params = SortFilesUseCase.Params(files, SortStrategy.NATURAL)

        // When: Sort naturally
        val result = useCase(params)

        // Then: Should sort case-insensitively
        val sorted = result.getOrNull()!!
        assertEquals("file_a.jpg", sorted[0].name)
        assertEquals("File_B.jpg", sorted[1].name)
        assertEquals("FILE_C.jpg", sorted[2].name)
    }

    @Test
    fun `natural sort handles special characters`() = runTest {
        // Given: Files with special characters
        val files = listOf(
            createFileItem(1L, "photo_10.jpg"),
            createFileItem(2L, "photo-5.jpg"),
            createFileItem(3L, "photo.2.jpg"),
            createFileItem(4L, "photo_1.jpg")
        )
        val params = SortFilesUseCase.Params(files, SortStrategy.NATURAL)

        // When: Sort naturally
        val result = useCase(params)

        // Then: Special chars should be sorted, numbers sorted numerically
        val sorted = result.getOrNull()!!
        // photo.2, photo-5, photo_1, photo_10
        assertTrue(sorted[0].name.contains("2"))
        assertTrue(sorted[3].name.contains("10"))
    }

    @Test
    fun `natural sort handles very large numbers`() = runTest {
        // Given: Files with large numbers
        val files = listOf(
            createFileItem(1L, "img99999.jpg"),
            createFileItem(2L, "img1000000.jpg"),
            createFileItem(3L, "img100.jpg")
        )
        val params = SortFilesUseCase.Params(files, SortStrategy.NATURAL)

        // When: Sort naturally
        val result = useCase(params)

        // Then: Should handle large numbers correctly
        val sorted = result.getOrNull()!!
        assertEquals("img100.jpg", sorted[0].name)
        assertEquals("img99999.jpg", sorted[1].name)
        assertEquals("img1000000.jpg", sorted[2].name)
    }

    @Test
    fun `natural sort handles filenames with multiple numeric segments`() = runTest {
        // Given: Files with multiple numbers
        val files = listOf(
            createFileItem(1L, "video_1_scene_10.mp4"),
            createFileItem(2L, "video_2_scene_5.mp4"),
            createFileItem(3L, "video_1_scene_2.mp4"),
            createFileItem(4L, "video_10_scene_1.mp4")
        )
        val params = SortFilesUseCase.Params(files, SortStrategy.NATURAL)

        // When: Sort naturally
        val result = useCase(params)

        // Then: Should sort by first number, then second number
        val sorted = result.getOrNull()!!
        assertEquals("video_1_scene_2.mp4", sorted[0].name)
        assertEquals("video_1_scene_10.mp4", sorted[1].name)
        assertEquals("video_2_scene_5.mp4", sorted[2].name)
        assertEquals("video_10_scene_1.mp4", sorted[3].name)
    }

    // ========== DATE MODIFIED SORT TESTS ==========

    @Test
    fun `date modified sort orders newest first`() = runTest {
        // Given: Files with different modification dates
        val files = listOf(
            createFileItem(1L, "old.jpg", dateModified = 1000L),
            createFileItem(2L, "newest.jpg", dateModified = 3000L),
            createFileItem(3L, "middle.jpg", dateModified = 2000L)
        )
        val params = SortFilesUseCase.Params(files, SortStrategy.DATE_MODIFIED)

        // When: Sort by date
        val result = useCase(params)

        // Then: Should be newest first
        val sorted = result.getOrNull()!!
        assertEquals("newest.jpg", sorted[0].name)
        assertEquals("middle.jpg", sorted[1].name)
        assertEquals("old.jpg", sorted[2].name)
    }

    @Test
    fun `date modified sort handles same timestamps`() = runTest {
        // Given: Files with same modification date
        val sameDate = System.currentTimeMillis()
        val files = listOf(
            createFileItem(1L, "file1.jpg", dateModified = sameDate),
            createFileItem(2L, "file2.jpg", dateModified = sameDate),
            createFileItem(3L, "file3.jpg", dateModified = sameDate)
        )
        val params = SortFilesUseCase.Params(files, SortStrategy.DATE_MODIFIED)

        // When: Sort by date
        val result = useCase(params)

        // Then: Should succeed (order among same dates is stable)
        assertTrue(result is Result.Success)
        assertEquals(3, result.getOrNull()!!.size)
    }

    // ========== SIZE SORT TESTS ==========

    @Test
    fun `size sort orders largest first`() = runTest {
        // Given: Files with different sizes
        val files = listOf(
            createFileItem(1L, "small.jpg", size = 100L),
            createFileItem(2L, "large.jpg", size = 5000L),
            createFileItem(3L, "medium.jpg", size = 1000L)
        )
        val params = SortFilesUseCase.Params(files, SortStrategy.SIZE)

        // When: Sort by size
        val result = useCase(params)

        // Then: Should be largest first
        val sorted = result.getOrNull()!!
        assertEquals("large.jpg", sorted[0].name)
        assertEquals("medium.jpg", sorted[1].name)
        assertEquals("small.jpg", sorted[2].name)
    }

    @Test
    fun `size sort handles zero-byte files`() = runTest {
        // Given: Files including zero-byte files
        val files = listOf(
            createFileItem(1L, "normal.jpg", size = 1024L),
            createFileItem(2L, "empty.txt", size = 0L),
            createFileItem(3L, "large.mp4", size = 10240L)
        )
        val params = SortFilesUseCase.Params(files, SortStrategy.SIZE)

        // When: Sort by size
        val result = useCase(params)

        // Then: Empty file should be last
        val sorted = result.getOrNull()!!
        assertEquals("large.mp4", sorted[0].name)
        assertEquals("normal.jpg", sorted[1].name)
        assertEquals("empty.txt", sorted[2].name)
    }

    @Test
    fun `size sort handles very large files`() = runTest {
        // Given: Files with very large sizes
        val files = listOf(
            createFileItem(1L, "huge.mp4", size = Long.MAX_VALUE),
            createFileItem(2L, "big.mp4", size = 1_000_000_000L),
            createFileItem(3L, "small.jpg", size = 1000L)
        )
        val params = SortFilesUseCase.Params(files, SortStrategy.SIZE)

        // When: Sort by size
        val result = useCase(params)

        // Then: Should handle large numbers
        val sorted = result.getOrNull()!!
        assertEquals("huge.mp4", sorted[0].name)
        assertEquals("big.mp4", sorted[1].name)
        assertEquals("small.jpg", sorted[2].name)
    }

    // ========== ORIGINAL ORDER TESTS ==========

    @Test
    fun `original order preserves input sequence`() = runTest {
        // Given: Files in specific order
        val files = listOf(
            createFileItem(1L, "zebra.jpg"),
            createFileItem(2L, "apple.jpg"),
            createFileItem(3L, "middle.jpg")
        )
        val params = SortFilesUseCase.Params(files, SortStrategy.ORIGINAL_ORDER)

        // When: Use original order
        val result = useCase(params)

        // Then: Should preserve exact input order
        val sorted = result.getOrNull()!!
        assertEquals("zebra.jpg", sorted[0].name)
        assertEquals("apple.jpg", sorted[1].name)
        assertEquals("middle.jpg", sorted[2].name)
    }

    // ========== EDGE CASES ==========

    @Test
    fun `sort handles empty list`() = runTest {
        // Given: Empty list
        val params = SortFilesUseCase.Params(emptyList(), SortStrategy.NATURAL)

        // When: Sort
        val result = useCase(params)

        // Then: Should return empty list
        assertTrue(result is Result.Success)
        assertTrue(result.getOrNull()!!.isEmpty())
    }

    @Test
    fun `sort handles single file`() = runTest {
        // Given: Single file
        val files = listOf(createFileItem(1L, "single.jpg"))
        val params = SortFilesUseCase.Params(files, SortStrategy.NATURAL)

        // When: Sort
        val result = useCase(params)

        // Then: Should return same file
        val sorted = result.getOrNull()!!
        assertEquals(1, sorted.size)
        assertEquals("single.jpg", sorted[0].name)
    }

    @Test
    fun `sort handles duplicate filenames`() = runTest {
        // Given: Files with same name
        val files = listOf(
            createFileItem(1L, "duplicate.jpg"),
            createFileItem(2L, "duplicate.jpg"),
            createFileItem(3L, "duplicate.jpg")
        )
        val params = SortFilesUseCase.Params(files, SortStrategy.NATURAL)

        // When: Sort
        val result = useCase(params)

        // Then: Should handle gracefully
        assertTrue(result is Result.Success)
        assertEquals(3, result.getOrNull()!!.size)
    }

    @Test
    fun `natural sort handles filenames with only numbers`() = runTest {
        // Given: Files named with just numbers
        val files = listOf(
            createFileItem(1L, "100.jpg"),
            createFileItem(2L, "10.jpg"),
            createFileItem(3L, "1.jpg"),
            createFileItem(4L, "1000.jpg")
        )
        val params = SortFilesUseCase.Params(files, SortStrategy.NATURAL)

        // When: Sort naturally
        val result = useCase(params)

        // Then: Should sort numerically
        val sorted = result.getOrNull()!!
        assertEquals("1.jpg", sorted[0].name)
        assertEquals("10.jpg", sorted[1].name)
        assertEquals("100.jpg", sorted[2].name)
        assertEquals("1000.jpg", sorted[3].name)
    }

    @Test
    fun `natural sort handles filenames with leading zeros`() = runTest {
        // Given: Files with leading zeros
        val files = listOf(
            createFileItem(1L, "file0010.jpg"),
            createFileItem(2L, "file0001.jpg"),
            createFileItem(3L, "file0100.jpg")
        )
        val params = SortFilesUseCase.Params(files, SortStrategy.NATURAL)

        // When: Sort naturally
        val result = useCase(params)

        // Then: Should sort numerically
        val sorted = result.getOrNull()!!
        assertEquals("file0001.jpg", sorted[0].name)
        assertEquals("file0010.jpg", sorted[1].name)
        assertEquals("file0100.jpg", sorted[2].name)
    }

    @Test
    fun `natural sort handles Unicode characters`() = runTest {
        // Given: Files with Unicode characters
        val files = listOf(
            createFileItem(1L, "文件10.jpg"),
            createFileItem(2L, "文件2.jpg"),
            createFileItem(3L, "文件1.jpg")
        )
        val params = SortFilesUseCase.Params(files, SortStrategy.NATURAL)

        // When: Sort naturally
        val result = useCase(params)

        // Then: Should sort numerically within Unicode text
        val sorted = result.getOrNull()!!
        assertTrue(sorted[0].name.contains("1"))
        assertTrue(sorted[1].name.contains("2"))
        assertTrue(sorted[2].name.contains("10"))
    }

    @Test
    fun `sort strategies are independent`() = runTest {
        // Given: Same files
        val files = listOf(
            createFileItem(1L, "z10.jpg", dateModified = 1000L, size = 100L),
            createFileItem(2L, "a2.jpg", dateModified = 3000L, size = 500L),
            createFileItem(3L, "m5.jpg", dateModified = 2000L, size = 300L)
        )

        // When: Sort with different strategies
        val naturalResult = useCase(SortFilesUseCase.Params(files, SortStrategy.NATURAL))
        val dateResult = useCase(SortFilesUseCase.Params(files, SortStrategy.DATE_MODIFIED))
        val sizeResult = useCase(SortFilesUseCase.Params(files, SortStrategy.SIZE))
        val originalResult = useCase(SortFilesUseCase.Params(files, SortStrategy.ORIGINAL_ORDER))

        // Then: Results should be different
        val natural = naturalResult.getOrNull()!!
        val byDate = dateResult.getOrNull()!!
        val bySize = sizeResult.getOrNull()!!
        val original = originalResult.getOrNull()!!

        // Natural: a2, m5, z10
        assertEquals("a2.jpg", natural[0].name)
        
        // Date: a2 (3000), m5 (2000), z10 (1000)
        assertEquals("a2.jpg", byDate[0].name)
        
        // Size: a2 (500), m5 (300), z10 (100)
        assertEquals("a2.jpg", bySize[0].name)
        
        // Original: z10, a2, m5
        assertEquals("z10.jpg", original[0].name)
    }

    // ========== HELPER METHODS ==========

    /**
     * Creates a test FileItem with specified properties.
     */
    private fun createFileItem(
        id: Long,
        name: String,
        dateModified: Long = System.currentTimeMillis(),
        size: Long = 1024L
    ): FileItem {
        return FileItem(
            id = id,
            uri = Uri.parse("content://media/external/images/$id"),
            name = name,
            path = "/storage/emulated/0/DCIM/$name",
            size = size,
            mimeType = "image/jpeg",
            dateModified = dateModified,
            thumbnailUri = null
        )
    }
}
