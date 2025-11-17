# Work Division - Auto Rename File Service
## Team Structure: 2 Developers

**Project Duration:** 60-80 hours total (30-40 hours per developer)  
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

#### **CHUNK 2: Permissions System** (1-2 hours)
**Status:** 75% Complete - Missing Data Layer

| Developer | Tasks | Estimate | Priority |
|-----------|-------|----------|----------|
| **Kai** | ✅ Domain: Permission models (Permission, PermissionStatus, PermissionState) | 20 min | HIGH |
| **Kai** | ✅ Domain: Use cases (Check, GetRequired, HasMediaAccess, Observe) | 30 min | HIGH |
| **Kai** | ❌ **Data: PermissionsManagerImpl** (CRITICAL - MISSING) | 1 hour | **URGENT** |
| **Kai** | Unit tests for PermissionsManagerImpl | 30 min | HIGH |
| **Kai** | ✅ Presentation: PermissionsContract (State/Events/Actions) | 20 min | HIGH |
| **Kai** | ✅ Presentation: PermissionsViewModel | 30 min | HIGH |
| **Kai** | ✅ Presentation: PermissionHandler composable | 45 min | HIGH |
| **Kai** | UI tests for PermissionHandler | 30 min | MEDIUM |

**Dependencies:** Sokchea's work depends on Kai completing PermissionsManagerImpl

---

#### **CHUNK 3: File Selection Feature** (2-3 hours)

| Developer | Tasks | Estimate | Priority |
|-----------|-------|----------|----------|
| **Kai** | Domain: FileItem model (path, name, size, type, thumbnail URI) | 30 min | HIGH |
| **Kai** | Domain: GetMediaFiles use case | 45 min | HIGH |
| **Kai** | Data: MediaStoreRepository implementation | 1.5 hours | HIGH |
| **Kai** | Query ContentResolver for images/videos | - | - |
| **Kai** | Implement pagination for large galleries | - | - |
| **Sokchea** | Presentation: FileSelectionContract | 20 min | HIGH |
| **Sokchea** | Presentation: FileSelectionViewModel | 45 min | HIGH |
| **Sokchea** | Presentation: File picker UI with grid layout | 1.5 hours | HIGH |
| **Sokchea** | Multi-select functionality with checkboxes | - | - |
| **Sokchea** | Thumbnail loading with Coil | - | - |
| **Sokchea** | Loading states and error handling UI | - | - |

**Parallel Work:** Can work simultaneously after models are defined

---

#### **CHUNK 4: Batch Rename Logic Core** (2-3 hours)

| Developer | Tasks | Estimate | Priority |
|-----------|-------|----------|----------|
| **Kai** | Domain: RenameConfig model (prefix, startNum, digitCount) | 30 min | HIGH |
| **Kai** | Domain: GenerateFilename use case | 1 hour | HIGH |
| **Kai** | Data: FileOperationsManager (validation, conflicts) | 1 hour | HIGH |
| **Kai** | Filename validation logic (illegal chars, length limits) | - | - |
| **Kai** | Duplicate name detection and handling | - | - |
| **Kai** | Unit tests for rename logic | 45 min | HIGH |
| **Sokchea** | Presentation: RenameConfigContract | 20 min | HIGH |
| **Sokchea** | Presentation: Configuration UI screen | 1.5 hours | HIGH |
| **Sokchea** | Input fields for prefix, start number, digit count | - | - |
| **Sokchea** | Live preview of filename pattern | - | - |
| **Sokchea** | Validation error messages | - | - |

**Dependencies:** Sokchea needs RenameConfig model from Kai first

---

#### **CHUNK 5: Rename Execution** (2 hours)

| Developer | Tasks | Estimate | Priority |
|-----------|-------|----------|----------|
| **Kai** | Domain: ExecuteBatchRename use case with Flow | 1 hour | HIGH |
| **Kai** | Data: Actual file renaming with scoped storage API | 1 hour | HIGH |
| **Kai** | MediaStore update after rename | 30 min | HIGH |
| **Kai** | Error recovery and rollback logic | 30 min | MEDIUM |
| **Sokchea** | Presentation: Progress indicator UI | 45 min | HIGH |
| **Sokchea** | Success/error result screens | 30 min | HIGH |
| **Sokchea** | Animated transitions and feedback | 30 min | MEDIUM |

**Dependencies:** Sequential - Kai must complete file operations first

---

#### **CHUNK 6: Destination Folder Selector** (1-2 hours)

