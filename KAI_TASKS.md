# Kai's Work Guide - Backend/Core Features
## Auto Rename File Service

**Role:** Backend/Core Features Specialist  
**Focus:** Domain layer, Data layer, Business logic, File operations, Permissions  
**Primary Skills:** Kotlin, Android APIs, File system operations, Clean Architecture

**Last Updated:** November 21, 2025

---

## 🎯 Your Responsibilities

### Primary Areas:
- ✅ Domain layer: Models, Repository interfaces, Use cases
- ✅ Data layer: Repository implementations, Data sources
- ✅ Dependency Injection modules
- ✅ File system operations and MediaStore integration
- ✅ Permission handling (data layer)
- ✅ Business logic and validation
- ✅ Unit and integration testing
- ✅ API integrations (ML Kit, Cloud services)

### Your Files (Exclusive Ownership):
```
domain/
├── model/               ✅ You create all models
├── repository/          ✅ You create all interfaces
└── usecase/            ✅ You implement all use cases

data/
├── repository/         ✅ You implement repositories
├── source/             ✅ You create data sources
└── model/              ✅ You create data models

di/
├── DataModule.kt       ✅ You own
├── DomainModule.kt     ✅ You own
├── DispatcherModule.kt ✅ You own
└── *DataModule.kt      ✅ You create feature-specific DI modules

test/
├── domain/             ✅ Your tests
└── data/               ✅ Your tests
```

---

## 🔄 Your Git Workflow

### Daily Workflow:
```bash
# Start of day: Sync with main
git checkout kai-dev
git pull origin main --rebase

# Create feature branch for your work
git checkout -b feature/chunk-X-backend
# Example: feature/chunk-3-file-selection-backend

# Make your changes...
# Test your code...

# Commit with clear messages
git add .
git commit -m "[CHUNK X] Implement Feature - Backend"

# Before creating PR: Rebase on main
git checkout kai-dev
git pull origin main --rebase
git checkout feature/chunk-X-backend
git rebase kai-dev

# Push and create PR
git push origin feature/chunk-X-backend
```

### PR Title Format:
```
"[CHUNK X] Feature Name - Backend Implementation"
"[READY] Domain Models for Feature X - Sokchea can start UI"
"[DATA] Feature X - Data layer implementation"
```

---

## 📋 Your Task Checklist Per Chunk

### Step 1: Domain Models (Day 1 Morning - BLOCKS Sokchea)
```kotlin
□ Create domain models in domain/model/
□ Create repository interface in domain/repository/
□ Create use cases in domain/usecase/
□ Add KDoc comments to all public APIs
□ Ensure models are stable (won't change)
□ Write basic unit tests for use cases
□ Commit and create PR with "[READY]" tag
□ Notify Sokchea that domain is ready
```

**Important:** Sokchea is waiting for this! Complete ASAP.

### Step 2: Data Implementation (Day 1 Afternoon - Parallel with Sokchea)
```kotlin
□ Create repository implementation in data/repository/
□ Create data sources if needed in data/source/
□ Create feature-specific DI module (e.g., PermissionsDataModule.kt)
□ Add repository binding to DI module
□ Write comprehensive unit tests
□ Test with mock ViewModels if needed
□ Commit and create PR
```

**Note:** Sokchea is working on UI in parallel - no conflicts!

### Step 3: Integration Testing (After both merge)
```kotlin
□ Pull latest main
□ Build and run the app
□ Test end-to-end flow with Sokchea's UI
□ Fix any integration issues
□ Verify all tests pass
```

---

## 📊 Your Work by Phase

### **Phase 2: Core Features (MVP)** - YOUR PRIORITY

#### ✅ CHUNK 2: Permissions System (COMPLETED)
**Status:** 100% Complete
- ✅ Domain models (Permission, PermissionStatus, PermissionState)
- ✅ Repository interface (PermissionsRepository)
- ✅ Use cases (Check, GetRequired, HasMediaAccess, Observe)
- ✅ Data implementation (PermissionsManagerImpl)
- ✅ DI module (DataModule updated)
- ✅ Unit tests (PermissionsManagerImplTest - 11 tests)

---

#### 🔜 CHUNK 3: File Selection Feature
**Your Tasks:**

