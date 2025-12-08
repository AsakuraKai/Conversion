package com.example.conversion.performance

import com.example.conversion.domain.model.RenameTemplate
import com.example.conversion.domain.model.OperationHistory
import com.example.conversion.domain.model.ActivityLog
import com.example.conversion.domain.model.ActivityStatus
import com.example.conversion.util.PerformanceUtils
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

/**
 * Benchmark tests for database query performance.
 * Mock implementation for development.
 *
 * Note: These benchmarks simulate database operations.
 * Production should test against real Room database with indices.
 */
class DatabaseQueryBenchmark {

    private lateinit var testTemplates: List<RenameTemplate>
    private lateinit var testHistory: List<OperationHistory>
    private lateinit var testLogs: List<ActivityLog>

    @Before
    fun setup() {
        testTemplates = generateTestTemplates(500)
        testHistory = generateTestHistory(1000)
        testLogs = generateTestLogs(1000)
    }

    @Test
    fun `benchmark template query by id`() {
        val (result, elapsed) = PerformanceUtils.measureTime {
            testTemplates.find { it.id == 250L }
        }

        println("Template query by ID: ${elapsed}ms")
        assertTrue("Query by ID should be instant", elapsed < 10)
    }

    @Test
    fun `benchmark template query with filter`() {
        val (result, elapsed) = PerformanceUtils.measureTime {
            testTemplates.filter { it.name.contains("important") }
        }

        println("Template query with filter: ${elapsed}ms")
        println("Results: ${result.size} templates")
        assertTrue("Filtered query should be fast", elapsed < 100)
    }

    @Test
    fun `benchmark history query by date range`() {
        val startDate = LocalDateTime.now().minusDays(7)
        val endDate = LocalDateTime.now()

        val (result, elapsed) = PerformanceUtils.measureTime {
            testHistory.filter {
                it.timestamp.isAfter(startDate) && it.timestamp.isBefore(endDate)
            }
        }

        println("History query by date range: ${elapsed}ms")
        println("Results: ${result.size} records")
        assertTrue("Date range query should be fast", elapsed < 200)
    }

    @Test
    fun `benchmark activity log pagination`() {
        val (result, elapsed) = PerformanceUtils.measureTime {
            testLogs
                .sortedByDescending { it.timestamp }
                .paginate(pageSize = 50, page = 0)
        }

        println("Activity log pagination: ${elapsed}ms")
        println("Page size: ${result.size}")
        assertTrue("Paginated query should be fast", elapsed < 100)
    }

    @Test
    fun `benchmark log query with multiple filters`() {
        val (result, elapsed) = PerformanceUtils.measureTime {
            testLogs
                .filter { it.status == ActivityStatus.SUCCESS }
                .filter { it.action.contains("RENAME") }
                .sortedByDescending { it.timestamp }
                .take(100)
        }

        println("Complex log query: ${elapsed}ms")
        println("Results: ${result.size} logs")
        assertTrue("Complex query should be reasonably fast", elapsed < 200)
    }

    @Test
    fun `benchmark aggregation query`() {
        val (result, elapsed) = PerformanceUtils.measureTime {
            testLogs.groupBy { it.action }.mapValues { it.value.size }
        }

        println("Aggregation query: ${elapsed}ms")
        println("Groups: ${result.size}")
        assertTrue("Aggregation should be fast", elapsed < 150)
    }

    @Test
    fun `benchmark sorting large dataset`() {
        val (result, elapsed) = PerformanceUtils.measureTime {
            testHistory.sortedByDescending { it.timestamp }
        }

        println("Sort 1000 history records: ${elapsed}ms")
        assertTrue("Sorting should be efficient", elapsed < 200)
    }

    @Test
    fun `simulate index performance improvement`() {
        // Without index (full scan)
        val (_, noIndexTime) = PerformanceUtils.measureTime {
            testLogs.filter { it.action == "FILE_RENAMED" }
        }

        // Simulated with index (should be much faster in real DB)
        val indexedLogs = testLogs.groupBy { it.action }
        val (_, indexTime) = PerformanceUtils.measureTime {
            indexedLogs["FILE_RENAMED"] ?: emptyList()
        }

        println("Query without index: ${noIndexTime}ms")
        println("Query with index: ${indexTime}ms")
        println("Improvement: ${noIndexTime - indexTime}ms")

        // Index should provide improvement (in real DB, much more significant)
        assertTrue("Index should improve performance", indexTime <= noIndexTime)
    }

    // Helper functions to generate test data
    private fun generateTestTemplates(count: Int): List<RenameTemplate> {
        return (1..count).map { i ->
            RenameTemplate(
                id = i.toLong(),
                name = if (i % 10 == 0) "important_template_$i" else "template_$i",
                pattern = "{name}_{date}",
                description = "Test template $i",
                tags = listOf("test", "benchmark"),
                createdAt = LocalDateTime.now().minusDays(i.toLong()),
                lastUsed = LocalDateTime.now()
            )
        }
    }

    private fun generateTestHistory(count: Int): List<OperationHistory> {
        return (1..count).map { i ->
            OperationHistory(
                id = i.toLong(),
                originalName = "original_$i.jpg",
                newName = "renamed_$i.jpg",
                timestamp = LocalDateTime.now().minusHours(i.toLong()),
                fileUri = "file:///test/$i.jpg",
                templateUsed = "template_${i % 50}"
            )
        }
    }

    private fun generateTestLogs(count: Int): List<ActivityLog> {
        val actions = listOf("FILE_RENAMED", "BATCH_PROCESS", "FOLDER_SCANNED", "EXPORT_DATA")
        val statuses = ActivityStatus.values()

        return (1..count).map { i ->
            ActivityLog(
                id = i.toLong(),
                action = actions[i % actions.size],
                details = "Test log entry $i",
                timestamp = LocalDateTime.now().minusMinutes(i.toLong()),
                status = statuses[i % statuses.size]
            )
        }
    }
}
