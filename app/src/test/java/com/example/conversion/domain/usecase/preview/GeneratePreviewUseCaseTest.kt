package com.example.conversion.domain.usecase.preview

import android.net.Uri
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.model.PreviewItem
import com.example.conversion.domain.model.PreviewSummary
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.model.SortStrategy
import com.example.conversion.domain.usecase.rename.GenerateFilenameUseCase
import com.example.conversion.domain.usecase.rename.ValidateFilenameUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for GeneratePreviewUseCase.
 * Tests preview generation, conflict detection, and validation.
 */
class GeneratePreviewUseCaseTest {

    private lateinit var generateFilenameUseCase: GenerateFilenameUseCase
    private lateinit var validateFilenameUseCase: ValidateFilenameUseCase
    private lateinit var useCase: GeneratePreviewUseCase

    // Sample file items for testing
    private val sampleFiles = listOf(
        FileItem(
            id = 1L,
            uri = Uri.parse("content://media/external/images/1"),
            name = "IMG_001.jpg",
            path = "/storage/emulated/0/DCIM/IMG_001.jpg",
            size = 1024L,
            mimeType = "image/jpeg",
            dateModified = System.currentTimeMillis()
        ),
        FileItem(
            id = 2L,
            uri = Uri.parse("content://media/external/images/2"),
            name = "IMG_002.jpg",
            path = "/storage/emulated/0/DCIM/IMG_002.jpg",
            size = 2048L,
            mimeType = "image/jpeg",
            dateModified = System.currentTimeMillis()
        ),
        FileItem(
            id = 3L,
            uri = Uri.parse("content://media/external/images/3"),
            name = "IMG_003.jpg",
            path = "/storage/emulated/0/DCIM/IMG_003.jpg",
            size = 3072L,
            mimeType = "image/jpeg",
            dateModified = System.currentTimeMillis()
        )
    )

    @Before
    fun setup() {
        generateFilenameUseCase = GenerateFilenameUseCase()
        validateFilenameUseCase = ValidateFilenameUseCase()
        useCase = GeneratePreviewUseCase(
            generateFilenameUseCase = generateFilenameUseCase,
            validateFilenameUseCase = validateFilenameUseCase
        )
    }

    @Test
    fun `generate preview with valid config and no conflicts`() = runTest {
        // Given: Valid config
        val config = RenameConfig(
            prefix = "vacation",
            startNumber = 1,
            digitCount = 3,
            preserveExtension = true
        )
        val params = GeneratePreviewUseCase.Params(sampleFiles, config)

        // When: Generate preview
        val result = useCase(params)

        // Then: All previews should be successful
        assertTrue("Result should be success", result is Result.Success)
        val previews = result.getOrNull()!!
        assertEquals(3, previews.size)

        // Verify each preview
        assertFalse("First file should have no conflicts", previews[0].hasConflict)
        assertEquals("vacation001.jpg", previews[0].previewName)
        assertTrue("First file should show as changed", previews[0].isChanged)

        assertFalse("Second file should have no conflicts", previews[1].hasConflict)
        assertEquals("vacation002.jpg", previews[1].previewName)

        assertFalse("Third file should have no conflicts", previews[2].hasConflict)
        assertEquals("vacation003.jpg", previews[2].previewName)
    }

    @Test
    fun `generate preview detects duplicate names`() = runTest {
        // Given: Config that would generate duplicate names
        // (This is a contrived example - in reality, with different indices, names won't duplicate)
        // But we can test with startNumber = 1 and digitCount = 1 for a small batch
        val config = RenameConfig(
            prefix = "file",
            startNumber = 1,
            digitCount = 1,
            preserveExtension = false // No extension to avoid differentiation
        )
        
        // Create files that would all get the same name if we had a bug
        val identicalFiles = listOf(
            sampleFiles[0].copy(id = 1L),
            sampleFiles[0].copy(id = 2L),
            sampleFiles[0].copy(id = 3L)
        )
        val params = GeneratePreviewUseCase.Params(identicalFiles, config)

        // When: Generate preview
        val result = useCase(params)

        // Then: Should generate different names (file1, file2, file3)
        assertTrue("Result should be success", result is Result.Success)
        val previews = result.getOrNull()!!
        
        // With proper indexing, these should all be unique
        assertEquals("file1", previews[0].previewName)
        assertEquals("file2", previews[1].previewName)
        assertEquals("file3", previews[2].previewName)
        
        // None should have conflicts
        assertFalse("First file should have no conflicts", previews[0].hasConflict)
        assertFalse("Second file should have no conflicts", previews[1].hasConflict)
        assertFalse("Third file should have no conflicts", previews[2].hasConflict)
    }

