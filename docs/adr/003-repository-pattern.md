# ADR 003: Repository Pattern Implementation

**Date:** December 8, 2025  
**Status:** Accepted  
**Deciders:** Kai (Backend)

## Context

The app needs to access data from multiple sources:
- MediaStore (media files)
- Room Database (templates, history, tags)
- SharedPreferences (user settings)
- File System (folder operations)
- Cloud APIs (sync, storage)

We need a consistent abstraction layer that:
- Hides data source complexity from UI
- Provides single source of truth
- Enables easy testing with fakes
- Supports offline-first architecture

## Decision

We will implement the **Repository Pattern** with:
- **Interface in Domain Layer**: Defines contract
- **Implementation in Data Layer**: Handles actual data operations
- **Result Wrapper**: Type-safe error handling
- **Flow for Reactive Data**: Real-time updates when data changes

## Architecture

```kotlin
// 1. Domain Interface (domain/repository/)
interface MediaRepository {
    suspend fun getMediaFiles(filter: FileFilter): Result<List<FileItem>>
    suspend fun getFilesByFolder(folderPath: String): Result<List<FileItem>>
    fun observeMediaFiles(): Flow<List<FileItem>>
}

// 2. Data Implementation (data/repository/)
class MediaRepositoryImpl @Inject constructor(
    private val mediaStoreDataSource: MediaStoreDataSource,
    private val contentResolver: ContentResolver,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : MediaRepository {
    
    override suspend fun getMediaFiles(filter: FileFilter): Result<List<FileItem>> =
        withContext(ioDispatcher) {
            try {
                val files = mediaStoreDataSource.queryMediaFiles(filter)
                Result.Success(files)
            } catch (e: Exception) {
                Result.Error(Exception("Failed to load media files", e))
            }
        }
    
    override fun observeMediaFiles(): Flow<List<FileItem>> =
        contentResolver.observeMediaStoreChanges()
            .map { getMediaFiles(FileFilter.DEFAULT).getOrNull() ?: emptyList() }
            .flowOn(ioDispatcher)
}

// 3. Dependency Injection (di/)
@Module
@InstallIn(SingletonComponent::class)
abstract class MediaDataModule {
    @Binds
    @Singleton
    abstract fun bindMediaRepository(
        impl: MediaRepositoryImpl
    ): MediaRepository
}
```

## Benefits

✅ **Single Source of Truth**: All data access goes through repository  
✅ **Testability**: Easy to create fake implementations for tests  
✅ **Abstraction**: UI doesn't know if data comes from DB, network, or cache  
✅ **Flexibility**: Can change data source without affecting UI  
✅ **Consistency**: Uniform error handling across all data operations

## Implementation Patterns

### 1. Basic CRUD Operations
```kotlin
interface TemplateRepository {
    suspend fun saveTemplate(template: RenameTemplate): Result<Unit>
    suspend fun getTemplateById(id: String): Result<RenameTemplate>
    suspend fun deleteTemplate(id: String): Result<Unit>
    fun observeTemplates(): Flow<List<RenameTemplate>>
}
```

### 2. Error Handling
```kotlin
override suspend fun getFilesByFolder(folderPath: String): Result<List<FileItem>> =
    withContext(ioDispatcher) {
        try {
            // Validate input
            if (!File(folderPath).exists()) {
                return@withContext Result.Error(
                    Exception("Folder does not exist: $folderPath")
                )
            }
            
            // Perform operation
            val files = mediaStoreDataSource.queryFolder(folderPath)
            Result.Success(files)
            
        } catch (e: SecurityException) {
            Result.Error(Exception("Permission denied", e))
        } catch (e: Exception) {
            Result.Error(Exception("Failed to load files", e))
        }
    }
```

### 3. Reactive Data with Flow
```kotlin
override fun observeTemplates(): Flow<List<RenameTemplate>> =
    templateDao.observeAll()
        .map { entities -> entities.map { it.toDomain() } }
        .flowOn(ioDispatcher)
```

