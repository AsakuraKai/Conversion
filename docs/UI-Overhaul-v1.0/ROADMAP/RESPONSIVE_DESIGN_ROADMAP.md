# Responsive Design Implementation Roadmap

**Version:** 1.0  
**Created:** January 23, 2026  
**Status:** Planning Phase  
**Owner:** Sokchea (Frontend/UI Specialist)

---

## 📋 Executive Summary

This roadmap addresses screen size responsiveness issues identified in the Files Management Service Android app. The UI currently uses fixed layouts and hardcoded dimensions that cause cramped interfaces on smaller devices. This document outlines a phased approach to implement adaptive layouts across all 21+ screens while maintaining design consistency and performance.

**🎯 Anti-Duplication Strategy:**
- **REUSE**: Leverage existing `MainContentArea`, `ContentCard`, `ResponsiveGridLayout` components
- **ENHANCE**: Make existing components responsive by replacing hardcoded dimensions
- **CONSOLIDATE**: Centralize scattered dimension tokens (`CONTENT_PADDING`, `CARD_PADDING`, etc.) into unified `Dimensions.*` system
- **ADD ONLY**: Create NEW components only for missing patterns (`TwoPaneLayout`, `AdaptiveButtonGroup`)

This approach avoids code duplication while building on the solid foundation already in place.

---

## 🔍 Current State Analysis

### Findings from Backend/Frontend Investigation

#### ✅ What's Working
1. **Navigation System Has Basic Responsiveness**
   - `CollapsibleSidebarLayout.kt` implements tablet detection (600dp breakpoint)
   - `AdaptiveCollapsibleSidebar.kt` handles phone/tablet differences
   - Navigation automatically collapses on phones, expands on tablets
   - Uses `LocalConfiguration` for screen size detection

2. **Some Adaptive Patterns Exist**
   - `GridCells.Adaptive(minSize = 120.dp)` in FileSelectionScreen
   - `ResponsiveGridLayout` component with `minItemWidth` parameter
   - Basic orientation awareness in navigation components

3. **Material 3 Foundation**
   - Using Material 3 components with built-in adaptive behavior
   - Dynamic color support enabled
   - Proper theme system in place

#### ❌ Critical Issues

1. **Hardcoded Dimensions Everywhere**
   - Fixed padding values: `padding(16.dp)`, `padding(24.dp)`
   - Fixed icon sizes: `size(24.dp)`, `size(48.dp)`
   - Fixed spacing: `spacedBy(16.dp)`, `height(16.dp)`
   - No scaling based on screen size or density

2. **No WindowSizeClass Implementation**
   - Not using Material 3's WindowSizeClass API
   - No distinction between compact/medium/expanded layouts
   - Missing official responsive patterns from Google

3. **Component-Level Problems**
   - `FeatureCard` in HomeScreen uses fixed layouts
   - Helper tools section in RenameConfigScreen (4 buttons in a row) will overflow on small screens
   - Form inputs don't adapt to available space
   - Grid items have fixed aspect ratios that may not work on all screens

4. **Typography Issues**
   - No responsive text scaling
   - Same font sizes on phones and tablets
   - May be illegible on very small or very large screens

5. **Spacing & Layout Issues**
   - No responsive margins/padding
   - Buttons and cards too close together on small screens
   - Empty space wasted on large screens/tablets
   - Bottom bars and FABs may overlap content on short screens

6. **No Screen Size Testing**
   - No preview configurations for different sizes
   - Missing landscape previews
   - No testing on foldables or edge cases

---

## 🎯 Goals & Success Criteria

### Primary Goals
1. **Eliminate UI Cramping**: Ensure all UI elements are comfortably spaced on phones (≥360dp width)
2. **Optimize Tablet Experience**: Utilize extra space on tablets with multi-column layouts
3. **Support Landscape**: Adapt layouts for landscape orientation on all devices
4. **Maintain Performance**: No impact on scroll performance or frame rates
5. **Preserve Design Language**: Keep consistent Material 3 design while being adaptive

### Success Criteria
- ✅ All screens render properly on screens 360dp-1200dp width
- ✅ No horizontal scrolling required on any screen
- ✅ All touch targets meet 48dp minimum size
- ✅ Typography scales appropriately across all sizes
- ✅ Layouts adapt smoothly during rotation
- ✅ 95%+ UI test coverage for responsive behaviors
- ✅ Zero layout-related crashes or visual bugs

---

## 📊 Screen Inventory & Priority Matrix

### High Priority (P0) - Core User Journeys
These screens are in the critical path for main features and have the most cramping issues:

| Screen | Current Issues | Complexity | Est. Days |
|--------|----------------|------------|-----------|
| **HomeScreen** | Multiple FeatureCards stack vertically, cramped on small screens, no grid layout | Medium | 2 |
| **FileSelectionScreen** | Grid uses fixed 120dp min size, may be too small or large | Low | 1 |
| **RenameConfigScreen** | 4 helper tool buttons in row overflow, form inputs cramped, long scrolling | High | 3 |
| **PreviewScreen** | File list with rename preview, no optimization for large screens | Medium | 2 |
| **RenameProgressScreen** | Progress bars and status cards, may be cramped | Low | 1 |

**Subtotal P0:** 9 days

### Medium Priority (P1) - Feature Screens
Important screens used in secondary workflows:

| Screen | Current Issues | Complexity | Est. Days |
|--------|----------------|------------|-----------|
| **SettingsScreen** | Long list of settings, no two-column layout for tablets | Medium | 2 |
| **TemplateScreen** | Template list and editor, form-heavy | Medium | 2 |
| **TagManagementScreen** | Tag chips may wrap poorly, list cramped | Low | 1 |
| **MonitoringScreen** | Folder monitoring status cards | Low | 1 |
| **HistoryScreen** | History list with actions, needs better tablet layout | Medium | 2 |
| **FolderSelectorScreen** | File browser with tree view | Medium | 2 |

**Subtotal P1:** 10 days

### Low Priority (P2) - Helper & Utility Screens
Modal dialogs and utility screens with less critical issues:

| Screen | Current Issues | Complexity | Est. Days |
|--------|----------------|------------|-----------|
| **AISuggestionsScreen** | Suggestion cards layout | Low | 1 |
| **RegexBuilderScreen** | Regex pattern builder interface | Low | 1 |
| **MetadataPickerScreen** | Metadata variable picker modal | Low | 1 |
| **OCRScreen** | OCR text extraction display | Low | 1 |
| **QRScannerScreen** | Camera preview with overlay | Low | 1 |
| **QRDisplayScreen** | QR code display centered | Low | 0.5 |
| **CloudSyncScreen** | Sync status and settings | Low | 1 |
| **AccountScreen** | User profile and settings | Low | 1 |
| **ActivityLogScreen** | Activity history list | Low | 1 |
| **BatchProcessScreen** | Batch operations interface | Medium | 1.5 |

**Subtotal P2:** 10 days

### Pending Screens (P3) - Not Yet Implemented
These will follow responsive patterns from the start:

- FormatConverterScreen
- ImageOptimizationScreen
- AdvancedSearchScreen
- TemplateStoreScreen
- QRHistoryScreen
- BackupRestoreDialogs

**Total Estimated Effort:** ~30 days of focused development

---

## 🏗️ Architecture & Foundation

### ⚠️ IMPORTANT: Avoid Duplication - Audit Existing Components

**EXISTING COMPONENTS TO REUSE (Do NOT recreate):**

1. **`MainContentArea.kt`** - Already exists with:
   - `MainContentArea()` - Container with consistent padding
   - `ResponsiveGridLayout()` - Adaptive grid with `GridCells.Adaptive`
   - `ContentColumnLayout()` - Scrollable column layout
   - Design tokens: `CONTENT_PADDING`, `GRID_PADDING`, `GRID_ITEM_SPACING`, etc.

2. **`ContentCard.kt`** - Already exists with:
   - `ContentCard()` - Reusable card component
   - Design tokens: `CARD_PADDING`, `CARD_CONTENT_SPACING`

3. **Navigation components** - Already have basic responsiveness:
   - `CollapsibleSidebarLayout.kt` - Tablet detection at 600dp
   - `AdaptiveCollapsibleSidebar.kt` - Device type handling
   - Uses `LocalConfiguration` for screen size detection

