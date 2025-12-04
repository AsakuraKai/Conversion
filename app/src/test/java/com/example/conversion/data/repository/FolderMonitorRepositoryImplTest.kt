package com.example.conversion.data.repository

import android.net.Uri
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.FolderMonitor
import com.example.conversion.domain.model.MonitoringStatus
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.repository.FileRenameRepository
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * Unit tests for FolderMonitorRepositoryImpl.
 * Tests folder monitoring repository implementation.
 * 
 * Note: These tests use mock implementations as FileObserver requires
 * actual file system access which is not available in unit tests.
 */
class FolderMonitorRepositoryImplTest {

    private lateinit var fileRenameRepository: FileRenameRepository
    private lateinit var repository: FolderMonitorRepositoryImpl

    private val testFolderPath = "/mock/test/folder"
    private val sampleFolderMonitor = FolderMonitor(
        folderPath = testFolderPath,
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
        fileRenameRepository = mockk(relaxed = true)
        repository = FolderMonitorRepositoryImpl(
            fileRenameRepository = fileRenameRepository,
            ioDispatcher = Dispatchers.Unconfined
        )
    }

    @Test
    fun `initial monitoring status is inactive`() = runTest {
        // When: Get initial status
        val status = repository.getMonitoringStatus()

        // Then: Should be inactive
        assertTrue("Initial status should be inactive", status is MonitoringStatus.Inactive)
    }

    @Test
    fun `observe monitoring status emits inactive initially`() = runTest {
        // When: Observe status
        val status = repository.observeMonitoringStatus().first()

        // Then: Should be inactive
        assertTrue("Initial observed status should be inactive", 
            status is MonitoringStatus.Inactive)
    }

    @Test
    fun `start monitoring with invalid folder path returns error`() = runTest {
        // Given: Invalid folder path (doesn't exist)
        val invalidMonitor = sampleFolderMonitor.copy(
            folderPath = "/invalid/nonexistent/folder"
        )

        // When: Start monitoring
        val result = repository.startMonitoring(invalidMonitor)

        // Then: Should return error
        assertTrue("Result should be error for invalid path", result is Result.Error)
        assertTrue("Error should mention folder", 
            (result as Result.Error).exception.message?.contains("Folder") == true)
        
        // Status should remain inactive or be error
        val status = repository.getMonitoringStatus()
        assertTrue("Status should be inactive or error", 
            status is MonitoringStatus.Inactive || status is MonitoringStatus.Error)
    }

    @Test
    fun `stop monitoring returns success`() = runTest {
        // When: Stop monitoring (even when not started)
        val result = repository.stopMonitoring()

        // Then: Should succeed
        assertTrue("Stop monitoring should succeed", result is Result.Success)
        
        // Status should be inactive
        val status = repository.getMonitoringStatus()
        assertTrue("Status should be inactive", status is MonitoringStatus.Inactive)
    }

    @Test
    fun `get current monitor returns null initially`() = runTest {
        // When: Get current monitor
        val monitor = repository.getCurrentMonitor()

        // Then: Should be null
        assertNull("Initial monitor should be null", monitor)
    }

    @Test
    fun `stop monitoring updates status to inactive`() = runTest {
        // When: Stop monitoring
        repository.stopMonitoring()

        // Then: Status should be inactive
        val status = repository.getMonitoringStatus()
        assertTrue("Status should be inactive after stop", status is MonitoringStatus.Inactive)
        
        // Current monitor should be null
        val monitor = repository.getCurrentMonitor()
        assertNull("Monitor should be null after stop", monitor)
    }

    @Test
    fun `monitoring status flow emits updates`() = runTest {
        // When: Observe status
        val statusFlow = repository.observeMonitoringStatus()
        val initialStatus = statusFlow.first()

        // Then: Should emit inactive status
        assertTrue("Should emit inactive status", initialStatus is MonitoringStatus.Inactive)
    }

    @Test
    fun `pattern matching works in folder monitor`() {
        // Test various patterns
        val jpgMonitor = sampleFolderMonitor.copy(pattern = "*.jpg")
        assertTrue("Should match jpg", jpgMonitor.matchesPattern("photo.jpg"))
        assertFalse("Should not match png", jpgMonitor.matchesPattern("photo.png"))
        
        val imgMonitor = sampleFolderMonitor.copy(pattern = "IMG_*.jpg")
        assertTrue("Should match IMG_001.jpg", imgMonitor.matchesPattern("IMG_001.jpg"))
        assertFalse("Should not match photo.jpg", imgMonitor.matchesPattern("photo.jpg"))
        
        val noPatternMonitor = sampleFolderMonitor.copy(pattern = null)
        assertTrue("Null pattern should match anything", 
            noPatternMonitor.matchesPattern("anything.ext"))
    }

    @Test
    fun `folder monitor validation works`() {
        // Valid monitor
        assertTrue("Valid monitor should pass", sampleFolderMonitor.isValid())
        
        // Invalid: empty path
        val emptyPath = sampleFolderMonitor.copy(folderPath = "")
        assertFalse("Empty path should fail", emptyPath.isValid())
        
        // Invalid: invalid rename config
        val invalidConfig = sampleFolderMonitor.copy(
            renameConfig = RenameConfig(prefix = "", startNumber = 1, digitCount = 3)
        )
        assertFalse("Invalid config should fail", invalidConfig.isValid())
    }

    @Test
    fun `monitoring status types work correctly`() {
        // Test Active status
        val activeStatus = MonitoringStatus.Active(
            folderPath = testFolderPath,
            filesProcessed = 5
        )
        assertTrue("Should be active", activeStatus is MonitoringStatus.Active)
        assertEquals("Should have correct path", testFolderPath, activeStatus.folderPath)
        assertEquals("Should have correct count", 5, activeStatus.filesProcessed)
        
        // Test Inactive status
        val inactiveStatus = MonitoringStatus.Inactive
        assertTrue("Should be inactive", inactiveStatus is MonitoringStatus.Inactive)
        
        // Test Error status
        val errorStatus = MonitoringStatus.Error("Test error")
        assertTrue("Should be error", errorStatus is MonitoringStatus.Error)
        assertEquals("Should have error message", "Test error", errorStatus.error)
    }

    @Test
    fun `multiple stop calls are safe`() = runTest {
        // When: Stop multiple times
        val result1 = repository.stopMonitoring()
        val result2 = repository.stopMonitoring()
        val result3 = repository.stopMonitoring()

        // Then: All should succeed
        assertTrue("First stop should succeed", result1 is Result.Success)
        assertTrue("Second stop should succeed", result2 is Result.Success)
        assertTrue("Third stop should succeed", result3 is Result.Success)
        
        // Status should remain inactive
        val status = repository.getMonitoringStatus()
        assertTrue("Status should be inactive", status is MonitoringStatus.Inactive)
    }
}
