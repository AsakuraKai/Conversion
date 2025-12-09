package com.example.conversion.presentation.tag

import androidx.lifecycle.viewModelScope
import com.example.conversion.domain.model.FileTag
import com.example.conversion.domain.usecase.tag.CreateTagUseCase
import com.example.conversion.domain.usecase.tag.DeleteTagUseCase
import com.example.conversion.domain.usecase.tag.GetTagsUseCase
import com.example.conversion.presentation.base.BaseViewModel
import com.example.conversion.presentation.tag.TagContract.Action
import com.example.conversion.presentation.tag.TagContract.Event
import com.example.conversion.presentation.tag.TagContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for tag management screen.
 * Manages CRUD operations for file tags.
 */
@HiltViewModel
class TagViewModel @Inject constructor(
    private val getTagsUseCase: GetTagsUseCase,
    private val createTagUseCase: CreateTagUseCase,
    private val deleteTagUseCase: DeleteTagUseCase
) : BaseViewModel<State, Event>(State()) {

    init {
        loadTags()
    }

    /**
     * Handles user actions and updates state accordingly.
     */
    fun handleAction(action: Action) {
        when (action) {
            is Action.LoadTags -> loadTags()
            is Action.ShowCreateDialog -> showCreateDialog()
            is Action.HideCreateDialog -> hideCreateDialog()
            is Action.ShowEditDialog -> showEditDialog(action.tag)
            is Action.HideEditDialog -> hideEditDialog()
            is Action.CreateTag -> createTag(action.name, action.color)
            is Action.UpdateTag -> updateTag(action.tag)
            is Action.ShowDeleteConfirmation -> showDeleteConfirmation(action.tag)
            is Action.HideDeleteConfirmation -> hideDeleteConfirmation()
            is Action.ConfirmDelete -> confirmDelete()
            is Action.DeleteTag -> deleteTag(action.tagId)
            is Action.UpdateTagName -> updateTagName(action.name)
            is Action.UpdateSelectedColor -> updateSelectedColor(action.color)
            is Action.UpdateSearchQuery -> updateSearchQuery(action.query)
            is Action.ClearError -> clearError()
        }
    }

    private fun loadTags() {
        updateState { copy(isLoading = true, error = null) }

        viewModelScope.launch {
            executeUseCase(
                block = { getTagsUseCase() },
                onSuccess = { tags ->
                    updateState { 
                        copy(
                            tags = tags,
                            isLoading = false
                        ) 
                    }
                },
                onError = { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load tags"
                        )
                    }
                    sendEvent(Event.ShowError("Failed to load tags"))
                }
            )
        }
    }

    private fun showCreateDialog() {
        updateState { 
            copy(
                showCreateDialog = true,
                newTagName = "",
                selectedColor = FileTag.PREDEFINED_COLORS.first()
            ) 
        }
    }

    private fun hideCreateDialog() {
        updateState { 
            copy(
                showCreateDialog = false,
                newTagName = "",
                selectedColor = FileTag.PREDEFINED_COLORS.first()
            ) 
        }
    }

    private fun showEditDialog(tag: FileTag) {
        updateState { 
            copy(
                showEditDialog = true,
                editingTag = tag,
                newTagName = tag.name,
                selectedColor = tag.color
            ) 
        }
    }

    private fun hideEditDialog() {
        updateState { 
            copy(
                showEditDialog = false,
                editingTag = null,
                newTagName = "",
                selectedColor = FileTag.PREDEFINED_COLORS.first()
            ) 
        }
    }

    private fun createTag(name: String, color: String) {
        // Validate inputs
        if (name.isBlank() || name.length > FileTag.MAX_NAME_LENGTH) {
            sendEvent(Event.ShowError("Invalid tag name"))
            return
        }

        if (!color.matches(Regex("^#[0-9A-Fa-f]{6}$"))) {
            sendEvent(Event.ShowError("Invalid color format"))
            return
        }

        updateState { copy(isLoading = true) }

        val tag = FileTag(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            color = color,
            createdAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            executeUseCase(
                block = { createTagUseCase(tag) },
                onSuccess = {
                    updateState { 
                        copy(
                            isLoading = false,
                            showCreateDialog = false,
                            newTagName = "",
                            selectedColor = FileTag.PREDEFINED_COLORS.first()
                        ) 
                    }
                    sendEvent(Event.TagCreated(tag))
                    sendEvent(Event.ShowMessage("Tag '${tag.name}' created"))
                    loadTags()
                },
                onError = { error ->
                    updateState { copy(isLoading = false) }
                    sendEvent(Event.ShowError(error.message ?: "Failed to create tag"))
                }
            )
        }
    }

    private fun updateTag(tag: FileTag) {
        // Validate inputs
        if (!tag.isValid()) {
            sendEvent(Event.ShowError("Invalid tag data"))
            return
        }

        updateState { copy(isLoading = true) }

        viewModelScope.launch {
            // For now, we'll recreate the tag since update use case isn't available
            // In production, use UpdateTagUseCase
            executeUseCase(
                block = { createTagUseCase(tag) },
                onSuccess = {
                    updateState { 
                        copy(
                            isLoading = false,
                            showEditDialog = false,
                            editingTag = null,
                            newTagName = "",
                            selectedColor = FileTag.PREDEFINED_COLORS.first()
                        ) 
                    }
                    sendEvent(Event.TagUpdated(tag))
                    sendEvent(Event.ShowMessage("Tag '${tag.name}' updated"))
                    loadTags()
                },
                onError = { error ->
                    updateState { copy(isLoading = false) }
                    sendEvent(Event.ShowError(error.message ?: "Failed to update tag"))
                }
            )
        }
    }

    private fun showDeleteConfirmation(tag: FileTag) {
        updateState { 
            copy(
                showDeleteConfirmation = true,
                tagToDelete = tag
            ) 
        }
    }

    private fun hideDeleteConfirmation() {
        updateState { 
            copy(
                showDeleteConfirmation = false,
                tagToDelete = null
            ) 
        }
    }

    private fun confirmDelete() {
        val tag = currentState.tagToDelete ?: return
        deleteTag(tag.id)
        hideDeleteConfirmation()
    }

    private fun deleteTag(tagId: String) {
        if (tagId.isBlank()) {
            sendEvent(Event.ShowError("Invalid tag ID"))
            return
        }

        updateState { copy(isLoading = true) }

        // Get tag name before deletion for the success message
        val tagName = currentState.tags.find { it.id == tagId }?.name ?: "Tag"

        viewModelScope.launch {
            executeUseCase(
                block = { deleteTagUseCase(tagId) },
                onSuccess = {
                    updateState { 
                        copy(
                            isLoading = false,
                            showDeleteConfirmation = false,
                            tagToDelete = null
                        ) 
                    }
                    sendEvent(Event.TagDeleted(tagName))
                    sendEvent(Event.ShowMessage("Tag '$tagName' deleted"))
                    loadTags()
                },
                onError = { error ->
                    updateState { copy(isLoading = false) }
                    sendEvent(Event.ShowError(error.message ?: "Failed to delete tag"))
                }
            )
        }
    }

    private fun updateTagName(name: String) {
        updateState { copy(newTagName = name) }
    }

    private fun updateSelectedColor(color: String) {
        updateState { copy(selectedColor = color) }
    }

    private fun updateSearchQuery(query: String) {
        updateState { copy(searchQuery = query) }
    }

    private fun clearError() {
        updateState { copy(error = null) }
    }
}