**PROBLEMS WITH EXISTING COMPONENTS:**
- ❌ Dimension tokens are **private** and **hardcoded** in each file
- ❌ Not using Material 3 WindowSizeClass API (uses manual LocalConfiguration)
- ❌ Each component duplicates dimension definitions
- ❌ No centralized, responsive dimension system

**STRATEGY: Enhance, Don't Replace**
- Make existing dimension tokens responsive
- Centralize dimensions into shared system
- Add missing WindowSizeClass wrapper
- Extend existing components, not recreate them

---

### 1. Responsive Design System

#### Dimension System
**Consolidate and enhance** existing scattered dimension tokens:

**File:** `app/src/main/java/com/example/conversion/ui/theme/Dimensions.kt` (NEW)

```kotlin
package com.example.conversion.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Centralized responsive dimension system.
 * Consolidates scattered dimension tokens from MainContentArea, ContentCard, etc.
 * Makes them responsive based on WindowSizeClass.
 * 
 * MIGRATION: Replace hardcoded values with these responsive tokens:
 * - MainContentArea: CONTENT_PADDING → Dimensions.contentPadding
 * - ContentCard: CARD_PADDING → Dimensions.cardPadding
 * - All screens: Fixed padding(16.dp) → padding(Dimensions.contentPadding)
 */
object Dimensions {
    
    /**
     * Content padding for main areas
     * Replaces: MainContentArea.CONTENT_PADDING (was 24.dp fixed)
     */
    val contentPadding: Dp
        @Composable get() = when (LocalWindowSizeClass.current.widthSizeClass) {
            WindowWidthSizeClass.Compact -> 16.dp
            WindowWidthSizeClass.Medium -> 24.dp
            WindowWidthSizeClass.Expanded -> 32.dp
            else -> 16.dp
        }
    
    /**
     * Card padding
     * Replaces: ContentCard.CARD_PADDING (was 16.dp fixed)
     */
    val cardPadding: Dp
        @Composable get() = when (LocalWindowSizeClass.current.widthSizeClass) {
            WindowWidthSizeClass.Compact -> 12.dp
            WindowWidthSizeClass.Medium -> 16.dp
            WindowWidthSizeClass.Expanded -> 20.dp
            else -> 12.dp
        }
    
    /**
     * Vertical spacing between sections
     */
    val sectionSpacing: Dp
        @Composable get() = when (LocalWindowSizeClass.current.widthSizeClass) {
            WindowWidthSizeClass.Compact -> 16.dp
            WindowWidthSizeClass.Medium -> 20.dp
            WindowWidthSizeClass.Expanded -> 24.dp
            else -> 16.dp
        }
    
    /**
     * Item spacing in lists/grids
     * Replaces: MainContentArea.GRID_ITEM_SPACING (was 16.dp fixed)
     */
    val itemSpacing: Dp
        @Composable get() = when (LocalWindowSizeClass.current.widthSizeClass) {
            WindowWidthSizeClass.Compact -> 8.dp
            WindowWidthSizeClass.Medium -> 12.dp
            WindowWidthSizeClass.Expanded -> 16.dp
            else -> 8.dp
        }
    
    /**
     * Minimum touch target size (always 48dp per Material guidelines)
     */
    val minTouchTarget: Dp = 48.dp
    
    /**
     * Icon sizes
     */
    object Icon {
        val small: Dp = 16.dp
        val medium: Dp = 24.dp
        val large: Dp = 32.dp
        val extraLarge: Dp
            @Composable get() = when (LocalWindowSizeClass.current.widthSizeClass) {
                WindowWidthSizeClass.Compact -> 48.dp
                WindowWidthSizeClass.Medium -> 56.dp
                WindowWidthSizeClass.Expanded -> 64.dp
                else -> 48.dp
            }
    }
    
    /**
     * Grid configuration
     * Enhances existing ResponsiveGridLayout defaults
     */
    object Grid {
        val minItemWidthCompact: Dp = 120.dp
        val minItemWidthMedium: Dp = 150.dp
        val minItemWidthExpanded: Dp = 200.dp
        
        @Composable
        fun getMinItemWidth(): Dp = when (LocalWindowSizeClass.current.widthSizeClass) {
            WindowWidthSizeClass.Compact -> minItemWidthCompact
            WindowWidthSizeClass.Medium -> minItemWidthMedium
            WindowWidthSizeClass.Expanded -> minItemWidthExpanded
            else -> minItemWidthCompact
        }
    }
}
```

#### WindowSizeClass Wrapper
Create a wrapper for Material 3's WindowSizeClass:

**File:** `app/src/main/java/com/example/conversion/ui/theme/WindowSizeClass.kt`

```kotlin
package com.example.conversion.ui.theme

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

/**
 * Provides current window size class for responsive layouts
 */
val LocalWindowSizeClass = compositionLocalOf<WindowSizeClass> {
    error("No WindowSizeClass provided")
}

/**
 * Calculate WindowSizeClass from current configuration
 */
@Composable
fun calculateWindowSizeClass(): WindowSizeClass {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    
    val width = with(density) { configuration.screenWidthDp.dp }
    val height = with(density) { configuration.screenHeightDp.dp }
    
    return WindowSizeClass.calculateFromSize(DpSize(width, height))
}

/**
 * Helper to determine if device is tablet-sized
 */
@Composable
fun isTablet(): Boolean {
    return LocalWindowSizeClass.current.widthSizeClass >= WindowWidthSizeClass.Medium
}

/**
 * Helper to determine if screen is in landscape
 */
@Composable
fun isLandscape(): Boolean {
    return LocalConfiguration.current.screenWidthDp > LocalConfiguration.current.screenHeightDp
}

/**
 * Get number of columns for grid layouts
 */
@Composable
fun getGridColumns(
    compactColumns: Int = 2,
    mediumColumns: Int = 3,
    expandedColumns: Int = 4
): Int {
    return when (LocalWindowSizeClass.current.widthSizeClass) {
        WindowWidthSizeClass.Compact -> compactColumns
        WindowWidthSizeClass.Medium -> mediumColumns
        WindowWidthSizeClass.Expanded -> expandedColumns
        else -> compactColumns
    }
}
```

### 2. Enhance Existing Components & Add Missing Pieces

#### 2.1 Update MainContentArea.kt (MODIFY EXISTING)

**Changes:**
1. Replace hardcoded private tokens with responsive `Dimensions.*`
2. Keep existing component signatures (no breaking changes)
3. Update default parameters to use responsive dimensions

```kotlin
// In MainContentArea.kt - UPDATE existing code

// BEFORE (lines 124-128):
private val CONTENT_PADDING = 24.dp
private val GRID_PADDING = 16.dp
private val GRID_ITEM_SPACING = 16.dp
private val COLUMN_PADDING = 16.dp
private val COLUMN_ITEM_SPACING = 16.dp

// AFTER - Delete these and use Dimensions.* directly in components:
// (No private tokens needed - use shared system)

// Update ResponsiveGridLayout to use responsive defaults:
@Composable
fun ResponsiveGridLayout(
    modifier: Modifier = Modifier,
    minItemWidth: Dp = Dimensions.Grid.getMinItemWidth(), // NEW: responsive default
    contentPadding: PaddingValues = PaddingValues(Dimensions.contentPadding), // NEW
    verticalSpacing: Dp = Dimensions.itemSpacing, // NEW
    horizontalSpacing: Dp = Dimensions.itemSpacing, // NEW
    content: LazyGridScope.() -> Unit
) { /* ... existing implementation ... */ }
```

#### 2.2 Update ContentCard.kt (MODIFY EXISTING)

```kotlin
// In ContentCard.kt - UPDATE existing code

// BEFORE (lines 84-85):
private val CARD_PADDING = 16.dp
private val CARD_CONTENT_SPACING = 12.dp

// AFTER - Replace with responsive values in component:
Card(
    // ... existing properties ...
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimensions.cardPadding), // NEW: responsive padding
        verticalArrangement = Arrangement.spacedBy(Dimensions.itemSpacing) // NEW
    ) { /* ... rest stays same ... */ }
}
```

#### 2.3 Add NEW Helper Components (ONLY THESE ARE NEW)

**File:** `app/src/main/java/com/example/conversion/ui/components/AdaptiveLayouts.kt` (NEW)

These are MISSING patterns not covered by existing components:

