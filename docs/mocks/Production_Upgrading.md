This roadmap transitions your project from "Mock/Development" status to "Production/Release" status. It is divided into **5 Logical Phases** (Chunks) to minimize breaking changes and ensure stability.

### 🗺️ The Production Roadmap Overview

| Phase | Focus Area | Key Technologies | Complexity | Status |
| :--- | :--- | :--- | :--- | :--- |
| **Phase 1** | **Persistence Layer** | Room Database, SQLite | ⭐⭐⭐ | ✅ **COMPLETED** |
| **Phase 2** | **System Integration** | SAF, ContentObserver, Service | ⭐⭐⭐⭐⭐ | ✅ **COMPLETED** |
| **Phase 3** | **Machine Learning** | Google ML Kit, CameraX | ⭐⭐ | ✅ **COMPLETED** |
| **Phase 4** | **Cloud Backend** | Firebase (Auth, Firestore, Storage) | ⭐⭐⭐⭐ | ✅ **COMPLETED** |
| **Phase 5** | **Hardening** | Optimization, Localization, CI/CD | ⭐⭐ | 🔜 **NEXT** |

---

# 🚀 Production Implementation Roadmap

## Phase 1: The Persistence Layer (Room Implementation) ✅ COMPLETED
**Goal:** Replace in-memory `MutableStateFlow` lists with a persistent local database. This ensures user data (templates, history, tags) survives app restarts.

**Strategic Chunks Affected:** 12, 14, 16, 21

### Step 1.1: Database Schema Design ✅
Define the relationships using Room Entities.
* **Templates (Chunk 12 - TemplateRepositoryImpl):** 1 Template has 1 Config. ✅ **COMPLETED**
* **Tags (Chunk 16 - TagRepositoryImpl):** N Files have M Tags (Many-to-Many). ✅ **COMPLETED**
* **History (Chunk 14 - HistoryRepositoryImpl):** 1 Operation (Linear). ✅ **COMPLETED**
* **Activity Logs (Chunk 21 - ActivityRepositoryImpl):** User activity tracking. ✅ **COMPLETED**

### Step 1.2: Implementation Plan ✅
1.  **Dependencies:** ✅ Added Room 2.6.1 KTX and KSP to `libs.versions.toml` / `build.gradle`.
2.  **Entities:** ✅ Converted all domain models to `@Entity` annotated classes.
    * ✅ `TemplateEntity` (Chunk 12) - with JSON serialization for RenameConfig
    * ✅ `OperationEntity` (Chunk 14) - with Uri string conversion and stack position
    * ✅ `TagEntity` & `FileTagCrossRef` (Chunk 16) - many-to-many junction table
    * ✅ `ActivityLogEntity` (Chunk 21) - with LocalDateTime conversion
3.  **DAOs:** ✅ Created comprehensive interfaces with suspended functions and Flow.
    * ✅ `TemplateDao` - 11 methods including observeAll(), markAsUsed(), toggleFavorite()
    * ✅ `OperationDao` - 12 methods for undo/redo stack management
    * ✅ `TagDao` - 15 methods with complex JOIN queries for many-to-many relationships
    * ✅ `ActivityLogDao` - 14 methods with time-based and status filtering
4.  **Database:** ✅ Created `AppDatabase` v1 with all entities and DAO providers
5.  **DI Module:** ✅ Created `DatabaseModule` providing database and DAO dependencies via Hilt
6.  **Migration:** ✅ Updated all 4 `*RepositoryImpl` classes to use Room DAOs.
    * ✅ `TemplateRepositoryImpl` - migrated from MutableStateFlow to TemplateDao
    * ✅ `HistoryRepositoryImpl` - migrated from in-memory to OperationDao with stack management
    * ✅ `TagRepositoryImpl` - migrated to TagDao with many-to-many FileTagCrossRef
    * ✅ `ActivityRepositoryImpl` - migrated to ActivityLogDao with optimized queries

**Implementation Notes:**
- All repositories maintain existing interface contracts
- Room handles thread safety automatically (removed Mutex usage)
- Flow-based reactive observations preserved
- Database uses fallbackToDestructiveMigration() for development (TODO: add proper migrations)
- Entity-to-domain model converters implemented with toDomain()/fromDomain() extension functions

> **Critical Note:** ✅ **PHASE COMPLETED** - All repositories now use persistent Room database storage.

---

## Phase 2: System Integration (Scoped Storage & Services) ✅ COMPLETED
**Goal:** Make the app compliant with Android 10+ (API 29+) storage restrictions and ensure robust background execution. This was the most technically difficult phase.

**Strategic Chunks Affected:** 6, 9

