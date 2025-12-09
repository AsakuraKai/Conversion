package com.example.conversion.presentation.ocr

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.conversion.domain.model.ExtractedText
import com.example.conversion.domain.usecase.ocr.ExtractTextFromImageUseCase
import com.example.conversion.presentation.base.BaseViewModel
import com.example.conversion.presentation.ocr.OCRContract.Action
import com.example.conversion.presentation.ocr.OCRContract.Event
import com.example.conversion.presentation.ocr.OCRContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for OCR text extraction screen.
 * Manages text extraction from images and text block selection.
 * 
 * @author Sokchea (Frontend/UI Specialist)
 * @since CHUNK 19 - OCR Integration UI
 */
@HiltViewModel
class OCRViewModel @Inject constructor(
    private val extractTextFromImageUseCase: ExtractTextFromImageUseCase
) : BaseViewModel<State, Event>(State()) {

    /**
     * Handles user actions and updates state accordingly.
     */
    fun handleAction(action: Action) {
        when (action) {
            is Action.ExtractText -> extractText(
                imageUri = action.imageUri,
                confidenceThreshold = currentState.confidenceThreshold,
                combineText = action.combineText
            )
            is Action.ExtractTextWithThreshold -> extractText(
                imageUri = action.imageUri,
                confidenceThreshold = action.confidenceThreshold,
                combineText = action.combineText
            )
            is Action.SelectTextBlock -> selectTextBlock(action.textBlock)
            is Action.UseCombinedText -> useCombinedText(action.text)
            is Action.ShowTextBlockDetails -> showTextBlockDetails(action.textBlock)
            is Action.DismissTextBlockDialog -> dismissTextBlockDialog()
            is Action.ChangeConfidenceThreshold -> changeConfidenceThreshold(action.threshold)
            is Action.ClearError -> clearError()
            is Action.ClearExtractedText -> clearExtractedText()
        }
    }

    /**
     * Extracts text from an image using OCR.
     */
    private fun extractText(
        imageUri: Uri,
        confidenceThreshold: Float,
        combineText: Boolean
    ) {
        updateState {
            copy(
                imageUri = imageUri,
                isExtracting = true,
                error = null,
                extractedTextBlocks = emptyList(),
                combinedText = null,
                confidenceThreshold = confidenceThreshold
            )
        }

        viewModelScope.launch {
            executeUseCase(
                block = {
                    extractTextFromImageUseCase(
                        ExtractTextFromImageUseCase.Params(
                            imageUri = imageUri,
                            confidenceThreshold = confidenceThreshold,
                            combineText = combineText
                        )
                    )
                },
                onSuccess = { textBlocks ->
                    if (combineText && textBlocks.isNotEmpty()) {
                        // Extract combined text from first block
                        val combined = textBlocks.first().text
                        updateState {
                            copy(
                                combinedText = combined,
                                extractedTextBlocks = emptyList(),
                                isExtracting = false
                            )
                        }
                        sendEvent(Event.TextExtracted(emptyList(), combined))
                    } else {
                        // Individual text blocks
                        updateState {
                            copy(
                                extractedTextBlocks = textBlocks,
                                combinedText = null,
                                isExtracting = false
                            )
                        }
                        sendEvent(Event.TextExtracted(textBlocks, null))
                    }
                    
                    if (textBlocks.isEmpty()) {
                        sendEvent(Event.ShowMessage("No text detected in image"))
                    }
                },
                onError = { error ->
                    updateState {
                        copy(
                            isExtracting = false,
                            error = "Failed to extract text: $error"
                        )
                    }
                    sendEvent(Event.Error("Failed to extract text: $error"))
                }
            )
        }
    }

    /**
     * Selects a specific text block to use.
     */
    private fun selectTextBlock(textBlock: ExtractedText) {
        val sanitizedText = textBlock.toFilenameFragment()
        updateState {
            copy(
                selectedTextBlock = textBlock,
                showTextBlockDialog = false
            )
        }
        sendEvent(Event.TextBlockSelected(sanitizedText))
    }

    /**
     * Uses combined text for filename.
     */
    private fun useCombinedText(text: String) {
        val sanitizedText = ExtractedText(
            text = text,
            confidence = 1.0f,
            boundingBox = android.graphics.Rect(0, 0, 0, 0)
        ).toFilenameFragment()
        
        sendEvent(Event.TextBlockSelected(sanitizedText))
    }

    /**
     * Shows text block details dialog.
     */
    private fun showTextBlockDetails(textBlock: ExtractedText) {
        updateState {
            copy(
                showTextBlockDialog = true,
                selectedTextBlock = textBlock
            )
        }
    }

    /**
     * Dismisses text block details dialog.
     */
    private fun dismissTextBlockDialog() {
        updateState {
            copy(
                showTextBlockDialog = false,
                selectedTextBlock = null
            )
        }
    }

    /**
     * Changes the confidence threshold.
     */
    private fun changeConfidenceThreshold(threshold: Float) {
        if (threshold !in 0.0f..1.0f) {
            updateState { copy(error = "Invalid confidence threshold: must be between 0.0 and 1.0") }
            sendEvent(Event.Error("Invalid confidence threshold"))
            return
        }

        updateState { copy(confidenceThreshold = threshold) }
    }

    /**
     * Clears error state.
     */
    private fun clearError() {
        updateState { copy(error = null) }
    }

    /**
     * Clears extracted text.
     */
    private fun clearExtractedText() {
        updateState {
            copy(
                extractedTextBlocks = emptyList(),
                combinedText = null,
                selectedTextBlock = null,
                error = null
            )
        }
    }
}
