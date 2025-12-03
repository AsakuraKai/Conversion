package com.example.conversion.domain.repository

import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.FolderInfo
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing folder operations.
 * Abstracts Storage Access Framework (SAF) and DocumentFile operations.
 * 
 * This repository provides functionality for:
 * - Browsing folder hierarchies
 * - Creating new folders
 * - Getting folder metadata and contents
 * - Observing folder changes
 */
interface FolderRepository {
    /**
     * Retrieves all folders (subdirectories) within a specified parent path.
     * 
     * @param parentPath Absolute path to the parent folder. Use null or empty string for root.
     * @return Result containing list of FolderInfo for all subfolders, or error
     */
    suspend fun getFolders(parentPath: String?): Result<List<FolderInfo>>

    /**
     * Retrieves detailed information about a specific folder.
     * 
     * @param folderPath Absolute path to the folder
     * @return Result containing FolderInfo with metadata, or error if folder doesn't exist
     */
    suspend fun getFolderInfo(folderPath: String): Result<FolderInfo?>

    /**
     * Creates a new folder at the specified location.
     * 
     * @param parentPath Absolute path where the new folder should be created
     * @param folderName Name for the new folder (without path separators)
     * @return Result containing FolderInfo for the newly created folder, or error
     */
    suspend fun createFolder(parentPath: String, folderName: String): Result<FolderInfo>

    /**
     * Gets all available root storage locations on the device.
     * Typically includes internal storage and external SD cards.
     * 
     * @return Result containing list of root FolderInfo objects, or error
     */
    suspend fun getRootFolders(): Result<List<FolderInfo>>

    /**
     * Observes changes to folders within a specific parent path.
     * Emits updated list whenever folders are added, removed, or modified.
     * 
     * @param parentPath Absolute path to the parent folder to observe
     * @return Flow emitting updated list of FolderInfo whenever changes occur
     */
    fun observeFolders(parentPath: String?): Flow<List<FolderInfo>>

    /**
     * Validates whether a folder name is valid for creation.
     * Checks for illegal characters, reserved names, and length limits.
     * 
     * @param folderName The proposed folder name to validate
     * @return Result.Success with true if valid, or Result.Error with reason if invalid
     */
    suspend fun validateFolderName(folderName: String): Result<Boolean>

    /**
     * Checks if a folder exists at the specified path.
     * 
     * @param folderPath Absolute path to check
     * @return Result containing true if folder exists and is accessible, false otherwise
     */
    suspend fun folderExists(folderPath: String): Result<Boolean>

    /**
     * Gets the parent folder path from a given folder path.
     * 
     * @param folderPath Absolute path to the folder
     * @return Result containing parent folder path, or null if already at root
     */
    suspend fun getParentFolder(folderPath: String): Result<String?>
}
