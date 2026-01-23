package com.example.conversion.domain.usecase.rename

import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.model.RenameProgress
import com.example.conversion.domain.model.RenameStatus
import com.example.conversion.domain.repository.FileRenameRepository
import com.example.conversion.domain.usecase.base.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Use case for executing batch file rename operations with progress tracking.
 * 
 * Emits RenameProgress updates for each file being processed, allowing the UI
 * to display real-time progress and status updates.
 *
 * Features:
 * - Sequential file processing
 * - Progress emission for each file
 * - Error recovery (continues on individual failures)
 * - Real-time status updates
 *
 * Input: ExecuteBatchRenameParams (files, config)
 * Output: Flow<RenameProgress> (progress updates)
 *
 * Example:
 * ```
 * executeBatchRenameUseCase(params).collect { progress ->
 *     updateUI(progress.progressPercentage, progress.status)
 * }
 * ```
 */
class ExecuteBatchRenameUseCase @Inject constructor(
    private val fileRenameRepository: FileRenameRepository,
    private val generateFilenameUseCase: GenerateFilenameUseCase,
    private val validateFilenameUseCase: ValidateFilenameUseCase,
    dispatcher: CoroutineDispatcher
) : FlowUseCase<ExecuteBatchRenameUseCase.Params, RenameProgress>(dispatcher) {

    /**
     * Parameters for batch rename execution.
     *
     * @property files The list of files to rename
     * @property config The rename configuration to apply
     */
    data class Params(
        val files: List<FileItem>,
        val config: RenameConfig
    )

    override fun invoke(params: Params): Flow<RenameProgress> = flow {
        val (files, config) = params
        
        // Validate config before processing
        if (!config.isValid()) {
            // Emit error for first file and return
            if (files.isNotEmpty()) {
                emit(RenameProgress(
                    currentIndex = 0,
                    total = files.size,
                    currentFile = files[0],
                    status = RenameStatus.FAILED
                ))
            }
            return@flow
        }

        // Process each file sequentially
        files.forEachIndexed { index, file ->
            // Emit processing status
            emit(RenameProgress(
                currentIndex = index,
                total = files.size,
                currentFile = file,
                status = RenameStatus.PROCESSING
            ))

            try {
                // Generate new filename
                val generateParams = GenerateFilenameUseCase.Params(file, config, index)
                val newFilename = when (val result = generateFilenameUseCase(generateParams)) {
                    is com.example.conversion.domain.common.Result.Success -> result.data
                    is com.example.conversion.domain.common.Result.Error -> {
                        // Emit failure and continue to next file
                        emit(RenameProgress(
                            currentIndex = index,
                            total = files.size,
                            currentFile = file,
                            status = RenameStatus.FAILED
                        ))
                        return@forEachIndexed
                    }
                    is com.example.conversion.domain.common.Result.Loading -> {
                        // Should not happen, but handle gracefully
                        emit(RenameProgress(
                            currentIndex = index,
                            total = files.size,
                            currentFile = file,
                            status = RenameStatus.SKIPPED
                        ))
                        return@forEachIndexed
                    }
                }

                // Validate the generated filename
                val validationResult = when (val result = validateFilenameUseCase(newFilename)) {
                    is com.example.conversion.domain.common.Result.Success -> result.data
                    is com.example.conversion.domain.common.Result.Error -> {
                        // Emit failure and continue
                        emit(RenameProgress(
                            currentIndex = index,
                            total = files.size,
                            currentFile = file,
                            status = RenameStatus.FAILED
                        ))
                        return@forEachIndexed
                    }
                    is com.example.conversion.domain.common.Result.Loading -> {
                        emit(RenameProgress(
                            currentIndex = index,
                            total = files.size,
                            currentFile = file,
                            status = RenameStatus.SKIPPED
                        ))
                        return@forEachIndexed
                    }
                }

                if (!validationResult.isValid) {
                    // Invalid filename, skip this file
                    emit(RenameProgress(
                        currentIndex = index,
                        total = files.size,
                        currentFile = file,
                        status = RenameStatus.SKIPPED
                    ))
                    return@forEachIndexed
                }

                // Check for naming conflicts
                val hasConflict = fileRenameRepository.checkNameConflict(file.uri, newFilename)
                if (hasConflict) {
                    // Conflict detected, skip this file
                    emit(RenameProgress(
                        currentIndex = index,
                        total = files.size,
                        currentFile = file,
                        status = RenameStatus.SKIPPED
                    ))
                    return@forEachIndexed
                }

                // Perform the actual rename
                val renameResult = fileRenameRepository.renameFile(file.uri, newFilename)
                
                val finalStatus = when (renameResult) {
                    is com.example.conversion.domain.common.Result.Success -> RenameStatus.SUCCESS
                    is com.example.conversion.domain.common.Result.Error -> RenameStatus.FAILED
                    is com.example.conversion.domain.common.Result.Loading -> RenameStatus.SKIPPED
                }

                // Emit final status for this file
                emit(RenameProgress(
                    currentIndex = index,
                    total = files.size,
                    currentFile = file,
                    status = finalStatus
                ))

            } catch (e: Exception) {
                // Handle any unexpected errors
                emit(RenameProgress(
                    currentIndex = index,
                    total = files.size,
                    currentFile = file,
                    status = RenameStatus.FAILED
                ))
            }
        }
    }.flowOn(dispatcher)
}
