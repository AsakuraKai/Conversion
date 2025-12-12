# P1 Integration Checklist

> **Purpose:** Step-by-step guide for integrating P1 components into the existing codebase  
> **Last Updated:** December 13, 2025  
> **Status:** Ready for Integration

---

## Pre-Integration Checks

- [ ] All P1 components compile without errors
- [ ] Project builds successfully
- [ ] No merge conflicts with main branch
- [ ] All dependencies are available
- [ ] Code review completed
- [ ] Unit tests written for new components

---

## Phase 1: Navigation Drawer Integration

**Priority:** HIGH - Foundation for other integrations  
**Estimated Time:** 1-2 hours

### Files to Modify
- [ ] `MainActivity.kt`
- [ ] `HomeScreen.kt`

### Steps

#### 1.1 Update MainActivity.kt
```kotlin
// Add imports
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import kotlinx.coroutines.launch

// Wrap NavHost with ModalNavigationDrawer
```
- [ ] Add `drawerState = rememberDrawerState()`
- [ ] Add `scope = rememberCoroutineScope()`
- [ ] Wrap Scaffold with ModalNavigationDrawer
- [ ] Pass AppNavigationDrawer as drawerContent
- [ ] Wire up all navigation callbacks

#### 1.2 Update HomeScreen TopAppBar
- [ ] Add menu icon button
- [ ] Connect to `drawerState.open()`
- [ ] Test drawer opens on icon click

#### 1.3 Remove Redundant Navigation
- [ ] Remove `onNavigateToAccount` from HomeScreen
- [ ] Remove `onNavigateToCloudSync` from HomeScreen
- [ ] Remove corresponding buttons from HomeScreen UI
- [ ] Move Account/Cloud features to Settings

#### 1.4 Testing
- [ ] Drawer opens when menu icon clicked
- [ ] Drawer closes when item selected
- [ ] All drawer items navigate correctly
- [ ] Current route highlighted properly
- [ ] No navigation stack issues

---

## Phase 2: QR Functions Hub Integration

**Priority:** HIGH - Improves UX significantly  
**Estimated Time:** 2-3 hours

### Files to Modify
- [ ] `ConversionNavHost.kt`
- [ ] `HomeScreen.kt`
- [ ] `Route.kt` (already updated)

### Steps

#### 2.1 Add QR Routes to NavHost
```kotlin
// In ConversionNavHost.kt
```
- [ ] Add `Route.QRFunctions` composable
- [ ] Add `Route.QRComparison` composable
- [ ] Add `Route.ImageToQR` composable (same as QRToImage)
- [ ] Wire up all navigation callbacks
- [ ] Connect to existing QRDisplayScreen
- [ ] Connect to existing QRScannerScreen

#### 2.2 Update HomeScreen QR Buttons
- [ ] Replace individual QR buttons with single "QR Functions" card
- [ ] Update icon to `Icons.Default.QrCode`
- [ ] Update onClick to navigate to `Route.QRFunctions`

#### 2.3 Testing
- [ ] QR Functions hub opens from home
- [ ] All QR function cards navigate correctly
- [ ] Existing QR screens still work
- [ ] Back navigation works properly
- [ ] Badge labels display correctly

---

## Phase 3: Image Theme Editor Integration

**Priority:** MEDIUM - Enhances customization  
**Estimated Time:** 3-4 hours

### Files to Modify
- [ ] `SettingsScreen.kt`
- [ ] `ConversionNavHost.kt`
- [ ] DataStore schema (create new file)
- [ ] `SettingsViewModel.kt`

### Steps

#### 3.1 Create DataStore Schema
```kotlin
// Create UserPreferences.kt or update existing
data class ImageThemePreferences(
    val imageUri: String? = null,
    val position: String = "CENTER",
    val blurAmount: Float = 0f,
    val overlayColor: Long = 0xFF000000,
    val overlayOpacity: Float = 0f,
    val brightness: Float = 0f,
    val contrast: Float = 0f
)
```
- [ ] Define schema in preferences
- [ ] Add serialization if needed
- [ ] Create save/load functions
- [ ] Test persistence

#### 3.2 Add Route to NavHost
- [ ] Add `Route.ImageThemeEditor` composable
- [ ] Pass current preferences from DataStore
- [ ] Handle onApplySettings callback
- [ ] Save settings to DataStore
- [ ] Navigate back after save

