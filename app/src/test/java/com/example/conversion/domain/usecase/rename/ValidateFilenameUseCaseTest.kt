package com.example.conversion.domain.usecase.rename

import com.example.conversion.domain.common.Result
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for ValidateFilenameUseCase.
 * Tests filename validation logic for various edge cases.
 */
class ValidateFilenameUseCaseTest {

    private lateinit var useCase: ValidateFilenameUseCase

    @Before
    fun setup() {
        useCase = ValidateFilenameUseCase()
    }

    @Test
    fun `validate simple valid filename`() = runTest {
        val result = useCase("photo001.jpg")
        
        assertTrue("Result should be success", result is Result.Success)
        val validation = result.getOrNull()!!
        assertTrue("Filename should be valid", validation.isValid)
        assertNull("Error message should be null", validation.errorMessage)
    }

    @Test
    fun `validate filename with various valid characters`() = runTest {
        val validFilenames = listOf(
            "my_file.txt",
            "document-2024.pdf",
            "photo (1).jpg",
            "test123.mp4",
            "file with spaces.doc",
            "report_final_v2.xlsx"
        )

        validFilenames.forEach { filename ->
            val result = useCase(filename)
            val validation = result.getOrNull()!!
            assertTrue("$filename should be valid", validation.isValid)
        }
    }

    @Test
    fun `validate empty filename returns error`() = runTest {
        val result = useCase("")
        
        val validation = result.getOrNull()!!
        assertFalse("Empty filename should be invalid", validation.isValid)
        assertEquals("Filename cannot be empty", validation.errorMessage)
    }

    @Test
    fun `validate blank filename returns error`() = runTest {
        val result = useCase("   ")
        
        val validation = result.getOrNull()!!
        assertFalse("Blank filename should be invalid", validation.isValid)
        assertEquals("Filename cannot be empty", validation.errorMessage)
    }

    @Test
    fun `validate filename with illegal characters`() = runTest {
        val illegalChars = listOf('<', '>', ':', '"', '/', '\\', '|', '?', '*')
        
        illegalChars.forEach { char ->
            val filename = "file${char}name.txt"
            val result = useCase(filename)
            val validation = result.getOrNull()!!
            
            assertFalse("Filename with '$char' should be invalid", validation.isValid)
            assertTrue(
                "Error message should mention illegal character",
                validation.errorMessage?.contains("illegal character") == true
            )
        }
    }

    @Test
    fun `validate filename ending with space`() = runTest {
        val result = useCase("filename ")
        
        val validation = result.getOrNull()!!
        assertFalse("Filename ending with space should be invalid", validation.isValid)
        assertTrue(
            "Error message should mention space or period",
            validation.errorMessage?.contains("space or period") == true
        )
    }

    @Test
    fun `validate filename ending with period`() = runTest {
        val result = useCase("filename.")
        
        val validation = result.getOrNull()!!
        assertFalse("Filename ending with period should be invalid", validation.isValid)
        assertTrue(
            "Error message should mention space or period",
            validation.errorMessage?.contains("space or period") == true
        )
    }

    @Test
    fun `validate filename with reserved Windows names`() = runTest {
        val reservedNames = listOf(
            "CON", "PRN", "AUX", "NUL",
            "COM1", "COM2", "COM9",
            "LPT1", "LPT2", "LPT9"
        )

        reservedNames.forEach { name ->
            // Test without extension
            var result = useCase(name)
            var validation = result.getOrNull()!!
            assertFalse("$name should be invalid", validation.isValid)
            assertTrue(
                "Error message should mention reserved filename",
                validation.errorMessage?.contains("reserved") == true
            )

            // Test with extension
            result = useCase("$name.txt")
            validation = result.getOrNull()!!
            assertFalse("$name.txt should be invalid", validation.isValid)
            assertTrue(
                "Error message should mention reserved filename",
                validation.errorMessage?.contains("reserved") == true
            )

            // Test with lowercase
            result = useCase(name.lowercase())
            validation = result.getOrNull()!!
            assertFalse("${name.lowercase()} should be invalid", validation.isValid)
        }
    }

