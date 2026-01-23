package com.example.conversion.domain.model

/**
 * Defines the strategy for sorting files before batch rename.
 */
enum class SortStrategy {
    /**
     * Natural sort order (handles numbers in filenames correctly).
     * Example: file1.jpg, file2.jpg, file10.jpg (not file1.jpg, file10.jpg, file2.jpg)
     */
    NATURAL,

    /**
     * Sort by date modified (newest first)
     */
    DATE_MODIFIED,

    /**
     * Sort by file size (largest first)
     */
    SIZE,

    /**
     * Keep the original order (no sorting)
     */
    ORIGINAL_ORDER
}
