package com.example.conversion.domain.model

/**
 * Represents the complete history of rename operations with support for undo/redo.
 *
 * This model maintains a list of operations and tracks the current position in the history,
 * enabling navigation through previously performed operations.
 *
 * @property operations List of all rename operations in chronological order
 * @property currentIndex Current position in the history (-1 if no operations or all undone)
 */
data class OperationHistory(
    val operations: List<RenameOperation> = emptyList(),
    val currentIndex: Int = -1
) {
    /**
     * Checks if undo is possible.
     *
     * @return true if there are operations that can be undone
     */
    fun canUndo(): Boolean = currentIndex >= 0 && operations.isNotEmpty()

    /**
     * Checks if redo is possible.
     *
     * @return true if there are operations that can be redone
     */
    fun canRedo(): Boolean = currentIndex < operations.size - 1

    /**
     * Gets the operation that would be undone if undo is called.
     *
     * @return The operation to undo, or null if undo is not possible
     */
    fun getUndoOperation(): RenameOperation? {
        return if (canUndo()) operations[currentIndex] else null
    }

    /**
     * Gets the operation that would be redone if redo is called.
     *
     * @return The operation to redo, or null if redo is not possible
     */
    fun getRedoOperation(): RenameOperation? {
        return if (canRedo()) operations[currentIndex + 1] else null
    }

    /**
     * Returns a new history with the given operation added.
     *
     * If the current index is not at the end, operations after the current index are discarded.
     *
     * @param operation The operation to add
     * @return A new OperationHistory with the operation added
     */
    fun addOperation(operation: RenameOperation): OperationHistory {
        val newOperations = if (currentIndex < operations.size - 1) {
            // Discard operations after current index
            operations.subList(0, currentIndex + 1) + operation
        } else {
            operations + operation
        }
        return copy(
            operations = newOperations,
            currentIndex = newOperations.size - 1
        )
    }

    /**
     * Returns a new history after performing an undo.
     *
     * @return A new OperationHistory with decremented index
     */
    fun undo(): OperationHistory {
        return if (canUndo()) {
            copy(currentIndex = currentIndex - 1)
        } else {
            this
        }
    }

    /**
     * Returns a new history after performing a redo.
     *
     * @return A new OperationHistory with incremented index
     */
    fun redo(): OperationHistory {
        return if (canRedo()) {
            copy(currentIndex = currentIndex + 1)
        } else {
            this
        }
    }

    /**
     * Returns a new empty history.
     *
     * @return An empty OperationHistory
     */
    fun clear(): OperationHistory {
        return OperationHistory()
    }

    companion object {
        /**
         * Creates an empty history.
         *
         * @return An empty OperationHistory instance
         */
        fun empty(): OperationHistory = OperationHistory()
    }
}
