# Work Division - Auto Rename File Service
## Team Structure: 2 Developers

**Last Updated:** November 17, 2025

---

## 👥 Team Roles & Responsibilities

### Kai (Backend/Core Features Specialist)
**Focus:** Domain layer, data layer, business logic, file operations, permissions
**Primary Skills:** Kotlin, Android APIs, file system operations, architecture

### Sokchea (Frontend/UI Specialist)
**Focus:** Presentation layer, UI/UX, Compose, state management, user interactions
**Primary Skills:** Jetpack Compose, Material 3, UI/UX design, animations

---

## 📋 Work Distribution by Phase

### **Phase 1: Foundation** ✅ (COMPLETED)
**Kai has already setup the initial stages which is Phase 1, Chunk 1, 2.**

---

### **Phase 2: Core Features (Batch Rename MVP)** 📁

#### **CHUNK 2: Permissions System**
**Status:** 75% Complete - Missing Data Layer

| Developer | Tasks |
|-----------|-------|
| **Kai** | ✅ Domain: Permission models (Permission, PermissionStatus, PermissionState) |
| **Kai** | ✅ Domain: Use cases (Check, GetRequired, HasMediaAccess, Observe) |
| **Kai** | ❌ **Data: PermissionsManagerImpl** (CRITICAL - MISSING) |
| **Kai** | Unit tests for PermissionsManagerImpl |
| **Kai** | ✅ Presentation: PermissionsContract (State/Events/Actions) |
| **Kai** | ✅ Presentation: PermissionsViewModel |
| **Kai** | ✅ Presentation: PermissionHandler composable |
| **Kai** | UI tests for PermissionHandler |

**Dependencies:** Sokchea's work depends on Kai completing PermissionsManagerImpl

---

#### **CHUNK 3: File Selection Feature**

| Developer | Tasks |
|-----------|-------|
| **Kai** | Domain: FileItem model (path, name, size, type, thumbnail URI) |
| **Kai** | Domain: GetMediaFiles use case |
| **Kai** | Data: MediaStoreRepository implementation |
| **Kai** | Query ContentResolver for images/videos |
| **Kai** | Implement pagination for large galleries |
| **Sokchea** | Presentation: FileSelectionContract |
| **Sokchea** | Presentation: FileSelectionViewModel |
| **Sokchea** | Presentation: File picker UI with grid layout |
| **Sokchea** | Multi-select functionality with checkboxes |
| **Sokchea** | Thumbnail loading with Coil |
| **Sokchea** | Loading states and error handling UI |

**Parallel Work:** Can work simultaneously after models are defined

---

#### **CHUNK 4: Batch Rename Logic Core**

| Developer | Tasks |
|-----------|-------|
| **Kai** | Domain: RenameConfig model (prefix, startNum, digitCount) |
| **Kai** | Domain: GenerateFilename use case |
| **Kai** | Data: FileOperationsManager (validation, conflicts) |
| **Kai** | Filename validation logic (illegal chars, length limits) |
| **Kai** | Duplicate name detection and handling |
| **Kai** | Unit tests for rename logic |
| **Sokchea** | Presentation: RenameConfigContract |
| **Sokchea** | Presentation: Configuration UI screen |
| **Sokchea** | Input fields for prefix, start number, digit count |
| **Sokchea** | Live preview of filename pattern |
| **Sokchea** | Validation error messages |

**Dependencies:** Sokchea needs RenameConfig model from Kai first

---

#### **CHUNK 5: Rename Execution**

| Developer | Tasks |
|-----------|-------|
| **Kai** | Domain: ExecuteBatchRename use case with Flow |
| **Kai** | Data: Actual file renaming with scoped storage API |
| **Kai** | MediaStore update after rename |
| **Kai** | Error recovery and rollback logic |
| **Sokchea** | Presentation: Progress indicator UI |
| **Sokchea** | Success/error result screens |
| **Sokchea** | Animated transitions and feedback |

**Dependencies:** Sequential - Kai must complete file operations first

---

#### **CHUNK 6: Destination Folder Selector**

| Developer | Tasks |
|-----------|-------|
| **Kai** | Domain: FolderInfo model |
| **Kai** | Domain: GetFolders use case |
| **Kai** | Data: Folder scanning with DocumentFile API |
| **Sokchea** | Presentation: FolderPickerContract |
| **Sokchea** | Presentation: Folder picker UI with navigation |
| **Sokchea** | Breadcrumb navigation and folder icons |

**Parallel Work:** Can work simultaneously after model definition

---

### **Phase 3: Advanced Features** 🚀

#### **CHUNK 7: Preview System**