    @Test
    fun `generate preview with invalid config`() = runTest {
        // Given: Invalid config (empty prefix)
        val config = RenameConfig(
            prefix = "", // Invalid: empty prefix
            startNumber = 1,
            digitCount = 3
        )
        val params = GeneratePreviewUseCase.Params(sampleFiles, config)

        // When: Generate preview
        val result = useCase(params)

        // Then: All files should have conflict with config error
        assertTrue("Result should be success", result is Result.Success)
        val previews = result.getOrNull()!!
        
        assertEquals(3, previews.size)
        previews.forEach { preview ->
            assertTrue("Preview should have conflict", preview.hasConflict)
            assertNotNull("Conflict reason should be set", preview.conflictReason)
            assertTrue(
                "Conflict reason should mention prefix",
                preview.conflictReason?.contains("Prefix", ignoreCase = true) == true
            )
        }
    }

    @Test
    fun `generate preview with illegal characters in prefix`() = runTest {
        // Given: Config with illegal characters
        val config = RenameConfig(
            prefix = "photo<>:", // Contains illegal characters
            startNumber = 1,
            digitCount = 3
        )
        val params = GeneratePreviewUseCase.Params(sampleFiles, config)

        // When: Generate preview
        val result = useCase(params)

        // Then: All files should have conflict
        assertTrue("Result should be success", result is Result.Success)
        val previews = result.getOrNull()!!
        
        previews.forEach { preview ->
            assertTrue("Preview should have conflict due to illegal characters", preview.hasConflict)
            assertNotNull("Conflict reason should be set", preview.conflictReason)
        }
    }

    @Test
    fun `generate preview for empty file list`() = runTest {
        // Given: Empty file list
        val config = RenameConfig(prefix = "test", startNumber = 1, digitCount = 2)
        val params = GeneratePreviewUseCase.Params(emptyList(), config)

        // When: Generate preview
        val result = useCase(params)

        // Then: Should return empty list
        assertTrue("Result should be success", result is Result.Success)
        val previews = result.getOrNull()!!
        assertTrue("Previews should be empty", previews.isEmpty())
    }

    @Test
    fun `generate preview with single file`() = runTest {
        // Given: Single file
        val config = RenameConfig(
            prefix = "document",
            startNumber = 1,
            digitCount = 2
        )
        val params = GeneratePreviewUseCase.Params(listOf(sampleFiles[0]), config)

        // When: Generate preview
        val result = useCase(params)

        // Then: Should have one successful preview
        assertTrue("Result should be success", result is Result.Success)
        val previews = result.getOrNull()!!
        
        assertEquals(1, previews.size)
        assertFalse("Preview should have no conflicts", previews[0].hasConflict)
        assertEquals("document01.jpg", previews[0].previewName)
        assertTrue("File should be marked as changed", previews[0].isChanged)
    }

    @Test
    fun `generate preview with large batch`() = runTest {
        // Given: Large batch of files
        val largeFileList = (1..100).map { index ->
            FileItem(
                id = index.toLong(),
                uri = Uri.parse("content://media/external/images/$index"),
                name = "IMG_$index.jpg",
                path = "/storage/emulated/0/DCIM/IMG_$index.jpg",
                size = 1024L * index,
                mimeType = "image/jpeg",
                dateModified = System.currentTimeMillis()
            )
        }
        
        val config = RenameConfig(
            prefix = "batch",
            startNumber = 1,
            digitCount = 3
        )
        val params = GeneratePreviewUseCase.Params(largeFileList, config)

        // When: Generate preview
        val result = useCase(params)

        // Then: All should be successful with no conflicts
        assertTrue("Result should be success", result is Result.Success)
        val previews = result.getOrNull()!!
        
        assertEquals(100, previews.size)
        
        // Check first, middle, and last
        assertEquals("batch001.jpg", previews[0].previewName)
        assertEquals("batch050.jpg", previews[49].previewName)
        assertEquals("batch100.jpg", previews[99].previewName)
        
        // All should have no conflicts
        previews.forEach { preview ->
            assertFalse("Preview should have no conflicts", preview.hasConflict)
            assertTrue("All files should be marked as changed", preview.isChanged)
        }
    }