**Completion Date:** December 10, 2025

### Step 2.1: Storage Access Framework (SAF) Upgrade ✅
Replaced `java.io.File` with `DocumentFile` and `ContentResolver`.

1.  **Folder Selection (Chunk 6 - FolderRepositoryImpl):** ✅ **COMPLETED**
    * ✅ Updated `FolderRepositoryImpl` to use DocumentFile and SAF APIs
    * ✅ Implemented `takePersistableUriPermission()` for long-term directory access
    * ✅ Replaced `file.listFiles()` with `DocumentFile.fromTreeUri(...).listFiles()`
    * ✅ Added ContentObserver for real-time folder monitoring
    * ✅ Full Android 10+ scoped storage compliance
2.  **File Operations:** ✅ **COMPLETED**
    * ✅ Migrated all folder operations to use DocumentFile API
    * ✅ Implemented `DocumentFile.createDirectory()` for folder creation
    * ✅ Added helper methods for URI-based folder information

### Step 2.2: Robust Monitoring (Chunk 9) ✅
Replaced `FileObserver` with `ContentObserver` for reliable monitoring.

1.  **Observer Logic (Chunk 9 - FolderMonitorRepositoryImpl):** ✅ **COMPLETED**
    * ✅ Implemented `ContentObserver` to monitor MediaStore URIs
    * ✅ Registered observers for Images, Video, Audio, and Files content URIs
    * ✅ Proper integration with DocumentFile for scoped storage
    * ✅ Handles file pattern matching and event filtering
2.  **Service Hardening (Chunk 9 - MonitoringService):** ✅ **COMPLETED**
    * ✅ Updated `MonitoringService` to strictly typed **Foreground Service** (`dataSync` type)
    * ✅ Implemented proper foreground service start with `ServiceCompat.startForeground()`
    * ✅ Added Android 10+ `FOREGROUND_SERVICE_TYPE_DATA_SYNC` support
    * ✅ Enhanced notification with stop action and persistent status
    * ✅ Proper handling of Android 13+ notification permissions
3.  **WorkManager Fallback:** ✅ **COMPLETED**
    * ✅ Created `FolderMonitorWorker` for periodic monitoring status checks
    * ✅ Implements 15-minute periodic work to restart service if killed
    * ✅ Handles device reboot scenarios
    * ✅ Integrated with Hilt for dependency injection

**Implementation Notes:**
- All file operations now use SAF-compliant DocumentFile API
- ContentObserver provides more reliable monitoring than FileObserver for scoped storage
- Foreground service properly typed and compliant with Android 14 restrictions
- WorkManager provides resilience against OS service termination
- Full backward compatibility maintained for Android 8+ (API 26+)

**Dependencies Added:****
- `androidx.work:work-runtime-ktx:2.9.0` - WorkManager for fallback mechanism
- `androidx.hilt:hilt-work:1.2.0` - Hilt integration for Worker
- `androidx.documentfile:documentfile:1.0.1` - DocumentFile for SAF support

**Manifest Updates:**
- Service already configured with `foregroundServiceType="dataSync"`
- All required permissions already in place:
  - `FOREGROUND_SERVICE`
  - `FOREGROUND_SERVICE_DATA_SYNC`
  - `POST_NOTIFICATIONS`

> **Critical Note:** ✅ **PHASE COMPLETED** - App is now fully compliant with Android 10+ scoped storage and modern foreground service requirements.

---

## Phase 3: On-Device Intelligence (ML Kit) ✅ COMPLETED
**Goal:** Replace mock patterns with real Google ML Kit implementation. This enables the "Smart" features of the app.

**Strategic Chunks Affected:** 13, 19, 18

### Step 3.1: Vision Integration ✅
1.  **Image Labeling (Chunk 13 - MLRepositoryImpl):** ✅ **COMPLETED**
    * Implementation: ✅ Integrated `com.google.mlkit:image-labeling:17.0.8`
    * Logic: ✅ Mapped ML Kit `ImageLabel` confidence scores to 9 categories (nature, architecture, portrait, activity, food, animal, indoor, event, general)
    * Features: ✅ 5 filename generation strategies (label, category, timestamp, combined, creative)
2.  **OCR (Chunk 19 - OCRRepositoryImpl):** ✅ **COMPLETED**
    * Implementation: ✅ Integrated `com.google.mlkit:text-recognition:16.0.1`
    * Logic: ✅ Extract text blocks with bounding boxes and confidence filtering
    * Features: ✅ Reading order sorting (top-to-bottom, left-to-right), language detection

