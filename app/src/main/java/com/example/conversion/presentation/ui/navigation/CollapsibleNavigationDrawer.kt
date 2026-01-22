package com.example.conversion.presentation.ui.navigation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.conversion.ui.theme.ConversionTheme

/**
 * Collapsible Navigation Drawer Component
 * 
 * A persistent navigation drawer that can be collapsed to icon-only view
 * or expanded to show labels. Never hidden, always visible.
 * 
 * @param isCollapsed Whether the drawer is in collapsed (icon-only) state
 * @param modifier Modifier for customization
 * @param content Content to display inside the drawer (navigation items)
 */
@Composable
fun CollapsibleNavigationDrawer(
    isCollapsed: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Animate width smoothly with 300ms duration and EaseInOutCubic easing
    val drawerWidth by animateDpAsState(
        targetValue = if (isCollapsed) DRAWER_WIDTH_COLLAPSED else DRAWER_WIDTH_EXPANDED,
        animationSpec = tween(
            durationMillis = ANIMATION_DURATION_MS,
            easing = androidx.compose.animation.core.FastOutSlowInEasing
        ),
        label = "drawer_width_animation"
    )

    Surface(
        modifier = modifier
            .width(drawerWidth)
            .fillMaxHeight()
            .semantics {
                contentDescription = if (isCollapsed) {
                    "Collapsed navigation drawer"
                } else {
                    "Expanded navigation drawer"
                }
            },
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = DRAWER_ELEVATION,
        shadowElevation = DRAWER_SHADOW_ELEVATION,
        shape = RoundedCornerShape(
            topEnd = DRAWER_CORNER_RADIUS,
            bottomEnd = DRAWER_CORNER_RADIUS
        )
    ) {
        Box(
            modifier = Modifier
                .border(
                    width = DRAWER_BORDER_WIDTH,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(
                        topEnd = DRAWER_CORNER_RADIUS,
                        bottomEnd = DRAWER_CORNER_RADIUS
                    )
                )
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(
                        topEnd = DRAWER_CORNER_RADIUS,
                        bottomEnd = DRAWER_CORNER_RADIUS
                    )
                )
        ) {
            Column(
                modifier = Modifier.fillMaxHeight()
            ) {
                content()
            }
        }
    }
}

// Design tokens
private val DRAWER_WIDTH_EXPANDED = 240.dp
private val DRAWER_WIDTH_COLLAPSED = 72.dp
private val DRAWER_ELEVATION = 1.dp
private val DRAWER_SHADOW_ELEVATION = 4.dp
private val DRAWER_CORNER_RADIUS = 16.dp
private val DRAWER_BORDER_WIDTH = 1.dp
private const val ANIMATION_DURATION_MS = 300

// Preview compositions
@Preview(name = "Expanded Drawer - Light", showBackground = true)
@Composable
private fun CollapsibleNavigationDrawerExpandedPreview() {
    ConversionTheme {
        CollapsibleNavigationDrawer(
            isCollapsed = false
        ) {
            // Preview content placeholder
        }
    }
}

@Preview(name = "Collapsed Drawer - Light", showBackground = true)
@Composable
private fun CollapsibleNavigationDrawerCollapsedPreview() {
    ConversionTheme {
        CollapsibleNavigationDrawer(
            isCollapsed = true
        ) {
            // Preview content placeholder
        }
    }
}

@Preview(name = "Expanded Drawer - Dark", showBackground = true)
@Composable
private fun CollapsibleNavigationDrawerExpandedDarkPreview() {
    ConversionTheme {
        CollapsibleNavigationDrawer(
            isCollapsed = false
        ) {
            // Preview content placeholder
        }
    }
}

@Preview(name = "Collapsed Drawer - Dark", showBackground = true)
@Composable
private fun CollapsibleNavigationDrawerCollapsedDarkPreview() {
    ConversionTheme {
        CollapsibleNavigationDrawer(
            isCollapsed = true
        ) {
            // Preview content placeholder
        }
    }
}
