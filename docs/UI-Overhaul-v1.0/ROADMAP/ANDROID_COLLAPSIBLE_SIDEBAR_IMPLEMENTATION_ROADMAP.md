# Android Collapsible Sidebar Navigation Implementation Roadmap

**Document Version:** 1.0  
**Date:** January 22, 2026  
**Scope:** Implementing the React/Web Collapsible Sidebar design into the Android Jetpack Compose application  
**Target Platform:** Android (API 29+)  
**Tech Stack:** Kotlin, Jetpack Compose, Material 3, Navigation Compose

---

## 📋 Executive Summary

> **🔗 Related Document**: This roadmap is part of the broader [UI Navigation System Overhaul](./UI_NAVIGATION_SYSTEM_OVERHAUL.md). This document focuses on **implementing the root navigation component**, while the overhaul document defines the complete navigation architecture and screen inventory.

This roadmap provides a detailed plan to implement the **Collapsible Sidebar Navigation** design pattern from the web-based React prototype into the Files Management Service Android application. The implementation will:

- ✅ Replace the current bottom Scaffold navigation with a side navigation drawer
- ✅ Add smooth collapse/expand animations with configurable width transitions
- ✅ Maintain the current Material 3 design system and theme consistency
- ✅ Integrate with existing Compose Navigation architecture
- ✅ Preserve all existing screen functionality and navigation flows
- ✅ Add accessibility support (WCAG 2.1 AA compliance)
- ✅ Support both light and dark themes with dynamic colors

---

## 🔄 Design Comparison: Web vs Android

### Web Implementation (React/TypeScript)
```
Features:
- Collapsible sidebar with smooth transitions (300ms)
- Icon + label display in expanded state
- Icon-only display in collapsed state
- Date header (shows current date)
- 10 navigation items with active state styling
- Dark theme (black background, zinc colors)
- Auto-collapse on navigation
- Click-to-collapse main content area
```

### Android Target Implementation
```
Features:
- Collapsible permanent navigation drawer (Compose)
  * Always visible, never hidden (collapses to icon-only, not modal)
  * Collapsed width: 64.dp, Expanded width: 280.dp
- Icon + label display in expanded state
- Icon-only display in collapsed state
- Header section (displays date matching web design)
- Navigation items matching current structure (Home, Settings, etc.)
- Material 3 dynamic theming (supports light/dark/system)
- Auto-collapse on navigation (configurable)
- Click main content to collapse (phone/tablet only)
- Keyboard and screen reader support
- Landscape/portrait responsiveness
```

---

## 📐 Architecture Overview

### Current Architecture
```
MainActivity
    ↓
ConversionTheme (Material 3)
    ↓
Scaffold (default layout)
    ├─ ConversionNavHost (Navigation Compose)
    │   ├─ HomeScreen
    │   ├─ FileSelectionScreen
    │   ├─ RenameConfigScreen
    │   └─ ... 20+ screens
    └─ Default bottom navigation (if present)
```

### New Architecture with Collapsible Sidebar
```
MainActivity
    ↓
ConversionTheme (Material 3)
    ↓
CollapsibleSidebarLayout (NEW)
    ├─ CollapsibleNavigationDrawer (NEW)
    │   ├─ SidebarHeader (NEW)
    │   └─ NavigationItems (existing converted)
    ├─ ConversionNavHost (existing, refactored)
    │   ├─ HomeScreen
    │   ├─ FileSelectionScreen
    │   ├─ RenameConfigScreen
    │   └─ ... 20+ screens
    └─ Floating Action Button (optional)
```

---

## 🎯 Implementation Phases

> **📊 Progress Tracking:** Use the [Implementation Index](./COLLAPSIBLE_SIDEBAR_IMPLEMENTATION_INDEX.md) to track daily progress, update task statuses, and log blockers.

### Phase 1: Foundation & Core Components (Week 1)
**Duration:** 2-3 days  
**Owner:** Sokchea (UI Lead)  
**Status:** Not Started

#### Tasks
1. **Create CollapsibleNavigationDrawer Composable**
   - File: `presentation/ui/navigation/CollapsibleNavigationDrawer.kt`
   - Implements horizontal expand/collapse animation
   - Manages sidebar state (collapsed/expanded)
   - Properties:
     ```kotlin
     @Composable
     fun CollapsibleNavigationDrawer(
         isCollapsed: Boolean,
         onToggleCollapse: () -> Unit,
         modifier: Modifier = Modifier,
         collapsedWidth: Dp = 64.dp,
         expandedWidth: Dp = 280.dp,
         content: @Composable (BoxScope) -> Unit
     )
     ```

2. **Create SidebarHeader Composable**
   - File: `presentation/ui/navigation/SidebarHeader.kt`
   - Displays date/time or app branding
   - Responsive to collapsed state
   - Shows full header in expanded state, compact in collapsed state
   - Properties:
     ```kotlin
     @Composable
     fun SidebarHeader(
         isCollapsed: Boolean,
         modifier: Modifier = Modifier
     )
     ```