| Developer | Tasks |
|-----------|-------|
| **Kai** | Domain: GeneratePreview use case (before/after mapping) |
| **Kai** | Validation: Name conflicts, character restrictions |
| **Sokchea** | Presentation: Preview list UI with warnings |
| **Sokchea** | Color-coded warnings (duplicates, invalid names) |

---

#### **CHUNK 8: Natural Sorting & Order Preservation**

| Developer | Tasks |
|-----------|-------|
| **Kai** | Domain: SortFiles use case with strategies |
| **Kai** | Implement natural, date, size, original sorting |
| **Sokchea** | Presentation: Sort options in configuration UI |
| **Sokchea** | Dropdown/radio buttons for sort selection |

---

#### **CHUNK 9: File Observer - Real-time Monitoring**

| Developer | Tasks |
|-----------|-------|
| **Kai** | Domain: FolderMonitor model |
| **Kai** | Domain: StartMonitoring/StopMonitoring use cases |
| **Kai** | Data: FileObserver implementation |
| **Kai** | Background: Foreground service setup |
| **Sokchea** | Presentation: Monitoring toggle UI |
| **Sokchea** | Active folder status indicator |
| **Sokchea** | Notification design for foreground service |

---

#### **CHUNK 10: Dynamic Theming from Images**

| Developer | Tasks |
|-----------|-------|
| **Kai** | Domain: ExtractPalette use case (Palette API) |
| **Kai** | Data: Safe URI handling, persistent permissions |
| **Sokchea** | Presentation: Background image picker |
| **Sokchea** | Dynamic color application to theme |
| **Sokchea** | Theme preview and reset option |

---

### **Phase 4: Smart Features** 🧠

#### **CHUNK 11: EXIF Metadata Extraction**

| Developer | Tasks |
|-----------|-------|
| **Kai** | Domain: MetadataExtractor use case |
| **Kai** | Data: ExifInterface parsing implementation |
| **Kai** | Extract date, location, camera model |
| **Sokchea** | Presentation: Variable picker UI |
| **Sokchea** | Metadata preview with sample values |

---

#### **CHUNK 12: Pattern Templates**

| Developer | Tasks |
|-----------|-------|
| **Kai** | Domain: Template model |
| **Kai** | Domain: SaveTemplate/LoadTemplate use cases |
| **Kai** | Data: Room database schema and DAO |
| **Sokchea** | Presentation: Template CRUD UI |
| **Sokchea** | Save, load, delete, quick apply buttons |

---

#### **CHUNK 13: AI-Powered Filename Suggestions**

| Developer | Tasks |
|-----------|-------|----------|----------|
| **Kai** | Domain: AnalyzeImage use case |
| **Kai** | Data: ML Kit image labeling integration |
| **Kai** | Handle ML Kit lifecycle and errors |
| **Sokchea** | Presentation: Suggestion chips UI |
| **Sokchea** | Auto-apply and manual selection options |

---

#### **CHUNK 14: Undo/Redo System**

| Developer | Tasks |
|-----------|-------|----------|----------|
| **Kai** | Domain: RenameHistory model |
| **Kai** | Domain: UndoRename/RedoRename use cases |
| **Kai** | Data: Room database for operation history |
| **Kai** | Implement actual undo file operations |
| **Sokchea** | Presentation: Undo/Redo buttons |
| **Sokchea** | History view with timestamps |
| **Sokchea** | Swipe to undo gesture |

---

#### **CHUNK 15: Regex Pattern Support**

| Developer | Tasks |
|-----------|-------|----------|----------|
| **Kai** | Domain: RegexRenameRule model |
| **Kai** | Domain: ApplyRegexPattern use case |
| **Kai** | Regex validation and error handling |
| **Sokchea** | Presentation: Regex builder UI |
| **Sokchea** | Common patterns library (presets) |
| **Sokchea** | Real-time regex validation feedback |

---

#### **CHUNK 16: Tag System for Files**

| Developer | Tasks |
|-----------|-------|----------|----------|
| **Kai** | Domain: Tag model |
| **Kai** | Domain: TagFile/SearchByTag use cases |
| **Kai** | Data: Room database for tag associations |
| **Sokchea** | Presentation: Tag management UI |
| **Sokchea** | Tag chips, color picker, filter UI |

---

### **Phase 5: Integration & Sync** ☁️

#### **CHUNK 17: Cloud Storage Integration**

