package com.example.conversion.presentation.permissions

import androidx.lifecycle.viewModelScope
import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.Permission
import com.example.conversion.domain.model.PermissionStatus
import com.example.conversion.domain.usecase.permissions.CheckPermissionsUseCase
import com.example.conversion.domain.usecase.permissions.GetRequiredPermissionsUseCase
import com.example.conversion.domain.usecase.permissions.HasMediaAccessUseCase
import com.example.conversion.domain.usecase.permissions.ObservePermissionsUseCase
import com.example.conversion.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing permissions state and handling permission requests.
 * Follows MVI pattern with State, Events, and Actions.
 */
@HiltViewModel
class PermissionsViewModel @Inject constructor(
    private val checkPermissionsUseCase: CheckPermissionsUseCase,
    private val getRequiredPermissionsUseCase: GetRequiredPermissionsUseCase,
    private val hasMediaAccessUseCase: HasMediaAccessUseCase,
    private val observePermissionsUseCase: ObservePermissionsUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<PermissionsContract.State, PermissionsContract.Event>(
    initialState = PermissionsContract.State()
) {

    init {
        // Check permissions on initialization
        handleAction(PermissionsContract.Action.CheckPermissions)

        // Observe permission changes
        observePermissions()
    }

    fun handleAction(action: PermissionsContract.Action) {
        when (action) {
            is PermissionsContract.Action.CheckPermissions -> checkPermissions()
            is PermissionsContract.Action.RequestPermissions -> requestAllPermissions()
            is PermissionsContract.Action.RequestSpecificPermissions -> {
                requestSpecificPermissions(action.permissions)
            }
            is PermissionsContract.Action.OnPermissionResult -> {
                handlePermissionResult(
                    action.permission,
                    action.isGranted,
                    action.shouldShowRationale
                )
            }
            is PermissionsContract.Action.DismissRationale -> dismissRationale()
            is PermissionsContract.Action.OpenSettings -> openSettings()
            is PermissionsContract.Action.RefreshAfterSettings -> refreshAfterSettings()
            is PermissionsContract.Action.ClearError -> clearError()
        }
    }

    /**
     * Checks the current status of all permissions.
     */
    private fun checkPermissions() {
        updateState { copy(isCheckingPermissions = true, error = null) }

        viewModelScope.launch(ioDispatcher) {
            when (val result = checkPermissionsUseCase()) {
                is Result.Success -> {
                    updateState {
                        copy(
                            permissionState = result.data,
                            isCheckingPermissions = false
                        )
                    }

                    // If all permissions granted, send success event
                    if (result.data.allGranted) {
                        sendEvent(PermissionsContract.Event.PermissionsGranted)
                    }
                }
                is Result.Error -> {
                    updateState {
                        copy(
                            isCheckingPermissions = false,
                            error = result.message
                        )
                    }
                    sendEvent(PermissionsContract.Event.ShowMessage(
                        result.message ?: "Failed to check permissions"
                    ))
                }
                is Result.Loading -> {
                    updateState { copy(isCheckingPermissions = true) }
                }
            }
        }
    }

    /**
     * Requests all required permissions.
     */
    private fun requestAllPermissions() {
        viewModelScope.launch(ioDispatcher) {
            when (val result = getRequiredPermissionsUseCase()) {
                is Result.Success -> {
                    val permissionsToRequest = result.data.filter { permission ->
                        !currentState.permissionState.isGranted(permission)
                    }

                    if (permissionsToRequest.isNotEmpty()) {
                        sendEvent(PermissionsContract.Event.RequestPermissions(permissionsToRequest))
                    } else {
                        sendEvent(PermissionsContract.Event.PermissionsGranted)
                    }
                }
                is Result.Error -> {
                    sendEvent(PermissionsContract.Event.ShowMessage(
                        result.message ?: "Failed to get required permissions"
                    ))
                }
                is Result.Loading -> {}
            }
        }
    }

    /**
     * Requests specific permissions.
     */
    private fun requestSpecificPermissions(permissions: List<Permission>) {
        val permissionsToRequest = permissions.filter { permission ->
            !currentState.permissionState.isGranted(permission) && permission.isApplicable()
        }

        if (permissionsToRequest.isNotEmpty()) {
            sendEvent(PermissionsContract.Event.RequestPermissions(permissionsToRequest))
        } else {
            sendEvent(PermissionsContract.Event.PermissionsGranted)
        }
    }

    /**
     * Handles the result of a permission request.
     */
    private fun handlePermissionResult(
        permission: Permission,
        isGranted: Boolean,
        shouldShowRationale: Boolean
    ) {
        if (isGranted) {
            // Update permission status to granted
            val updatedStatuses = currentState.permissionState.permissionStatuses.toMutableMap()
            updatedStatuses[permission] = PermissionStatus.Granted
            updateState {
                copy(
                    permissionState = currentState.permissionState.copy(
                        permissionStatuses = updatedStatuses
                    )
                )
            }

            // Check if all permissions are now granted
            if (currentState.hasAllPermissions) {
                sendEvent(PermissionsContract.Event.PermissionsGranted)
            }
        } else {
            // Update permission status
            val updatedStatuses = currentState.permissionState.permissionStatuses.toMutableMap()
            updatedStatuses[permission] = if (shouldShowRationale) {
                PermissionStatus.Denied
            } else {
                PermissionStatus.PermanentlyDenied
            }
            updateState {
                copy(
                    permissionState = currentState.permissionState.copy(
                        permissionStatuses = updatedStatuses
                    )
                )
            }

            // Show appropriate message
            if (!shouldShowRationale) {
                // Permission permanently denied, suggest going to settings
                updateState { copy(shouldNavigateToSettings = true) }
                sendEvent(PermissionsContract.Event.ShowMessage(
                    "Permission permanently denied. Please enable it in Settings."
                ))
            } else {
                // Show rationale
                sendEvent(PermissionsContract.Event.ShowRationale(listOf(permission)))
            }
        }

        // Refresh permissions to get updated state
        checkPermissions()
    }

    /**
     * Dismisses the rationale dialog.
     */
    private fun dismissRationale() {
        updateState { copy(showRationaleFor = emptyList()) }
    }

    /**
     * Opens app settings.
     */
    private fun openSettings() {
        updateState { copy(shouldNavigateToSettings = false) }
        sendEvent(PermissionsContract.Event.NavigateToSettings)
    }

    /**
     * Refreshes permissions after returning from settings.
     */
    private fun refreshAfterSettings() {
        checkPermissions()
    }

    /**
     * Clears the error message.
     */
    private fun clearError() {
        updateState { copy(error = null) }
    }

    /**
     * Observes permission state changes.
     */
    private fun observePermissions() {
        observePermissionsUseCase()
            .onEach { permissionState ->
                updateState { copy(permissionState = permissionState) }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Public helper to check if media access is available.
     */
    fun hasMediaAccess(): Boolean {
        return currentState.hasMediaAccess
    }

    /**
     * Public helper to get permissions that need to be requested.
     */
    fun getPermissionsToRequest(): List<Permission> {
        return currentState.permissionsToRequest
    }
}
