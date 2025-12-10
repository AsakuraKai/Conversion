# File System Mocks - Mock Implementations

**Last Updated:** December 10, 2025  
**Category:** Data Layer - File System  
**Related Chunks:** 1, 2, 3

---

## 📋 Overview

This group covers file system access, monitoring, and Android storage compliance implementations. These mocks enable development without blocking on complex Storage Access Framework (SAF) integration while maintaining production-quality architecture.

**Implementations in this group:**
- **#1:** FolderRepositoryImpl (File API → DocumentFile + SAF)
- **#2:** triggerMediaScan (Deferred → MediaScannerConnection)
- **#3:** FolderMonitorRepositoryImpl (FileObserver → ContentObserver)

**Common theme:** File system operations with upgrade path to Android 10+ scoped storage compliance

**Technology Stack:** Native Android (Room, WorkManager, SAF) - Zero external setup required

---

## 1️⃣ FolderRepositoryImpl.kt

**Location:** `data/repository/FolderRepositoryImpl.kt`  
**Chunk:** 6 (Destination Folder Selector)  
**Priority:** Medium

### Strategic Implementation
Uses `java.io.File` API as a development-friendly approach that unblocks UI implementation. This provides a fully functional folder system without requiring complex Storage Access Framework setup.

### Fully Functional Features
✅ Complete folder browsing and navigation  
✅ Folder creation with comprehensive validation  
✅ Root folder listing for all common directories  
✅ Folder metadata extraction (file count, subfolder count)  
✅ Clean architecture with proper repository pattern  
✅ Complete error handling and edge cases

### Production Enhancements Needed
🔄 Upgrade to scoped storage for Android 10+ full compliance  
🔄 Add external SD card support via SAF  
🔄 Implement real-time folder observation  
🔄 Integrate FileObserver for dynamic updates

### Production Upgrade

```kotlin
// Replace File API with DocumentFile + SAF
class FolderRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver,
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : FolderRepository {
    
    override suspend fun getFolders(parentPath: String): Result<List<FolderInfo>> = 
        withContext(ioDispatcher) {
            try {
                // Use DocumentFile for scoped storage
                val parentUri = Uri.parse(parentPath)
                val documentFile = DocumentFile.fromTreeUri(context, parentUri)
                    ?: return@withContext Result.Error(Exception("Invalid folder path"))
                
                val folders = documentFile.listFiles()
                    ?.filter { it.isDirectory }
                    ?.map { folder ->
                        FolderInfo(
                            path = folder.uri.toString(),
                            name = folder.name ?: "Unknown",
                            fileCount = folder.listFiles()?.count { !it.isDirectory } ?: 0,
                            subfolderCount = folder.listFiles()?.count { it.isDirectory } ?: 0,
                            lastModified = folder.lastModified()
                        )
                    } ?: emptyList()
                
                Result.Success(folders)
            } catch (e: Exception) {
                Result.Error(Exception("Failed to list folders", e))
            }
        }
    
    // Request directory access from user
    fun requestFolderAccess(activity: ComponentActivity) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        }
        activity.startActivityForResult(intent, REQUEST_CODE_OPEN_DIRECTORY)
    }
    
    // Handle result and persist permissions
    fun handleFolderAccessResult(data: Intent?) {
        val uri = data?.data ?: return
        
        // Persist permissions for app restart survival
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
    }
    
    companion object {
        private const val REQUEST_CODE_OPEN_DIRECTORY = 1001
    }
}
```

### Trade-offs

**Current Implementation:**
- ✅ Simple File API - easy to understand and debug
- ✅ Fast development - no permission complexity
- ✅ Works on all Android versions
- ✅ Clean code - straightforward logic
- ⚠️ Not scoped storage compliant (Android 10+)
- ⚠️ Limited to app-accessible directories
- ⚠️ No external SD card support

**Production Implementation:**
- ✅ Scoped storage compliant (Android 10+)
- ✅ User-selected folder support via SAF
- ✅ External SD card access
- ✅ Proper permission management
- ✅ System-enforced security
- ⚠️ More complex code
- ⚠️ Requires user permission flow
- ⚠️ URI-based instead of path-based

---

## 2️⃣ triggerMediaScan()

**Location:** `data/repository/FileRenameRepositoryImpl.kt` (Lines 107-125)  
**Chunk:** 5 (Rename Execution)  
**Priority:** Low

### Strategic Implementation
Media scanning is intentionally deferred to avoid Context injection complexity in the data layer. The file renaming functionality is complete and robust; media database updates occur through Android's natural scanning mechanisms.

