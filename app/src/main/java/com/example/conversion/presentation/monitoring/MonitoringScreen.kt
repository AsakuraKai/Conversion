package com.example.conversion.presentation.monitoring

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.conversion.domain.model.FileEvent
import com.example.conversion.domain.model.FileEventType
import com.example.conversion.domain.model.FolderMonitor
import com.example.conversion.presentation.monitoring.MonitoringContract.Action
import com.example.conversion.presentation.monitoring.MonitoringContract.Event
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Screen for folder monitoring and automatic file renaming.
 * Allows users to monitor folders for new files and apply rename configurations automatically.
 *
 * @param viewModel The monitoring view model
 * @param onNavigateToSettings Callback for navigating to settings
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitoringScreen(
    viewModel: MonitoringViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is Event.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is Event.NavigateToSettings -> {
                    onNavigateToSettings()
                }
                is Event.MonitoringStarted -> {
                    snackbarHostState.showSnackbar("Monitoring started: ${event.folderPath}")
                }
                is Event.MonitoringStopped -> {
                    snackbarHostState.showSnackbar("Monitoring stopped")
                }
                is Event.PermissionRequired -> {
                    snackbarHostState.showSnackbar("Storage permission required")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Folder Monitoring") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Card
            item {
                MonitoringStatusCard(
                    isMonitoring = state.isMonitoring,
                    monitoredFolder = state.monitoredFolder,
                    filesProcessed = state.filesProcessed,
                    statusText = state.statusText,
                    isLoading = state.isLoading
                )
            }

            // Folder Selection Card
            item {
                FolderSelectionCard(
                    selectedFolder = state.monitoredFolder,
                    onSelectFolder = { path ->
                        viewModel.handleAction(Action.SelectFolder(path))
                    },
                    enabled = !state.isMonitoring
                )
            }

            // Configuration Card
            item {
                MonitoringConfigCard(
                    filePattern = state.filePattern,
                    monitorSubfolders = state.monitorSubfolders,
                    onPatternChanged = { pattern ->
                        viewModel.handleAction(Action.UpdatePattern(pattern))
                    },
                    onSubfoldersChanged = { monitor ->
                        viewModel.handleAction(Action.UpdateMonitorSubfolders(monitor))
                    },
                    enabled = !state.isMonitoring
                )
            }

            // Toggle Card
            item {
                MonitoringToggleCard(
                    isMonitoring = state.isMonitoring,
                    canStart = state.canStartMonitoring,
                    canStop = state.canStopMonitoring,
                    isLoading = state.isLoading,
                    onStartClick = {
                        val config = FolderMonitor(
                            folderPath = state.monitoredFolder ?: return@MonitoringToggleCard,
                            folderUri = Uri.parse(state.monitoredFolder),
                            renameConfig = state.renameConfig,
                            isActive = true,
                            pattern = state.filePattern.takeIf { it.isNotBlank() },
                            monitorSubfolders = state.monitorSubfolders
                        )
                        viewModel.handleAction(Action.StartMonitoring(config))
                    },
                    onStopClick = {
                        viewModel.handleAction(Action.StopMonitoring)
                    }
                )
            }

            // Recent Events Card
            if (state.hasRecentEvents) {
                item {
                    RecentEventsCard(
                        events = state.recentEventsList,
                        onClearEvents = {
                            viewModel.handleAction(Action.ClearEvents)
                        }
                    )
                }
            }

            // Empty state
            if (!state.isMonitoring && state.monitoredFolder == null) {
                item {
                    EmptyStateCard()
                }
            }
        }
    }
}

/**
 * Card displaying current monitoring status.
 */
@Composable
private fun MonitoringStatusCard(
    isMonitoring: Boolean,
    monitoredFolder: String?,
    filesProcessed: Int,
    statusText: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isMonitoring) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (isMonitoring) Icons.Default.PlayArrow else Icons.Default.Stop,
                    contentDescription = null,
                    tint = if (isMonitoring) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            if (isMonitoring && monitoredFolder != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Folder: $monitoredFolder",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Files processed: $filesProcessed",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Card for selecting folder to monitor.
 */
@Composable
private fun FolderSelectionCard(
    selectedFolder: String?,
    onSelectFolder: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null
                )
                Text(
                    text = "Monitored Folder",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (selectedFolder != null) {
                Text(
                    text = selectedFolder,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                Text(
                    text = "No folder selected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            OutlinedButton(
                onClick = {
                    // TODO: Implement folder picker
                    // For now, use a mock path
                    onSelectFolder("/storage/emulated/0/Download")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(if (selectedFolder != null) "Change Folder" else "Select Folder")
            }
        }
    }
}

/**
 * Card for monitoring configuration.
 */
@Composable
private fun MonitoringConfigCard(
    filePattern: String,
    monitorSubfolders: Boolean,
    onPatternChanged: (String) -> Unit,
    onSubfoldersChanged: (Boolean) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Configuration",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = filePattern,
                onValueChange = onPatternChanged,
                label = { Text("File Pattern") },
                placeholder = { Text("*.jpg or IMG_*") },
                supportingText = { Text("Filter files by pattern (e.g., *.jpg, IMG_*)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Monitor Subfolders",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Include files in subdirectories",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = monitorSubfolders,
                    onCheckedChange = onSubfoldersChanged,
                    enabled = enabled
                )
            }
        }
    }
}

/**
 * Card for starting/stopping monitoring.
 */
@Composable
private fun MonitoringToggleCard(
    isMonitoring: Boolean,
    canStart: Boolean,
    canStop: Boolean,
    isLoading: Boolean,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isMonitoring) {
                Button(
                    onClick = onStopClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = canStop && !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Stop Monitoring")
                }
            } else {
                Button(
                    onClick = onStartClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = canStart && !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Start Monitoring")
                }
            }

            if (!canStart && !isMonitoring) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Please select a folder first",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * Card displaying recent file events.
 */
@Composable
private fun RecentEventsCard(
    events: List<FileEvent>,
    onClearEvents: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null
                    )
                    Text(
                        text = "Recent Events",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onClearEvents) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear events"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            events.forEach { event ->
                FileEventItem(event = event)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

/**
 * Individual file event item.
 */
@Composable
private fun FileEventItem(
    event: FileEvent,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
    val icon = when (event.eventType) {
        FileEventType.CREATED -> Icons.Default.Check
        FileEventType.MODIFIED -> Icons.Default.Info
        FileEventType.DELETED -> Icons.Default.Delete
        FileEventType.MOVED -> Icons.Default.Folder
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = when (event.eventType) {
                FileEventType.CREATED -> MaterialTheme.colorScheme.primary
                FileEventType.MODIFIED -> MaterialTheme.colorScheme.tertiary
                FileEventType.DELETED -> MaterialTheme.colorScheme.error
                FileEventType.MOVED -> MaterialTheme.colorScheme.secondary
            }
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.filePath.split("/").lastOrNull() ?: event.filePath,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${event.eventType.name} • ${dateFormat.format(Date(event.timestamp))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Empty state when no monitoring is active.
 */
@Composable
private fun EmptyStateCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Active Monitoring",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Select a folder to start monitoring for automatic file renaming",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
