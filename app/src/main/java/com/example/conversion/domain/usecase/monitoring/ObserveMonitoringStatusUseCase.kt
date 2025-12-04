package com.example.conversion.domain.usecase.monitoring

import com.example.conversion.domain.model.MonitoringStatus
import com.example.conversion.domain.repository.FolderMonitorRepository
import com.example.conversion.domain.usecase.base.FlowUseCaseNoParams
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing the folder monitoring status.
 * Returns a Flow that emits the current monitoring status.
 *
 * @property folderMonitorRepository Repository for folder monitoring operations
 * @property dispatcher Coroutine dispatcher for execution
 */
class ObserveMonitoringStatusUseCase @Inject constructor(
    private val folderMonitorRepository: FolderMonitorRepository,
    dispatcher: CoroutineDispatcher
) : FlowUseCaseNoParams<MonitoringStatus>(dispatcher) {

    /**
     * Observes the folder monitoring status.
     *
     * @return Flow emitting monitoring status updates
     */
    override fun invoke(params: Unit): Flow<MonitoringStatus> {
        return folderMonitorRepository.observeMonitoringStatus()
    }
}