3. **Create NavigationItem Composable**
   - File: `presentation/ui/navigation/NavigationItem.kt`
   - Reusable navigation item component
   - Supports icon + label in expanded state
   - Tooltip support for collapsed state
   - Active/inactive styling with Material 3 tokens
   - Properties:
     ```kotlin
     @Composable
     fun NavigationItem(
         label: String,
         icon: @Composable () -> Unit,
         isSelected: Boolean,
         isCollapsed: Boolean,
         onClick: () -> Unit,
         modifier: Modifier = Modifier
     )
     ```

4. **Create CollapsibleSidebarLayout Composable**
   - File: `presentation/ui/navigation/CollapsibleSidebarLayout.kt`
   - Main layout wrapper combining drawer + content
   - Manages sidebar open/close state
   - Handles content area responsive sizing
   - Properties:
     ```kotlin
     @Composable
     fun CollapsibleSidebarLayout(
         sidebarContent: @Composable (BoxScope, Boolean) -> Unit,
         content: @Composable (BoxScope) -> Unit,
         modifier: Modifier = Modifier,
         collapsedWidth: Dp = 64.dp,
         expandedWidth: Dp = 280.dp
     )
     ```

#### Deliverables
- ✅ 4 new Composable functions with full documentation
- ✅ Integration tests for each component
- ✅ Preview compositions for design validation

---

### Phase 2: Navigation Integration (Week 1-2)
**Duration:** 2-3 days  
**Owner:** Sokchea (UI Lead)  
**Status:** Not Started

#### Tasks
1. **Create SidebarNavigationState ViewModel**
   - File: `presentation/ui/navigation/SidebarNavigationState.kt`
   - Manages sidebar open/close state
   - Persists preference to SharedPreferences or DataStore
   - Properties:
     ```kotlin
     data class SidebarNavigationState(
         val isCollapsed: Boolean = false,
         val activeRouteId: String? = null
     )
     
     class SidebarNavigationViewModel : ViewModel() {
         val state: StateFlow<SidebarNavigationState>
         fun toggleSidebar()
         fun setActiveRoute(routeId: String)
         fun collapseSidebarOnNavigation(routeId: String)
     }
     ```

2. **Refactor MainActivity Scaffold Layout**
   - File: `MainActivity.kt`
   - Replace current Scaffold with CollapsibleSidebarLayout
   - Connect NavHost to sidebar navigation items
   - Handle back button behavior

3. **Create Navigation Route Model**
   - File: `presentation/model/NavigationRoute.kt`
   - Represents sidebar navigation items
   - Associates routes with icons and labels
   - Properties:
     ```kotlin
     data class NavigationRoute(
         val id: String,
         val label: String,
         val icon: @Composable () -> Unit,
         val route: String,
         val badge: Int? = null
     )
     ```

4. **Update ConversionNavHost Integration**
   - File: `navigation/ConversionNavHost.kt`
   - Integrate sidebar active item tracking
   - Handle navigation item clicks
   - Auto-collapse behavior on navigation

#### Deliverables
- ✅ SidebarNavigationViewModel with state management
- ✅ Updated MainActivity with new layout structure
- ✅ Navigation routes properly mapped to sidebar items

---

### Phase 3: Visual Polish & Theming (Week 2)
**Duration:** 2 days  
**Owner:** Sokchea (UI Lead)  
**Status:** Not Started

#### Tasks
1. **Implement Material 3 Theming**
   - Apply `colorScheme.surface` for sidebar background
   - Use `colorScheme.onSurface` for text
   - Apply `colorScheme.primary` for active items
   - Use `colorScheme.outline` for borders/dividers
   - File: Update `CollapsibleNavigationDrawer.kt`

2. **Add Smooth Animations**
   - Width transition: `animateDpAsState` (300ms, EaseInOutCubic)
   - Opacity transitions for labels
   - Icon scale animation on collapse
   - File: `presentation/ui/navigation/CollapsibleNavigationDrawer.kt`
   - Code example:
     ```kotlin
     val currentWidth = animateDpAsState(
         targetValue = if (isCollapsed) collapsedWidth else expandedWidth,
         animationSpec = tween(300, easing = EaseInOutCubic),
         label = "sidebarWidthAnimation"
     )
     ```

3. **Create Dark/Light Theme Variants**
   - Light theme: Light surface, darker text
   - Dark theme: Dark surface, lighter text
   - OLED mode: Pure black background
   - File: Update `presentation/ui/theme/ConversionTheme.kt`

4. **Add Ripple Effects & Hover States**
   - Material 3 ripple for touch feedback
   - Hover state for mouse/trackpad (if applicable)
   - File: `presentation/ui/navigation/NavigationItem.kt`

5. **Implement Main Content Card Layout**
   - Create reusable ContentCard composable
   - Implement responsive grid layout
   - Match web implementation card design
   - File: `presentation/ui/common/ContentCard.kt`

