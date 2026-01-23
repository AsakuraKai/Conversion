package com.example.conversion.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.conversion.data.local.entity.OperationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Operation entities (undo/redo history).
 * Provides methods to interact with the operations table.
 */
@Dao
interface OperationDao {
    /**
     * Observe all operations ordered by stack position.
     */
    @Query("SELECT * FROM rename_operations ORDER BY stackPosition ASC")
    fun observeAll(): Flow<List<OperationEntity>>
    
    /**
     * Get all operations ordered by stack position.
     */
    @Query("SELECT * FROM rename_operations ORDER BY stackPosition ASC")
    suspend fun getAll(): List<OperationEntity>
    
    /**
     * Get a specific operation by ID.
     */
    @Query("SELECT * FROM rename_operations WHERE id = :operationId")
    suspend fun getById(operationId: String): OperationEntity?
    
    /**
     * Get operations up to a specific stack position.
     */
    @Query("SELECT * FROM rename_operations WHERE stackPosition <= :position ORDER BY stackPosition ASC")
    suspend fun getUpToPosition(position: Int): List<OperationEntity>
    
    /**
     * Insert a new operation. Replace if conflict occurs.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(operation: OperationEntity)
    
    /**
     * Insert multiple operations.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(operations: List<OperationEntity>)
    
    /**
     * Delete operations after a specific stack position.
     * Used when performing a new operation after undo.
     */
    @Query("DELETE FROM rename_operations WHERE stackPosition > :position")
    suspend fun deleteAfterPosition(position: Int)
    
    /**
     * Delete all operations.
     */
    @Query("DELETE FROM rename_operations")
    suspend fun deleteAll()
    
    /**
     * Get the count of operations.
     */
    @Query("SELECT COUNT(*) FROM rename_operations")
    suspend fun count(): Int
    
    /**
     * Get the maximum stack position.
     */
    @Query("SELECT MAX(stackPosition) FROM rename_operations")
    suspend fun getMaxPosition(): Int?
    
    /**
     * Delete operation by ID.
     */
    @Query("DELETE FROM rename_operations WHERE id = :operationId")
    suspend fun deleteById(operationId: String)
}
