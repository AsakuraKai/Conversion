package com.example.conversion.domain.model

import android.Manifest
import android.os.Build

/**
 * Represents the different permissions required by the app.
 * Each permission maps to specific Android manifest permissions based on API level.
 */
enum class Permission(val manifestPermissions: List<String>) {
    /**
     * Permission to read image files from external storage.
     * - Android 13+: READ_MEDIA_IMAGES
     * - Android 10-12: READ_EXTERNAL_STORAGE
     */
    READ_IMAGES(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    ),

    /**
     * Permission to read video files from external storage.
     * - Android 13+: READ_MEDIA_VIDEO
     * - Android 10-12: READ_EXTERNAL_STORAGE
     */
    READ_VIDEOS(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    ),

    /**
     * Permission to read audio files from external storage.
     * - Android 13+: READ_MEDIA_AUDIO
     * - Android 10-12: READ_EXTERNAL_STORAGE
     */
    READ_AUDIO(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    ),

    /**
     * Permission to write files to external storage.
     * - Android 10+: Managed through scoped storage (SAF/MediaStore)
     * - Android 9 and below: WRITE_EXTERNAL_STORAGE
     */
    WRITE_STORAGE(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            emptyList() // Scoped storage, no explicit permission needed
        } else {
            listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    ),

    /**
     * Permission to manage all files on external storage.
     * Required for file operations outside MediaStore on Android 11+.
     * - Android 11+: MANAGE_EXTERNAL_STORAGE (requires Settings intent)
     * - Android 10 and below: Not applicable
     */
    MANAGE_EXTERNAL_STORAGE(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            listOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        } else {
            emptyList()
        }
    ),

    /**
     * Permission to show notifications (Android 13+).
     */
    POST_NOTIFICATIONS(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            emptyList()
        }
    );

    /**
     * Checks if this permission is applicable for the current Android version.
     */
    fun isApplicable(): Boolean = manifestPermissions.isNotEmpty()

    companion object {
        /**
         * Returns all permissions required for media file operations.
         */
        fun getMediaPermissions(): List<Permission> = listOf(
            READ_IMAGES,
            READ_VIDEOS,
            READ_AUDIO,
            WRITE_STORAGE
        )

        /**
         * Returns all permissions required for the app.
         */
        fun getAllRequiredPermissions(): List<Permission> = listOf(
            READ_IMAGES,
            READ_VIDEOS,
            READ_AUDIO,
            WRITE_STORAGE,
            POST_NOTIFICATIONS
        )
    }
}

/**
 * Represents the status of a permission.
 */
sealed class PermissionStatus {
    /**
     * Permission has been granted.
     */
    data object Granted : PermissionStatus()

    /**
     * Permission has been denied.
     */
    data object Denied : PermissionStatus()

    /**
     * Permission has been denied and "Don't ask again" was selected.
     */
    data object PermanentlyDenied : PermissionStatus()

    /**
     * Permission is not applicable for the current Android version.
     */
    data object NotApplicable : PermissionStatus()

    /**
     * Permission status is unknown or not yet checked.
     */
    data object Unknown : PermissionStatus()
}

/**
 * Represents the state of permissions for the app.
 *
 * @property permissionStatuses Map of permissions to their current status
 * @property allGranted True if all required permissions are granted
 * @property hasMediaAccess True if all media permissions are granted
 */
data class PermissionState(
    val permissionStatuses: Map<Permission, PermissionStatus> = emptyMap(),
) {
    /**
     * Checks if all required permissions are granted.
     */
    val allGranted: Boolean
        get() = Permission.getAllRequiredPermissions()
            .filter { it.isApplicable() }
            .all { permission ->
                permissionStatuses[permission] == PermissionStatus.Granted
            }

    /**
     * Checks if all media permissions (images, videos, audio) are granted.
     */
    val hasMediaAccess: Boolean
        get() = Permission.getMediaPermissions()
            .filter { it.isApplicable() }
            .all { permission ->
                permissionStatuses[permission] == PermissionStatus.Granted
            }

    /**
     * Returns list of permissions that are denied.
     */
    val deniedPermissions: List<Permission>
        get() = permissionStatuses.filterValues { status ->
            status is PermissionStatus.Denied || status is PermissionStatus.PermanentlyDenied
        }.keys.toList()

    /**
     * Returns list of permissions that are permanently denied.
     */
    val permanentlyDeniedPermissions: List<Permission>
        get() = permissionStatuses.filterValues { status ->
            status is PermissionStatus.PermanentlyDenied
        }.keys.toList()

    /**
     * Checks if a specific permission is granted.
     */
    fun isGranted(permission: Permission): Boolean =
        permissionStatuses[permission] == PermissionStatus.Granted

    /**
     * Checks if any permissions need to be requested.
     */
    val needsPermissionRequest: Boolean
        get() = Permission.getAllRequiredPermissions()
            .filter { it.isApplicable() }
            .any { permission ->
                permissionStatuses[permission] != PermissionStatus.Granted &&
                        permissionStatuses[permission] != PermissionStatus.NotApplicable
            }
}
