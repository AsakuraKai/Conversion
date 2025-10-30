package com.example.conversion.presentation.settings

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.conversion.domain.model.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    // Handle one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SettingsEvent.ShowToast -> {
                    // In a real app, show a Snackbar or Toast
                    android.widget.Toast.makeText(context, event.message, android.widget.Toast.LENGTH_SHORT).show()
                }
                is SettingsEvent.ShowError -> {
                    android.widget.Toast.makeText(context, event.error, android.widget.Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Theme Section
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Theme Mode",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        ThemeMode.entries.forEach { mode ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = state.preferences.themeMode == mode,
                                    onClick = {
                                        viewModel.onAction(SettingsAction.UpdateThemeMode(mode))
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = mode.name.lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                        
                        // Dynamic Colors option (Android 12+)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Dynamic Colors",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Use colors from your wallpaper",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = state.preferences.useDynamicColors,
                                    onCheckedChange = { enabled ->
                                        viewModel.onAction(SettingsAction.UpdateDynamicColors(enabled))
                                    }
                                )
                            }
                        }
                    }
                }
                
                // App Info Section
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InfoRow("Version", "1.0.0")
                        InfoRow("Build", "Phase 1 - Architecture Foundation")
                    }
                }
                
                // Error message
                state.errorMessage?.let { error ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
