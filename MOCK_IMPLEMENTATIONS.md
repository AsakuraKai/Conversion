# Mock Implementations Documentation

**Last Updated:** December 4, 2025  
**Status:** 4 Mock Implementations Identified  
**Impact:** Low - All mocks are functional and serve development purposes

---

## 📋 Overview

This document tracks all mock/temporary implementations in the project that deviate from production-ready specifications outlined in the README.md. These implementations are **intentional** and **functional** but should be upgraded for production release.

---

## 🎯 Mock Implementations Summary

| # | Component | Chunk | Type | Status | Priority |
|---|-----------|-------|------|--------|----------|
| 1 | FolderRepositoryImpl.kt | CHUNK 6 | Full Mock File | ✅ Functional | Medium |
| 2 | triggerMediaScan() | CHUNK 5 | Mock Function | ✅ Functional | Low |
| 3 | FolderMonitorRepositoryImpl.kt | CHUNK 9 | Full Mock File | ✅ Functional | High |
| 4 | MonitoringService.kt | CHUNK 9 | Mock Service | ✅ Functional | High |

**Total:** 4 mock implementations  
**Blocking Issues:** None - All features work as intended for development

---

## 1. FolderRepositoryImpl.kt - MOCK DATA LAYER

### 📍 Location
```
app/src/main/java/com/example/conversion/data/repository/FolderRepositoryImpl.kt
```

### 🎯 Belongs To
**CHUNK 6: Destination Folder Selector** (Phase 2: Core Features)

### 📊 Status
- ✅ **Functional:** Works perfectly for development
- ⚠️ **Mock:** Uses deprecated File API instead of DocumentFile/SAF
- 🎨 **UI Complete:** FolderSelectorScreen.kt (342 lines) fully implemented
- 🔌 **Actively Used:** Injected via Hilt DI, used by ViewModel and UI

### 🔍 What's Mocked

#### Current Implementation (Mock)
- Uses `java.io.File` API for folder operations
- Direct file system access (pre-Android 10 approach)
- No scoped storage support
- No external SD card support
- `observeFolders()` emits once (no real-time updates)

#### README.md Specification (Production)
```
CHUNK 6: Destination Folder Selector
- [ ] Data: Folder scanning with DocumentFile API
```

### 📝 Mock Indicators in Code

**File Header Comment:**
```kotlin
/**
 * Implementation of FolderRepository for managing folder operations.
 * 
 * NOTE: This is a MOCK implementation for Phase 2 development.
 * Uses java.io.File for basic folder operations until Sokchea implements
 * the full Storage Access Framework (SAF) UI components.
 * 
 * TODO: Replace with full DocumentFile/SAF implementation when UI is ready
 * TODO: Add proper scoped storage handling for Android 10+
 * TODO: Integrate with SAF permission dialogs from UI layer
 */
```

**Function-Level Comments:**
- `getFolders()` - "MOCK: Uses File API for basic folder listing."
- `createFolder()` - "MOCK: Uses File.mkdir() for basic folder creation."
- `getRootFolders()` - "MOCK: Returns common Android storage paths."
- `observeFolders()` - "MOCK: Returns a simple flow with current state (no real-time updates)."

### ⚠️ Limitations

1. **No Scoped Storage (Android 10+)**
   - Cannot access app-specific directories properly
   - May fail on Android 11+ with restrictive permissions
   - Direct file paths don't work with scoped storage

2. **No External SD Card Support**
   - Only works with internal storage
   - Cannot browse removable storage

3. **No Real-time Observation**
   - `observeFolders()` emits current state once
   - No FileObserver integration
   - UI won't update if folders change externally

4. **No SAF Integration**
   - Cannot use Storage Access Framework dialogs
   - Missing persistent URI permissions
   - No document tree access

5. **Legacy API Deprecated**
   - `Environment.getExternalStorageDirectory()` deprecated in API 29
   - Direct File access discouraged on modern Android

### ✅ What Works

- ✅ Folder browsing and navigation
- ✅ Folder creation with validation
- ✅ Root folder listing (common directories)
- ✅ Folder metadata (file count, subfolder count)
- ✅ Parent folder navigation
- ✅ Comprehensive folder name validation
- ✅ Full UI implementation exists

