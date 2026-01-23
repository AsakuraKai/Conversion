package com.example.conversion.domain.usecase.settings

import com.example.conversion.domain.repository.PreferencesRepository
import javax.inject.Inject

/**
 * Use case for updating auto-delete originals preference.
 * When enabled, original files are automatically deleted after operations.
 * Mutually exclusive with auto-backup.
 */
class SetAutoDeleteOriginalsUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(enabled: Boolean): Result<Unit> = runCatching {
        preferencesRepository.setAutoDeleteOriginals(enabled)
    }
}
