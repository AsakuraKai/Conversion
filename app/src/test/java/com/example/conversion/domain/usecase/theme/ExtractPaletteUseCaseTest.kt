package com.example.conversion.domain.usecase.theme

import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.ImagePalette
import com.example.conversion.domain.repository.ThemeRepository
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for ExtractPaletteUseCase.
 * Tests palette extraction logic and error handling.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ExtractPaletteUseCaseTest {
    
    private lateinit var themeRepository: ThemeRepository
    private lateinit var useCase: ExtractPaletteUseCase
    private val testDispatcher = UnconfinedTestDispatcher()
    
    // Mock data
    private val mockUri = Uri.parse("content://media/external/images/1")
    private val mockPalette = ImagePalette(
        dominantColor = Color(0xFF1976D2),
        vibrantColor = Color(0xFF2196F3),
        mutedColor = Color(0xFF90CAF9),
        darkVibrantColor = Color(0xFF0D47A1),
        lightVibrantColor = Color(0xFFBBDEFB),
        darkMutedColor = Color(0xFF5C6BC0),
        lightMutedColor = Color(0xFFC5CAE9)
    )
    
    @Before
    fun setup() {
        themeRepository = mockk()
        useCase = ExtractPaletteUseCase(themeRepository, testDispatcher)
    }
    
    @Test
    fun `extractPalette returns success with complete palette`() = runTest {
        // Given
        coEvery { themeRepository.extractPalette(mockUri) } returns Result.Success(mockPalette)
        
        // When
        val result = useCase(mockUri)
        
        // Then
        assertTrue(result is Result.Success)
        val palette = (result as Result.Success).data
        assertEquals(mockPalette, palette)
        assertTrue(palette.hasColors)
        verify { themeRepository.extractPalette(mockUri) }
    }
    
    @Test
    fun `extractPalette returns palette with only dominant color`() = runTest {
        // Given
        val partialPalette = ImagePalette(
            dominantColor = Color(0xFF1976D2),
            vibrantColor = null,
            mutedColor = null
        )
        coEvery { themeRepository.extractPalette(mockUri) } returns Result.Success(partialPalette)
        
        // When
        val result = useCase(mockUri)
        
        // Then
        assertTrue(result is Result.Success)
        val palette = (result as Result.Success).data
        assertEquals(Color(0xFF1976D2), palette.dominantColor)
        assertTrue(palette.hasColors)
    }
    
    @Test
    fun `extractPalette returns error when repository fails`() = runTest {
        // Given
        val exception = Exception("Failed to load image")
        coEvery { themeRepository.extractPalette(mockUri) } returns Result.Error(exception)
        
        // When
        val result = useCase(mockUri)
        
        // Then
        assertTrue(result is Result.Error)
        val error = result as Result.Error
        assertEquals(exception, error.exception)
    }
    
    @Test
    fun `extractPalette returns error on SecurityException`() = runTest {
        // Given
        val securityException = SecurityException("Permission denied")
        coEvery { themeRepository.extractPalette(mockUri) } returns Result.Error(securityException)
        
        // When
        val result = useCase(mockUri)
        
        // Then
        assertTrue(result is Result.Error)
        val error = result as Result.Error
        assertTrue(error.exception is SecurityException)
    }
    
    @Test
    fun `extractPalette returns error on invalid URI`() = runTest {
        // Given
        val invalidUri = Uri.parse("invalid://uri")
        val exception = IllegalArgumentException("Invalid URI scheme")
        coEvery { themeRepository.extractPalette(invalidUri) } returns Result.Error(exception)
        
        // When
        val result = useCase(invalidUri)
        
        // Then
        assertTrue(result is Result.Error)
        val error = result as Result.Error
        assertTrue(error.exception is IllegalArgumentException)
    }
    
    @Test
    fun `extractPalette verifies primaryColor selection logic`() = runTest {
        // Given - palette with vibrant color
        val paletteWithVibrant = mockPalette
        coEvery { themeRepository.extractPalette(mockUri) } returns Result.Success(paletteWithVibrant)
        
        // When
        val result = useCase(mockUri)
        
        // Then
        assertTrue(result is Result.Success)
        val palette = (result as Result.Success).data
        assertEquals(palette.vibrantColor, palette.primaryColor)
    }
    
    @Test
    fun `extractPalette verifies primaryColor fallback to dominant`() = runTest {
        // Given - palette without vibrant color
        val paletteNoDominant = ImagePalette(
            dominantColor = Color(0xFF1976D2),
            vibrantColor = null,
            mutedColor = Color(0xFF90CAF9)
        )
        coEvery { themeRepository.extractPalette(mockUri) } returns Result.Success(paletteNoDominant)
        
        // When
        val result = useCase(mockUri)
        
        // Then
        assertTrue(result is Result.Success)
        val palette = (result as Result.Success).data
        assertEquals(palette.dominantColor, palette.primaryColor)
    }
    
    @Test
    fun `extractPalette verifies secondaryColor selection logic`() = runTest {
        // Given - palette with muted color
        coEvery { themeRepository.extractPalette(mockUri) } returns Result.Success(mockPalette)
        
        // When
        val result = useCase(mockUri)
        
        // Then
        assertTrue(result is Result.Success)
        val palette = (result as Result.Success).data
        assertEquals(palette.mutedColor, palette.secondaryColor)
    }
    
    @Test
    fun `extractPalette verifies darkThemePrimary color`() = runTest {
        // Given
        coEvery { themeRepository.extractPalette(mockUri) } returns Result.Success(mockPalette)
        
        // When
        val result = useCase(mockUri)
        
        // Then
        assertTrue(result is Result.Success)
        val palette = (result as Result.Success).data
        assertEquals(palette.darkVibrantColor, palette.darkThemePrimary)
    }
    
    @Test
    fun `extractPalette verifies lightThemePrimary color`() = runTest {
        // Given
        coEvery { themeRepository.extractPalette(mockUri) } returns Result.Success(mockPalette)
        
        // When
        val result = useCase(mockUri)
        
        // Then
        assertTrue(result is Result.Success)
        val palette = (result as Result.Success).data
        assertEquals(palette.lightVibrantColor, palette.lightThemePrimary)
    }
}
