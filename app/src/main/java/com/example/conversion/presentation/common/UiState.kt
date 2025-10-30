package com.example.conversion.presentation.common

/**
 * Common UI state wrapper for handling loading, success, error, and empty states.
 * Provides a consistent way to represent UI states across the app.
 *
 * @param T The type of data being displayed
 */
sealed class UiState<out T> {
    /**
     * Initial state before any data is loaded
     */
    data object Idle : UiState<Nothing>()
    
    /**
     * Loading state while data is being fetched
     */
    data object Loading : UiState<Nothing>()
    
    /**
     * Success state with data
     */
    data class Success<T>(val data: T) : UiState<T>()
    
    /**
     * Error state with error information
     */
    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : UiState<Nothing>()
    
    /**
     * Empty state when there's no data to display
     */
    data class Empty(val message: String = "No data available") : UiState<Nothing>()
    
    /**
     * Returns true if the state is Loading
     */
    val isLoading: Boolean
        get() = this is Loading
    
    /**
     * Returns true if the state is Success
     */
    val isSuccess: Boolean
        get() = this is Success
    
    /**
     * Returns true if the state is Error
     */
    val isError: Boolean
        get() = this is Error
    
    /**
     * Returns the data if Success, null otherwise
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
}

/**
 * Helper to convert Result to UiState
 */
fun <T> com.example.conversion.domain.common.Result<T>.toUiState(): UiState<T> = when (this) {
    is com.example.conversion.domain.common.Result.Loading -> UiState.Loading
    is com.example.conversion.domain.common.Result.Success -> {
        if (data is Collection<*> && (data as Collection<*>).isEmpty()) {
            UiState.Empty()
        } else {
            UiState.Success(data)
        }
    }
    is com.example.conversion.domain.common.Result.Error -> UiState.Error(
        message = message ?: "An error occurred",
        throwable = exception
    )
}
