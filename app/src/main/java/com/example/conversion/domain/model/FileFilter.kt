package com.example.conversion.domain.model

/**
 * Configuration for filtering media files from device storage.
 * Used to specify which types of files to retrieve and display.
 *
 * @property includeImages Whether to include image files (MIME type: image/&#42;)
 * @property includeVideos Whether to include video files (MIME type: video/&#42;)
 * @property includeAudio Whether to include audio files (MIME type: audio/&#42;)
 * @property minSize Minimum file size in bytes (null = no minimum)
 * @property maxSize Maximum file size in bytes (null = no maximum)
 * @property folderPath Specific folder path to filter by (null = all folders)
 * @property sortOrder How to sort the results
 */
data class FileFilter(
    val includeImages: Boolean = true,
    val includeVideos: Boolean = true,
    val includeAudio: Boolean = false,
    val minSize: Long? = null,
    val maxSize: Long? = null,
    val folderPath: String? = null,
    val sortOrder: SortOrder = SortOrder.DATE_MODIFIED_DESC
) {
    /**
     * Returns true if at least one media type is selected.
     */
    val hasMediaTypeSelected: Boolean
        get() = includeImages || includeVideos || includeAudio

    /**
     * Returns the MIME type selections as a list.
     */
    val selectedMimeTypes: List<String>
        get() = buildList {
            if (includeImages) add("image/*")
            if (includeVideos) add("video/*")
            if (includeAudio) add("audio/*")
        }

    companion object {
        /**
         * Default filter that includes images and videos.
         */
        val DEFAULT = FileFilter(
            includeImages = true,
            includeVideos = true,
            includeAudio = false
        )

        /**
         * Filter for images only.
         */
        val IMAGES_ONLY = FileFilter(
            includeImages = true,
            includeVideos = false,
            includeAudio = false
        )

        /**
         * Filter for videos only.
         */
        val VIDEOS_ONLY = FileFilter(
            includeImages = false,
            includeVideos = true,
            includeAudio = false
        )

        /**
         * Filter for all media types.
         */
        val ALL_MEDIA = FileFilter(
            includeImages = true,
            includeVideos = true,
            includeAudio = true
        )
    }
}

/**
 * Defines how files should be sorted.
 */
enum class SortOrder {
    /** Sort by name A-Z */
    NAME_ASC,
    
    /** Sort by name Z-A */
    NAME_DESC,
    
    /** Sort by date modified (oldest first) */
    DATE_MODIFIED_ASC,
    
    /** Sort by date modified (newest first) */
    DATE_MODIFIED_DESC,
    
    /** Sort by size (smallest first) */
    SIZE_ASC,
    
    /** Sort by size (largest first) */
    SIZE_DESC;

    /**
     * Returns the MediaStore column and order for this sort option.
     */
    fun toMediaStoreOrder(): String = when (this) {
        NAME_ASC -> "${android.provider.MediaStore.MediaColumns.DISPLAY_NAME} ASC"
        NAME_DESC -> "${android.provider.MediaStore.MediaColumns.DISPLAY_NAME} DESC"
        DATE_MODIFIED_ASC -> "${android.provider.MediaStore.MediaColumns.DATE_MODIFIED} ASC"
        DATE_MODIFIED_DESC -> "${android.provider.MediaStore.MediaColumns.DATE_MODIFIED} DESC"
        SIZE_ASC -> "${android.provider.MediaStore.MediaColumns.SIZE} ASC"
        SIZE_DESC -> "${android.provider.MediaStore.MediaColumns.SIZE} DESC"
    }
}