```kotlin
package com.example.conversion.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.conversion.ui.theme.Dimensions
import com.example.conversion.ui.theme.LocalWindowSizeClass

/**
 * NEW: Two-pane layout for tablets (master-detail pattern)
 * Use for: Settings, History, Template editor
 */
@Composable
fun TwoPaneLayout(
    modifier: Modifier = Modifier,
    showTwoPanes: Boolean = LocalWindowSizeClass.current.widthSizeClass >= WindowWidthSizeClass.Medium,
    masterPane: @Composable () -> Unit,
    detailPane: @Composable () -> Unit
) {
    if (showTwoPanes) {
        Row(modifier = modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(0.4f)) { masterPane() }
            Box(modifier = Modifier.weight(0.6f)) { detailPane() }
        }
    } else {
        Box(modifier = modifier.fillMaxSize()) { masterPane() }
    }
}

/**
 * NEW: Adaptive button group that prevents overflow on small screens
 * Use for: RenameConfigScreen helper tools, multi-button actions
 */
@Composable
fun AdaptiveButtonGroup(
    modifier: Modifier = Modifier,
    maxButtonsPerRow: Int = 4,
    buttons: List<@Composable RowScope.() -> Unit>
) {
    val buttonsPerRow = when (LocalWindowSizeClass.current.widthSizeClass) {
        WindowWidthSizeClass.Compact -> minOf(2, maxButtonsPerRow)
        WindowWidthSizeClass.Medium -> minOf(3, maxButtonsPerRow)
        WindowWidthSizeClass.Expanded -> maxButtonsPerRow
        else -> 2
    }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimensions.itemSpacing)
    ) {
        buttons.chunked(buttonsPerRow).forEach { rowButtons ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimensions.itemSpacing)
            ) {
                rowButtons.forEach { it() }
            }
        }
    }
}

/**
 * NEW: Fixed-column responsive grid (alternative to adaptive)
 * Use for: Dashboard cards, feature cards
 */
@Composable
fun ResponsiveCardGrid(
    modifier: Modifier = Modifier,
    compactColumns: Int = 1,
    mediumColumns: Int = 2,
    expandedColumns: Int = 3,
    content: LazyGridScope.() -> Unit
) {
    val columns = when (LocalWindowSizeClass.current.widthSizeClass) {
        WindowWidthSizeClass.Compact -> compactColumns
        WindowWidthSizeClass.Medium -> mediumColumns
        WindowWidthSizeClass.Expanded -> expandedColumns
        else -> compactColumns
    }
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier,
        contentPadding = PaddingValues(Dimensions.contentPadding),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.itemSpacing),
        verticalArrangement = Arrangement.spacedBy(Dimensions.itemSpacing),
        content = content
    )
}
```

**Summary of Component Strategy:**
- ✅ REUSE: `MainContentArea`, `ContentCard`, `ResponsiveGridLayout` (with responsive updates)
- ✅ ADD NEW: `TwoPaneLayout`, `AdaptiveButtonGroup`, `ResponsiveCardGrid` (new patterns)
- ❌ DON'T CREATE: Anything that duplicates existing functionality

### 3. Update Build Dependencies

Add Material 3 WindowSizeClass to gradle:

**File:** `gradle/libs.versions.toml`

```toml
[versions]
# ... existing versions ...
material3-window-size = "1.2.0"

[libraries]
# ... existing libraries ...
androidx-material3-window-size-class = { module = "androidx.compose.material3:material3-window-size-class", version.ref = "material3-window-size" }
```

**File:** `app/build.gradle.kts`

```kotlin
dependencies {
    // ... existing dependencies ...
    
    // Responsive design - Material 3 WindowSizeClass
    implementation(libs.androidx.material3.window.size.class)
}
```

---

## 📅 Implementation Phases

### Phase 1: Foundation Setup (Week 1)
**Goal:** Establish responsive design infrastructure

#### Tasks
1. **Add WindowSizeClass Dependency** (0.5 days)
   - Update gradle dependencies
   - Sync and verify build

2. **Create Dimension System** (1 day)
   - Implement `Dimensions.kt` (consolidates scattered dimension tokens)
   - Create dimension tokens for all spacing/sizing needs
   - Document usage patterns and migration guide

3. **Create WindowSizeClass Wrapper** (0.5 days)
   - Implement `WindowSizeClass.kt`
   - Add helper functions
   - Create CompositionLocal provider

4. **Update Existing Components** (1.5 days)
   - **Modify MainContentArea.kt**: Replace hardcoded tokens with `Dimensions.*`
   - **Modify ContentCard.kt**: Use responsive padding/spacing
   - **Preserve existing APIs**: No breaking changes, just make internals responsive
   - Add responsive default parameters

5. **Build NEW Adaptive Components** (1 day)
   - Create `AdaptiveLayouts.kt` with:
     - `TwoPaneLayout` (NEW - for master-detail)
     - `AdaptiveButtonGroup` (NEW - prevents overflow)
     - `ResponsiveCardGrid` (NEW - fixed-column alternative)
   - Add comprehensive previews

6. **Update Theme Integration** (0.5 days)
   - Integrate WindowSizeClass into theme
   - Update main activity to provide WindowSizeClass
   - Test on emulators (phone/tablet)

**Deliverables:**
- ✅ Responsive dimension system with NO duplication
- ✅ Enhanced existing components (backward compatible)
- ✅ New utility components for missing patterns
- ✅ Documentation and examples

**Total:** 5 days

---

### Phase 2: High Priority Screens (Week 2-3)
**Goal:** Fix critical user journey screens

#### 2.1 HomeScreen (2 days)

**Current Issues:**
- FeatureCard list stacks vertically, wastes tablet space
- No grid layout for feature cards
- Fixed padding throughout

**Changes:**
1. Replace `Column` with `ResponsiveCardGrid`
   - 1 column on phones
   - 2 columns on medium tablets
   - 3 columns on large tablets/landscape

2. Update all padding to use `Dimensions.contentPadding`

3. Adjust FeatureCard component:
   - Use adaptive icon sizes
   - Responsive text sizes
   - Flexible card heights

**Acceptance Criteria:**
- ✅ Feature cards display in grid on tablets
- ✅ All spacing scales properly
- ✅ No cramping on 360dp phones
- ✅ Efficient use of space on tablets

#### 2.2 FileSelectionScreen (1 day)

**Current Issues:**
- Fixed grid min size (120dp)
- May be too small on large screens, too large on small

**Changes:**
1. Replace hardcoded `120.dp` with `Dimensions.Grid.getMinItemWidth()`
2. Ensure grid adapts: 2-3-4 columns for compact-medium-expanded
3. Update FileGridItem to handle varying sizes gracefully

**Acceptance Criteria:**
- ✅ Grid items scale appropriately
- ✅ Optimal columns for each screen size
- ✅ Images don't pixelate or get cropped poorly

#### 2.3 RenameConfigScreen (3 days)

**Current Issues:**
- 4 helper tool buttons in single row overflow on small screens
- Form inputs cramped
- Very long vertical scrolling

**Changes:**
1. Replace helper tools Row with `AdaptiveButtonGroup`
   - 2 buttons per row on compact (2×2 grid)
   - 3 buttons per row on medium
   - 4 buttons per row on expanded

2. Implement two-pane layout for tablets:
   - Left: Configuration form
   - Right: Live preview

3. Update all form fields to use responsive padding

4. Add minimum/maximum width constraints

**Acceptance Criteria:**
- ✅ Helper buttons never overflow
- ✅ Tablet shows split view (config + preview)
- ✅ Form fields properly sized on all screens
- ✅ Reduced scrolling on tablets

#### 2.4 PreviewScreen (2 days)

**Current Issues:**
- File list not optimized for large screens
- No multi-column display

**Changes:**
1. Implement adaptive list/grid view:
   - Single column list on compact
   - Two column grid on medium/expanded

2. Add before/after preview side-by-side on tablets

3. Responsive typography for file names

**Acceptance Criteria:**
- ✅ Efficient layout on all screen sizes
- ✅ Better preview visualization on tablets
- ✅ Smooth transitions between orientations

#### 2.5 RenameProgressScreen (1 day)

**Current Issues:**
- Progress cards may be cramped

**Changes:**
1. Use responsive card layout
2. Adaptive typography for status messages
3. Ensure progress bars scale properly

**Acceptance Criteria:**
- ✅ Clear progress display on all screens
- ✅ No overlapping elements

**Total Phase 2:** 9 days

---

### Phase 3: Medium Priority Screens (Week 4-5)
**Goal:** Enhance feature screens

#### 3.1 SettingsScreen (2 days)
- Two-column layout on tablets
- Responsive section headers
- Adaptive spacing

