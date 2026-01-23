package com.example.conversion.data.repository

import android.content.ContentResolver
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import com.example.conversion.data.source.local.MediaStoreDataSource
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.FileFilter
import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.repository.MediaRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Implementation of MediaRepository using MediaStore and ContentResolver.
 * Handles scoped storage properly for Android 10+.
 */
class MediaRepositoryImpl @Inject constructor(
    private val mediaStoreDataSource: MediaStoreDataSource,
    private val contentResolver: ContentResolver,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MediaRepository {

    override suspend fun getMediaFiles(filter: FileFilter): Result<List<FileItem>> =
        withContext(ioDispatcher) {
            try {
                val files = mediaStoreDataSource.queryMediaFiles(filter)
                Result.Success(files)
            } catch (e: SecurityException) {
                // Permission denied
                Result.Error(
                    exception = e,
                    message = "Permission denied. Please grant media access permissions."
                )
            } catch (e: Exception) {
                Result.Error(
                    exception = e,
                    message = "Failed to retrieve media files: ${e.message}"
                )
            }
        }

    override suspend fun getFilesByFolder(folderPath: String): Result<List<FileItem>> =
        withContext(ioDispatcher) {
            try {
                val filter = FileFilter.DEFAULT.copy(folderPath = folderPath)
                val files = mediaStoreDataSource.queryMediaFiles(filter)
                Result.Success(files)
            } catch (e: SecurityException) {
                Result.Error(
                    exception = e,
                    message = "Permission denied. Please grant media access permissions."
                )
            } catch (e: Exception) {
                Result.Error(
                    exception = e,
                    message = "Failed to retrieve files from folder: ${e.message}"
                )
            }
        }

    override fun observeMediaFiles(filter: FileFilter): Flow<List<FileItem>> = callbackFlow {
        // Create content observer to watch for changes
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                // Query files and emit to flow
                try {
                    val files = mediaStoreDataSource.queryMediaFiles(filter)
                    trySend(files)
                } catch (e: Exception) {
                    // Emit empty list on error
                    trySend(emptyList())
                }
            }
        }

        // Register observers for all media types
        if (filter.includeImages) {
            contentResolver.registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true,
                observer
            )
        }
        if (filter.includeVideos) {
            contentResolver.registerContentObserver(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                true,
                observer
            )
        }
        if (filter.includeAudio) {
            contentResolver.registerContentObserver(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                true,
                observer
            )
        }

        // Emit initial data
        try {
            val initialFiles = mediaStoreDataSource.queryMediaFiles(filter)
            send(initialFiles)
        } catch (e: Exception) {
            send(emptyList())
        }

        // Wait for flow to be cancelled, then cleanup
        awaitClose {
            contentResolver.unregisterContentObserver(observer)
        }
    }

    override suspend fun getFileByUri(uriString: String): Result<FileItem?> =
        withContext(ioDispatcher) {
            try {
                val file = mediaStoreDataSource.queryFileByUri(uriString)
                Result.Success(file)
            } catch (e: SecurityException) {
                Result.Error(
                    exception = e,
                    message = "Permission denied. Cannot access file."
                )
            } catch (e: Exception) {
                Result.Error(
                    exception = e,
                    message = "Failed to retrieve file: ${e.message}"
                )
            }
        }

    override suspend fun getMediaFolders(filter: FileFilter): Result<List<String>> =
        withContext(ioDispatcher) {
            try {
                val folders = mediaStoreDataSource.queryMediaFolders(filter)
                Result.Success(folders)
            } catch (e: SecurityException) {
                Result.Error(
                    exception = e,
                    message = "Permission denied. Please grant media access permissions."
                )
            } catch (e: Exception) {
                Result.Error(
                    exception = e,
                    message = "Failed to retrieve media folders: ${e.message}"
                )
            }
        }
}
