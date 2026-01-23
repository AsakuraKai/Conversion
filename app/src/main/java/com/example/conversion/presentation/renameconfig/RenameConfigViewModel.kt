package com.example.conversion.presentation.renameconfig

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.model.SortStrategy
import com.example.conversion.domain.usecase.rename.GenerateFilenameUseCase
import com.example.conversion.domain.usecase.rename.ValidateFilenameUseCase
import com.example.conversion.presentation.base.BaseViewModel
import com.example.conversion.presentation.renameconfig.RenameConfigContract.Action
import com.example.conversion.presentation.renameconfig.RenameConfigContract.Event
import com.example.conversion.presentation.renameconfig.RenameConfigContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for batch rename configuration screen.
 * Manages rename settings and generates filename previews.
 */
@HiltViewModel
class RenameConfigViewModel @Inject constructor(
    private val generateFilenameUseCase: GenerateFilenameUseCase,
    private val validateFilenameUseCase: ValidateFilenameUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<State, Event>(State()) {

    // Create a sample FileItem for preview generation
    private val sampleFile = FileItem(
        id = 1L,
        uri = Uri.EMPTY,
        name = "sample.jpg",
        path = "/sample.jpg",
        size = 1024L,
        mimeType = "image/jpeg",
        dateModified = System.currentTimeMillis()
    )

    init {
        // Initialize with file count from navigation args if available
        val fileCount = savedStateHandle.get<Int>("fileCount") ?: 1
        handleAction(Action.Initialize(fileCount))
    }

    /**
     * Handles user actions and updates state accordingly.
     */
    fun handleAction(action: Action) {
        when (action) {
            is Action.UpdatePrefix -> updatePrefix(action.prefix)
            is Action.UpdateStartNumber -> updateStartNumber(action.number)
            is Action.UpdateDigitCount -> updateDigitCount(action.count)
            is Action.TogglePreserveExtension -> togglePreserveExtension(action.preserve)
            is Action.UpdateSortStrategy -> updateSortStrategy(action.strategy)
            is Action.Confirm -> confirmConfiguration()
            is Action.Back -> navigateBack()
            is Action.Initialize -> initialize(action.fileCount)
        }
    }

    private fun initialize(fileCount: Int) {
        updateState { copy(selectedFileCount = fileCount) }
        generatePreview()
    }

    private fun updatePrefix(prefix: String) {
        val newConfig = currentState.config.copy(prefix = prefix)
        updateState { copy(config = newConfig) }
        validateAndGeneratePreview()
    }

    private fun updateStartNumber(number: Int) {
        val newConfig = currentState.config.copy(startNumber = number)
        updateState { copy(config = newConfig) }
        generatePreview()
    }

    private fun updateDigitCount(count: Int) {
        val newConfig = currentState.config.copy(digitCount = count)
        updateState { copy(config = newConfig) }
        generatePreview()
    }

    private fun togglePreserveExtension(preserve: Boolean) {
        val newConfig = currentState.config.copy(preserveExtension = preserve)
        updateState { copy(config = newConfig) }
        generatePreview()
    }

    private fun updateSortStrategy(strategy: SortStrategy) {
        val newConfig = currentState.config.copy(sortStrategy = strategy)
        updateState { copy(config = newConfig) }
    }

    /**
     * Validates the configuration and generates preview filename.
     */
    private fun validateAndGeneratePreview() {
        viewModelScope.launch {
            val config = currentState.config

            // First validate the configuration
            val validationError = config.getValidationError()
            
            if (validationError != null) {
                updateState { 
                    copy(
                        validationError = validationError,
                        previewFilename = ""
                    )
                }
                return@launch
            }

            // Validate prefix using ValidateFilenameUseCase
            val result = validateFilenameUseCase(config.prefix)
            result.handleResult(
                onSuccess = { validation ->
                    if (!validation.isValid) {
                        updateState { 
                            copy(
                                validationError = validation.errorMessage,
                                previewFilename = ""
                            )
                        }
                    } else {
                        updateState { copy(validationError = null) }
                        generatePreview()
                    }
                },
                onError = { error ->
                    updateState { 
                        copy(
                            validationError = "Validation error: ${error.message}",
                            previewFilename = ""
                        )
                    }
                }
            )
        }
    }

    /**
     * Generates a preview filename using the current configuration.
     */
    private fun generatePreview() {
        if (currentState.config.prefix.isEmpty()) {
            updateState { copy(previewFilename = "") }
            return
        }

        viewModelScope.launch {
            val params = GenerateFilenameUseCase.Params(
                fileItem = sampleFile,
                config = currentState.config,
                index = 0
            )

            val result = generateFilenameUseCase(params)
            result.handleResult(
                onSuccess = { filename ->
                    updateState { copy(previewFilename = filename) }
                },
                onError = { error ->
                    updateState { 
                        copy(
                            previewFilename = "",
                            validationError = "Preview error: ${error.message}"
                        )
                    }
                }
            )
        }
    }

    private fun confirmConfiguration() {
        val config = currentState.config

        // Final validation
        if (!currentState.canProceed) {
            sendEvent(Event.ShowMessage("Please fix validation errors before proceeding"))
            return
        }

        // Navigate to preview screen with the configuration
        sendEvent(Event.NavigateToPreview(config))
    }

    private fun navigateBack() {
        sendEvent(Event.NavigateBack)
    }
}
