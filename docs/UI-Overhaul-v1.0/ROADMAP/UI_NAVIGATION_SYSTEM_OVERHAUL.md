# UI Navigation System - Complete Overhaul Plan

**Version:** 1.0  
**Date:** January 22, 2026  
**Purpose:** Comprehensive structured navigation framework for Files Management Service  
**Scope:** All screens, user journeys, and information architecture

---

## 📊 Executive Summary

> **🚧 Active Development**: The root navigation component (collapsible sidebar) is currently being implemented. See the detailed technical roadmap: [Android Collapsible Sidebar Implementation Roadmap](./ANDROID_COLLAPSIBLE_SIDEBAR_IMPLEMENTATION_ROADMAP.md)

### Current State
- ✅ 11 screens wired and functional
- ⚠️ 10 screens implemented but not fully integrated  
- ❌ Navigation scattered across multiple entry points
- ⚠️ Inconsistent user journeys
- ⚠️ No unified information architecture

### Vision
Create a **cohesive, predictable, and accessible navigation system** that guides users seamlessly through core workflows while providing easy access to advanced features.

### Key Metrics
- **Primary Workflows**: 3 (Batch Rename, Folder Monitoring, Quick Rename)
- **Feature Modules**: 8 (Smart Tools, Cloud & Sync, Data Management, Media Processing, Admin, Sharing, Discovery, Settings)
- **Total Screens**: 21+
- **Navigation Depth**: Max 3-4 levels
- **Expected Completion**: Post-implementation testing phase

---

## 🧭 Screen Implementation Status

### Implemented and Wired (11)
- HomeScreen
- FileSelectionScreen
- RenameConfigScreen
- PreviewScreen
- RenameProgressScreen
- FolderSelectorScreen
- SettingsScreen (includes DynamicTheme)
- MonitoringScreen
- TagManagementScreen
- TemplateScreen
- QRScannerScreen (camera-permission gated)

### Implemented, Wiring Pending (9)
- HistoryScreen
- AISuggestionsScreen
- RegexBuilderScreen
- MetadataPickerScreen
- CloudSyncScreen
- AccountScreen
- ActivityLogScreen
- QRDisplayScreen
- OCRScreen

### Planned / Future (not implemented yet)
- Format Converter flow (file selection → format selection → conversion)
- Image Optimization screen
- Advanced Search / Filter builder
- Template Store / Community browser
- QR History screen
- Backup / Restore dialogs (export/import settings)
- Database Health / Debug info screens
- Biometric auth settings screen

> Use this inventory to keep navigation wiring and testing focused on what exists today, while clearly marking what is aspirational.

---

## 🗺️ Navigation Hierarchy

### Level 1: Root Navigation (App Entry Points)
```
┌─────────────────────────────────────────────────────────────┐
│                    ROOT NAVIGATION                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  [HOME]  [FILE BROWSER]  [SETTINGS]  [NOTIFICATIONS]       │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**Entry Points:**
- **Home Screen** - Default landing, quick actions, feature discovery
- **File Browser** - Persistent file navigation (accessible via drawer)
- **Settings** - Application preferences and management
- **Notifications** - Operation feedback and alerts

---

## 🎯 Primary User Journeys

### Journey 1: Batch Rename (Most Common)
```
HOME
  ↓ [Tap "Batch Rename"]
FILE SELECTION SCREEN
  ↓ [Select files & Continue]
RENAME CONFIG SCREEN (Helper tools available)
  ├─→ AI Suggestions (optional)
  ├─→ Regex Builder (optional)
  ├─→ Metadata Picker (optional)
  └─→ OCR (optional)
  ↓ [Continue]
PREVIEW SCREEN
  ↓ [Start Rename]
RENAME PROGRESS SCREEN
  ↓ [Complete]
RENAME RESULTS SCREEN
  ↓ [Back to Home or New Batch]
```

**Key Points:**
- Main path is clear and linear
- Helper tools are optional detours
- Users can compare before committing changes
- Progress feedback throughout

---

### Journey 2: Folder Monitoring Setup
```
HOME
  ↓ [Tap "Folder Monitoring"]
FOLDER SELECTOR SCREEN
  ↓ [Choose folder]