**Tasks:**
```kotlin
// 1. Create domain models
domain/model/FileItem.kt
- Properties: id, uri, name, path, size, mimeType, dateModified, thumbnailUri
- Extension property: isImage, isVideo, extension

domain/model/FileFilter.kt
- Properties: includeImages, includeVideos, includeAudio, minSize, maxSize

// 2. Create repository interface
domain/repository/MediaRepository.kt
- suspend fun getMediaFiles(filter: FileFilter): Result<List<FileItem>>
- suspend fun getFilesByFolder(folderPath: String): Result<List<FileItem>>
- fun observeMediaFiles(): Flow<List<FileItem>>

// 3. Create use case
domain/usecase/fileselection/GetMediaFilesUseCase.kt
- Extend BaseUseCase<FileFilter, List<FileItem>>
- Call repository.getMediaFiles(filter)
```

**Commit & PR:** `[READY] CHUNK 3 Domain Models - Sokchea can start UI`

**Tasks:**
```kotlin
// 4. Create data source
data/source/local/MediaStoreDataSource.kt
- Use ContentResolver to query MediaStore
- Query MediaStore.Images.Media
- Query MediaStore.Video.Media
- Implement pagination if needed

// 5. Create repository implementation
data/repository/MediaRepositoryImpl.kt
- Inject MediaStoreDataSource and Context
- Implement all interface methods
- Handle scoped storage properly
- Add error handling

// 6. Create DI module
di/FileSelectionDataModule.kt
@Module
@InstallIn(SingletonComponent::class)
object FileSelectionDataModule {
    @Provides
    fun provideMediaStoreDataSource(@ApplicationContext context: Context) = ...
    
    @Provides
    fun provideMediaRepository(dataSource: MediaStoreDataSource): MediaRepository = ...
}

// 7. Write tests
test/data/repository/MediaRepositoryImplTest.kt
- Test getMediaFiles with different filters
- Test empty results
- Test error handling
- Mock ContentResolver
```



---

#### 🔜 CHUNK 4: Batch Rename Logic Core
**Your Tasks:**

**Tasks:**
```kotlin
// 1. Create domain models
domain/model/RenameConfig.kt
- Properties: prefix, startNumber, digitCount, preserveExtension, sortStrategy

domain/model/RenameResult.kt
- Properties: originalFile, newName, success, error

// 2. Create use case
domain/usecase/rename/GenerateFilenameUseCase.kt
- Input: FileItem, RenameConfig, index
- Output: String (generated filename)
- Logic: "{prefix}{paddedNumber}.{extension}"

// 3. Create validation use case
domain/usecase/rename/ValidateFilenameUseCase.kt
- Check illegal characters
- Check length limits
- Check reserved names (Windows: CON, PRN, etc.)
```

**Commit & PR:** `[READY] CHUNK 4 Domain Models - Sokchea can start UI`

**Tasks:**
```kotlin
// 4. Create data manager
data/manager/FileOperationsManager.kt
- fun validateFilename(name: String): Boolean
- fun detectConflicts(names: List<String>): List<String>
- fun generateSafeName(name: String, index: Int): String

// 5. Write tests
test/domain/usecase/GenerateFilenameUseCaseTest.kt
test/data/manager/FileOperationsManagerTest.kt
- Test various prefix combinations
- Test padding (001, 0001, etc.)
- Test invalid character handling
- Test duplicate detection
```



---

#### 🔜 CHUNK 5: Rename Execution
**Your Tasks:**

**Tasks:**
```kotlin
// 1. Create use case with progress
domain/usecase/rename/ExecuteBatchRenameUseCase.kt
- Input: List<FileItem>, RenameConfig
- Output: Flow<RenameProgress>
- Emit progress for each file
- Handle errors gracefully

domain/model/RenameProgress.kt
- Properties: currentIndex, total, currentFile, status

// 2. Implement file renaming
data/repository/FileRenameRepository.kt
- suspend fun renameFile(uri: Uri, newName: String): Result<Uri>
- Use MediaStore.Files for scoped storage
- Update MediaStore after rename

// 3. Create repository implementation
data/repository/FileRenameRepositoryImpl.kt
- Use ContentResolver.update()
- Handle Android 10+ scoped storage
- Trigger MediaScannerConnection after rename

// 4. Add DI binding
di/RenameDataModule.kt

// 5. Write tests
test/domain/usecase/ExecuteBatchRenameUseCaseTest.kt
- Test successful rename
- Test error handling
- Test progress emission
- Mock repository
```



