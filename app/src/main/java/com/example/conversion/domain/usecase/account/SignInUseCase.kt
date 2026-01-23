package com.example.conversion.domain.usecase.account

import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.AuthUser
import com.example.conversion.domain.repository.AuthRepository
import com.example.conversion.domain.usecase.base.BaseUseCaseNoParams
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for signing in with Google account
 *
 * Initiates Google OAuth authentication flow and creates
 * a persistent Firebase user linked to the Google account.
 *
 * Input: None
 * Output: AuthUser - The authenticated user
 *
 * Throws:
 * - Exception if authentication fails
 * - Exception if OAuth was cancelled by user
 */
class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCaseNoParams<AuthUser>(dispatcher) {

    /**
     * Executes Google Sign-In flow
     *
     * @return AuthUser on success
     * @throws Exception if authentication fails
     */
    override suspend fun execute(params: Unit): AuthUser {
        return  when (val result = authRepository.signInWithGoogle()) {
            is Result.Success<AuthUser> -> result.data
            is Result.Error -> throw result.exception
            else -> throw IllegalStateException("Unexpected state: $result")
        }
    }
}
