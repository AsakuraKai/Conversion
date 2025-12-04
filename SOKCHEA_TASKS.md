# Sokchea's Work Guide - Frontend/UI Specialist
## Auto Rename File Service

**Role:** Frontend/UI Specialist  
**Focus:** Presentation layer, UI/UX, Jetpack Compose, State management  
**Primary Skills:** Jetpack Compose, Material 3, UI/UX design, Animations

**Last Updated:** November 21, 2025

---

## 🎯 Your Responsibilities

### Primary Areas:
- ✅ Presentation layer: ViewModels, UI States, Contracts
- ✅ Jetpack Compose UI: Screens, Components, Navigation
- ✅ Material 3 Design implementation
- ✅ Animations and transitions
- ✅ Theme and styling
- ✅ User interactions and gestures
- ✅ UI testing with Compose Testing
- ✅ Accessibility implementation
- ✅ Empty and error states

### Your Files (Exclusive Ownership):
```
presentation/
├── <feature>/
│   ├── <Feature>Contract.kt    ✅ You create State/Events/Actions
│   ├── <Feature>ViewModel.kt   ✅ You implement logic
│   └── <Feature>Screen.kt      ✅ You create UI
│
ui/
├── components/         ✅ You create reusable components
├── theme/              ✅ You modify theme (after initial setup)
└── navigation/         ✅ You own navigation

MainActivity.kt         ✅ You own (after initial setup)

test/presentation/      ✅ Your ViewModel tests
test/ui/                ✅ Your Compose UI tests
```

---

## 🔄 Your Git Workflow

### Daily Workflow:
```bash
# Start of day: Sync with main
git checkout sokchea-dev
git pull origin main --rebase

# Create feature branch for your work
git checkout -b feature/chunk-X-ui
# Example: feature/chunk-3-file-selection-ui

# Make your changes...
# Preview your UI...
# Test your ViewModel...

# Commit with clear messages
git add .
git commit -m "[CHUNK X] Implement Feature - UI"

# Before creating PR: Rebase on main
git checkout sokchea-dev
git pull origin main --rebase
git checkout feature/chunk-X-ui
git rebase sokchea-dev

# Push and create PR
git push origin feature/chunk-X-ui
```

### PR Title Format:
```
"[CHUNK X] Feature Name - UI Implementation"
"[INTEGRATION] Feature X - Ready to merge with backend"
"[UI] Feature X - Polish and animations"
```

---

## 📋 Your Task Checklist Per Chunk

### Step 1: Wait for Domain Models (Kai works on this first)
```
□ Wait for Kai's "[READY]" PR notification
□ Pull latest main branch
□ Review Kai's domain models and interfaces
□ Understand the data structures you'll work with
□ Ask questions if APIs are unclear
```

**Important:** You need domain models before starting!

### Step 2: Create MVI Contract (Day 1 Afternoon)
```kotlin
□ Create Contract file: presentation/<feature>/<Feature>Contract.kt
□ Define State data class (what UI displays)
□ Define Event sealed class (one-time UI events)
□ Define Action sealed class (user actions)
□ Add computed properties to State for derived data
□ Write KDoc comments
```

### Step 3: Implement ViewModel (Day 1 Afternoon - Day 2)
```kotlin
□ Create ViewModel: presentation/<feature>/<Feature>ViewModel.kt
□ Extend BaseViewModel<State, Event>
□ Inject use cases (from Kai's domain layer)
□ Implement action handlers
□ Handle success/error states
□ Send appropriate events
□ Write unit tests with mocked use cases
```

**Note:** Use fake/mock data if Kai's data layer isn't ready yet!

### Step 4: Create UI (Day 2)
```kotlin
□ Create Screen composable
□ Implement all UI states (loading, success, error, empty)
□ Add user interactions
□ Collect state and events from ViewModel
□ Create reusable components if needed
□ Add accessibility content descriptions
□ Test with @Preview
□ Verify light/dark theme support
```

### Step 5: Integration Testing (After both merge)
```kotlin
□ Pull latest main (get Kai's data implementation)
□ Remove fake data, use real ViewModels
□ Build and run the app
□ Test end-to-end flow with real data
□ Fix any integration issues
□ Take screenshots/videos for documentation
```

---

## 📊 Your Work by Phase

### **Phase 2: Core Features (MVP)** - YOUR PRIORITY

#### ✅ CHUNK 2: Permissions System (COMPLETED)
**Status:** 100% Complete
- ✅ MVI Contract (State, Events, Actions)
- ✅ PermissionsViewModel with all action handlers
- ✅ PermissionHandler composable (reusable component)
- ✅ Integration with Accompanist Permissions
- ✅ Settings navigation support

---

#### 🔜 CHUNK 3: File Selection Feature
**Your Tasks:**

**Prerequisites:** Wait for Kai's domain models (FileItem, FileFilter, MediaRepository interface)

**Tasks:**
```kotlin
// 1. Create MVI Contract
presentation/fileselection/FileSelectionContract.kt

data class State(
    val files: List<FileItem> = emptyList(),
    val selectedFiles: Set<FileItem> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val filter: FileFilter = FileFilter(),
) {
    val hasSelection: Boolean get() = selectedFiles.isNotEmpty()
    val selectedCount: Int get() = selectedFiles.size
}

sealed class Event {
    data class ShowMessage(val message: String) : Event()
    data class NavigateToRename(val files: List<FileItem>) : Event()
}

sealed class Action {
    data object LoadFiles : Action()
    data class ToggleSelection(val file: FileItem) : Action()
    data object SelectAll : Action()
    data object ClearSelection : Action()
    data class ApplyFilter(val filter: FileFilter) : Action()
    data object ConfirmSelection : Action()
}
```

