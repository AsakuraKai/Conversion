# Auto Rename File Service (Optimized Rebuild)

An Android application that automatically renames files in monitored directories using sequential numbering.

**Status:** 🚧 In Development - Rebuilding from scratch with modern optimization practices

## Project Goals

This is a complete rebuild of an existing Auto Rename File Service app, reimagined with:
- **Performance optimization**: Efficient memory usage, lazy loading, and proper coroutine management
- **Modern architecture**: Clean Architecture with MVI/MVVM, Jetpack Compose, and Kotlin coroutines
- **Code quality**: Modular design, dependency injection (Hilt/Koin), and comprehensive testing
- **User experience**: Smooth animations, responsive UI, and proper error handling
- **Best practices**: Latest Android guidelines, Material 3 design, and optimized build configuration

## Summary

Auto Rename File Service helps you quickly batch-rename selected media files with clean, sequential names. You pick the files, configure numbering (start value, digit count, optional prefix), preview the results, and then apply the changes while keeping each file's original extension. The app supports Android's scoped storage and media scanning, and can also watch folders in real time using FileObserver.

- Batch-rename images/videos with sequential numbering and optional prefix
- Preserve file extensions and (optionally) the original selection order
- Choose a destination folder and preview filenames before committing or automatically create a new folder in the folder which contain the files
- Android 10+ compatible (scoped storage) with MediaStore updates for proper indexing
- Optional real-time monitoring via FileObserver
- Light/dark theme with customizable background

## Optimization Targets

### Performance
- **Memory efficiency**: Use of Sequences for large collections, proper Bitmap recycling, LRU caching strategies
- **Coroutine optimization**: Structured concurrency, Flow for reactive streams, proper dispatcher selection
- **Lazy initialization**: Viewmodels, dependencies, and resources loaded only when needed
- **RecyclerView optimization**: ViewHolder pattern, DiffUtil for efficient updates, pagination for large lists
- **Image loading**: Coil/Glide with proper sizing, caching, and placeholder strategies

### Architecture
- **Clean Architecture**: Domain, Data, and Presentation layers with clear separation
- **Dependency Injection**: Hilt for compile-time DI, scoped dependencies, and testability
- **State management**: MVI/MVVM with sealed classes, immutable state, and unidirectional data flow
- **Repository pattern**: Single source of truth, offline-first capability, proper error handling
- **Use cases**: Single responsibility, reusable business logic encapsulation
- **Modularization**: Feature modules (BatchProcessor, FolderScanner, UriPathResolver, FileOperationsManager, PermissionsManager)

### Code Quality
- **Modularization**: Feature modules, shared core module, proper dependency graphs
- **Testing**: Unit tests with MockK, UI tests with Compose Testing, integration tests
- **Code style**: Ktlint/Detekt for consistent formatting and static analysis
- **Documentation**: KDoc comments, architecture decision records (ADRs)
- **Error handling**: Sealed Result types, proper exception handling, user-friendly error messages
- **Utilities**: SafeImageLoader for robust URI handling, proper resource management

### Build Optimization
- **Build speed**: Gradle configuration cache, parallel execution, build cache optimization
- **APK size**: R8/ProGuard optimization, resource shrinking, vector drawables
- **Build variants**: Proper flavor dimensions, BuildConfig fields, product flavors
- **Dependencies**: Version catalogs, dependency analysis, avoiding over-fetching

## Features (Planned)

### Batch file renaming
- Select multiple media files from device storage or scan a folder (images/videos supported)
- Choose a destination folder (source vs destination selections are kept independent)
- Numbering options: start number, digit count, and optional custom prefix
- Natural sorting and optional preservation of original selection order
- Preview new filenames before processing and maintain original file extensions
- MediaStore refresh so renamed files immediately appear correctly in gallery/apps

### Dynamic theme and universal background
- Image-based dynamic theming: extracts a cohesive color palette from your selected background image (Palette API)
- **Integrated into Settings**: All theme customization (mode, dynamic colors, image-based theming) consolidated in Settings
- Automatic light/dark adaptation, status/navigation bar coloring, and dynamic button styles
- Universal background wallpaper shown consistently across Main screen, Batch Processing, and supported fragments
- Safe background loading for Google Photos and other content providers (no SecurityException crashes)
- Persistent URI permissions so background images survive app restarts
- Automatic theme propagation across activities without manual app restart
- Follows device theme by default; all theme controls are organized under Settings

