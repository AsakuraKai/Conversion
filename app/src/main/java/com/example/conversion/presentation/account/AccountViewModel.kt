package com.example.conversion.presentation.account

import androidx.lifecycle.viewModelScope
import com.example.conversion.domain.model.UserPreferences
import com.example.conversion.domain.repository.PreferencesRepository
import com.example.conversion.domain.usecase.sync.ObserveSyncStatusUseCase
import com.example.conversion.domain.usecase.sync.SyncPreferencesUseCase
import com.example.conversion.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Account screen
 * 
 * Manages account authentication state and multi-device sync operations.
 * 
 * Features:
 * - Observes real-time sync status
 * - Triggers manual sync
 * - Displays synced data summary (templates, tags, settings)
 * - Handles sign in/out (mock implementation)
 * 
 * Mock Implementation Notes:
 * - User is always signed in (isSignedIn = true)
 * - Sign in/out actions are simulated
 * - No actual Firebase authentication
 * - Sync operations use mock repository with in-memory cloud storage
 * 
 * @property syncPreferencesUseCase Use case for syncing user preferences
 * @property observeSyncStatusUseCase Use case for observing sync status
 * @property preferencesRepository Repository for user preferences
 */
@HiltViewModel
class AccountViewModel @Inject constructor(
    private val syncPreferencesUseCase: SyncPreferencesUseCase,
    private val observeSyncStatusUseCase: ObserveSyncStatusUseCase,
    private val preferencesRepository: PreferencesRepository
) : BaseViewModel<AccountUiState, AccountEvent>(AccountUiState()) {
    
    init {
        observeSyncStatus()
        loadAccountData()
    }
    
    /**
     * Handles user actions from the UI
     */
    fun handleAction(action: AccountAction) {
        when (action) {
            AccountAction.SyncNow -> syncNow()
            AccountAction.SignIn -> signIn()
            AccountAction.SignOut -> signOut()
            AccountAction.RefreshData -> loadAccountData()
            AccountAction.ClearError -> clearError()
        }
    }
    
    /**
     * Observes sync status changes in real-time
     */
    private fun observeSyncStatus() {
        observeSyncStatusUseCase()
            .onEach { syncStatus ->
                updateState {
                    copy(syncStatus = syncStatus)
                }
            }
            .launchIn(viewModelScope)
    }
    
    /**
     * Loads account data including synced item counts
     */
    private fun loadAccountData() {
        launch {
            updateState { copy(isLoading = true) }
            
            // Load preferences to get synced data counts
            preferencesRepository.getUserPreferences().collect { preferences ->
                updateState {
                    copy(
                        syncedTemplatesCount = preferences.templates.size,
                        syncedTagsCount = preferences.tags.size,
                        settingsSynced = true, // Settings are always part of preferences
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * Triggers manual sync of user preferences
     */
    private fun syncNow() {
        launch {
            val result = syncPreferencesUseCase()
            
            result.fold(
                onSuccess = {
                    sendEvent(AccountEvent.ShowToast("Sync completed successfully"))
                    loadAccountData() // Refresh counts after sync
                },
                onFailure = { error ->
                    updateState {
                        copy(errorMessage = error.message ?: "Sync failed")
                    }
                    sendEvent(AccountEvent.ShowError(error.message ?: "Sync failed"))
                }
            )
        }
    }
    
    /**
     * Mock sign in implementation
     * In production, this would use Firebase Authentication
     */
    private fun signIn() {
        // Mock implementation: User is always signed in
        updateState {
            copy(
                isSignedIn = true,
                currentUser = MockUser.DEFAULT
            )
        }
        sendEvent(AccountEvent.ShowToast("Signed in as ${MockUser.DEFAULT.displayName}"))
    }
    
    /**
     * Mock sign out implementation
     * In production, this would use Firebase Authentication
     */
    private fun signOut() {
        // Mock implementation: Simulate sign out
        updateState {
            copy(
                isSignedIn = false,
                currentUser = MockUser.DEFAULT,
                syncedTemplatesCount = 0,
                syncedTagsCount = 0,
                settingsSynced = false
            )
        }
        sendEvent(AccountEvent.SignOutCompleted)
        sendEvent(AccountEvent.ShowToast("Signed out successfully"))
    }
    
    /**
     * Clears the error message
     */
    private fun clearError() {
        updateState {
            copy(errorMessage = null)
        }
    }
}