**Tasks:**
```kotlin
// 2. Create ViewModel
presentation/fileselection/FileSelectionViewModel.kt

@HiltViewModel
class FileSelectionViewModel @Inject constructor(
    private val getMediaFilesUseCase: GetMediaFilesUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<State, Event>(State()) {
    
    init {
        handleAction(Action.LoadFiles)
    }
    
    fun handleAction(action: Action) {
        when (action) {
            is Action.LoadFiles -> loadFiles()
            is Action.ToggleSelection -> toggleSelection(action.file)
            // ... implement all actions
        }
    }
    
    private fun loadFiles() {
        // Use fake data if Kai's implementation isn't ready:
        val fakeFiles = listOf(
            FileItem(id = "1", name = "IMG_001.jpg", ...),
            FileItem(id = "2", name = "IMG_002.jpg", ...),
        )
        updateState { copy(files = fakeFiles, isLoading = false) }
        
        // Or use real use case if Kai merged:
        // executeUseCase(...) { getMediaFilesUseCase(currentState.filter) }
    }
}
```

**Tasks:**
```kotlin
// 3. Create UI Screen
presentation/fileselection/FileSelectionScreen.kt

@Composable
fun FileSelectionScreen(
    viewModel: FileSelectionViewModel = hiltViewModel(),
    onNavigateToRename: (List<FileItem>) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is Event.NavigateToRename -> onNavigateToRename(event.files)
                is Event.ShowMessage -> // Show snackbar
            }
        }
    }
    
    Scaffold(
        topBar = {
            FileSelectionToolbar(
                selectedCount = state.selectedCount,
                onSelectAll = { viewModel.handleAction(Action.SelectAll) },
                onClearSelection = { viewModel.handleAction(Action.ClearSelection) }
            )
        },
        floatingActionButton = {
            if (state.hasSelection) {
                ExtendedFloatingActionButton(
                    text = { Text("Rename ${state.selectedCount} files") },
                    onClick = { viewModel.handleAction(Action.ConfirmSelection) }
                )
            }
        }
    ) { padding ->
        when {
            state.isLoading -> LoadingIndicator()
            state.error != null -> ErrorState(state.error!!)
            state.files.isEmpty() -> EmptyState()
            else -> FileGrid(
                files = state.files,
                selectedFiles = state.selectedFiles,
                onFileClick = { file -> 
                    viewModel.handleAction(Action.ToggleSelection(file))
                }
            )
        }
    }
}

// 4. Create reusable component
ui/components/FileGridItem.kt

@Composable
fun FileGridItem(
    file: FileItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        colors = if (isSelected) selectedColors else defaultColors
    ) {
        Box {
            // Thumbnail with Coil
            AsyncImage(
                model = file.thumbnailUri,
                contentDescription = file.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            // Selection indicator
            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                )
            }
        }
    }
}
```



---

#### 🔜 CHUNK 4: Batch Rename Configuration UI
**Your Tasks:**

**Prerequisites:** Wait for Kai's RenameConfig model and GenerateFilenameUseCase

**Tasks:**
```kotlin
// 1. Create Contract
presentation/renameconfig/RenameConfigContract.kt

data class State(
    val config: RenameConfig = RenameConfig(),
    val previewFilename: String = "",
    val validationError: String? = null,
) {
    val canProceed: Boolean get() = validationError == null
}

sealed class Event {
    data class NavigateToPreview(val config: RenameConfig) : Event()
}

sealed class Action {
    data class UpdatePrefix(val prefix: String) : Action()
    data class UpdateStartNumber(val number: Int) : Action()
    data class UpdateDigitCount(val count: Int) : Action()
    data object Confirm : Action()
}

// 2. Create ViewModel
presentation/renameconfig/RenameConfigViewModel.kt

// 3. Create UI
presentation/renameconfig/RenameConfigScreen.kt

@Composable
fun RenameConfigScreen(
    viewModel: RenameConfigViewModel = hiltViewModel(),
    onNavigateToPreview: (RenameConfig) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Prefix input
        OutlinedTextField(
            value = state.config.prefix,
            onValueChange = { viewModel.handleAction(Action.UpdatePrefix(it)) },
            label = { Text("Prefix") },
            placeholder = { Text("e.g., IMG_") }
        )
        
        // Start number input
        OutlinedTextField(
            value = state.config.startNumber.toString(),
            onValueChange = { /* parse and update */ },
            label = { Text("Start Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        
        // Digit count slider
        Text("Digit Count: ${state.config.digitCount}")
        Slider(
            value = state.config.digitCount.toFloat(),
            onValueChange = { /* update */ },
            valueRange = 2f..6f,
            steps = 4
        )
        
        // Live preview
        PreviewCard(
            title = "Preview",
            example = state.previewFilename
        )
        
        // Validation error
        if (state.validationError != null) {
            ErrorText(state.validationError!!)
        }
        
        // Confirm button
        Button(
            onClick = { viewModel.handleAction(Action.Confirm) },
            enabled = state.canProceed,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue to Preview")
        }
    }
}
```



---

#### 🔜 CHUNK 5: Rename Progress UI
**Your Tasks:**

**Prerequisites:** Kai's ExecuteBatchRenameUseCase with Flow<RenameProgress>

```kotlin
// 1. Create Contract
presentation/renameprogress/RenameProgressContract.kt

data class State(
    val progress: RenameProgress? = null,
    val isComplete: Boolean = false,
    val hasErrors: Boolean = false,
) {
    val progressPercentage: Float get() = 
        progress?.let { it.currentIndex.toFloat() / it.total } ?: 0f
}

// 2. Create ViewModel that collects Flow from use case
presentation/renameprogress/RenameProgressViewModel.kt

// 3. Create animated progress UI
presentation/renameprogress/RenameProgressScreen.kt

@Composable
fun RenameProgressScreen() {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated circular progress
        CircularProgressIndicator(
            progress = state.progressPercentage,
            modifier = Modifier.size(120.dp)
        )
        
        // Current file info
        Text(
            text = "Renaming ${state.progress?.currentIndex} of ${state.progress?.total}",
            style = MaterialTheme.typography.titleMedium
        )
        
        Text(
            text = state.progress?.currentFile?.name ?: "",
            style = MaterialTheme.typography.bodyMedium
        )
        
        // Success animation when complete
        AnimatedVisibility(visible = state.isComplete) {
            SuccessAnimation()
        }
    }
}
```



