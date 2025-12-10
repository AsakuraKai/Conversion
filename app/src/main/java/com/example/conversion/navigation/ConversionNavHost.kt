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
import com.example.conversion.presentation.fileselection.FileSelectionScreen
import com.example.conversion.presentation.folder.FolderSelectorScreen
import com.example.conversion.presentation.home.HomeScreen
import com.example.conversion.presentation.monitoring.MonitoringScreen
import com.example.conversion.presentation.preview.PreviewScreen
import com.example.conversion.presentation.renameconfig.RenameConfigScreen
import com.example.conversion.presentation.renameprogress.RenameProgressScreen
import com.example.conversion.presentation.permissions.PermissionHandler
import com.example.conversion.presentation.settings.SettingsScreen
import com.example.conversion.presentation.tag.TagManagementScreen
import com.example.conversion.presentation.template.TemplateScreen
import com.example.conversion.domain.model.Permission

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
                    navController.navigate(Route.FileSelection)
                },
                onNavigateToFormatConverter = {
                    navController.navigate(Route.FormatConverter)
                },
                onNavigateToBookReader = {
                    navController.navigate(Route.BookReader)
                },
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToTagManagement = {
                    navController.navigate(Route.TagManagement)
                },
                onNavigateToTemplateManagement = {
                    navController.navigate(Route.TemplateManagement)
                },
                onNavigateToMonitoring = {
                    navController.navigate(Route.Monitoring)
                }
            )
        }
        
        composable<Route.FileSelection> {
            PermissionHandler(
                permissions = Permission.getMediaPermissions(),
                rationaleMessage = "This app needs storage access to read and rename your files.",
                onPermissionsGranted = { /* Permissions granted, FileSelectionScreen will load */ },
                onPermissionsDenied = { deniedPermissions ->
                    // User denied permissions, show message and go back
                    navController.popBackStack()
                }
            ) {
                FileSelectionScreen(
                    onNavigateToRename = { files ->
                        selectedFiles = files
                        navController.navigate(Route.RenameConfig(fileCount = files.size))
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
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
                        selectedFiles = filesToRename
                        renameConfig = finalConfig
                        navController.navigate(Route.RenameProgress)
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
        
        composable<Route.RenameProgress> {
            val config = renameConfig
            val files = selectedFiles
            
            if (config != null && files.isNotEmpty()) {
                PermissionHandler(
                    permissions = listOf(
                        Permission.READ_IMAGES,
                        Permission.READ_VIDEOS,
                        Permission.READ_AUDIO,
                        Permission.WRITE_STORAGE
                    ),
                    rationaleMessage = "This app needs storage access to rename your files.",
                    onPermissionsGranted = { /* Permissions granted, proceed with rename */ },
                    onPermissionsDenied = { deniedPermissions ->
                        // User denied permissions, go back
                        selectedFiles = emptyList()
                        renameConfig = null
                        navController.popBackStack(Route.Home, inclusive = false)
                    }
                ) {
                    RenameProgressScreen(
                        files = files,
                        config = config,
                        onNavigateBack = {
                            // Clear state and return to home
                            selectedFiles = emptyList()
                            renameConfig = null
                            navController.popBackStack(Route.Home, inclusive = false)
                        }
                    )
                }
            } else {
                // No config or files available, navigate back
                navController.popBackStack(Route.Home, inclusive = false)
            }
        }
        
        composable<Route.FolderSelector> {
            PermissionHandler(
                permissions = Permission.getMediaPermissions(),
                rationaleMessage = "This app needs storage access to browse folders.",
                onPermissionsGranted = { /* Permissions granted */ },
                onPermissionsDenied = { deniedPermissions ->
                    navController.popBackStack()
                }
            ) {
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
        }
        
        composable<Route.Monitoring> {
            PermissionHandler(
                permissions = Permission.getAllRequiredPermissions(),
                rationaleMessage = "This app needs storage access and notification permission to monitor folders and notify you of changes.",
                onPermissionsGranted = { /* Permissions granted */ },
                onPermissionsDenied = { deniedPermissions ->
                    navController.popBackStack()
                }
            ) {
                MonitoringScreen(
                    onNavigateToSettings = {
                        navController.navigate(Route.Settings)
                    }
                )
            }
        }
        
        composable<Route.TagManagement> {
            TagManagementScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable<Route.TemplateManagement> {
            TemplateScreen(
                onNavigateBack = { config ->
                    // Handle optional config return
                    navController.popBackStack()
                },
                onBack = {
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
