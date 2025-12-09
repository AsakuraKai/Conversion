# UI Guidelines - Auto Rename File Service

**Version:** 1.0  
**Last Updated:** December 9, 2025  
**Owner:** Sokchea (Frontend/UI Specialist)

---

## 📐 Design System Overview

This document outlines the UI/UX standards, component usage patterns, and design guidelines for the Auto Rename File Service application. All UI components follow Material 3 design principles and accessibility best practices.

---

## 🎨 Theme & Colors

### Color System

**Material 3 Dynamic Colors**
- Primary: System-generated or custom from image palette
- Secondary: Complementary accent colors
- Tertiary: Additional accent for variety
- Error: Red tones for error states
- Surface: Background variations for hierarchy

**Theme Modes**
- **Light Mode**: Default for daytime usage
- **Dark Mode**: OLED-friendly for low-light environments
- **System**: Follows device preference (recommended)

**Custom Palette Generation**
- Extract colors from selected images using ImagePalette
- Apply harmonious color schemes based on dominant colors
- Preserve accessibility contrast ratios

### Using Themes in Composables

```kotlin
@Composable
fun MyComponent() {
    // Access theme colors
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background
    
    // Access typography
    val titleStyle = MaterialTheme.typography.titleLarge
    
    // Access shapes
    val cardShape = MaterialTheme.shapes.medium
}
```

---

## 📝 Typography

### Type Scale Hierarchy

**Display Styles** (Large, prominent text)
- `displayLarge`: 57sp - Hero headlines
- `displayMedium`: 45sp - Large marketing content
- `displaySmall`: 36sp - Section headers

**Headline Styles** (Major sections)
- `headlineLarge`: 32sp - Main screen titles
- `headlineMedium`: 28sp - Card titles
- `headlineSmall`: 24sp - Dialog titles

**Title Styles** (Subsections)
- `titleLarge`: 22sp - List headers, prominent labels
- `titleMedium`: 16sp - Card subtitles
- `titleSmall`: 14sp - Dense information

**Body Styles** (Content text)
- `bodyLarge`: 16sp - Primary content
- `bodyMedium`: 14sp - Secondary content
- `bodySmall`: 12sp - Captions, metadata

**Label Styles** (Buttons, tabs)
- `labelLarge`: 14sp - Button text, tabs
- `labelMedium`: 12sp - Input labels
- `labelSmall`: 11sp - Tiny labels

### Font Families

- **Default**: System font (Roboto on Android)
- **Monospace**: Code, file paths, technical info

### Usage Guidelines

```kotlin
// Screen title
Text(
    text = "File Selection",
    style = MaterialTheme.typography.headlineLarge
)

// Card header
Text(
    text = "Rename Configuration",
    style = MaterialTheme.typography.titleLarge
)

// Body content
Text(
    text = "Select files to rename...",
    style = MaterialTheme.typography.bodyMedium
)

// Button label
Button(onClick = {}) {
    Text("Continue", style = MaterialTheme.typography.labelLarge)
}
```

---

## 📏 Spacing & Layout

### Spacing Scale

**Base Unit**: 4dp (follows Material Design 4dp grid)

| Token | Value | Usage |
|-------|-------|-------|
| `space_xs` | 4dp | Minimal gaps, icon padding |
| `space_sm` | 8dp | Compact spacing, chip gaps |
| `space_md` | 16dp | Standard padding, content spacing |
| `space_lg` | 24dp | Section separation, card padding |
| `space_xl` | 32dp | Screen margins, major sections |
| `space_xxl` | 48dp | Large empty states, hero sections |

### Layout Patterns

**Screen Structure**
```kotlin
Scaffold(
    topBar = { TopAppBar(...) },
    bottomBar = { BottomNavigationBar(...) },
    floatingActionButton = { FloatingActionButton(...) }
) { paddingValues ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp) // Standard margin
    ) {
        // Content
    }
}
```

**Card Layout**
```kotlin
Card(
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp), // Outer spacing
    shape = MaterialTheme.shapes.medium
) {
    Column(
        modifier = Modifier.padding(16.dp) // Inner padding
    ) {
        // Card content
    }
}
```

**Grid Spacing**
```kotlin
LazyVerticalGrid(
    columns = GridCells.Adaptive(minSize = 120.dp),
    contentPadding = PaddingValues(16.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
) {
    // Grid items
}
```

### Responsive Layouts

- **Phone Portrait**: Single column, full width
- **Phone Landscape**: Adaptive grid (2-3 columns)
- **Tablet**: Multi-column layouts, master-detail
- **Large Screens**: Maximum width constraints (840dp)

