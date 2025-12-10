package com.example.conversion.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.conversion.domain.repository.SyncRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Background worker for periodic cloud sync operations
 *
 * Performs bidirectional sync of user preferences with Firestore.
 * Scheduled to run periodically (e.g., every 15 minutes) when:
 * - Device has network connectivity
 * - User is authenticated
 *
 * Uses WorkManager for guaranteed execution even when app is closed.
 *
 * @property syncRepository Repository for cloud sync operations
 */
@HiltWorker
class CloudSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncRepository: SyncRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Perform sync
            val syncResult = syncRepository.syncPreferences()

            if (syncResult.isSuccess) {
                Result.success()
            } else {
                // Retry on failure (WorkManager handles exponential backoff)
                Result.retry()
            }
        } catch (e: Exception) {
            // Retry on exceptions
            Result.retry()
        }
    }
}
