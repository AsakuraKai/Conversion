package com.example.conversion.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.conversion.presentation.batch.BatchProcessScreen
import com.example.conversion.presentation.folder.FolderSelectorScreen
import com.example.conversion.presentation.home.HomeScreen
import com.example.conversion.presentation.settings.SettingsScreen

@Composable
fun ConversionNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onNavigateToSettings: () -> Unit
) {
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
        
        // Placeholder routes for future features
        composable<Route.FormatConverter> {
            // FormatConverterScreen - Coming soon
        }
        
        composable<Route.BookReader> {
            // BookReaderScreen - Coming soon
        }
    }
}