---

#### 🔜 CHUNK 6: Folder Picker UI
**Your Tasks:**

**Prerequisites:** Kai's FolderInfo model and GetFoldersUseCase

```kotlin
// Similar pattern: Contract → ViewModel → Screen
// Tree navigation UI
// Breadcrumb navigation
// Folder icons and file counts
```

---

### **Phase 3: Advanced Features**

#### 🔜 CHUNK 7: Preview List UI
```kotlin
// Create preview list with before/after columns
// Color-coded warnings (red for conflicts, yellow for warnings)
// Swipe actions to edit individual names
```

---

#### 🔜 CHUNK 8: Natural Sorting UI
**Your Tasks:**

**Prerequisites:** Kai's SortStrategy enum and SortFilesUseCase

```kotlin
// 1. Add to RenameConfigContract
presentation/renameconfig/RenameConfigContract.kt

data class State(
    val config: RenameConfig = RenameConfig(),
    val sortStrategy: SortStrategy = SortStrategy.NATURAL,
    val previewFilename: String = "",
    val validationError: String? = null,
)

sealed class Action {
    // ... existing actions
    data class UpdateSortStrategy(val strategy: SortStrategy) : Action()
}

// 2. Update RenameConfigScreen
presentation/renameconfig/RenameConfigScreen.kt

@Composable
fun RenameConfigScreen() {
    Column {
        // ... existing fields
        
        // Sort strategy selector
        Text(
            "Sort Order",
            style = MaterialTheme.typography.titleSmall
        )
        
        Column {
            SortStrategy.values().forEach { strategy ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.handleAction(Action.UpdateSortStrategy(strategy))
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = state.sortStrategy == strategy,
                        onClick = {
                            viewModel.handleAction(Action.UpdateSortStrategy(strategy))
                        }
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = when(strategy) {
                                SortStrategy.NATURAL -> "Natural (IMG_1, IMG_2, IMG_10)"
                                SortStrategy.DATE_MODIFIED -> "Date Modified"
                                SortStrategy.SIZE -> "File Size"
                                SortStrategy.ORIGINAL_ORDER -> "Original Selection Order"
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = when(strategy) {
                                SortStrategy.NATURAL -> "Smart number sorting"
                                SortStrategy.DATE_MODIFIED -> "Oldest to newest"
                                SortStrategy.SIZE -> "Smallest to largest"
                                SortStrategy.ORIGINAL_ORDER -> "Keep selection order"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// 3. Create reusable component (optional)
ui/components/SortStrategyPicker.kt

@Composable
fun SortStrategyPicker(
    selectedStrategy: SortStrategy,
    onStrategySelected: (SortStrategy) -> Unit,
    modifier: Modifier = Modifier
) {
    // Reusable picker component
}
```

---

#### 🔜 CHUNK 9: File Observer - Real-time Monitoring UI
**Your Tasks:**

**Prerequisites:** Wait for Kai's FolderMonitor model, monitoring use cases, and MonitoringService shell

```kotlin
// 1. Create Contract
presentation/monitoring/MonitoringContract.kt

data class State(
    val isMonitoring: Boolean = false,
    val monitoredFolder: String? = null,
    val recentEvents: List<FileEvent> = emptyList(),
    val config: FolderMonitor? = null,
    val error: String? = null
) {
    val canStartMonitoring: Boolean get() = monitoredFolder != null
}

sealed class Event {
    data class ShowMessage(val message: String) : Event()
    data object NavigateToSettings : Event()
}

sealed class Action {
    data class SelectFolder(val path: String) : Action()
    data class StartMonitoring(val config: FolderMonitor) : Action()
    data object StopMonitoring : Action()
    data class UpdatePattern(val pattern: String) : Action()
}

// 2. Create ViewModel
presentation/monitoring/MonitoringViewModel.kt

// 3. Implement MonitoringService UI components
service/MonitoringService.kt (complete Kai's shell)
- Create notification channel
- Design notification layout
- Add action buttons (Stop, Settings)
- Handle notification clicks

// 4. Create UI Screen
presentation/monitoring/MonitoringScreen.kt

@Composable
fun MonitoringScreen() {
    Column {
        // Folder selector
        FolderSelectionCard(
            selectedFolder = state.monitoredFolder,
            onSelectFolder = { /* folder picker */ }
        )
        
        // Pattern configuration
        OutlinedTextField(
            value = state.config?.filePattern ?: "",
            onValueChange = { viewModel.handleAction(Action.UpdatePattern(it)) },
            label = { Text("File Pattern (e.g., *.jpg)") }
        )
        
        // Toggle switch
        MonitoringToggleCard(
            isMonitoring = state.isMonitoring,
            onToggle = {
                if (state.isMonitoring) {
                    viewModel.handleAction(Action.StopMonitoring)
                } else {
                    viewModel.handleAction(Action.StartMonitoring(state.config!!))
                }
            }
        )
        
        // Recent events list
        if (state.recentEvents.isNotEmpty()) {
            RecentEventsCard(
                events = state.recentEvents
            )
        }
    }
}
```

---

#### 🔜 CHUNK 10: Dynamic Theming from Images UI
**Your Tasks:**

**Prerequisites:** Kai's ExtractPaletteUseCase and ImagePalette model

