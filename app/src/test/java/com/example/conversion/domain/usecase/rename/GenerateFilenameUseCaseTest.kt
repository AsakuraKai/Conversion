package com.example.conversion.domain.usecase.rename

import android.net.Uri
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.model.SortStrategy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for GenerateFilenameUseCase.
 * Tests filename generation with various configurations.
 */
class GenerateFilenameUseCaseTest {

    private lateinit var useCase: GenerateFilenameUseCase
    
    // Sample file item for testing
    private val sampleFileItem = FileItem(
        id = 1L,
        uri = Uri.parse("content://media/external/images/1"),
        name = "IMG_001.jpg",
        path = "/storage/emulated/0/DCIM/IMG_001.jpg",
        size = 1024L,
        mimeType = "image/jpeg",
        dateModified = System.currentTimeMillis(),
        thumbnailUri = null
    )

    @Before
    fun setup() {
        useCase = GenerateFilenameUseCase()
    }

    @Test
    fun `generate filename with default config`() = runTest {
        // Given: Default config with 3 digits
        val config = RenameConfig(
            prefix = "photo",
            startNumber = 1,
            digitCount = 3,
            preserveExtension = true
        )
        val params = GenerateFilenameUseCase.Params(sampleFileItem, config, index = 0)

        // When: Generate filename
        val result = useCase(params)

        // Then: Should be "photo001.jpg"
        assertTrue("Result should be success", result is Result.Success)
        assertEquals("photo001.jpg", result.getOrNull())
    }

    @Test
    fun `generate filename with custom start number`() = runTest {
        // Given: Config with start number 10
        val config = RenameConfig(
            prefix = "image",
            startNumber = 10,
            digitCount = 3,
            preserveExtension = true
        )
        val params = GenerateFilenameUseCase.Params(sampleFileItem, config, index = 5)

        // When: Generate filename
        val result = useCase(params)

        // Then: Should be "image015.jpg" (10 + 5 = 15)
        assertTrue("Result should be success", result is Result.Success)
        assertEquals("image015.jpg", result.getOrNull())
    }

    @Test
    fun `generate filename with different digit counts`() = runTest {
        // Test with 1 digit
        val config1 = RenameConfig(prefix = "pic", startNumber = 1, digitCount = 1)
        val params1 = GenerateFilenameUseCase.Params(sampleFileItem, config1, index = 5)
        var result = useCase(params1)
        assertEquals("pic6.jpg", result.getOrNull())

        // Test with 2 digits
        val config2 = RenameConfig(prefix = "pic", startNumber = 1, digitCount = 2)
        val params2 = GenerateFilenameUseCase.Params(sampleFileItem, config2, index = 5)
        result = useCase(params2)
        assertEquals("pic06.jpg", result.getOrNull())

        // Test with 4 digits
        val config4 = RenameConfig(prefix = "pic", startNumber = 1, digitCount = 4)
        val params4 = GenerateFilenameUseCase.Params(sampleFileItem, config4, index = 5)
        result = useCase(params4)
        assertEquals("pic0006.jpg", result.getOrNull())

        // Test with 5 digits
        val config5 = RenameConfig(prefix = "pic", startNumber = 1, digitCount = 5)
        val params5 = GenerateFilenameUseCase.Params(sampleFileItem, config5, index = 99)
        result = useCase(params5)
        assertEquals("pic00100.jpg", result.getOrNull())
    }

    @Test
    fun `generate filename without preserving extension`() = runTest {
        // Given: Config with preserveExtension = false
        val config = RenameConfig(
            prefix = "file",
            startNumber = 1,
            digitCount = 3,
            preserveExtension = false
        )
        val params = GenerateFilenameUseCase.Params(sampleFileItem, config, index = 0)

        // When: Generate filename
        val result = useCase(params)

        // Then: Should be "file001" without extension
        assertTrue("Result should be success", result is Result.Success)
        assertEquals("file001", result.getOrNull())
    }

