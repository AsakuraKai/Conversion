package com.example.conversion.domain.model

/**
 * Domain model representing progress of a cloud sync operation
 * 
 * Used to track and display progress during file synchronization
 * to cloud storage providers.
 * 
 * @property currentFile Index of file currently being synced (0-based)
 * @property totalFiles Total number of files to sync
 * @property currentFileName Name of the file currently being uploaded
 * @property bytesUploaded Total bytes uploaded so far
 * @property totalBytes Total bytes to upload across all files
 * @property isComplete Whether the sync operation is complete
 */
data class SyncProgress(
    val currentFile: Int = 0,
    val totalFiles: Int = 0,
    val currentFileName: String = "",
    val bytesUploaded: Long = 0L,
    val totalBytes: Long = 0L,
    val isComplete: Boolean = false
) {
    /**
     * Returns progress as a percentage (0-100)
     */
    val percentComplete: Int
        get() = if (totalFiles > 0) {
            ((currentFile.toFloat() / totalFiles) * 100).toInt()
        } else {
            0
        }
    
    /**
     * Returns bytes progress as a percentage (0-100)
     */
    val bytesPercentComplete: Int
        get() = if (totalBytes > 0) {
            ((bytesUploaded.toFloat() / totalBytes) * 100).toInt()
        } else {
            0
        }
}
