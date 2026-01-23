package com.example.conversion.presentation.renameconfig

import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.model.SortStrategy

/**
 * Batch Rename Configuration feature contract defining State, Events, and Actions.
 * Follows MVI pattern for unidirectional data flow.
 */
object RenameConfigContract {

    /**
     * UI State for rename configuration screen.
     */
    data class State(
        val config: RenameConfig = RenameConfig(
            prefix = "",
            startNumber = 1,
            digitCount = 3,
            preserveExtension = true,
            sortStrategy = SortStrategy.NATURAL
        ),
        val previewFilename: String = "",
        val validationError: String? = null,
        val isLoading: Boolean = false,
        val selectedFileCount: Int = 0
    ) {
        /**
         * Whether the current configuration is valid and can proceed.
         */
        val canProceed: Boolean
            get() = validationError == null && config.isValid() && selectedFileCount > 0

        /**
         * Whether the prefix field has been touched by the user.
         */
        val showValidation: Boolean
            get() = config.prefix.isNotEmpty()

        /**
         * Helper to check if preview should be shown.
         */
        val hasValidPreview: Boolean
            get() = previewFilename.isNotEmpty() && validationError == null
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
         * Navigate to preview screen with the configuration.
         */
        data class NavigateToPreview(val config: RenameConfig) : Event()

        /**
         * Navigate back to previous screen.
         */
        data object NavigateBack : Event()
    }

    /**
     * User actions that trigger state changes.
     */
    sealed class Action {
        /**
         * User updated the prefix text.
         */
        data class UpdatePrefix(val prefix: String) : Action()

        /**
         * User changed the start number.
         */
        data class UpdateStartNumber(val number: Int) : Action()

        /**
         * User changed the digit count.
         */
        data class UpdateDigitCount(val count: Int) : Action()

        /**
         * User toggled preserve extension.
         */
        data class TogglePreserveExtension(val preserve: Boolean) : Action()

        /**
         * User selected a sort strategy.
         */
        data class UpdateSortStrategy(val strategy: SortStrategy) : Action()

        /**
         * User wants to proceed to preview.
         */
        data object Confirm : Action()

        /**
         * User wants to go back.
         */
        data object Back : Action()

        /**
         * Initialize with selected file count.
         */
        data class Initialize(val fileCount: Int) : Action()
    }
}
