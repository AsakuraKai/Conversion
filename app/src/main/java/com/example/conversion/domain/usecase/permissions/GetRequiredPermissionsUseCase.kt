package com.example.conversion.domain.usecase.permissions

import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.Permission
import com.example.conversion.domain.repository.PermissionsRepository
import com.example.conversion.domain.usecase.base.BaseUseCaseNoParams
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case to get the list of permissions required for the current Android version.
 */
class GetRequiredPermissionsUseCase @Inject constructor(
    private val permissionsRepository: PermissionsRepository,
    dispatcher: CoroutineDispatcher
) : BaseUseCaseNoParams<List<Permission>>(dispatcher) {

    override suspend fun execute(params: Unit): List<Permission> {
        return permissionsRepository.getRequiredPermissions()
    }
}
