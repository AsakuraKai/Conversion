package com.example.conversion.domain.usecase.tag

import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.FileTag
import com.example.conversion.domain.repository.TagRepository
import javax.inject.Inject

/**
 * Use case for retrieving all tags.
 */
class GetTagsUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    /**
     * Executes the use case to retrieve all tags.
     * @return Result containing the list of tags or an error
     */
    suspend operator fun invoke(): Result<List<FileTag>> {
        return tagRepository.getTags()
    }
}