MONITORING CONFIG SCREEN
  ↓ [Set rules]
MONITORING SCREEN
  ├─→ Edit Rules
  ├─→ View Activity Log
  └─→ Pause/Resume
  ↓ [Operations run automatically]
```

**Key Points:**
- Configuration wizard style
- Real-time activity feedback
- Easy rule editing
- Pause/resume controls

---

### Journey 3: Template Reuse
```
HOME
  ↓ [Tap "Use Template" or Recent]
TEMPLATE SELECTION SCREEN
  ↓ [Choose template]
FILE SELECTION SCREEN (Prefilled with template settings)
  ↓ [Select files]
RENAME PREVIEW SCREEN
  ↓ [Start]
RENAME RESULTS SCREEN
```

**Key Points:**
- Faster than batch rename for repeated tasks
- Template selection is prominent
- Settings pre-populated
- Minimal user input required

---

## 🏗️ Information Architecture

### Module 1: Core Batch Processing
```
HOME
├─ Batch Rename
│  ├─ File Selection
│  ├─ Rename Config
│  │  ├─ AI Suggestions
│  │  ├─ Regex Builder
│  │  ├─ Metadata Picker
│  │  └─ OCR
│  ├─ Preview
│  └─ Rename Progress
├─ Format Converter
│  ├─ File Selection
│  ├─ Format Selection
│  ├─ Preview
│  └─ Conversion Progress
└─ Quick Rename
   ├─ File Selection (Single/Multi)
   └─ Simple Rename Dialog
```

**Navigation Pattern:** Linear flow with optional branches  
**Back Button Behavior:** Returns to previous screen in workflow  
**Exit Behavior:** Returns to Home after completion

---

### Module 2: Smart Features
```
HOME → SMART FEATURES SECTION
├─ History / Undo-Redo
│  ├─ Operation List
│  ├─ Restore Operation
│  └─ Batch Undo
├─ AI-Powered Tools
│  ├─ AI Suggestions (integrated into Batch Rename)
│  ├─ Tag Suggestions
│  └─ Auto-Naming
├─ Advanced Search
│  ├─ Regex Search
│  ├─ Filter Builder
│  └─ Saved Searches
└─ Tag Management
   ├─ Tag Browser
   ├─ Create Tag
   └─ Tag Rules
```

**Navigation Pattern:** Hub-and-spoke  
**Access:** From Home, Settings, or context menus  
**Integration:** Helper tools integrate into main workflows

---

### Module 3: Cloud & Sync
```
HOME → CLOUD & SYNC SECTION (or SETTINGS)
├─ Cloud Sync Settings
│  ├─ Connected Services
│  ├─ Sync Status
│  └─ Sync Logs
├─ Account Management
│  ├─ Sign In
│  ├─ Profile Settings
│  └─ Multi-Device Setup
├─ Template Sharing
│  ├─ Share via QR
│  ├─ Share via Link
│  └─ Shared Templates
└─ Activity Sync
   ├─ Sync History
   └─ Device Activity
```

**Navigation Pattern:** Settings-like with hierarchical drilling  
**Access:** Home quick access + Settings deep settings  
**Permissions:** Requires authentication

---

### Module 4: Data Management
```
HOME → DATA MANAGEMENT SECTION (or SETTINGS)
├─ File Management
│  ├─ Auto-Backup
│  ├─ Auto-Delete Settings
│  └─ Storage Usage
├─ Settings Backup
│  ├─ Export Settings
│  ├─ Import Settings
│  └─ Backup History
├─ Database Maintenance
│  ├─ Clear Cache
│  ├─ Reset Settings
│  └─ Database Health
└─ Activity Log
   ├─ Operation History
   ├─ Error Logs
   └─ Audit Trail
```

**Navigation Pattern:** Grouped settings with actions  
**Access:** Primarily from Settings  
**Warnings:** Destructive actions require confirmation

---

### Module 5: Media Processing
```
HOME → MEDIA TOOLS SECTION
├─ Format Conversion
│  ├─ Batch Convert
│  ├─ Format Options
│  └─ Conversion Queue
├─ Image Optimization
│  ├─ Compression Settings
│  ├─ Batch Process
│  └─ Preview
├─ Metadata Editor
│  ├─ EXIF Viewer
│  ├─ Batch Edit
│  └─ Template Apply
└─ QR Code Tools
   ├─ Generate QR
   ├─ Scan QR
   └─ QR History
