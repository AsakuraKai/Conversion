package com.example.conversion.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderOpen
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
 * Empty state component for displaying when there is no content to show.
 *
 * Provides user-friendly feedback with icon, title, description, and optional action button.
 * Follows Material 3 design guidelines for empty states.
 *
 * @param icon The icon to display at the center
 * @param title The main title text
 * @param description The descriptive text explaining the empty state
 * @param actionLabel Optional label for the action button
 * @param onAction Optional callback when action button is clicked
 * @param modifier Optional modifier for the component
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
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
            tint = MaterialTheme.colorScheme.outline
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
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onAction,
                modifier = Modifier
                    .height(48.dp)
                    .widthIn(min = 120.dp)
            ) {
                Text(text = actionLabel)
            }
        }
    }
}

/**
 * Compact variant of EmptyState for smaller areas like bottom sheets or dialogs.
 */
@Composable
fun CompactEmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outline
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
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStatePreview() {
    ConversionTheme {
        EmptyState(
            icon = Icons.Outlined.FolderOpen,
            title = "No Files Selected",
            description = "Select files from your gallery to get started with batch renaming",
            actionLabel = "Select Files",
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStateWithoutActionPreview() {
    ConversionTheme {
        EmptyState(
            icon = Icons.Outlined.FolderOpen,
            title = "No Recent Activity",
            description = "Your rename operations will appear here"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CompactEmptyStatePreview() {
    ConversionTheme {
        CompactEmptyState(
            icon = Icons.Outlined.FolderOpen,
            title = "No Templates",
            description = "Create your first template to get started"
        )
    }
}
