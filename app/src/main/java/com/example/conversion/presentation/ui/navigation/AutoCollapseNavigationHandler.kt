package com.example.conversion.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.conversion.presentation.viewmodel.SidebarNavigationViewModel
import android.content.res.Configuration

/**
 * Navigation helper that handles auto-collapse behavior after navigation.
 * 
 * Monitors navigation events and automatically collapses the sidebar
 * on phone devices when enabled.
 * 
 * @param navController The navigation controller to observe
 * @param viewModel The sidebar navigation ViewModel
 */
@Composable
fun AutoCollapseNavigationHandler(
    navController: NavController,
    viewModel: SidebarNavigationViewModel
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val isPhone = screenWidthDp < 600.dp
    
    val state by viewModel.state.collectAsState()

    // Observe navigation changes
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            // Only auto-collapse on phone devices and if enabled
            if (isPhone && state.autoCollapseOnNavigation) {
                viewModel.onNavigationComplete()
            }
        }
    }
}

/**
 * Determines if auto-collapse should be enabled based on device type.
 * 
 * @return True if auto-collapse should be enabled by default
 */
@Composable
fun shouldEnableAutoCollapse(): Boolean {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val isPhone = screenWidthDp < 600.dp
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    
    // Enable auto-collapse on phones in portrait mode by default
    return isPhone && isPortrait
}
