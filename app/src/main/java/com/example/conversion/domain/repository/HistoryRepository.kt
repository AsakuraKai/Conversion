package com.example.conversion.domain.repository

import android.net.Uri
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.OperationHistory
import com.example.conversion.domain.model.RenameOperation
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing rename operation history.
 *
 * This repository provides functionality to track, store, and manage rename operations,
 * enabling undo/redo capabilities throughout the application.
 */
interface HistoryRepository {

    /**
     * Saves a rename operation to the history.
     *
     * @param operation The operation to save
     * @return Success with Unit, or Error with exception
     */
    suspend fun saveOperation(operation: RenameOperation): Result<Unit>

    /**
     * Retrieves the complete operation history.
     *
     * @return Success with list of operations in chronological order, or Error with exception
     */
    suspend fun getHistory(): Result<List<RenameOperation>>

    /**
     * Observes changes to the operation history.
     *
     * @return Flow emitting the current operation history whenever it changes
     */
    fun observeHistory(): Flow<OperationHistory>

    /**
     * Clears all operations from the history.
     *
     * This action is irreversible and should be confirmed with the user.
     *
     * @return Success with Unit, or Error with exception
     */
    suspend fun clearHistory(): Result<Unit>

    /**
     * Undoes a rename operation by reverting the file to its original name.
     *
     * @param operation The operation to undo
     * @return Success with the reverted file URI, or Error with exception
     */
    suspend fun undoOperation(operation: RenameOperation): Result<Uri>

    /**
     * Redoes a previously undone rename operation.
     *
     * @param operation The operation to redo
     * @return Success with the renamed file URI, or Error with exception
     */
    suspend fun redoOperation(operation: RenameOperation): Result<Uri>

    /**
     * Gets a specific operation by its ID.
     *
     * @param operationId The ID of the operation to retrieve
     * @return Success with the operation if found, or Error if not found
     */
    suspend fun getOperationById(operationId: String): Result<RenameOperation>

    /**
     * Deletes a specific operation from the history.
     *
     * @param operationId The ID of the operation to delete
     * @return Success with Unit, or Error with exception
     */
    suspend fun deleteOperation(operationId: String): Result<Unit>

    /**
     * Gets the most recent operations up to a specified limit.
     *
     * @param limit Maximum number of operations to retrieve (default: 50)
     * @return Success with list of recent operations, or Error with exception
     */
    suspend fun getRecentOperations(limit: Int = 50): Result<List<RenameOperation>>
}
