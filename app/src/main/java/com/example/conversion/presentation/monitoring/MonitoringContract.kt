package com.example.conversion.presentation.monitoring

import com.example.conversion.domain.model.FileEvent
import com.example.conversion.domain.model.FolderMonitor
import com.example.conversion.domain.model.RenameConfig

/**
 * Monitoring feature contract defining State, Events, and Actions.
 * Follows MVI pattern for unidirectional data flow.
 */
object MonitoringContract {

    /**
     * UI State for monitoring screen.
     */
    data class State(
        val isMonitoring: Boolean = false,
        val monitoredFolder: String? = null,
        val recentEvents: List<FileEvent> = emptyList(),
        val config: FolderMonitor? = null,
        val error: String? = null,
        val filesProcessed: Int = 0,
        val filePattern: String = "*.*",
        val monitorSubfolders: Boolean = false,
        val renameConfig: RenameConfig = RenameConfig(),
        val isLoading: Boolean = false
    ) {
        /**
         * Whether monitoring can be started.
         */
        val canStartMonitoring: Boolean
            get() = monitoredFolder != null && !isMonitoring && !isLoading

        /**
         * Whether monitoring can be stopped.
         */
        val canStopMonitoring: Boolean
            get() = isMonitoring && !isLoading

        /**
         * Whether there are recent events to display.
         */
        val hasRecentEvents: Boolean
            get() = recentEvents.isNotEmpty()

        /**
         * Display text for monitoring status.
         */
        val statusText: String
            get() = when {
                isMonitoring -> "Monitoring active"
                error != null -> "Error: $error"
                else -> "Monitoring inactive"
            }

        /**
         * Limited list of recent events (max 10).
         */
        val recentEventsList: List<FileEvent>
            get() = recentEvents.take(10)
    }

    /**
     * One-time events sent from ViewModel to UI.
     */
    sealed class Event {
        /**
         * Show a message to the user.
         */
        data class ShowMessage(val message: String) : Event()

        /**
         * Navigate to settings screen.
         */
        data object NavigateToSettings : Event()

        /**
         * Monitoring started successfully.
         */
        data class MonitoringStarted(val folderPath: String) : Event()

        /**
         * Monitoring stopped successfully.
         */
        data object MonitoringStopped : Event()

        /**
         * Permission required for monitoring.
         */
        data object PermissionRequired : Event()
    }

    /**
     * User actions from UI.
     */
    sealed class Action {
        /**
         * Select a folder to monitor.
         */
        data class SelectFolder(val path: String) : Action()

        /**
         * Start monitoring with current configuration.
         */
        data class StartMonitoring(val config: FolderMonitor) : Action()

        /**
         * Stop active monitoring.
         */
        data object StopMonitoring : Action()

        /**
         * Update file pattern filter.
         */
        data class UpdatePattern(val pattern: String) : Action()

        /**
         * Update whether to monitor subfolders.
         */
        data class UpdateMonitorSubfolders(val monitor: Boolean) : Action()

        /**
         * Update rename configuration.
         */
        data class UpdateRenameConfig(val config: RenameConfig) : Action()

        /**
         * Refresh monitoring status.
         */
        data object RefreshStatus : Action()

        /**
         * Clear recent events list.
         */
        data object ClearEvents : Action()
    }
}
