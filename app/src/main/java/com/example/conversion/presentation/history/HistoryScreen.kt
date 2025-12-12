package com.example.conversion.presentation.history

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.conversion.domain.model.OperationHistory
import com.example.conversion.domain.model.RenameOperation
import com.example.conversion.presentation.history.HistoryContract.Action
import com.example.conversion.presentation.history.HistoryContract.Event
import com.example.conversion.presentation.history.components.HistoryOperationItem
import com.example.conversion.ui.theme.ConversionTheme
import java.text.SimpleDateFormat
import java.util.*

/**
 * History Management Screen.
 * Displays rename operation history with undo/redo functionality.
 */
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is Event.OperationUndone -> {
                    // Handled by ShowSuccess
                }
                is Event.OperationRedone -> {
                    // Handled by ShowSuccess
                }
                is Event.HistoryCleared -> {
                    // Handled by ShowSuccess
                }
                is Event.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Long
                    )
                }
                is Event.ShowSuccess -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    // Show clear confirmation dialog
    if (state.showClearConfirmation) {
        ClearHistoryConfirmationDialog(
            onConfirm = { viewModel.handleAction(Action.ConfirmClear) },
            onDismiss = { viewModel.handleAction(Action.HideClearConfirmation) }
        )
    }

    HistoryScreenContent(
        state = state,
        onAction = viewModel::handleAction,
        onBack = onBack,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryScreenContent(
    state: HistoryContract.State,
    onAction: (Action) -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                actions = {
                    // Undo button
                    IconButton(
                        onClick = { onAction(Action.Undo) },
                        enabled = state.canUndo
                    ) {
                        Icon(
                            imageVector = Icons.Default.Undo,
                            contentDescription = "Undo last operation",
                            tint = if (state.canUndo)
                                MaterialTheme.colorScheme.onSurface
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    }
                    // Redo button
                    IconButton(
                        onClick = { onAction(Action.Redo) },
                        enabled = state.canRedo
                    ) {
                        Icon(
                            imageVector = Icons.Default.Redo,
                            contentDescription = "Redo operation",
                            tint = if (state.canRedo)
                                MaterialTheme.colorScheme.onSurface
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    }
                    // Clear history button
                    IconButton(
                        onClick = { onAction(Action.ShowClearConfirmation) },
                        enabled = state.hasOperations
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear all history",
                            tint = if (state.hasOperations)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                !state.hasOperations -> {
                    EmptyHistoryState(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    HistoryList(
                        state = state,
                        onAction = onAction,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HistoryList(
    state: HistoryContract.State,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Show current position indicator
        item {
            CurrentPositionIndicator(
                canUndo = state.canUndo,
                canRedo = state.canRedo,
                totalOperations = state.displayedOperations.size
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(
            items = state.displayedOperations,
            key = { it.id }
        ) { operation ->
            val isCurrentOperation = state.nextUndoOperation?.id == operation.id
            
            HistoryOperationItem(
                operation = operation,
                isCurrentOperation = isCurrentOperation,
                onUndo = { onAction(Action.UndoSpecific(operation.id)) },
                onClick = { onAction(Action.SelectOperation(operation)) },
                modifier = Modifier.animateItemPlacement()
            )
        }
    }
}

@Composable
private fun CurrentPositionIndicator(
    canUndo: Boolean,
    canRedo: Boolean,
    totalOperations: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "History Status",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$totalOperations operation${if (totalOperations != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusChip(
                    label = "Undo",
                    enabled = canUndo,
                    icon = Icons.Default.Undo
                )
                StatusChip(
                    label = "Redo",
                    enabled = canRedo,
                    icon = Icons.Default.Redo
                )
            }
        }
    }
}

@Composable
private fun StatusChip(
    label: String,
    enabled: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = { },
        label = { Text(label) },
        enabled = enabled,
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        },
        modifier = modifier
    )
}

@Composable
private fun EmptyHistoryState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            modifier = Modifier.size(96.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No History Yet",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Rename operations will appear here",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    }
}

@Composable
private fun ClearHistoryConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Clear All History?") },
        text = {
            Text("This will permanently delete all rename operation history. This action cannot be undone.")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Clear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Preview
@Preview(name = "History Screen - Light")
@Preview(name = "History Screen - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HistoryScreenPreview() {
    ConversionTheme {
        Surface {
            HistoryScreenContent(
                state = HistoryContract.State(
                    history = OperationHistory(
                        operations = listOf(
                            RenameOperation.create(
                                id = "1",
                                originalUri = android.net.Uri.parse("file:///test1.jpg"),
                                newUri = android.net.Uri.parse("file:///IMG_001.jpg"),
                                originalName = "test1.jpg",
                                newName = "IMG_001.jpg"
                            ),
                            RenameOperation.create(
                                id = "2",
                                originalUri = android.net.Uri.parse("file:///test2.jpg"),
                                newUri = android.net.Uri.parse("file:///IMG_002.jpg"),
                                originalName = "test2.jpg",
                                newName = "IMG_002.jpg"
                            )
                        ),
                        currentIndex = 1
                    )
                ),
                onAction = {},
                onBack = {}
            )
        }
    }
}

@Preview(name = "Empty History - Light")
@Preview(name = "Empty History - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun EmptyHistoryPreview() {
    ConversionTheme {
        Surface {
            HistoryScreenContent(
                state = HistoryContract.State(),
                onAction = {},
                onBack = {}
            )
        }
    }
}
