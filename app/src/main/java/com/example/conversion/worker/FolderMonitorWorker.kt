package com.example.conversion.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.conversion.domain.model.MonitoringStatus
import com.example.conversion.domain.repository.FolderMonitorRepository
import com.example.conversion.service.MonitoringService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * WorkManager fallback for folder monitoring.
 * 
 * Phase 2: Ensures monitoring continues even if MonitoringService is killed by OS.
 * 
 * Features:
 * - Periodic check of monitoring status (every 15 minutes)
 * - Automatically restarts MonitoringService if it was stopped unexpectedly
 * - Handles device reboot scenarios
 * - Works with ContentObserver-based monitoring
 * 
 * Usage:
 * Schedule this worker when monitoring is started:
 * ```
 * val workRequest = PeriodicWorkRequestBuilder<FolderMonitorWorker>(
 *     15, TimeUnit.MINUTES
 * ).build()
 * WorkManager.getInstance(context).enqueueUniquePeriodicWork(
 *     "folder_monitoring_fallback",
 *     ExistingPeriodicWorkPolicy.KEEP,
 *     workRequest
 * )
 * ```
 * 
 * Cancel when monitoring is stopped:
 * ```
 * WorkManager.getInstance(context).cancelUniqueWork("folder_monitoring_fallback")
 * ```
 * 
 * @param context Application context
 * @param params Worker parameters
 * @param folderMonitorRepository Repository for monitoring operations
 */
@HiltWorker
class FolderMonitorWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val folderMonitorRepository: FolderMonitorRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Check current monitoring status
            val status = folderMonitorRepository.getMonitoringStatus()
            val currentMonitor = folderMonitorRepository.getCurrentMonitor()
            
            when {
                // If monitoring should be active but isn't, restart service
                currentMonitor != null && currentMonitor.isActive && status is MonitoringStatus.Inactive -> {
                    MonitoringService.startMonitoring(
                        applicationContext,
                        currentMonitor.folderPath
                    )
                    Result.success()
                }
                
                // If monitoring is in error state, attempt restart
                status is MonitoringStatus.Error && currentMonitor != null -> {
                    MonitoringService.startMonitoring(
                        applicationContext,
                        currentMonitor.folderPath
                    )
                    Result.success()
                }
                
                // Otherwise, everything is fine
                else -> Result.success()
            }
        } catch (e: Exception) {
            // Retry on failure
            Result.retry()
        }
    }
}
