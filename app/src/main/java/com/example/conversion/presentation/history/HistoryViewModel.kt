package com.example.conversion.presentation.history

import androidx.lifecycle.viewModelScope
import com.example.conversion.domain.model.RenameOperation
import com.example.conversion.domain.usecase.history.ClearHistoryUseCase
import com.example.conversion.domain.usecase.history.GetHistoryUseCase
import com.example.conversion.domain.usecase.history.ObserveHistoryUseCase
import com.example.conversion.domain.usecase.history.RedoRenameUseCase
import com.example.conversion.domain.usecase.history.UndoRenameUseCase
import com.example.conversion.presentation.base.BaseViewModel
import com.example.conversion.presentation.history.HistoryContract.Action
import com.example.conversion.presentation.history.HistoryContract.Event
import com.example.conversion.presentation.history.HistoryContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for history management screen.
 * Manages undo/redo operations and history display.
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val observeHistoryUseCase: ObserveHistoryUseCase,
    private val undoRenameUseCase: UndoRenameUseCase,
    private val redoRenameUseCase: RedoRenameUseCase,
    private val getHistoryUseCase: GetHistoryUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase
) : BaseViewModel<State, Event>(State()) {

    init {
        observeHistory()
    }

    /**
     * Handles user actions and updates state accordingly.
     */
    fun handleAction(action: Action) {
        when (action) {
            is Action.LoadHistory -> loadHistory()
            is Action.Undo -> undo()
            is Action.Redo -> redo()
            is Action.UndoSpecific -> undoSpecific(action.operationId)
            is Action.ShowClearConfirmation -> showClearConfirmation()
            is Action.HideClearConfirmation -> hideClearConfirmation()
            is Action.ConfirmClear -> confirmClear()
            is Action.SelectOperation -> selectOperation(action.operation)
            is Action.ClearError -> clearError()
        }
    }

    /**
     * Observes history changes in real-time.
     */
    private fun observeHistory() {
        observeHistoryUseCase()
            .onEach { operationHistory ->
                updateState {
                    copy(
                        history = operationHistory,
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Loads the complete history.
     */
    private fun loadHistory() {
        updateState { copy(isLoading = true, error = null) }

        viewModelScope.launch {
            executeUseCase(
                block = { getHistoryUseCase(Unit) },
                onSuccess = { operations ->
                    // History is already being observed via observeHistory()
                    // This is just for explicit reload if needed
                    updateState { copy(isLoading = false) }
                },
                onError = { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load history"
                        )
                    }
                    sendEvent(Event.ShowError("Failed to load history"))
                }
            )
        }
    }

    /**
     * Undoes the most recent operation.
     */
    private fun undo() {
        val operation = currentState.nextUndoOperation
        if (operation == null) {
            sendEvent(Event.ShowError("No operation to undo"))
            return
        }

        updateState { copy(isLoading = true, error = null) }

        viewModelScope.launch {
            executeUseCase(
                block = { undoRenameUseCase(operation) },
                onSuccess = { _ ->
                    updateState { copy(isLoading = false) }
                    sendEvent(Event.OperationUndone(operation))
                    sendEvent(Event.ShowSuccess("Renamed back to \"${operation.originalName}\""))
                },
                onError = { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            error = error.message ?: "Failed to undo operation"
                        )
                    }
                    sendEvent(Event.ShowError("Failed to undo: ${error.message}"))
                }
            )
        }
    }

    /**
     * Redoes the most recently undone operation.
     */
    private fun redo() {
        val operation = currentState.nextRedoOperation
        if (operation == null) {
            sendEvent(Event.ShowError("No operation to redo"))
            return
        }

        updateState { copy(isLoading = true, error = null) }

        viewModelScope.launch {
            executeUseCase(
                block = { redoRenameUseCase(operation) },
                onSuccess = { _ ->
                    updateState { copy(isLoading = false) }
                    sendEvent(Event.OperationRedone(operation))
                    sendEvent(Event.ShowSuccess("Renamed to \"${operation.newName}\""))
                },
                onError = { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            error = error.message ?: "Failed to redo operation"
                        )
                    }
                    sendEvent(Event.ShowError("Failed to redo: ${error.message}"))
                }
            )
        }
    }

    /**
     * Undoes a specific operation by ID.
     */
    private fun undoSpecific(operationId: String) {
        val operation = currentState.displayedOperations.find { it.id == operationId }
        if (operation == null) {
            sendEvent(Event.ShowError("Operation not found"))
            return
        }

        updateState { copy(isLoading = true, error = null) }

        viewModelScope.launch {
            executeUseCase(
                block = { undoRenameUseCase(operation) },
                onSuccess = { _ ->
                    updateState { copy(isLoading = false) }
                    sendEvent(Event.OperationUndone(operation))
                    sendEvent(Event.ShowSuccess("Renamed back to \"${operation.originalName}\""))
                },
                onError = { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            error = error.message ?: "Failed to undo operation"
                        )
                    }
                    sendEvent(Event.ShowError("Failed to undo: ${error.message}"))
                }
            )
        }
    }

    /**
     * Shows clear history confirmation dialog.
     */
    private fun showClearConfirmation() {
        updateState { copy(showClearConfirmation = true) }
    }

    /**
     * Hides clear history confirmation dialog.
     */
    private fun hideClearConfirmation() {
        updateState { copy(showClearConfirmation = false) }
    }

    /**
     * Confirms clearing all history.
     */
    private fun confirmClear() {
        updateState {
            copy(
                isLoading = true,
                error = null,
                showClearConfirmation = false
            )
        }

        viewModelScope.launch {
            executeUseCase(
                block = { clearHistoryUseCase(Unit) },
                onSuccess = {
                    updateState { copy(isLoading = false) }
                    sendEvent(Event.HistoryCleared)
                    sendEvent(Event.ShowSuccess("History cleared"))
                },
                onError = { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            error = error.message ?: "Failed to clear history"
                        )
                    }
                    sendEvent(Event.ShowError("Failed to clear history"))
                }
            )
        }
    }

    /**
     * Selects an operation to view details.
     */
    private fun selectOperation(operation: RenameOperation?) {
        updateState { copy(selectedOperation = operation) }
    }

    /**
     * Clears any error state.
     */
    private fun clearError() {
        updateState { copy(error = null) }
    }
}