#### Deliverables
- ✅ Smooth animations meeting Material 3 standards
- ✅ Full dark/light theme support
- ✅ Main content card layout components
- ✅ Visual parity with web design reference

---

### Phase 4: Accessibility & Responsiveness (Week 2-3)
**Duration:** 2 days  
**Owner:** Sokchea (UI Lead)  
**Status:** Not Started

#### Tasks
1. **Implement Accessibility Features**
   - Add `semantics` blocks for screen readers
   - Semantic labels for navigation items
   - Proper role definitions (Role.NavigationBar)
   - Keyboard navigation support (Tab to cycle items, Enter to select)
   - File: `presentation/ui/navigation/NavigationItem.kt`
   - Code example:
     ```kotlin
     NavigationItem(
         modifier = Modifier.semantics {
             contentDescription = label
             role = Role.Button
         }
     )
     ```

2. **Add Tooltips for Collapsed State**
   - Show label as tooltip when collapsed
   - Delay tooltip display (500ms)
   - Use Material 3 tooltip component
   - File: `presentation/ui/navigation/NavigationItem.kt`

3. **Handle Landscape/Tablet Layouts**
   - Landscape: Consider forcing expanded sidebar on tablets
   - Tablet (>=600dp): Always show expanded sidebar
   - Phone (< 600dp): Allow collapse/expand
   - File: Create `presentation/ui/navigation/AdaptiveCollapsibleSidebar.kt`

4. **Keyboard Navigation**
   - Tab through navigation items
   - Arrow keys to move between items (optional)
   - Enter/Space to activate
   - Escape to collapse sidebar (optional)

#### Deliverables
- ✅ WCAG 2.1 AA accessibility compliance
- ✅ Tablet and landscape layout support
- ✅ Full keyboard navigation support
- ✅ Accessibility audit report

---

### Phase 5: State Persistence & Advanced Features (Week 3)
**Duration:** 1-2 days  
**Owner:** Sokchea (UI Lead)  
**Status:** Not Started

#### Tasks
1. **Persist Sidebar State**
   - Save collapsed/expanded preference
   - Restore on app restart
   - Use DataStore or SharedPreferences
   - File: Update `SidebarNavigationViewModel.kt`

2. **Add Auto-Collapse on Navigation**
   - Collapse sidebar when navigating to new screen
   - Configurable behavior (on/off)
   - File: `presentation/ui/navigation/CollapsibleSidebarLayout.kt`

3. **Add Click Main Content to Collapse**
   - Collapse sidebar when user clicks/taps main content area
   - Only active on phones/tablets (< 600dp)
   - Tablets (≥600dp) keep sidebar permanently expanded
   - Use clickable modifier with scrim detection
   - File: `presentation/ui/navigation/CollapsibleSidebarLayout.kt`
   - Code example:
     ```kotlin
     Box(modifier = Modifier.fillMaxSize()) {
         if (isExpanded && !isTablet) {
             Box(
                 modifier = Modifier
                     .fillMaxSize()
                     .clickable { onCollapse() }
             )
         }
         content()
     }
     ```

4. **Add Badges & Notifications**
   - Support badge counts on navigation items (e.g., "3 new messages")
   - Badge display in collapsed state (circular indicator)
   - File: Update `NavigationRoute.kt` and `NavigationItem.kt`

5. **Add Search/Filter in Sidebar** (Optional)
   - Quick search for navigation items
   - Filter items based on query
   - File: `presentation/ui/navigation/SidebarSearch.kt`

#### Deliverables
- ✅ State persistence across app sessions
- ✅ Auto-collapse behavior working correctly
- ✅ Click main content to collapse (mobile/tablet)
- ✅ Badge support for navigation items

---

### Phase 6: Integration Testing & Refinement (Week 3-4)
**Duration:** 2-3 days  
**Owner:** Sokchea + Kai (QA)  
**Status:** Not Started

#### Tasks
1. **Unit Tests**
   - Test CollapsibleNavigationDrawer state changes
   - Test SidebarNavigationViewModel logic
   - Test navigation routing
   - File: `app/src/test/java/com/.../SidebarNavigationViewModelTest.kt`

2. **UI/Instrumentation Tests**
   - Test animations and transitions
   - Test touch interactions
   - Test keyboard navigation
   - File: `app/src/androidTest/java/com/.../CollapsibleSidebarLayoutTest.kt`

3. **Manual Testing**
   - Test on multiple devices (phones and tablets)
   - Test on different Android versions (API 29+)
   - Test theme switching (light/dark/dynamic)
   - Test landscape/portrait rotation

4. **Performance Testing**
   - Measure animation performance (should maintain 60fps)
   - Profile memory usage
   - Check battery impact

#### Deliverables
- ✅ Unit test coverage (>80%)
- ✅ UI tests for critical user journeys
- ✅ Manual testing report
- ✅ Performance metrics

---