#### 3.2 TemplateScreen (2 days)
- Grid layout for template cards
- Two-pane editor on tablets
- Responsive form inputs

#### 3.3 TagManagementScreen (1 day)
- Adaptive tag chip wrapping
- Grid view for tag list

#### 3.4 MonitoringScreen (1 day)
- Responsive status cards
- Grid layout for multiple monitors

#### 3.5 HistoryScreen (2 days)
- Two-pane layout (list + detail) on tablets
- Adaptive list items
- Efficient scrolling

#### 3.6 FolderSelectorScreen (2 days)
- Adaptive folder tree layout
- Two-pane browser on tablets
- Responsive breadcrumbs

**Total Phase 3:** 10 days

---

### Phase 4: Low Priority & Polish (Week 6)
**Goal:** Complete remaining screens and polish

#### 4.1 AISuggestionsScreen (0.5 days)

**Current Issues:**
- Suggestion cards may stack inefficiently
- Fixed card dimensions

**Changes:**
1. Use `ResponsiveCardGrid` for suggestion cards
   - 1 column on compact
   - 2 columns on medium/expanded
2. Update padding with `Dimensions.cardPadding`
3. Ensure suggestion text wraps properly

**Acceptance Criteria:**
- ✅ Suggestion cards display efficiently on all screens
- ✅ No text truncation or overflow
- ✅ Proper spacing between cards

#### 4.2 RegexBuilderScreen (0.5 days)

**Current Issues:**
- Complex builder interface may be cramped
- Pattern preview area fixed size

**Changes:**
1. Implement two-pane layout on tablets:
   - Left: Regex controls
   - Right: Live pattern preview
2. Use responsive padding for input fields
3. Adaptive button sizing

**Acceptance Criteria:**
- ✅ Builder tools accessible on small screens
- ✅ Pattern preview visible on tablets
- ✅ All controls meet 48dp touch target

#### 4.3 MetadataPickerScreen (0.5 days)

**Current Issues:**
- Metadata variable list may be cramped
- Fixed modal size

**Changes:**
1. Use `ResponsiveGridLayout` for metadata variables
   - 2 columns compact
   - 3 columns medium
   - 4 columns expanded
2. Adaptive modal sizing based on WindowSizeClass
3. Responsive chip layout

**Acceptance Criteria:**
- ✅ Variables easily selectable on all screens
- ✅ Modal doesn't overwhelm small screens
- ✅ Chips wrap properly

#### 4.4 OCRScreen (0.5 days)

**Current Issues:**
- Image preview and text output fixed layout
- May not optimize space on tablets

**Changes:**
1. Implement adaptive layout:
   - Compact: Stacked (image above, text below)
   - Medium/Expanded: Side-by-side (image left, text right)
2. Responsive text area sizing
3. Adaptive button layout

**Acceptance Criteria:**
- ✅ Image and text both visible efficiently
- ✅ Text area properly sized
- ✅ Works in both orientations

#### 4.5 QRScannerScreen (0.5 days)

**Current Issues:**
- Camera preview fixed aspect ratio
- Overlay guides may not scale

**Changes:**
1. Adaptive camera preview sizing
2. Responsive overlay guides (maintain center square)
3. Position action buttons responsively
4. Handle safe areas (notches, camera cutouts)

**Acceptance Criteria:**
- ✅ Camera preview fills screen appropriately
- ✅ Scan guides visible on all screens
- ✅ Buttons don't overlap content

#### 4.6 QRDisplayScreen (0.25 days)

**Current Issues:**
- QR code fixed size
- Simple centered layout

**Changes:**
1. Adaptive QR code sizing:
   - Compact: 200dp
   - Medium: 300dp
   - Expanded: 400dp
2. Ensure QR remains centered with proper margins
3. Responsive text below QR

**Acceptance Criteria:**
- ✅ QR code appropriately sized for screen
- ✅ Always scannable (not too small)
- ✅ Proper spacing around QR

#### 4.7 CloudSyncScreen (0.5 days)

**Current Issues:**
- Sync status cards stacked
- Settings list cramped

**Changes:**
1. Use `ResponsiveCardGrid` for sync status cards
2. Two-pane layout on tablets (status left, settings right)
3. Responsive list items for sync history
4. Adaptive button group for sync actions

**Acceptance Criteria:**
- ✅ Status cards efficiently displayed
- ✅ Settings accessible on tablets
- ✅ Sync history scrolls smoothly

#### 4.8 AccountScreen (0.5 days)

**Current Issues:**
- Profile section fixed layout
- Settings list vertical only

**Changes:**
1. Adaptive profile header:
   - Compact: Avatar above, info below
   - Medium/Expanded: Avatar left, info right
2. Two-column settings list on tablets
3. Responsive form fields for account editing

**Acceptance Criteria:**
- ✅ Profile displays efficiently
- ✅ Settings organized for tablets
- ✅ Edit forms properly sized

#### 4.9 ActivityLogScreen (0.5 days)

**Current Issues:**
- Activity list single column
- Fixed item height

**Changes:**
1. Use adaptive list item layout
2. Two-pane on tablets (list + activity detail)
3. Responsive timestamp and action text
4. Filter chips wrap responsively

**Acceptance Criteria:**
- ✅ Activity items clear on all screens
- ✅ Detail pane shows on tablets
- ✅ Filters accessible

#### 4.10 BatchProcessScreen (0.75 days)

**Current Issues:**
- Operation cards stacked vertically
- Progress indicators fixed size

**Changes:**
1. Use `ResponsiveCardGrid` for operation cards
   - 1 column compact
   - 2 columns medium/expanded
2. Adaptive progress bar sizing
3. Responsive action buttons (use `AdaptiveButtonGroup`)
4. Two-pane layout for operation detail on tablets

**Acceptance Criteria:**
- ✅ Operations visible efficiently
- ✅ Progress clear on all screens
- ✅ Actions don't overflow

#### 4.11 Cross-Screen Consistency Review (1 day)

**Focus Areas:**
1. **Dimension Usage Audit**
   - Verify all screens use `Dimensions.*` tokens
   - No hardcoded values remain (except 48dp touch targets)
   - Consistent spacing patterns

2. **Component Usage Audit**
   - All grids use enhanced `ResponsiveGridLayout` or new `ResponsiveCardGrid`
   - All cards use updated `ContentCard`
   - All button groups use `AdaptiveButtonGroup` where appropriate
   - All master-detail layouts use `TwoPaneLayout`

3. **Navigation Consistency**
   - Responsive padding in all navigation components
   - Drawer width adapts on tablets
   - Bottom bar properly positioned on all screens

4. **Typography Audit**
   - All text uses Material 3 typography tokens
   - No hardcoded text sizes
   - Text wraps properly on small screens

**Deliverables:**
- Audit checklist completed for all screens
- List of inconsistencies fixed
- Documentation of any intentional deviations

#### 4.12 Performance Optimization (0.5 days)

**Optimization Tasks:**
1. **Recomposition Optimization**
   - Profile all screens with Layout Inspector
   - Identify unnecessary recompositions
   - Add `remember` and `derivedStateOf` where needed
   - Ensure WindowSizeClass doesn't cause excessive recomposition

2. **Layout Performance**
   - Verify no layout jank during orientation changes
   - Test scroll performance on all list/grid screens
   - Optimize image loading in grids (proper sizing)

3. **Memory Optimization**
   - Check for memory leaks in responsive components
   - Optimize bitmap caching for different screen sizes
   - Profile memory usage on low-end devices

**Acceptance Criteria:**
- ✅ 60fps maintained during orientation changes
- ✅ No frame drops on list/grid scrolling
- ✅ Memory usage within acceptable limits

#### 4.13 Accessibility Audit (0.5 days)

**Audit Tasks:**
1. **Touch Targets**
   - Verify all interactive elements ≥48dp
   - Add padding where needed
   - Test with TalkBack enabled

2. **Screen Reader Support**
   - All content properly labeled
   - Navigation order logical
   - Dynamic content announces changes

3. **Color Contrast**
   - Text meets WCAG AA standards
   - Works with dynamic color on/off
   - High contrast mode compatible

4. **Scaling Support**
   - Test with system font size 1.5x
   - Test with display size Large
   - Ensure no text truncation

**Deliverables:**
- Accessibility checklist completed
- Issues documented and fixed
- TalkBack testing report

**Total Phase 4:** 6.5 days

---