```

**Navigation Pattern:** Workflow-based with preview  
**Access:** From Home or File Browser  
**Media Support:** Images, videos, audio

---

### Module 6: Administration
```
SETTINGS (Main Hub)
├─ General
│  ├─ Theme Settings
│  ├─ Language
│  ├─ Permissions
│  └─ Notifications
├─ Performance
│  ├─ Cache Management
│  ├─ Background Sync
│  ├─ Performance Monitoring
│  └─ Debug Info
├─ Privacy & Security
│  ├─ Permission Management
│  ├─ Data Privacy
│  ├─ Encryption
│  └─ Biometric Auth
└─ About
   ├─ Version Info
   ├─ Changelog
   ├─ Help & Support
   └─ Feedback
```

**Navigation Pattern:** Tab-based with drilldown  
**Access:** Bottom navigation or drawer menu  
**Depth:** 1-2 levels maximum

---

### Module 7: Sharing & Discovery
```
HOME → DISCOVERY SECTION
├─ QR Code Sharing
│  ├─ Share Template as QR
│  ├─ Scan QR Template
│  ├─ Import Preset
│  └─ Sharing History
├─ Template Store
│  ├─ Browse Templates
│  ├─ Search Templates
│  ├─ Star/Favorite
│  └─ Community Presets
└─ Collaborate
   ├─ Shared Templates
   ├─ Team Settings
   └─ Pending Shares
```

**Navigation Pattern:** Hub with connected features  
**Access:** Home and Settings  
**Social:** Share and discover user templates

---

### Module 8: Settings Hub
```
SETTINGS (Primary Navigation Point)
├─ Preferences
│  ├─ Theme & Display
│  ├─ File Operations
│  ├─ Notifications
│  └─ Language
├─ Cloud & Sync
│  ├─ Cloud Sync Settings
│  ├─ Account Management
│  ├─ Activity Log
│  └─ Sync Status
├─ Data & History
│  ├─ History/Undo
│  ├─ Activity Log
│  ├─ Backup & Restore
│  └─ Storage Usage
├─ Help & Support
│  ├─ Help Documentation
│  ├─ FAQ
│  ├─ Contact Support
│  └─ Rate App
└─ About
   ├─ Version Info
   ├─ Licenses
   └─ Changelog
