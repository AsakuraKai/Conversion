package com.example.conversion.data.repository

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.FolderInfo
import com.example.conversion.domain.repository.FolderRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

/**
 * Implementation of FolderRepository for managing folder operations.
 * 
 * NOTE: This is a MOCK implementation for Phase 2 development.
 * Uses java.io.File for basic folder operations until Sokchea implements
 * the full Storage Access Framework (SAF) UI components.
 * 
 * TODO: Replace with full DocumentFile/SAF implementation when UI is ready
 * TODO: Add proper scoped storage handling for Android 10+
 * TODO: Integrate with SAF permission dialogs from UI layer
 */
class FolderRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : FolderRepository {

    /**
     * Retrieves all folders within a specified parent path.
     * MOCK: Uses File API for basic folder listing.
     */
    override suspend fun getFolders(parentPath: String?): Result<List<FolderInfo>> {
        return try {
            val parent = if (parentPath.isNullOrEmpty()) {
                // Return root folders
                return getRootFolders()
            } else {
                File(parentPath)
            }

            if (!parent.exists() || !parent.isDirectory) {
                return Result.Success(emptyList())
            }

            val folders = parent.listFiles { file -> file.isDirectory }
                ?.mapNotNull { file ->
                    try {
                        createFolderInfo(file)
                    } catch (e: Exception) {
                        null // Skip folders that can't be read
                    }
                }
                ?.sortedBy { it.name.lowercase() }
                ?: emptyList()

            Result.Success(folders)
        } catch (e: Exception) {
            Result.Error(e, "Failed to get folders: ${e.message}")
        }
    }

    /**
     * Retrieves detailed information about a specific folder.
     */
    override suspend fun getFolderInfo(folderPath: String): Result<FolderInfo?> {
        return try {
            val folder = File(folderPath)
            if (!folder.exists() || !folder.isDirectory) {
                return Result.Success(null)
            }

            Result.Success(createFolderInfo(folder))
        } catch (e: Exception) {
            Result.Error(e, "Failed to get folder info: ${e.message}")
        }
    }

    /**
     * Creates a new folder at the specified location.
     * MOCK: Uses File.mkdir() for basic folder creation.
     */
    override suspend fun createFolder(parentPath: String, folderName: String): Result<FolderInfo> {
        return try {
            // Validate folder name first
            when (val validationResult = validateFolderName(folderName)) {
                is Result.Error -> return validationResult
                is Result.Success -> if (!validationResult.data) {
                    return Result.Error(
                        IllegalArgumentException("Invalid folder name: $folderName")
                    )
                }
                else -> {}
            }

            val parent = File(parentPath)
            if (!parent.exists() || !parent.isDirectory) {
                return Result.Error(
                    IllegalArgumentException("Parent folder does not exist: $parentPath")
                )
            }

            val newFolder = File(parent, folderName)
            if (newFolder.exists()) {
                return Result.Error(
                    IllegalStateException("Folder already exists: ${newFolder.absolutePath}")
                )
            }

            val created = newFolder.mkdir()
            if (!created) {
                return Result.Error(
                    IllegalStateException("Failed to create folder: ${newFolder.absolutePath}")
                )
            }

            Result.Success(createFolderInfo(newFolder))
        } catch (e: Exception) {
            Result.Error(e, "Failed to create folder: ${e.message}")
        }
    }

    /**
     * Gets all available root storage locations.
     * MOCK: Returns common Android storage paths.
     */
    override suspend fun getRootFolders(): Result<List<FolderInfo>> {
        return try {
            val rootFolders = mutableListOf<FolderInfo>()

            // Internal storage root
            val internalRoot = Environment.getExternalStorageDirectory()
            if (internalRoot.exists()) {
                rootFolders.add(
                    FolderInfo.createRoot(
                        uri = Uri.fromFile(internalRoot),
                        path = internalRoot.absolutePath,
                        name = "Internal Storage"
                    )
                )
            }

            // Common user-accessible folders
            val commonFolders = listOf(
                Environment.DIRECTORY_DCIM to "Camera",
                Environment.DIRECTORY_PICTURES to "Pictures",
                Environment.DIRECTORY_DOWNLOADS to "Downloads",
                Environment.DIRECTORY_DOCUMENTS to "Documents",
                Environment.DIRECTORY_MOVIES to "Movies"
            )

            commonFolders.forEach { (type, displayName) ->
                val folder = Environment.getExternalStoragePublicDirectory(type)
                if (folder.exists()) {
                    rootFolders.add(
                        createFolderInfo(folder).copy(name = displayName)
                    )
                }
            }

            Result.Success(rootFolders.sortedBy { it.name })
        } catch (e: Exception) {
            Result.Error(e, "Failed to get root folders: ${e.message}")
        }
    }

