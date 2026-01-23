package com.example.conversion.presentation.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * Dialog shown on first app launch to educate users about auto-backup.
 * Explains the importance of backing up files before rename operations.
 *
 * @param onDismiss Callback when user dismisses the dialog
 * @param onConfirm Callback when user acknowledges the information
 */
@Composable
fun FirstLaunchEducationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit = onDismiss
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icon
                Icon(
                    imageVector = Icons.Default.Backup,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                // Title
                Text(
                    text = "Welcome to File Conversion!",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                
                // Message
                Text(
                    text = "Auto-Backup is enabled by default to protect your files during rename operations.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                HorizontalDivider()
                
                // Info section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Why Auto-Backup?",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        
                        Text(
                            text = "• Protects original files from accidental loss\n" +
                                    "• Allows you to revert changes if needed\n" +
                                    "• Creates backup copies before operations",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                HorizontalDivider()
                
                // Note
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "You can change this setting anytime in Settings > File Operations",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                // Action button
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Got it!")
                }
            }
        }
    }
}
