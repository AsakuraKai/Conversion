package com.example.conversion.domain.usecase.rename

import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Use case for generating a new filename based on rename configuration.
 * Applies prefix, sequential numbering with padding, and preserves extension.
 *
 * Input: GenerateFilenameParams (file, config, index)
 * Output: String (generated filename)
 *
 * Example:
 * - prefix = "photo", startNumber = 1, digitCount = 3, index = 0
 * - Result: "photo001.jpg"
 */
class GenerateFilenameUseCase @Inject constructor() : BaseUseCase<GenerateFilenameUseCase.Params, String>(Dispatchers.Default) {

    /**
     * Parameters for filename generation.
     *
     * @property fileItem The original file item
     * @property config The rename configuration
     * @property index The index of the file in the batch (0-based)
     */
    data class Params(
        val fileItem: FileItem,
        val config: RenameConfig,
        val index: Int
    )

    override suspend fun execute(params: Params): String {
        val (fileItem, config, index) = params

        // Calculate the sequential number
        val sequentialNumber = config.startNumber + index

        // Pad the number with leading zeros based on digitCount
        val paddedNumber = sequentialNumber.toString().padStart(config.digitCount, '0')

        // Build the new filename
        val baseFilename = "${config.prefix}$paddedNumber"

        // Add extension if preserveExtension is true
        return if (config.preserveExtension && fileItem.extension.isNotEmpty()) {
            "$baseFilename.${fileItem.extension}"
        } else {
            baseFilename
        }
    }
}
