package com.example.conversion.domain.usecase.rename

import android.net.Uri
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.model.RenameStatus
import com.example.conversion.domain.model.SortStrategy
import com.example.conversion.domain.repository.FileRenameRepository
import io.mockk.*
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for ExecuteBatchRenameUseCase.
 * Tests batch rename execution with progress tracking and error handling.
 */
class ExecuteBatchRenameUseCaseTest {

    private lateinit var fileRenameRepository: FileRenameRepository
    private lateinit var generateFilenameUseCase: GenerateFilenameUseCase
    private lateinit var validateFilenameUseCase: ValidateFilenameUseCase
    private lateinit var useCase: ExecuteBatchRenameUseCase
    
    private val testDispatcher = UnconfinedTestDispatcher()
    
    // Sample files for testing
    private val file1 = FileItem(
        id = 1L,
        uri = Uri.parse("content://media/external/images/1"),
        name = "IMG_001.jpg",
        path = "/storage/emulated/0/DCIM/IMG_001.jpg",
        size = 1024L,
        mimeType = "image/jpeg",
        dateModified = System.currentTimeMillis()
    )
    
    private val file2 = FileItem(
        id = 2L,
        uri = Uri.parse("content://media/external/images/2"),
        name = "IMG_002.jpg",
        path = "/storage/emulated/0/DCIM/IMG_002.jpg",
        size = 2048L,
        mimeType = "image/jpeg",
        dateModified = System.currentTimeMillis()
    )
    
    private val file3 = FileItem(
        id = 3L,
        uri = Uri.parse("content://media/external/images/3"),
        name = "IMG_003.jpg",
        path = "/storage/emulated/0/DCIM/IMG_003.jpg",
        size = 3072L,
        mimeType = "image/jpeg",
        dateModified = System.currentTimeMillis()
    )

