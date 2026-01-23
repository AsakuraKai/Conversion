# Files Management Service - AI Coding Agent Guide

## Project Overview
Android file management app built with **Clean Architecture** + **MVI** pattern for intelligent batch renaming with AI/ML features, cloud sync, and real-time monitoring. Targets Android 13+ (minSdk 26).

## 🎯 Current Focus: UI Navigation System Overhaul (Phase 2)

**Status**: Implementing comprehensive navigation framework for 21+ screens  
**Goal**: Wire all screens with consistent navigation patterns, savedStateHandle data passing, and permission gating  
**Documentation**: [UI_NAVIGATION_SYSTEM_OVERHAUL.md](docs/UI-Overhaul-v1.0/ROADMAP/UI_NAVIGATION_SYSTEM_OVERHAUL.md)

### Screen Implementation Status
- ✅ **Wired (11)**: HomeScreen, FileSelectionScreen, RenameConfigScreen, PreviewScreen, RenameProgressScreen, FolderSelectorScreen, SettingsScreen, MonitoringScreen, TagManagementScreen, TemplateScreen, QRScannerScreen
- 🔄 **Pending (9)**: HistoryScreen, AISuggestionsScreen, RegexBuilderScreen, MetadataPickerScreen, CloudSyncScreen, AccountScreen, ActivityLogScreen, QRDisplayScreen, OCRScreen
- ⏳ **Planned**: Format Converter, Image Optimization, Advanced Search, Template Store, QR History, Backup/Restore dialogs

### Navigation Hierarchy
```
Root Navigation:
├─ HOME (default landing, quick actions, feature discovery)
├─ FILE BROWSER (persistent file navigation via drawer)
├─ SETTINGS (preferences and management)
└─ NOTIFICATIONS (operation feedback)

Primary User Journeys:
1. Batch Rename: Home → File Selection → Config (+ Helper Tools) → Preview → Progress → Results
2. Folder Monitoring: Home → Folder Select → Config → Monitoring → Activity Log
3. Template Reuse: Home → Template Select → File Select → Preview → Results
```

### Navigation Patterns in Use
1. **Linear Workflow**: Batch rename, format conversion (max 5 screens, clear back stack on completion)
2. **Hub & Spoke**: Settings, Home screen (central hub with multiple branches)
3. **Modal Dialog**: AI suggestions, regex builder, metadata picker (overlays, no back stack changes)
4. **Deep Link**: Notification → Results, Widget → Batch Rename

### Critical Navigation Rules
- Maximum navigation depth: 4 levels from entry point
- All screens accessible within 2-3 taps from Home
- Consistent back button behavior (pop back stack)
- Modals for helper tools (AI, Regex, Metadata, OCR)
- Full screens for workflows, Settings, browsing
- Permission-gated screens (request on open)

## Architecture Fundamentals

### Three-Layer Clean Architecture
```
presentation/ → domain/ ← data/
```

**Critical Rule**: Dependencies flow **inward only**. Domain layer has ZERO Android dependencies.

- **domain/**: Pure Kotlin business logic
  - `model/`: Domain entities (FileItem, RenameConfig, RenameTemplate, etc.)
  - `repository/`: Interface definitions only
  - `usecase/`: Single-responsibility business operations (one class per action)
  - `common/`: `Result<T>` wrapper (Success/Error/Loading), `BaseUseCase` parent

- **data/**: Android-specific implementations
  - `repository/`: Concrete repository implementations (e.g., `MediaRepositoryImpl`)
  - `local/entity/`: Room database entities with `toDomain()`/`toEntity()` mappers
  - `source/`: MediaStore, Room DAOs, SharedPreferences access

- **presentation/**: Jetpack Compose UI
  - Each feature has: `Contract` (State/Event/Action), `ViewModel`, `Screen`, `Components`
  - ViewModels use `StateFlow<State>` for unidirectional data flow

### MVI Pattern (Strict)
Every feature follows this structure:

```kotlin
// 1. Contract file defines everything
object FeatureContract {
    data class State(val data: List<Item> = emptyList(), val isLoading: Boolean = false)
    sealed class Event { data class LoadData(...) : Event() }
    sealed class Action { data class NavigateTo(...) : Action() }
}

// 2. ViewModel processes events
class FeatureViewModel @Inject constructor(...) : ViewModel() {
    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()
    
    fun onEvent(event: Event) { /* update state */ }
}

