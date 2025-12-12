package com.example.conversion.domain.usecase.settings

import com.example.conversion.domain.repository.PreferencesRepository
import javax.inject.Inject

/**
 * Use case for updating auto-backup enabled preference.
 * When enabled, files are automatically backed up before operations.
 * Mutually exclusive with auto-delete originals.
 */
class SetAutoBackupEnabledUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(enabled: Boolean): Result<Unit> = runCatching {
        preferencesRepository.setAutoBackupEnabled(enabled)
    }
}
