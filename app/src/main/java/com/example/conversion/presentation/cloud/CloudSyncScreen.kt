package com.example.conversion.presentation.cloud

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.conversion.domain.model.CloudProvider
import java.text.SimpleDateFormat
import java.util.*

/**
 * Cloud Sync screen for managing cloud storage connections and sync settings.
 * 
 * MOCK IMPLEMENTATION: Simulates cloud provider connections and sync operations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudSyncScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CloudSyncViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    // Handle one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CloudSyncContract.Event.ShowMessage -> {
                    android.widget.Toast.makeText(context, event.message, android.widget.Toast.LENGTH_SHORT).show()
                }
                is CloudSyncContract.Event.ShowError -> {
                    android.widget.Toast.makeText(context, event.error, android.widget.Toast.LENGTH_LONG).show()
                }
                is CloudSyncContract.Event.AuthenticationSuccess -> {
                    // Could show success dialog
                }
                is CloudSyncContract.Event.AuthenticationFailed -> {
                    // Error already shown via ShowError
                }
                is CloudSyncContract.Event.SyncSuccess -> {
                    // Success message already shown
                }
                is CloudSyncContract.Event.SyncFailed -> {
                    // Error already shown
                }
                is CloudSyncContract.Event.NavigateToAuth -> {
                    // In real implementation, would launch browser for OAuth
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cloud Sync") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Provider Section
                Text(
                    text = "Cloud Providers",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                CloudProviderCard(
                    provider = CloudProvider.GOOGLE_DRIVE,
                    isConnected = state.isProviderConnected(CloudProvider.GOOGLE_DRIVE),
                    isAuthenticating = state.isProviderConnecting(CloudProvider.GOOGLE_DRIVE),
                    onConnect = { viewModel.handleAction(CloudSyncContract.Action.ConnectProvider(CloudProvider.GOOGLE_DRIVE)) },
                    onDisconnect = { viewModel.handleAction(CloudSyncContract.Action.DisconnectProvider(CloudProvider.GOOGLE_DRIVE)) }
                )
                
                CloudProviderCard(
                    provider = CloudProvider.DROPBOX,
                    isConnected = state.isProviderConnected(CloudProvider.DROPBOX),
                    isAuthenticating = state.isProviderConnecting(CloudProvider.DROPBOX),
                    onConnect = { viewModel.handleAction(CloudSyncContract.Action.ConnectProvider(CloudProvider.DROPBOX)) },
                    onDisconnect = { viewModel.handleAction(CloudSyncContract.Action.DisconnectProvider(CloudProvider.DROPBOX)) }
                )
                
                CloudProviderCard(
                    provider = CloudProvider.ONEDRIVE,
                    isConnected = state.isProviderConnected(CloudProvider.ONEDRIVE),
                    isAuthenticating = state.isProviderConnecting(CloudProvider.ONEDRIVE),
                    onConnect = { viewModel.handleAction(CloudSyncContract.Action.ConnectProvider(CloudProvider.ONEDRIVE)) },
                    onDisconnect = { viewModel.handleAction(CloudSyncContract.Action.DisconnectProvider(CloudProvider.ONEDRIVE)) }
                )
                
                // Sync Settings Section
                if (state.hasConnectedProvider) {
                    HorizontalDivider()
                    
                    Text(
                        text = "Sync Settings",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    SyncSettingsCard(
                        autoSync = state.syncConfig.autoSync,
                        syncInterval = state.syncConfig.syncInterval,
                        syncOnWifiOnly = state.syncConfig.syncOnWifiOnly,
                        enableBackup = state.syncConfig.enableBackup,
                        onAutoSyncChange = { viewModel.handleAction(CloudSyncContract.Action.UpdateAutoSync(it)) },
                        onIntervalChange = { viewModel.handleAction(CloudSyncContract.Action.UpdateSyncInterval(it)) },
                        onWifiOnlyChange = { viewModel.handleAction(CloudSyncContract.Action.UpdateWifiOnly(it)) },
                        onBackupChange = { viewModel.handleAction(CloudSyncContract.Action.UpdateBackupEnabled(it)) }
                    )
                    
                    // Manual Sync Section
                    HorizontalDivider()
                    
                    Text(
                        text = "Manual Sync",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    ManualSyncCard(
                        isSyncing = state.isSyncing,
                        syncStatus = state.syncStatus,
                        onSyncNow = { viewModel.handleAction(CloudSyncContract.Action.ManualSync) },
                        onRetry = { viewModel.handleAction(CloudSyncContract.Action.RetrySync) }
                    )
                }
                
                // Error display
                state.error?.let { error ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = error,
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            IconButton(
                                onClick = { viewModel.handleAction(CloudSyncContract.Action.DismissError) }
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CloudProviderCard(
    provider: CloudProvider,
    isConnected: Boolean,
    isAuthenticating: Boolean,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = when (provider) {
                        CloudProvider.GOOGLE_DRIVE -> Icons.Default.CloudQueue
                        CloudProvider.DROPBOX -> Icons.Default.Storage
                        CloudProvider.ONEDRIVE -> Icons.Default.CloudCircle
                    },
                    contentDescription = provider.displayName,
                    tint = if (isConnected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                
                Column {
                    Text(
                        text = provider.displayName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (isConnected) "Connected" else "Not connected",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isConnected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
            
            if (isAuthenticating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp)
                )
            } else if (isConnected) {
                FilledTonalButton(onClick = onDisconnect) {
                    Text("Disconnect")
                }
            } else {
                Button(onClick = onConnect) {
                    Text("Connect")
                }
            }
        }
    }
}

@Composable
private fun SyncSettingsCard(
    autoSync: Boolean,
    syncInterval: Int,
    syncOnWifiOnly: Boolean,
    enableBackup: Boolean,
    onAutoSyncChange: (Boolean) -> Unit,
    onIntervalChange: (Int) -> Unit,
    onWifiOnlyChange: (Boolean) -> Unit,
    onBackupChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Auto-sync toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Auto-sync",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Automatically sync files in background",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = autoSync,
                    onCheckedChange = onAutoSyncChange
                )
            }
            
            if (autoSync) {
                HorizontalDivider()
                
                // Sync interval
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Sync interval: $syncInterval minutes",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(15, 30, 60, 120, 240).forEach { interval ->
                            FilterChip(
                                selected = syncInterval == interval,
                                onClick = { onIntervalChange(interval) },
                                label = { Text("${interval}m") }
                            )
                        }
                    }
                }
            }
            
            HorizontalDivider()
            
            // WiFi only
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "WiFi only",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Sync only when connected to WiFi",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = syncOnWifiOnly,
                    onCheckedChange = onWifiOnlyChange
                )
            }
            
            HorizontalDivider()
            
            // Backup toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Backup files",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Automatically backup renamed files",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = enableBackup,
                    onCheckedChange = onBackupChange
                )
            }
        }
    }
}

@Composable
private fun ManualSyncCard(
    isSyncing: Boolean,
    syncStatus: com.example.conversion.domain.model.SyncStatus,
    onSyncNow: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isSyncing) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Syncing files...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                // Last sync time
                syncStatus.lastSyncTime?.let { lastSync ->
                    val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
                    Text(
                        text = "Last synced: ${dateFormat.format(Date(lastSync))}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } ?: run {
                    Text(
                        text = "Never synced",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Error status
                if (syncStatus.hasError) {
                    Text(
                        text = "Last sync failed: ${syncStatus.error}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    
                    Button(
                        onClick = onRetry,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Retry Sync")
                    }
                } else {
                    Button(
                        onClick = onSyncNow,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sync Now")
                    }
                }
            }
        }
    }
}
