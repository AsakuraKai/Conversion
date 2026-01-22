package com.example.conversion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.conversion.navigation.ConversionNavHost
import com.example.conversion.navigation.Route
import com.example.conversion.presentation.model.NavigationRoutes
import com.example.conversion.presentation.settings.SettingsViewModel
import com.example.conversion.presentation.ui.navigation.CollapsibleNavigationDrawer
import com.example.conversion.presentation.ui.navigation.CollapsibleSidebarLayout
import com.example.conversion.presentation.ui.navigation.NavigationItem
import com.example.conversion.presentation.ui.navigation.SidebarHeader
import com.example.conversion.presentation.viewmodel.SidebarNavigationViewModel
import com.example.conversion.ui.theme.ConversionTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Get theme preferences from Settings ViewModel
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()
            
            // Get sidebar navigation state
            val sidebarViewModel: SidebarNavigationViewModel = hiltViewModel()
            val sidebarState by sidebarViewModel.state.collectAsStateWithLifecycle()
            
            ConversionTheme(
                themeMode = settingsState.preferences.themeMode,
                dynamicColor = settingsState.preferences.useDynamicColors
            ) {
                val navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStackEntry?.destination?.route
                
                // Find the current navigation item based on route
                // Check against SIDEBAR_ROUTES to ensure we only match sidebar-visible items
                val currentNavRoute = NavigationRoutes.SIDEBAR_ROUTES.find { navRoute ->
                    when (val route = currentBackStackEntry?.destination?.route) {
                        null -> false
                        else -> {
                            // Handle route matching (simple string comparison for now)
                            navRoute.route::class.simpleName == route.substringBefore("/")
                        }
                    }
                } ?: NavigationRoutes.HOME
                
                CollapsibleSidebarLayout(
                    isCollapsed = sidebarState.isCollapsed,
                    onContentClick = {
                        // Collapse sidebar when content is clicked (phone/tablet behavior)
                        sidebarViewModel.collapseSidebar()
                    },
                    modifier = Modifier.fillMaxSize(),
                    sidebarContent = {
                        CollapsibleNavigationDrawer(
                            isCollapsed = sidebarState.isCollapsed,
                            content = {
                                Column {
                                    // Sidebar header
                                    SidebarHeader(
                                        isCollapsed = sidebarState.isCollapsed
                                    )
                                    
                                    // Navigation items - only show sidebar-visible routes
                                    LazyColumn {
                                        items(NavigationRoutes.SIDEBAR_ROUTES) { navRoute ->
                                            NavigationItem(
                                                icon = if (currentNavRoute.id == navRoute.id) navRoute.icon else navRoute.iconOutlined,
                                                label = navRoute.label,
                                                isCollapsed = sidebarState.isCollapsed,
                                                isSelected = currentNavRoute.id == navRoute.id,
                                                badgeCount = navRoute.badgeCount,
                                                onClick = {
                                                    // Navigate to selected route
                                                    navController.navigate(navRoute.route)
                                                    sidebarViewModel.selectNavigationItem(navRoute.id)
                                                    sidebarViewModel.onNavigationComplete()
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    },
                    mainContent = {
                        ConversionNavHost(
                            navController = navController,
                            onNavigateToSettings = {
                                navController.navigate(Route.Settings)
                            }
                        )
                    }
                )
            }
        }
    }
}