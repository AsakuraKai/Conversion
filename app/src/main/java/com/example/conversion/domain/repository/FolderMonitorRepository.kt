package com.example.conversion.domain.repository

import com.example.conversion.domain.model.FileEvent
import com.example.conversion.domain.model.FolderMonitor
import com.example.conversion.domain.model.MonitoringStatus
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for folder monitoring operations.
 * Handles real-time monitoring of folders for file changes and automatic renaming.
 */
interface FolderMonitorRepository {
    
    /**
     * Starts monitoring a folder for file changes.
     * When new files are detected, they will be automatically renamed according to the configuration.
     *
     * @param folderMonitor The folder monitor configuration
     * @return Result indicating success or failure
     */
    suspend fun startMonitoring(folderMonitor: FolderMonitor): Result<Unit>
    
    /**
     * Stops monitoring the currently monitored folder.
     *
     * @return Result indicating success or failure
     */
    suspend fun stopMonitoring(): Result<Unit>
    
    /**
     * Gets the current monitoring status.
     *
     * @return Flow emitting the current monitoring status
     */
    fun observeMonitoringStatus(): Flow<MonitoringStatus>
    
    /**
     * Gets the current monitoring status as a single value.
     *
     * @return The current monitoring status
     */
    suspend fun getMonitoringStatus(): MonitoringStatus
    
    /**
     * Gets the currently monitored folder configuration.
     *
     * @return The current folder monitor, or null if no folder is being monitored
     */
    suspend fun getCurrentMonitor(): FolderMonitor?
    
    /**
     * Observes file events in the monitored folder.
     * Emits events when files are created, modified, deleted, or moved.
     *
     * @return Flow emitting file events
     */
    fun observeFileEvents(): Flow<FileEvent>
}
