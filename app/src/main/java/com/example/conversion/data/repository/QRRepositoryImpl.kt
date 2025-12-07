package com.example.conversion.data.repository

import android.graphics.Bitmap
import android.graphics.Color
import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.PresetQRData
import com.example.conversion.domain.model.RenameTemplate
import com.example.conversion.domain.repository.QRRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock implementation of QRRepository for development.
 * Simulates QR code generation and parsing without requiring ZXing library.
 *
 * STRATEGIC IMPLEMENTATION:
 * - Uses simulated QR code generation with pattern-based bitmaps
 * - JSON serialization/deserialization fully functional
 * - Provides realistic QR code behavior for UI testing
 * - No external dependencies required (ZXing)
 *
 * PRODUCTION UPGRADE:
 * - Integrate ZXing library for real QR code generation
 * - Implement actual QR code encoding/decoding
 * - Add error correction levels
 * - Support various QR code formats
 *
 * @see QRRepository
 */
@Singleton
class QRRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : QRRepository {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    // Store generated QR data for mock decoding
    private val qrDataCache = mutableMapOf<Int, String>()

    /**
     * Generates a simulated QR code bitmap from a RenameTemplate.
     * Creates a pattern-based bitmap that represents the template data.
     */
    override suspend fun generateQRCode(template: RenameTemplate, size: Int): Result<Bitmap> =
        withContext(ioDispatcher) {
            try {
                // Convert template to QR data
                val qrData = PresetQRData.fromRenameTemplate(template)

                // Serialize to JSON
                val jsonString = json.encodeToString(qrData)

                // Generate mock QR code bitmap
                val bitmap = generateMockQRBitmap(jsonString, size)

                // Cache the data for decoding
                val bitmapHash = bitmap.hashCode()
                qrDataCache[bitmapHash] = jsonString

                Result.Success(bitmap)
            } catch (e: Exception) {
                Result.Error("Failed to generate QR code: ${e.message}")
            }
        }

    /**
     * Parses a simulated QR code bitmap and extracts a RenameTemplate.
     * Decodes the bitmap using cached data.
     */
    override suspend fun parseQRCode(bitmap: Bitmap): Result<RenameTemplate> =
        withContext(ioDispatcher) {
            try {
                // Get cached data using bitmap hash
                val bitmapHash = bitmap.hashCode()
                val jsonString = qrDataCache[bitmapHash]
                    ?: return@withContext Result.Error("QR code not found in cache. In production, this would use ZXing to decode.")

                // Deserialize from JSON
                val qrData = json.decodeFromString<PresetQRData>(jsonString)

                // Validate QR data
                if (!qrData.isValid()) {
                    return@withContext Result.Error("Invalid QR code data")
                }

                // Convert to RenameTemplate
                val template = qrData.toRenameTemplate()

                Result.Success(template)
            } catch (e: Exception) {
                Result.Error("Failed to parse QR code: ${e.message}")
            }
        }

    /**
     * Encodes a RenameTemplate to a JSON string.
     */
    override suspend fun encodeToJson(template: RenameTemplate): Result<String> =
        withContext(ioDispatcher) {
            try {
                val qrData = PresetQRData.fromRenameTemplate(template)
                val jsonString = json.encodeToString(qrData)
                Result.Success(jsonString)
            } catch (e: Exception) {
                Result.Error("Failed to encode template to JSON: ${e.message}")
            }
        }

    /**
     * Decodes a JSON string to a RenameTemplate.
     */
    override suspend fun decodeFromJson(json: String): Result<RenameTemplate> =
        withContext(ioDispatcher) {
            try {
                val qrData = this@QRRepositoryImpl.json.decodeFromString<PresetQRData>(json)

                if (!qrData.isValid()) {
                    return@withContext Result.Error("Invalid template data in JSON")
                }

                val template = qrData.toRenameTemplate()
                Result.Success(template)
            } catch (e: Exception) {
                Result.Error("Failed to decode JSON to template: ${e.message}")
            }
        }

    /**
     * Generates a mock QR code bitmap with a pattern based on the data.
     * In production, this would use ZXing to generate a real QR code.
     */
    private fun generateMockQRBitmap(data: String, size: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        // Create a simple pattern based on data hash
        val hash = data.hashCode()
        val moduleSize = size / 25 // 25x25 grid (like QR code)

        for (y in 0 until 25) {
            for (x in 0 until 25) {
                val index = y * 25 + x
                val bit = (hash shr (index % 32)) and 1
                val color = if (bit == 1) Color.BLACK else Color.WHITE

                // Fill module
                for (py in 0 until moduleSize) {
                    for (px in 0 until moduleSize) {
                        val pixelX = x * moduleSize + px
                        val pixelY = y * moduleSize + py
                        if (pixelX < size && pixelY < size) {
                            bitmap.setPixel(pixelX, pixelY, color)
                        }
                    }
                }
            }
        }

        // Add corner markers (like real QR codes)
        drawFinderPattern(bitmap, 0, 0, moduleSize)
        drawFinderPattern(bitmap, size - 7 * moduleSize, 0, moduleSize)
        drawFinderPattern(bitmap, 0, size - 7 * moduleSize, moduleSize)

        return bitmap
    }

    /**
     * Draws a finder pattern (corner square) on the bitmap.
     */
    private fun drawFinderPattern(bitmap: Bitmap, startX: Int, startY: Int, moduleSize: Int) {
        // Outer square (7x7 modules, black)
        fillRect(bitmap, startX, startY, 7 * moduleSize, 7 * moduleSize, Color.BLACK)
        // Inner white square (5x5 modules)
        fillRect(bitmap, startX + moduleSize, startY + moduleSize, 5 * moduleSize, 5 * moduleSize, Color.WHITE)
        // Center black square (3x3 modules)
        fillRect(bitmap, startX + 2 * moduleSize, startY + 2 * moduleSize, 3 * moduleSize, 3 * moduleSize, Color.BLACK)
    }

    /**
     * Fills a rectangle on the bitmap.
     */
    private fun fillRect(bitmap: Bitmap, x: Int, y: Int, width: Int, height: Int, color: Int) {
        val maxX = minOf(x + width, bitmap.width)
        val maxY = minOf(y + height, bitmap.height)
        for (py in y until maxY) {
            for (px in x until maxX) {
                if (px >= 0 && py >= 0) {
                    bitmap.setPixel(px, py, color)
                }
            }
        }
    }
}