### Navigation and modules
- Type-safe navigation with Kotlin serialization
- Permission-aware navigation: all file-access screens wrapped with PermissionHandler
- Batch Processing screen for renaming workflows (with permission checks)
- Settings: Centralized hub for theme, dynamic colors, image-based theming, and permission management
- Format Converter (prototype): UI prepared for converting images, documents, audio, and video; includes merge operations UI
- Book Reader (prototype): UI shell for opening PDFs/EPUBs/TXT with planned bookmarks, notes, search, and night mode

### Smart renaming capabilities
- **AI-powered naming**: Use on-device ML Kit to analyze image content and suggest descriptive filenames
- **Pattern templates**: Save and reuse custom naming patterns (e.g., `{date}_{prefix}_{counter}`)
- **Metadata extraction**: Use EXIF data for photos (date, location, camera model) in filename
- **Regex support**: Advanced users can use regex patterns for complex renaming rules
- **Undo/Redo**: Keep history of recent rename operations with ability to revert
- **Duplicate detection**: Smart handling of duplicate names with auto-increment or merge options
- **Batch operations**: Multiple rename rules applied in sequence (prefix → numbering → suffix)

### File organization and management
- **Smart folders**: Auto-sort files into folders by date, type, size, or custom rules
- **Tag system**: Add tags to files for better organization and quick filtering
- **File compression**: Batch compress images/videos with quality presets before renaming
- **Cloud sync integration**: Sync renamed files to Google Drive, Dropbox, or OneDrive
- **Scheduled tasks**: Set up automatic renaming rules that run at specific times
- **File statistics**: View detailed analytics (total files processed, storage saved, etc.)
- **Search and filter**: Quick search through renamed files with advanced filters

### Advanced features
- **QR code generation**: Generate QR codes for file links or metadata
- **OCR integration**: Extract text from images and use in filenames (ML Kit)
- **Audio transcription**: Convert voice notes to text for filename generation
<!-- - **Barcode scanning**: Scan product barcodes to auto-name product photos -->
- **GPS location names**: Convert coordinates to place names for travel photos
### Collaboration and sharing
- **Preset sharing**: Export/import rename presets with other users via QR or file
- **Batch share**: Share multiple renamed files directly from the app
- **Before/after gallery**: Visual comparison of original vs renamed files
- **Activity log**: Detailed log of all operations with export functionality
- **Multi-device sync**: Sync settings and presets across devices(google account or others, optional to login/signup)
- **Collaborative folders**: Share monitored folders with team members (Firebase)

### Compatibility and permissions
- **First-launch permission request**: Follows Android best practices by requesting permissions immediately on app launch
- **Permission management in Settings**: Dedicated section to view required permissions and open system settings to revoke/manage permissions
- Scoped storage support on Android 10+
- Proper MediaStore updates after renames
- Version-aware permissions: Android 13+ READ_MEDIA_*; Android 11+ MANAGE_EXTERNAL_STORAGE; legacy READ/WRITE for older versions
- Foreground service and notification permissions for long-running operations
- **Transparent permission handling**: Clear rationale messages and graceful degradation when permissions denied
- **Cross-platform**: Shared logic for potential iOS/Desktop versions
- **Backward compatibility**: Support for Android 8+ with graceful feature degradation

## Permissions

The app requires the following permissions:

- Android 13+: READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_AUDIO
- Android 11+: MANAGE_EXTERNAL_STORAGE
- Android 10 and below: READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE
- Service permissions: FOREGROUND_SERVICE, FOREGROUND_SERVICE_DATA_SYNC, POST_NOTIFICATIONS
- Optional: CAMERA (for QR/barcode scanning), INTERNET (for cloud sync), ACCESS_FINE_LOCATION (for GPS naming)

## Innovative Ideas for Future Expansion

### AI & Machine Learning
- **Content-aware renaming**: Detect objects, faces, scenes in photos and suggest names
<!-- - **Smart categorization**: Auto-categorize files by content (receipts, documents, selfies, etc.) -->
- **Duplicate finder**: Use perceptual hashing to find similar images
<!-- - **Auto-tagging**: Generate relevant tags based on image analysis -->
- **Face recognition**: Group photos by detected faces (on-device, privacy-focused)

### Productivity & Workflow
- **Shortcuts integration**: Android shortcuts for quick rename operations
- **Widget support**: Home screen widget for quick access to common tasks
- **Tasker/IFTTT integration**: Trigger rename operations from other apps
- **Batch scripts**: Create and run custom scripts for complex workflows
<!-- - **Time-based rules**: Automatically process files added to watched folders -->
<!-- - **Integration with file managers**: Plugin for popular file manager apps -->

