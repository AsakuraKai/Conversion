package com.example.conversion.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Utility object for performance optimization helpers.
 * Provides lazy processing, flow optimization, and chunking utilities.
 */
object PerformanceUtils {

    /**
     * Process items lazily using sequences to avoid loading all items in memory.
     * Best for large collections where not all items need immediate processing.
     *
     * @param items Collection to process
     * @param transform Transformation function
     * @return Lazily evaluated sequence
     */
    fun <T, R> processLazy(items: List<T>, transform: (T) -> R): Sequence<R> {
        return items.asSequence().map(transform)
    }

    /**
     * Process items in chunks to optimize memory usage for batch operations.
     *
     * @param items Collection to process
     * @param chunkSize Size of each chunk (default: 50)
     * @param transform Transformation function applied to each chunk
     * @return List of processed chunks
     */
    fun <T, R> processInChunks(
        items: List<T>,
        chunkSize: Int = 50,
        transform: (List<T>) -> R
    ): List<R> {
        return items.chunked(chunkSize).map(transform)
    }

    /**
     * Create a debounced flow that only emits after a period of inactivity.
     * Useful for search inputs, text changes, etc.
     *
     * @param timeoutMillis Debounce timeout in milliseconds (default: 300ms)
     * @return Debounced flow
     */
    fun <T> Flow<T>.optimizeDebounce(timeoutMillis: Long = 300L): Flow<T> {
        return this.debounce(timeoutMillis)
    }

    /**
     * Create a conflated flow that only keeps the latest value.
     * Useful for reducing backpressure when only the latest state matters.
     *
     * @return Conflated flow
     */
    fun <T> Flow<T>.optimizeConflate(): Flow<T> {
        return this.conflate()
    }

    /**
     * Paginate a list into pages for efficient loading.
     *
     * @param pageSize Number of items per page
     * @param page Page number (0-indexed)
     * @return Sublist for the requested page
     */
    fun <T> List<T>.paginate(pageSize: Int = 100, page: Int = 0): List<T> {
        val startIndex = page * pageSize
        if (startIndex >= size) return emptyList()
        val endIndex = minOf(startIndex + pageSize, size)
        return subList(startIndex, endIndex)
    }

    /**
     * Create a paged flow that emits items in batches.
     *
     * @param items Source list
     * @param pageSize Items per page
     * @return Flow emitting pages of items
     */
    fun <T> createPagedFlow(items: List<T>, pageSize: Int = 100): Flow<List<T>> = flow {
        var page = 0
        while (true) {
            val chunk = items.paginate(pageSize, page)
            if (chunk.isEmpty()) break
            emit(chunk)
            page++
        }
    }

    /**
     * Measure execution time of a block.
     * Useful for identifying performance bottlenecks.
     *
     * @param block Code block to measure
     * @return Pair of result and elapsed time in milliseconds
     */
    inline fun <T> measureTime(block: () -> T): Pair<T, Long> {
        val startTime = System.currentTimeMillis()
        val result = block()
        val elapsed = System.currentTimeMillis() - startTime
        return result to elapsed
    }

    /**
     * Estimate memory usage of a string.
     *
     * @return Approximate memory in bytes
     */
    fun String.estimateMemory(): Long {
        return (length * 2).toLong() // Approximate: each char is 2 bytes
    }

    /**
     * Estimate memory usage of a list.
     *
     * @return Approximate memory in bytes
     */
    fun <T> List<T>.estimateMemory(): Long {
        return size * 16L // Rough estimate: 16 bytes overhead per object
    }
}
