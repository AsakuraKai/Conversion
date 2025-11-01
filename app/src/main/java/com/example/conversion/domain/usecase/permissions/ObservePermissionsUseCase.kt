package com.example.conversion.domain.usecase.permissions

import com.example.conversion.domain.model.Permission
import com.example.conversion.domain.repository.PermissionsRepository
import com.example.conversion.domain.usecase.base.FlowUseCaseNoParams
import com.example.conversion.domain.model.PermissionState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing permission state changes.
 * Returns a Flow that emits whenever permissions are updated.
 */
class ObservePermissionsUseCase @Inject constructor(
    private val permissionsRepository: PermissionsRepository,
    dispatcher: CoroutineDispatcher
) : FlowUseCaseNoParams<PermissionState>(dispatcher) {

    override fun invoke(params: Unit): Flow<PermissionState> {
        return permissionsRepository.observePermissions()
    }
}