### Media-specific Features
- **Video thumbnail extraction**: Use video frames for preview before renaming
- **Audio file metadata**: Extract artist, album, duration for music files
- **Document parsing**: Extract title from PDF/DOC files for smart naming
- **Image format conversion**: Convert formats while renaming (JPG→PNG, HEIC→JPG)
<!-- - **Watermark addition**: Add watermarks during batch processing -->
- **Metadata editor**: Edit EXIF, ID3 tags alongside renaming

### Business & Professional Use
- **Invoice/receipt processing**: OCR to extract invoice numbers for naming
- **Compliance features**: Rename files according to company naming standards
- **Audit trail**: Detailed logs for regulatory compliance
- **Bulk CSV import**: Import rename mappings from spreadsheets
- **Team templates**: Share organizational templates across teams
- **API access**: RESTful API for integration with other business tools

### Gamification & User Engagement
<!-- - **Achievements system**: Unlock badges for processing milestones -->
- **Statistics dashboard**: Visualize renaming history, time saved, storage optimized
<!-- - **Streak counter**: Track consecutive days of organizing files -->
<!-- - **Leaderboards**: Optional community feature for most organized users -->
- **Tips & tutorials**: In-app guidance for advanced features
- **Dark patterns avoided**: No manipulative engagement tactics

### Privacy & Security
- **Encrypted vault**: Secure folder with biometric access
- **Filename obfuscation**: Option to encrypt filenames for privacy
- **No cloud requirement**: All features work locally
- **Open-source**: Transparent code for security audit
<!-- - **Privacy dashboard**: Show what data is collected (none!) and permissions used -->
- **Secure deletion**: Overwrite files before deletion for sensitive data

## Usage

### Batch Processing

1. Click "Batch Process Files" on the main screen
2. Select multiple images from your device
3. Configure numbering options (start number, digit count, prefix)
4. Choose a destination folder
5. Use "Preview" to see how files will be renamed
6. Click "Start" to process the files

## Technical Stack

### Core Technologies
- **Language**: Kotlin 2.0+ with coroutines and Flow
- **UI**: Jetpack Compose with Material 3
- **Architecture**: Clean Architecture + MVI/MVVM
- **DI**: Hilt for dependency injection
- **Async**: Kotlin Coroutines + Flow
- **Storage**: Room for local data, DataStore for preferences
- **Image Loading**: Coil with proper memory management
- **Background Work**: WorkManager for reliable task execution

### Libraries & Tools
- **Navigation**: Compose Navigation with type-safe routes
- **State**: ViewModel + StateFlow/SharedFlow
- **Permissions**: Accompanist Permissions (Compose-friendly)
- **Testing**: JUnit 5, MockK, Turbine, Compose Testing
- **Build**: Gradle 8.x with Kotlin DSL, version catalogs
- **Quality**: Ktlint, Detekt, Spotless

## Implementation Strategy

**Status:** ✅ Firebase configured and operational

This project follows a strategic, pragmatic approach to technology selection, balancing development speed, external dependencies, and production quality.

### Core Principles

**Three Technology Tiers:**
1. **Native Android Components** (Zero external setup)
2. **Google ML Kit** (On-device, privacy-first AI)
3. **Firebase** (One-time setup, comprehensive backend)

---

### Group 1-3, 7: Native Android Components
**Tools:** Room, WorkManager, Storage Access Framework (SAF), ContentResolver, FileObserver

**Use Cases:**
- File system operations (browse, monitor, rename)
- Local data persistence (templates, history, tags, activity logs)
- Background services and scheduled tasks
- Testing infrastructure

**Why These Tools:**
- ✅ Industry standard "correct" way to build Android apps
- ✅ Zero external dependencies or API keys
- ✅ 100% logic and code - full control
- ✅ Offline-first by design
- ✅ Battle-tested, well-documented

**External Work Required:** None - pure Android development

**Trade-offs:**
- ⚠️ More code to write (no shortcuts)
- ⚠️ Requires deep Android knowledge
- ✅ Maximum flexibility and customization

---

### Group 4: AI & Machine Learning
**Tool:** Google ML Kit (On-Device)

**Use Cases:**
- Image content analysis and labeling (CHUNK 6: AI-powered filename suggestions)
- OCR text extraction from images (CHUNK 11: Extract text for smart naming)
- QR code generation and scanning (CHUNK 10: Preset sharing)

