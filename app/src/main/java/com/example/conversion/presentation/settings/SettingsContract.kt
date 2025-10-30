package com.example.conversion.presentation.settings

import com.example.conversion.domain.model.ThemeMode
import com.example.conversion.domain.model.UserPreferences

/**
 * UI state for the Settings screen
 */
data class SettingsUiState(
    val preferences: UserPreferences = UserPreferences(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

/**
 * One-time events for the Settings screen
 */
sealed class SettingsEvent {
    data class ShowToast(val message: String) : SettingsEvent()
    data class ShowError(val error: String) : SettingsEvent()
}

/**
 * User actions/intents for the Settings screen
 */
sealed class SettingsAction {
    data class UpdateThemeMode(val themeMode: ThemeMode) : SettingsAction()
    data class UpdateDynamicColors(val enabled: Boolean) : SettingsAction()
}