---

#### 🔜 CHUNK 6: Destination Folder Selector
**Your Tasks:**

**Tasks:**
```kotlin
// 1. Create domain model
domain/model/FolderInfo.kt
- Properties: path, name, fileCount, subfolderCount, uri

// 2. Create repository interface
domain/repository/FolderRepository.kt
- suspend fun getFolders(parentPath: String): Result<List<FolderInfo>>
- suspend fun createFolder(parentPath: String, name: String): Result<FolderInfo>

// 3. Create use case
domain/usecase/folder/GetFoldersUseCase.kt

// 4. Implement data layer
data/repository/FolderRepositoryImpl.kt
- Use DocumentFile API
- Handle SAF (Storage Access Framework)
- List directories

// 5. Add DI module
di/FolderDataModule.kt
```



---

### **Phase 3: Advanced Features**

#### 🔜 CHUNK 7: Preview System
**Your Tasks:**
```kotlin
// 1. Create domain model
domain/model/PreviewItem.kt
- Properties: original, preview, hasConflict, conflictReason

// 2. Create use case
domain/usecase/preview/GeneratePreviewUseCase.kt
- Input: List<FileItem>, RenameConfig
- Output: List<PreviewItem>
- Detect name conflicts
- Validate all names

// 3. Write tests
```



---

#### 🔜 CHUNK 8: Natural Sorting
**Your Tasks:**
```kotlin
// 1. Create sorting strategies
domain/model/SortStrategy.kt (enum)
- NATURAL, DATE_MODIFIED, SIZE, ORIGINAL_ORDER

// 2. Create use case
domain/usecase/sort/SortFilesUseCase.kt
- Implement natural sort (handles numbers correctly)
- Implement other sort strategies

// 3. Write tests
```

---

#### ✅ CHUNK 9: File Observer - Real-time Monitoring (COMPLETED)
**Status:** 100% Complete
- ✅ Domain models (FolderMonitor, MonitoringStatus, FileEvent)
- ✅ Repository interface (FolderMonitorRepository)
- ✅ Use cases (Start, Stop, GetStatus, ObserveStatus, ObserveEvents)
- ✅ Data implementation (FolderMonitorRepositoryImpl with FileObserver)
- ✅ DI module (MonitoringDataModule)
- ✅ Service shell (MonitoringService - Sokchea needs to implement)
- ✅ Unit tests (26 tests passing)

**Note:** Service implementation and UI are Sokchea's responsibility

---

#### 🔜 CHUNK 10: Dynamic Theming from Images
**Your Tasks:**

**Tasks:**
```kotlin
// 1. Create domain model
domain/model/ImagePalette.kt
- Properties: dominantColor, vibrantColor, mutedColor

// 2. Create use case
domain/usecase/theme/ExtractPaletteUseCase.kt
- Use Android Palette API
- Extract color palette from image URI

// 3. Implement data layer
data/repository/ThemeRepository.kt
- Safe URI handling
- Persistent permissions check
- Handle image loading and processing

// 4. Add DI module
di/ThemeDataModule.kt
```

**Note:** Sokchea will handle UI (image picker, theme application, preview)

---

### **Phase 4: Smart Features**

#### 🔜 CHUNK 11: EXIF Metadata Extraction
**Your Tasks:**

**Tasks:**
```kotlin
// 1. Create domain models
domain/model/ImageMetadata.kt
- Properties: dateTaken, location, cameraModel, dimensions, orientation

// 2. Create use case
domain/usecase/metadata/ExtractMetadataUseCase.kt
- Use ExifInterface API
- Extract EXIF data from image
- Handle missing or corrupted metadata

// 3. Create metadata variable system
domain/model/MetadataVariable.kt (enum)
- Variables: DATE, LOCATION, CAMERA, WIDTH, HEIGHT

// 4. Implement data layer
data/repository/MetadataRepositoryImpl.kt
- ExifInterface parsing
- GPS coordinate formatting
- Date formatting options

// 5. Add DI module
di/MetadataDataModule.kt

// 6. Write tests
test/domain/usecase/ExtractMetadataUseCaseTest.kt
test/data/repository/MetadataRepositoryImplTest.kt
```