    /**
     * Observes changes to folders within a specific parent path.
     * MOCK: Returns a simple flow with current state (no real-time updates).
     */
    override fun observeFolders(parentPath: String?): Flow<List<FolderInfo>> = flow {
        // Mock implementation: Just emit current state once
        // TODO: Implement real FileObserver when UI is ready
        when (val result = getFolders(parentPath)) {
            is Result.Success -> emit(result.data)
            is Result.Error -> throw result.exception
            is Result.Loading -> emit(emptyList())
        }
    }

    /**
     * Validates whether a folder name is valid for creation.
     */
    override suspend fun validateFolderName(folderName: String): Result<Boolean> {
        return try {
            // Check for empty name
            if (folderName.isBlank()) {
                return Result.Error(
                    IllegalArgumentException("Folder name cannot be empty")
                )
            }

            // Check length (typical filesystem limit)
            if (folderName.length > 255) {
                return Result.Error(
                    IllegalArgumentException("Folder name too long (max 255 characters)")
                )
            }

            // Check for illegal characters
            val illegalChars = listOf('/', '\\', ':', '*', '?', '"', '<', '>', '|', '\u0000')
            if (folderName.any { it in illegalChars }) {
                return Result.Error(
                    IllegalArgumentException("Folder name contains illegal characters: / \\ : * ? \" < > |")
                )
            }

            // Check for Windows reserved names
            val reservedNames = listOf(
                "CON", "PRN", "AUX", "NUL",
                "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
                "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"
            )
            if (folderName.uppercase() in reservedNames) {
                return Result.Error(
                    IllegalArgumentException("'$folderName' is a reserved name")
                )
            }

            // Check for leading/trailing dots or spaces
            if (folderName.startsWith('.') || folderName.endsWith('.') ||
                folderName.startsWith(' ') || folderName.endsWith(' ')) {
                return Result.Error(
                    IllegalArgumentException("Folder name cannot start or end with dot or space")
                )
            }

            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e, "Failed to validate folder name: ${e.message}")
        }
    }

    /**
     * Checks if a folder exists at the specified path.
     */
    override suspend fun folderExists(folderPath: String): Result<Boolean> {
        return try {
            val folder = File(folderPath)
            Result.Success(folder.exists() && folder.isDirectory)
        } catch (e: Exception) {
            Result.Error(e, "Failed to check folder existence: ${e.message}")
        }
    }

    /**
     * Gets the parent folder path from a given folder path.
     */
    override suspend fun getParentFolder(folderPath: String): Result<String?> {
        return try {
            val folder = File(folderPath)
            val parent = folder.parentFile
            Result.Success(parent?.absolutePath)
        } catch (e: Exception) {
            Result.Error(e, "Failed to get parent folder: ${e.message}")
        }
    }

    // Helper function to create FolderInfo from File
    private fun createFolderInfo(folder: File): FolderInfo {
        val files = folder.listFiles()
        val fileCount = files?.count { it.isFile } ?: 0
        val subfolderCount = files?.count { it.isDirectory } ?: 0

        return FolderInfo(
            uri = Uri.fromFile(folder),
            path = folder.absolutePath,
            name = folder.name,
            fileCount = fileCount,
            subfolderCount = subfolderCount,
            parentPath = folder.parentFile?.absolutePath,
            isRoot = folder.parent == null || folder.parent == "/"
        )
    }
}
