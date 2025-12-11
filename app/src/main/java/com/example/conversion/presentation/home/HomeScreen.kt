package com.example.conversion.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.conversion.presentation.permissions.PermissionStatusBanner
import com.example.conversion.presentation.permissions.PermissionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToBatchProcess: () -> Unit,
    onNavigateToFormatConverter: () -> Unit,
    onNavigateToBookReader: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToTagManagement: () -> Unit = {},
    onNavigateToTemplateManagement: () -> Unit = {},
    onNavigateToMonitoring: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToAISuggestions: () -> Unit = {},
    onNavigateToCloudSync: () -> Unit = {},
    onNavigateToAccount: () -> Unit = {},
    onNavigateToActivityLog: () -> Unit = {},
    onNavigateToQRDisplay: () -> Unit = {},
    onNavigateToQRScanner: () -> Unit = {},
    modifier: Modifier = Modifier,
    permissionsViewModel: PermissionsViewModel = hiltViewModel()
) {
    val permissionState by permissionsViewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Auto Rename File Service") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Permission status banner at top
            PermissionStatusBanner(
                permissionState = permissionState.permissionState,
                onManagePermissions = onNavigateToSettings
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Text(
                text = "Welcome to Auto Rename File Service",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Choose a feature to get started",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Batch Process Card
            FeatureCard(
                title = "Batch Rename Files",
                description = "Select and rename multiple files with patterns",
                icon = Icons.Default.DriveFileRenameOutline,
                onClick = onNavigateToBatchProcess
            )
            
            // Tag Management Card
            FeatureCard(
                title = "Manage Tags",
                description = "Organize files with custom tags",
                icon = Icons.Default.Label,
                onClick = onNavigateToTagManagement
            )
            
            // Template Management Card
            FeatureCard(
                title = "Rename Templates",
                description = "Create and manage rename patterns",
                icon = Icons.Default.TextSnippet,
                onClick = onNavigateToTemplateManagement
            )
            
            // Monitoring Card
            FeatureCard(
                title = "Folder Monitoring",
                description = "Watch folders for automatic renaming",
                icon = Icons.Default.FolderOpen,
                onClick = onNavigateToMonitoring
            )
            
            // Format Converter Card (Coming Soon)
            FeatureCard(
                title = "Format Converter",
                description = "Convert between different file formats",
                icon = Icons.Default.Transform,
                onClick = onNavigateToFormatConverter,
                enabled = false,
                badge = "Coming Soon"
            )
            
            // Book Reader Card (Coming Soon)
            FeatureCard(
                title = "Book Reader",
                description = "Read PDFs, EPUBs, and text files",
                icon = Icons.Default.MenuBook,
                onClick = onNavigateToBookReader,
                enabled = false,
                badge = "Coming Soon"
            )
            
            // Smart Features Section
            Text(
                text = "Smart Features",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
            
            FeatureCard(
                title = "Rename History",
                description = "View, undo, and redo recent rename operations",
                icon = Icons.Default.History,
                onClick = onNavigateToHistory
            )
            
            FeatureCard(
                title = "AI Suggestions",
                description = "Get intelligent filename suggestions using AI",
                icon = Icons.Default.AutoAwesome,
                onClick = onNavigateToAISuggestions
            )
            
            // Cloud & Sync Section
            Text(
                text = "Cloud & Sync",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
            
            FeatureCard(
                title = "Cloud Sync",
                description = "Sync your templates and settings across devices",
                icon = Icons.Default.CloudSync,
                onClick = onNavigateToCloudSync
            )
            
            FeatureCard(
                title = "Account",
                description = "Manage your account and connected services",
                icon = Icons.Default.AccountCircle,
                onClick = onNavigateToAccount
            )
            
            FeatureCard(
                title = "Activity Log",
                description = "View detailed history of all operations",
                icon = Icons.Default.Assignment,
                onClick = onNavigateToActivityLog
            )
            
            // Share & Import Section
            Text(
                text = "Share & Import",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
            
            FeatureCard(
                title = "Share QR Code",
                description = "Generate QR code for your rename presets",
                icon = Icons.Default.QrCode,
                onClick = onNavigateToQRDisplay
            )
            
            FeatureCard(
                title = "Scan QR Code",
                description = "Import rename presets from QR codes",
                icon = Icons.Default.QrCodeScanner,
                onClick = onNavigateToQRScanner
            )
        }
    }
    }
}

@Composable
private fun FeatureCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    badge: String? = null
) {
    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = if (enabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    if (badge != null) {
                        Badge {
                            Text(badge)
                        }
                    }
                }
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
