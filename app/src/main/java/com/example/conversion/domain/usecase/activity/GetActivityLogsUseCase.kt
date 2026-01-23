package com.example.conversion.domain.usecase.activity

import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.ActivityLog
import com.example.conversion.domain.model.LogFilter
import com.example.conversion.domain.repository.ActivityRepository
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for retrieving activity logs based on filter criteria.
 *
 * This use case queries the activity log storage and returns logs
 * that match the specified filter criteria.
 *
 * @property activityRepository Repository for managing activity logs
 * @property dispatcher Coroutine dispatcher for background operations
 */
class GetActivityLogsUseCase @Inject constructor(
    private val activityRepository: ActivityRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<LogFilter, List<ActivityLog>>(dispatcher) {

    /**
     * Retrieves activity logs based on filter criteria.
     *
     * @param params The LogFilter specifying query criteria
     * @return List of matching ActivityLogs
     * @throws Exception if the logs cannot be retrieved
     */
    override suspend fun execute(params: LogFilter): List<ActivityLog> {
        return when (val result = activityRepository.getActivityLogs(params)) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
            is Result.Loading -> throw IllegalStateException("Unexpected Loading state")
        }
    }
}
