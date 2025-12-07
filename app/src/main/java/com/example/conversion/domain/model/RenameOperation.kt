package com.example.conversion.domain.model

import android.net.Uri

/**
 * Represents a single rename operation that can be undone or redone.
 *
 * This model captures all information needed to reverse or reapply a file rename operation,
 * including both the original and new URIs and names, as well as timing information.
 *
 * @property id Unique identifier for this operation
 * @property originalUri URI of the file before rename
 * @property newUri URI of the file after rename
 * @property originalName Original filename (including extension)
 * @property newName New filename (including extension)
 * @property timestamp Time when the operation was performed (epoch milliseconds)
 */
data class RenameOperation(
    val id: String,
    val originalUri: Uri,
    val newUri: Uri,
    val originalName: String,
    val newName: String,
    val timestamp: Long
) {
    /**
     * Validates that this operation has all required data.
     *
     * @return true if all fields are valid, false otherwise
     */
    fun isValid(): Boolean {
        return id.isNotBlank() &&
                originalName.isNotBlank() &&
                newName.isNotBlank() &&
                timestamp > 0
    }

    companion object {
        /**
         * Creates a new RenameOperation with current timestamp.
         *
         * @param id Unique identifier
         * @param originalUri Original file URI
         * @param newUri New file URI
         * @param originalName Original filename
         * @param newName New filename
         * @return A new RenameOperation instance
         */
        fun create(
            id: String,
            originalUri: Uri,
            newUri: Uri,
            originalName: String,
            newName: String
        ): RenameOperation {
            return RenameOperation(
                id = id,
                originalUri = originalUri,
                newUri = newUri,
                originalName = originalName,
                newName = newName,
                timestamp = System.currentTimeMillis()
            )
        }
    }
}