---

#### 🔜 CHUNK 12: Pattern Templates
**Your Tasks:**

**Tasks:**
```kotlin
// 1. Create domain models
domain/model/RenameTemplate.kt
- Properties: id, name, pattern, config, isFavorite, createdAt

// 2. Create repository interface
domain/repository/TemplateRepository.kt
- suspend fun saveTemplate(template: RenameTemplate): Result<Unit>
- suspend fun getTemplates(): Result<List<RenameTemplate>>
- suspend fun deleteTemplate(id: String): Result<Unit>
- fun observeTemplates(): Flow<List<RenameTemplate>>

// 3. Create use cases
domain/usecase/template/SaveTemplateUseCase.kt
domain/usecase/template/GetTemplatesUseCase.kt
domain/usecase/template/DeleteTemplateUseCase.kt
domain/usecase/template/ObserveTemplatesUseCase.kt

// 4. Implement Room database
data/local/dao/TemplateDao.kt
data/local/entity/TemplateEntity.kt
- Define @Entity with proper schema
- Create @Dao with CRUD operations

// 5. Implement repository
data/repository/TemplateRepositoryImpl.kt
- Map between Entity and Domain models
- Handle database operations

// 6. Add DI module
di/TemplateDataModule.kt

// 7. Write tests
test/data/local/dao/TemplateDaoTest.kt
test/data/repository/TemplateRepositoryImplTest.kt
```

---

#### 🔜 CHUNK 13: AI-Powered Filename Suggestions
**Your Tasks:**

**Tasks:**
```kotlin
// 1. Create domain models
domain/model/ImageLabel.kt
- Properties: text, confidence, category

// 2. Create use case
domain/usecase/ai/AnalyzeImageUseCase.kt
- Use ML Kit Image Labeling
- Return top N labels
- Filter by confidence threshold

// 3. Create suggestion use case
domain/usecase/ai/GenerateSuggestionsUseCase.kt
- Input: List<ImageLabel>
- Output: List<String> (suggested filenames)
- Combine labels into meaningful names

// 4. Implement data layer
data/repository/MLRepositoryImpl.kt
- ML Kit initialization
- Image labeling lifecycle
- Error handling for ML failures

// 5. Add DI module
di/MLDataModule.kt

// 6. Write tests (with mocked ML Kit)
test/domain/usecase/AnalyzeImageUseCaseTest.kt
```

---

#### 🔜 CHUNK 14: Undo/Redo System
**Your Tasks:**

**Tasks:**
```kotlin
// 1. Create domain models
domain/model/RenameOperation.kt
- Properties: id, originalUri, newUri, originalName, newName, timestamp

domain/model/OperationHistory.kt
- Properties: operations, currentIndex

// 2. Create repository interface
domain/repository/HistoryRepository.kt
- suspend fun saveOperation(operation: RenameOperation): Result<Unit>
- suspend fun getHistory(): Result<List<RenameOperation>>
- suspend fun clearHistory(): Result<Unit>

// 3. Create use cases
domain/usecase/history/UndoRenameUseCase.kt
- Revert file to original name
- Update MediaStore
- Update history

domain/usecase/history/RedoRenameUseCase.kt
- Reapply rename operation
- Update MediaStore

domain/usecase/history/GetHistoryUseCase.kt

// 4. Implement Room database
data/local/dao/HistoryDao.kt
data/local/entity/OperationEntity.kt

// 5. Implement repository
data/repository/HistoryRepositoryImpl.kt
- File operations for undo/redo
- Database operations

// 6. Add DI module
di/HistoryDataModule.kt

// 7. Write tests
```

---

#### 🔜 CHUNK 15: Regex Pattern Support
**Your Tasks:**

**Tasks:**
```kotlin
// 1. Create domain models
domain/model/RegexRule.kt
- Properties: pattern, replacement, flags

domain/model/RegexPreset.kt (enum)
- Common patterns: REMOVE_SPACES, CAMEL_CASE, SNAKE_CASE, etc.

// 2. Create use case
domain/usecase/regex/ApplyRegexPatternUseCase.kt
- Apply regex to filename
- Validate regex pattern
- Handle regex errors gracefully

// 3. Create validation use case
domain/usecase/regex/ValidateRegexUseCase.kt
- Test if regex pattern is valid
- Provide error messages

// 4. Write tests
test/domain/usecase/ApplyRegexPatternUseCaseTest.kt
- Test with various regex patterns
- Test error cases (invalid regex)
```

