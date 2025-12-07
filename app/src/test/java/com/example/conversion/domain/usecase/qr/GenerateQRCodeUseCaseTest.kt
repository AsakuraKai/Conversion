package com.example.conversion.domain.usecase.qr

import android.graphics.Bitmap
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.model.RenameTemplate
import com.example.conversion.domain.model.SortStrategy
import com.example.conversion.domain.repository.QRRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for GenerateQRCodeUseCase.
 */
class GenerateQRCodeUseCaseTest {

    private lateinit var qrRepository: QRRepository
    private lateinit var generateQRCodeUseCase: GenerateQRCodeUseCase
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
        generateQRCodeUseCase = GenerateQRCodeUseCase(qrRepository, testDispatcher)
    }

    @Test
    fun `invoke with valid template generates QR code`() = runTest(testDispatcher) {
        // Arrange
        val expectedBitmap = mockk<Bitmap>()
        coEvery { qrRepository.generateQRCode(validTemplate, 512) } returns Result.Success(expectedBitmap)

        // Act
        val result = generateQRCodeUseCase(
            GenerateQRCodeUseCase.GenerateQRParams(validTemplate, 512)
        )

        // Assert
        assertTrue(result is Result.Success)
        assertEquals(expectedBitmap, (result as Result.Success).data)
        coVerify { qrRepository.generateQRCode(validTemplate, 512) }
    }

    @Test
    fun `invoke with default size uses DEFAULT_QR_SIZE`() = runTest(testDispatcher) {
        // Arrange
        val expectedBitmap = mockk<Bitmap>()
        coEvery { qrRepository.generateQRCode(validTemplate, 512) } returns Result.Success(expectedBitmap)

        // Act
        val result = generateQRCodeUseCase(
            GenerateQRCodeUseCase.GenerateQRParams(validTemplate)
        )

        // Assert
        assertTrue(result is Result.Success)
        coVerify { qrRepository.generateQRCode(validTemplate, 512) }
    }

    @Test
    fun `invoke with invalid template returns error`() = runTest(testDispatcher) {
        // Arrange
        val invalidTemplate = validTemplate.copy(name = "")

        // Act
        val result = generateQRCodeUseCase(
            GenerateQRCodeUseCase.GenerateQRParams(invalidTemplate)
        )

        // Assert
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).message.contains("Invalid template"))
    }

    @Test
    fun `invoke with size below minimum returns error`() = runTest(testDispatcher) {
        // Act
        val result = generateQRCodeUseCase(
            GenerateQRCodeUseCase.GenerateQRParams(validTemplate, 100)
        )

        // Assert
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).message.contains("size must be between"))
    }

    @Test
    fun `invoke with size above maximum returns error`() = runTest(testDispatcher) {
        // Act
        val result = generateQRCodeUseCase(
            GenerateQRCodeUseCase.GenerateQRParams(validTemplate, 5000)
        )

        // Assert
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).message.contains("size must be between"))
    }

    @Test
    fun `invoke with minimum allowed size succeeds`() = runTest(testDispatcher) {
        // Arrange
        val expectedBitmap = mockk<Bitmap>()
        coEvery { qrRepository.generateQRCode(validTemplate, 256) } returns Result.Success(expectedBitmap)

        // Act
        val result = generateQRCodeUseCase(
            GenerateQRCodeUseCase.GenerateQRParams(validTemplate, 256)
        )

        // Assert
        assertTrue(result is Result.Success)
    }

    @Test
    fun `invoke with maximum allowed size succeeds`() = runTest(testDispatcher) {
        // Arrange
        val expectedBitmap = mockk<Bitmap>()
        coEvery { qrRepository.generateQRCode(validTemplate, 2048) } returns Result.Success(expectedBitmap)

        // Act
        val result = generateQRCodeUseCase(
            GenerateQRCodeUseCase.GenerateQRParams(validTemplate, 2048)
        )

        // Assert
        assertTrue(result is Result.Success)
    }

    @Test
    fun `invoke with repository error returns error`() = runTest(testDispatcher) {
        // Arrange
        coEvery { qrRepository.generateQRCode(any(), any()) } returns Result.Error("Generation failed")

        // Act
        val result = generateQRCodeUseCase(
            GenerateQRCodeUseCase.GenerateQRParams(validTemplate)
        )

        // Assert
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).message.contains("Failed to generate QR code"))
    }

    @Test
    fun `invoke with template with illegal characters in prefix returns error`() = runTest(testDispatcher) {
        // Arrange
        val invalidTemplate = validTemplate.copy(
            config = validTemplate.config.copy(prefix = "Photo<>:")
        )

        // Act
        val result = generateQRCodeUseCase(
            GenerateQRCodeUseCase.GenerateQRParams(invalidTemplate)
        )

        // Assert
        assertTrue(result is Result.Error)
    }

    @Test
    fun `constants have expected values`() {
        assertEquals(512, GenerateQRCodeUseCase.DEFAULT_QR_SIZE)
        assertEquals(256, GenerateQRCodeUseCase.MIN_QR_SIZE)
        assertEquals(2048, GenerateQRCodeUseCase.MAX_QR_SIZE)
    }
}
