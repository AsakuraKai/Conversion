package com.example.conversion.domain.usecase.monitoring

import com.example.conversion.domain.model.MonitoringStatus
import com.example.conversion.domain.repository.FolderMonitorRepository
import com.example.conversion.domain.usecase.base.BaseUseCaseNoParams
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for getting the current folder monitoring status.
 * Returns the current monitoring status as a single value.
 *
 * @property folderMonitorRepository Repository for folder monitoring operations
 * @property dispatcher Coroutine dispatcher for execution
 */
class GetMonitoringStatusUseCase @Inject constructor(
    private val folderMonitorRepository: FolderMonitorRepository,
    dispatcher: CoroutineDispatcher
) : BaseUseCaseNoParams<MonitoringStatus>(dispatcher) {

    /**
     * Gets the current folder monitoring status.
     *
     * @return The current monitoring status
     */
    override suspend fun execute(params: Unit): MonitoringStatus {
        return folderMonitorRepository.getMonitoringStatus()
    }
}
