package com.example.conversion.domain.usecase.history

import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.repository.HistoryRepository
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for clearing all rename operations from history.
 *
 * This permanently removes all stored operations and cannot be undone.
 * Should typically be confirmed with the user before execution.
 *
 * @property historyRepository Repository for managing operation history
 * @property dispatcher Coroutine dispatcher for background operations
 */
class ClearHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<Unit, Unit>(dispatcher) {

    /**
     * Clears all operations from history.
     *
     * @param params Unit (no parameters needed)
     * @return Unit on success
     * @throws Exception if history cannot be cleared
     */
    override suspend fun execute(params: Unit) {
        when (val result = historyRepository.clearHistory()) {
            is Result.Success -> Unit
            is Result.Error -> throw result.exception
            is Result.Loading -> throw IllegalStateException("Unexpected loading state")
        }
    }
}
