package com.example.conversion.presentation.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.conversion.ui.theme.ConversionTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment

/**
 * Collapsible Sidebar Layout Component
 * 
 * A layout that combines a collapsible sidebar with main content area.
 * Handles responsive behavior across different screen sizes:
 * - Phone: Sidebar with scrim overlay on content
 * - Tablet: Always expanded sidebar without scrim
 * 
 * @param isCollapsed Whether the sidebar is collapsed
 * @param onContentClick Callback when main content is clicked (used to collapse sidebar)
 * @param modifier Modifier for customization
 * @param sidebarContent Content for the sidebar
 * @param mainContent Main content area
 */
@Composable
fun CollapsibleSidebarLayout(
    isCollapsed: Boolean,
    onContentClick: () -> Unit,
    modifier: Modifier = Modifier,
    sidebarContent: @Composable () -> Unit,
    mainContent: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp

    // Determine if device is tablet based on screen width
    val isTablet = screenWidthDp >= TABLET_BREAKPOINT

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Sidebar
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .zIndex(2f) // Ensure sidebar is above scrim
            ) {
                sidebarContent()
            }

            // Main content area with scrim overlay for phone/tablet when not collapsed
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // Main content
                mainContent()

                // Scrim overlay (only visible on phone/small screens when expanded)
                if (!isTablet && !isCollapsed) {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(durationMillis = SCRIM_ANIMATION_DURATION_MS)),
                        exit = fadeOut(animationSpec = tween(durationMillis = SCRIM_ANIMATION_DURATION_MS)),
                        modifier = Modifier.zIndex(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(SCRIM_COLOR)
                                .clickable(
                                    onClick = onContentClick,
                                    indication = null,
                                    interactionSource = androidx.compose.foundation.interaction.MutableInteractionSource()
                                )
                                .semantics {
                                    contentDescription = "Tap to collapse sidebar"
                                }
                        )
                    }
                }
            }
        }
    }
}

// Design tokens
private val TABLET_BREAKPOINT = 600.dp
private val SCRIM_COLOR = Color.Black.copy(alpha = 0.5f)
private const val SCRIM_ANIMATION_DURATION_MS = 200

// Preview compositions
@Preview(name = "Layout - Phone Expanded", showBackground = true, widthDp = 360)
@Composable
private fun CollapsibleSidebarLayoutPhoneExpandedPreview() {
    ConversionTheme {
        CollapsibleSidebarLayout(
            isCollapsed = false,
            onContentClick = {},
            sidebarContent = {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sidebar (Expanded)")
                }
            },
            mainContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Main Content")
                }
            }
        )
    }
}

@Preview(name = "Layout - Phone Collapsed", showBackground = true, widthDp = 360)
@Composable
private fun CollapsibleSidebarLayoutPhoneCollapsedPreview() {
    ConversionTheme {
        CollapsibleSidebarLayout(
            isCollapsed = true,
            onContentClick = {},
            sidebarContent = {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("S")
                }
            },
            mainContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Main Content")
                }
            }
        )
    }
}

@Preview(name = "Layout - Tablet", showBackground = true, widthDp = 800)
@Composable
private fun CollapsibleSidebarLayoutTabletPreview() {
    ConversionTheme {
        CollapsibleSidebarLayout(
            isCollapsed = false,
            onContentClick = {},
            sidebarContent = {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sidebar (Always Expanded)")
                }
            },
            mainContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Main Content (No Scrim)")
                }
            }
        )
    }
}

@Preview(name = "Layout - Dark Theme", showBackground = true, widthDp = 360)
@Composable
private fun CollapsibleSidebarLayoutDarkPreview() {
    ConversionTheme {
        CollapsibleSidebarLayout(
            isCollapsed = false,
            onContentClick = {},
            sidebarContent = {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sidebar")
                }
            },
            mainContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Main Content")
                }
            }
        )
    }
}
