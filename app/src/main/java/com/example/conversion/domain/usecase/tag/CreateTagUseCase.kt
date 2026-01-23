package com.example.conversion.domain.usecase.tag

import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.FileTag
import com.example.conversion.domain.repository.TagRepository
import javax.inject.Inject

/**
 * Use case for creating a new tag.
 */
class CreateTagUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    /**
     * Executes the use case to create a tag.
     * @param tag The tag to create
     * @return Result indicating success or failure with validation
     */
    suspend operator fun invoke(tag: FileTag): Result<Unit> {
        // Validate the tag
        if (!tag.isValid()) {
            return Result.Error(
                IllegalArgumentException("Invalid tag: name must not be blank and color must be valid hex")
            )
        }

        return tagRepository.createTag(tag)
    }
}
