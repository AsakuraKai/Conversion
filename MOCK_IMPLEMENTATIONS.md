# Mock Implementations Documentation

**Last Updated:** December 3, 2025  
**Status:** 2 Mock Implementations Identified  
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

**Total:** 2 mock implementations  
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

---

## 🚀 Upgrade Priority & Timeline

### High Priority (Before Production Release)
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

### When Upgrading FolderRepositoryImpl:
- [ ] Test on Android 10, 11, 12, 13, 14
- [ ] Test external SD card access
- [ ] Test folder creation in scoped storage
- [ ] Test folder observation (real-time updates)
- [ ] Test SAF permission dialogs
- [ ] Test persistent URI permissions across app restarts
- [ ] Verify no regressions in folder navigation UI

### When Upgrading triggerMediaScan():
- [ ] Test on Android 10+ with scoped storage
- [ ] Verify immediate gallery app updates
- [ ] Test with large batch renames (100+ files)
- [ ] Verify MediaStore database consistency
- [ ] Test with different file types (images, videos, audio)

---

## 🏗️ Architecture Decisions

### Why Mock Implementations Were Chosen:

1. **Faster Development Iteration**
   - Basic File API is simpler to implement
   - UI development could proceed without waiting for SAF complexity
   - Reduced initial complexity for Sokchea (UI developer)

2. **Functional MVP**
   - Both mocks provide working features
   - Users can complete workflows end-to-end
   - No blocking issues for development phase

3. **Clear Upgrade Path**
   - TODOs documented in code
   - Upgrade steps well-defined
   - No architectural refactoring needed

4. **Separation of Concerns**
   - Kai (backend) implemented core logic
   - Deferred SAF complexity to production phase
   - UI and data layers properly separated

### Production Readiness Criteria:

Before production release, ensure:
- ✅ All mock implementations upgraded to production code
- ✅ Android 10+ scoped storage fully supported
- ✅ External storage (SD cards) accessible
- ✅ Real-time folder observation working
- ✅ MediaStore scanning immediate after renames
- ✅ All tests passing on Android 10-14
- ✅ No deprecated API usage

---

## 📚 Related Documentation

- **CHUNK_5_COMPLETION.md** - Documents ExecuteBatchRenameUseCase implementation
- **CHUNK_6_COMPLETION.md** - Documents FolderRepository implementation
- **README.md** - Original specifications for CHUNK 5 and CHUNK 6
- **KAI_TASKS.md** - Backend developer's task guide
- **SOKCHEA_TASKS.md** - UI developer's task guide

---

## ✅ Conclusion

Both mock implementations are:
- ✅ **Intentional** - Documented with clear TODOs
- ✅ **Functional** - Provide working features for development
- ✅ **Non-Blocking** - Don't prevent feature development
- ✅ **Well-Architected** - Easy to upgrade without refactoring
- ✅ **Low Impact** - Users can complete all workflows

**No immediate action required.** Upgrade during production hardening phase before public release.

---

**Document Maintained By:** Development Team  
**Review Frequency:** Before each production release  
**Last Review:** December 3, 2025
