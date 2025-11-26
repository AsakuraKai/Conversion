package com.example.conversion.domain.repository

import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.FileFilter
import com.example.conversion.domain.model.FileItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for accessing media files from device storage.
 * Abstracts MediaStore operations and provides clean data access layer.
 */
interface MediaRepository {
    /**
     * Retrieves media files based on the provided filter criteria.
     *
     * @param filter Configuration specifying which files to retrieve
     * @return Result containing list of FileItems or error
     */
    suspend fun getMediaFiles(filter: FileFilter): Result<List<FileItem>>

    /**
     * Retrieves all files from a specific folder path.
     *
     * @param folderPath Absolute path to the folder
     * @return Result containing list of FileItems in the folder or error
     */
    suspend fun getFilesByFolder(folderPath: String): Result<List<FileItem>>

    /**
     * Observes changes to media files in real-time.
     * Useful for auto-updating UI when files are added/removed externally.
     *
     * @return Flow emitting updated list of FileItems whenever changes occur
     */
    fun observeMediaFiles(filter: FileFilter = FileFilter.DEFAULT): Flow<List<FileItem>>

    /**
     * Gets a single file by its content URI.
     *
     * @param uriString String representation of the file's content URI
     * @return Result containing FileItem or error if not found
     */
    suspend fun getFileByUri(uriString: String): Result<FileItem?>

    /**
     * Retrieves all unique folder paths containing media files.
     *
     * @param filter Filter to determine which media types to consider
     * @return Result containing list of folder paths or error
     */
    suspend fun getMediaFolders(filter: FileFilter = FileFilter.DEFAULT): Result<List<String>>
}
