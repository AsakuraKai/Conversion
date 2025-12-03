package com.example.conversion.domain.usecase.preview

import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.model.PreviewItem
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.usecase.base.BaseUseCase
import com.example.conversion.domain.usecase.rename.GenerateFilenameUseCase
import com.example.conversion.domain.usecase.rename.ValidateFilenameUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Use case for generating a preview of batch rename operations.
 * Shows users how files will be renamed before executing the operation.
 * Detects and reports conflicts (duplicate names, invalid names, etc.).
 *
 * Input: Params (list of files, rename configuration)
 * Output: List<PreviewItem> (preview for each file with conflict detection)
 */
class GeneratePreviewUseCase @Inject constructor(
    private val generateFilenameUseCase: GenerateFilenameUseCase,
    private val validateFilenameUseCase: ValidateFilenameUseCase,
    dispatcher: CoroutineDispatcher = Dispatchers.Default
) : BaseUseCase<GeneratePreviewUseCase.Params, List<PreviewItem>>(dispatcher) {

    /**
     * Parameters for preview generation.
     *
     * @property files List of files to preview
     * @property config Rename configuration to apply
     */
    data class Params(
        val files: List<FileItem>,
        val config: RenameConfig
    )

    override suspend fun execute(params: Params): List<PreviewItem> {
        val (files, config) = params

        // First, validate the rename config itself
        if (!config.isValid()) {
            val error = config.getValidationError() ?: "Invalid configuration"
            // Return all files with the same error
            return files.map { file ->
                PreviewItem.withConflict(
                    original = file,
                    previewName = file.name,
                    reason = error
                )
            }
        }

        // Generate preview names for all files
        val previews = mutableListOf<PreviewItem>()
        val generatedNames = mutableMapOf<String, Int>() // Track name occurrences (case-insensitive)

        files.forEachIndexed { index, file ->
            // Generate the new filename
            val generateResult = generateFilenameUseCase(
                GenerateFilenameUseCase.Params(
                    fileItem = file,
                    config = config,
                    index = index
                )
            )

            val newName = when (generateResult) {
                is com.example.conversion.domain.common.Result.Success -> generateResult.data
                is com.example.conversion.domain.common.Result.Error -> {
                    previews.add(
                        PreviewItem.withConflict(
                            original = file,
                            previewName = file.name,
                            reason = "Failed to generate filename: ${generateResult.exception.message}"
                        )
                    )
                    return@forEachIndexed
                }
                is com.example.conversion.domain.common.Result.Loading -> {
                    previews.add(
                        PreviewItem.withConflict(
                            original = file,
                            previewName = file.name,
                            reason = "Unexpected loading state"
                        )
                    )
                    return@forEachIndexed
                }
            }

            // Validate the generated filename
            val validationResult = validateFilenameUseCase(newName)
            
            val preview = when (validationResult) {
                is com.example.conversion.domain.common.Result.Success -> {
                    val validation = validationResult.data
                    
                    if (!validation.isValid) {
                        // Invalid filename
                        PreviewItem.withConflict(
                            original = file,
                            previewName = newName,
                            reason = validation.errorMessage ?: "Invalid filename"
                        )
                    } else {
                        // Valid filename, but check for duplicates
                        val nameLowercase = newName.lowercase()
                        val occurrences = generatedNames.getOrDefault(nameLowercase, 0)
                        
                        if (occurrences > 0) {
                            // Duplicate detected
                            PreviewItem.withConflict(
                                original = file,
                                previewName = newName,
                                reason = "Duplicate name: '$newName' already exists in batch"
                            )
                        } else {
                            // Success - no conflicts
                            PreviewItem.success(
                                original = file,
                                previewName = newName
                            )
                        }
                    }
                }
                is com.example.conversion.domain.common.Result.Error -> {
                    PreviewItem.withConflict(
                        original = file,
                        previewName = newName,
                        reason = "Validation error: ${validationResult.exception.message}"
                    )
                }
                is com.example.conversion.domain.common.Result.Loading -> {
                    PreviewItem.withConflict(
                        original = file,
                        previewName = newName,
                        reason = "Unexpected loading state"
                    )
                }
            }

            // Track this name for duplicate detection
            if (!preview.hasConflict) {
                generatedNames[newName.lowercase()] = generatedNames.getOrDefault(newName.lowercase(), 0) + 1
            }

            previews.add(preview)
        }

        return previews
    }
}