---

## 🧩 Component Library

### 1. FileGridItem

**Purpose**: Display media files in grid with selection state

**Usage**:
```kotlin
FileGridItem(
    file = fileItem,
    isSelected = selectedFiles.contains(fileItem),
    onClick = { viewModel.handleAction(ToggleSelection(fileItem)) }
)
```

**Visual States**:
- Unselected: Standard elevation (1dp)
- Selected: Elevated (4dp) with checkmark overlay
- Loading: Placeholder with shimmer effect

### 2. ErrorState

**Purpose**: User-friendly error display with retry actions

**Usage**:
```kotlin
ErrorState(
    title = "Unable to Load Files",
    message = "Check storage permissions and try again.",
    primaryActionLabel = "Retry",
    onPrimaryAction = { viewModel.handleAction(LoadFiles) },
    secondaryActionLabel = "Settings",
    onSecondaryAction = { navigateToSettings() }
)
```

**Visual Elements**:
- Large error icon (120dp)
- Title in `titleLarge` style
- Message in `bodyMedium` style
- Action buttons (primary filled, secondary outlined)

### 3. EmptyState

**Purpose**: Informative empty state with guidance

**Usage**:
```kotlin
EmptyState(
    title = "No Files Selected",
    message = "Select media files to rename from your gallery.",
    icon = Icons.Default.Image,
    actionLabel = "Browse Files",
    onAction = { navigateToFilePicker() }
)
```

### 4. LoadingSkeleton

**Purpose**: Content placeholders during data loading

**Variants**:
- `FileGridSkeleton`: Grid of placeholder cards
- `ListItemSkeleton`: List item placeholders
- `ProfileSkeleton`: User profile placeholder

**Usage**:
```kotlin
if (state.isLoading) {
    FileGridSkeleton(itemCount = 12)
} else {
    FileGrid(files = state.files)
}
```

### 5. SortStrategyPicker

**Purpose**: Select file sorting method

**Usage**:
```kotlin
SortStrategyPicker(
    selectedStrategy = state.sortStrategy,
    onStrategySelected = { strategy ->
        viewModel.handleAction(ChangeSortStrategy(strategy))
    },
    showCard = true,
    title = "Sort Order"
)
```

**Strategies**:
- Natural: Alphanumeric with number awareness
- Date Modified: Newest/oldest first
- File Size: Largest/smallest first
- Original Order: Preserve selection order

### 6. MetadataVariableChip

**Purpose**: Insert metadata variables into rename patterns

**Usage**:
```kotlin
MetadataVariableChip(
    variable = MetadataVariable.DATE,
    onInsert = { variable ->
        viewModel.handleAction(InsertVariable(variable))
    }
)
```

**Variables**:
- `{date}`: File creation/modification date
- `{time}`: Timestamp
- `{counter}`: Sequential numbering
- `{original}`: Original filename

### 7. TagFilterChips

**Purpose**: Filter files by tags

**Usage**:
```kotlin
TagFilterChips(
    availableTags = state.availableTags,
    selectedTags = state.selectedTags,
    onTagToggled = { tag ->
        viewModel.handleAction(ToggleTagFilter(tag))
    }
)
```

---

## 🎭 Animation Guidelines

### Animation Duration

- **Quick**: 100-150ms - Micro-interactions (ripple, hover)
- **Normal**: 200-300ms - State changes (expand/collapse)
- **Slow**: 400-500ms - Screen transitions, complex animations

### Animation Curves

- **Standard**: Default easing for most animations
- **Emphasized**: Bold, expressive motion
- **Decelerate**: Incoming elements (enter)
- **Accelerate**: Outgoing elements (exit)

### Common Animations

**Visibility Animations**
```kotlin
AnimatedVisibility(
    visible = isVisible,
    enter = fadeIn() + slideInVertically(),
    exit = fadeOut() + slideOutVertically()
) {
    // Content
}
```

**State Transitions**
```kotlin
AnimatedContent(
    targetState = currentState,
    transitionSpec = {
        fadeIn(animationSpec = tween(300)) with
        fadeOut(animationSpec = tween(300))
    }
) { state ->
    // Content based on state
}
```

**Size Changes**
```kotlin
Box(
    modifier = Modifier.animateContentSize(
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
) {
    // Content that changes size
}
```

**List Item Animations**
```kotlin
LazyColumn {
    items(
        items = itemsList,
        key = { it.id }
    ) { item ->
        ItemCard(
            modifier = Modifier.animateItemPlacement()
        )
    }
}
```

