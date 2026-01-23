# Files Management Service - Architecture Model

## Three-Layer Clean Architecture + MVI Pattern

```
╔══════════════════════════════════════════════════════════════════════════════╗
║                           PRESENTATION LAYER                                 ║
║                    (Jetpack Compose UI + ViewModels)                         ║
╠══════════════════════════════════════════════════════════════════════════════╣
║                                                                              ║
║  ┌─────────────────────────────────────────────────────────────────────┐   ║
║  │                         UI COMPONENTS                                │   ║
║  │  • HomeScreen                    • TemplateScreen                   │   ║
║  │  • FileSelectionScreen           • MonitoringScreen                 │   ║
║  │  • RenameConfigScreen            • SettingsScreen                   │   ║
║  │  • PreviewScreen                 • HistoryScreen                    │   ║
║  │  • ProgressScreen                • CloudSyncScreen                  │   ║
║  │  • AI/OCR/QR Screens             • Theme Customization              │   ║
║  └─────────────────────────────────────────────────────────────────────┘   ║
║                                    ▲                                         ║
║                                    │ StateFlow<State>                        ║
║                                    │                                         ║
║  ┌─────────────────────────────────────────────────────────────────────┐   ║
║  │                      MVI VIEWMODELS                                  │   ║
║  │                                                                      │   ║
║  │  Contract (State/Event/Action) + ViewModel                          │   ║
║  │  ├─ HomeViewModel              ├─ TemplateViewModel                │   ║
║  │  ├─ FileSelectionViewModel     ├─ MonitoringViewModel              │   ║
║  │  ├─ RenameConfigViewModel      ├─ CloudSyncViewModel               │   ║
║  │  ├─ PreviewViewModel            ├─ HistoryViewModel                │   ║
║  │  └─ ProgressViewModel           └─ SettingsViewModel               │   ║
║  │                                                                      │   ║
║  │  • Immutable State Management                                       │   ║
║  │  • Unidirectional Data Flow                                         │   ║
║  │  • Sealed Classes for Events                                        │   ║
║  └─────────────────────────────────────────────────────────────────────┘   ║
║                                    │                                         ║
║                                    │ Use Cases                               ║
║                                    ▼                                         ║
╚══════════════════════════════════════════════════════════════════════════════╝
                                     │
                                     │
╔══════════════════════════════════════════════════════════════════════════════╗
║                              DOMAIN LAYER                                    ║
║                        (Pure Kotlin - No Android)                            ║
╠══════════════════════════════════════════════════════════════════════════════╣
║                                                                              ║
║  ┌─────────────────────────────────────────────────────────────────────┐   ║
║  │                          USE CASES                                   │   ║
║  │                   (Single Responsibility Operations)                 │   ║
║  │                                                                      │   ║
║  │  Rename:                        Template:                           │   ║
║  │  • GenerateFilenameUseCase      • SaveTemplateUseCase              │   ║
║  │  • ExecuteBatchRenameUseCase    • GetTemplatesUseCase              │   ║
║  │  • PreviewRenameUseCase         • ApplyTemplateUseCase             │   ║
║  │                                                                      │   ║
║  │  File Operations:               Monitoring:                         │   ║
║  │  • GetMediaFilesUseCase         • StartMonitoringUseCase           │   ║
║  │  • SelectFilesUseCase           • StopMonitoringUseCase            │   ║
║  │  • ConvertFormatUseCase                                            │   ║
║  │                                                                      │   ║
║  │  AI/ML:                         Cloud Sync:                         │   ║
║  │  • GenerateAISuggestionsUseCase • SyncToCloudUseCase               │   ║
║  │  • ExtractTextUseCase           • FetchCloudDataUseCase            │   ║
║  │  • AnalyzeImageUseCase                                             │   ║
║  │                                                                      │   ║
║  │  History/Activity:              Tag Management:                     │   ║
║  │  • LogActivityUseCase           • CreateTagUseCase                 │   ║
║  │  • GetHistoryUseCase            • ApplyTagsUseCase                 │   ║
║  │  • UndoOperationUseCase                                            │   ║
║  └─────────────────────────────────────────────────────────────────────┘   ║
║                                    │                                         ║
║                                    │ Result<T>                               ║
║                                    ▼                                         ║
║  ┌─────────────────────────────────────────────────────────────────────┐   ║
║  │                     DOMAIN MODELS                                    │   ║
║  │  • FileItem                     • RenameTemplate                    │   ║
║  │  • RenameConfig                 • ActivityLog                       │   ║
║  │  • FileFilter                   • Tag                               │   ║
║  │  • Permission                   • CloudFile                         │   ║
║  │  • Operation                    • MonitorConfig                     │   ║
║  └─────────────────────────────────────────────────────────────────────┘   ║
║                                    │                                         ║
║                                    │ Repository Interfaces                   ║
║                                    ▼                                         ║
║  ┌─────────────────────────────────────────────────────────────────────┐   ║
║  │                  REPOSITORY INTERFACES                               │   ║
║  │  • MediaRepository              • TemplateRepository                │   ║
║  │  • PermissionsRepository        • TagRepository                     │   ║
║  │  • HistoryRepository            • CloudSyncRepository               │   ║
║  │  • MonitoringRepository         • ActivityRepository                │   ║
║  └─────────────────────────────────────────────────────────────────────┘   ║
║                                                                              ║
║  ┌─────────────────────────────────────────────────────────────────────┐   ║
║  │                         COMMON                                       │   ║
║  │  • Result<T> (Success/Error/Loading)                                │   ║
║  │  • BaseUseCase<Input, Output>                                       │   ║
║  │  • Constants & Utilities                                            │   ║
║  └─────────────────────────────────────────────────────────────────────┘   ║
║                                                                              ║
╚══════════════════════════════════════════════════════════════════════════════╝
                                     │
                                     │ implements
                                     ▼
╔══════════════════════════════════════════════════════════════════════════════╗
║                               DATA LAYER                                     ║
║                        (Android Implementations)                             ║
╠══════════════════════════════════════════════════════════════════════════════╣
║                                                                              ║
║  ┌─────────────────────────────────────────────────────────────────────┐   ║
║  │              REPOSITORY IMPLEMENTATIONS                              │   ║
║  │  • MediaRepositoryImpl          • TemplateRepositoryImpl            │   ║
║  │  • PermissionsManagerImpl       • TagRepositoryImpl                 │   ║
║  │  • HistoryRepositoryImpl        • CloudSyncRepositoryImpl           │   ║
║  │  • MonitoringRepositoryImpl     • ActivityRepositoryImpl            │   ║
║  └─────────────────────────────────────────────────────────────────────┘   ║
║                                    │                                         ║
║                                    ▼                                         ║
║  ┌─────────────────────────────────────────────────────────────────────┐   ║
║  │                      DATA SOURCES                                    │   ║
║  │                                                                      │   ║
║  │  Local Sources:                 Remote Sources:                     │   ║
║  │  ├─ MediaStoreDataSource        ├─ FirebaseAuthDataSource          │   ║
║  │  ├─ RoomDatabase (DAOs)         ├─ FirestoreDataSource             │   ║
║  │  ├─ DataStore Preferences       ├─ FirebaseStorageDataSource       │   ║
║  │  └─ FileObserver                ├─ GoogleDriveService               │   ║
║  │                                  ├─ OneDriveService                  │   ║
║  │  Managers:                       └─ DropboxService                   │   ║
║  │  ├─ FileOperationsManager                                           │   ║
║  │  ├─ AIManager (ML Kit)                                              │   ║
║  │  └─ NotificationManager                                             │   ║
║  └─────────────────────────────────────────────────────────────────────┘   ║
║                                    │                                         ║
║                                    ▼                                         ║
║  ┌─────────────────────────────────────────────────────────────────────┐   ║
║  │                   DATABASE & ENTITIES                                │   ║
║  │                                                                      │   ║
║  │  Room Database:                 Entities & Mappers:                 │   ║
║  │  ├─ ConversionDatabase          ├─ TemplateEntity ↔ RenameTemplate │   ║
║  │  │                               ├─ TagEntity ↔ Tag                 │   ║
║  │  DAOs:                          ├─ ActivityLogEntity ↔ ActivityLog  │   ║
║  │  ├─ TemplateDao                 ├─ OperationEntity ↔ Operation     │   ║
║  │  ├─ TagDao                      └─ FolderEntity ↔ MonitorConfig    │   ║
║  │  ├─ ActivityLogDao                                                  │   ║
║  │  ├─ OperationDao                                                    │   ║
║  │  └─ MonitoringDao                                                   │   ║
║  └─────────────────────────────────────────────────────────────────────┘   ║
║                                                                              ║
╚══════════════════════════════════════════════════════════════════════════════╝
                                     │
                                     │
╔══════════════════════════════════════════════════════════════════════════════╗
║                        CROSS-CUTTING CONCERNS                                ║
╠══════════════════════════════════════════════════════════════════════════════╣
║                                                                              ║
║  ┌─────────────────────────────────────────────────────────────────────┐   ║
║  │              DEPENDENCY INJECTION (Hilt)                             │   ║
║  │  19 Specialized Modules:                                            │   ║
║  │  • DataModule, RepositoryModule, UseCaseModule                      │   ║
║  │  • TemplateModule, RenameModule, TagModule                          │   ║
║  │  • MonitoringModule, CloudModule, AIModule                          │   ║
║  │  • NetworkModule, DatabaseModule, PreferencesModule                 │   ║
║  │                                                                      │   ║
║  │  Qualifiers: @IoDispatcher, @MainDispatcher, @DefaultDispatcher     │   ║
║  └─────────────────────────────────────────────────────────────────────┘   ║
║                                                                              ║
║  ┌─────────────────────────────────────────────────────────────────────┐   ║
║  │                   BACKGROUND PROCESSING                              │   ║
║  │  • WorkManager (Scheduled Tasks)                                    │   ║
║  │  • Foreground Services (Long Operations)                            │   ║
║  │  • FileObserver (Real-time Monitoring)                              │   ║
║  │  • Coroutines (Async Operations)                                    │   ║
║  └─────────────────────────────────────────────────────────────────────┘   ║
║                                                                              ║
║  ┌─────────────────────────────────────────────────────────────────────┐   ║
║  │                      NAVIGATION                                      │   ║
║  │  • Compose Navigation (Type-safe Routes)                            │   ║
║  │  • Bottom Navigation (Home/History/Cloud/Settings)                  │   ║
║  │  • Navigation Drawer (Feature Access)                               │   ║
║  │  • Deep Links (Notifications, Widgets)                              │   ║
║  │  • savedStateHandle (Data Passing)                                  │   ║
║  └─────────────────────────────────────────────────────────────────────┘   ║
║                                                                              ║
╚══════════════════════════════════════════════════════════════════════════════╝
```