### Phase 5: Testing & Documentation (Week 7)
**Goal:** Ensure quality and maintainability

#### 5.1 Unit Testing (1 day)

**Test Coverage Targets:**
- Dimension system: 100%
- WindowSizeClass helpers: 100%
- Responsive components: ≥95%

**Test Files to Create:**

1. **`DimensionsTest.kt`**
```kotlin
class DimensionsTest {
    @Test
    fun `contentPadding returns 16dp for compact screens`()
    
    @Test
    fun `contentPadding returns 24dp for medium screens`()
    
    @Test
    fun `contentPadding returns 32dp for expanded screens`()
    
    @Test
    fun `cardPadding scales correctly across screen sizes`()
    
    @Test
    fun `minTouchTarget is always 48dp`()
    
    @Test
    fun `icon sizes scale correctly for extraLarge`()
    
    @Test
    fun `grid minItemWidth adapts to WindowSizeClass`()
}
```

2. **`WindowSizeClassTest.kt`**
```kotlin
class WindowSizeClassTest {
    @Test
    fun `calculateWindowSizeClass returns compact for 360dp width`()
    
    @Test
    fun `calculateWindowSizeClass returns medium for 600dp width`()
    
    @Test
    fun `calculateWindowSizeClass returns expanded for 840dp width`()
    
    @Test
    fun `isTablet returns false for compact screens`()
    
    @Test
    fun `isTablet returns true for medium and expanded screens`()
    
    @Test
    fun `isLandscape detects orientation correctly`()
    
    @Test
    fun `getGridColumns returns correct count for each size class`()
}
```

3. **`AdaptiveLayoutsTest.kt`**
```kotlin
class AdaptiveLayoutsTest {
    @Test
    fun `TwoPaneLayout shows single pane on compact`()
    
    @Test
    fun `TwoPaneLayout shows two panes on medium and expanded`()
    
    @Test
    fun `AdaptiveButtonGroup shows 2 buttons per row on compact`()
    
    @Test
    fun `AdaptiveButtonGroup shows 3 buttons per row on medium`()
    
    @Test
    fun `ResponsiveCardGrid uses correct columns for each size class`()
}
```

**Deliverables:**
- ✅ All dimension calculations tested
- ✅ WindowSizeClass logic verified
- ✅ Component behavior validated
- ✅ 100% line coverage for new foundation code

#### 5.2 UI Component Testing (1 day)

**Test Strategy:** Compose Testing with different WindowSizeClass configurations

**Test Files to Create:**

1. **`ResponsiveComponentsTest.kt`**
```kotlin
class ResponsiveComponentsTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun `MainContentArea uses responsive padding`()
    
    @Test
    fun `ContentCard uses responsive padding`()
    
    @Test
    fun `ResponsiveGridLayout adapts columns correctly`()
    
    @Test
    fun `ResponsiveCardGrid shows correct number of columns`()
}
```

2. **Screen-Specific Tests** (sample for critical screens)

**`HomeScreenResponsiveTest.kt`**
```kotlin
class HomeScreenResponsiveTest {
    @Test
    fun `feature cards display in single column on compact screens`()
    
    @Test
    fun `feature cards display in 2 columns on medium screens`()
    
    @Test
    fun `feature cards display in 3 columns on expanded screens`()
    
    @Test
    fun `all touch targets meet 48dp minimum on compact screens`()
}
```

**`RenameConfigScreenResponsiveTest.kt`**
```kotlin
class RenameConfigScreenResponsiveTest {
    @Test
    fun `helper tools show 2 buttons per row on compact`()
    
    @Test
    fun `helper tools show 4 buttons per row on expanded`()
    
    @Test
    fun `two-pane layout appears on medium and expanded screens`()
    
    @Test
    fun `form inputs don't overflow on compact screens`()
}
```

**Test Matrix:**
- High Priority (P0) screens: Full responsive test suite
- Medium Priority (P1) screens: Core responsive tests
- Low Priority (P2) screens: Basic layout tests

**Deliverables:**
- ✅ Core components tested on all screen sizes
- ✅ P0 screens have comprehensive test coverage
- ✅ P1/P2 screens have basic test coverage
- ✅ All tests passing in CI

#### 5.3 Visual Regression Testing (0.5 days)

**Setup:**
1. Integrate Screenshot Testing Library
2. Configure baseline capture process
3. Set up automated comparison

**Screenshot Matrix:**

For each P0 screen:
- Compact Portrait (360dp × 640dp)
- Compact Landscape (640dp × 360dp)
- Medium Portrait (600dp × 960dp)
- Medium Landscape (960dp × 600dp)
- Expanded Landscape (1024dp × 768dp)

For P1/P2 screens:
- Compact Portrait
- Medium Landscape
- Expanded Landscape

**Test File Structure:**
```kotlin
class ScreenNameScreenshotTest {
    @get:Rule
    val screenshotRule = ScreenshotTestRule()
    
    @Test
    fun homeScreen_compact_portrait_matchesBaseline()
    
    @Test
    fun homeScreen_medium_landscape_matchesBaseline()
    
    // ... etc
}
```

**Deliverables:**
- ✅ Screenshot baselines captured for all configurations
- ✅ Automated comparison in CI
- ✅ Visual diff reports for failures

#### 5.4 Device Testing (0.5 days)

**Physical Device Testing:**

**Required Devices:**
1. **Small Phone** (≤360dp width)
   - Test: All P0 screens
   - Focus: Cramping, overflow, touch targets

2. **Normal Phone** (411dp width)
   - Test: Full app navigation
   - Focus: General usability

3. **Large Phone** (428dp+ width)
   - Test: Spot check P0 screens
   - Focus: Space utilization

4. **Tablet** (600dp+ width)
   - Test: All P0 and P1 screens
   - Focus: Multi-column layouts, two-pane views

**Testing Protocol:**

1. **Layout Verification**
   - No horizontal scrolling
   - No overlapping elements
   - Proper content clipping
   - Correct column counts

2. **Interaction Testing**
   - All touch targets reachable
   - Buttons don't overlap
   - Gestures work correctly
   - Focus navigation logical

3. **Orientation Testing**
   - Smooth transitions
   - No layout breaks
   - Content preserved
   - Proper reflow

4. **Edge Cases**
   - System font size Large
   - Display size Large
   - Split-screen mode (Android 7+)
   - Foldable devices (if available)

**Test Report Template:**
```markdown
## Device: [Device Name]
- Screen Size: [width × height dp]
- Android Version: [version]
- Test Date: [date]

### Pass/Fail Summary
- Layout: ✅/❌
- Interactions: ✅/❌
- Orientation: ✅/❌
- Edge Cases: ✅/❌

### Issues Found
1. [Screen Name] - [Issue description] - Priority: P0/P1/P2
2. ...

### Screenshots
[Attach screenshots of any issues]
```

**Deliverables:**
- ✅ Test report for each device type
- ✅ All P0 issues fixed before release
- ✅ P1/P2 issues documented for future work

#### 5.5 Performance Testing (0.5 days)

**Testing Tools:**
- Android Studio Layout Inspector
- Systrace for frame timing
- Memory Profiler
- CPU Profiler

**Performance Benchmarks:**

1. **Orientation Change Performance**
   - Target: <200ms recomposition time
   - Test: All P0 screens
   - Measure: Time to stable layout

2. **Scroll Performance**
   - Target: 60fps (0 jank)
   - Test: All list/grid screens
   - Measure: Frame drops during fling

3. **Memory Usage**
   - Target: No leaks, <50MB increase per screen
   - Test: Navigate through all screens
   - Measure: Heap allocation deltas

4. **Cold Start Time**
   - Target: No regression from pre-responsive code
   - Test: App launch to HomeScreen
   - Measure: Time to interactive

**Test Protocol:**

```kotlin
@Test
fun measureOrientationChangePerformance() {
    // 1. Load screen in portrait
    // 2. Trigger rotation to landscape
    // 3. Measure recomposition time
    // 4. Assert < 200ms
}

@Test
fun measureScrollPerformance() {
    // 1. Load list/grid screen with 100 items
    // 2. Perform fling scroll
    // 3. Measure frame drops
    // 4. Assert 0 dropped frames
}
```

**Deliverables:**
- ✅ Performance report for all critical screens
- ✅ Benchmarks meet targets or issues documented
- ✅ Optimization recommendations if needed

#### 5.6 Documentation: RESPONSIVE_DESIGN_GUIDE.md (0.5 days)

**New File:** `docs/RESPONSIVE_DESIGN_GUIDE.md`

**Contents:**

```markdown
# Responsive Design Implementation Guide

