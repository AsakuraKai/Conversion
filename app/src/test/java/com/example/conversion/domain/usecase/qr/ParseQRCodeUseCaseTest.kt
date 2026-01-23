package com.example.conversion.domain.usecase.qr

import android.graphics.Bitmap
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.model.RenameTemplate
import com.example.conversion.domain.model.SortStrategy
import com.example.conversion.domain.repository.QRRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for ParseQRCodeUseCase.
 */
class ParseQRCodeUseCaseTest {

    private lateinit var qrRepository: QRRepository
    private lateinit var parseQRCodeUseCase: ParseQRCodeUseCase
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
        qrRepository = mockk()
        parseQRCodeUseCase = ParseQRCodeUseCase(qrRepository, testDispatcher)
    }

    @Test
    fun `invoke with valid QR code returns template`() = runTest(testDispatcher) {
        // Arrange
        val bitmap = mockk<Bitmap>()
        every { bitmap.width } returns 512
        every { bitmap.height } returns 512
        coEvery { qrRepository.parseQRCode(bitmap) } returns Result.Success(validTemplate)

        // Act
        val result = parseQRCodeUseCase(bitmap)

        // Assert
        assertTrue(result is Result.Success)
        assertEquals(validTemplate, (result as Result.Success).data)
        coVerify { qrRepository.parseQRCode(bitmap) }
    }

    @Test
    fun `invoke with invalid bitmap dimensions returns error`() = runTest(testDispatcher) {
        // Arrange
        val bitmap = mockk<Bitmap>()
        every { bitmap.width } returns 0
        every { bitmap.height } returns 0

        // Act
        val result = parseQRCodeUseCase(bitmap)

        // Assert
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).message.contains("Invalid bitmap"))
    }

    @Test
    fun `invoke with zero width returns error`() = runTest(testDispatcher) {
        // Arrange
        val bitmap = mockk<Bitmap>()
        every { bitmap.width } returns 0
        every { bitmap.height } returns 512

        // Act
        val result = parseQRCodeUseCase(bitmap)

        // Assert
        assertTrue(result is Result.Error)
    }

    @Test
    fun `invoke with zero height returns error`() = runTest(testDispatcher) {
        // Arrange
        val bitmap = mockk<Bitmap>()
        every { bitmap.width } returns 512
        every { bitmap.height } returns 0

        // Act
        val result = parseQRCodeUseCase(bitmap)

        // Assert
        assertTrue(result is Result.Error)
    }

    @Test
    fun `invoke with repository error returns error`() = runTest(testDispatcher) {
        // Arrange
        val bitmap = mockk<Bitmap>()
        every { bitmap.width } returns 512
        every { bitmap.height } returns 512
        coEvery { qrRepository.parseQRCode(bitmap) } returns Result.Error("Parse failed")

        // Act
        val result = parseQRCodeUseCase(bitmap)

        // Assert
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).message.contains("Failed to parse QR code"))
    }

    @Test
    fun `invoke with invalid template data returns error`() = runTest(testDispatcher) {
        // Arrange
        val bitmap = mockk<Bitmap>()
        every { bitmap.width } returns 512
        every { bitmap.height } returns 512
        val invalidTemplate = validTemplate.copy(name = "")
        coEvery { qrRepository.parseQRCode(bitmap) } returns Result.Success(invalidTemplate)

        // Act
        val result = parseQRCodeUseCase(bitmap)

        // Assert
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).message.contains("invalid template data"))
    }

    @Test
    fun `invoke with template with invalid config returns error`() = runTest(testDispatcher) {
        // Arrange
        val bitmap = mockk<Bitmap>()
        every { bitmap.width } returns 512
        every { bitmap.height } returns 512
        val invalidTemplate = validTemplate.copy(
            config = validTemplate.config.copy(prefix = "")
        )
        coEvery { qrRepository.parseQRCode(bitmap) } returns Result.Success(invalidTemplate)

        // Act
        val result = parseQRCodeUseCase(bitmap)

        // Assert
        assertTrue(result is Result.Error)
    }

    @Test
    fun `invoke with template with illegal characters returns error`() = runTest(testDispatcher) {
        // Arrange
        val bitmap = mockk<Bitmap>()
        every { bitmap.width } returns 512
        every { bitmap.height } returns 512
        val invalidTemplate = validTemplate.copy(
            config = validTemplate.config.copy(prefix = "Photo<>:")
        )
        coEvery { qrRepository.parseQRCode(bitmap) } returns Result.Success(invalidTemplate)

        // Act
        val result = parseQRCodeUseCase(bitmap)

        // Assert
        assertTrue(result is Result.Error)
    }

    @Test
    fun `invoke with various valid bitmap sizes succeeds`() = runTest(testDispatcher) {
        val sizes = listOf(256, 512, 1024, 2048)

        sizes.forEach { size ->
            // Arrange
            val bitmap = mockk<Bitmap>()
            every { bitmap.width } returns size
            every { bitmap.height } returns size
            coEvery { qrRepository.parseQRCode(bitmap) } returns Result.Success(validTemplate)

            // Act
            val result = parseQRCodeUseCase(bitmap)

            // Assert
            assertTrue("Failed for size $size", result is Result.Success)
        }
    }
}
