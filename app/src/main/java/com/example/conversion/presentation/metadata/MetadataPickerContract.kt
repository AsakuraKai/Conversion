package com.example.conversion.presentation.metadata

import com.example.conversion.domain.model.ImageMetadata
import com.example.conversion.domain.model.MetadataVariable

/**
 * EXIF Metadata Variable Picker feature contract defining State, Events, and Actions.
 * Follows MVI pattern for unidirectional data flow.
 */
object MetadataPickerContract {

    /**
     * UI State for metadata variable picker screen.
     */
    data class State(
        val sampleMetadata: ImageMetadata? = null,
        val currentPattern: String = "",
        val selectedVariables: List<MetadataVariable> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val previewFilename: String = ""
    ) {
        /**
         * Whether metadata has been loaded.
         */
        val hasMetadata: Boolean
            get() = sampleMetadata != null

        /**
         * Whether the pattern contains any variables.
         */
        val hasVariables: Boolean
            get() = currentPattern.isNotEmpty() && MetadataVariable.containsVariables(currentPattern)

        /**
         * All available metadata variables grouped by category.
         */
        val dateTimeVariables: List<MetadataVariable>
            get() = listOf(
                MetadataVariable.DATE,
                MetadataVariable.YEAR,
                MetadataVariable.MONTH,
                MetadataVariable.DAY,
                MetadataVariable.TIME
            )

        val locationVariables: List<MetadataVariable>
            get() = listOf(
                MetadataVariable.LATITUDE,
                MetadataVariable.LONGITUDE,
                MetadataVariable.LOCATION
            )

        val cameraVariables: List<MetadataVariable>
            get() = listOf(
                MetadataVariable.CAMERA,
                MetadataVariable.FNUMBER,
                MetadataVariable.EXPOSURE,
                MetadataVariable.ISO,
                MetadataVariable.FOCAL_LENGTH
            )

        val dimensionVariables: List<MetadataVariable>
            get() = listOf(
                MetadataVariable.WIDTH,
                MetadataVariable.HEIGHT,
                MetadataVariable.MEGAPIXELS,
                MetadataVariable.ORIENTATION
            )
    }

    /**
     * One-time events sent from ViewModel to UI.
     */
    sealed class Event {
        /**
         * Variable was inserted into the pattern.
         */
        data class VariableInserted(val variable: MetadataVariable) : Event()

        /**
         * Show an error message to the user.
         */
        data class ShowError(val message: String) : Event()

        /**
         * Show an informational message.
         */
        data class ShowMessage(val message: String) : Event()

        /**
         * Navigate back with the selected pattern.
         */
        data class NavigateBack(val pattern: String) : Event()
    }

    /**
     * User actions that trigger state changes.
     */
    sealed class Action {
        /**
         * Load sample metadata from a selected image.
         */
        data class LoadSampleMetadata(val imageUri: android.net.Uri) : Action()

        /**
         * Insert a metadata variable into the pattern.
         */
        data class InsertVariable(val variable: MetadataVariable) : Action()

        /**
         * Update the current pattern text.
         */
        data class UpdatePattern(val pattern: String) : Action()

        /**
         * Clear the current pattern.
         */
        data object ClearPattern : Action()

        /**
         * Apply the current pattern and navigate back.
         */
        data object ApplyPattern : Action()

        /**
         * Generate preview with sample metadata.
         */
        data object GeneratePreview : Action()
    }
}