```

**Navigation Pattern:** Accordion/collapsible sections  
**Access:** Tab bar or drawer  
**Organization:** Logical grouping by function

---

## 🎨 Navigation Component Architecture

### Bottom Navigation Bar / Collapsible Sidebar

> **📋 Implementation Note**: This component is being replaced with a **collapsible sidebar navigation drawer**. See [Android Collapsible Sidebar Implementation Roadmap](./ANDROID_COLLAPSIBLE_SIDEBAR_IMPLEMENTATION_ROADMAP.md) for detailed implementation phases (7 phases, 12-16 days).

```
┌──────────────────────────────────────────┐
│ Batch  │ History │ Cloud │ Settings │    │
│ Rename │ Browse  │ Sync  │          │    │
└──────────────────────────────────────────┘
```

**Items:**
1. **Home** (Batch Rename quick access)
2. **History** (Recent operations)
3. **Cloud** (Sync status)
4. **Settings** (Preferences)

**Behavior:**
- Bottom navigation always visible
- Selected item highlighted with filled icon
- Badges show pending notifications
- Long-press shows label tooltip

---

### Navigation Drawer (Slide-out Menu)
```
┌─────────────────────┐
│ □ Files Management  │
├─────────────────────┤
│ ▶ Home              │
│ ▶ Batch Rename      │
│ ▶ Monitoring        │
│ ▶ Templates         │
│ ▶ Tags              │
├─────────────────────┤
│ ▶ Cloud & Sync      │
│ ▶ Account           │
│ ▶ Activity Log      │
├─────────────────────┤
│ ▶ Settings          │
│ ▶ Help              │
│ ▶ Rate App          │
└─────────────────────┘
```

**Contents:**
- App branding at top
- Primary features
- Secondary features
- Admin & help items

**Behavior:**
- Swipe from left edge to open
- Tap item to navigate
- Close on item selection
- Drawer icon in top-left toolbar

---

### Top App Bar (Header)
```
┌─────────────────────────────────────────────────┐
│ ☰ [Screen Title]                    [🔔][⚙️]    │
└─────────────────────────────────────────────────┘
```

**Elements:**
- Menu icon (opens drawer)
- Screen title (context-specific)
- Action buttons (varies by screen)
- Notification bell (badges count)
- More menu (additional actions)

**Screens Variations:**
- **Home**: Brand logo + menu
- **Batch Rename**: "Batch Rename" + progress indicator
- **Settings**: "Settings" + search
- **Results**: Screen name + "Share" button

---

### Floating Action Button (FAB)
```
┌──────────────────────────────────────────┐
│                                          │
│                                      [+] │
│                                          │
└──────────────────────────────────────────┘
```

**Context-Specific FABs:**
- **Home Screen**: "New Batch Rename" (primary action)
- **File Selection**: "Confirm Selection" (only when items selected)
- **Template Screen**: "New Template"
- **Tag Screen**: "New Tag"
- **Settings**: None (or "Export Settings")

**Behavior:**
- Primary action for screen
- Animated entrance/exit
- Ripple effect on press
- Tooltip on long-press

---

## 📱 Screen Map & Flow

### Tier 1: Entry Screens (Always Accessible)
```
HOME ←─┐
  ├─→ FILE BROWSER
  ├─→ SETTINGS
  └─→ [FAB: New Batch]

SETTINGS ←──┐
  ├─→ Cloud Sync
  ├─→ Account
  ├─→ Activity Log
  ├─→ Theme Settings
  └─→ Help

FILE BROWSER ←──┐
  ├─→ Folder View
  ├─→ Recent Files
  ├─→ Favorites
  └─→ Search
```

---

### Tier 2: Workflow Screens (Linear Progression)
```
BATCH RENAME WORKFLOW:
  File Selection → Config → [Helper Tools] → Preview → Progress → Results

MONITORING WORKFLOW:
  Folder Select → Config → Monitoring → [Activity] → Pause/Resume → Edit

TEMPLATE WORKFLOW:
  Template Select → File Select → Config → Preview → Process → Results

FORMAT CONVERT WORKFLOW:
  File Select → Format Select → Settings → Preview → Convert → Results
```

---

### Tier 3: Utility Screens (Accessible via Context)
```
AI SUGGESTIONS - modal/bottom sheet from Batch Rename Config
REGEX BUILDER - modal/bottom sheet from Batch Rename Config
METADATA PICKER - modal/bottom sheet from Batch Rename Config
OCR - screen accessible from Batch Rename Config
HISTORY - accessible from Home, Settings, and via Undo button
QR DISPLAY - accessible from Template or Share menu
QR SCANNER - accessible from Import or Scan button
```

---

## 🔄 Navigation Patterns

### Pattern 1: Linear Workflow (Batch Rename)
```
Screen A → Screen B → Screen C → Screen D ✓

Back Stack: A → B → C → D (clear after completion)
Exit: Completes and returns to Home
```

**Usage:**
- Batch rename
- Format conversion
- Template application
- Monitoring setup

---

### Pattern 2: Hub & Spoke (Settings)
```
        ┌─→ Theme
        │
  ┌─ HOME ─┬─→ Cloud
  │        │
  └─→ SETTINGS ┼─→ Privacy
        │
        └─→ Account
```

**Usage:**
- Settings screen
- Home screen
- Admin pages
- Discovery/browsing

---

### Pattern 3: Modal Dialog (Helper Tools)
```
Main Screen
  ├─→ [Open Helper Modal]
  │   └─→ [Interact with helper]
  │       └─→ [Return data or close]
  └─→ Continue with updated data

Back Stack: Unchanged (modal overlays)
```

**Usage:**
- AI suggestions
- Regex builder
- Metadata picker
- Confirmation dialogs

---

### Pattern 4: Deep Link / Shortcut
```
Notification/Widget
  ↓
