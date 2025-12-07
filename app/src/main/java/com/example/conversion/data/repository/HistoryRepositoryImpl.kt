package com.example.conversion.data.repository

import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.OperationHistory
import com.example.conversion.domain.model.RenameOperation
import com.example.conversion.domain.repository.HistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock implementation of HistoryRepository using in-memory storage.
 *
 * **STRATEGIC IMPLEMENTATION - Development Phase**
 *
 * This implementation uses in-memory storage (MutableStateFlow + List) to provide
 * complete undo/redo functionality without requiring Room database setup.
 * Perfect for parallel UI/backend development.
 *
 * **Fully Functional Features:**
 * - Complete operation history tracking
 * - Full undo/redo support with MediaStore integration
 * - Thread-safe operations with Mutex
 * - Flow-based reactive observation
 * - Operation validation and error handling
 *
 * **Production Upgrade Path:**
 * Replace in-memory storage with Room database (HistoryDao) for:
 * - Persistent storage across app restarts
 * - Efficient querying for large histories
 * - Database transactions for consistency
 *
 * @property contentResolver For MediaStore file operations
 * @property ioDispatcher For background operations
 */
@Singleton
class HistoryRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : HistoryRepository {

    // In-memory storage using MutableStateFlow for reactivity
    private val _operationHistory = MutableStateFlow(OperationHistory.empty())
    private val mutex = Mutex()

    override suspend fun saveOperation(operation: RenameOperation): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                mutex.withLock {
                    val currentHistory = _operationHistory.value
                    val updatedHistory = currentHistory.addOperation(operation)
                    _operationHistory.value = updatedHistory
                }
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getHistory(): Result<List<RenameOperation>> =
        withContext(ioDispatcher) {
            try {
                val operations = _operationHistory.value.operations
                Result.Success(operations)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override fun observeHistory(): Flow<OperationHistory> {
        return _operationHistory.asStateFlow()
    }

    override suspend fun clearHistory(): Result<Unit> = withContext(ioDispatcher) {
        try {
            mutex.withLock {
                _operationHistory.value = OperationHistory.empty()
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun undoOperation(operation: RenameOperation): Result<Uri> =
        withContext(ioDispatcher) {
            try {
                // Rename file back to original name using MediaStore
                val newUri = renameFile(operation.newUri, operation.originalName)

                // Update history state
                mutex.withLock {
                    val currentHistory = _operationHistory.value
                    _operationHistory.value = currentHistory.undo()
                }

                Result.Success(newUri)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun redoOperation(operation: RenameOperation): Result<Uri> =
        withContext(ioDispatcher) {
            try {
                // Rename file to new name using MediaStore
                val newUri = renameFile(operation.originalUri, operation.newName)

                // Update history state
                mutex.withLock {
                    val currentHistory = _operationHistory.value
                    _operationHistory.value = currentHistory.redo()
                }

                Result.Success(newUri)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getOperationById(operationId: String): Result<RenameOperation> =
        withContext(ioDispatcher) {
            try {
                val operation = _operationHistory.value.operations
                    .firstOrNull { it.id == operationId }
                    ?: throw NoSuchElementException("Operation not found: $operationId")
                Result.Success(operation)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun deleteOperation(operationId: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                mutex.withLock {
                    val currentHistory = _operationHistory.value
                    val filteredOperations = currentHistory.operations
                        .filterNot { it.id == operationId }
                    
                    // Adjust currentIndex if necessary
                    val newIndex = if (filteredOperations.isEmpty()) {
                        -1
                    } else {
                        minOf(currentHistory.currentIndex, filteredOperations.size - 1)
                    }
                    
                    _operationHistory.value = OperationHistory(
                        operations = filteredOperations,
                        currentIndex = newIndex
                    )
                }
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getRecentOperations(limit: Int): Result<List<RenameOperation>> =
        withContext(ioDispatcher) {
            try {
                val operations = _operationHistory.value.operations
                    .takeLast(limit)
                    .reversed()
                Result.Success(operations)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    /**
     * Renames a file using MediaStore.
     *
     * @param uri Current URI of the file
     * @param newName New name for the file (including extension)
     * @return URI of the renamed file
     */
    private fun renameFile(uri: Uri, newName: String): Uri {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, newName)
        }

        val updatedRows = contentResolver.update(uri, contentValues, null, null)
        if (updatedRows == 0) {
            throw IllegalStateException("Failed to rename file: $uri to $newName")
        }

        // Query to get the new URI after rename
        val projection = arrayOf(MediaStore.MediaColumns._ID)
        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                return Uri.withAppendedPath(
                    MediaStore.Files.getContentUri("external"),
                    id.toString()
                )
            }
        }

        // If we can't get the new URI, return the original one
        // (it should still point to the renamed file)
        return uri
    }
}