**Why ML Kit:**
- ✅ Most versatile - handles OCR, QR, and Image Labeling in one SDK
- ✅ On-device processing - privacy-friendly, no data leaves phone
- ✅ Works completely offline after initial model download
- ✅ Zero external setup - no API console, no keys, just build.gradle dependencies
- ✅ Excellent accuracy with Google's pre-trained models
- ✅ Free tier sufficient for most use cases

**External Work Required:** None - just add dependencies

**Dependencies:**
```kotlin
// build.gradle.kts (app)
implementation("com.google.mlkit:image-labeling:17.0.7")
implementation("com.google.mlkit:text-recognition:16.0.0")
implementation("com.google.mlkit:barcode-scanning:17.2.0")
```

**Trade-offs:**
- ⚠️ Slightly larger app size (~15-20MB for models)
- ⚠️ First-run model download required (automatic, one-time)
- ✅ Privacy-first: all processing on-device
- ✅ Offline capability after initial setup

**Alternatives Considered:**
- ❌ TensorFlow Lite: More complex setup, requires custom model training
- ❌ Cloud Vision API: Requires internet, privacy concerns, API costs
- ❌ ZXing (QR only): Less integrated, missing OCR/labeling

---

### Group 5: Cloud & Synchronization
**Tool:** Firebase (Firestore + Auth + Storage)

**Use Cases:**
- Multi-device sync for settings and templates (CHUNK 12: SyncRepositoryImpl)
- Cloud backup for renamed files (CHUNK 9: CloudSyncRepositoryImpl)
- User authentication for collaborative features
- Crash reporting and analytics
- Remote configuration

**Why Firebase:**
- ✅ The "Cheat Code" - one SDK solves multiple problems:
  - **Firestore**: Real-time NoSQL database for settings/templates sync
  - **Cloud Storage**: File backup and sharing
  - **Authentication**: Email, Google, social login
  - **Crashlytics**: Automatic crash reporting
  - **Remote Config**: A/B testing, feature flags
- ✅ Saves months of backend development
- ✅ Scales automatically (serverless)
- ✅ Generous free tier (Spark plan)
- ✅ Real-time synchronization out of the box
- ✅ Offline persistence built-in
- ✅ Excellent Android integration with Kotlin extensions

**External Work Required:** High (one-time)
1. Create Firebase project in console (5 minutes)
2. Download `google-services.json` (1 minute)
3. Add Firebase dependencies to build.gradle (2 minutes)
4. Enable required services (Firestore, Auth, Storage) in console (5 minutes)

**Status:** ✅ **Complete** - Firebase project configured and operational

