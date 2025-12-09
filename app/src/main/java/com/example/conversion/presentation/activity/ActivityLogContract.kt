package com.example.conversion.presentation.activity

import com.example.conversion.domain.model.ActivityLog
import com.example.conversion.domain.model.ActivityStatus
import com.example.conversion.domain.model.ExportFormat
import java.time.LocalDateTime

/**
 * Activity Log feature contract defining State, Events, and Actions.
 * Follows MVI pattern for unidirectional data flow.
 */
object ActivityLogContract {

    /**
     * UI State for activity log screen.
     */
    data class State(
        val logs: List<ActivityLog> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val showFilterDialog: Boolean = false,
        val showExportDialog: Boolean = false,
        val selectedLog: ActivityLog? = null,
        val filterStartDate: LocalDateTime? = null,
        val filterEndDate: LocalDateTime? = null,
        val filterStatus: ActivityStatus? = null,
        val filterAction: String? = null,
        val searchQuery: String = ""
    ) {
        /**
         * Whether any logs exist.
         */
        val hasLogs: Boolean
            get() = logs.isNotEmpty()

        /**
         * Whether any filters are active.
         */
        val hasActiveFilters: Boolean
            get() = filterStartDate != null || 
                    filterEndDate != null || 
                    filterStatus != null || 
                    filterAction != null ||
                    searchQuery.isNotBlank()

        /**
         * Get filtered logs based on search query.
         */
        val displayedLogs: List<ActivityLog>
            get() = if (searchQuery.isBlank()) {
                logs
            } else {
                logs.filter { log ->
                    log.action.contains(searchQuery, ignoreCase = true) ||
                    log.details.contains(searchQuery, ignoreCase = true)
                }
            }

        /**
         * Count of displayed logs after filtering.
         */
        val displayedLogCount: Int
            get() = displayedLogs.size
    }

    /**
     * One-time events sent from ViewModel to UI.
     */
    sealed class Event {
        /**
         * Logs were successfully exported.
         */
        data class LogsExported(val format: ExportFormat, val fileUri: String) : Event()

        /**
         * Show error message to user.
         */
        data class ShowError(val message: String) : Event()

        /**
         * Show success message to user.
         */
        data class ShowSuccess(val message: String) : Event()

        /**
         * Filters were successfully applied.
         */
        object FiltersApplied : Event()

        /**
         * Filters were successfully cleared.
         */
        object FiltersCleared : Event()
    }

    /**
     * User actions that can be performed on the activity log screen.
     */
    sealed class Action {
        /**
         * Load the activity logs with current filters.
         */
        object LoadLogs : Action()

        /**
         * Refresh the activity logs.
         */
        object RefreshLogs : Action()

        /**
         * Show filter dialog.
         */
        object ShowFilterDialog : Action()

        /**
         * Hide filter dialog.
         */
        object HideFilterDialog : Action()

        /**
         * Apply filters to logs.
         */
        data class ApplyFilters(
            val startDate: LocalDateTime? = null,
            val endDate: LocalDateTime? = null,
            val status: ActivityStatus? = null,
            val action: String? = null
        ) : Action()

        /**
         * Clear all filters.
         */
        object ClearFilters : Action()

        /**
         * Update search query.
         */
        data class UpdateSearchQuery(val query: String) : Action()

        /**
         * Show export dialog.
         */
        object ShowExportDialog : Action()

        /**
         * Hide export dialog.
         */
        object HideExportDialog : Action()

        /**
         * Export logs in specified format.
         */
        data class ExportLogs(val format: ExportFormat) : Action()

        /**
         * Select a log to view details.
         */
        data class SelectLog(val log: ActivityLog?) : Action()

        /**
         * Clear any error state.
         */
        object ClearError : Action()
    }
}
