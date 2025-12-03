package com.example.conversion.data.manager

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for file operations related to batch rename.
 * Provides validation, conflict detection, and safe name generation.
 */
@Singleton
class FileOperationsManager @Inject constructor() {

    companion object {
        // Windows illegal characters
        private val ILLEGAL_CHARACTERS = setOf('<', '>', ':', '"', '/', '\\', '|', '?', '*')

        // Windows reserved names
        private val RESERVED_NAMES = setOf(
            "CON", "PRN", "AUX", "NUL",
            "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
            "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"
        )

        private const val MAX_FILENAME_LENGTH = 255
    }

    /**
     * Validates if a filename is safe to use.
     *
     * @param name The filename to validate
     * @return True if the filename is valid, false otherwise
     */
    fun validateFilename(name: String): Boolean {
        if (name.isBlank() || name.length > MAX_FILENAME_LENGTH) {
            return false
        }

        // Check for illegal characters
        if (name.any { it in ILLEGAL_CHARACTERS }) {
            return false
        }

        // Check for control characters
        if (name.any { it.code in 0..31 }) {
            return false
        }

        // Check if ends with space or period
        if (name.endsWith(' ') || name.endsWith('.')) {
            return false
        }

        // Check for reserved names
        val nameWithoutExtension = name.substringBeforeLast('.', name).uppercase()
        if (nameWithoutExtension in RESERVED_NAMES) {
            return false
        }

        // Check if consists only of dots
        if (name.all { it == '.' }) {
            return false
        }

        return true
    }

    /**
     * Detects duplicate filenames in a list.
     *
     * @param names List of filenames to check
     * @return List of filenames that appear more than once
     */
    fun detectConflicts(names: List<String>): List<String> {
        val nameCount = mutableMapOf<String, Int>()
        
        names.forEach { name ->
            nameCount[name] = nameCount.getOrDefault(name, 0) + 1
        }
        
        return nameCount.filter { it.value > 1 }.keys.toList()
    }

    /**
     * Generates a safe filename by appending an index if there are conflicts.
     *
     * @param name The base filename
     * @param index The index to append (if needed)
     * @return A safe filename that avoids conflicts
     */
    fun generateSafeName(name: String, index: Int): String {
        if (index == 0) {
            return name
        }

        val nameWithoutExtension = name.substringBeforeLast('.', name)
        val extension = if (name.contains('.')) {
            ".${name.substringAfterLast('.')}"
        } else {
            ""
        }

        return "${nameWithoutExtension}_$index$extension"
    }

    /**
     * Sanitizes a filename by removing or replacing illegal characters.
     *
     * @param name The filename to sanitize
     * @param replacement The character to replace illegal characters with
     * @return A sanitized filename
     */
    fun sanitizeFilename(name: String, replacement: Char = '_'): String {
        var sanitized = name.map { char ->
            when {
                char in ILLEGAL_CHARACTERS -> replacement
                char.code in 0..31 -> replacement
                else -> char
            }
        }.joinToString("")

        // Remove trailing spaces or periods
        sanitized = sanitized.trimEnd(' ', '.')

        // Handle reserved names
        val nameWithoutExtension = sanitized.substringBeforeLast('.', sanitized)
        if (nameWithoutExtension.uppercase() in RESERVED_NAMES) {
            val extension = if (sanitized.contains('.')) {
                ".${sanitized.substringAfterLast('.')}"
            } else {
                ""
            }
            sanitized = "${nameWithoutExtension}_renamed$extension"
        }

        // Ensure name is not empty
        if (sanitized.isBlank()) {
            sanitized = "file"
        }

        // Truncate if too long
        if (sanitized.length > MAX_FILENAME_LENGTH) {
            val extension = if (sanitized.contains('.')) {
                ".${sanitized.substringAfterLast('.')}"
            } else {
                ""
            }
            val maxBaseLength = MAX_FILENAME_LENGTH - extension.length
            sanitized = sanitized.substringBeforeLast('.').take(maxBaseLength) + extension
        }

        return sanitized
    }

    /**
     * Checks if two filenames would conflict (case-insensitive comparison).
     * Windows file system is case-insensitive.
     *
     * @param name1 First filename
     * @param name2 Second filename
     * @return True if the filenames conflict
     */
    fun wouldConflict(name1: String, name2: String): Boolean {
        return name1.equals(name2, ignoreCase = true)
    }

    /**
     * Generates a batch of safe filenames, resolving any conflicts automatically.
     *
     * @param baseNames List of base filenames
     * @return List of safe filenames with conflicts resolved
     */
    fun generateSafeBatch(baseNames: List<String>): List<String> {
        val result = mutableListOf<String>()
        val usedNames = mutableSetOf<String>()

        baseNames.forEach { baseName ->
            var safeName = baseName
            var index = 0

            // Find a unique name
            while (usedNames.any { wouldConflict(it, safeName) }) {
                index++
                safeName = generateSafeName(baseName, index)
            }

            result.add(safeName)
            usedNames.add(safeName.lowercase())
        }

        return result
    }
}
