package com.example.conversion.presentation.theme

import android.net.Uri
import androidx.compose.ui.graphics.Color
import app.cash.turbine.test
import com.example.conversion.domain.model.ImagePalette
import com.example.conversion.domain.usecase.theme.ExtractPaletteUseCase
import com.example.conversion.presentation.theme.DynamicThemeContract.Action
import com.example.conversion.presentation.theme.DynamicThemeContract.Event
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DynamicThemeViewModelTest {
    
    private lateinit var extractPaletteUseCase: ExtractPaletteUseCase
    private lateinit var viewModel: DynamicThemeViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    private val testImageUri = mockk<Uri>(relaxed = true)
    private val testPalette = ImagePalette(
        dominantColor = Color.Blue,
        vibrantColor = Color.Red,
        mutedColor = Color.Gray,
        darkVibrantColor = Color.DarkGray,
        lightVibrantColor = Color.Cyan
    )
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        extractPaletteUseCase = mockk()
        viewModel = DynamicThemeViewModel(extractPaletteUseCase, testDispatcher)
    }
    
    @Test
    fun `initial state should be correct`() {
        val state = viewModel.state.value
        
        assertNull(state.selectedImageUri)
        assertNull(state.palette)
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertFalse(state.isThemeApplied)
        assertFalse(state.hasPalette)
        assertFalse(state.canApplyTheme)
        assertFalse(state.canResetTheme)
    }
    
    @Test
    fun `selectImage action should set loading state`() = runTest {
        coEvery { extractPaletteUseCase(testImageUri) } returns testPalette
        
        viewModel.handleAction(Action.SelectImage(testImageUri))
        
        val state = viewModel.state.value
        assertEquals(testImageUri, state.selectedImageUri)
        assertTrue(state.isLoading)
        assertNull(state.error)
        assertNull(state.palette)
        assertFalse(state.isThemeApplied)
    }
    
    @Test
    fun `selectImage should extract palette successfully`() = runTest {
        coEvery { extractPaletteUseCase(testImageUri) } returns testPalette
        
        viewModel.handleAction(Action.SelectImage(testImageUri))
        advanceUntilIdle()
        
        val state = viewModel.state.value
        assertEquals(testImageUri, state.selectedImageUri)
        assertEquals(testPalette, state.palette)
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertTrue(state.hasPalette)
        assertTrue(state.canApplyTheme)
        
        coVerify { extractPaletteUseCase(testImageUri) }
    }
    
    @Test
    fun `selectImage should emit success message on palette extraction`() = runTest {
        coEvery { extractPaletteUseCase(testImageUri) } returns testPalette
        
        viewModel.events.test {
            viewModel.handleAction(Action.SelectImage(testImageUri))
            advanceUntilIdle()
            
            val event = awaitItem() as Event.ShowMessage
            assertEquals("Colors extracted successfully!", event.message)
        }
    }
    
    @Test
    fun `selectImage should handle palette with no colors`() = runTest {
        val emptyPalette = ImagePalette(
            dominantColor = null,
            vibrantColor = null,
            mutedColor = null
        )
        coEvery { extractPaletteUseCase(testImageUri) } returns emptyPalette
        
        viewModel.handleAction(Action.SelectImage(testImageUri))
        advanceUntilIdle()
        
        val state = viewModel.state.value
        assertEquals(emptyPalette, state.palette)
        assertFalse(state.isLoading)
        assertEquals("Could not extract colors from this image. Try another one.", state.error)
        assertFalse(state.hasPalette)
        assertFalse(state.canApplyTheme)
    }
    
    @Test
    fun `selectImage should handle SecurityException`() = runTest {
        coEvery { extractPaletteUseCase(testImageUri) } throws SecurityException("Permission denied")
        
        viewModel.events.test {
            viewModel.handleAction(Action.SelectImage(testImageUri))
            advanceUntilIdle()
            
            val state = viewModel.state.value
            assertFalse(state.isLoading)
            assertEquals("Permission denied. Please grant storage access.", state.error)
            assertNull(state.palette)
            
            val event = awaitItem() as Event.ShowMessage
            assertEquals("Permission denied", event.message)
        }
    }
    
    @Test
    fun `selectImage should handle IllegalArgumentException`() = runTest {
        coEvery { extractPaletteUseCase(testImageUri) } throws IllegalArgumentException("Invalid URI")
        
        viewModel.events.test {
            viewModel.handleAction(Action.SelectImage(testImageUri))
            advanceUntilIdle()
            
            val state = viewModel.state.value
            assertFalse(state.isLoading)
            assertEquals("Invalid image. Please select a different image.", state.error)
            assertNull(state.palette)
            
            val event = awaitItem() as Event.ShowMessage
            assertEquals("Invalid image selected", event.message)
        }
    }
    
    @Test
    fun `selectImage should handle generic exception`() = runTest {
        coEvery { extractPaletteUseCase(testImageUri) } throws Exception("Unknown error")
        
        viewModel.events.test {
            viewModel.handleAction(Action.SelectImage(testImageUri))
            advanceUntilIdle()
            
            val state = viewModel.state.value
            assertFalse(state.isLoading)
            assertEquals("Failed to extract colors: Unknown error", state.error)
            assertNull(state.palette)
            
            val event = awaitItem() as Event.ShowMessage
            assertEquals("Failed to extract colors", event.message)
        }
    }
    
    @Test
    fun `applyTheme should mark theme as applied with valid palette`() = runTest {
        coEvery { extractPaletteUseCase(testImageUri) } returns testPalette
        
        viewModel.events.test {
            viewModel.handleAction(Action.SelectImage(testImageUri))
            advanceUntilIdle()
            
            skipItems(1) // Skip the success message from SelectImage
            
            viewModel.handleAction(Action.ApplyTheme)
            
            val state = viewModel.state.value
            assertTrue(state.isThemeApplied)
            assertTrue(state.canResetTheme)
            assertFalse(state.canApplyTheme)
            
            val themeAppliedEvent = awaitItem()
            assertTrue(themeAppliedEvent is Event.ThemeApplied)
            
            val messageEvent = awaitItem() as Event.ShowMessage
            assertEquals("Theme applied successfully!", messageEvent.message)
        }
    }
    
    @Test
    fun `applyTheme should do nothing with null palette`() = runTest {
        viewModel.events.test {
            viewModel.handleAction(Action.ApplyTheme)
            
            val state = viewModel.state.value
            assertFalse(state.isThemeApplied)
            
            val event = awaitItem() as Event.ShowMessage
            assertEquals("No valid colors to apply", event.message)
        }
    }
    
    @Test
    fun `applyTheme should do nothing with empty palette`() = runTest {
        val emptyPalette = ImagePalette(
            dominantColor = null,
            vibrantColor = null,
            mutedColor = null
        )
        coEvery { extractPaletteUseCase(testImageUri) } returns emptyPalette
        
        viewModel.handleAction(Action.SelectImage(testImageUri))
        advanceUntilIdle()
        
        viewModel.events.test {
            viewModel.handleAction(Action.ApplyTheme)
            
            val state = viewModel.state.value
            assertFalse(state.isThemeApplied)
            
            val event = awaitItem() as Event.ShowMessage
            assertEquals("No valid colors to apply", event.message)
        }
    }
    
    @Test
    fun `resetTheme should clear all state and emit events`() = runTest {
        coEvery { extractPaletteUseCase(testImageUri) } returns testPalette
        
        // First apply a theme
        viewModel.handleAction(Action.SelectImage(testImageUri))
        advanceUntilIdle()
        viewModel.handleAction(Action.ApplyTheme)
        advanceUntilIdle()
        
        viewModel.events.test {
            viewModel.handleAction(Action.ResetTheme)
            
            val state = viewModel.state.value
            assertNull(state.selectedImageUri)
            assertNull(state.palette)
            assertFalse(state.isThemeApplied)
            assertNull(state.error)
            assertFalse(state.hasPalette)
            
            val resetEvent = awaitItem()
            assertTrue(resetEvent is Event.ThemeReset)
            
            val messageEvent = awaitItem() as Event.ShowMessage
            assertEquals("Theme reset to default", messageEvent.message)
        }
    }
    
    @Test
    fun `dismissError should clear error message`() = runTest {
        coEvery { extractPaletteUseCase(testImageUri) } throws Exception("Test error")
        
        viewModel.handleAction(Action.SelectImage(testImageUri))
        advanceUntilIdle()
        
        val stateWithError = viewModel.state.value
        assertEquals("Failed to extract colors: Test error", stateWithError.error)
        
        viewModel.handleAction(Action.DismissError)
        
        val stateAfterDismiss = viewModel.state.value
        assertNull(stateAfterDismiss.error)
    }
    
    @Test
    fun `pickNewImage should clear error and theme applied flag`() = runTest {
        coEvery { extractPaletteUseCase(testImageUri) } returns testPalette
        
        // Apply a theme first
        viewModel.handleAction(Action.SelectImage(testImageUri))
        advanceUntilIdle()
        viewModel.handleAction(Action.ApplyTheme)
        
        viewModel.handleAction(Action.PickNewImage)
        
        val state = viewModel.state.value
        assertNull(state.error)
        assertFalse(state.isThemeApplied)
    }
}
