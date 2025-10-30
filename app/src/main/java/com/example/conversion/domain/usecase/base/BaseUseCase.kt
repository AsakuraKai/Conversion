package com.example.conversion.domain.usecase.base

import com.example.conversion.domain.common.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Base class for all use cases in the application.
 * Enforces clean architecture principles and provides consistent error handling.
 *
 * @param P The type of parameters required by the use case
 * @param R The type of result returned by the use case
 */
abstract class BaseUseCase<in P, R>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    /**
     * Executes the use case with the provided parameters.
     * Automatically handles exceptions and wraps the result in a Result type.
     *
     * @param params The parameters required by the use case
     * @return Result<R> containing either success data or error information
     */
    suspend operator fun invoke(params: P): Result<R> = withContext(dispatcher) {
        try {
            Result.Success(execute(params))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * The actual business logic implementation.
     * Override this method to define the use case behavior.
     *
     * @param params The parameters required by the use case
     * @return R The result of the use case execution
     * @throws Exception if an error occurs during execution
     */
    @Throws(Exception::class)
    protected abstract suspend fun execute(params: P): R
}

/**
 * Base class for use cases that don't require parameters.
 */
abstract class BaseUseCaseNoParams<R>(
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseUseCase<Unit, R>(dispatcher) {
    
    suspend operator fun invoke(): Result<R> = invoke(Unit)
}

/**
 * Base class for use cases that return Flow.
 * Does not wrap in Result - the Flow itself handles emissions and errors.
 */
abstract class FlowUseCase<in P, R>(
    protected val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    /**
     * Execute the use case and return a Flow
     */
    abstract operator fun invoke(params: P): kotlinx.coroutines.flow.Flow<R>
}

/**
 * Base class for Flow use cases without parameters
 */
abstract class FlowUseCaseNoParams<R>(
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : FlowUseCase<Unit, R>(dispatcher) {
    
    operator fun invoke(): kotlinx.coroutines.flow.Flow<R> = invoke(Unit)
}
