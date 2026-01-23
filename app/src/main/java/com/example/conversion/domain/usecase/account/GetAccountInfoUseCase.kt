package com.example.conversion.domain.usecase.account

import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.AuthUser
import com.example.conversion.domain.repository.AuthRepository
import com.example.conversion.domain.usecase.base.BaseUseCaseNoParams
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for getting current account information
 *
 * Retrieves information about the currently authenticated user.
 * Returns null if user is not signed in (anonymous mode).
 *
 * Input: None
 * Output: AuthUser? - The authenticated user or null
 *
 * Throws:
 * - Exception if retrieval fails
 */
class GetAccountInfoUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCaseNoParams<AuthUser?>(dispatcher) {

    /**
     * Executes account info retrieval
     *
     * @return AuthUser on success, null if anonymous
     * @throws Exception if retrieval fails
     */
    override suspend fun execute(params: Unit): AuthUser? {
        return authRepository.getCurrentUser()
    }
}
