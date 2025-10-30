package com.example.conversion.domain.usecase.settings

import com.example.conversion.domain.model.UserPreferences
import com.example.conversion.domain.repository.PreferencesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to observe user preferences.
 * Returns a Flow that emits whenever preferences change.
 */
class GetUserPreferencesUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val dispatcher: CoroutineDispatcher
) {
    
    operator fun invoke(): Flow<UserPreferences> {
        return preferencesRepository.getUserPreferences()
    }
}
