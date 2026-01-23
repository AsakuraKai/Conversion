package com.example.conversion.presentation.ai

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.conversion.domain.model.ImageLabel
import com.example.conversion.domain.usecase.ai.AnalyzeImageUseCase
import com.example.conversion.domain.usecase.ai.GenerateSuggestionsUseCase
import com.example.conversion.presentation.ai.AISuggestionsContract.Action
import com.example.conversion.presentation.ai.AISuggestionsContract.Event
import com.example.conversion.presentation.ai.AISuggestionsContract.State
import com.example.conversion.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for AI-powered filename suggestions feature.
 * Manages image analysis and suggestion generation using ML Kit.
 * 
 * @author Sokchea (Frontend/UI Specialist)
 * @since CHUNK 13 - AI-Powered Filename Suggestions
 */
@HiltViewModel
class AISuggestionsViewModel @Inject constructor(
    private val analyzeImageUseCase: AnalyzeImageUseCase,
    private val generateSuggestionsUseCase: GenerateSuggestionsUseCase
) : BaseViewModel<State, Event>(State()) {

    /**
     * Handles user actions and updates state accordingly.
     */
    fun handleAction(action: Action) {
        when (action) {
            is Action.SelectImage -> selectImage(action.imageUri)
            is Action.AnalyzeImage -> analyzeImage()
            is Action.GenerateSuggestions -> generateSuggestions()
            is Action.AnalyzeAndSuggest -> analyzeAndSuggest(action.imageUri)
            is Action.SelectSuggestion -> selectSuggestion(action.suggestion)
            is Action.ApplySuggestion -> applySuggestion()
            is Action.UpdateConfidenceThreshold -> updateConfidenceThreshold(action.threshold)
            is Action.UpdateMaxSuggestions -> updateMaxSuggestions(action.maxCount)
            is Action.ToggleConfidenceSettings -> toggleConfidenceSettings()
            is Action.ClearAnalysis -> clearAnalysis()
            is Action.RetryAnalysis -> retryAnalysis()
            is Action.DismissError -> dismissError()
        }
    }

    private fun selectImage(imageUri: Uri) {
        updateState {
            copy(
                selectedImage = imageUri,
                detectedLabels = emptyList(),
                suggestions = emptyList(),
                selectedSuggestion = null,
                error = null
            )
        }
    }

    private fun analyzeImage() {
        val imageUri = currentState.selectedImage
        if (imageUri == null) {
            sendEvent(Event.ShowError("No image selected"))
            return
        }

        updateState { 
            copy(
                isAnalyzing = true,
                error = null,
                detectedLabels = emptyList(),
                suggestions = emptyList()
            ) 
        }

        viewModelScope.launch {
            executeUseCase(
                block = {
                    analyzeImageUseCase(
                        AnalyzeImageUseCase.Params(
                            imageUri = imageUri,
                            confidenceThreshold = currentState.confidenceThreshold,
                            maxResults = 10
                        )
                    )
                },
                onSuccess = { labels ->
                    updateState {
                        copy(
                            isAnalyzing = false,
                            detectedLabels = labels
                        )
                    }
                    sendEvent(Event.AnalysisComplete(labels.size))
                    
                    if (labels.isNotEmpty()) {
                        sendEvent(Event.ShowMessage("Detected ${labels.size} labels"))
                    } else {
                        sendEvent(Event.ShowMessage("No labels detected. Try lowering confidence threshold."))
                    }
                },
                onError = { error ->
                    updateState {
                        copy(
                            isAnalyzing = false,
                            error = error.message ?: "Analysis failed"
                        )
                    }
                    sendEvent(Event.AnalysisFailed(error.message ?: "Unknown error"))
                    sendEvent(Event.ShowError("Failed to analyze image"))
                }
            )
        }
    }

    private fun generateSuggestions() {
        val labels = currentState.detectedLabels
        if (labels.isEmpty()) {
            sendEvent(Event.ShowError("No labels available. Analyze an image first."))
            return
        }

        updateState { 
            copy(
                isGeneratingSuggestions = true,
                error = null
            ) 
        }

        viewModelScope.launch {
            executeUseCase(
                block = {
                    generateSuggestionsUseCase(
                        GenerateSuggestionsUseCase.Params(
                            labels = labels,
                            maxSuggestions = currentState.maxSuggestions,
                            allowSingleLabel = true
                        )
                    )
                },
                onSuccess = { suggestions ->
                    updateState {
                        copy(
                            isGeneratingSuggestions = false,
                            suggestions = suggestions,
                            selectedSuggestion = suggestions.firstOrNull()
                        )
                    }
                    sendEvent(Event.SuggestionsGenerated(suggestions.size))
                    
                    if (suggestions.isNotEmpty()) {
                        sendEvent(Event.ShowMessage("Generated ${suggestions.size} suggestions"))
                    } else {
                        sendEvent(Event.ShowMessage("No suggestions could be generated"))
                    }
                },
                onError = { error ->
                    updateState {
                        copy(
                            isGeneratingSuggestions = false,
                            error = error.message ?: "Failed to generate suggestions"
                        )
                    }
                    sendEvent(Event.SuggestionGenerationFailed(error.message ?: "Unknown error"))
                    sendEvent(Event.ShowError("Failed to generate suggestions"))
                }
            )
        }
    }

    private fun analyzeAndSuggest(imageUri: Uri) {
        // First select and analyze the image
        selectImage(imageUri)
        updateState { 
            copy(
                isAnalyzing = true,
                error = null
            ) 
        }

        viewModelScope.launch {
            // Step 1: Analyze image
            executeUseCase(
                block = {
                    analyzeImageUseCase(
                        AnalyzeImageUseCase.Params(
                            imageUri = imageUri,
                            confidenceThreshold = currentState.confidenceThreshold,
                            maxResults = 10
                        )
                    )
                },
                onSuccess = { labels ->
                    updateState {
                        copy(
                            isAnalyzing = false,
                            detectedLabels = labels
                        )
                    }
                    
                    if (labels.isNotEmpty()) {
                        // Step 2: Generate suggestions from labels
                        updateState { copy(isGeneratingSuggestions = true) }
                        
                        viewModelScope.launch {
                            executeUseCase(
                                block = {
                                    generateSuggestionsUseCase(
                                        GenerateSuggestionsUseCase.Params(
                                            labels = labels,
                                            maxSuggestions = currentState.maxSuggestions,
                                            allowSingleLabel = true
                                        )
                                    )
                                },
                                onSuccess = { suggestions ->
                                    updateState {
                                        copy(
                                            isGeneratingSuggestions = false,
                                            suggestions = suggestions,
                                            selectedSuggestion = suggestions.firstOrNull()
                                        )
                                    }
                                    sendEvent(Event.AnalysisComplete(labels.size))
                                    sendEvent(Event.SuggestionsGenerated(suggestions.size))
                                    sendEvent(Event.ShowMessage("Generated ${suggestions.size} suggestions from ${labels.size} labels"))
                                },
                                onError = { error ->
                                    updateState {
                                        copy(
                                            isGeneratingSuggestions = false,
                                            error = error.message
                                        )
                                    }
                                    sendEvent(Event.SuggestionGenerationFailed(error.message ?: "Unknown error"))
                                }
                            )
                        }
                    } else {
                        sendEvent(Event.ShowMessage("No labels detected. Try a different image."))
                    }
                },
                onError = { error ->
                    updateState {
                        copy(
                            isAnalyzing = false,
                            error = error.message
                        )
                    }
                    sendEvent(Event.AnalysisFailed(error.message ?: "Unknown error"))
                }
            )
        }
    }

    private fun selectSuggestion(suggestion: String) {
        updateState { copy(selectedSuggestion = suggestion) }
    }

    private fun applySuggestion() {
        val suggestion = currentState.selectedSuggestion
        if (suggestion == null) {
            sendEvent(Event.ShowError("No suggestion selected"))
            return
        }

        updateState { copy(appliedSuggestion = suggestion) }
        sendEvent(Event.SuggestionApplied(suggestion))
        sendEvent(Event.NavigateBackWithSuggestion(suggestion))
    }

    private fun updateConfidenceThreshold(threshold: Float) {
        if (threshold !in 0.0f..1.0f) {
            sendEvent(Event.ShowError("Invalid confidence threshold"))
            return
        }

        updateState { 
            copy(
                confidenceThreshold = threshold,
                // Clear previous results as they may not match new threshold
                detectedLabels = emptyList(),
                suggestions = emptyList()
            ) 
        }
        sendEvent(Event.ShowMessage("Confidence threshold updated to ${(threshold * 100).toInt()}%"))
    }

    private fun updateMaxSuggestions(maxCount: Int) {
        if (maxCount <= 0 || maxCount > GenerateSuggestionsUseCase.MAX_SUGGESTIONS_LIMIT) {
            sendEvent(Event.ShowError("Invalid suggestion count"))
            return
        }

        updateState { 
            copy(
                maxSuggestions = maxCount,
                // Clear previous suggestions as count may have changed
                suggestions = emptyList()
            ) 
        }
        sendEvent(Event.ShowMessage("Max suggestions updated to $maxCount"))
    }

    private fun toggleConfidenceSettings() {
        updateState { copy(showConfidenceSettings = !showConfidenceSettings) }
    }

    private fun clearAnalysis() {
        updateState {
            copy(
                selectedImage = null,
                detectedLabels = emptyList(),
                suggestions = emptyList(),
                selectedSuggestion = null,
                error = null,
                appliedSuggestion = null
            )
        }
    }

    private fun retryAnalysis() {
        val imageUri = currentState.selectedImage
        if (imageUri != null) {
            analyzeImage()
        } else {
            sendEvent(Event.ShowError("No image to analyze"))
        }
    }

    private fun dismissError() {
        updateState { copy(error = null) }
    }
}
