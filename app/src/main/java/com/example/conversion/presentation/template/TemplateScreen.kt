package com.example.conversion.presentation.template

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.model.RenameTemplate
import com.example.conversion.domain.model.SortStrategy
import com.example.conversion.presentation.template.TemplateContract.Action
import com.example.conversion.presentation.template.TemplateContract.Event
import com.example.conversion.ui.theme.ConversionTheme
import java.text.SimpleDateFormat
import java.util.*

/**
 * Template Management Screen.
 * Allows users to save, load, delete, and manage Reusable Templates.
 */
@Composable
fun TemplateScreen(
    viewModel: TemplateViewModel = hiltViewModel(),
    onNavigateBack: (RenameConfig?) -> Unit,
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
                is Event.NavigateBackWithConfig -> {
                    onNavigateBack(event.config)
                }
                is Event.TemplateSaved,
                is Event.TemplateDeleted,
                is Event.TemplateApplied -> {
                    // Already showing messages for these
                }
            }
        }
    }

    // Show save dialog
    if (state.showSaveDialog) {
        SaveTemplateDialog(
            state = state,
            onSave = { name, pattern ->
                state.currentConfig?.let { config ->
                    viewModel.handleAction(Action.SaveTemplate(name, pattern, config))
                }
            },
            onDismiss = { viewModel.handleAction(Action.HideSaveDialog) },
            onNameChange = { viewModel.handleAction(Action.UpdateTemplateName(it)) },
            onPatternChange = { viewModel.handleAction(Action.UpdateTemplatePattern(it)) }
        )
    }

    // Show delete confirmation
    state.templateToDelete?.let { template ->
        DeleteConfirmationDialog(
            template = template,
            onConfirm = { viewModel.handleAction(Action.ConfirmDelete) },
            onDismiss = { viewModel.handleAction(Action.HideDeleteConfirmation) }
        )
    }

    TemplateScreenContent(
        state = state,
        onAction = viewModel::handleAction,
        onBack = onBack,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TemplateScreenContent(
    state: TemplateContract.State,
    onAction: (Action) -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Template Manager") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                actions = {
                    // Filter toggle button
                    IconButton(
                        onClick = { onAction(Action.ToggleFilter) }
                    ) {
                        Icon(
                            imageVector = if (state.filterByFavorites) 
                                Icons.Default.Star 
                            else 
                                Icons.Default.StarBorder,
                            contentDescription = if (state.filterByFavorites)
                                "Show all templates"
                            else
                                "Show only favorites",
                            tint = if (state.filterByFavorites)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
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
            ExtendedFloatingActionButton(
                onClick = { 
                    // For demo, create a sample config
                    val sampleConfig = RenameConfig(
                        prefix = "Photo_",
                        startNumber = 1,
                        digitCount = 3,
                        preserveExtension = true,
                        sortStrategy = SortStrategy.NATURAL
                    )
                    onAction(Action.ShowSaveDialog(sampleConfig))
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Template") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading && !state.hasTemplates -> {
                    // Initial loading state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                !state.hasTemplates -> {
                    // Empty state
                    EmptyTemplateState(
                        onCreateTemplate = {
                            val sampleConfig = RenameConfig(
                                prefix = "Photo_",
                                startNumber = 1,
                                digitCount = 3,
                                preserveExtension = true,
                                sortStrategy = SortStrategy.NATURAL
                            )
                            onAction(Action.ShowSaveDialog(sampleConfig))
                        }
                    )
                }
                
                state.displayedTemplates.isEmpty() -> {
                    // No templates match current filter
                    EmptyFilterState(
                        isFilteringFavorites = state.filterByFavorites,
                        onClearFilter = { onAction(Action.ToggleFilter) }
                    )
                }
                
                else -> {
                    // Template list
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = state.displayedTemplates,
                            key = { it.id }
                        ) { template ->
                            TemplateCard(
                                template = template,
                                onApply = { onAction(Action.ApplyTemplate(template)) },
                                onToggleFavorite = { onAction(Action.ToggleFavorite(template.id)) },
                                onDelete = { onAction(Action.ShowDeleteConfirmation(template)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TemplateCard(
    template: RenameTemplate,
    onApply: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onApply() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header: Title and Favorite icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = template.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (template.isFavorite) 
                            Icons.Default.Star 
                        else 
                            Icons.Default.StarBorder,
                        contentDescription = if (template.isFavorite)
                            "Remove from favorites"
                        else
                            "Add to favorites",
                        tint = if (template.isFavorite)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Pattern preview
            Text(
                text = "Pattern: ${template.pattern}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Configuration details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Prefix: ${template.config.prefix}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Start: ${template.config.startNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Digits: ${template.config.digitCount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Metadata: Created and Last Used
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Created: ${formatTimestamp(template.createdAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                template.lastUsedAt?.let { lastUsed ->
                    Text(
                        text = "Last used: ${formatTimestamp(lastUsed)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onApply,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Apply")
                }
                
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
private fun EmptyTemplateState(
    onCreateTemplate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No Templates Yet",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Save your rename configurations as templates for quick reuse",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onCreateTemplate) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create First Template")
        }
    }
}

@Composable
private fun EmptyFilterState(
    isFilteringFavorites: Boolean,
    onClearFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isFilteringFavorites) Icons.Default.Star else Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = if (isFilteringFavorites) "No Favorite Templates" else "No Templates Found",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = if (isFilteringFavorites) 
                "Mark templates as favorites to see them here"
            else 
                "No templates match the current filter",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onClearFilter) {
            Text(if (isFilteringFavorites) "Show All Templates" else "Clear Filter")
        }
    }
}

@Composable
private fun SaveTemplateDialog(
    state: TemplateContract.State,
    onSave: (String, String) -> Unit,
    onDismiss: () -> Unit,
    onNameChange: (String) -> Unit,
    onPatternChange: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save Template") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = state.newTemplateName,
                    onValueChange = onNameChange,
                    label = { Text("Template Name") },
                    placeholder = { Text("My Rename Template") },
                    isError = state.nameValidationError != null,
                    supportingText = state.nameValidationError?.let { { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = state.newTemplatePattern,
                    onValueChange = onPatternChange,
                    label = { Text("Pattern Description") },
                    placeholder = { Text("Photo_001.jpg") },
                    isError = state.patternValidationError != null,
                    supportingText = state.patternValidationError?.let { { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Show current config preview
                state.currentConfig?.let { config ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Configuration",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Prefix: ${config.prefix}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Start: ${config.startNumber}, Digits: ${config.digitCount}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Sort: ${config.sortStrategy}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(state.newTemplateName, state.newTemplatePattern) },
                enabled = state.canSaveTemplate
            ) {
                Text("Save")
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
private fun DeleteConfirmationDialog(
    template: RenameTemplate,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Delete, contentDescription = null) },
        title = { Text("Delete Template?") },
        text = { 
            Text("Are you sure you want to delete the template '${template.name}'? This action cannot be undone.") 
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
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

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

// ============= Previews =============

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun TemplateScreenPreview() {
    val sampleTemplates = listOf(
        RenameTemplate(
            id = "1",
            name = "Photo Rename",
            pattern = "Photo_001.jpg",
            config = RenameConfig(
                prefix = "Photo_",
                startNumber = 1,
                digitCount = 3,
                preserveExtension = true,
                sortStrategy = SortStrategy.NATURAL
            ),
            isFavorite = true,
            createdAt = System.currentTimeMillis() - 86400000,
            lastUsedAt = System.currentTimeMillis() - 3600000
        ),
        RenameTemplate(
            id = "2",
            name = "Video Batch",
            pattern = "VID_0001.mp4",
            config = RenameConfig(
                prefix = "VID_",
                startNumber = 1,
                digitCount = 4,
                preserveExtension = true,
                sortStrategy = SortStrategy.DATE_MODIFIED
            ),
            isFavorite = false,
            createdAt = System.currentTimeMillis() - 172800000,
            lastUsedAt = null
        )
    )

    ConversionTheme {
        TemplateScreenContent(
            state = TemplateContract.State(
                templates = sampleTemplates,
                favoriteTemplates = sampleTemplates.filter { it.isFavorite }
            ),
            onAction = {},
            onBack = {}
        )
    }
}

@Preview(name = "Empty State", showBackground = true)
@Composable
private fun EmptyTemplateStatePreview() {
    ConversionTheme {
        TemplateScreenContent(
            state = TemplateContract.State(),
            onAction = {},
            onBack = {}
        )
    }
}

@Preview(name = "Save Dialog", showBackground = true)
@Composable
private fun SaveTemplateDialogPreview() {
    ConversionTheme {
        SaveTemplateDialog(
            state = TemplateContract.State(
                newTemplateName = "My Template",
                newTemplatePattern = "Photo_001.jpg",
                currentConfig = RenameConfig(
                    prefix = "Photo_",
                    startNumber = 1,
                    digitCount = 3,
                    preserveExtension = true,
                    sortStrategy = SortStrategy.NATURAL
                )
            ),
            onSave = { _, _ -> },
            onDismiss = {},
            onNameChange = {},
            onPatternChange = {}
        )
    }
}
