package com.example.conversion.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.conversion.data.local.entity.OperationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for rename operation history.
 *
 * Provides methods to perform CRUD operations on the rename_operations table.
 */
@Dao
interface                                                                                                                                            HistoryDao {

    /**
     * Observes all operations in chronological order.
     *
     * @return Flow emitting the list of all operations whenever it changes
     */
    @Query("SELECT * FROM rename_operations ORDER BY timestamp ASC")
    fun observeAll(): Flow<List<OperationEntity>>

    /**
     * Gets all operations in chronological order.
     *
     * @return List of all operations
     */
    @Query("SELECT * FROM rename_operations ORDER BY timestamp ASC")
    suspend fun getAll(): List<OperationEntity>

    /**
     * Gets a specific operation by ID.
     *
     * @param id The operation ID
     * @return The operation if found, null otherwise
     */
    @Query("SELECT * FROM rename_operations WHERE id = :id")
    suspend fun getById(id: String): OperationEntity?

    /**
     * Gets the most recent operations up to a limit.
     *
     * @param limit Maximum number of operations to retrieve
     * @return List of recent operations in reverse chronological order
     */
    @Query("SELECT * FROM rename_operations ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<OperationEntity>

    /**
     * Inserts a new operation or replaces if it already exists.
     *
     * @param operation The operation to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(operation: OperationEntity)

    /**
     * Inserts multiple operations.
     *
     * @param operations The operations to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(operations: List<OperationEntity>)

    /**
     * Deletes a specific operation by ID.
     *
     * @param id The ID of the operation to delete
     */
    @Query("DELETE FROM rename_operations WHERE id = :id")
    suspend fun deleteById(id: String)

    /**
     * Deletes all operations from the history.
     */
    @Query("DELETE FROM rename_operations")
    suspend fun deleteAll()

    /**
     * Gets the total count of operations in history.
     *
     * @return Total number of operations
     */
    @Query("SELECT COUNT(*) FROM rename_operations")
    suspend fun getCount(): Int

    /**
     * Gets operations within a time range.
     *
     * @param startTime Start timestamp (inclusive)
     * @param endTime End timestamp (inclusive)
     * @return List of operations within the time range
     */
    @Query("SELECT * FROM rename_operations WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp ASC")
    suspend fun getByTimeRange(startTime: Long, endTime: Long): List<OperationEntity>
}
