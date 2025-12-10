# UI Wiring Analysis Report
**Date:** December 10, 2025  
**Status:** Near completion but many UIs not wired to navigation

---

## 🚨 Critical Issues Found

### **Problem Summary**
The Android project has comprehensive ViewModels and UI screens implemented, but **many screens are NOT connected to the navigation system**, making them inaccessible to users. This is a critical integration gap that prevents users from accessing existing functionality.

---

## 📊 Current Navigation Status

### ✅ **Screens Currently Wired in Navigation**

| Screen | Route | Status | Notes |
|--------|-------|--------|-------|
| **HomeScreen** | `Route.Home` | ✅ Working | Start destination |
| **FileSelectionScreen** | `Route.FileSelection` | ✅ Working | Core feature |
| **RenameConfigScreen** | `Route.RenameConfig` | ✅ Working | Core feature |
| **PreviewScreen** | `Route.Preview` | ✅ Working | Core feature |
| **RenameProgressScreen** | `Route.RenameProgress` | ✅ Working | Core feature |
| **FolderSelectorScreen** | `Route.FolderSelector` | ✅ Working | Core feature |
| **SettingsScreen** | `Route.Settings` | ✅ Working | Settings |
| **MonitoringScreen** | `Route.Monitoring` | ✅ Working | Monitoring |
| **DynamicThemeScreen** | `Route.DynamicTheme` | ✅ Working | Theme customization |
| **TagManagementScreen** | `Route.TagManagement` | ✅ Working | Tag management |
| **TemplateScreen** | `Route.TemplateManagement` | ✅ Working | Template management |

**Total Wired: 11 screens**

---

## ❌ **Missing Screens (NOT in Navigation)**

### **High Priority - Core Features**

| Screen | ViewModel | Contract | Status | Impact |
|--------|-----------|----------|--------|--------|
| **HistoryScreen** | ✅ HistoryViewModel | ✅ HistoryContract | 🔴 NOT WIRED | **CRITICAL** - Undo/Redo feature inaccessible |
| **AISuggestionsScreen** | ✅ AISuggestionsViewModel | ✅ AISuggestionsContract | 🔴 NOT WIRED | **HIGH** - AI feature inaccessible |
| **RegexBuilderScreen** | ✅ RegexViewModel | ✅ RegexContract | 🔴 NOT WIRED | **HIGH** - Advanced rename feature missing |
| **MetadataPickerScreen** | ✅ MetadataPickerViewModel | ✅ MetadataPickerContract | 🔴 NOT WIRED | **HIGH** - EXIF metadata feature inaccessible |

### **Medium Priority - Integration Features**

| Screen | ViewModel | Contract | Status | Impact |
|--------|-----------|----------|--------|--------|
| **CloudSyncScreen** | ✅ CloudSyncViewModel | ✅ CloudSyncContract | 🔴 NOT WIRED | **MEDIUM** - Cloud sync feature missing |
| **AccountScreen** | ✅ AccountViewModel | ✅ AccountContract | 🔴 NOT WIRED | **MEDIUM** - Multi-device sync inaccessible |
| **ActivityLogScreen** | ✅ ActivityLogViewModel | ✅ ActivityLogContract | 🔴 NOT WIRED | **MEDIUM** - Activity tracking not accessible |
| **QRDisplayScreen** | ✅ QRViewModel | ✅ QRContract | 🔴 NOT WIRED | **MEDIUM** - QR sharing feature missing |
| **QRScannerScreen** | ✅ QRViewModel | ✅ QRContract | 🔴 NOT WIRED | **MEDIUM** - QR import feature missing |

### **Critical Missing - NO Screen Implementation**

| Screen | ViewModel | Contract | Status | Impact |
|--------|-----------|----------|--------|--------|
| **OCRScreen** | ✅ OCRViewModel | ✅ OCRContract | 🔴 **NO SCREEN FILE** | **HIGH** - OCR feature completely missing UI |

**Total Missing: 10+ screens**

---

## 📁 File Structure Analysis

