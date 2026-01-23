package com.example.conversion.domain.model

import kotlinx.serialization.Serializable

/**
 * Serializable preset data for QR code encoding/decoding.
 * Contains all information needed to recreate a RenameTemplate from a QR code.
 *
 * @property version Schema version for compatibility (current: 1)
 * @property templateName Display name for the template
 * @property pattern User-friendly description of the pattern
 * @property prefix The prefix to add to renamed files
 * @property startNumber The starting number for sequential numbering
 * @property digitCount The number of digits for padding
 * @property preserveExtension Whether to keep original file extension
 * @property sortStrategy The sort strategy (NATURAL, DATE_MODIFIED, SIZE, ORIGINAL_ORDER)
 * @property createdAt Timestamp when the original template was created
 */
@Serializable
data class PresetQRData(
    val version: Int = CURRENT_VERSION,
    val templateName: String,
    val pattern: String,
    val prefix: String,
    val startNumber: Int,
    val digitCount: Int,
    val preserveExtension: Boolean,
    val sortStrategy: String,
    val createdAt: Long
) {
    /**
     * Validates the QR data.
     * @return true if the data is valid, false otherwise
     */
    fun isValid(): Boolean {
        return version == CURRENT_VERSION &&
                templateName.isNotBlank() &&
                pattern.isNotBlank() &&
                prefix.isNotBlank() &&
                startNumber >= 0 &&
                digitCount in 1..10 &&
                sortStrategy in VALID_SORT_STRATEGIES &&
                !containsIllegalCharacters(prefix)
    }

    /**
     * Checks if the prefix contains illegal filename characters.
     */
    private fun containsIllegalCharacters(str: String): Boolean {
        val illegalChars = setOf('<', '>', ':', '"', '/', '\\', '|', '?', '*')
        return str.any { it in illegalChars }
    }

    /**
     * Converts this QR data to a RenameTemplate.
     * @return RenameTemplate instance
     */
    fun toRenameTemplate(): RenameTemplate {
        val config = RenameConfig(
            prefix = prefix,
            startNumber = startNumber,
            digitCount = digitCount,
            preserveExtension = preserveExtension,
            sortStrategy = SortStrategy.valueOf(sortStrategy)
        )

        return RenameTemplate(
            id = java.util.UUID.randomUUID().toString(),
            name = templateName,
            pattern = pattern,
            config = config,
            isFavorite = false,
            createdAt = System.currentTimeMillis(),
            lastUsedAt = null
        )
    }

    companion object {
        /**
         * Current schema version.
         */
        const val CURRENT_VERSION = 1

        /**
         * Valid sort strategy names.
         */
        private val VALID_SORT_STRATEGIES = setOf(
            "NATURAL", "DATE_MODIFIED", "SIZE", "ORIGINAL_ORDER"
        )

        /**
         * Creates PresetQRData from a RenameTemplate.
         */
        fun fromRenameTemplate(template: RenameTemplate): PresetQRData {
            return PresetQRData(
                version = CURRENT_VERSION,
                templateName = template.name,
                pattern = template.pattern,
                prefix = template.config.prefix,
                startNumber = template.config.startNumber,
                digitCount = template.config.digitCount,
                preserveExtension = template.config.preserveExtension,
                sortStrategy = template.config.sortStrategy.name,
                createdAt = template.createdAt
            )
        }
    }
}
