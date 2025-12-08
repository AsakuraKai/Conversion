# ADR 002: MVI Pattern for State Management

**Date:** December 8, 2025  
**Status:** Accepted  
**Deciders:** Sokchea (UI), Kai (Backend)

## Context

The app requires predictable state management for complex UI flows like:
- File selection with multiple filters
- Batch rename with progress tracking
- Real-time folder monitoring
- Multi-step workflows with error handling

Traditional MVVM with LiveData can lead to:
- Mutable state issues
- Difficult-to-trace state changes
- Race conditions with multiple events
- Complex error state management

## Decision

We will implement **Model-View-Intent (MVI)** pattern with:
- **Immutable State**: Single source of truth with data classes
- **Unidirectional Data Flow**: Events → ViewModel → State → UI
- **StateFlow**: For reactive state observation
- **Sealed Classes**: For type-safe events and states

## Architecture

```kotlin
// 1. UI State (Immutable)
data class FileSelectionState(
    val files: List<FileItem> = emptyList(),
    val selectedFiles: Set<FileItem> = emptySet(),
    val filter: FileFilter = FileFilter.DEFAULT,
    val isLoading: Boolean = false,
    val error: String? = null
)

// 2. UI Events (Sealed Class)
sealed class FileSelectionEvent {
    data class LoadFiles(val filter: FileFilter) : FileSelectionEvent()
    data class SelectFile(val file: FileItem) : FileSelectionEvent()
    data class DeselectFile(val file: FileItem) : FileSelectionEvent()
    object ClearSelection : FileSelectionEvent()
}

// 3. ViewModel
class FileSelectionViewModel @Inject constructor(
    private val getMediaFilesUseCase: GetMediaFilesUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(FileSelectionState())
    val state: StateFlow<FileSelectionState> = _state.asStateFlow()
    
    fun onEvent(event: FileSelectionEvent) {
        when (event) {
            is FileSelectionEvent.LoadFiles -> loadFiles(event.filter)
            is FileSelectionEvent.SelectFile -> selectFile(event.file)
            is FileSelectionEvent.DeselectFile -> deselectFile(event.file)
            FileSelectionEvent.ClearSelection -> clearSelection()
        }
    }
    
    private fun loadFiles(filter: FileFilter) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = getMediaFilesUseCase(filter)) {
                is Result.Success -> _state.update {
                    it.copy(files = result.data, isLoading = false)
                }
                is Result.Error -> _state.update {
                    it.copy(error = result.message, isLoading = false)
                }
                is Result.Loading -> { /* handled above */ }
            }
        }
    }
    
    private fun selectFile(file: FileItem) {
        _state.update { it.copy(selectedFiles = it.selectedFiles + file) }
    }
}

// 4. UI (Compose)
@Composable
fun FileSelectionScreen(viewModel: FileSelectionViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.onEvent(FileSelectionEvent.LoadFiles(FileFilter.DEFAULT))
    }
    
    Column {
        if (state.isLoading) {
            CircularProgressIndicator()
        }
        
        LazyColumn {
            items(state.files) { file ->
                FileItem(
                    file = file,
                    isSelected = file in state.selectedFiles,
                    onSelect = { viewModel.onEvent(FileSelectionEvent.SelectFile(file)) }
                )
            }
        }
        
        state.error?.let { error ->
            ErrorMessage(text = error)
        }
    }
}
```

## Benefits

✅ **Predictable State Changes**: Single immutable state makes debugging easy  
✅ **Testability**: Pure functions for state transitions  
✅ **Type Safety**: Sealed classes ensure all events are handled  
✅ **Time Travel Debugging**: Can replay events to reproduce bugs  
✅ **No Race Conditions**: StateFlow ensures sequential state updates  
✅ **Clear Contract**: UI and ViewModel have explicit interfaces

## Trade-offs

⚠️ **More Boilerplate**: Need to define State, Event, and Effect classes  
⚠️ **Learning Curve**: Developers need to understand unidirectional flow  
✅ **Mitigated**: Clear patterns and examples in codebase

## Implementation Guidelines

### 1. State Design
```kotlin
// ✅ Good: Immutable, clear properties
data class MyState(
    val data: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// ❌ Bad: Mutable properties
data class MyState(
    var data: MutableList<Item>,
    var isLoading: Boolean
)
```

### 2. Event Handling
```kotlin
// ✅ Good: Exhaustive when expression
fun onEvent(event: MyEvent) {
    when (event) {
        is MyEvent.Load -> load()
        is MyEvent.Refresh -> refresh()
        MyEvent.Clear -> clear()
    }
}

// ❌ Bad: Non-exhaustive, easy to miss cases
fun onEvent(event: MyEvent) {
    if (event is MyEvent.Load) load()
}
```

### 3. State Updates
```kotlin
// ✅ Good: Copy with modifications
_state.update { it.copy(isLoading = true) }

// ❌ Bad: Direct assignment
_state.value = _state.value.copy(isLoading = true)
```

## Testing

```kotlin
class FileSelectionViewModelTest {
    @Test
    fun `selecting file adds to selected set`() = runTest {
        val viewModel = FileSelectionViewModel(mockUseCase)
        val file = FileItem(id = 1, ...)
        
        viewModel.onEvent(FileSelectionEvent.SelectFile(file))
        
        val state = viewModel.state.value
        assertTrue(file in state.selectedFiles)
    }
}
```

## Related ADRs
- ADR 001: Clean Architecture
- ADR 003: Repository Pattern

## References
- [MVI Architecture Pattern](https://hannesdorfmann.com/android/mosby3-mvi-1)
- [StateFlow Best Practices](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)
