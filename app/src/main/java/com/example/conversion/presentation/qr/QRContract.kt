package com.example.conversion.presentation.qr

import android.graphics.Bitmap
import com.example.conversion.domain.model.RenameTemplate

/**
 * QR Code feature contract defining State, Events, and Actions.
 * Follows MVI pattern for unidirectional data flow.
 */
object QRContract {

    /**
     * UI State for QR code screen.
     */
    data class State(
        val selectedTemplate: RenameTemplate? = null,
        val qrBitmap: Bitmap? = null,
        val isGenerating: Boolean = false,
        val isScanning: Boolean = false,
        val error: String? = null,
        val scannedTemplate: RenameTemplate? = null,
        val showImportDialog: Boolean = false,
        val qrSize: Int = DEFAULT_QR_SIZE
    ) {
        /**
         * Whether QR code can be generated.
         */
        val canGenerateQR: Boolean
            get() = selectedTemplate != null && !isGenerating

        /**
         * Whether QR code can be shared.
         */
        val canShare: Boolean
            get() = qrBitmap != null && !isGenerating

        /**
         * Whether template can be imported.
         */
        val canImportTemplate: Boolean
            get() = scannedTemplate != null && scannedTemplate.isValid()
    }

    /**
     * One-time events sent from ViewModel to UI.
     */
    sealed class Event {
        /**
         * QR code was successfully generated.
         */
        data class QRGenerated(val bitmap: Bitmap) : Event()

        /**
         * QR code was successfully shared.
         */
        object QRShared : Event()

        /**
         * Template was successfully scanned.
         */
        data class TemplateScanned(val template: RenameTemplate) : Event()

        /**
         * Template was successfully imported.
         */
        data class TemplateImported(val template: RenameTemplate) : Event()

        /**
         * Error occurred during QR operation.
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
         * Generate QR code from template.
         */
        data class GenerateQR(val template: RenameTemplate, val size: Int = DEFAULT_QR_SIZE) : Action()

        /**
         * Share QR code bitmap.
         */
        data class ShareQR(val bitmap: Bitmap) : Action()

        /**
         * Start scanning QR code (mock - uses file picker).
         */
        object StartScanning : Action()

        /**
         * Parse scanned QR code bitmap.
         */
        data class ParseQR(val bitmap: Bitmap) : Action()

        /**
         * Import scanned template.
         */
        data class ImportTemplate(val template: RenameTemplate) : Action()

        /**
         * Dismiss import dialog.
         */
        object DismissImportDialog : Action()

        /**
         * Clear error state.
         */
        object ClearError : Action()

        /**
         * Change QR code size.
         */
        data class ChangeQRSize(val size: Int) : Action()
    }

    companion object {
        /**
         * Default QR code size (512x512 pixels).
         */
        const val DEFAULT_QR_SIZE = 512

        /**
         * Available QR code sizes.
         */
        val AVAILABLE_SIZES = listOf(256, 512, 768, 1024)
    }
}
