package com.example.conversion.presentation.renameprogress

import androidx.lifecycle.viewModelScope
import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.model.RenameStatus
import com.example.conversion.domain.usecase.rename.ExecuteBatchRenameUseCase
import com.example.conversion.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for rename progress screen.
 * Manages the batch rename operation and tracks progress.
 *
 * Responsibilities:
 * - Execute batch rename operation
 * - Track progress in real-time
 * - Count success/failure/skipped files
 * - Handle cancellation
 * - Provide completion summary
 */
@HiltViewModel
class RenameProgressViewModel @Inject constructor(
    private val executeBatchRenameUseCase: ExecuteBatchRenameUseCase
) : BaseViewModel<RenameProgressContract.State, RenameProgressContract.Event>(
    RenameProgressContract.State()
) {

    private var renameJob: Job? = null
    private val failedFiles = mutableListOf<FileItem>()

    /**
     * Handles user actions.
     */
    fun handleAction(action: RenameProgressContract.Action) {
        when (action) {
            is RenameProgressContract.Action.StartRename -> {
                startRename(action.files, action.config)
            }
            is RenameProgressContract.Action.CancelRename -> {
                cancelRename()
            }
            is RenameProgressContract.Action.RetryFailed -> {
                retryFailed()
            }
            is RenameProgressContract.Action.AcknowledgeCompletion -> {
                acknowledgeCompletion()
            }
            is RenameProgressContract.Action.ClearError -> {
                clearError()
            }
        }
    }

    /**
     * Starts the batch rename operation.
     */
    private fun startRename(files: List<FileItem>, config: RenameConfig) {
        // Don't start if already processing
        if (currentState.isProcessing) {
            sendEvent(RenameProgressContract.Event.ShowMessage("Rename already in progress"))
            return
        }

        // Reset state
        updateState {
            copy(
                successCount = 0,
                failedCount = 0,
                skippedCount = 0,
                isProcessing = true,
                isComplete = false,
                isCancelled = false,
                error = null
            )
        }
        failedFiles.clear()

        // Execute rename operation
        renameJob = viewModelScope.launch {
            try {
                val params = ExecuteBatchRenameUseCase.Params(files, config)
                
                executeBatchRenameUseCase(params).collect { progress ->
                    // Update progress
                    updateState { copy(progress = progress) }

                    // Count results based on status
                    when (progress.status) {
                        RenameStatus.SUCCESS -> {
                            updateState { copy(successCount = successCount + 1) }
                        }
                        RenameStatus.FAILED -> {
                            updateState { copy(failedCount = failedCount + 1) }
                            failedFiles.add(progress.currentFile)
                        }
                        RenameStatus.SKIPPED -> {
                            updateState { copy(skippedCount = skippedCount + 1) }
                        }
                        RenameStatus.PROCESSING -> {
                            // Just update progress, no counting
                        }
                    }

                    // Check if this is the last file
                    if (progress.isLastFile && progress.status != RenameStatus.PROCESSING) {
                        handleCompletion()
                    }
                }
            } catch (e: Exception) {
                handleError(e)
                updateState {
                    copy(
                        isProcessing = false,
                        isComplete = true,
                        error = "Rename operation failed: ${e.message}"
                    )
                }
                sendEvent(RenameProgressContract.Event.ShowMessage(
                    "Rename failed: ${e.message}"
                ))
            }
        }
    }

    /**
     * Handles completion of the rename operation.
     */
    private fun handleCompletion() {
        updateState {
            copy(
                isProcessing = false,
                isComplete = true
            )
        }

        val state = currentState
        sendEvent(
            RenameProgressContract.Event.ShowCompletion(
                total = state.totalFiles,
                successful = state.successCount,
                failed = state.failedCount,
                skipped = state.skippedCount
            )
        )
    }

    /**
     * Cancels the ongoing rename operation.
     */
    private fun cancelRename() {
        if (currentState.canCancel) {
            renameJob?.cancel()
            renameJob = null
            
            updateState {
                copy(
                    isProcessing = false,
                    isCancelled = true
                )
            }
            
            sendEvent(RenameProgressContract.Event.OperationCancelled)
            sendEvent(RenameProgressContract.Event.ShowMessage("Rename operation cancelled"))
        }
    }

    /**
     * Retries rename for failed files.
     */
    private fun retryFailed() {
        if (failedFiles.isNotEmpty() && !currentState.isProcessing) {
            val config = currentState.progress?.let {
                // Need to extract config from somewhere or pass it separately
                // For now, show message that retry is not yet implemented
                sendEvent(RenameProgressContract.Event.ShowMessage(
                    "Retry feature will be available in a future update"
                ))
            }
        }
    }

    /**
     * Acknowledges completion and navigates back.
     */
    private fun acknowledgeCompletion() {
        sendEvent(RenameProgressContract.Event.NavigateBack)
    }

    /**
     * Clears error message.
     */
    private fun clearError() {
        updateState { copy(error = null) }
    }

    override fun handleError(error: Throwable) {
        super.handleError(error)
        updateState {
            copy(
                error = error.message ?: "Unknown error occurred",
                isProcessing = false
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        renameJob?.cancel()
    }
}
