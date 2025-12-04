package com.example.conversion.domain.usecase.sort

import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.model.SortStrategy
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Use case for sorting files based on different strategies.
 * 
 * Supports multiple sorting strategies:
 * - NATURAL: Alphanumeric sort that handles numbers correctly
 * - DATE_MODIFIED: Sort by modification date (newest first)
 * - SIZE: Sort by file size (largest first)
 * - ORIGINAL_ORDER: Preserve the original order (no sorting)
 * 
 * Example usage:
 * ```
 * val sortedFiles = sortFilesUseCase(
 *     SortFilesParams(files, SortStrategy.NATURAL)
 * )
 * ```
 */
class SortFilesUseCase @Inject constructor(
    dispatcher: CoroutineDispatcher
) : BaseUseCase<SortFilesUseCase.Params, List<FileItem>>(dispatcher) {

    /**
     * Parameters for sorting files.
     * 
     * @property files List of files to sort
     * @property strategy Sorting strategy to apply
     */
    data class Params(
        val files: List<FileItem>,
        val strategy: SortStrategy
    )

    override suspend fun execute(params: Params): List<FileItem> {
        if (params.files.isEmpty()) {
            return emptyList()
        }

        return when (params.strategy) {
            SortStrategy.NATURAL -> sortNatural(params.files)
            SortStrategy.DATE_MODIFIED -> sortByDateModified(params.files)
            SortStrategy.SIZE -> sortBySize(params.files)
            SortStrategy.ORIGINAL_ORDER -> params.files
        }
    }

    /**
     * Natural sort that handles numbers in filenames correctly.
     * 
     * Example: file1.jpg, file2.jpg, file10.jpg (not file1.jpg, file10.jpg, file2.jpg)
     * 
     * Algorithm:
     * 1. Split filename into text and numeric chunks
     * 2. Compare chunks pairwise
     * 3. For numeric chunks, compare as numbers
     * 4. For text chunks, compare alphabetically
     */
    private fun sortNatural(files: List<FileItem>): List<FileItem> {
        return files.sortedWith(naturalComparator)
    }

    /**
     * Sort by date modified (newest first).
     */
    private fun sortByDateModified(files: List<FileItem>): List<FileItem> {
        return files.sortedByDescending { it.dateModified }
    }

    /**
     * Sort by file size (largest first).
     */
    private fun sortBySize(files: List<FileItem>): List<FileItem> {
        return files.sortedByDescending { it.size }
    }

    companion object {
        /**
         * Natural order comparator for FileItem.
         * Compares filenames using natural sort algorithm.
         */
        private val naturalComparator = Comparator<FileItem> { a, b ->
            compareNatural(a.name, b.name)
        }

        /**
         * Compares two strings using natural sort algorithm.
         * 
         * Splits strings into chunks of digits and non-digits, then compares:
         * - Numeric chunks are compared as numbers
         * - Text chunks are compared alphabetically (case-insensitive)
         * 
         * Examples:
         * - "file1" < "file2" < "file10" (not lexicographic "file1" < "file10" < "file2")
         * - "img001" < "img002" < "img100"
         * - "photo_10" < "photo_20" < "photo_100"
         * 
         * @param str1 First string to compare
         * @param str2 Second string to compare
         * @return Negative if str1 < str2, positive if str1 > str2, zero if equal
         */
        private fun compareNatural(str1: String, str2: String): Int {
            val chunks1 = splitIntoChunks(str1)
            val chunks2 = splitIntoChunks(str2)

            val minSize = minOf(chunks1.size, chunks2.size)

            for (i in 0 until minSize) {
                val chunk1 = chunks1[i]
                val chunk2 = chunks2[i]

                val result = when {
                    // Both chunks are numeric - compare as numbers
                    chunk1.isNumeric() && chunk2.isNumeric() -> {
                        val num1 = chunk1.toLongOrNull() ?: 0L
                        val num2 = chunk2.toLongOrNull() ?: 0L
                        num1.compareTo(num2)
                    }
                    // One is numeric, one is not - numeric comes first
                    chunk1.isNumeric() && !chunk2.isNumeric() -> -1
                    !chunk1.isNumeric() && chunk2.isNumeric() -> 1
                    // Both are text - compare alphabetically (case-insensitive)
                    else -> chunk1.compareTo(chunk2, ignoreCase = true)
                }

                if (result != 0) {
                    return result
                }
            }

            // All compared chunks are equal, shorter string comes first
            return chunks1.size.compareTo(chunks2.size)
        }

        /**
         * Splits a string into chunks of consecutive digits and non-digits.
         * 
         * Examples:
         * - "file123abc456" -> ["file", "123", "abc", "456"]
         * - "img001" -> ["img", "001"]
         * - "photo_10_final" -> ["photo_", "10", "_final"]
         */
        private fun splitIntoChunks(str: String): List<String> {
            if (str.isEmpty()) return emptyList()

            val chunks = mutableListOf<String>()
            val currentChunk = StringBuilder()
            var isDigit = str[0].isDigit()

            for (char in str) {
                val charIsDigit = char.isDigit()

                if (charIsDigit == isDigit) {
                    // Same type, add to current chunk
                    currentChunk.append(char)
                } else {
                    // Type changed, save current chunk and start new one
                    if (currentChunk.isNotEmpty()) {
                        chunks.add(currentChunk.toString())
                        currentChunk.clear()
                    }
                    currentChunk.append(char)
                    isDigit = charIsDigit
                }
            }

            // Add the last chunk
            if (currentChunk.isNotEmpty()) {
                chunks.add(currentChunk.toString())
            }

            return chunks
        }

        /**
         * Checks if a string contains only digits.
         */
        private fun String.isNumeric(): Boolean {
            return this.isNotEmpty() && this.all { it.isDigit() }
        }
    }
}
