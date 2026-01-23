package com.example.conversion.domain.repository

import com.example.conversion.domain.model.SyncStatus
import com.example.conversion.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for multi-device synchronization
 * 
 * Handles syncing user preferences (templates, tags, settings) across multiple devices
 * via cloud storage (Firebase Firestore in production).
 * 
 * Features:
 * - Bidirectional sync (upload and download)
 * - Conflict resolution (last-write-wins strategy)
 * - Real-time sync status observation
 * - Error handling and reporting
 */
interface SyncRepository {
    
    /**
     * Synchronizes all user preferences with the cloud
     * 
     * Performs a bidirectional sync:
     * 1. Downloads latest preferences from cloud
     * 2. Merges with local preferences (last-write-wins)
     * 3. Uploads merged result to cloud
     * 
     * @return Result.Success if sync completed, Result.Error if failed
     */
    suspend fun syncPreferences(): Result<Unit>
    
    /**
     * Uploads user preferences to the cloud
     * 
     * Forcefully uploads local preferences, overwriting cloud data.
     * Use this when you want to push local changes without merging.
     * 
     * @param preferences The user preferences to upload
     * @return Result.Success with unit if upload succeeded, Result.Error if failed
     */
    suspend fun uploadPreferences(preferences: UserPreferences): Result<Unit>
    
    /**
     * Downloads user preferences from the cloud
     * 
     * Fetches the latest preferences from cloud storage without modifying local data.
     * Use this to preview what would be synced before applying changes.
     * 
     * @return Result.Success with UserPreferences if download succeeded, Result.Error if failed
     */
    suspend fun downloadPreferences(): Result<UserPreferences>
    
    /**
     * Observes the current sync status in real-time
     * 
     * Emits status updates whenever:
     * - Sync starts (isSyncing = true)
     * - Sync completes successfully (lastSyncTime updated)
     * - Sync fails (error message set)
     * 
     * @return Flow of SyncStatus updates
     */
    fun observeSyncStatus(): Flow<SyncStatus>
    
    /**
     * Gets the current sync status (one-time read)
     * 
     * @return Current SyncStatus snapshot
     */
    suspend fun getSyncStatus(): Result<SyncStatus>
}