### Phase 7: Documentation & Developer Handoff (Week 4)
**Duration:** 1-2 days  
**Owner:** Sokchea (UI Lead)  
**Status:** Not Started

#### Tasks
1. **Create Developer Documentation**
   - File: `docs/COLLAPSIBLE_SIDEBAR_USAGE_GUIDE.md`
   - How to add new navigation items
   - How to customize styling
   - How to handle edge cases

2. **Create Design Tokens Document**
   - File: `docs/SIDEBAR_DESIGN_TOKENS.md`
   - Animation timings
   - Sizing rules (collapsed/expanded widths)
   - Color specifications
   - Typography scale

3. **Update Architecture Decision Record**
   - File: `docs/adr/COLLAPSIBLE_SIDEBAR_ADR.md`
   - Decision rationale
   - Alternative considered
   - Consequences and trade-offs

4. **Create Figma Design File (Optional)**
   - Mirror Android implementation
   - Document component library
   - Create annotated design spec

#### Deliverables
- ✅ Complete developer documentation
- ✅ Design tokens specification
- ✅ ADR documentation
- ✅ Figma design file (optional)

---

## 🛠 Detailed Component Specifications

### 1. CollapsibleNavigationDrawer Component

```kotlin
@Composable
fun CollapsibleNavigationDrawer(
    isCollapsed: Boolean,
    onToggleCollapse: () -> Unit,
    modifier: Modifier = Modifier,
    collapsedWidth: Dp = 64.dp,
    expandedWidth: Dp = 280.dp,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable (BoxScope) -> Unit
) {
    // Implementation details
}
```

**Key Features:**
- Horizontal animation between collapsed and expanded states
- Smooth width transition (300ms, EaseInOutCubic)
- Material 3 compatible colors
- Scrim overlay when drawer is open (on mobile)
- Content padding and spacing

**Design Specifications:**
- Collapsed width: 64.dp (icon + 16.dp padding on each side)
- Expanded width: 280.dp (standard drawer width)
- Animation duration: 300ms
- Animation curve: EaseInOutCubic
- Elevation: 1.dp
- Border: 1.dp, colorScheme.outlineVariant

---

### 2. SidebarHeader Component

```kotlin
@Composable
fun SidebarHeader(
    isCollapsed: Boolean,
    modifier: Modifier = Modifier,
    showDateTime: Boolean = true,
    showAppBranding: Boolean = false
) {
    // Implementation details
}
```

**Key Features:**
- Shows date/time or app name/logo
- Responsive to collapsed state
- Accessible heading role
- Customizable content

**Design Specifications (Expanded State):**
- Padding: 24.dp (top/bottom), 16.dp (sides)
- Date display: 32sp, font weight 300 (light)
- Month/year display: 12sp, color = onSurfaceVariant
- Height: 120.dp (approx)

**Design Specifications (Collapsed State):**
- Padding: 16.dp
- Show only day number: 22sp
- Height: 80.dp (approx)

---

### 3. NavigationItem Component

```kotlin
@Composable
fun NavigationItem(
    label: String,
    icon: @Composable () -> Unit,
    isSelected: Boolean,
    isCollapsed: Boolean,
    onClick: () -> Unit,
    badge: Int? = null,
    modifier: Modifier = Modifier
) {
    // Implementation details
}
```

**Key Features:**
- Icon + label display in expanded state
- Icon-only with tooltip in collapsed state
- Active/inactive state styling
- Badge support for notifications
- Ripple effect on click
- Keyboard accessible

**Design Specifications:**
- Icon size: 24.dp
- Label text: 14sp, labelLarge style
- Height: 48.dp
- Padding (expanded): 16.dp (horizontal), 12.dp (vertical)
- Padding (collapsed): 8.dp
- Active background: colorScheme.secondaryContainer
- Active text color: colorScheme.onSecondaryContainer
- Inactive text color: colorScheme.onSurface (60% opacity)
- Hover background: colorScheme.onSurface (8% opacity)

---

### 4. CollapsibleSidebarLayout Component

```kotlin
@Composable
fun CollapsibleSidebarLayout(
    sidebarContent: @Composable (BoxScope, Boolean) -> Unit,
    content: @Composable (BoxScope) -> Unit,
    modifier: Modifier = Modifier,
    collapsedWidth: Dp = 64.dp,
    expandedWidth: Dp = 280.dp,
    showScrim: Boolean = true,
    enableClickToCollapse: Boolean = true // Collapse when clicking main content
) {
    // Implementation details
}
```

**Key Features:**
- Main layout combining sidebar + content area
- Responsive to screen size and orientation
- Scrim overlay for mobile/tablet
- Smooth layout transition

**Design Specifications:**
- Sidebar fixed on left side
- Content takes remaining space
- Scrim color: black, 32% opacity
- Scrim appears on mobile/tablet, not on landscape tablets

---

## 📊 Navigation Items Structure

The sidebar will display the following navigation items based on the current app structure:

