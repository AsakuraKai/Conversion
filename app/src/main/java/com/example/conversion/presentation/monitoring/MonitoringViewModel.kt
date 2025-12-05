package com.example.conversion.presentation.monitoring

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.model.FileEvent
import com.example.conversion.domain.model.FolderMonitor
import com.example.conversion.domain.model.MonitoringStatus
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.usecase.monitoring.ObserveFileEventsUseCase
import com.example.conversion.domain.usecase.monitoring.ObserveMonitoringStatusUseCase
import com.example.conversion.domain.usecase.monitoring.StartMonitoringUseCase
import com.example.conversion.domain.usecase.monitoring.StopMonitoringUseCase
import com.example.conversion.presentation.base.BaseViewModel
import com.example.conversion.presentation.monitoring.MonitoringContract.Action
import com.example.conversion.presentation.monitoring.MonitoringContract.Event
import com.example.conversion.presentation.monitoring.MonitoringContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for folder monitoring screen.
 * Manages monitoring state and handles user actions.
 *
 * @property startMonitoringUseCase Use case for starting monitoring
 * @property stopMonitoringUseCase Use case for stopping monitoring
 * @property observeMonitoringStatusUseCase Use case for observing monitoring status
 * @property observeFileEventsUseCase Use case for observing file events
 * @property ioDispatcher IO dispatcher for background operations
 */
@HiltViewModel
class MonitoringViewModel @Inject constructor(
    private val startMonitoringUseCase: StartMonitoringUseCase,
    private val stopMonitoringUseCase: StopMonitoringUseCase,
    private val observeMonitoringStatusUseCase: ObserveMonitoringStatusUseCase,
    private val observeFileEventsUseCase: ObserveFileEventsUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<State, Event>(State()) {

    init {
        observeMonitoringStatus()
        observeFileEvents()
    }

    /**
     * Handles user actions from the UI.
     */
    fun handleAction(action: Action) {
        when (action) {
            is Action.SelectFolder -> handleSelectFolder(action.path)
            is Action.StartMonitoring -> handleStartMonitoring(action.config)
            is Action.StopMonitoring -> handleStopMonitoring()
            is Action.UpdatePattern -> handleUpdatePattern(action.pattern)
            is Action.UpdateMonitorSubfolders -> handleUpdateMonitorSubfolders(action.monitor)
            is Action.UpdateRenameConfig -> handleUpdateRenameConfig(action.config)
            is Action.RefreshStatus -> observeMonitoringStatus()
            is Action.ClearEvents -> handleClearEvents()
        }
    }

    /**
     * Observes monitoring status changes from the repository.
     */
    private fun observeMonitoringStatus() {
        observeMonitoringStatusUseCase()
            .onEach { status ->
                when (status) {
                    is MonitoringStatus.Active -> {
                        updateState {
                            copy(
                                isMonitoring = true,
                                filesProcessed = status.filesProcessed,
                                monitoredFolder = status.folderPath,
                                error = null,
                                isLoading = false
                            )
                        }
                    }
                    is MonitoringStatus.Inactive -> {
                        updateState {
                            copy(
                                isMonitoring = false,
                                isLoading = false
                            )
                        }
                    }
                    is MonitoringStatus.Error -> {
                        updateState {
                            copy(
                                isMonitoring = false,
                                error = status.error,
                                isLoading = false
                            )
                        }
                        sendEvent(Event.ShowMessage(status.error))
                    }
                }
            }
            .catch { error ->
                handleError(error)
                updateState { copy(error = error.message, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Observes file events from the monitored folder.
     */
    private fun observeFileEvents() {
        observeFileEventsUseCase()
            .onEach { event ->
                updateState {
                    copy(recentEvents = (listOf(event) + recentEvents).take(20))
                }
            }
            .catch { error ->
                handleError(error)
            }
            .launchIn(viewModelScope)
    }

    /**
     * Handles folder selection.
     */
    private fun handleSelectFolder(path: String) {
        updateState {
            copy(
                monitoredFolder = path,
                error = null
            )
        }
    }

    /**
     * Handles starting monitoring.
     */
    private fun handleStartMonitoring(config: FolderMonitor) {
        if (!config.isValid()) {
            sendEvent(Event.ShowMessage("Invalid monitoring configuration"))
            return
        }

        updateState { copy(isLoading = true) }

        viewModelScope.launch(ioDispatcher) {
            try {
                startMonitoringUseCase(config)
                updateState {
                    copy(
                        config = config,
                        isLoading = false
                    )
                }
                sendEvent(Event.MonitoringStarted(config.folderPath))
                sendEvent(Event.ShowMessage("Monitoring started for ${config.folderPath}"))
            } catch (e: Exception) {
                handleError(e)
                updateState {
                    copy(
                        error = e.message ?: "Failed to start monitoring",
                        isLoading = false
                    )
                }
                sendEvent(Event.ShowMessage(e.message ?: "Failed to start monitoring"))
            }
        }
    }

    /**
     * Handles stopping monitoring.
     */
    private fun handleStopMonitoring() {
        updateState { copy(isLoading = true) }

        viewModelScope.launch(ioDispatcher) {
            try {
                stopMonitoringUseCase()
                updateState {
                    copy(
                        config = null,
                        recentEvents = emptyList(),
                        filesProcessed = 0,
                        isLoading = false
                    )
                }
                sendEvent(Event.MonitoringStopped)
                sendEvent(Event.ShowMessage("Monitoring stopped"))
            } catch (e: Exception) {
                handleError(e)
                updateState {
                    copy(
                        error = e.message ?: "Failed to stop monitoring",
                        isLoading = false
                    )
                }
                sendEvent(Event.ShowMessage(e.message ?: "Failed to stop monitoring"))
            }
        }
    }

    /**
     * Handles updating file pattern filter.
     */
    private fun handleUpdatePattern(pattern: String) {
        updateState {
            copy(filePattern = pattern)
        }
    }

    /**
     * Handles updating monitor subfolders setting.
     */
    private fun handleUpdateMonitorSubfolders(monitor: Boolean) {
        updateState {
            copy(monitorSubfolders = monitor)
        }
    }

    /**
     * Handles updating rename configuration.
     */
    private fun handleUpdateRenameConfig(config: RenameConfig) {
        updateState {
            copy(renameConfig = config)
        }
    }

    /**
     * Handles clearing recent events.
     */
    private fun handleClearEvents() {
        updateState {
            copy(recentEvents = emptyList())
        }
    }

    override fun handleError(error: Throwable) {
        super.handleError(error)
        error.printStackTrace()
    }
}
