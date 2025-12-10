# Cloud & Synchronization - Mock Implementations

**Last Updated:** December 10, 2025  
**Category:** Data Layer - Cloud Integration  
**Tool Decision:** ✅ Firebase (Firestore + Auth + Storage) - Configured and operational  
**Related Chunks:** CHUNKS 9, 12

---

## 📋 Overview

This group covers cloud storage and multi-device synchronization features using mock implementations with simulated network behavior. Both implementations will upgrade to **Firebase** for production, providing a unified cloud backend with real-time sync, offline persistence, and automatic scaling.

**Implementations in this group:**
- **CHUNK 9:** CloudSyncRepositoryImpl - Cloud file backup and storage integration
- **CHUNK 12:** SyncRepositoryImpl - Multi-device settings and template synchronization

**Common theme:** Cloud storage and multi-device synchronization using Firebase as unified backend

**Why Firebase (Unified Strategy):**
- **The "Cheat Code":** One SDK solves multiple problems:
  - **Firestore:** Real-time NoSQL database for settings/templates sync
  - **Cloud Storage:** File backup and sharing
  - **Authentication:** Email, Google, social login
  - **Crashlytics:** Automatic crash reporting
  - **Remote Config:** A/B testing, feature flags
- **Saves months:** No backend development needed
- **Scales automatically:** Serverless infrastructure
- **Generous free tier:** Spark plan sufficient for MVP and beyond
- **Real-time sync:** Built-in, no polling required
- **Offline-first:** Automatic local caching and sync
- **✅ Already configured:** google-services.json in place, ready to use

**Firebase Dependencies (Production):**
```kotlin
// build.gradle.kts (project)
plugins {
    id("com.google.gms.google-services") version "4.4.0" apply false
}

// build.gradle.kts (app)
plugins {
    id("com.google.gms.google-services")
}

dependencies {
    // Firebase BoM for version management
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    
    // Core Firebase services
    implementation("com.google.firebase:firebase-firestore-ktx")      // CHUNK 12
    implementation("com.google.firebase:firebase-auth-ktx")           // Both
    implementation("com.google.firebase:firebase-storage-ktx")        // CHUNK 9
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-config-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
}
```

**Setup Status:** ✅ **Complete** - Firebase project configured, google-services.json in place  
**External Work Required:** None - ready for implementation  
**Total Setup Investment:** ~15 minutes (one-time, already done)

**Free Tier Limits:**
- Firestore: 1GB storage, 50K reads/day, 20K writes/day
- Cloud Storage: 5GB storage, 1GB/day download
- Authentication: Unlimited users
**Verdict:** Sufficient for MVP and beyond

---

## 9️⃣ CloudSyncRepositoryImpl.kt

**Location:** `data/repository/CloudSyncRepositoryImpl.kt`  
**Chunk:** 17 (Cloud Storage Integration)  
**Priority:** Medium  
**Production Tool:** ✅ Firebase Storage - Configured and operational

### Strategic Implementation
Uses simulated cloud API calls with realistic OAuth flow and upload behavior to unblock UI development. Provides complete cloud sync functionality without requiring Firebase setup during development phase.

**Mock Strategy:**
- Simulates OAuth authentication with configurable success rate (90%)
- Mock file uploads with progress tracking (2s delay, 95% success)
- In-memory configuration storage
- Thread-safe operations with Mutex
- Realistic network failure simulation
- Multi-provider UI support (Drive, Dropbox, OneDrive)

**Why Firebase Storage for Production:**
- Unified with Firestore and Auth (one SDK, one setup)
- ✅ Already configured - google-services.json in place
- Automatic retry logic and resumable uploads built-in
- Excellent offline persistence and sync
- Generous free tier (5GB storage, 1GB/day download)

### Fully Functional Features
✅ OAuth authentication simulation (90% success rate)  
✅ Multi-provider support (simulated for UI testing)  
✅ File upload with progress tracking  
✅ Batch sync with Flow-based progress updates  
✅ Configuration persistence (in-memory)  
✅ Real-time status observation via StateFlow  
✅ File download simulation  
✅ Thread-safe operations with Mutex  
✅ Simulated network failures for testing  
✅ Clean architecture with proper repository pattern  
✅ Comprehensive test coverage (60+ tests)

### Production Enhancements Needed
🔄 Integrate Firebase Storage for file uploads/downloads  
🔄 Implement Firebase Auth for user management  
🔄 Add WorkManager for background sync  
🔄 Implement persistent token storage (Firebase Auth handles this)  
🔄 Add network connectivity checks (Firebase handles offline automatically)  
🔄 Implement upload retry logic (Firebase built-in)  
🔄 Handle file conflicts with versioning  
🔄 Support resumable uploads (Firebase built-in)  
🔄 Add storage quota monitoring (Firebase Firestore rules)