| Developer | Tasks |
|-----------|-------|----------|----------|
| **Kai** | Domain: CloudSync abstraction |
| **Kai** | Data: Drive/Dropbox/OneDrive API implementations |
| **Kai** | OAuth authentication handling |
| **Kai** | Background: WorkManager for scheduled sync |
| **Sokchea** | Presentation: Cloud account linking UI |
| **Sokchea** | Sync settings and status indicators |

---

#### **CHUNK 18: QR Code Generation for Presets**

| Developer | Tasks |
|-----------|-------|----------|----------|
| **Kai** | Domain: GenerateQRCode use case |
| **Kai** | Data: Preset serialization to JSON |
| **Kai** | QR code library integration (ZXing) |
| **Sokchea** | Presentation: QR code display UI |
| **Sokchea** | QR scanner for import |

---

#### **CHUNK 19: OCR Integration**

| Developer | Tasks |
|-----------|-------|----------|----------|
| **Kai** | Domain: ExtractTextFromImage use case |
| **Kai** | Data: ML Kit text recognition |
| **Sokchea** | Presentation: Text extraction preview UI |
| **Sokchea** | Use extracted text in filename field |

---

#### **CHUNK 20: Multi-Device Sync**

| Developer | Tasks |
|-----------|-------|----------|----------|
| **Kai** | Domain: SyncPreferences use case |
| **Kai** | Data: Firebase Firestore implementation |
| **Kai** | Conflict resolution logic |
| **Sokchea** | Presentation: Account management UI |
| **Sokchea** | Sync status and manual sync button |

---

#### **CHUNK 21: Activity Log & Export**

| Developer | Tasks |
|-----------|-------|----------|----------|
| **Kai** | Domain: LogActivity use case |
| **Kai** | Data: Room database for activity history |
| **Kai** | Export to CSV/JSON logic |
| **Sokchea** | Presentation: Log viewer UI |
| **Sokchea** | Filter and search in logs |

---

### **Phase 6: Polish & Optimization** ✨

#### **CHUNK 22: Performance Optimization**

| Developer | Tasks |
|-----------|-------|----------|----------|
| **Kai** | Profiling: Android Profiler analysis |
| **Kai** | Optimization: Lazy sequences, Flow optimization |
| **Kai** | Memory leak detection and fixes |
| **Sokchea** | UI performance: Recomposition optimization |
| **Sokchea** | Image loading optimization (Coil config) |

---

#### **CHUNK 23: Comprehensive Testing**

| Developer | Tasks |
|-----------|-------|----------|----------|
| **Kai** | Unit tests: Use cases, repositories |
| **Kai** | Integration tests: Database, file operations |
| **Sokchea** | UI tests: Compose Testing |
| **Sokchea** | End-to-end flow tests |

---

#### **CHUNK 24: UI/UX Polish**

| Developer | Tasks |
|-----------|-------|----------|----------|
| **Sokchea** | Animations: Transitions, loading states |
| **Sokchea** | Empty states: Helpful guidance, onboarding |
| **Sokchea** | Error states: User-friendly messages |
| **Sokchea** | Success feedback animations |

---

#### **CHUNK 25: Accessibility & i18n**

| Developer | Tasks |
|-----------|-------|----------|----------|
| **Kai** | String resources extraction |
| **Kai** | Multi-language support setup |
| **Sokchea** | Accessibility: Content descriptions |
| **Sokchea** | TalkBack testing and fixes |
| **Sokchea** | RTL support implementation |

---

#### **CHUNK 26: Documentation & Code Cleanup**

| Developer | Tasks |
|-----------|-------|----------|----------|
| **Kai** | KDoc comments for domain/data layers |
| **Kai** | Architecture Decision Records (ADRs) |
| **Sokchea** | KDoc comments for presentation layer |
| **Both** | README updates and setup instructions |
| **Both** | Code style: Ktlint/Detekt cleanup |

---

## 📊 Workload Distribution Summary

### Kai (Backend/Core)

| Tasks |
|-------|
| Permissions, File Selection, Rename Logic, Execution |
| Preview, Undo/Redo, Testing, Optimization |
| Advanced Features, Sync, Polish |

**Key Responsibilities:**
- ✅ Permission system data layer (URGENT)
- File operations and MediaStore integration
- Business logic and validation
- Database operations (Room)
- API integrations (ML Kit, Cloud, Firebase)
- Unit and integration testing
- Performance profiling

---

### Sokchea (Frontend/UI)

| Tasks |
|-------|
| Permissions UI, File Picker, Config UI, Testing |
| Progress UI, Preview, Theme, Accessibility |
| Advanced UI, Animations, Polish |

**Key Responsibilities:**
- All Compose UI screens and components
- ViewModels and state management
- User interactions and gestures
- Animations and transitions
- Theme and styling
- UI testing with Compose Testing
- Accessibility implementation
- Empty and error states