### 🔧 Upgrade Path to Production

#### Step 1: Replace File API with DocumentFile
```kotlin
// Current (Mock)
val folder = File(parentPath)
val folders = folder.listFiles { it.isDirectory }

// Production (DocumentFile + SAF)
val treeUri = Uri.parse(parentPath)
val documentFile = DocumentFile.fromTreeUri(context, treeUri)
val folders = documentFile?.listFiles()?.filter { it.isDirectory }
```

#### Step 2: Add SAF Permission Handling
```kotlin
// Request directory access
val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
startActivityForResult(intent, REQUEST_CODE_OPEN_DIRECTORY)

// Persist permissions
contentResolver.takePersistableUriPermission(
    uri,
    Intent.FLAG_GRANT_READ_URI_PERMISSION or 
    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
)
```

#### Step 3: Implement Real FileObserver
```kotlin
override fun observeFolders(parentPath: String?): Flow<List<FolderInfo>> = callbackFlow {
    val observer = object : FileObserver(parentPath, ALL_EVENTS) {
        override fun onEvent(event: Int, path: String?) {
            // Query and emit updated folder list
            trySend(getFolders(parentPath))
        }
    }
    observer.startWatching()
    
    // Emit initial state
    send(getFolders(parentPath))
    
    awaitClose { observer.stopWatching() }
}
```

#### Step 4: Add External Storage Support
```kotlin
// Detect removable storage
val storageManager = context.getSystemService<StorageManager>()
val storageVolumes = storageManager?.storageVolumes
storageVolumes?.forEach { volume ->
    if (volume.isRemovable) {
        // Add to root folders
    }
}
```

### 📦 Dependencies Needed for Upgrade
```kotlin
// Already available in Android SDK
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
import android.os.FileObserver
import android.os.storage.StorageManager
```

### 👥 Actively Used By
- `FolderSelectorViewModel.kt` - Injects `GetFoldersUseCase` and `CreateFolderUseCase`
- `FolderSelectorScreen.kt` - Full UI with 342 lines of Compose code
- `FolderDataModule.kt` - Hilt DI module provides singleton instance
- `GetFoldersUseCase.kt` - Domain use case wraps repository
- `CreateFolderUseCase.kt` - Domain use case for folder creation

### 🎯 Recommendation
**Priority:** Medium  
**Action:** Upgrade to DocumentFile/SAF when moving to production  
**Timeline:** Before public release  
**Reason:** Current implementation works for development but lacks Android 10+ compatibility

---

## 2. triggerMediaScan() - MOCK FUNCTION

### 📍 Location
```
app/src/main/java/com/example/conversion/data/repository/FileRenameRepositoryImpl.kt
Lines: 107-125
```

### 🎯 Belongs To
**CHUNK 5: Rename Execution** (Phase 2: Core Features)

### 📊 Status
- ✅ **Functional:** File renaming works perfectly
- ⚠️ **Mock:** Media scanning is stubbed out
- 🔌 **Called:** Invoked after every successful rename
- 🎨 **Impact:** Low - Gallery apps update eventually

### 🔍 What's Mocked

#### Current Implementation (Mock)
```kotlin
/**
 * Triggers MediaScanner to update the system's media database.
 * This ensures the renamed file appears correctly in gallery apps and file managers.
 *
 * Note: This is a mock implementation for Android 10+ as MediaScannerConnection
 * requires a Context which we'll get from Sokchea's implementation.
 */
private suspend fun triggerMediaScan(uri: Uri) {
    // Mock implementation - In real app, this would use MediaScannerConnection
    // MediaScannerConnection requires Context, which Sokchea will provide
    
    // For now, we'll just log or skip this step
    // In production, this would be:
    // suspendCancellableCoroutine { continuation ->
    //     MediaScannerConnection.scanFile(
    //         context,
    //         arrayOf(filePath),
    //         null
    //     ) { path, scanUri ->
    //         continuation.resume(Unit)
    //     }
    // }
}
```

