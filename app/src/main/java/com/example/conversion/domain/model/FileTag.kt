package com.example.conversion.domain.model

/**
 * Represents a tag that can be applied to files for organization and categorization.
 *
 * @property id Unique identifier for the tag
 * @property name Display name of the tag
 * @property color Hex color code for visual identification (e.g., "#FF5722")
 * @property createdAt Timestamp when the tag was created (milliseconds since epoch)
 */
data class FileTag(
    val id: String,
    val name: String,
    val color: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Validates the tag.
     * @return true if the tag is valid, false otherwise
     */
    fun isValid(): Boolean {
        return name.isNotBlank() &&
                name.length <= MAX_NAME_LENGTH &&
                color.matches(COLOR_PATTERN)
    }

    companion object {
        /**
         * Maximum length for tag name.
         */
        const val MAX_NAME_LENGTH = 50

        /**
         * Regex pattern for valid hex color codes.
         */
        private val COLOR_PATTERN = Regex("^#[0-9A-Fa-f]{6}$")

        /**
         * Predefined colors for quick tag creation.
         */
        val PREDEFINED_COLORS = listOf(
            "#F44336", // Red
            "#E91E63", // Pink
            "#9C27B0", // Purple
            "#673AB7", // Deep Purple
            "#3F51B5", // Indigo
            "#2196F3", // Blue
            "#03A9F4", // Light Blue
            "#00BCD4", // Cyan
            "#009688", // Teal
            "#4CAF50", // Green
            "#8BC34A", // Light Green
            "#CDDC39", // Lime
            "#FFEB3B", // Yellow
            "#FFC107", // Amber
            "#FF9800", // Orange
            "#FF5722", // Deep Orange
        )
    }
}