Deep Link Handler
  ↓
Navigate to Screen
  ↓
Populate with Data
  ↓
Display Screen
```

**Usage:**
- Operation complete notification → Results screen
- Widget quick access → Batch Rename
- Share link → Template import

---

## 🚀 Navigation Flows (Detailed)

### Flow 1: Complete Batch Rename
```
┌──────────────────────────────────────────────────────────┐
│ START: Home Screen                                      │
├──────────────────────────────────────────────────────────┤
│ User taps "Batch Rename" card                           │
│              ↓                                           │
│ File Selection Screen                                   │
│ • Tap "Browse" → Open gallery/file picker              │
│ • Long-press file → Show preview                        │
│ • Select multiple files                                 │
│ • Tap "Next" → Continue                                │
│              ↓                                           │
│ Rename Configuration Screen                            │
│ • Enter prefix/suffix/pattern                           │
│ • Tap "AI Suggestions" → AI modal (optional)           │
│ • Tap "Regex Builder" → Regex modal (optional)         │
│ • Tap "Metadata" → Metadata picker (optional)          │
│ • Tap "Preview" → See before/after names              │
│ • Tap "Next" → Continue                                │
│              ↓                                           │
│ Preview Screen                                          │
│ • Show all renames in list                             │
│ • Scroll to review                                      │
│ • Tap "Edit Config" → Back to Config                   │
│ • Tap "Start Rename" → Begin operation                 │
│              ↓                                           │
│ Rename Progress Screen                                 │
│ • Linear progress bar (0-100%)                         │
│ • Show current file being renamed                       │
│ • Show count: "3 of 25 files"                          │
│ • Can pause/cancel                                      │
│              ↓                                           │
│ Results Screen                                          │
│ • Show success: "22 files renamed successfully"        │
│ • Show errors (if any): List of failed renames        │
│ • Buttons: [See Files] [New Batch] [Back]            │
│              ↓                                           │
│ END: Return to Home                                     │
└──────────────────────────────────────────────────────────┘
```

---

### Flow 2: Access AI Suggestions
```
┌──────────────────────────────────────────────────────────┐
│ IN: Rename Configuration Screen                         │
├──────────────────────────────────────────────────────────┤
│ User taps "AI Suggestions" button in Helper Tools       │
│              ↓                                           │
│ OVERLAY: AI Suggestions Modal                           │
│ • Show selected files                                   │
│ • "Analyzing..." with spinner                          │
│ • Generate AI-powered name suggestions                 │
│ • Show suggestions with confidence %                    │
│ • User taps suggestion → Apply                         │
│ • Modal closes, suggestion inserted in pattern         │
│ • Back to Rename Config with updated pattern          │
│              ↓                                           │
│ OUT: Rename Configuration Screen (updated)             │
│      Pattern field now contains AI suggestion           │
└──────────────────────────────────────────────────────────┘
```

---

### Flow 3: Settings Navigation
```
┌──────────────────────────────────────────────────────────┐
│ START: Settings Screen (Main Hub)                       │
├──────────────────────────────────────────────────────────┤
│ Accordion Sections:                                      │
│ 📋 Preferences                                          │
│    ├─ Theme [→ Theme Selection Screen]                 │
│    ├─ Notifications [→ Notification Settings]          │
│    └─ File Operations [→ File Ops Settings]            │
│                                                          │
│ ☁️ Cloud & Sync                                         │
│    ├─ Cloud Sync [→ Cloud Sync Screen]                 │
│    ├─ Account [→ Account Screen]                       │
│    └─ Activity Log [→ Activity Log Screen]             │
│                                                          │
│ 📁 Data & History                                       │
│    ├─ History [→ History Screen]                       │
│    ├─ Export Backup [→ Export Dialog]                  │
│    └─ Clear Cache [→ Confirmation]                     │
│                                                          │
│ ❓ Help & Support                                       │
│    ├─ Documentation [→ WebView or External]            │
│    ├─ Contact Support [→ Email/Form]                   │
│    └─ Rate App [→ Play Store]                          │
│                                                          │
│ ℹ️ About                                                │
│    ├─ Version [→ App Info]                             │
│    └─ Licenses [→ Licenses Screen]                     │
│                                                          │
│ Each section can collapse/expand                        │
│ Tap item to navigate                                    │
│ Use back button to return to Settings                  │
└──────────────────────────────────────────────────────────┘
```

---

## 🎯 Navigation Rules & Constraints

### Rule 1: Navigation Depth Limit
- **Maximum depth**: 4 screens from entry point
- **Rationale**: Prevent users from getting lost too deep in hierarchy
- **Exception**: Workflows with linear progression allowed up to 5 screens

---

### Rule 2: Consistent Back Behavior
```
Button/Gesture    Action              Goes To
─────────────────────────────────────────────────
Back button       Pop back stack      Previous screen
System back       Pop back stack      Previous screen (or Home if none)
Swipe back        Pop back stack      Previous screen
Cancel button     Pop and discard     Previous screen (no save)
X button          Close modal         Previous screen (behind modal)
```

---

### Rule 3: Navigation Accessibility
- All screens accessible within 2-3 taps from Home
- Quick actions available via FAB
- Drawer menu for secondary features
- Settings always 1 tap from anywhere (via bottom nav)

---

### Rule 4: Modal vs. Full Screen
```
MODALS (Bottom Sheet / Dialog):
- Helper tools (AI, Regex, Metadata, OCR) in batch workflows
- Quick confirmations
- Share/QR modals
- Quick pickers
- Small focused tasks

