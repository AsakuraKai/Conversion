package com.example.conversion.presentation.permissions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.conversion.domain.model.PermissionState

/**
 * Permission status banner that appears at the top of screens when permissions are missing.
 * Provides quick access to grant permissions without blocking the user experience.
 *
 * @param permissionState Current permission state
 * @param onManagePermissions Callback when user taps to manage permissions
 * @param onDismiss Optional callback to dismiss the banner
 * @param modifier Modifier for styling
 */
@Composable
fun PermissionStatusBanner(
    permissionState: PermissionState,
    onManagePermissions: () -> Unit,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isDismissed by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = !permissionState.allGranted && !isDismissed,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable(onClick = onManagePermissions),
            colors = CardDefaults.cardColors(
                containerColor = getBackgroundColor(permissionState)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Icon
                Icon(
                    imageVector = if (permissionState.hasMediaAccess) {
                        Icons.Default.Info
                    } else {
                        Icons.Default.Warning
                    },
                    contentDescription = null,
                    tint = getIconColor(permissionState)
                )

                // Text content
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = getBannerTitle(permissionState),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = getTextColor(permissionState)
                    )
                    Text(
                        text = getBannerMessage(permissionState),
                        style = MaterialTheme.typography.bodySmall,
                        color = getTextColor(permissionState).copy(alpha = 0.8f)
                    )
                }

                // Action icon
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Manage permissions",
                    tint = getIconColor(permissionState)
                )

                // Dismiss button (if provided)
                if (onDismiss != null) {
                    IconButton(
                        onClick = {
                            isDismissed = true
                            onDismiss()
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = getIconColor(permissionState),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Compact version of the permission banner with minimal information.
 */
@Composable
fun CompactPermissionBanner(
    permissionState: PermissionState,
    onManagePermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = !permissionState.allGranted,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onManagePermissions),
            color = MaterialTheme.colorScheme.secondaryContainer,
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(20.dp)
                )

                Text(
                    text = getCompactMessage(permissionState),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Manage",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Get background color based on permission state severity.
 */
@Composable
private fun getBackgroundColor(permissionState: PermissionState): androidx.compose.ui.graphics.Color {
    return when {
        !permissionState.hasMediaAccess -> MaterialTheme.colorScheme.errorContainer
        permissionState.deniedPermissions.size > 2 -> MaterialTheme.colorScheme.warningContainer
        else -> MaterialTheme.colorScheme.secondaryContainer
    }
}

/**
 * Get icon color based on permission state.
 */
@Composable
private fun getIconColor(permissionState: PermissionState): androidx.compose.ui.graphics.Color {
    return when {
        !permissionState.hasMediaAccess -> MaterialTheme.colorScheme.onErrorContainer
        permissionState.deniedPermissions.size > 2 -> MaterialTheme.colorScheme.onWarningContainer
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }
}

/**
 * Get text color based on permission state.
 */
@Composable
private fun getTextColor(permissionState: PermissionState): androidx.compose.ui.graphics.Color {
    return when {
        !permissionState.hasMediaAccess -> MaterialTheme.colorScheme.onErrorContainer
        permissionState.deniedPermissions.size > 2 -> MaterialTheme.colorScheme.onWarningContainer
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }
}

/**
 * Get banner title based on permission state.
 */
private fun getBannerTitle(permissionState: PermissionState): String {
    return when {
        !permissionState.hasMediaAccess -> "Storage Access Required"
        permissionState.deniedPermissions.size > 2 -> "Limited Functionality"
        else -> "Some Features Need Permissions"
    }
}

/**
 * Get banner message based on permission state.
 */
private fun getBannerMessage(permissionState: PermissionState): String {
    val deniedCount = permissionState.deniedPermissions.size
    return when {
        !permissionState.hasMediaAccess -> "Grant access to use file management features"
        deniedCount > 2 -> "Grant $deniedCount permissions for full app features"
        deniedCount == 1 -> "1 permission needed for optimal experience"
        else -> "$deniedCount permissions needed"
    }
}

/**
 * Get compact message for minimal banner.
 */
private fun getCompactMessage(permissionState: PermissionState): String {
    val deniedCount = permissionState.deniedPermissions.size
    return when {
        !permissionState.hasMediaAccess -> "Tap to enable storage access"
        else -> "$deniedCount permission${if (deniedCount > 1) "s" else ""} needed • Tap to manage"
    }
}

/**
 * Extension property for warning container color (Material 3 doesn't have it by default).
 */
private val ColorScheme.warningContainer: androidx.compose.ui.graphics.Color
    @Composable
    get() = MaterialTheme.colorScheme.tertiaryContainer

/**
 * Extension property for warning container text color.
 */
private val ColorScheme.onWarningContainer: androidx.compose.ui.graphics.Color
    @Composable
    get() = MaterialTheme.colorScheme.onTertiaryContainer
