package com.example.conversion.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.conversion.data.local.entity.ActivityLogEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for ActivityLog entities.
 * Provides methods to interact with the activity_logs table.
 */
@Dao
interface ActivityLogDao {
    /**
     * Observe all activity logs ordered by timestamp (newest first).
     */
    @Query("SELECT * FROM activity_logs ORDER BY timestampMillis DESC")
    fun observeAll(): Flow<List<ActivityLogEntity>>
    
    /**
     * Get all activity logs (one-time query).
     */
    @Query("SELECT * FROM activity_logs ORDER BY timestampMillis DESC")
    suspend fun getAll(): List<ActivityLogEntity>
    
    /**
     * Get a specific activity log by ID.
     */
    @Query("SELECT * FROM activity_logs WHERE id = :logId")
    suspend fun getById(logId: Long): ActivityLogEntity?
    
    /**
     * Insert a new activity log.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: ActivityLogEntity): Long
    
    /**
     * Insert multiple activity logs.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<ActivityLogEntity>)
    
    /**
     * Delete all activity logs.
     */
    @Query("DELETE FROM activity_logs")
    suspend fun deleteAll()
    
    /**
     * Get activity logs by status.
     */
    @Query("SELECT * FROM activity_logs WHERE status = :status ORDER BY timestampMillis DESC")
    suspend fun getByStatus(status: String): List<ActivityLogEntity>
    
    /**
     * Get activity logs by action type.
     */
    @Query("SELECT * FROM activity_logs WHERE action = :action ORDER BY timestampMillis DESC")
    suspend fun getByAction(action: String): List<ActivityLogEntity>
    
    /**
     * Get activity logs within a time range.
     */
    @Query("""
        SELECT * FROM activity_logs 
        WHERE timestampMillis BETWEEN :startMillis AND :endMillis 
        ORDER BY timestampMillis DESC
    """)
    suspend fun getInTimeRange(startMillis: Long, endMillis: Long): List<ActivityLogEntity>
    
    /**
     * Get recent activity logs (limit by count).
     */
    @Query("SELECT * FROM activity_logs ORDER BY timestampMillis DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<ActivityLogEntity>
    
    /**
     * Observe recent activity logs.
     */
    @Query("SELECT * FROM activity_logs ORDER BY timestampMillis DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<ActivityLogEntity>>
    
    /**
     * Delete logs older than a specific timestamp.
     */
    @Query("DELETE FROM activity_logs WHERE timestampMillis < :timestampMillis")
    suspend fun deleteOlderThan(timestampMillis: Long)
    
    /**
     * Count total number of logs.
     */
    @Query("SELECT COUNT(*) FROM activity_logs")
    suspend fun count(): Int
    
    /**
     * Count logs by status.
     */
    @Query("SELECT COUNT(*) FROM activity_logs WHERE status = :status")
    suspend fun countByStatus(status: String): Int
}
