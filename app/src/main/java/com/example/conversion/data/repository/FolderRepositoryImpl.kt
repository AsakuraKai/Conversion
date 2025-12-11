package com.example.conversion.data.repository

import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.FolderInfo
import com.example.conversion.domain.repository.FolderRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

/**
 * Production implementation of FolderRepository using Storage Access Framework (SAF).
 * 
 * Features:
 * - Uses DocumentFile for Android 10+ scoped storage compliance
 * - Persists directory URIs with takePersistableUriPermission
 * - ContentObserver for real-time folder monitoring
 * - Full SAF integration with proper permission handling
 * 
 * Phase 2 Complete: Migrated from java.io.File to DocumentFile + SAF
 * 
 * @param context Application context for SAF operations
 */
class FolderRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : FolderRepository {

    /**
     * Retrieves all folders within a specified parent path using SAF.
     * Uses DocumentFile for scoped storage compliance.
     */
    override suspend fun getFolders(parentPath: String?): Result<List<FolderInfo>> {
        return try {
            if (parentPath.isNullOrEmpty()) {
                return getRootFolders()
            }

            val uri = Uri.parse(parentPath)
            val parentDoc = DocumentFile.fromTreeUri(context, uri)
                ?: return Result.Success(emptyList())

            if (!parentDoc.exists() || !parentDoc.isDirectory) {
                return Result.Success(emptyList())
            }

            val folders = parentDoc.listFiles()
                .filter { it.isDirectory }
                .mapNotNull { doc ->
                    try {
                        createFolderInfoFromDocumentFile(doc)
                    } catch (e: Exception) {
                        null // Skip folders that can't be read
                    }
                }
                .sortedBy { it.name.lowercase() }

            Result.Success(folders)
        } catch (e: SecurityException) {
            Result.Error(e, "Permission denied: ${e.message}")
        } catch (e: Exception) {
            Result.Error(e, "Failed to get folders: ${e.message}")
        }
    }

    /**
     * Retrieves detailed information about a specific folder using SAF.
     */
    override suspend fun getFolderInfo(folderPath: String): Result<FolderInfo?> {
        return try {
            val uri = Uri.parse(folderPath)
            val folder = DocumentFile.fromTreeUri(context, uri)
                ?: return Result.Success(null)

            if (!folder.exists() || !folder.isDirectory) {
                return Result.Success(null)
            }

            Result.Success(createFolderInfoFromDocumentFile(folder))
        } catch (e: Exception) {
            Result.Error(e, "Failed to get folder info: ${e.message}")
        }
    }

    /**
     * Creates a new folder at the specified location using SAF.
     * Uses DocumentsContract.createDocument for scoped storage compliance.
     */
    override suspend fun createFolder(parentPath: String, folderName: String): Result<FolderInfo> {
        return try {
            // Validate folder name first
            when (val validationResult = validateFolderName(folderName)) {
                is Result.Error -> return validationResult
                is Result.Success -> {
                    if (!validationResult.data) {
                        return Result.Error(
                            IllegalArgumentException("Invalid folder name: $folderName")
                        )
                    }
                }
                is Result.Loading -> { /* Should never happen */ }
            }

            val parentUri = Uri.parse(parentPath)
            val parent = DocumentFile.fromTreeUri(context, parentUri)
                ?: return Result.Error(
                    IllegalArgumentException("Parent folder does not exist: $parentPath")
                )

            if (!parent.exists() || !parent.isDirectory) {
                return Result.Error(
                    IllegalArgumentException("Parent folder does not exist or is not a directory")
                )
            }

            // Check if folder already exists
            val existing = parent.findFile(folderName)
            if (existing != null && existing.exists()) {
                return Result.Error(
                    IllegalStateException("Folder already exists: $folderName")
                )
            }

            val newFolder = parent.createDirectory(folderName)
                ?: return Result.Error(
                    IllegalStateException("Failed to create folder: $folderName")
                )

            Result.Success(createFolderInfoFromDocumentFile(newFolder))
        } catch (e: SecurityException) {
            Result.Error(e, "Permission denied: ${e.message}")
        } catch (e: Exception) {
            Result.Error(e, "Failed to create folder: ${e.message}")
        }
    }

