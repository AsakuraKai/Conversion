package com.example.conversion.data.source.local

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import com.example.conversion.domain.model.FileFilter
import com.example.conversion.domain.model.FileItem
import javax.inject.Inject

/**
 * Data source for querying media files from MediaStore.
 * Handles all direct interactions with ContentResolver and MediaStore APIs.
 */
class MediaStoreDataSource @Inject constructor(
    private val contentResolver: ContentResolver
) {
    /**
     * Queries media files based on filter criteria.
     *
     * @param filter Criteria for filtering files
     * @return List of FileItems matching the filter
     */
    fun queryMediaFiles(filter: FileFilter): List<FileItem> {
        val files = mutableListOf<FileItem>()

        // Query images if selected
        if (filter.includeImages) {
            files.addAll(queryMediaType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, filter))
        }

        // Query videos if selected
        if (filter.includeVideos) {
            files.addAll(queryMediaType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, filter))
        }

        // Query audio if selected
        if (filter.includeAudio) {
            files.addAll(queryMediaType(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, filter))
        }

        return files
    }

    /**
     * Queries a specific media type from MediaStore.
     */
    private fun queryMediaType(uri: Uri, filter: FileFilter): List<FileItem> {
        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.DATE_MODIFIED
        )

        // Build selection criteria
        val selection = buildSelection(filter)
        val selectionArgs = buildSelectionArgs(filter)
        val sortOrder = filter.sortOrder.toMediaStoreOrder()

        val files = mutableListOf<FileItem>()

        contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
            val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
            val dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn) ?: continue
                val path = cursor.getString(dataColumn) ?: continue
                val size = cursor.getLong(sizeColumn)
                val mimeType = cursor.getString(mimeTypeColumn) ?: continue
                val dateModified = cursor.getLong(dateModifiedColumn)

                // Create content URI
                val contentUri = ContentUris.withAppendedId(uri, id)

                // Get thumbnail URI if available
                val thumbnailUri = getThumbnailUri(uri, id)

                files.add(
                    FileItem(
                        id = id,
                        uri = contentUri,
                        name = name,
                        path = path,
                        size = size,
                        mimeType = mimeType,
                        dateModified = dateModified * 1000, // Convert to milliseconds
                        thumbnailUri = thumbnailUri
                    )
                )
            }
        }

        return files
    }

    /**
     * Builds the WHERE clause for MediaStore query.
     */
    private fun buildSelection(filter: FileFilter): String? {
        val conditions = mutableListOf<String>()

        // Filter by folder path
        filter.folderPath?.let { path ->
            conditions.add("${MediaStore.MediaColumns.DATA} LIKE ?")
        }

        // Filter by size
        filter.minSize?.let {
            conditions.add("${MediaStore.MediaColumns.SIZE} >= ?")
        }
        filter.maxSize?.let {
            conditions.add("${MediaStore.MediaColumns.SIZE} <= ?")
        }

        return if (conditions.isNotEmpty()) {
            conditions.joinToString(" AND ")
        } else {
            null
        }
    }

    /**
     * Builds the selection arguments array for MediaStore query.
     */
    private fun buildSelectionArgs(filter: FileFilter): Array<String>? {
        val args = mutableListOf<String>()

        // Folder path argument
        filter.folderPath?.let { path ->
            args.add("$path%")
        }

        // Size arguments
        filter.minSize?.let {
            args.add(it.toString())
        }
        filter.maxSize?.let {
            args.add(it.toString())
        }

        return if (args.isNotEmpty()) {
            args.toTypedArray()
        } else {
            null
        }
    }

    /**
     * Gets thumbnail URI for media file if available.
     */
    private fun getThumbnailUri(baseUri: Uri, id: Long): Uri? {
        return when {
            baseUri == MediaStore.Images.Media.EXTERNAL_CONTENT_URI -> {
                // For images, use thumbnail API
                try {
                    ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                } catch (e: Exception) {
                    null
                }
            }
            baseUri == MediaStore.Video.Media.EXTERNAL_CONTENT_URI -> {
                // For videos, use thumbnail API
                try {
                    ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                } catch (e: Exception) {
                    null
                }
            }
            else -> null
        }
    }

    /**
     * Queries files by specific URI.
     */
    fun queryFileByUri(uriString: String): FileItem? {
        val uri = Uri.parse(uriString)
        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.DATE_MODIFIED
        )

        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
                val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
                val dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED)

                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn) ?: return null
                val path = cursor.getString(dataColumn) ?: return null
                val size = cursor.getLong(sizeColumn)
                val mimeType = cursor.getString(mimeTypeColumn) ?: return null
                val dateModified = cursor.getLong(dateModifiedColumn)

                return FileItem(
                    id = id,
                    uri = uri,
                    name = name,
                    path = path,
                    size = size,
                    mimeType = mimeType,
                    dateModified = dateModified * 1000,
                    thumbnailUri = null
                )
            }
        }

        return null
    }

    /**
     * Queries unique folder paths containing media files.
     */
    fun queryMediaFolders(filter: FileFilter): List<String> {
        val folders = mutableSetOf<String>()

        if (filter.includeImages) {
            folders.addAll(queryFoldersForType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
        }
        if (filter.includeVideos) {
            folders.addAll(queryFoldersForType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI))
        }
        if (filter.includeAudio) {
            folders.addAll(queryFoldersForType(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI))
        }

        return folders.sorted()
    }

    /**
     * Queries folder paths for a specific media type.
     */
    private fun queryFoldersForType(uri: Uri): Set<String> {
        val folders = mutableSetOf<String>()
        val projection = arrayOf(MediaStore.MediaColumns.DATA)

        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

            while (cursor.moveToNext()) {
                val path = cursor.getString(dataColumn)
                path?.let {
                    // Extract folder path (everything before the last '/')
                    val folderPath = it.substringBeforeLast('/', "")
                    if (folderPath.isNotEmpty()) {
                        folders.add(folderPath)
                    }
                }
            }
        }

        return folders
    }
}