**Dependencies:**
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
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-config-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
}
```

**Trade-offs:**
- ⚠️ Vendor lock-in to Google's infrastructure
- ⚠️ Requires internet for sync (offline-first with local cache)
- ⚠️ Free tier limits (10GB storage, 50K reads/day - sufficient for MVP)
- ✅ Professional backend without server management
- ✅ Extremely fast development velocity
- ✅ Production-ready scalability

**Alternatives Considered:**
- ❌ Supabase: Good alternative, but less Android integration
- ❌ AWS Amplify: More complex setup, higher learning curve
- ❌ Custom backend: Months of development, server costs, maintenance burden
- ❌ Google Drive API: Only file storage, no database or auth
- ❌ Dropbox/OneDrive: Third-party, requires separate auth per provider

---

### Decision Matrix

| Feature Group | Tool Choice | Setup Time | External Dependencies | Offline Support | Privacy | Verdict |
|---------------|-------------|------------|----------------------|-----------------|---------|----------|
| **File Operations** | Native Android | None | None | ✅ Full | ✅ Local only | ✅ Perfect |
| **Local Database** | Room | None | None | ✅ Full | ✅ Local only | ✅ Perfect |
| **Background Tasks** | WorkManager | None | None | ✅ Full | ✅ Local only | ✅ Perfect |
| **AI/ML Features** | ML Kit | ~15 min | Google Play Services | ✅ After initial download | ✅ On-device | ✅ Best choice |
| **Cloud Sync** | Firebase | ~15 min (✅ Done) | Internet for sync | ✅ Offline cache | ⚠️ Data on Google servers | ✅ Best ROI |

**Total Setup Investment:** ~30 minutes (one-time)
**Development Time Saved:** Months (backend avoided)
**Production Readiness:** Enterprise-grade

---

### Implementation Timeline

**Current Status:**
- ✅ Native Android infrastructure operational (Phase 1-2)
- ✅ Firebase configured and ready (Phase 5)
- 🔜 ML Kit integration pending (Phase 4 - CHUNKS 10, 11, 13)

**Upgrade Path from Mocks:**
1. **Native Features** (Phase 2-3): Already production-ready, no mocks needed
2. **ML Features** (Phase 4): Replace mock AI responses with ML Kit (1-2 days per feature)
3. **Cloud Features** (Phase 5): Replace in-memory storage with Firebase (1-2 days per feature)

See [MOCK_IMPLEMENTATIONS.md](MOCK_IMPLEMENTATIONS.md) for detailed upgrade guides.

## Development Roadmap

> **Development Strategy:** Vertical Slice (Feature-First) Approach  
> Each chunk completes one feature end-to-end (Domain → Data → Presentation) for faster iteration and testable results.

---

### **Phase 1: Foundation**

**Status:** Architecture foundation complete with permission system and consolidated Settings

#### Completed
- Project setup with optimized Gradle configuration
- Dependency injection setup (Hilt)
- Navigation structure (type-safe with Kotlin serialization)
- Theme system with Material 3 (dynamic colors, dark mode, image-based theming)
- Basic UI screens (Home, Batch Process, Settings)
- **Permission System (CHUNK 2)**: ✅ Fully operational
  - First-launch permission request in MainActivity
  - Permission management section in Settings
  - PermissionHandler composable wrapping file-access screens
  - Version-aware permission handling (Android 13+, 11+, legacy)
- **CHUNK 1: Architecture Foundation**
  - Created domain/data/presentation folder structure
  - Base classes: `Result<T>`, `BaseViewModel`, `BaseUseCase`
  - Error handling with sealed classes
  - DI module structure (domain, data, presentation modules)
  - State management patterns (sealed UI states, MVI)
  - Complete Settings feature with theme persistence
  - Build successful with no errors
- **CHUNK 10: Dynamic Theming**: ✅ Integrated into Settings
  - Image-based theme customization consolidated in Settings
  - Removed standalone DynamicThemeScreen for better UX

**Achievement:** Established reusable patterns with production-ready permission handling. Settings screen now serves as central hub for all app preferences (theme, permissions, image-based theming). See [CHUNK_1_COMPLETION.md](CHUNK_1_COMPLETION.md) for details.

---

### **Phase 2: Core Features (Batch Rename MVP)**

#### CHUNK 2: Permissions System ✅
- Domain: Permission models, CheckPermission/RequestPermission use cases
- Data: PermissionsManager repository
- Presentation: PermissionHandler composable, permission management in Settings
- Support: Android 13+ READ_MEDIA_*, Android 11+ MANAGE_EXTERNAL_STORAGE
- **Implementation**: First-launch request in MainActivity, Settings integration for management

**Output:** ✅ Production-ready permission system with first-launch request and Settings management

#### CHUNK 3: File Selection Feature
- Domain: FileItem model, GetMediaFiles use case
- Data: MediaStoreRepository (query images/videos with ContentResolver)
- Presentation: File picker UI with thumbnails, multi-select
- Optimization: Lazy loading, image caching with Coil

**Output:** Working file picker with gallery integration

#### CHUNK 4: Batch Rename Logic Core
- Domain: RenameConfig model, GenerateFilename use case
- Data: FileOperationsManager (filename validation, conflict detection)
- Presentation: Configuration UI (prefix, start number, digit count)
- Logic: Sequential numbering, extension preservation

**Output:** Configure and preview rename patterns

#### CHUNK 5: Rename Execution
- Domain: ExecuteBatchRename use case with progress Flow
- Data: Actual file renaming with scoped storage API
- Presentation: Progress indicator, success/error states
- MediaStore: Scan files after rename for gallery updates

**Output:** Complete working batch rename feature

#### CHUNK 6: Destination Folder Selector
- Domain: FolderInfo model, GetFolders use case
- Data: Folder scanning with DocumentFile API
- Presentation: Folder picker with navigation

**Output:** Choose destination folders for renamed files

---

### **Phase 3: Advanced Features**

#### CHUNK 7: Preview System
- Domain: GeneratePreview use case (before/after mapping)
- Presentation: Preview list with warnings (duplicates, invalid names)
- Validation: Name conflicts, character restrictions

**Output:** Preview results before committing renames

#### CHUNK 8: Natural Sorting & Order Preservation
- Domain: SortFiles use case with strategies (natural, date, size, original)
- Presentation: Sort options in configuration UI

**Output:** Smart file ordering options

#### CHUNK 9: File Observer - Real-time Monitoring
- Domain: FolderMonitor model, StartMonitoring/StopMonitoring use cases
- Data: FileObserver implementation with pattern matching
- Presentation: Monitoring toggle, active folder status
- Background: Foreground service with notification

**Output:** Auto-rename files on detection in monitored folders

#### CHUNK 10: Dynamic Theming from Images ✅
- Domain: ExtractPalette use case (Palette API)
- Data: Safe URI handling for Google Photos, persistent permissions
- Presentation: **Integrated into Settings** - image picker, color palette preview, apply/reset theme
- Storage: DataStore for theme preferences

**Output:** ✅ Image-based dynamic theming integrated into Settings (consolidated with theme mode and dynamic colors)

---

### **Phase 4: Smart Features**

#### CHUNK 11: EXIF Metadata Extraction
- Domain: MetadataExtractor, pattern variables ({date}, {location}, {camera})
- Data: ExifInterface parsing for photos
- Presentation: Variable picker UI, preview with metadata

**Output:** Use photo metadata in filename patterns

#### CHUNK 12: Pattern Templates
- Domain: Template model, SaveTemplate/LoadTemplate use cases
- Data: Room database for template storage
- Presentation: Template CRUD UI, quick apply

**Output:** Save and reuse naming patterns

#### CHUNK 13: AI-Powered Filename Suggestions
- Domain: AnalyzeImage use case
- Data: ML Kit image labeling integration (on-device)
- Presentation: Suggestion chips, auto-apply option

**Output:** Smart filename suggestions based on image content

#### CHUNK 14: Undo/Redo System
- Domain: RenameHistory model, UndoRename/RedoRename use cases
- Data: Room database for operation history
- Presentation: Undo button, history view with timestamps

**Output:** Revert rename operations with full history

#### CHUNK 15: Regex Pattern Support
- Domain: RegexRenameRule, ApplyRegexPattern use case
- [ ] Presentation: Regex builder UI with validation, common patterns library

**Output:** ✅ Advanced users can use regex for complex renames

#### CHUNK 16: Tag System for Files
- [ ] Domain: Tag model, TagFile/SearchByTag use cases
- [ ] Data: Room database for tag associations
- [ ] Presentation: Tag management UI, filter by tags

**Output:** Organize and filter files with custom tags

---

### **Phase 5: Integration & Sync**

#### CHUNK 17: Cloud Storage Integration
- Domain: CloudSync abstraction, SyncFiles use case
- Data: Drive/Dropbox/OneDrive API implementations
- Presentation: Cloud account linking, sync settings
- Background: WorkManager for scheduled sync

**Output:** Sync renamed files to cloud storage

#### CHUNK 18: QR Code Generation for Presets
- Domain: GenerateQRCode use case
- Data: Preset serialization to JSON
- Presentation: QR code display, scanner for import

**Output:** Share rename presets via QR codes

#### CHUNK 19: OCR Integration
- Domain: ExtractTextFromImage use case
- Data: ML Kit text recognition
- Presentation: Text extraction preview, use in filename

**Output:** Extract text from images for smart naming

#### CHUNK 20: Multi-Device Sync
- Domain: SyncPreferences use case
- Data: Firebase Firestore for settings/templates
- Presentation: Account management, sync status

**Output:** Sync settings and templates across devices

#### CHUNK 21: Activity Log & Export
- Domain: LogActivity use case, operation tracking
- Data: Room database for activity history
- Presentation: Log viewer, export to CSV/JSON

**Output:** Detailed operation logs with export

---

### **Phase 6: Polish & Optimization**

#### CHUNK 22: Performance Optimization
- Profiling: Android Profiler analysis (CPU, memory, network)
- Optimization: Lazy sequences, Flow optimization, bitmap recycling
- Benchmarking: Measure against performance goals

**Output:** Achieve performance benchmarks (startup < 1.5s, etc.)

#### CHUNK 23: Comprehensive Testing
- Unit tests: Use cases, repositories (MockK, JUnit 5)
- Integration tests: Database, file operations
- UI tests: Compose Testing, end-to-end flows

**Output:** 70%+ code coverage with reliable tests

#### CHUNK 24: UI/UX Polish
- Animations: Transitions, loading states, success feedback
- Empty states: Helpful guidance, onboarding
- Error states: User-friendly messages, recovery actions

**Output:** Smooth, delightful user experience

#### CHUNK 25: Accessibility & i18n
- Accessibility: Content descriptions, semantic properties, TalkBack testing
- Internationalization: String resources, multi-language support
- RTL support: Layout mirroring for RTL languages

**Output:** Inclusive app for all users

#### CHUNK 26: Documentation & Code Cleanup
- KDoc comments for public APIs
- Architecture Decision Records (ADRs)
- README updates with setup instructions
- Code style: Ktlint/Detekt cleanup

**Output:** Well-documented, maintainable codebase

---

## Technical Details

- **Scoped Storage**: Proper handling of Android 10+ storage restrictions with MediaStore API
- **File Operations**: Efficient batch processing with coroutines and Flow
- **Memory Management**: LRU caching, proper Bitmap handling, WeakReference where appropriate
- **Error Handling**: Sealed Result types, comprehensive error states, user-friendly messages
- **Testing**: Unit tests for business logic, UI tests for Compose screens, integration tests for workflows
- **Accessibility**: Proper content descriptions, semantic properties, screen reader support
- **Performance**: Lazy collections, efficient RecyclerView usage, optimized recomposition in Compose

## Performance Benchmarks

- **App startup**: Cold start < 1.5s, warm start < 0.5s
- **File selection**: 1000+ files without jank
- **Batch processing**: 100 files in < 5s with progress updates
- **Memory**: Peak memory usage < 150MB for typical use
- **APK size**: Release APK < 10MB

---

## Setup Instructions

### Prerequisites
- **Android Studio**: Hedgehog (2023.1.1) or later
- **JDK**: 17 or later
- **Android SDK**: API 34 (compile), API 26+ (minimum)
- **Gradle**: 8.2+
- **Kotlin**: 2.0+

### Clone and Build

```bash
# Clone the repository
git clone https://github.com/your-org/conversion.git
cd conversion

