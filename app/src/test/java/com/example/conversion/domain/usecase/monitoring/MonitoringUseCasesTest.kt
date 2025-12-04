package com.example.conversion.domain.usecase.monitoring

import android.net.Uri
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.FolderMonitor
import com.example.conversion.domain.model.MonitoringStatus
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.repository.FolderMonitorRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for monitoring use cases.
 * Tests folder monitoring operations with mocked repository.
 */
class MonitoringUseCasesTest {

    private lateinit var repository: FolderMonitorRepository
    private lateinit var startMonitoringUseCase: StartMonitoringUseCase
    private lateinit var stopMonitoringUseCase: StopMonitoringUseCase
    private lateinit var getMonitoringStatusUseCase: GetMonitoringStatusUseCase

    private val sampleFolderMonitor = FolderMonitor(
        folderPath = "/storage/emulated/0/DCIM/Camera",
        folderUri = Uri.parse("content://media/external/file/1"),
        renameConfig = RenameConfig(
            prefix = "photo",
            startNumber = 1,
            digitCount = 3,
            preserveExtension = true
        ),
        isActive = false,
        pattern = "*.jpg",
        monitorSubfolders = false
    )

    @Before
    fun setup() {
        repository = mockk()
        startMonitoringUseCase = StartMonitoringUseCase(repository, Dispatchers.Unconfined)
        stopMonitoringUseCase = StopMonitoringUseCase(repository, Dispatchers.Unconfined)
        getMonitoringStatusUseCase = GetMonitoringStatusUseCase(repository, Dispatchers.Unconfined)
    }

    @Test
    fun `start monitoring with valid config succeeds`() = runTest {
        // Given: Repository returns success
        coEvery { repository.startMonitoring(any()) } returns Result.Success(Unit)

        // When: Start monitoring
        val result = startMonitoringUseCase(sampleFolderMonitor)

        // Then: Should succeed and call repository
        assertTrue("Result should be success", result is Result.Success)
        coVerify { repository.startMonitoring(sampleFolderMonitor) }
    }

    @Test
    fun `start monitoring with invalid config fails`() = runTest {
        // Given: Invalid folder monitor (empty path)
        val invalidMonitor = sampleFolderMonitor.copy(folderPath = "")

        // When: Start monitoring
        val result = startMonitoringUseCase(invalidMonitor)

        // Then: Should fail with IllegalArgumentException
        assertTrue("Result should be error", result is Result.Error)
        val error = (result as Result.Error).exception
        assertTrue("Error should be IllegalArgumentException", 
            error is IllegalArgumentException)
        
        // Repository should not be called
        coVerify(exactly = 0) { repository.startMonitoring(any()) }
    }

    @Test
    fun `start monitoring with invalid rename config fails`() = runTest {
        // Given: Invalid rename config (empty prefix)
        val invalidConfig = sampleFolderMonitor.copy(
            renameConfig = RenameConfig(
                prefix = "",
                startNumber = 1,
                digitCount = 3
            )
        )

        // When: Start monitoring
        val result = startMonitoringUseCase(invalidConfig)

        // Then: Should fail
        assertTrue("Result should be error", result is Result.Error)
        coVerify(exactly = 0) { repository.startMonitoring(any()) }
    }

    @Test
    fun `start monitoring handles repository error`() = runTest {
        // Given: Repository returns error
        val errorMessage = "Permission denied"
        coEvery { repository.startMonitoring(any()) } returns 
            Result.Error(Exception(errorMessage))

        // When: Start monitoring
        val result = startMonitoringUseCase(sampleFolderMonitor)

        // Then: Should return error
        assertTrue("Result should be error", result is Result.Error)
        assertEquals(errorMessage, (result as Result.Error).exception.message)
    }

    @Test
    fun `stop monitoring succeeds`() = runTest {
        // Given: Repository returns success
        coEvery { repository.stopMonitoring() } returns Result.Success(Unit)

        // When: Stop monitoring
        val result = stopMonitoringUseCase()

        // Then: Should succeed
        assertTrue("Result should be success", result is Result.Success)
        coVerify { repository.stopMonitoring() }
    }

    @Test
    fun `stop monitoring handles repository error`() = runTest {
        // Given: Repository returns error
        coEvery { repository.stopMonitoring() } returns 
            Result.Error(Exception("Failed to stop"))

        // When: Stop monitoring
        val result = stopMonitoringUseCase()

        // Then: Should return error
        assertTrue("Result should be error", result is Result.Error)
    }

