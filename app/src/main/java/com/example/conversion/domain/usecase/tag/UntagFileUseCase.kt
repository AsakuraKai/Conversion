package com.example.conversion.domain.usecase.tag

import android.net.Uri
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.repository.TagRepository
import javax.inject.Inject

/**
 * Use case for removing a tag from a file.
 */
class UntagFileUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    /**
     * Executes the use case to remove a tag from a file.
     * @param fileUri URI of the file to untag
     * @param tagId ID of the tag to remove
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(fileUri: Uri, tagId: String): Result<Unit> {
        if (tagId.isBlank()) {
            return Result.Error(IllegalArgumentException("Tag ID must not be blank"))
        }

        return tagRepository.untagFile(fileUri, tagId)
    }
}
