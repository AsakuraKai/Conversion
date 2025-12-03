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
- Automatic light/dark adaptation, status/navigation bar coloring, and dynamic button styles
- Universal background wallpaper shown consistently across Main screen, Batch Processing, and supported fragments
- Safe background loading for Google Photos and other content providers (no SecurityException crashes)
- Persistent URI permissions so background images survive app restarts
- Automatic theme propagation across activities without manual app restart
- Follows device theme by default; theme controls are organized under Settings

### Navigation and modules
- Drawer-based navigation with a clean “Settings” section
- Batch Processing screen for renaming workflows
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
- Scoped storage support on Android 10+
- Proper MediaStore updates after renames
- Version-aware permissions: Android 13+ READ_MEDIA_*; Android 11+ MANAGE_EXTERNAL_STORAGE; legacy READ/WRITE for older versions
- Foreground service and notification permissions for long-running operations
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

## Development Roadmap

> **Development Strategy:** Vertical Slice (Feature-First) Approach  
> Each chunk completes one feature end-to-end (Domain → Data → Presentation) for faster iteration and testable results.

---

### **Phase 1: Foundation** ✅ (100% Complete)

**Status:** Architecture foundation complete and validated with working Settings feature

#### Completed ✅
- [x] Project setup with optimized Gradle configuration
- [x] Dependency injection setup (Hilt)
- [x] Navigation structure (type-safe with Kotlin serialization)
- [x] Theme system with Material 3 (dynamic colors, dark mode)
- [x] Basic UI screens (Home, Batch Process, Settings)
- [x] **CHUNK 1: Architecture Foundation** ✅
  - [x] Created domain/data/presentation folder structure
  - [x] Base classes: `Result<T>`, `BaseViewModel`, `BaseUseCase`
  - [x] Error handling with sealed classes
  - [x] DI module structure (domain, data, presentation modules)
  - [x] State management patterns (sealed UI states, MVI)
  - [x] Complete Settings feature with theme persistence
  - [x] Build successful with no errors

**Achievement:** Established reusable patterns for all future features. Working end-to-end Settings feature with theme persistence validates architecture. See [CHUNK_1_COMPLETION.md](CHUNK_1_COMPLETION.md) for details.

---

### **Phase 2: Core Features** 📁 (Batch Rename MVP)

#### CHUNK 2: Permissions System
- [ ] Domain: Permission models, CheckPermission/RequestPermission use cases
- [ ] Data: PermissionsManager repository
- [ ] Presentation: Permission handling composables
- [ ] Support: Android 13+ READ_MEDIA_*, Android 11+ MANAGE_EXTERNAL_STORAGE

**Output:** ✅ Reusable permission system for file operations

#### CHUNK 3: File Selection Feature
- [ ] Domain: FileItem model, GetMediaFiles use case
- [ ] Data: MediaStoreRepository (query images/videos with ContentResolver)
- [ ] Presentation: File picker UI with thumbnails, multi-select
- [ ] Optimization: Lazy loading, image caching with Coil

**Output:** ✅ Working file picker with gallery integration

#### CHUNK 4: Batch Rename Logic Core
- [ ] Domain: RenameConfig model, GenerateFilename use case
- [ ] Data: FileOperationsManager (filename validation, conflict detection)
- [ ] Presentation: Configuration UI (prefix, start number, digit count)
- [ ] Logic: Sequential numbering, extension preservation

**Output:** ✅ Configure and preview rename patterns

#### CHUNK 5: Rename Execution
- [ ] Domain: ExecuteBatchRename use case with progress Flow
- [ ] Data: Actual file renaming with scoped storage API
- [ ] Presentation: Progress indicator, success/error states
- [ ] MediaStore: Scan files after rename for gallery updates

**Output:** ✅ Complete working batch rename feature

#### CHUNK 6: Destination Folder Selector
- [ ] Domain: FolderInfo model, GetFolders use case
- [ ] Data: Folder scanning with DocumentFile API
- [ ] Presentation: Folder picker with navigation

**Output:** ✅ Choose destination folders for renamed files

---

### **Phase 3: Advanced Features** 🚀

#### CHUNK 7: Preview System
- [ ] Domain: GeneratePreview use case (before/after mapping)
- [ ] Presentation: Preview list with warnings (duplicates, invalid names)
- [ ] Validation: Name conflicts, character restrictions

