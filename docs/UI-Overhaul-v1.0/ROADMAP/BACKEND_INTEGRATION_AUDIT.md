# Backend Integration Audit - Collapsible Sidebar Navigation

**Audit Date:** January 23, 2026  
**Project:** Android Collapsible Sidebar Implementation  
**Status:** 🟡 Partial Backend Integration (navigation dedup complete)

---

## 🎯 Executive Summary

**Finding:** Your navigation UI is **fully wired**, but several screens have **MOCK/PLACEHOLDER** backend implementations or **MISSING** domain logic.

**Key Issues:**
1. ✅ **All 21+ screens have navigation routes** - UI navigation is 100% complete
2. ⚠️ **10 screens have ViewModels BUT use MOCK data** - UI works, backend is simulated
3. ❌ **AccountScreen lacks use cases** - No domain/usecase/account/ directory exists
4. ⚠️ **Cloud/Sync features are MOCK implementations** - Firebase integration incomplete

**Impact:** Users can navigate everywhere, but some features won't persist data or connect to real services.

**Recent Change:** Sidebar/Settings deduplication is complete; Settings is now the single entry point for CloudSync, Account, ActivityLog, and History. See [docs/UI-Overhaul-v1.0/DOCUMENTATION/SIDEBAR_DEDUPLICATION_COMPLETION.md](docs/UI-Overhaul-v1.0/DOCUMENTATION/SIDEBAR_DEDUPLICATION_COMPLETION.md) for details. Navigation now uses SIDEBAR_ROUTES and SETTINGS_ONLY_ROUTES; backend scope below is unchanged but entry points are consolidated.

---

## 📊 Backend Integration Status by Screen

### ✅ **FULLY IMPLEMENTED (11 Screens)** - Production Ready

