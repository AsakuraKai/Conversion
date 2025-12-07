package com.example.conversion.domain.model

import android.net.Uri

/**
 * Represents a file with associated tags.
 *
 * @property fileUri URI of the tagged file
 * @property tags List of tags applied to this file
 */
data class TaggedFile(
    val fileUri: Uri,
    val tags: List<FileTag>
) {
    /**
     * Checks if the file has a specific tag.
     * @param tagId The ID of the tag to check
     * @return true if the file has the tag, false otherwise
     */
    fun hasTag(tagId: String): Boolean {
        return tags.any { it.id == tagId }
    }

    /**
     * Checks if the file has any tags.
     * @return true if the file has at least one tag, false otherwise
     */
    fun hasTags(): Boolean {
        return tags.isNotEmpty()
    }

    /**
     * Gets the count of tags applied to this file.
     */
    val tagCount: Int
        get() = tags.size
}
