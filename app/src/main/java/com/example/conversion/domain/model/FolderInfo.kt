package com.example.conversion.domain.model

import android.net.Uri

/**
 * Represents a folder in device storage with metadata.
 * Used for folder selection and navigation in the destination folder selector.
 *
 * @property uri Content URI for accessing the folder (SAF-compatible)
 * @property path Absolute file path on device
 * @property name Display name of the folder
 * @property fileCount Number of files directly in this folder (not including subfolders)
 * @property subfolderCount Number of immediate subfolders
 * @property parentPath Path to parent folder (null if root)
 * @property isRoot Whether this is a root-level folder
 */
data class FolderInfo(
    val uri: Uri,
    val path: String,
    val name: String,
    val fileCount: Int = 0,
    val subfolderCount: Int = 0,
    val parentPath: String? = null,
    val isRoot: Boolean = false
) {
    /**
     * Returns true if this folder contains any items (files or subfolders).
     */
    val isEmpty: Boolean
        get() = fileCount == 0 && subfolderCount == 0

    /**
     * Returns true if this folder has subfolders that can be navigated into.
     */
    val hasSubfolders: Boolean
        get() = subfolderCount > 0

    /**
     * Returns true if this folder contains files.
     */
    val hasFiles: Boolean
        get() = fileCount > 0

    /**
     * Returns total number of items (files + subfolders).
     */
    val totalItems: Int
        get() = fileCount + subfolderCount

    /**
     * Returns a human-readable summary of folder contents.
     * Example: "15 files, 3 folders" or "Empty" or "10 files"
     */
    val contentSummary: String
        get() = when {
            isEmpty -> "Empty"
            fileCount > 0 && subfolderCount > 0 -> "$fileCount ${if (fileCount == 1) "file" else "files"}, $subfolderCount ${if (subfolderCount == 1) "folder" else "folders"}"
            fileCount > 0 -> "$fileCount ${if (fileCount == 1) "file" else "files"}"
            else -> "$subfolderCount ${if (subfolderCount == 1) "folder" else "folders"}"
        }

    /**
     * Returns the folder's display path.
     * For root folders, returns just the name.
     * For nested folders, returns the full path or relative path from a root.
     */
    val displayPath: String
        get() = if (isRoot) name else path

    companion object {
        /**
         * Creates a FolderInfo for the root storage location.
         */
        fun createRoot(uri: Uri, path: String, name: String = "Internal Storage"): FolderInfo {
            return FolderInfo(
                uri = uri,
                path = path,
                name = name,
                isRoot = true
            )
        }
    }
}