# Build the project
./gradlew build

# Run tests
./gradlew test

# Install on device/emulator
./gradlew installDebug
```

### Project Structure

```
app/src/main/java/com/example/conversion/
├── domain/                 # Business logic layer (pure Kotlin)
│   ├── model/             # Domain models
│   ├── repository/        # Repository interfaces
│   ├── usecase/           # Use cases (business logic)
│   └── common/            # Shared types (Result, BaseUseCase)
├── data/                  # Data layer (implementations)
│   ├── repository/        # Repository implementations
│   ├── source/            # Data sources (MediaStore, Room)
│   ├── local/             # Local storage (DAOs, entities)
│   ├── manager/           # Specialized operations
│   └── util/              # Data utilities
├── presentation/          # UI layer
│   ├── ui/                # Compose screens
│   ├── viewmodel/         # ViewModels
│   ├── navigation/        # Navigation setup
│   └── theme/             # Material 3 theming
├── di/                    # Dependency injection modules
└── service/               # Background services
```

### Configuration

#### gradle.properties
```properties
# Gradle optimization
org.gradle.jvmargs=-Xmx4g -XX:+HeapDumpOnOutOfMemoryError
org.gradle.caching=true
org.gradle.parallel=true
org.gradle.configureondemand=true

# Kotlin optimization
kotlin.incremental=true
kotlin.compiler.execution.strategy=in-process

