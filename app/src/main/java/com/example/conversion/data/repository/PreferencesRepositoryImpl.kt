package com.example.conversion.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.conversion.domain.model.ThemeMode
import com.example.conversion.domain.model.UserPreferences
import com.example.conversion.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of PreferencesRepository using DataStore.
 * Manages user preferences with reactive Flow updates.
 *
 * @param dataStore DataStore instance for storing preferences
 */
class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferencesRepository {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val USE_DYNAMIC_COLORS = booleanPreferencesKey("use_dynamic_colors")
    }

    override fun getUserPreferences(): Flow<UserPreferences> {
        return dataStore.data.map { preferences ->
            UserPreferences(
                themeMode = preferences[PreferencesKeys.THEME_MODE]?.let { modeString ->
                    try {
                        ThemeMode.valueOf(modeString)
                    } catch (e: IllegalArgumentException) {
                        ThemeMode.SYSTEM // Default if invalid value
                    }
                } ?: ThemeMode.SYSTEM,
                useDynamicColors = preferences[PreferencesKeys.USE_DYNAMIC_COLORS] ?: false
            )
        }
    }

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = themeMode.name
        }
    }

    override suspend fun setUseDynamicColors(useDynamicColors: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USE_DYNAMIC_COLORS] = useDynamicColors
        }
    }

    override suspend fun clearPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
