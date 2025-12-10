package com.example.conversion.presentation.cloud

import androidx.lifecycle.viewModelScope
import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.model.CloudProvider
import com.example.conversion.domain.model.SyncConfig
import com.example.conversion.domain.model.SyncStatus
import com.example.conversion.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing cloud sync state and handling cloud operations.
 * Follows MVI pattern with State, Events, and Actions.
 * 
 * MOCK IMPLEMENTATION: Uses simulated cloud operations for UI development.
 */
@HiltViewModel
class CloudSyncViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<CloudSyncContract.State, CloudSyncContract.Event>(
    initialState = CloudSyncContract.State()
) {

    init {
        // Load initial configuration
        handleAction(CloudSyncContract.Action.LoadConfig)
    }

    fun handleAction(action: CloudSyncContract.Action) {
        when (action) {
            is CloudSyncContract.Action.ConnectProvider -> connectProvider(action.provider)
            is CloudSyncContract.Action.DisconnectProvider -> disconnectProvider(action.provider)
            is CloudSyncContract.Action.UpdateAutoSync -> updateAutoSync(action.enabled)
            is CloudSyncContract.Action.UpdateSyncInterval -> updateSyncInterval(action.intervalMinutes)
            is CloudSyncContract.Action.UpdateWifiOnly -> updateWifiOnly(action.wifiOnly)
            is CloudSyncContract.Action.UpdateBackupEnabled -> updateBackupEnabled(action.enabled)
            is CloudSyncContract.Action.ManualSync -> performManualSync()
            is CloudSyncContract.Action.RetrySync -> performManualSync()
            is CloudSyncContract.Action.DismissError -> dismissError()
            is CloudSyncContract.Action.LoadConfig -> loadConfiguration()
        }
    }

    /**
     * MOCK: Simulates connecting to a cloud provider.
     */
    private fun connectProvider(provider: CloudProvider) {
        updateState { copy(isAuthenticating = true, error = null) }

        viewModelScope.launch(ioDispatcher) {
            try {
                // Simulate OAuth authentication delay
                delay(1500)
                
                // Mock 90% success rate
                val isSuccess = (0..9).random() != 0
                
                if (isSuccess) {
                    updateState {
                        copy(
                            isAuthenticating = false,
                            connectedProviders = connectedProviders + provider,
                            syncConfig = syncConfig.copy(provider = provider)
                        )
                    }
                    sendEvent(CloudSyncContract.Event.AuthenticationSuccess(provider))
                    sendEvent(CloudSyncContract.Event.ShowMessage("Connected to ${provider.displayName}"))
                } else {
                    updateState { copy(isAuthenticating = false) }
                    val errorMsg = "Authentication failed. Please try again."
                    sendEvent(CloudSyncContract.Event.AuthenticationFailed(provider, errorMsg))
                    sendEvent(CloudSyncContract.Event.ShowError(errorMsg))
                }
            } catch (e: Exception) {
                updateState { copy(isAuthenticating = false, error = e.message) }
                sendEvent(CloudSyncContract.Event.ShowError(e.message ?: "Connection failed"))
            }
        }
    }

    /**
     * MOCK: Simulates disconnecting from a cloud provider.
     */
    private fun disconnectProvider(provider: CloudProvider) {
        updateState {
            copy(
                connectedProviders = connectedProviders - provider,
                syncConfig = if (syncConfig.provider == provider) {
                    syncConfig.copy(provider = null, autoSync = false)
                } else {
                    syncConfig
                }
            )
        }
        sendEvent(CloudSyncContract.Event.ShowMessage("Disconnected from ${provider.displayName}"))
    }

    /**
     * Updates auto-sync setting.
     */
    private fun updateAutoSync(enabled: Boolean) {
        updateState {
            copy(
                syncConfig = syncConfig.copy(autoSync = enabled)
            )
        }
        
        val message = if (enabled) {
            "Auto-sync enabled"
        } else {
            "Auto-sync disabled"
        }
        sendEvent(CloudSyncContract.Event.ShowMessage(message))
    }

    /**
     * Updates sync interval.
     */
    private fun updateSyncInterval(intervalMinutes: Int) {
        updateState {
            copy(
                syncConfig = syncConfig.copy(syncInterval = intervalMinutes)
            )
        }
        sendEvent(CloudSyncContract.Event.ShowMessage("Sync interval updated to $intervalMinutes minutes"))
    }

    /**
     * Updates WiFi-only setting.
     */
    private fun updateWifiOnly(wifiOnly: Boolean) {
        updateState {
            copy(
                syncConfig = syncConfig.copy(syncOnWifiOnly = wifiOnly)
            )
        }
        
        val message = if (wifiOnly) {
            "Will sync on WiFi only"
        } else {
            "Will sync on any connection"
        }
        sendEvent(CloudSyncContract.Event.ShowMessage(message))
    }

    /**
     * Updates backup enabled setting.
     */
    private fun updateBackupEnabled(enabled: Boolean) {
        updateState {
            copy(
                syncConfig = syncConfig.copy(enableBackup = enabled)
            )
        }
        
        val message = if (enabled) {
            "Automatic backup enabled"
        } else {
            "Automatic backup disabled"
        }
        sendEvent(CloudSyncContract.Event.ShowMessage(message))
    }

    /**
     * MOCK: Simulates manual sync operation.
     */
    private fun performManualSync() {
        if (!currentState.hasConnectedProvider) {
            sendEvent(CloudSyncContract.Event.ShowError("Please connect a cloud provider first"))
            return
        }

        updateState {
            copy(
                syncStatus = SyncStatus.SYNCING,
                error = null
            )
        }

        viewModelScope.launch(ioDispatcher) {
            try {
                // Simulate sync operation
                delay(2000)
                
                // Mock 95% success rate
                val isSuccess = (0..19).random() != 0
                
                if (isSuccess) {
                    val fileCount = (5..20).random()
                    updateState {
                        copy(
                            syncStatus = SyncStatus(
                                isSyncing = false,
                                lastSyncTime = System.currentTimeMillis(),
                                error = null
                            )
                        )
                    }
                    sendEvent(CloudSyncContract.Event.SyncSuccess(fileCount))
                    sendEvent(CloudSyncContract.Event.ShowMessage("Successfully synced $fileCount files"))
                } else {
                    val errorMsg = "Sync failed. Network error."
                    updateState {
                        copy(
                            syncStatus = SyncStatus(
                                isSyncing = false,
                                lastSyncTime = syncStatus.lastSyncTime,
                                error = errorMsg
                            ),
                            error = errorMsg
                        )
                    }
                    sendEvent(CloudSyncContract.Event.SyncFailed(errorMsg))
                    sendEvent(CloudSyncContract.Event.ShowError(errorMsg))
                }
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Unknown error"
                updateState {
                    copy(
                        syncStatus = SyncStatus(
                            isSyncing = false,
                            lastSyncTime = syncStatus.lastSyncTime,
                            error = errorMsg
                        ),
                        error = errorMsg
                    )
                }
                sendEvent(CloudSyncContract.Event.ShowError(errorMsg))
            }
        }
    }

    /**
     * MOCK: Loads saved configuration.
     */
    private fun loadConfiguration() {
        updateState { copy(isLoading = true) }

        viewModelScope.launch(ioDispatcher) {
            try {
                // Simulate loading delay
                delay(300)
                
                // Mock: Load default configuration
                val mockConfig = SyncConfig(
                    provider = null,
                    autoSync = false,
                    syncInterval = 60,
                    syncOnWifiOnly = true,
                    enableBackup = false
                )
                
                updateState {
                    copy(
                        syncConfig = mockConfig,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                updateState {
                    copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    /**
     * Dismisses the current error.
     */
    private fun dismissError() {
        updateState {
            copy(
                error = null,
                syncStatus = if (syncStatus.hasError) {
                    syncStatus.copy(error = null)
                } else {
                    syncStatus
                }
            )
        }
    }
}
