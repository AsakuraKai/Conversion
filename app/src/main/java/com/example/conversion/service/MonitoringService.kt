package com.example.conversion.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.conversion.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Foreground service for folder monitoring.
 * Runs in the background to monitor folders for file changes and apply automatic renaming.
 * 
 * IMPORTANT: This is a MOCK/PLACEHOLDER implementation.
 * The actual implementation should be done by Sokchea (UI/Presentation specialist).
 * 
 * Production requirements:
 * - Must run as foreground service with persistent notification
 * - Requires POST_NOTIFICATIONS permission on Android 13+
 * - Should handle service lifecycle properly (start, stop, restart)
 * - Should integrate with FolderMonitorRepository
 * - Should provide status updates through notification
 * - Should handle app termination gracefully
 * - Should respect battery optimization settings
 * 
 * Integration points:
 * - AndroidManifest.xml: Add service declaration
 * - Permission handling: Request POST_NOTIFICATIONS
 * - UI: Add controls to start/stop monitoring
 * - Notification: Design notification layout and actions
 */
@AndroidEntryPoint
class MonitoringService : Service() {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "folder_monitoring_channel"
        private const val NOTIFICATION_ID = 1001
        
        const val ACTION_START_MONITORING = "com.example.conversion.ACTION_START_MONITORING"
        const val ACTION_STOP_MONITORING = "com.example.conversion.ACTION_STOP_MONITORING"
        
        const val EXTRA_FOLDER_PATH = "extra_folder_path"
        const val EXTRA_FOLDER_URI = "extra_folder_uri"
        const val EXTRA_RENAME_CONFIG = "extra_rename_config"
        
        /**
         * Helper method to start the monitoring service.
         * Sokchea should call this from the UI when user enables monitoring.
         */
        fun startMonitoring(context: Context, folderPath: String) {
            val intent = Intent(context, MonitoringService::class.java).apply {
                action = ACTION_START_MONITORING
                putExtra(EXTRA_FOLDER_PATH, folderPath)
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        
        /**
         * Helper method to stop the monitoring service.
         * Sokchea should call this from the UI when user disables monitoring.
         */
        fun stopMonitoring(context: Context) {
            val intent = Intent(context, MonitoringService::class.java).apply {
                action = ACTION_STOP_MONITORING
            }
            context.startService(intent)
        }
    }

    // TODO: Inject FolderMonitorRepository when Sokchea integrates with DI
    // @Inject lateinit var folderMonitorRepository: FolderMonitorRepository

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_MONITORING -> {
                val folderPath = intent.getStringExtra(EXTRA_FOLDER_PATH) ?: ""
                startForegroundMonitoring(folderPath)
            }
            ACTION_STOP_MONITORING -> {
                stopForegroundMonitoring()
            }
        }
        
        return START_STICKY // Service will be restarted if killed
    }

    override fun onBind(intent: Intent?): IBinder? {
        // This service doesn't support binding
        return null
    }

    /**
     * Starts foreground monitoring with notification.
     * MOCK implementation - Sokchea should implement the actual UI/notification.
     */
    private fun startForegroundMonitoring(folderPath: String) {
        val notification = createNotification(
            title = "Monitoring Active",
            content = "Monitoring folder: $folderPath",
            filesProcessed = 0
        )
        
        startForeground(NOTIFICATION_ID, notification)
        
        // TODO: Sokchea should:
        // 1. Call folderMonitorRepository.startMonitoring()
        // 2. Observe monitoring status
        // 3. Update notification with progress
        // 4. Handle errors and show in notification
    }

    /**
     * Stops foreground monitoring and removes notification.
     * MOCK implementation - Sokchea should implement the actual logic.
     */
    private fun stopForegroundMonitoring() {
        // TODO: Sokchea should:
        // 1. Call folderMonitorRepository.stopMonitoring()
        // 2. Remove notification
        // 3. Stop service
        
        stopForeground(true)
        stopSelf()
    }

    /**
     * Creates notification channel (required for Android O+).
     * Sokchea should customize the channel name and description.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Folder Monitoring", // TODO: Sokchea - use string resource
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitors folders for automatic file renaming" // TODO: use string resource
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    /**
     * Creates a notification for the foreground service.
     * MOCK implementation - Sokchea should design the actual notification layout.
     * 
     * Should include:
     * - Current folder being monitored
     * - Number of files processed
     * - Stop button action
     * - Tap to open app
     */
    private fun createNotification(
        title: String,
        content: String,
        filesProcessed: Int
    ): Notification {
        // TODO: Sokchea should:
        // 1. Create proper notification layout with Material 3 design
        // 2. Add action buttons (Stop, Settings)
        // 3. Add pending intent to open the app
        // 4. Use proper string resources
        // 5. Add notification icon
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            packageManager.getLaunchIntentForPackage(packageName),
            PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText("$content • $filesProcessed files processed")
            .setSmallIcon(android.R.drawable.ic_menu_info_details) // TODO: Use app icon
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}

/**
 * MANIFEST ENTRY REQUIRED (Sokchea should add this):
 * 
 * <service
 *     android:name=".service.MonitoringService"
 *     android:enabled="true"
 *     android:exported="false"
 *     android:foregroundServiceType="dataSync" />
 * 
 * PERMISSIONS REQUIRED:
 * 
 * <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
 * <uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC" />
 * <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
 */