```kotlin
val navigationItems = listOf(
    NavigationRoute(
        id = "home",
        label = "Home",
        icon = { Icon(Icons.Rounded.Home, contentDescription = null) },
        route = Route.Home.route,
        badge = null
    ),
    NavigationRoute(
        id = "fileSelection",
        label = "Files",
        icon = { Icon(Icons.Rounded.FolderOpen, contentDescription = null) },
        route = Route.FileSelection.route,
        badge = null
    ),
    NavigationRoute(
        id = "renameConfig",
        label = "Rename",
        icon = { Icon(Icons.Rounded.Edit, contentDescription = null) },
        route = Route.RenameConfig.route,
        badge = null
    ),
    NavigationRoute(
        id = "preview",
        label = "Preview",
        icon = { Icon(Icons.Rounded.Visibility, contentDescription = null) },
        route = Route.Preview.route,
        badge = null
    ),
    NavigationRoute(
        id = "template",
        label = "Templates",
        icon = { Icon(Icons.Rounded.Description, contentDescription = null) },
        route = Route.Template.route,
        badge = null
    ),
    NavigationRoute(
        id = "monitoring",
        label = "Monitoring",
        icon = { Icon(Icons.Rounded.Monitor, contentDescription = null) },
        route = Route.Monitoring.route,
        badge = null
    ),
    NavigationRoute(
        id = "tags",
        label = "Tags",
        icon = { Icon(Icons.Rounded.Label, contentDescription = null) },
        route = Route.TagManagement.route,
        badge = null
    ),
    NavigationRoute(
        id = "history",
        label = "History",
        icon = { Icon(Icons.Rounded.History, contentDescription = null) },
        route = Route.History.route,
        badge = null
    ),
    NavigationRoute(
        id = "settings",
        label = "Settings",
        icon = { Icon(Icons.Rounded.Settings, contentDescription = null) },
        route = Route.Settings.route,
        badge = null
    ),
    NavigationRoute(
        id = "about",
        label = "About",
        icon = { Icon(Icons.Rounded.Info, contentDescription = null) },
        route = Route.About.route,
        badge = null
    )
)
```

---

## 🎨 Main Content Layout Pattern

The main content area uses a responsive card grid layout matching the web implementation:

### Layout Structure
```kotlin
@Composable
fun MainContentArea(
    title: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        // Page Title
        Text(
            text = title,
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // Responsive Card Grid
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 280.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(6) { index ->
                ContentCard(
                    title = "Item ${index + 1}",
                    description = "Content preview for this item goes here"
                )
            }
        }
        
        // About Section
        Spacer(modifier = Modifier.height(32.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "About This View",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "This is the main content area that automatically expands when " +
                           "the sidebar collapses. Use the navigation items to switch between " +
                           "different sections.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ContentCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Icon placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
```

### Responsive Grid Breakpoints
- **Phone (< 600dp)**: 1 column
- **Tablet (600-839dp)**: 2 columns
- **Large Tablet/Desktop (≥840dp)**: 3 columns

### Card Design Specifications
- Card min-width: 280.dp
- Card padding: 24.dp
- Gap between cards: 24.dp
- Card border: 1.dp, outlineVariant color
- Card hover: Elevation increase (optional)
- Icon placeholder: 48.dp square with 12.dp corner radius

---

## 🔄 Navigation Flow Diagram

```
┌─────────────────────────────────────────────────────┐
│                   MainActivity                      │
│         enableEdgeToEdge()                          │
│         setContent { ... }                          │
└─────────────┬───────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────────────┐
│              ConversionTheme                        │
│      (Light/Dark/DynamicColor support)              │
└─────────────┬───────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────────────┐
│        CollapsibleSidebarLayout                     │
│  ┌────────────────────────────────────────────────┐ │
│  │  CollapsibleNavigationDrawer                   │ │
│  │  ┌──────────────────────────────────────────┐ │ │
│  │  │ SidebarHeader                            │ │ │
│  │  │ ┌────────────┐                           │ │ │
│  │  │ │  29        │ (collapsed: just 29)      │ │ │
│  │  │ │ January    │ (hidden when collapsed)   │ │ │
│  │  │ │ 2026       │                           │ │ │
│  │  │ └────────────┘                           │ │ │
│  │  │                                          │ │ │
│  │  │ ┌──────────────────────────────────────┐ │ │
│  │  │ │ NavigationItems                      │ │ │
│  │  │ │ • [🏠] Home                          │ │ │
│  │  │ │ • [📁] Files                         │ │ │
│  │  │ │ • [✏️] Rename                        │ │ │
│  │  │ │ • [👁️] Preview                      │ │ │
│  │  │ │ • [📋] Templates                    │ │ │
│  │  │ │ • [📡] Monitoring                   │ │ │
│  │  │ │ • [🏷️] Tags                         │ │ │
│  │  │ │ • [⏱️] History                      │ │ │
│  │  │ │ • [⚙️] Settings                     │ │ │
│  │  │ │ • [ℹ️] About                        │ │ │
│  │  │ └──────────────────────────────────────┘ │ │
│  │  │                                          │ │
│  │  │ ┌──────────────────────────────────────┐ │ │
│  │  │ │ Toggle Button                        │ │ │
│  │  │ │ < Collapse (expanded)                │ │ │
│  │  │ │ > (collapsed)                        │ │ │
│  │  │ └──────────────────────────────────────┘ │ │
│  │  └──────────────────────────────────────────┘ │
│  │                                                │
│  │  ┌──────────────────────────────────────────┐ │
│  │  │         ConversionNavHost                │ │
│  │  │  (Displays current screen based on       │ │
│  │  │   selected navigation item)              │ │
│  │  └──────────────────────────────────────────┘ │
│  └────────────────────────────────────────────────┘
└─────────────────────────────────────────────────────┘
```

