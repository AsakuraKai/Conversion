package com.example.conversion.presentation.theme

import android.net.Uri
import com.example.conversion.domain.model.ImagePalette

/**
 * MVI Contract for Dynamic Theme feature.
 * Defines the state, events, and actions for dynamic theming from images.
 */
object DynamicThemeContract {
    
    /**
     * UI State for Dynamic Theme screen.
     *
     * @property selectedImageUri URI of the selected background image
     * @property palette Extracted color palette from the image
     * @property isLoading Whether palette extraction is in progress
     * @property error Error message if extraction failed
     * @property isThemeApplied Whether the extracted theme is currently applied
     */
    data class State(
        val selectedImageUri: Uri? = null,
        val palette: ImagePalette? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val isThemeApplied: Boolean = false
    ) {
        /**
         * Whether we have a valid palette to preview.
         */
        val hasPalette: Boolean
            get() = palette != null && palette.hasColors
        
        /**
         * Whether the apply button should be enabled.
         */
        val canApplyTheme: Boolean
            get() = hasPalette && !isLoading && !isThemeApplied
        
        /**
         * Whether the reset button should be shown.
         */
        val canResetTheme: Boolean
            get() = isThemeApplied
    }
    
    /**
     * One-time events for navigation and UI feedback.
     */
    sealed class Event {
        /**
         * Show a toast or snackbar message.
         */
        data class ShowMessage(val message: String) : Event()
        
        /**
         * Theme was successfully applied.
         */
        data object ThemeApplied : Event()
        
        /**
         * Theme was reset to default.
         */
        data object ThemeReset : Event()
        
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
         * User selected an image from gallery.
         */
        data class SelectImage(val imageUri: Uri) : Action()
        
        /**
         * User wants to apply the extracted theme.
         */
        data object ApplyTheme : Action()
        
        /**
         * User wants to reset to default theme.
         */
        data object ResetTheme : Action()
        
        /**
         * User wants to dismiss the error message.
         */
        data object DismissError : Action()
        
        /**
         * User wants to pick a new image.
         */
        data object PickNewImage : Action()
    }
}
