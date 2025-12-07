package com.example.conversion.domain.usecase.history

import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.RenameOperation
import com.example.conversion.domain.repository.HistoryRepository
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for saving a rename operation to history.
 *
 * This use case stores a completed rename operation so it can be undone later.
 * Should be called immediately after a successful rename operation.
 *
 * @property historyRepository Repository for managing operation history
 * @property dispatcher Coroutine dispatcher for background operations
 */
class SaveOperationUseCase @Inject constructor(
    private val historyRepository: HistoryRepository,
    dispatcher: CoroutineDispatcher
) : BaseUseCase<RenameOperation, Unit>(dispatcher) {

    /**
     * Saves an operation to history.
     *
     * @param params The RenameOperation to save
     * @return Unit on success
     * @throws IllegalArgumentException if the operation is invalid
     * @throws Exception if the operation cannot be saved
     */
    override suspend fun execute(params: RenameOperation) {
        require(params.isValid()) {
            "Cannot save invalid operation: ${params.id}"
        }

        when (val result = historyRepository.saveOperation(params)) {
            is Result.Success -> Unit
            is Result.Error -> throw result.exception
        }
    }
}