### 4. Caching Strategy (When Needed)
```kotlin
class MediaRepositoryImpl @Inject constructor(...) : MediaRepository {
    
    private val cache = MutableStateFlow<List<FileItem>>(emptyList())
    private var lastFetchTime = 0L
    private val cacheDuration = 5000L // 5 seconds
    
    override suspend fun getMediaFiles(filter: FileFilter): Result<List<FileItem>> =
        withContext(ioDispatcher) {
            // Check cache
            val now = System.currentTimeMillis()
            if (now - lastFetchTime < cacheDuration && cache.value.isNotEmpty()) {
                return@withContext Result.Success(cache.value)
            }
            
            // Fetch fresh data
            try {
                val files = mediaStoreDataSource.queryMediaFiles(filter)
                cache.value = files
                lastFetchTime = now
                Result.Success(files)
            } catch (e: Exception) {
                Result.Error(Exception("Failed to load media files", e))
            }
        }
}
```

## Repository Types in Our App

### 1. **Local Data Repositories**
- `MediaRepository`: MediaStore queries
- `FolderRepository`: File system operations
- `TemplateRepository`: Room database (templates)
- `HistoryRepository`: Room database (rename history)
- `TagRepository`: Room database (file tags)
- `ActivityRepository`: Room database (activity logs)

### 2. **System Repositories**
- `PermissionsRepository`: Android permissions
- `PreferencesRepository`: SharedPreferences/DataStore

### 3. **Feature Repositories**
- `FileRenameRepository`: File rename operations
- `FolderMonitorRepository`: FileObserver for monitoring
- `MetadataRepository`: EXIF data extraction

### 4. **Integration Repositories**
- `MLRepository`: ML Kit integration
- `OCRRepository`: Text recognition
- `QRRepository`: QR code generation/parsing
- `SyncRepository`: Multi-device sync
- `ThemeRepository`: Dynamic color extraction

## Testing Strategy

```kotlin
// 1. Create Fake Repository for Tests
class FakeMediaRepository : MediaRepository {
    private val files = mutableListOf<FileItem>()
    private val filesFlow = MutableStateFlow<List<FileItem>>(emptyList())
    
    fun setFiles(newFiles: List<FileItem>) {
        files.clear()
        files.addAll(newFiles)
        filesFlow.value = newFiles
    }
    
    override suspend fun getMediaFiles(filter: FileFilter): Result<List<FileItem>> {
        return Result.Success(files.filter { filter.matches(it) })
    }
    
    override fun observeMediaFiles(): Flow<List<FileItem>> = filesFlow
}

// 2. Use in ViewModel Tests
class FileSelectionViewModelTest {
    private lateinit var fakeRepository: FakeMediaRepository
    private lateinit var viewModel: FileSelectionViewModel
    
    @Before
    fun setup() {
        fakeRepository = FakeMediaRepository()
        val useCase = GetMediaFilesUseCase(fakeRepository)
        viewModel = FileSelectionViewModel(useCase)
    }
    
    @Test
    fun `loading files updates state`() = runTest {
        val testFiles = listOf(FileItem(id = 1, name = "test.jpg", ...))
        fakeRepository.setFiles(testFiles)
        
        viewModel.onEvent(FileSelectionEvent.LoadFiles(FileFilter.DEFAULT))
        
        assertEquals(testFiles, viewModel.state.value.files)
    }
}
```

## Trade-offs

### Benefits
- ✅ Clear separation between data and business logic
- ✅ Easy to switch implementations (mock → real)
- ✅ Consistent error handling
- ✅ Testable without Android dependencies

### Costs
- ⚠️ Additional interface layer (more files)
- ⚠️ Need to maintain mapping between entities and models
- ✅ Worth it for maintainability and testability

## Related ADRs
- ADR 001: Clean Architecture
- ADR 004: Use Case Pattern

## References
- [Repository Pattern Guide](https://developer.android.com/codelabs/basic-android-kotlin-training-repository-pattern)
- [Martin Fowler: Repository Pattern](https://martinfowler.com/eaaCatalog/repository.html)
