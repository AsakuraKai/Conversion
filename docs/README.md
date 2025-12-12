# Files Management Service

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

*Coming soon*


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

1. **Clone the repository**
```bash
git clone https://github.com/yourusername/conversion.git
cd conversion
```

2. **Build the project**
```bash
./gradlew build
```

3. **Run tests**
```bash
./gradlew test
```

4. **Install on device/emulator**
```bash
./gradlew installDebug
```

### Firebase Setup (Optional for Cloud Features)

If you want to use cloud sync features:

1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com)
2. Download `google-services.json` and place it in `app/`
3. Enable Firestore, Authentication, and Storage in Firebase Console
4. Rebuild the project

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

# Run with coverage report
./gradlew testDebugUnitTestCoverage

# Run specific test class
./gradlew test --tests "BatchRenameUseCaseTest"
```

**Test Coverage Goals:**
- Domain Layer: 100%
- Data Layer: 90%+
- Overall Project: 70%+

**Testing Tools:**
- JUnit 5 for test framework
- MockK for mocking
- Turbine for Flow testing
- Compose Testing for UI tests

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
- Use MVI pattern for state management
- Write KDoc comments for public APIs
- Maintain test coverage above 70%
- Use meaningful commit messages

See [CONTRIBUTING.md](CONTRIBUTING.md) for detailed guidelines.

## Roadmap

### Current Phase: Enhancement & Polish
- [x] Core batch rename functionality
- [x] Permission system with first-launch request
- [x] Material 3 theming with dynamic colors
- [x] Image-based theme customization
- [ ] Complete batch rename UI overhaul
- [ ] Advanced image theme editor
- [ ] Folder monitoring improvements
- [ ] Cloud sync reliability enhancements

### Upcoming Features
- [ ] Home screen widget support
- [ ] Android shortcuts integration
- [ ] Advanced format conversion (batch convert)
- [ ] Statistics and analytics dashboard
- [ ] Enhanced search and filtering
- [ ] Team collaboration features

### Long-term Vision
- Multi-platform support (iOS, Desktop)
- Plugin system for custom operations
- Enterprise features (compliance, audit trails)
- Advanced ML model customization
- Workflow automation with rules engine

See [Project Board](https://github.com/yourusername/conversion/projects) for detailed progress tracking.

## Documentation

- **[Architecture Decision Records](docs/adr/)** - Key architectural decisions
- **[Chunk Completion Docs](docs/CHUNKS_COMPLETION/)** - Detailed implementation guides
- **[UI Guidelines](docs/UI_GUIDELINES.md)** - Design system and patterns
- **[Accessibility Guidelines](docs/ACCESSIBILITY_GUIDELINES.md)** - A11y best practices
- **[Firebase Security](docs/FIREBASE_SECURITY_RULES.md)** - Cloud security configuration

## Acknowledgments

- **[Material 3 Design](https://m3.material.io/)** - Google's design system
- **[ML Kit](https://developers.google.com/ml-kit)** - On-device machine learning
- **[Firebase](https://firebase.google.com/)** - Backend services
- **[Jetpack Compose](https://developer.android.com/jetpack/compose)** - Modern UI toolkit
- **[Android Open Source Project](https://source.android.com/)** - Foundation libraries

---

*Last Updated: December 12, 2025*