package com.example.conversion.presentation.permissions

import com.example.conversion.domain.model.Permission
import com.example.conversion.domain.model.PermissionState

/**
 * Permissions feature contract defining State, Events, and Actions.
 * Follows MVI pattern for unidirectional data flow.
 */
object PermissionsContract {

    /**
     * UI State for permissions screen/handling.
     */
    data class State(
        val permissionState: PermissionState = PermissionState(),
        val isLoading: Boolean = false,
        val isCheckingPermissions: Boolean = false,
        val error: String? = null,
        val showRationaleFor: List<Permission> = emptyList(),
        val shouldNavigateToSettings: Boolean = false
    ) {
        /**
         * Whether all required permissions are granted.
         */
        val hasAllPermissions: Boolean
            get() = permissionState.allGranted

        /**
         * Whether media access (images, videos, audio) is granted.
         */
        val hasMediaAccess: Boolean
            get() = permissionState.hasMediaAccess

        /**
         * List of permissions that need to be requested.
         */
        val permissionsToRequest: List<Permission>
            get() = permissionState.deniedPermissions.filter { it.isApplicable() }

        /**
         * Whether any permissions are permanently denied.
         */
        val hasPermanentlyDeniedPermissions: Boolean
            get() = permissionState.permanentlyDeniedPermissions.isNotEmpty()
    }

    /**
     * One-time events sent from ViewModel to UI.
     */
    sealed class Event {
        /**
         * Show a message to the user.
         */
        data class ShowMessage(val message: String) : Event()

        /**
         * Show rationale dialog for specific permissions.
         */
        data class ShowRationale(val permissions: List<Permission>) : Event()

        /**
         * Navigate to app settings to enable permissions.
         */
        data object NavigateToSettings : Event()

        /**
         * Request specific permissions.
         */
        data class RequestPermissions(val permissions: List<Permission>) : Event()

        /**
         * Permissions were successfully granted.
         */
        data object PermissionsGranted : Event()

        /**
         * Some permissions were denied.
         */
        data class PermissionsDenied(val deniedPermissions: List<Permission>) : Event()
    }

    /**
     * User actions that trigger state changes.
     */
    sealed class Action {
        /**
         * Check current permission status.
         */
        data object CheckPermissions : Action()

        /**
         * Request all required permissions.
         */
        data object RequestPermissions : Action()

        /**
         * Request specific permissions.
         */
        data class RequestSpecificPermissions(val permissions: List<Permission>) : Action()

        /**
         * Handle permission request result.
         */
        data class OnPermissionResult(
            val permission: Permission,
            val isGranted: Boolean,
            val shouldShowRationale: Boolean
        ) : Action()

        /**
         * User dismissed the rationale dialog.
         */
        data object DismissRationale : Action()

        /**
         * User wants to open app settings.
         */
        data object OpenSettings : Action()

        /**
         * Refresh permissions after returning from settings.
         */
        data object RefreshAfterSettings : Action()

        /**
         * Clear error message.
         */
        data object ClearError : Action()
    }
}