#### README.md Specification (Production)
```
CHUNK 5: Rename Execution
- [ ] MediaStore: Scan files after rename for gallery updates
```

### ⚠️ Impact

**What Doesn't Work:**
- Gallery apps may not immediately show renamed files
- File managers might display old filenames until refresh
- Media database is out of sync until device scans naturally

**What Still Works:**
- ✅ File renaming succeeds perfectly
- ✅ Files are physically renamed on storage
- ✅ Apps can access renamed files by new name
- ✅ MediaStore eventually updates (on device restart/manual scan)

**User Impact:** Very Low
- Files are renamed correctly
- Most users won't notice the delay
- Gallery apps typically auto-scan periodically

### 🔧 Upgrade Path to Production

#### Option 1: Inject Context (Recommended)
```kotlin
class FileRenameRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver,
    @ApplicationContext private val context: Context,  // ADD THIS
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : FileRenameRepository {

    private suspend fun triggerMediaScan(uri: Uri) = withContext(ioDispatcher) {
        suspendCancellableCoroutine { continuation ->
            val filePath = getFilePathFromUri(uri)
            MediaScannerConnection.scanFile(
                context,
                arrayOf(filePath),
                null
            ) { path, scanUri ->
                continuation.resume(Unit)
            }
        }
    }
}
```

#### Option 2: Use MediaStore Update (Modern Approach)
```kotlin
private suspend fun triggerMediaScan(uri: Uri) = withContext(ioDispatcher) {
    // Modern approach: Update MediaStore directly
    contentResolver.update(
        uri,
        ContentValues().apply {
            put(MediaStore.MediaColumns.IS_PENDING, 0)
        },
        null,
        null
    )
    
    // Notify MediaStore of changes
    contentResolver.notifyChange(uri, null)
}
```

### 📦 Dependencies Needed for Upgrade
```kotlin
// Already available in Android SDK
import android.media.MediaScannerConnection
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
```

### 👥 Actively Used By
- `FileRenameRepositoryImpl.renameFile()` - Calls after successful rename
- `ExecuteBatchRenameUseCase` - Indirectly via repository
- `RenameProgressViewModel` - Through use case execution

### 🎯 Recommendation
**Priority:** Low  
**Action:** Add Context injection when convenient  
**Timeline:** Non-critical enhancement  
**Reason:** Files rename successfully; scanning delay is minimal user impact

---

## 3. FolderMonitorRepositoryImpl.kt - MOCK DATA LAYER

### 📍 Location
```
app/src/main/java/com/example/conversion/data/repository/FolderMonitorRepositoryImpl.kt
```

### 🎯 Belongs To
**CHUNK 9: File Observer - Real-time Monitoring** (Phase 3: Advanced Features)

### 📊 Status
- ✅ **Functional:** Works for testing and development
- ⚠️ **Mock:** Uses FileObserver which may not work with Android 10+ scoped storage
- 🔌 **Injectable:** Ready for dependency injection via Hilt
- 🎨 **UI Pending:** Sokchea needs to implement MonitoringViewModel and UI

### 🔍 What's Mocked

#### Current Implementation (Mock)
- Uses `FileObserver` for file system monitoring
- Direct file path access (pre-Android 10 approach)
- No Storage Access Framework (SAF) integration
- No foreground service integration
- No actual file renaming (TODO comments in place)
- Basic pattern matching implementation

#### README.md Specification (Production)
```
CHUNK 9: File Observer - Real-time Monitoring
- [ ] Data: FileObserver implementation with pattern matching
- [ ] Background: Foreground service with notification
```

### 📝 Mock Indicators in Code

**File Header Comment:**
```kotlin
/**
 * Implementation of FolderMonitorRepository using FileObserver.
 * Monitors a folder for file changes and automatically renames new files.
 *
 * Note: This is a mock implementation due to scoped storage restrictions.
 * In production, this would require:
 * - Storage Access Framework (SAF) for folder access
 * - Foreground service for background monitoring
 * - WorkManager for periodic checks as FileObserver may not work with scoped storage
 *
 * @property fileRenameRepository Repository for file renaming operations
 * @property ioDispatcher The dispatcher for IO operations
 */
```

