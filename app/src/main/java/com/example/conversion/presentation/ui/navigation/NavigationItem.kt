package com.example.conversion.presentation.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.uiMode
import androidx.compose.ui.unit.dp
import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import com.example.conversion.ui.theme.ConversionTheme

/**
 * Navigation Item Component
 * 
 * A navigation item that displays an icon and optional label based on sidebar state.
 * Supports active/inactive states, badges, and tooltips for collapsed state.
 * 
 * @param icon The icon to display
 * @param label The label text
 * @param isCollapsed Whether the sidebar is collapsed
 * @param isSelected Whether this item is currently selected
 * @param onClick Callback when the item is clicked
 * @param modifier Modifier for customization
 * @param badgeCount Optional badge count to display
 * @param contentDescription Optional content description for accessibility
 */
@Composable
fun NavigationItem(
    icon: ImageVector,
    label: String,
    isCollapsed: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeCount: Int? = null,
    contentDescription: String? = null
) {
    // Track hover state for desktop/mouse interactions
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.secondaryContainer
        isHovered -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    // Animate icon scale for feedback
    val iconScale by animateFloatAsState(
        targetValue = if (isCollapsed) 1.1f else 1f,
        animationSpec = tween(
            durationMillis = ANIMATION_DURATION_MS,
            easing = EaseInOutCubic
        ),
        label = "icon_scale_animation"
    )

    val semanticDescription = contentDescription ?: label
    val stateDesc = when {
        isSelected -> "Selected"
        else -> "Not selected"
    } + if (badgeCount != null && badgeCount > 0) {
        ", $badgeCount unread items"
    } else ""

    // Tooltip for collapsed state
    val tooltipState = rememberTooltipState()
    
    val itemContent: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(ITEM_SHAPE)
                .background(backgroundColor)
                .hoverable(interactionSource = interactionSource)
                .clickable(
                    onClick = onClick,
                    indication = ripple(bounded = true),
                    interactionSource = interactionSource
                )
                .semantics {
                    role = Role.Button
                    selected = isSelected
                    this.contentDescription = semanticDescription
                    this.stateDescription = stateDesc
                }
                .padding(
                    horizontal = if (isCollapsed) ITEM_PADDING_COLLAPSED else ITEM_PADDING_EXPANDED,
                    vertical = ITEM_PADDING_VERTICAL
                ),
            contentAlignment = if (isCollapsed) Alignment.Center else Alignment.CenterStart
        ) {
            if (isCollapsed) {
                // Collapsed state: Icon only with optional badge
                BadgedBox(
                    badge = {
                        if (badgeCount != null && badgeCount > 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ) {
                                Text(
                                    text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = semanticDescription,
                        tint = contentColor,
                        modifier = Modifier
                            .size(ICON_SIZE)
                            .scale(iconScale)
                    )
                }
            } else {
                // Expanded state: Icon + Label with optional badge
                AnimatedVisibility(
                    visible = !isCollapsed,
                    enter = fadeIn(animationSpec = tween(
                        durationMillis = ANIMATION_DURATION_MS,
                        easing = EaseInOutCubic
                    )),
                    exit = fadeOut(animationSpec = tween(
                        durationMillis = ANIMATION_DURATION_MS,
                        easing = EaseInOutCubic
                    ))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        BadgedBox(
                            badge = {
                                if (badgeCount != null && badgeCount > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError
                                    ) {
                                        Text(
                                            text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null, // Label provides context
                                tint = contentColor,
                                modifier = Modifier.size(ICON_SIZE)
                            )
                        }
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge,
                            color = contentColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    // Wrap with tooltip only when collapsed
    if (isCollapsed) {
        TooltipBox(
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
            tooltip = {
                PlainTooltip {
                    Text(label)
                }
            },
            state = tooltipState,
            modifier = modifier
        ) {
            itemContent()
        }
    } else {
        Box(modifier = modifier) {
            itemContent()
        }
    }
}

// Design tokens
private val ITEM_PADDING_EXPANDED = 16.dp
private val ITEM_PADDING_COLLAPSED = 12.dp
private val ITEM_PADDING_VERTICAL = 12.dp
private val ICON_SIZE = 24.dp
private val ITEM_SHAPE = RoundedCornerShape(12.dp)
private const val ANIMATION_DURATION_MS = 300

// EaseInOutCubic easing function for smooth animations
private val EaseInOutCubic = CubicBezierEasing(0.645f, 0.045f, 0.355f, 1.0f)

// Preview compositions
@Preview(name = "Expanded Item - Selected", showBackground = true)
@Composable
private fun NavigationItemExpandedSelectedPreview() {
    ConversionTheme {
        NavigationItem(
            icon = Icons.Default.Home,
            label = "Home",
            isCollapsed = false,
            isSelected = true,
            onClick = {}
        )
    }
}

@Preview(name = "Expanded Item - Unselected", showBackground = true)
@Composable
private fun NavigationItemExpandedUnselectedPreview() {
    ConversionTheme {
        NavigationItem(
            icon = Icons.Default.Settings,
            label = "Settings",
            isCollapsed = false,
            isSelected = false,
            onClick = {}
        )
    }
}

@Preview(name = "Collapsed Item - Selected", showBackground = true)
@Composable
private fun NavigationItemCollapsedSelectedPreview() {
    ConversionTheme {
        NavigationItem(
            icon = Icons.Default.Home,
            label = "Home",
            isCollapsed = true,
            isSelected = true,
            onClick = {}
        )
    }
}

@Preview(name = "Collapsed Item - With Badge", showBackground = true)
@Composable
private fun NavigationItemCollapsedWithBadgePreview() {
    ConversionTheme {
        NavigationItem(
            icon = Icons.Default.Folder,
            label = "Files",
            isCollapsed = true,
            isSelected = false,
            onClick = {},
            badgeCount = 5
        )
    }
}

@Preview(
    name = "Expanded Item - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun NavigationItemExpandedDarkPreview() {
    ConversionTheme {
        NavigationItem(
            icon = Icons.Default.Home,
            label = "Home",
            isCollapsed = false,
            isSelected = true,
            onClick = {}
        )
    }
}

@Preview(
    name = "Collapsed Item - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun NavigationItemCollapsedDarkPreview() {
    ConversionTheme {
        NavigationItem(
            icon = Icons.Default.Settings,
            label = "Settings",
            isCollapsed = true,
            isSelected = false,
            onClick = {}
        )
    }
}
