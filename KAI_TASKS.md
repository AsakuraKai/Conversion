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

#### 🔜 CHUNK 3: File Selection Feature (2-3 hours)
**Your Tasks:**

**Day 1 Morning (1 hour):**
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

**Day 1 Afternoon - Day 2 (1.5-2 hours):**
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

**Estimate:** 2-3 hours total

---

#### 🔜 CHUNK 4: Batch Rename Logic Core (2-3 hours)
**Your Tasks:**

**Day 1 (1.5 hours):**
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

**Day 2 (1 hour):**
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

**Estimate:** 2-3 hours total

---

#### 🔜 CHUNK 5: Rename Execution (2 hours)
**Your Tasks:**

**Day 1 (2 hours):**
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

**Estimate:** 2 hours total

---

#### 🔜 CHUNK 6: Destination Folder Selector (1-2 hours)
**Your Tasks:**

**Day 1 (1-2 hours):**
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

**Estimate:** 1-2 hours total

---

### **Phase 3: Advanced Features**

#### 🔜 CHUNK 7: Preview System (1 hour)
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

**Estimate:** 1 hour

---

#### 🔜 CHUNK 8: Natural Sorting (1 hour)
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

**Estimate:** 1 hour

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
