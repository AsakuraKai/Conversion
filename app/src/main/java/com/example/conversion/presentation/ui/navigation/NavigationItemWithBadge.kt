package com.example.conversion.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.conversion.presentation.viewmodel.SidebarNavigationViewModel

/**
 * Navigation item wrapper that integrates with badge system.
 * 
 * Automatically displays badges from the ViewModel state.
 * 
 * @param icon The icon to display
 * @param label The label text
 * @param itemId The unique identifier for this navigation item
 * @param isCollapsed Whether the sidebar is collapsed
 * @param isSelected Whether this item is currently selected
 * @param onClick Callback when the item is clicked
 * @param viewModel The sidebar navigation ViewModel (for badge support)
 * @param modifier Modifier for customization
 * @param contentDescription Optional content description for accessibility
 */
@Composable
fun NavigationItemWithBadge(
    icon: ImageVector,
    label: String,
    itemId: String,
    isCollapsed: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    viewModel: SidebarNavigationViewModel,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val state by viewModel.state.collectAsState()
    val badgeCount = state.badges[itemId]

    NavigationItem(
        icon = icon,
        label = label,
        isCollapsed = isCollapsed,
        isSelected = isSelected,
        onClick = {
            onClick()
            // Clear badge when item is clicked
            if (badgeCount != null) {
                viewModel.updateBadge(itemId, null)
            }
        },
        modifier = modifier,
        badgeCount = badgeCount,
        contentDescription = contentDescription
    )
}

/**
 * Extension function to get badge count for a navigation item.
 * 
 * @param itemId The ID of the navigation item
 * @return Badge count or null if no badge
 */
@Composable
fun SidebarNavigationViewModel.getBadgeCount(itemId: String): Int? {
    val state by this.state.collectAsState()
    return state.badges[itemId]
}

/**
 * Extension function to check if a navigation item has a badge.
 * 
 * @param itemId The ID of the navigation item
 * @return True if the item has a badge
 */
@Composable
fun SidebarNavigationViewModel.hasBadge(itemId: String): Boolean {
    val state by this.state.collectAsState()
    return state.badges.containsKey(itemId) && (state.badges[itemId] ?: 0) > 0
}
