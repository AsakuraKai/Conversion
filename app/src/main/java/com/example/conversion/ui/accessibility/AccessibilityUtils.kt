package com.example.conversion.ui.accessibility

import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp

/**
 * Accessibility utilities for Jetpack Compose UI.
 *
 * Provides common accessibility modifiers and constants to ensure
 * consistent accessibility implementation across the app.
 *
 * Features:
 * - Minimum touch target sizes (48dp per Material Design)
 * - Semantic properties for screen readers
 * - Content descriptions
 * - State descriptions for dynamic UI elements
 */
object AccessibilityUtils {
    
    /**
     * Minimum touch target size per Material Design guidelines.
     * Used for buttons, clickable elements, etc.
     */
    val MIN_TOUCH_TARGET_SIZE = 48.dp
    
    /**
     * Smaller minimum size for compact UI elements.
     * Still accessible but allows for denser layouts.
     */
    val MIN_COMPACT_TARGET_SIZE = 40.dp
    
    /**
     * Add semantic content description for screen readers.
     *
     * @param description Text read by TalkBack when element is focused
     * @param role Semantic role of the element (Button, Checkbox, etc.)
     */
    fun Modifier.semanticContentDescription(
        description: String,
        role: Role? = null
    ): Modifier = this.semantics {
        contentDescription = description
        role?.let { this.role = it }
    }
    
    /**
     * Add state description for dynamic UI elements.
     *
     * @param description Current state read by TalkBack
     * @param contentDesc Optional content description
     */
    fun Modifier.semanticStateDescription(
        description: String,
        contentDesc: String? = null
    ): Modifier = this.semantics {
        stateDescription = description
        contentDesc?.let { contentDescription = it }
    }
    
    /**
     * Ensure minimum touch target size for accessibility.
     *
     * @param size Minimum size (default 48dp)
     */
    fun Modifier.minimumTouchTarget(
        size: Int = 48
    ): Modifier = this.size(size.dp)
}

/**
 * Common accessibility content descriptions.
 */
object AccessibilityStrings {
    
    // Navigation
    const val NAVIGATE_BACK = "Navigate back"
    const val NAVIGATE_UP = "Navigate up"
    const val OPEN_MENU = "Open menu"
    const val CLOSE_MENU = "Close menu"
    const val OPEN_SETTINGS = "Open settings"
    
    // File operations
    const val SELECT_FILE = "Select file"
    const val DESELECT_FILE = "Deselect file"
    const val DELETE_FILE = "Delete file"
    const val RENAME_FILE = "Rename file"
    const val FILE_SELECTED = "Selected"
    const val FILE_NOT_SELECTED = "Not selected"
    
    // Actions
    const val CONFIRM = "Confirm"
    const val CANCEL = "Cancel"
    const val SAVE = "Save"
    const val DELETE = "Delete"
    const val EDIT = "Edit"
    const val ADD = "Add"
    const val REMOVE = "Remove"
    const val SEARCH = "Search"
    const val FILTER = "Filter"
    const val SORT = "Sort"
    const val REFRESH = "Refresh"
    
    // Loading states
    const val LOADING = "Loading"
    const val LOADING_FILES = "Loading files"
    const val PROCESSING = "Processing"
    
    // Empty states
    const val NO_FILES = "No files available"
    const val NO_RESULTS = "No results found"
    const val EMPTY_LIST = "Empty list"
    
    // Templates
    const val CREATE_TEMPLATE = "Create new template"
    const val EDIT_TEMPLATE = "Edit template"
    const val DELETE_TEMPLATE = "Delete template"
    const val SELECT_TEMPLATE = "Select template"
    
    // Tags
    const val ADD_TAG = "Add tag"
    const val REMOVE_TAG = "Remove tag"
    const val FILTER_BY_TAG = "Filter by tag"
    
    // Preview
    const val PREVIEW_FILE = "Preview file"
    const val CLOSE_PREVIEW = "Close preview"
    
    // Decorative images
    const val DECORATIVE = "" // Empty string for decorative images
}

/**
 * State descriptions for dynamic UI elements.
 */
object StateDescriptions {
    
    fun fileSelection(isSelected: Boolean): String =
        if (isSelected) AccessibilityStrings.FILE_SELECTED 
        else AccessibilityStrings.FILE_NOT_SELECTED
    
    fun filesSelected(count: Int): String =
        when (count) {
            0 -> "No files selected"
            1 -> "1 file selected"
            else -> "$count files selected"
        }
    
    fun loading(isLoading: Boolean): String =
        if (isLoading) "Loading" else "Loaded"
    
    fun expanded(isExpanded: Boolean): String =
        if (isExpanded) "Expanded" else "Collapsed"
    
    fun enabled(isEnabled: Boolean): String =
        if (isEnabled) "Enabled" else "Disabled"
    
    fun checked(isChecked: Boolean): String =
        if (isChecked) "Checked" else "Unchecked"
    
    fun progress(current: Int, total: Int): String =
        "Progress: $current of $total"
}
