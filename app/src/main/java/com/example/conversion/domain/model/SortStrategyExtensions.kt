package com.example.conversion.domain.model

/**
 * Extension functions for SortStrategy to provide UI-friendly text.
 */

/**
 * Converts SortStrategy to a human-readable display name.
 * Used in UI components to show the strategy name to users.
 *
 * @return User-friendly display name
 */
fun SortStrategy.toDisplayName(): String {
    return when (this) {
        SortStrategy.NATURAL -> "Natural (IMG_1, IMG_2, IMG_10)"
        SortStrategy.DATE_MODIFIED -> "Date Modified"
        SortStrategy.SIZE -> "File Size"
        SortStrategy.ORIGINAL_ORDER -> "Original Selection Order"
    }
}

/**
 * Converts SortStrategy to a detailed description.
 * Used to explain what each strategy does to the user.
 *
 * @return User-friendly description
 */
fun SortStrategy.toDescription(): String {
    return when (this) {
        SortStrategy.NATURAL -> "Smart number sorting"
        SortStrategy.DATE_MODIFIED -> "Newest to oldest"
        SortStrategy.SIZE -> "Largest to smallest"
        SortStrategy.ORIGINAL_ORDER -> "Keep selection order"
    }
}

/**
 * Converts SortStrategy to a short example.
 * Shows a concrete example of how this strategy sorts files.
 *
 * @return Example text demonstrating the sort order
 */
fun SortStrategy.toExample(): String {
    return when (this) {
        SortStrategy.NATURAL -> "file1, file2, file10 (not file1, file10, file2)"
        SortStrategy.DATE_MODIFIED -> "Most recent files first"
        SortStrategy.SIZE -> "Biggest files first"
        SortStrategy.ORIGINAL_ORDER -> "Same order as you selected them"
    }
}