---

#### 🔜 CHUNK 16: Tag System for Files
**Your Tasks:**

**Tasks:**
```kotlin
// 1. Create domain models
domain/model/FileTag.kt
- Properties: id, name, color, createdAt

domain/model/TaggedFile.kt
- Properties: fileUri, tags

// 2. Create repository interface
domain/repository/TagRepository.kt
- suspend fun createTag(tag: FileTag): Result<Unit>
- suspend fun getTags(): Result<List<FileTag>>
- suspend fun tagFile(fileUri: Uri, tagId: String): Result<Unit>
- suspend fun getFilesByTag(tagId: String): Result<List<FileItem>>

// 3. Create use cases
domain/usecase/tag/CreateTagUseCase.kt
domain/usecase/tag/GetTagsUseCase.kt
domain/usecase/tag/TagFileUseCase.kt
domain/usecase/tag/SearchByTagUseCase.kt

// 4. Implement Room database
data/local/dao/TagDao.kt
data/local/entity/TagEntity.kt
data/local/entity/FileTagCrossRef.kt (many-to-many)

// 5. Implement repository
data/repository/TagRepositoryImpl.kt

// 6. Add DI module
di/TagDataModule.kt

// 7. Write tests
```

---

### **Phase 5: Integration & Sync**

#### 🔜 CHUNK 17: Cloud Storage Integration
**Your Tasks:**

**Tasks:**
```kotlin
// 1. Create domain models
domain/model/CloudProvider.kt (enum)
- GOOGLE_DRIVE, DROPBOX, ONEDRIVE

domain/model/SyncConfig.kt
- Properties: provider, autoSync, syncInterval

// 2. Create repository interface
domain/repository/CloudSyncRepository.kt
- suspend fun authenticate(provider: CloudProvider): Result<Unit>
- suspend fun uploadFile(uri: Uri, path: String): Result<Unit>
- suspend fun syncFiles(files: List<FileItem>): Flow<SyncProgress>

// 3. Create use cases
domain/usecase/cloud/AuthenticateCloudUseCase.kt
domain/usecase/cloud/SyncFilesUseCase.kt

// 4. Implement data layer
data/repository/CloudSyncRepositoryImpl.kt
- Google Drive API integration
- Dropbox API integration
- OneDrive API integration
- OAuth handling

// 5. Setup WorkManager
data/worker/CloudSyncWorker.kt
- Background sync implementation
- Periodic sync scheduling

// 6. Add DI module
di/CloudDataModule.kt

// 7. Write tests (with mocked APIs)
```

---

#### 🔜 CHUNK 18: QR Code Generation for Presets
**Your Tasks:**

**Tasks:**
```kotlin
// 1. Create domain models
domain/model/PresetQRData.kt
- Serializable preset data for QR encoding

// 2. Create use cases
domain/usecase/qr/GenerateQRCodeUseCase.kt
- Serialize RenameTemplate to JSON
- Generate QR code bitmap
- Use ZXing library

domain/usecase/qr/ParseQRCodeUseCase.kt
- Decode QR to JSON
- Deserialize to RenameTemplate
- Validate imported data

// 3. Implement data layer
data/repository/QRRepositoryImpl.kt
- ZXing integration
- JSON serialization/deserialization

// 4. Add dependencies
// ZXing library in build.gradle.kts

// 5. Add DI module
di/QRDataModule.kt

// 6. Write tests
```

---

#### 🔜 CHUNK 19: OCR Integration
**Your Tasks:**

**Tasks:**
```kotlin
// 1. Create domain model
domain/model/ExtractedText.kt
- Properties: text, confidence, boundingBox

// 2. Create use case
domain/usecase/ocr/ExtractTextFromImageUseCase.kt
- Use ML Kit Text Recognition
- Extract text from image URI
- Return recognized text blocks

// 3. Implement data layer
data/repository/OCRRepositoryImpl.kt
- ML Kit Text Recognition API
- Handle recognition lifecycle
- Error handling

// 4. Add DI module
di/OCRDataModule.kt

// 5. Write tests (with mocked ML Kit)
```