```kotlin
// 1. Create Contract
presentation/theme/DynamicThemeContract.kt

// 2. Create ViewModel
presentation/theme/DynamicThemeViewModel.kt

// 3. Create UI
presentation/theme/DynamicThemeScreen.kt

@Composable
fun DynamicThemeScreen() {
    Column {
        // Image picker button
        Button(
            onClick = { /* launch image picker */ }
        ) {
            Text("Choose Background Image")
        }
        
        // Current image preview
        if (state.selectedImageUri != null) {
            AsyncImage(
                model = state.selectedImageUri,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
        }
        
        // Extracted colors preview
        if (state.palette != null) {
            ColorPalettePreview(
                palette = state.palette!!
            )
        }
        
        // Apply/Reset buttons
        Row {
            Button(onClick = { viewModel.handleAction(Action.ApplyTheme) }) {
                Text("Apply Theme")
            }
            TextButton(onClick = { viewModel.handleAction(Action.ResetTheme) }) {
                Text("Reset to Default")
            }
        }
    }
}

// 4. Update theme system
ui/theme/Theme.kt
- Support dynamic color scheme
- Apply colors from ImagePalette
```

---

### **Phase 4: Smart Features**

#### 🔜 CHUNK 11: EXIF Metadata Variable Picker UI
**Your Tasks:**

**Prerequisites:** Kai's ImageMetadata model and ExtractMetadataUseCase

```kotlin
// 1. Create Contract
presentation/metadata/MetadataPickerContract.kt

// 2. Create ViewModel
presentation/metadata/MetadataPickerViewModel.kt

// 3. Create UI
presentation/metadata/MetadataPickerScreen.kt

@Composable
fun MetadataPickerScreen() {
    Column {
        // Variable chips
        FlowRow {
            MetadataVariableChip(
                label = "Date",
                example = "2024_12_04",
                onClick = { /* insert {date} */ }
            )
            MetadataVariableChip(
                label = "Location",
                example = "Paris_France",
                onClick = { /* insert {location} */ }
            )
            MetadataVariableChip(
                label = "Camera",
                example = "Canon_EOS_R5",
                onClick = { /* insert {camera} */ }
            )
        }
        
        // Live preview with actual metadata
        MetadataPreviewCard(
            metadata = state.sampleMetadata,
            pattern = state.currentPattern
        )
    }
}
```

---

#### 🔜 CHUNK 12: Pattern Templates CRUD UI
**Your Tasks:**

**Prerequisites:** Kai's RenameTemplate model and template use cases

```kotlin
// 1. Create Contract
presentation/template/TemplateContract.kt

// 2. Create ViewModel
presentation/template/TemplateViewModel.kt

// 3. Create UI
presentation/template/TemplateScreen.kt

@Composable
fun TemplateScreen() {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* show save dialog */ }
            ) {
                Icon(Icons.Default.Add, "Add Template")
            }
        }
    ) {
        LazyColumn {
            items(state.templates) { template ->
                TemplateCard(
                    template = template,
                    onApply = { viewModel.handleAction(Action.ApplyTemplate(template)) },
                    onEdit = { viewModel.handleAction(Action.EditTemplate(template)) },
                    onDelete = { viewModel.handleAction(Action.DeleteTemplate(template.id)) },
                    onToggleFavorite = { viewModel.handleAction(Action.ToggleFavorite(template.id)) }
                )
            }
        }
    }
}

// 4. Create save/edit dialog
ui/components/TemplateDialog.kt
```

---

#### 🔜 CHUNK 13: AI Suggestion Chips UI
**Your Tasks:**

**Prerequisites:** Kai's AnalyzeImageUseCase and ImageLabel model

```kotlin
// 1. Create Contract
presentation/ai/AISuggestionContract.kt

// 2. Create ViewModel
presentation/ai/AISuggestionViewModel.kt

// 3. Create UI component
ui/components/AISuggestionChips.kt

@Composable
fun AISuggestionChips(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit
) {
    Column {
        Text(
            "AI Suggestions",
            style = MaterialTheme.typography.titleSmall
        )
        
        FlowRow(spacing = 8.dp) {
            suggestions.forEach { suggestion ->
                SuggestionChip(
                    onClick = { onSuggestionClick(suggestion) },
                    label = { Text(suggestion) },
                    icon = {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }
        
        // Loading state while analyzing
        if (isAnalyzing) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}

// 4. Integrate into RenameConfigScreen
// Add AI suggestion section above prefix field
```

---

#### 🔜 CHUNK 14: Undo/Redo History UI
**Your Tasks:**

**Prerequisites:** Kai's RenameOperation model and history use cases

```kotlin
// 1. Create Contract
presentation/history/HistoryContract.kt

// 2. Create ViewModel
presentation/history/HistoryViewModel.kt

// 3. Create UI
presentation/history/HistoryScreen.kt

@Composable
fun HistoryScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rename History") },
                actions = {
                    // Undo button
                    IconButton(
                        onClick = { viewModel.handleAction(Action.Undo) },
                        enabled = state.canUndo
                    ) {
                        Icon(Icons.Default.Undo, "Undo")
                    }
                    // Redo button
                    IconButton(
                        onClick = { viewModel.handleAction(Action.Redo) },
                        enabled = state.canRedo
                    ) {
                        Icon(Icons.Default.Redo, "Redo")
                    }
                }
            )
        }
    ) {
        LazyColumn {
            items(state.history) { operation ->
                OperationHistoryItem(
                    operation = operation,
                    onUndo = { viewModel.handleAction(Action.UndoSpecific(operation.id)) }
                )
            }
        }
    }
}

// 4. Add swipe-to-undo gesture
ui/components/SwipeToUndoItem.kt
```

---

#### 🔜 CHUNK 15: Regex Builder UI
**Your Tasks:**

**Prerequisites:** Kai's RegexRule model and ApplyRegexPatternUseCase

