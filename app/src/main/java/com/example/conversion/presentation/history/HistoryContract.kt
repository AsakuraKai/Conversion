package com.example.conversion.presentation.history

import com.example.conversion.domain.model.OperationHistory
import com.example.conversion.domain.model.RenameOperation

/**
 * History Management feature contract defining State, Events, and Actions.
 * Follows MVI pattern for unidirectional data flow.
 */
object HistoryContract {

    /**
     * UI State for history screen.
     */
    data class State(
        val history: OperationHistory = OperationHistory.empty(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val showClearConfirmation: Boolean = false,
        val selectedOperation: RenameOperation? = null
    ) {
        /**
         * Whether any operations exist in history.
         */
        val hasOperations: Boolean
            get() = history.operations.isNotEmpty()

        /**
         * Whether undo is possible.
         */
        val canUndo: Boolean
            get() = history.canUndo()

        /**
         * Whether redo is possible.
         */
        val canRedo: Boolean
            get() = history.canRedo()

        /**
         * Get the next operation that would be undone.
         */
        val nextUndoOperation: RenameOperation?
            get() = history.getUndoOperation()

        /**
         * Get the next operation that would be redone.
         */
        val nextRedoOperation: RenameOperation?
            get() = history.getRedoOperation()

        /**
         * Get the list of operations for display.
         */
        val displayedOperations: List<RenameOperation>
            get() = history.operations
    }

    /**
     * One-time events sent from ViewModel to UI.
     */
    sealed class Event {
        /**
         * Operation was successfully undone.
         */
        data class OperationUndone(val operation: RenameOperation) : Event()

        /**
         * Operation was successfully redone.
         */
        data class OperationRedone(val operation: RenameOperation) : Event()

        /**
         * History was successfully cleared.
         */
        object HistoryCleared : Event()

        /**
         * Show error message to user.
         */
        data class ShowError(val message: String) : Event()

        /**
         * Show success message to user.
         */
        data class ShowSuccess(val message: String) : Event()
    }

    /**
     * User actions that can be performed on the history screen.
     */
    sealed class Action {
        /**
         * Load the operation history.
         */
        object LoadHistory : Action()

        /**
         * Undo the most recent operation.
         */
        object Undo : Action()

        /**
         * Redo the most recently undone operation.
         */
        object Redo : Action()

        /**
         * Undo a specific operation by ID.
         */
        data class UndoSpecific(val operationId: String) : Action()

        /**
         * Show clear history confirmation dialog.
         */
        object ShowClearConfirmation : Action()

        /**
         * Hide clear history confirmation dialog.
         */
        object HideClearConfirmation : Action()

        /**
         * Confirm clearing all history.
         */
        object ConfirmClear : Action()

        /**
         * Select an operation to view details.
         */
        data class SelectOperation(val operation: RenameOperation?) : Action()

        /**
         * Clear any error state.
         */
        object ClearError : Action()
    }
}