// 3. Screen observes state
@Composable
fun FeatureScreen(viewModel: FeatureViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    /* UI reacts to state changes */
}
```

**Examples**: See [RenameConfigContract.kt](app/src/main/java/com/example/conversion/presentation/renameconfig/RenameConfigContract.kt), [TemplateContract.kt](app/src/main/java/com/example/conversion/presentation/template/TemplateContract.kt)

### Dependency Injection (Hilt)
- **19 specialized DI modules** in `di/` (one per feature: `TemplateDataModule`, `RenameDataModule`, etc.)
- Repositories bound with `@Binds` in `@InstallIn(SingletonComponent::class)` modules
- Dispatchers injected via `@IoDispatcher`, `@MainDispatcher` qualifiers
- Use cases receive `CoroutineDispatcher` in constructor, extend `BaseUseCase<Input, Output>`

**Example**: [DataModule.kt](app/src/main/java/com/example/conversion/di/DataModule.kt) - prefer this pattern

## Domain Layer Patterns

### Result Wrapper
ALL data/domain operations return `Result<T>`:

```kotlin
suspend fun getTemplates(): Result<List<RenameTemplate>>

// Never throw from repositories - wrap in Result.Error
try { /* operation */ } catch (e: Exception) { 
    return Result.Error(e, "Contextual message") 
}
```

**Extensions**: `result.map { }`, `result.onSuccess { }`, `result.onError { }`, `result.getOrNull()`

### Use Case Structure
Single-responsibility classes that inherit `BaseUseCase`:

```kotlin
class SaveTemplateUseCase @Inject constructor(
    private val templateRepository: TemplateRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<RenameTemplate, Unit>(dispatcher) {
    override suspend fun execute(params: RenameTemplate): Unit {
        // Validation here
        return when (val result = templateRepository.saveTemplate(params)) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
            is Result.Loading -> throw IllegalStateException("Unexpected loading")
        }
    }
}
```

**Browse**: `domain/usecase/template/`, `domain/usecase/rename/`, `domain/usecase/tag/` for examples

## Data Layer Patterns

### Entity Mapping (CRITICAL)
Domain models and database entities are **separate**. Entities live in `data/local/entity/`:

```kotlin
@Entity(tableName = "rename_templates")
data class TemplateEntity(/* Room annotations */)