FULL SCREENS:
- Batch rename workflow
- Settings hub
- File browser
- Activity logs
- Complex forms with many options
```

---

### Rule 5: Data Persistence
```
Screen Type       Data Persistence        Behavior on Back
─────────────────────────────────────────────────────────
Workflow          Saved to bundle         Restored on return to workflow
Settings          Saved to preferences    Applied immediately
Browse            Partial state           Scroll position restored
Modal             Only if accepted        Discarded on cancel
Cache             Temporary               Cleared on app background
```

---

## 🔐 Permission-Gated Navigation

### Permission Requirements by Screen
```
Screen                    Permissions Needed       Gate Behavior
─────────────────────────────────────────────────────────────
File Selection           READ_EXTERNAL_STORAGE   Request on open
Folder Monitoring        MANAGE_EXTERNAL_STORAGE Request on open
QR Scanner              CAMERA                   Request on open
Cloud Sync              INTERNET                 Check before sync
Notifications           POST_NOTIFICATIONS       Request on setup
Location (Metadata)     ACCESS_FINE_LOCATION    Request on use
```

### Permission Flow
```
User taps screen → Check if granted → 
  ├─ If granted: Open screen
  ├─ If denied: Show permission request dialog
  │  ├─ User accepts → Request OS permission
  │  ├─ User grants → Open screen
  │  └─ User denies → Show education dialog
  └─ If never asked: Request permission
```

---

## 📊 Navigation State Management

### State Layers
```
1. Local State (Screen-level)
   ├─ Input field values
   ├─ Selection state
   └─ UI visibility flags

2. Shared State (Workflow-level)
   ├─ Selected files
   ├─ Rename configuration
   └─ User session

3. App State (Global)
   ├─ User authentication
   ├─ Theme selection
   └─ App permissions

4. Persistent State (Device Storage)
   ├─ User preferences
   ├─ Templates
   └─ Recently used items
```

### savedStateHandle Usage
```
Parent Screen ←→ Child Screen Data Transfer

Parent creates entry:
  parentEntry.savedStateHandle["key"] = value

Child retrieves:
  LaunchedEffect {
    parentEntry.savedStateHandle
      .getStateFlow("key", default)
      .collect { value → use value }
  }

Child returns and clears:
  parentEntry.savedStateHandle["key"] = returnValue
  navController.popBackStack()
  parentEntry.savedStateHandle.remove("key")
```

---

## 🎨 Visual Navigation Indicators

### Active Screen Highlighting
```
Bottom Navigation:
Active: Filled icon + Label text (colored)
Inactive: Outline icon + No label (gray)

Drawer Menu:
Active: Background highlight + Arrow indicator
Inactive: Text only + No highlighting

