package com.example.conversion.presentation.tag

import com.example.conversion.domain.model.FileTag

/**
 * Tag Management feature contract defining State, Events, and Actions.
 * Follows MVI pattern for unidirectional data flow.
 */
object TagContract {

    /**
     * UI State for tag management screen.
     */
    data class State(
        val tags: List<FileTag> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val showCreateDialog: Boolean = false,
        val showEditDialog: Boolean = false,
        val showDeleteConfirmation: Boolean = false,
        val editingTag: FileTag? = null,
        val tagToDelete: FileTag? = null,
        val newTagName: String = "",
        val selectedColor: String = FileTag.PREDEFINED_COLORS.first(),
        val searchQuery: String = ""
    ) {
        /**
         * Whether any tags exist.
         */
        val hasTags: Boolean
            get() = tags.isNotEmpty()

        /**
         * Tags filtered by search query.
         */
        val filteredTags: List<FileTag>
            get() = if (searchQuery.isBlank()) {
                tags
            } else {
                tags.filter { it.name.contains(searchQuery, ignoreCase = true) }
            }

        /**
         * Whether the create/edit dialog inputs are valid.
         */
        val canSaveTag: Boolean
            get() = newTagName.isNotBlank() && 
                    newTagName.length <= FileTag.MAX_NAME_LENGTH &&
                    selectedColor.matches(Regex("^#[0-9A-Fa-f]{6}$"))

        /**
         * Validation error for tag name.
         */
        val nameValidationError: String?
            get() = when {
                newTagName.isBlank() -> "Tag name cannot be empty"
                newTagName.length > FileTag.MAX_NAME_LENGTH -> 
                    "Name too long (max ${FileTag.MAX_NAME_LENGTH} characters)"
                else -> null
            }

        /**
         * Color validation error.
         */
        val colorValidationError: String?
            get() = if (!selectedColor.matches(Regex("^#[0-9A-Fa-f]{6}$"))) {
                "Invalid color format"
            } else null
    }

    /**
     * One-time events sent from ViewModel to UI.
     */
    sealed class Event {
        /**
         * Tag was successfully created.
         */
        data class TagCreated(val tag: FileTag) : Event()

        /**
         * Tag was successfully updated.
         */
        data class TagUpdated(val tag: FileTag) : Event()

        /**
         * Tag was successfully deleted.
         */
        data class TagDeleted(val tagName: String) : Event()

        /**
         * Show an error message to the user.
         */
        data class ShowError(val message: String) : Event()

        /**
         * Show an informational message.
         */
        data class ShowMessage(val message: String) : Event()
    }

    /**
     * User actions that trigger state changes.
     */
    sealed class Action {
        /**
         * Load all tags from repository.
         */
        data object LoadTags : Action()

        /**
         * Show the create tag dialog.
         */
        data object ShowCreateDialog : Action()

        /**
         * Hide the create tag dialog.
         */
        data object HideCreateDialog : Action()

        /**
         * Show the edit tag dialog.
         */
        data class ShowEditDialog(val tag: FileTag) : Action()

        /**
         * Hide the edit tag dialog.
         */
        data object HideEditDialog : Action()

        /**
         * Create a new tag.
         */
        data class CreateTag(val name: String, val color: String) : Action()

        /**
         * Update an existing tag.
         */
        data class UpdateTag(val tag: FileTag) : Action()

        /**
         * Show delete confirmation dialog.
         */
        data class ShowDeleteConfirmation(val tag: FileTag) : Action()

        /**
         * Hide delete confirmation dialog.
         */
        data object HideDeleteConfirmation : Action()

        /**
         * Confirm deletion of tag.
         */
        data object ConfirmDelete : Action()

        /**
         * Delete a tag by ID.
         */
        data class DeleteTag(val tagId: String) : Action()

        /**
         * Update the new tag name in the dialog.
         */
        data class UpdateTagName(val name: String) : Action()

        /**
         * Update the selected color in the dialog.
         */
        data class UpdateSelectedColor(val color: String) : Action()

        /**
         * Update the search query.
         */
        data class UpdateSearchQuery(val query: String) : Action()

        /**
         * Clear any error state.
         */
        data object ClearError : Action()
    }
}
