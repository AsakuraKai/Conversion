package com.example.conversion.e2e

import android.net.Uri
import com.example.conversion.domain.model.*
import com.example.conversion.domain.usecase.fileselection.GetMediaFilesUseCase
import com.example.conversion.domain.usecase.preview.GeneratePreviewUseCase
import com.example.conversion.domain.usecase.rename.*
import com.example.conversion.util.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * End-to-end test for complete rename workflow.
 * 
 * NOTE: This is a mock E2E test using fake repositories.
 * Real E2E tests would require instrumented tests with actual file system access.
 * This test validates the complete user flow and business logic integration.
 */
class RenameFlowE2ETest {

    // Repositories
    private lateinit var mediaRepository: FakeMediaRepository
    private lateinit var renameRepository: FakeFileRenameRepository
    private lateinit var templateRepository: FakeTemplateRepository
    private lateinit var historyRepository: FakeHistoryRepository

    // Use Cases
    private lateinit var getMediaFilesUseCase: GetMediaFilesUseCase
    private lateinit var generateFilenameUseCase: GenerateFilenameUseCase
    private lateinit var generatePreviewUseCase: GeneratePreviewUseCase
    private lateinit var executeBatchRenameUseCase: ExecuteBatchRenameUseCase
    private lateinit var saveTemplateUseCase: com.example.conversion.domain.usecase.template.SaveTemplateUseCase

    @Before
    fun setup() {
        // Initialize repositories
        mediaRepository = FakeMediaRepository()
        renameRepository = FakeFileRenameRepository()
        templateRepository = FakeTemplateRepository()
        historyRepository = FakeHistoryRepository()

        // Initialize use cases
        getMediaFilesUseCase = GetMediaFilesUseCase(mediaRepository)
        generateFilenameUseCase = GenerateFilenameUseCase()
        generatePreviewUseCase = GeneratePreviewUseCase(generateFilenameUseCase)
        executeBatchRenameUseCase = ExecuteBatchRenameUseCase(renameRepository)
        saveTemplateUseCase = com.example.conversion.domain.usecase.template.SaveTemplateUseCase(templateRepository)
    }

    @After
    fun teardown() {
        mediaRepository.reset()
        renameRepository.reset()
        templateRepository.reset()
        historyRepository.reset()
    }

    @Test
    fun `complete user flow - select files, preview, rename, save template`() = runTest {
        // STEP 1: User opens app and views files
        val testFiles = TestDataFactory.createFileItems(10)
        mediaRepository.mediaFiles.addAll(testFiles)

        val filter = FileFilter(includeImages = true, includeVideos = false, includeAudio = false)
        val filesResult = getMediaFilesUseCase(filter)
        
        assertTrue("Should get files successfully", filesResult.isSuccess)
        val selectedFiles = filesResult.getOrThrow()
        assertEquals("Should have 10 files", 10, selectedFiles.size)

        // STEP 2: User configures rename settings
        val renameConfig = TestDataFactory.createRenameConfig(
            prefix = "VACATION",
            startNumber = 1,
            digitCount = 3,
            preserveExtension = true,
            sortStrategy = SortStrategy.DATE_MODIFIED
        )

        // STEP 3: User previews rename results
        val previewResult = generatePreviewUseCase(selectedFiles, renameConfig)
        assertTrue("Preview should succeed", previewResult.isSuccess)
        val previews = previewResult.getOrThrow()
        
        assertEquals("Should have 10 previews", 10, previews.size)
        assertFalse("Should have no conflicts", previews.any { it.hasConflict })
        
        // Verify preview names follow pattern
        assertTrue("First file should be VACATION_001", 
            previews[0].preview.startsWith("VACATION_001"))

        // STEP 4: User saves configuration as template
        val template = TestDataFactory.createRenameTemplate(
            id = "vacation_template",
            name = "Vacation Photos",
            pattern = "{prefix}{number}",
            config = renameConfig,
            isFavorite = true
        )
        val saveTemplateResult = saveTemplateUseCase(template)
        assertTrue("Template should save", saveTemplateResult.isSuccess)

        // STEP 5: User executes rename
        val progressStates = mutableListOf<RenameProgress>()
        executeBatchRenameUseCase(selectedFiles, renameConfig).collect { progress ->
            progressStates.add(progress)
            
            // Simulate saving to history on each success
            if (progress.status == RenameStatus.SUCCESS && progress.currentFile != null) {
                val operation = TestDataFactory.createRenameOperation(
                    originalUri = progress.currentFile.uri,
                    newUri = progress.currentFile.uri,
                    originalName = progress.currentFile.name,
                    newName = previews[progress.currentIndex].preview
                )
                historyRepository.saveOperation(operation)
            }
        }

        // STEP 6: Verify results
        assertEquals("Should have progress for all files", 10, 
            progressStates.count { it.status == RenameStatus.SUCCESS })
        assertEquals("All files should be renamed", 10, renameRepository.renamedFiles.size)
        
        // Verify history recorded
        val history = historyRepository.getHistory(100).getOrThrow()
        assertEquals("Should have 10 history entries", 10, history.size)

        // STEP 7: User can retrieve saved template later
        val retrievedTemplate = templateRepository.getTemplate("vacation_template").getOrThrow()
        assertEquals("Template name should match", "Vacation Photos", retrievedTemplate.name)
        assertTrue("Template should be favorite", retrievedTemplate.isFavorite)
    }

