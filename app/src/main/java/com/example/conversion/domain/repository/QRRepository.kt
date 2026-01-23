package com.example.conversion.domain.repository

import android.graphics.Bitmap
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.PresetQRData
import com.example.conversion.domain.model.RenameTemplate

/**
 * Repository interface for QR code operations.
 * Handles encoding templates to QR codes and decoding QR codes to templates.
 */
interface QRRepository {
    /**
     * Generates a QR code bitmap from a RenameTemplate.
     *
     * @param template The template to encode
     * @param size The size of the QR code bitmap (width and height in pixels)
     * @return Result containing the QR code Bitmap or an error
     */
    suspend fun generateQRCode(template: RenameTemplate, size: Int = 512): Result<Bitmap>

    /**
     * Parses a QR code bitmap and extracts a RenameTemplate.
     *
     * @param bitmap The QR code bitmap to decode
     * @return Result containing the decoded RenameTemplate or an error
     */
    suspend fun parseQRCode(bitmap: Bitmap): Result<RenameTemplate>

    /**
     * Encodes a RenameTemplate to a JSON string.
     * Useful for debugging or manual sharing.
     *
     * @param template The template to encode
     * @return Result containing the JSON string or an error
     */
    suspend fun encodeToJson(template: RenameTemplate): Result<String>

    /**
     * Decodes a JSON string to a RenameTemplate.
     *
     * @param json The JSON string to decode
     * @return Result containing the decoded RenameTemplate or an error
     */
    suspend fun decodeFromJson(json: String): Result<RenameTemplate>
}
