package com.example.conversion.data.repository

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.conversion.domain.model.Permission
import com.example.conversion.domain.model.PermissionState
import com.example.conversion.domain.model.PermissionStatus
import com.example.conversion.domain.repository.PermissionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Implementation of PermissionsRepository for managing app permissions.
 * Handles runtime permission checking for different Android API levels.
 *
 * @param context Application context for permission checking
 */
class PermissionsManagerImpl @Inject constructor(
    private val context: Context
) : PermissionsRepository {

    private val _permissionsFlow = MutableStateFlow(PermissionState())

    override suspend fun checkPermissions(): PermissionState {
        val statusMap = mutableMapOf<Permission, PermissionStatus>()

        // Check each permission type
        Permission.getAllRequiredPermissions().forEach { permission ->
            statusMap[permission] = checkPermissionStatus(permission)
        }

        // Update the flow with new state
        val newState = PermissionState(permissionStatuses = statusMap)
        _permissionsFlow.value = newState

        return newState
    }

    override suspend fun isPermissionGranted(permission: Permission): Boolean {
        return if (!permission.isApplicable()) {
            true // Permission not needed for this Android version
        } else {
            // Special case for MANAGE_EXTERNAL_STORAGE
            if (permission == Permission.MANAGE_EXTERNAL_STORAGE && 
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                android.os.Environment.isExternalStorageManager()
            } else {
                // Check all manifest permissions for this Permission enum
                permission.manifestPermissions.all { manifestPermission ->
                    ContextCompat.checkSelfPermission(
                        context,
                        manifestPermission
                    ) == PackageManager.PERMISSION_GRANTED
                }
            }
        }
    }

    override fun observePermissions(): Flow<PermissionState> {
        return _permissionsFlow.asStateFlow()
    }

    override fun getRequiredPermissions(): List<Permission> {
        return Permission.getAllRequiredPermissions()
            .filter { it.isApplicable() }
    }

    override suspend fun hasMediaAccess(): Boolean {
        return Permission.getMediaPermissions()
            .filter { it.isApplicable() }
            .all { permission -> isPermissionGranted(permission) }
    }

    override suspend fun hasManageStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            android.os.Environment.isExternalStorageManager()
        } else {
            // Not required for Android 10 and below
            true
        }
    }

    override suspend fun shouldShowRationale(permission: Permission): Boolean {
        // Note: This method has limitations as it requires Activity context
        // For now, we return false as the actual rationale logic should be
        // handled in the Presentation layer using Accompanist Permissions
        // which has access to the Activity and permission state
        
        // Alternative: Store activity reference or use a different approach
        // For the MVP, the PermissionHandler composable handles rationale logic
        return false
    }

    override suspend fun refreshPermissions() {
        checkPermissions()
    }

    /**
     * Helper method to check the status of a single permission.
     */
    private suspend fun checkPermissionStatus(permission: Permission): PermissionStatus {
        // If permission is not applicable for this Android version
        if (!permission.isApplicable()) {
            return PermissionStatus.NotApplicable
        }

        // Special handling for MANAGE_EXTERNAL_STORAGE
        if (permission == Permission.MANAGE_EXTERNAL_STORAGE) {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (android.os.Environment.isExternalStorageManager()) {
                    PermissionStatus.Granted
                } else {
                    // Cannot determine if permanently denied for this special permission
                    // User must manually go to Settings
                    PermissionStatus.Denied
                }
            } else {
                PermissionStatus.NotApplicable
            }
        }

        // Check if all manifest permissions are granted
        val allGranted = permission.manifestPermissions.all { manifestPermission ->
            ContextCompat.checkSelfPermission(
                context,
                manifestPermission
            ) == PackageManager.PERMISSION_GRANTED
        }

        return if (allGranted) {
            PermissionStatus.Granted
        } else {
            // Note: We cannot determine if permanently denied without Activity context
            // The PermissionHandler composable in the Presentation layer will handle
            // the distinction between Denied and PermanentlyDenied using Accompanist
            PermissionStatus.Denied
        }
    }
}
