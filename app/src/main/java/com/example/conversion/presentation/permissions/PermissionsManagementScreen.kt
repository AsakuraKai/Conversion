package com.example.conversion.presentation.permissions

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.conversion.domain.model.Permission
import com.example.conversion.domain.model.PermissionStatus

/**
 * Permissions Management Screen.
 * Provides a centralized interface for managing all app permissions with a "Grant All" option.
 * Implements staged permission requesting for better user experience.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsManagementScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PermissionsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Permission launcher for requesting multiple permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle results for each permission
        permissions.forEach { (manifestPermission, isGranted) ->
            // Find which Permission enum this manifest permission belongs to
            Permission.entries.forEach { permission ->
                if (permission.manifestPermissions.contains(manifestPermission)) {
                    viewModel.handleAction(
                        PermissionsContract.Action.OnPermissionResult(
                            permission = permission,
                            isGranted = isGranted,
                            shouldShowRationale = false // Will be checked internally
                        )
                    )
                }
            }
        }
    }

    // Launcher for MANAGE_EXTERNAL_STORAGE (API 30+)
    val manageStorageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        // Check if permission was granted after returning from settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val isGranted = android.os.Environment.isExternalStorageManager()
            viewModel.handleAction(
                PermissionsContract.Action.OnPermissionResult(
                    permission = Permission.MANAGE_EXTERNAL_STORAGE,
                    isGranted = isGranted,
                    shouldShowRationale = false
                )
            )
        }
    }

    // Handle one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is PermissionsContract.Event.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is PermissionsContract.Event.PermissionsGranted -> {
                    snackbarHostState.showSnackbar("All permissions granted!")
                }
                is PermissionsContract.Event.PermissionsDenied -> {
                    snackbarHostState.showSnackbar("Some permissions were denied")
                }
                is PermissionsContract.Event.NavigateToSettings -> {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                }
                is PermissionsContract.Event.RequestPermissions -> {
                    // Handle permission requests
                    val hasManageStorage = event.permissions.contains(Permission.MANAGE_EXTERNAL_STORAGE)
                    
                    // Get regular manifest permissions
                    val manifestPermissions = event.permissions
                        .filter { it != Permission.MANAGE_EXTERNAL_STORAGE }
                        .flatMap { it.manifestPermissions }
                        .distinct()
                    
                    // Request regular permissions first
                    if (manifestPermissions.isNotEmpty()) {
                        permissionLauncher.launch(manifestPermissions.toTypedArray())
                    }
                    
                    // Request MANAGE_EXTERNAL_STORAGE separately (API 30+)
                    if (hasManageStorage && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        manageStorageLauncher.launch(intent)
                    }
                }
                is PermissionsContract.Event.ShowRationale -> {
                    // Could show a dialog here if needed
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Permissions") },
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Grant All Permissions Card
            if (!state.hasAllPermissions) {
                GrantAllPermissionsCard(
                    onGrantAll = {
                        viewModel.handleAction(PermissionsContract.Action.RequestPermissions)
                    }
                )
            } else {
                AllPermissionsGrantedCard()
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Individual Permission Cards
            Text(
                text = "Individual Permissions",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // Media Permissions Section
            PermissionSection(
                title = "Media Access",
                permissions = listOf(
                    Permission.READ_IMAGES,
                    Permission.READ_VIDEOS,
                    Permission.READ_AUDIO
                ),
                permissionState = state.permissionState,
                onRequestPermission = { permission ->
                    viewModel.handleAction(
                        PermissionsContract.Action.RequestSpecificPermissions(listOf(permission))
                    )
                },
                onOpenSettings = {
                    viewModel.handleAction(PermissionsContract.Action.OpenSettings)
                }
            )

            // Storage Write Permission
            if (Permission.WRITE_STORAGE.isApplicable()) {
                PermissionCard(
                    permission = Permission.WRITE_STORAGE,
                    status = state.permissionState.permissionStatuses[Permission.WRITE_STORAGE]
                        ?: PermissionStatus.NotApplicable,
                    title = "Storage Write Access",
                    description = "Save renamed files to your device",
                    icon = Icons.Default.Edit,
                    onRequest = {
                        viewModel.handleAction(
                            PermissionsContract.Action.RequestSpecificPermissions(
                                listOf(Permission.WRITE_STORAGE)
                            )
                        )
                    },
                    onOpenSettings = {
                        viewModel.handleAction(PermissionsContract.Action.OpenSettings)
                    }
                )
            }

            // Optional Permissions Section
            Text(
                text = "Optional Permissions",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Notifications
            if (Permission.POST_NOTIFICATIONS.isApplicable()) {
                PermissionCard(
                    permission = Permission.POST_NOTIFICATIONS,
                    status = state.permissionState.permissionStatuses[Permission.POST_NOTIFICATIONS]
                        ?: PermissionStatus.NotApplicable,
                    title = "Notifications",
                    description = "Get updates on file operations and monitoring",
                    icon = Icons.Default.Notifications,
                    onRequest = {
                        viewModel.handleAction(
                            PermissionsContract.Action.RequestSpecificPermissions(
                                listOf(Permission.POST_NOTIFICATIONS)
                            )
                        )
                    },
                    onOpenSettings = {
                        viewModel.handleAction(PermissionsContract.Action.OpenSettings)
                    }
                )
            }

            // Camera Permission
            PermissionCard(
                permission = Permission.CAMERA,
                status = state.permissionState.permissionStatuses[Permission.CAMERA]
                    ?: PermissionStatus.NotApplicable,
                title = "Camera Access",
                description = "Scan QR codes to import templates",
                icon = Icons.Default.CameraAlt,
                onRequest = {
                    viewModel.handleAction(
                        PermissionsContract.Action.RequestSpecificPermissions(
                            listOf(Permission.CAMERA)
                        )
                    )
                },
                onOpenSettings = {
                    viewModel.handleAction(PermissionsContract.Action.OpenSettings)
                }
            )

            // System Settings Button
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    viewModel.handleAction(PermissionsContract.Action.OpenSettings)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Open System Settings")
            }
        }
    }
}

/**
 * Card for "Grant All Permissions" feature with staged requesting.
 */