### **Screens with Complete Implementation**
```
presentation/
├── fileselection/          ✅ Complete & Wired
│   ├── FileSelectionContract.kt
│   ├── FileSelectionViewModel.kt
│   └── FileSelectionScreen.kt
├── renameconfig/           ✅ Complete & Wired
│   ├── RenameConfigContract.kt
│   ├── RenameConfigViewModel.kt
│   └── RenameConfigScreen.kt
├── preview/                ✅ Complete & Wired
│   ├── PreviewContract.kt
│   ├── PreviewViewModel.kt
│   └── PreviewScreen.kt
├── renameprogress/         ✅ Complete & Wired
│   ├── RenameProgressContract.kt
│   ├── RenameProgressViewModel.kt
│   └── RenameProgressScreen.kt
├── folder/                 ✅ Complete & Wired
│   ├── FolderSelectorContract.kt
│   ├── FolderSelectorViewModel.kt
│   └── FolderSelectorScreen.kt
├── settings/               ✅ Complete & Wired
│   ├── SettingsContract.kt
│   ├── SettingsViewModel.kt
│   └── SettingsScreen.kt
├── monitoring/             ✅ Complete & Wired
│   ├── MonitoringContract.kt
│   ├── MonitoringViewModel.kt
│   └── MonitoringScreen.kt
├── theme/                  ✅ Complete & Wired
│   ├── DynamicThemeContract.kt
│   ├── DynamicThemeViewModel.kt
│   └── DynamicThemeScreen.kt
├── tag/                    ✅ Complete & Wired
│   ├── TagContract.kt
│   ├── TagViewModel.kt
│   └── TagManagementScreen.kt
└── template/               ✅ Complete & Wired
    ├── TemplateContract.kt
    ├── TemplateViewModel.kt
    └── TemplateScreen.kt
```

### **Screens with Implementation but NOT Wired**
```
presentation/
├── history/                ⚠️ Complete but NOT wired
│   ├── HistoryContract.kt
│   ├── HistoryViewModel.kt
│   └── HistoryScreen.kt
├── ai/                     ⚠️ Complete but NOT wired
│   ├── AISuggestionsContract.kt
│   ├── AISuggestionsViewModel.kt
│   └── AISuggestionsScreen.kt
├── regex/                  ⚠️ Complete but NOT wired
│   ├── RegexContract.kt
│   ├── RegexViewModel.kt
│   └── RegexBuilderScreen.kt
├── metadata/               ⚠️ Complete but NOT wired
│   ├── MetadataPickerContract.kt
│   ├── MetadataPickerViewModel.kt
│   └── MetadataPickerScreen.kt
├── cloud/                  ⚠️ Complete but NOT wired
│   ├── CloudSyncContract.kt
│   ├── CloudSyncViewModel.kt
│   └── CloudSyncScreen.kt
├── account/                ⚠️ Complete but NOT wired
│   ├── AccountContract.kt
│   ├── AccountViewModel.kt
│   └── AccountScreen.kt
├── activity/               ⚠️ Complete but NOT wired
│   ├── ActivityLogContract.kt
│   ├── ActivityLogViewModel.kt
│   └── ActivityLogScreen.kt
├── qr/                     ⚠️ Complete but NOT wired
│   ├── QRContract.kt
│   ├── QRViewModel.kt
│   ├── QRDisplayScreen.kt
│   └── QRScannerScreen.kt
└── ocr/                    🔴 INCOMPLETE - Missing Screen
    ├── OCRContract.kt
    ├── OCRViewModel.kt
    └── ❌ NO OCRScreen.kt
```

---

## 🔧 Required Navigation Routes (Missing)

### Routes that need to be added to `Route.kt`:
```kotlin
sealed interface Route {
    // ... existing routes ...
    
    // ========== MISSING ROUTES ==========
    
    @Serializable
    data object History : Route                    // Undo/Redo history
    
    @Serializable
    data object AISuggestions : Route              // AI filename suggestions
    
    @Serializable
    data object RegexBuilder : Route               // Regex pattern builder
    
    @Serializable
    data object MetadataPicker : Route             // EXIF metadata picker
    
    @Serializable
    data object CloudSync : Route                  // Cloud storage sync
    
    @Serializable
    data object Account : Route                    // Multi-device account
    
    @Serializable
    data object ActivityLog : Route                // Activity log viewer
    
    @Serializable
    data object QRDisplay : Route                  // QR code display
    
    @Serializable
    data object QRScanner : Route                  // QR code scanner
    
    @Serializable
    data object OCR : Route                        // OCR text extraction
}
```

---

## 🔧 Required Navigation Entries (Missing)

