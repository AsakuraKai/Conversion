package com.example.conversion.domain.usecase.permissions

import com.example.conversion.domain.common.Result
import com.example.conversion.domain.repository.PermissionsRepository
import com.example.conversion.domain.usecase.base.BaseUseCaseNoParams
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case to check if all required media permissions are granted.
 * This is a convenience use case for quickly checking media access.
 */
class HasMediaAccessUseCase @Inject constructor(
    private val permissionsRepository: PermissionsRepository,
    dispatcher: CoroutineDispatcher
) : BaseUseCaseNoParams<Boolean>(dispatcher) {

    override suspend fun execute(params: Unit): Boolean {
        return permissionsRepository.hasMediaAccess()
    }
}
