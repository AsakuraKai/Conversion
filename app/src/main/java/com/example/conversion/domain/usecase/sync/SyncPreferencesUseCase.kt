package com.example.conversion.domain.usecase.sync

import com.example.conversion.domain.repository.SyncRepository
import javax.inject.Inject

/**
 * Use case for synchronizing user preferences across devices
 * 
 * Performs a complete bidirectional sync:
 * 1. Downloads latest preferences from cloud storage
 * 2. Merges with local preferences using conflict resolution
 * 3. Uploads merged result back to cloud
 * 
 * Conflict resolution strategy: Last-write-wins
 * - Compares lastSyncTimestamp to determine which data is newer
 * - Newer data overwrites older data
 * 
 * Example usage:
 * ```
 * val result = syncPreferencesUseCase()
 * when (result) {
 *     is Result.Success -> println("Sync completed successfully")
 *     is Result.Error -> println("Sync failed: ${result.error}")
 * }
 * ```
 * 
 * @property syncRepository Repository for cloud sync operations
 */
class SyncPreferencesUseCase @Inject constructor(
    private val syncRepository: SyncRepository
) {
    /**
     * Executes the sync operation
     * 
     * @return Result.Success if sync completed, Result.Error if failed
     */
    suspend operator fun invoke(): Result<Unit> {
        return syncRepository.syncPreferences()
    }
}
