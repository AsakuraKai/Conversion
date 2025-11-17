package com.example.conversion.domain.repository

import com.example.conversion.domain.model.Permission
import com.example.conversion.domain.model.PermissionState
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing app permissions.
 * Provides methods to check permission status and observe permission changes.
 */
interface PermissionsRepository {

    /**
     * Checks the current status of all permissions.
     *
     * @return PermissionState containing the status of all permissions
     */
    suspend fun checkPermissions(): PermissionState

    /**
     * Checks if a specific permission is granted.
     *
     * @param permission The permission to check
     * @return True if the permission is granted, false otherwise
     */
    suspend fun isPermissionGranted(permission: Permission): Boolean

    /**
     * Returns a Flow that emits permission state changes.
     * Useful for observing permission changes in real-time.
     *
     * @return Flow of PermissionState
     */
    fun observePermissions(): Flow<PermissionState>

    /**
     * Gets the list of permissions required for the current Android version.
     *
     * @return List of applicable permissions
     */
    fun getRequiredPermissions(): List<Permission>

    /**
     * Checks if all required media permissions are granted.
     *
     * @return True if all media permissions are granted
     */
    suspend fun hasMediaAccess(): Boolean

    /**
     * Checks if MANAGE_EXTERNAL_STORAGE permission is granted (Android 11+).
     * This is a special permission that requires navigating to Settings.
     *
     * @return True if permission is granted or not applicable
     */
    suspend fun hasManageStoragePermission(): Boolean

    /**
     * Checks if a permission should show rationale (user denied but can still request).
     *
     * @param permission The permission to check
     * @return True if rationale should be shown
     */
    suspend fun shouldShowRationale(permission: Permission): Boolean

    /**
     * Updates the permission state cache.
     * Should be called after permission requests are handled.
     */
    suspend fun refreshPermissions()
}
