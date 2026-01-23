package com.example.conversion.performance

import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.model.FileType
import com.example.conversion.util.PerformanceUtils
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

/**
 * Benchmark tests for file operations performance.
 * Tests measure against performance goals:
 * - File selection: 1000+ files without jank
 * - Batch processing: 100 files in < 5s
 * - Memory: Peak < 150MB
 *
 * Note: These are mock benchmarks for development.
 * Production should use Android Benchmark library for accurate measurements.
 */
class FileOperationsBenchmark {

    private lateinit var testFiles: List<FileItem>

    @Before
    fun setup() {
        // Create test data
        testFiles = generateTestFiles(1000)
    }

    @Test
    fun `benchmark file selection with 1000 files`() {
        // Measure time to process 1000 files
        val (result, elapsed) = PerformanceUtils.measureTime {
            testFiles.filter { it.type == FileType.IMAGE }
        }

        println("File selection (1000 files): ${elapsed}ms")
        println("Filtered results: ${result.size} files")

        // Goal: Should complete without significant delay
        assertTrue("File selection took too long: ${elapsed}ms", elapsed < 1000)
    }

    @Test
    fun `benchmark batch processing 100 files`() {
        val files = testFiles.take(100)

        val (result, elapsed) = PerformanceUtils.measureTime {
            files.map { file ->
                // Simulate rename operation
                file.copy(name = "renamed_${file.name}")
            }
        }

        println("Batch processing (100 files): ${elapsed}ms")
        println("Processed files: ${result.size}")

        // Goal: 100 files in < 5s (5000ms)
        assertTrue("Batch processing took too long: ${elapsed}ms", elapsed < 5000)
    }

    @Test
    fun `benchmark lazy sequence processing`() {
        val (result, elapsed) = PerformanceUtils.measureTime {
            PerformanceUtils.processLazy(testFiles) { file ->
                file.copy(name = "processed_${file.name}")
            }.take(100).toList()
        }

        println("Lazy processing (100 of 1000): ${elapsed}ms")

        // Lazy processing should be faster than processing all
        assertTrue("Lazy processing should be fast", elapsed < 500)
    }

    @Test
    fun `benchmark chunked processing`() {
        val (result, elapsed) = PerformanceUtils.measureTime {
            PerformanceUtils.processInChunks(testFiles, chunkSize = 50) { chunk ->
                chunk.map { it.copy(name = "chunk_${it.name}") }
            }
        }

        println("Chunked processing (1000 files, 50 per chunk): ${elapsed}ms")
        println("Total chunks processed: ${result.size}")

        assertTrue("Chunked processing should be efficient", elapsed < 2000)
    }

    @Test
    fun `benchmark pagination`() {
        val (result, elapsed) = PerformanceUtils.measureTime {
            val page1 = testFiles.paginate(pageSize = 100, page = 0)
            val page2 = testFiles.paginate(pageSize = 100, page = 1)
            val page3 = testFiles.paginate(pageSize = 100, page = 2)
            listOf(page1, page2, page3)
        }

        println("Pagination (3 pages of 100): ${elapsed}ms")

        assertTrue("Pagination should be very fast", elapsed < 100)
    }

    @Test
    fun `estimate memory usage for file list`() {
        val memoryEstimate = testFiles.estimateMemory()
        println("Estimated memory for 1000 files: ${memoryEstimate / 1024}KB")

        // Goal: Peak < 150MB (157286400 bytes)
        // For 1000 FileItem objects, should be well under that
        assertTrue("Memory usage too high", memoryEstimate < 157286400)
    }

    @Test
    fun `benchmark filtering with multiple criteria`() {
        val (result, elapsed) = PerformanceUtils.measureTime {
            testFiles
                .asSequence()
                .filter { it.type == FileType.IMAGE }
                .filter { it.size > 1000L }
                .filter { it.name.contains("test") }
                .take(100)
                .toList()
        }

        println("Complex filtering: ${elapsed}ms")
        println("Results: ${result.size} files")

        assertTrue("Complex filtering should be fast", elapsed < 500)
    }

    @Test
    fun `benchmark sorting large list`() {
        val (result, elapsed) = PerformanceUtils.measureTime {
            testFiles.sortedBy { it.name }
        }

        println("Sorting 1000 files: ${elapsed}ms")

        assertTrue("Sorting should be fast", elapsed < 200)
    }

    // Helper function to generate test files
    private fun generateTestFiles(count: Int): List<FileItem> {
        return (1..count).map { i ->
            FileItem(
                uri = "file:///test/file_$i.jpg",
                name = "test_file_$i.jpg",
                path = "/test/file_$i.jpg",
                size = (1000L..100000L).random(),
                type = FileType.IMAGE,
                mimeType = "image/jpeg",
                dateModified = LocalDateTime.now(),
                extension = "jpg"
            )
        }
    }
}
