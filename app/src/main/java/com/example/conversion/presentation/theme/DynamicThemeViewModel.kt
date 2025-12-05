package com.example.conversion.presentation.theme

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.usecase.theme.ExtractPaletteUseCase
import com.example.conversion.presentation.base.BaseViewModel
import com.example.conversion.presentation.theme.DynamicThemeContract.Action
import com.example.conversion.presentation.theme.DynamicThemeContract.Event
import com.example.conversion.presentation.theme.DynamicThemeContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Dynamic Theme feature.
 * Handles image selection and color palette extraction for dynamic theming.
 *
 * @property extractPaletteUseCase Use case for extracting color palette from images
 * @property ioDispatcher Dispatcher for IO operations
 */
@HiltViewModel
class DynamicThemeViewModel @Inject constructor(
    private val extractPaletteUseCase: ExtractPaletteUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<State, Event>(State()) {
    
    /**
     * Handles user actions and updates state accordingly.
     */
    fun handleAction(action: Action) {
        when (action) {
            is Action.SelectImage -> selectImage(action.imageUri)
            is Action.ApplyTheme -> applyTheme()
            is Action.ResetTheme -> resetTheme()
            is Action.DismissError -> dismissError()
            is Action.PickNewImage -> pickNewImage()
        }
    }
    
    /**
     * Processes the selected image and extracts color palette.
     */
    private fun selectImage(imageUri: Uri) {
        updateState {
            copy(
                selectedImageUri = imageUri,
                isLoading = true,
                error = null,
                palette = null,
                isThemeApplied = false
            )
        }
        
        viewModelScope.launch(ioDispatcher) {
            try {
                val palette = extractPaletteUseCase(imageUri)
                
                updateState {
                    copy(
                        palette = palette,
                        isLoading = false,
                        error = if (!palette.hasColors) {
                            "Could not extract colors from this image. Try another one."
                        } else null
                    )
                }
                
                if (palette.hasColors) {
                    sendEvent(Event.ShowMessage("Colors extracted successfully!"))
                }
            } catch (e: SecurityException) {
                updateState {
                    copy(
                        isLoading = false,
                        error = "Permission denied. Please grant storage access."
                    )
                }
                sendEvent(Event.ShowMessage("Permission denied"))
            } catch (e: IllegalArgumentException) {
                updateState {
                    copy(
                        isLoading = false,
                        error = "Invalid image. Please select a different image."
                    )
                }
                sendEvent(Event.ShowMessage("Invalid image selected"))
            } catch (e: Exception) {
                updateState {
                    copy(
                        isLoading = false,
                        error = "Failed to extract colors: ${e.message}"
                    )
                }
                sendEvent(Event.ShowMessage("Failed to extract colors"))
            }
        }
    }
    
    /**
     * Applies the extracted theme to the app.
     */
    private fun applyTheme() {
        val palette = currentState.palette
        if (palette == null || !palette.hasColors) {
            sendEvent(Event.ShowMessage("No valid colors to apply"))
            return
        }
        
        updateState {
            copy(isThemeApplied = true)
        }
        
        // TODO: Persist theme preference to DataStore
        // This will be handled by ThemeManager or similar
        
        sendEvent(Event.ThemeApplied)
        sendEvent(Event.ShowMessage("Theme applied successfully!"))
    }
    
    /**
     * Resets to the default theme.
     */
    private fun resetTheme() {
        updateState {
            copy(
                selectedImageUri = null,
                palette = null,
                isThemeApplied = false,
                error = null
            )
        }
        
        // TODO: Clear theme preference from DataStore
        
        sendEvent(Event.ThemeReset)
        sendEvent(Event.ShowMessage("Theme reset to default"))
    }
    
    /**
     * Dismisses the current error message.
     */
    private fun dismissError() {
        updateState {
            copy(error = null)
        }
    }
    
    /**
     * Prepares for picking a new image.
     */
    private fun pickNewImage() {
        updateState {
            copy(
                error = null,
                isThemeApplied = false
            )
        }
    }
}