# Android optimization
android.useAndroidX=true
android.enableJetifier=false
```

#### local.properties
```properties
# Android SDK location
sdk.dir=/path/to/Android/sdk
```

### Development Guidelines

#### Code Style
- **Formatting**: Use Ktlint for consistent formatting
- **Static Analysis**: Detekt for code quality checks
- **Naming**: Follow Kotlin conventions (PascalCase for classes, camelCase for functions)

```bash
# Format code
./gradlew ktlintFormat

# Run static analysis
./gradlew detekt
```

#### Architecture Patterns

**Clean Architecture Layers:**
```
Presentation → Domain ← Data
```

- **Domain**: Pure Kotlin, no Android dependencies
- **Data**: Implements domain interfaces, Android-specific code
- **Presentation**: UI (Compose), ViewModels

**See Architecture Decision Records:**
- [ADR 001: Clean Architecture](docs/adr/001-clean-architecture.md)
- [ADR 002: MVI Pattern](docs/adr/002-mvi-pattern.md)
- [ADR 003: Repository Pattern](docs/adr/003-repository-pattern.md)
- [ADR 004: Use Case Pattern](docs/adr/004-use-case-pattern.md)

#### Testing Strategy

```bash
# Run all tests
./gradlew test

# Run specific test
./gradlew test --tests "PermissionsManagerImplTest"

