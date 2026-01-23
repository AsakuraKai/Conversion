package com.example.conversion.data.repository

import android.graphics.Bitmap
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.model.RenameTemplate
import com.example.conversion.domain.model.SortStrategy
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for QRRepositoryImpl (mock implementation).
 */
class QRRepositoryImplTest {

    private lateinit var qrRepository: QRRepositoryImpl
    private val testDispatcher = UnconfinedTestDispatcher()

    private val validTemplate = RenameTemplate(
        id = "test-id",
        name = "Test Template",
        pattern = "Photo_{number}",
        config = RenameConfig(
            prefix = "Photo_",
            startNumber = 1,
            digitCount = 3,
            preserveExtension = true,
            sortStrategy = SortStrategy.NATURAL
        )
    )

    @Before
    fun setup() {
        qrRepository = QRRepositoryImpl(testDispatcher)
    }

    @Test
    fun `generateQRCode with valid template returns bitmap`() = runTest(testDispatcher) {
        // Act
        val result = qrRepository.generateQRCode(validTemplate, 512)

        // Assert
        assertTrue(result is Result.Success)
        val bitmap = (result as Result.Success).data
        assertNotNull(bitmap)
        assertEquals(512, bitmap.width)
        assertEquals(512, bitmap.height)
    }

    @Test
    fun `generateQRCode with different sizes returns correct dimensions`() = runTest(testDispatcher) {
        val sizes = listOf(256, 512, 1024, 2048)

        sizes.forEach { size ->
            // Act
            val result = qrRepository.generateQRCode(validTemplate, size)

            // Assert
            assertTrue("Failed for size $size", result is Result.Success)
            val bitmap = (result as Result.Success).data
            assertEquals(size, bitmap.width)
            assertEquals(size, bitmap.height)
        }
    }

    @Test
    fun `parseQRCode with generated QR code returns original template`() = runTest(testDispatcher) {
        // Arrange
        val generateResult = qrRepository.generateQRCode(validTemplate, 512)
        assertTrue(generateResult is Result.Success)
        val bitmap = (generateResult as Result.Success).data

        // Act
        val parseResult = qrRepository.parseQRCode(bitmap)

        // Assert
        assertTrue(parseResult is Result.Success)
        val parsedTemplate = (parseResult as Result.Success).data
        assertEquals(validTemplate.name, parsedTemplate.name)
        assertEquals(validTemplate.pattern, parsedTemplate.pattern)
        assertEquals(validTemplate.config.prefix, parsedTemplate.config.prefix)
        assertEquals(validTemplate.config.startNumber, parsedTemplate.config.startNumber)
        assertEquals(validTemplate.config.digitCount, parsedTemplate.config.digitCount)
        assertEquals(validTemplate.config.preserveExtension, parsedTemplate.config.preserveExtension)
        assertEquals(validTemplate.config.sortStrategy, parsedTemplate.config.sortStrategy)
    }