**Output:** ✅ Preview results before committing renames

#### CHUNK 8: Natural Sorting & Order Preservation
- [ ] Domain: SortFiles use case with strategies (natural, date, size, original)
- [ ] Presentation: Sort options in configuration UI

**Output:** ✅ Smart file ordering options

#### CHUNK 9: File Observer - Real-time Monitoring
- [ ] Domain: FolderMonitor model, StartMonitoring/StopMonitoring use cases
- [ ] Data: FileObserver implementation with pattern matching
- [ ] Presentation: Monitoring toggle, active folder status
- [ ] Background: Foreground service with notification

**Output:** ✅ Auto-rename files on detection in monitored folders

#### CHUNK 10: Dynamic Theming from Images
- [ ] Domain: ExtractPalette use case (Palette API)
- [ ] Data: Safe URI handling for Google Photos, persistent permissions
- [ ] Presentation: Background image picker, dynamic color application
- [ ] Storage: DataStore for theme preferences

**Output:** ✅ Image-based dynamic theming across all screens

---

### **Phase 4: Smart Features** 🧠

#### CHUNK 11: EXIF Metadata Extraction
- [ ] Domain: MetadataExtractor, pattern variables ({date}, {location}, {camera})
- [ ] Data: ExifInterface parsing for photos
- [ ] Presentation: Variable picker UI, preview with metadata

**Output:** ✅ Use photo metadata in filename patterns

#### CHUNK 12: Pattern Templates
- [ ] Domain: Template model, SaveTemplate/LoadTemplate use cases
- [ ] Data: Room database for template storage
- [ ] Presentation: Template CRUD UI, quick apply

**Output:** ✅ Save and reuse naming patterns

#### CHUNK 13: AI-Powered Filename Suggestions
- [ ] Domain: AnalyzeImage use case
- [ ] Data: ML Kit image labeling integration (on-device)
- [ ] Presentation: Suggestion chips, auto-apply option

**Output:** ✅ Smart filename suggestions based on image content

#### CHUNK 14: Undo/Redo System
- [ ] Domain: RenameHistory model, UndoRename/RedoRename use cases
- [ ] Data: Room database for operation history
- [ ] Presentation: Undo button, history view with timestamps

**Output:** ✅ Revert rename operations with full history

#### CHUNK 15: Regex Pattern Support
- [ ] Domain: RegexRenameRule, ApplyRegexPattern use case
- [ ] Presentation: Regex builder UI with validation, common patterns library

**Output:** ✅ Advanced users can use regex for complex renames

#### CHUNK 16: Tag System for Files
- [ ] Domain: Tag model, TagFile/SearchByTag use cases
- [ ] Data: Room database for tag associations
- [ ] Presentation: Tag management UI, filter by tags

**Output:** ✅ Organize and filter files with custom tags

---

### **Phase 5: Integration & Sync** ☁️

#### CHUNK 17: Cloud Storage Integration
- [ ] Domain: CloudSync abstraction, SyncFiles use case
- [ ] Data: Drive/Dropbox/OneDrive API implementations
- [ ] Presentation: Cloud account linking, sync settings
- [ ] Background: WorkManager for scheduled sync

**Output:** ✅ Sync renamed files to cloud storage

#### CHUNK 18: QR Code Generation for Presets
- [ ] Domain: GenerateQRCode use case
- [ ] Data: Preset serialization to JSON
- [ ] Presentation: QR code display, scanner for import

**Output:** ✅ Share rename presets via QR codes

#### CHUNK 19: OCR Integration
- [ ] Domain: ExtractTextFromImage use case
- [ ] Data: ML Kit text recognition
- [ ] Presentation: Text extraction preview, use in filename

**Output:** ✅ Extract text from images for smart naming

#### CHUNK 20: Multi-Device Sync
- [ ] Domain: SyncPreferences use case
- [ ] Data: Firebase Firestore for settings/templates
- [ ] Presentation: Account management, sync status

**Output:** ✅ Sync settings and templates across devices

#### CHUNK 21: Activity Log & Export
- [ ] Domain: LogActivity use case, operation tracking
- [ ] Data: Room database for activity history
- [ ] Presentation: Log viewer, export to CSV/JSON

**Output:** ✅ Detailed operation logs with export

---