### Fully Functional Features
✅ Complete file renaming with MediaStore integration  
✅ Files physically renamed with proper URI handling  
✅ MediaStore database updates (with system-managed timing)  
✅ Comprehensive error handling for all rename scenarios  
✅ Batch processing with progress tracking

### Trade-off
Gallery apps update within seconds to minutes (system-dependent) rather than immediately. This is an acceptable trade-off for cleaner architecture during development.

### Production Upgrade

**Option 1: Inject Context (Recommended)**
```kotlin
class FileRenameRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver,
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : FileRenameRepository {
    
    override suspend fun renameFile(fileUri: Uri, newName: String): Result<Uri> = 
        withContext(ioDispatcher) {
            try {
                // Perform rename...
                val newUri = performRename(fileUri, newName)
                
                // Trigger immediate media scan
                triggerMediaScan(newUri)
                
                Result.Success(newUri)
            } catch (e: Exception) {
                Result.Error(Exception("Rename failed", e))
            }
        }
    
    private suspend fun triggerMediaScan(uri: Uri) = withContext(ioDispatcher) {
        suspendCancellableCoroutine<Unit> { continuation ->
            val path = getFilePathFromUri(uri)
            MediaScannerConnection.scanFile(
                context,
                arrayOf(path),
                null
            ) { _, _ ->
                continuation.resume(Unit)
            }
        }
    }
    
    private fun getFilePathFromUri(uri: Uri): String {
        contentResolver.query(uri, arrayOf(MediaStore.MediaColumns.DATA), null, null, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                    return cursor.getString(columnIndex)
                }
            }
        throw IllegalArgumentException("Cannot resolve file path from URI")
    }
}
```

**Option 2: Modern MediaStore Update**
```kotlin
private suspend fun triggerMediaScan(uri: Uri) = withContext(ioDispatcher) {
    try {
        // Update MediaStore to mark file as ready
        contentResolver.update(uri, ContentValues().apply {
            put(MediaStore.MediaColumns.IS_PENDING, 0)
        }, null, null)
        
        // Notify system of change
        contentResolver.notifyChange(uri, null)
    } catch (e: Exception) {
        // Media scan failed but file was renamed successfully
        Log.w(TAG, "Media scan failed", e)
    }
}
```

### Trade-offs

**Current Implementation:**
- ✅ Clean architecture - no Context in data layer
- ✅ Simpler dependency injection
- ✅ Files rename successfully
- ✅ System eventually updates gallery
- ⚠️ Gallery refresh delayed (seconds to minutes)
- ⚠️ Manual refresh may be needed by user

**Production Implementation:**
- ✅ Immediate gallery updates
- ✅ Better user experience
- ✅ No manual refresh needed
- ✅ Professional behavior
- ⚠️ Context dependency in data layer
- ⚠️ More complex error handling
- ⚠️ Requires path resolution logic

---

## 3️⃣ FolderMonitorRepositoryImpl.kt

**Location:** `data/repository/FolderMonitorRepositoryImpl.kt`  
**Chunk:** 9 (File Observer)  
**Priority:** High

### Strategic Implementation
Implements a functional monitoring system using `FileObserver` to validate the monitoring architecture and flow patterns. This provides a working proof-of-concept while more complex SAF integration is prepared.

### Fully Functional Features
✅ Complete FileObserver setup and lifecycle management  
✅ Advanced pattern matching with wildcards (*.jpg, IMG_*)  
✅ Comprehensive file event detection (CREATE, MODIFY, DELETE, MOVED)  
✅ Real-time status tracking with StateFlow  
✅ File event streaming via Flow  
✅ Thread-safe implementation with coroutines  
✅ Statistics tracking and monitoring state management  
✅ Clean architecture with proper repository pattern

### Production Enhancements Needed
🔄 Upgrade to ContentObserver for reliable scoped storage monitoring  
🔄 Add SAF integration for user-selected folder permissions  
🔄 Connect to foreground service for background operation  
🔄 Complete file renaming integration with MediaStore  
🔄 Add WorkManager fallback for monitoring reliability

### Production Upgrade