---

## 📊 Visual Workflow Example: CHUNK 3 (File Selection)

### Timeline View (No Conflicts!)

```
Day 1 Morning (Kai):
├─ Create feature/chunk-3-domain branch
├─ domain/model/FileItem.kt ✅
├─ domain/model/FileFilter.kt ✅
├─ domain/repository/MediaRepository.kt ✅
├─ domain/usecase/GetMediaFilesUseCase.kt ✅
└─ Commit & Push → PR #1 to main
    Title: "[READY] CHUNK 3 Domain Models - Sokchea can start"

Day 1 Afternoon (Sokchea):
├─ Pull main (gets Kai's domain models) ✅
├─ Create feature/chunk-3-ui branch
├─ presentation/fileselection/FileSelectionContract.kt
│   └─ Uses: FileItem, FileFilter (Kai's models) ✅
├─ presentation/fileselection/FileSelectionViewModel.kt
│   └─ Uses: GetMediaFilesUseCase (Kai's interface) ✅
├─ presentation/fileselection/FileSelectionScreen.kt
│   └─ Uses fake data for preview
└─ Work continues...

Day 2 (Kai - In Parallel, NO CONFLICT):
├─ Create feature/chunk-3-data branch
├─ data/repository/MediaRepositoryImpl.kt ✅
│   └─ Different file than Sokchea!
├─ data/source/MediaStoreDataSource.kt ✅
│   └─ Different file than Sokchea!
├─ di/FileSelectionDataModule.kt ✅
│   └─ Separate DI file!
└─ Commit & Push → PR #2 to main

Day 2 (Sokchea - Completes UI):
├─ ui/components/FileGridItem.kt ✅
│   └─ Different file than Kai!
├─ ui/components/FileSelectionToolbar.kt ✅
│   └─ Different file than Kai!
└─ Commit & Push → PR #3 to main

Day 3 (Integration):
├─ Both PRs merged to main ✅
├─ No conflicts because different files! 🎉
├─ Sokchea rebases: Gets Kai's real implementation
├─ Sokchea removes fake data, uses real repository
├─ Both test together
└─ CHUNK 3 Complete! ✅

NO WAITING TIME! Maximum parallelism! 🚀
```

### File Ownership Map for CHUNK 3:

```
✅ = Safe to modify (no conflict)
⚠️ = Coordinate before modifying
❌ = Don't touch (other dev's file)

Kai's Files:
✅ domain/model/FileItem.kt
✅ domain/model/FileFilter.kt  
✅ domain/repository/MediaRepository.kt
✅ domain/usecase/GetMediaFilesUseCase.kt
✅ data/repository/MediaRepositoryImpl.kt
✅ data/source/MediaStoreDataSource.kt
✅ di/FileSelectionDataModule.kt
❌ presentation/fileselection/* (Sokchea's)
❌ ui/components/FileGridItem.kt (Sokchea's)

Sokchea's Files:
✅ presentation/fileselection/FileSelectionContract.kt
✅ presentation/fileselection/FileSelectionViewModel.kt
✅ presentation/fileselection/FileSelectionScreen.kt
✅ ui/components/FileGridItem.kt
✅ ui/components/FileSelectionToolbar.kt
❌ domain/* (Kai's - read only)
❌ data/* (Kai's)
❌ di/FileSelectionDataModule.kt (Kai's)

Shared (Coordinate):
⚠️ build.gradle.kts (if adding dependencies)
⚠️ AndroidManifest.xml (if adding permissions)
⚠️ strings.xml (use different keys)
```

---

## 🎯 Recommended Implementation Order

### Sprint 1 (Week 1-2): Core MVP
**Goal:** Get basic batch rename working

1. **CHUNK 2** (URGENT): Complete Permissions System
   - Dev A: Implement PermissionsManagerImpl ⚠️
   - Dev B: Test and integrate PermissionHandler
   
2. **CHUNK 3**: File Selection
   - Both work in parallel after model definition
   
3. **CHUNK 4**: Batch Rename Logic
   - Sequential: Dev A → Dev B
   
4. **CHUNK 5**: Rename Execution
   - Sequential: Dev A → Dev B
   
5. **CHUNK 6**: Destination Folder Selector
   - Both work in parallel

**Deliverable:** Working batch rename app ✅

---

### Sprint 2 (Week 3): Enhancement & Polish
**Goal:** Add preview, sorting, and basic testing

6. **CHUNK 7**: Preview System
7. **CHUNK 8**: Natural Sorting
8. **CHUNK 23** (Partial): Basic Testing
9. **CHUNK 24** (Partial): UI Polish