### **Phase 6: Polish & Optimization** ✨

#### CHUNK 22: Performance Optimization
- [ ] Profiling: Android Profiler analysis (CPU, memory, network)
- [ ] Optimization: Lazy sequences, Flow optimization, bitmap recycling
- [ ] Benchmarking: Measure against performance goals

**Output:** ✅ Achieve performance benchmarks (startup < 1.5s, etc.)

#### CHUNK 23: Comprehensive Testing
- [ ] Unit tests: Use cases, repositories (MockK, JUnit 5)
- [ ] Integration tests: Database, file operations
- [ ] UI tests: Compose Testing, end-to-end flows

**Output:** ✅ 70%+ code coverage with reliable tests

#### CHUNK 24: UI/UX Polish
- [ ] Animations: Transitions, loading states, success feedback
- [ ] Empty states: Helpful guidance, onboarding
- [ ] Error states: User-friendly messages, recovery actions

**Output:** ✅ Smooth, delightful user experience

#### CHUNK 25: Accessibility & i18n
- [ ] Accessibility: Content descriptions, semantic properties, TalkBack testing
- [ ] Internationalization: String resources, multi-language support
- [ ] RTL support: Layout mirroring for RTL languages

**Output:** ✅ Inclusive app for all users

#### CHUNK 26: Documentation & Code Cleanup
- [ ] KDoc comments for public APIs
- [ ] Architecture Decision Records (ADRs)
- [ ] README updates with setup instructions
- [ ] Code style: Ktlint/Detekt cleanup

**Output:** ✅ Well-documented, maintainable codebase

---

## 📊 Development Progress Tracker

| Phase | Chunks | Status | Completion |
|-------|--------|--------|------------|
| Phase 1: Foundation | 1 | ✅ Complete | 100% (6/6 tasks) |
| Phase 2: Core Features | 2-6 | ⏳ Ready to Start | 0% (0/5 chunks) |
| Phase 3: Advanced Features | 7-10 | ⏳ Pending | 0% (0/4 chunks) |
| Phase 4: Smart Features | 11-16 | ⏳ Pending | 0% (0/6 chunks) |
| Phase 5: Integration & Sync | 17-21 | ⏳ Pending | 0% (0/5 chunks) |
| Phase 6: Polish & Optimization | 22-26 | ⏳ Pending | 0% (0/5 chunks) |

**Total:** 26 chunks | **Completed:** 1 chunk

---

## 🎯 Current Focus: CHUNK 2 - Permissions System

**Phase 1 Complete!** ✅ Architecture foundation is ready.

**Next Steps for CHUNK 2:**
1. Create permission domain models (Permission types, status)
2. Implement CheckPermission/RequestPermission use cases
3. Create PermissionsManager repository
4. Add permission handling ViewModels
5. Test permission flows for Android 13+ and legacy versions

## Technical Details

- **Scoped Storage**: Proper handling of Android 10+ storage restrictions with MediaStore API
- **File Operations**: Efficient batch processing with coroutines and Flow
- **Memory Management**: LRU caching, proper Bitmap handling, WeakReference where appropriate
- **Error Handling**: Sealed Result types, comprehensive error states, user-friendly messages
- **Testing**: Unit tests for business logic, UI tests for Compose screens, integration tests for workflows
- **Accessibility**: Proper content descriptions, semantic properties, screen reader support
- **Performance**: Lazy collections, efficient RecyclerView usage, optimized recomposition in Compose

## Performance Benchmarks (Goals)

- **App startup**: Cold start < 1.5s, warm start < 0.5s
- **File selection**: 1000+ files without jank
- **Batch processing**: 100 files in < 5s with progress updates
- **Memory**: Peak memory usage < 150MB for typical use
- **APK size**: Release APK < 10MB

There are 6 markdown (.md) files in the repository root:

CHUNK_1_COMPLETION.md (12.8 KB) - Documentation for completed Chunk 1
CHUNK_2_COMPLETION.md (22.7 KB) - Documentation for completed Chunk 2
KAI_TASKS.md (16.8 KB) - NEW - Kai's personalized task guide
README.md (23.9 KB) - Main project documentation
SOKCHEA_TASKS.md (24.9 KB) - NEW - Sokchea's personalized task guide
WORK_DIVISION.md (38.9 KB) - Updated with conflict prevention strategies