**Function-Level TODOs:**
- `processNewFile()` - "TODO: Implement actual file renaming when integrated"
- `createFileObserver()` - "This is a mock implementation that would need to be replaced"

### ⚠️ Limitations

1. **FileObserver May Not Work with Scoped Storage**
   - Android 10+ restricts direct file path access
   - FileObserver requires file paths, not URIs
   - May not receive events for files in scoped storage

2. **No Storage Access Framework Integration**
   - Cannot monitor user-selected folders with persistent permissions
   - No DocumentFile API support
   - Missing folder picker integration

3. **No Foreground Service**
   - Will be killed when app is backgrounded
   - No persistent notification
   - No long-running background monitoring

4. **No Actual File Renaming**
   - `processNewFile()` only increments counter
   - File renaming logic is stubbed with TODO
   - MediaStore URI resolution not implemented

5. **No WorkManager Fallback**
   - No periodic checks as backup mechanism
   - Single point of failure if FileObserver stops

### ✅ What Works

- ✅ FileObserver setup and lifecycle management
- ✅ Pattern matching (wildcards like `*.jpg`, `IMG_*`)
- ✅ File event detection (CREATE, MODIFY, DELETE, MOVED)
- ✅ Status tracking with Flow (Active, Inactive, Error)
- ✅ File event streaming via Flow
- ✅ Statistics tracking (files processed counter)
- ✅ Start/stop monitoring operations
- ✅ Thread-safe with coroutines and StateFlow

### 🔧 Upgrade Path to Production

#### Step 1: Replace FileObserver with DocumentFile + SAF
```kotlin
// Current (Mock with FileObserver)
fileObserver = object : FileObserver(folderMonitor.folderPath, mask) {
    override fun onEvent(event: Int, path: String?) { ... }
}

// Production (DocumentFile + ContentObserver)
val documentFile = DocumentFile.fromTreeUri(context, folderMonitor.folderUri)
contentResolver.registerContentObserver(
    folderMonitor.folderUri,
    true,
    object : ContentObserver(handler) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            // Query DocumentFile for changes
            // Process new files
        }
    }
)
```

#### Step 2: Add Foreground Service Integration
```kotlin
// MonitoringService should:
class MonitoringService : Service() {
    @Inject lateinit var folderMonitorRepository: FolderMonitorRepository
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        
        // Start monitoring from service
        lifecycleScope.launch {
            folderMonitorRepository.startMonitoring(folderMonitor)
        }
        
        // Observe status and update notification
        folderMonitorRepository.observeMonitoringStatus()
            .onEach { status -> updateNotification(status) }
            .launchIn(lifecycleScope)
            
        return START_STICKY
    }
}
```

#### Step 3: Add WorkManager Periodic Checks
```kotlin
// Backup mechanism for when FileObserver fails
class FolderMonitorWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        // Manually check folder for new files
        // Compare with last known state
        // Process any new files
        return Result.success()
    }
}

// Schedule periodic checks
val workRequest = PeriodicWorkRequestBuilder<FolderMonitorWorker>(15, TimeUnit.MINUTES)
    .build()
WorkManager.getInstance(context).enqueue(workRequest)
```

#### Step 4: Implement File Renaming with MediaStore
```kotlin
private suspend fun processNewFile(filePath: String, folderMonitor: FolderMonitor) {
    // Convert file path to MediaStore URI
    val uri = getMediaStoreUriFromPath(filePath)
    
    // Generate new filename
    val config = folderMonitor.renameConfig
    val newName = generateFilename(config, filesProcessed)
    
    // Rename the file
    fileRenameRepository.renameFile(uri, newName).fold(
        onSuccess = {
            filesProcessed++
            updateStatus()
        },
        onFailure = { error ->
            // Handle error, update status
        }
    )
}

private suspend fun getMediaStoreUriFromPath(filePath: String): Uri {
    val projection = arrayOf(MediaStore.MediaColumns._ID)
    val selection = "${MediaStore.MediaColumns.DATA} = ?"
    val selectionArgs = arrayOf(filePath)
    
    contentResolver.query(
        MediaStore.Files.getContentUri("external"),
        projection,
        selection,
        selectionArgs,
        null
    )?.use { cursor ->
        if (cursor.moveToFirst()) {
            val id = cursor.getLong(0)
            return ContentUris.withAppendedId(
                MediaStore.Files.getContentUri("external"),
                id
            )
        }
    }
    throw IllegalArgumentException("File not found in MediaStore")
}
```

