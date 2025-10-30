package com.example.conversion.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.conversion.domain.common.Result
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base ViewModel providing common functionality for all ViewModels in the app.
 * Implements MVI-style state management with unidirectional data flow.
 *
 * @param STATE The type of UI state this ViewModel manages
 * @param EVENT One-time events (navigation, toasts, etc.)
 */
abstract class BaseViewModel<STATE, EVENT>(
    initialState: STATE
) : ViewModel() {
    
    // State management
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<STATE> = _state.asStateFlow()
    
    // One-time events
    private val _events = Channel<EVENT>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    
    // Current state value
    protected val currentState: STATE
        get() = _state.value
    
    // Global error handler for coroutines
    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        handleError(throwable)
    }
    
    /**
     * Updates the UI state using the provided reducer function
     */
    protected fun updateState(reducer: STATE.() -> STATE) {
        _state.update(reducer)
    }
    
    /**
     * Sets the UI state to a new value
     */
    protected fun setState(newState: STATE) {
        _state.value = newState
    }
    
    /**
     * Sends a one-time event to the UI
     */
    protected fun sendEvent(event: EVENT) {
        viewModelScope.launch {
            _events.send(event)
        }
    }
    
    /**
     * Launches a coroutine in viewModelScope with error handling
     */
    protected fun launch(
        block: suspend CoroutineScope.() -> Unit
    ) = viewModelScope.launch(errorHandler, block = block)
    
    /**
     * Handles errors that occur during coroutine execution
     * Override to provide custom error handling
     */
    protected open fun handleError(error: Throwable) {
        // Default implementation - can be overridden by subclasses
        error.printStackTrace()
    }
    
    /**
     * Helper to handle Result types with loading, success, and error states
     */
    protected suspend fun <T> Result<T>.handleResult(
        onLoading: () -> Unit = {},
        onSuccess: (T) -> Unit,
        onError: (Throwable) -> Unit = { handleError(it) }
    ) {
        when (this) {
            is Result.Loading -> onLoading()
            is Result.Success -> onSuccess(data)
            is Result.Error -> onError(exception)
        }
    }
    
    /**
     * Helper to safely execute a block and handle Result
     */
    protected fun <T> executeUseCase(
        onLoading: () -> Unit = {},
        onSuccess: (T) -> Unit,
        onError: (Throwable) -> Unit = { handleError(it) },
        block: suspend () -> Result<T>
    ) = launch {
        block().handleResult(onLoading, onSuccess, onError)
    }
}

/**
 * Base ViewModel for screens that don't need one-time events
 */
abstract class BaseStateViewModel<STATE>(
    initialState: STATE
) : BaseViewModel<STATE, Nothing>(initialState)
