package com.example.conversion.presentation.template

import androidx.lifecycle.viewModelScope
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.domain.model.RenameTemplate
import com.example.conversion.domain.usecase.template.DeleteTemplateUseCase
import com.example.conversion.domain.usecase.template.GetFavoriteTemplatesUseCase
import com.example.conversion.domain.usecase.template.GetTemplatesUseCase
import com.example.conversion.domain.usecase.template.MarkTemplateAsUsedUseCase
import com.example.conversion.domain.usecase.template.SaveTemplateUseCase
import com.example.conversion.domain.usecase.template.ToggleFavoriteUseCase
import com.example.conversion.presentation.base.BaseViewModel
import com.example.conversion.presentation.template.TemplateContract.Action
import com.example.conversion.presentation.template.TemplateContract.Event
import com.example.conversion.presentation.template.TemplateContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for template management screen.
 * Manages CRUD operations for Reusable Templates.
 */
@HiltViewModel
class TemplateViewModel @Inject constructor(
    private val getTemplatesUseCase: GetTemplatesUseCase,
    private val getFavoriteTemplatesUseCase: GetFavoriteTemplatesUseCase,
    private val saveTemplateUseCase: SaveTemplateUseCase,
    private val deleteTemplateUseCase: DeleteTemplateUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val markTemplateAsUsedUseCase: MarkTemplateAsUsedUseCase
) : BaseViewModel<State, Event>(State()) {

    init {
        loadTemplates()
    }

    /**
     * Handles user actions and updates state accordingly.
     */
    fun handleAction(action: Action) {
        when (action) {
            is Action.LoadTemplates -> loadTemplates()
            is Action.SaveTemplate -> saveTemplate(action.name, action.pattern, action.config)
            is Action.DeleteTemplate -> deleteTemplate(action.templateId)
            is Action.ApplyTemplate -> applyTemplate(action.template)
            is Action.ToggleFavorite -> toggleFavorite(action.templateId)
            is Action.SelectTemplate -> selectTemplate(action.template)
            is Action.ShowSaveDialog -> showSaveDialog(action.config)
            is Action.HideSaveDialog -> hideSaveDialog()
            is Action.ShowDeleteConfirmation -> showDeleteConfirmation(action.template)
            is Action.HideDeleteConfirmation -> hideDeleteConfirmation()
            is Action.ConfirmDelete -> confirmDelete()
            is Action.ToggleFilter -> toggleFilter()
            is Action.UpdateTemplateName -> updateTemplateName(action.name)
            is Action.UpdateTemplatePattern -> updateTemplatePattern(action.pattern)
            is Action.ClearError -> clearError()
        }
    }

    private fun loadTemplates() {
        updateState { copy(isLoading = true, error = null) }

        viewModelScope.launch {
            // Load all templates
            executeUseCase(
                block = { getTemplatesUseCase(Unit) },
                onSuccess = { templates ->
                    updateState { 
                        copy(
                            templates = templates,
                            isLoading = false
                        ) 
                    }
                    loadFavoriteTemplates()
                },
                onError = { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load templates"
                        )
                    }
                    sendEvent(Event.ShowError("Failed to load templates"))
                }
            )
        }
    }

    private fun loadFavoriteTemplates() {
        viewModelScope.launch {
            executeUseCase(
                block = { getFavoriteTemplatesUseCase(Unit) },
                onSuccess = { favorites ->
                    updateState { copy(favoriteTemplates = favorites) }
                },
                onError = { error ->
                    // Silently handle favorite loading errors
                    // Main templates are already loaded
                }
            )
        }
    }

    private fun saveTemplate(name: String, pattern: String, config: RenameConfig) {
        // Validate inputs
        if (name.isBlank() || name.length > RenameTemplate.MAX_NAME_LENGTH) {
            sendEvent(Event.ShowError("Invalid template name"))
            return
        }

        if (pattern.isBlank() || pattern.length > RenameTemplate.MAX_PATTERN_LENGTH) {
            sendEvent(Event.ShowError("Invalid template pattern"))
            return
        }

        if (!config.isValid()) {
            sendEvent(Event.ShowError("Invalid configuration"))
            return
        }

        updateState { copy(isLoading = true) }

        val template = RenameTemplate(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            pattern = pattern.trim(),
            config = config,
            isFavorite = false,
            createdAt = System.currentTimeMillis(),
            lastUsedAt = null
        )

        viewModelScope.launch {
            executeUseCase(
                block = { saveTemplateUseCase(template) },
                onSuccess = {
                    updateState { 
                        copy(
                            isLoading = false,
                            showSaveDialog = false,
                            newTemplateName = "",
                            newTemplatePattern = ""
                        ) 
                    }
                    sendEvent(Event.TemplateSaved(template))
                    sendEvent(Event.ShowMessage("Template '${template.name}' saved"))
                    loadTemplates()
                },
                onError = { error ->
                    updateState { copy(isLoading = false) }
                    sendEvent(Event.ShowError("Failed to save template: ${error.message}"))
                }
            )
        }
    }

    private fun deleteTemplate(templateId: String) {
        updateState { copy(isLoading = true) }

        viewModelScope.launch {
            executeUseCase(
                block = { deleteTemplateUseCase(templateId) },
                onSuccess = {
                    val templateName = currentState.templateToDelete?.name ?: "Template"
                    updateState { 
                        copy(
                            isLoading = false,
                            showDeleteConfirmation = false,
                            templateToDelete = null
                        ) 
                    }
                    sendEvent(Event.TemplateDeleted(templateName))
                    sendEvent(Event.ShowMessage("Template deleted"))
                    loadTemplates()
                },
                onError = { error ->
                    updateState { 
                        copy(
                            isLoading = false,
                            showDeleteConfirmation = false
                        ) 
                    }
                    sendEvent(Event.ShowError("Failed to delete template: ${error.message}"))
                }
            )
        }
    }

    private fun applyTemplate(template: RenameTemplate) {
        viewModelScope.launch {
            // Mark template as used
            executeUseCase(
                block = { markTemplateAsUsedUseCase(template.id) },
                onSuccess = {
                    sendEvent(Event.TemplateApplied(template))
                    sendEvent(Event.NavigateBackWithConfig(template.config))
                    loadTemplates() // Refresh to update lastUsedAt
                },
                onError = { error ->
                    // Still apply the template even if marking as used fails
                    sendEvent(Event.TemplateApplied(template))
                    sendEvent(Event.NavigateBackWithConfig(template.config))
                }
            )
        }
    }

    private fun toggleFavorite(templateId: String) {
        viewModelScope.launch {
            executeUseCase(
                block = { toggleFavoriteUseCase(templateId) },
                onSuccess = {
                    loadTemplates() // Refresh templates
                },
                onError = { error ->
                    sendEvent(Event.ShowError("Failed to update favorite status"))
                }
            )
        }
    }

    private fun selectTemplate(template: RenameTemplate?) {
        updateState { copy(selectedTemplate = template) }
    }

    private fun showSaveDialog(config: RenameConfig) {
        // Generate default pattern preview
        val patternPreview = RenameTemplate.generatePatternPreview(config)
        
        updateState { 
            copy(
                showSaveDialog = true,
                currentConfig = config,
                newTemplateName = "",
                newTemplatePattern = patternPreview
            ) 
        }
    }

    private fun hideSaveDialog() {
        updateState { 
            copy(
                showSaveDialog = false,
                newTemplateName = "",
                newTemplatePattern = "",
                currentConfig = null
            ) 
        }
    }

    private fun showDeleteConfirmation(template: RenameTemplate) {
        updateState { 
            copy(
                showDeleteConfirmation = true,
                templateToDelete = template
            ) 
        }
    }

    private fun hideDeleteConfirmation() {
        updateState { 
            copy(
                showDeleteConfirmation = false,
                templateToDelete = null
            ) 
        }
    }

    private fun confirmDelete() {
        currentState.templateToDelete?.let { template ->
            deleteTemplate(template.id)
        }
    }

    private fun toggleFilter() {
        updateState { copy(filterByFavorites = !filterByFavorites) }
    }

    private fun updateTemplateName(name: String) {
        updateState { copy(newTemplateName = name) }
    }

    private fun updateTemplatePattern(pattern: String) {
        updateState { copy(newTemplatePattern = pattern) }
    }

    private fun clearError() {
        updateState { copy(error = null) }
    }
}