| Developer | Tasks | Estimate | Priority |
|-----------|-------|----------|----------|
| **Kai** | Domain: FolderInfo model | 20 min | MEDIUM |
| **Kai** | Domain: GetFolders use case | 30 min | MEDIUM |
| **Kai** | Data: Folder scanning with DocumentFile API | 1 hour | MEDIUM |
| **Sokchea** | Presentation: FolderPickerContract | 15 min | MEDIUM |
| **Sokchea** | Presentation: Folder picker UI with navigation | 1 hour | MEDIUM |
| **Sokchea** | Breadcrumb navigation and folder icons | - | - |

**Parallel Work:** Can work simultaneously after model definition

---

### **Phase 3: Advanced Features** 🚀

#### **CHUNK 7: Preview System** (1-2 hours)

| Developer | Tasks | Estimate | Priority |
|-----------|-------|----------|----------|
| **Kai** | Domain: GeneratePreview use case (before/after mapping) | 1 hour | MEDIUM |
| **Kai** | Validation: Name conflicts, character restrictions | 30 min | MEDIUM |
| **Sokchea** | Presentation: Preview list UI with warnings | 1 hour | MEDIUM |
| **Sokchea** | Color-coded warnings (duplicates, invalid names) | 30 min | MEDIUM |

---

#### **CHUNK 8: Natural Sorting & Order Preservation** (1 hour)

| Developer | Tasks | Estimate | Priority |
|-----------|-------|----------|----------|
| **Kai** | Domain: SortFiles use case with strategies | 1 hour | LOW |
| **Kai** | Implement natural, date, size, original sorting | - | - |
| **Sokchea** | Presentation: Sort options in configuration UI | 30 min | LOW |
| **Sokchea** | Dropdown/radio buttons for sort selection | - | - |

---

#### **CHUNK 9: File Observer - Real-time Monitoring** (2-3 hours)

| Developer | Tasks | Estimate | Priority |
|-----------|-------|----------|----------|
| **Kai** | Domain: FolderMonitor model | 30 min | LOW |
| **Kai** | Domain: StartMonitoring/StopMonitoring use cases | 30 min | LOW |
| **Kai** | Data: FileObserver implementation | 1.5 hours | LOW |
| **Kai** | Background: Foreground service setup | 1 hour | LOW |
| **Sokchea** | Presentation: Monitoring toggle UI | 30 min | LOW |
| **Sokchea** | Active folder status indicator | 30 min | LOW |
| **Sokchea** | Notification design for foreground service | 30 min | LOW |

---

#### **CHUNK 10: Dynamic Theming from Images** (2 hours)

| Developer | Tasks | Estimate | Priority |
|-----------|-------|----------|----------|
| **Kai** | Domain: ExtractPalette use case (Palette API) | 1 hour | MEDIUM |
| **Kai** | Data: Safe URI handling, persistent permissions | 1 hour | MEDIUM |
| **Sokchea** | Presentation: Background image picker | 1 hour | MEDIUM |
| **Sokchea** | Dynamic color application to theme | 1 hour | MEDIUM |
| **Sokchea** | Theme preview and reset option | 30 min | MEDIUM |

---

### **Phase 4: Smart Features** 🧠

#### **CHUNK 11: EXIF Metadata Extraction** (2 hours)

| Developer | Tasks | Estimate | Priority |
|-----------|-------|----------|----------|
| **Kai** | Domain: MetadataExtractor use case | 1 hour | LOW |
| **Kai** | Data: ExifInterface parsing implementation | 1 hour | LOW |
| **Kai** | Extract date, location, camera model | - | - |
| **Sokchea** | Presentation: Variable picker UI | 1 hour | LOW |
| **Sokchea** | Metadata preview with sample values | 30 min | LOW |

---

#### **CHUNK 12: Pattern Templates** (2 hours)

| Developer | Tasks | Estimate | Priority |
|-----------|-------|----------|----------|
| **Kai** | Domain: Template model | 30 min | LOW |
| **Kai** | Domain: SaveTemplate/LoadTemplate use cases | 30 min | LOW |
| **Kai** | Data: Room database schema and DAO | 1 hour | LOW |
| **Sokchea** | Presentation: Template CRUD UI | 1.5 hours | LOW |
| **Sokchea** | Save, load, delete, quick apply buttons | - | - |

---

#### **CHUNK 13: AI-Powered Filename Suggestions** (3-4 hours)

| Developer | Tasks | Estimate | Priority |
|-----------|-------|----------|----------|
| **Kai** | Domain: AnalyzeImage use case | 1 hour | LOW |
| **Kai** | Data: ML Kit image labeling integration | 2 hours | LOW |
| **Kai** | Handle ML Kit lifecycle and errors | - | - |
| **Sokchea** | Presentation: Suggestion chips UI | 1 hour | LOW |
| **Sokchea** | Auto-apply and manual selection options | 30 min | LOW |

---

#### **CHUNK 14: Undo/Redo System** (2-3 hours)

