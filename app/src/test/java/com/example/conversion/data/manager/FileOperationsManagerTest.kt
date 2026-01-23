package com.example.conversion.data.manager

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for FileOperationsManager.
 * Tests file validation, conflict detection, and safe name generation.
 */
class FileOperationsManagerTest {

    private lateinit var manager: FileOperationsManager

    @Before
    fun setup() {
        manager = FileOperationsManager()
    }

    // ========== validateFilename tests ==========

    @Test
    fun `validateFilename returns true for valid filenames`() {
        val validNames = listOf(
            "photo001.jpg",
            "document.pdf",
            "my_file.txt",
            "test-file.mp4",
            "file with spaces.doc"
        )

        validNames.forEach { name ->
            assertTrue("$name should be valid", manager.validateFilename(name))
        }
    }

    @Test
    fun `validateFilename returns false for empty or blank names`() {
        assertFalse(manager.validateFilename(""))
        assertFalse(manager.validateFilename("   "))
    }

    @Test
    fun `validateFilename returns false for names with illegal characters`() {
        val illegalNames = listOf(
            "file<name.txt",
            "file>name.txt",
            "file:name.txt",
            "file\"name.txt",
            "file/name.txt",
            "file\\name.txt",
            "file|name.txt",
            "file?name.txt",
            "file*name.txt"
        )

        illegalNames.forEach { name ->
            assertFalse("$name should be invalid", manager.validateFilename(name))
        }
    }

    @Test
    fun `validateFilename returns false for names ending with space or period`() {
        assertFalse(manager.validateFilename("filename "))
        assertFalse(manager.validateFilename("filename."))
    }

    @Test
    fun `validateFilename returns false for reserved Windows names`() {
        val reservedNames = listOf("CON", "PRN", "AUX", "NUL", "COM1", "LPT1")
        
        reservedNames.forEach { name ->
            assertFalse("$name should be invalid", manager.validateFilename(name))
            assertFalse("$name.txt should be invalid", manager.validateFilename("$name.txt"))
        }
    }

    @Test
    fun `validateFilename returns false for names consisting only of dots`() {
        assertFalse(manager.validateFilename("."))
        assertFalse(manager.validateFilename(".."))
        assertFalse(manager.validateFilename("..."))
    }

    @Test
    fun `validateFilename returns false for names exceeding max length`() {
        val tooLongName = "a".repeat(256)
        assertFalse(manager.validateFilename(tooLongName))
    }

    // ========== detectConflicts tests ==========

    @Test
    fun `detectConflicts returns empty list when no duplicates`() {
        val names = listOf("file1.txt", "file2.txt", "file3.txt")
        val conflicts = manager.detectConflicts(names)
        
        assertTrue("Should have no conflicts", conflicts.isEmpty())
    }

    @Test
    fun `detectConflicts detects duplicate filenames`() {
        val names = listOf("file1.txt", "file2.txt", "file1.txt", "file3.txt")
        val conflicts = manager.detectConflicts(names)
        
        assertEquals("Should detect 1 conflict", 1, conflicts.size)
        assertTrue("Should contain 'file1.txt'", conflicts.contains("file1.txt"))
    }

    @Test
    fun `detectConflicts detects multiple duplicates`() {
        val names = listOf("a.txt", "b.txt", "a.txt", "b.txt", "c.txt")
        val conflicts = manager.detectConflicts(names)
        
        assertEquals("Should detect 2 conflicts", 2, conflicts.size)
        assertTrue("Should contain 'a.txt'", conflicts.contains("a.txt"))
        assertTrue("Should contain 'b.txt'", conflicts.contains("b.txt"))
    }

    @Test
    fun `detectConflicts handles empty list`() {
        val conflicts = manager.detectConflicts(emptyList())
        assertTrue("Empty list should have no conflicts", conflicts.isEmpty())
    }

    // ========== generateSafeName tests ==========

    @Test
    fun `generateSafeName with index 0 returns original name`() {
        val result = manager.generateSafeName("photo.jpg", 0)
        assertEquals("photo.jpg", result)
    }

    @Test
    fun `generateSafeName with index appends index before extension`() {
        val result = manager.generateSafeName("photo.jpg", 1)
        assertEquals("photo_1.jpg", result)
    }

    @Test
    fun `generateSafeName works with various indices`() {
        assertEquals("file_1.txt", manager.generateSafeName("file.txt", 1))
        assertEquals("file_2.txt", manager.generateSafeName("file.txt", 2))
        assertEquals("file_10.txt", manager.generateSafeName("file.txt", 10))
    }

    @Test
    fun `generateSafeName works with files without extension`() {
        val result = manager.generateSafeName("document", 1)
        assertEquals("document_1", result)
    }

    @Test
    fun `generateSafeName preserves multiple dots in extension`() {
        val result = manager.generateSafeName("archive.tar.gz", 1)
        assertEquals("archive.tar_1.gz", result)
    }

    // ========== sanitizeFilename tests ==========

    @Test
    fun `sanitizeFilename removes illegal characters`() {
        val result = manager.sanitizeFilename("file<name>.txt")
        assertFalse("Should not contain <", result.contains('<'))
        assertFalse("Should not contain >", result.contains('>'))
    }

