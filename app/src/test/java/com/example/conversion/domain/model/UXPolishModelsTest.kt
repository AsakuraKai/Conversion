package com.example.conversion.domain.model

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnimationStateTest {
    
    @Test
    fun `AnimationState Idle is correct initial state`() {
        val state = AnimationState.Idle
        assertTrue(state is AnimationState.Idle)
    }
    
    @Test
    fun `AnimationState InProgress tracks progress correctly`() {
        val state = AnimationState.InProgress(progress = 0.5f, delayMs = 100L)
        assertTrue(state is AnimationState.InProgress)
        assertEquals(0.5f, state.progress, 0.001f)
        assertEquals(100L, state.delayMs)
    }
    
    @Test
    fun `AnimationState Completed represents finished animation`() {
        val state = AnimationState.Completed
        assertTrue(state is AnimationState.Completed)
    }
    
    @Test
    fun `AnimationState Cancelled represents cancelled animation`() {
        val state = AnimationState.Cancelled
        assertTrue(state is AnimationState.Cancelled)
    }
    
    @Test
    fun `UITransition contains correct transition data`() {
        val transition = UITransition(
            from = "Home",
            to = "Settings",
            animationState = AnimationState.InProgress(0.3f),
            durationMs = 500L
        )
        
        assertEquals("Home", transition.from)
        assertEquals("Settings", transition.to)
        assertTrue(transition.animationState is AnimationState.InProgress)
        assertEquals(500L, transition.durationMs)
    }
    
    @Test
    fun `UITransition defaults to Idle state`() {
        val transition = UITransition(from = "A", to = "B")
        assertTrue(transition.animationState is AnimationState.Idle)
        assertEquals(300L, transition.durationMs)
    }
    
    @Test
    fun `AnimationConfig has correct defaults`() {
        val config = AnimationConfig()
        assertTrue(config.enabled)
        assertEquals(300L, config.durationMs)
        assertEquals(0L, config.testDelayMs)
    }
    
    @Test
    fun `AnimationConfig can be customized`() {
        val config = AnimationConfig(
            enabled = false,
            durationMs = 1000L,
            testDelayMs = 500L
        )
        
        assertFalse(config.enabled)
        assertEquals(1000L, config.durationMs)
        assertEquals(500L, config.testDelayMs)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class EnhancedProgressTest {
    
    @Test
    fun `EnhancedProgress calculates percentage correctly`() {
        val progress = EnhancedProgress(
            currentStep = 5,
            totalSteps = 10,
            message = "Processing..."
        )
        
        assertEquals(50, progress.percentage)
    }
    
    @Test
    fun `EnhancedProgress handles zero total steps`() {
        val progress = EnhancedProgress(
            currentStep = 0,
            totalSteps = 0,
            message = "Empty"
        )
        
        assertEquals(0, progress.percentage)
    }
    
    @Test
    fun `EnhancedProgress initial creates correct state`() {
        val progress = EnhancedProgress.initial(totalSteps = 100, isCancellable = true)
        
        assertEquals(0, progress.currentStep)
        assertEquals(100, progress.totalSteps)
        assertEquals("Starting...", progress.message)
        assertTrue(progress.isCancellable)
    }
    
    @Test
    fun `EnhancedProgress completed creates final state`() {
        val progress = EnhancedProgress.completed(totalSteps = 50)
        
        assertEquals(50, progress.currentStep)
        assertEquals(50, progress.totalSteps)
        assertEquals("Completed", progress.message)
        assertFalse(progress.isCancellable)
        assertEquals(100, progress.percentage)
    }
    
    @Test
    fun `DefaultProgressTracker starts with initial state`() = runTest {
        val tracker = DefaultProgressTracker(totalSteps = 10, isCancellable = true)
        val initialProgress = tracker.progress.first()
        
        assertEquals(0, initialProgress.currentStep)
        assertEquals(10, initialProgress.totalSteps)
        assertTrue(initialProgress.isCancellable)
    }
    
    @Test
    fun `DefaultProgressTracker updates progress correctly`() = runTest {
        val tracker = DefaultProgressTracker(totalSteps = 10)
        
        tracker.updateProgress(step = 5, message = "Halfway", estimatedTimeRemainingMs = 5000L)
        val progress = tracker.progress.first()
        
        assertEquals(5, progress.currentStep)
        assertEquals("Halfway", progress.message)
        assertEquals(5000L, progress.estimatedTimeRemainingMs)
    }
    
    @Test
    fun `DefaultProgressTracker completes correctly`() = runTest {
        val tracker = DefaultProgressTracker(totalSteps = 10)
        
        tracker.complete()
        val progress = tracker.progress.first()
        
        assertEquals(10, progress.currentStep)
        assertEquals("Completed", progress.message)
        assertFalse(progress.isCancellable)
    }
    
    @Test
    fun `DefaultProgressTracker handles cancellation`() {
        val tracker = DefaultProgressTracker(totalSteps = 10, isCancellable = true)
        
        assertFalse(tracker.isCancelled())
        tracker.cancel()
        assertTrue(tracker.isCancelled())
    }
    
    @Test
    fun `DefaultProgressTracker does not cancel if not cancellable`() {
        val tracker = DefaultProgressTracker(totalSteps = 10, isCancellable = false)
        
        tracker.cancel()
        assertFalse(tracker.isCancelled())
    }
}

class CancellableOperationTest {
    
    @Test
    fun `DefaultCancellableOperation starts not cancelled`() {
        val operation = DefaultCancellableOperation()
        assertFalse(operation.isCancelled)
    }
    
    @Test
    fun `DefaultCancellableOperation can be cancelled`() {
        val operation = DefaultCancellableOperation()
        
        val result = operation.cancel()
        assertTrue(result)
        assertTrue(operation.isCancelled)
    }
    
    @Test
    fun `DefaultCancellableOperation cancel returns false if already cancelled`() {
        val operation = DefaultCancellableOperation()
        
        operation.cancel()
        val secondCancel = operation.cancel()
        
        assertFalse(secondCancel)
    }
    
    @Test
    fun `DefaultCancellableOperation throws if cancelled`() {
        val operation = DefaultCancellableOperation()
        operation.cancel()
        
        try {
            operation.throwIfCancelled()
            fail("Should have thrown OperationCancelledException")
        } catch (e: OperationCancelledException) {
            // Expected
        }
    }
    
    @Test
    fun `DefaultCancellableOperation does not throw if not cancelled`() {
        val operation = DefaultCancellableOperation()
        
        // Should not throw
        operation.throwIfCancelled()
    }
    
    @Test
    fun `CancellableOperationBuilder creates operation`() {
        val operation = CancellableOperationBuilder.create()
        assertNotNull(operation)
        assertFalse(operation.isCancelled)
    }
    
    @Test
    fun `DefaultCancellationToken starts not cancelled`() {
        val token = DefaultCancellationToken()
        assertFalse(token.isCancellationRequested)
    }
    
    @Test
    fun `DefaultCancellationToken can be cancelled`() {
        val token = DefaultCancellationToken()
        token.cancel()
        assertTrue(token.isCancellationRequested)
    }
    
    @Test
    fun `OperationCancelledException has correct message`() {
        val exception = OperationCancelledException("Test message")
        assertEquals("Test message", exception.message)
    }
    
    @Test
    fun `OperationCancelledException has default message`() {
        val exception = OperationCancelledException()
        assertEquals("Operation was cancelled", exception.message)
    }
}

class UserFriendlyErrorTest {
    
    @Test
    fun `PermissionError contains correct information`() {
        val error = UserFriendlyError.PermissionError(
            message = "Storage permission required",
            permissionType = "Storage"
        )
        
        assertEquals("Storage permission required", error.message)
        assertEquals("Storage", error.permissionType)
        assertTrue(error.recoverySuggestions.isNotEmpty())
    }
    
    @Test
    fun `FileOperationError contains operation info`() {
        val error = UserFriendlyError.FileOperationError(
            message = "Failed to rename file",
            operation = "Rename"
        )
        
        assertEquals("Failed to rename file", error.message)
        assertEquals("Rename", error.operation)
        assertTrue(error.recoverySuggestions.isNotEmpty())
    }
    
    @Test
    fun `NetworkError has recovery suggestions`() {
        val error = UserFriendlyError.NetworkError()
        
        assertTrue(error.recoverySuggestions.contains("Check your internet connection"))
    }
    
    @Test
    fun `ValidationError includes field name`() {
        val error = UserFriendlyError.ValidationError(
            message = "Invalid filename",
            field = "filename"
        )
        
        assertEquals("Invalid filename", error.message)
        assertEquals("filename", error.field)
    }
    
    @Test
    fun `fromException creates CancellationError for OperationCancelledException`() {
        val exception = OperationCancelledException("User cancelled")
        val error = UserFriendlyError.fromException(exception)
        
        assertTrue(error is UserFriendlyError.CancellationError)
        assertEquals("User cancelled", error.message)
    }
    
    @Test
    fun `fromException creates PermissionError for SecurityException`() {
        val exception = SecurityException("Permission denied")
        val error = UserFriendlyError.fromException(exception)
        
        assertTrue(error is UserFriendlyError.PermissionError)
    }
    
    @Test
    fun `fromException creates FileOperationError for IOException`() {
        val exception = java.io.IOException("File not found")
        val error = UserFriendlyError.fromException(exception)
        
        assertTrue(error is UserFriendlyError.FileOperationError)
    }
    
    @Test
    fun `fromException creates ValidationError for IllegalArgumentException`() {
        val exception = IllegalArgumentException("Invalid input")
        val error = UserFriendlyError.fromException(exception)
        
        assertTrue(error is UserFriendlyError.ValidationError)
    }
    
    @Test
    fun `fromException creates GenericError for unknown exceptions`() {
        val exception = RuntimeException("Unknown error")
        val error = UserFriendlyError.fromException(exception)
        
        assertTrue(error is UserFriendlyError.GenericError)
    }
    
    @Test
    fun `permissionDenied creates detailed PermissionError`() {
        val error = UserFriendlyError.permissionDenied("Camera")
        
        assertTrue(error is UserFriendlyError.PermissionError)
        assertTrue(error.message.contains("Camera"))
        assertTrue(error.recoverySuggestions.any { it.contains("Settings") })
    }
    
    @Test
    fun `insufficientStorage creates StorageError`() {
        val error = UserFriendlyError.insufficientStorage()
        
        assertTrue(error is UserFriendlyError.StorageError)
        assertTrue(error.recoverySuggestions.any { it.contains("free up space") })
    }
    
    @Test
    fun `noInternet creates NetworkError`() {
        val error = UserFriendlyError.noInternet()
        
        assertTrue(error is UserFriendlyError.NetworkError)
        assertTrue(error.message.contains("internet"))
    }
}
