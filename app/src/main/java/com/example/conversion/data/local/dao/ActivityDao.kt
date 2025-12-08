package com.example.conversion.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.conversion.data.local.entity.ActivityEntity

/**
 * Data Access Object for activity logs.
 *
 * Provides methods to perform CRUD operations on the activity_logs table.
 */
@Dao
interface ActivityDao {

    /**
     * Inserts a new activity log entry.
     *
     * @param activity The activity log to insert
     * @return The row ID of the inserted log
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activity: ActivityEntity): Long

    /**
     * Gets all activity logs in reverse chronological order.
     *
     * @return List of all activity logs
     */
    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC")
    suspend fun getAll(): List<ActivityEntity>

    /**
     * Gets activity logs within a specific time range.
     *
     * @param startTime Start of the time range (epoch milliseconds)
     * @param endTime End of the time range (epoch milliseconds)
     * @return List of activity logs within the time range
     */
    @Query("SELECT * FROM activity_logs WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    suspend fun getByTimeRange(startTime: Long, endTime: Long): List<ActivityEntity>

    /**
     * Gets activity logs by status.
     *
     * @param status The status to filter by
     * @return List of activity logs with the specified status
     */
    @Query("SELECT * FROM activity_logs WHERE status = :status ORDER BY timestamp DESC")
    suspend fun getByStatus(status: String): List<ActivityEntity>

    /**
     * Gets activity logs by action.
     *
     * @param action The action to filter by
     * @return List of activity logs with the specified action
     */
    @Query("SELECT * FROM activity_logs WHERE action = :action ORDER BY timestamp DESC")
    suspend fun getByAction(action: String): List<ActivityEntity>

    /**
     * Gets activity logs with multiple filters.
     *
     * @param startTime Start of the time range (nullable, epoch milliseconds)
     * @param endTime End of the time range (nullable, epoch milliseconds)
     * @param status The status to filter by (nullable)
     * @param action The action to filter by (nullable)
     * @param limit Maximum number of logs to return
     * @return List of filtered activity logs
     */
    @Query("""
        SELECT * FROM activity_logs 
        WHERE (:startTime IS NULL OR timestamp >= :startTime)
        AND (:endTime IS NULL OR timestamp <= :endTime)
        AND (:status IS NULL OR status = :status)
        AND (:action IS NULL OR action = :action)
        ORDER BY timestamp DESC 
        LIMIT :limit
    """)
    suspend fun getFiltered(
        startTime: Long?,
        endTime: Long?,
        status: String?,
        action: String?,
        limit: Int
    ): List<ActivityEntity>

    /**
     * Deletes all activity logs.
     */
    @Query("DELETE FROM activity_logs")
    suspend fun deleteAll()

    /**
     * Deletes activity logs older than a specific timestamp.
     *
     * @param timestamp Threshold timestamp (epoch milliseconds)
     * @return Number of deleted rows
     */
    @Query("DELETE FROM activity_logs WHERE timestamp < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long): Int
}
