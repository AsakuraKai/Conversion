package com.example.conversion.presentation.renameconfig.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.File

/**
 * Destination Folder Picker Component.
 * Allows users to select an output directory for batch rename operations.
 */
@Composable
fun DestinationFolderPicker(
    selectedFolder: File?,
    onFolderSelect: () -> Unit,
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
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Destination Folder",
                    style = MaterialTheme.typography.titleMedium
                )
                
                IconButton(onClick = onFolderSelect) {
                    Icon(
                        imageVector = if (selectedFolder != null) {
                            Icons.Default.FolderOpen
                        } else {
                            Icons.Default.Folder
                        },
                        contentDescription = "Select destination folder",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onFolderSelect),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = selectedFolder?.path ?: "Tap to select folder (defaults to original location)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (selectedFolder != null) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            if (selectedFolder == null) {
                Text(
                    text = "Files will be renamed in their original locations",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
