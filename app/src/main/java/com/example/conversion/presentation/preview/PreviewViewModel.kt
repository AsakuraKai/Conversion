package com.example.conversion.presentation.preview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.model.PreviewItem
import com.example.conversion.domain.model.PreviewSummary
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.usecase.preview.GeneratePreviewUseCase
import com.example.conversion.presentation.base.BaseViewModel
import com.example.conversion.presentation.preview.PreviewContract.Action
import com.example.conversion.presentation.preview.PreviewContract.Event
import com.example.conversion.presentation.preview.PreviewContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for preview screen.
 * Manages preview generation and rename confirmation.
 */
@HiltViewModel
class PreviewViewModel @Inject constructor(
    private val generatePreviewUseCase: GeneratePreviewUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<State, Event>(State.Loading) {

    private var currentFiles: List<FileItem> = emptyList()
    private var currentConfig: RenameConfig? = null

    /**
     * Handles user actions and updates state accordingly.
     */
    fun handleAction(action: Action) {
        when (action) {
            is Action.Initialize -> initialize(action.files, action.config)
            is Action.ConfirmRename -> confirmRename()
            is Action.Back -> navigateBack()
            is Action.Retry -> retry()
            is Action.EditItem -> editItem(action.itemId)
            is Action.SaveCustomName -> saveCustomName(action.itemId, action.customName)
            is Action.CancelEdit -> cancelEdit()
            is Action.ResetCustomName -> resetCustomName(action.itemId)
        }
    }

    /**
     * Initialize and generate preview for the given files and configuration.
     */
    private fun initialize(files: List<FileItem>, config: RenameConfig) {
        currentFiles = files
        currentConfig = config
        generatePreview(files, config)
    }

    /**
     * Generate preview for the given files and configuration.
     */
    private fun generatePreview(files: List<FileItem>, config: RenameConfig) {
        if (files.isEmpty()) {
            setState(State.Error("No files selected"))
            return
        }

        setState(State.Loading)

        viewModelScope.launch {
            val params = GeneratePreviewUseCase.Params(
                files = files,
                config = config
            )

            val result = generatePreviewUseCase(params)
            
            result.handleResult(
                onSuccess = { previews ->
                    val summary = PreviewSummary.from(previews)
                    setState(
                        State.Success(
                            previews = previews,
                            summary = summary,
                            config = config
                        )
                    )
                },
                onError = { error ->
                    setState(
                        State.Error(
                            message = error.message ?: "Failed to generate preview"
                        )
                    )
                }
            )
        }
    }

    /**
     * Retry preview generation after error.
     */
    private fun retry() {
        val config = currentConfig
        if (config != null && currentFiles.isNotEmpty()) {
            generatePreview(currentFiles, config)
        } else {
            sendEvent(Event.ShowMessage("Cannot retry: missing configuration"))
        }
    }

    /**
     * Confirm rename and navigate to rename progress screen.
     */
    private fun confirmRename() {
        val state = currentState
        
        if (state !is State.Success) {
            sendEvent(Event.ShowMessage("Preview not ready"))
            return
        }

        if (!state.canProceed) {
            sendEvent(Event.ShowMessage("Cannot proceed: ${state.summary.message}"))
            return
        }

        // Get only files that can be renamed (no conflicts and changed)
        val filesToRename = state.previews
            .filter { it.canRename }
            .map { it.original }

        if (filesToRename.isEmpty()) {
            sendEvent(Event.ShowMessage("No files to rename"))
            return
        }

        // Navigate to rename progress screen
        sendEvent(Event.NavigateToRenameProgress(filesToRename, state.config))
    }

    /**
     * Navigate back to configuration screen.
     */
    private fun navigateBack() {
        sendEvent(Event.NavigateBack)
    }
    
    /**
     * Start editing a specific preview item.
     */
    private fun editItem(itemId: String) {
        val state = currentState
        if (state is State.Success) {
            setState(state.copy(editingItemId = itemId))
        }
    }
    
    /**
     * Save custom name for a preview item.
     */
    private fun saveCustomName(itemId: String, customName: String) {
        val state = currentState
        if (state !is State.Success) return
        
        // Validate custom name
        val trimmedName = customName.trim()
        if (trimmedName.isEmpty()) {
            sendEvent(Event.ShowMessage("Filename cannot be empty"))
            return
        }
        
        // Check for conflicts with other items
        val hasConflict = state.previews.any { preview ->
            val effectiveName = if (preview.original.id == itemId) {
                trimmedName
            } else {
                state.getEffectiveName(preview.original.id, preview.previewName)
            }
            
            // Check if this name conflicts with other files
            state.previews.any { other ->
                other.original.id != preview.original.id &&
                state.getEffectiveName(other.original.id, other.previewName) == effectiveName
            }
        }
        
        if (hasConflict) {
            sendEvent(Event.ShowMessage("This name conflicts with another file"))
            return
        }
        
        // Update custom names
        val newCustomNames = state.customNames.toMutableMap()
        newCustomNames[itemId] = trimmedName
        
        setState(state.copy(
            customNames = newCustomNames,
            editingItemId = null
        ))
        
        sendEvent(Event.ShowMessage("Custom name saved"))
    }
    
    /**
     * Cancel editing.
     */
    private fun cancelEdit() {
        val state = currentState
        if (state is State.Success) {
            setState(state.copy(editingItemId = null))
        }
    }
    
    /**
     * Reset custom name to generated name.
     */
    private fun resetCustomName(itemId: String) {
        val state = currentState
        if (state !is State.Success) return
        
        val newCustomNames = state.customNames.toMutableMap()
        newCustomNames.remove(itemId)
        
        setState(state.copy(customNames = newCustomNames))
        sendEvent(Event.ShowMessage("Reset to generated name"))
    }

    override fun handleError(error: Throwable) {
        super.handleError(error)
        setState(State.Error(error.message ?: "Unknown error occurred"))
    }
}