    @Test
    fun `get monitoring status returns active status`() = runTest {
        // Given: Repository returns active status
        val activeStatus = MonitoringStatus.Active(
            folderPath = "/storage/emulated/0/DCIM",
            filesProcessed = 5
        )
        coEvery { repository.getMonitoringStatus() } returns activeStatus

        // When: Get status
        val result = getMonitoringStatusUseCase()

        // Then: Should return active status
        assertTrue("Result should be success", result is Result.Success)
        val status = result.getOrNull()
        assertTrue("Status should be active", status is MonitoringStatus.Active)
        assertEquals(5, (status as MonitoringStatus.Active).filesProcessed)
    }

    @Test
    fun `get monitoring status returns inactive status`() = runTest {
        // Given: Repository returns inactive status
        coEvery { repository.getMonitoringStatus() } returns MonitoringStatus.Inactive

        // When: Get status
        val result = getMonitoringStatusUseCase()

        // Then: Should return inactive status
        assertTrue("Result should be success", result is Result.Success)
        val status = result.getOrNull()
        assertTrue("Status should be inactive", status is MonitoringStatus.Inactive)
    }

    @Test
    fun `get monitoring status returns error status`() = runTest {
        // Given: Repository returns error status
        val errorStatus = MonitoringStatus.Error("Folder not found")
        coEvery { repository.getMonitoringStatus() } returns errorStatus

        // When: Get status
        val result = getMonitoringStatusUseCase()

        // Then: Should return error status
        assertTrue("Result should be success", result is Result.Success)
        val status = result.getOrNull()
        assertTrue("Status should be error", status is MonitoringStatus.Error)
        assertEquals("Folder not found", (status as MonitoringStatus.Error).error)
    }

    @Test
    fun `start monitoring with pattern matching config`() = runTest {
        // Given: Monitor with specific pattern
        val monitorWithPattern = sampleFolderMonitor.copy(
            pattern = "IMG_*.jpg",
            monitorSubfolders = true
        )
        coEvery { repository.startMonitoring(any()) } returns Result.Success(Unit)

        // When: Start monitoring
        val result = startMonitoringUseCase(monitorWithPattern)

        // Then: Should succeed
        assertTrue("Result should be success", result is Result.Success)
        coVerify { repository.startMonitoring(monitorWithPattern) }
    }

    @Test
    fun `folder monitor pattern matching works correctly`() {
        // Test pattern matching in FolderMonitor model
        val monitor = sampleFolderMonitor.copy(pattern = "IMG_*.jpg")
        
        assertTrue("Should match IMG_001.jpg", monitor.matchesPattern("IMG_001.jpg"))
        assertTrue("Should match IMG_999.jpg", monitor.matchesPattern("IMG_999.jpg"))
        assertFalse("Should not match video.mp4", monitor.matchesPattern("video.mp4"))
        assertFalse("Should not match IMG_001.png", monitor.matchesPattern("IMG_001.png"))
        
        // Test wildcard pattern
        val wildcardMonitor = sampleFolderMonitor.copy(pattern = "*.jpg")
        assertTrue("Should match any jpg", wildcardMonitor.matchesPattern("photo.jpg"))
        assertTrue("Should match any jpg", wildcardMonitor.matchesPattern("IMG_001.jpg"))
        assertFalse("Should not match png", wildcardMonitor.matchesPattern("image.png"))
        
        // Test no pattern (matches all)
        val noPatternMonitor = sampleFolderMonitor.copy(pattern = null)
        assertTrue("Should match anything", noPatternMonitor.matchesPattern("any_file.any"))
    }

    @Test
    fun `folder monitor validation works correctly`() {
        // Valid monitor
        assertTrue("Valid monitor should pass validation", sampleFolderMonitor.isValid())
        
        // Invalid: empty path
        val emptyPath = sampleFolderMonitor.copy(folderPath = "")
        assertFalse("Empty path should fail validation", emptyPath.isValid())
        
        // Invalid: invalid rename config
        val invalidConfig = sampleFolderMonitor.copy(
            renameConfig = RenameConfig(prefix = "", startNumber = 1, digitCount = 3)
        )
        assertFalse("Invalid config should fail validation", invalidConfig.isValid())
    }
}
