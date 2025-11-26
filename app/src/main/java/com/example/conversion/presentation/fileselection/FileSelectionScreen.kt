package com.example.conversion.presentation.fileselection

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.conversion.domain.model.FileFilter
import com.example.conversion.domain.model.FileItem
import com.example.conversion.ui.components.FileGridItem
import com.example.conversion.ui.theme.ConversionTheme

/**
 * File selection screen for choosing media files to rename.
 * Displays files in a grid with selection capability.
 *
 * @param onNavigateToRename Callback when user confirms selection and proceeds to rename
 * @param onNavigateBack Callback when user navigates back
 * @param viewModel ViewModel for managing file selection state
 */
@Composable
fun FileSelectionScreen(
    onNavigateToRename: (List<FileItem>) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: FileSelectionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is FileSelectionContract.Event.NavigateToRename -> {
                    onNavigateToRename(event.files)
                }
                is FileSelectionContract.Event.ShowMessage -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
                is FileSelectionContract.Event.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = "${event.title}: ${event.message}",
                        duration = SnackbarDuration.Long
                    )
                }
            }
        }
    }

    FileSelectionContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::handleAction,
        onNavigateBack = onNavigateBack
    )
}

/**
 * Content composable for file selection screen.
 */
@Composable
private fun FileSelectionContent(
    state: FileSelectionContract.State,
    snackbarHostState: SnackbarHostState,
    onAction: (FileSelectionContract.Action) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            FileSelectionTopBar(
                selectedCount = state.selectedCount,
                hasSelection = state.hasSelection,
                areAllSelected = state.areAllSelected,
                onNavigateBack = onNavigateBack,
                onSelectAll = { onAction(FileSelectionContract.Action.SelectAll) },
                onClearSelection = { onAction(FileSelectionContract.Action.ClearSelection) }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = state.hasSelection,
                enter = slideInVertically(initialOffsetY = { it * 2 }),
                exit = slideOutVertically(targetOffsetY = { it * 2 })
            ) {
                ExtendedFloatingActionButton(
                    onClick = { onAction(FileSelectionContract.Action.ConfirmSelection) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.DriveFileRenameOutline,
                            contentDescription = null
                        )
                    },
                    text = {
                        Text("Rename ${state.selectedCount} file${if (state.selectedCount != 1) "s" else ""}")
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> LoadingState()
                state.error != null -> ErrorState(
                    error = state.error,
                    onRetry = { onAction(FileSelectionContract.Action.LoadFiles) },
                    onDismiss = { onAction(FileSelectionContract.Action.ClearError) }
                )
                state.isEmpty -> EmptyState(
                    onRefresh = { onAction(FileSelectionContract.Action.RefreshFiles) }
                )
                state.canShowContent -> FileGridContent(
                    files = state.files,
                    selectedFiles = state.selectedFiles,
                    onFileClick = { file ->
                        onAction(FileSelectionContract.Action.ToggleSelection(file))
                    }
                )
            }
        }
    }
}

/**
 * Top app bar for file selection screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FileSelectionTopBar(
    selectedCount: Int,
    hasSelection: Boolean,
    areAllSelected: Boolean,
    onNavigateBack: () -> Unit,
    onSelectAll: () -> Unit,
    onClearSelection: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = if (hasSelection) "$selectedCount selected" else "Select Files"
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            if (hasSelection) {
                IconButton(onClick = onClearSelection) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear selection"
                    )
                }
            }
            IconButton(
                onClick = if (areAllSelected) onClearSelection else onSelectAll
            ) {
                Icon(
                    imageVector = if (areAllSelected) {
                        Icons.Default.Deselect
                    } else {
                        Icons.Default.SelectAll
                    },
                    contentDescription = if (areAllSelected) "Deselect all" else "Select all"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = if (hasSelection) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            },
            titleContentColor = if (hasSelection) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    )
}

/**
 * Grid content displaying files.
 */
@Composable
private fun FileGridContent(
    files: List<FileItem>,
    selectedFiles: Set<FileItem>,
    onFileClick: (FileItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = files,
            key = { it.id }
        ) { file ->
            FileGridItem(
                file = file,
                isSelected = file in selectedFiles,
                onClick = { onFileClick(file) }
            )
        }
    }
}

/**
 * Loading state indicator.
 */
@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "Loading files...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Empty state when no files are found.
 */
@Composable
private fun EmptyState(
    onRefresh: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ImageNotSupported,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "No media files found",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Make sure you have granted media access permissions and have media files on your device.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Refresh")
            }
        }
    }
}

/**
 * Error state display.
 */
@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Error",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Dismiss")
                    }
                    Button(onClick = onRetry) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}

// Preview functions
@Preview(name = "File Selection - Light")
@Preview(name = "File Selection - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FileSelectionScreenPreview() {
    ConversionTheme {
        Surface {
            FileSelectionContent(
                state = FileSelectionContract.State(
                    files = List(10) { index ->
                        FileItem(
                            id = index.toLong(),
                            uri = Uri.parse("content://media/external/images/$index"),
                            name = "IMG_${String.format("%03d", index)}.jpg",
                            path = "/storage/emulated/0/Pictures/IMG_$index.jpg",
                            size = 1024000L,
                            mimeType = "image/jpeg",
                            dateModified = System.currentTimeMillis(),
                            thumbnailUri = null
                        )
                    },
                    selectedFiles = setOf()
                ),
                snackbarHostState = remember { SnackbarHostState() },
                onAction = {},
                onNavigateBack = {}
            )
        }
    }
}

@Preview(name = "With Selection")
@Composable
private fun FileSelectionWithSelectionPreview() {
    ConversionTheme {
        Surface {
            val files = List(10) { index ->
                FileItem(
                    id = index.toLong(),
                    uri = Uri.parse("content://media/external/images/$index"),
                    name = "IMG_${String.format("%03d", index)}.jpg",
                    path = "/storage/emulated/0/Pictures/IMG_$index.jpg",
                    size = 1024000L,
                    mimeType = "image/jpeg",
                    dateModified = System.currentTimeMillis(),
                    thumbnailUri = null
                )
            }
            FileSelectionContent(
                state = FileSelectionContract.State(
                    files = files,
                    selectedFiles = setOf(files[0], files[2], files[5])
                ),
                snackbarHostState = remember { SnackbarHostState() },
                onAction = {},
                onNavigateBack = {}
            )
        }
    }
}

@Preview(name = "Loading State")
@Composable
private fun LoadingStatePreview() {
    ConversionTheme {
        Surface {
            FileSelectionContent(
                state = FileSelectionContract.State(isLoading = true),
                snackbarHostState = remember { SnackbarHostState() },
                onAction = {},
                onNavigateBack = {}
            )
        }
    }
}

@Preview(name = "Empty State")
@Composable
private fun EmptyStatePreview() {
    ConversionTheme {
        Surface {
            FileSelectionContent(
                state = FileSelectionContract.State(
                    files = emptyList(),
                    isLoading = false
                ),
                snackbarHostState = remember { SnackbarHostState() },
                onAction = {},
                onNavigateBack = {}
            )
        }
    }
}

@Preview(name = "Error State")
@Composable
private fun ErrorStatePreview() {
    ConversionTheme {
        Surface {
            FileSelectionContent(
                state = FileSelectionContract.State(
                    error = "Failed to load files. Please check your permissions."
                ),
                snackbarHostState = remember { SnackbarHostState() },
                onAction = {},
                onNavigateBack = {}
            )
        }
    }
}