---

## 🎨 Styling & Theme Integration

### Material 3 Color Tokens

The sidebar will use the following Material 3 color tokens:

| Component | Light Theme | Dark Theme | Reference |
|-----------|-------------|-----------|-----------|
| **Sidebar Background** | `surface` | `surface` | Primary container |
| **Sidebar Border** | `outlineVariant` | `outlineVariant` | Subtle divider |
| **Navigation Item (Active)** | `secondaryContainer` | `secondaryContainer` | Highlighted state |
| **Navigation Item (Active Text)** | `onSecondaryContainer` | `onSecondaryContainer` | Text on highlighted |
| **Navigation Item (Inactive)** | `onSurface` (60%) | `onSurface` (60%) | Default text |
| **Sidebar Header Text** | `onSurface` | `onSurface` | Primary text |
| **Sidebar Header Subtext** | `onSurfaceVariant` | `onSurfaceVariant` | Secondary text |

### Animation Specifications

| Animation | Duration | Easing Function | Use Case |
|-----------|----------|-----------------|----------|
| **Sidebar Width** | 300ms | EaseInOutCubic | Expand/collapse drawer |
| **Label Opacity** | 300ms | EaseInOutQuad | Fade labels in/out |
| **Icon Scale** | 300ms | EaseInOutQuad | Icon size transitions |
| **Background Color** | 200ms | EaseInOutQuad | Active item highlight |
| **Ripple** | 400ms | EaseOut | Touch feedback |

---

## 📱 Responsive Breakpoints

| Device Type | Width | Behavior |
|-------------|-------|----------|
| **Phone (< 600dp)** | < 600dp | Permanent drawer: Allow collapse/expand. Click main content to collapse. |
| **Tablet (600-839dp)** | 600-839dp | Permanent drawer: Always expanded, no collapse. |
| **Large Tablet (840+dp)** | 840+dp | Permanent drawer: Always expanded, no collapse. |
| **Landscape Phone** | Any height < 600dp | Permanent drawer: Allow collapse/expand with reduced header. |

---

## 🔐 Accessibility Features

### Screen Reader Support
- Navigation drawer has `role = Role.NavigationBar`
- Each item has semantic `contentDescription`
- Active item announced as "selected"
- Tooltip labels for collapsed state items

### Keyboard Navigation
- Tab key: Move between items
- Arrow keys (optional): Cycle through items
- Enter/Space: Activate selected item
- Escape: Collapse sidebar (optional)

### Color Contrast
- All text meets WCAG AAA standards (7:1 ratio minimum)
- Active indicators use color + shape differentiation
- No color-only information conveyance

### Text Size & Spacing
- Minimum touch target: 48dp x 48dp
- Labels: 14sp (minimum 12sp at 120% scaling)
- Line height: 1.5em for better readability

---

## 📚 File Structure & Dependencies

### New Files to Create
```
app/src/main/java/com/example/conversion/
├── presentation/
│   ├── ui/
│   │   ├── navigation/
│   │   │   ├── CollapsibleNavigationDrawer.kt (NEW)
│   │   │   ├── SidebarHeader.kt (NEW)
│   │   │   ├── NavigationItem.kt (NEW)
│   │   │   ├── CollapsibleSidebarLayout.kt (NEW)
│   │   │   └── AdaptiveCollapsibleSidebar.kt (NEW)
│   │   ├── common/
│   │   │   ├── ContentCard.kt (NEW)
│   │   │   └── MainContentArea.kt (NEW)
│   │   └── theme/
│   │       └── ConversionTheme.kt (MODIFY)
│   └── viewmodel/
│       └── SidebarNavigationViewModel.kt (NEW)
├── navigation/
│   ├── ConversionNavHost.kt (MODIFY)
│   ├── Route.kt (MODIFY)
│   └── NavigationRoute.kt (NEW)
└── MainActivity.kt (MODIFY)

app/src/test/java/com/example/conversion/
├── presentation/
│   ├── ui/
│   │   └── navigation/
│   │       ├── CollapsibleNavigationDrawerTest.kt (NEW)
│   │       ├── SidebarHeaderTest.kt (NEW)
│   │       └── NavigationItemTest.kt (NEW)
│   └── viewmodel/
│       └── SidebarNavigationViewModelTest.kt (NEW)

app/src/androidTest/java/com/example/conversion/
├── presentation/
│   ├── ui/
│   │   └── navigation/
│   │       ├── CollapsibleSidebarLayoutTest.kt (NEW)
│   │       └── SidebarNavigationIntegrationTest.kt (NEW)

docs/
├── COLLAPSIBLE_SIDEBAR_USAGE_GUIDE.md (NEW)
├── SIDEBAR_DESIGN_TOKENS.md (NEW)
└── adr/
    └── 005-collapsible-sidebar-navigation.md (NEW)
```

