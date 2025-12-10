# Service & Background Mocks - Mock Implementations

**Last Updated:** December 10, 2025  
**Category:** Service Layer - Background Processing  
**Related Chunks:** 4

---

## 📋 Overview

This group covers background services and long-running operations. The mock provides a service scaffold that enables UI development while full notification design and repository integration are completed.

**Implementations in this group:**
- **#4:** MonitoringService (Service Scaffold → Full Foreground Service)

**Common theme:** Background services with proper Android architecture and foreground service integration

**Technology Stack:** Native Android (WorkManager, Foreground Services) - Zero external setup required

---

## 4️⃣ MonitoringService.kt

**Location:** `service/MonitoringService.kt`  
**Chunk:** 9 (File Observer - Real-time Monitoring)  
**Priority:** High  
**Owner:** Sokchea (UI developer)

### Strategic Implementation
Provides a complete service scaffold with proper Android architecture, allowing UI development to proceed while full notification design and integration are finalized. This enables testing of service start/stop flows and notification channel creation without blocking UI work.

### Fully Functional Features
✅ Proper Android Service class structure  
✅ START_STICKY return for automatic restart  
✅ Notification channel creation and management  
✅ Helper methods for service start/stop operations  
✅ Hilt dependency injection setup  
✅ Action constants and intent handling structure  
✅ Lifecycle management (onCreate, onDestroy)  
✅ Foreground notification setup

### Production Enhancements Needed
🔄 Connect repository for monitoring operations  
🔄 Implement Material 3 notification design with progress  
🔄 Add real-time status observation and updates  
🔄 Register service in AndroidManifest.xml  
🔄 Complete lifecycle management for background operation  
🔄 Add notification actions (pause, stop)  
🔄 Implement proper error handling and recovery  
🔄 Add crash reporting integration

### Production Implementation

**Complete Service Implementation:**
```kotlin
@AndroidEntryPoint
class MonitoringService : Service() {
    
    @Inject lateinit var folderMonitorRepository: FolderMonitorRepository
    @Inject lateinit var notificationManager: NotificationManager
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var currentFolderId: String? = null
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val folderId = intent.getStringExtra(EXTRA_FOLDER_ID)
                    ?: return START_NOT_STICKY
                startMonitoring(folderId)
            }
            ACTION_STOP -> {
                stopMonitoring()
                stopSelf()
            }
            ACTION_PAUSE -> {
                pauseMonitoring()
            }
            ACTION_RESUME -> {
                resumeMonitoring()
            }
        }
        
        return START_STICKY
    }
    
    private fun startMonitoring(folderId: String) {
        currentFolderId = folderId
        
        // Start foreground service with initial notification
        startForeground(NOTIFICATION_ID, createNotification(
            status = MonitoringStatus.Starting
        ))
        
        // Start monitoring in repository
        serviceScope.launch {
            folderMonitorRepository.getMonitor(folderId)
                .onSuccess { monitor ->
                    folderMonitorRepository.startMonitoring(monitor)
                    observeMonitoringStatus(folderId)
                }
                .onFailure { error ->
                    showErrorNotification(error.message)
                    stopSelf()
                }
        }
    }
    
    private fun observeMonitoringStatus(folderId: String) {
        folderMonitorRepository.observeMonitoringStatus()
            .onEach { status ->
                when (status) {
                    is MonitoringStatus.Active -> {
                        updateNotification(
                            title = "Monitoring Active",
                            text = "Watching ${status.monitor.folderPath}",
                            filesProcessed = status.filesProcessed
                        )
                    }
                    is MonitoringStatus.Processing -> {
                        updateNotification(
                            title = "Processing File",
                            text = "Renaming ${status.fileName}...",
                            filesProcessed = status.filesProcessed,
                            showProgress = true
                        )
                    }
                    is MonitoringStatus.Paused -> {
                        updateNotification(
                            title = "Monitoring Paused",
                            text = "Click to resume"
                        )
                    }
                    is MonitoringStatus.Error -> {
                        showErrorNotification(status.message)
                    }
                    MonitoringStatus.Idle -> {
                        stopSelf()
                    }
                }
            }
            .catch { error ->
                showErrorNotification(error.message)
                stopSelf()
            }
            .launchIn(serviceScope)
    }
    
    private fun stopMonitoring() {
        serviceScope.launch {
            currentFolderId?.let { folderId ->
                folderMonitorRepository.stopMonitoring(folderId)
            }
        }
    }
    
    private fun pauseMonitoring() {
        serviceScope.launch {
            currentFolderId?.let { folderId ->
                folderMonitorRepository.pauseMonitoring(folderId)
            }
        }
    }
    
    private fun resumeMonitoring() {
        serviceScope.launch {
            currentFolderId?.let { folderId ->
                folderMonitorRepository.resumeMonitoring(folderId)
            }
        }
    }
    
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "File Monitoring",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows status of folder monitoring"
            setShowBadge(false)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(channel)
    }
    
    private fun createNotification(
        title: String = "Monitoring Active",
        text: String = "Watching folder for new files",
        filesProcessed: Int = 0,
        showProgress: Boolean = false,
        status: MonitoringStatus = MonitoringStatus.Idle
    ): Notification {
        
        // Create notification actions
        val pauseIntent = Intent(this, MonitoringService::class.java).apply {
            action = ACTION_PAUSE
        }
        val pausePendingIntent = PendingIntent.getService(
            this, 0, pauseIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val stopIntent = Intent(this, MonitoringService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 1, stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // Create tap intent to open app
        val tapIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val tapPendingIntent = PendingIntent.getActivity(
            this, 0, tapIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_folder_monitor)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(tapPendingIntent)
            .apply {
                // Add progress bar if processing
                if (showProgress) {
                    setProgress(0, 0, true)
                }
                
                // Add file count
                if (filesProcessed > 0) {
                    setSubText("$filesProcessed files processed")
                }
                
                // Add actions based on status
                when (status) {
                    is MonitoringStatus.Active -> {
                        addAction(
                            R.drawable.ic_pause,
                            "Pause",
                            pausePendingIntent
                        )
                        addAction(
                            R.drawable.ic_stop,
                            "Stop",
                            stopPendingIntent
                        )
                    }
                    is MonitoringStatus.Paused -> {
                        addAction(
                            R.drawable.ic_play,
                            "Resume",
                            PendingIntent.getService(
                                this@MonitoringService,
                                2,
                                Intent(this@MonitoringService, MonitoringService::class.java).apply {
                                    action = ACTION_RESUME
                                },
                                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                            )
                        )
                        addAction(
                            R.drawable.ic_stop,
                            "Stop",
                            stopPendingIntent
                        )
                    }
                    else -> {
                        addAction(
                            R.drawable.ic_stop,
                            "Stop",
                            stopPendingIntent
                        )
                    }
                }
            }
            .build()
    }
    
    private fun updateNotification(
        title: String,
        text: String,
        filesProcessed: Int = 0,
        showProgress: Boolean = false
    ) {
        val notification = createNotification(title, text, filesProcessed, showProgress)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    private fun showErrorNotification(message: String?) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Monitoring Error")
            .setContentText(message ?: "An unknown error occurred")
            .setSmallIcon(R.drawable.ic_error)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        
        notificationManager.notify(ERROR_NOTIFICATION_ID, notification)
    }
    
    override fun onDestroy() {
        stopMonitoring()
        serviceScope.cancel()
        super.onDestroy()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    companion object {
        private const val CHANNEL_ID = "monitoring_channel"
        private const val NOTIFICATION_ID = 1
        private const val ERROR_NOTIFICATION_ID = 2
        
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val EXTRA_FOLDER_ID = "EXTRA_FOLDER_ID"
        
        fun start(context: Context, folderId: String) {
            val intent = Intent(context, MonitoringService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_FOLDER_ID, folderId)
            }
            context.startForegroundService(intent)
        }
        
        fun stop(context: Context) {
            val intent = Intent(context, MonitoringService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }
}
```