### Composables that need to be added to `ConversionNavHost.kt`:
```kotlin
// In ConversionNavHost.kt

NavHost(...) {
    // ... existing routes ...
    
    // ========== MISSING COMPOSABLES ==========
    
    composable<Route.History> {
        HistoryScreen(
            onBack = { navController.popBackStack() }
        )
    }
    
    composable<Route.AISuggestions> {
        AISuggestionsScreen(
            onNavigateBackWithSuggestion = { suggestion ->
                // Pass suggestion back to RenameConfig
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("ai_suggestion", suggestion)
                navController.popBackStack()
            },
            onBack = { navController.popBackStack() }
        )
    }
    
    composable<Route.RegexBuilder> {
        RegexBuilderScreen(
            onNavigateBackWithPattern = { pattern ->
                // Pass pattern back to RenameConfig
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("regex_pattern", pattern)
                navController.popBackStack()
            },
            onBack = { navController.popBackStack() }
        )
    }
    
    composable<Route.MetadataPicker> {
        MetadataPickerScreen(
            onNavigateBackWithVariable = { variable ->
                // Pass metadata variable back to RenameConfig
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("metadata_variable", variable)
                navController.popBackStack()
            },
            onBack = { navController.popBackStack() }
        )
    }
    
    composable<Route.CloudSync> {
        CloudSyncScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
    
    composable<Route.Account> {
        AccountScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
    
    composable<Route.ActivityLog> {
        ActivityLogScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
    
    composable<Route.QRDisplay> {
        QRDisplayScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
    
    composable<Route.QRScanner> {
        QRScannerScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
    
    composable<Route.OCR> {
        // TODO: Create OCRScreen.kt first
        // OCRScreen(
        //     onNavigateBackWithText = { text -> ... },
        //     onBack = { navController.popBackStack() }
        // )
    }
}
```

---

## 🔗 Integration Points

### **HomeScreen Integration Needed**
Add navigation buttons to `HomeScreen.kt` for new features:

```kotlin
@Composable
fun HomeScreen(
    // ... existing params ...
    onNavigateToHistory: () -> Unit = {},           // NEW
    onNavigateToAISuggestions: () -> Unit = {},    // NEW
    onNavigateToRegexBuilder: () -> Unit = {},     // NEW
    onNavigateToCloudSync: () -> Unit = {},        // NEW
    onNavigateToAccount: () -> Unit = {},          // NEW
    onNavigateToActivityLog: () -> Unit = {},      // NEW
    onNavigateToQRDisplay: () -> Unit = {},        // NEW
    onNavigateToOCR: () -> Unit = {},              // NEW
) {
    // ... implementation ...
}
```

### **RenameConfigScreen Integration Needed**
Add helper tools to `RenameConfigScreen.kt`:

```kotlin
@Composable
fun RenameConfigScreen(
    // ... existing params ...
    onNavigateToAISuggestions: () -> Unit = {},    // NEW - AI button
    onNavigateToRegexBuilder: () -> Unit = {},     // NEW - Regex button
    onNavigateToMetadataPicker: () -> Unit = {},   // NEW - Metadata button
    onNavigateToOCR: () -> Unit = {},              // NEW - OCR button
) {
    // ... implementation ...
}
```

### **SettingsScreen Integration Needed**
Add settings entries for:
- Cloud Sync settings → `onNavigateToCloudSync()`
- Account management → `onNavigateToAccount()`
- Activity Log → `onNavigateToActivityLog()`
- History settings → `onNavigateToHistory()`

---

## 📦 Missing UI Screen File

### **OCRScreen.kt** (CRITICAL - Needs Creation)
```kotlin
// File: presentation/ocr/OCRScreen.kt
// Status: DOES NOT EXIST

// This file needs to be created by Sokchea (Frontend/UI Specialist)
// Following the pattern of other screens in the project

@Composable
fun OCRScreen(
    viewModel: OCRViewModel = hiltViewModel(),
    onNavigateBackWithText: (String) -> Unit,
    onBack: () -> Unit
) {
    // TODO: Implement OCR UI
    // - Image picker for selecting image
    // - Text extraction button
    // - Display extracted text blocks
    // - Confidence threshold slider
    // - Use text button (passes back to caller)
}
```

**Reference screens to follow:**
- `AISuggestionsScreen.kt` - Similar pattern (analyze → suggest → use)
- `MetadataPickerScreen.kt` - Similar picker pattern
- `RegexBuilderScreen.kt` - Similar builder/preview pattern

---

## 🎯 Recommended Action Plan