### Step 3.2: Barcode Scanning (Chunk 18 - QRRepositoryImpl) ✅
1.  **ML Kit Integration:** ✅ **COMPLETED**
    * Implementation: ✅ Integrated `com.google.mlkit:barcode-scanning:17.3.0`
    * Logic: ✅ Real QR code scanning with format validation (QR_CODE filtering)
    * Features: ✅ Supports multiple barcode formats, JSON serialization for template sharing
2.  **CameraX Integration (Optional UI Enhancement):** ⏳ **FUTURE ENHANCEMENT**
    * Add live camera preview with CameraX for real-time QR scanning.
    * Attach `ImageAnalysis.Analyzer` to feed frames to ML Kit Barcode Scanner.

**Implementation Notes:**
- All ML Kit clients initialized via Hilt dependency injection
- Error handling with comprehensive validation (file existence, bitmap dimensions, format support)
- InputImage creation from file paths with automatic orientation handling
- Async/await pattern using Kotlin Coroutines
- Hybrid approach for QRRepositoryImpl: simplified bitmap generation + real ML Kit scanning

> **Critical Note:** ✅ **PHASE COMPLETED** - All ML features now use production Google ML Kit APIs.

---

## Phase 4: Cloud Backend (Firebase) ✅ COMPLETED
**Goal:** Enable multi-device sync and cloud backup using Firebase services.

**Strategic Chunks Affected:** 17, 20

**Completion Date:** December 10, 2025

### Step 4.1: Authentication & Firestore ✅
1.  **Firebase Setup:** ✅ **COMPLETED**
    * ✅ Firebase dependencies added (Auth, Firestore, Storage)
    * ✅ Firebase BOM 34.6.0 for version management
    * ✅ google-services.json configured (Project: daten-sequence)
2.  **Authentication (Firebase Auth):** ✅ **COMPLETED**
    * ✅ Created `AuthRepository` interface
    * ✅ Implemented `AuthRepositoryImpl` with FirebaseAuth
    * ✅ Anonymous sign-in for immediate access
    * ✅ Google Sign-In preparation (credential-based flow)
    * ✅ Persistent session management
    * ✅ `AuthUser` domain model created
3.  **Sync Logic (Chunk 20 - SyncRepositoryImpl):** ✅ **COMPLETED**
    * ✅ Migrated from mock to Firebase Firestore implementation
    * ✅ User-scoped Firestore paths: `/users/{uid}/data/preferences`
    * ✅ Last-write-wins conflict resolution using `FieldValue.serverTimestamp()`
    * ✅ Bidirectional sync (upload & download)
    * ✅ Real-time status tracking via Flow
    * ✅ Offline persistence enabled in Firestore

### Step 4.2: Storage Backup (Chunk 17 - CloudSyncRepositoryImpl) ✅
1.  **File Upload:** ✅ **COMPLETED**
    * ✅ Implemented `CloudSyncRepository` interface
    * ✅ Created `CloudSyncRepositoryImpl` with Firebase Storage
    * ✅ User-scoped storage paths: `/users/{uid}/files/{filename}`
    * ✅ Single file upload with metadata
    * ✅ Batch file sync with progress tracking (Flow-based)
    * ✅ Automatic retry and resumable uploads (Firebase SDK)
2.  **Background Sync:** ✅ **COMPLETED**
    * ✅ Created `CloudSyncWorker` (HiltWorker)
    * ✅ WorkManager integration for periodic sync
    * ✅ `CloudSyncScheduler` utility for managing sync schedule
    * ✅ Configurable interval (minimum 15 minutes)
    * ✅ WiFi-only constraint support
    * ✅ Automatic retry with exponential backoff

### Step 4.3: Dependency Injection ✅
1.  **Firebase Module:** ✅ **COMPLETED**
    * ✅ Created `FirebaseModule` providing FirebaseAuth, Firestore, Storage
    * ✅ Singleton scope for all Firebase instances
    * ✅ Offline persistence enabled for Firestore
2.  **Repository Bindings:** ✅ **COMPLETED**
    * ✅ Updated `SyncDataModule` with CloudSyncRepository binding
    * ✅ Created `AuthDataModule` for AuthRepository binding

### Step 4.4: Security Configuration 📝
**Action Required:** Deploy Firestore and Storage security rules to Firebase Console.

See `docs/FIREBASE_SECURITY_RULES.md` for:
- Firestore rules (user data isolation)
- Storage rules (user file isolation)
- Deployment instructions
- Testing guidelines

**Implementation Notes:**
- All repositories follow Clean Architecture principles
- Firebase SDK handles token refresh and session persistence automatically
- Offline-first approach: Firestore caches data locally
- Error handling with comprehensive Result types
- Real-time sync status via Flow for reactive UI updates

