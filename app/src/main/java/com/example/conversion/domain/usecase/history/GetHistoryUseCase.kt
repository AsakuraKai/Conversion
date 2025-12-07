package com.example.conversion.domain.usecase.history

import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.RenameOperation
import com.example.conversion.domain.repository.HistoryRepository
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for retrieving the complete rename operation history.
 *
 * Returns all rename operations that have been performed, in chronological order.
 * This is useful for displaying a history list to the user.
 *
 * @property historyRepository Repository for managing operation history
 * @property dispatcher Coroutine dispatcher for background operations
 */
class GetHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository,
    dispatcher: CoroutineDispatcher
) : BaseUseCase<Unit, List<RenameOperation>>(dispatcher) {

    /**
     * Retrieves the complete operation history.
     *
     * @param params Unit (no parameters needed)
     * @return List of all rename operations in chronological order
     * @throws Exception if history cannot be retrieved
     */
    override suspend fun execute(params: Unit): List<RenameOperation> {
        return when (val result = historyRepository.getHistory()) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }
}
