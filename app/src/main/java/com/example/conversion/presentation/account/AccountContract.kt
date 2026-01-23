package com.example.conversion.presentation.account

import com.example.conversion.domain.model.SyncStatus

/**
 * UI state for the Account screen
 * 
 * Manages account authentication state, sync status, and synced data summary.
 * Since this is a mock implementation (no real Firebase), the signed-in state
 * is simulated for development purposes.
 * 
 * @property isSignedIn Whether user is authenticated (mock: always true for dev)
 * @property currentUser Current user information (mock data)
 * @property syncStatus Real-time sync operation status
 * @property syncedTemplatesCount Number of templates synced to cloud
 * @property syncedTagsCount Number of tags synced to cloud
 * @property settingsSynced Whether user settings are synced
 * @property isLoading Loading state for async operations
 * @property errorMessage Error message to display, if any
 */
data class AccountUiState(
    val isSignedIn: Boolean = true, // Mock: always signed in for dev
    val currentUser: MockUser = MockUser.DEFAULT,
    val syncStatus: SyncStatus = SyncStatus.IDLE,
    val syncedTemplatesCount: Int = 0,
    val syncedTagsCount: Int = 0,
    val settingsSynced: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    /**
     * Returns true if sync is currently in progress
     */
    val isSyncing: Boolean
        get() = syncStatus.isSyncing
    
    /**
     * Returns true if there's a sync error
     */
    val hasSyncError: Boolean
        get() = syncStatus.hasError
    
    /**
     * Formatted last sync time message
     */
    val lastSyncMessage: String
        get() = when {
            syncStatus.lastSyncTime == null -> "Never synced"
            else -> {
                val minutes = (System.currentTimeMillis() - syncStatus.lastSyncTime) / (60 * 1000)
                when {
                    minutes < 1 -> "Just now"
                    minutes < 60 -> "$minutes minutes ago"
                    else -> {
                        val hours = minutes / 60
                        "$hours hours ago"
                    }
                }
            }
        }
    
    /**
     * Total count of synced items
     */
    val totalSyncedItems: Int
        get() = syncedTemplatesCount + syncedTagsCount + if (settingsSynced) 1 else 0
}

/**
 * Mock user data for development
 * In production, this would come from Firebase Authentication
 */
data class MockUser(
    val id: String,
    val email: String,
    val displayName: String,
    val photoUrl: String?
) {
    companion object {
        val DEFAULT = MockUser(
            id = "mock_user_001",
            email = "dev@conversion.app",
            displayName = "Development User",
            photoUrl = null
        )
    }
}

/**
 * One-time events for the Account screen
 */
sealed class AccountEvent {
    /**
     * Show a toast message
     */
    data class ShowToast(val message: String) : AccountEvent()
    
    /**
     * Show an error message
     */
    data class ShowError(val error: String) : AccountEvent()
    
    /**
     * Navigate to sign-in screen (mock: not implemented)
     */
    data object NavigateToSignIn : AccountEvent()
    
    /**
     * Sign out completed successfully
     */
    data object SignOutCompleted : AccountEvent()
}

/**
 * User actions/intents for the Account screen
 */
sealed class AccountAction {
    /**
     * Trigger manual sync of user preferences
     */
    data object SyncNow : AccountAction()
    
    /**
     * Sign in to account (mock: simulated)
     */
    data object SignIn : AccountAction()
    
    /**
     * Sign out from account (mock: simulated)
     */
    data object SignOut : AccountAction()
    
    /**
     * Refresh account data and sync status
     */
    data object RefreshData : AccountAction()
    
    /**
     * Clear sync error message
     */
    data object ClearError : AccountAction()
}
