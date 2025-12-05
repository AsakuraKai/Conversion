package com.example.conversion.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.presentation.batch.BatchProcessScreen
import com.example.conversion.presentation.folder.FolderSelectorScreen
import com.example.conversion.presentation.home.HomeScreen
import com.example.conversion.presentation.monitoring.MonitoringScreen
import com.example.conversion.presentation.preview.PreviewScreen
import com.example.conversion.presentation.renameconfig.RenameConfigScreen
import com.example.conversion.presentation.settings.SettingsScreen
import com.example.conversion.presentation.theme.DynamicThemeScreen

@Composable
fun ConversionNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onNavigateToSettings: () -> Unit
) {
    // State holders for passing complex objects between screens
    var selectedFiles by remember { mutableStateOf<List<FileItem>>(emptyList()) }
    var renameConfig by remember { mutableStateOf<RenameConfig?>(null) }
    
    NavHost(
        navController = navController,
        startDestination = Route.Home,
        modifier = modifier
    ) {
        composable<Route.Home> {
            HomeScreen(
                onNavigateToBatchProcess = {
                    navController.navigate(Route.BatchProcess)
                },
                onNavigateToFormatConverter = {
                    navController.navigate(Route.FormatConverter)
                },
                onNavigateToBookReader = {
                    navController.navigate(Route.BookReader)
                },
                onNavigateToSettings = onNavigateToSettings
            )
        }
        
        composable<Route.BatchProcess> {
            BatchProcessScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable<Route.Settings> {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable<Route.RenameConfig> {
            RenameConfigScreen(
                onNavigateToPreview = { config ->
                    renameConfig = config
                    navController.navigate(Route.Preview)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable<Route.Preview> {
            val config = renameConfig
            val files = selectedFiles
            
            if (config != null && files.isNotEmpty()) {
                PreviewScreen(
                    files = files,
                    config = config,
                    onNavigateToRenameProgress = { filesToRename, finalConfig ->
                        // TODO: Navigate to rename progress screen when implemented
                        // For now, just navigate back to home
                        navController.popBackStack(Route.Home, inclusive = false)
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            } else {
                // No config or files available, navigate back
                navController.popBackStack(Route.Home, inclusive = false)
            }
        }
        
        composable<Route.FolderSelector> {
            FolderSelectorScreen(
                onFolderSelected = { folder ->
                    // TODO: Pass selected folder to rename configuration
                    // For now, just navigate back
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable<Route.Monitoring> {
            MonitoringScreen(
                onNavigateToSettings = {
                    navController.navigate(Route.Settings)
                }
            )
        }
        
        composable<Route.DynamicTheme> {
            DynamicThemeScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Placeholder routes for future features
        composable<Route.FormatConverter> {
            // FormatConverterScreen - Coming soon
        }
        
        composable<Route.BookReader> {
            // BookReaderScreen - Coming soon
        }
    }
}
