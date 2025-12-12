package com.example.conversion.presentation.common.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Navigation Drawer for the app.
 * Provides access to major app sections:
 * - Home
 * - Batch Processing
 * - QR Functions
 * - History & Logs
 * - Settings
 * - About
 */
@Composable
fun AppNavigationDrawer(
    currentRoute: String?,
    onNavigateToHome: () -> Unit,
    onNavigateToBatchProcess: () -> Unit,
    onNavigateToQRFunctions: () -> Unit,
    onNavigateToActivityLog: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp)
        ) {
            // Drawer Header
            DrawerHeader()
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Divider()
            
            // Navigation Items
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Home, contentDescription = null) },
                label = { Text("Home") },
                selected = currentRoute == "Home",
                onClick = {
                    onNavigateToHome()
                    onCloseDrawer()
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.DriveFileRenameOutline, contentDescription = null) },
                label = { Text("Batch Processing") },
                selected = currentRoute == "BatchProcess",
                onClick = {
                    onNavigateToBatchProcess()
                    onCloseDrawer()
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.QrCode, contentDescription = null) },
                label = { Text("QR Functions") },
                selected = currentRoute == "QRFunctions",
                onClick = {
                    onNavigateToQRFunctions()
                    onCloseDrawer()
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // History & Logs Section
            Text(
                text = "History & Logs",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp)
            )
            
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.History, contentDescription = null) },
                label = { Text("Operation History") },
                selected = currentRoute == "History",
                onClick = {
                    onNavigateToHistory()
                    onCloseDrawer()
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.List, contentDescription = null) },
                label = { Text("Activity Log") },
                selected = currentRoute == "ActivityLog",
                onClick = {
                    onNavigateToActivityLog()
                    onCloseDrawer()
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Divider()
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Bottom Items
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                label = { Text("Settings") },
                selected = currentRoute == "Settings",
                onClick = {
                    onNavigateToSettings()
                    onCloseDrawer()
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Info, contentDescription = null) },
                label = { Text("About") },
                selected = currentRoute == "About",
                onClick = {
                    onNavigateToAbout()
                    onCloseDrawer()
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}

@Composable
private fun DrawerHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.DriveFileRenameOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            Column {
                Text(
                    text = "Daten Sequence",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "File Management",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
