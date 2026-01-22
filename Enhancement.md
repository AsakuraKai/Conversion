# Enhancement Tasks & Issues

> **Last Updated:** December 12, 2025  
> **Project Phase:** Enhancement & Polish (Pre-Release)

## 📋 General Guidelines

**Code Hygiene Rule:**
- When moving, changing, or removing features, **always clean up completely**
- Remove unused files, functions, UI components, and dependencies
- No ghosting code or dead references allowed
- Update all navigation routes and remove obsolete screens

---

## 🎯 Priority Matrix

| Priority | Description | Timeline |
|----------|-------------|----------|
| **P0** | Critical bugs blocking release | Fix immediately |
| **P1** | Major features or UX issues | Current sprint |
| **P2** | Nice-to-have enhancements | Next sprint |
| **P3** | Future considerations | Backlog |

---

## 🔴 P0 - Critical Issues

### 1. Back Button Missing in Folder Monitoring ✅ COMPLETED
**Issue:** Navigation back button not present in folder monitoring screen

**Impact:** Users cannot navigate back without using system back button

**Action Items:**
- [x] Add TopAppBar with back navigation icon to `FolderMonitoringScreen.kt`
- [x] Ensure proper navigation stack cleanup on back press
- [x] Test navigation flow: Main → Folder Monitoring → Back → Main

**Status:** COMPLETED - Added back button with proper navigation callback

**Related:** Phase 2 - CHUNK 9 (File Observer)

---

### 2. Cloud Sync Animation State Bug ✅ COMPLETED
**Issue:** When connecting to one cloud service, all connect buttons show loading animation

**Root Cause:** Shared/global state not properly scoped per provider

**Action Items:**
- [x] Investigate state management in `CloudSyncViewModel.kt`
- [x] Use separate state properties per provider (Google Drive, OneDrive)
- [x] Fix button state: `isConnecting: Map<Provider, Boolean>`
- [x] Add unit tests for multi-provider state isolation

**Status:** COMPLETED - Replaced `isAuthenticating: Boolean` with `connectingProviders: Set<CloudProvider>` for per-provider state management

**Related:** Phase 5 - CHUNK 17 (Cloud Storage Integration)

---

### 3. Auto-Backup Default State ✅ COMPLETED
**Issue:** Auto-backup is off by default; users risk losing original files during rename operations

**Current Behavior:**
- Auto-backup disabled on first launch
- No warning when processing files without backup

**Required Behavior:**
- Auto-backup **enabled by default** on first app launch
- Show first-time user education dialog explaining backup importance
- Add per-operation backup toggle with persistent preference

**Action Items:**
- [x] Set `autoBackupEnabled = true` in DataStore defaults
- [x] Create `FirstLaunchEducationDialog.kt` for backup explanation
- [x] Add toggle in Settings: "Auto-backup before file operations"
- [x] Add inline backup toggle in batch rename flow
- [x] Implement mutual exclusivity: Auto-delete ON → Auto-backup OFF (with warning)
- [x] Store backup preference per operation type (rename, convert, etc.)

**Status:** COMPLETED
- Added `autoBackupEnabled` and `autoDeleteOriginals` to `UserPreferences` with defaults (backup=true, delete=false)
- Created `FirstLaunchEducationDialog.kt` component for user education
- Added File Operations section in Settings with both toggles
- Implemented mutual exclusivity in `PreferencesRepositoryImpl`
- Added warning messages for safety

**Acceptance Criteria:**
- [x] First-time users see backup education dialog (component ready for integration)
- [x] Auto-backup is ON by default in fresh installs
- [x] Toggle state persists across app restarts
- [x] Warning shown when disabling backup
- [x] Auto-delete and auto-backup cannot both be active

**Related:** Phase 2 - CHUNK 5 (Rename Execution)

---

## 🟠 P1 - Major Enhancements

### 4. Batch Rename UI Overhaul
**Issue:** Current batch rename UI missing critical functions and not matching design spec

**Reference Design:** `Batch_rename_template.png`

**Missing Features:**
1. **Destination Folder Selection** - Choose output directory
2. **Preserve Original Order** - Toggle to maintain file selection order
3. **Cancel Operation** - Stop in-progress rename
4. **Start Button** - Explicit action to begin processing
5. **Clear Files** - Remove all selected files from queue
6. **Numbering Options** - Start number, digit count, prefix
7. **File Type Conversion** - Convert formats during rename (JPG→PNG, etc.)
8. **Gallery Mode** - Show only images for selection
9. **Select Entire Folder** - Import all files from a folder at once