## Overview
Comprehensive guide for implementing responsive layouts in the Files Management app.

## Quick Start
[5-minute tutorial with code example]

## Dimension System
### Using Responsive Dimensions
- When to use Dimensions.contentPadding vs cardPadding
- How to choose spacing tokens
- Icon sizing guidelines

### Migration from Hardcoded Values
- Before/after code examples
- Common migration patterns
- Troubleshooting

## WindowSizeClass API
### Understanding WindowSizeClass
- Compact, Medium, Expanded explained
- Breakpoint table
- How to test different size classes

### Accessing WindowSizeClass
- Using LocalWindowSizeClass
- Helper functions (isTablet, isLandscape, getGridColumns)
- Custom size-based logic

## Responsive Components
### Enhanced Existing Components
- MainContentArea & ResponsiveGridLayout
- ContentCard with responsive padding
- When to use which component

### New Adaptive Components
- TwoPaneLayout for master-detail
- AdaptiveButtonGroup to prevent overflow
- ResponsiveCardGrid for fixed columns

### Component Selection Guide
[Decision tree: "Which component should I use?"]

## Layout Patterns by Use Case
### List Screens
- Single column → Two-pane pattern
- Code example

### Form Screens
- Stacked → Side-by-side pattern
- Code example

### Grid Screens
- 2 → 3 → 4 columns pattern
- Code example

### Dashboard Screens
- Vertical stack → Card grid pattern
- Code example

## Testing Responsive Layouts
### Preview Configurations
- Standard preview template
- How to test all size classes

### Unit Testing
- Testing dimension calculations
- Testing WindowSizeClass logic

### Device Testing
- Emulator setup
- Physical device testing checklist

## Common Issues & Solutions
### Issue: Text Truncation
[Solution with code]

### Issue: Button Overflow
[Solution with code]

### Issue: Poor Space Utilization on Tablets
[Solution with code]

### Issue: Orientation Change Jank
[Solution with code]

## Best Practices
- ✅ Do: Use Dimensions.* tokens
- ❌ Don't: Hardcode dimension values
- ✅ Do: Test on multiple screen sizes
- ❌ Don't: Assume phone-only usage
- [More best practices]

## Examples
### Full Screen Implementation
[Complete example: HomeScreen before/after]

### Component Implementation
[Complete example: Custom responsive component]

## Resources
- Material 3 Adaptive Guidelines
- Jetpack Compose Responsive UI
- WindowSizeClass API Reference
```

**Deliverables:**
- ✅ Comprehensive guide written
- ✅ All code examples tested
- ✅ Screenshots/diagrams included

#### 5.7 Documentation: Update Existing Docs (0.5 days)

**Files to Update:**

1. **`docs/UI_GUIDELINES.md`**

Add new section:
```markdown
## Responsive Design

### Dimension System
[Link to RESPONSIVE_DESIGN_GUIDE.md]
- Use Dimensions.* for all spacing
- No hardcoded dimensions except 48dp touch targets

### WindowSizeClass
[Explanation and link]

### Layout Patterns
- Compact: Single column, minimal info
- Medium: Two columns, more info
- Expanded: Multi-column, master-detail

### Component Guidelines
- When to use ResponsiveGridLayout vs ResponsiveCardGrid
- TwoPaneLayout for master-detail
- AdaptiveButtonGroup for buttons
```

2. **`docs/README.md`**

Update project overview:
```markdown
## Platform Support
- **Android Version:** 13+ (API 33+)
- **Screen Sizes:** 360dp - 1200dp width
- **Orientations:** Portrait and Landscape
- **Devices:** Phones, Tablets, Foldables
```

3. **`.github/copilot-instructions.md`**

Add responsive design section:
```markdown
## Responsive Design Standards

### Always Use Responsive Dimensions
- `Dimensions.contentPadding` for main areas
- `Dimensions.cardPadding` for cards
- `Dimensions.itemSpacing` for lists/grids
- NEVER hardcode spacing values

### WindowSizeClass-Based Logic
- Use `LocalWindowSizeClass.current` for adaptive layouts
- Test with `isTablet()` and `isLandscape()` helpers
- Provide appropriate layouts for Compact/Medium/Expanded

### Required Components
- Use `ResponsiveGridLayout` or `ResponsiveCardGrid` for grids
- Use `TwoPaneLayout` for master-detail on tablets
- Use `AdaptiveButtonGroup` to prevent button overflow
- Use enhanced `ContentCard` for all cards

### Testing Requirements
- Add preview configurations for all screen sizes
- Test orientation changes
- Verify 48dp minimum touch targets
```

4. **`BACKEND_FRONTEND_CLEANUP_PLAN.md`**

Add note about responsive implementation:
```markdown
## ✅ Completed: Responsive Design Implementation
- Centralized dimension system eliminates scattered dimension tokens
- Enhanced existing components (MainContentArea, ContentCard) for responsiveness
- Added new adaptive patterns (TwoPaneLayout, AdaptiveButtonGroup, ResponsiveCardGrid)
- All screens support 360dp-1200dp width range
- Tablet-optimized layouts implemented
```

**Deliverables:**
- ✅ All documentation updated
- ✅ Consistent terminology across docs
- ✅ Easy discoverability of responsive guidelines

#### 5.8 Documentation: DIMENSION_SYSTEM.md (0.5 days)

**New File:** `docs/DIMENSION_SYSTEM.md`

**Contents:**

```markdown
# Dimension System Reference

## Overview
Centralized responsive dimension system for consistent spacing and sizing.

## Core Principles
1. No hardcoded dimensions (except 48dp touch targets)
2. All spacing scales with WindowSizeClass
3. Consistent tokens across entire app

## Dimension Tokens Reference

### Content Spacing
| Token | Compact | Medium | Expanded | Usage |
|-------|---------|--------|----------|-------|
| `contentPadding` | 16dp | 24dp | 32dp | Main content areas |
| `cardPadding` | 12dp | 16dp | 20dp | Inside cards |
| `sectionSpacing` | 16dp | 20dp | 24dp | Between sections |
| `itemSpacing` | 8dp | 12dp | 16dp | List/grid item gaps |

### Icons
| Token | All Sizes | Usage |
|-------|-----------|-------|
| `Icon.small` | 16dp | Inline icons |
| `Icon.medium` | 24dp | Standard icons |
| `Icon.large` | 32dp | Featured icons |
| `Icon.extraLarge` | 48dp / 56dp / 64dp | Hero icons (responsive) |

### Grid Configuration
| Token | Compact | Medium | Expanded |
|-------|---------|--------|----------|
| `Grid.minItemWidth` | 120dp | 150dp | 200dp |

### Touch Targets
| Token | Value | Usage |
|-------|-------|-------|
| `minTouchTarget` | 48dp | Minimum interactive size |

## Usage Examples

### Basic Padding
```kotlin
// ❌ WRONG
Column(modifier = Modifier.padding(16.dp))

// ✅ CORRECT
Column(modifier = Modifier.padding(Dimensions.contentPadding))
```

### Spacing
```kotlin
// ❌ WRONG
Column(verticalArrangement = Arrangement.spacedBy(12.dp))

// ✅ CORRECT
Column(verticalArrangement = Arrangement.spacedBy(Dimensions.itemSpacing))
```

### Icons
```kotlin
// ❌ WRONG
Icon(modifier = Modifier.size(24.dp))

// ✅ CORRECT
Icon(modifier = Modifier.size(Dimensions.Icon.medium))

// For responsive large icons:
Icon(modifier = Modifier.size(Dimensions.Icon.extraLarge))
```

### Grid Configuration
```kotlin
// ❌ WRONG
ResponsiveGridLayout(minItemWidth = 120.dp)

// ✅ CORRECT
ResponsiveGridLayout(minItemWidth = Dimensions.Grid.getMinItemWidth())
```

## Migration Guide

### Step 1: Identify Hardcoded Values
Search for: `.padding(\d+\.dp)`, `.size(\d+\.dp)`, `spacedBy(\d+\.dp)`

### Step 2: Map to Dimension Tokens
Use conversion table in Appendix B of RESPONSIVE_DESIGN_ROADMAP.md

### Step 3: Replace Values
```kotlin
// Before
Box(modifier = Modifier.padding(16.dp))
Icon(modifier = Modifier.size(48.dp))

// After
Box(modifier = Modifier.padding(Dimensions.contentPadding))
Icon(modifier = Modifier.size(Dimensions.Icon.extraLarge))
```