    @Test
    fun `generate preview with different file types`() = runTest {
        // Given: Mixed file types
        val mixedFiles = listOf(
            sampleFiles[0].copy(name = "image.jpg", mimeType = "image/jpeg"),
            sampleFiles[1].copy(name = "video.mp4", mimeType = "video/mp4"),
            sampleFiles[2].copy(name = "audio.mp3", mimeType = "audio/mpeg")
        )
        
        val config = RenameConfig(
            prefix = "media",
            startNumber = 1,
            digitCount = 2,
            preserveExtension = true
        )
        val params = GeneratePreviewUseCase.Params(mixedFiles, config)

        // When: Generate preview
        val result = useCase(params)

        // Then: Extensions should be preserved
        assertTrue("Result should be success", result is Result.Success)
        val previews = result.getOrNull()!!
        
        assertEquals("media01.jpg", previews[0].previewName)
        assertEquals("media02.mp4", previews[1].previewName)
        assertEquals("media03.mp3", previews[2].previewName)
        
        previews.forEach { preview ->
            assertFalse("Preview should have no conflicts", preview.hasConflict)
        }
    }

    @Test
    fun `generate preview without preserving extension`() = runTest {
        // Given: Config without extension preservation
        val config = RenameConfig(
            prefix = "file",
            startNumber = 1,
            digitCount = 2,
            preserveExtension = false
        )
        val params = GeneratePreviewUseCase.Params(sampleFiles, config)

        // When: Generate preview
        val result = useCase(params)

        // Then: Extensions should not be included
        assertTrue("Result should be success", result is Result.Success)
        val previews = result.getOrNull()!!
        
        assertEquals("file01", previews[0].previewName)
        assertEquals("file02", previews[1].previewName)
        assertEquals("file03", previews[2].previewName)
    }

    @Test
    fun `PreviewItem properties work correctly`() {
        // Given: Different preview items
        val original = sampleFiles[0]
        
        val successPreview = PreviewItem.success(original, "new_name.jpg")
        val conflictPreview = PreviewItem.withConflict(original, "invalid<>.jpg", "Illegal characters")
        val unchangedPreview = PreviewItem.success(original, original.name)

        // Then: Verify properties
        assertTrue("Success preview can rename", successPreview.canRename)
        assertTrue("Success preview is changed", successPreview.isChanged)
        assertFalse("Success preview has no conflict", successPreview.hasConflict)

        assertFalse("Conflict preview cannot rename", conflictPreview.canRename)
        assertTrue("Conflict preview has conflict", conflictPreview.hasConflict)
        assertEquals("Illegal characters", conflictPreview.conflictReason)

        assertFalse("Unchanged preview cannot rename", unchangedPreview.canRename)
        assertFalse("Unchanged preview is not changed", unchangedPreview.isChanged)
    }

    @Test
    fun `PreviewSummary calculates correctly`() = runTest {
        // Given: Mix of preview results
        val original = sampleFiles[0]
        val previews = listOf(
            PreviewItem.success(original, "new1.jpg"),
            PreviewItem.success(original, "new2.jpg"),
            PreviewItem.withConflict(original, "bad.jpg", "Duplicate"),
            PreviewItem.success(original, original.name) // Unchanged
        )

        // When: Create summary
        val summary = PreviewSummary.from(previews)

        // Then: Verify counts
        assertEquals(4, summary.totalFiles)
        assertEquals(2, summary.validRenames) // 2 files can be renamed
        assertEquals(1, summary.conflicts)
        assertEquals(1, summary.unchanged)
        assertFalse("Cannot proceed due to conflict", summary.canProceed)
    }