**Action Items:**
- [ ] Create new `BatchRenameConfigScreen.kt` (full-screen workflow)
- [ ] Add `DestinationFolderPicker` composable with folder tree navigation
- [ ] Implement `PreserveOrderToggle` with drag-to-reorder UI
- [ ] Add `CancelButton` with confirmation dialog for in-progress operations
- [ ] Create `StartRenameButton` with validation checks
- [ ] Implement `ClearAllButton` with undo snackbar
- [ ] Design `NumberingOptionsPanel`: start, digits, prefix, suffix
- [ ] Add `FileTypeConverter` dropdown with format options (JPG, PNG, HEIC, WebP)
- [ ] Create `GalleryPickerMode` filter (images only, videos only, all media)
- [ ] Implement `FolderImporter` - select folder to import all contents

**UI Flow:**
```
Main Screen → Batch Process Button
  ↓
File Selection Screen (Gallery/Folder mode)
  ↓
Configuration Screen (Numbering, Type, Destination)
  ↓
Preview Screen (Before/After with warnings)
  ↓
Execution Screen (Progress, Cancel option)
  ↓
Results Screen (Success/Errors summary)
```

**Related:** Phase 2 - CHUNKS 3-7

---

### 5. Custom Image Theme Editor
**Issue:** Image-based theming exists but lacks advanced customization

**Current:** Select image → auto-apply theme

**Required Features:**
1. **Image Background Customization Screen** (full editor)
   - Image position controls (center, stretch, fit, fill, tile)
   - Color overlay with opacity slider
   - Blur effect with intensity slider (0-100%)
   - Brightness/contrast adjustments
   - Live preview of changes

2. **Global Background Display**
   - Show custom image consistently across:
     - Main screen
     - Batch processing screens
     - Settings
     - All major navigation destinations
   - Respect system dark/light mode with overlay adaptation

**Action Items:**
- [ ] Create `ImageThemeEditorScreen.kt` with real-time preview
- [ ] Implement `ImagePositionSelector`: Center, Stretch, Fit, Fill, Tile
- [ ] Add `ColorOverlayPicker` with opacity slider (0-100%, 16 preset colors)
- [ ] Implement `BlurSlider` (0-25px blur radius) using RenderScript
- [ ] Add `BrightnessContrastControls` (-100 to +100 range)
- [ ] Create `BackgroundImageBox` composable for global use
- [ ] Update all screens to use `BackgroundImageBox` as base layer
- [ ] Store preferences in DataStore: `imageUri`, `position`, `overlay`, `blur`, `brightness`
- [ ] Handle image loading failures gracefully (fallback to solid color)

**UI Layout:**
```
┌─────────────────────────┐
│   Live Preview Area     │  ← Shows real-time changes
│   (Sample App Screen)   │
├─────────────────────────┤
│ Position: [Dropdown]    │  ← Center/Stretch/Fit/Fill/Tile
│ Blur:     [Slider] 15%  │  ← 0-100%
│ Overlay:  [Color] 50%   │  ← Color picker + opacity
│ Brightness: [Slider] +5 │  ← -100 to +100
│ Contrast:   [Slider] +3 │  ← -100 to +100
├─────────────────────────┤
│  [Reset]  [Apply]       │
└─────────────────────────┘
```

**Related:** Phase 3 - CHUNK 10 (Dynamic Theming) ✅

---

### 6. Consolidate Image Theme Settings
**Issue:** Image-based theme settings scattered across multiple locations

**Current State:**
- Standalone DynamicThemeScreen (redundant)
- Theme mode in Appearance section
- Image-based theme separate