    @Test
    fun `parseQRCode with unknown bitmap returns error`() = runTest(testDispatcher) {
        // Arrange
        val unknownBitmap = mockk<Bitmap>()

        // Act
        val result = qrRepository.parseQRCode(unknownBitmap)

        // Assert
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).message.contains("not found in cache"))
    }

    @Test
    fun `encodeToJson with valid template returns JSON string`() = runTest(testDispatcher) {
        // Act
        val result = qrRepository.encodeToJson(validTemplate)

        // Assert
        assertTrue(result is Result.Success)
        val json = (result as Result.Success).data
        assertTrue(json.contains("\"templateName\""))
        assertTrue(json.contains("\"Test Template\""))
        assertTrue(json.contains("\"prefix\""))
        assertTrue(json.contains("\"Photo_\""))
    }

    @Test
    fun `decodeFromJson with valid JSON returns template`() = runTest(testDispatcher) {
        // Arrange
        val encodeResult = qrRepository.encodeToJson(validTemplate)
        assertTrue(encodeResult is Result.Success)
        val json = (encodeResult as Result.Success).data

        // Act
        val decodeResult = qrRepository.decodeFromJson(json)

        // Assert
        assertTrue(decodeResult is Result.Success)
        val decodedTemplate = (decodeResult as Result.Success).data
        assertEquals(validTemplate.name, decodedTemplate.name)
        assertEquals(validTemplate.pattern, decodedTemplate.pattern)
        assertEquals(validTemplate.config.prefix, decodedTemplate.config.prefix)
    }

    @Test
    fun `decodeFromJson with invalid JSON returns error`() = runTest(testDispatcher) {
        // Act
        val result = qrRepository.decodeFromJson("invalid json")

        // Assert
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).message.contains("Failed to decode"))
    }

    @Test
    fun `decodeFromJson with empty JSON returns error`() = runTest(testDispatcher) {
        // Act
        val result = qrRepository.decodeFromJson("")

        // Assert
        assertTrue(result is Result.Error)
    }

    @Test
    fun `round trip encode and decode preserves all template data`() = runTest(testDispatcher) {
        // Arrange
        val templates = listOf(
            validTemplate,
            validTemplate.copy(
                name = "Different Name",
                config = validTemplate.config.copy(prefix = "IMG_")
            ),
            validTemplate.copy(
                config = validTemplate.config.copy(
                    startNumber = 100,
                    digitCount = 5,
                    sortStrategy = SortStrategy.DATE_MODIFIED
                )
            )
        )

        templates.forEach { template ->
            // Act
            val encodeResult = qrRepository.encodeToJson(template)
            assertTrue(encodeResult is Result.Success)
            val json = (encodeResult as Result.Success).data

            val decodeResult = qrRepository.decodeFromJson(json)
            assertTrue(decodeResult is Result.Success)
            val decoded = (decodeResult as Result.Success).data

            // Assert
            assertEquals(template.name, decoded.name)
            assertEquals(template.pattern, decoded.pattern)
            assertEquals(template.config.prefix, decoded.config.prefix)
            assertEquals(template.config.startNumber, decoded.config.startNumber)
            assertEquals(template.config.digitCount, decoded.config.digitCount)
            assertEquals(template.config.preserveExtension, decoded.config.preserveExtension)
            assertEquals(template.config.sortStrategy, decoded.config.sortStrategy)
        }
    }

    @Test
    fun `generateQRCode creates bitmap with QR code pattern`() = runTest(testDispatcher) {
        // Act
        val result = qrRepository.generateQRCode(validTemplate, 512)

        // Assert
        assertTrue(result is Result.Success)
        val bitmap = (result as Result.Success).data

        // Check that bitmap has black and white pixels (QR code pattern)
        var hasBlackPixels = false
        var hasWhitePixels = false

        for (x in 0 until minOf(50, bitmap.width)) {
            for (y in 0 until minOf(50, bitmap.height)) {
                val pixel = bitmap.getPixel(x, y)
                if (pixel == android.graphics.Color.BLACK) hasBlackPixels = true
                if (pixel == android.graphics.Color.WHITE) hasWhitePixels = true
            }
        }

        assertTrue("Bitmap should have black pixels", hasBlackPixels)
        assertTrue("Bitmap should have white pixels", hasWhitePixels)
    }

    @Test
    fun `multiple generateQRCode calls with same template produce cacheable results`() = runTest(testDispatcher) {
        // Act
        val result1 = qrRepository.generateQRCode(validTemplate, 512)
        val result2 = qrRepository.generateQRCode(validTemplate, 512)

        // Assert
        assertTrue(result1 is Result.Success)
        assertTrue(result2 is Result.Success)
        val bitmap1 = (result1 as Result.Success).data
        val bitmap2 = (result2 as Result.Success).data

        // Both should be parseable
        val parse1 = qrRepository.parseQRCode(bitmap1)
        val parse2 = qrRepository.parseQRCode(bitmap2)
        assertTrue(parse1 is Result.Success)
        assertTrue(parse2 is Result.Success)
    }
}
