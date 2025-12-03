package com.example.conversion.domain.repository

import android.net.Uri
import com.example.conversion.domain.common.Result

/**
 * Repository interface for file rename operations.
 * Handles the actual file renaming through MediaStore or file system.
 */
interface FileRenameRepository {
    
    /**
     * Renames a file identified by its URI.
     *
     * @param uri The URI of the file to rename
     * @param newName The new filename (without path, may include extension)
     * @return Result containing the new URI on success, or error information
     */
    suspend fun renameFile(uri: Uri, newName: String): Result<Uri>
    
    /**
     * Checks if a file with the given name already exists in the same directory.
     *
     * @param uri The URI of the file being renamed
     * @param newName The proposed new filename
     * @return true if a conflict exists, false otherwise
     */
    suspend fun checkNameConflict(uri: Uri, newName: String): Boolean
    
    /**
     * Batch rename multiple files with error recovery.
     * Continues processing even if individual files fail.
     *
     * @param renamePairs List of (original URI, new name) pairs
     * @return Map of original URI to Result (success or failure for each file)
     */
    suspend fun batchRenameFiles(renamePairs: List<Pair<Uri, String>>): Map<Uri, Result<Uri>>
}
