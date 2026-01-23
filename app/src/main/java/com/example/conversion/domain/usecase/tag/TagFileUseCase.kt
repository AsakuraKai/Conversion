package com.example.conversion.domain.usecase.tag

import android.net.Uri
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.repository.TagRepository
import javax.inject.Inject

/**
 * Use case for tagging a file with a specific tag.
 */
class TagFileUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    /**
     * Executes the use case to apply a tag to a file.
     * @param fileUri URI of the file to tag
     * @param tagId ID of the tag to apply
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(fileUri: Uri, tagId: String): Result<Unit> {
        // Validate inputs
        if (tagId.isBlank()) {
            return Result.Error(IllegalArgumentException("Tag ID must not be blank"))
        }

        // Check if tag exists
        return when (val tagResult = tagRepository.getTagById(tagId)) {
            is Result.Success -> {
                if (tagResult.data == null) {
                    Result.Error(IllegalArgumentException("Tag with ID $tagId does not exist"))
                } else {
                    tagRepository.tagFile(fileUri, tagId)
                }
            }
            is Result.Error -> Result.Error(tagResult.exception)
            is Result.Loading -> Result.Error(IllegalStateException("Unexpected loading state"))
        }
    }
}