### Production Upgrade

#### Firebase Storage Integration
```kotlin
// 1. Dependencies already configured ✅
// google-services.json in place
// Firebase BoM and dependencies in build.gradle.kts

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
}

// 2. Implement Firebase Storage Repository
@Singleton
class CloudSyncRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage,
    private val contentResolver: ContentResolver,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CloudSyncRepository {
    
    // User's cloud folder reference
    private val userStorageRef: StorageReference
        get() = firebaseStorage.reference
            .child("users")
            .child(firebaseAuth.currentUser?.uid ?: throw IllegalStateException("Not authenticated"))
            .child("renamed_files")
    
    override suspend fun authenticate(): Result<Unit> = withContext(ioDispatcher) {
        try {
            // Check if user is already signed in
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                return@withContext Result.Success(Unit)
            }
            
            // Anonymous authentication for MVP (optional email/Google later)
            val authResult = suspendCancellableCoroutine<AuthResult> { continuation ->
                firebaseAuth.signInAnonymously()
                    .addOnSuccessListener { result ->
                        continuation.resume(result)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun uploadFile(
        localUri: Uri,
        remotePath: String,
        onProgress: (Float) -> Unit = {}
    ): Result<String> = withContext(ioDispatcher) {
        try {
            val fileRef = userStorageRef.child(remotePath)
            
            // Open input stream
            val inputStream = contentResolver.openInputStream(localUri)
                ?: return@withContext Result.Error("Cannot read file")
            
            // Upload with progress tracking
            val uploadTask = fileRef.putStream(inputStream)
            
            // Monitor progress
            uploadTask.addOnProgressListener { snapshot ->
                val progress = (snapshot.bytesTransferred.toFloat() / snapshot.totalByteCount)
                onProgress(progress)
            }
            
            // Wait for completion
            val taskSnapshot = suspendCancellableCoroutine<UploadTask.TaskSnapshot> { continuation ->
                uploadTask
                    .addOnSuccessListener { snapshot ->
                        continuation.resume(snapshot)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }
            
            // Get download URL
            val downloadUrl = suspendCancellableCoroutine<Uri> { continuation ->
                fileRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        continuation.resume(uri)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }
            
            Result.Success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.Error(e)
        } finally {
            inputStream?.close()
        }
    }
    
    override suspend fun downloadFile(
        remotePath: String,
        localUri: Uri
    ): Result<Unit> = withContext(ioDispatcher) {
        try {
            val fileRef = userStorageRef.child(remotePath)
            val outputStream = contentResolver.openOutputStream(localUri)
                ?: return@withContext Result.Error("Cannot write to file")
            
            // Download file
            val bytes = suspendCancellableCoroutine<ByteArray> { continuation ->
                fileRef.getBytes(Long.MAX_VALUE)
                    .addOnSuccessListener { data ->
                        continuation.resume(data)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }
            
            outputStream.write(bytes)
            outputStream.close()
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun listFiles(remotePath: String): Result<List<CloudFile>> =
        withContext(ioDispatcher) {
            try {
                val folderRef = userStorageRef.child(remotePath)
                
                val listResult = suspendCancellableCoroutine<ListResult> { continuation ->
                    folderRef.listAll()
                        .addOnSuccessListener { result ->
                            continuation.resume(result)
                        }
                        .addOnFailureListener { exception ->
                            continuation.resumeWithException(exception)
                        }
                }
                
                // Convert to domain models
                val cloudFiles = listResult.items.map { fileRef ->
                    val metadata = suspendCancellableCoroutine<StorageMetadata> { continuation ->
                        fileRef.metadata
                            .addOnSuccessListener { meta ->
                                continuation.resume(meta)
                            }
                            .addOnFailureListener { exception ->
                                continuation.resumeWithException(exception)
                            }
                    }
                    
                    CloudFile(
                        name = fileRef.name,
                        path = fileRef.path,
                        size = metadata.sizeBytes,
                        modifiedTime = metadata.updatedTimeMillis,
                        mimeType = metadata.contentType
                    )
                }
                
                Result.Success(cloudFiles)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    
    override suspend fun deleteFile(remotePath: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                val fileRef = userStorageRef.child(remotePath)
                
                suspendCancellableCoroutine<Void?> { continuation ->
                    fileRef.delete()
                        .addOnSuccessListener {
                            continuation.resume(null)
                        }
                        .addOnFailureListener { exception ->
                            continuation.resumeWithException(exception)
                        }
                }
                
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
}

// 3. WorkManager for Background Sync
@HiltWorker
class CloudSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val cloudSyncRepository: CloudSyncRepository
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            // Get pending files from local database
            val pendingFiles = getPendingFilesFromRoom()
            
            // Upload each file
            pendingFiles.forEach { fileInfo ->
                when (val result = cloudSyncRepository.uploadFile(
                    localUri = fileInfo.uri,
                    remotePath = fileInfo.remotePath
                )) {
                    is Result.Success -> markFileAsSynced(fileInfo.id)
                    is Result.Error -> {
                        // Retry later
                        return Result.retry()
                    }
                }
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}

// 4. DI Module
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()
    
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class CloudSyncDataModule {
    @Binds
    @Singleton
    abstract fun bindCloudSyncRepository(impl: CloudSyncRepositoryImpl): CloudSyncRepository
}
```

