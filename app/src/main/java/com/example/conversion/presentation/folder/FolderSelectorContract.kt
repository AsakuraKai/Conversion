package com.example.conversion.presentation.folder

import com.example.conversion.domain.model.FolderInfo

/**
 * MVI Contract for Folder Selector feature
 * 
 * This contract defines the State, Events, and Actions for the folder selection UI,
 * allowing users to browse folders, navigate the folder hierarchy, and create new folders.
 */
object FolderSelectorContract {
    
    /**
     * UI State for folder selector
     * 
     * @property folders List of folders in current directory
     * @property currentPath Current folder path being viewed (null for root)
     * @property selectedFolder Currently selected folder for destination
     * @property isLoading Whether folders are being loaded
     * @property error Error message if folder loading failed
     * @property showCreateDialog Whether create folder dialog is visible
     */
    data class State(
        val folders: List<FolderInfo> = emptyList(),
        val currentPath: String? = null,
        val selectedFolder: FolderInfo? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val showCreateDialog: Boolean = false,
    ) {
        /**
         * Whether user can navigate up to parent folder
         */
        val canNavigateUp: Boolean 
            get() = currentPath != null
        
        /**
         * Breadcrumb path segments for navigation
         */
        val breadcrumbs: List<String> 
            get() = currentPath?.split("/")?.filter { it.isNotEmpty() } ?: emptyList()
        
        /**
         * Display path for current location
         */
        val displayPath: String
            get() = currentPath ?: "Root"
        
        /**
         * Whether folders list is empty
         */
        val isEmpty: Boolean
            get() = folders.isEmpty() && !isLoading && error == null
    }
    
    /**
     * One-time events that trigger UI actions
     */
    sealed class Event {
        /**
         * Navigate to a specific folder
         */
        data class NavigateToFolder(val folder: FolderInfo) : Event()
        
        /**
         * Show a message to the user
         */
        data class ShowMessage(val message: String) : Event()
        
        /**
         * Folder was successfully created
         */
        data class FolderCreated(val folder: FolderInfo) : Event()
        
        /**
         * Folder selection confirmed - navigate to next screen
         */
        data class FolderSelected(val folder: FolderInfo) : Event()
    }
    
    /**
     * User actions that modify state
     */
    sealed class Action {
        /**
         * Load root-level folders
         */
        data object LoadRootFolders : Action()
        
        /**
         * Load folders at specific path
         */
        data class LoadFolders(val path: String) : Action()
        
        /**
         * Navigate into a folder
         */
        data class NavigateToFolder(val folder: FolderInfo) : Action()
        
        /**
         * Navigate up to parent folder
         */
        data object NavigateUp : Action()
        
        /**
         * Select folder as destination
         */
        data class SelectFolder(val folder: FolderInfo) : Action()
        
        /**
         * Show create folder dialog
         */
        data object ShowCreateFolderDialog : Action()
        
        /**
         * Hide create folder dialog
         */
        data object HideCreateFolderDialog : Action()
        
        /**
         * Create new folder with given name
         */
        data class CreateFolder(val name: String) : Action()
        
        /**
         * Confirm current folder as destination
         */
        data object ConfirmSelection : Action()
    }
}