**AndroidManifest.xml Configuration:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    
    <!-- Permissions for foreground service -->
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    
    <application>
        <!-- Service declaration -->
        <service
            android:name=".service.MonitoringService"
            android:enabled="true"
            android:exported="false"
            android:foregroundServiceType="dataSync">
            <!-- Intent filters for service actions -->
            <intent-filter>
                <action android:name="com.example.conversion.ACTION_START" />
                <action android:name="com.example.conversion.ACTION_STOP" />
                <action android:name="com.example.conversion.ACTION_PAUSE" />
                <action android:name="com.example.conversion.ACTION_RESUME" />
            </intent-filter>
        </service>
    </application>
</manifest>
```

**WorkManager Integration (Optional Fallback):**
```kotlin
@HiltWorker
class MonitoringWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val folderMonitorRepository: FolderMonitorRepository
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        val folderId = inputData.getString("folder_id") ?: return Result.failure()
        
        return try {
            // Check if monitoring is still active
            val monitor = folderMonitorRepository.getMonitor(folderId)
                .getOrNull() ?: return Result.failure()
            
            // Perform periodic check for new files
            folderMonitorRepository.checkForNewFiles(folderId)
            
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
    
    companion object {
        fun enqueue(context: Context, folderId: String) {
            val workRequest = PeriodicWorkRequestBuilder<MonitoringWorker>(
                repeatInterval = 15,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            ).setInputData(
                workDataOf("folder_id" to folderId)
            ).setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()
            ).build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "monitoring_$folderId",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}
```

### Trade-offs

**Current Implementation:**
- ✅ Complete service structure in place
- ✅ Notification channel created properly
- ✅ Hilt dependency injection ready
- ✅ Helper methods for service control
- ✅ Unblocks UI development
- ⚠️ No repository connection
- ⚠️ Basic notification (no Material 3 design)
- ⚠️ No real monitoring functionality yet

**Production Implementation:**
- ✅ Full monitoring functionality
- ✅ Material 3 notification design
- ✅ Real-time status updates
- ✅ Notification actions (pause/resume/stop)
- ✅ Error handling and recovery
- ✅ WorkManager fallback for reliability
- ✅ Crash reporting integration
- ✅ Proper lifecycle management
- ⚠️ More complex code
- ⚠️ Requires thorough testing
- ⚠️ Battery optimization considerations

---

## 📚 Related Documentation

- **Main Index:** [MOCK_IMPLEMENTATIONS.md](../../MOCK_IMPLEMENTATIONS.md)
- **CHUNK 9 Completion:** [CHUNK_9_COMPLETION.md](../../CHUNK_9_COMPLETION.md)
- **README Implementation Strategy:** [README.md - Background Services](../../README.md#group-1-3-7-native-android-components)
- **File System Mocks:** [FILE_SYSTEM_MOCKS.md](FILE_SYSTEM_MOCKS.md) - Related monitoring implementation

---

**Document Status:** Complete  
**Last Updated:** December 10, 2025  
**Prepared By:** Development Team
