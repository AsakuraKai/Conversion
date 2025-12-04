package com.example.conversion.domain.usecase.monitoring

import com.example.conversion.domain.repository.FolderMonitorRepository
import com.example.conversion.domain.usecase.base.BaseUseCaseNoParams
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for stopping folder monitoring.
 * Stops monitoring the currently monitored folder.
 *
 * @property folderMonitorRepository Repository for folder monitoring operations
 * @property dispatcher Coroutine dispatcher for execution
 */
class StopMonitoringUseCase @Inject constructor(
    private val folderMonitorRepository: FolderMonitorRepository,
    dispatcher: CoroutineDispatcher
) : BaseUseCaseNoParams<Unit>(dispatcher) {

    /**
     * Stops monitoring the currently monitored folder.
     *
     * @return Unit on success
     */
    override suspend fun execute(params: Unit): Unit {
        return folderMonitorRepository.stopMonitoring().getOrThrow()
    }
}
