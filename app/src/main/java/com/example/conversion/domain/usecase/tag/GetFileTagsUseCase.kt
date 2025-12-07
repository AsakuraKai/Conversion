package com.example.conversion.domain.usecase.tag

import android.net.Uri
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.FileTag
import com.example.conversion.domain.repository.TagRepository
import javax.inject.Inject

/**
 * Use case for getting all tags applied to a file.
 */
class GetFileTagsUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    /**
     * Executes the use case to retrieve all tags for a specific file.
     * @param fileUri URI of the file
     * @return Result containing the list of tags or an error
     */
    suspend operator fun invoke(fileUri: Uri): Result<List<FileTag>> {
        return tagRepository.getTagsForFile(fileUri)
    }
}