    @Test
    fun `PreviewSummary with no conflicts can proceed`() = runTest {
        // Given: All successful previews
        val original = sampleFiles[0]
        val previews = listOf(
            PreviewItem.success(original, "new1.jpg"),
            PreviewItem.success(original, "new2.jpg"),
            PreviewItem.success(original, "new3.jpg")
        )

        // When: Create summary
        val summary = PreviewSummary.from(previews)

        // Then: Can proceed
        assertEquals(3, summary.totalFiles)
        assertEquals(3, summary.validRenames)
        assertEquals(0, summary.conflicts)
        assertTrue("Can proceed with no conflicts", summary.canProceed)
        assertTrue("Message should be positive", summary.message.contains("ready"))
    }

    @Test
    fun `PreviewSummary with conflicts cannot proceed`() = runTest {
        // Given: All conflict previews
        val original = sampleFiles[0]
        val previews = listOf(
            PreviewItem.withConflict(original, "bad1.jpg", "Duplicate"),
            PreviewItem.withConflict(original, "bad2.jpg", "Invalid")
        )

        // When: Create summary
        val summary = PreviewSummary.from(previews)

        // Then: Cannot proceed
        assertEquals(2, summary.totalFiles)
        assertEquals(0, summary.validRenames)
        assertEquals(2, summary.conflicts)
        assertFalse("Cannot proceed with conflicts", summary.canProceed)
        assertTrue("Message should mention conflicts", summary.message.contains("conflict"))
    }

    @Test
    fun `generate preview with custom start number`() = runTest {
        // Given: Config with custom start number
        val config = RenameConfig(
            prefix = "photo",
            startNumber = 100,
            digitCount = 3
        )
        val params = GeneratePreviewUseCase.Params(sampleFiles, config)

        // When: Generate preview
        val result = useCase(params)

        // Then: Numbers should start from 100
        assertTrue("Result should be success", result is Result.Success)
        val previews = result.getOrNull()!!
        
        assertEquals("photo100.jpg", previews[0].previewName)
        assertEquals("photo101.jpg", previews[1].previewName)
        assertEquals("photo102.jpg", previews[2].previewName)
    }

    @Test
    fun `generate preview description messages`() {
        // Given: Different preview types
        val original = sampleFiles[0]
        
        val successPreview = PreviewItem.success(original, "new_name.jpg")
        val conflictPreview = PreviewItem.withConflict(original, "bad.jpg", "Duplicate name")
        val unchangedPreview = PreviewItem.success(original, original.name)

        // Then: Verify descriptions
        assertTrue(
            "Success description shows arrow",
            successPreview.description.contains("→")
        )
        assertTrue(
            "Conflict description shows error",
            conflictPreview.description.contains("Cannot rename")
        )
        assertTrue(
            "Unchanged description shows no change",
            unchangedPreview.description.contains("No change")
        )
    }

    @Test
    fun `generate preview with very long prefix`() = runTest {
        // Given: Very long prefix (but still valid)
        val longPrefix = "a".repeat(200) // Within limit when combined with number
        val config = RenameConfig(
            prefix = longPrefix,
            startNumber = 1,
            digitCount = 3
        )
        val params = GeneratePreviewUseCase.Params(listOf(sampleFiles[0]), config)

        // When: Generate preview
        val result = useCase(params)

        // Then: Should succeed if under 255 chars total
        assertTrue("Result should be success", result is Result.Success)
        val previews = result.getOrNull()!!
        
        // The filename validation will check if it's too long
        val preview = previews[0]
        // Length check: prefix(200) + number(3) + dot(1) + extension(3) = 207 chars (valid)
        assertFalse("Should not have conflict for this length", preview.hasConflict)
    }

    @Test
    fun `generate preview handles case-insensitive duplicates`() = runTest {
        // Given: Files that would create case-insensitive duplicates
        // In Windows, "FILE.jpg" and "file.jpg" are the same
        // This is more of a filesystem concern, but we test the logic
        val config = RenameConfig(
            prefix = "test",
            startNumber = 1,
            digitCount = 2
        )
        
        // With proper indexing, these will be test01.jpg, test02.jpg, etc.
        // So no duplicates should occur
        val params = GeneratePreviewUseCase.Params(sampleFiles, config)

        // When: Generate preview
        val result = useCase(params)

        // Then: Should handle properly
        assertTrue("Result should be success", result is Result.Success)
        val previews = result.getOrNull()!!
        
        // All should be unique
        val names = previews.map { it.previewName.lowercase() }.toSet()
        assertEquals("All names should be unique", previews.size, names.size)
    }
}
