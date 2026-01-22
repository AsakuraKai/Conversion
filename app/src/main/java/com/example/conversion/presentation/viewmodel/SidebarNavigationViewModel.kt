package com.example.conversion.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.conversion.data.local.preferences.SidebarPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing collapsible sidebar navigation state.
 * 
 * Responsibilities:
 * - Maintains collapsed/expanded state with DataStore persistence
 * - Tracks selected navigation item across app restarts
 * - Handles navigation item selection logic
 * - Manages auto-collapse on navigation behavior
 * - Provides badge notification system
 * 
 * Follows MVI pattern with StateFlow for unidirectional data flow.
 * State is persisted using DataStore Preferences.
 */
@HiltViewModel
class SidebarNavigationViewModel @Inject constructor(
    private val sidebarPreferences: SidebarPreferences
) : ViewModel() {

    // Private mutable state
    private val _state = MutableStateFlow(SidebarNavigationState())
    
    // Public immutable state
    val state: StateFlow<SidebarNavigationState> = _state.asStateFlow()

    /**
     * Initializes the ViewModel and loads saved state from DataStore.
     */
    init {
        loadSavedState()
    }

    /**
     * Loads saved state from DataStore and updates UI state.
     */
    private fun loadSavedState() {
        viewModelScope.launch {
            combine(
                sidebarPreferences.isCollapsed,
                sidebarPreferences.autoCollapseOnNavigation,
                sidebarPreferences.selectedItemId
            ) { isCollapsed, autoCollapse, selectedId ->
                SidebarNavigationState(
                    isCollapsed = isCollapsed,
                    selectedItemId = selectedId,
                    autoCollapseOnNavigation = autoCollapse,
                    badges = _state.value.badges // Preserve badges
                )
            }.collect { savedState ->
                _state.value = savedState
            }
        }
    }

    /**
     * Toggles the sidebar between collapsed and expanded states.
     * Persists the new state to DataStore.
     */
    fun toggleSidebar() {
        viewModelScope.launch {
            val newCollapsedState = !_state.value.isCollapsed
            _state.value = _state.value.copy(isCollapsed = newCollapsedState)
            sidebarPreferences.setCollapsed(newCollapsedState)
        }
    }

    /**
     * Collapses the sidebar to icon-only view.
     * Persists the state to DataStore.
     */
    fun collapseSidebar() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isCollapsed = true)
            sidebarPreferences.setCollapsed(true)
        }
    }

    /**
     * Expands the sidebar to show full labels.
     * Persists the state to DataStore.
     */
    fun expandSidebar() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isCollapsed = false)
            sidebarPreferences.setCollapsed(false)
        }
    }

    /**
     * Selects a navigation item and updates the state.
     * Persists the selection to DataStore.
     * 
     * @param itemId The ID of the navigation item to select
     */
    fun selectNavigationItem(itemId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(selectedItemId = itemId)
            sidebarPreferences.setSelectedItemId(itemId)
        }
    }

   
 * @param isCollapsed Whether the sidebar is collapsed (icon-only view)
 * @param selectedItemId ID of the currently selected navigation item
 * @param autoCollapseOnNavigation Whether to auto-collapse after navigation
 * @param badges Map of item IDs to badge counts for notification indicators
 */
data class SidebarNavigationState(
    val isCollapsed: Boolean = false,
    val selectedItemId: String = "home",
    val autoCollapseOnNavigation: Boolean = false,
    val badges: Map<String, Int> = emptyMap()se after navigation
     */
    fun setAutoCollapseOnNavigation(autoCollapse: Boolean) {
        viewModelScope.launch {
            _state.value = _state.value.copy(autoCollapseOnNavigation = autoCollapse)
            sidebarPreferences.setAutoCollapseOnNavigation(autoCollapse)
        }
    }

    /**
     * Handles navigation completion and auto-collapse if enabled.
     * Should be called after navigation occurs.
     */
    fun onNavigationComplete() {
        if (_state.value.autoCollapseOnNavigation) {
            collapseSidebar()
        }
    }

    /**
     * Updates the badge count for a specific navigation item.
     * 
     * @param itemId The ID of the navigation item
     * @param count The badge count (null to remove badge)
     */
    fun updateBadge(itemId: String, count: Int?) {
        viewModelScope.launch {
            val updatedBadges = _state.value.badges.toMutableMap()
            if (count != null && count > 0) {
                updatedBadges[itemId] = count
            } else {
                updatedBadges.remove(itemId)
            }
            _state.value = _state.value.copy(badges = updatedBadges)
        }
    }

    /**
     * Clears all badges.
     */
    fun clearAllBadges() {
        viewModelScope.launch {
            _state.value = _state.value.copy(badges = emptyMap())
        }
    }

    /**
     * Increments the badge count for a specific navigation item.
     * 
     * @param itemId The ID of the navigation item
     * @param increment The amount to increment by (default 1)
     */
    fun incrementBadge(itemId: String, increment: Int = 1) {
        val currentCount = _state.value.badges[itemId] ?: 0
        updateBadge(itemId, currentCount + increment)
    }

    /**
     * Resets all preferences to defaults.
     */
    fun resetToDefaults() {
        viewModelScope.launch {
            sidebarPreferences.clearAll()
            _state.value = SidebarNavigationState()
        }
    }
}

/**
 * UI state for the sidebar navigation.
 * Immutable data class following MVI pattern.
 */
data class SidebarNavigationState(
    val isCollapsed: Boolean = false,
    val selectedItemId: String = "home",
    val autoCollapseOnNavigation: Boolean = false
)