    @Before
    fun setup() {
        fileRenameRepository = mockk()
        generateFilenameUseCase = GenerateFilenameUseCase(testDispatcher)
        validateFilenameUseCase = ValidateFilenameUseCase(testDispatcher)
        
        useCase = ExecuteBatchRenameUseCase(
            fileRenameRepository = fileRenameRepository,
            generateFilenameUseCase = generateFilenameUseCase,
            validateFilenameUseCase = validateFilenameUseCase,
            dispatcher = testDispatcher
        )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `execute batch rename successfully emits progress for all files`() = runTest {
        // Given: Valid config and files
        val config = RenameConfig(
            prefix = "photo",
            startNumber = 1,
            digitCount = 3,
            preserveExtension = true
        )
        val files = listOf(file1, file2, file3)
        val params = ExecuteBatchRenameUseCase.Params(files, config)
        
        // Mock repository to return success for all files
        every { fileRenameRepository.checkNameConflict(any(), any()) } returns false
        coEvery { fileRenameRepository.renameFile(any(), any()) } returns Result.Success(file1.uri)

        // When: Execute batch rename
        val progressList = useCase(params).toList()

        // Then: Should emit 2 progress updates per file (processing + result)
        assertEquals(6, progressList.size)
        
        // Verify processing status for each file
        assertEquals(RenameStatus.PROCESSING, progressList[0].status)
        assertEquals(0, progressList[0].currentIndex)
        
        assertEquals(RenameStatus.PROCESSING, progressList[2].status)
        assertEquals(1, progressList[2].currentIndex)
        
        assertEquals(RenameStatus.PROCESSING, progressList[4].status)
        assertEquals(2, progressList[4].currentIndex)
        
        // Verify success status for each file
        assertEquals(RenameStatus.SUCCESS, progressList[1].status)
        assertEquals(RenameStatus.SUCCESS, progressList[3].status)
        assertEquals(RenameStatus.SUCCESS, progressList[5].status)
    }

    @Test
    fun `execute batch rename handles individual file failures gracefully`() = runTest {
        // Given: Valid config but second file will fail
        val config = RenameConfig(
            prefix = "photo",
            startNumber = 1,
            digitCount = 3,
            preserveExtension = true
        )
        val files = listOf(file1, file2, file3)
        val params = ExecuteBatchRenameUseCase.Params(files, config)
        
        // Mock repository: first succeeds, second fails, third succeeds
        every { fileRenameRepository.checkNameConflict(any(), any()) } returns false
        coEvery { fileRenameRepository.renameFile(file1.uri, any()) } returns Result.Success(file1.uri)
        coEvery { fileRenameRepository.renameFile(file2.uri, any()) } returns 
            Result.Error(Exception("Permission denied"))
        coEvery { fileRenameRepository.renameFile(file3.uri, any()) } returns Result.Success(file3.uri)

        // When: Execute batch rename
        val progressList = useCase(params).toList()

        // Then: Should continue processing after failure
        assertEquals(6, progressList.size)
        assertEquals(RenameStatus.SUCCESS, progressList[1].status)
        assertEquals(RenameStatus.FAILED, progressList[3].status)
        assertEquals(RenameStatus.SUCCESS, progressList[5].status)
    }

    @Test
    fun `execute batch rename skips files with naming conflicts`() = runTest {
        // Given: Valid config but second file has naming conflict
        val config = RenameConfig(
            prefix = "photo",
            startNumber = 1,
            digitCount = 3,
            preserveExtension = true
        )
        val files = listOf(file1, file2, file3)
        val params = ExecuteBatchRenameUseCase.Params(files, config)
        
        // Mock repository: second file has conflict
        every { fileRenameRepository.checkNameConflict(file1.uri, any()) } returns false
        every { fileRenameRepository.checkNameConflict(file2.uri, any()) } returns true
        every { fileRenameRepository.checkNameConflict(file3.uri, any()) } returns false
        coEvery { fileRenameRepository.renameFile(file1.uri, any()) } returns Result.Success(file1.uri)
        coEvery { fileRenameRepository.renameFile(file3.uri, any()) } returns Result.Success(file3.uri)

        // When: Execute batch rename
        val progressList = useCase(params).toList()

        // Then: Second file should be skipped
        assertEquals(6, progressList.size)
        assertEquals(RenameStatus.SUCCESS, progressList[1].status)
        assertEquals(RenameStatus.SKIPPED, progressList[3].status)
        assertEquals(RenameStatus.SUCCESS, progressList[5].status)
    }

    @Test
    fun `execute batch rename fails early with invalid config`() = runTest {
        // Given: Invalid config (empty prefix)
        val config = RenameConfig(
            prefix = "",
            startNumber = 1,
            digitCount = 3,
            preserveExtension = true
        )
        val files = listOf(file1, file2)
        val params = ExecuteBatchRenameUseCase.Params(files, config)

        // When: Execute batch rename
        val progressList = useCase(params).toList()

        // Then: Should emit only one failed status and stop
        assertEquals(1, progressList.size)
        assertEquals(RenameStatus.FAILED, progressList[0].status)
    }

    @Test
    fun `execute batch rename handles empty file list`() = runTest {
        // Given: Valid config but no files
        val config = RenameConfig(
            prefix = "photo",
            startNumber = 1,
            digitCount = 3,
            preserveExtension = true
        )
        val files = emptyList<FileItem>()
        val params = ExecuteBatchRenameUseCase.Params(files, config)

        // When: Execute batch rename
        val progressList = useCase(params).toList()

        // Then: Should emit no progress
        assertTrue(progressList.isEmpty())
    }

    @Test
    fun `execute batch rename emits correct progress percentages`() = runTest {
        // Given: Valid config and files
        val config = RenameConfig(
            prefix = "photo",
            startNumber = 1,
            digitCount = 3,
            preserveExtension = true
        )
        val files = listOf(file1, file2, file3)
        val params = ExecuteBatchRenameUseCase.Params(files, config)
        
        every { fileRenameRepository.checkNameConflict(any(), any()) } returns false
        coEvery { fileRenameRepository.renameFile(any(), any()) } returns Result.Success(file1.uri)

        // When: Execute batch rename
        val progressList = useCase(params).toList()

        // Then: Progress percentages should be correct
        assertEquals(33, progressList[0].progressPercentage)  // 1/3
        assertEquals(33, progressList[1].progressPercentage)
        assertEquals(66, progressList[2].progressPercentage)  // 2/3
        assertEquals(66, progressList[3].progressPercentage)
        assertEquals(100, progressList[4].progressPercentage) // 3/3
        assertEquals(100, progressList[5].progressPercentage)
    }

    @Test
    fun `execute batch rename tracks current file correctly`() = runTest {
        // Given: Valid config and files
        val config = RenameConfig(
            prefix = "photo",
            startNumber = 1,
            digitCount = 3,
            preserveExtension = true
        )
        val files = listOf(file1, file2, file3)
        val params = ExecuteBatchRenameUseCase.Params(files, config)
        
        every { fileRenameRepository.checkNameConflict(any(), any()) } returns false
        coEvery { fileRenameRepository.renameFile(any(), any()) } returns Result.Success(file1.uri)

        // When: Execute batch rename
        val progressList = useCase(params).toList()

        // Then: Current file should be tracked correctly
        assertEquals(file1, progressList[0].currentFile)
        assertEquals(file1, progressList[1].currentFile)
        assertEquals(file2, progressList[2].currentFile)
        assertEquals(file2, progressList[3].currentFile)
        assertEquals(file3, progressList[4].currentFile)
        assertEquals(file3, progressList[5].currentFile)
    }

    @Test
    fun `execute batch rename identifies last file correctly`() = runTest {
        // Given: Valid config and files
        val config = RenameConfig(
            prefix = "photo",
            startNumber = 1,
            digitCount = 3,
            preserveExtension = true
        )
        val files = listOf(file1, file2, file3)
        val params = ExecuteBatchRenameUseCase.Params(files, config)
        
        every { fileRenameRepository.checkNameConflict(any(), any()) } returns false
        coEvery { fileRenameRepository.renameFile(any(), any()) } returns Result.Success(file1.uri)

        // When: Execute batch rename
        val progressList = useCase(params).toList()

        // Then: Last file should be identified
        assertFalse(progressList[0].isLastFile)
        assertFalse(progressList[1].isLastFile)
        assertFalse(progressList[2].isLastFile)
        assertFalse(progressList[3].isLastFile)
        assertTrue(progressList[4].isLastFile)
        assertTrue(progressList[5].isLastFile)
    }

    @Test
    fun `execute batch rename handles exception during processing`() = runTest {
        // Given: Valid config but repository throws exception
        val config = RenameConfig(
            prefix = "photo",
            startNumber = 1,
            digitCount = 3,
            preserveExtension = true
        )
        val files = listOf(file1, file2)
        val params = ExecuteBatchRenameUseCase.Params(files, config)
        
        every { fileRenameRepository.checkNameConflict(any(), any()) } throws RuntimeException("Unexpected error")

        // When: Execute batch rename
        val progressList = useCase(params).toList()

        // Then: Should handle exception and emit failed status
        assertEquals(4, progressList.size)
        assertEquals(RenameStatus.FAILED, progressList[1].status)
        assertEquals(RenameStatus.FAILED, progressList[3].status)
    }

    @Test
    fun `execute batch rename with single file`() = runTest {
        // Given: Valid config and single file
        val config = RenameConfig(
            prefix = "photo",
            startNumber = 1,
            digitCount = 3,
            preserveExtension = true
        )
        val files = listOf(file1)
        val params = ExecuteBatchRenameUseCase.Params(files, config)
        
        every { fileRenameRepository.checkNameConflict(any(), any()) } returns false
        coEvery { fileRenameRepository.renameFile(any(), any()) } returns Result.Success(file1.uri)

        // When: Execute batch rename
        val progressList = useCase(params).toList()

        // Then: Should process single file correctly
        assertEquals(2, progressList.size)
        assertEquals(RenameStatus.PROCESSING, progressList[0].status)
        assertEquals(RenameStatus.SUCCESS, progressList[1].status)
        assertTrue(progressList[1].isLastFile)
        assertEquals(100, progressList[1].progressPercentage)
    }
}
