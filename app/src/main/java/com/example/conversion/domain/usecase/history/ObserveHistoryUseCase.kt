package com.example.conversion.domain.usecase.history

import com.example.conversion.domain.model.OperationHistory
import com.example.conversion.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing changes to the rename operation history.
 *
 * This use case provides a Flow that emits the current operation history
 * whenever it changes, enabling real-time UI updates for undo/redo buttons.
 *
 * @property historyRepository Repository for managing operation history
 */
class ObserveHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    /**
     * Observes the operation history.
     *
     * @return Flow emitting the current OperationHistory whenever it changes
     */
    operator fun invoke(): Flow<OperationHistory> {
        return historyRepository.observeHistory()
    }
}