```kotlin
// 1. Create Contract
presentation/regex/RegexContract.kt

// 2. Create ViewModel
presentation/regex/RegexViewModel.kt

// 3. Create UI
presentation/regex/RegexBuilderScreen.kt

@Composable
fun RegexBuilderScreen() {
    Column {
        // Regex pattern input
        OutlinedTextField(
            value = state.pattern,
            onValueChange = { viewModel.handleAction(Action.UpdatePattern(it)) },
            label = { Text("Regex Pattern") },
            isError = state.validationError != null,
            supportingText = {
                if (state.validationError != null) {
                    Text(state.validationError!!, color = MaterialTheme.colorScheme.error)
                }
            }
        )
        
        // Replacement input
        OutlinedTextField(
            value = state.replacement,
            onValueChange = { viewModel.handleAction(Action.UpdateReplacement(it)) },
            label = { Text("Replacement") }
        )
        
        // Common patterns library
        Text("Common Patterns", style = MaterialTheme.typography.titleSmall)
        LazyRow {
            items(RegexPreset.values()) { preset ->
                PresetChip(
                    preset = preset,
                    onClick = { viewModel.handleAction(Action.ApplyPreset(preset)) }
                )
            }
        }
        
        // Live preview
        RegexPreviewCard(
            sampleText = "IMG_001.jpg",
            pattern = state.pattern,
            replacement = state.replacement,
            result = state.previewResult
        )
    }
}
```

---

#### 🔜 CHUNK 16: Tag Management UI
**Your Tasks:**

**Prerequisites:** Kai's FileTag model and tag use cases

```kotlin
// 1. Create Contract
presentation/tag/TagContract.kt

// 2. Create ViewModel
presentation/tag/TagViewModel.kt

// 3. Create UI
presentation/tag/TagManagementScreen.kt

@Composable
fun TagManagementScreen() {
    Column {
        // Create tag section
        CreateTagCard(
            onCreateTag = { name, color ->
                viewModel.handleAction(Action.CreateTag(name, color))
            }
        )
        
        // Existing tags
        LazyColumn {
            items(state.tags) { tag ->
                TagItem(
                    tag = tag,
                    onDelete = { viewModel.handleAction(Action.DeleteTag(tag.id)) },
                    onEdit = { viewModel.handleAction(Action.EditTag(tag)) }
                )
            }
        }
    }
}

// 4. Create tag filter UI
ui/components/TagFilterChips.kt

@Composable
fun TagFilterChips(
    tags: List<FileTag>,
    selectedTags: Set<String>,
    onTagClick: (String) -> Unit
) {
    FlowRow {
        tags.forEach { tag ->
            FilterChip(
                selected = tag.id in selectedTags,
                onClick = { onTagClick(tag.id) },
                label = { Text(tag.name) },
                leadingIcon = {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color(tag.color), CircleShape)
                    )
                }
            )
        }
    }
}

// 5. Integrate tags into FileSelectionScreen
// Add tag filter above file grid
```

---

### **Phase 5: Integration & Sync**

#### 🔜 CHUNK 17: Cloud Account Linking UI
**Your Tasks:**

**Prerequisites:** Kai's CloudSyncRepository and authentication use cases

```kotlin
// 1. Create Contract
presentation/cloud/CloudSyncContract.kt

// 2. Create ViewModel
presentation/cloud/CloudSyncViewModel.kt

// 3. Create UI
presentation/cloud/CloudSyncScreen.kt

@Composable
fun CloudSyncScreen() {
    Column {
        // Provider selection
        CloudProviderCard(
            provider = CloudProvider.GOOGLE_DRIVE,
            isConnected = state.googleDriveConnected,
            onConnect = { viewModel.handleAction(Action.ConnectProvider(CloudProvider.GOOGLE_DRIVE)) },
            onDisconnect = { viewModel.handleAction(Action.DisconnectProvider(CloudProvider.GOOGLE_DRIVE)) }
        )
        
        CloudProviderCard(
            provider = CloudProvider.DROPBOX,
            isConnected = state.dropboxConnected,
            onConnect = { viewModel.handleAction(Action.ConnectProvider(CloudProvider.DROPBOX)) },
            onDisconnect = { viewModel.handleAction(Action.DisconnectProvider(CloudProvider.DROPBOX)) }
        )
        
        // Sync settings
        SyncSettingsCard(
            autoSync = state.config.autoSync,
            syncInterval = state.config.syncInterval,
            onAutoSyncChange = { viewModel.handleAction(Action.UpdateAutoSync(it)) },
            onIntervalChange = { viewModel.handleAction(Action.UpdateSyncInterval(it)) }
        )
        
        // Manual sync button
        Button(
            onClick = { viewModel.handleAction(Action.ManualSync) },
            enabled = state.hasConnectedProvider
        ) {
            Text("Sync Now")
        }
        
        // Sync status
        if (state.isSyncing) {
            SyncProgressCard(
                progress = state.syncProgress
            )
        }
    }
}
```

---

#### 🔜 CHUNK 18: QR Code Display & Scanner UI
**Your Tasks:**

**Prerequisites:** Kai's QR generation/parsing use cases

```kotlin
// 1. Create Contract
presentation/qr/QRContract.kt

// 2. Create ViewModel
presentation/qr/QRViewModel.kt

// 3. Create QR display UI
presentation/qr/QRDisplayScreen.kt

@Composable
fun QRDisplayScreen(
    template: RenameTemplate
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Share Template: ${template.name}",
            style = MaterialTheme.typography.titleLarge
        )
        
        // QR Code image
        if (state.qrBitmap != null) {
            Image(
                bitmap = state.qrBitmap!!.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier
                    .size(300.dp)
                    .padding(16.dp)
            )
        }
        
        // Share button
        Button(
            onClick = { /* share QR image */ }
        ) {
            Icon(Icons.Default.Share, "Share")
            Text("Share QR Code")
        }
    }
}

// 4. Create QR scanner UI
presentation/qr/QRScannerScreen.kt

@Composable
fun QRScannerScreen() {
    // Use CameraX for camera preview
    // Integrate ZXing scanning
    // Show import confirmation dialog
}
```

---

#### 🔜 CHUNK 19: OCR Text Extraction UI
**Your Tasks:**

**Prerequisites:** Kai's ExtractTextFromImageUseCase