## Key Architectural Principles

### 1. **Dependency Rule**
```
Presentation ───→ Domain ←─── Data
     ↓                           ↑
  depends on              implements
```
- **Domain layer**: Pure Kotlin, no Android dependencies
- **Data layer**: Implements domain interfaces
- **Presentation layer**: Uses domain models and use cases

### 2. **MVI Pattern Flow**
```
User Action → Event → ViewModel → Use Case → Repository → Data Source
                ↓                                              ↓
            State Update ←─────────── Result<T> ←─────────────┘
                ↓
            UI Update
```

### 3. **Result Wrapper Pattern**
All operations return `Result<T>`:
- **Result.Success<T>**: Operation succeeded with data
- **Result.Error**: Operation failed with exception and message
- **Result.Loading**: Operation in progress

### 4. **Single Responsibility**
- **Use Cases**: One action per class (e.g., `SaveTemplateUseCase`)
- **ViewModels**: One screen per ViewModel
- **Repositories**: One data domain per repository

### 5. **Testability**
- **Domain layer**: 100% unit testable (no Android)
- **ViewModels**: Tested with fakes/mocks
- **UI**: Compose testing with Semantics

## Technology Stack

| Layer | Technologies |
|-------|-------------|
| **Presentation** | Jetpack Compose, Material 3, Coil, Navigation Compose |
| **Domain** | Pure Kotlin, Coroutines, Flow |
| **Data** | Room, DataStore, MediaStore, Firebase (Auth, Firestore, Storage) |
| **DI** | Hilt |
| **AI/ML** | ML Kit (Image Labeling, OCR, Barcode Scanning) |
| **Background** | WorkManager, Foreground Services, FileObserver |
| **Testing** | JUnit 5, MockK, Turbine, Compose Testing |
| **Quality** | Detekt, KtLint |

## Build Configuration

- **Language**: Kotlin 2.0.21
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34
- **Build System**: Gradle 8.10 (Kotlin DSL)
- **Code Generation**: KSP 2.0.21-1.0.25

## Key Features Architecture

### Batch Rename Flow
```
Home → File Selection → Rename Config → Preview → Progress → Results
           ↓                 ↓              ↓          ↓
      MediaStore      Pattern Logic    Conflict    File I/O
```

### Template System
```
Template Screen → Save/Load → Room DB → Apply to Files
                     ↓
              Shared across devices (Firebase)
```

### Real-time Monitoring
```
Folder Select → Start Monitoring → FileObserver → Auto-process → Activity Log
                                        ↓
                                 WorkManager (Background)
```

### Cloud Sync
```
Local Operation → Upload to Cloud → Sync across Devices
                       ↓
          Firebase Storage + Firestore
```

---

**Architecture Version**: 1.0  
**Last Updated**: January 23, 2026  
**Team**: Kai (Backend/Domain/Data), Sokchea (Frontend/Presentation/UI)
