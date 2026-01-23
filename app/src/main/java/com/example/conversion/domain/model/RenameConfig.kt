package com.example.conversion.domain.model

/**
 * Configuration for batch rename operations.
 * Defines how files should be renamed during batch processing.
 *
 * @property prefix The prefix to add to all renamed files
 * @property startNumber The starting number for sequential numbering
 * @property digitCount The number of digits for padding (e.g., 3 for "001")
 * @property preserveExtension If true, keep the original file extension
 * @property sortStrategy The strategy used to sort files before renaming
 */
data class RenameConfig(
    val prefix: String,
    val startNumber: Int = 1,
    val digitCount: Int = 3,
    val preserveExtension: Boolean = true,
    val sortStrategy: SortStrategy = SortStrategy.NATURAL
) {
    /**
     * Validates the rename configuration.
     * @return true if the configuration is valid, false otherwise
     */
    fun isValid(): Boolean {
        return prefix.isNotBlank() &&
                startNumber >= 0 &&
                digitCount in 1..10 &&
                !containsIllegalCharacters(prefix)
    }

    /**
     * Checks if the prefix contains illegal filename characters.
     * Windows illegal characters: < > : " / \ | ? *
     */
    private fun containsIllegalCharacters(str: String): Boolean {
        val illegalChars = setOf('<', '>', ':', '"', '/', '\\', '|', '?', '*')
        return str.any { it in illegalChars }
    }

    /**
     * Returns a validation error message if the config is invalid.
     */
    fun getValidationError(): String? {
        return when {
            prefix.isBlank() -> "Prefix cannot be empty"
            containsIllegalCharacters(prefix) -> "Prefix contains illegal characters (< > : \" / \\ | ? *)"
            startNumber < 0 -> "Start number must be non-negative"
            digitCount !in 1..10 -> "Digit count must be between 1 and 10"
            else -> null
        }
    }
}