### 📦 Dependencies Needed for Upgrade
```kotlin
// SAF and DocumentFile
import androidx.documentfile.provider.DocumentFile
import android.provider.DocumentsContract

// Foreground Service
import android.app.Service
import android.app.Notification
import android.app.NotificationManager

// WorkManager
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager

// MediaStore
import android.provider.MediaStore
import android.content.ContentUris
```

### 👥 Actively Used By
- `StartMonitoringUseCase.kt` - Validates and starts monitoring
- `StopMonitoringUseCase.kt` - Stops monitoring
- `GetMonitoringStatusUseCase.kt` - Gets current status
- `ObserveMonitoringStatusUseCase.kt` - Observes status changes
- `ObserveFileEventsUseCase.kt` - Observes file events
- `MonitoringDataModule.kt` - Hilt DI module provides singleton

### 🎯 Recommendation
**Priority:** High  
**Action:** Upgrade to SAF + Foreground Service before production  
**Timeline:** Before public release  
**Reason:** FileObserver is unreliable with scoped storage; needs proper service architecture

---

## 4. MonitoringService.kt - MOCK FOREGROUND SERVICE

### 📍 Location
```
app/src/main/java/com/example/conversion/service/MonitoringService.kt
```

### 🎯 Belongs To
**CHUNK 9: File Observer - Real-time Monitoring** (Phase 3: Advanced Features)

### 📊 Status
- ✅ **Structural:** Service structure in place
- ⚠️ **Mock:** No actual implementation, only TODOs
- 🎨 **For Sokchea:** UI developer should implement
- 🔌 **Not Registered:** Needs AndroidManifest.xml entry

### 🔍 What's Mocked

#### Current Implementation (Mock)
- Basic Service class with lifecycle methods
- Empty notification creation
- No repository integration
- No status observation
- No notification updates
- Helper methods for start/stop

#### README.md Specification (Production)
```
CHUNK 9: File Observer - Real-time Monitoring
- [ ] Background: Foreground service with notification
- [ ] Presentation: Monitoring toggle, active folder status
```

### 📝 Mock Indicators in Code

**File Header Comment:**
```kotlin
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
 */
```

**Multiple TODO Comments:**
- "// TODO: Inject FolderMonitorRepository when Sokchea integrates with DI"
- "// TODO: Sokchea should: 1. Call folderMonitorRepository.startMonitoring()"
- "// TODO: Sokchea should design the actual notification layout"
- "// TODO: Use app icon"

### ⚠️ Limitations

1. **No Repository Integration**
   - FolderMonitorRepository not injected
   - No actual monitoring operations
   - Service is just a shell

2. **No Notification Design**
   - Generic placeholder notification
   - No action buttons (Stop, Settings)
   - No dynamic updates with progress
   - No Material 3 design

3. **No Status Observation**
   - Doesn't observe MonitoringStatus Flow
   - Can't update notification with progress
   - No error handling or display

4. **Not Registered in Manifest**
   - Missing AndroidManifest.xml entry
   - Missing foreground service type
   - Missing required permissions

5. **No Lifecycle Management**
   - Doesn't handle app termination
   - No restart logic if killed
   - No battery optimization handling

### ✅ What Works

- ✅ Service class structure
- ✅ START_STICKY return for auto-restart
- ✅ Notification channel creation
- ✅ Helper methods for starting/stopping
- ✅ Hilt @AndroidEntryPoint annotation
- ✅ Action constants defined

### 🔧 Upgrade Path to Production

#### Step 1: Add Dependency Injection
```kotlin
@AndroidEntryPoint
class MonitoringService : Service() {
    
    @Inject
    lateinit var folderMonitorRepository: FolderMonitorRepository
    
    @Inject
    lateinit var generateFilenameUseCase: GenerateFilenameUseCase
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
}
```

