package com.example.conversion.presentation.ocr

import android.net.Uri
import com.example.conversion.domain.model.ExtractedText

/**
 * OCR Text Extraction feature contract defining State, Events, and Actions.
 * Follows MVI pattern for unidirectional data flow.
 * 
 * @author Sokchea (Frontend/UI Specialist)
 * @since CHUNK 19 - OCR Integration UI
 */
object OCRContract {

    /**
     * UI State for OCR text extraction.
     */
    data class State(
        val imageUri: Uri? = null,
        val extractedTextBlocks: List<ExtractedText> = emptyList(),
        val combinedText: String? = null,
        val isExtracting: Boolean = false,
        val error: String? = null,
        val confidenceThreshold: Float = ExtractedText.DEFAULT_CONFIDENCE_THRESHOLD,
        val showTextBlockDialog: Boolean = false,
        val selectedTextBlock: ExtractedText? = null
    ) {
        /**
         * Whether OCR extraction can be triggered.
         */
        val canExtractText: Boolean
            get() = imageUri != null && !isExtracting

        /**
         * Whether extracted text is available.
         */
        val hasExtractedText: Boolean
            get() = extractedTextBlocks.isNotEmpty() || combinedText != null

        /**
         * High confidence text blocks (90%+).
         */
        val highConfidenceBlocks: List<ExtractedText>
            get() = extractedTextBlocks.filter { 
                it.meetsThreshold(ExtractedText.HIGH_CONFIDENCE_THRESHOLD) 
            }

        /**
         * Get the best text for filename generation.
         * Prioritizes combined text or the first high-confidence block.
         */
        val suggestedFilename: String?
            get() = combinedText?.let { 
                ExtractedText(
                    text = it,
                    confidence = 1.0f,
                    boundingBox = android.graphics.Rect(0, 0, 0, 0)
                ).toFilenameFragment() 
            } ?: highConfidenceBlocks.firstOrNull()?.toFilenameFragment()
    }

    /**
     * One-time events sent from ViewModel to UI.
     */
    sealed class Event {
        /**
         * Text extraction completed successfully.
         */
        data class TextExtracted(
            val textBlocks: List<ExtractedText>,
            val combinedText: String?
        ) : Event()

        /**
         * Text block was selected for use.
         */
        data class TextBlockSelected(val text: String) : Event()

        /**
         * Error occurred during OCR operation.
         */
        data class Error(val message: String) : Event()

        /**
         * Show a message to the user.
         */
        data class ShowMessage(val message: String) : Event()
    }

    /**
     * User actions that trigger state changes.
     */
    sealed class Action {
        /**
         * Extract text from image using OCR.
         */
        data class ExtractText(
            val imageUri: Uri,
            val combineText: Boolean = false
        ) : Action()

        /**
         * Extract text with custom confidence threshold.
         */
        data class ExtractTextWithThreshold(
            val imageUri: Uri,
            val confidenceThreshold: Float,
            val combineText: Boolean = false
        ) : Action()

        /**
         * Select a specific text block to use.
         */
        data class SelectTextBlock(val textBlock: ExtractedText) : Action()

        /**
         * Use combined text for filename.
         */
        data class UseCombinedText(val text: String) : Action()

        /**
         * Show text block details dialog.
         */
        data class ShowTextBlockDetails(val textBlock: ExtractedText) : Action()

        /**
         * Dismiss text block details dialog.
         */
        object DismissTextBlockDialog : Action()

        /**
         * Change confidence threshold.
         */
        data class ChangeConfidenceThreshold(val threshold: Float) : Action()

        /**
         * Clear error state.
         */
        object ClearError : Action()

        /**
         * Clear extracted text.
         */
        object ClearExtractedText : Action()
    }
}