---

#### 🔜 CHUNK 20: Multi-Device Sync
**Your Tasks:**

**Tasks:**
```kotlin
// 1. Create domain models
domain/model/UserPreferences.kt
- Properties: settings, templates, tags, lastSyncTimestamp

domain/model/SyncStatus.kt
- Properties: isSyncing, lastSyncTime, error

// 2. Create repository interface
domain/repository/SyncRepository.kt
- suspend fun syncPreferences(): Result<Unit>
- suspend fun uploadPreferences(prefs: UserPreferences): Result<Unit>
- suspend fun downloadPreferences(): Result<UserPreferences>
- fun observeSyncStatus(): Flow<SyncStatus>

// 3. Create use cases
domain/usecase/sync/SyncPreferencesUseCase.kt
domain/usecase/sync/ObserveSyncStatusUseCase.kt

// 4. Implement Firebase Firestore
data/repository/SyncRepositoryImpl.kt
- Firebase Authentication
- Firestore database operations
- Conflict resolution (last-write-wins)

// 5. Add DI module
di/SyncDataModule.kt

// 6. Write tests (with mocked Firebase)
```

---

#### 🔜 CHUNK 21: Activity Log & Export
**Your Tasks:**

**Tasks:**
```kotlin
// 1. Create domain models
domain/model/ActivityLog.kt
- Properties: id, action, details, timestamp, status

domain/model/ExportFormat.kt (enum)
- CSV, JSON

// 2. Create repository interface
domain/repository/ActivityRepository.kt
- suspend fun logActivity(log: ActivityLog): Result<Unit>
- suspend fun getActivityLogs(filter: LogFilter): Result<List<ActivityLog>>
- suspend fun exportLogs(format: ExportFormat): Result<Uri>

// 3. Create use cases
domain/usecase/activity/LogActivityUseCase.kt
domain/usecase/activity/GetActivityLogsUseCase.kt
domain/usecase/activity/ExportLogsUseCase.kt

// 4. Implement Room database
data/local/dao/ActivityDao.kt
data/local/entity/ActivityEntity.kt

// 5. Implement repository
data/repository/ActivityRepositoryImpl.kt
- Database operations
- CSV export logic
- JSON export logic
- File writing

// 6. Add DI module
di/ActivityDataModule.kt

// 7. Write tests
```

---

### **Phase 6: Polish & Optimization**

#### 🔜 CHUNK 22: Performance Optimization
**Your Tasks:**

**Tasks:**
```kotlin
// 1. Profiling
- Use Android Profiler for CPU, memory, network analysis
- Identify bottlenecks in file operations
- Analyze database query performance

// 2. Optimization
- Implement lazy sequences for file processing
- Optimize Flow operators (conflate, debounce)
- Add database indices for frequent queries
- Implement pagination for large file lists
- Optimize bitmap loading and caching

// 3. Memory Management
- Fix memory leaks (LeakCanary)
- Implement proper lifecycle handling
- Use WeakReference where appropriate

// 4. Benchmarking
- Create benchmark tests
- Measure against performance goals:
  * File selection: 1000+ files without jank
  * Batch processing: 100 files in < 5s
  * Memory: Peak < 150MB

// 5. Write performance tests
test/performance/FileOperationsBenchmark.kt
```

---

#### 🔜 CHUNK 23: Comprehensive Testing
**Your Tasks:**

**Tasks:**
```kotlin
// 1. Increase unit test coverage
- All use cases: 100% coverage
- All repositories: 90%+ coverage
- All managers: 90%+ coverage

// 2. Integration tests
test/integration/DatabaseIntegrationTest.kt
- Test Room database operations
- Test data consistency

test/integration/FileOperationsIntegrationTest.kt
- Test file rename operations
- Test MediaStore updates

// 3. End-to-end tests
test/e2e/RenameFlowTest.kt
- Complete user flow testing
- Real file operations (test directory)

// 4. Test utilities
test/util/TestDataFactory.kt
- Factories for test data creation

test/util/FakeRepositories.kt
- Fake implementations for testing

// 5. Run coverage report
./gradlew testDebugUnitTestCoverage
// Target: 70%+ overall coverage
```

---

#### 🔜 CHUNK 24: UI/UX Polish
**Your Tasks:**

