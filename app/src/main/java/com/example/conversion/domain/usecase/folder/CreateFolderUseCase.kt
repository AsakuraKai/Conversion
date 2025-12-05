package com.example.conversion.domain.usecase.folder

import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.FolderInfo
import com.example.conversion.domain.repository.FolderRepository
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Parameters for creating a new folder.
 *
 * @property parentPath Absolute path where the folder should be created
 * @property folderName Name for the new folder
 */
data class CreateFolderParams(
    val parentPath: String,
    val folderName: String
)

/**
 * Use case for creating a new folder in device storage.
 * Validates folder name and handles folder creation through Storage Access Framework.
 *
 * Input: CreateFolderParams - Parent path and folder name
 * Output: FolderInfo - Information about the newly created folder
 */
class CreateFolderUseCase @Inject constructor(
    private val folderRepository: FolderRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<CreateFolderParams, FolderInfo>(dispatcher) {

    /**
     * Executes the use case to create a new folder.
     *
     * @param params CreateFolderParams containing parent path and folder name
     * @return FolderInfo for the newly created folder
     * @throws IllegalArgumentException if folder name is invalid
     * @throws Exception if folder creation fails or permissions are missing
     */
    override suspend fun execute(params: CreateFolderParams): FolderInfo {
        val (parentPath, folderName) = params

        // Validate parent path exists
        when (val existsResult = folderRepository.folderExists(parentPath)) {
            is Result.Success -> {
                if (!existsResult.data) {
                    throw IllegalArgumentException("Parent folder does not exist: $parentPath")
                }
            }
            is Result.Error -> throw existsResult.exception
            is Result.Loading -> {} // Continue with creation
        }

        // Validate folder name
        when (val validationResult = folderRepository.validateFolderName(folderName)) {
            is Result.Success -> {
                if (!validationResult.data) {
                    throw IllegalArgumentException("Invalid folder name: $folderName")
                }
            }
            is Result.Error -> throw validationResult.exception
            is Result.Loading -> {} // Continue with creation
        }

        // Create the folder
        return when (val result = folderRepository.createFolder(parentPath, folderName)) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
            is Result.Loading -> throw IllegalStateException("Folder creation returned Loading state")
        }
    }
}
