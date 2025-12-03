package com.example.conversion.domain.usecase.folder

import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.FolderInfo
import com.example.conversion.domain.repository.FolderRepository
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Use case for retrieving folders from device storage.
 * Handles folder browsing and navigation for the destination folder selector.
 *
 * Input: String? - Parent folder path (null for root folders)
 * Output: List<FolderInfo> - List of folders in the specified location
 */
class GetFoldersUseCase @Inject constructor(
    private val folderRepository: FolderRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseUseCase<String?, List<FolderInfo>>(dispatcher) {

    /**
     * Executes the use case to retrieve folders.
     *
     * @param params Parent folder path. Pass null to get root folders.
     * @return List of FolderInfo objects for all folders in the specified location
     * @throws Exception if folder access fails or permissions are missing
     */
    override suspend fun execute(params: String?): List<FolderInfo> {
        // If params is null or empty, get root folders
        val result = if (params.isNullOrEmpty()) {
            folderRepository.getRootFolders()
        } else {
            // Validate folder exists before retrieving subfolders
            when (val existsResult = folderRepository.folderExists(params)) {
                is Result.Success -> {
                    if (!existsResult.data) {
                        throw IllegalArgumentException("Folder does not exist: $params")
                    }
                    folderRepository.getFolders(params)
                }
                is Result.Error -> throw existsResult.exception
                is Result.Loading -> folderRepository.getFolders(params)
            }
        }

        return when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
            is Result.Loading -> emptyList()
        }
    }
}