    @Test
    fun `user flow with conflict detection`() = runTest {
        // STEP 1: Setup files with potential naming conflicts
        val files = listOf(
            TestDataFactory.createFileItem(id = "1", name = "IMG_001.jpg"),
            TestDataFactory.createFileItem(id = "2", name = "IMG_002.jpg"),
            TestDataFactory.createFileItem(id = "3", name = "photo.jpg")
        )
        mediaRepository.mediaFiles.addAll(files)

        val config = TestDataFactory.createRenameConfig(
            prefix = "IMG",
            startNumber = 1,
            digitCount = 3
        )

        // STEP 2: Generate preview (would detect if new names match existing files)
        val previewResult = generatePreviewUseCase(files, config)
        assertTrue(previewResult.isSuccess)
        val previews = previewResult.getOrThrow()

        // STEP 3: User sees preview and decides to proceed or adjust
        // (In real app, UI would show conflicts and let user modify)
        assertEquals(3, previews.size)
    }

    @Test
    fun `user flow with error handling`() = runTest {
        // STEP 1: Setup files
        val files = TestDataFactory.createFileItems(5)
        mediaRepository.mediaFiles.addAll(files)

        val config = TestDataFactory.createRenameConfig()

        // STEP 2: Preview succeeds
        val previewResult = generatePreviewUseCase(files, config)
        assertTrue(previewResult.isSuccess)

        // STEP 3: Rename fails (simulate permission error)
        renameRepository.shouldFail = true

        val progressStates = mutableListOf<RenameProgress>()
        executeBatchRenameUseCase(files, config).collect { progress ->
            progressStates.add(progress)
        }

        // STEP 4: User sees errors in UI
        assertTrue("Should have error states", 
            progressStates.any { it.status == RenameStatus.ERROR })
        assertEquals("No files should be renamed", 0, renameRepository.renamedFiles.size)
    }

    @Test
    fun `user flow with template reuse`() = runTest {
        // STEP 1: User previously saved a template
        val savedTemplate = TestDataFactory.createRenameTemplate(
            id = "work_template",
            name = "Work Documents",
            config = TestDataFactory.createRenameConfig(
                prefix = "DOC",
                startNumber = 100,
                digitCount = 4
            )
        )
        saveTemplateUseCase(savedTemplate)

        // STEP 2: User loads files
        val files = TestDataFactory.createFileItems(3)
        mediaRepository.mediaFiles.addAll(files)

        // STEP 3: User selects saved template
        val template = templateRepository.getTemplate("work_template").getOrThrow()
        assertNotNull(template)

        // STEP 4: Preview with template config
        val previewResult = generatePreviewUseCase(files, template.config)
        assertTrue(previewResult.isSuccess)
        val previews = previewResult.getOrThrow()

        // STEP 5: Verify preview uses template settings
        assertTrue("Should use template prefix", previews[0].preview.startsWith("DOC_"))
        assertTrue("Should use 4 digits", previews[0].preview.contains("0100"))

        // STEP 6: Execute rename
        executeBatchRenameUseCase(files, template.config).collect { /* Collect to execute */ }

        // STEP 7: Verify success
        assertEquals(3, renameRepository.renamedFiles.size)
    }

    @Test
    fun `complete monitoring flow - setup, detect, rename`() = runTest {
        // This simulates the file monitoring feature end-to-end
        
        // STEP 1: User sets up folder monitor
        val monitorConfig = TestDataFactory.createRenameConfig(
            prefix = "AUTO",
            startNumber = 1
        )
        
        val monitor = TestDataFactory.createFolderMonitor(
            id = "monitor_1",
            folderName = "Downloads",
            pattern = "*.jpg",
            renameConfig = monitorConfig,
            isActive = true
        )

        // STEP 2: Simulate new file detected
        val newFile = TestDataFactory.createFileItem(
            name = "Screenshot_123.jpg",
            path = "/storage/emulated/0/Download/Screenshot_123.jpg"
        )

        // STEP 3: Auto-rename triggered
        val newName = generateFilenameUseCase.generateFilename(newFile, monitorConfig, 0)
        val renameResult = renameRepository.renameFile(newFile.uri, newName)

        // STEP 4: Verify auto-rename success
        assertTrue(renameResult.isSuccess)
        assertEquals(1, renameRepository.renamedFiles.size)
        assertEquals("AUTO_001.jpg", newName)
    }

    @Test
    fun `performance test - large batch rename`() = runTest {
        // STEP 1: Setup large number of files
        val files = TestDataFactory.createFileItems(100)
        mediaRepository.mediaFiles.addAll(files)

        val config = TestDataFactory.createRenameConfig()

        // STEP 2: Generate preview (should be fast)
        val previewStart = System.currentTimeMillis()
        val previewResult = generatePreviewUseCase(files, config)
        val previewDuration = System.currentTimeMillis() - previewStart

        assertTrue(previewResult.isSuccess)
        assertTrue("Preview should be fast: ${previewDuration}ms", previewDuration < 1000)

        // STEP 3: Execute rename (measure performance)
        val renameStart = System.currentTimeMillis()
        var completedCount = 0
        executeBatchRenameUseCase(files, config).collect { progress ->
            if (progress.status == RenameStatus.SUCCESS) {
                completedCount++
            }
        }
        val renameDuration = System.currentTimeMillis() - renameStart

        // STEP 4: Verify performance meets requirements
        assertEquals(100, completedCount)
        assertTrue("Batch rename should complete in reasonable time: ${renameDuration}ms", 
            renameDuration < 5000)
    }
}
