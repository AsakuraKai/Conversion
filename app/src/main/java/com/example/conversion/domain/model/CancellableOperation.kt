package com.example.conversion.domain.model

import kotlinx.coroutines.Job

/**
 * Represents a cancellable operation.
 * Provides consistent cancellation handling across different operations.
 */
interface CancellableOperation {
    /**
     * Checks if the operation has been cancelled.
     */
    val isCancelled: Boolean
    
    /**
     * Cancels the operation.
     * @return true if cancellation was successful, false if already cancelled or not cancellable
     */
    fun cancel(): Boolean
    
    /**
     * Throws CancellationException if the operation has been cancelled.
     */
    @Throws(OperationCancelledException::class)
    fun throwIfCancelled()
}

/**
 * Exception thrown when an operation is cancelled.
 */
class OperationCancelledException(
    message: String = "Operation was cancelled",
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * Default implementation of CancellableOperation.
 */
class DefaultCancellableOperation(
    private val job: Job? = null
) : CancellableOperation {
    private var _cancelled = false
    
    override val isCancelled: Boolean
        get() = _cancelled || job?.isCancelled == true
    
    override fun cancel(): Boolean {
        if (_cancelled) return false
        _cancelled = true
        job?.cancel()
        return true
    }
    
    override fun throwIfCancelled() {
        if (isCancelled) {
            throw OperationCancelledException()
        }
    }
}

/**
 * Builder for cancellable operations.
 */
object CancellableOperationBuilder {
    /**
     * Creates a cancellable operation linked to a coroutine Job.
     */
    fun fromJob(job: Job): CancellableOperation = DefaultCancellableOperation(job)
    
    /**
     * Creates a standalone cancellable operation.
     */
    fun create(): CancellableOperation = DefaultCancellableOperation()
}

/**
 * Token that can be used to cancel an operation.
 * Provides a safe way to pass cancellation capability to callers.
 */
interface CancellationToken {
    /**
     * Requests cancellation.
     */
    fun cancel()
    
    /**
     * Checks if cancellation was requested.
     */
    val isCancellationRequested: Boolean
}

/**
 * Default implementation of CancellationToken.
 */
class DefaultCancellationToken : CancellationToken {
    private var _cancelled = false
    
    override fun cancel() {
        _cancelled = true
    }
    
    override val isCancellationRequested: Boolean
        get() = _cancelled
}
