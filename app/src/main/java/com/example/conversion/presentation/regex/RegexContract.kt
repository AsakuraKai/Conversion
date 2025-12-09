package com.example.conversion.presentation.regex

import com.example.conversion.domain.model.RegexFlag
import com.example.conversion.domain.model.RegexPreset

/**
 * Regex Builder feature contract defining State, Events, and Actions.
 * Follows MVI pattern for unidirectional data flow.
 */
object RegexContract {

    /**
     * UI State for regex builder screen.
     */
    data class State(
        val pattern: String = "",
        val replacement: String = "",
        val flags: Set<RegexFlag> = emptySet(),
        val selectedPreset: RegexPreset? = null,
        val previewInput: String = "IMG_001.jpg",
        val previewResult: String? = null,
        val validationError: String? = null,
        val validationSuggestion: String? = null,
        val isValidating: Boolean = false,
        val preserveExtension: Boolean = true,
    ) {
        /**
         * Whether the current pattern is valid.
         */
        val isValid: Boolean
            get() = validationError == null && pattern.isNotEmpty()

        /**
         * Whether preview is available.
         */
        val canPreview: Boolean
            get() = isValid && previewInput.isNotEmpty()

        /**
         * Whether the pattern can be applied.
         */
        val canApply: Boolean
            get() = isValid && pattern.isNotEmpty()
    }

    /**
     * One-time events sent from ViewModel to UI.
     */
    sealed class Event {
        /**
         * Show a message to the user.
         */
        data class ShowMessage(val message: String) : Event()

        /**
         * Pattern successfully applied.
         */
        data object PatternApplied : Event()

        /**
         * Show error dialog.
         */
        data class ShowError(val title: String, val message: String) : Event()

        /**
         * Navigate back after successful application.
         */
        data object NavigateBack : Event()
    }

    /**
     * User actions that trigger state changes.
     */
    sealed class Action {
        /**
         * Update the regex pattern.
         */
        data class UpdatePattern(val pattern: String) : Action()

        /**
         * Update the replacement string.
         */
        data class UpdateReplacement(val replacement: String) : Action()

        /**
         * Toggle a regex flag.
         */
        data class ToggleFlag(val flag: RegexFlag) : Action()

        /**
         * Apply a preset pattern.
         */
        data class ApplyPreset(val preset: RegexPreset) : Action()

        /**
         * Update the preview input text.
         */
        data class UpdatePreviewInput(val input: String) : Action()

        /**
         * Toggle preserve extension option.
         */
        data object TogglePreserveExtension : Action()

        /**
         * Validate the current pattern.
         */
        data object ValidatePattern : Action()

        /**
         * Update the live preview.
         */
        data object UpdatePreview : Action()

        /**
         * Apply the pattern and close.
         */
        data object ApplyPattern : Action()

        /**
         * Clear validation error.
         */
        data object ClearError : Action()

        /**
         * Reset to default state.
         */
        data object Reset : Action()
    }
}
