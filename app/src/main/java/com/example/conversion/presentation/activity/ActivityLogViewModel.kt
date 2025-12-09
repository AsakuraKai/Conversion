package com.example.conversion.presentation.activity

import androidx.lifecycle.viewModelScope
import com.example.conversion.domain.model.ActivityStatus
import com.example.conversion.domain.model.ExportFormat
import com.example.conversion.domain.model.LogFilter
import com.example.conversion.domain.usecase.activity.ExportLogsUseCase
import com.example.conversion.domain.usecase.activity.GetActivityLogsUseCase
import com.example.conversion.presentation.activity.ActivityLogContract.Action
import com.example.conversion.presentation.activity.ActivityLogContract.Event
import com.example.conversion.presentation.activity.ActivityLogContract.State
import com.example.conversion.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * ViewModel for activity log screen.
 * Manages activity log display, filtering, and export functionality.
 */
@HiltViewModel
class ActivityLogViewModel @Inject constructor(
    private val getActivityLogsUseCase: GetActivityLogsUseCase,
    private val exportLogsUseCase: ExportLogsUseCase
) : BaseViewModel<State, Event>(State()) {

    init {
        loadLogs()
    }

    /**
     * Handles user actions and updates state accordingly.
     */
    fun handleAction(action: Action) {
        when (action) {
            is Action.LoadLogs -> loadLogs()
            is Action.RefreshLogs -> refreshLogs()
            is Action.ShowFilterDialog -> showFilterDialog()
            is Action.HideFilterDialog -> hideFilterDialog()
            is Action.ApplyFilters -> applyFilters(
                action.startDate,
                action.endDate,
                action.status,
                action.action
            )
            is Action.ClearFilters -> clearFilters()
            is Action.UpdateSearchQuery -> updateSearchQuery(action.query)
            is Action.ShowExportDialog -> showExportDialog()
            is Action.HideExportDialog -> hideExportDialog()
            is Action.ExportLogs -> exportLogs(action.format)
            is Action.SelectLog -> selectLog(action.log)
            is Action.ClearError -> clearError()
        }
    }

    /**
     * Loads activity logs with current filters.
     */
    private fun loadLogs() {
        updateState { copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val filter = LogFilter(
                startDate = currentState.filterStartDate,
                endDate = currentState.filterEndDate,
                status = currentState.filterStatus,
                action = currentState.filterAction,
                limit = 500 // Reasonable limit for UI display
            )

            executeUseCase(
                block = { getActivityLogsUseCase(filter) },
                onSuccess = { logs ->
                    updateState {
                        copy(
                            logs = logs,
                            isLoading = false
                        )
                    }
                },
                onError = { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load activity logs"
                        )
                    }
                    sendEvent(Event.ShowError("Failed to load logs: ${error.message}"))
                }
            )
        }
    }

    /**
     * Refreshes the activity logs.
     */
    private fun refreshLogs() {
        loadLogs()
    }

    /**
     * Shows the filter dialog.
     */
    private fun showFilterDialog() {
        updateState { copy(showFilterDialog = true) }
    }

    /**
     * Hides the filter dialog.
     */
    private fun hideFilterDialog() {
        updateState { copy(showFilterDialog = false) }
    }

    /**
     * Applies filters to the activity logs.
     */
    private fun applyFilters(
        startDate: LocalDateTime?,
        endDate: LocalDateTime?,
        status: ActivityStatus?,
        action: String?
    ) {
        updateState {
            copy(
                filterStartDate = startDate,
                filterEndDate = endDate,
                filterStatus = status,
                filterAction = action,
                showFilterDialog = false
            )
        }
        loadLogs()
        sendEvent(Event.FiltersApplied)
        sendEvent(Event.ShowSuccess("Filters applied"))
    }

    /**
     * Clears all filters.
     */
    private fun clearFilters() {
        updateState {
            copy(
                filterStartDate = null,
                filterEndDate = null,
                filterStatus = null,
                filterAction = null,
                searchQuery = ""
            )
        }
        loadLogs()
        sendEvent(Event.FiltersCleared)
        sendEvent(Event.ShowSuccess("Filters cleared"))
    }

    /**
     * Updates the search query.
     */
    private fun updateSearchQuery(query: String) {
        updateState { copy(searchQuery = query) }
    }

    /**
     * Shows the export dialog.
     */
    private fun showExportDialog() {
        updateState { copy(showExportDialog = true) }
    }

    /**
     * Hides the export dialog.
     */
    private fun hideExportDialog() {
        updateState { copy(showExportDialog = false) }
    }

    /**
     * Exports activity logs in the specified format.
     */
    private fun exportLogs(format: ExportFormat) {
        updateState { copy(isLoading = true, error = null, showExportDialog = false) }

        viewModelScope.launch {
            executeUseCase(
                block = { exportLogsUseCase(format) },
                onSuccess = { uri ->
                    updateState { copy(isLoading = false) }
                    sendEvent(Event.LogsExported(format, uri.toString()))
                    val formatName = when (format) {
                        ExportFormat.CSV -> "CSV"
                        ExportFormat.JSON -> "JSON"
                    }
                    sendEvent(Event.ShowSuccess("Logs exported to $formatName"))
                },
                onError = { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            error = error.message ?: "Failed to export logs"
                        )
                    }
                    sendEvent(Event.ShowError("Export failed: ${error.message}"))
                }
            )
        }
    }

    /**
     * Selects a log to view details.
     */
    private fun selectLog(log: com.example.conversion.domain.model.ActivityLog?) {
        updateState { copy(selectedLog = log) }
    }

    /**
     * Clears any error state.
     */
    private fun clearError() {
        updateState { copy(error = null) }
    }
}
