package com.example.conversion.presentation.ai

import android.net.Uri
import com.example.conversion.domain.model.ImageLabel

/**
 * AI-Powered Filename Suggestions feature contract defining State, Events, and Actions.
 * Follows MVI pattern for unidirectional data flow.
 * 
 * @author Sokchea (Frontend/UI Specialist)
 * @since CHUNK 13 - AI-Powered Filename Suggestions
 */
object AISuggestionsContract {

    /**
     * UI State for AI suggestions feature.
     */
    data class State(
        val selectedImage: Uri? = null,
        val detectedLabels: List<ImageLabel> = emptyList(),
        val suggestions: List<String> = emptyList(),
        val selectedSuggestion: String? = null,
        val isAnalyzing: Boolean = false,
        val isGeneratingSuggestions: Boolean = false,
        val error: String? = null,
        val confidenceThreshold: Float = ImageLabel.DEFAULT_CONFIDENCE_THRESHOLD,
        val maxSuggestions: Int = 5,
        val showConfidenceSettings: Boolean = false,
        val appliedSuggestion: String? = null
    ) {
        /**
         * Whether an image is selected for analysis.
         */
        val hasImage: Boolean
            get() = selectedImage != null

        /**
         * Whether any labels were detected.
         */
        val hasLabels: Boolean
            get() = detectedLabels.isNotEmpty()

        /**
         * Whether any suggestions are available.
         */
        val hasSuggestions: Boolean
            get() = suggestions.isNotEmpty()

        /**
         * Whether the UI is in a loading state.
         */
        val isLoading: Boolean
            get() = isAnalyzing || isGeneratingSuggestions

        /**
         * Whether a suggestion can be applied.
         */
        val canApplySuggestion: Boolean
            get() = selectedSuggestion != null && !isLoading

        /**
         * Top 3 labels for quick display.
         */
        val topLabels: List<ImageLabel>
            get() = detectedLabels.take(3)

        /**
         * Formatted confidence threshold for display (e.g., "70%").
         */
        val confidenceThresholdDisplay: String
            get() = "${(confidenceThreshold * 100).toInt()}%"
    }

    /**
     * One-time events sent from ViewModel to UI.
     */
    sealed class Event {
        /**
         * Image analysis completed successfully.
         */
        data class AnalysisComplete(val labelCount: Int) : Event()

        /**
         * Filename suggestions generated successfully.
         */
        data class SuggestionsGenerated(val count: Int) : Event()

        /**
         * A suggestion was applied successfully.
         */
        data class SuggestionApplied(val suggestion: String) : Event()

        /**
         * Show an error message to the user.
         */
        data class ShowError(val message: String) : Event()

        /**
         * Show an informational message.
         */
        data class ShowMessage(val message: String) : Event()

        /**
         * Navigate back with applied suggestion.
         */
        data class NavigateBackWithSuggestion(val suggestion: String) : Event()

        /**
         * Image analysis failed.
         */
        data class AnalysisFailed(val reason: String) : Event()

        /**
         * Suggestion generation failed.
         */
        data class SuggestionGenerationFailed(val reason: String) : Event()
    }

    /**
     * User actions that trigger state changes.
     */
    sealed class Action {
        /**
         * Select an image for AI analysis.
         */
        data class SelectImage(val imageUri: Uri) : Action()

        /**
         * Analyze the selected image to detect labels.
         */
        data object AnalyzeImage : Action()

        /**
         * Generate filename suggestions from detected labels.
         */
        data object GenerateSuggestions : Action()

        /**
         * Analyze image and generate suggestions in one action.
         */
        data class AnalyzeAndSuggest(val imageUri: Uri) : Action()

        /**
         * Select a suggestion from the list.
         */
        data class SelectSuggestion(val suggestion: String) : Action()

        /**
         * Apply the selected suggestion.
         */
        data object ApplySuggestion : Action()

        /**
         * Update confidence threshold for label detection.
         */
        data class UpdateConfidenceThreshold(val threshold: Float) : Action()

        /**
         * Update maximum number of suggestions to generate.
         */
        data class UpdateMaxSuggestions(val maxCount: Int) : Action()

        /**
         * Toggle confidence settings visibility.
         */
        data object ToggleConfidenceSettings : Action()

        /**
         * Clear current analysis and suggestions.
         */
        data object ClearAnalysis : Action()

        /**
         * Retry failed analysis.
         */
        data object RetryAnalysis : Action()

        /**
         * Dismiss error state.
         */
        data object DismissError : Action()
    }
}
