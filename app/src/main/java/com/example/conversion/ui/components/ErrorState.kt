package com.example.conversion.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.conversion.ui.theme.ConversionTheme

/**
 * Error state component for displaying error messages with optional retry action.
 *
 * Provides user-friendly error feedback with icon, title, message, and action buttons.
 * Follows Material 3 design guidelines for error states.
 *
 * @param title The error title
 * @param message The detailed error message
 * @param icon The icon to display (defaults to error icon)
 * @param primaryActionLabel Label for the primary action button (e.g., "Retry")
 * @param onPrimaryAction Callback when primary action is clicked
 * @param secondaryActionLabel Optional label for secondary action (e.g., "Cancel")
 * @param onSecondaryAction Optional callback when secondary action is clicked
 * @param modifier Optional modifier for the component
 */
@Composable
fun ErrorState(
    title: String,
    message: String,
    icon: ImageVector = Icons.Outlined.Error,
    primaryActionLabel: String? = null,
    onPrimaryAction: (() -> Unit)? = null,
    secondaryActionLabel: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null, // Decorative
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        if (primaryActionLabel != null && onPrimaryAction != null) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (secondaryActionLabel != null && onSecondaryAction != null) {
                    OutlinedButton(
                        onClick = onSecondaryAction,
                        modifier = Modifier
                            .height(48.dp)
                            .widthIn(min = 100.dp)
                    ) {
                        Text(text = secondaryActionLabel)
                    }
                }
                
                Button(
                    onClick = onPrimaryAction,
                    modifier = Modifier
                        .height(48.dp)
                        .widthIn(min = 100.dp)
                ) {
                    Text(text = primaryActionLabel)
                }
            }
        }
    }
}

/**
 * Inline error message component for displaying errors within a section.
 */
@Composable
fun InlineError(
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.errorContainer,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(24.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            
            if (actionLabel != null && onAction != null) {
                TextButton(onClick = onAction) {
                    Text(
                        text = actionLabel,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

/**
 * Compact error state for smaller areas like bottom sheets.
 */
@Composable
fun CompactErrorState(
    title: String,
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onAction,
                modifier = Modifier.height(40.dp)
            ) {
                Text(text = actionLabel)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorStatePreview() {
    ConversionTheme {
        ErrorState(
            title = "Operation Failed",
            message = "Failed to rename files. Please check permissions and try again.",
            primaryActionLabel = "Retry",
            onPrimaryAction = {},
            secondaryActionLabel = "Cancel",
            onSecondaryAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorStateWithoutActionsPreview() {
    ConversionTheme {
        ErrorState(
            title = "Network Error",
            message = "Unable to connect to the server. Please check your internet connection."
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InlineErrorPreview() {
    ConversionTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            InlineError(
                message = "Some files could not be renamed due to permission issues.",
                actionLabel = "Details",
                onAction = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CompactErrorStatePreview() {
    ConversionTheme {
        CompactErrorState(
            title = "Upload Failed",
            message = "Could not upload file to cloud storage",
            actionLabel = "Retry",
            onAction = {}
        )
    }
}