```kotlin
// 1. Create Contract
presentation/ocr/OCRContract.kt

// 2. Create ViewModel
presentation/ocr/OCRViewModel.kt

// 3. Create UI
ui/components/OCRExtractButton.kt

@Composable
fun OCRExtractButton(
    imageUri: Uri?,
    onTextExtracted: (String) -> Unit
) {
    Column {
        Button(
            onClick = { viewModel.handleAction(Action.ExtractText(imageUri!!)) },
            enabled = imageUri != null
        ) {
            Icon(Icons.Default.TextFields, "Extract Text")
            Text("Extract Text from Image")
        }
        
        // Extracted text preview
        if (state.extractedText != null) {
            ExtractedTextCard(
                text = state.extractedText!!,
                onUseText = { onTextExtracted(it) }
            )
        }
        
        // Loading indicator
        if (state.isExtracting) {
            CircularProgressIndicator()
        }
    }
}

// 4. Integrate into RenameConfigScreen
// Add OCR button when image files are selected
```

---

#### 🔜 CHUNK 20: Multi-Device Account Management UI
**Your Tasks:**

**Prerequisites:** Kai's SyncRepository and Firebase integration

```kotlin
// 1. Create Contract
presentation/account/AccountContract.kt

// 2. Create ViewModel
presentation/account/AccountViewModel.kt

// 3. Create UI
presentation/account/AccountScreen.kt

@Composable
fun AccountScreen() {
    Column {
        // Account info
        if (state.isSignedIn) {
            AccountInfoCard(
                user = state.currentUser,
                onSignOut = { viewModel.handleAction(Action.SignOut) }
            )
            
            // Sync status
            SyncStatusCard(
                lastSync = state.lastSyncTime,
                onManualSync = { viewModel.handleAction(Action.SyncNow) }
            )
            
            // Synced data summary
            SyncedDataCard(
                templates = state.syncedTemplatesCount,
                tags = state.syncedTagsCount,
                settings = state.settingsSynced
            )
        } else {
            // Sign in button
            SignInPromptCard(
                onSignIn = { viewModel.handleAction(Action.SignIn) }
            )
        }
    }
}
```

---

#### 🔜 CHUNK 21: Activity Log Viewer UI
**Your Tasks:**

**Prerequisites:** Kai's ActivityLog model and log use cases

```kotlin
// 1. Create Contract
presentation/activity/ActivityLogContract.kt

// 2. Create ViewModel
presentation/activity/ActivityLogViewModel.kt

// 3. Create UI
presentation/activity/ActivityLogScreen.kt

@Composable
fun ActivityLogScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Activity Log") },
                actions = {
                    // Filter button
                    IconButton(onClick = { /* show filter dialog */ }) {
                        Icon(Icons.Default.FilterList, "Filter")
                    }
                    // Export button
                    IconButton(onClick = { viewModel.handleAction(Action.ExportLogs) }) {
                        Icon(Icons.Default.Share, "Export")
                    }
                }
            )
        }
    ) {
        LazyColumn {
            items(state.logs) { log ->
                ActivityLogItem(
                    log = log,
                    onItemClick = { /* show details */ }
                )
            }
        }
    }
}

// 4. Create filter dialog
ui/components/LogFilterDialog.kt

// 5. Create export format selector
ui/components/ExportFormatDialog.kt
```

---

### **Phase 6: Polish & Optimization**

#### 🔜 CHUNK 22: UI Performance Optimization
**Your Tasks:**

```kotlin
// 1. Recomposition optimization
- Use remember { } for expensive calculations
- Use derivedStateOf for computed values
- Add keys to LazyColumn items
- Use @Stable annotations on state classes

// 2. Image loading optimization
- Configure Coil with proper disk/memory cache
- Implement thumbnail loading with size restrictions
- Add placeholder and error images

// 3. Layout optimization
- Use LazyColumn/Grid properly (avoid nested scrollables)
- Implement proper item keys
- Add content padding for better scrolling

// 4. Animation optimization
- Use animateContentSize sparingly
- Prefer AnimatedVisibility over manual animations
- Reduce over-drawing with proper clipping

// 5. Measure and benchmark
- Use Layout Inspector
- Profile with Android Studio Profiler
- Test on low-end devices
```

---

#### 🔜 CHUNK 23: UI Testing
**Your Tasks:**

```kotlin
// 1. Compose UI tests
androidTest/ui/FileSelectionScreenTest.kt
androidTest/ui/RenameConfigScreenTest.kt
androidTest/ui/PreviewScreenTest.kt

@Test
fun whenFilesLoaded_displaysFileGrid() {
    composeTestRule.setContent {
        FileSelectionScreen(
            viewModel = FakeFileSelectionViewModel()
        )
    }
    
    composeTestRule
        .onNodeWithTag("FileGrid")
        .assertIsDisplayed()
}

// 2. Screenshot tests
androidTest/screenshot/ScreenshotTests.kt

// 3. Accessibility tests
androidTest/accessibility/AccessibilityTests.kt
- Test with TalkBack
- Verify content descriptions
- Check touch target sizes

// 4. End-to-end flow tests
androidTest/e2e/RenameFlowTest.kt
```

---

#### 🔜 CHUNK 24: UI/UX Polish
**Your Tasks:**

```kotlin
// 1. Animations and transitions
- Add enter/exit animations to screens
- Smooth FAB transitions
- Loading skeleton screens
- Success celebrations (confetti, checkmark animations)

// 2. Empty states
ui/components/EmptyState.kt
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    actionLabel: String?,
    onAction: (() -> Unit)?
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.titleLarge)
        Text(description, style = MaterialTheme.typography.bodyMedium)
        if (actionLabel != null && onAction != null) {
            Button(onClick = onAction) {
                Text(actionLabel)
            }
        }
    }
}

// 3. Error states
ui/components/ErrorState.kt
- User-friendly error messages
- Retry buttons
- Error illustrations

// 4. Loading states
ui/components/LoadingSkeleton.kt
- Shimmer effects for lists
- Skeleton cards

// 5. Haptic feedback
- Add vibration on success/error
- Haptic feedback on button clicks
- Use HapticFeedback.performHapticFeedback()

// 6. Micro-interactions
- Button press animations
- Card elevation changes on hover
- Smooth transitions between states
```

---

