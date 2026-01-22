package com.example.conversion.presentation.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.content.res.Configuration
import com.example.conversion.ui.theme.ConversionTheme

/**
 * Adaptive Collapsible Sidebar Component
 * 
 * Provides responsive behavior across different device sizes and orientations:
 * 
 * Phone Portrait:
 * - Sidebar can be collapsed/expanded
 * - Shows scrim overlay when expanded
 * - Auto-collapses on navigation
 * 
 * Phone Landscape:
 * - Sidebar starts collapsed
 * - Can be expanded with scrim
 * - Limited vertical space optimization
 * 
 * Tablet Portrait/Landscape:
 * - Sidebar always expanded
 * - No scrim overlay
 * - Persistent navigation visibility
 * 
 * @param isCollapsed Current collapsed state (for phone devices)
 * @param onCollapseChange Callback when collapse state changes
 * @param modifier Modifier for customization
 * @param sidebarContent Content to display in the sidebar
 * @param mainContent Main content area
 */
@Composable
fun AdaptiveCollapsibleSidebar(
    isCollapsed: Boolean,
    onCollapseChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    sidebarContent: @Composable (isCollapsed: Boolean) -> Unit,
    mainContent: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Determine device type based on screen width
    val deviceType = when {
        screenWidthDp >= TABLET_BREAKPOINT -> DeviceType.TABLET
        else -> DeviceType.PHONE
    }

    // Determine effective collapse state based on device type
    val effectiveIsCollapsed = when (deviceType) {
        DeviceType.TABLET -> false // Always expanded on tablet
        DeviceType.PHONE -> when {
            isLandscape -> true // Prefer collapsed in landscape for more horizontal space
            else -> isCollapsed // Use provided state in portrait
        }
    }

    val semanticDesc = when (deviceType) {
        DeviceType.TABLET -> "Adaptive sidebar layout, tablet mode, always expanded"
        DeviceType.PHONE -> if (isLandscape) {
            "Adaptive sidebar layout, phone landscape mode"
        } else {
            "Adaptive sidebar layout, phone portrait mode"
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics {
                contentDescription = semanticDesc
            }
    ) {
        CollapsibleSidebarLayout(
            isCollapsed = effectiveIsCollapsed,
            onContentClick = {
                // Only allow collapse on phone devices
                if (deviceType == DeviceType.PHONE && !isLandscape) {
                    onCollapseChange(true)
                }
            },
            sidebarContent = {
                sidebarContent(effectiveIsCollapsed)
            },
            mainContent = mainContent
        )
    }
}

/**
 * Device type classification for responsive behavior
 */
private enum class DeviceType {
    PHONE,
    TABLET
}

// Design tokens
private val TABLET_BREAKPOINT = 600.dp

// Preview compositions
@Preview(
    name = "Phone Portrait - Expanded",
    showBackground = true,
    widthDp = 360,
    heightDp = 640
)
@Composable
private fun AdaptiveCollapsibleSidebarPhonePortraitExpandedPreview() {
    ConversionTheme {
        var isCollapsed by remember { mutableStateOf(false) }
        
        AdaptiveCollapsibleSidebar(
            isCollapsed = isCollapsed,
            onCollapseChange = { isCollapsed = it },
            sidebarContent = { collapsed ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (collapsed) "Collapsed" else "Expanded",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            },
            mainContent = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Main Content\nPhone Portrait",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        )
    }
}

@Preview(
    name = "Phone Portrait - Collapsed",
    showBackground = true,
    widthDp = 360,
    heightDp = 640
)
@Composable
private fun AdaptiveCollapsibleSidebarPhonePortraitCollapsedPreview() {
    ConversionTheme {
        var isCollapsed by remember { mutableStateOf(true) }
        
        AdaptiveCollapsibleSidebar(
            isCollapsed = isCollapsed,
            onCollapseChange = { isCollapsed = it },
            sidebarContent = { collapsed ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (collapsed) "Collapsed" else "Expanded",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            },
            mainContent = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Main Content\nPhone Portrait",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        )
    }
}

@Preview(
    name = "Phone Landscape",
    showBackground = true,
    widthDp = 640,
    heightDp = 360
)
@Composable
private fun AdaptiveCollapsibleSidebarPhoneLandscapePreview() {
    ConversionTheme {
        var isCollapsed by remember { mutableStateOf(true) }
        
        AdaptiveCollapsibleSidebar(
            isCollapsed = isCollapsed,
            onCollapseChange = { isCollapsed = it },
            sidebarContent = { collapsed ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (collapsed) "Collapsed" else "Expanded",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            },
            mainContent = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Main Content\nPhone Landscape",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        )
    }
}

@Preview(
    name = "Tablet Portrait",
    showBackground = true,
    widthDp = 768,
    heightDp = 1024
)
@Composable
private fun AdaptiveCollapsibleSidebarTabletPortraitPreview() {
    ConversionTheme {
        var isCollapsed by remember { mutableStateOf(false) }
        
        AdaptiveCollapsibleSidebar(
            isCollapsed = isCollapsed,
            onCollapseChange = { isCollapsed = it },
            sidebarContent = { collapsed ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Always Expanded\nTablet",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            },
            mainContent = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Main Content\nTablet Portrait",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        )
    }
}

@Preview(
    name = "Tablet Landscape",
    showBackground = true,
    widthDp = 1024,
    heightDp = 768
)
@Composable
private fun AdaptiveCollapsibleSidebarTabletLandscapePreview() {
    ConversionTheme {
        var isCollapsed by remember { mutableStateOf(false) }
        
        AdaptiveCollapsibleSidebar(
            isCollapsed = isCollapsed,
            onCollapseChange = { isCollapsed = it },
            sidebarContent = { collapsed ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Always Expanded\nTablet",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            },
            mainContent = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Main Content\nTablet Landscape",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        )
    }
}