#### 3.3 Update Settings Screen
- [ ] Add "Customize Background" list item in Appearance section
- [ ] Add navigation callback parameter
- [ ] Connect to ImageThemeEditor route
- [ ] Add icon (Icons.Default.Image)

#### 3.4 Testing
- [ ] Settings button navigates to editor
- [ ] Editor loads current settings
- [ ] Live preview updates in real-time
- [ ] Settings save successfully
- [ ] Settings persist after app restart
- [ ] Reset button works

---

## Phase 4: Background Image Global Application

**Priority:** MEDIUM - Visual polish  
**Estimated Time:** 2-3 hours

### Files to Modify
- [ ] `HomeScreen.kt`
- [ ] `SettingsScreen.kt`
- [ ] `RenameConfigScreen.kt`
- [ ] `FileSelectionScreen.kt`
- [ ] Other major screens

### Steps

#### 4.1 Create Preferences Flow
```kotlin
// In ViewModel or use shared preference holder
val imageThemePrefs by preferencesRepository.imageThemeFlow.collectAsState()
```
- [ ] Expose preferences as StateFlow
- [ ] Collect in composables

#### 4.2 Wrap Screens with BackgroundImageBox
- [ ] Start with HomeScreen as test
- [ ] Wrap entire Scaffold content
- [ ] Pass preferences to BackgroundImageBox
- [ ] Test rendering and performance
- [ ] Apply to other screens if successful

#### 4.3 Handle Edge Cases
- [ ] Null image URI (no background)
- [ ] Invalid image URI (show fallback)
- [ ] Large images (implement downscaling)
- [ ] Dark mode compatibility

#### 4.4 Performance Testing
- [ ] Check frame rate with blur enabled
- [ ] Monitor memory usage with large images
- [ ] Test on low-end devices
- [ ] Optimize if needed

---

## Phase 5: Batch Rename Components Integration

**Priority:** MEDIUM - Feature enhancement  
**Estimated Time:** 4-5 hours

### Files to Modify
- [ ] `RenameConfigScreen.kt`
- [ ] `RenameConfigViewModel.kt`
- [ ] `RenameConfigContract.kt`
- [ ] DataStore schema

### Steps

#### 5.1 Update Contract (State)
```kotlin
// In RenameConfigContract.kt - State
data class State(
    // Add new fields
    val destinationFolder: File? = null,
    val conversionFormat: ConversionFormat = ConversionFormat.NONE,
    val isProcessing: Boolean = false,
    // ... existing fields
)
```
- [ ] Add destinationFolder field
- [ ] Add conversionFormat field
- [ ] Add isProcessing field

#### 5.2 Update Contract (Actions)
```kotlin
// In RenameConfigContract.kt - Action
sealed interface Action {
    // Add new actions
    data object SelectDestinationFolder : Action
    data class UpdateConversionFormat(val format: ConversionFormat) : Action
    data object StartRename : Action
    data object CancelRename : Action
    data object ClearAll : Action
    // ... existing actions
}
```
- [ ] Add destination folder action
- [ ] Add conversion format action
- [ ] Add operation control actions

#### 5.3 Update ViewModel
- [ ] Implement SelectDestinationFolder handler
- [ ] Implement UpdateConversionFormat handler
- [ ] Implement StartRename handler (emit event to navigate)
- [ ] Implement CancelRename handler
- [ ] Implement ClearAll handler

#### 5.4 Update Screen Layout
```kotlin
// In RenameConfigScreen.kt, add components:
```
- [ ] Add DestinationFolderPicker after prefix input
- [ ] Add FileTypeConverter before sort strategy
- [ ] Replace bottom bar with BatchRenameActionButtons
- [ ] Update spacing and layout

#### 5.5 Wire Up Folder Selection
- [ ] Create folder picker dialog or use system picker
- [ ] Store selected folder in state
- [ ] Display selected folder path

#### 5.6 Implement File Conversion Logic
- [ ] Add conversion library (if needed)
- [ ] Implement conversion in rename execution
- [ ] Handle conversion errors
- [ ] Test with different formats

#### 5.7 Testing
- [ ] Destination folder can be selected
- [ ] Conversion format can be changed
- [ ] Start button starts operation
- [ ] Cancel button stops operation
- [ ] Clear all removes files
- [ ] Confirmation dialogs work