#### 🔜 CHUNK 25: Accessibility & Theme Support
**Your Tasks:**

```kotlin
// 1. Content descriptions
- Add to all Icon components
- Add to all IconButton components
- Add to decorative images (empty string)

// 2. Semantic properties
Modifier.semantics {
    contentDescription = "Select file"
    role = Role.Button
    stateDescription = if (isSelected) "Selected" else "Not selected"
}

// 3. TalkBack testing
- Enable TalkBack
- Navigate through all screens
- Verify all interactive elements are announced
- Fix any issues found

// 4. Touch target sizes
- Ensure all clickable elements are at least 48.dp
- Add padding to small icons
- Use Modifier.minimumInteractiveComponentSize()

// 5. Color contrast
- Verify text meets WCAG AA standards
- Test with color blindness simulators
- Ensure sufficient contrast in all themes

// 6. RTL support
- Test with Arabic locale
- Verify layouts mirror correctly
- Use Modifier.fillMaxWidth() appropriately
- Use Row with Arrangement.SpaceBetween

// 7. Dynamic font scaling
- Test with large font sizes (Settings → Display → Font size)
- Ensure text doesn't overflow
- Use appropriate typography scales
```

---

#### 🔜 CHUNK 26: Documentation & Polish
**Your Tasks:**

```kotlin
// 1. KDoc comments for presentation layer
- Add KDoc to all ViewModels
- Add KDoc to all Contract classes
- Add KDoc to reusable composables

/**
 * Screen for selecting files to rename.
 *
 * Displays a grid of media files with multi-select functionality.
 * Users can filter by file type and navigate to rename configuration.
 *
 * @param viewModel ViewModel for managing file selection state
 * @param onNavigateToRename Callback when user confirms selection
 */
@Composable
fun FileSelectionScreen(...) { }

// 2. Preview functions
- Add @Preview to all major composables
- Add dark mode previews
- Add different screen size previews

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Large Font", fontScale = 1.5f)
@Composable
fun FileSelectionScreenPreview() { }

// 3. UI guidelines document
docs/ui-guidelines.md:
- Component usage guide
- Spacing and layout standards
- Color usage guide
- Typography guide
- Animation guidelines

// 4. Screenshot gallery
docs/screenshots/:
- Capture all major screens
- Both light and dark themes
- Different states (loading, error, empty, success)

// 5. Code cleanup
- Remove all TODO comments
- Remove unused imports
- Remove commented code
- Organize imports properly
- Run ktlintFormat

// 6. Final UI review
- Check all screens match design
- Verify consistent spacing
- Test all user flows
- Fix any visual bugs
```

---

## 🛠️ Your Tools & Setup

### Dependencies You'll Use:
```kotlin
// Already in project:
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose")
implementation("androidx.hilt:hilt-navigation-compose")

// For image loading:
implementation("io.coil-kt:coil-compose:2.5.0")

// For testing:
testImplementation("io.mockk:mockk:1.13.9")
androidTestImplementation("androidx.compose.ui:ui-test-junit4")
```

### Key Compose APIs You'll Use:
- `@Composable` - Define UI components
- `remember` / `rememberSaveable` - State management
- `LaunchedEffect` - Side effects
- `collectAsStateWithLifecycle()` - Observe StateFlow
- `hiltViewModel()` - Inject ViewModels
- `Scaffold` - App structure
- `LazyColumn` / `LazyVerticalGrid` - Lists
- `AnimatedVisibility` - Animations

---

## ✅ Your PR Checklist

Before creating each PR:
```
□ UI previews work correctly (@Preview functions)
□ Code compiles without errors
□ ViewModel tests pass (with mocked use cases)
□ UI follows Material 3 design guidelines
□ Accessibility content descriptions added
□ Loading/error/empty states implemented
□ No hardcoded strings (use string resources)
□ Animations are smooth (tested on emulator)
□ Works with both light and dark theme
□ Tested with fake data if Kai's impl isn't ready
□ Screenshots/video captured for PR
□ Tagged with appropriate labels
```

### PR Description Template:
```markdown
## [CHUNK X] Feature Name - UI Implementation

### Screenshots:
![Screenshot 1](link)
![Screenshot 2](link)

### What's Implemented:
- ✅ MVI Contract (State/Events/Actions)
- ✅ ViewModel with action handlers
- ✅ UI Screen with all states
- ✅ Reusable components: [list]
- ✅ Tests: [test coverage]

### UI States Covered:
- ✅ Loading state
- ✅ Success state
- ✅ Error state
- ✅ Empty state

### Accessibility:
- ✅ Content descriptions added
- ✅ TalkBack tested
- ✅ Touch targets are 48dp minimum

### Notes:
[Any important notes or decisions]
```

---

## 🤝 Communication with Kai

### Morning Standup (Async):
```
Template:
"Morning! 🎨
Yesterday: Completed [feature]
Today: Working on [feature]
Waiting for: [Kai's work if blocked]
Blockers: None / [describe if any]
@Kai - [any question or request]"

Example:
"Morning! 🎨
Yesterday: Completed PermissionHandler UI
Today: Starting FileSelectionScreen
Waiting for: FileItem model and MediaRepository interface
Blockers: Blocked until Kai merges domain models
@Kai - Can you let me know when domain PR is merged? Thanks!"
```

### When Kai Notifies You:
```
1. Kai tags you in PR: "@Sokchea - Domain layer ready"
2. You respond: "Thanks! Pulling now and starting UI"
3. Pull latest main immediately
4. Review his models and interfaces
5. Ask questions if anything is unclear
6. Start your UI work
```

### If You Have Questions:
```
❌ Don't guess what a model/function does
✅ Ask Kai directly: "@Kai - What does FileItem.thumbnailUri return?"
✅ Check his KDoc comments
✅ Look at his unit tests for usage examples
✅ Request pair programming if needed
```

---

## 🐛 Testing Strategy