**Note:** This is primarily Sokchea's chunk, but you may need to support with:

```kotlin
// 1. Backend support for animations
- Ensure use cases return proper states for UI transitions
- Add delay parameters for testing animations

// 2. Loading state improvements
- Add progress callbacks to long-running operations
- Implement cancellation support

// 3. Error message improvements
- Create user-friendly error messages
- Add error recovery suggestions
```

---

#### 🔜 CHUNK 25: Accessibility & i18n
**Your Tasks:**

**Tasks:**
```kotlin
// 1. String resources
- Extract all hardcoded strings to strings.xml
- Organize by feature
- Add plurals where needed

res/values/strings.xml
res/values-es/strings.xml (Spanish)
res/values-fr/strings.xml (French)
res/values-ar/strings.xml (Arabic - for RTL testing)

// 2. Multi-language support
- Use string resources in all domain error messages
- Format dates/numbers using Locale

// 3. Testing
- Test with different locales
- Verify text doesn't overflow in other languages

// 4. Write localization tests
test/localization/StringResourcesTest.kt
- Verify all strings have translations
- Check for missing string keys
```

---

#### 🔜 CHUNK 26: Documentation & Code Cleanup
**Your Tasks:**

**Tasks:**
```kotlin
// 1. KDoc comments
- Add KDoc to all public APIs in domain layer
- Add KDoc to all public APIs in data layer
- Include @param, @return, @throws tags
- Add usage examples

// 2. Architecture Decision Records
docs/adr/001-clean-architecture.md
docs/adr/002-mvi-pattern.md
docs/adr/003-repository-pattern.md
docs/adr/004-use-case-pattern.md

// 3. README updates
README.md:
- Setup instructions
- Architecture overview
- Development guidelines
- Testing guide

// 4. Code cleanup
- Run Ktlint/Detekt
- Remove all TODO comments
- Remove unused imports
- Remove commented code
- Optimize imports

// 5. Commands
./gradlew ktlintFormat
./gradlew detekt

// 6. Final review
- Review all code for consistency
- Ensure all tests pass
- Verify no lint warnings
```

---

## 🛠️ Your Tools & Setup

### Dependencies You'll Add:
```kotlin
// In build.gradle.kts or libs.versions.toml

// For testing
testImplementation("io.mockk:mockk:1.13.9")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
testImplementation("junit:junit:4.13.2")

// For file operations
// (Already in Android SDK, no extra dependencies needed)
```

### Key Android APIs You'll Use:
- `ContentResolver` - Query MediaStore
- `MediaStore.Images.Media` - Image files
- `MediaStore.Video.Media` - Video files
- `MediaStore.Files` - Update file metadata
- `DocumentFile` - Folder operations (SAF)
- `ContextCompat.checkSelfPermission()` - Permission checking
- `Environment.isExternalStorageManager()` - Android 11+ storage permission

---

## ✅ Your PR Checklist

Before creating each PR:
```
□ Code compiles without errors
□ All unit tests pass (run: ./gradlew test)
□ Added KDoc comments to all public APIs
□ Updated DI modules in designated section
□ No TODOs or commented-out code
□ Follows established patterns from CHUNK 1 & 2
□ Domain models/interfaces are stable
□ Tested with mock data if Sokchea's UI isn't ready
□ PR description explains what Sokchea can now build
□ Tagged PR with: [READY], [DATA], or [BACKEND]
□ Labeled with: `kai`, `domain`, `data`, appropriate phase
```

### PR Description Template:
```markdown
## [CHUNK X] Feature Name - Backend Implementation

### What's Implemented:
- ✅ Domain models: [list models]
- ✅ Repository interface: [interface name]
- ✅ Use cases: [list use cases]
- ✅ Data implementation: [implementation class]
- ✅ Tests: [test coverage]

### For Sokchea:
- 📦 You can now use: [list models/interfaces]
- 🎨 Start working on: [UI components]
- 📝 See usage example: [code snippet or test]

### Testing:
- Unit tests: [X] passing
- Integration tests: [status]

### Notes:
[Any important notes or gotchas]
```

---

## 🤝 Communication with Sokchea