---

## Phase 6: AI Suggestions Integration

**Priority:** LOW - Depends on Phase 5  
**Estimated Time:** 3-4 hours

### Files to Modify
- [ ] `RenameConfigScreen.kt`
- [ ] `RenameConfigViewModel.kt`
- [ ] `RenameConfigContract.kt`
- [ ] AI service/repository

### Steps

#### 6.1 Update Contract (State)
```kotlin
// Add to State
val aiSuggestions: List<AISuggestion> = emptyList(),
val isLoadingAISuggestions: Boolean = false
```
- [ ] Add aiSuggestions list
- [ ] Add isLoadingAISuggestions flag

#### 6.2 Update Contract (Actions)
```kotlin
// Add actions
data object RequestAISuggestions : Action
data class AcceptAISuggestion(val index: Int, val name: String) : Action
data object ApplyAllAISuggestions : Action
```
- [ ] Add AI action types

#### 6.3 Implement AI Analysis
- [ ] Integrate ML Kit or existing AI service
- [ ] Process selected files
- [ ] Generate suggestions
- [ ] Update state with suggestions

#### 6.4 Add Component to Screen
```kotlin
// Add AISuggestionsPanel after helper tools or before preview
```
- [ ] Place component in screen layout
- [ ] Wire up callbacks
- [ ] Test loading state
- [ ] Test suggestion acceptance

#### 6.5 Testing
- [ ] Request suggestions button works
- [ ] Loading indicator shows
- [ ] Suggestions display correctly
- [ ] Individual accept works
- [ ] Apply all works
- [ ] Edit functionality works

---

## Phase 7: Final Polish & Testing

**Priority:** HIGH - Ensure quality  
**Estimated Time:** 2-3 hours

### Tasks
- [ ] Run all unit tests
- [ ] Run integration tests
- [ ] Manual test all new features
- [ ] Test on multiple devices
- [ ] Test in dark mode
- [ ] Test with TalkBack (accessibility)
- [ ] Check for memory leaks
- [ ] Profile performance
- [ ] Fix any discovered issues
- [ ] Update screenshots in docs
- [ ] Update README if needed

---

## Rollback Plan

If critical issues discovered:

1. **Navigation Drawer Issues**
   - [ ] Revert MainActivity changes
   - [ ] Restore HomeScreen navigation buttons

2. **QR Hub Issues**
   - [ ] Revert ConversionNavHost changes
   - [ ] Restore individual QR buttons

3. **Theme Editor Issues**
   - [ ] Remove ImageThemeEditor route
   - [ ] Hide Settings button
   - [ ] Keep existing theme system

4. **Batch Rename Issues**
   - [ ] Revert RenameConfigScreen changes
   - [ ] Revert ViewModel changes
   - [ ] Keep existing functionality

5. **AI Integration Issues**
   - [ ] Remove AISuggestionsPanel
   - [ ] Keep existing AI flow

---

## Post-Integration

### Code Review
- [ ] Submit PR for review
- [ ] Address review comments
- [ ] Get approval from team lead

### Documentation
- [ ] Update CHANGELOG.md
- [ ] Update user guide
- [ ] Update API documentation
- [ ] Create release notes

### Deployment
- [ ] Merge to main branch
- [ ] Create release tag
- [ ] Build release APK
- [ ] Test release build
- [ ] Deploy to Play Store (internal track first)

---

## Success Metrics

After integration, verify:
- [ ] No crashes introduced
- [ ] No performance regression
- [ ] All existing features still work
- [ ] All new features work as designed
- [ ] User feedback positive
- [ ] No increase in bug reports

---

## Estimated Total Integration Time

- Phase 1: 1-2 hours
- Phase 2: 2-3 hours
- Phase 3: 3-4 hours
- Phase 4: 2-3 hours
- Phase 5: 4-5 hours
- Phase 6: 3-4 hours
- Phase 7: 2-3 hours

**Total:** 17-24 hours (2-3 days of focused work)

---

## Notes

- Integration can be done in phases
- Each phase can be tested independently
- Phases 1-3 are independent and can be parallelized
- Phases 5-6 must be done sequentially
- Phase 7 is mandatory before deployment

---

**Last Updated:** December 13, 2025  
**Status:** Ready to Begin Integration
