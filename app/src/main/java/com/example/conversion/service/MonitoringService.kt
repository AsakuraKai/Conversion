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
import com.example.conversion.MainActivity
import com.example.conversion.R
import com.example.conversion.domain.model.MonitoringStatus
import com.example.conversion.domain.repository.FolderMonitorRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * Foreground service for folder monitoring.
 * Runs in the background to monitor folders for file changes and apply automatic renaming.
 * 
 * Features:
 * - Runs as foreground service with persistent notification
 * - Integrates with FolderMonitorRepository
 * - Provides real-time status updates through notification
 * - Handles service lifecycle properly
 * 
 * Requirements:
 * - POST_NOTIFICATIONS permission on Android 13+
 * - FOREGROUND_SERVICE permission
 * - FOREGROUND_SERVICE_DATA_SYNC permission
 * - Service declaration in AndroidManifest.xml
 */
@AndroidEntryPoint
class MonitoringService : Service() {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "folder_monitoring_channel"
        private const val NOTIFICATION_CHANNEL_NAME = "Folder Monitoring"
        private const val NOTIFICATION_ID = 1001
        
        const val ACTION_START_MONITORING = "com.example.conversion.ACTION_START_MONITORING"
        const val ACTION_STOP_MONITORING = "com.example.conversion.ACTION_STOP_MONITORING"
        
        const val EXTRA_FOLDER_PATH = "extra_folder_path"
        
        /**
         * Starts the monitoring service.
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
         * Stops the monitoring service.
         */
        fun stopMonitoring(context: Context) {
            val intent = Intent(context, MonitoringService::class.java).apply {
                action = ACTION_STOP_MONITORING
            }
            context.startService(intent)
        }
    }

    @Inject
    lateinit var folderMonitorRepository: FolderMonitorRepository
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var currentFolderPath: String? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        observeMonitoringStatus()
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
        
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
    /**
     * Observes monitoring status and updates notification.
     */
    private fun observeMonitoringStatus() {
        folderMonitorRepository.observeMonitoringStatus()
            .onEach { status ->
                when (status) {
                    is MonitoringStatus.Active -> {
                        updateNotification(
                            title = "Monitoring Active",
                            content = status.folderPath,
                            filesProcessed = status.filesProcessed
                        )
                    }
                    is MonitoringStatus.Inactive -> {
                        stopForegroundMonitoring()
                    }
                    is MonitoringStatus.Error -> {
                        updateNotification(
                            title = "Monitoring Error",
                            content = status.error,
                            filesProcessed = 0
                        )
                    }
                }
            }
            .launchIn(serviceScope)
    }

    /**
     * Starts foreground monitoring with notification.
     */
    private fun startForegroundMonitoring(folderPath: String) {
        currentFolderPath = folderPath
        
        val notification = createNotification(
            title = "Starting Monitoring...",
    /**
     * Creates notification channel (required for Android O+).
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitors folders for automatic file renaming"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }   stopSelf()
    }
    /**
     * Creates a notification for the foreground service.
     */
    private fun createNotification(
        title: String,
        content: String,
        filesProcessed: Int
    ): Notification {
        // Create intent to open app
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // Create stop action
        val stopIntent = Intent(this, MonitoringService::class.java).apply {
            action = ACTION_STOP_MONITORING
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText("$content • $filesProcessed files processed")
            .setSmallIcon(android.R.drawable.ic_menu_myplaces)
            .setContentIntent(openAppPendingIntent)
            .addAction(
                android.R.drawable.ic_media_pause,
                "Stop",
                stopPendingIntent
            )
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }
}    * - Number of files processed
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
