package com.example.conversion.domain.model

/**
 * Represents the animation state for UI transitions.
 * Used to coordinate animations between different screens and operations.
 */
sealed class AnimationState {
    /**
     * Initial idle state, no animation in progress.
     */
    data object Idle : AnimationState()
    
    /**
     * Animation is currently in progress.
     * @property progress Animation progress from 0.0 to 1.0
     * @property delayMs Optional delay before starting animation (for testing)
     */
    data class InProgress(
        val progress: Float = 0f,
        val delayMs: Long = 0L
    ) : AnimationState()
    
    /**
     * Animation completed successfully.
     */
    data object Completed : AnimationState()
    
    /**
     * Animation was cancelled.
     */
    data object Cancelled : AnimationState()
}

/**
 * Represents a transition between UI states with animation support.
 * @property from Source state
 * @property to Target state
 * @property animationState Current animation state
 * @property durationMs Animation duration in milliseconds
 */
data class UITransition(
    val from: String,
    val to: String,
    val animationState: AnimationState = AnimationState.Idle,
    val durationMs: Long = 300L
)

/**
 * Animation configuration for operations.
 * @property enabled Whether animations are enabled
 * @property durationMs Base duration for animations
 * @property testDelayMs Additional delay for testing animations
 */
data class AnimationConfig(
    val enabled: Boolean = true,
    val durationMs: Long = 300L,
    val testDelayMs: Long = 0L
)
