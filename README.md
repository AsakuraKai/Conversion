# Files Management Service

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg)](https://kotlinlang.org)
[![MinSDK](https://img.shields.io/badge/MinSDK-26-orange.svg)](https://developer.android.com/about/versions/oreo)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Build](https://img.shields.io/badge/Build-Passing-brightgreen.svg)](https://github.com/yourusername/conversion/actions)

A modern Android application for intelligent file organization and batch renaming with advanced features including AI-powered suggestions, cloud sync, and real-time folder monitoring.

**Status:** Near Production - Enhancement Phase

## Overview

Files Management Service is a powerful file organization tool designed for Android that helps you efficiently manage, rename, and organize your media files. Built with modern Android best practices, the app combines intuitive UI with advanced features like AI-powered filename suggestions, cloud synchronization, and real-time folder monitoring.


## Key Features

### Core Functionality
- **Intelligent Batch Renaming**: Rename multiple files with sequential numbering, custom prefixes, and configurable digit counts
- **Smart File Selection**: Multi-select files from gallery or import entire folders at once
- **Preview System**: View before/after filename changes with conflict detection before committing
- **Format Conversion**: Convert between image formats (JPG, PNG, HEIC, WebP) during rename operations
- **Natural Sorting**: Smart file ordering with multiple sorting strategies (date, size, name, original order)

### Advanced Features
- **AI-Powered Suggestions**: ML Kit integration for intelligent filename suggestions based on image content
- **EXIF Metadata Extraction**: Use photo metadata (date, location, camera info) in filename patterns
- **Pattern Templates**: Save and reuse custom naming patterns with variable support
- **Real-time Folder Monitoring**: Automatic file processing when new files are added to monitored folders
- **Regex Pattern Support**: Advanced renaming rules for power users
- **Undo/Redo System**: Full operation history with ability to revert changes
- **Tag System**: Organize and filter files with custom tags

### Cloud & Sync
- **Multi-Cloud Integration**: Sync with Google Drive, OneDrive, and Dropbox
- **Multi-Device Sync**: Share settings and templates across devices via Firebase
- **Auto-Backup**: Optional automatic backup before file operations for data safety
- **Activity Logging**: Comprehensive operation logs with export capability

### User Experience
- **Material 3 Design**: Modern, beautiful interface with dynamic color theming
- **Image-Based Themes**: Customize app appearance with your own images (blur, overlay, brightness controls)
- **Dark Mode Support**: Full light/dark theme support with system integration
- **Smooth Animations**: Polished transitions and loading states
- **Accessibility**: Full screen reader support and content descriptions

## Screenshots

> Screenshots will be added in the next release. The app features Material 3 design with dynamic theming and smooth animations.

**Key Screens:**
- Home Screen with quick actions and feature discovery
- File Selection with multi-select and folder import
- Rename Configuration with real-time preview
- Progress Tracking with detailed operation status
- Template Management for reusable patterns
- Settings with comprehensive customization options


## Permissions

The app requires the following permissions based on your Android version:

| Android Version | Required Permissions |
|----------------|---------------------|
| **Android 13+** | `READ_MEDIA_IMAGES`, `READ_MEDIA_VIDEO`, `READ_MEDIA_AUDIO` |
| **Android 11-12** | `MANAGE_EXTERNAL_STORAGE` |
| **Android 10 and below** | `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE` |

**Additional Permissions:**
- `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_DATA_SYNC` - For background operations
- `POST_NOTIFICATIONS` - For operation progress notifications
- `CAMERA` - Optional, for QR code scanning
- `INTERNET` - Optional, for cloud sync features
- `ACCESS_FINE_LOCATION` - Optional, for GPS-based naming

All permissions are requested at runtime with clear explanations, and can be managed through the in-app Settings screen.


## Architecture & Technology

### Architecture Pattern
Built with **Clean Architecture** principles, ensuring separation of concerns and testability:

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (Jetpack Compose UI + ViewModels)     │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│           Domain Layer                  │
│  (Business Logic + Use Cases)           │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│            Data Layer                   │
│  (Repositories + Data Sources)          │
└─────────────────────────────────────────┘
```

### Tech Stack

**Core Technologies:**
- **Language**: Kotlin 2.0+ with Coroutines and Flow
- **UI**: Jetpack Compose with Material 3 Design
- **Architecture**: Clean Architecture + MVI/MVVM pattern
- **Dependency Injection**: Hilt
- **Async Operations**: Kotlin Coroutines + StateFlow/SharedFlow
- **Navigation**: Compose Navigation with type-safe routes

**Data & Storage:**
- **Local Database**: Room for structured data
- **Preferences**: DataStore for settings persistence
- **Cloud Backend**: Firebase (Firestore, Auth, Storage, Crashlytics)

**Media & AI:**
- **Image Loading**: Coil with LRU caching
- **Machine Learning**: ML Kit (on-device) for image analysis, OCR, and QR codes
- **Image Processing**: Android Palette API for dynamic theming

**Background Processing:**
- **WorkManager**: Scheduled and reliable background tasks
- **FileObserver**: Real-time folder monitoring
- **Foreground Services**: Long-running operations with notifications

**Quality Assurance:**
- **Testing**: JUnit 5, MockK, Turbine, Compose Testing
- **Code Quality**: Ktlint, Detekt
- **Build System**: Gradle 8.x with Kotlin DSL and Version Catalogs

### Performance Optimizations
- **Memory Efficiency**: Lazy sequences, bitmap recycling, LRU caching
- **Coroutine Optimization**: Structured concurrency with proper dispatcher selection
- **UI Performance**: Efficient recomposition, DiffUtil for lists, lazy loading
- **Build Speed**: Configuration cache, parallel execution, dependency optimization

## Getting Started

### Prerequisites
- **Android Studio**: Hedgehog (2023.1.1) or later
- **JDK**: 17 or higher
- **Android SDK**: 
  - Compile SDK: API 34
  - Minimum SDK: API 26 (Android 8.0)
- **Gradle**: 8.2+
- **Kotlin**: 2.0+

### Installation

#### For Users
**Download the latest APK** from the [Releases](https://github.com/yourusername/conversion/releases) page.

#### For Developers

1. **Clone the repository**
```bash
git clone https://github.com/yourusername/conversion.git
cd conversion
```

2. **Open in Android Studio**
   - Launch Android Studio Hedgehog (2023.1.1) or later
   - Select "Open an Existing Project"
   - Navigate to the cloned directory
   - Wait for Gradle sync to complete

3. **Build the project**
```bash
./gradlew build
```

4. **Run tests**
```bash
./gradlew test
```

5. **Install on device/emulator**
```bash
./gradlew installDebug
```

> **Note:** For Windows, use `gradlew.bat` instead of `./gradlew`

### Firebase Setup (Optional for Cloud Features)

Cloud sync, multi-device templates, and backup features require Firebase configuration:

1. **Create a Firebase project** at [Firebase Console](https://console.firebase.google.com)
2. **Add an Android app** to your project
   - Package name: `com.example.conversion`
   - Download `google-services.json`
3. **Place configuration file**
   ```bash
   cp ~/Downloads/google-services.json app/
   ```
4. **Enable Firebase services**
   - Firestore Database (for template sync)
   - Authentication (Email/Google Sign-In)
   - Cloud Storage (for file backup)
   - Crashlytics (optional, for error reporting)
5. **Configure security rules** using [firestore.rules](firestore.rules)
6. **Rebuild the project**
   ```bash
   ./gradlew clean build
   ```

> **Note:** The app works fully offline without Firebase. Cloud features are optional enhancements.

### Project Structure

```
app/src/main/java/com/example/conversion/
├── domain/                 # Business logic layer
│   ├── model/             # Domain models
│   ├── repository/        # Repository interfaces
│   ├── usecase/           # Use cases
│   └── common/            # Shared domain types
├── data/                  # Data layer
│   ├── repository/        # Repository implementations
│   ├── source/            # Data sources (MediaStore, Room, Firebase)
│   ├── local/             # Local storage (DAOs, entities)
│   └── manager/           # Specialized managers
├── presentation/          # UI layer
│   ├── ui/                # Compose screens
│   ├── viewmodel/         # ViewModels
│   ├── navigation/        # Navigation setup
│   └── theme/             # Material 3 theming
├── di/                    # Dependency injection modules
└── service/               # Background services
```

## Usage Guide

### Quick Start

1. **Launch the app** and grant required permissions
2. **Select files** - Choose from gallery or import entire folders
3. **Configure naming** - Set numbering options, prefix, and patterns
4. **Preview changes** - Review the before/after filenames
5. **Execute rename** - Process files with progress tracking
6. **Review results** - Check success/error summary

### Common Workflows

#### Basic Batch Rename
```
Main Screen → Batch Process
  → Select Files (multi-select from gallery)
  → Configure (prefix: "photo_", start: 1, digits: 3)
  → Preview (photo_001.jpg, photo_002.jpg...)
  → Start → Done!
```

#### Folder Monitoring
```
Main Screen → Folder Monitoring
  → Select Folder to Watch
  → Configure Auto-Rename Rules
  → Enable Monitoring
  → New files automatically renamed!
```

#### AI-Powered Naming
```
Batch Process → Select Images
  → Enable AI Suggestions
  → Review suggested names (sunset_beach, mountain_view...)
  → Accept or customize
  → Apply!
```

## Customization

### Themes
- **Light/Dark Mode**: Automatic system theme following
- **Dynamic Colors**: Material You color extraction from wallpaper
- **Custom Image Themes**: Use your own images as app background with blur, overlay, and brightness controls

### Pattern Templates
Save frequently used naming patterns:
- `{date}_{counter}` → 2024-12-12_001.jpg
- `{location}_{time}` → Paris_14-30-45.jpg
- `vacation_{counter}` → vacation_001.jpg

## Testing

The project maintains comprehensive test coverage across all layers:

```bash
# Run all tests
./gradlew test

# Run unit tests only
./gradlew testDebugUnitTest

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Run with coverage report
./gradlew testDebugUnitTestCoverage

# Run specific test class
./gradlew test --tests "BatchRenameUseCaseTest"

# Run static analysis
./gradlew detekt
```

**Test Coverage Goals:**
- Domain Layer: 100% (Pure business logic)
- Data Layer: 90%+ (Repository implementations)
- Overall Project: 70%+ (Including UI tests)

**Testing Tools:**
- **JUnit 5** - Test framework with parameterized tests
- **MockK** - Mocking library for Kotlin
- **Turbine** - Flow testing utilities
- **Compose Testing** - UI semantics-based testing
- **TestDataFactory** - Consistent test data generation
- **Detekt** - Static code analysis

**Test Structure:**
```
app/src/
├── test/                  # Unit tests
│   └── java/.../
│       ├── domain/       # Use case tests
│       ├── data/         # Repository tests
│       └── util/         # Test utilities
└── androidTest/          # Instrumented tests
    └── java/.../
        └── ui/           # Compose UI tests
```

## Contributing

We welcome contributions! Here's how you can help:

### Reporting Issues
- Use [GitHub Issues](https://github.com/yourusername/conversion/issues) for bug reports and feature requests
- Include Android version, device model, and steps to reproduce
- Attach logs if possible (Settings → Export Logs)

### Submitting Pull Requests

1. **Fork the repository**
```bash
git clone https://github.com/yourusername/conversion.git
cd conversion
git checkout -b feature/amazing-feature
```

2. **Follow coding standards**
```bash
# Format code before committing
./gradlew ktlintFormat

# Run static analysis
./gradlew detekt
```

3. **Write tests**
   - Add unit tests for new business logic
   - Add UI tests for new screens
   - Ensure all tests pass

4. **Commit with clear messages**
```bash
git commit -m "[FEATURE] Add batch format conversion"
```

5. **Push and create PR**
```bash
git push origin feature/amazing-feature
```

### Development Guidelines

- Follow [Clean Architecture principles](docs/adr/001-clean-architecture.md)
- Use MVI pattern for state management (see [ADR 002](docs/adr/002-mvi-pattern.md))
- Write KDoc comments for public APIs
- Maintain test coverage above 70%
- Use meaningful commit messages (Conventional Commits)
- Run `./gradlew ktlintFormat` before committing
- Ensure `./gradlew detekt` passes without warnings

**Code Style:**
```kotlin
// Good: Clear, testable use case
class SaveTemplateUseCase @Inject constructor(
    private val repository: TemplateRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<RenameTemplate, Unit>(dispatcher) {
    override suspend fun execute(params: RenameTemplate): Unit {
        return when (val result = repository.saveTemplate(params)) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }
}
```

**Commit Message Format:**
```
[FEATURE] Add batch format conversion
[FIX] Resolve memory leak in file observer
[REFACTOR] Simplify template repository
[DOCS] Update architecture diagrams
[TEST] Add coverage for rename use cases
```

## Roadmap

### Phase 1: Foundation (Completed)
- [x] Core batch rename functionality
- [x] Permission system with first-launch request
- [x] Material 3 theming with dynamic colors
- [x] Image-based theme customization
- [x] Clean Architecture + MVI implementation
- [x] Comprehensive test coverage
- [x] Firebase integration

### Phase 2: Enhancement & Polish (In Progress)
- [x] Navigation system overhaul (21+ screens)
- [ ] Complete UI wiring for all screens
- [ ] Advanced image theme editor
- [ ] Folder monitoring improvements
- [ ] Cloud sync reliability enhancements
- [ ] Performance optimizations
- [ ] Accessibility improvements

### Phase 3: Advanced Features (Q2 2026)
- [ ] Home screen widget support
- [ ] Android shortcuts integration
- [ ] Advanced format conversion (batch convert)
- [ ] Statistics and analytics dashboard
- [ ] Enhanced search and filtering
- [ ] QR code history and management
- [ ] Backup/Restore system

### Phase 4: Collaboration & Intelligence (Q3 2026)
- [ ] Team collaboration features
- [ ] Custom ML model training
- [ ] Advanced regex pattern builder
- [ ] Workflow automation engine
- [ ] Plugin system for extensibility

### Long-term Vision (2027+)
- Multi-platform support (iOS, Desktop, Web)
- Enterprise features (compliance, audit trails)
- Advanced AI-powered organization
- Integration marketplace
- White-label solutions

See [Project Board](https://github.com/yourusername/conversion/projects) for detailed progress tracking.

## Documentation

### Architecture & Design
- **[Architecture Model](docs/ARCHITECTURE_MODEL.md)** - Comprehensive system architecture
- **[Architecture Decision Records](docs/adr/)** - Key architectural decisions
  - [ADR 001: Clean Architecture](docs/adr/001-clean-architecture.md)
  - [ADR 002: MVI Pattern](docs/adr/002-mvi-pattern.md)
  - [ADR 003: Repository Pattern](docs/adr/003-repository-pattern.md)
  - [ADR 004: Use Case Pattern](docs/adr/004-use-case-pattern.md)

### Implementation Guides
- **[Chunk Completion Docs](docs/CHUNKS_COMPLETION/)** - Detailed feature implementation
- **[UI Guidelines](docs/UI_GUIDELINES.md)** - Design system and patterns
- **[Navigation Wiring](docs/NAVIGATION_WIRING_ROADMAP.md)** - Screen navigation architecture
- **[Accessibility Guidelines](docs/ACCESSIBILITY_GUIDELINES.md)** - A11y best practices

### Security & Operations
- **[Firebase Security Rules](docs/FIREBASE_SECURITY_RULES.md)** - Cloud security configuration
- **[Permission System](docs/Permission.md)** - Runtime permission handling

### Team Collaboration
- **[Work Division](docs/Division/WORK_DIVISION.md)** - Team task breakdown
- **[Kai's Tasks](docs/Division/KAI_TASKS.md)** - Backend/Domain work
- **[Sokchea's Tasks](docs/Division/SOKCHEA_TASKS.md)** - Frontend/UI work

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

- **Email**: support@filesmanagement.app
- **Bug Reports**: [GitHub Issues](https://github.com/yourusername/conversion/issues)
- **Discussions**: [GitHub Discussions](https://github.com/yourusername/conversion/discussions)
- **Wiki**: [Project Wiki](https://github.com/yourusername/conversion/wiki)

## Acknowledgments

Built with amazing open-source technologies:

- **[Material 3 Design](https://m3.material.io/)** - Google's design system
- **[ML Kit](https://developers.google.com/ml-kit)** - On-device machine learning
- **[Firebase](https://firebase.google.com/)** - Backend services
- **[Jetpack Compose](https://developer.android.com/jetpack/compose)** - Modern UI toolkit
- **[Hilt](https://dagger.dev/hilt/)** - Dependency injection
- **[Room](https://developer.android.com/training/data-storage/room)** - Local database
- **[Coil](https://coil-kt.github.io/coil/)** - Image loading
- **[Android Open Source Project](https://source.android.com/)** - Foundation libraries

Special thanks to the Kotlin and Android developer communities for their continuous support and contributions.

---

<div align="center">

**Files Management Service** - Intelligent File Organization for Android

Made by [Kai](https://github.com/kai) & [Sokchea](https://github.com/sokchea)

*Last Updated: January 23, 2026*

[Back to Top](#files-management-service)

</div>