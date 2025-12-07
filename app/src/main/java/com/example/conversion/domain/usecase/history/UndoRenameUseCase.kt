package com.example.conversion.domain.usecase.history

import android.net.Uri
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.RenameOperation
import com.example.conversion.domain.repository.HistoryRepository
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for undoing a rename operation.
 *
 * This use case reverts a file to its original name by using the information
 * stored in a RenameOperation. It also updates the operation history accordingly.
 *
 * @property historyRepository Repository for managing operation history
 * @property dispatcher Coroutine dispatcher for background operations
 */
class UndoRenameUseCase @Inject constructor(
    private val historyRepository: HistoryRepository,
    dispatcher: CoroutineDispatcher
) : BaseUseCase<RenameOperation, Uri>(dispatcher) {

    /**
     * Executes the undo operation.
     *
     * @param params The RenameOperation to undo
     * @return The URI of the file after reverting to its original name
     * @throws IllegalArgumentException if the operation is invalid
     * @throws IllegalStateException if the file cannot be reverted
     */
    override suspend fun execute(params: RenameOperation): Uri {
        require(params.isValid()) {
            "Cannot undo invalid operation: ${params.id}"
        }

        // Perform the undo operation through the repository
        return when (val result = historyRepository.undoOperation(params)) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }
}