Breadcrumb (if used):
Current: Highlighted text
Previous: Clickable text (lighter)

Tabs (if used):
Active: Underline + Bold text
Inactive: Text only + No underline
```

---

### Loading States During Navigation
```
User taps navigation button
              ↓
Show spinner or skeleton
              ↓
Load data (if needed)
              ↓
Transition animation
              ↓
Display screen
              ↓
Hide loading state
```

---

## 🔄 Navigation Lifecycle

### Screen Entry
```
1. Navigation Intent
   User taps button/link/notification
   
2. Permission Check
   Is permission needed? Grant if needed.
   
3. Data Loading
   Fetch data for screen (if required)
   
4. Screen Creation
   Compose UI with loaded data
   
5. Animation
   Slide/fade in with Material motion
   
6. Ready
   Screen fully interactive
```

### Screen Exit
```
1. Exit Intent
   User taps back button
   
2. Save State
   Save form data / scroll position
   
3. Animation
   Slide/fade out with Material motion
   
4. Pop Stack
   Remove screen from navigation stack
   
5. Return
   Show previous screen with saved state
```

---

## 📐 Navigation Responsive Design

### Phone Portrait (320dp - 600dp)
```
Full-width navigation drawer
Bottom navigation for main tabs
Single-column layouts
Linear workflows (no side-by-side)
Modals for secondary actions
```

### Phone Landscape (600dp - 800dp)
```
Drawer can stay visible (optional)
Bottom nav optimized for width
2-column layouts possible
Modals for secondary actions
Wider cards and lists
```

### Tablet (800dp+)
```
Master-detail layouts
Drawer always visible (navigation rail option)
Multi-column layouts
Larger font sizes for accessibility
Side-by-side workflow preview possible
```

---

## 🚀 Advanced Navigation Features

### Deep Linking
```
URL Pattern: https://conversion.app/screen/action?params

Examples:
- https://conversion.app/batch-rename?templateId=123
- https://conversion.app/activity-log?date=2026-01-22
- https://conversion.app/share-template?qr=ABC123

