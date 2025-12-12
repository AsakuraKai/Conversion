package com.example.conversion.presentation.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.conversion.domain.model.ThemeMode
import com.example.conversion.presentation.theme.DynamicThemeContract
import com.example.conversion.presentation.theme.DynamicThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToCloudSync: () -> Unit = {},
    onNavigateToAccount: () -> Unit = {},
    onNavigateToActivityLog: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToPermissions: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel(),
    dynamicThemeViewModel: DynamicThemeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val dynamicThemeState by dynamicThemeViewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Image picker launcher for dynamic theming
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { dynamicThemeViewModel.handleAction(DynamicThemeContract.Action.SelectImage(it)) }
    }
    
    // Handle one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SettingsEvent.ShowToast -> {
                    android.widget.Toast.makeText(context, event.message, android.widget.Toast.LENGTH_SHORT).show()
                }
                is SettingsEvent.ShowError -> {
                    android.widget.Toast.makeText(context, event.error, android.widget.Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    // Handle dynamic theme events
    LaunchedEffect(Unit) {
        dynamicThemeViewModel.events.collect { event ->
            when (event) {
                is DynamicThemeContract.Event.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is DynamicThemeContract.Event.ThemeApplied -> {
                    snackbarHostState.showSnackbar("Theme applied successfully")
                }
                is DynamicThemeContract.Event.ThemeReset -> {
                    snackbarHostState.showSnackbar("Theme reset to default")
                }
                else -> {}
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                // Theme Section
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Theme Mode",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        ThemeMode.entries.forEach { mode ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = state.preferences.themeMode == mode,
                                    onClick = {
                                        viewModel.onAction(SettingsAction.UpdateThemeMode(mode))
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = mode.name.lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                        
                        // Dynamic Colors option (Android 12+)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Dynamic Colors",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Use colors from wallpaper",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = state.preferences.useDynamicColors,
                                    onCheckedChange = { enabled ->
                                        viewModel.onAction(SettingsAction.UpdateDynamicColors(enabled))
                                    }
                                )
                            }
                        }
                    }
                }
                
                // Permissions Section
                Text(
                    text = "Permissions",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Manage App Permissions",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Text(
                            text = "Control which permissions this app can use. Grant all at once or manage individually.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        // New: Navigate to Permissions Management Screen
                        Button(
                            onClick = onNavigateToPermissions,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Manage Permissions")
                        }
                        
                        // System Settings (secondary option)
                        OutlinedButton(
                            onClick = {
                                // Open app settings where user can manage/revoke permissions
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Open System Settings")
                        }
                        
                        // Info about required permissions
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        Text(
                            text = "Required Permissions:",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                PermissionItem("Read Media Images", "Access your photos")
                                PermissionItem("Read Media Videos", "Access your videos")
                                PermissionItem("Read Media Audio", "Access your audio files")
                                PermissionItem("Notifications", "Show progress updates")
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                PermissionItem("Storage Access", "Read your files")
                                PermissionItem("Manage External Storage", "Rename your files")
                            } else {
                                PermissionItem("Read Storage", "Access your files")
                                PermissionItem("Write Storage", "Rename your files")
                            }
                        }
                    }
                }
                
                // Image-based Dynamic Theming Section
                Text(
                    text = "Image-Based Theme",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Create a custom theme from an image",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        // Image selection
                        if (dynamicThemeState.selectedImageUri != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                AsyncImage(
                                    model = dynamicThemeState.selectedImageUri,
                                    contentDescription = "Selected theme image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                
                                if (dynamicThemeState.isLoading) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.5f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = Color.White)
                                    }
                                }
                            }
                            
                            OutlinedButton(
                                onClick = {
                                    dynamicThemeViewModel.handleAction(DynamicThemeContract.Action.PickNewImage)
                                    imagePickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !dynamicThemeState.isLoading
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Choose Different Image")
                            }
                        } else {
                            Button(
                                onClick = {
                                    dynamicThemeViewModel.handleAction(DynamicThemeContract.Action.PickNewImage)
                                    imagePickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                enabled = !dynamicThemeState.isLoading
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Choose Image for Theme")
                            }
                        }
                        
                        // Show color palette if available
                        if (dynamicThemeState.hasPalette && dynamicThemeState.palette != null) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            
                            Text(
                                text = "Extracted Colors",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            
                            dynamicThemeState.palette?.dominantColor?.let { color ->
                                ColorSwatch(color = color, label = "Dominant")
                            }
                            
                            dynamicThemeState.palette?.vibrantColor?.let { color ->
                                ColorSwatch(color = color, label = "Vibrant")
                            }
                            
                            dynamicThemeState.palette?.mutedColor?.let { color ->
                                ColorSwatch(color = color, label = "Muted")
                            }
                        }
                        
                        // Action buttons
                        if (dynamicThemeState.hasPalette || dynamicThemeState.isThemeApplied) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                if (dynamicThemeState.canResetTheme) {
                                    OutlinedButton(
                                        onClick = {
                                            dynamicThemeViewModel.handleAction(DynamicThemeContract.Action.ResetTheme)
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Reset Theme")
                                    }
                                }
                                
                                Button(
                                    onClick = {
                                        dynamicThemeViewModel.handleAction(DynamicThemeContract.Action.ApplyTheme)
                                    },
                                    modifier = Modifier.weight(1f),
                                    enabled = dynamicThemeState.canApplyTheme
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(if (dynamicThemeState.canResetTheme) "Update" else "Apply")
                                }
                            }
                        }
                        
                        // Error message
                        dynamicThemeState.error?.let { errorMessage ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                ),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text(
                                    text = errorMessage,
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
                
                // Data & History Section
                Text(
                    text = "Data & History",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsItem(
                            title = "History",
                            description = "View, undo, and redo recent changes",
                            icon = Icons.Default.History,
                            onClick = onNavigateToHistory
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        SettingsItem(
                            title = "Activity Log",
                            description = "View detailed history of all operations",
                            icon = Icons.Default.Assignment,
                            onClick = onNavigateToActivityLog
                        )
                    }
                }
                
                // Cloud & Sync Section
                Text(
                    text = "Cloud & Sync",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsItem(
                            title = "Cloud Sync",
                            description = "Sync your templates and settings across devices",
                            icon = Icons.Default.CloudSync,
                            onClick = onNavigateToCloudSync
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        SettingsItem(
                            title = "Account",
                            description = "Manage your account and connected services",
                            icon = Icons.Default.AccountCircle,
                            onClick = onNavigateToAccount
                        )
                    }
                }
                
                // App Info Section
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InfoRow("Version", "N/A")
                        InfoRow("Build", "N/A")
                    }
                }
                
                // Error message
                state.errorMessage?.let { error ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsItem(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PermissionItem(
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ColorSwatch(
    color: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                )
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
