package com.example.conversion.presentation.settings

import androidx.lifecycle.viewModelScope
import com.example.conversion.domain.model.ThemeMode
import com.example.conversion.domain.usecase.settings.GetUserPreferencesUseCase
import com.example.conversion.domain.usecase.settings.SetAutoBackupEnabledUseCase
import com.example.conversion.domain.usecase.settings.SetAutoDeleteOriginalsUseCase
import com.example.conversion.domain.usecase.settings.SetThemeModeUseCase
import com.example.conversion.domain.usecase.settings.SetUseDynamicColorsUseCase
import com.example.conversion.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Settings screen.
 * Manages user preferences and theme settings.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase,
    private val setUseDynamicColorsUseCase: SetUseDynamicColorsUseCase,
    private val setAutoBackupEnabledUseCase: SetAutoBackupEnabledUseCase,
    private val setAutoDeleteOriginalsUseCase: SetAutoDeleteOriginalsUseCase
) : BaseViewModel<SettingsUiState, SettingsEvent>(
    initialState = SettingsUiState()
) {
    
    init {
        observePreferences()
    }
    
    /**
     * Observe user preferences and update UI state
     */
    private fun observePreferences() {
        viewModelScope.launch {
            getUserPreferencesUseCase()
                .catch { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Failed to load preferences"
                        )
                    }
                }
                .collect { preferences ->
                    updateState {
                        copy(
                            preferences = preferences,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }
    
    /**
     * Handle user actions
     */
    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.UpdateThemeMode -> updateThemeMode(action.themeMode)
            is SettingsAction.UpdateDynamicColors -> updateDynamicColors(action.enabled)
            is SettingsAction.UpdateAutoBackup -> updateAutoBackup(action.enabled)
            is SettingsAction.UpdateAutoDelete -> updateAutoDelete(action.enabled)
        }
    }
    
    /**
     * Update the theme mode preference
     */
    private fun updateThemeMode(themeMode: ThemeMode) {
        executeUseCase(
            onSuccess = {
                sendEvent(SettingsEvent.ShowToast("Theme updated"))
            },
            onError = { error ->
                sendEvent(SettingsEvent.ShowError(error.message ?: "Failed to update theme"))
            }
        ) {
            setThemeModeUseCase(themeMode)
        }
    }
    
    /**
     * Update the dynamic colors preference
     */
    private fun updateDynamicColors(enabled: Boolean) {
        executeUseCase(
            onSuccess = {
                sendEvent(SettingsEvent.ShowToast("Dynamic colors ${if (enabled) "enabled" else "disabled"}"))
            },
            onError = { error ->
                sendEvent(SettingsEvent.ShowError(error.message ?: "Failed to update dynamic colors"))
            }
        ) {
            setUseDynamicColorsUseCase(enabled)
        }
    }
    
    /**
     * Update the auto-backup preference
     */
    private fun updateAutoBackup(enabled: Boolean) {
        viewModelScope.launch {
            setAutoBackupEnabledUseCase(enabled)
                .onSuccess {
                    val message = if (enabled) {
                        "Auto-backup enabled. Files will be backed up before operations."
                    } else {
                        "Auto-backup disabled. Warning: Original files may be lost."
                    }
                    sendEvent(SettingsEvent.ShowToast(message))
                }
                .onFailure { error ->
                    sendEvent(SettingsEvent.ShowError(error.message ?: "Failed to update auto-backup"))
                }
        }
    }
    
    /**
     * Update the auto-delete originals preference
     */
    private fun updateAutoDelete(enabled: Boolean) {
        viewModelScope.launch {
            setAutoDeleteOriginalsUseCase(enabled)
                .onSuccess {
                    val message = if (enabled) {
                        "Auto-delete enabled. Original files will be deleted after operations."
                    } else {
                        "Auto-delete disabled."
                    }
                    sendEvent(SettingsEvent.ShowToast(message))
                }
                .onFailure { error ->
                    sendEvent(SettingsEvent.ShowError(error.message ?: "Failed to update auto-delete"))
                }
        }
    }
}