**Deliverable:** Production-ready MVP ✅

---

### Sprint 3+ (Optional): Advanced Features
**Goal:** Smart features and integrations (as time permits)

- CHUNK 10: Dynamic Theming
- CHUNK 14: Undo/Redo
- CHUNK 12: Pattern Templates
- Other chunks based on priorities

---

## 🔄 Conflict Prevention Strategy

### Git Workflow to Prevent Conflicts

#### Branch Strategy (Feature Branch Workflow)
```
main (stable/production)
├── kai (Kai's personal development branch)
│   ├── feature/chunk-2-permissions-data
│   ├── feature/chunk-3-file-selection-backend
│   └── feature/chunk-4-rename-logic
│
└── sokchea (Sokchea's personal development branch)
    ├── feature/chunk-2-permissions-ui
    ├── feature/chunk-3-file-selection-ui
    └── feature/chunk-4-rename-ui
```

#### Workflow Rules:
1. **Each developer has their own branch** (`kai`, `sokchea`)
2. **Feature branches are created from personal branches**
3. **Pull Requests merge feature → personal branch**
4. **Regular syncing**: Pull from main to personal branch daily
5. **Integration**: Merge personal branches to main when chunk is complete

---

### File Ownership Matrix (Who Touches What)

#### 🟢 Kai's Exclusive Files (No Conflicts):
```
domain/
├── model/               ✅ Kai creates, Sokchea reads only
├── repository/          ✅ Kai creates interfaces
└── usecase/            ✅ Kai implements

data/
├── repository/         ✅ Kai implements
├── source/             ✅ Kai implements
└── model/              ✅ Kai creates data models

di/
├── DataModule.kt       ✅ Kai owns
├── DomainModule.kt     ✅ Kai owns
└── DispatcherModule.kt ✅ Kai owns
```

#### 🔵 Sokchea's Exclusive Files (No Conflicts):
```
presentation/
├── <feature>/
│   ├── <Feature>Contract.kt    ✅ Sokchea creates (after domain models ready)
│   ├── <Feature>ViewModel.kt   ✅ Sokchea implements
│   └── <Feature>Screen.kt      ✅ Sokchea creates UI
│
ui/
├── components/         ✅ Sokchea creates reusable components
├── theme/              ✅ Sokchea modifies (except initial setup)
└── navigation/         ✅ Sokchea owns

MainActivity.kt         ✅ Sokchea owns (after initial setup)
```

#### ⚠️ Shared Files (Requires Coordination):
```
build.gradle.kts        ⚠️ Coordinate dependency additions
AndroidManifest.xml     ⚠️ Coordinate permission/component additions
libs.versions.toml      ⚠️ Coordinate version updates
```

**Solution for Shared Files:**
- Use comments to mark sections: `// Kai's dependencies` vs `// Sokchea's dependencies`
- Communicate before modifying
- One person adds dependency, other rebases immediately

---

### Dependency Handoff Protocol

#### Phase 1: Kai Creates Foundation (Blocking Work)
```kotlin
// Step 1: Kai commits domain models to main
domain/model/Permission.kt              ✅ Commit to main
domain/repository/PermissionsRepository.kt  ✅ Commit to main

// Step 2: Kai creates PR notification
// PR Title: "[READY] Domain Models for Permissions - Sokchea can start UI"
```

#### Phase 2: Sokchea Can Start UI (Non-Blocking)
```kotlin
// Sokchea pulls latest main, creates feature branch
feature/chunk-2-permissions-ui

// Sokchea works with interfaces, doesn't need implementation yet
presentation/permissions/PermissionsContract.kt
presentation/permissions/PermissionsViewModel.kt
presentation/permissions/PermissionHandler.kt
```

#### Phase 3: Kai Completes Implementation (Parallel)
```kotlin
// Kai continues on his branch
data/repository/PermissionsManagerImpl.kt
di/DataModule.kt (adds binding)
```

#### Phase 4: Integration (Both Ready)
```
1. Kai merges his data implementation to main
2. Sokchea rebases her feature branch on latest main
3. Sokchea tests integration
4. Sokchea merges UI to main
```

---

### Chunk-by-Chunk Conflict Prevention Plan

#### CHUNK 2: Permissions System
**Kai's Branch:** `feature/chunk-2-permissions-backend`
```kotlin
// Day 1: Kai commits interfaces (BLOCKS Sokchea)
domain/model/Permission.kt
domain/repository/PermissionsRepository.kt
domain/usecase/permissions/*

→ Merge to main, notify Sokchea
```