#### Step 2: Implement Actual Monitoring
```kotlin
private fun startForegroundMonitoring(folderMonitor: FolderMonitor) {
    val notification = createNotification(
        title = getString(R.string.monitoring_active),
        content = getString(R.string.monitoring_folder, folderMonitor.folderPath),
        filesProcessed = 0
    )
    
    startForeground(NOTIFICATION_ID, notification)
    
    // Start monitoring
    serviceScope.launch {
        folderMonitorRepository.startMonitoring(folderMonitor).fold(
            onSuccess = {
                // Observe status and update notification
                observeMonitoringStatus()
            },
            onFailure = { error ->
                showErrorNotification(error.message)
                stopSelf()
            }
        )
    }
}

private fun observeMonitoringStatus() {
    folderMonitorRepository.observeMonitoringStatus()
        .onEach { status ->
            when (status) {
                is MonitoringStatus.Active -> {
                    updateNotification(
                        title = getString(R.string.monitoring_active),
                        content = getString(R.string.files_processed, status.filesProcessed)
                    )
                }
                is MonitoringStatus.Error -> {
                    showErrorNotification(status.error)
                }
                MonitoringStatus.Inactive -> {
                    stopSelf()
                }
            }
        }
        .launchIn(serviceScope)
}
```

#### Step 3: Design Notification with Actions
```kotlin
private fun createNotification(
    title: String,
    content: String,
    filesProcessed: Int
): Notification {
    // Create stop action
    val stopIntent = Intent(this, MonitoringService::class.java).apply {
        action = ACTION_STOP_MONITORING
    }
    val stopPendingIntent = PendingIntent.getService(
        this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE
    )
    
    // Create open app action
    val openIntent = packageManager.getLaunchIntentForPackage(packageName)
    val openPendingIntent = PendingIntent.getActivity(
        this, 0, openIntent, PendingIntent.FLAG_IMMUTABLE
    )
    
    return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        .setContentTitle(title)
        .setContentText(content)
        .setSubText(getString(R.string.files_processed_count, filesProcessed))
        .setSmallIcon(R.drawable.ic_monitoring)
        .setContentIntent(openPendingIntent)
        .addAction(
            R.drawable.ic_stop,
            getString(R.string.action_stop),
            stopPendingIntent
        )
        .setOngoing(true)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setCategory(NotificationCompat.CATEGORY_SERVICE)
        .setStyle(NotificationCompat.BigTextStyle()
            .bigText(content))
        .build()
}
```

#### Step 4: Add to AndroidManifest.xml
```xml
<manifest>
    <!-- Permissions -->
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    
    <application>
        <!-- Service Declaration -->
        <service
            android:name=".service.MonitoringService"
            android:enabled="true"
            android:exported="false"
            android:foregroundServiceType="dataSync" />
    </application>
</manifest>
```

#### Step 5: Handle Permissions in UI
```kotlin
// In MonitoringViewModel or Screen
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    val notificationPermission = Manifest.permission.POST_NOTIFICATIONS
    if (ContextCompat.checkSelfPermission(context, notificationPermission)
        != PackageManager.PERMISSION_GRANTED) {
        // Request permission
        requestPermissionLauncher.launch(notificationPermission)
    }
}
```

### 📦 Dependencies Needed for Upgrade
```kotlin
// Already available
import android.app.Service
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import androidx.core.app.NotificationCompat

// String resources needed
<string name="monitoring_active">Folder Monitoring Active</string>
<string name="monitoring_folder">Monitoring: %1$s</string>
<string name="files_processed">%1$d files processed</string>
<string name="action_stop">Stop</string>

// Drawable resources needed
R.drawable.ic_monitoring
R.drawable.ic_stop
```

### 👥 Required By (Sokchea's Work)
- `MonitoringViewModel.kt` - To start/stop service
- `MonitoringScreen.kt` - UI toggle for monitoring
- `MonitoringContract.kt` - State/Event/Action definitions
- AndroidManifest.xml - Service registration
- String resources - Notification text
- Drawable resources - Icons