    @Test
    fun `validate filename with reserved name as part of longer name is valid`() = runTest {
        // "CON" is reserved, but "CONFIG" is not
        val result = useCase("CONFIG.txt")
        
        val validation = result.getOrNull()!!
        assertTrue("CONFIG.txt should be valid", validation.isValid)
    }

    @Test
    fun `validate filename with maximum length`() = runTest {
        // Maximum filename length is 255 characters
        val maxLengthName = "a".repeat(255)
        val result = useCase(maxLengthName)
        
        val validation = result.getOrNull()!!
        assertTrue("255 character filename should be valid", validation.isValid)
    }

    @Test
    fun `validate filename exceeding maximum length`() = runTest {
        // 256 characters should be too long
        val tooLongName = "a".repeat(256)
        val result = useCase(tooLongName)
        
        val validation = result.getOrNull()!!
        assertFalse("256 character filename should be invalid", validation.isValid)
        assertTrue(
            "Error message should mention length",
            validation.errorMessage?.contains("too long") == true
        )
    }

    @Test
    fun `validate filename with control characters`() = runTest {
        // Test with ASCII control character (e.g., tab)
        val result = useCase("file\tname.txt")
        
        val validation = result.getOrNull()!!
        assertFalse("Filename with control character should be invalid", validation.isValid)
        assertTrue(
            "Error message should mention control character",
            validation.errorMessage?.contains("control character") == true
        )
    }

    @Test
    fun `validate filename consisting only of dots`() = runTest {
        val dotFilenames = listOf(".", "..", "...", "....")
        
        dotFilenames.forEach { filename ->
            val result = useCase(filename)
            val validation = result.getOrNull()!!
            assertFalse("'$filename' should be invalid", validation.isValid)
            assertTrue(
                "Error message should mention dots",
                validation.errorMessage?.contains("dots") == true
            )
        }
    }

    @Test
    fun `validate filename starting with dot is valid`() = runTest {
        // Hidden files on Unix-like systems start with dot
        val result = useCase(".hidden_file.txt")
        
        val validation = result.getOrNull()!!
        assertTrue(".hidden_file.txt should be valid", validation.isValid)
    }

    @Test
    fun `validate filename with multiple extensions`() = runTest {
        val result = useCase("archive.tar.gz")
        
        val validation = result.getOrNull()!!
        assertTrue("archive.tar.gz should be valid", validation.isValid)
    }

    @Test
    fun `validate filename with unicode characters`() = runTest {
        val unicodeFilenames = listOf(
            "文件.txt",
            "ファイル.jpg",
            "файл.pdf",
            "αρχείο.doc"
        )

        unicodeFilenames.forEach { filename ->
            val result = useCase(filename)
            val validation = result.getOrNull()!!
            assertTrue("Unicode filename '$filename' should be valid", validation.isValid)
        }
    }

    @Test
    fun `validate long filename with extension`() = runTest {
        // Create a filename that's exactly 255 characters with extension
        val baseName = "a".repeat(250)
        val filename = "$baseName.txt" // 254 characters total
        
        val result = useCase(filename)
        val validation = result.getOrNull()!!
        assertTrue("Long valid filename should be valid", validation.isValid)
    }

    @Test
    fun `validate ValidationResult helper methods`() {
        val validResult = ValidateFilenameUseCase.ValidationResult.valid()
        assertTrue(validResult.isValid)
        assertNull(validResult.errorMessage)

        val invalidResult = ValidateFilenameUseCase.ValidationResult.invalid("Test error")
        assertFalse(invalidResult.isValid)
        assertEquals("Test error", invalidResult.errorMessage)
    }

    @Test
    fun `validate filename with special characters allowed`() = runTest {
        val specialFilenames = listOf(
            "file!.txt",
            "file@.txt",
            "file#.txt",
            "file\$.txt",
            "file%.txt",
            "file&.txt",
            "file'.txt",
            "file+.txt",
            "file=.txt",
            "file[1].txt",
            "file{test}.txt"
        )

        specialFilenames.forEach { filename ->
            val result = useCase(filename)
            val validation = result.getOrNull()!!
            assertTrue("'$filename' should be valid", validation.isValid)
        }
    }
}
