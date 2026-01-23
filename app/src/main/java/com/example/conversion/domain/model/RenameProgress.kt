package com.example.conversion.domain.model

/**
 * Represents the progress of a batch rename operation.
 * Used to provide real-time feedback during the rename process.
 *
 * @property currentIndex The index of the file currently being processed (0-based)
 * @property total The total number of files to be processed
 * @property currentFile The file currently being processed
 * @property status The current status of the operation
 */
data class RenameProgress(
    val currentIndex: Int,
    val total: Int,
    val currentFile: FileItem,
    val status: RenameStatus
) {
    /**
     * Returns the progress percentage (0-100).
     */
    val progressPercentage: Int
        get() = if (total > 0) ((currentIndex + 1) * 100) / total else 0

    /**
     * Returns a human-readable progress string (e.g., "5/10").
     */
    val progressString: String
        get() = "${currentIndex + 1}/$total"

    /**
     * Returns true if this is the last file.
     */
    val isLastFile: Boolean
        get() = currentIndex == total - 1
}

/**
 * Represents the status of a rename operation.
 */
enum class RenameStatus {
    /**
     * The file is currently being processed
     */
    PROCESSING,

    /**
     * The file was renamed successfully
     */
    SUCCESS,

    /**
     * The file rename failed
     */
    FAILED,

    /**
     * The file was skipped (e.g., due to validation errors)
     */
    SKIPPED
}
