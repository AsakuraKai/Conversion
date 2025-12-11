package com.example.conversion.presentation.permissions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.conversion.domain.model.Permission

/**
 * Educational bottom sheet that explains permission purposes before requesting them.
 * Helps users understand why permissions are needed and builds trust.
 *
 * @param permissions List of permissions to explain
 * @param onDismiss Callback when sheet is dismissed
 * @param onProceed Callback when user proceeds to grant permissions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionEducationSheet(
    permissions: List<Permission>,
    onDismiss: () -> Unit,
    onProceed: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Why We Need Permissions",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "We respect your privacy and only request permissions necessary for features you use.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Permission explanations
            permissions.forEach { permission ->
                PermissionExplanationItem(permission = permission)
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Privacy notice
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Your Data Stays Private",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "We never collect, store, or share your personal files. All operations happen locally on your device.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Not Now")
                }

                Button(
                    onClick = onProceed,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Continue")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * Individual permission explanation item with icon, title, and description.
 */
@Composable
private fun PermissionExplanationItem(
    permission: Permission,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Icon
        Icon(
            imageVector = getPermissionIcon(permission),
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        // Text content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = getPermissionName(permission),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = getPermissionExplanation(permission),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "• ${getPermissionUseCase(permission)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

/**
 * Get user-friendly name for permission.
 */
private fun getPermissionName(permission: Permission): String = when (permission) {
    Permission.READ_IMAGES -> "Photo Access"
    Permission.READ_VIDEOS -> "Video Access"
    Permission.READ_AUDIO -> "Audio Access"
    Permission.WRITE_STORAGE -> "Storage Write"
    Permission.MANAGE_EXTERNAL_STORAGE -> "File Management"
    Permission.POST_NOTIFICATIONS -> "Notifications"
    Permission.CAMERA -> "Camera Access"
}

/**
 * Get detailed explanation for permission.
 */
private fun getPermissionExplanation(permission: Permission): String = when (permission) {
    Permission.READ_IMAGES -> "Access your photos to rename and organize them efficiently."
    Permission.READ_VIDEOS -> "Access your videos to apply custom naming patterns."
    Permission.READ_AUDIO -> "Access your audio files to keep your music library organized."
    Permission.WRITE_STORAGE -> "Save renamed files to your device storage."
    Permission.MANAGE_EXTERNAL_STORAGE -> "Full file management capabilities across all folders."
    Permission.POST_NOTIFICATIONS -> "Receive updates on file operations and monitoring alerts."
    Permission.CAMERA -> "Scan QR codes for quick file naming and tagging."
}

/**
 * Get specific use case example for permission.
 */
private fun getPermissionUseCase(permission: Permission): String = when (permission) {
    Permission.READ_IMAGES -> "Example: Rename vacation photos to 'Trip_2024_001.jpg'"
    Permission.READ_VIDEOS -> "Example: Organize videos by date and event"
    Permission.READ_AUDIO -> "Example: Standardize music file naming"
    Permission.WRITE_STORAGE -> "Example: Save renamed files to your chosen location"
    Permission.MANAGE_EXTERNAL_STORAGE -> "Example: Batch rename across multiple folders"
    Permission.POST_NOTIFICATIONS -> "Example: Get notified when folder monitoring detects changes"
    Permission.CAMERA -> "Example: Scan QR code to apply preset naming template"
}

/**
 * Get icon for permission.
 */
private fun getPermissionIcon(permission: Permission): ImageVector = when (permission) {
    Permission.READ_IMAGES -> Icons.Default.Image
    Permission.READ_VIDEOS -> Icons.Default.VideoLibrary
    Permission.READ_AUDIO -> Icons.Default.AudioFile
    Permission.WRITE_STORAGE -> Icons.Default.Save
    Permission.MANAGE_EXTERNAL_STORAGE -> Icons.Default.FolderOpen
    Permission.POST_NOTIFICATIONS -> Icons.Default.Notifications
    Permission.CAMERA -> Icons.Default.CameraAlt
}