---

## ✅ State Management Patterns

### Loading States

```kotlin
when {
    state.isLoading -> LoadingSkeleton()
    state.error != null -> ErrorState(
        title = "Error",
        message = state.error,
        onPrimaryAction = { retry() }
    )
    state.isEmpty -> EmptyState(
        title = "No Data",
        message = "Try adjusting filters"
    )
    else -> Content(data = state.data)
}
```

### Multi-State Screens

```kotlin
// Define clear state hierarchy
data class ScreenState(
    val data: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isEmpty: Boolean
        get() = !isLoading && error == null && data.isEmpty()
        
    val canShowContent: Boolean
        get() = !isLoading && error == null && data.isNotEmpty()
}
```

### Optimistic Updates

```kotlin
// Update UI immediately, rollback on error
fun handleAction(action: SelectItem(item)) {
    // Optimistic update
    updateState { copy(selectedItems = selectedItems + item) }
    
    // Background operation
    viewModelScope.launch {
        val result = repository.selectItem(item)
        if (result is Error) {
            // Rollback
            updateState { copy(selectedItems = selectedItems - item) }
            sendEvent(ShowError(result.message))
        }
    }
}
```

---

## ♿ Accessibility Standards

### Content Descriptions

**Images & Icons**
```kotlin
Icon(
    imageVector = Icons.Default.Delete,
    contentDescription = "Delete file", // Descriptive label
    tint = MaterialTheme.colorScheme.error
)

// Decorative icons
Icon(
    imageVector = Icons.Default.Star,
    contentDescription = null // Decorative only
)
```

**Interactive Elements**
```kotlin
IconButton(
    onClick = { /* action */ },
    modifier = Modifier.semantics {
        contentDescription = "Add new template"
        role = Role.Button
    }
) {
    Icon(Icons.Default.Add, contentDescription = null)
}
```

### Touch Targets

- **Minimum Size**: 48dp × 48dp (Material Design standard)
- **Recommended**: 56dp × 56dp for primary actions
- **Spacing**: 8dp minimum between targets

```kotlin
// Ensure adequate touch target
IconButton(
    onClick = { /* action */ },
    modifier = Modifier.size(48.dp) // Minimum touch target
) {
    Icon(Icons.Default.Settings, contentDescription = "Settings")
}
```

### Semantic Properties

```kotlin
Text(
    text = "3 files selected",
    modifier = Modifier.semantics {
        contentDescription = "3 files selected out of 25 total"
        stateDescription = "Selectable, currently selected"
    }
)
```

### Screen Reader Support

- Provide meaningful labels for all interactive elements
- Use `contentDescription` for images and icons
- Set `stateDescription` for toggle states
- Group related content with `Modifier.semantics(mergeDescendants = true)`

---

## 🎯 Best Practices

### Component Design

**DO**:
- ✅ Keep components small and focused (single responsibility)
- ✅ Use `remember` for internal state
- ✅ Accept lambda callbacks for actions
- ✅ Provide modifier parameter for flexibility
- ✅ Add preview functions for all composables
- ✅ Document parameters with KDoc

**DON'T**:
- ❌ Access ViewModels directly in reusable components
- ❌ Perform side effects without `LaunchedEffect`
- ❌ Create mutable state outside of `remember`
- ❌ Hardcode dimensions (use spacing tokens)
- ❌ Ignore accessibility requirements

### State Hoisting

```kotlin
// ❌ BAD: State owned by component
@Composable
fun SearchBar() {
    var query by remember { mutableStateOf("") }
    TextField(value = query, onValueChange = { query = it })
}

// ✅ GOOD: State hoisted to caller
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
    )
}
```

### Performance Optimization

**Avoid Unnecessary Recomposition**
```kotlin
// Use derivedStateOf for computed values
val filteredItems by remember {
    derivedStateOf {
        items.filter { it.matches(searchQuery) }
    }
}

// Use key() for stable list items
LazyColumn {
    items(items, key = { it.id }) { item ->
        ItemCard(item)
    }
}
```

**Lazy Loading**
```kotlin
// Load images lazily with Coil
AsyncImage(
    model = ImageRequest.Builder(LocalContext.current)
        .data(imageUri)
        .crossfade(true)
        .build(),
    contentDescription = null,
    modifier = Modifier.fillMaxSize()
)
```

### Preview Functions