### ViewModel Tests (Your Responsibility):
```kotlin
class FileSelectionViewModelTest {
    private lateinit var getMediaFilesUseCase: GetMediaFilesUseCase
    private lateinit var viewModel: FileSelectionViewModel
    
    @Before
    fun setup() {
        // Mock Kai's use case
        getMediaFilesUseCase = mockk()
        viewModel = FileSelectionViewModel(
            getMediaFilesUseCase,
            Dispatchers.Unconfined
        )
    }
    
    @Test
    fun `when LoadFiles action, state updates with files`() = runTest {
        // Given
        val fakeFiles = listOf(FileItem(...), FileItem(...))
        coEvery { getMediaFilesUseCase(any()) } returns Result.Success(fakeFiles)
        
        // When
        viewModel.handleAction(Action.LoadFiles)
        
        // Then
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(fakeFiles, state.files)
    }
    
    @Test
    fun `when ToggleSelection, file is added to selection`() {
        // Test selection logic
    }
}
```

### Compose UI Tests:
```kotlin
class FileSelectionScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun whenLoadingState_showsProgressIndicator() {
        // Given
        val viewModel = FakeFileSelectionViewModel(
            state = State(isLoading = true)
        )
        
        // When
        composeTestRule.setContent {
            FileSelectionScreen(viewModel = viewModel)
        }
        
        // Then
        composeTestRule
            .onNodeWithTag("LoadingIndicator")
            .assertIsDisplayed()
    }
}
```

### Preview Functions (Always Include):
```kotlin
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FileSelectionScreenPreview() {
    ConversionTheme {
        FileSelectionScreen(
            viewModel = FakeFileSelectionViewModel()
        )
    }
}
```

---

## 🚀 Quick Command Reference

```bash
# Build the project
./gradlew build

# Run ViewModel tests
./gradlew testDebugUnitTest

# Run UI tests
./gradlew connectedAndroidTest

# Check code style
./gradlew ktlintCheck

# Create branch
git checkout -b feature/chunk-X-ui

# Rebase on main
git checkout sokchea-dev
git pull origin main --rebase
git checkout feature/chunk-X-ui
git rebase sokchea-dev

# Push and create PR
git push origin feature/chunk-X-ui
```

---

## 🎨 Design Resources

### Material 3 Components:
- [Material 3 Compose Catalog](https://developer.android.com/jetpack/compose/designsystems/material3)
- [Material Design 3](https://m3.material.io/)
- [Color System](https://m3.material.io/styles/color/the-color-system/color-roles)

### Compose Documentation:
- [Compose Basics](https://developer.android.com/jetpack/compose/tutorial)
- [State Management](https://developer.android.com/jetpack/compose/state)
- [Layouts](https://developer.android.com/jetpack/compose/layouts/basics)
- [Lists and Grids](https://developer.android.com/jetpack/compose/lists)
- [Navigation](https://developer.android.com/jetpack/compose/navigation)

### Animation:
- [Compose Animation](https://developer.android.com/jetpack/compose/animation)
- [Motion in Material 3](https://m3.material.io/styles/motion/overview)

---

## 💡 Tips for Success

### UI Best Practices:
1. **Always handle all states:** Loading, Success, Error, Empty
2. **Use preview functions:** Test UI without running the app
3. **Accessibility first:** Add content descriptions immediately
4. **48dp touch targets:** Minimum size for clickable elements
5. **Consistent spacing:** Use Material spacing (4dp, 8dp, 16dp, 24dp)
6. **Smooth animations:** Use AnimatedVisibility, animateDpAsState, etc.
7. **Error messages:** User-friendly, not technical
8. **Loading states:** Use skeletons or shimmer effects
9. **Test both themes:** Light and dark mode
10. **Responsive layouts:** Work on different screen sizes

### ViewModel Best Practices:
1. **Use fake data during development:** Don't wait for Kai's data layer
2. **Mock use cases in tests:** Test ViewModel logic independently
3. **Handle all action types:** Implement every action in sealed class
4. **Update state immutably:** Use `copy()` function
5. **Send events for navigation:** Don't navigate from ViewModel
6. **Collect events once:** In LaunchedEffect with Unit key
7. **Use computed properties:** Derive state instead of storing duplicates
8. **Keep ViewModels thin:** Move complex logic to use cases (ask Kai)

### Working with Kai:
1. **Review his models carefully:** Understand what data you'll display
2. **Ask questions early:** Don't assume API behavior
3. **Use his interfaces:** Even if implementation isn't ready
4. **Provide feedback:** If an API is hard to use, tell him
5. **Write usage examples:** Show him how you're using his APIs
6. **Pair program when needed:** Complex integration? Work together
7. **Review his PRs:** Catch issues early from UI perspective

---

## 🎯 Your Success Metrics

### For Each Chunk:
```
□ UI looks professional (follows Material 3)
□ All states handled (loading/success/error/empty)
□ Smooth animations (60 FPS)
□ Accessible (TalkBack works)
□ Works in both themes (light/dark)
□ Responsive to different screen sizes
□ No hardcoded strings
□ ViewModel tests pass
□ Screenshots/videos captured
□ Kai approves integration
```

---

## 📚 Learning Resources

### Jetpack Compose:
- [Compose Pathway](https://developer.android.com/courses/pathways/compose)
- [Compose Samples](https://github.com/android/compose-samples)
- [Now in Android App](https://github.com/android/nowinandroid) - Best practices example

### Material Design 3:
- [Material 3 Guidelines](https://m3.material.io/)
- [Compose Material 3 Catalog](https://github.com/material-components/material-components-android-compose-theme-adapter)

### Testing:
- [Testing in Compose](https://developer.android.com/jetpack/compose/testing)
- [Testing ViewModels](https://developer.android.com/codelabs/advanced-android-kotlin-training-testing-basics)

---

**Remember:** You're creating the face of the app that users will interact with. Make it beautiful and intuitive! ✨

**Questions?** Check WORK_DIVISION.md, KAI_TASKS.md, or ask Kai directly.

**Last Updated:** November 21, 2025
