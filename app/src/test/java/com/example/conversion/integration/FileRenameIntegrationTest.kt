package com.example.conversion.integration

import com.example.conversion.domain.model.*
import com.example.conversion.domain.usecase.rename.*
import com.example.conversion.util.FakeFileRenameRepository
import com.example.conversion.util.FakeMediaRepository
import com.example.conversion.util.TestDataFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Integration tests for file rename operations.
 * Tests the complete flow from file selection to rename execution.
 */
class FileRenameIntegrationTest {

    private lateinit var mediaRepository: FakeMediaRepository
    private lateinit var renameRepository: FakeFileRenameRepository
    private lateinit var generateFilenameUseCase: GenerateFilenameUseCase
    private lateinit var executeBatchRenameUseCase: ExecuteBatchRenameUseCase

    @Before
    fun setup() {
        mediaRepository = FakeMediaRepository()
        renameRepository = FakeFileRenameRepository()
        generateFilenameUseCase = GenerateFilenameUseCase()
        executeBatchRenameUseCase = ExecuteBatchRenameUseCase(renameRepository)
    }

    @After
    fun teardown() {
        mediaRepository.reset()
        renameRepository.reset()
    }

    @Test
    fun `complete rename flow - from selection to execution`() = runTest {
        // Given: Files in repository
        val files = TestDataFactory.createFileItems(5)
        mediaRepository.mediaFiles.addAll(files)

        // When: Get files
        val getFilesResult = mediaRepository.getMediaFiles(
            FileFilter(includeImages = true, includeVideos = false, includeAudio = false)
        )
        assertTrue(getFilesResult.isSuccess)
        val selectedFiles = getFilesResult.getOrThrow()

        // And: Generate new names
        val config = TestDataFactory.createRenameConfig(prefix = "PHOTO", startNumber = 1)
        val newNames = selectedFiles.mapIndexed { index, file ->
            generateFilenameUseCase.generateFilename(file, config, index)
        }

        // And: Execute rename
        val renameResults = mutableListOf<RenameResult>()
        executeBatchRenameUseCase(selectedFiles, config).collect { progress ->
            if (progress.status == RenameStatus.SUCCESS) {
                renameResults.add(
                    RenameResult(
                        originalFile = progress.currentFile!!,
                        newName = newNames[progress.currentIndex],
                        success = true,
                        error = null
                    )
                )
            }
        }

        // Then: All files renamed successfully
        assertEquals(5, renameResults.size)
        assertEquals(5, renameRepository.renamedFiles.size)
        assertTrue(renameResults.all { it.success })
    }

    @Test
    fun `rename flow handles errors gracefully`() = runTest {
        // Given: Files and repository that fails
        val files = TestDataFactory.createFileItems(3)
        mediaRepository.mediaFiles.addAll(files)
        renameRepository.shouldFail = true

        val config = TestDataFactory.createRenameConfig()

        // When: Execute rename
        val progressList = mutableListOf<RenameProgress>()
        executeBatchRenameUseCase(files, config).collect { progress ->
            progressList.add(progress)
        }

        // Then: Errors captured in progress
        assertTrue(progressList.any { it.status == RenameStatus.ERROR })
        assertEquals(0, renameRepository.renamedFiles.size)
    }

    @Test
    fun `rename preserves file extensions`() = runTest {
        // Given: Files with various extensions
        val files = listOf(
            TestDataFactory.createFileItem(name = "photo1.jpg", mimeType = "image/jpeg"),
            TestDataFactory.createFileItem(name = "photo2.png", mimeType = "image/png"),
            TestDataFactory.createFileItem(name = "video.mp4", mimeType = "video/mp4")
        )

        val config = TestDataFactory.createRenameConfig(
            prefix = "FILE",
            preserveExtension = true
        )

        // When: Generate filenames
        val newNames = files.mapIndexed { index, file ->
            generateFilenameUseCase.generateFilename(file, config, index)
        }

        // Then: Extensions preserved
        assertEquals("FILE_001.jpg", newNames[0])
        assertEquals("FILE_002.png", newNames[1])
        assertEquals("FILE_003.mp4", newNames[2])
    }

    @Test
    fun `batch rename with different sort strategies`() = runTest {
        // Given: Files with different creation times
        val files = listOf(
            TestDataFactory.createFileItem(id = "3", name = "c.jpg", dateModified = 3000L),
            TestDataFactory.createFileItem(id = "1", name = "a.jpg", dateModified = 1000L),
            TestDataFactory.createFileItem(id = "2", name = "b.jpg", dateModified = 2000L)
        )

        val configByName = TestDataFactory.createRenameConfig(sortStrategy = SortStrategy.NAME)
        val configByDate = TestDataFactory.createRenameConfig(sortStrategy = SortStrategy.DATE_MODIFIED)

        // When: Sort by name
        val sortedByName = files.sortedWith(configByName.sortStrategy.comparator)
        val nameBasedRenames = sortedByName.mapIndexed { index, file ->
            generateFilenameUseCase.generateFilename(file, configByName, index)
        }

        // Then: Sorted alphabetically
        assertTrue(nameBasedRenames[0].contains("001"))
        assertEquals("a.jpg", sortedByName[0].name)

        // When: Sort by date
        val sortedByDate = files.sortedWith(configByDate.sortStrategy.comparator)
        val dateBasedRenames = sortedByDate.mapIndexed { index, file ->
            generateFilenameUseCase.generateFilename(file, configByDate, index)
        }

        // Then: Sorted by date
        assertEquals("a.jpg", sortedByDate[0].name)
        assertEquals(1000L, sortedByDate[0].dateModified)
    }

    @Test
    fun `rename handles large batch efficiently`() = runTest {
        // Given: Large number of files
        val files = TestDataFactory.createFileItems(100)
        mediaRepository.mediaFiles.addAll(files)

        val config = TestDataFactory.createRenameConfig()

        // When: Execute batch rename
        val startTime = System.currentTimeMillis()
        var completedCount = 0
        executeBatchRenameUseCase(files, config).collect { progress ->
            if (progress.status == RenameStatus.SUCCESS) {
                completedCount++
            }
        }
        val duration = System.currentTimeMillis() - startTime

        // Then: All files processed
        assertEquals(100, completedCount)
        assertEquals(100, renameRepository.renamedFiles.size)
        
        // Performance check (should be fast in test environment)
        assertTrue("Batch rename took too long: ${duration}ms", duration < 5000)
    }
}
