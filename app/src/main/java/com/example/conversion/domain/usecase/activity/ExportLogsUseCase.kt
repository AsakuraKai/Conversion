package com.example.conversion.domain.usecase.activity

import android.net.Uri
import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.ExportFormat
import com.example.conversion.domain.repository.ActivityRepository
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for exporting activity logs to a file.
 *
 * This use case exports all activity logs to a file in the specified format
 * (CSV or JSON) for external analysis or archival purposes.
 *
 * @property activityRepository Repository for managing activity logs
 * @property dispatcher Coroutine dispatcher for background operations
 */
class ExportLogsUseCase @Inject constructor(
    private val activityRepository: ActivityRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<ExportFormat, Uri>(dispatcher) {

    /**
     * Exports activity logs to a file.
     *
     * @param params The ExportFormat (CSV or JSON)
     * @return Uri of the exported file
     * @throws Exception if the export fails
     */
    override suspend fun execute(params: ExportFormat): Uri {
        return when (val result = activityRepository.exportLogs(params)) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
            is Result.Loading -> throw IllegalStateException("Unexpected Loading state")
        }
    }
}