**Comprehensive Previews**
```kotlin
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Large Font", fontScale = 1.5f)
@Composable
fun MyComponentPreview() {
    ConversionTheme {
        MyComponent(
            // Provide sample data
            title = "Sample Title",
            subtitle = "Sample subtitle text"
        )
    }
}
```

**Multiple States**
```kotlin
@Preview(name = "Empty State")
@Composable
fun EmptyStatePreview() {
    ConversionTheme {
        MyScreen(state = State(data = emptyList()))
    }
}

@Preview(name = "Loading State")
@Composable
fun LoadingStatePreview() {
    ConversionTheme {
        MyScreen(state = State(isLoading = true))
    }
}

@Preview(name = "Error State")
@Composable
fun ErrorStatePreview() {
    ConversionTheme {
        MyScreen(state = State(error = "Network error"))
    }
}
```

---

## 📱 Screen-Specific Guidelines

### File Selection Screen

**Layout**: Adaptive grid (2-4 columns based on screen width)
**Selection**: Visual feedback with elevation and checkmark
**Actions**: Floating action button for confirming selection
**Empty State**: Guidance to grant permissions or add files

### Rename Configuration Screen

**Layout**: Vertical form with sectioned cards
**Input Validation**: Real-time feedback on pattern validity
**Preview**: Live preview of first few renamed files
**Actions**: Bottom button bar (Cancel, Apply)

### Rename Progress Screen

**Layout**: Centered progress indicator with statistics
**Updates**: Real-time progress updates (files renamed / total)
**Error Handling**: Collapsible list of failed operations
**Completion**: Success animation with summary statistics

### Settings Screen

**Layout**: Grouped preferences with sections
**Navigation**: Hierarchical navigation for nested settings
**Toggles**: Clear labels with descriptions
**Actions**: Immediate effect or save button based on preference type

---

## 🔧 Development Workflow

### Adding New Screen

1. **Create Contract** (State/Events/Actions)
2. **Create ViewModel** (State management logic)
3. **Create Screen** (UI composables)
4. **Add Navigation** (Navigation graph integration)
5. **Add Previews** (Light/Dark/States)
6. **Add Tests** (ViewModel + UI tests)

### Component Checklist

- [ ] KDoc comments on public APIs
- [ ] Modifier parameter with `= Modifier`
- [ ] Preview functions (Light/Dark modes)
- [ ] Accessibility content descriptions
- [ ] Semantic properties for screen readers
- [ ] Touch targets ≥ 48dp
- [ ] Error states handled
- [ ] Loading states handled
- [ ] Empty states handled

---

## 📊 Quality Metrics

### UI Performance Targets

- **Frame Rate**: 60 FPS (no janky animations)
- **Touch Response**: < 100ms tap-to-visual feedback
- **Screen Load**: < 500ms time to interactive
- **Scroll Performance**: Smooth 60 FPS scrolling

### Accessibility Compliance

- **WCAG 2.1 Level AA**: Minimum compliance
- **Color Contrast**: 4.5:1 for text, 3:1 for UI elements
- **Touch Targets**: 48dp × 48dp minimum
- **Screen Reader**: All content accessible via TalkBack

### Code Quality

- **Preview Coverage**: 100% of screens and major components
- **KDoc Coverage**: All public composables and parameters
- **Reusability**: Maximum component reuse across screens
- **Consistency**: Uniform spacing, colors, typography

---

## 📚 Resources

### Material Design 3

- [Material 3 Design Kit](https://m3.material.io/)
- [Color System](https://m3.material.io/styles/color/overview)
- [Typography Scale](https://m3.material.io/styles/typography/overview)
- [Motion Guidelines](https://m3.material.io/styles/motion/overview)

### Jetpack Compose

- [Compose Documentation](https://developer.android.com/jetpack/compose)
- [Compose Guidelines](https://developer.android.com/jetpack/compose/guidelines)
- [Compose Samples](https://github.com/android/compose-samples)

### Accessibility

- [Android Accessibility](https://developer.android.com/guide/topics/ui/accessibility)
- [WCAG 2.1 Guidelines](https://www.w3.org/WAI/WCAG21/quickref/)
- [Material Accessibility](https://m3.material.io/foundations/accessible-design/overview)

---

## 🎉 Conclusion

Following these guidelines ensures a consistent, accessible, and high-quality user interface throughout the Auto Rename File Service application. When in doubt, refer to Material 3 design principles and prioritize user experience and accessibility.

**Questions or suggestions?** Contact Sokchea (UI Specialist) or refer to the team's design discussions.

---

**Version History:**
- **1.0** (Dec 9, 2025): Initial UI guidelines document
