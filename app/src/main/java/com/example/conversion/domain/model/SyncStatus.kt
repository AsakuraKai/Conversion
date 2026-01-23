package com.example.conversion.domain.model

/**
 * Domain model representing the current sync status
 * 
 * Tracks the state of multi-device synchronization, including:
 * - Whether a sync operation is currently running
 * - The last successful sync timestamp
 * - Any error that occurred during sync
 * 
 * @property isSyncing True if a sync operation is currently in progress
 * @property lastSyncTime Timestamp of the last successful sync (milliseconds since epoch), null if never synced
 * @property error Error message if the last sync failed, null if successful or no sync attempted
 */
data class SyncStatus(
    val isSyncing: Boolean = false,
    val lastSyncTime: Long? = null,
    val error: String? = null
) {
    /**
     * Returns true if the last sync operation failed
     */
    val hasError: Boolean
        get() = error != null
    
    /**
     * Returns true if sync has ever been performed successfully
     */
    val hasSynced: Boolean
        get() = lastSyncTime != null
    
    companion object {
        /**
         * Initial state with no sync activity
         */
        val IDLE = SyncStatus(
            isSyncing = false,
            lastSyncTime = null,
            error = null
        )
        
        /**
         * State indicating sync is in progress
         */
        val SYNCING = SyncStatus(
            isSyncing = true,
            lastSyncTime = null,
            error = null
        )
    }
}
