package com.example.conversion.presentation.tag

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.conversion.domain.model.FileTag
import com.example.conversion.presentation.tag.TagContract.Action
import com.example.conversion.presentation.tag.TagContract.Event
import com.example.conversion.ui.theme.ConversionTheme

/**
 * Tag Management Screen.
 * Allows users to create, edit, delete, and manage file tags.
 */
@Composable
fun TagManagementScreen(
    viewModel: TagViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is Event.ShowMessage -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
                is Event.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Long
                    )
                }
                is Event.TagCreated,
                is Event.TagUpdated,
                is Event.TagDeleted -> {
                    // Already showing messages for these
                }
            }
        }
    }

    // Show create dialog
    if (state.showCreateDialog) {
        CreateTagDialog(
            state = state,
            onCreate = { name, color ->
                viewModel.handleAction(Action.CreateTag(name, color))
            },
            onDismiss = { viewModel.handleAction(Action.HideCreateDialog) },
            onNameChange = { viewModel.handleAction(Action.UpdateTagName(it)) },
            onColorChange = { viewModel.handleAction(Action.UpdateSelectedColor(it)) }
        )
    }

    // Show edit dialog
    if (state.showEditDialog && state.editingTag != null) {
        EditTagDialog(
            state = state,
            tag = state.editingTag!!,
            onUpdate = { tag ->
                viewModel.handleAction(Action.UpdateTag(tag))
            },
            onDismiss = { viewModel.handleAction(Action.HideEditDialog) },
            onNameChange = { viewModel.handleAction(Action.UpdateTagName(it)) },
            onColorChange = { viewModel.handleAction(Action.UpdateSelectedColor(it)) }
        )
    }

    // Show delete confirmation
    if (state.showDeleteConfirmation && state.tagToDelete != null) {
        DeleteTagConfirmationDialog(
            tag = state.tagToDelete!!,
            onConfirm = { viewModel.handleAction(Action.ConfirmDelete) },
            onDismiss = { viewModel.handleAction(Action.HideDeleteConfirmation) }
        )
    }

    TagManagementScreenContent(
        state = state,
        onAction = viewModel::handleAction,
        onBack = onBack,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TagManagementScreenContent(
    state: TagContract.State,
    onAction: (Action) -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tag Management") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAction(Action.ShowCreateDialog) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create new tag")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Search bar
            if (state.hasTags) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { onAction(Action.UpdateSearchQuery(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search tags...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onAction(Action.UpdateSearchQuery("")) }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear search")
                            }
                        }
                    },
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                state.isLoading && !state.hasTags -> {
                    // Initial loading state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                !state.hasTags -> {
                    // Empty state
                    EmptyTagState(
                        onCreateTag = { onAction(Action.ShowCreateDialog) }
                    )
                }
                
                state.filteredTags.isEmpty() -> {
                    // No search results
                    EmptySearchState()
                }
                
                else -> {
                    // Tag list
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = state.filteredTags,
                            key = { it.id }
                        ) { tag ->
                            TagItem(
                                tag = tag,
                                onEdit = { onAction(Action.ShowEditDialog(tag)) },
                                onDelete = { onAction(Action.ShowDeleteConfirmation(tag)) }
                            )
                        }
                        
                        // Bottom spacing for FAB
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TagItem(
    tag: FileTag,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color indicator
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color(android.graphics.Color.parseColor(tag.color)),
                        shape = CircleShape
                    )
            )

            // Tag name
            Text(
                text = tag.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            // Edit button
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit tag",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Delete button
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete tag",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun EmptyTagState(
    onCreateTag: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No tags yet",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Create tags to organize your files",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onCreateTag) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create First Tag")
        }
    }
}

@Composable
private fun EmptySearchState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No matching tags",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun CreateTagDialog(
    state: TagContract.State,
    onCreate: (String, String) -> Unit,
    onDismiss: () -> Unit,
    onNameChange: (String) -> Unit,
    onColorChange: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Tag") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tag name input
                OutlinedTextField(
                    value = state.newTagName,
                    onValueChange = onNameChange,
                    label = { Text("Tag Name") },
                    isError = state.nameValidationError != null,
                    supportingText = state.nameValidationError?.let { { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Color selection
                Text(
                    text = "Select Color",
                    style = MaterialTheme.typography.labelMedium
                )
                
                ColorPicker(
                    selectedColor = state.selectedColor,
                    onColorSelect = onColorChange
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onCreate(state.newTagName, state.selectedColor) },
                enabled = state.canSaveTag
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun EditTagDialog(
    state: TagContract.State,
    tag: FileTag,
    onUpdate: (FileTag) -> Unit,
    onDismiss: () -> Unit,
    onNameChange: (String) -> Unit,
    onColorChange: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Tag") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tag name input
                OutlinedTextField(
                    value = state.newTagName,
                    onValueChange = onNameChange,
                    label = { Text("Tag Name") },
                    isError = state.nameValidationError != null,
                    supportingText = state.nameValidationError?.let { { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Color selection
                Text(
                    text = "Select Color",
                    style = MaterialTheme.typography.labelMedium
                )
                
                ColorPicker(
                    selectedColor = state.selectedColor,
                    onColorSelect = onColorChange
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { 
                    onUpdate(tag.copy(name = state.newTagName, color = state.selectedColor))
                },
                enabled = state.canSaveTag
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun DeleteTagConfirmationDialog(
    tag: FileTag,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = { Text("Delete Tag?") },
        text = {
            Text("Are you sure you want to delete the tag '${tag.name}'? This action cannot be undone.")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ColorPicker(
    selectedColor: String,
    onColorSelect: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FileTag.PREDEFINED_COLORS.chunked(4).forEach { rowColors ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowColors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = Color(android.graphics.Color.parseColor(color)),
                                shape = CircleShape
                            )
                            .clickable { onColorSelect(color) }
                            .then(
                                if (color == selectedColor) {
                                    Modifier.padding(4.dp)
                                } else {
                                    Modifier
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (color == selectedColor) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TagManagementScreenPreview() {
    ConversionTheme {
        Surface {
            TagManagementScreenContent(
                state = TagContract.State(
                    tags = listOf(
                        FileTag("1", "Work", "#2196F3", System.currentTimeMillis()),
                        FileTag("2", "Personal", "#4CAF50", System.currentTimeMillis()),
                        FileTag("3", "Important", "#F44336", System.currentTimeMillis())
                    )
                ),
                onAction = {},
                onBack = {}
            )
        }
    }
}

@Preview(name = "Empty State")
@Composable
private fun EmptyTagStatePreview() {
    ConversionTheme {
        Surface {
            EmptyTagState(onCreateTag = {})
        }
    }
}
