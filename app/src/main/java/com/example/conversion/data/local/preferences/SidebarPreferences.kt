package com.example.conversion.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataStore extension for sidebar preferences.
 */
private val Context.sidebarDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "sidebar_preferences"
)

/**
 * Manages sidebar navigation preferences using DataStore.
 * 
 * Persists:
 * - Collapsed/expanded state
 * - Auto-collapse on navigation preference
 * - Selected navigation item
 * 
 * Uses Preferences DataStore for simple key-value storage.
 */
@Singleton
class SidebarPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.sidebarDataStore

    /**
     * Observes the collapsed state of the sidebar.
     */
    val isCollapsed: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_COLLAPSED] ?: false
    }

    /**
     * Observes the auto-collapse on navigation preference.
     */
    val autoCollapseOnNavigation: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.AUTO_COLLAPSE_ON_NAVIGATION] ?: false
    }

    /**
     * Observes the selected navigation item ID.
     */
    val selectedItemId: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SELECTED_ITEM_ID] ?: "home"
    }

    /**
     * Saves the collapsed state.
     * 
     * @param isCollapsed Whether the sidebar is collapsed
     */
    suspend fun setCollapsed(isCollapsed: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_COLLAPSED] = isCollapsed
        }
    }

    /**
     * Saves the auto-collapse on navigation preference.
     * 
     * @param autoCollapse Whether to auto-collapse after navigation
     */
    suspend fun setAutoCollapseOnNavigation(autoCollapse: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_COLLAPSE_ON_NAVIGATION] = autoCollapse
        }
    }

    /**
     * Saves the selected navigation item ID.
     * 
     * @param itemId The ID of the selected navigation item
     */
    suspend fun setSelectedItemId(itemId: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_ITEM_ID] = itemId
        }
    }

    /**
     * Clears all sidebar preferences (reset to defaults).
     */
    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * DataStore preference keys.
     */
    private object PreferencesKeys {
        val IS_COLLAPSED = booleanPreferencesKey("is_collapsed")
        val AUTO_COLLAPSE_ON_NAVIGATION = booleanPreferencesKey("auto_collapse_on_navigation")
        val SELECTED_ITEM_ID = stringPreferencesKey("selected_item_id")
    }
}
