package com.example.conversion.presentation.folder.components

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.conversion.domain.model.FolderInfo
import com.example.conversion.ui.theme.ConversionTheme

/**
 * Folder list item component
 * 
 * Displays a folder with its name, file count, and subfolder count.
 * Supports click to navigate and long click to select.
 * 
 * @param folder The folder to display
 * @param isSelected Whether this folder is currently selected
 * @param onClick Called when folder is tapped (navigate into folder)
 * @param onLongClick Called when folder is long-pressed (select as destination)
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FolderListItem(
    folder: FolderInfo,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Folder icon
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.secondary
                },
                modifier = Modifier.size(40.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Folder info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Folder name
                Text(
                    text = folder.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Content summary
                if (folder.totalItems > 0) {
                    Text(
                        text = folder.contentSummary,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                } else {
                    Text(
                        text = "Empty folder",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        }
                    )
                }
            }
        }
    }
}

// Previews
@Preview(name = "Light Mode - Normal")
@Preview(name = "Dark Mode - Normal", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FolderListItemPreview() {
    ConversionTheme {
        FolderListItem(
            folder = FolderInfo(
                uri = android.net.Uri.parse("file:///storage/emulated/0/Pictures"),
                path = "/storage/emulated/0/Pictures",
                name = "Pictures",
                fileCount = 150,
                subfolderCount = 12,
                parentPath = "/storage/emulated/0",
                isRoot = false
            ),
            isSelected = false
        )
    }
}

@Preview(name = "Light Mode - Selected")
@Preview(name = "Dark Mode - Selected", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FolderListItemSelectedPreview() {
    ConversionTheme {
        FolderListItem(
            folder = FolderInfo(
                uri = android.net.Uri.parse("file:///storage/emulated/0/DCIM"),
                path = "/storage/emulated/0/DCIM",
                name = "Camera",
                fileCount = 42,
                subfolderCount = 3,
                parentPath = "/storage/emulated/0",
                isRoot = false
            ),
            isSelected = true
        )
    }
}

@Preview(name = "Light Mode - Empty")
@Composable
private fun FolderListItemEmptyPreview() {
    ConversionTheme {
        FolderListItem(
            folder = FolderInfo(
                uri = android.net.Uri.parse("file:///storage/emulated/0/NewFolder"),
                path = "/storage/emulated/0/NewFolder",
                name = "NewFolder",
                fileCount = 0,
                subfolderCount = 0,
                parentPath = "/storage/emulated/0",
                isRoot = false
            ),
            isSelected = false
        )
    }
}