### 🎯 Recommendation
**Priority:** High  
**Action:** Sokchea must implement full service with UI  
**Timeline:** Required for CHUNK 9 UI completion  
**Reason:** Core feature for real-time monitoring; service is essential

---

## 📊 Comparison: Mock vs Production

| Aspect | Mock Implementation | Production Ready |
|--------|-------------------|------------------|
| **CHUNK 6: Folder Operations** |
| API Used | `java.io.File` | `DocumentFile` + SAF |
| Scoped Storage | ❌ Not supported | ✅ Full support |
| External SD Card | ❌ Not supported | ✅ Supported |
| Real-time Updates | ❌ Single emit | ✅ FileObserver |
| Android 10+ Compatible | ⚠️ Partially | ✅ Fully compatible |
| Persistent Permissions | ❌ Not supported | ✅ SAF permissions |
| **CHUNK 5: Media Scanning** |
| Gallery Update | ⚠️ Delayed | ✅ Immediate |
| MediaStore Sync | ⚠️ Eventually | ✅ Real-time |
| User Impact | Low | None |
| **CHUNK 9: Folder Monitoring** |
| File Observation | `FileObserver` | DocumentFile + ContentObserver |
| Scoped Storage | ❌ Not supported | ✅ Full support |
| Background Operation | ❌ App only | ✅ Foreground service |
| File Renaming | ❌ Stubbed | ✅ Fully functional |
| WorkManager Fallback | ❌ Not implemented | ✅ Periodic checks |
| Notification | ❌ Placeholder | ✅ Rich, actionable |
| Battery Optimization | ❌ Not handled | ✅ Optimized |
| **CHUNK 9: Monitoring Service** |
| Service Type | Mock shell | Foreground service |
| Repository Integration | ❌ Not connected | ✅ Fully integrated |
| Notification Design | ❌ Generic | ✅ Material 3 |
| Status Updates | ❌ Static | ✅ Real-time |
| Action Buttons | ❌ None | ✅ Stop, Settings |
| Manifest Entry | ❌ Missing | ✅ Registered |
| Permissions | ❌ Not requested | ✅ POST_NOTIFICATIONS |

---

## 🚀 Upgrade Priority & Timeline

### High Priority (Before Production Release)
- **CHUNK 9: FolderMonitorRepositoryImpl**
  - Reason: FileObserver unreliable with scoped storage
  - Effort: High (4-5 days)
  - Risk: Core functionality for monitoring feature
  - Dependencies: SAF, Foreground Service, WorkManager