### Morning Standup (Async):
```
Template:
"Morning! 🌅
Yesterday: Completed [feature]
Today: Working on [feature]
Files I'll touch: [list files]
Blockers: None / [describe if any]
@Sokchea - [any message for her]"

Example:
"Morning! 🌅
Yesterday: Completed PermissionsManagerImpl, merged to main
Today: Starting FileItem models and MediaRepository interface
Files I'll touch: domain/model/, domain/repository/MediaRepository.kt
Blockers: None
@Sokchea - Domain models will be ready by lunch, you can start FileSelectionViewModel after!"
```

### When You Merge to Main:
```
1. Create PR with [READY] tag if Sokchea needs it
2. Tag her in PR: "@Sokchea - Domain layer ready for CHUNK X"
3. Message on Discord/Slack: "Merged! You can pull and start UI now"
4. Be available for questions about the APIs you created
```

### If You're Ahead:
```
✅ Keep going! Start the next chunk's domain layer
✅ Write extra tests
✅ Improve documentation
✅ Review Sokchea's PRs
❌ Don't modify presentation layer files
```

---

## 🐛 Testing Strategy

### Unit Tests (Your Responsibility):
```kotlin
// For every use case
CheckPermissionsUseCaseTest.kt
GetMediaFilesUseCaseTest.kt
GenerateFilenameUseCaseTest.kt

// For every repository implementation
PermissionsManagerImplTest.kt
MediaRepositoryImplTest.kt
FileRenameRepositoryImplTest.kt

// For data managers
FileOperationsManagerTest.kt
```

### Test Pattern:
```kotlin
class FeatureUseCaseTest {
    private lateinit var repository: FeatureRepository
    private lateinit var useCase: FeatureUseCase
    
    @Before
    fun setup() {
        repository = mockk()
        useCase = FeatureUseCase(repository, Dispatchers.Unconfined)
    }
    
    @Test
    fun `test success case`() = runTest {
        // Given
        every { repository.getData() } returns expectedData
        
        // When
        val result = useCase()
        
        // Then
        assertTrue(result is Result.Success)
        assertEquals(expectedData, result.getOrNull())
    }
    
    @Test
    fun `test error case`() = runTest {
        // Given
        every { repository.getData() } throws Exception("Error")
        
        // When
        val result = useCase()
        
        // Then
        assertTrue(result is Result.Error)
    }
}
```

---

## 🚀 Quick Command Reference

```bash
# Build the project
./gradlew build

# Run unit tests
./gradlew test

# Run specific test
./gradlew test --tests PermissionsManagerImplTest

# Check code style
./gradlew ktlintCheck

# Format code
./gradlew ktlintFormat

# Clean build
./gradlew clean build

# Create branch
git checkout -b feature/chunk-X-backend

# Rebase on main
git checkout kai-dev
git pull origin main --rebase
git checkout feature/chunk-X-backend
git rebase kai-dev

# Push and create PR
git push origin feature/chunk-X-backend
```

---

## 📚 Resources

### Code References:
- CHUNK 1: Base classes in `domain/usecase/base/`, `presentation/base/`
- CHUNK 2: Permission system as complete example
- Clean Architecture: Domain → Data → Presentation

### Android Documentation:
- [MediaStore](https://developer.android.com/training/data-storage/shared/media)
- [Scoped Storage](https://developer.android.com/about/versions/11/privacy/storage)
- [ContentResolver](https://developer.android.com/reference/android/content/ContentResolver)
- [Permissions](https://developer.android.com/training/permissions/requesting)

---

## 💡 Tips for Success

1. **Domain first, always:** Models → Interfaces → Use cases → Implementation
2. **Make APIs stable:** Once Sokchea starts using your models, avoid breaking changes
3. **Document everything:** Future you will thank present you
4. **Test thoroughly:** Your tests prevent integration issues
5. **Communicate early:** Let Sokchea know when domain is ready
6. **Separate DI modules:** Avoid merge conflicts in DataModule.kt
7. **Use feature branches:** One branch per chunk
8. **Commit frequently:** Small commits are easier to review
9. **Be responsive:** Answer Sokchea's questions about your APIs
10. **Review her PRs:** Provide feedback on ViewModel implementations

---

**Remember:** You're building the foundation that Sokchea will build UI on top of. Make it solid! 💪

**Questions?** Check WORK_DIVISION.md or ask in team chat.

**Last Updated:** November 21, 2025