| Developer | Tasks | Estimate | Priority |
|-----------|-------|----------|----------|
| **Kai** | Domain: RenameHistory model | 30 min | MEDIUM |
| **Kai** | Domain: UndoRename/RedoRename use cases | 1 hour | MEDIUM |
| **Kai** | Data: Room database for operation history | 1 hour | MEDIUM |
| **Kai** | Implement actual undo file operations | 1 hour | MEDIUM |
| **Sokchea** | Presentation: Undo/Redo buttons | 30 min | MEDIUM |
| **Sokchea** | History view with timestamps | 1 hour | MEDIUM |
| **Sokchea** | Swipe to undo gesture | 30 min | LOW |

---

#### **CHUNK 15: Regex Pattern Support** (2 hours)

| Developer | Tasks | Estimate | Priority |
|-----------|-------|----------|----------|
| **Kai** | Domain: RegexRenameRule model | 30 min | LOW |
| **Kai** | Domain: ApplyRegexPattern use case | 1 hour | LOW |
| **Kai** | Regex validation and error handling | 30 min | LOW |
| **Sokchea** | Presentation: Regex builder UI | 1.5 hours | LOW |
| **Sokchea** | Common patterns library (presets) | 30 min | LOW |
| **Sokchea** | Real-time regex validation feedback | 30 min | LOW |

---

#### **CHUNK 16: Tag System for Files** (2-3 hours)

| Developer | Tasks | Estimate | Priority |
|-----------|-------|----------|----------|
| **Kai** | Domain: Tag model | 20 min | LOW |
| **Kai** | Domain: TagFile/SearchByTag use cases | 45 min | LOW |
| **Kai** | Data: Room database for tag associations | 1.5 hours | LOW |
| **Sokchea** | Presentation: Tag management UI | 1.5 hours | LOW |
| **Sokchea** | Tag chips, color picker, filter UI | - | - |

---

### **Phase 5: Integration & Sync** ☁️

#### **CHUNK 17: Cloud Storage Integration** (4-5 hours)

| Developer | Tasks | Estimate | Priority |
|-----------|-------|----------|----------|
| **Kai** | Domain: CloudSync abstraction | 1 hour | LOW |
| **Kai** | Data: Drive/Dropbox/OneDrive API implementations | 3 hours | LOW |
| **Kai** | OAuth authentication handling | 1 hour | LOW |
| **Kai** | Background: WorkManager for scheduled sync | 1 hour | LOW |
| **Sokchea** | Presentation: Cloud account linking UI | 1.5 hours | LOW |
| **Sokchea** | Sync settings and status indicators | 1 hour | LOW |

---

#### **CHUNK 18: QR Code Generation for Presets** (1-2 hours)

| Developer | Tasks | Estimate | Priority |
|-----------|-------|----------|----------|
| **Kai** | Domain: GenerateQRCode use case | 30 min | LOW |
| **Kai** | Data: Preset serialization to JSON | 30 min | LOW |
| **Kai** | QR code library integration (ZXing) | 30 min | LOW |
| **Sokchea** | Presentation: QR code display UI | 45 min | LOW |
| **Sokchea** | QR scanner for import | 45 min | LOW |

---

#### **CHUNK 19: OCR Integration** (2-3 hours)

| Developer | Tasks | Estimate | Priority |
|-----------|-------|----------|----------|
| **Kai** | Domain: ExtractTextFromImage use case | 1 hour | LOW |
| **Kai** | Data: ML Kit text recognition | 1.5 hours | LOW |
| **Sokchea** | Presentation: Text extraction preview UI | 1 hour | LOW |
| **Sokchea** | Use extracted text in filename field | 30 min | LOW |

---

#### **CHUNK 20: Multi-Device Sync** (3-4 hours)

| Developer | Tasks | Estimate | Priority |
|-----------|-------|----------|----------|
| **Kai** | Domain: SyncPreferences use case | 1 hour | LOW |
| **Kai** | Data: Firebase Firestore implementation | 2 hours | LOW |
| **Kai** | Conflict resolution logic | 1 hour | LOW |
| **Sokchea** | Presentation: Account management UI | 1.5 hours | LOW |
| **Sokchea** | Sync status and manual sync button | 30 min | LOW |

---

#### **CHUNK 21: Activity Log & Export** (2 hours)

| Developer | Tasks | Estimate | Priority |
|-----------|-------|----------|----------|
| **Kai** | Domain: LogActivity use case | 30 min | LOW |
| **Kai** | Data: Room database for activity history | 1 hour | LOW |
| **Kai** | Export to CSV/JSON logic | 1 hour | LOW |
| **Sokchea** | Presentation: Log viewer UI | 1 hour | LOW |
| **Sokchea** | Filter and search in logs | 30 min | LOW |

---

### **Phase 6: Polish & Optimization** ✨

