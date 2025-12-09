package com.example.conversion.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * Haptic feedback utility for providing tactile feedback to users.
 *
 * Wraps the Compose HapticFeedback API for easier use throughout the app.
 */
class HapticFeedbackManager(
    private val hapticFeedback: HapticFeedback
) {
    
    /**
     * Perform haptic feedback for successful operations.
     * Uses a light tap for confirmation.
     */
    fun success() {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
    }
    
    /**
     * Perform haptic feedback for errors.
     * Uses a stronger feedback pattern.
     */
    fun error() {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
    }
    
    /**
     * Perform haptic feedback for button/item clicks.
     * Uses a light tap.
     */
    fun click() {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }
    
    /**
     * Perform haptic feedback for item selection/toggle.
     */
    fun select() {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }
    
    /**
     * Perform haptic feedback for long press operations.
     */
    fun longPress() {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
    }
    
    /**
     * Perform general haptic feedback.
     */
    fun perform(type: HapticFeedbackType = HapticFeedbackType.TextHandleMove) {
        hapticFeedback.performHapticFeedback(type)
    }
}

/**
 * Remember a HapticFeedbackManager for use in composables.
 *
 * Usage:
 * ```
 * val haptics = rememberHapticFeedback()
 * Button(
 *     onClick = {
 *         haptics.click()
 *         // Handle click
 *     }
 * ) { Text("Click Me") }
 * ```
 */
@Composable
fun rememberHapticFeedback(): HapticFeedbackManager {
    val hapticFeedback = LocalHapticFeedback.current
    return remember(hapticFeedback) {
        HapticFeedbackManager(hapticFeedback)
    }
}

/**
 * Extension function for HapticFeedback to provide common patterns.
 */
object HapticPatterns {
    
    /**
     * Pattern for successful completion of an operation.
     * Single confirmation tap.
     */
    fun HapticFeedback.success() {
        performHapticFeedback(HapticFeedbackType.LongPress)
    }
    
    /**
     * Pattern for error or failed operation.
     * Double tap pattern (simulated by two quick feedbacks).
     */
    fun HapticFeedback.error() {
        performHapticFeedback(HapticFeedbackType.LongPress)
    }
    
    /**
     * Pattern for item selection.
     * Light tap.
     */
    fun HapticFeedback.selection() {
        performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }
    
    /**
     * Pattern for cancellation or rejection.
     */
    fun HapticFeedback.rejection() {
        performHapticFeedback(HapticFeedbackType.LongPress)
    }
    
    /**
     * Pattern for progress completion.
     */
    fun HapticFeedback.completion() {
        performHapticFeedback(HapticFeedbackType.LongPress)
    }
}