### Trade-offs
**Current Implementation:**
- ✅ No external SDK dependencies or setup
- ✅ Works offline without API keys
- ✅ Instant setup for development
- ✅ Realistic OAuth and upload simulation
- ✅ Configurable failure rates for testing
- ⚠️ No actual cloud upload (files not backed up)
- ⚠️ Authentication lost on app restart

**Production Implementation (Firebase Storage):**
- ✅ Real cloud backup and sync
- ✅ Persistent authentication (Firebase Auth)
- ✅ Background sync with WorkManager
- ✅ Automatic retry and resumable uploads (built-in)
- ✅ Offline persistence with automatic sync when online
- ✅ One SDK for storage, database, and auth
- ✅ ✅ **Firebase configured** - google-services.json ready
- ✅ Generous free tier (5GB storage, 1GB/day downloads)
- ⚠️ Vendor lock-in to Google infrastructure
- ⚠️ Requires internet for sync (local cache available)
- ⚠️ Free tier limits (sufficient for MVP)

**Why Not Multi-Provider (Drive/Dropbox/OneDrive):**
- ❌ Each provider requires separate SDK (~5-10MB each)
- ❌ Complex OAuth setup for each provider
- ❌ Fragmented user experience across providers
- ❌ Multiple API keys and console configurations
- ✅ Firebase provides unified backend with better integration

---

## 1️⃣2️⃣ SyncRepositoryImpl.kt

**Location:** `data/repository/SyncRepositoryImpl.kt`  
**Chunk:** 20 (Multi-Device Sync)  
**Priority:** Medium  
**Production Tool:** ✅ Firebase Firestore - Configured and operational

### Strategic Implementation
Uses in-memory "cloud" storage with simulated network behavior to unblock UI development. Provides complete multi-device sync functionality without requiring Firebase setup during development phase.

**Mock Strategy:**
- In-memory "cloud" storage for testing
- Simulated network latency (500-1500ms)
- Configurable failure rate (10%) for error testing
- Thread-safe operations with Mutex
- Last-write-wins conflict resolution
- Real-time sync status via Flow

**Why Firebase Firestore for Production:**
- Real-time synchronization across devices (no polling needed)
- Offline persistence with automatic sync when online
- ✅ Already configured - google-services.json in place
- Excellent Kotlin coroutines support
- Generous free tier (1GB storage, 50K reads/day)

### Fully Functional Features
✅ Complete bidirectional sync (upload & download)  
✅ In-memory "cloud" storage for testing  
✅ Real-time sync status observation via Flow  
✅ Simulated network latency (500-1500ms)  
✅ Configurable failure rate (10%) for error testing  
✅ Thread-safe operations with Mutex  
✅ Last-write-wins conflict resolution  
✅ Comprehensive error handling  
✅ Clean architecture with proper repository pattern  
✅ Comprehensive test coverage (14+ tests)

### UI Implementation (Sokchea)
✅ **AccountContract.kt** - MVI pattern with State/Event/Action  
✅ **AccountViewModel.kt** - Real-time sync status, manual trigger  
✅ **AccountScreen.kt** - Main account & sync management screen  
✅ **AccountComponents.kt** - 6 reusable UI components  
✅ Mock authentication UI (user always signed in for dev)  
✅ Real-time sync status display with Flow observation  
✅ Manual sync trigger with user feedback  
✅ Synced data summary (templates, tags, settings counts)  
✅ Error handling with dismissible error card  
✅ Material 3 design with proper theming  
✅ 8 preview variations for different states  
✅ Loading states and empty states  
✅ Sign in/out UI (mock implementation)

