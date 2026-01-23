package com.example.conversion.presentation.template

import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.model.RenameTemplate

/**
 * Template Management feature contract defining State, Events, and Actions.
 * Follows MVI pattern for unidirectional data flow.
 */
object TemplateContract {

    /**
     * UI State for template management screen.
     */
    data class State(
        val templates: List<RenameTemplate> = emptyList(),
        val favoriteTemplates: List<RenameTemplate> = emptyList(),
        val selectedTemplate: RenameTemplate? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val showSaveDialog: Boolean = false,
        val showDeleteConfirmation: Boolean = false,
        val templateToDelete: RenameTemplate? = null,
        val filterByFavorites: Boolean = false,
        val currentConfig: RenameConfig? = null,
        val newTemplateName: String = "",
        val newTemplatePattern: String = ""
    ) {
        /**
         * Whether any templates exist.
         */
        val hasTemplates: Boolean
            get() = templates.isNotEmpty()

        /**
         * Whether any favorite templates exist.
         */
        val hasFavorites: Boolean
            get() = favoriteTemplates.isNotEmpty()

        /**
         * Templates to display based on current filter.
         */
        val displayedTemplates: List<RenameTemplate>
            get() = if (filterByFavorites) favoriteTemplates else templates

        /**
         * Whether the save dialog inputs are valid.
         */
        val canSaveTemplate: Boolean
            get() = newTemplateName.isNotBlank() && 
                    newTemplateName.length <= RenameTemplate.MAX_NAME_LENGTH &&
                    newTemplatePattern.isNotBlank() &&
                    newTemplatePattern.length <= RenameTemplate.MAX_PATTERN_LENGTH &&
                    currentConfig != null &&
                    currentConfig.isValid()

        /**
         * Validation error for template name.
         */
        val nameValidationError: String?
            get() = when {
                newTemplateName.isBlank() -> "Template name cannot be empty"
                newTemplateName.length > RenameTemplate.MAX_NAME_LENGTH -> 
                    "Name too long (max ${RenameTemplate.MAX_NAME_LENGTH} characters)"
                else -> null
            }

        /**
         * Validation error for template pattern.
         */
        val patternValidationError: String?
            get() = when {
                newTemplatePattern.isBlank() -> "Pattern cannot be empty"
                newTemplatePattern.length > RenameTemplate.MAX_PATTERN_LENGTH -> 
                    "Pattern too long (max ${RenameTemplate.MAX_PATTERN_LENGTH} characters)"
                else -> null
            }
    }

    /**
     * One-time events sent from ViewModel to UI.
     */
    sealed class Event {
        /**
         * Template was successfully saved.
         */
        data class TemplateSaved(val template: RenameTemplate) : Event()

        /**
         * Template was successfully deleted.
         */
        data class TemplateDeleted(val templateName: String) : Event()

        /**
         * Template was applied to current configuration.
         */
        data class TemplateApplied(val template: RenameTemplate) : Event()

        /**
         * Show an error message to the user.
         */
        data class ShowError(val message: String) : Event()

        /**
         * Show an informational message.
         */
        data class ShowMessage(val message: String) : Event()

        /**
         * Navigate back with selected template config.
         */
        data class NavigateBackWithConfig(val config: RenameConfig) : Event()
    }

    /**
     * User actions that trigger state changes.
     */
    sealed class Action {
        /**
         * Load all templates from repository.
         */
        data object LoadTemplates : Action()

        /**
         * Save a new template with the current configuration.
         */
        data class SaveTemplate(
            val name: String,
            val pattern: String,
            val config: RenameConfig
        ) : Action()

        /**
         * Delete an existing template.
         */
        data class DeleteTemplate(val templateId: String) : Action()

        /**
         * Apply a template to the current configuration.
         */
        data class ApplyTemplate(val template: RenameTemplate) : Action()

        /**
         * Toggle favorite status of a template.
         */
        data class ToggleFavorite(val templateId: String) : Action()

        /**
         * Select a template for preview.
         */
        data class SelectTemplate(val template: RenameTemplate?) : Action()

        /**
         * Show the save template dialog.
         */
        data class ShowSaveDialog(val config: RenameConfig) : Action()

        /**
         * Hide the save template dialog.
         */
        data object HideSaveDialog : Action()

        /**
         * Show delete confirmation dialog.
         */
        data class ShowDeleteConfirmation(val template: RenameTemplate) : Action()

        /**
         * Hide delete confirmation dialog.
         */
        data object HideDeleteConfirmation : Action()

        /**
         * Confirm deletion of template.
         */
        data object ConfirmDelete : Action()

        /**
         * Toggle filter between all templates and favorites only.
         */
        data object ToggleFilter : Action()

        /**
         * Update the new template name in the dialog.
         */
        data class UpdateTemplateName(val name: String) : Action()

        /**
         * Update the new template pattern in the dialog.
         */
        data class UpdateTemplatePattern(val pattern: String) : Action()

        /**
         * Clear any error state.
         */
        data object ClearError : Action()
    }
}