```kotlin
class FolderMonitorRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver,
    @ApplicationContext private val context: Context,
    private val fileRenameRepository: FileRenameRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : FolderMonitorRepository {
    
    private val _monitoringStatus = MutableStateFlow<MonitoringStatus>(MonitoringStatus.Idle)
    override val monitoringStatus: StateFlow<MonitoringStatus> = _monitoringStatus.asStateFlow()
    
    private val contentObservers = mutableMapOf<String, ContentObserver>()
    private val handler = Handler(Looper.getMainLooper())
    
    override suspend fun startMonitoring(folderMonitor: FolderMonitor): Result<Unit> = 
        withContext(ioDispatcher) {
            try {
                // Use DocumentFile for user-selected folders
                val folderUri = Uri.parse(folderMonitor.folderPath)
                val documentFile = DocumentFile.fromTreeUri(context, folderUri)
                    ?: return@withContext Result.Error(Exception("Invalid folder URI"))
                
                // Register ContentObserver for scoped storage
                val observer = object : ContentObserver(handler) {
                    override fun onChange(selfChange: Boolean, uri: Uri?) {
                        uri?.let { processFileChange(it, folderMonitor) }
                    }
                }
                
                contentResolver.registerContentObserver(
                    folderUri,
                    true, // Watch subdirectories
                    observer
                )
                
                contentObservers[folderMonitor.id] = observer
                _monitoringStatus.value = MonitoringStatus.Active(folderMonitor)
                
                // Schedule WorkManager fallback
                schedulePeriodicCheck(folderMonitor)
                
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(Exception("Failed to start monitoring", e))
            }
        }
    
    private fun processFileChange(uri: Uri, folderMonitor: FolderMonitor) {
        CoroutineScope(ioDispatcher).launch {
            try {
                // Query MediaStore for file details
                val fileInfo = queryFileInfo(uri) ?: return@launch
                
                // Check if file matches pattern
                if (!matchesPattern(fileInfo.name, folderMonitor.filePattern)) return@launch
                
                // Generate new filename and rename
                val config = folderMonitor.renameConfig
                val newName = generateFilename(config, getProcessedCount())
                
                fileRenameRepository.renameFile(uri, newName).fold(
                    onSuccess = { incrementProcessedCount() },
                    onFailure = { error -> 
                        Log.e(TAG, "Rename failed", error)
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "File processing error", e)
            }
        }
    }
    
    private fun schedulePeriodicCheck(folderMonitor: FolderMonitor) {
        val workRequest = PeriodicWorkRequestBuilder<FolderMonitorWorker>(
            15, TimeUnit.MINUTES
        ).setInputData(
            workDataOf("folder_id" to folderMonitor.id)
        ).build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "monitor_${folderMonitor.id}",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
    
    override suspend fun stopMonitoring(folderId: String): Result<Unit> = 
        withContext(ioDispatcher) {
            try {
                // Unregister ContentObserver
                contentObservers[folderId]?.let { observer ->
                    contentResolver.unregisterContentObserver(observer)
                    contentObservers.remove(folderId)
                }
                
                // Cancel WorkManager task
                WorkManager.getInstance(context).cancelUniqueWork("monitor_$folderId")
                
                _monitoringStatus.value = MonitoringStatus.Idle
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(Exception("Failed to stop monitoring", e))
            }
        }
}
```

**WorkManager Implementation:**
```kotlin
@HiltWorker
class FolderMonitorWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val folderMonitorRepository: FolderMonitorRepository
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        val folderId = inputData.getString("folder_id") ?: return Result.failure()
        
        return try {
            // Check for new files and process them
            folderMonitorRepository.checkForNewFiles(folderId)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
```

### Trade-offs

**Current Implementation:**
- ✅ Simple FileObserver - straightforward implementation
- ✅ Real-time file detection works
- ✅ Pattern matching functional
- ✅ Clean architecture validated
- ✅ No permission complexity
- ⚠️ Limited to app-accessible directories
- ⚠️ Not scoped storage compliant
- ⚠️ No background reliability

**Production Implementation:**
- ✅ Scoped storage compliant (Android 10+)
- ✅ User-selected folder support
- ✅ Reliable background operation
- ✅ WorkManager fallback for reliability
- ✅ Proper foreground service integration
- ⚠️ More complex implementation
- ⚠️ Requires permission flow
- ⚠️ Additional dependencies (WorkManager)

---

## 📚 Related Documentation

- **Main Index:** [MOCK_IMPLEMENTATIONS.md](../../MOCK_IMPLEMENTATIONS.md)
- **CHUNK 6 Completion:** [CHUNK_6_COMPLETION.md](../../CHUNK_6_COMPLETION.md)
- **CHUNK 5 Completion:** [CHUNK_5_COMPLETION.md](../../CHUNK_5_COMPLETION.md)
- **CHUNK 9 Completion:** [CHUNK_9_COMPLETION.md](../../CHUNK_9_COMPLETION.md)
- **README Implementation Strategy:** [README.md - Group 1-3,7](../../README.md#group-1-3-7-native-android-components)

---

**Document Status:** Complete  
**Last Updated:** December 10, 2025  
**Prepared By:** Development Team
