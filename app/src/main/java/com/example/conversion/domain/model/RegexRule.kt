package com.example.conversion.domain.model

/**
 * Represents a regex transformation rule for filename manipulation.
 *
 * @property pattern The regex pattern to match in the filename
 * @property replacement The string to replace matched patterns with
 * @property flags Set of regex options to modify pattern matching behavior
 */
data class RegexRule(
    val pattern: String,
    val replacement: String,
    val flags: Set<RegexFlag> = emptySet()
) {
    /**
     * Validates the regex rule.
     *
     * @return Result indicating success or validation errors
     */
    fun validate(): com.example.conversion.domain.common.Result<Unit> {
        return try {
            // Test pattern compilation
            pattern.toRegex(buildRegexOptions())
            
            if (pattern.isEmpty()) {
                return com.example.conversion.domain.common.Result.Error(IllegalArgumentException("Pattern cannot be empty"))
            }
            
            com.example.conversion.domain.common.Result.Success(Unit)
        } catch (e: Exception) {
            com.example.conversion.domain.common.Result.Error(IllegalArgumentException("Invalid regex pattern: ${e.message}", e))
        }
    }
    
    /**
     * Converts RegexFlags to RegexOptions for Kotlin Regex.
     */
    internal fun buildRegexOptions(): Set<RegexOption> {
        return flags.mapNotNull { flag ->
            when (flag) {
                RegexFlag.IGNORE_CASE -> RegexOption.IGNORE_CASE
                RegexFlag.MULTILINE -> RegexOption.MULTILINE
                RegexFlag.DOT_MATCHES_ALL -> RegexOption.DOT_MATCHES_ALL
                RegexFlag.LITERAL -> RegexOption.LITERAL
            }
        }.toSet()
    }
}

/**
 * Flags to modify regex pattern matching behavior.
 */
enum class RegexFlag {
    /** Case-insensitive matching */
    IGNORE_CASE,
    
    /** ^ and $ match line boundaries in addition to string boundaries */
    MULTILINE,
    
    /** . matches any character including newline */
    DOT_MATCHES_ALL,
    
    /** Treat pattern as literal string (escape special characters) */
    LITERAL
}
