package com.example.conversion.presentation.fileselection

import androidx.lifecycle.viewModelScope
import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.FileFilter
import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.usecase.fileselection.GetMediaFilesUseCase
import com.example.conversion.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for file selection screen.
 * Handles file loading, filtering, and selection logic.
 * Follows MVI pattern with State, Events, and Actions.
 */
@HiltViewModel
class FileSelectionViewModel @Inject constructor(
    private val getMediaFilesUseCase: GetMediaFilesUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<FileSelectionContract.State, FileSelectionContract.Event>(
    initialState = FileSelectionContract.State()
) {

    init {
        // Load files on initialization
        handleAction(FileSelectionContract.Action.LoadFiles)
    }

    /**
     * Handles user actions.
     */
    fun handleAction(action: FileSelectionContract.Action) {
        when (action) {
            is FileSelectionContract.Action.LoadFiles -> loadFiles()
            is FileSelectionContract.Action.RefreshFiles -> refreshFiles()
            is FileSelectionContract.Action.ToggleSelection -> toggleSelection(action.file)
            is FileSelectionContract.Action.SelectAll -> selectAll()
            is FileSelectionContract.Action.ClearSelection -> clearSelection()
            is FileSelectionContract.Action.ApplyFilter -> applyFilter(action.filter)
            is FileSelectionContract.Action.ConfirmSelection -> confirmSelection()
            is FileSelectionContract.Action.ClearError -> clearError()
        }
    }

    /**
     * Loads files based on current filter.
     */
    private fun loadFiles() {
        updateState { copy(isLoading = true, error = null) }

        viewModelScope.launch(ioDispatcher) {
            try {
                val files = getMediaFilesUseCase.execute(currentState.filter)
                updateState {
                    copy(
                        files = files,
                        isLoading = false,
                        error = null
                    )
                }

                // Show message if no files found
                if (files.isEmpty()) {
                    sendEvent(FileSelectionContract.Event.ShowMessage(
                        "No media files found. Try changing the filter."
                    ))
                }
            } catch (e: Exception) {
                updateState {
                    copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load files"
                    )
                }
                sendEvent(FileSelectionContract.Event.ShowError(
                    title = "Error Loading Files",
                    message = e.message ?: "An unexpected error occurred while loading files."
                ))
            }
        }
    }

    /**
     * Refreshes the file list.
     */
    private fun refreshFiles() {
        // Clear selections when refreshing
        updateState { copy(selectedFiles = emptySet()) }
        loadFiles()
    }

    /**
     * Toggles selection of a file.
     */
    private fun toggleSelection(file: FileItem) {
        updateState {
            val newSelection = if (file in selectedFiles) {
                selectedFiles - file
            } else {
                selectedFiles + file
            }
            copy(selectedFiles = newSelection)
        }
    }

    /**
     * Selects all visible files.
     */
    private fun selectAll() {
        updateState {
            copy(selectedFiles = files.toSet())
        }
        sendEvent(FileSelectionContract.Event.ShowMessage(
            "Selected ${currentState.selectedCount} files"
        ))
    }

    /**
     * Clears all selections.
     */
    private fun clearSelection() {
        updateState {
            copy(selectedFiles = emptySet())
        }
    }

    /**
     * Applies a new filter and reloads files.
     */
    private fun applyFilter(filter: FileFilter) {
        updateState {
            copy(
                filter = filter,
                selectedFiles = emptySet() // Clear selections when filter changes
            )
        }
        loadFiles()
    }

    /**
     * Confirms selection and navigates to rename screen.
     */
    private fun confirmSelection() {
        if (currentState.selectedFiles.isEmpty()) {
            sendEvent(FileSelectionContract.Event.ShowMessage(
                "Please select at least one file to rename"
            ))
            return
        }

        // Navigate with selected files (sorted by name for consistency)
        val selectedFilesList = currentState.selectedFiles.sortedBy { it.name }
        sendEvent(FileSelectionContract.Event.NavigateToRename(selectedFilesList))
    }

    /**
     * Clears the error message.
     */
    private fun clearError() {
        updateState { copy(error = null) }
    }

    /**
     * Public helper to get selected file count.
     */
    fun getSelectedCount(): Int = currentState.selectedCount

    /**
     * Public helper to check if file is selected.
     */
    fun isFileSelected(file: FileItem): Boolean = file in currentState.selectedFiles
}
