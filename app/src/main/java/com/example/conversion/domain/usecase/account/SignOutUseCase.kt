package com.example.conversion.domain.usecase.account

import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.repository.AuthRepository
import com.example.conversion.domain.usecase.base.BaseUseCaseNoParams
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for signing out the current user
 *
 * Signs out the currently authenticated user and returns
 * the app to anonymous mode. Note: This does NOT delete
 * user data from cloud storage.
 *
 * Input: None
 * Output: Unit - Success or failure
 *
 * Throws:
 * - Exception if sign-out fails
 */
class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCaseNoParams<Unit>(dispatcher) {

    /**
     * Executes user sign-out
     *
     * @return Unit on success
     * @throws Exception if sign-out fails
     */
    override suspend fun execute(params: Unit) {
        return when (val result = authRepository.signOut()) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
            is Result.Loading -> throw IllegalStateException("Unexpected loading state")
        }
    }
}
