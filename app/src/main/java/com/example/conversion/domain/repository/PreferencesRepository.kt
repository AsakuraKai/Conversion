package com.example.conversion.domain.repository

import com.example.conversion.domain.model.ThemeMode
import com.example.conversion.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing user preferences.
 * Follows clean architecture by defining contracts in the domain layer.
 */
interface PreferencesRepository {
    /**
     * Observes user preferences as a Flow for reactive updates
     */
    fun getUserPreferences(): Flow<UserPreferences>
    
    /**
     * Updates the theme mode preference
     */
    suspend fun setThemeMode(themeMode: ThemeMode)
    
    /**
     * Updates the dynamic colors preference
     */
    suspend fun setUseDynamicColors(useDynamicColors: Boolean)
    
    /**
     * Clears all preferences (reset to defaults)
     */
    suspend fun clearPreferences()
}
