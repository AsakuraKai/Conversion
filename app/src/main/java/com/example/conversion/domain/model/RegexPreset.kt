package com.example.conversion.domain.model

/**
 * Common regex patterns for filename transformations.
 *
 * Provides pre-configured regex rules for frequent naming conventions
 * and text transformations.
 */
enum class RegexPreset(
    val displayName: String,
    val description: String,
    val pattern: String,
    val replacement: String,
    val flags: Set<RegexFlag> = emptySet()
) {
    /** Remove all spaces from filename */
    REMOVE_SPACES(
        displayName = "Remove Spaces",
        description = "Remove all whitespace characters",
        pattern = "\\s+",
        replacement = ""
    ),
    
    /** Replace spaces with underscores */
    SPACES_TO_UNDERSCORES(
        displayName = "Spaces to Underscores",
        description = "Replace all spaces with underscores",
        pattern = "\\s+",
        replacement = "_"
    ),
    
    /** Replace spaces with hyphens */
    SPACES_TO_HYPHENS(
        displayName = "Spaces to Hyphens",
        description = "Replace all spaces with hyphens",
        pattern = "\\s+",
        replacement = "-"
    ),
    
    /** Convert to snake_case (simplified) */
    SNAKE_CASE(
        displayName = "Snake Case",
        description = "Convert to lowercase with underscores",
        pattern = "[\\s-]+",
        replacement = "_"
    ),
    
    /** Convert to kebab-case (simplified) */
    KEBAB_CASE(
        displayName = "Kebab Case",
        description = "Convert to lowercase with hyphens",
        pattern = "[\\s_]+",
        replacement = "-"
    ),
    
    /** Remove special characters (keep alphanumeric, spaces, dots, underscores, hyphens) */
    REMOVE_SPECIAL_CHARS(
        displayName = "Remove Special Characters",
        description = "Keep only alphanumeric, spaces, dots, underscores, and hyphens",
        pattern = "[^a-zA-Z0-9\\s._-]+",
        replacement = ""
    ),
    
    /** Remove numbers */
    REMOVE_NUMBERS(
        displayName = "Remove Numbers",
        description = "Remove all numeric digits",
        pattern = "\\d+",
        replacement = ""
    ),
    
    /** Remove parentheses and their content */
    REMOVE_PARENTHESES(
        displayName = "Remove Parentheses",
        description = "Remove text in parentheses including parentheses",
        pattern = "\\([^)]*\\)",
        replacement = ""
    ),
    
    /** Remove brackets and their content */
    REMOVE_BRACKETS(
        displayName = "Remove Brackets",
        description = "Remove text in square brackets including brackets",
        pattern = "\\[[^]]*\\]",
        replacement = ""
    ),
    
    /** Trim leading/trailing spaces */
    TRIM_SPACES(
        displayName = "Trim Spaces",
        description = "Remove leading and trailing whitespace",
        pattern = "^\\s+|\\s+$",
        replacement = ""
    ),
    
    /** Collapse multiple spaces to single space */
    COLLAPSE_SPACES(
        displayName = "Collapse Spaces",
        description = "Replace multiple consecutive spaces with single space",
        pattern = "\\s{2,}",
        replacement = " "
    ),
    
    /** Remove leading zeros from numbers */
    REMOVE_LEADING_ZEROS(
        displayName = "Remove Leading Zeros",
        description = "Remove zeros at the start of numbers",
        pattern = "\\b0+(\\d+)",
        replacement = "$1"
    ),
    
    /** Convert to uppercase (letter-by-letter replacement) */
    TO_UPPERCASE(
        displayName = "To Uppercase",
        description = "Convert all letters to uppercase",
        pattern = "[a-z]",
        replacement = "" // Note: Actual uppercase conversion requires custom logic
    ),
    
    /** Convert to lowercase (letter-by-letter replacement) */
    TO_LOWERCASE(
        displayName = "To Lowercase",
        description = "Convert all letters to lowercase",
        pattern = "[A-Z]",
        replacement = "" // Note: Actual lowercase conversion requires custom logic
    );
    
    /**
     * Converts this preset to a [RegexRule].
     */
    fun toRegexRule(): RegexRule {
        return RegexRule(
            pattern = pattern,
            replacement = replacement,
            flags = flags
        )
    }
    
    companion object {
        /**
         * Get preset by display name (case-insensitive).
         *
         * @param name The display name to search for
         * @return The matching preset or null if not found
         */
        fun fromDisplayName(name: String): RegexPreset? {
            return values().find { 
                it.displayName.equals(name, ignoreCase = true) 
            }
        }
        
        /**
         * Get all presets as a list of RegexRules.
         */
        fun allRules(): List<RegexRule> {
            return values().map { it.toRegexRule() }
        }
        
        /**
         * Get presets filtered by category.
         */
        fun getSpacingPresets(): List<RegexPreset> {
            return listOf(
                REMOVE_SPACES,
                SPACES_TO_UNDERSCORES,
                SPACES_TO_HYPHENS,
                TRIM_SPACES,
                COLLAPSE_SPACES
            )
        }
        
        fun getCasePresets(): List<RegexPreset> {
            return listOf(
                SNAKE_CASE,
                KEBAB_CASE,
                TO_UPPERCASE,
                TO_LOWERCASE
            )
        }
        
        fun getCleanupPresets(): List<RegexPreset> {
            return listOf(
                REMOVE_SPECIAL_CHARS,
                REMOVE_NUMBERS,
                REMOVE_PARENTHESES,
                REMOVE_BRACKETS,
                REMOVE_LEADING_ZEROS
            )
        }
    }
}