    @Test
    fun `generate filename for file without extension`() = runTest {
        // Given: File without extension
        val fileWithoutExt = sampleFileItem.copy(name = "document")
        val config = RenameConfig(prefix = "doc", startNumber = 1, digitCount = 2)
        val params = GenerateFilenameUseCase.Params(fileWithoutExt, config, index = 0)

        // When: Generate filename
        val result = useCase(params)

        // Then: Should be "doc01" (no extension to preserve)
        assertEquals("doc01", result.getOrNull())
    }

    @Test
    fun `generate filename with various prefixes`() = runTest {
        val testCases = listOf(
            "vacation" to "vacation001.jpg",
            "2024_trip" to "2024_trip001.jpg",
            "My Photos" to "My Photos001.jpg",
            "test-file" to "test-file001.jpg"
        )

        testCases.forEach { (prefix, expected) ->
            val config = RenameConfig(prefix = prefix, startNumber = 1, digitCount = 3)
            val params = GenerateFilenameUseCase.Params(sampleFileItem, config, index = 0)
            val result = useCase(params)
            assertEquals("Prefix '$prefix' should generate '$expected'", expected, result.getOrNull())
        }
    }

    @Test
    fun `generate filename with different file types`() = runTest {
        val config = RenameConfig(prefix = "file", startNumber = 1, digitCount = 3)

        // Test various file types
        val testFiles = listOf(
            sampleFileItem.copy(name = "video.mp4") to "file001.mp4",
            sampleFileItem.copy(name = "audio.mp3") to "file001.mp3",
            sampleFileItem.copy(name = "document.pdf") to "file001.pdf",
            sampleFileItem.copy(name = "archive.zip") to "file001.zip",
            sampleFileItem.copy(name = "image.PNG") to "file001.PNG"
        )

        testFiles.forEach { (file, expected) ->
            val params = GenerateFilenameUseCase.Params(file, config, index = 0)
            val result = useCase(params)
            assertEquals("File ${file.name} should generate $expected", expected, result.getOrNull())
        }
    }

    @Test
    fun `generate filename handles large index numbers`() = runTest {
        val config = RenameConfig(prefix = "img", startNumber = 1, digitCount = 3)
        
        // Test with large index (should not be limited by digitCount)
        val params = GenerateFilenameUseCase.Params(sampleFileItem, config, index = 9999)
        val result = useCase(params)
        
        // Should be "img10000.jpg" (no truncation, padding doesn't limit)
        assertEquals("img10000.jpg", result.getOrNull())
    }

    @Test
    fun `generate filename with zero start number`() = runTest {
        val config = RenameConfig(prefix = "pic", startNumber = 0, digitCount = 3)
        val params = GenerateFilenameUseCase.Params(sampleFileItem, config, index = 0)
        val result = useCase(params)
        
        assertEquals("pic000.jpg", result.getOrNull())
    }

    @Test
    fun `generate filenames for sequential batch`() = runTest {
        val config = RenameConfig(prefix = "batch", startNumber = 1, digitCount = 3)
        
        val expectedResults = listOf(
            "batch001.jpg",
            "batch002.jpg",
            "batch003.jpg",
            "batch004.jpg",
            "batch005.jpg"
        )

        expectedResults.forEachIndexed { index, expected ->
            val params = GenerateFilenameUseCase.Params(sampleFileItem, config, index = index)
            val result = useCase(params)
            assertEquals("Index $index should generate $expected", expected, result.getOrNull())
        }
    }

    @Test
    fun `generate filename preserves case of extension`() = runTest {
        val config = RenameConfig(prefix = "file", startNumber = 1, digitCount = 2)
        
        // Test uppercase extension
        val upperFile = sampleFileItem.copy(name = "IMAGE.JPG")
        val params1 = GenerateFilenameUseCase.Params(upperFile, config, index = 0)
        val result1 = useCase(params1)
        assertEquals("file01.JPG", result1.getOrNull())
        
        // Test mixed case extension
        val mixedFile = sampleFileItem.copy(name = "image.JpEg")
        val params2 = GenerateFilenameUseCase.Params(mixedFile, config, index = 0)
        val result2 = useCase(params2)
        assertEquals("file01.JpEg", result2.getOrNull())
    }
}
