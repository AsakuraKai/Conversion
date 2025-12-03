package com.example.conversion.domain.usecase.rename

import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Use case for validating a filename against system constraints.
 * Checks for illegal characters, length limits, and reserved names.
 *
 * Input: String (filename to validate)
 * Output: ValidationResult (isValid, errorMessage)
 */
class ValidateFilenameUseCase @Inject constructor() : BaseUseCase<String, ValidateFilenameUseCase.ValidationResult>(Dispatchers.Default) {

    /**
     * Result of filename validation.
     *
     * @property isValid True if the filename is valid
     * @property errorMessage Error message if invalid, null otherwise
     */
    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    ) {
        companion object {
            fun valid() = ValidationResult(isValid = true, errorMessage = null)
            fun invalid(message: String) = ValidationResult(isValid = false, errorMessage = message)
        }
    }

    companion object {
        // Windows illegal characters
        private val ILLEGAL_CHARACTERS = setOf('<', '>', ':', '"', '/', '\\', '|', '?', '*')

        // Windows reserved names
        private val RESERVED_NAMES = setOf(
            "CON", "PRN", "AUX", "NUL",
            "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
            "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"
        )

        // Maximum filename length (Windows: 255 characters)
        private const val MAX_FILENAME_LENGTH = 255

        // Minimum filename length
        private const val MIN_FILENAME_LENGTH = 1
    }

    override suspend fun execute(params: String): ValidationResult {
        val filename = params.trim()

        // Check if filename is empty
        if (filename.isEmpty() || filename.length < MIN_FILENAME_LENGTH) {
            return ValidationResult.invalid("Filename cannot be empty")
        }

        // Check if filename is too long
        if (filename.length > MAX_FILENAME_LENGTH) {
            return ValidationResult.invalid("Filename is too long (max $MAX_FILENAME_LENGTH characters)")
        }

        // Check for illegal characters
        val illegalChar = filename.firstOrNull { it in ILLEGAL_CHARACTERS }
        if (illegalChar != null) {
            return ValidationResult.invalid("Filename contains illegal character: '$illegalChar'")
        }

        // Check for control characters (ASCII 0-31)
        val controlChar = filename.firstOrNull { it.code in 0..31 }
        if (controlChar != null) {
            return ValidationResult.invalid("Filename contains control character")
        }

        // Check if filename ends with space or period (not allowed on Windows)
        if (filename.endsWith(' ') || filename.endsWith('.')) {
            return ValidationResult.invalid("Filename cannot end with space or period")
        }

        // Check for reserved names (Windows)
        val nameWithoutExtension = filename.substringBeforeLast('.', filename).uppercase()
        if (nameWithoutExtension in RESERVED_NAMES) {
            return ValidationResult.invalid("'$nameWithoutExtension' is a reserved filename")
        }

        // Check if filename consists only of dots
        if (filename.all { it == '.' }) {
            return ValidationResult.invalid("Filename cannot consist only of dots")
        }

        return ValidationResult.valid()
    }
}
