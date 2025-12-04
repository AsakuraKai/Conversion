package com.example.conversion.presentation.preview

import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.model.PreviewItem
import com.example.conversion.domain.model.PreviewSummary
import com.example.conversion.domain.model.RenameConfig

/**
 * Preview Screen feature contract defining State, Events, and Actions.
 * Follows MVI pattern for unidirectional data flow.
 */
object PreviewContract {

    /**
     * UI State for preview screen.
     */
    sealed class State {
        /**
         * Loading state while generating previews.
         */
        data object Loading : State()

        /**
         * Success state with generated previews.
         */
        data class Success(
            val previews: List<PreviewItem>,
            val summary: PreviewSummary,
            val config: RenameConfig
        ) : State() {
            /**
             * Whether the rename operation can proceed.
             */
            val canProceed: Boolean
                get() = summary.canProceed
        }

        /**
         * Error state when preview generation fails.
         */
        data class Error(
            val message: String
        ) : State()
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
         * Navigate to rename progress screen with files to rename.
         */
        data class NavigateToRenameProgress(
            val files: List<FileItem>,
            val config: RenameConfig
        ) : Event()

        /**
         * Navigate back to previous screen.
         */
        data object NavigateBack : Event()
    }

    /**
     * User actions that trigger state changes.
     */
    sealed class Action {
        /**
         * Initialize and generate preview.
         */
        data class Initialize(
            val files: List<FileItem>,
            val config: RenameConfig
        ) : Action()

        /**
         * User wants to proceed with renaming.
         */
        data object ConfirmRename : Action()

        /**
         * User wants to go back and modify configuration.
         */
        data object Back : Action()

        /**
         * Retry preview generation after error.
         */
        data object Retry : Action()
    }
}
