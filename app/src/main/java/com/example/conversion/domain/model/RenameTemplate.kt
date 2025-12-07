package com.example.conversion.domain.model

/**
 * Represents a saved rename template/preset.
 * Templates allow users to save and reuse rename configurations.
 *
 * @property id Unique identifier for the template
 * @property name Display name for the template
 * @property pattern User-friendly description of the pattern (e.g., "Photo_001")
 * @property config The rename configuration this template uses
 * @property isFavorite Whether this template is marked as favorite
 * @property createdAt Timestamp when the template was created (milliseconds since epoch)
 * @property lastUsedAt Timestamp when the template was last used (null if never used)
 */
data class RenameTemplate(
    val id: String,
    val name: String,
    val pattern: String,
    val config: RenameConfig,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUsedAt: Long? = null
) {
    /**
     * Validates the template.
     * @return true if the template is valid, false otherwise
     */
    fun isValid(): Boolean {
        return name.isNotBlank() &&
                pattern.isNotBlank() &&
                config.isValid()
    }

    /**
     * Creates a copy of this template with updated lastUsedAt timestamp.
     */
    fun markAsUsed(): RenameTemplate {
        return copy(lastUsedAt = System.currentTimeMillis())
    }

    /**
     * Creates a copy of this template with toggled favorite status.
     */
    fun toggleFavorite(): RenameTemplate {
        return copy(isFavorite = !isFavorite)
    }

    companion object {
        /**
         * Maximum length for template name.
         */
        const val MAX_NAME_LENGTH = 50

        /**
         * Maximum length for pattern description.
         */
        const val MAX_PATTERN_LENGTH = 100

        /**
         * Generates a sample pattern preview from the config.
         * Example: "Photo_001.jpg"
         */
        fun generatePatternPreview(config: RenameConfig, extension: String = "jpg"): String {
            val number = config.startNumber.toString().padStart(config.digitCount, '0')
            val ext = if (config.preserveExtension && extension.isNotEmpty()) ".$extension" else ""
            return "${config.prefix}$number$ext"
        }
    }
}