### Production Enhancements Needed
🔄 Integrate Firebase Firestore for real cloud storage  
🔄 Add Firebase Authentication for user management  
🔄 Implement WorkManager for background sync  
🔄 Add network connectivity checks  
🔄 Implement retry logic with exponential backoff  
🔄 Handle concurrent edit conflicts robustly  
🔄 Add data encryption for sensitive preferences  
🔄 Support offline-first sync with queue  
🔄 Add sync conflict resolution UI

### Production Upgrade
```kotlin
// 1. Add Firebase dependencies (build.gradle.kts)
implementation("com.google.firebase:firebase-firestore-ktx:24.10.0")
implementation("com.google.firebase:firebase-auth-ktx:22.3.0")
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

// 2. Implement real Firestore repository
@Singleton
class SyncRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SyncRepository {
    
    private val userPrefsCollection = "user_preferences"
    
    override suspend fun syncPreferences(): Result<Unit> = withContext(ioDispatcher) {
        try {
            val userId = auth.currentUser?.uid 
                ?: return@withContext Result.failure(Exception("Not authenticated"))
            
            // Download from Firestore
            val cloudDoc = firestore.collection(userPrefsCollection)
                .document(userId)
                .get()
                .await()
            
            val cloudPrefs = cloudDoc.toObject(UserPreferences::class.java)
            
            // Merge with local (last-write-wins based on lastSyncTimestamp)
            val localPrefs = getLocalPreferences()
            val merged = mergePreferences(localPrefs, cloudPrefs)
            
            // Upload merged result
            firestore.collection(userPrefsCollection)
                .document(userId)
                .set(merged.copy(lastSyncTimestamp = System.currentTimeMillis()))
                .await()
            
            // Save locally
            saveLocalPreferences(merged)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun mergePreferences(
        local: UserPreferences?,
        cloud: UserPreferences?
    ): UserPreferences {
        // Last-write-wins conflict resolution
        return when {
            local == null -> cloud ?: UserPreferences()
            cloud == null -> local
            (local.lastSyncTimestamp ?: 0) > (cloud.lastSyncTimestamp ?: 0) -> local
            else -> cloud
        }
    }
}

// 3. Setup Firestore security rules
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /user_preferences/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}

// 4. Add WorkManager for background sync
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncRepository: SyncRepository
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return when (syncRepository.syncPreferences()) {
            is kotlin.Result.Success -> Result.success()
            is kotlin.Result.Failure -> Result.retry()
        }
    }
}

// 5. Schedule periodic sync
val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
    .setConstraints(
        Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    )
    .build()

WorkManager.getInstance(context).enqueue(syncRequest)
```

### Trade-offs
**Current Implementation:**
- ✅ No Firebase/Google Play Services dependency
- ✅ Works offline without network
- ✅ Instant setup for development
- ✅ Realistic network simulation
- ✅ Configurable failure rates for testing
- ✅ Zero latency for development
- ✅ Complete UI for account management
- ✅ Real-time sync status updates
- ✅ Mock authentication (always signed in)
- ⚠️ Data lost on app restart (no persistence)
- ⚠️ No actual cloud backup
- ⚠️ Single-device only (no real multi-device sync)
- ⚠️ Mock user authentication (no real Firebase Auth)

**Production Implementation:**
- ✅ Real cloud backup and sync
- ✅ Multi-device synchronization
- ✅ Persistent authentication tokens
- ✅ Background sync with WorkManager
- ✅ Offline-first architecture
- ✅ Conflict resolution for concurrent edits
- ✅ Real Firebase Authentication UI
- ✅ User profile management
- ⚠️ Requires Firebase setup and configuration
- ⚠️ Need Google Play Services on device
- ⚠️ Network dependency for sync
- ⚠️ More complex error handling (auth, network, conflicts)
- ⚠️ SDK size increase (~2-3MB)
- ⚠️ Additional authentication flows (email, Google, etc.)

---

## 📚 Related Documentation

- **Main Index:** [MOCK_IMPLEMENTATIONS.md](../MOCK_IMPLEMENTATIONS.md)
- **CHUNK_17_COMPLETION.md:** Cloud storage integration implementation details
- **CHUNK_20_COMPLETION.md:** Multi-device sync implementation details
- **DATA_PERSISTENCE_MOCKS.md:** Local data storage implementations
- **AI_ML_MOCKS.md:** Machine learning implementations
- **README.md - Implementation Strategy:** Firebase selection rationale

---

📚 **Back to:** [Main Index](../MOCK_IMPLEMENTATIONS.md)
