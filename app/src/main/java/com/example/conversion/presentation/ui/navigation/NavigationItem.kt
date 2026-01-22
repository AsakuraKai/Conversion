package com.example.conversion.presentation.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Folder
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
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    // Animate icon scale for feedback
    val iconScale by animateFloatAsState(
        targetValue = if (isCollapsed) 1.1f else 1f,
        animationSpec = tween(durationMillis = ANIMATION_DURATION_MS),
        label = "icon_scale_animation"
    )

    val semanticDescription = contentDescription ?: label

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(ITEM_SHAPE)
            .background(backgroundColor)
            .clickable(
                onClick = onClick,
                indication = ripple(bounded = true),
                interactionSource = androidx.compose.foundation.interaction.MutableInteractionSource()
            )
            .semantics {
                role = Role.Button
                selected = isSelected
                this.contentDescription = semanticDescription
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
                enter = fadeIn(animationSpec = tween(durationMillis = ANIMATION_DURATION_MS)),
                exit = fadeOut(animationSpec = tween(durationMillis = ANIMATION_DURATION_MS))
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

// Design tokens
private val ITEM_PADDING_EXPANDED = 16.dp
private val ITEM_PADDING_COLLAPSED = 12.dp
private val ITEM_PADDING_VERTICAL = 12.dp
private val ICON_SIZE = 24.dp
private val ITEM_SHAPE = RoundedCornerShape(12.dp)
private const val ANIMATION_DURATION_MS = 300

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

@Preview(name = "Expanded Item - Dark", showBackground = true)
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

@Preview(name = "Collapsed Item - Dark", showBackground = true)
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