# Generate coverage report
./gradlew testDebugUnitTestCoverage
```

**Test Types:**
- **Unit Tests**: Domain and data layer (MockK, JUnit 5)
- **Integration Tests**: Database and file operations
- **UI Tests**: Compose Testing for screens

**Coverage Goals:**
- Domain layer: 100%
- Data layer: 90%+
- Overall: 70%+

#### Commit Conventions

```bash
# Feature development
[CHUNK X] Feature Name - Description

# Bug fixes
[FIX] Description of fix

# Documentation
[DOCS] Description of documentation changes

# Refactoring
[REFACTOR] Description of refactor
```

#### Branch Strategy

```
main                    # Stable production code
├── kai-dev            # Kai's development branch
│   └── feature/chunk-X-backend
└── sokchea-dev        # Sokchea's development branch
    └── feature/chunk-X-ui
```

### Common Tasks

#### Add New Feature (Use Case)

1. **Create Domain Model** (`domain/model/`)
```kotlin
data class MyModel(
    val id: String,
    val name: String
)
```

2. **Create Repository Interface** (`domain/repository/`)
```kotlin
interface MyRepository {
    suspend fun getData(): Result<MyModel>
}
```

3. **Create Use Case** (`domain/usecase/`)
```kotlin
class GetDataUseCase @Inject constructor(
    private val repository: MyRepository
) : BaseUseCase<Unit, MyModel>(Dispatchers.IO) {
    override suspend fun execute(input: Unit): Result<MyModel> {
        return repository.getData()
    }
}
```

4. **Implement Repository** (`data/repository/`)
```kotlin
class MyRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : MyRepository {
    override suspend fun getData(): Result<MyModel> = withContext(ioDispatcher) {
        try {
            // Implementation
            Result.Success(MyModel(...))
        } catch (e: Exception) {
            Result.Error(Exception("Failed to get data", e))
        }
    }
}
```

5. **Add Dependency Injection** (`di/`)
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class MyDataModule {
    @Binds
    @Singleton
    abstract fun bindMyRepository(impl: MyRepositoryImpl): MyRepository
}
```

6. **Create ViewModel** (`presentation/viewmodel/`)
```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val getDataUseCase: GetDataUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(MyState())
    val state: StateFlow<MyState> = _state.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = getDataUseCase(Unit)) {
                is Result.Success -> _state.update {
                    it.copy(data = result.data, isLoading = false)
                }
                is Result.Error -> _state.update {
                    it.copy(error = result.message, isLoading = false)
                }
                is Result.Loading -> { }
            }
        }
    }
}
```

7. **Create UI** (`presentation/ui/`)
```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    when {
        state.isLoading -> LoadingIndicator()
        state.error != null -> ErrorMessage(state.error!!)
        else -> Content(state.data)
    }
}
```

8. **Write Tests**
```kotlin
class GetDataUseCaseTest {
    private lateinit var repository: FakeMyRepository
    private lateinit var useCase: GetDataUseCase
    
    @Before
    fun setup() {
        repository = FakeMyRepository()
        useCase = GetDataUseCase(repository)
    }
    
    @Test
    fun `returns data successfully`() = runTest {
        repository.setData(MyModel(...))
        
        val result = useCase(Unit)
        
        assertTrue(result is Result.Success)
        assertEquals(expectedData, (result as Result.Success).data)
    }
}
```

### Troubleshooting

#### Build Issues
```bash
# Clean build
./gradlew clean build

# Invalidate caches (Android Studio)
File > Invalidate Caches / Restart
```

#### Permission Issues on Android 11+
- Enable "All files access" in device settings for testing
- Use proper MediaStore APIs for scoped storage

#### Tests Not Running
```bash
# Check test configuration
./gradlew test --info

# Run specific test suite
./gradlew testDebugUnitTest
```

### Resources

#### Documentation
- [Chunk Completion Docs](CHUNK_1_COMPLETION.md) - Detailed implementation guides
- [Kai's Tasks](KAI_TASKS.md) - Backend development guide
- [Sokchea's Tasks](SOKCHEA_TASKS.md) - UI development guide
- [Work Division](WORK_DIVISION.md) - Team collaboration strategy
- [Mock Implementations](MOCK_IMPLEMENTATIONS.md) - Strategic simplifications

#### External Resources
- [Android Developers](https://developer.android.com/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Hilt Documentation](https://dagger.dev/hilt/)

---

## License

[License information to be added]

## Contributors

- **Kai** - Backend/Core Features Specialist
- **Sokchea** - UI/UX Specialist

---

**Last Updated:** December 8, 2025