package com.example.conversion.domain.usecase.tag

import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.repository.TagRepository
import javax.inject.Inject

/**
 * Use case for searching files by tag.
 */
class SearchByTagUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    /**
     * Executes the use case to find all files with a specific tag.
     * @param tagId ID of the tag to search for
     * @return Result containing the list of files with the tag or an error
     */
    suspend operator fun invoke(tagId: String): Result<List<FileItem>> {
        if (tagId.isBlank()) {
            return Result.Error(IllegalArgumentException("Tag ID must not be blank"))
        }

        return tagRepository.getFilesByTag(tagId)
    }
}
