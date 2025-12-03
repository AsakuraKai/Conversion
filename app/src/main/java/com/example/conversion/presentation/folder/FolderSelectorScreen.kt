package com.example.conversion.presentation.folder

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.conversion.domain.model.FolderInfo
import com.example.conversion.presentation.folder.FolderSelectorContract.Action
import com.example.conversion.presentation.folder.FolderSelectorContract.Event
import com.example.conversion.presentation.folder.FolderSelectorContract.State
import com.example.conversion.presentation.folder.components.CreateFolderDialog
import com.example.conversion.presentation.folder.components.EmptyFolderState
import com.example.conversion.presentation.folder.components.FolderListItem
import com.example.conversion.ui.theme.ConversionTheme

/**
 * Folder Selector Screen
 * 
 * Allows users to browse folders, navigate folder hierarchy, and select a destination folder.
 * Supports creating new folders via a dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderSelectorScreen(
    viewModel: FolderSelectorViewModel = hiltViewModel(),
    onFolderSelected: (FolderInfo) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Handle one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is Event.NavigateToFolder -> {
                    // Folder navigation handled by ViewModel
                }
                is Event.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is Event.FolderCreated -> {
                    snackbarHostState.showSnackbar("Folder '${event.folder.name}' created")
                }
                is Event.FolderSelected -> {
                    onFolderSelected(event.folder)
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Destination Folder") },
                navigationIcon = {
                    if (state.canNavigateUp) {
                        IconButton(onClick = { viewModel.handleAction(Action.NavigateUp) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Navigate up"
                            )
                        }
                    } else {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home"
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Create folder FAB - only show when not at root
                if (state.currentPath != null) {
                    FloatingActionButton(
                        onClick = { viewModel.handleAction(Action.ShowCreateFolderDialog) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreateNewFolder,
                            contentDescription = "Create folder"
                        )
                    }
                }
                
                // Select current folder FAB - only show when not at root
                if (state.currentPath != null) {
                    ExtendedFloatingActionButton(
                        text = { Text("Select This Folder") },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null
                            )
                        },
                        onClick = { viewModel.handleAction(Action.ConfirmSelection) }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Breadcrumb navigation
            BreadcrumbNavigation(
                displayPath = state.displayPath,
                breadcrumbs = state.breadcrumbs,
                onNavigateToRoot = { viewModel.handleAction(Action.LoadRootFolders) }
            )
            
            // Content area
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    state.isLoading -> {
                        LoadingIndicator()
                    }
                    state.error != null -> {
                        ErrorState(
                            error = state.error!!,
                            onRetry = {
                                if (state.currentPath == null) {
                                    viewModel.handleAction(Action.LoadRootFolders)
                                } else {
                                    viewModel.handleAction(Action.LoadFolders(state.currentPath!!))
                                }
                            }
                        )
                    }
                    state.isEmpty -> {
                        EmptyFolderState(
                            onCreateFolder = { viewModel.handleAction(Action.ShowCreateFolderDialog) },
                            canCreateFolder = state.currentPath != null
                        )
                    }
                    else -> {
                        FolderList(
                            folders = state.folders,
                            selectedFolder = state.selectedFolder,
                            onFolderClick = { folder ->
                                viewModel.handleAction(Action.NavigateToFolder(folder))
                            },
                            onFolderLongClick = { folder ->
                                viewModel.handleAction(Action.SelectFolder(folder))
                            }
                        )
                    }
                }
            }
        }
    }
    
    // Create folder dialog
    if (state.showCreateDialog) {
        CreateFolderDialog(
            onDismiss = { viewModel.handleAction(Action.HideCreateFolderDialog) },
            onCreate = { name ->
                viewModel.handleAction(Action.CreateFolder(name))
            }
        )
    }
}

/**
 * Breadcrumb navigation component
 */
@Composable
private fun BreadcrumbNavigation(
    displayPath: String,
    breadcrumbs: List<String>,
    onNavigateToRoot: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = displayPath,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Folder list component
 */
@Composable
private fun FolderList(
    folders: List<FolderInfo>,
    selectedFolder: FolderInfo?,
    onFolderClick: (FolderInfo) -> Unit,
    onFolderLongClick: (FolderInfo) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(folders, key = { it.path }) { folder ->
            FolderListItem(
                folder = folder,
                isSelected = folder.path == selectedFolder?.path,
                onClick = { onFolderClick(folder) },
                onLongClick = { onFolderLongClick(folder) }
            )
        }
    }
}

/**
 * Loading indicator
 */
@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Error state
 */
@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.padding(16.dp))
        TextButton(onClick = onRetry) {
            Text("Retry")
        }
    }
}

// Previews
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FolderSelectorScreenPreview() {
    ConversionTheme {
        // Preview with mock data would go here
        // Requires fake ViewModel for preview
    }
}
