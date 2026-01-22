package com.example.conversion.presentation.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.conversion.navigation.Route

/**
 * Represents a navigation route item in the sidebar.
 * Maps navigation routes to their visual representation.
 * 
 * @param route The navigation route (from Route sealed interface)
 * @param id Unique identifier for the navigation item
 * @param label Display label for the navigation item
 * @param icon Icon to display (filled when active, outlined when inactive)
 * @param iconOutlined Outlined variant of the icon (for inactive state)
 * @param badge Optional badge text (e.g., "3" for notifications, "New" for new features)
 * @param badgeCount Optional numeric badge count
 * @param isVisible Whether this item should be shown in the navigation
 * @param category Category for grouping navigation items
 */
data class NavigationRoute(
    val route: Route,
    val id: String,
    val label: String,
    val icon: ImageVector,
    val iconOutlined: ImageVector,
    val badge: String? = null,
    val badgeCount: Int? = null,
    val isVisible: Boolean = true,
    val category: NavigationCategory = NavigationCategory.PRIMARY
)

/**
 * Categories for organizing navigation items.
 */
enum class NavigationCategory {
    PRIMARY,        // Main features (Home, File Selection, etc.)
    TOOLS,          // Helper tools (AI, Regex, Metadata, OCR)
    MANAGEMENT,     // Settings, Monitoring, Tags, Templates
    INTEGRATION,    // Cloud, Account, Activity Log
    HISTORY         // History and recent operations
}

/**
 * Complete navigation structure for the application.
 * Defines all available navigation routes with their properties.
 */
object NavigationRoutes {
    
    // ========== PRIMARY NAVIGATION ==========
    
    val HOME = NavigationRoute(
        route = Route.Home,
        id = "home",
        label = "Home",
        icon = Icons.Filled.Home,
        iconOutlined = Icons.Outlined.Home,
        category = NavigationCategory.PRIMARY
    )
    
    val FILE_SELECTION = NavigationRoute(
        route = Route.FileSelection,
        id = "file_selection",
        label = "Select Files",
        icon = Icons.Filled.Folder,
        iconOutlined = Icons.Outlined.Folder,
        category = NavigationCategory.PRIMARY
    )
    
    val RENAME_CONFIG = NavigationRoute(
        route = Route.RenameConfig(),
        id = "rename_config",
        label = "Rename Config",
        icon = Icons.Filled.Edit,
        iconOutlined = Icons.Outlined.Edit,
        category = NavigationCategory.PRIMARY
    )
    
    val PREVIEW = NavigationRoute(
        route = Route.Preview,
        id = "preview",
        label = "Preview",
        icon = Icons.Filled.Visibility,
        iconOutlined = Icons.Outlined.Visibility,
        category = NavigationCategory.PRIMARY
    )
    
    val FOLDER_SELECTOR = NavigationRoute(
        route = Route.FolderSelector,
        id = "folder_selector",
        label = "Folders",
        icon = Icons.Filled.FolderOpen,
        iconOutlined = Icons.Outlined.FolderOpen,
        category = NavigationCategory.PRIMARY
    )
    
    // ========== TOOLS ==========
    
    val AI_SUGGESTIONS = NavigationRoute(
        route = Route.AISuggestions,
        id = "ai_suggestions",
        label = "AI Suggestions",
        icon = Icons.Filled.AutoAwesome,
        iconOutlined = Icons.Outlined.AutoAwesome,
        badge = "AI",
        category = NavigationCategory.TOOLS
    )
    
    val REGEX_BUILDER = NavigationRoute(
        route = Route.RegexBuilder,
        id = "regex_builder",
        label = "Regex Builder",
        icon = Icons.Filled.Code,
        iconOutlined = Icons.Outlined.Code,
        category = NavigationCategory.TOOLS
    )
    
    val METADATA_PICKER = NavigationRoute(
        route = Route.MetadataPicker,
        id = "metadata_picker",
        label = "Metadata",
        icon = Icons.Filled.Info,
        iconOutlined = Icons.Outlined.Info,
        category = NavigationCategory.TOOLS
    )
    
    val OCR = NavigationRoute(
        route = Route.OCR,
        id = "ocr",
        label = "OCR Scanner",
        icon = Icons.Filled.DocumentScanner,
        iconOutlined = Icons.Outlined.DocumentScanner,
        category = NavigationCategory.TOOLS
    )
    
    val QR_SCANNER = NavigationRoute(
        route = Route.QRScanner,
        id = "qr_scanner",
        label = "QR Scanner",
        icon = Icons.Filled.QrCode,
        iconOutlined = Icons.Outlined.QrCode,
        category = NavigationCategory.TOOLS
    )
    
    // ========== MANAGEMENT ==========
    
