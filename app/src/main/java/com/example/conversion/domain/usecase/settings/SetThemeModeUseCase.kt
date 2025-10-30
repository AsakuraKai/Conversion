package com.example.conversion.domain.usecase.settings

import com.example.conversion.domain.model.ThemeMode
import com.example.conversion.domain.repository.PreferencesRepository
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case to update the theme mode preference
 */
class SetThemeModeUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    dispatcher: CoroutineDispatcher
) : BaseUseCase<ThemeMode, Unit>(dispatcher) {
    
    override suspend fun execute(params: ThemeMode) {
        preferencesRepository.setThemeMode(params)
    }
}
