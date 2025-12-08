package com.example.conversion.domain.usecase.activity

import android.net.Uri
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.ActivityLog
import com.example.conversion.domain.model.ActivityStatus
import com.example.conversion.domain.model.ExportFormat
import com.example.conversion.domain.model.LogFilter
import com.example.conversion.domain.repository.ActivityRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class ActivityUseCasesTest {

    private lateinit var activityRepository: ActivityRepository
    private val testDispatcher = StandardTestDispatcher()

    private val testLog = ActivityLog(
        id = 1L,
        action = "FILE_RENAMED",
        details = "Renamed IMG_001.jpg to vacation_001.jpg",
        timestamp = LocalDateTime.now(),
        status = ActivityStatus.SUCCESS
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        activityRepository = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    // ============================================
    // LogActivityUseCase Tests
    // ============================================

    @Test
    fun `logActivity should save valid log successfully`() = runTest {
        // Given
        coEvery { activityRepository.logActivity(testLog) } returns Result.Success(Unit)
        val useCase = LogActivityUseCase(activityRepository, testDispatcher)

        // When
        val result = useCase(testLog)

        // Then
        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { activityRepository.logActivity(testLog) }
    }

    @Test
    fun `logActivity should handle repository errors`() = runTest {
        // Given
        val exception = Exception("Database error")
        coEvery { activityRepository.logActivity(testLog) } returns Result.Error(exception)
        val useCase = LogActivityUseCase(activityRepository, testDispatcher)

        // When
        val result = useCase(testLog)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception, (result as Result.Error).exception)
    }

    @Test
    fun `logActivity should save different activity types`() = runTest {
        // Given
        val failedLog = testLog.copy(status = ActivityStatus.FAILED)
        coEvery { activityRepository.logActivity(failedLog) } returns Result.Success(Unit)
        val useCase = LogActivityUseCase(activityRepository, testDispatcher)

        // When
        val result = useCase(failedLog)

        // Then
        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { activityRepository.logActivity(failedLog) }
    }

    // ============================================
    // GetActivityLogsUseCase Tests
    // ============================================

    @Test
    fun `getActivityLogs should retrieve logs with filter`() = runTest {
        // Given
        val filter = LogFilter(limit = 10)
        val logs = listOf(testLog)
        coEvery { activityRepository.getActivityLogs(filter) } returns Result.Success(logs)
        val useCase = GetActivityLogsUseCase(activityRepository, testDispatcher)

        // When
        val result = useCase(filter)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(logs, (result as Result.Success).data)
        coVerify(exactly = 1) { activityRepository.getActivityLogs(filter) }
    }

    @Test
    fun `getActivityLogs should handle empty results`() = runTest {
        // Given
        val filter = LogFilter(status = ActivityStatus.CANCELLED)
        coEvery { activityRepository.getActivityLogs(filter) } returns Result.Success(emptyList())
        val useCase = GetActivityLogsUseCase(activityRepository, testDispatcher)

        // When
        val result = useCase(filter)

        // Then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data.isEmpty())
    }

    @Test
    fun `getActivityLogs should filter by status`() = runTest {
        // Given
        val filter = LogFilter(status = ActivityStatus.SUCCESS)
        val logs = listOf(testLog)
        coEvery { activityRepository.getActivityLogs(filter) } returns Result.Success(logs)
        val useCase = GetActivityLogsUseCase(activityRepository, testDispatcher)

        // When
        val result = useCase(filter)

        // Then
        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { activityRepository.getActivityLogs(filter) }
    }

    @Test
    fun `getActivityLogs should filter by action`() = runTest {
        // Given
        val filter = LogFilter(action = "FILE_RENAMED")
        val logs = listOf(testLog)
        coEvery { activityRepository.getActivityLogs(filter) } returns Result.Success(logs)
        val useCase = GetActivityLogsUseCase(activityRepository, testDispatcher)

        // When
        val result = useCase(filter)

        // Then
        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { activityRepository.getActivityLogs(filter) }
    }

    @Test
    fun `getActivityLogs should handle repository errors`() = runTest {
        // Given
        val filter = LogFilter()
        val exception = Exception("Database error")
        coEvery { activityRepository.getActivityLogs(filter) } returns Result.Error(exception)
        val useCase = GetActivityLogsUseCase(activityRepository, testDispatcher)

        // When
        val result = useCase(filter)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception, (result as Result.Error).exception)
    }

    // ============================================
    // ExportLogsUseCase Tests
    // ============================================

    @Test
    fun `exportLogs should export to CSV successfully`() = runTest {
        // Given
        val format = ExportFormat.CSV
        val uri = Uri.parse("file:///storage/exports/logs.csv")
        coEvery { activityRepository.exportLogs(format) } returns Result.Success(uri)
        val useCase = ExportLogsUseCase(activityRepository, testDispatcher)

        // When
        val result = useCase(format)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(uri, (result as Result.Success).data)
        coVerify(exactly = 1) { activityRepository.exportLogs(format) }
    }

    @Test
    fun `exportLogs should export to JSON successfully`() = runTest {
        // Given
        val format = ExportFormat.JSON
        val uri = Uri.parse("file:///storage/exports/logs.json")
        coEvery { activityRepository.exportLogs(format) } returns Result.Success(uri)
        val useCase = ExportLogsUseCase(activityRepository, testDispatcher)

        // When
        val result = useCase(format)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(uri, (result as Result.Success).data)
        coVerify(exactly = 1) { activityRepository.exportLogs(format) }
    }

    @Test
    fun `exportLogs should handle export errors`() = runTest {
        // Given
        val format = ExportFormat.CSV
        val exception = Exception("Export failed")
        coEvery { activityRepository.exportLogs(format) } returns Result.Error(exception)
        val useCase = ExportLogsUseCase(activityRepository, testDispatcher)

        // When
        val result = useCase(format)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception, (result as Result.Error).exception)
    }
}
