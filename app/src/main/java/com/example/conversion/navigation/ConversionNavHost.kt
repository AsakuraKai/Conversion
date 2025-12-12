package com.example.conversion.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.presentation.account.AccountScreen
import com.example.conversion.presentation.activity.ActivityLogScreen
import com.example.conversion.presentation.ai.AISuggestionsScreen
import com.example.conversion.presentation.cloud.CloudSyncScreen
import com.example.conversion.presentation.fileselection.FileSelectionScreen
import com.example.conversion.presentation.folder.FolderSelectorScreen
import com.example.conversion.presentation.history.HistoryScreen
import com.example.conversion.presentation.home.HomeScreen
import com.example.conversion.presentation.metadata.MetadataPickerScreen
import com.example.conversion.presentation.monitoring.MonitoringScreen
import com.example.conversion.presentation.ocr.OCRScreen
import com.example.conversion.presentation.preview.PreviewScreen
import com.example.conversion.presentation.qr.QRDisplayScreen
import com.example.conversion.presentation.qr.QRScannerScreen
import com.example.conversion.presentation.regex.RegexBuilderScreen
import com.example.conversion.presentation.renameconfig.RenameConfigScreen
import com.example.conversion.presentation.renameconfig.RenameConfigViewModel
import com.example.conversion.presentation.renameconfig.RenameConfigContract.Action
import com.example.conversion.presentation.renameprogress.RenameProgressScreen
import com.example.conversion.presentation.permissions.PermissionHandler
import com.example.conversion.presentation.settings.SettingsScreen
import com.example.conversion.presentation.tag.TagManagementScreen
import com.example.conversion.presentation.template.TemplateScreen
import com.example.conversion.domain.model.Permission
import android.Manifest
import kotlinx.coroutines.launch

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
                },
                onNavigateToHistory = {
                    navController.navigate(Route.History)
                },
                onNavigateToAISuggestions = {
                    navController.navigate(Route.AISuggestions)
                },
                onNavigateToCloudSync = {
                    navController.navigate(Route.CloudSync)
                },
                onNavigateToAccount = {
                    navController.navigate(Route.Account)
                },
                onNavigateToActivityLog = {
                    navController.navigate(Route.ActivityLog)
                },
                onNavigateToQRDisplay = {
                    navController.navigate(Route.QRDisplay)
                },
                onNavigateToQRScanner = {
                    navController.navigate(Route.QRScanner)
                }
            )
        }
        
        composable<Route.FileSelection> {
            PermissionHandler(
                permissions = Permission.getMediaPermissions(),
                rationaleMessage = "Batch rename your photos, videos, and audio files effortlessly. Grant access to organize your media library.",
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
                },
                onNavigateToCloudSync = {
                    navController.navigate(Route.CloudSync)
                },
                onNavigateToAccount = {
                    navController.navigate(Route.Account)
                },
                onNavigateToActivityLog = {
                    navController.navigate(Route.ActivityLog)
                },
                onNavigateToHistory = {
                    navController.navigate(Route.History)
                },
                onNavigateToPermissions = {
                    navController.navigate(Route.PermissionsManagement)
                }
            )
        }
        
        composable<Route.PermissionsManagement> {
            com.example.conversion.presentation.permissions.PermissionsManagementScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable<Route.RenameConfig> { backStackEntry ->
            val viewModel: RenameConfigViewModel = hiltViewModel()
            
            // Listen for data from helper tools
            LaunchedEffect(Unit) {
                launch {
                    backStackEntry.savedStateHandle.getStateFlow("ai_suggestion", "").collect { suggestion ->
                        if (suggestion.isNotEmpty()) {
                            viewModel.handleAction(Action.UpdatePrefix(suggestion))
                            backStackEntry.savedStateHandle.remove<String>("ai_suggestion")
                        }
                    }
                }
                launch {
                    backStackEntry.savedStateHandle.getStateFlow("regex_pattern", "").collect { pattern ->
                        if (pattern.isNotEmpty()) {
                            viewModel.handleAction(Action.UpdatePrefix(pattern))
                            backStackEntry.savedStateHandle.remove<String>("regex_pattern")
                        }
                    }
                }
                launch {
                    backStackEntry.savedStateHandle.getStateFlow("metadata_variable", "").collect { variable ->
                        if (variable.isNotEmpty()) {
                            // Insert variable at cursor position in prefix
                            viewModel.handleAction(Action.UpdatePrefix(variable))
                            backStackEntry.savedStateHandle.remove<String>("metadata_variable")
                        }
                    }
                }
                launch {
                    backStackEntry.savedStateHandle.getStateFlow("ocr_text", "").collect { text ->
                        if (text.isNotEmpty()) {
                            viewModel.handleAction(Action.UpdatePrefix(text))
                            backStackEntry.savedStateHandle.remove<String>("ocr_text")
                        }
                    }
                }
            }
            
            RenameConfigScreen(
                viewModel = viewModel,
                onNavigateToPreview = { config ->
                    renameConfig = config
                    navController.navigate(Route.Preview)
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAISuggestions = {
                    navController.navigate(Route.AISuggestions)
                },
                onNavigateToRegexBuilder = {
                    navController.navigate(Route.RegexBuilder)
                },
                onNavigateToMetadataPicker = {
                    navController.navigate(Route.MetadataPicker)
                },
                onNavigateToOCR = {
                    navController.navigate(Route.OCR)
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
                    rationaleMessage = "Save your renamed files permanently. Storage access ensures your changes are preserved.",
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
                rationaleMessage = "Choose where to save your files. Access your folders to organize your content exactly where you want it.",
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
                rationaleMessage = "Stay updated on file changes automatically. Get instant notifications when your folders are modified.",
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
        
        // ========== SMART FEATURES ==========
        
        composable<Route.History> {
            HistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }
        
        composable<Route.AISuggestions> {
            val parentEntry = remember(it) {
                navController.currentBackStack.value
                    .firstOrNull { entry -> entry.destination.route == Route.RenameConfig::class.qualifiedName }
            }
            AISuggestionsScreen(
                onNavigateBackWithSuggestion = { suggestion ->
                    parentEntry?.savedStateHandle?.set("ai_suggestion", suggestion)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable<Route.RegexBuilder> {
            val parentEntry = remember(it) {
                navController.currentBackStack.value
                    .firstOrNull { entry -> entry.destination.route == Route.RenameConfig::class.qualifiedName }
            }
            RegexBuilderScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateBackWithPattern = { pattern ->
                    parentEntry?.savedStateHandle?.set("regex_pattern", pattern)
                    navController.popBackStack()
                }
            )
        }
        
        composable<Route.MetadataPicker> {
            val parentEntry = remember(it) {
                navController.currentBackStack.value
                    .firstOrNull { entry -> entry.destination.route == Route.RenameConfig::class.qualifiedName }
            }
            MetadataPickerScreen(
                onNavigateBack = { variable ->
                    parentEntry?.savedStateHandle?.set("metadata_variable", variable)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable<Route.OCR> {
            val parentEntry = remember(it) {
                navController.currentBackStack.value
                    .firstOrNull { entry -> entry.destination.route == Route.RenameConfig::class.qualifiedName }
            }
            OCRScreen(
                onNavigateBackWithText = { text ->
                    parentEntry?.savedStateHandle?.set("ocr_text", text)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
        
        // ========== INTEGRATION FEATURES ==========
        
        composable<Route.CloudSync> {
            CloudSyncScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable<Route.Account> {
            AccountScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable<Route.ActivityLog> {
            ActivityLogScreen(
                onBack = { navController.popBackStack() }
            )
        }
        
        composable<Route.QRDisplay> {
            // QRDisplay requires a RenameTemplate parameter
            // For now, navigate back if accessed directly
            // TODO: Pass template from calling screen
            navController.popBackStack()
        }
        
        composable<Route.QRScanner> {
            PermissionHandler(
                permissions = listOf(Permission.CAMERA),
                rationaleMessage = "Import Reusable Templates instantly. Scan QR codes to share and reuse your favorite naming patterns.",
                onPermissionsGranted = { /* Permissions granted, proceed with scanner */ },
                onPermissionsDenied = { deniedPermissions ->
                    // User denied camera permission, go back
                    navController.popBackStack()
                }
            ) {
                QRScannerScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onTemplateImported = { template ->
                        // TODO: Handle imported template
                        // For now, just navigate back
                        navController.popBackStack()
                    },
                    onPickImage = {
                        // TODO: Handle image picker for QR code from gallery
                    }
                )
            }
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
