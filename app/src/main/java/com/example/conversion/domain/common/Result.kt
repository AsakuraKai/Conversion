package com.example.conversion.domain.common

/**
 * A generic wrapper for handling success and error states in a type-safe way.
 * Used throughout the domain and data layers to represent operation results.
 *
 * @param T The type of data returned on success
 */
sealed class Result<out T> {
    /**
     * Represents a successful operation with data
     */
    data class Success<T>(val data: T) : Result<T>()
    
    /**
     * Represents a failed operation with error information
     */
    data class Error(
        val exception: Throwable,
        val message: String? = exception.message
    ) : Result<Nothing>()
    
    /**
     * Represents an operation in progress
     */
    data object Loading : Result<Nothing>()
    
    /**
     * Returns true if the result is Success
     */
    val isSuccess: Boolean
        get() = this is Success
    
    /**
     * Returns true if the result is Error
     */
    val isError: Boolean
        get() = this is Error
    
    /**
     * Returns true if the result is Loading
     */
    val isLoading: Boolean
        get() = this is Loading
    
    /**
     * Returns the data if Success, null otherwise
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    /**
     * Returns the data if Success, or throws the exception if Error
     */
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception
        is Loading -> throw IllegalStateException("Cannot get data from Loading state")
    }
    
    /**
     * Maps the success value using the provided transform function
     */
    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> this
    }
    
    /**
     * Performs an action if the result is Success
     */
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }
    
    /**
     * Performs an action if the result is Error
     */
    inline fun onError(action: (Throwable) -> Unit): Result<T> {
        if (this is Error) action(exception)
        return this
    }
    
    /**
     * Performs an action if the result is Loading
     */
    inline fun onLoading(action: () -> Unit): Result<T> {
        if (this is Loading) action()
        return this
    }
}

/**
 * Helper function to wrap a suspending operation in a Result
 */
suspend inline fun <T> resultOf(block: suspend () -> T): Result<T> = try {
    Result.Success(block())
} catch (e: Exception) {
    Result.Error(e)
}

/**
 * Helper function to wrap a regular operation in a Result
 */
inline fun <T> resultOfCatching(block: () -> T): Result<T> = try {
    Result.Success(block())
} catch (e: Exception) {
    Result.Error(e)
}