### Dependencies Required
- ✅ Jetpack Compose (already present)
- ✅ Material 3 (already present)
- ✅ Navigation Compose (already present)
- ✅ Material Icons Extended (already present)
- ✅ Hilt (already present)
- ✅ ViewModel (already present)
- ✅ DataStore or SharedPreferences (already present)

**No new external dependencies required** ✅

---

## 🧪 Testing Strategy

### Unit Tests
- Test sidebar state transitions
- Test navigation route mapping
- Test collapse/expand logic
- Test state persistence

### UI Tests (Compose)
- Test animations and transitions
- Test touch interactions
- Test navigation item clicks
- Test keyboard navigation

### Manual Testing Checklist
- [ ] Sidebar expands/collapses smoothly
- [ ] Navigation items highlight correctly
- [ ] Active screen matches selected item
- [ ] Animations run at 60fps
- [ ] Works on phones (API 29, 30, 31, 32, 33, 34)
- [ ] Works on tablets in portrait and landscape
- [ ] Dark theme colors are correct
- [ ] Light theme colors are correct
- [ ] Dynamic colors work correctly
- [ ] Screen reader announces items correctly
- [ ] Keyboard navigation works end-to-end
- [ ] Back button behavior is correct
- [ ] State persists after app restart
- [ ] Auto-collapse works on navigation
- [ ] No layout jank or stuttering

---

## ⚠️ Risks & Mitigation

| Risk | Impact | Probability | Mitigation |
|------|--------|-------------|-----------|
| **Animation performance issues** | Janky UI, poor UX | Medium | Test on low-end devices, optimize with `currentRecompositionScope` |
| **Navigation routing conflicts** | App crashes, broken UX | Low | Thorough testing, design review with Kai |
| **Accessibility gaps** | Non-compliant, excludes users | Medium | Follow Material 3 guidelines, WCAG audit |
| **State persistence bugs** | User preferences lost | Low | Comprehensive state management testing |
| **Tablet layout issues** | Broken on large screens | Medium | Test on range of device sizes, use adaptive layouts |
| **Theme switching delays** | Perceived lag | Low | Optimize color composition, cache theme data |

---

## 📅 Timeline & Milestones

| Phase | Duration | Target Date | Status |
|-------|----------|------------|--------|
| Phase 1: Core Components | 2-3 days | Jan 24-25 | Not Started |
| Phase 2: Navigation Integration | 2-3 days | Jan 25-27 | Not Started |
| Phase 3: Theming & Polish | 2 days | Jan 27-28 | Not Started |
| Phase 4: Accessibility | 2 days | Jan 28-29 | Not Started |
| Phase 5: Advanced Features | 1-2 days | Jan 29-30 | Not Started |
| Phase 6: Testing & Refinement | 2-3 days | Jan 30-Feb 1 | Not Started |
| Phase 7: Documentation | 1-2 days | Feb 1-2 | Not Started |
| **Total Estimated Duration** | **12-16 days** | **Feb 2, 2026** | - |

---

## 🎓 Code Examples & Snippets

### Example 1: Basic CollapsibleSidebarLayout Usage

```kotlin
@Composable
fun MainApp() {
    val sidebarViewModel: SidebarNavigationViewModel = hiltViewModel()
    val sidebarState by sidebarViewModel.state.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    
    CollapsibleSidebarLayout(
        sidebarContent = { scope, isCollapsed ->
            SidebarContent(
                isCollapsed = isCollapsed,
                activeRoute = sidebarState.activeRouteId,
                navigationItems = navigationItems,
                onNavigationItemClick = { routeId, route ->
                    sidebarViewModel.setActiveRoute(routeId)
                    navController.navigate(route)
                },
                onToggleSidebar = { sidebarViewModel.toggleSidebar() }
            )
        },
        content = { scope ->
            ConversionNavHost(
                navController = navController,
                modifier = Modifier.fillMaxSize(),
                onNavigateToSettings = {
                    navController.navigate(Route.Settings)
                }
            )
        }
    )
}
```

### Example 2: Creating a Custom Navigation Item

