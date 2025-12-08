package com.example.conversion.domain.usecase.qr

import android.graphics.Bitmap
import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.model.RenameTemplate
import com.example.conversion.domain.repository.QRRepository
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for generating a QR code from a RenameTemplate.
 * Validates the template and encodes it to a QR code bitmap.
 *
 * Input: GenerateQRParams - Parameters containing template and QR code size
 * Output: Bitmap - The generated QR code bitmap
 */
class GenerateQRCodeUseCase @Inject constructor(
    private val qrRepository: QRRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<GenerateQRCodeUseCase.GenerateQRParams, Bitmap>(dispatcher) {

    /**
     * Executes the use case to generate a QR code.
     *
     * @param params Parameters containing template and size
     * @return Bitmap of the QR code
     * @throws IllegalArgumentException if template is invalid or size is invalid
     */
    override suspend fun execute(params: GenerateQRParams): Bitmap {
        // Validate template
        if (!params.template.isValid()) {
            throw IllegalArgumentException("Invalid template: Cannot generate QR code from invalid template")
        }

        // Validate QR code size
        if (params.size !in MIN_QR_SIZE..MAX_QR_SIZE) {
            throw IllegalArgumentException("QR code size must be between $MIN_QR_SIZE and $MAX_QR_SIZE pixels")
        }

        // Generate QR code
        return when (val result = qrRepository.generateQRCode(params.template, params.size)) {
            is com.example.conversion.domain.common.Result.Success -> result.data
            is com.example.conversion.domain.common.Result.Error -> {
                throw IllegalStateException("Failed to generate QR code: ${result.message}")
            }
            is com.example.conversion.domain.common.Result.Loading -> {
                throw IllegalStateException("Unexpected loading state")
            }
        }
    }

    /**
     * Parameters for QR code generation.
     *
     * @property template The template to encode
     * @property size The size of the QR code bitmap (width and height in pixels)
     */
    data class GenerateQRParams(
        val template: RenameTemplate,
        val size: Int = DEFAULT_QR_SIZE
    )

    companion object {
        /**
         * Default QR code size (512x512 pixels).
         */
        const val DEFAULT_QR_SIZE = 512

        /**
         * Minimum allowed QR code size.
         */
        const val MIN_QR_SIZE = 256

        /**
         * Maximum allowed QR code size.
         */
        const val MAX_QR_SIZE = 2048
    }
}
