package com.example.conversion.domain.usecase.tag

import com.example.conversion.domain.common.Result
import com.example.conversion.domain.repository.TagRepository
import javax.inject.Inject

/**
 * Use case for deleting a tag.
 */
class DeleteTagUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    /**
     * Executes the use case to delete a tag by its ID.
     * @param id The ID of the tag to delete
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(id: String): Result<Unit> {
        if (id.isBlank()) {
            return Result.Error(IllegalArgumentException("Tag ID must not be blank"))
        }

        return tagRepository.deleteTag(id)
    }
}
