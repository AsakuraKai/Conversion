package com.example.conversion.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing collapsible sidebar navigation state.
 * 
 * Responsibilities:
 * - Maintains collapsed/expanded state
 * - Tracks selected navigation item
 * - Handles navigation item selection logic
 * - Prepares for state persistence (DataStore integration)
 * 
 * Follows MVI pattern with StateFlow for unidirectional data flow.
 */
@HiltViewModel
class SidebarNavigationViewModel @Inject constructor(
    // TODO: Inject PreferencesRepository for state persistence in Phase 5
) : ViewModel() {

    // Private mutable state
    private val _state = MutableStateFlow(SidebarNavigationState())
    
    // Public immutable state
    val state: StateFlow<SidebarNavigationState> = _state.asStateFlow()

    /**
     * Initializes the ViewModel.
     * TODO: Load saved state from DataStore in Phase 5
     */
    init {
        // Default initialization - state persistence will be added in Phase 5
    }

    /**
     * Toggles the sidebar between collapsed and expanded states.
     */
    fun toggleSidebar() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isCollapsed = !_state.value.isCollapsed
            )
            // TODO: Persist state to DataStore in Phase 5
        }
    }

    /**
     * Collapses the sidebar to icon-only view.
     */
    fun collapseSidebar() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isCollapsed = true)
            // TODO: Persist state to DataStore in Phase 5
        }
    }

    /**
     * Expands the sidebar to show full labels.
     */
    fun expandSidebar() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isCollapsed = false)
            // TODO: Persist state to DataStore in Phase 5
        }
    }

    /**
     * Selects a navigation item and updates the state.
     * 
     * @param itemId The ID of the navigation item to select
     */
    fun selectNavigationItem(itemId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(selectedItemId = itemId)
        }
    }

    /**
     * Auto-collapses the sidebar after navigation (useful on phones/tablets).
     * Behavior can be configured per device type in Phase 5.
     * 
     * @param autoCollapse Whether to auto-collapse after navigation
     */
    fun setAutoCollapseOnNavigation(autoCollapse: Boolean) {
        viewModelScope.launch {
            _state.value = _state.value.copy(autoCollapseOnNavigation = autoCollapse)
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
