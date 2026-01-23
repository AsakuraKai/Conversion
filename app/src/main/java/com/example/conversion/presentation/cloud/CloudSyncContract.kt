package com.example.conversion.presentation.cloud

import com.example.conversion.domain.model.CloudProvider
import com.example.conversion.domain.model.SyncConfig
import com.example.conversion.domain.model.SyncProgress
import com.example.conversion.domain.model.SyncStatus

/**
 * Cloud Sync feature contract defining State, Events, and Actions.
 * Follows MVI pattern for unidirectional data flow.
 */
object CloudSyncContract {

    /**
     * UI State for cloud sync screen.
     */
    data class State(
        val syncConfig: SyncConfig = SyncConfig(),
        val syncStatus: SyncStatus = SyncStatus.IDLE,
        val syncProgress: SyncProgress? = null,
        val isLoading: Boolean = false,
        val connectingProviders: Set<CloudProvider> = emptySet(),
        val error: String? = null,
        val connectedProviders: Set<CloudProvider> = emptySet()
    ) {
        /**
         * Whether any cloud provider is connected.
         */
        val hasConnectedProvider: Boolean
            get() = connectedProviders.isNotEmpty()

        /**
         * Whether sync is currently in progress.
         */
        val isSyncing: Boolean
            get() = syncStatus.isSyncing

        /**
         * Whether the selected provider is connected.
         */
        fun isProviderConnected(provider: CloudProvider): Boolean {
            return connectedProviders.contains(provider)
        }

        /**
         * Whether a specific provider is currently connecting.
         */
        fun isProviderConnecting(provider: CloudProvider): Boolean {
            return connectingProviders.contains(provider)
        }

        /**
         * Whether auto-sync is enabled and configured.
         */
        val isAutoSyncEnabled: Boolean
            get() = syncConfig.isConfigured && syncConfig.autoSync
    }

    /**
     * One-time events sent from ViewModel to UI.
     */
    sealed class Event {
        /**
         * Show a message to the user.
         */
        data class ShowMessage(val message: String) : Event()

        /**
         * Show error message.
         */
        data class ShowError(val error: String) : Event()

        /**
         * Authentication successful.
         */
        data class AuthenticationSuccess(val provider: CloudProvider) : Event()

        /**
         * Authentication failed.
         */
        data class AuthenticationFailed(val provider: CloudProvider, val error: String) : Event()

        /**
         * Sync completed successfully.
         */
        data class SyncSuccess(val fileCount: Int) : Event()

        /**
         * Sync failed.
         */
        data class SyncFailed(val error: String) : Event()

        /**
         * Navigate to provider authentication page.
         */
        data class NavigateToAuth(val provider: CloudProvider) : Event()
    }

    /**
     * User actions that can be performed.
     */
    sealed class Action {
        /**
         * Connect to a cloud provider.
         */
        data class ConnectProvider(val provider: CloudProvider) : Action()

        /**
         * Disconnect from a cloud provider.
         */
        data class DisconnectProvider(val provider: CloudProvider) : Action()

        /**
         * Update auto-sync setting.
         */
        data class UpdateAutoSync(val enabled: Boolean) : Action()

        /**
         * Update sync interval.
         */
        data class UpdateSyncInterval(val intervalMinutes: Int) : Action()

        /**
         * Update WiFi-only setting.
         */
        data class UpdateWifiOnly(val wifiOnly: Boolean) : Action()

        /**
         * Update backup enabled setting.
         */
        data class UpdateBackupEnabled(val enabled: Boolean) : Action()

        /**
         * Trigger manual sync.
         */
        data object ManualSync : Action()

        /**
         * Retry failed sync.
         */
        data object RetrySync : Action()

        /**
         * Dismiss error.
         */
        data object DismissError : Action()

        /**
         * Load configuration.
         */
        data object LoadConfig : Action()
    }
}
