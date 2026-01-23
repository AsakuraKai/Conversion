package com.example.conversion.domain.usecase.sync

import com.example.conversion.domain.model.SyncStatus
import com.example.conversion.domain.repository.SyncRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing sync status changes in real-time
 * 
 * Provides a reactive stream of sync status updates that can be observed
 * by the UI layer to display:
 * - Sync progress indicators
 * - Last sync time
 * - Error messages
 * 
 * The Flow will emit whenever:
 * - Sync operation starts
 * - Sync operation completes (success or failure)
 * - Sync status changes for any reason
 * 
 * Example usage:
 * ```
 * observeSyncStatusUseCase().collect { status ->
 *     when {
 *         status.isSyncing -> showSyncProgress()
 *         status.hasError -> showError(status.error)
 *         status.hasSynced -> showLastSyncTime(status.lastSyncTime)
 *     }
 * }
 * ```
 * 
 * @property syncRepository Repository for cloud sync operations
 */
class ObserveSyncStatusUseCase @Inject constructor(
    private val syncRepository: SyncRepository
) {
    /**
     * Returns a Flow that emits sync status updates
     * 
     * @return Flow of SyncStatus that updates in real-time
     */
    operator fun invoke(): Flow<SyncStatus> {
        return syncRepository.observeSyncStatus()
    }
}
