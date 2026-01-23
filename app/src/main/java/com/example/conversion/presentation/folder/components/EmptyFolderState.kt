package com.example.conversion.presentation.folder.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.conversion.ui.theme.ConversionTheme

/**
 * Empty folder state component
 * 
 * Displayed when a folder contains no subfolders.
 * Provides option to create a new folder if not at root level.
 * 
 * @param onCreateFolder Called when user wants to create a new folder
 * @param canCreateFolder Whether folder creation is allowed (false at root level)
 */
@Composable
fun EmptyFolderState(
    onCreateFolder: () -> Unit = {},
    canCreateFolder: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Folder icon
        Icon(
            imageVector = Icons.Default.FolderOpen,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
            modifier = Modifier.size(80.dp)
        )
        
        Spacer(modifier = Modifier.padding(16.dp))
        
        // Empty message
        Text(
            text = "No folders here",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.padding(8.dp))
        
        Text(
            text = if (canCreateFolder) {
                "This folder is empty.\nCreate a new folder to get started."
            } else {
                "No storage locations found.\nPlease check your device storage."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        // Create folder button
        if (canCreateFolder) {
            Spacer(modifier = Modifier.padding(16.dp))
            
            Button(onClick = onCreateFolder) {
                Icon(
                    imageVector = Icons.Default.CreateNewFolder,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Text("Create Folder")
            }
        }
    }
}

// Previews
@Preview(name = "Light Mode - Can Create")
@Preview(name = "Dark Mode - Can Create", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun EmptyFolderStatePreview() {
    ConversionTheme {
        EmptyFolderState(
            canCreateFolder = true
        )
    }
}

@Preview(name = "Light Mode - Root Level")
@Preview(name = "Dark Mode - Root Level", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun EmptyFolderStateRootPreview() {
    ConversionTheme {
        EmptyFolderState(
            canCreateFolder = false
        )
    }
}