    val MONITORING = NavigationRoute(
        route = Route.Monitoring,
        id = "monitoring",
        label = "Monitoring",
        icon = Icons.Filled.Monitor,
        iconOutlined = Icons.Outlined.Monitor,
        category = NavigationCategory.MANAGEMENT
    )
    
    val TAG_MANAGEMENT = NavigationRoute(
        route = Route.TagManagement,
        id = "tag_management",
        label = "Tags",
        icon = Icons.Filled.LocalOffer,
        iconOutlined = Icons.Outlined.LocalOffer,
        category = NavigationCategory.MANAGEMENT
    )
    
    val TEMPLATE_MANAGEMENT = NavigationRoute(
        route = Route.TemplateManagement,
        id = "template_management",
        label = "Templates",
        icon = Icons.Filled.ViewAgenda,
        iconOutlined = Icons.Outlined.ViewAgenda,
        category = NavigationCategory.MANAGEMENT
    )
    
    val SETTINGS = NavigationRoute(
        route = Route.Settings,
        id = "settings",
        label = "Settings",
        icon = Icons.Filled.Settings,
        iconOutlined = Icons.Outlined.Settings,
        category = NavigationCategory.MANAGEMENT
    )
    
    // ========== INTEGRATION ==========
    
    val CLOUD_SYNC = NavigationRoute(
        route = Route.CloudSync,
        id = "cloud_sync",
        label = "Cloud Sync",
        icon = Icons.Filled.Cloud,
        iconOutlined = Icons.Outlined.Cloud,
        category = NavigationCategory.INTEGRATION
    )
    
    val ACCOUNT = NavigationRoute(
        route = Route.Account,
        id = "account",
        label = "Account",
        icon = Icons.Filled.AccountCircle,
        iconOutlined = Icons.Outlined.AccountCircle,
        category = NavigationCategory.INTEGRATION
    )
    
    val ACTIVITY_LOG = NavigationRoute(
        route = Route.ActivityLog,
        id = "activity_log",
        label = "Activity Log",
        icon = Icons.Filled.History,
        iconOutlined = Icons.Outlined.History,
        category = NavigationCategory.INTEGRATION
    )
    
    // ========== HISTORY ==========
    
    val HISTORY = NavigationRoute(
        route = Route.History,
        id = "history",
        label = "History",
        icon = Icons.Filled.RestoreFromTrash,
        iconOutlined = Icons.Outlined.RestoreFromTrash,
        category = NavigationCategory.HISTORY
    )
    
    // ========== ALL ROUTES ==========
    
    /**
     * Complete list of all navigation routes.
     * Organized by category for easy filtering and display.
     */
    val ALL_ROUTES = listOf(
        // Primary (shown first)
        HOME,
        FILE_SELECTION,
        FOLDER_SELECTOR,
        
        // Tools (helper features)
        AI_SUGGESTIONS,
        REGEX_BUILDER,
        METADATA_PICKER,
        OCR,
        QR_SCANNER,
        
        // Management
        MONITORING,
        TAG_MANAGEMENT,
        TEMPLATE_MANAGEMENT,
        
        // Integration
        CLOUD_SYNC,
        ACCOUNT,
        ACTIVITY_LOG,
        
        // History
        HISTORY,
        
        // Settings (shown last)
        SETTINGS
    )
    
    /**
     * Primary navigation items (most commonly used).
     */
    val PRIMARY_ROUTES = ALL_ROUTES.filter { it.category == NavigationCategory.PRIMARY }
    
    /**
     * Tool navigation items (helper features).
     */
    val TOOL_ROUTES = ALL_ROUTES.filter { it.category == NavigationCategory.TOOLS }
    
    /**
     * Management navigation items.
     */
    val MANAGEMENT_ROUTES = ALL_ROUTES.filter { it.category == NavigationCategory.MANAGEMENT }
    
    /**
     * Integration navigation items.
     */
    val INTEGRATION_ROUTES = ALL_ROUTES.filter { it.category == NavigationCategory.INTEGRATION }
    
    /**
     * History navigation items.
     */
    val HISTORY_ROUTES = ALL_ROUTES.filter { it.category == NavigationCategory.HISTORY }
    
    /**
     * Finds a navigation route by its ID.
     * 
     * @param id The unique identifier of the navigation item
     * @return The matching NavigationRoute or null if not found
     */
    fun findById(id: String): NavigationRoute? = ALL_ROUTES.find { it.id == id }
    
    /**
     * Finds a navigation route by its Route object.
     * 
     * @param route The navigation route to find
     * @return The matching NavigationRoute or null if not found
     */
    fun findByRoute(route: Route): NavigationRoute? {
        // Special handling for routes with parameters (like RenameConfig)
        return ALL_ROUTES.find { navRoute ->
            when {
                navRoute.route::class == route::class -> true
                else -> navRoute.route == route
            }
        }
    }
}