| Screen | ViewModel | Contract | Use Cases | Repository | Status |
|--------|-----------|----------|-----------|------------|--------|
| **FileSelectionScreen** | ✅ | ✅ | ✅ (6 use cases) | ✅ MediaRepository | 🟢 **Full** |
| **RenameConfigScreen** | ✅ | ✅ | ✅ (rename/*) | ✅ FileRenameRepository | 🟢 **Full** |
| **PreviewScreen** | ✅ | ✅ | ✅ GeneratePreviewUseCase | ✅ FileRenameRepository | 🟢 **Full** |
| **RenameProgressScreen** | ✅ | ✅ | ✅ (rename/*) | ✅ FileRenameRepository | 🟢 **Full** |
| **FolderSelectorScreen** | ✅ | ✅ | ✅ folder/* | ✅ FolderRepository | 🟢 **Full** |
| **MonitoringScreen** | ✅ | ✅ | ✅ monitoring/* | ✅ FolderMonitorRepository | 🟢 **Full** |
| **SettingsScreen** | ✅ | ✅ | ✅ settings/* | ✅ PreferencesRepository | 🟢 **Full** |
| **TagManagementScreen** | ✅ | ✅ | ✅ (7 tag use cases) | ✅ TagRepository | 🟢 **Full** |
| **TemplateScreen** | ✅ | ✅ | ✅ (7 template use cases) | ✅ TemplateRepository | 🟢 **Full** |
| **HistoryScreen** | ✅ | ✅ | ✅ history/* | ✅ HistoryRepository | 🟢 **Full** |
| **PermissionsManagementScreen** | ✅ | ✅ | ✅ permissions/* | ✅ PermissionsRepository | 🟢 **Full** |

**Details:**
- These screens have complete data flow: UI → ViewModel → UseCase → Repository → Data Source
- Room database entities exist with proper mappers
- State persistence implemented
- Production-ready

---

### ⚠️ **MOCK IMPLEMENTATIONS (7 Screens)** - UI Works, Backend Simulated

| Screen | ViewModel | Contract | Use Cases | Repository | Mock Status |
|--------|-----------|----------|-----------|------------|-------------|
| **AISuggestionsScreen** | ✅ | ✅ | ✅ ai/* | ✅ MLRepository | 🟡 **ML Kit Mock** |
| **OCRScreen** | ✅ | ✅ | ✅ ocr/* | ✅ OCRRepository | 🟡 **ML Kit Mock** |
| **QRScannerScreen** | ✅ (QRViewModel) | ✅ | ✅ qr/* | ✅ QRRepository | 🟡 **ML Kit Mock** |
| **QRDisplayScreen** | ✅ (QRViewModel) | ✅ | ✅ qr/* | ✅ QRRepository | 🟡 **Mock** |
| **CloudSyncScreen** | ✅ | ✅ | ❌ **Missing** | ✅ CloudSyncRepository | 🟡 **Firebase Mock** |
| **AccountScreen** | ✅ | ✅ | ❌ **Missing** | ✅ AuthRepository | 🟡 **Mock** |
| **ActivityLogScreen** | ✅ | ✅ | ✅ activity/* | ✅ ActivityRepository | 🟡 **Mock** |

**Critical Issues:**

#### 1. AI/ML Features (AISuggestions, OCR, QR)
**Status:** 🟡 **Mock ML Kit Implementation**
```kotlin
// Current: Mock responses
MLRepository returns hardcoded suggestions
OCRRepository returns placeholder text
QRRepository generates static QR codes
```

**Problem:**
- ML Kit Text Recognition API not initialized
- Image Labeling API not configured
- Barcode Scanning not integrated with Firebase ML Kit

**Required Actions:**
```kotlin
// Need to implement:
1. MLRepositoryImpl with actual ML Kit calls
2. Configure google-services.json with ML Kit APIs
3. Add ML Kit dependencies (already in gradle)
4. Test on-device ML models
```

**Files to Create/Update:**
- `data/repository/MLRepositoryImpl.kt` - Replace mock with ML Kit API calls
- `data/repository/OCRRepositoryImpl.kt` - Integrate ML Kit Text Recognition
- `data/repository/QRRepositoryImpl.kt` - Integrate Barcode Scanning API

---

#### 2. Cloud Sync & Account (CloudSyncScreen, AccountScreen)
**Status:** 🟡 **Firebase Mock**

**Missing Use Cases:**
```
❌ domain/usecase/cloud/ - Directory doesn't exist
❌ domain/usecase/account/ - Directory doesn't exist
```

**Current Implementation:**
```kotlin
// CloudSyncViewModel - Uses mock SyncRepository
class CloudSyncViewModel @Inject constructor(
    private val syncRepository: SyncRepository, // ✅ Exists
    // BUT: No use cases for cloud operations!
)

// AccountViewModel - Uses mock AuthRepository
class AccountViewModel @Inject constructor(
    private val authRepository: AuthRepository, // ✅ Exists
    // BUT: No use cases for account operations!
)
```

**Problem:**
- `CloudSyncRepository` exists but returns mock data
- `AuthRepository` exists but doesn't connect to Firebase Auth
- No business logic layer (use cases)

**Required Actions:**
```kotlin
// 1. Create Cloud Use Cases
domain/usecase/cloud/
├── ConnectCloudProviderUseCase.kt
├── SyncTemplatesUseCase.kt
├── SyncPreferencesUseCase.kt
└── DisconnectProviderUseCase.kt

// 2. Create Account Use Cases
domain/usecase/account/
├── SignInUseCase.kt
├── SignOutUseCase.kt
├── GetAccountInfoUseCase.kt
└── SyncAccountDataUseCase.kt

// 3. Implement Firebase Integration
data/repository/CloudSyncRepositoryImpl.kt - Connect to Firestore
data/repository/AuthRepositoryImpl.kt - Connect to Firebase Auth
```

**Firebase Setup Needed:**
- ✅ `google-services.json` exists
- ❌ Firebase Authentication not initialized
- ❌ Firestore sync rules not configured
- ❌ Firebase Storage not connected

---

#### 3. Activity Log
**Status:** 🟡 **Mock Data**

**Existing:**
- ✅ ActivityRepository exists
- ✅ 3 use cases: GetActivityLogsUseCase, LogActivityUseCase, ExportLogsUseCase
- ✅ ViewModel and Contract complete

**Problem:**
- ActivityRepository returns hardcoded log entries
- No Room database entity for ActivityLog
- Logs not persisted locally

**Required Actions:**
```kotlin
// Create Room entity
data/local/entity/ActivityLogEntity.kt

// Update repository implementation
data/repository/ActivityRepositoryImpl.kt
- Use Room DAO instead of mock list
- Implement actual log filtering
- Add export to file functionality
```

---

### ⚠️ **HELPER TOOLS (3 Screens)** - UI Only, Minimal Backend

| Screen | ViewModel | Contract | Use Cases | Status |
|--------|-----------|----------|-----------|--------|
| **RegexBuilderScreen** | ✅ | ✅ | ✅ regex/* | 🟡 **UI Logic Only** |
| **MetadataPickerScreen** | ✅ | ✅ | ✅ metadata/* | 🟡 **MediaStore Mock** |
| **HomeScreen** | ❌ None | ❌ | ❌ | 🟢 **Navigation Hub** |

**Notes:**
- RegexBuilderScreen: Pure UI tool, no persistence needed (by design)
- MetadataPickerScreen: Reads from MediaStore, but currently returns mock metadata
- HomeScreen: Stateless navigation hub, no ViewModel needed (by design)

---

## 🔍 Detailed Backend Gaps

### Missing Domain Layer Components

#### 1. Cloud Sync Use Cases ❌
**Location:** `domain/usecase/cloud/` - **DOESN'T EXIST**

**Needed:**
```kotlin
ConnectCloudProviderUseCase.kt
DisconnectCloudProviderUseCase.kt
SyncTemplatesUseCase.kt
SyncPreferencesUseCase.kt
GetCloudSyncStatusUseCase.kt
```

**Impact:** Cloud sync feature can't be implemented without business logic layer

---

#### 2. Account Management Use Cases ❌
**Location:** `domain/usecase/account/` - **DOESN'T EXIST**

**Needed:**
```kotlin
SignInUseCase.kt
SignOutUseCase.kt
GetAccountInfoUseCase.kt
SyncAccountDataUseCase.kt
ValidateCredentialsUseCase.kt
```

**Impact:** Account screen has no way to perform authentication operations

---

#### 3. ML Kit Integration ❌
**Current:** Mock responses in `MLRepositoryImpl`

**Needed:**
```kotlin
// Update MLRepositoryImpl.kt
override suspend fun generateSuggestions(files: List<FileItem>): Result<List<String>> {
    return withContext(dispatcher) {
        resultOf {
            // REPLACE THIS:
            listOf("vacation_2024_", "trip_", "photo_")
            
            // WITH THIS:
            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
            val suggestions = files.flatMap { file ->
                val image = InputImage.fromFilePath(context, file.uri)
                labeler.process(image).await().map { it.text }
            }
            suggestions.distinct()
        }
    }
}
```

**Impact:** AI suggestions return hardcoded data instead of analyzing file content

---

#### 4. Firebase Services Integration ❌

**Current Status:**
- ✅ Firebase BOM dependency included
- ✅ google-services.json exists
- ❌ Firebase Auth not initialized in Application class
- ❌ Firestore not configured
- ❌ Firebase Storage not setup

**Needed in Application.kt:**
```kotlin
class ConversionApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
    }
}
```

**Impact:** Cloud sync and account features can't connect to Firebase backend

---

### Missing Data Layer Components

#### 1. ActivityLogEntity ❌
**Location:** `data/local/entity/` - Missing ActivityLogEntity.kt

**Needed:**
```kotlin
@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val action: String,
    val status: String,
    val filesAffected: Int,
    val details: String
)

// Mapper extensions
fun ActivityLogEntity.toDomain(): ActivityLog
fun ActivityLog.toEntity(): ActivityLogEntity
```

**Impact:** Activity logs not persisted, lost on app restart

---

#### 2. Room DAO for ActivityLog ❌
**Location:** `data/local/dao/` - Missing ActivityLogDao.kt

**Needed:**
```kotlin
@Dao
interface ActivityLogDao {
    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentLogs(limit: Int): Flow<List<ActivityLogEntity>>
    
    @Insert
    suspend fun insertLog(log: ActivityLogEntity)
    
    @Query("DELETE FROM activity_logs WHERE timestamp < :cutoffTime")
    suspend fun deleteOldLogs(cutoffTime: Long)
}
```

---

## 📋 Action Plan

### Priority 1: Critical Backend Gaps (P0)

#### Task 1.1: Create Cloud Sync Use Cases
**Duration:** 4 hours  
**Owner:** Kai (Backend Lead)

```bash
# Files to create
domain/usecase/cloud/ConnectCloudProviderUseCase.kt
domain/usecase/cloud/DisconnectCloudProviderUseCase.kt
domain/usecase/cloud/SyncTemplatesUseCase.kt
domain/usecase/cloud/SyncPreferencesUseCase.kt
```

#### Task 1.2: Create Account Management Use Cases
**Duration:** 3 hours  
**Owner:** Kai (Backend Lead)

```bash
# Files to create
domain/usecase/account/SignInUseCase.kt
domain/usecase/account/SignOutUseCase.kt
domain/usecase/account/GetAccountInfoUseCase.kt
domain/usecase/account/SyncAccountDataUseCase.kt
```

#### Task 1.3: Implement Firebase Integration
**Duration:** 6 hours  
**Owner:** Kai (Backend Lead)

```kotlin
// 1. Update Application.kt
ConversionApplication.kt - Initialize Firebase

// 2. Update CloudSyncRepositoryImpl.kt
Connect to Firestore for template/preference sync

// 3. Update AuthRepositoryImpl.kt
Implement Firebase Authentication

// 4. Configure Firestore Security Rules
firestore.rules - Add security rules
```

#### Task 1.4: Implement ActivityLog Persistence
**Duration:** 3 hours  
**Owner:** Kai (Backend Lead)

```bash
# Files to create
data/local/entity/ActivityLogEntity.kt
data/local/dao/ActivityLogDao.kt

# Files to update
data/repository/ActivityRepositoryImpl.kt - Use Room instead of mock
data/local/ConversionDatabase.kt - Add ActivityLogDao
```

---

### Priority 2: ML Kit Integration (P1)

#### Task 2.1: Replace ML Mock with ML Kit API
**Duration:** 8 hours  
**Owner:** Kai (Backend Lead)

```kotlin
// Files to update
data/repository/MLRepositoryImpl.kt - Integrate Image Labeling API
data/repository/OCRRepositoryImpl.kt - Integrate Text Recognition API
data/repository/QRRepositoryImpl.kt - Integrate Barcode Scanning API

// Test on real device
Verify ML Kit models download and run
```

---

### Priority 3: Metadata Integration (P2)

#### Task 3.1: Connect MetadataPicker to MediaStore
**Duration:** 2 hours  
**Owner:** Kai (Backend Lead)

```kotlin
// Update MetadataRepositoryImpl.kt
Read actual EXIF data from ContentResolver
Parse date, location, camera info
```

---

## ✅ Backend Coverage Summary

### By Feature Category

| Category | Total Screens | Full Backend | Mock Backend | UI Only | Coverage % |
|----------|---------------|--------------|--------------|---------|-----------|
| **Core Rename** | 5 | 5 | 0 | 0 | 100% ✅ |
| **Management** | 3 | 3 | 0 | 0 | 100% ✅ |
| **Smart Features** | 5 | 1 | 3 | 1 | 20% ⚠️ |
| **Integration** | 3 | 0 | 3 | 0 | 0% ❌ |
| **Helper Tools** | 3 | 0 | 1 | 2 | 0% ⚠️ |
| **Navigation** | 2 | 2 | 0 | 0 | 100% ✅ |

**Overall Backend Coverage:** 11/21 screens (52%) have full production backend

---

## 🎯 Success Criteria

### Must Have for MVP (P0)
- ✅ Core batch rename features (FileSelection → Config → Preview → Progress) - **100% Complete**
- ✅ Template and Tag management - **100% Complete**
- ✅ Settings and Permissions - **100% Complete**
- ❌ Cloud sync with Firebase - **Mock Implementation**
- ❌ Account management - **Mock Implementation**
- ❌ Activity log persistence - **No Database**

### Should Have (P1)
- ❌ AI filename suggestions with ML Kit - **Mock Data**
- ❌ OCR text extraction - **Mock Implementation**
- ❌ QR code scanning/generation - **Mock Implementation**
- ✅ History tracking with undo/redo - **Complete**

### Nice to Have (P2)
- ❌ Metadata picker from MediaStore - **Mock Data**
- ✅ Regex builder helper - **UI Only (by design)**

---

## 🚨 Risks & Mitigation

### Risk 1: Firebase Services Not Configured
**Impact:** HIGH - Cloud sync and account features won't work  
**Likelihood:** CERTAIN - Current implementation is mock  
**Mitigation:**
1. Initialize Firebase in Application.onCreate()
2. Configure Firestore security rules
3. Test authentication flow
4. Implement offline-first sync with WorkManager

### Risk 2: ML Kit Models Not Downloaded
**Impact:** MEDIUM - AI features fail on first use  
**Likelihood:** HIGH - Models download on-demand  
**Mitigation:**
1. Pre-download ML models in background
2. Show loading state during first analysis
3. Cache ML results locally
4. Provide fallback suggestions

### Risk 3: Activity Logs Lost on App Restart
**Impact:** LOW - Users lose operation history  
**Likelihood:** CERTAIN - No persistence currently  
**Mitigation:**
1. Implement Room entity ASAP
2. Add migration script
3. Test with large log datasets

---

## 📝 Recommendations

### For Sokchea (UI Lead)
✅ **Your navigation implementation is excellent** - All screens are wired properly  
✅ **UI layer is production-ready** - ViewModels, Contracts, Screens all follow MVI pattern  
⏸️ **Wait for Kai to complete backend tasks** before final integration testing

### For Kai (Backend Lead)
🔴 **URGENT:** Create cloud and account use cases (Tasks 1.1, 1.2)  
🔴 **HIGH:** Implement Firebase integration (Task 1.3)  
🟡 **MEDIUM:** Replace ML Kit mocks with real API calls (Task 2.1)  
🟢 **LOW:** ActivityLog persistence can be deferred (Task 1.4)

### For Team
1. **Document mock status** in each ViewModel file:
```kotlin
/**
 * CloudSyncViewModel
 * 
 * **MOCK STATUS:** Firebase integration pending
 * **TODO:** Connect to real Firestore backend (Task 1.3)
 * **SEE:** BACKEND_INTEGRATION_AUDIT.md
 */
```

2. **Add integration tests** that will fail until backend is ready:
```kotlin
@Test
fun `cloudSync should persist to Firestore`() {
    // This test will fail until Firebase is configured
    assumeTrue(FirebaseApp.getApps(context).isNotEmpty())
    // Test implementation
}
```

3. **Create feature flags** to disable mock screens in production:
```kotlin
object FeatureFlags {
    const val ENABLE_CLOUD_SYNC = false // Set to true after Task 1.3
    const val ENABLE_ML_FEATURES = false // Set to true after Task 2.1
}
```

---

## 📚 Reference Documentation

- **Navigation Wiring:** [NAVIGATION_WIRING_ROADMAP.md](NAVIGATION_WIRING_ROADMAP.md)
- **Work Division:** [docs/Division/WORK_DIVISION.md](../../Division/WORK_DIVISION.md)
- **Architecture Decisions:** [docs/adr/](../../adr/)
- **Cleanup Plan:** [BACKEND_FRONTEND_CLEANUP_PLAN.md](../../../BACKEND_FRONTEND_CLEANUP_PLAN.md)

---

**Last Updated:** January 23, 2026  
**Next Review:** January 25, 2026 (After P0 tasks completion)  
**Audit Completed By:** GitHub Copilot (AI Assistant)
