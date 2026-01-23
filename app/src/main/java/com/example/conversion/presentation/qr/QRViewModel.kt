package com.example.conversion.presentation.qr

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.example.conversion.domain.model.RenameTemplate
import com.example.conversion.domain.usecase.qr.GenerateQRCodeUseCase
import com.example.conversion.domain.usecase.qr.ParseQRCodeUseCase
import com.example.conversion.domain.usecase.template.SaveTemplateUseCase
import com.example.conversion.presentation.base.BaseViewModel
import com.example.conversion.presentation.qr.QRContract.Action
import com.example.conversion.presentation.qr.QRContract.Event
import com.example.conversion.presentation.qr.QRContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for QR code screen.
 * Manages QR code generation, scanning, and template import.
 */
@HiltViewModel
class QRViewModel @Inject constructor(
    private val generateQRCodeUseCase: GenerateQRCodeUseCase,
    private val parseQRCodeUseCase: ParseQRCodeUseCase,
    private val saveTemplateUseCase: SaveTemplateUseCase
) : BaseViewModel<State, Event>(State()) {

    /**
     * Handles user actions and updates state accordingly.
     */
    fun handleAction(action: Action) {
        when (action) {
            is Action.GenerateQR -> generateQR(action.template, action.size)
            is Action.ShareQR -> shareQR(action.bitmap)
            is Action.StartScanning -> startScanning()
            is Action.ParseQR -> parseQR(action.bitmap)
            is Action.ImportTemplate -> importTemplate(action.template)
            is Action.DismissImportDialog -> dismissImportDialog()
            is Action.ClearError -> clearError()
            is Action.ChangeQRSize -> changeQRSize(action.size)
        }
    }

    /**
     * Generates a QR code from a template.
     */
    private fun generateQR(template: RenameTemplate, size: Int) {
        updateState { 
            copy(
                selectedTemplate = template,
                isGenerating = true,
                error = null,
                qrBitmap = null,
                qrSize = size
            ) 
        }

        viewModelScope.launch {
            executeUseCase(
                block = { 
                    generateQRCodeUseCase(
                        GenerateQRCodeUseCase.GenerateQRParams(template, size)
                    )
                },
                onSuccess = { bitmap ->
                    updateState { 
                        copy(
                            qrBitmap = bitmap,
                            isGenerating = false
                        ) 
                    }
                    sendEvent(Event.QRGenerated(bitmap))
                },
                onError = { error ->
                    updateState {
                        copy(
                            isGenerating = false,
                            error = "Failed to generate QR code: $error"
                        )
                    }
                    sendEvent(Event.Error("Failed to generate QR code: $error"))
                }
            )
        }
    }

    /**
     * Shares a QR code bitmap.
     * Note: Actual sharing implementation should be handled in the UI layer
     * as it requires Context for creating share intent.
     */
    private fun shareQR(bitmap: Bitmap) {
        // Validation - actual sharing handled by UI
        if (bitmap.width == 0 || bitmap.height == 0) {
            updateState { copy(error = "Invalid QR code") }
            sendEvent(Event.Error("Invalid QR code"))
            return
        }

        sendEvent(Event.QRShared)
        sendEvent(Event.ShowMessage("Use the share button to share this QR code"))
    }

    /**
     * Starts QR code scanning.
     * Note: This is a mock implementation. In production, this would trigger
     * camera-based scanning using CameraX + ZXing.
     */
    private fun startScanning() {
        updateState { 
            copy(
                isScanning = true,
                error = null,
                scannedTemplate = null
            ) 
        }

        // Mock: In production, this would open camera scanner
        sendEvent(Event.ShowMessage("Mock: Select a QR code image to scan"))
    }

    /**
     * Parses a QR code bitmap to extract template.
     */
    private fun parseQR(bitmap: Bitmap) {
        updateState { 
            copy(
                isScanning = true,
                error = null
            ) 
        }

        viewModelScope.launch {
            executeUseCase(
                block = { parseQRCodeUseCase(bitmap) },
                onSuccess = { template ->
                    updateState { 
                        copy(
                            scannedTemplate = template,
                            isScanning = false,
                            showImportDialog = true
                        ) 
                    }
                    sendEvent(Event.TemplateScanned(template))
                },
                onError = { error ->
                    updateState {
                        copy(
                            isScanning = false,
                            error = "Failed to scan QR code: $error"
                        )
                    }
                    sendEvent(Event.Error("Failed to scan QR code: $error"))
                }
            )
        }
    }

    /**
     * Imports a scanned template.
     */
    private fun importTemplate(template: RenameTemplate) {
        viewModelScope.launch {
            executeUseCase(
                block = { saveTemplateUseCase(template) },
                onSuccess = {
                    updateState { 
                        copy(
                            showImportDialog = false,
                            scannedTemplate = null
                        ) 
                    }
                    sendEvent(Event.TemplateImported(template))
                    sendEvent(Event.ShowMessage("Template '${template.name}' imported successfully"))
                },
                onError = { error ->
                    updateState {
                        copy(error = "Failed to import template: $error")
                    }
                    sendEvent(Event.Error("Failed to import template: $error"))
                }
            )
        }
    }

    /**
     * Dismisses the import dialog.
     */
    private fun dismissImportDialog() {
        updateState { 
            copy(
                showImportDialog = false,
                scannedTemplate = null
            ) 
        }
    }

    /**
     * Clears error state.
     */
    private fun clearError() {
        updateState { copy(error = null) }
    }

    /**
     * Changes QR code size and regenerates if template is selected.
     */
    private fun changeQRSize(size: Int) {
        val template = currentState.selectedTemplate
        if (template != null) {
            generateQR(template, size)
        } else {
            updateState { copy(qrSize = size) }
        }
    }
}
