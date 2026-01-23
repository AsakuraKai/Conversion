package com.example.conversion.domain.usecase.qr

import android.graphics.Bitmap
import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.model.RenameTemplate
import com.example.conversion.domain.repository.QRRepository
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for parsing a QR code and extracting a RenameTemplate.
 * Validates the QR code data and creates a valid template.
 *
 * Input: Bitmap - The QR code bitmap to decode
 * Output: RenameTemplate - The decoded template
 */
class ParseQRCodeUseCase @Inject constructor(
    private val qrRepository: QRRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<Bitmap, RenameTemplate>(dispatcher) {

    /**
     * Executes the use case to parse a QR code.
     *
     * @param params Bitmap of the QR code
     * @return RenameTemplate decoded from the QR code
     * @throws IllegalArgumentException if bitmap is invalid or QR code cannot be decoded
     * @throws IllegalStateException if decoded data is invalid
     */
    override suspend fun execute(params: Bitmap): RenameTemplate {
        // Validate bitmap
        if (params.width == 0 || params.height == 0) {
            throw IllegalArgumentException("Invalid bitmap: Width and height must be greater than 0")
        }

        // Parse QR code
        val template = when (val result = qrRepository.parseQRCode(params)) {
            is com.example.conversion.domain.common.Result.Success -> result.data
            is com.example.conversion.domain.common.Result.Error -> {
                throw IllegalStateException("Failed to parse QR code: ${result.message}")
            }
            is com.example.conversion.domain.common.Result.Loading -> {
                throw IllegalStateException("Unexpected loading state")
            }
        }

        // Validate decoded template
        if (!template.isValid()) {
            throw IllegalStateException("QR code contains invalid template data")
        }

        return template
    }
}
