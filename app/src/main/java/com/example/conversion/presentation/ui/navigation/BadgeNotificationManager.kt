package com.example.conversion.presentation.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.conversion.presentation.viewmodel.SidebarNavigationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Badge notification manager for sidebar navigation items.
 * 
 * Provides convenient methods for updating badge notifications
 * based on application events.
 * 
 * Example usage:
 * ```
 * badgeManager.notifyHistoryUpdate(3) // Show 3 new history items
 * badgeManager.notifyCloudSync(true) // Show sync indicator
 * badgeManager.clearBadge("history") // Clear history badge
 * ```
 */
class BadgeNotificationManager(
    private val sidebarViewModel: SidebarNavigationViewModel
) {

    /**
     * Updates the history badge with new item count.
     * 
     * @param count Number of new history items
     */
    fun notifyHistoryUpdate(count: Int) {
        sidebarViewModel.updateBadge("history", count)
    }

    /**
     * Updates the cloud sync badge.
     * 
     * @param hasPendingSync Whether there are pending sync operations
     */
    fun notifyCloudSync(hasPendingSync: Boolean) {
        if (hasPendingSync) {
            sidebarViewModel.updateBadge("cloud_sync", 1)
        } else {
            sidebarViewModel.updateBadge("cloud_sync", null)
        }
    }

    /**
     * Updates the monitoring badge with active monitors count.
     * 
     * @param activeCount Number of active folder monitors
     */
    fun notifyActiveMonitors(activeCount: Int) {
        sidebarViewModel.updateBadge("monitoring", activeCount)
    }

    /**
     * Updates the activity log badge with new events.
     * 
     * @param eventCount Number of new events
     */
    fun notifyNewEvents(eventCount: Int) {
        sidebarViewModel.updateBadge("activity_log", eventCount)
    }

    /**
     * Shows a "New" badge on AI suggestions.
     * 
     * @param isNew Whether to show the new badge
     */
    fun notifyAISuggestionsNew(isNew: Boolean) {
        if (isNew) {
            sidebarViewModel.updateBadge("ai_suggestions", 1)
        } else {
            sidebarViewModel.updateBadge("ai_suggestions", null)
        }
    }

    /**
     * Updates notification settings badge.
     * 
     * @param hasUnreadNotifications Whether there are unread notifications
     */
    fun notifySettings(hasUnreadNotifications: Boolean) {
        if (hasUnreadNotifications) {
            sidebarViewModel.updateBadge("settings", 1)
        } else {
            sidebarViewModel.updateBadge("settings", null)
        }
    }

    /**
     * Clears a specific badge.
     * 
     * @param itemId The ID of the navigation item
     */
    fun clearBadge(itemId: String) {
        sidebarViewModel.updateBadge(itemId, null)
    }

    /**
     * Clears all badges.
     */
    fun clearAllBadges() {
        sidebarViewModel.clearAllBadges()
    }

    /**
     * Increments a badge count.
     * 
     * @param itemId The ID of the navigation item
     * @param increment Amount to increment (default 1)
     */
    fun incrementBadge(itemId: String, increment: Int = 1) {
        sidebarViewModel.incrementBadge(itemId, increment)
    }

    companion object {
        // Common badge item IDs
        const val HISTORY = "history"
        const val CLOUD_SYNC = "cloud_sync"
        const val MONITORING = "monitoring"
        const val ACTIVITY_LOG = "activity_log"
        const val AI_SUGGESTIONS = "ai_suggestions"
        const val SETTINGS = "settings"
    }
}

/**
 * Factory for creating BadgeNotificationManager instances.
 */
object BadgeNotificationManagerFactory {
    
    /**
     * Creates a BadgeNotificationManager from a SidebarNavigationViewModel.
     * 
     * @param viewModel The sidebar navigation ViewModel
     * @return BadgeNotificationManager instance
     */
    fun create(viewModel: SidebarNavigationViewModel): BadgeNotificationManager {
        return BadgeNotificationManager(viewModel)
    }
}
