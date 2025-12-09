package com.example.conversion.presentation.metadata

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.conversion.domain.model.MetadataVariable
import com.example.conversion.domain.usecase.metadata.ExtractMetadataUseCase
import com.example.conversion.presentation.base.BaseViewModel
import com.example.conversion.presentation.metadata.MetadataPickerContract.Action
import com.example.conversion.presentation.metadata.MetadataPickerContract.Event
import com.example.conversion.presentation.metadata.MetadataPickerContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel for metadata variable picker screen.
 * Manages metadata extraction and variable insertion for rename patterns.
 */
@HiltViewModel
class MetadataPickerViewModel @Inject constructor(
    private val extractMetadataUseCase: ExtractMetadataUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<State, Event>(State()) {

    init {
        // Get initial pattern from navigation args if available
        val initialPattern = savedStateHandle.get<String>("currentPattern") ?: ""
        if (initialPattern.isNotEmpty()) {
            handleAction(Action.UpdatePattern(initialPattern))
        }

        // Get sample image URI from navigation args if available
        val sampleUriString = savedStateHandle.get<String>("sampleImageUri")
        sampleUriString?.let { uriString ->
            val uri = Uri.parse(uriString)
            handleAction(Action.LoadSampleMetadata(uri))
        }
    }

    /**
     * Handles user actions and updates state accordingly.
     */
    fun handleAction(action: Action) {
        when (action) {
            is Action.LoadSampleMetadata -> loadSampleMetadata(action.imageUri)
            is Action.InsertVariable -> insertVariable(action.variable)
            is Action.UpdatePattern -> updatePattern(action.pattern)
            is Action.ClearPattern -> clearPattern()
            is Action.ApplyPattern -> applyPattern()
            is Action.GeneratePreview -> generatePreview()
        }
    }

    private fun loadSampleMetadata(imageUri: Uri) {
        updateState { copy(isLoading = true, error = null) }

        viewModelScope.launch {
            executeUseCase(
                block = { extractMetadataUseCase(imageUri) },
                onSuccess = { metadata ->
                    updateState {
                        copy(
                            sampleMetadata = metadata,
                            isLoading = false,
                            error = null
                        )
                    }
                    generatePreview()
                },
                onError = { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load metadata"
                        )
                    }
                    sendEvent(Event.ShowError("Failed to load metadata from image"))
                }
            )
        }
    }

    private fun insertVariable(variable: MetadataVariable) {
        val currentPattern = currentState.currentPattern
        val newPattern = currentPattern + variable.variable

        updateState {
            copy(
                currentPattern = newPattern,
                selectedVariables = selectedVariables + variable
            )
        }

        sendEvent(Event.VariableInserted(variable))
        generatePreview()
    }

    private fun updatePattern(pattern: String) {
        updateState { copy(currentPattern = pattern) }
        
        // Find which variables are in the pattern
        val variablesInPattern = MetadataVariable.findVariables(pattern)
        updateState { copy(selectedVariables = variablesInPattern) }
        
        generatePreview()
    }

    private fun clearPattern() {
        updateState {
            copy(
                currentPattern = "",
                selectedVariables = emptyList(),
                previewFilename = ""
            )
        }
    }

    private fun applyPattern() {
        val pattern = currentState.currentPattern
        
        if (pattern.isEmpty()) {
            sendEvent(Event.ShowError("Pattern cannot be empty"))
            return
        }

        sendEvent(Event.NavigateBack(pattern))
    }

    private fun generatePreview() {
        val metadata = currentState.sampleMetadata
        val pattern = currentState.currentPattern

        if (pattern.isEmpty()) {
            updateState { copy(previewFilename = "") }
            return
        }

        // If no metadata, show pattern with placeholder text
        if (metadata == null) {
            updateState { copy(previewFilename = pattern) }
            return
        }

        // Replace variables with actual metadata values
        var preview = pattern

        // Date/Time variables
        metadata.dateTaken?.let { timestamp ->
            val date = Date(timestamp)
            preview = preview
                .replace("{date}", SimpleDateFormat("yyyyMMdd", Locale.US).format(date))
                .replace("{year}", SimpleDateFormat("yyyy", Locale.US).format(date))
                .replace("{month}", SimpleDateFormat("MM", Locale.US).format(date))
                .replace("{day}", SimpleDateFormat("dd", Locale.US).format(date))
                .replace("{time}", SimpleDateFormat("HHmmss", Locale.US).format(date))
        }

        // GPS variables
        metadata.latitude?.let { lat ->
            preview = preview.replace("{lat}", String.format("%.6f", lat))
        }
        metadata.longitude?.let { lon ->
            preview = preview.replace("{lon}", String.format("%.6f", lon))
        }
        if (metadata.hasLocation) {
            val locationStr = "${metadata.latitude}_${metadata.longitude}"
            preview = preview.replace("{location}", locationStr)
        }

        // Camera variables
        metadata.cameraModel?.let { camera ->
            val sanitized = camera.replace(" ", "_").replace("/", "_")
            preview = preview.replace("{camera}", sanitized)
        }
        metadata.fNumber?.let { fnumber ->
            preview = preview.replace("{fnumber}", fnumber.replace(".", "_"))
        }
        metadata.exposureTime?.let { exposure ->
            preview = preview.replace("{exposure}", exposure.replace("/", "_"))
        }
        metadata.iso?.let { iso ->
            preview = preview.replace("{iso}", iso)
        }
        metadata.focalLength?.let { focal ->
            preview = preview.replace("{focal}", focal.replace(".", "_"))
        }

        // Dimension variables
        metadata.dimensions?.let { (width, height) ->
            preview = preview
                .replace("{width}", width.toString())
                .replace("{height}", height.toString())
        }
        metadata.getMegapixels()?.let { mp ->
            preview = preview.replace("{mp}", String.format("%.1f", mp).replace(".", "_"))
        }
        metadata.orientation?.let { orientation ->
            preview = preview.replace("{orientation}", orientation.toString())
        }

        updateState { copy(previewFilename = preview) }
    }
}
