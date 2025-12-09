package com.example.conversion.presentation.account

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.conversion.domain.model.SyncStatus
import com.example.conversion.ui.theme.ConversionTheme

/**
 * Card displaying account information
 * 
 * @param user Current user information
 * @param onSignOut Callback when sign out button is clicked
 */
@Composable
fun AccountInfoCard(
    user: MockUser,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Column {
                    Text(
                        text = user.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
            
            HorizontalDivider()
            
            OutlinedButton(
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out")
            }
        }
    }
}

/**
 * Card displaying sync status and manual sync button
 * 
 * @param syncStatus Current sync status
 * @param lastSyncMessage Formatted message showing last sync time
 * @param onManualSync Callback when manual sync button is clicked
 */
@Composable
fun SyncStatusCard(
    syncStatus: SyncStatus,
    lastSyncMessage: String,
    onManualSync: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CloudSync,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Sync Status",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            HorizontalDivider()
            
            // Sync status indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when {
                    syncStatus.isSyncing -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Syncing...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    syncStatus.hasError -> {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Column {
                            Text(
                                text = "Sync failed",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            syncStatus.error?.let { error ->
                                Text(
                                    text = error,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                    syncStatus.hasSynced -> {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = "Synced",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = lastSyncMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                    else -> {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "Not synced",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
            
            // Manual sync button
            Button(
                onClick = onManualSync,
                modifier = Modifier.fillMaxWidth(),
                enabled = !syncStatus.isSyncing
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (syncStatus.isSyncing) "Syncing..." else "Sync Now")
            }
        }
    }
}

/**
 * Card displaying summary of synced data
 * 
 * @param templatesCount Number of synced templates
 * @param tagsCount Number of synced tags
 * @param settingsSynced Whether settings are synced
 * @param totalItems Total number of synced items
 */
@Composable
fun SyncedDataCard(
    templatesCount: Int,
    tagsCount: Int,
    settingsSynced: Boolean,
    totalItems: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Storage,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Synced Data",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            HorizontalDivider()
            
            // Total items
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Items",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = totalItems.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            HorizontalDivider()
            
            // Templates
            SyncedDataItem(
                icon = Icons.Default.Description,
                label = "Templates",
                count = templatesCount
            )
            
            // Tags
            SyncedDataItem(
                icon = Icons.Default.Label,
                label = "Tags",
                count = tagsCount
            )
            
            // Settings
            SyncedDataItem(
                icon = Icons.Default.Settings,
                label = "Settings",
                isSynced = settingsSynced
            )
        }
    }
}

/**
 * Individual synced data item
 */
@Composable
private fun SyncedDataItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    count: Int? = null,
    isSynced: Boolean? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        when {
            count != null -> {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            isSynced != null -> {
                Icon(
                    imageVector = if (isSynced) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = null,
                    tint = if (isSynced) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }
        }
    }
}

/**
 * Card prompting user to sign in
 * 
 * @param onSignIn Callback when sign in button is clicked
 */
@Composable
fun SignInPromptCard(
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
            )
            
            Text(
                text = "Sign in to sync your data",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Text(
                text = "Keep your templates, tags, and settings synced across all your devices",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
            
            Button(
                onClick = onSignIn,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Login,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign In")
            }
        }
    }
}

/**
 * Card displaying error message
 * 
 * @param errorMessage Error message to display
 * @param onDismiss Callback when dismiss button is clicked
 */
@Composable
fun ErrorCard(
    errorMessage: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

// Previews
@Preview(showBackground = true)
@Composable
private fun AccountInfoCardPreview() {
    ConversionTheme {
        AccountInfoCard(
            user = MockUser.DEFAULT,
            onSignOut = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SyncStatusCardSyncingPreview() {
    ConversionTheme {
        SyncStatusCard(
            syncStatus = SyncStatus.SYNCING,
            lastSyncMessage = "Never synced",
            onManualSync = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SyncStatusCardSyncedPreview() {
    ConversionTheme {
        SyncStatusCard(
            syncStatus = SyncStatus(
                isSyncing = false,
                lastSyncTime = System.currentTimeMillis() - 5 * 60 * 1000,
                error = null
            ),
            lastSyncMessage = "5 minutes ago",
            onManualSync = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SyncStatusCardErrorPreview() {
    ConversionTheme {
        SyncStatusCard(
            syncStatus = SyncStatus(
                isSyncing = false,
                lastSyncTime = null,
                error = "Network connection failed"
            ),
            lastSyncMessage = "Never synced",
            onManualSync = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SyncedDataCardPreview() {
    ConversionTheme {
        SyncedDataCard(
            templatesCount = 5,
            tagsCount = 12,
            settingsSynced = true,
            totalItems = 18
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SignInPromptCardPreview() {
    ConversionTheme {
        SignInPromptCard(
            onSignIn = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorCardPreview() {
    ConversionTheme {
        ErrorCard(
            errorMessage = "Sync failed: Network connection lost",
            onDismiss = {}
        )
    }
}
