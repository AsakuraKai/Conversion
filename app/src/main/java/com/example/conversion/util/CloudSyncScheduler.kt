package com.example.conversion.util

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.conversion.worker.CloudSyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Scheduler for background cloud sync operations
 *
 * Manages periodic sync worker scheduling using WorkManager.
 * Ensures sync runs when:
 * - Device has network connectivity
 * - Optionally on WiFi only
 * - With configurable interval
 *
 * WorkManager guarantees execution even when app is closed or device restarts.
 */
@Singleton
class CloudSyncScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val SYNC_WORK_NAME = "cloud_sync_work"
        private const val DEFAULT_INTERVAL_MINUTES = 60L
    }

    /**
     * Schedule periodic cloud sync
     *
     * @param intervalMinutes Sync interval in minutes (minimum 15)
     * @param wifiOnly True to sync only on WiFi, false for any network
     */
    fun schedulePeriodicSync(
        intervalMinutes: Long = DEFAULT_INTERVAL_MINUTES,
        wifiOnly: Boolean = true
    ) {
        // Ensure minimum interval (WorkManager requirement)
        val interval = maxOf(intervalMinutes, 15L)

        // Build constraints
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(
                if (wifiOnly) NetworkType.UNMETERED else NetworkType.CONNECTED
            )
            .build()

        // Build periodic work request
        val syncRequest = PeriodicWorkRequestBuilder<CloudSyncWorker>(
            interval, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        // Schedule work (replace existing if already scheduled)
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            syncRequest
        )
    }

    /**
     * Cancel periodic sync
     */
    fun cancelPeriodicSync() {
        WorkManager.getInstance(context).cancelUniqueWork(SYNC_WORK_NAME)
    }

    /**
     * Check if sync is currently scheduled
     */
    fun isSyncScheduled(): Boolean {
        val workInfos = WorkManager.getInstance(context)
            .getWorkInfosForUniqueWork(SYNC_WORK_NAME)
            .get() // Blocking call, use with caution

        return workInfos.any { !it.state.isFinished }
    }
}
