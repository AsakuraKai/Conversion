package com.example.conversion.presentation.renameprogress

import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.model.RenameProgress
import com.example.conversion.domain.model.RenameStatus

/**
 * Rename Progress feature contract defining State, Events, and Actions.
 * Follows MVI pattern for unidirectional data flow.
 */
object RenameProgressContract {

    /**
     * UI State for rename progress screen.
     */
    data class State(
        val progress: RenameProgress? = null,
        val successCount: Int = 0,
        val failedCount: Int = 0,
        val skippedCount: Int = 0,
        val isProcessing: Boolean = false,
        val isComplete: Boolean = false,
        val isCancelled: Boolean = false,
        val error: String? = null
    ) {
        /**
         * Progress percentage (0.0 to 1.0) for UI indicators.
         */
        val progressPercentage: Float
            get() = progress?.let { it.progressPercentage / 100f } ?: 0f

        /**
         * Human-readable progress string (e.g., "5/10").
         */
        val progressString: String
            get() = progress?.progressString ?: "0/0"

        /**
         * Current file being processed name.
         */
        val currentFileName: String
            get() = progress?.currentFile?.name ?: ""

        /**
         * Total number of files to process.
         */
        val totalFiles: Int
            get() = progress?.total ?: 0

        /**
         * Whether there are any errors during processing.
         */
        val hasErrors: Boolean
            get() = failedCount > 0

        /**
         * Whether the operation can be cancelled.
         */
        val canCancel: Boolean
            get() = isProcessing && !isComplete && !isCancelled

        /**
         * Summary message for completion.
         */
        val completionMessage: String
            get() = when {
                totalFiles == 0 -> "No files to rename"
                failedCount == 0 && skippedCount == 0 -> 
                    "Successfully renamed $successCount file${if (successCount != 1) "s" else ""}"
                else -> buildString {
                    append("Completed: ")
                    append("$successCount successful")
                    if (failedCount > 0) append(", $failedCount failed")
                    if (skippedCount > 0) append(", $skippedCount skipped")
                }
            }
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
         * Show completion dialog with summary.
         */
        data class ShowCompletion(
            val total: Int,
            val successful: Int,
            val failed: Int,
            val skipped: Int
        ) : Event()

        /**
         * Navigate back to previous screen.
         */
        data object NavigateBack : Event()

        /**
         * Operation was cancelled by user.
         */
        data object OperationCancelled : Event()
    }

    /**
     * User actions that trigger state changes.
     */
    sealed class Action {
        /**
         * Start the rename operation.
         */
        data class StartRename(
            val files: List<FileItem>,
            val config: RenameConfig
        ) : Action()

        /**
         * Cancel the ongoing rename operation.
         */
        data object CancelRename : Action()

        /**
         * Retry failed files.
         */
        data object RetryFailed : Action()

        /**
         * Acknowledge completion and navigate back.
         */
        data object AcknowledgeCompletion : Action()

        /**
         * Clear error message.
         */
        data object ClearError : Action()
    }
}