    @Test
    fun `sanitizeFilename replaces illegal characters with replacement`() {
        val result = manager.sanitizeFilename("file:name.txt", '_')
        assertEquals("file_name.txt", result)
    }

    @Test
    fun `sanitizeFilename removes trailing spaces and periods`() {
        var result = manager.sanitizeFilename("filename ")
        assertFalse("Should not end with space", result.endsWith(' '))
        
        result = manager.sanitizeFilename("filename.")
        assertFalse("Should not end with period", result.endsWith('.'))
    }

    @Test
    fun `sanitizeFilename handles reserved names`() {
        val result = manager.sanitizeFilename("CON.txt")
        assertNotEquals("CON.txt", result)
        assertTrue("Should contain 'renamed'", result.contains("renamed"))
    }

    @Test
    fun `sanitizeFilename returns default name for empty input`() {
        val result = manager.sanitizeFilename("")
        assertEquals("file", result)
    }

    @Test
    fun `sanitizeFilename truncates long filenames`() {
        val longName = "a".repeat(300) + ".txt"
        val result = manager.sanitizeFilename(longName)
        
        assertTrue("Should not exceed max length", result.length <= 255)
    }

    @Test
    fun `sanitizeFilename handles all illegal characters`() {
        val result = manager.sanitizeFilename("file<>:\"/\\|?*.txt", '_')
        // All illegal characters should be replaced
        assertFalse(result.any { it in setOf('<', '>', ':', '"', '/', '\\', '|', '?', '*') })
        assertEquals("file_________.txt", result)
    }

    // ========== wouldConflict tests ==========

    @Test
    fun `wouldConflict returns true for identical names`() {
        assertTrue(manager.wouldConflict("file.txt", "file.txt"))
    }

    @Test
    fun `wouldConflict returns true for case-insensitive match`() {
        assertTrue("Should be case-insensitive", manager.wouldConflict("FILE.TXT", "file.txt"))
        assertTrue("Should be case-insensitive", manager.wouldConflict("Photo.JPG", "photo.jpg"))
    }

    @Test
    fun `wouldConflict returns false for different names`() {
        assertFalse(manager.wouldConflict("file1.txt", "file2.txt"))
    }

    // ========== generateSafeBatch tests ==========

    @Test
    fun `generateSafeBatch returns unique names for all files`() {
        val baseNames = listOf("photo.jpg", "photo.jpg", "photo.jpg")
        val result = manager.generateSafeBatch(baseNames)
        
        assertEquals("Should have 3 results", 3, result.size)
        assertEquals("photo.jpg", result[0])
        assertEquals("photo_1.jpg", result[1])
        assertEquals("photo_2.jpg", result[2])
    }

    @Test
    fun `generateSafeBatch handles case-insensitive conflicts`() {
        val baseNames = listOf("Photo.jpg", "PHOTO.JPG", "photo.jpg")
        val result = manager.generateSafeBatch(baseNames)
        
        // All names should be unique (case-insensitive)
        assertEquals("Should have 3 results", 3, result.size)
        val lowerCaseResults = result.map { it.lowercase() }
        assertEquals("All lowercase names should be unique", 3, lowerCaseResults.toSet().size)
    }

    @Test
    fun `generateSafeBatch preserves non-conflicting names`() {
        val baseNames = listOf("file1.txt", "file2.txt", "file3.txt")
        val result = manager.generateSafeBatch(baseNames)
        
        assertEquals(baseNames, result)
    }

    @Test
    fun `generateSafeBatch handles mix of conflicts and unique names`() {
        val baseNames = listOf("a.txt", "b.txt", "a.txt", "c.txt", "b.txt")
        val result = manager.generateSafeBatch(baseNames)
        
        assertEquals("Should have 5 results", 5, result.size)
        assertEquals("a.txt", result[0])
        assertEquals("b.txt", result[1])
        assertEquals("a_1.txt", result[2])
        assertEquals("c.txt", result[3])
        assertEquals("b_1.txt", result[4])
    }

    @Test
    fun `generateSafeBatch handles empty list`() {
        val result = manager.generateSafeBatch(emptyList())
        assertTrue("Empty input should return empty list", result.isEmpty())
    }

    @Test
    fun `generateSafeBatch handles large batch with many conflicts`() {
        val baseNames = List(100) { "photo.jpg" }
        val result = manager.generateSafeBatch(baseNames)
        
        assertEquals("Should have 100 results", 100, result.size)
        // All names should be unique (case-insensitive)
        val lowerCaseResults = result.map { it.lowercase() }
        assertEquals("All names should be unique", 100, lowerCaseResults.toSet().size)
        
        // First one should be original
        assertEquals("photo.jpg", result[0])
        // Others should have indices
        assertEquals("photo_1.jpg", result[1])
        assertEquals("photo_99.jpg", result[99])
    }

    @Test
    fun `validateFilename handles control characters`() {
        val nameWithTab = "file\tname.txt"
        assertFalse("Should reject control characters", manager.validateFilename(nameWithTab))
    }
}
