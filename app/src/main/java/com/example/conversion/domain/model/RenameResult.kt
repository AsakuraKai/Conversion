package com.example.conversion.domain.model

/**
 * Represents the result of a single file rename operation.
 *
 * @property originalFile The original file item before rename
 * @property newName The new filename (including extension)
 * @property success True if the rename was successful
 * @property error Error message if the rename failed (null if successful)
 */
data class RenameResult(
    val originalFile: FileItem,
    val newName: String,
    val success: Boolean,
    val error: String? = null
) {
    /**
     * Returns true if the rename failed.
     */
    val isFailed: Boolean
        get() = !success

    /**
     * Returns a human-readable status message.
     */
    val statusMessage: String
        get() = when {
            success -> "Renamed successfully"
            error != null -> "Failed: $error"
            else -> "Failed: Unknown error"
        }
}
