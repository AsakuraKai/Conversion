package com.example.conversion.domain.util

/**
 * Interface for providing localized strings to the domain layer.
 * 
 * This abstraction allows the domain layer to remain platform-independent
 * while still being able to return user-facing error messages and strings.
 * 
 * Implementation should be provided by the data/presentation layer using
 * Android's Context and Resources.
 */
interface LocalizedStringProvider {
    
    /**
     * Get a localized string by its resource key.
     * 
     * @param key The string resource key (e.g., "error_no_permission")
     * @return The localized string value
     */
    fun getString(key: String): String
    
    /**
     * Get a localized string with format arguments.
     * 
     * @param key The string resource key
     * @param formatArgs Arguments to format into the string
     * @return The formatted localized string
     */
    fun getString(key: String, vararg formatArgs: Any): String
    
    /**
     * Get a plural quantity string.
     * 
     * @param key The plurals resource key
     * @param quantity The quantity to determine plural form
     * @param formatArgs Additional format arguments
     * @return The localized plural string
     */
    fun getQuantityString(key: String, quantity: Int, vararg formatArgs: Any): String
}

/**
 * String resource keys for domain layer error messages and user-facing strings.
 * 
 * These constants should match the keys in strings.xml resources.
 */
object StringKeys {
    // Common
    const val OK = "ok"
    const val CANCEL = "cancel"
    const val ERROR = "error"
    const val SUCCESS = "success"
    const val LOADING = "loading"
    
    // Permissions
    const val PERMISSION_DENIED = "permission_denied"
    const val PERMISSION_GRANTED = "permission_granted"
    
    // File Operations
    const val NO_FILES_FOUND = "no_files_found"
    const val RENAME_COMPLETE = "rename_complete"
    const val RENAME_FAILED = "rename_failed"
    
    // Folder Operations
    const val FOLDER_CREATED = "folder_created"
    const val FOLDER_EXISTS = "folder_exists"
    const val INVALID_FOLDER_NAME = "invalid_folder_name"
    
    // Templates
    const val TEMPLATE_SAVED = "template_saved"
    const val TEMPLATE_DELETED = "template_deleted"
    const val NO_TEMPLATES = "no_templates"
    
    // Tags
    const val TAG_CREATED = "tag_created"
    const val TAG_DELETED = "tag_deleted"
    const val NO_TAGS = "no_tags"
    
    // Cloud Sync
    const val SYNC_COMPLETE = "sync_complete"
    const val SYNC_FAILED = "sync_failed"
    
    // OCR
    const val TEXT_EXTRACTED = "text_extracted"
    const val NO_TEXT_FOUND = "no_text_found"
    
    // History
    const val UNDO_SUCCESS = "undo_success"
    const val REDO_SUCCESS = "redo_success"
    const val HISTORY_CLEARED = "history_cleared"
    const val NO_HISTORY = "no_history"
    
    // Error Messages
    const val ERROR_NO_PERMISSION = "error_no_permission"
    const val ERROR_FILE_NOT_FOUND = "error_file_not_found"
    const val ERROR_RENAME_FAILED = "error_rename_failed"
    const val ERROR_INVALID_FILENAME = "error_invalid_filename"
    const val ERROR_NETWORK = "error_network"
    const val ERROR_UNKNOWN = "error_unknown"
    const val ERROR_AUTH_FAILED = "error_auth_failed"
    const val ERROR_SYNC_FAILED = "error_sync_failed"
    
    // Plurals
    const val FILES_COUNT = "files_count"
    const val FOLDERS_COUNT = "folders_count"
    const val TEMPLATES_COUNT = "templates_count"
    const val TAGS_COUNT = "tags_count"
}
