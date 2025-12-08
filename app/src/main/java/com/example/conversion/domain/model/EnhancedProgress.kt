package com.example.conversion.domain.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Enhanced progress tracking for long-running operations with cancellation support.
 * @property currentStep Current step number (0-based)
 * @property totalSteps Total number of steps
 * @property message User-friendly progress message
 * @property percentage Progress as a percentage (0-100)
 * @property isCancellable Whether the operation can be cancelled
 * @property estimatedTimeRemainingMs Estimated time remaining in milliseconds
 */
data class EnhancedProgress(
    val currentStep: Int,
    val totalSteps: Int,
    val message: String,
    val percentage: Int = (currentStep * 100) / totalSteps.coerceAtLeast(1),
    val isCancellable: Boolean = true,
    val estimatedTimeRemainingMs: Long? = null
) {
    companion object {
        /**
         * Creates an initial progress state.
         */
        fun initial(totalSteps: Int, isCancellable: Boolean = true) = EnhancedProgress(
            currentStep = 0,
            totalSteps = totalSteps,
            message = "Starting...",
            isCancellable = isCancellable
        )
        
        /**
         * Creates a completion progress state.
         */
        fun completed(totalSteps: Int) = EnhancedProgress(
            currentStep = totalSteps,
            totalSteps = totalSteps,
            message = "Completed",
            isCancellable = false
        )
    }
}

/**
 * Progress tracker for operations with callback support.
 * Provides real-time progress updates and cancellation capabilities.
 */
interface ProgressTracker {
    /**
     * Current progress as a Flow.
     */
    val progress: Flow<EnhancedProgress>
    
    /**
     * Updates the current progress.
     * @param step Current step number
     * @param message Progress message
     * @param estimatedTimeRemainingMs Estimated time remaining
     */
    suspend fun updateProgress(
        step: Int,
        message: String,
        estimatedTimeRemainingMs: Long? = null
    )
    
    /**
     * Marks the operation as completed.
     */
    suspend fun complete()
    
    /**
     * Checks if cancellation has been requested.
     * @return true if cancelled, false otherwise
     */
    fun isCancelled(): Boolean
    
    /**
     * Requests cancellation of the operation.
     */
    fun cancel()
}

/**
 * Default implementation of ProgressTracker.
 */
class DefaultProgressTracker(
    private val totalSteps: Int,
    private val isCancellable: Boolean = true
) : ProgressTracker {
    private val _progress = MutableStateFlow(EnhancedProgress.initial(totalSteps, isCancellable))
    override val progress: Flow<EnhancedProgress> = _progress
    
    private var cancelled = false
    
    override suspend fun updateProgress(
        step: Int,
        message: String,
        estimatedTimeRemainingMs: Long?
    ) {
        _progress.emit(
            EnhancedProgress(
                currentStep = step,
                totalSteps = totalSteps,
                message = message,
                isCancellable = isCancellable,
                estimatedTimeRemainingMs = estimatedTimeRemainingMs
            )
        )
    }
    
    override suspend fun complete() {
        _progress.emit(EnhancedProgress.completed(totalSteps))
    }
    
    override fun isCancelled(): Boolean = cancelled
    
    override fun cancel() {
        if (isCancellable) {
            cancelled = true
        }
    }
}

/**
 * Progress callback for operations.
 */
typealias ProgressCallback = suspend (EnhancedProgress) -> Unit