**Next Steps:**
1. Deploy security rules to Firebase Console (see FIREBASE_SECURITY_RULES.md)
2. Test authentication flow in app
3. Implement Google Sign-In UI integration (Activity/ViewModel layer)
4. Schedule periodic sync based on user preferences
5. Add sync status indicators in UI

> **Critical Note:** ✅ **PHASE COMPLETED** - Firebase backend fully integrated and ready for production use. Security rules must be deployed before public release.

---

## Phase 5: Production Polish & Hardening 🔜 NEXT
**Goal:** Remove "Dev-Only" shortcuts and prepare for release.

**Strategic Chunks Affected:** 5, 22, 23, 25

### Step 5.0: Critical Setup Tasks 🔴
**REQUIRED BEFORE TESTING:**
1.  **Firebase Configuration:**
    * ✅ `google-services.json` exists in `app/` directory
    * ✅ Firebase BOM 34.6.0 configured
    * ✅ All Firebase dependencies added
2.  **Gradle Sync:**
    * ✅ Gradle 8.11.1 confirmed
    * ✅ Room compiler switched from KAPT to KSP
    * ✅ Production code builds successfully (`assembleDebug`)
    * ❌ Unit tests have compilation errors (see Step 5.2)
3.  **Firebase Security Rules:**
    * ❌ Deploy Firestore and Storage security rules (see `FIREBASE_SECURITY_RULES.md`)
    * Rules must be deployed before testing cloud sync features

### Step 5.2: Fix Unit Tests 🔴 HIGH PRIORITY
**Status:** ~300+ compilation errors in test files

**Root Causes:**
1.  **Domain Model Changes:** Test files reference old model structures
    * FileItem ID type changed from String to Long
    * RenameTemplate structure changed
    * Result type wrapper implementation changed
    * PreviewItem and other models have new required parameters
2.  **Repository Interface Changes:** Fake repositories out of sync
    * Missing new methods (observeHistory, getTemplates, etc.)
    * Changed return types (Result wrappers)
    * New required parameters in existing methods
3.  **Use Case Changes:** Test constructors missing new dependencies
    * Missing dispatcher parameters
    * Missing use case dependencies
    * Changed parameter types

**Action Required:**
* Either fix all test files to match new implementations
* Or temporarily disable tests for production deployment
* Recommendation: Fix during Phase 5 comprehensive testing

### Step 5.1: Performance & Localization
1.  **Localization (Chunk 25):**
    * Run the provided `StringResourcesTest`.
    * Generate `strings.xml` for target languages (ES, FR, AR).
    * Replace `AndroidLocalizedStringProvider` runtime lookups with direct `R.string` references for compile-time safety.
2.  **Media Scanner (Chunk 5):**
    * Implement `MediaScannerConnection.scanFile` to ensure renamed files appear immediately in the user's Gallery.
3.  **Performance Monitoring (Chunk 22):**
    * Replace mock benchmarks with Android Profiler integration.
    * Add LeakCanary for memory leak detection.
    * Implement performance telemetry for critical operations (file rename, batch processing).
4.  **E2E Testing (Chunk 23):**
    * Replace mock E2E tests with full instrumented tests.
    * Implement critical user flows (file selection → rename → verification).
    * Add UI testing with Espresso for end-to-end validation.
5.  **Complete TagRepository (Chunk 16):**
    * Implement `getFilesByTag()` with real MediaStore/DocumentFile queries.
    * Remove TODO placeholders for file size, mimeType, and modification date.
    * Test tag filtering with actual file metadata.

---

## 📅 Suggested Sprint Schedule

This project is well-scoped. Assuming one developer:

* **Sprint 1 (Weeks 1-2):** ✅ **Phase 1 (Room DB).** This solidified the data structure.
* **Sprint 2 (Weeks 3-4):** ✅ **Phase 2 (SAF & Monitoring).** This ensured the core feature works on modern Android.
* **Sprint 3 (Week 5):** ✅ **Phase 3 (ML Kit).** Fun, high-value features.
* **Sprint 4 (Week 6):** ✅ **Phase 4 (Firebase).** Cloud sync and backup fully integrated.
* **Sprint 5 (Week 7-8):** 🔜 **Phase 5 (Polish).** Testing and App Store prep.

**Project Status:** 90% Complete (Phases 1-4 done, production code builds)
**Remaining Work:** 
- 🔴 Deploy Firebase security rules (CRITICAL)
- 🔴 Fix ~300+ unit test compilation errors
- 🟡 Complete TagRepository MediaStore integration
- 🟡 Performance optimization and localization
- 🟡 E2E testing with fixed test suite
- 🟡 App Store preparation