### **Phase 1: Critical Fixes (Immediate)**
1. ✅ Add missing routes to `Route.kt`
2. ✅ Add missing composables to `ConversionNavHost.kt`
3. ✅ Create `OCRScreen.kt` (Sokchea's task)
4. ✅ Wire History screen (Undo/Redo is critical)
5. ✅ Wire AI Suggestions screen

### **Phase 2: Feature Integration (Short-term)**
6. ✅ Wire Regex Builder screen
7. ✅ Wire Metadata Picker screen
8. ✅ Add navigation from RenameConfigScreen to helper tools
9. ✅ Add navigation from HomeScreen to new features

### **Phase 3: Advanced Features (Medium-term)**
10. ✅ Wire Cloud Sync screen
11. ✅ Wire Account screen
12. ✅ Wire Activity Log screen
13. ✅ Wire QR Display/Scanner screens
14. ✅ Wire OCR screen

### **Phase 4: Polish (Final)**
15. ✅ Add proper back stack handling
16. ✅ Add savedStateHandle for data passing
17. ✅ Test all navigation flows
18. ✅ Add deep linking support

---

## 📝 Work Assignment

### **Kai (Backend Specialist)**
- ✅ Domain & Data layers are COMPLETE ✅
- No backend work needed for navigation
- Can assist with testing

### **Sokchea (Frontend/UI Specialist)** - PRIMARY RESPONSIBILITY
- 🔴 **URGENT:** Create `OCRScreen.kt`
- 🔴 **CRITICAL:** Wire all missing screens to navigation
- 🔴 **HIGH:** Add navigation buttons to HomeScreen
- 🔴 **HIGH:** Add helper tool buttons to RenameConfigScreen
- 🟡 **MEDIUM:** Add settings entries for new features
- 🟢 **LOW:** Polish navigation transitions

**Estimated Time:** 4-6 hours for complete navigation wiring

---

## 🎓 Implementation Example

### **Step-by-Step: Wire History Screen**

1. **Add Route:**
```kotlin
// In Route.kt
@Serializable
data object History : Route
```

2. **Add Composable:**
```kotlin
// In ConversionNavHost.kt
composable<Route.History> {
    HistoryScreen(
        onBack = { navController.popBackStack() }
    )
}
```

3. **Add Navigation from HomeScreen:**
```kotlin
// In HomeScreen.kt
FeatureCard(
    title = "Rename History",
    description = "View and undo recent operations",
    icon = Icons.Default.History,
    onClick = onNavigateToHistory
)

// In ConversionNavHost.kt - HomeScreen composable
HomeScreen(
    // ... existing params ...
    onNavigateToHistory = {
        navController.navigate(Route.History)
    }
)
```

---

## ✅ Success Criteria

### Navigation is complete when:
- ✅ All implemented screens are accessible via navigation
- ✅ HomeScreen has buttons for all major features
- ✅ RenameConfigScreen has helper tool buttons
- ✅ Settings has entries for management features
- ✅ All screens can navigate back properly
- ✅ Data passing works (savedStateHandle)
- ✅ No orphaned screens exist
- ✅ OCRScreen.kt is created and functional

---

## 🚀 Current Project Status

### **What's Working:**
- ✅ Core batch rename flow (File Selection → Config → Preview → Progress)
- ✅ Settings management
- ✅ Theme customization
- ✅ Tag management
- ✅ Template management
- ✅ Folder selection
- ✅ Monitoring

### **What's NOT Working (But Implemented):**
- ❌ History/Undo/Redo (screen exists, not wired)
- ❌ AI Suggestions (screen exists, not wired)
- ❌ Regex Builder (screen exists, not wired)
- ❌ Metadata Picker (screen exists, not wired)
- ❌ Cloud Sync (screen exists, not wired)
- ❌ Account Management (screen exists, not wired)
- ❌ Activity Log (screen exists, not wired)
- ❌ QR Code features (screens exist, not wired)
- ❌ OCR (ViewM odel exists, screen missing)

---

## 📊 Statistics

| Category | Count | Status |
|----------|-------|--------|
| **Total Screen Files** | 21+ | Mostly complete |
| **ViewModels** | 22+ | ✅ All complete |
| **Contracts** | 22+ | ✅ All complete |
| **Screens Wired** | 11 | ✅ Core features work |
| **Screens NOT Wired** | 10 | 🔴 Major features inaccessible |
| **Missing Screen Files** | 1 (OCRScreen) | 🔴 Needs creation |
| **Navigation Routes** | 11 | ⚠️ Missing 10 routes |
| **Completion %** | ~52% | ⚠️ Backend done, UI wiring incomplete |

---

## 🎯 Priority Matrix

### **Must Fix (P0 - Critical):**
1. Wire HistoryScreen → Undo/Redo is core feature
2. Create OCRScreen.kt → Complete missing UI
3. Wire AISuggestionsScreen → Key feature for users

### **Should Fix (P1 - High):**
4. Wire RegexBuilderScreen → Advanced users need this
5. Wire MetadataPickerScreen → EXIF is important feature
6. Add navigation from RenameConfigScreen → Improve UX

### **Nice to Have (P2 - Medium):**
7. Wire CloudSyncScreen → Sync features
8. Wire AccountScreen → Multi-device
9. Wire ActivityLogScreen → Tracking
10. Wire QR screens → Sharing features

---

**Report Generated:** December 10, 2025  
**Next Review:** After navigation wiring complete  
**Assignee:** Sokchea (Frontend/UI Specialist)