**Required State:**
- Single "Appearance" section in Settings containing:
  - Theme Mode (Light/Dark/System)
  - Dynamic Colors toggle
  - Image-Based Theme (with editor button)
  - Custom Image Background (new feature #5)

**Action Items:**
- [ ] Remove standalone `DynamicThemeScreen.kt` (if exists)
- [ ] Consolidate all theme options in Settings → Appearance section
- [ ] Add "Edit Background" button that opens Image Theme Editor
- [ ] Update navigation to remove old theme routes
- [ ] Clean up unused theme-related composables

**Settings Structure:**
```
Settings
└── Appearance
    ├── Theme Mode (Light/Dark/System)
    ├── Dynamic Colors (Toggle)
    ├── Image-Based Theme
    │   ├── Select Image
    │   └── Customize → Opens Editor (Feature #5)
    └── Permissions (link to system settings)
```

**Related:** Phase 1 - CHUNK 10 (Completed)

---

## 🟡 P2 - Navigation & UX Improvements

### 7. Navigation Drawer Restructure
**Required Changes:**
1. **Remove from Main Screen:**
   - Account management (move to Settings)
   - Cloud Sync (move to Settings → Cloud Services)

2. **Add to Navigation Drawer:**
   - Activity Log (grouped with History)
   - Operation History (grouped with Activity Log)
   - Create new "History & Logs" section

**Action Items:**
- [ ] Create `NavigationDrawer.kt` if not exists
- [ ] Add "History & Logs" section with two menu items:
  - "Activity Log" → `ActivityLogScreen`
  - "Operation History" → `HistoryScreen`
- [ ] Remove Account and Cloud Sync buttons from `MainScreen.kt`
- [ ] Move account/cloud features to Settings
- [ ] Update navigation graph to support drawer destinations

**Drawer Structure:**
```
Navigation Drawer
├── Home
├── Batch Processing
├── QR Functions (new)
├── History & Logs
│   ├── Activity Log
│   └── Operation History
├── Settings
└── About
```

**Related:** Phase 5 - CHUNK 21 (Activity Log)

---

### 8. Unified QR Functions Screen
**Requirement:** Consolidate all QR-related features into single dedicated screen

**Current:** Share QR and Scan QR in different locations

**New Structure:**
```
QR Functions Screen
├── Generate QR Code
│   ├── From Preset (share templates)
│   ├── From Text
│   └── From File Link
├── Scan QR Code
│   ├── Import Preset
│   ├── Scan Generic QR
│   └── QR Comparison (new feature #9)
└── Image ⇄ QR Conversion (new feature #9)
```

**Action Items:**
- [ ] Create `QRFunctionsScreen.kt` with tab layout or sections
- [ ] Move `ShareQRComposable` to QR Functions
- [ ] Move `ScanQRComposable` to QR Functions
- [ ] Add navigation drawer item "QR Functions"
- [ ] Update navigation routes
- [ ] Remove old QR buttons from main screen

**Related:** Phase 5 - CHUNK 18 (QR Generation)

---

### 9. AI Integration into Batch Rename
**Requirement:** Move AI-powered filename suggestions into batch rename workflow

**Current:** AI suggestions might be standalone or not integrated

**Desired Flow:**
```
Batch Rename → Select Files → Configure
  ↓
[Get AI Suggestions] button appears
  ↓
Shows suggested filenames based on image content
  ↓
User can accept/modify suggestions
  ↓
Continue with rename
```

**Action Items:**
- [ ] Add "AI Suggestions" button to `BatchRenameConfigScreen`
- [ ] Integrate ML Kit analysis during file selection
- [ ] Show suggestion chips below each file thumbnail
- [ ] Add "Apply All Suggestions" bulk action
- [ ] Allow per-file suggestion editing
- [ ] Move AI feature from standalone location to batch workflow

**Prerequisite:** Complete Batch Rename UI Overhaul (#4)

**Related:** Phase 4 - CHUNK 13 (AI Suggestions)

---

## 🟢 P3 - Future Enhancements

### 10. Advanced QR Features

#### A. Image ⇄ QR Conversion
**Feature:** Encode images as QR codes and decode back to images

**Technical Approach:**
- Encode: Compress image → Base64 → Split into multiple QR codes
- Decode: Scan multiple QR codes → Reassemble → Decompress → Restore image

**Challenges:**
- QR code data limit (~2KB per code)
- Need multi-code generation/scanning for images
- Preserve image metadata (EXIF)

**Action Items:**
- [ ] Research multi-QR encoding libraries (e.g., QArt)
- [ ] Implement image compression (JPEG, WebP)
- [ ] Create QR sequence generator (Image → QR[1/N])
- [ ] Build multi-QR scanner with sequence reconstruction
- [ ] Add UI: "Convert Image to QR" + "Scan QR to Image"
- [ ] Handle large images (limit to 100KB compressed)

#### B. QR Recognition & Comparison
**Feature:** Identify and compare QR codes (like face recognition)

**Use Cases:**
- Detect duplicate QR codes in batch
- Compare two QR codes for similarity
- Group similar QR codes

**Action Items:**
- [ ] Generate hash of QR code content
- [ ] Implement similarity algorithm (Hamming distance for error correction)
- [ ] Create comparison UI showing side-by-side QR codes
- [ ] Add batch QR scanning with duplicate detection

**Related:** Phase 5 - CHUNK 18

---

### 11. Enhanced AI Recognition

**Current State:** AI suggests filenames using ML Kit image labeling

**Expansion Areas:**

#### A. Human Face Recognition
- Use ML Kit Face Detection API
- Detect faces in images
- Suggest filenames: "Person_001.jpg", "Group_Photo.jpg"
- Privacy-first: on-device only, no face identification (just detection)

#### B. Advanced Object Recognition
- Expand beyond generic image labels
- Specific categories:
  - **QR Codes** - "QR_Code_001.jpg"
  - **Wallpapers** - "Wallpaper_Nature.jpg"
  - **Characters** - "Character_Anime.jpg" (requires custom model)
  - **Documents** - "Receipt_2025_01.jpg" (OCR-based)
  - **Screenshots** - "Screenshot_App.jpg"

#### C. AI-Powered Template Generation
**Feature:** User describes desired preset in natural language, AI generates template

**Example:**
```
User input: "Date first, then location, underscore, 3-digit number"
AI generates: "{date}_{location}_{counter:3}"
```

**Implementation:**
- Add text input field in Template screen
- Use rule-based parser or simple NLP
- Safeguards: validate generated template, warn about invalid patterns
- Reject non-template requests ("Make me a sandwich")

**Action Items:**
- [ ] Implement ML Kit Face Detection (optional feature)
- [ ] Add object category presets in AI settings
- [ ] Create template generation parser
- [ ] Build validation for AI-generated templates
- [ ] Add "Generate from Description" button in template editor

**Related:** Phase 4 - CHUNK 13 (AI Features)

---

### 12. Cloud Services Simplification
**Decision:** Remove Dropbox, keep only Google Drive and OneDrive

**Rationale:**
- Google Drive: Auto-connects via Google account
- OneDrive: Auto-connects via Microsoft account
- Dropbox: Requires separate OAuth setup (extra friction)

**Action Items:**
- [ ] Remove Dropbox integration code from `CloudSyncRepository`
- [ ] Update UI to show only 2 providers
- [ ] Simplify auth flow (auto-detect existing account sessions)
- [ ] Update documentation to reflect supported providers

**Related:** Phase 5 - CHUNK 17

---

## 📊 Progress Tracking

**Overall Enhancement Phase Progress:**

- [x] P0 Issues (3/3 items) ✅ **COMPLETED**
- [ ] P1 Enhancements (6/6 items)
- [ ] P2 UX Improvements (3/3 items)
- [ ] P3 Future Features (3/3 items)

**P0 Completion Summary (December 12, 2025):**
1. ✅ Back button added to MonitoringScreen with proper navigation
2. ✅ Cloud Sync animation state fixed with per-provider state management
3. ✅ Auto-backup enabled by default with full UI and mutual exclusivity logic

**Next Steps:**
1. ~~Fix all P0 critical issues~~ ✅ COMPLETED
2. Complete P1 Batch Rename overhaul
3. Polish navigation and theme features
4. Evaluate P3 features for post-release updates

---

## 🔗 Related Documentation

- [README.md](docs/README.md) - Full project roadmap and architecture
- [UI_GUIDELINES.md](docs/UI_GUIDELINES.md) - Design system standards
- [NAVIGATION_WIRING_ROADMAP.md](docs/NAVIGATION_WIRING_ROADMAP.md) - Navigation architecture
- [ACCESSIBILITY_GUIDELINES.md](docs/ACCESSIBILITY_GUIDELINES.md) - Accessibility requirements

---

**Notes:**
- All enhancements should maintain code hygiene (no ghosting)
- Follow clean architecture patterns established in Phase 1
- Add unit tests for new features (70%+ coverage target)
- Update UI to match Material 3 design system
- Consider accessibility (content descriptions, semantic properties)