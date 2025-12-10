package com.example.conversion.domain.usecase.history

import android.net.Uri
import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.RenameOperation
import com.example.conversion.domain.repository.HistoryRepository
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for redoing a previously undone rename operation.
 *
 * This use case reapplies a rename operation that was previously undone,
 * restoring the file to its renamed state.
 *
 * @property historyRepository Repository for managing operation history
 * @property dispatcher Coroutine dispatcher for background operations
 */
class RedoRenameUseCase @Inject constructor(
    private val historyRepository: HistoryRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<RenameOperation, Uri>(dispatcher) {

    /**
     * Executes the redo operation.
     *
     * @param params The RenameOperation to redo
     * @return The URI of the file after reapplying the rename
     * @throws IllegalArgumentException if the operation is invalid
     * @throws IllegalStateException if the file cannot be renamed
     */
    override suspend fun execute(params: RenameOperation): Uri {
        require(params.isValid()) {
            "Cannot redo invalid operation: ${params.id}"
        }

        // Perform the redo operation through the repository
        return when (val result = historyRepository.redoOperation(params)) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
            is Result.Loading -> throw IllegalStateException("Unexpected loading state")
        }
    }
}
