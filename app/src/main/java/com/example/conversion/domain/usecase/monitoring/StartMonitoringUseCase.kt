package com.example.conversion.domain.usecase.monitoring

import com.example.conversion.domain.model.FolderMonitor
import com.example.conversion.domain.repository.FolderMonitorRepository
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for starting folder monitoring.
 * Begins monitoring a folder for file changes and applies automatic renaming.
 *
 * @property folderMonitorRepository Repository for folder monitoring operations
 * @property dispatcher Coroutine dispatcher for execution
 */
class StartMonitoringUseCase @Inject constructor(
    private val folderMonitorRepository: FolderMonitorRepository,
    dispatcher: CoroutineDispatcher
) : BaseUseCase<FolderMonitor, Unit>(dispatcher) {

    /**
     * Starts monitoring the specified folder.
     *
     * @param params The folder monitor configuration
     * @return Unit on success
     * @throws IllegalArgumentException if the folder monitor configuration is invalid
     */
    override suspend fun execute(params: FolderMonitor): Unit {
        if (!params.isValid()) {
            throw IllegalArgumentException("Invalid folder monitor configuration")
        }
        
        return folderMonitorRepository.startMonitoring(params).getOrThrow()
    }
}
