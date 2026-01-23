package com.example.conversion.domain.usecase.activity

import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.ActivityLog
import com.example.conversion.domain.repository.ActivityRepository
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for logging activities in the system.
 *
 * This use case saves activity logs to track all operations performed
 * in the application for auditing and debugging purposes.
 *
 * @property activityRepository Repository for managing activity logs
 * @property dispatcher Coroutine dispatcher for background operations
 */
class LogActivityUseCase @Inject constructor(
    private val activityRepository: ActivityRepository,
    dispatcher: CoroutineDispatcher
) : BaseUseCase<ActivityLog, Unit>(dispatcher) {

    /**
     * Logs an activity in the system.
     *
     * @param params The ActivityLog to save
     * @return Unit on success
     * @throws Exception if the log cannot be saved
     */
    override suspend fun execute(params: ActivityLog): Unit {
        return when (val result = activityRepository.logActivity(params)) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
            is Result.Loading -> throw IllegalStateException("Unexpected Loading state")
        }
    }
}