### Step 4: Test
- Add preview configurations
- Test on compact/medium/expanded emulators
- Verify spacing scales correctly

## Troubleshooting

### Spacing Feels Wrong
- Check WindowSizeClass is provided via CompositionLocal
- Verify using correct token (content vs card vs item spacing)
- Test in actual preview, not just in IDE

### Icons Too Large/Small
- extraLarge icons are responsive, others are fixed
- Use medium for standard icons, extraLarge only for hero/featured

### Grid Not Adapting
- Use `Dimensions.Grid.getMinItemWidth()` not fixed value
- Verify `GridCells.Adaptive()` is being used
- Check WindowSizeClass provider

## When to Deviate

Acceptable exceptions:
1. **48dp Touch Targets** - Material Design requirement
2. **1dp Dividers** - Visual separators
3. **Fixed Aspect Ratios** - When maintaining specific ratios (1:1, 16:9)
4. **Design-Specific Values** - Must document reason in code comment

```kotlin
// OK: Touch target requirement
Button(modifier = Modifier.heightIn(min = 48.dp))

// OK: Divider
Divider(thickness = 1.dp)

// OK: Aspect ratio
Image(modifier = Modifier.aspectRatio(16f / 9f))

// OK: Documented exception
Box(modifier = Modifier.height(200.dp)) // Fixed height for animation
```

## Testing Dimensions

```kotlin
class DimensionsTest {
    @Test
    fun `contentPadding scales with WindowSizeClass`() {
        // Test compact
        val compactWindowSize = WindowSizeClass.calculateFromSize(DpSize(360.dp, 640.dp))
        composeTestRule.setContent {
            CompositionLocalProvider(LocalWindowSizeClass provides compactWindowSize) {
                assertEquals(16.dp, Dimensions.contentPadding)
            }
        }
        
        // Test medium
        val mediumWindowSize = WindowSizeClass.calculateFromSize(DpSize(600.dp, 960.dp))
        composeTestRule.setContent {
            CompositionLocalProvider(LocalWindowSizeClass provides mediumWindowSize) {
                assertEquals(24.dp, Dimensions.contentPadding)
            }
        }
    }
}
```
```

**Deliverables:**
- ✅ Complete dimension token reference
- ✅ Clear usage examples
- ✅ Migration guide
- ✅ Troubleshooting section

**Total Phase 5:** 5 days

---

## 📏 Design Standards & Patterns

### Screen Size Breakpoints

Following Material 3 WindowSizeClass:

| Size Class | Width Range | Device Examples | Layout Strategy |
|-----------|-------------|-----------------|-----------------|
| **Compact** | 0dp - 599dp | Phones (portrait) | Single column, minimal info |
| **Medium** | 600dp - 839dp | Tablets (portrait), Phones (landscape) | Two columns, more info |
| **Expanded** | 840dp+ | Tablets (landscape), Foldables | Multi-column, master-detail |

### Dimension Scaling Guidelines

| Element | Compact | Medium | Expanded |
|---------|---------|--------|----------|
| Content padding | 16dp | 24dp | 32dp |
| Card padding | 12dp | 16dp | 20dp |
| Section spacing | 16dp | 20dp | 24dp |
| Item spacing | 8dp | 12dp | 16dp |
| Icon small | 16dp | 16dp | 16dp |
| Icon medium | 24dp | 24dp | 24dp |
| Icon large | 32dp | 32dp | 32dp |
| Icon extra large | 48dp | 56dp | 64dp |

### Layout Patterns by Screen

| Screen Type | Compact | Medium/Expanded |
|-------------|---------|-----------------|
| **List Screen** | Single column | Two columns or list + detail |
| **Form Screen** | Vertical form | Form + preview pane |
| **Grid Screen** | 2 columns | 3-4 columns |
| **Dashboard** | Stacked cards | Card grid (2×2 or 3×3) |
| **Detail View** | Full screen | Master-detail split |

### Typography Scaling

Use Material 3 typography tokens - they scale automatically:
- `titleLarge` / `titleMedium` / `titleSmall` for headers
- `bodyLarge` / `bodyMedium` / `bodySmall` for content
- `labelLarge` / `labelMedium` / `labelSmall` for UI elements

Avoid hardcoded text sizes unless absolutely necessary.

### Touch Target Guidelines

- **Minimum:** 48dp × 48dp (Material Design standard)
- **Recommended:** 56dp × 56dp for primary actions
- Add padding around small icons to meet minimum
- Use `Modifier.minimumInteractiveComponentSize()` to enforce

### Grid Configurations

```kotlin
// File grids
Compact: GridCells.Adaptive(120.dp) → ~2-3 columns
Medium: GridCells.Adaptive(150.dp) → ~3-4 columns
Expanded: GridCells.Adaptive(200.dp) → ~4-5 columns

