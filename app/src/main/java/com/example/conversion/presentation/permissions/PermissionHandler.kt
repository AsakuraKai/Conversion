package com.example.conversion.presentation.permissions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.conversion.domain.model.Permission
import com.google.accompanist.permissions.*

/**
 * Composable that handles permission requests with UI feedback.
 * Provides a reusable component for checking and requesting permissions.
 *
 * @param permissions List of permissions to check/request
 * @param rational e Optional text to explain why permissions are needed
 * @param onPermissionsGranted Callback when all permissions are granted
 * @param onPermissionsDenied Callback when permissions are denied
 * @param content Content to show when permissions are granted
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionHandler(
    permissions: List<Permission>,
    rationaleMessage: String = "This app needs access to your files to function properly.",
    onPermissionsGranted: () -> Unit = {},
    onPermissionsDenied: (List<Permission>) -> Unit = {},
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    // Get all manifest permissions from Permission enums
    val manifestPermissions = remember(permissions) {
        permissions.filter { it.isApplicable() }
            .flatMap { it.manifestPermissions }
            .distinct()
    }

    // Special handling for MANAGE_EXTERNAL_STORAGE
    val needsManageStorage = remember(permissions) {
        permissions.contains(Permission.MANAGE_EXTERNAL_STORAGE) &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }

    // Launcher for MANAGE_EXTERNAL_STORAGE settings
    val manageStorageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        // Check if permission was granted after returning from settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (android.os.Environment.isExternalStorageManager()) {
                onPermissionsGranted()
            }
        }
    }

    // Accompanist permissions state
    val permissionsState = if (manifestPermissions.isNotEmpty()) {
        rememberMultiplePermissionsState(
            permissions = manifestPermissions,
            onPermissionsResult = { results ->
                val allGranted = results.all { it.value }
                if (allGranted && !needsManageStorage) {
                    onPermissionsGranted()
                } else if (!allGranted) {
                    val denied = permissions.filter { permission ->
                        permission.manifestPermissions.any { !results[it]!! }
                    }
                    onPermissionsDenied(denied)
                }
            }
        )
    } else {
        null
    }

    // Check permissions on composition
    LaunchedEffect(manifestPermissions) {
        val regularPermissionsGranted = permissionsState?.allPermissionsGranted ?: true
        val manageStorageGranted = if (needsManageStorage) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                android.os.Environment.isExternalStorageManager()
            } else {
                true
            }
        } else {
            true
        }

        if (regularPermissionsGranted && manageStorageGranted) {
            onPermissionsGranted()
        }
    }

    // Determine current state
    val allGranted = (permissionsState?.allPermissionsGranted ?: true) &&
            (!needsManageStorage || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                    android.os.Environment.isExternalStorageManager()))

    when {
        allGranted -> {
            // All permissions granted, show content
            content()
        }
        permissionsState?.shouldShowRationale == true -> {
            // Show rationale and request button
            PermissionRationaleContent(
                message = rationaleMessage,
                onRequestPermissions = {
                    permissionsState.launchMultiplePermissionRequest()
                },
                onOpenSettings = {
                    openAppSettings(context)
                }
            )
        }
        else -> {
            // Show initial request or permanently denied state
            val isPermanentlyDenied = permissionsState?.permissions?.any {
                !it.status.isGranted && !it.status.shouldShowRationale
            } ?: false

            if (isPermanentlyDenied) {
                PermissionDeniedContent(
                    onOpenSettings = {
                        if (needsManageStorage && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            openManageStorageSettings(context, manageStorageLauncher)
                        } else {
                            openAppSettings(context)
                        }
                    }
                )
            } else {
                PermissionRequestContent(
                    message = rationaleMessage,
                    onRequestPermissions = {
                        if (needsManageStorage && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            openManageStorageSettings(context, manageStorageLauncher)
                        } else {
                            permissionsState?.launchMultiplePermissionRequest()
                        }
                    }
                )
            }
        }
    }
}

/**
 * Content shown when rationale should be displayed.
 */
@Composable
private fun PermissionRationaleContent(
    message: String,
    onRequestPermissions: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Permission Required",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRequestPermissions,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Grant Permission")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onOpenSettings) {
            Text("Open Settings")
        }
    }
}

/**
 * Content shown for initial permission request.
 */
@Composable
private fun PermissionRequestContent(
    message: String,
    onRequestPermissions: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Storage Access Required",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRequestPermissions,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue")
        }
    }
}

/**
 * Content shown when permissions are permanently denied.
 */
@Composable
private fun PermissionDeniedContent(
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Permission Denied",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Storage permission is required for this app to function. " +
                    "Please enable it in the app settings.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onOpenSettings,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open Settings")
        }
    }
}

/**
 * Opens the app's settings page.
 */
private fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}

/**
 * Opens the MANAGE_EXTERNAL_STORAGE settings for Android 11+.
 */
private fun openManageStorageSettings(
    context: Context,
    launcher: androidx.activity.result.ActivityResultLauncher<Intent>
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        launcher.launch(intent)
    }
}