**Sokchea's Branch:** `feature/chunk-2-permissions-ui`
```kotlin
// Day 1: After Kai's merge, Sokchea starts (NO CONFLICT)
presentation/permissions/PermissionsContract.kt
presentation/permissions/PermissionsViewModel.kt
presentation/permissions/PermissionHandler.kt

// Sokchea works with mocked repository
```

**Kai's Branch:** `feature/chunk-2-permissions-data` (continues in parallel)
```kotlin
// Day 2: Kai implements data layer (NO CONFLICT with Sokchea's UI)
data/repository/PermissionsManagerImpl.kt
di/DataModule.kt
test/data/repository/PermissionsManagerImplTest.kt

→ Merge to main when complete
```

**Integration:**
```
1. Both merge their features
2. No conflicts because they touched different files
3. If there are conflicts in DI modules, resolve together
```

---

#### CHUNK 3: File Selection
**Separation Strategy:**

**Kai - Day 1:**
```kotlin
// Domain layer (Sokchea waits for this)
domain/model/FileItem.kt
domain/model/FileFilter.kt
domain/repository/MediaRepository.kt
domain/usecase/GetMediaFilesUseCase.kt

→ Merge to main, tag: "chunk-3-domain-ready"
```

**Sokchea - Day 1 (after Kai's merge):**
```kotlin
// Presentation layer (NO CONFLICT)
presentation/fileselection/FileSelectionContract.kt
presentation/fileselection/FileSelectionViewModel.kt
presentation/fileselection/FileSelectionScreen.kt
ui/components/FileGridItem.kt

// Can use fake/mock data for preview
```

**Kai - Day 2-3 (parallel):**
```kotlin
// Data layer (NO CONFLICT with Sokchea's work)
data/repository/MediaRepositoryImpl.kt
data/source/MediaStoreDataSource.kt
di/DataModule.kt (add MediaRepository binding)
```

---

#### CHUNK 4: Batch Rename Logic
**Clear Separation:**

**Kai owns:**
- `domain/model/RenameConfig.kt`
- `domain/usecase/GenerateFilenameUseCase.kt`
- `data/manager/FileOperationsManager.kt`

**Sokchea owns:**
- `presentation/renameconfig/RenameConfigContract.kt`
- `presentation/renameconfig/RenameConfigViewModel.kt`
- `presentation/renameconfig/RenameConfigScreen.kt`

**No file overlap = No conflicts!**

---

### Communication Protocol

#### Daily Sync
```
Morning Standup (Async via Slack/Discord):
- What I completed yesterday
- What I'm working on today
- What files I'll be touching
- Am I blocked? Do I need anything?

Example:
Kai: "Completed PermissionsManagerImpl, merging to main today. 
      Starting FileItem model tomorrow. @Sokchea you can start 
      FileSelectionViewModel after my morning commit."

Sokchea: "Working on PermissionHandler UI today. Will need 
          PermissionsManagerImpl merged by EOD to test integration. 
          No blockers currently."
```

#### PR Notification System
```
When merging to main:
1. Tag PR with: [READY FOR INTEGRATION]
2. Mention other developer: "@Sokchea - Domain models ready"
3. Use Discord/Slack notification
4. Other dev rebases immediately after merge
```

#### Conflict Resolution Rules
```
If merge conflict occurs:
1. Person who pushed second resolves conflict
2. Ask the other person for help if needed
3. Test thoroughly after resolving
4. Use git rerere to remember conflict resolutions
```

---

### Git Commands Reference

#### Kai's Daily Workflow:
```bash
# Start of day: Sync with main
git checkout kai
git pull origin main --rebase

# Create feature branch
git checkout -b feature/chunk-X-component
# ... make changes ...
git add .
git commit -m "Implement [feature]"

# Before creating PR: Rebase on main if needed
git checkout kai
git pull origin main --rebase
git checkout feature/chunk-X-component
git rebase kai

# Push and create PR
git push origin feature/chunk-X-component
# Create PR: feature/chunk-X-component → kai
```

#### Sokchea's Daily Workflow:
```bash
# Start of day: Sync with main
git checkout sokchea
git pull origin main --rebase

# Create feature branch
git checkout -b feature/chunk-X-ui
# ... make changes ...
git add .
git commit -m "Implement [feature] UI"

# Before creating PR: Rebase on main if needed
git checkout sokchea
git pull origin main --rebase
git checkout feature/chunk-X-ui
git rebase sokchea

# Push and create PR
git push origin feature/chunk-X-ui
# Create PR: feature/chunk-X-ui → sokchea
```

#### When Other Dev Merges:
```bash
# Immediately pull the changes from main
git checkout <your-branch>
git pull origin main --rebase

# If you have a feature branch in progress:
git checkout feature/your-feature
git rebase <your-branch>
# Resolve any conflicts
git rebase --continue
```

---

### Testing Strategy to Prevent Integration Issues

#### Kai's Testing (Before PR):
```kotlin
// 1. Unit tests for use cases
CheckPermissionsUseCaseTest.kt

// 2. Repository implementation tests
PermissionsManagerImplTest.kt

// 3. Mock-based tests (so Sokchea can run without full implementation)
// Use interfaces, not concrete implementations in tests
```

#### Sokchea's Testing (Before PR):
```kotlin
// 1. ViewModel tests with mocked use cases
PermissionsViewModelTest.kt

// 2. Composable preview tests
@Preview
@Composable
fun PermissionHandlerPreview() { ... }

// 3. UI tests with fake data
PermissionHandlerTest.kt
```

#### Integration Testing (After Both Merge):
```kotlin
// Both developers coordinate to run:
1. Build the app together
2. Test end-to-end flows
3. Fix any integration issues collaboratively
```

---

### Emergency Conflict Resolution

#### If Both Touch Same File Accidentally:

**Option 1: Rebase and Resolve**
```bash
# Person B (second to merge) does:
git checkout feature/your-feature
git fetch origin main
git rebase origin/main

# Resolve conflicts in the file
# Test that everything works
git add <resolved-files>
git rebase --continue
```

**Option 2: Communicate and Coordinate**
```
Person A: "I need to modify DataModule.kt to add permissions binding"
Person B: "OK, I also need it for file selection. You go first."
Person A: *makes change, merges*
Person B: *rebases, adds their change, merges*
```

**Option 3: Pair Programming**
```
For shared files like DI modules:
- Schedule a 30-minute session
- Both work together on the same screen
- One person commits the joint work
- No conflicts possible!
```

---

## 🔄 GitHub Project Kanban Structure

### Recommended Columns:

1. **📋 Backlog** - All future chunks not yet started
2. **🎯 Ready** - Next chunks with no dependencies blocking
3. **🏗️ In Progress (Kai)** - Kai's current work
4. **🏗️ In Progress (Sokchea)** - Sokchea's current work
5. **👀 Review** - Completed work awaiting code review
6. **🔄 Integration** - Both parts done, testing together
7. **✅ Done** - Merged and completed work

### Issue Labeling Strategy:

**By Developer:**
- `kai` - Kai tasks
- `sokchea` - Sokchea tasks
- `both` - Requires collaboration

**By Priority:**
- `priority: high` - Must complete for MVP
- `priority: medium` - Important but not critical
- `priority: low` - Nice to have

**By Type:**
- `domain` - Domain layer work
- `data` - Data layer work
- `presentation` - UI/Presentation layer
- `testing` - Test implementation
- `documentation` - Docs and comments

**By Phase:**
- `phase-2: core` - Phase 2 chunks
- `phase-3: advanced` - Phase 3 chunks
- `phase-4: smart` - Phase 4 chunks
- `phase-5: integration` - Phase 5 chunks
- `phase-6: polish` - Phase 6 chunks

**Special:**
- `blocked` - Waiting on dependency
- `urgent` - Critical fix needed
- `bug` - Bug fix
- `enhancement` - New feature
- `ready-for-integration` - Both parts complete, needs integration test

---

## �️ DI Module Coordination (Conflict Hotspot!)

DI modules are the most common source of conflicts. Here's how to handle them:

### Strategy 1: Separate DI Files Per Feature
```kotlin
// Instead of one big DataModule.kt, split by feature:

di/
├── DataModule.kt              // Core only (DataStore, Context)
├── PermissionsDataModule.kt   // Kai owns
├── FileSelectionDataModule.kt // Kai owns
├── RenameDataModule.kt        // Kai owns
└── ThemeDataModule.kt         // Sokchea owns

// In each module:
@Module
@InstallIn(SingletonComponent::class)
object PermissionsDataModule {
    @Provides
    @Singleton
    fun providePermissionsRepository(
        @ApplicationContext context: Context
    ): PermissionsRepository = PermissionsManagerImpl(context)
}
```

**Benefits:**
- No merge conflicts!
- Clear ownership
- Easy to review
- Can merge independently

### Strategy 2: Reserved Sections in Shared Files
```kotlin
// DataModule.kt
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    
    // ==================== CORE (Kai) ====================
    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.dataStore
    
    @Provides
    @Singleton
    fun providePreferencesRepository(
        dataStore: DataStore<Preferences>
    ): PreferencesRepository = PreferencesRepositoryImpl(dataStore)
    
    // ==================== PERMISSIONS (Kai) ====================
    @Provides
    @Singleton
    fun providePermissionsRepository(
        @ApplicationContext context: Context
    ): PermissionsRepository = PermissionsManagerImpl(context)
    
    // ==================== FILE SELECTION (Kai - Reserved) ====================
    // TODO: Kai will add MediaRepository here
    
    // ==================== THEME (Sokchea - Reserved) ====================
    // TODO: Sokchea will add ThemeRepository here
    
}
```

**Rules:**
1. Add section comments with owner name
2. Reserve space with TODO comments
3. Only modify your section
4. Other dev rebases to see your additions

### Strategy 3: Gradle Module Per Feature (Advanced)
```
// For larger projects, use Gradle modules:
:core               // Shared base
:feature:permissions // Kai's module
:feature:fileselection // Kai's module  
:feature:theme       // Sokchea's module
:app                 // Integration module (both)

// Each feature has its own DI module
// No conflicts possible!
```

**Recommended for this project:** Strategy 1 (Separate DI files per feature)

---

## 📋 Checklist Before Creating PR

### Kai's PR Checklist:
```
□ Code compiles without errors
□ All unit tests pass
□ Added KDoc comments to public APIs
□ Updated DI modules in my designated section
□ No TODOs or commented code
□ Follows established patterns from CHUNK 1
□ Domain models/interfaces are stable (won't change soon)
□ Tested with mock data if Sokchea's UI isn't ready
□ PR description explains what Sokchea can now build
□ Tagged with appropriate labels

Title format: 
"[CHUNK X] Feature Name - Backend Implementation"
"[READY] Domain Models for Feature X - Sokchea can start UI"
```

### Sokchea's PR Checklist:
```
□ UI previews work correctly
□ Code compiles without errors
□ ViewModel tests pass (with mocked use cases)
□ UI follows Material 3 guidelines
□ Accessibility content descriptions added
□ Loading/error states implemented
□ No hardcoded strings (use string resources)
□ Animations are smooth (tested on emulator)
□ Works with both light and dark theme
□ Tested with fake data if Kai's implementation isn't ready
□ PR description shows screenshots/video
□ Tagged with appropriate labels

Title format:
"[CHUNK X] Feature Name - UI Implementation"
"[INTEGRATION] Feature X - Ready to merge with backend"
```

---

## 🚨 Critical Dependencies

### Sokchea is blocked until:
- ✅ CHUNK 2: Kai completes domain models (Permission, PermissionStatus, PermissionState)
  - **Solution:** Kai commits domain package first, Sokchea can start immediately
  
- ✅ CHUNK 3: Kai completes FileItem model
  - **Solution:** Kai commits model + interface day 1, Sokchea uses fake data for UI
  
- ✅ CHUNK 4: Kai completes RenameConfig model
  - **Solution:** Kai commits model + use case interface, Sokchea mocks in ViewModel

### Kai is blocked until:
- ⚠️ NEVER! Kai can always work ahead on domain/data layers
- 💡 Kai can create mock ViewModels if needed for testing repositories

### Best Practice:
- **Kai works in order:** Domain → Data → (wait for Sokchea)
- **Sokchea works after domain ready:** ViewModel → UI → Integration
- **Overlap allowed:** Kai can start next chunk while Sokchea finishes UI
- **Regular sync meetings** to unblock dependencies
- **Use feature branches** to work in parallel safely
- **Merge frequently** (small PRs, not big ones)

---

## 📝 Notes

1. **Estimates are approximate** - Adjust based on actual progress
2. **Prioritize Phase 2 chunks** - These are essential for MVP
3. **Phase 3-6 can be flexible** - Implement based on time and priorities
4. **Testing should be continuous** - Don't leave all testing for CHUNK 23
5. **Code reviews are essential** - Review each other's PRs regularly
6. **Communication is key** - Daily standups or async updates recommended

---

## 🎓 Skills Development Opportunities

### Kai can learn:
- Advanced Compose patterns from Sokchea
- UI/UX best practices
- Animation and transitions

### Sokchea can learn:
- Android file system operations from Kai
- Repository pattern implementation
- Database design with Room

**Recommendation:** Pair programming sessions for complex chunks like CHUNK 5 and CHUNK 14

---

**Last Updated:** November 17, 2025  
**Project Status:** Phase 2 - CHUNK 2 at 75% (blocked by PermissionsManagerImpl)  
**Next Milestone:** Complete CHUNK 2 and start CHUNK 3