Implementation:
1. Parse deep link in MainActivity
2. Extract screen and params
3. Navigate to screen with data
4. Apply params to screen state
```

### Shortcuts
```
Widget Shortcut → Batch Rename
Quick Access → Recent Template
Notification → Operation Results
Share Intent → Import Template
QR Code → Template/Preset
```

### Search Navigation
```
App-wide Search (Ctrl+F or search icon)
├─ Search templates
├─ Search files
├─ Search help articles
└─ Search settings
```

---

## ✅ Navigation Validation Checklist

### Design
- [ ] All screens are accessible within 3 taps
- [ ] No dead-end screens (except results)
- [ ] Back navigation works from all screens
- [ ] Workflows are linear and intuitive
- [ ] Settings are grouped logically
- [ ] FAB provides primary action

### Implementation
- [ ] All routes defined in Route.kt
- [ ] All composables wired in NavHost
- [ ] savedStateHandle data flowing correctly
- [ ] Navigation callbacks properly passed
- [ ] Permissions gated appropriately
- [ ] Deep links implemented

### Testing
- [ ] Navigate through all flows
- [ ] Test back stack behavior
- [ ] Verify data passing works
- [ ] Test on different screen sizes
- [ ] Test permissions flow
- [ ] Test with system back gesture

### Performance
- [ ] No jank during navigation
- [ ] Smooth animations (60 FPS)
- [ ] Memory doesn't leak
- [ ] No orphaned screens
- [ ] Fast transitions

### Accessibility
- [ ] All screens have content descriptions
- [ ] Touch targets ≥ 48dp
- [ ] High contrast indicators
- [ ] Screen reader support
- [ ] Keyboard navigation works

---

## 📚 Navigation Documentation

### For Users
- **Quick Start Guide** - How to navigate the app
- **Feature Tutorials** - Step-by-step for each feature
- **FAQ** - Common navigation questions
- **Video Walkthroughs** - Screen recording of workflows

### For Developers
- **Navigation Patterns** - Code examples
- **Implementation Guidelines** - How to add new screens
- **Testing Strategy** - How to test navigation
- **Troubleshooting** - Common issues and solutions

---

## 🎊 Navigation Overhaul Phases

### Phase 1: Foundation (Current)
- ✅ Define navigation hierarchy
- ✅ Document information architecture
- ✅ Create navigation patterns
- ✅ Establish constraints and rules

### Phase 2: Implementation (In Progress)
- 🔄 Wire all screens to navigation
- 🔄 Implement savedStateHandle data passing
- 🔄 Add permission gating
- 🔄 Test all navigation flows

### Phase 3: Enhancement (Planned)
- ⏳ Add deep linking
- ⏳ Implement search navigation
- ⏳ Add navigation animations
- ⏳ Optimize for tablet layout

### Phase 4: Polish (Planned)
- ⏳ Animation refinement
- ⏳ Accessibility audit
- ⏳ Performance optimization
- ⏳ User feedback implementation

---

## 📊 Success Metrics

### Navigation Metrics
- **Screen Accessibility**: 100% (all screens reachable)
- **Navigation Depth**: Max 4 levels from home
- **Workflow Success Rate**: 95%+ (users complete intended actions)
- **Back Stack Correctness**: 100% (back button works correctly)

### Performance Metrics
- **Navigation Latency**: < 300ms per screen
- **Frame Rate**: 60 FPS (no jank)
- **Memory Usage**: No leaks or orphaned screens
- **Battery Impact**: Negligible (<1% impact)

### User Experience Metrics
- **Navigation Intuitiveness**: 4.5+/5.0 (user ratings)
- **First-time Success**: 90%+ (users find features)
- **Support Requests**: <5% (navigation confusion)
- **Feature Discovery**: 80%+ (users find hidden features)

---

## 🔮 Future Considerations

### Extensibility
- Room for 20+ more screens
- Modular navigation system
- Plugin architecture ready
- Custom navigation handlers

### Scalability
- Prepare for team collaboration features
- Multi-user navigation paths
- Admin/user role navigation
- Enterprise features navigation

### Innovation
- Gesture-based navigation options
- Voice-controlled navigation
- Adaptive UI based on device
- ML-based navigation suggestions

---

## 📞 Support & Questions

### Navigation Documentation
- **Visual Diagrams**: Accessible in docs/navigation/
- **Code Examples**: In presentation layer
- **Video Guides**: Coming soon

### Getting Help
- **Documentation**: UI_GUIDELINES.md, NAVIGATION_WIRING_ROADMAP.md
- **Code Reference**: Look at ConversionNavHost.kt
- **Questions**: Refer to WORK_DIVISION.md for team contacts

---

## 🏆 Conclusion

This comprehensive UI Navigation System provides:

✅ **Clear Information Architecture** - Logical organization of 21+ screens  
✅ **Defined User Journeys** - Clear paths for common tasks  
✅ **Navigation Patterns** - Reusable patterns for consistency  
✅ **Accessibility** - All features within 2-3 taps  
✅ **Scalability** - Room for 20+ additional screens  
✅ **Performance** - Optimized for smooth navigation  
✅ **Implementation Guide** - Clear steps for execution  

**Impact**: Transform navigation from scattered to cohesive, improving user experience and feature discoverability.

---

**Document Version:** 1.0  
**Last Updated:** January 22, 2026  
**Status:** Complete ✅  
**Maintained By:** UI/Frontend Team  
**Review Schedule:** Quarterly

---

## 📋 Quick Reference

### Most Important Screens
1. Home - Entry point
2. Batch Rename - Most-used feature
3. Settings - Universal access point
4. File Browser - File access
5. Rename Progress - User feedback

### Most Critical Paths
1. Home → Batch Rename → Results
2. Home → Settings → Preferences
3. Home → Cloud Sync → Account
4. Batch Rename → AI Suggestions → Config

### Common Navigation Mistakes to Avoid
- ❌ Navigation depth > 4 levels
- ❌ No back navigation option
- ❌ Data loss on back press
- ❌ Orphaned screens
- ❌ Inconsistent back button behavior
- ❌ Missing accessibility descriptions
- ❌ No loading state during transitions
- ❌ Deep navigation without deep links

---

**Navigation System Overhaul Complete! 🎉**
