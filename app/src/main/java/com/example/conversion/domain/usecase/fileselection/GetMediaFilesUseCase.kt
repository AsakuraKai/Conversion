package com.example.conversion.domain.usecase.fileselection

import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.FileFilter
import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.repository.MediaRepository
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Use case for retrieving media files from device storage.
 * Filters files based on provided criteria and returns them ready for display.
 *
 * Input: FileFilter - configuration for filtering files
 * Output: List<FileItem> - filtered and sorted list of media files
 */
class GetMediaFilesUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseUseCase<FileFilter, List<FileItem>>(dispatcher) {

    /**
     * Executes the use case to retrieve media files.
     *
     * @param params FileFilter specifying which files to retrieve
     * @return List of FileItems matching the filter criteria
     * @throws Exception if MediaStore query fails or permissions are missing
     */
    override suspend fun execute(params: FileFilter): List<FileItem> {
        // Validate that at least one media type is selected
        if (!params.hasMediaTypeSelected) {
            return emptyList()
        }

        // Get files from repository
        return when (val result = mediaRepository.getMediaFiles(params)) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
            is Result.Loading -> emptyList()
        }
    }
}
