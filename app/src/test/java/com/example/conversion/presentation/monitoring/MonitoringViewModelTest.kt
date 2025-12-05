package com.example.conversion.presentation.monitoring

import android.net.Uri
import com.example.conversion.domain.model.FileEvent
import com.example.conversion.domain.model.FileEventType
import com.example.conversion.domain.model.FolderMonitor
import com.example.conversion.domain.model.MonitoringStatus
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.usecase.monitoring.ObserveFileEventsUseCase
import com.example.conversion.domain.usecase.monitoring.ObserveMonitoringStatusUseCase
import com.example.conversion.domain.usecase.monitoring.StartMonitoringUseCase
import com.example.conversion.domain.usecase.monitoring.StopMonitoringUseCase
import com.example.conversion.presentation.monitoring.MonitoringContract.Action
import com.example.conversion.presentation.monitoring.MonitoringContract.Event
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for MonitoringViewModel.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MonitoringViewModelTest {

    private lateinit var viewModel: MonitoringViewModel
    private lateinit var startMonitoringUseCase: StartMonitoringUseCase
    private lateinit var stopMonitoringUseCase: StopMonitoringUseCase
    private lateinit var observeMonitoringStatusUseCase: ObserveMonitoringStatusUseCase
    private lateinit var observeFileEventsUseCase: ObserveFileEventsUseCase

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        startMonitoringUseCase = mockk(relaxed = true)
        stopMonitoringUseCase = mockk(relaxed = true)
        observeMonitoringStatusUseCase = mockk()
        observeFileEventsUseCase = mockk()

        // Default mock behavior
        every { observeMonitoringStatusUseCase() } returns flowOf(MonitoringStatus.Inactive)
        every { observeFileEventsUseCase() } returns flowOf()

        viewModel = MonitoringViewModel(
            startMonitoringUseCase = startMonitoringUseCase,
            stopMonitoringUseCase = stopMonitoringUseCase,
            observeMonitoringStatusUseCase = observeMonitoringStatusUseCase,
            observeFileEventsUseCase = observeFileEventsUseCase,
            ioDispatcher = testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is inactive`() = runTest {
        val state = viewModel.state.value
        assertFalse(state.isMonitoring)
        assertNull(state.monitoredFolder)
        assertEquals(0, state.filesProcessed)
        assertTrue(state.recentEvents.isEmpty())
    }

    @Test
    fun `handleAction SelectFolder updates state`() = runTest {
        val folderPath = "/storage/emulated/0/Download"
        
        viewModel.handleAction(Action.SelectFolder(folderPath))
        
        val state = viewModel.state.value
        assertEquals(folderPath, state.monitoredFolder)
    }

    @Test
    fun `handleAction StartMonitoring with valid config calls use case`() = runTest {
        val folderMonitor = FolderMonitor(
            folderPath = "/storage/emulated/0/Download",
            folderUri = Uri.parse("/storage/emulated/0/Download"),
            renameConfig = RenameConfig(prefix = "IMG_"),
            isActive = true
        )
        
        coEvery { startMonitoringUseCase(folderMonitor) } returns Unit
        
        viewModel.handleAction(Action.StartMonitoring(folderMonitor))
        
        coVerify { startMonitoringUseCase(folderMonitor) }
    }

    @Test
    fun `handleAction StartMonitoring with invalid config shows error`() = runTest {
        val invalidMonitor = FolderMonitor(
            folderPath = "", // Invalid empty path
            folderUri = Uri.parse(""),
            renameConfig = RenameConfig(),
            isActive = true
        )
        
        viewModel.handleAction(Action.StartMonitoring(invalidMonitor))
        
        // Should not call use case with invalid config
        coVerify(exactly = 0) { startMonitoringUseCase(any()) }
    }

    @Test
    fun `handleAction StopMonitoring calls use case`() = runTest {
        coEvery { stopMonitoringUseCase() } returns Unit
        
        viewModel.handleAction(Action.StopMonitoring)
        
        coVerify { stopMonitoringUseCase() }
    }

    @Test
    fun `handleAction UpdatePattern updates state`() = runTest {
        val pattern = "*.jpg"
        
        viewModel.handleAction(Action.UpdatePattern(pattern))
        
        val state = viewModel.state.value
        assertEquals(pattern, state.filePattern)
    }

    @Test
    fun `handleAction UpdateMonitorSubfolders updates state`() = runTest {
        viewModel.handleAction(Action.UpdateMonitorSubfolders(true))
        
        val state = viewModel.state.value
        assertTrue(state.monitorSubfolders)
    }

    @Test
    fun `observeMonitoringStatus updates state when Active`() = runTest {
        val activeStatus = MonitoringStatus.Active(
            folderPath = "/storage/emulated/0/Download",
            filesProcessed = 5
        )
        every { observeMonitoringStatusUseCase() } returns flowOf(activeStatus)
        
        // Create new viewModel to trigger init
        viewModel = MonitoringViewModel(
            startMonitoringUseCase = startMonitoringUseCase,
            stopMonitoringUseCase = stopMonitoringUseCase,
            observeMonitoringStatusUseCase = observeMonitoringStatusUseCase,
            observeFileEventsUseCase = observeFileEventsUseCase,
            ioDispatcher = testDispatcher
        )
        
        val state = viewModel.state.value
        assertTrue(state.isMonitoring)
        assertEquals("/storage/emulated/0/Download", state.monitoredFolder)
        assertEquals(5, state.filesProcessed)
    }

    @Test
    fun `observeMonitoringStatus updates state when Inactive`() = runTest {
        every { observeMonitoringStatusUseCase() } returns flowOf(MonitoringStatus.Inactive)
        
        viewModel = MonitoringViewModel(
            startMonitoringUseCase = startMonitoringUseCase,
            stopMonitoringUseCase = stopMonitoringUseCase,
            observeMonitoringStatusUseCase = observeMonitoringStatusUseCase,
            observeFileEventsUseCase = observeFileEventsUseCase,
            ioDispatcher = testDispatcher
        )
        
        val state = viewModel.state.value
        assertFalse(state.isMonitoring)
    }

    @Test
    fun `observeMonitoringStatus updates state when Error`() = runTest {
        val errorStatus = MonitoringStatus.Error("Permission denied")
        every { observeMonitoringStatusUseCase() } returns flowOf(errorStatus)
        
        viewModel = MonitoringViewModel(
            startMonitoringUseCase = startMonitoringUseCase,
            stopMonitoringUseCase = stopMonitoringUseCase,
            observeMonitoringStatusUseCase = observeMonitoringStatusUseCase,
            observeFileEventsUseCase = observeFileEventsUseCase,
            ioDispatcher = testDispatcher
        )
        
        val state = viewModel.state.value
        assertFalse(state.isMonitoring)
        assertEquals("Permission denied", state.error)
    }

    @Test
    fun `observeFileEvents updates state with new events`() = runTest {
        val fileEvent = FileEvent(
            filePath = "/storage/emulated/0/Download/test.jpg",
            eventType = FileEventType.CREATED,
            timestamp = System.currentTimeMillis()
        )
        every { observeFileEventsUseCase() } returns flowOf(fileEvent)
        
        viewModel = MonitoringViewModel(
            startMonitoringUseCase = startMonitoringUseCase,
            stopMonitoringUseCase = stopMonitoringUseCase,
            observeMonitoringStatusUseCase = observeMonitoringStatusUseCase,
            observeFileEventsUseCase = observeFileEventsUseCase,
            ioDispatcher = testDispatcher
        )
        
        val state = viewModel.state.value
        assertTrue(state.recentEvents.isNotEmpty())
        assertEquals(fileEvent, state.recentEvents.first())
    }

    @Test
    fun `handleAction ClearEvents clears recent events`() = runTest {
        // Add an event first
        val fileEvent = FileEvent(
            filePath = "/test.jpg",
            eventType = FileEventType.CREATED
        )
        every { observeFileEventsUseCase() } returns flowOf(fileEvent)
        
        viewModel = MonitoringViewModel(
            startMonitoringUseCase = startMonitoringUseCase,
            stopMonitoringUseCase = stopMonitoringUseCase,
            observeMonitoringStatusUseCase = observeMonitoringStatusUseCase,
            observeFileEventsUseCase = observeFileEventsUseCase,
            ioDispatcher = testDispatcher
        )
        
        // Verify event is added
        assertTrue(viewModel.state.value.recentEvents.isNotEmpty())
        
        // Clear events
        viewModel.handleAction(Action.ClearEvents)
        
        // Verify events are cleared
        assertTrue(viewModel.state.value.recentEvents.isEmpty())
    }

    @Test
    fun `canStartMonitoring returns true when folder selected and not monitoring`() = runTest {
        viewModel.handleAction(Action.SelectFolder("/storage/emulated/0/Download"))
        
        val state = viewModel.state.value
        assertTrue(state.canStartMonitoring)
    }

    @Test
    fun `canStartMonitoring returns false when no folder selected`() = runTest {
        val state = viewModel.state.value
        assertFalse(state.canStartMonitoring)
    }

    @Test
    fun `canStopMonitoring returns true when monitoring is active`() = runTest {
        val activeStatus = MonitoringStatus.Active(
            folderPath = "/storage/emulated/0/Download",
            filesProcessed = 0
        )
        every { observeMonitoringStatusUseCase() } returns flowOf(activeStatus)
        
        viewModel = MonitoringViewModel(
            startMonitoringUseCase = startMonitoringUseCase,
            stopMonitoringUseCase = stopMonitoringUseCase,
            observeMonitoringStatusUseCase = observeMonitoringStatusUseCase,
            observeFileEventsUseCase = observeFileEventsUseCase,
            ioDispatcher = testDispatcher
        )
        
        val state = viewModel.state.value
        assertTrue(state.canStopMonitoring)
    }

    @Test
    fun `recent events list is limited to 10 items`() = runTest {
        val state = viewModel.state.value.copy(
            recentEvents = (1..15).map { index ->
                FileEvent(
                    filePath = "/test$index.jpg",
                    eventType = FileEventType.CREATED
                )
            }
        )
        
        assertEquals(10, state.recentEventsList.size)
    }
}
