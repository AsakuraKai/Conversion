# Collapsible Sidebar - Developer Usage Guide

**Version:** 1.0  
**Last Updated:** January 22, 2026  
**Author:** Sokchea (UI Lead)

---

## Table of Contents

1. [Overview](#overview)
2. [Quick Start](#quick-start)
3. [Component Architecture](#component-architecture)
4. [Core Components](#core-components)
5. [Integration Guide](#integration-guide)
6. [Customization](#customization)
7. [State Management](#state-management)
8. [Accessibility](#accessibility)
9. [Best Practices](#best-practices)
10. [Troubleshooting](#troubleshooting)
11. [API Reference](#api-reference)

---

## Overview

The Collapsible Sidebar Navigation is a Material 3-compliant navigation component that provides:

- **Adaptive Layout**: Automatically adjusts for phones, tablets, portrait, and landscape orientations
- **Permanent Drawer**: Never hidden, only collapsed/expanded
- **State Persistence**: Remembers user preference across sessions
- **Accessibility**: Full WCAG 2.1 AA compliance with keyboard and screen reader support
- **Performance**: Smooth 60fps animations on API 29+ devices
- **Badge Support**: Display notification counts on navigation items

### Key Features

✅ Click main content to collapse (phones/tablets)  
✅ Auto-collapse on navigation (configurable)  
✅ Tooltip support in collapsed state  
✅ Material 3 theming (light/dark/dynamic colors)  
✅ Responsive breakpoints  
✅ State persistence via DataStore  

---

## Quick Start

### Basic Usage

Add the `CollapsibleSidebarLayout` to your main screen:

```kotlin
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController()
) {
    val viewModel: SidebarNavigationViewModel = hiltViewModel()
    val sidebarState by viewModel.state.collectAsStateWithLifecycle()

    CollapsibleSidebarLayout(
        isCollapsed = sidebarState.isCollapsed,
        onToggleCollapse = { viewModel.toggleCollapse() },
        navigationItems = sidebarState.navigationItems,
        selectedRoute = sidebarState.selectedRoute,
        onNavigate = { route -> 
            viewModel.onNavigationItemClick(route)
            navController.navigate(route)
        }
    ) {
        // Your main content here
        ConversionNavHost(navController = navController)
    }
}
```

### ViewModel Setup

Inject the `SidebarNavigationViewModel` in your activity:

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val sidebarViewModel: SidebarNavigationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConversionTheme {
                MainScreen()
            }
        }
    }
}
```

---

## Component Architecture

### Architecture Diagram

```
┌─────────────────────────────────────────────────────────┐
│                 CollapsibleSidebarLayout                │
│  ┌───────────────────┐  ┌───────────────────────────┐  │
│  │ CollapsibleNav    │  │ Main Content Area         │  │
│  │ DrawerContent     │  │                           │  │
│  │                   │  │  ┌────────────────────┐   │  │
│  │ ┌───────────────┐ │  │  │ NavHost           │   │  │
│  │ │ SidebarHeader │ │  │  │                   │   │  │
│  │ └───────────────┘ │  │  │  ┌─────────────┐  │   │  │
│  │                   │  │  │  │ Screen A    │  │   │  │
│  │ ┌───────────────┐ │  │  │  └─────────────┘  │   │  │
│  │ │ NavigationItem│ │  │  │                   │   │  │
│  │ └───────────────┘ │  │  │  ┌─────────────┐  │   │  │
│  │ ┌───────────────┐ │  │  │  │ Screen B    │  │   │  │
│  │ │ NavigationItem│ │  │  │  └─────────────┘  │   │  │
│  │ └───────────────┘ │  │  └────────────────────┘   │  │
│  │       ...         │  │                           │  │
│  └───────────────────┘  └───────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
               SidebarNavigationViewModel
                          │
                          ▼
                  SidebarPreferences
                   (DataStore)
```

### File Structure

```
app/src/main/java/com/example/conversion/
├── presentation/
│   ├── ui/
│   │   ├── navigation/
│   │   │   ├── CollapsibleNavigationDrawer.kt      # Main drawer container
│   │   │   ├── CollapsibleSidebarLayout.kt         # Layout wrapper
│   │   │   ├── SidebarHeader.kt                    # Date/collapse button
│   │   │   ├── NavigationItem.kt                   # Individual nav item
│   │   │   ├── NavigationItemWithBadge.kt          # Nav item with badge
│   │   │   ├── AdaptiveCollapsibleSidebar.kt       # Responsive wrapper
│   │   │   ├── AutoCollapseNavigationHandler.kt    # Auto-collapse logic
│   │   │   └── SidebarPreferences.kt               # DataStore persistence
│   │   └── common/
│   │       ├── ContentCard.kt                      # Card component
│   │       └── MainContentArea.kt                  # Content grid layout
│   ├── viewmodel/
│   │   └── SidebarNavigationViewModel.kt           # State management
│   ├── model/
│   │   └── NavigationRoute.kt                      # Route definitions
│   └── util/
│       └── BadgeNotificationManager.kt             # Badge management
├── di/
│   └── SidebarModule.kt                            # Hilt DI module
```

---

## Core Components

### 1. CollapsibleNavigationDrawer

The main container component for the sidebar navigation.

**Parameters:**

```kotlin
@Composable
fun CollapsibleNavigationDrawer(
    modifier: Modifier = Modifier,
    isCollapsed: Boolean = false,
    navigationItems: List<NavigationItem> = emptyList(),
    selectedRoute: String? = null,
    onNavigate: (String) -> Unit = {},
    onToggleCollapse: () -> Unit = {},
    header: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {}
)
```

**Example:**

```kotlin
CollapsibleNavigationDrawer(
    isCollapsed = sidebarState.isCollapsed,
    navigationItems = sidebarState.navigationItems,
    selectedRoute = sidebarState.selectedRoute,
    onNavigate = { route -> handleNavigation(route) },
    onToggleCollapse = { viewModel.toggleCollapse() },
    header = {
        SidebarHeader(
            isCollapsed = sidebarState.isCollapsed,
            onToggleCollapse = { viewModel.toggleCollapse() }
        )
    }
)
```

### 2. SidebarHeader

Displays current date and collapse/expand toggle button.

**Parameters:**

```kotlin
@Composable
fun SidebarHeader(
    modifier: Modifier = Modifier,
    isCollapsed: Boolean = false,
    onToggleCollapse: () -> Unit = {}
)
```

**Features:**
- Shows full date when expanded (e.g., "Wednesday, January 22")
- Shows day number only when collapsed (e.g., "22")
- Animated transitions between states
- Accessibility support

### 3. NavigationItem

Individual navigation item with icon and label.

**Parameters:**

```kotlin
@Composable
fun NavigationItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    isSelected: Boolean = false,
    isCollapsed: Boolean = false,
    onClick: () -> Unit = {},
    badge: Int? = null
)
```

**Example:**

```kotlin
NavigationItem(
    icon = Icons.Outlined.Home,
    label = "Home",
    isSelected = selectedRoute == "home",
    isCollapsed = sidebarState.isCollapsed,
    onClick = { onNavigate("home") },
    badge = 3  // Shows notification count
)
```

### 4. CollapsibleSidebarLayout

Main layout wrapper that combines sidebar and content.

**Parameters:**

```kotlin
@Composable
fun CollapsibleSidebarLayout(
    modifier: Modifier = Modifier,
    isCollapsed: Boolean = false,
    onToggleCollapse: () -> Unit = {},
    navigationItems: List<NavigationItem> = emptyList(),
    selectedRoute: String? = null,
    onNavigate: (String) -> Unit = {},
    content: @Composable () -> Unit
)
```

**Features:**
- Automatic scrim overlay on phones/tablets when expanded
- Click content to collapse behavior
- Responsive layout based on device type

### 5. AdaptiveCollapsibleSidebar

Responsive wrapper that adjusts behavior based on device size.

**Parameters:**

```kotlin
@Composable
fun AdaptiveCollapsibleSidebar(
    modifier: Modifier = Modifier,
    isCollapsed: Boolean = false,
    onToggleCollapse: () -> Unit = {},
    navigationItems: List<NavigationItem> = emptyList(),
    selectedRoute: String? = null,
    onNavigate: (String) -> Unit = {},
    content: @Composable () -> Unit
)
```

**Device Behavior:**
- **Phone Portrait**: Collapsible, auto-collapse enabled
- **Phone Landscape**: Collapsible, auto-collapse enabled
- **Tablet Portrait**: Always expanded, collapse disabled
- **Tablet Landscape**: Always expanded, collapse disabled

---

## Integration Guide

### Step 1: Add ViewModel to Activity

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val sidebarViewModel: SidebarNavigationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConversionTheme {
                MainScreen(viewModel = sidebarViewModel)
            }
        }
    }
}
```

### Step 2: Set Up Navigation Routes

Define your navigation routes in `NavigationRoute.kt`:

```kotlin
data class NavigationRoute(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
    val badge: Int? = null
)

// Define your routes
val routes = listOf(
    NavigationRoute(
        route = "home",
        label = "Home",
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    ),
    NavigationRoute(
        route = "files",
        label = "Files",
        icon = Icons.Outlined.Folder,
        selectedIcon = Icons.Filled.Folder
    ),
    // ... more routes
)
```

### Step 3: Wire Navigation

```kotlin
@Composable
fun MainScreen(viewModel: SidebarNavigationViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val sidebarState by viewModel.state.collectAsStateWithLifecycle()

    // Auto-collapse handler
    AutoCollapseNavigationHandler(
        navController = navController,
        isAutoCollapseEnabled = sidebarState.isAutoCollapseEnabled,
        onAutoCollapse = { viewModel.toggleCollapse() }
    )

    AdaptiveCollapsibleSidebar(
        isCollapsed = sidebarState.isCollapsed,
        onToggleCollapse = { viewModel.toggleCollapse() },
        navigationItems = sidebarState.navigationItems,
        selectedRoute = sidebarState.selectedRoute,
        onNavigate = { route ->
            viewModel.onNavigationItemClick(route)
            navController.navigate(route)
        }
    ) {
        ConversionNavHost(navController = navController)
    }
}
```

### Step 4: Configure Auto-Collapse (Optional)

```kotlin
// Enable/disable auto-collapse
viewModel.setAutoCollapseEnabled(true)

// Auto-collapse is triggered automatically on navigation
// when enabled and on phone/tablet devices
```

---

## Customization

### Theme Customization

The sidebar uses Material 3 color tokens. Customize colors in your theme:

```kotlin
@Composable
fun ConversionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

**Sidebar-Specific Colors:**
- Background: `MaterialTheme.colorScheme.surface`
- Border: `MaterialTheme.colorScheme.outlineVariant`
- Selected item: `MaterialTheme.colorScheme.secondaryContainer`
- Text: `MaterialTheme.colorScheme.onSurface`

### Animation Customization

Modify animation durations in `CollapsibleNavigationDrawer.kt`:

```kotlin
// Default values
private const val ANIMATION_DURATION = 300
private const val EXPANDED_WIDTH = 240.dp
private const val COLLAPSED_WIDTH = 72.dp

// Customize by modifying these constants
val animatedWidth by animateDpAsState(
    targetValue = if (isCollapsed) COLLAPSED_WIDTH else EXPANDED_WIDTH,
    animationSpec = tween(
        durationMillis = ANIMATION_DURATION,
        easing = FastOutSlowInEasing
    ),
    label = "sidebar_width"
)
```

### Badge Customization

Configure badge appearance:

```kotlin
// Set badge for a specific route
viewModel.setBadge("home", 5)

// Clear badge
viewModel.clearBadge("home")

// Clear all badges
viewModel.clearAllBadges()

// Custom badge styling (modify NavigationItemWithBadge.kt)
Badge(
    modifier = Modifier.offset(x = 4.dp, y = (-4).dp),
    containerColor = MaterialTheme.colorScheme.error,
    contentColor = MaterialTheme.colorScheme.onError
) {
    Text(text = count.toString())
}
```

---

## State Management

### ViewModel State

The `SidebarNavigationViewModel` manages:

```kotlin
data class SidebarNavigationState(
    val isCollapsed: Boolean = false,
    val selectedRoute: String? = null,
    val navigationItems: List<NavigationItem> = emptyList(),
    val isAutoCollapseEnabled: Boolean = true,
    val badges: Map<String, Int> = emptyMap()
)
```

### State Persistence

Sidebar state is automatically persisted using DataStore:

```kotlin
// Preferences saved automatically:
// - isCollapsed: Boolean
// - selectedRoute: String?
// - isAutoCollapseEnabled: Boolean

// Access preferences directly (advanced usage)
@Inject lateinit var sidebarPreferences: SidebarPreferences

lifecycleScope.launch {
    sidebarPreferences.isCollapsed.collect { isCollapsed ->
        // React to state changes
    }
}
```

### Navigation Events

Handle navigation events in your ViewModel:

```kotlin
class YourViewModel @Inject constructor(
    private val sidebarViewModel: SidebarNavigationViewModel
) : ViewModel() {
    
    fun navigateToHome() {
        sidebarViewModel.onNavigationItemClick("home")
        // Your navigation logic
    }
}
```

---

## Accessibility

### Screen Reader Support

All components have comprehensive accessibility support:

```kotlin
// Navigation item with semantic properties
NavigationItem(
    icon = Icons.Outlined.Home,
    label = "Home",
    isSelected = true,
    onClick = { /* ... */ },
    modifier = Modifier.semantics {
        contentDescription = "Home navigation item, selected"
        role = Role.Button
        stateDescription = "Selected"
    }
)
```

### Keyboard Navigation

Full keyboard support is implemented:

- **Tab**: Navigate between items
- **Enter/Space**: Select item
- **Escape**: Collapse sidebar (when expanded)

### Testing with TalkBack

Enable TalkBack and verify:

1. All navigation items are announced correctly
2. Selected state is communicated
3. Collapse/expand button announces state changes
4. Badge counts are announced
5. Tooltips are read in collapsed state

### Accessibility Best Practices

✅ Use semantic properties on all interactive elements  
✅ Provide content descriptions for icons  
✅ Announce state changes (selected, collapsed, etc.)  
✅ Ensure touch targets are at least 48dp  
✅ Support keyboard navigation  
✅ Test with TalkBack enabled  
✅ Support different font sizes  

---

## Best Practices

### Performance Optimization

1. **Use remember** for expensive computations:

```kotlin
val formattedDate = remember(isCollapsed) {
    if (isCollapsed) SimpleDateFormat("dd", Locale.getDefault()).format(Date())
    else SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault()).format(Date())
}
```

2. **Avoid unnecessary recompositions**:

```kotlin
// Use stable parameters
@Stable
data class NavigationItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)
```

3. **Lazy load navigation items**:

```kotlin
LazyColumn {
    items(
        items = navigationItems,
        key = { item -> item.route }  // Use stable key
    ) { item ->
        NavigationItem(/* ... */)
    }
}
```

### State Management

1. **Single source of truth**: Use ViewModel for all sidebar state
2. **Immutable state**: Use data classes with copy()
3. **Reactive updates**: Use StateFlow for state propagation

```kotlin
// Good ✅
_state.update { it.copy(isCollapsed = !it.isCollapsed) }

// Bad ❌
_state.value.isCollapsed = !_state.value.isCollapsed
```

### Navigation

1. **Use navigation graph**: Define all routes in NavGraph
2. **Handle back stack**: Ensure proper back navigation
3. **Deep linking**: Support deep links for notifications

```kotlin
NavHost(navController, startDestination = "home") {
    composable("home") { HomeScreen() }
    composable("files") { FilesScreen() }
    // Define deep links
    composable(
        route = "details/{itemId}",
        deepLinks = listOf(navDeepLink { 
            uriPattern = "conversion://details/{itemId}" 
        })
    ) { /* ... */ }
}
```

---

## Troubleshooting

### Issue: Sidebar Not Animating Smoothly

**Symptoms:** Choppy animations, frame drops  
**Solution:**

1. Check device API level (requires 29+)
2. Reduce animation complexity
3. Use hardware layer during animation:

```kotlin
val animatedWidth by animateDpAsState(/* ... */)
Surface(
    modifier = Modifier
        .width(animatedWidth)
        .graphicsLayer { /* Force hardware acceleration */ }
) { /* ... */ }
```

### Issue: State Not Persisting

**Symptoms:** Sidebar resets to default state on app restart  
**Solution:**

1. Verify DataStore initialization in `SidebarModule.kt`
2. Check coroutine scope in ViewModel
3. Ensure preferences are collected properly:

```kotlin
init {
    viewModelScope.launch {
        sidebarPreferences.isCollapsed.collect { collapsed ->
            _state.update { it.copy(isCollapsed = collapsed) }
        }
    }
}
```

### Issue: Navigation Items Not Updating

**Symptoms:** Selected route doesn't highlight correctly  
**Solution:**

1. Verify route matching logic
2. Check if route is set in ViewModel:

```kotlin
fun onNavigationItemClick(route: String) {
    viewModelScope.launch {
        sidebarPreferences.setSelectedRoute(route)
        _state.update { it.copy(selectedRoute = route) }
    }
}
```

### Issue: Auto-Collapse Not Working

**Symptoms:** Sidebar doesn't collapse after navigation  
**Solution:**

1. Check if auto-collapse is enabled
2. Verify `AutoCollapseNavigationHandler` is composed
3. Ensure device type is phone/tablet (not large tablet):

```kotlin
val windowSizeClass = calculateWindowSizeClass(this)
val isTablet = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium
// Auto-collapse disabled on tablets
```

### Issue: Badge Not Showing

**Symptoms:** Badge count not visible on navigation item  
**Solution:**

1. Verify badge is set in state:

```kotlin
viewModel.setBadge("route", 5)
```

2. Check if using `NavigationItemWithBadge` composable
3. Ensure badge value is greater than 0

### Issue: Accessibility Announcements Missing

**Symptoms:** TalkBack not announcing elements correctly  
**Solution:**

1. Add semantic properties:

```kotlin
Modifier.semantics {
    contentDescription = "Descriptive text"
    role = Role.Button
}
```

2. Test with TalkBack enabled
3. Check content description is not empty

### Issue: Click Content to Collapse Not Working

**Symptoms:** Clicking main content doesn't collapse sidebar  
**Solution:**

1. Verify `CollapsibleSidebarLayout` is used (not `CollapsibleNavigationDrawer` alone)
2. Check scrim is clickable:

```kotlin
if (isExpanded && !isTablet) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onToggleCollapse() }
    )
}
```

---

## API Reference

### CollapsibleNavigationDrawer

```kotlin
@Composable
fun CollapsibleNavigationDrawer(
    modifier: Modifier = Modifier,
    isCollapsed: Boolean = false,
    navigationItems: List<NavigationItem> = emptyList(),
    selectedRoute: String? = null,
    onNavigate: (String) -> Unit = {},
    onToggleCollapse: () -> Unit = {},
    header: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {}
)
```

### SidebarHeader

```kotlin
@Composable
fun SidebarHeader(
    modifier: Modifier = Modifier,
    isCollapsed: Boolean = false,
    onToggleCollapse: () -> Unit = {}
)
```

### NavigationItem

```kotlin
@Composable
fun NavigationItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    isSelected: Boolean = false,
    isCollapsed: Boolean = false,
    onClick: () -> Unit = {},
    badge: Int? = null
)
```

### NavigationItemWithBadge

```kotlin
@Composable
fun NavigationItemWithBadge(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    isSelected: Boolean = false,
    isCollapsed: Boolean = false,
    onClick: () -> Unit = {},
    badgeCount: Int? = null
)
```

### CollapsibleSidebarLayout

```kotlin
@Composable
fun CollapsibleSidebarLayout(
    modifier: Modifier = Modifier,
    isCollapsed: Boolean = false,
    onToggleCollapse: () -> Unit = {},
    navigationItems: List<NavigationItem> = emptyList(),
    selectedRoute: String? = null,
    onNavigate: (String) -> Unit = {},
    content: @Composable () -> Unit
)
```

### AdaptiveCollapsibleSidebar

```kotlin
@Composable
fun AdaptiveCollapsibleSidebar(
    modifier: Modifier = Modifier,
    isCollapsed: Boolean = false,
    onToggleCollapse: () -> Unit = {},
    navigationItems: List<NavigationItem> = emptyList(),
    selectedRoute: String? = null,
    onNavigate: (String) -> Unit = {},
    content: @Composable () -> Unit
)
```

### SidebarNavigationViewModel

```kotlin
class SidebarNavigationViewModel @Inject constructor(
    private val sidebarPreferences: SidebarPreferences
) : ViewModel() {
    
    val state: StateFlow<SidebarNavigationState>
    
    fun toggleCollapse()
    fun onNavigationItemClick(route: String)
    fun setAutoCollapseEnabled(enabled: Boolean)
    fun setBadge(route: String, count: Int)
    fun clearBadge(route: String)
    fun clearAllBadges()
}
```

### SidebarPreferences

```kotlin
interface SidebarPreferences {
    val isCollapsed: Flow<Boolean>
    val selectedRoute: Flow<String?>
    val isAutoCollapseEnabled: Flow<Boolean>
    
    suspend fun setCollapsed(collapsed: Boolean)
    suspend fun setSelectedRoute(route: String?)
    suspend fun setAutoCollapseEnabled(enabled: Boolean)
}
```

### BadgeNotificationManager

```kotlin
class BadgeNotificationManager @Inject constructor() {
    
    fun setBadge(route: String, count: Int)
    fun incrementBadge(route: String, increment: Int = 1)
    fun decrementBadge(route: String, decrement: Int = 1)
    fun clearBadge(route: String)
    fun clearAllBadges()
    fun getBadge(route: String): Int?
    fun getAllBadges(): Map<String, Int>
}
```

---

## Additional Resources

- **Material 3 Guidelines**: [Navigation Drawer](https://m3.material.io/components/navigation-drawer)
- **Jetpack Compose**: [Navigation Documentation](https://developer.android.com/jetpack/compose/navigation)
- **Accessibility**: [WCAG 2.1 AA Guidelines](https://www.w3.org/WAI/WCAG21/quickref/)
- **Architecture**: [ADR 005 - Collapsible Sidebar Navigation](../docs/adr/005-collapsible-sidebar-navigation.md)
- **Design Tokens**: [Sidebar Design Tokens](./SIDEBAR_DESIGN_TOKENS.md)

---

## Support & Contact

For issues, questions, or contributions:

- **UI Lead**: Sokchea
- **Architecture Review**: Kai
- **GitHub Issues**: [Create an issue](https://github.com/your-repo/issues)

---

**Document Version:** 1.0  
**Last Reviewed:** January 22, 2026