@Composable
private fun GrantAllPermissionsCard(
    onGrantAll: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Grant All Permissions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Enable all features at once",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Text(
                text = "Permissions will be requested in stages:\n• Media access (photos, videos, audio)\n• Storage write permission\n• Optional features (notifications, camera)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Button(
                onClick = onGrantAll,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text("Grant All Permissions")
            }
        }
    }
}

/**
 * Card shown when all permissions are granted.
 */
@Composable
private fun AllPermissionsGrantedCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.tertiary
            )
            Column {
                Text(
                    text = "All Set!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "All permissions granted. You have full access to app features.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

/**
 * Section for grouping related permissions.
 */
@Composable
private fun PermissionSection(
    title: String,
    permissions: List<Permission>,
    permissionState: com.example.conversion.domain.model.PermissionState,
    onRequestPermission: (Permission) -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        permissions.forEach { permission ->
            if (permission.isApplicable()) {
                PermissionCard(
                    permission = permission,
                    status = permissionState.permissionStatuses[permission]
                        ?: PermissionStatus.NotApplicable,
                    title = getPermissionTitle(permission),
                    description = getPermissionDescription(permission),
                    icon = getPermissionIcon(permission),
                    onRequest = { onRequestPermission(permission) },
                    onOpenSettings = onOpenSettings
                )
            }
        }
    }
}

/**
 * Individual permission card.
 */
@Composable
private fun PermissionCard(
    permission: Permission,
    status: PermissionStatus,
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onRequest: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = when (status) {
                    is PermissionStatus.Granted -> MaterialTheme.colorScheme.primary
                    is PermissionStatus.Denied,
                    is PermissionStatus.PermanentlyDenied -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Status indicator
                Text(
                    text = when (status) {
                        is PermissionStatus.Granted -> "✓ Granted"
                        is PermissionStatus.Denied -> "⚠ Not Granted"
                        is PermissionStatus.PermanentlyDenied -> "⚠ Denied - Needs Settings"
                        is PermissionStatus.NotApplicable -> "Not Applicable"
                        is PermissionStatus.Unknown -> "Unknown"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = when (status) {
                        is PermissionStatus.Granted -> MaterialTheme.colorScheme.primary
                        is PermissionStatus.Denied,
                        is PermissionStatus.PermanentlyDenied -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Action button
            when (status) {
                is PermissionStatus.Granted -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Granted",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                is PermissionStatus.PermanentlyDenied -> {
                    TextButton(onClick = onOpenSettings) {
                        Text("Settings")
                    }
                }
                is PermissionStatus.Denied -> {
                    Button(onClick = onRequest) {
                        Text("Grant")
                    }
                }
                else -> {}
            }
        }
    }
}


/**
 * Helper functions for permission display information.
 */
private fun getPermissionTitle(permission: Permission): String = when (permission) {
    Permission.READ_IMAGES -> "Photos Access"
    Permission.READ_VIDEOS -> "Videos Access"
    Permission.READ_AUDIO -> "Audio Access"
    Permission.WRITE_STORAGE -> "Storage Write Access"
    Permission.MANAGE_EXTERNAL_STORAGE -> "Manage All Files"
    Permission.POST_NOTIFICATIONS -> "Notifications"
    Permission.CAMERA -> "Camera Access"
}

private fun getPermissionDescription(permission: Permission): String = when (permission) {
    Permission.READ_IMAGES -> "Access your photos for renaming"
    Permission.READ_VIDEOS -> "Access your videos for renaming"
    Permission.READ_AUDIO -> "Access your audio files for renaming"
    Permission.WRITE_STORAGE -> "Save renamed files to your device"
    Permission.MANAGE_EXTERNAL_STORAGE -> "Full file access for advanced features"
    Permission.POST_NOTIFICATIONS -> "Get updates on file operations"
    Permission.CAMERA -> "Scan QR codes to import templates"
}

private fun getPermissionIcon(permission: Permission): androidx.compose.ui.graphics.vector.ImageVector = when (permission) {
    Permission.READ_IMAGES -> Icons.Default.Image
    Permission.READ_VIDEOS -> Icons.Default.VideoLibrary
    Permission.READ_AUDIO -> Icons.Default.AudioFile
    Permission.WRITE_STORAGE -> Icons.Default.Edit
    Permission.MANAGE_EXTERNAL_STORAGE -> Icons.Default.Folder
    Permission.POST_NOTIFICATIONS -> Icons.Default.Notifications
    Permission.CAMERA -> Icons.Default.CameraAlt
}