```kotlin
@Composable
fun SidebarContent(
    isCollapsed: Boolean,
    activeRoute: String?,
    navigationItems: List<NavigationRoute>,
    onNavigationItemClick: (routeId: String, route: String) -> Unit,
    onToggleSidebar: () -> Unit,
    modifier: Modifier = Modifier
) {
    CollapsibleNavigationDrawer(
        isCollapsed = isCollapsed,
        onToggleCollapse = onToggleSidebar,
        modifier = modifier.fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            SidebarHeader(isCollapsed = isCollapsed)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            navigationItems.forEach { item ->
                NavigationItem(
                    label = item.label,
                    icon = item.icon,
                    isSelected = activeRoute == item.id,
                    isCollapsed = isCollapsed,
                    onClick = {
                        onNavigationItemClick(item.id, item.route)
                    }
                )
            }
        }
    }
}
```

### Example 3: SidebarNavigationViewModel

```kotlin
@HiltViewModel
class SidebarNavigationViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(
        SidebarNavigationState(isCollapsed = false)
    )
    val state: StateFlow<SidebarNavigationState> = _state.asStateFlow()
    
    init {
        viewModelScope.launch {
            userPreferencesRepository.sidebarCollapsed.collect { isCollapsed ->
                _state.update { it.copy(isCollapsed = isCollapsed) }
            }
        }
    }
    
    fun toggleSidebar() {
        viewModelScope.launch {
            val newState = _state.value.isCollapsed.not()
            userPreferencesRepository.setSidebarCollapsed(newState)
        }
    }
    
    fun setActiveRoute(routeId: String) {
        _state.update { it.copy(activeRouteId = routeId) }
    }
    
    fun collapseSidebarOnNavigation(routeId: String) {
        viewModelScope.launch {
            if (_state.value.activeRouteId != routeId) {
                userPreferencesRepository.setSidebarCollapsed(true)
            }
        }
    }
}
```

---

## 📝 Success Criteria

The implementation will be considered complete when:

- ✅ All 4 core navigation components are implemented and tested
- ✅ Main content layout components match web design
- ✅ Sidebar seamlessly integrates with existing navigation
- ✅ Click main content to collapse works on phones/tablets
- ✅ Permanent drawer behavior (never hidden, only collapsed)
- ✅ Animations run at 60fps on devices with API 29+
- ✅ Dark and light themes are correctly applied
- ✅ Accessibility audit passes WCAG 2.1 AA standards
- ✅ All unit and UI tests pass (>80% coverage)
- ✅ Manual testing completed on 3+ device types (phones, tablets, landscape)
- ✅ Documentation is complete and reviewed
- ✅ Zero crashes or regressions in existing functionality
- ✅ Performance metrics meet targets (initial load <500ms)
- ✅ Visual parity with web implementation confirmed

---

## 🤝 Collaboration Notes

### Sokchea (UI/Frontend Lead)
- Owns implementation of all UI components
- Responsible for visual design and animations
- Handles accessibility and responsive design
- Manages design tokens and theme integration

### Kai (Backend/Architecture Lead)
- Reviews architecture decisions
- Ensures alignment with Clean Architecture
- Conducts code reviews
- Provides input on testing strategy

### QA Team
- Conducts manual testing across devices
- Reports accessibility issues
- Validates performance metrics
- Signs off on release readiness

---

## 📞 Contact & Support

For questions or issues related to this roadmap:
- **UI/Design Questions:** Contact Sokchea
- **Architecture Questions:** Contact Kai
- **Testing/QA Questions:** Contact QA Team

---

## 📦 Appendix: Reference Materials

### Related Documents
- **[UI Navigation System Overhaul](./UI_NAVIGATION_SYSTEM_OVERHAUL.md)** ⭐ - Master navigation architecture and screen inventory (reference for context)
- [UI Guidelines](../../UI_GUIDELINES.md)
- [ADR 001: Clean Architecture](../../adr/001-clean-architecture.md)
- [ADR 002: MVI Pattern](../../adr/002-mvi-pattern.md)

### External Resources
- [Material 3 Navigation Drawer](https://m3.material.io/components/navigation-drawer)
- [Material 3 Design System](https://m3.material.io)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Compose Navigation Guide](https://developer.android.com/guide/navigation/navigation-compose)
- [WCAG 2.1 Accessibility Guidelines](https://www.w3.org/WAI/WCAG21/quickref/)

### Design Reference
- **Figma Design:** [Collapsible Sidebar Navigation](https://www.figma.com/design/UzuB0Gy9KNtDD3TzrVOcQV/Collapsible-Sidebar-Navigation)
- **React Implementation:** `/Collapsible Sidebar Navigation/src/app/components/CollapsibleSidebar.tsx`

---

**Document Version History**

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | Jan 22, 2026 | Sokchea | Initial comprehensive roadmap |

---

**Next Steps:**
1. Review and approve this roadmap
2. Schedule kickoff meeting with team
3. Create JIRA tickets for each phase
4. Set up design file in Figma
5. Begin Phase 1 implementation