#### **CHUNK 22: Performance Optimization** (3-4 hours)

| Developer | Tasks | Estimate | Priority |
|-----------|-------|----------|----------|
| **Kai** | Profiling: Android Profiler analysis | 1.5 hours | MEDIUM |
| **Kai** | Optimization: Lazy sequences, Flow optimization | 1.5 hours | MEDIUM |
| **Kai** | Memory leak detection and fixes | 1 hour | MEDIUM |
| **Sokchea** | UI performance: Recomposition optimization | 1.5 hours | MEDIUM |
| **Sokchea** | Image loading optimization (Coil config) | 1 hour | MEDIUM |

---

#### **CHUNK 23: Comprehensive Testing** (4-5 hours)

| Developer | Tasks | Estimate | Priority |
|-----------|-------|----------|----------|
| **Kai** | Unit tests: Use cases, repositories | 2.5 hours | HIGH |
| **Kai** | Integration tests: Database, file operations | 1.5 hours | MEDIUM |
| **Sokchea** | UI tests: Compose Testing | 2 hours | HIGH |
| **Sokchea** | End-to-end flow tests | 1.5 hours | MEDIUM |

---

#### **CHUNK 24: UI/UX Polish** (2-3 hours)

| Developer | Tasks | Estimate | Priority |
|-----------|-------|----------|----------|
| **Sokchea** | Animations: Transitions, loading states | 1.5 hours | MEDIUM |
| **Sokchea** | Empty states: Helpful guidance, onboarding | 1 hour | MEDIUM |
| **Sokchea** | Error states: User-friendly messages | 1 hour | MEDIUM |
| **Sokchea** | Success feedback animations | 30 min | LOW |

---

#### **CHUNK 25: Accessibility & i18n** (2-3 hours)

| Developer | Tasks | Estimate | Priority |
|-----------|-------|----------|----------|
| **Kai** | String resources extraction | 1 hour | MEDIUM |
| **Kai** | Multi-language support setup | 1 hour | MEDIUM |
| **Sokchea** | Accessibility: Content descriptions | 1.5 hours | MEDIUM |
| **Sokchea** | TalkBack testing and fixes | 1 hour | MEDIUM |
| **Sokchea** | RTL support implementation | 30 min | LOW |

---

#### **CHUNK 26: Documentation & Code Cleanup** (2 hours)

| Developer | Tasks | Estimate | Priority |
|-----------|-------|----------|----------|
| **Kai** | KDoc comments for domain/data layers | 1 hour | MEDIUM |
| **Kai** | Architecture Decision Records (ADRs) | 1 hour | LOW |
| **Sokchea** | KDoc comments for presentation layer | 1 hour | MEDIUM |
| **Both** | README updates and setup instructions | 1 hour | MEDIUM |
| **Both** | Code style: Ktlint/Detekt cleanup | 1 hour | LOW |

---

## 📊 Workload Distribution Summary

### Kai (Backend/Core)
**Total Estimated Hours:** 35-42 hours

| Priority | Tasks | Hours |
|----------|-------|-------|
| **HIGH** | Permissions, File Selection, Rename Logic, Execution | 12-15 |
| **MEDIUM** | Preview, Undo/Redo, Testing, Optimization | 10-12 |
| **LOW** | Advanced Features, Sync, Polish | 13-15 |

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
**Total Estimated Hours:** 33-40 hours

| Priority | Tasks | Hours |
|----------|-------|-------|
| **HIGH** | Permissions UI, File Picker, Config UI, Testing | 10-12 |
| **MEDIUM** | Progress UI, Preview, Theme, Accessibility | 12-15 |
| **LOW** | Advanced UI, Animations, Polish | 11-13 |

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

## 🔄 GitHub Project Kanban Structure

### Recommended Columns:

1. **📋 Backlog** - All future chunks not yet started
2. **🎯 Ready** - Next chunks with no dependencies blocking
3. **🏗️ In Progress (Dev A)** - Kai's current work
4. **🏗️ In Progress (Dev B)** - Sokchea's current work
5. **👀 Review** - Completed work awaiting code review
6. **✅ Done** - Merged and completed work

### Issue Labeling Strategy:

**By Developer:**
- `dev-a` - Kai tasks
- `dev-b` - Sokchea tasks
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

---

## 🚨 Critical Dependencies

### Sokchea is blocked until:
- ✅ CHUNK 2: Kai completes PermissionsManagerImpl
- ✅ CHUNK 4: Kai completes RenameConfig model
- ✅ CHUNK 5: Kai completes ExecuteBatchRename use case

### Best Practice:
- Kai should complete domain models first in each chunk
- Sokchea can start UI mockups while waiting
- Regular sync meetings to unblock dependencies
- Use feature branches to work in parallel safely

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
