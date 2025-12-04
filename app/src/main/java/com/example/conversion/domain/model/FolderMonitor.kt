package com.example.conversion.domain.model

import android.net.Uri

/**
 * Represents a folder that is being monitored for file changes.
 * When enabled, new files added to this folder will be automatically renamed.
 *
 * @property folderPath The absolute path of the folder being monitored
 * @property folderUri The URI of the folder (for scoped storage)
 * @property renameConfig The rename configuration to apply to new files
 * @property isActive Whether monitoring is currently active
 * @property pattern Optional file pattern to match (e.g., "*.jpg", "IMG_*")
 * @property monitorSubfolders Whether to monitor subfolders as well
 */
data class FolderMonitor(
    val folderPath: String,
    val folderUri: Uri,
    val renameConfig: RenameConfig,
    val isActive: Boolean = false,
    val pattern: String? = null,
    val monitorSubfolders: Boolean = false
) {
    /**
     * Validates the folder monitor configuration.
     * @return true if the configuration is valid, false otherwise
     */
    fun isValid(): Boolean {
        return folderPath.isNotBlank() &&
                renameConfig.isValid()
    }

    /**
     * Checks if a filename matches the configured pattern.
     * @param filename The filename to check
     * @return true if the pattern is null or the filename matches
     */
    fun matchesPattern(filename: String): Boolean {
        if (pattern.isNullOrBlank()) return true
        
        // Convert simple wildcard pattern to regex
        val regexPattern = pattern
            .replace(".", "\\.")
            .replace("*", ".*")
            .replace("?", ".")
        
        return filename.matches(Regex(regexPattern, RegexOption.IGNORE_CASE))
    }
}

/**
 * Represents the status of folder monitoring.
 */
sealed class MonitoringStatus {
    /**
     * Monitoring is active.
     * @property folderPath The path of the monitored folder
     * @property filesProcessed Number of files processed since monitoring started
     */
    data class Active(
        val folderPath: String,
        val filesProcessed: Int = 0
    ) : MonitoringStatus()

    /**
     * Monitoring is inactive/stopped.
     */
    data object Inactive : MonitoringStatus()

    /**
     * Monitoring encountered an error.
     * @property error The error message
     */
    data class Error(val error: String) : MonitoringStatus()
}

/**
 * Represents a file event detected by the folder monitor.
 *
 * @property filePath The path of the file that triggered the event
 * @property eventType The type of event (created, modified, etc.)
 * @property timestamp The timestamp when the event occurred
 */
data class FileEvent(
    val filePath: String,
    val eventType: FileEventType,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Types of file events that can be monitored.
 */
enum class FileEventType {
    /**
     * A new file was created in the monitored folder.
     */
    CREATED,

    /**
     * An existing file was modified.
     */
    MODIFIED,

    /**
     * A file was deleted from the monitored folder.
     */
    DELETED,

    /**
     * A file was moved into or out of the monitored folder.
     */
    MOVED
}
