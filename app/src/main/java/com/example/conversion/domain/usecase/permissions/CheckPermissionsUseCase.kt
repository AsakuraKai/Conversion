package com.example.conversion.domain.usecase.permissions

import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.PermissionState
import com.example.conversion.domain.repository.PermissionsRepository
import com.example.conversion.domain.usecase.base.BaseUseCaseNoParams
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for checking the current status of all app permissions.
 * Returns a PermissionState containing the status of each permission.
 */
class CheckPermissionsUseCase @Inject constructor(
    private val permissionsRepository: PermissionsRepository,
    dispatcher: CoroutineDispatcher
) : BaseUseCaseNoParams<PermissionState>(dispatcher) {

    override suspend fun execute(params: Unit): PermissionState {
        return permissionsRepository.checkPermissions()
    }
}
