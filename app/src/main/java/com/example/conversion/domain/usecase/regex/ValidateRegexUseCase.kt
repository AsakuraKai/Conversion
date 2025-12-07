package com.example.conversion.domain.usecase.regex

import com.example.conversion.domain.model.RegexRule
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Use case for validating regex patterns before use.
 *
 * Tests regex pattern validity and provides detailed error messages
 * for common regex syntax issues. Helps prevent runtime errors when
 * applying regex transformations.
 *
 * Input: RegexRule (the rule to validate)
 * Output: ValidationResult (validity and error details)
 */
class ValidateRegexUseCase @Inject constructor() : 
    BaseUseCase<RegexRule, ValidateRegexUseCase.ValidationResult>(Dispatchers.Default) {
    
    /**
     * Result of regex validation with detailed error information.
     *
     * @property isValid True if the regex pattern is valid
     * @property errorMessage Human-readable error message if invalid
     * @property errorType Category of error for programmatic handling
     * @property suggestion Suggested fix for the error (optional)
     */
    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null,
        val errorType: ErrorType? = null,
        val suggestion: String? = null
    ) {
        enum class ErrorType {
            EMPTY_PATTERN,
            INVALID_SYNTAX,
            UNCLOSED_GROUP,
            INVALID_ESCAPE,
            INVALID_QUANTIFIER,
            UNKNOWN
        }
        
        companion object {
            fun valid() = ValidationResult(isValid = true)
            
            fun invalid(
                message: String, 
                errorType: ErrorType = ErrorType.UNKNOWN,
                suggestion: String? = null
            ) = ValidationResult(
                isValid = false, 
                errorMessage = message, 
                errorType = errorType,
                suggestion = suggestion
            )
        }
    }
    
    override suspend fun execute(params: RegexRule): com.example.conversion.domain.model.Result<ValidationResult> {
        return try {
            // Check for empty pattern
            if (params.pattern.isEmpty()) {
                return com.example.conversion.domain.model.Result.Success(
                    ValidationResult.invalid(
                        message = "Pattern cannot be empty",
                        errorType = ValidationResult.ErrorType.EMPTY_PATTERN,
                        suggestion = "Enter a valid regex pattern or use a preset"
                    )
                )
            }
            
            // Attempt to compile the regex pattern
            try {
                val regex = params.pattern.toRegex(params.buildRegexOptions())
                
                // Test the pattern with a sample string to ensure it works
                "test_string_123".replace(regex, params.replacement)
                
                // Pattern is valid
                com.example.conversion.domain.model.Result.Success(ValidationResult.valid())
                
            } catch (e: Exception) {
                // Parse error details and provide helpful feedback
                val validationResult = parseRegexError(e, params.pattern)
                com.example.conversion.domain.model.Result.Success(validationResult)
            }
            
        } catch (e: Exception) {
            // Unexpected error during validation
            com.example.conversion.domain.model.Result.Error(
                IllegalStateException("Unexpected error during validation: ${e.message}", e)
            )
        }
    }
    
    /**
     * Parses regex compilation errors and provides user-friendly messages.
     */
    private fun parseRegexError(error: Exception, pattern: String): ValidationResult {
        val errorMessage = error.message ?: "Unknown regex error"
        
        return when {
            // Unclosed group/bracket errors
            errorMessage.contains("Unclosed", ignoreCase = true) ||
            errorMessage.contains("Unmatched", ignoreCase = true) -> {
                ValidationResult.invalid(
                    message = "Unclosed parenthesis or bracket in pattern",
                    errorType = ValidationResult.ErrorType.UNCLOSED_GROUP,
                    suggestion = "Check that all '(', '[', and '{' have matching closing characters"
                )
            }
            
            // Invalid escape sequence
            errorMessage.contains("Illegal escape", ignoreCase = true) ||
            errorMessage.contains("Invalid escape", ignoreCase = true) -> {
                ValidationResult.invalid(
                    message = "Invalid escape sequence in pattern",
                    errorType = ValidationResult.ErrorType.INVALID_ESCAPE,
                    suggestion = "Use '\\\\' to match backslash, or check your escape sequences"
                )
            }
            
            // Dangling metacharacter (quantifier issues)
            errorMessage.contains("Dangling", ignoreCase = true) ||
            errorMessage.contains("Nothing to repeat", ignoreCase = true) -> {
                ValidationResult.invalid(
                    message = "Invalid use of quantifier (*, +, ?, {}) in pattern",
                    errorType = ValidationResult.ErrorType.INVALID_QUANTIFIER,
                    suggestion = "Quantifiers must follow a character or group. Use '\\*' to match literal '*'"
                )
            }
            
            // Generic syntax error
            else -> {
                ValidationResult.invalid(
                    message = "Invalid regex syntax: $errorMessage",
                    errorType = ValidationResult.ErrorType.INVALID_SYNTAX,
                    suggestion = "Check your regex pattern or try using a preset"
                )
            }
        }
    }
    
    /**
     * Validates a pattern string directly (convenience method for testing).
     */
    suspend fun validatePattern(pattern: String): com.example.conversion.domain.model.Result<ValidationResult> {
        return execute(RegexRule(pattern = pattern, replacement = ""))
    }
}
