package com.example.conversion.domain.model

/**
 * User-friendly error messages with recovery suggestions.
 * Provides consistent error messaging across the application.
 */
sealed class UserFriendlyError {
    abstract val message: String
    abstract val recoverySuggestions: List<String>
    abstract val technicalDetails: String?
    
    /**
     * Permission-related errors.
     */
    data class PermissionError(
        override val message: String = "Permission denied",
        val permissionType: String,
        override val recoverySuggestions: List<String> = listOf(
            "Grant the required permission in Settings",
            "Try again after granting permission"
        ),
        override val technicalDetails: String? = null
    ) : UserFriendlyError()
    
    /**
     * File operation errors.
     */
    data class FileOperationError(
        override val message: String = "File operation failed",
        val operation: String,
        override val recoverySuggestions: List<String> = listOf(
            "Check if the file still exists",
            "Ensure you have write permissions",
            "Try freeing up storage space"
        ),
        override val technicalDetails: String? = null
    ) : UserFriendlyError()
    
    /**
     * Network-related errors.
     */
    data class NetworkError(
        override val message: String = "Network error occurred",
        override val recoverySuggestions: List<String> = listOf(
            "Check your internet connection",
            "Try again in a moment",
            "Verify that the service is available"
        ),
        override val technicalDetails: String? = null
    ) : UserFriendlyError()
    
    /**
     * Validation errors.
     */
    data class ValidationError(
        override val message: String,
        val field: String,
        override val recoverySuggestions: List<String> = listOf(
            "Check the input and try again",
            "Ensure all required fields are filled"
        ),
        override val technicalDetails: String? = null
    ) : UserFriendlyError()
    
    /**
     * Storage-related errors.
     */
    data class StorageError(
        override val message: String = "Storage error",
        override val recoverySuggestions: List<String> = listOf(
            "Free up storage space",
            "Check if storage is available",
            "Try saving to a different location"
        ),
        override val technicalDetails: String? = null
    ) : UserFriendlyError()
    
    /**
     * Operation cancelled by user.
     */
    data class CancellationError(
        override val message: String = "Operation cancelled",
        override val recoverySuggestions: List<String> = listOf(
            "Try the operation again if needed"
        ),
        override val technicalDetails: String? = null
    ) : UserFriendlyError()
    
    /**
     * Generic errors with custom messages.
     */
    data class GenericError(
        override val message: String,
        override val recoverySuggestions: List<String> = listOf(
            "Try again",
            "Restart the app if the problem persists"
        ),
        override val technicalDetails: String? = null
    ) : UserFriendlyError()
    
    companion object {
        /**
         * Creates a user-friendly error from an exception.
         */
        fun fromException(exception: Throwable): UserFriendlyError {
            return when (exception) {
                is OperationCancelledException -> CancellationError(
                    message = exception.message ?: "Operation cancelled",
                    technicalDetails = exception.stackTraceToString()
                )
                is SecurityException -> PermissionError(
                    message = "Permission required",
                    permissionType = "Storage",
                    technicalDetails = exception.message
                )
                is java.io.IOException -> FileOperationError(
                    message = "File operation failed: ${exception.message}",
                    operation = "File I/O",
                    technicalDetails = exception.message
                )
                is IllegalArgumentException -> ValidationError(
                    message = exception.message ?: "Invalid input",
                    field = "Input",
                    technicalDetails = exception.message
                )
                else -> GenericError(
                    message = exception.message ?: "An unexpected error occurred",
                    technicalDetails = exception.stackTraceToString()
                )
            }
        }
        
        /**
         * Creates a permission error for a specific permission.
         */
        fun permissionDenied(permissionName: String): UserFriendlyError {
            return PermissionError(
                message = "Permission denied: $permissionName",
                permissionType = permissionName,
                recoverySuggestions = listOf(
                    "Go to Settings → Apps → Files Management → Permissions",
                    "Enable the '$permissionName' permission",
                    "Return to the app and try again"
                )
            )
        }
        
        /**
         * Creates a storage error.
         */
        fun insufficientStorage(): UserFriendlyError {
            return StorageError(
                message = "Insufficient storage space",
                recoverySuggestions = listOf(
                    "Delete unnecessary files to free up space",
                    "Move files to external storage",
                    "Clear app cache in Settings"
                )
            )
        }
        
        /**
         * Creates a network error.
         */
        fun noInternet(): UserFriendlyError {
            return NetworkError(
                message = "No internet connection",
                recoverySuggestions = listOf(
                    "Check your Wi-Fi or mobile data connection",
                    "Try again when connected to the internet"
                )
            )
        }
    }
}

/**
 * Extension function to convert Result.Error to UserFriendlyError.
 */
fun com.example.conversion.domain.common.Result.Error.toUserFriendlyError(): UserFriendlyError {
    return UserFriendlyError.fromException(this.exception)
}
