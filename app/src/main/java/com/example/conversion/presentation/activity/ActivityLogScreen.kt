package com.example.conversion.presentation.activity

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.conversion.domain.model.ActivityLog
import com.example.conversion.domain.model.ActivityStatus
import com.example.conversion.presentation.activity.ActivityLogContract.Action
import com.example.conversion.presentation.activity.ActivityLogContract.Event
import com.example.conversion.presentation.activity.components.ExportFormatDialog
import com.example.conversion.presentation.activity.components.LogFilterDialog
import java.time.format.DateTimeFormatter

/**
 * Activity Log Screen.
 * Displays activity logs with filtering and export functionality.
 */
@Composable
fun ActivityLogScreen(
    viewModel: ActivityLogViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is Event.LogsExported -> {
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
                is Event.FiltersApplied -> {
                    // Handled by ShowSuccess
                }
                is Event.FiltersCleared -> {
                    // Handled by ShowSuccess
                }
            }
        }
    }

    // Show filter dialog
    if (state.showFilterDialog) {
        LogFilterDialog(
            currentStartDate = state.filterStartDate,
            currentEndDate = state.filterEndDate,
            currentStatus = state.filterStatus,
            currentAction = state.filterAction,
            onApplyFilters = { startDate, endDate, status, action ->
                viewModel.handleAction(
                    Action.ApplyFilters(
                        startDate = startDate,
                        endDate = endDate,
                        status = status,
                        action = action
                    )
                )
            },
            onDismiss = { viewModel.handleAction(Action.HideFilterDialog) }
        )
    }

    // Show export dialog
    if (state.showExportDialog) {
        ExportFormatDialog(
            onExport = { format ->
                viewModel.handleAction(Action.ExportLogs(format))
            },
            onDismiss = { viewModel.handleAction(Action.HideExportDialog) }
        )
    }

    ActivityLogScreenContent(
        state = state,
        onAction = viewModel::handleAction,
        onBack = onBack,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActivityLogScreenContent(
    state: ActivityLogContract.State,
    onAction: (Action) -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Activity Log") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                actions = {
                    // Search button
                    IconButton(onClick = { /* Search functionality could be added */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search logs"
                        )
                    }
                    // Filter button
                    IconButton(onClick = { onAction(Action.ShowFilterDialog) }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter logs",
                            tint = if (state.hasActiveFilters)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                    // Export button
                    IconButton(
                        onClick = { onAction(Action.ShowExportDialog) },
                        enabled = state.hasLogs
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Export logs"
                        )
                    }
                    // Refresh button
                    IconButton(onClick = { onAction(Action.RefreshLogs) }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh logs"
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
                !state.hasLogs -> {
                    EmptyLogsState(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Filter summary
                        if (state.hasActiveFilters) {
                            FilterSummaryBar(
                                state = state,
                                onClearFilters = { onAction(Action.ClearFilters) }
                            )
                        }
                        
                        // Log list
                        ActivityLogList(
                            state = state,
                            onAction = onAction,
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ActivityLogList(
    state: ActivityLogContract.State,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Log count header
        item {
            Text(
                text = "${state.displayedLogCount} log(s)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(
            items = state.displayedLogs,
            key = { it.id }
        ) { log ->
            ActivityLogItem(
                log = log,
                onClick = { onAction(Action.SelectLog(log)) },
                modifier = Modifier.animateItemPlacement()
            )
        }
    }
}

@Composable
private fun ActivityLogItem(
    log: ActivityLog,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Action and status
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = log.action,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    StatusBadge(status = log.status)
                }

                // Details
                Text(
                    text = log.details,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Timestamp
                Text(
                    text = formatTimestamp(log.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Status icon
            Icon(
                imageVector = getStatusIcon(log.status),
                contentDescription = log.status.name,
                tint = getStatusColor(log.status),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun StatusBadge(
    status: ActivityStatus,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = getStatusColor(status).copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status.name,
            style = MaterialTheme.typography.labelSmall,
            color = getStatusColor(status),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun FilterSummaryBar(
    state: ActivityLogContract.State,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Active filters",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = buildFilterSummary(state),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }
            TextButton(onClick = onClearFilters) {
                Text("Clear")
            }
        }
    }
}

@Composable
private fun EmptyLogsState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Assignment,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No activity logs",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Activity logs will appear here as you use the app",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

private fun getStatusColor(status: ActivityStatus): Color {
    return when (status) {
        ActivityStatus.SUCCESS -> Color(0xFF4CAF50)
        ActivityStatus.FAILED -> Color(0xFFF44336)
        ActivityStatus.IN_PROGRESS -> Color(0xFF2196F3)
        ActivityStatus.CANCELLED -> Color(0xFF9E9E9E)
    }
}

private fun getStatusIcon(status: ActivityStatus) = when (status) {
    ActivityStatus.SUCCESS -> Icons.Default.CheckCircle
    ActivityStatus.FAILED -> Icons.Default.Error
    ActivityStatus.IN_PROGRESS -> Icons.Default.Pending
    ActivityStatus.CANCELLED -> Icons.Default.Cancel
}

private fun formatTimestamp(timestamp: java.time.LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss")
    return timestamp.format(formatter)
}

private fun buildFilterSummary(state: ActivityLogContract.State): String {
    val parts = mutableListOf<String>()
    
    if (state.filterStartDate != null || state.filterEndDate != null) {
        parts.add("Date range")
    }
    if (state.filterStatus != null) {
        parts.add("Status: ${state.filterStatus.name}")
    }
    if (state.filterAction != null) {
        parts.add("Action: ${state.filterAction}")
    }
    if (state.searchQuery.isNotBlank()) {
        parts.add("Search: ${state.searchQuery}")
    }
    
    return parts.joinToString(", ")
}
