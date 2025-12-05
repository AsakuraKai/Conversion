package com.example.conversion.presentation.folder

import androidx.lifecycle.viewModelScope
import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.usecase.folder.CreateFolderParams
import com.example.conversion.domain.usecase.folder.CreateFolderUseCase
import com.example.conversion.domain.usecase.folder.GetFoldersUseCase
import com.example.conversion.presentation.base.BaseViewModel
import com.example.conversion.presentation.folder.FolderSelectorContract.Action
import com.example.conversion.presentation.folder.FolderSelectorContract.Event
import com.example.conversion.presentation.folder.FolderSelectorContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ViewModel for Folder Selector feature
 * 
 * Handles folder browsing, navigation, and folder creation.
 * Uses GetFoldersUseCase to retrieve folders and CreateFolderUseCase to create new folders.
 */
@HiltViewModel
class FolderSelectorViewModel @Inject constructor(
    private val getFoldersUseCase: GetFoldersUseCase,
    private val createFolderUseCase: CreateFolderUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<State, Event>(State()) {
    
    init {
        // Load root folders on initialization
        handleAction(Action.LoadRootFolders)
    }
    
    /**
     * Handle user actions
     */
    fun handleAction(action: Action) {
        when (action) {
            is Action.LoadRootFolders -> loadRootFolders()
            is Action.LoadFolders -> loadFolders(action.path)
            is Action.NavigateToFolder -> navigateToFolder(action.folder)
            is Action.NavigateUp -> navigateUp()
            is Action.SelectFolder -> selectFolder(action.folder)
            is Action.ShowCreateFolderDialog -> showCreateDialog()
            is Action.HideCreateFolderDialog -> hideCreateDialog()
            is Action.CreateFolder -> createFolder(action.name)
            is Action.ConfirmSelection -> confirmSelection()
        }
    }
    
    /**
     * Load root-level folders
     */
    private fun loadRootFolders() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            
            val result = withContext(ioDispatcher) {
                getFoldersUseCase(null)
            }
            
            when (result) {
                is Result.Success -> {
                    updateState { 
                        copy(
                            folders = result.data,
                            currentPath = null,
                            isLoading = false,
                            error = null
                        ) 
                    }
                }
                is Result.Error -> {
                    updateState { 
                        copy(
                            isLoading = false,
                            error = result.exception.message ?: "Failed to load folders"
                        ) 
                    }
                    sendEvent(Event.ShowMessage(result.exception.message ?: "Failed to load folders"))
                }
                is Result.Loading -> {
                    // Already in loading state
                }
            }
        }
    }
    
    /**
     * Load folders at specific path
     */
    private fun loadFolders(path: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            
            val result = withContext(ioDispatcher) {
                getFoldersUseCase(path)
            }
            
            when (result) {
                is Result.Success -> {
                    updateState { 
                        copy(
                            folders = result.data,
                            currentPath = path,
                            isLoading = false,
                            error = null
                        ) 
                    }
                }
                is Result.Error -> {
                    updateState { 
                        copy(
                            isLoading = false,
                            error = result.exception.message ?: "Failed to load folders"
                        ) 
                    }
                    sendEvent(Event.ShowMessage(result.exception.message ?: "Failed to load folders"))
                }
                is Result.Loading -> {
                    // Already in loading state
                }
            }
        }
    }
    
    /**
     * Navigate into a folder
     */
    private fun navigateToFolder(folder: com.example.conversion.domain.model.FolderInfo) {
        loadFolders(folder.path)
        sendEvent(Event.NavigateToFolder(folder))
    }
    
    /**
     * Navigate up to parent folder
     */
    private fun navigateUp() {
        val currentPath = currentState.currentPath ?: return
        
        // Get parent path by removing last segment
        val segments = currentPath.split("/").filter { it.isNotEmpty() }
        if (segments.isEmpty()) {
            loadRootFolders()
            return
        }
        
        val parentPath = if (segments.size == 1) {
            null // Back to root
        } else {
            "/" + segments.dropLast(1).joinToString("/")
        }
        
        if (parentPath == null) {
            loadRootFolders()
        } else {
            loadFolders(parentPath)
        }
    }
    
    /**
     * Select folder as destination
     */
    private fun selectFolder(folder: com.example.conversion.domain.model.FolderInfo) {
        updateState { copy(selectedFolder = folder) }
        sendEvent(Event.FolderSelected(folder))
    }
    
    /**
     * Show create folder dialog
     */
    private fun showCreateDialog() {
        updateState { copy(showCreateDialog = true) }
    }
    
    /**
     * Hide create folder dialog
     */
    private fun hideCreateDialog() {
        updateState { copy(showCreateDialog = false) }
    }
    
    /**
     * Create new folder
     */
    private fun createFolder(name: String) {
        val parentPath = currentState.currentPath
        if (parentPath == null) {
            sendEvent(Event.ShowMessage("Cannot create folder at root level"))
            return
        }
        
        if (name.isBlank()) {
            sendEvent(Event.ShowMessage("Folder name cannot be empty"))
            return
        }
        
        viewModelScope.launch {
            val params = CreateFolderParams(
                parentPath = parentPath,
                folderName = name
            )
            
            val result = withContext(ioDispatcher) {
                createFolderUseCase(params)
            }
            
            when (result) {
                is Result.Success -> {
                    hideCreateDialog()
                    sendEvent(Event.FolderCreated(result.data))
                    sendEvent(Event.ShowMessage("Folder created successfully"))
                    // Reload folders to show the new one
                    loadFolders(parentPath)
                }
                is Result.Error -> {
                    sendEvent(Event.ShowMessage(result.exception.message ?: "Failed to create folder"))
                }
                is Result.Loading -> {
                    // Ignore loading state
                }
            }
        }
    }
    
    /**
     * Confirm current folder as destination
     */
    private fun confirmSelection() {
        val currentPath = currentState.currentPath ?: return
        
        // Create FolderInfo for current path
        viewModelScope.launch {
            val result = withContext(ioDispatcher) {
                getFoldersUseCase(currentPath)
            }
            
            when (result) {
                is Result.Success -> {
                    // Use the first folder or create a FolderInfo for current path
                    val folder = com.example.conversion.domain.model.FolderInfo(
                        uri = android.net.Uri.parse("file://$currentPath"),
                        path = currentPath,
                        name = currentPath.split("/").lastOrNull() ?: "Unknown",
                        fileCount = 0,
                        subfolderCount = result.data.size,
                        parentPath = currentPath.split("/").dropLast(1).joinToString("/").takeIf { it.isNotEmpty() },
                        isRoot = false
                    )
                    selectFolder(folder)
                }
                is Result.Error -> {
                    sendEvent(Event.ShowMessage("Failed to select folder"))
                }
                is Result.Loading -> {
                    // Ignore loading state
                }
            }
        }
    }
}
