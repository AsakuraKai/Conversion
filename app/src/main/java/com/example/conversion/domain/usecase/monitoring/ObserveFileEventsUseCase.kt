package com.example.conversion.domain.usecase.monitoring

import com.example.conversion.domain.model.FileEvent
import com.example.conversion.domain.repository.FolderMonitorRepository
import com.example.conversion.domain.usecase.base.FlowUseCaseNoParams
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing file events in the monitored folder.
 * Returns a Flow that emits file events (create, modify, delete, move).
 *
 * @property folderMonitorRepository Repository for folder monitoring operations
 * @property dispatcher Coroutine dispatcher for execution
 */
class ObserveFileEventsUseCase @Inject constructor(
    private val folderMonitorRepository: FolderMonitorRepository,
    dispatcher: CoroutineDispatcher
) : FlowUseCaseNoParams<FileEvent>(dispatcher) {

    /**
     * Observes file events in the monitored folder.
     *
     * @return Flow emitting file events
     */
    override fun invoke(params: Unit): Flow<FileEvent> {
        return folderMonitorRepository.observeFileEvents()
    }
}
