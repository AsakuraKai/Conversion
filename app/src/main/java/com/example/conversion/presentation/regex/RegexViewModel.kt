package com.example.conversion.presentation.regex

import androidx.lifecycle.viewModelScope
import com.example.conversion.di.DefaultDispatcher
import com.example.conversion.domain.model.RegexFlag
import com.example.conversion.domain.model.RegexPreset
import com.example.conversion.domain.model.RegexRule
import com.example.conversion.domain.usecase.regex.ApplyRegexPatternUseCase
import com.example.conversion.domain.usecase.regex.ValidateRegexUseCase
import com.example.conversion.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for regex builder screen.
 * Handles regex pattern editing, validation, and preview.
 * Follows MVI pattern with State, Events, and Actions.
 */
@HiltViewModel
class RegexViewModel @Inject constructor(
    private val applyRegexPatternUseCase: ApplyRegexPatternUseCase,
    private val validateRegexUseCase: ValidateRegexUseCase,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : BaseViewModel<RegexContract.State, RegexContract.Event>(
    initialState = RegexContract.State()
) {

    /**
     * Handles user actions.
     */
    fun handleAction(action: RegexContract.Action) {
        when (action) {
            is RegexContract.Action.UpdatePattern -> updatePattern(action.pattern)
            is RegexContract.Action.UpdateReplacement -> updateReplacement(action.replacement)
            is RegexContract.Action.ToggleFlag -> toggleFlag(action.flag)
            is RegexContract.Action.ApplyPreset -> applyPreset(action.preset)
            is RegexContract.Action.UpdatePreviewInput -> updatePreviewInput(action.input)
            is RegexContract.Action.TogglePreserveExtension -> togglePreserveExtension()
            is RegexContract.Action.ValidatePattern -> validatePattern()
            is RegexContract.Action.UpdatePreview -> updatePreview()
            is RegexContract.Action.ApplyPattern -> applyPattern()
            is RegexContract.Action.ClearError -> clearError()
            is RegexContract.Action.Reset -> reset()
        }
    }

    /**
     * Updates the regex pattern and triggers validation.
     */
    private fun updatePattern(pattern: String) {
        updateState { copy(pattern = pattern, selectedPreset = null) }
        validatePattern()
        updatePreview()
    }

    /**
     * Updates the replacement string.
     */
    private fun updateReplacement(replacement: String) {
        updateState { copy(replacement = replacement, selectedPreset = null) }
        updatePreview()
    }

    /**
     * Toggles a regex flag.
     */
    private fun toggleFlag(flag: RegexFlag) {
        updateState {
            val newFlags = if (flag in flags) {
                flags - flag
            } else {
                flags + flag
            }
            copy(flags = newFlags)
        }
        updatePreview()
    }

    /**
     * Applies a preset pattern.
     */
    private fun applyPreset(preset: RegexPreset) {
        val rule = preset.toRegexRule()
        updateState {
            copy(
                pattern = rule.pattern,
                replacement = rule.replacement,
                flags = rule.flags,
                selectedPreset = preset,
                validationError = null,
                validationSuggestion = null
            )
        }
        updatePreview()
    }

    /**
     * Updates the preview input text.
     */
    private fun updatePreviewInput(input: String) {
        updateState { copy(previewInput = input) }
        updatePreview()
    }

    /**
     * Toggles preserve extension option.
     */
    private fun togglePreserveExtension() {
        updateState { copy(preserveExtension = !preserveExtension) }
        updatePreview()
    }

    /**
     * Validates the current pattern.
     */
    private fun validatePattern() {
        if (currentState.pattern.isEmpty()) {
            updateState {
                copy(
                    validationError = null,
                    validationSuggestion = null
                )
            }
            return
        }

        updateState { copy(isValidating = true) }

        viewModelScope.launch(defaultDispatcher) {
            val rule = RegexRule(
                pattern = currentState.pattern,
                replacement = currentState.replacement,
                flags = currentState.flags
            )

            when (val result = validateRegexUseCase(rule)) {
                is com.example.conversion.domain.common.Result.Success -> {
                    val validation = result.data
                    updateState {
                        copy(
                            validationError = if (!validation.isValid) validation.errorMessage else null,
                            validationSuggestion = validation.suggestion,
                            isValidating = false
                        )
                    }
                }
                is com.example.conversion.domain.common.Result.Error -> {
                    updateState {
                        copy(
                            validationError = result.exception.message ?: "Unknown validation error",
                            validationSuggestion = null,
                            isValidating = false
                        )
                    }
                }
                is com.example.conversion.domain.common.Result.Loading -> {
                    // Should not happen as use case completes immediately
                }
            }
        }
    }

    /**
     * Updates the live preview.
     */
    private fun updatePreview() {
        if (currentState.pattern.isEmpty() || currentState.previewInput.isEmpty()) {
            updateState { copy(previewResult = null) }
            return
        }

        viewModelScope.launch(defaultDispatcher) {
            val rule = RegexRule(
                pattern = currentState.pattern,
                replacement = currentState.replacement,
                flags = currentState.flags
            )

            val params = ApplyRegexPatternUseCase.ApplyRegexParams(
                filename = currentState.previewInput,
                regexRule = rule,
                preserveExtension = currentState.preserveExtension
            )

            when (val result = applyRegexPatternUseCase(params)) {
                is com.example.conversion.domain.common.Result.Success -> {
                    updateState { copy(previewResult = result.data) }
                }
                is com.example.conversion.domain.common.Result.Error -> {
                    updateState { copy(previewResult = null) }
                }
                is com.example.conversion.domain.common.Result.Loading -> {
                    // Should not happen as use case completes immediately
                }
            }
        }
    }

    /**
     * Applies the pattern and closes.
     */
    private fun applyPattern() {
        if (!currentState.canApply) {
            sendEvent(RegexContract.Event.ShowError(
                title = "Invalid Pattern",
                message = currentState.validationError ?: "Pattern is not valid or empty"
            ))
            return
        }

        sendEvent(RegexContract.Event.PatternApplied)
        sendEvent(RegexContract.Event.ShowMessage("Regex pattern applied successfully"))
        sendEvent(RegexContract.Event.NavigateBack)
    }

    /**
     * Clears validation error.
     */
    private fun clearError() {
        updateState {
            copy(
                validationError = null,
                validationSuggestion = null
            )
        }
    }

    /**
     * Resets to default state.
     */
    private fun reset() {
        setState(RegexContract.State())
    }

    /**
     * Gets the current regex rule.
     */
    fun getCurrentRule(): RegexRule? {
        return if (currentState.isValid) {
            RegexRule(
                pattern = currentState.pattern,
                replacement = currentState.replacement,
                flags = currentState.flags
            )
        } else {
            null
        }
    }
}
