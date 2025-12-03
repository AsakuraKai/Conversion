package com.example.conversion.presentation.folder.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.conversion.ui.theme.ConversionTheme

/**
 * Create folder dialog component
 * 
 * Allows user to enter a name for a new folder.
 * Validates folder name and provides feedback.
 * 
 * @param onDismiss Called when dialog is dismissed without creating
 * @param onCreate Called when user confirms folder creation with valid name
 */
@Composable
fun CreateFolderDialog(
    onDismiss: () -> Unit = {},
    onCreate: (String) -> Unit = {}
) {
    var folderName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Validate folder name
    fun validateFolderName(name: String): String? {
        return when {
            name.isBlank() -> "Folder name cannot be empty"
            name.length > 255 -> "Folder name is too long (max 255 characters)"
            name.contains(Regex("[/\\\\:*?\"<>|]")) -> "Folder name contains invalid characters"
            name.startsWith(".") -> "Folder name cannot start with a dot"
            name.endsWith(".") -> "Folder name cannot end with a dot"
            name.trim() != name -> "Folder name cannot start or end with spaces"
            name.uppercase() in listOf("CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4", 
                "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", 
                "LPT5", "LPT6", "LPT7", "LPT8", "LPT9") -> "This is a reserved system name"
            else -> null
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.CreateNewFolder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text("Create New Folder")
        },
        text = {
            Column {
                Text(
                    text = "Enter a name for the new folder",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.padding(8.dp))
                
                OutlinedTextField(
                    value = folderName,
                    onValueChange = { 
                        folderName = it
                        errorMessage = validateFolderName(it)
                    },
                    label = { Text("Folder Name") },
                    placeholder = { Text("e.g., My Photos") },
                    isError = errorMessage != null,
                    supportingText = {
                        if (errorMessage != null) {
                            Text(
                                text = errorMessage!!,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (errorMessage == null && folderName.isNotBlank()) {
                                onCreate(folderName)
                            }
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val error = validateFolderName(folderName)
                    if (error == null) {
                        onCreate(folderName)
                    } else {
                        errorMessage = error
                    }
                },
                enabled = folderName.isNotBlank() && errorMessage == null
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Previews
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CreateFolderDialogPreview() {
    ConversionTheme {
        CreateFolderDialog()
    }
}
