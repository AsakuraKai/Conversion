package com.example.conversion.presentation.preview

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
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
import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.model.PreviewItem
import com.example.conversion.domain.model.PreviewSummary
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.presentation.preview.PreviewContract.Action
import com.example.conversion.presentation.preview.PreviewContract.Event
import com.example.conversion.presentation.preview.PreviewContract.State
import com.example.conversion.ui.theme.ConversionTheme

/**
 * Preview Screen.
 * Shows users how files will be renamed before executing the operation.
 */
@Composable
fun PreviewScreen(
    files: List<FileItem>,
    config: RenameConfig,
    viewModel: PreviewViewModel = hiltViewModel(),
    onNavigateToRenameProgress: (List<FileItem>, RenameConfig) -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Initialize preview generation when screen is created
    LaunchedEffect(files, config) {
        viewModel.handleAction(Action.Initialize(files, config))
    }

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
                is Event.NavigateToRenameProgress -> {
                    onNavigateToRenameProgress(event.files, event.config)
                }
                is Event.NavigateBack -> {
                    onNavigateBack()
                }
            }
        }
    }

    PreviewContent(
        state = state,
        onAction = viewModel::handleAction,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreviewContent(
    state: State,
    onAction: (Action) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rename Preview") },
                navigationIcon = {
                    IconButton(onClick = { onAction(Action.Back) }) {
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
        bottomBar = {
            if (state is State.Success) {
                PreviewBottomBar(
                    summary = state.summary,
                    canProceed = state.canProceed,
                    onConfirm = { onAction(Action.ConfirmRename) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is State.Loading -> {
                    LoadingContent()
                }
                is State.Success -> {
                    SuccessContent(
                        previews = state.previews,
                        summary = state.summary
                    )
                }
                is State.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = { onAction(Action.Retry) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
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
                text = "Generating preview...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun SuccessContent(
    previews: List<PreviewItem>,
    summary: PreviewSummary
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Summary header
        PreviewSummaryCard(summary = summary)

        // Preview list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(previews, key = { it.original.id }) { preview ->
                PreviewItemCard(preview = preview)
            }
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
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
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = "Error",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun PreviewSummaryCard(summary: PreviewSummary) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                summary.conflicts > 0 -> MaterialTheme.colorScheme.errorContainer
                summary.validRenames == 0 -> MaterialTheme.colorScheme.surfaceVariant
                else -> MaterialTheme.colorScheme.primaryContainer
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = summary.message,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryItem(
                    label = "Total Files",
                    value = summary.totalFiles.toString()
                )
                SummaryItem(
                    label = "Ready to Rename",
                    value = summary.validRenames.toString(),
                    color = MaterialTheme.colorScheme.primary
                )
                if (summary.conflicts > 0) {
                    SummaryItem(
                        label = "Conflicts",
                        value = summary.conflicts.toString(),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                if (summary.unchanged > 0) {
                    SummaryItem(
                        label = "Unchanged",
                        value = summary.unchanged.toString()
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PreviewItemCard(preview: PreviewItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                preview.hasConflict -> MaterialTheme.colorScheme.errorContainer
                !preview.isChanged -> MaterialTheme.colorScheme.surfaceVariant
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status icon
            Icon(
                imageVector = when {
                    preview.hasConflict -> Icons.Default.Warning
                    preview.canRename -> Icons.Default.Check
                    else -> Icons.Default.Warning
                },
                contentDescription = null,
                tint = when {
                    preview.hasConflict -> MaterialTheme.colorScheme.error
                    preview.canRename -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(24.dp)
            )

            // File names
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Original name
                Text(
                    text = preview.original.name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Arrow and preview name
                if (preview.isChanged) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "→",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = preview.previewName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = when {
                                preview.hasConflict -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }

                // Conflict reason or description
                if (preview.hasConflict && preview.conflictReason != null) {
                    Text(
                        text = preview.conflictReason,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                } else if (!preview.isChanged) {
                    Text(
                        text = "No change needed",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun PreviewBottomBar(
    summary: PreviewSummary,
    canProceed: Boolean,
    onConfirm: () -> Unit
) {
    Surface(
        tonalElevation = 3.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = onConfirm,
                enabled = canProceed,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (canProceed) {
                        "Rename ${summary.validRenames} File${if (summary.validRenames != 1) "s" else ""}"
                    } else {
                        "Cannot Proceed"
                    }
                )
            }
            
            if (!canProceed && summary.conflicts > 0) {
                Text(
                    text = "Fix conflicts before proceeding",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

// Preview composables
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun PreviewScreenLoadingPreview() {
    ConversionTheme {
        PreviewContent(
            state = State.Loading,
            onAction = {}
        )
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun PreviewScreenErrorPreview() {
    ConversionTheme {
        PreviewContent(
            state = State.Error("Failed to generate preview: Invalid configuration"),
            onAction = {}
        )
    }
}