    /**
     * Gets all available root storage locations using SAF.
     * Returns persistable URIs for common storage locations.
     */
    override suspend fun getRootFolders(): Result<List<FolderInfo>> {
        return try {
            val rootFolders = mutableListOf<FolderInfo>()

            // Get persisted URI permissions
            val persistedUris = context.contentResolver.persistedUriPermissions
                .filter { it.isReadPermission }
                .mapNotNull { permission ->
                    try {
                        val doc = DocumentFile.fromTreeUri(context, permission.uri)
                        if (doc?.exists() == true && doc.isDirectory) {
                            createFolderInfoFromDocumentFile(doc)
                        } else null
                    } catch (e: Exception) {
                        null
                    }
                }

            rootFolders.addAll(persistedUris)

            // Add common Android storage folders if accessible
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Android 11+ common folders via MediaStore
                val commonFolders = listOf(
                    Environment.DIRECTORY_DCIM to "Camera",
                    Environment.DIRECTORY_PICTURES to "Pictures",
                    Environment.DIRECTORY_DOWNLOADS to "Downloads",
                    Environment.DIRECTORY_DOCUMENTS to "Documents",
                    Environment.DIRECTORY_MOVIES to "Movies"
                )

                commonFolders.forEach { (type, displayName) ->
                    val folder = Environment.getExternalStoragePublicDirectory(type)
                    // Note: These are provided as hints, actual access requires SAF
                    if (folder.exists()) {
                        rootFolders.add(
                            FolderInfo.createRoot(
                                uri = Uri.fromFile(folder),
                                path = folder.absolutePath,
                                name = "$displayName (requires permission)"
                            )
                        )
                    }
                }
            }

            Result.Success(rootFolders.sortedBy { it.name })
        } catch (e: Exception) {
            Result.Error(e, "Failed to get root folders: ${e.message}")
        }
    }

    /**
     * Observes changes to folders within a specific parent path using ContentObserver.
     * Provides real-time updates when folder contents change.
     * 
     * Phase 2: Migrated from simple flow to ContentObserver for real-time monitoring.
     */
    override fun observeFolders(parentPath: String?): Flow<List<FolderInfo>> = callbackFlow {
        if (parentPath.isNullOrEmpty()) {
            when (val result = getRootFolders()) {
                is Result.Success -> send(result.data)
                is Result.Error -> close(result.exception)
                is Result.Loading -> send(emptyList())
            }
            close()
            return@callbackFlow
        }

        val uri = Uri.parse(parentPath)
        
        // Create ContentObserver for real-time updates
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                // Emit updated folder list when changes detected
                val result = runCatching {
                    val parentDoc = DocumentFile.fromTreeUri(context, uri)
                    parentDoc?.listFiles()
                        ?.filter { it.isDirectory }
                        ?.mapNotNull { doc ->
                            try {
                                createFolderInfoFromDocumentFile(doc)
                            } catch (e: Exception) {
                                null
                            }
                        }
                        ?.sortedBy { it.name.lowercase() }
                        ?: emptyList()
                }
                
                result.onSuccess { folders ->
                    trySend(folders)
                }.onFailure { error ->
                    close(error)
                }
            }
        }

        // Register observer
        context.contentResolver.registerContentObserver(
            uri,
            true,  // notifyForDescendants
            observer
        )

        // Emit initial state
        when (val result = getFolders(parentPath)) {
            is Result.Success -> send(result.data)
            is Result.Error -> close(result.exception)
            is Result.Loading -> send(emptyList())
        }

        // Cleanup when flow is cancelled
        awaitClose {
            context.contentResolver.unregisterContentObserver(observer)
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
     * Checks if a folder exists at the specified path using SAF.
     */
    override suspend fun folderExists(folderPath: String): Result<Boolean> {
        return try {
            val uri = Uri.parse(folderPath)
            val folder = DocumentFile.fromTreeUri(context, uri)
            Result.Success(folder?.exists() == true && folder.isDirectory)
        } catch (e: Exception) {
            Result.Error(e, "Failed to check folder existence: ${e.message}")
        }
    }

    /**
     * Gets the parent folder path from a given folder path using SAF.
     */
    override suspend fun getParentFolder(folderPath: String): Result<String?> {
        return try {
            val uri = Uri.parse(folderPath)
            val folder = DocumentFile.fromTreeUri(context, uri)
            val parent = folder?.parentFile
            Result.Success(parent?.uri?.toString())
        } catch (e: Exception) {
            Result.Error(e, "Failed to get parent folder: ${e.message}")
        }
    }

    /**
     * Takes persistable URI permission for long-term access.
     * Call this after user grants access via SAF picker.
     */
    suspend fun takePersistablePermission(uri: Uri): Result<Unit> {
        return try {
            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            context.contentResolver.takePersistableUriPermission(uri, flags)
            Result.Success(Unit)
        } catch (e: SecurityException) {
            Result.Error(e, "Failed to take persistable permission: ${e.message}")
        } catch (e: Exception) {
            Result.Error(e, "Failed to take persistable permission: ${e.message}")
        }
    }

    /**
     * Releases persistable URI permission.
     */
    suspend fun releasePersistablePermission(uri: Uri): Result<Unit> {
        return try {
            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            context.contentResolver.releasePersistableUriPermission(uri, flags)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "Failed to release persistable permission: ${e.message}")
        }
    }

    // Helper function to create FolderInfo from DocumentFile
    private fun createFolderInfoFromDocumentFile(folder: DocumentFile): FolderInfo {
        val files = folder.listFiles()
        val fileCount = files.count { it.isFile }
        val subfolderCount = files.count { it.isDirectory }

        return FolderInfo(
            uri = folder.uri,
            path = folder.uri.toString(),
            name = folder.name ?: "Unknown",
            fileCount = fileCount,
            subfolderCount = subfolderCount,
            parentPath = folder.parentFile?.uri?.toString(),
            isRoot = folder.parentFile == null
        )
    }
}