// Mapping extensions in same file
fun TemplateEntity.toDomain(): RenameTemplate = RenameTemplate(...)
fun RenameTemplate.toEntity(): TemplateEntity = TemplateEntity(...)
```

**Known Inconsistency**: Some use `companion object fromDomain()`, others use extension functions. Prefer extensions for new code.

### Repository Implementation
```kotlin
class TemplateRepositoryImpl @Inject constructor(
    private val templateDao: TemplateDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : TemplateRepository {
    override suspend fun saveTemplate(template: RenameTemplate): Result<Unit> =
        withContext(dispatcher) {
            resultOf { templateDao.insert(template.toEntity()) }
        }
}
```

## Presentation Layer Patterns

### State Management
- **Immutable state**: `data class State(...)`
- **Computed properties**: Add helpers like `val canProceed: Boolean get() = ...` to State
- **State updates**: Use `_state.update { it.copy(newField = value) }`
- **One-time events**: Use `Action` sealed class with `SharedFlow` for navigation/toasts

### Compose UI Standards
- Material 3 theming, dynamic colors enabled
- Use `collectAsStateWithLifecycle()` for flows
- Prefer `LazyColumn` with `key = { item.id }` for lists
- Coil for image loading: `AsyncImage(model = uri, ...)`
- Accompanist for permissions: `rememberPermissionState()`

### Navigation & Data Passing
All navigation handled through Compose Navigation with type-safe routes.

**savedStateHandle Pattern** (for data passing between screens):
```kotlin
// Parent screen sets data
navBackStackEntry.savedStateHandle["result_key"] = data

// Child screen observes
LaunchedEffect(Unit) {
    navBackStackEntry.savedStateHandle
        .getStateFlow<ResultType?>("result_key", null)
        .collect { result ->
            if (result != null) {
                // Use result
                navBackStackEntry.savedStateHandle.remove("result_key")
            }
        }
}
```

**Navigation Components**:
- Bottom Navigation Bar: Home, History, Cloud, Settings (always visible)
- Navigation Drawer: Primary/secondary features, slide from left
- Top App Bar: Menu icon, screen title, action buttons
- FAB: Context-specific primary action (e.g., "New Batch Rename" on Home)

**Permission-Gated Navigation**:
```kotlin
// Check permissions before navigation
if (hasPermission) navigateToScreen()
else showPermissionRequest()
```

Common permission gates:
- File Selection: `READ_MEDIA_IMAGES` (Android 13+) or `READ_EXTERNAL_STORAGE`
- Folder Monitoring: `MANAGE_EXTERNAL_STORAGE`
- QR Scanner: `CAMERA`
- Cloud Sync: `INTERNET`

## Tech Stack & Dependencies

**Core**: Kotlin 2.0.21, Compose, Hilt, Coroutines, StateFlow  
**Storage**: Room 2.6.1, DataStore Preferences  
**Firebase**: Auth, Firestore, Storage (via BOM)  
**ML**: ML Kit (Image Labeling, Text Recognition, Barcode Scanning)  
**Build**: Gradle 8.10.0, KSP 2.0.21-1.0.25

**Key libs**: Navigation Compose, Coil, Kotlinx Serialization, ExifInterface, Accompanist Permissions, WorkManager

## Testing Standards

### Test Data Factory
Use `TestDataFactory` for consistent test data:

```kotlin
val file = TestDataFactory.createFileItem(name = "test.jpg")
val config = TestDataFactory.createRenameConfig(prefix = "IMG")
```

**Location**: [app/src/test/java/com/example/conversion/util/TestDataFactory.kt](app/src/test/java/com/example/conversion/util/TestDataFactory.kt)

### Testing Structure
- Unit tests: `app/src/test/` (domain/use cases, ViewModels)
- Instrumented tests: `app/src/androidTest/` (UI, E2E flows)
- Fakes: Use `FakeRepositories.kt` for repository testing
- Libraries: JUnit, MockK, Turbine (Flow testing), Coroutines Test

## Build & Development

### Commands
```bash
./gradlew build              # Build project
./gradlew test               # Run unit tests
./gradlew connectedAndroidTest  # Run instrumented tests
./gradlew detekt             # Run static analysis
```

### Code Quality
- **Detekt** configured in `config/detekt.yml`
  - Max method length: 60 lines
  - Max class size: 600 lines
  - Enforces dispatcher injection in coroutines
- **KtLint** rules active (via Detekt)
- No experimental APIs without `@OptIn`

## Team Workflow

**Kai (Backend)**: Domain models, repositories, use cases, file operations  
**Sokchea (Frontend)**: Compose UI, ViewModels, state management, animations

**Parallel Work Strategy**: Domain interfaces defined first → Backend implements data layer while Frontend builds UI with contracts

**See**: [docs/Division/WORK_DIVISION.md](docs/Division/WORK_DIVISION.md) for task breakdown

## Critical Gotchas

1. **Never** import Android classes in `domain/` - keep it pure Kotlin
2. **Always** use `Result<T>` wrapper for domain/data operations
3. **ViewModel events** must be sealed classes, not lambdas
4. **Entity mapping** is manual - no automatic conversion (see `BACKEND_FRONTEND_CLEANUP_PLAN.md`)
5. **Permissions** are version-specific (Android 13+ uses granular media permissions)
6. **MediaStore** access must use ContentResolver with proper URI handling
7. **Hilt modules** must have `@InstallIn(SingletonComponent::class)` for app-wide dependencies

## Key Documentation

- **ADRs**: [docs/adr/](docs/adr/) - Architecture decisions (Clean Architecture, MVI, Repository pattern)
- **Work Division**: [docs/Division/WORK_DIVISION.md](docs/Division/WORK_DIVISION.md)
- **Project Overview**: [docs/README.md](docs/README.md)
- **UI Guidelines**: [docs/UI_GUIDELINES.md](docs/UI_GUIDELINES.md)
- **Cleanup Plan**: [BACKEND_FRONTEND_CLEANUP_PLAN.md](BACKEND_FRONTEND_CLEANUP_PLAN.md) - Known duplication issues

## When Adding New Features

1. **Domain first**: Create models in `domain/model/`, repository interface in `domain/repository/`
2. **Use case**: Implement single-responsibility use case extending `BaseUseCase`
3. **Data layer**: Create Room entity (if needed), implement repository in `data/repository/`
4. **DI module**: Create or update module in `di/` binding repository implementation
5. **Presentation**: Create Contract (State/Event/Action), ViewModel, Screen, Components
6. **Tests**: Add unit tests for use cases, UI tests for screens

Follow existing patterns - grep for similar features as reference before creating new structures.
