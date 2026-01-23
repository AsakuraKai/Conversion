package com.example.conversion.presentation.fileselection

import com.example.conversion.domain.model.FileFilter
import com.example.conversion.domain.model.FileItem

/**
 * File Selection feature contract defining State, Events, and Actions.
 * Follows MVI pattern for unidirectional data flow.
 */
object FileSelectionContract {

    /**
     * UI State for file selection screen.
     */
    data class State(
        val files: List<FileItem> = emptyList(),
        val selectedFiles: Set<FileItem> = emptySet(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val filter: FileFilter = FileFilter.DEFAULT,
    ) {
        /**
         * Whether any files are selected.
         */
        val hasSelection: Boolean
            get() = selectedFiles.isNotEmpty()

        /**
         * Number of selected files.
         */
        val selectedCount: Int
            get() = selectedFiles.size

        /**
         * Whether all visible files are selected.
         */
        val areAllSelected: Boolean
            get() = files.isNotEmpty() && selectedFiles.size == files.size

        /**
         * Whether the screen has data to display.
         */
        val isEmpty: Boolean
            get() = !isLoading && files.isEmpty()

        /**
         * Whether data can be displayed.
         */
        val canShowContent: Boolean
            get() = !isLoading && error == null && files.isNotEmpty()
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
         * Navigate to rename screen with selected files.
         */
        data class NavigateToRename(val files: List<FileItem>) : Event()

        /**
         * Show error dialog.
         */
        data class ShowError(val title: String, val message: String) : Event()
    }

    /**
     * User actions that trigger state changes.
     */
    sealed class Action {
        /**
         * Load files based on current filter.
         */
        data object LoadFiles : Action()

        /**
         * Refresh file list.
         */
        data object RefreshFiles : Action()

        /**
         * Toggle selection of a specific file.
         */
        data class ToggleSelection(val file: FileItem) : Action()

        /**
         * Select all visible files.
         */
        data object SelectAll : Action()

        /**
         * Clear all selections.
         */
        data object ClearSelection : Action()

        /**
         * Apply a new filter and reload files.
         */
        data class ApplyFilter(val filter: FileFilter) : Action()

        /**
         * Confirm selection and proceed to rename.
         */
        data object ConfirmSelection : Action()

        /**
         * Clear error message.
         */
        data object ClearError : Action()
    }
}