// Card grids (larger items)
Compact: GridCells.Fixed(1) → 1 column
Medium: GridCells.Fixed(2) → 2 columns
Expanded: GridCells.Fixed(3) → 3 columns
```

---

## 🧪 Testing Strategy

### Test Devices & Configurations

#### Emulators Required
1. **Small Phone** (360dp × 640dp) - Pixel 2
2. **Normal Phone** (411dp × 731dp) - Pixel 5
3. **Large Phone** (428dp × 926dp) - Pixel 7 Pro
4. **Small Tablet** (600dp × 960dp) - Nexus 7
5. **Large Tablet** (1024dp × 768dp) - Pixel Tablet

#### Test Orientations
- Portrait (all devices)
- Landscape (all devices)
- Rotation transitions (smooth recomposition)

### Preview Configurations

Add to all screens:

```kotlin
@Preview(name = "Phone - Portrait", device = "spec:width=360dp,height=640dp")
@Preview(name = "Phone - Landscape", device = "spec:width=640dp,height=360dp")
@Preview(name = "Tablet - Portrait", device = "spec:width=600dp,height=960dp")
@Preview(name = "Tablet - Landscape", device = "spec:width=960dp,height=600dp")
@Composable
fun ScreenNamePreview() {
    ConversionTheme {
        // Screen content
    }
}
```

### Automated Tests

#### Unit Tests
```kotlin
@Test
fun `dimensions scale correctly for compact screens`() {
    val windowSize = WindowSizeClass.calculateFromSize(DpSize(360.dp, 640.dp))
    // Assert dimensions
}
```

#### UI Tests
```kotlin
@Test
fun `homeScreen displays grid layout on tablet`() {
    composeTestRule.setContent {
        CompositionLocalProvider(LocalWindowSizeClass provides mediumWindowSize) {
            HomeScreen(...)
        }
    }
    // Assert grid is visible
}
```

### Visual Regression

Use Screenshot Testing Library:
```kotlin
@Test
fun `homeScreen_compact_portrait_matchesGolden`() {
    screenshotTestRule.snapshot(
        "home_compact_portrait",
        HomeScreen()
    )
}
```

---

## 🚧 Migration Strategy

### For Each Screen

1. **Analysis** (15 min)
   - Identify all hardcoded dimensions
   - Note layout structure (Column, Row, Grid)
   - List components used

2. **Planning** (15 min)
   - Decide on layout strategy (single/multi-column, two-pane, etc.)
   - Map old dimensions to new dimension tokens
   - Identify custom responsive logic needed

3. **Implementation** (2-4 hours)
   - Replace hardcoded dimensions with `Dimensions.*`
   - Refactor layouts to use responsive components
   - Add WindowSizeClass-based logic
   - Update component usage

4. **Testing** (1 hour)
   - Add preview configurations
   - Test on all emulator sizes
   - Check orientation changes
   - Verify touch targets

5. **Review** (30 min)
   - Code review with focus on responsiveness
   - Verify against design standards
   - Check accessibility

### Code Review Checklist

- [ ] No hardcoded dimensions (except standard values like 48dp for touch targets)
- [ ] Uses `Dimensions.*` for spacing/sizing
- [ ] Adapts layout based on `WindowSizeClass`
- [ ] Preview configurations for all screen sizes
- [ ] Touch targets meet 48dp minimum
- [ ] Typography uses Material 3 tokens
- [ ] Accessibility semantics preserved
- [ ] No horizontal scrolling on any screen size
- [ ] Performance: no jank during orientation changes

---

## 📚 Documentation Updates

### Files to Update

1. **UI_GUIDELINES.md**
   - Add "Responsive Design" section
   - Document dimension system
   - Add layout pattern examples
   - Update component usage guidelines

2. **README.md**
   - Add screen size support information
   - Update screenshots with tablet layouts

3. **Code Comments**
   - Document responsive behavior in components
   - Explain WindowSizeClass usage
   - Note any edge cases

### New Documentation

1. **RESPONSIVE_DESIGN_GUIDE.md**
   - Comprehensive guide for developers
   - Code examples for common patterns
   - Troubleshooting section
   - Best practices

2. **DIMENSION_SYSTEM.md**
   - Complete dimension token reference
   - Usage examples
   - Migration guide from hardcoded values

---

## ⚠️ Risks & Mitigation

### Risk: Breaking Existing Layouts
**Mitigation:**
- Implement phase-by-phase (one screen at a time)
- Extensive testing before moving to next screen
- Keep old code in git history for reference
- Use feature flags if needed

### Risk: Performance Impact
**Mitigation:**
- Use CompositionLocal for WindowSizeClass (computed once)
- Avoid excessive recomposition
- Profile with Layout Inspector
- Benchmark critical screens

### Risk: Design Inconsistency
**Mitigation:**
- Strict adherence to dimension system
- Code reviews focus on consistency
- Use shared responsive components
- Regular design reviews

### Risk: Testing Coverage Gaps
**Mitigation:**
- Mandatory preview configurations
- Automated screenshot tests
- Real device testing on key screens
- User acceptance testing

### Risk: Time Overruns
**Mitigation:**
- Start with high-priority screens
- Low-priority screens can ship in later release
- Buffer time in estimates (30% added)
- Regular progress tracking

---

## 📈 Success Metrics

### Quantitative Metrics
- **Layout Shifts**: 0 layout-related crashes in production
- **Test Coverage**: ≥95% of screens have responsive tests
- **Device Support**: 100% of screens work on 360dp-1024dp range
- **Performance**: No frame drops during rotation (maintain 60fps)
- **Touch Targets**: 100% of interactive elements meet 48dp minimum

### Qualitative Metrics
- **User Feedback**: Positive feedback on UI improvements
- **Developer Experience**: Easier to implement new screens
- **Design Consistency**: All screens follow same patterns
- **Accessibility**: Improved screen reader experience

### Tracking
- Use Firebase Analytics to track:
  - Screen size distribution of users
  - Orientation usage patterns
  - Time spent on screens (before/after improvements)
- Track crash reports related to UI rendering
- Monitor Play Store reviews for UI-related comments

---

## 🎯 Quick Wins for Immediate Impact

Before starting full implementation, these can be done quickly:

1. **Add WindowSizeClass to MainActivity** (1 hour)
   - Provides infrastructure for all future work

2. **Fix RenameConfigScreen Helper Buttons** (2 hours)
   - Most visible cramping issue
   - Simple row-to-FlowRow change

3. **Increase HomeScreen Card Spacing** (1 hour)
   - Makes phone experience immediately better
   - Low risk change

4. **Update FileSelectionScreen Grid** (1 hour)
   - Better use of space on tablets
   - Improves core workflow

**Total Quick Wins:** ~5 hours, high user impact

---

## 📞 Support & Resources

### Key Contacts
- **Sokchea**: Frontend/UI implementation
- **Kai**: Backend integration, testing support
- **Design Review**: Weekly check-ins

### Resources
- [Material 3 Adaptive Guidance](https://m3.material.io/foundations/layout/applying-layout/window-size-classes)
- [Jetpack Compose Responsive UI](https://developer.android.com/jetpack/compose/layouts/adaptive)
- [WindowSizeClass API](https://developer.android.com/reference/kotlin/androidx/compose/material3/windowsizeclass/WindowSizeClass)

### Tools
- Android Studio Layout Inspector
- Compose Preview with custom device specs
- Screenshot Testing Library
- Firebase Test Lab for device testing

---

## 📋 Appendix

### A. Example Migration: HomeScreen

**Before:**
```kotlin
Column(
    modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp),  // ❌ Hardcoded
    verticalArrangement = Arrangement.spacedBy(16.dp),  // ❌ Hardcoded
    horizontalAlignment = Alignment.CenterHorizontally
) {
    FeatureCard(title = "Batch Rename", ...)
    FeatureCard(title = "Tags", ...)
    FeatureCard(title = "Templates", ...)
    // ... more cards
}
```

**After:**
```kotlin
// ✅ Use EXISTING ResponsiveCardGrid (fixed-column variant)
ResponsiveCardGrid(  // NEW component from AdaptiveLayouts.kt
    compactColumns = 1,
    mediumColumns = 2,
    expandedColumns = 3
) {
    items(featureItems) { feature ->
        ContentCard(  // ✅ EXISTING component (now uses responsive padding internally)
            title = feature.title,
            ...
        )
    }
}

// NOTE: If using existing ResponsiveGridLayout with adaptive cells:
ResponsiveGridLayout(  // ✅ EXISTING component from MainContentArea.kt (updated with responsive defaults)
    minItemWidth = Dimensions.Grid.getMinItemWidth()  // Now responsive!
) {
    items(featureItems.size) { index ->
        ContentCard(title = featureItems[index].title, ...)
    }
}
```

**Key Changes:**
- ❌ DON'T recreate `ResponsiveGridLayout` - it already exists in `MainContentArea.kt`
- ✅ DO update it to use responsive `Dimensions.*` instead of hardcoded values
- ✅ DO use NEW `ResponsiveCardGrid` when you need fixed columns (1-2-3)
- ✅ DO use EXISTING `ResponsiveGridLayout` when you need adaptive sizing

### B. Dimension Migration Table

| Old Value | New Value | Notes |
|-----------|-----------|-------|
| `.padding(16.dp)` | `.padding(Dimensions.contentPadding)` | Main content areas |
| `.padding(12.dp)` | `.padding(Dimensions.cardPadding)` | Inside cards |
| `.padding(8.dp)` | `.padding(Dimensions.itemSpacing)` | Between items |
| `Arrangement.spacedBy(16.dp)` | `Arrangement.spacedBy(Dimensions.sectionSpacing)` | Section gaps |
| `Arrangement.spacedBy(8.dp)` | `Arrangement.spacedBy(Dimensions.itemSpacing)` | Item gaps |
| `.size(24.dp)` | `.size(Dimensions.Icon.medium)` | Standard icons |
| `.size(48.dp)` | `.size(Dimensions.Icon.extraLarge)` | Large icons (adaptive) |
| `GridCells.Adaptive(120.dp)` | `GridCells.Adaptive(Dimensions.Grid.getMinItemWidth())` | File grids |

### C. Preview Template

Add to every screen file:

```kotlin
// Standard preview configurations
@Preview(
    name = "Phone - Portrait",
    device = "spec:width=360dp,height=640dp,dpi=420",
    showBackground = true
)
@Preview(
    name = "Phone - Landscape",
    device = "spec:width=640dp,height=360dp,dpi=420",
    showBackground = true
)
@Preview(
    name = "Tablet - Portrait",
    device = "spec:width=600dp,height=960dp,dpi=240",
    showBackground = true
)
@Preview(
    name = "Tablet - Landscape",
    device = "spec:width=960dp,height=600dp,dpi=240",
    showBackground = true
)
@Preview(
    name = "Large Tablet",
    device = "spec:width=1024dp,height=768dp,dpi=240",
    showBackground = true
)
@Composable
private fun ScreenNamePreview() {
    ConversionTheme {
        // Provide WindowSizeClass based on preview config
        val windowSize = calculateWindowSizeClass()
        CompositionLocalProvider(LocalWindowSizeClass provides windowSize) {
            ScreenName(
                // Mock dependencies
            )
        }
    }
}
```

---

## ✅ Next Steps

1. **Review & Approval** (1 day)
   - Team review this roadmap
   - Get sign-off from stakeholders
   - Finalize priorities if needed

2. **Environment Setup** (1 day)
   - Set up emulators for all test sizes
   - Configure screenshot testing
   - Prepare development branch

3. **Begin Phase 1** (Week 1)
   - Start foundation implementation
   - Daily progress updates

4. **Weekly Reviews**
   - Demo completed screens
   - Adjust timeline if needed
   - Gather feedback

---

**Last Updated:** January 23, 2026  
**Status:** ✅ Ready for Implementation  
**Next Review:** After Phase 1 completion