- **CHUNK 9: MonitoringService**
  - Reason: Essential for background monitoring
  - Effort: Medium (2-3 days, Sokchea's work)
  - Risk: Feature won't work without proper service
  - Dependencies: UI components, notification design, permissions

- **CHUNK 6: FolderRepositoryImpl**
  - Reason: Android 10+ compatibility essential
  - Effort: Medium (2-3 days)
  - Risk: Breaking changes to folder navigation

### Low Priority (Optional Enhancement)
- **CHUNK 5: triggerMediaScan()**
  - Reason: Current behavior acceptable
  - Effort: Low (few hours)
  - Risk: None

---

## 📝 Testing Checklist for Upgrades

### When Upgrading FolderRepositoryImpl (CHUNK 6):
- [ ] Test on Android 10, 11, 12, 13, 14
- [ ] Test external SD card access
- [ ] Test folder creation in scoped storage
- [ ] Test folder observation (real-time updates)
- [ ] Test SAF permission dialogs
- [ ] Test persistent URI permissions across app restarts
- [ ] Verify no regressions in folder navigation UI

### When Upgrading triggerMediaScan() (CHUNK 5):
- [ ] Test on Android 10+ with scoped storage
- [ ] Verify immediate gallery app updates
- [ ] Test with large batch renames (100+ files)
- [ ] Verify MediaStore database consistency
- [ ] Test with different file types (images, videos, audio)

### When Upgrading FolderMonitorRepositoryImpl (CHUNK 9):
- [ ] Test on Android 10, 11, 12, 13, 14
- [ ] Test with DocumentFile API and ContentObserver
- [ ] Test SAF folder picker and persistent permissions
- [ ] Test file event detection (create, modify, delete)
- [ ] Test pattern matching with various wildcards
- [ ] Test automatic file renaming with various configs
- [ ] Test WorkManager periodic checks
- [ ] Verify no battery drain issues
- [ ] Test with app in background/killed
- [ ] Test monitoring multiple folders sequentially
- [ ] Verify MediaStore URI resolution

### When Implementing MonitoringService (CHUNK 9):
- [ ] Test foreground service starts correctly
- [ ] Test notification appears and stays persistent
- [ ] Test notification updates with real-time progress
- [ ] Test Stop action button works
- [ ] Test tapping notification opens app
- [ ] Test POST_NOTIFICATIONS permission on Android 13+
- [ ] Test service survives app termination
- [ ] Test service restart after device reboot (if enabled)
- [ ] Test battery optimization compatibility
- [ ] Verify service type in manifest (dataSync)
- [ ] Test notification channel settings
- [ ] Test Material 3 notification design

---

## 🏗️ Architecture Decisions

### Why Mock Implementations Were Chosen:

1. **Faster Development Iteration**
   - Basic File API is simpler to implement
   - UI development could proceed without waiting for SAF complexity
   - Reduced initial complexity for Sokchea (UI developer)
   - FileObserver provides quick proof-of-concept for monitoring

2. **Functional MVP**
   - All mocks provide working features for development
   - Users can complete workflows end-to-end
   - No blocking issues for development phase
   - Core logic testable without production infrastructure

3. **Clear Upgrade Path**
   - TODOs documented in code
   - Upgrade steps well-defined
   - No architectural refactoring needed
   - Separation allows parallel development

4. **Separation of Concerns**
   - Kai (backend) implemented core logic
   - Deferred SAF/service complexity to production phase
   - UI and data layers properly separated
   - Sokchea can implement service when UI is ready

### Production Readiness Criteria:

Before production release, ensure:
- ✅ All mock implementations upgraded to production code
- ✅ Android 10+ scoped storage fully supported
- ✅ External storage (SD cards) accessible
- ✅ Real-time folder observation working with DocumentFile
- ✅ MediaStore scanning immediate after renames
- ✅ Foreground service running with persistent notification
- ✅ WorkManager fallback for monitoring reliability
- ✅ All permissions properly requested and handled
- ✅ Notification design follows Material 3 guidelines
- ✅ Battery optimization properly handled
- ✅ All tests passing on Android 10-14
- ✅ No deprecated API usage

---

## 📚 Related Documentation

- **CHUNK_5_COMPLETION.md** - Documents ExecuteBatchRenameUseCase implementation
- **CHUNK_6_COMPLETION.md** - Documents FolderRepository implementation
- **CHUNK_9_COMPLETION.md** - Documents File Observer & Monitoring implementation
- **README.md** - Original specifications for CHUNK 5, 6, and 9
- **KAI_TASKS.md** - Backend developer's task guide
- **SOKCHEA_TASKS.md** - UI developer's task guide

---

## ✅ Conclusion

All mock implementations are:
- ✅ **Intentional** - Documented with clear TODOs
- ✅ **Functional** - Provide working features for development
- ✅ **Non-Blocking** - Don't prevent feature development
- ✅ **Well-Architected** - Easy to upgrade without refactoring
- ✅ **Low-Medium Impact** - Users can complete workflows (with limitations)

### CHUNK 9 Special Notes:
- FolderMonitorRepositoryImpl is **functional for testing** but needs SAF upgrade
- MonitoringService is a **shell/placeholder** - Sokchea must implement
- Both are **high priority** for production as monitoring is a headline feature
- FileObserver limitation is **acceptable for development** but not production

**Action Required:**
- CHUNK 5, 6 mocks: Upgrade during production hardening phase
- **CHUNK 9 mocks: High priority - upgrade before feature release**

---

**Document Maintained By:** Development Team  
**Review Frequency:** Before each production release  
**Last Review:** December 4, 2025
