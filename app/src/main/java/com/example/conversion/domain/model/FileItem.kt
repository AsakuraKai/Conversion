package com.example.conversion.domain.model

import android.net.Uri

/**
 * Represents a media file item from device storage.
 * Used for file selection and batch rename operations.
 *
 * @property id Unique identifier from MediaStore
 * @property uri Content URI for accessing the file
 * @property name Current file name (with extension)
 * @property path Absolute file path on device
 * @property size File size in bytes
 * @property mimeType MIME type (e.g., "image/jpeg", "video/mp4")
 * @property dateModified Last modification timestamp in milliseconds
 * @property thumbnailUri URI for file thumbnail (if available)
 */
data class FileItem(
    val id: Long,
    val uri: Uri,
    val name: String,
    val path: String,
    val size: Long,
    val mimeType: String,
    val dateModified: Long,
    val thumbnailUri: Uri? = null
) {
    /**
     * Returns true if the file is an image based on MIME type.
     */
    val isImage: Boolean
        get() = mimeType.startsWith("image/", ignoreCase = true)

    /**
     * Returns true if the file is a video based on MIME type.
     */
    val isVideo: Boolean
        get() = mimeType.startsWith("video/", ignoreCase = true)

    /**
     * Returns true if the file is an audio file based on MIME type.
     */
    val isAudio: Boolean
        get() = mimeType.startsWith("audio/", ignoreCase = true)

    /**
     * Returns the file extension (without the dot).
     * Returns empty string if no extension is found.
     */
    val extension: String
        get() = name.substringAfterLast('.', "")

    /**
     * Returns the file name without extension.
     */
    val nameWithoutExtension: String
        get() = name.substringBeforeLast('.', name)

    /**
     * Returns human-readable file size.
     */
    val formattedSize: String
        get() = when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "${size / 1024} KB"
            size < 1024 * 1024 * 1024 -> "${size / (1024 * 1024)} MB"
            else -> "${size / (1024 * 1024 * 1024)} GB"
        }
}
