package com.example.conversion.domain.usecase.settings

import com.example.conversion.domain.repository.PreferencesRepository
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case to update the dynamic colors preference
 */
class SetUseDynamicColorsUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    dispatcher: CoroutineDispatcher
) : BaseUseCase<Boolean, Unit>(dispatcher) {
    
    override suspend fun execute(params: Boolean) {
        preferencesRepository.setUseDynamicColors(params)
    }
}
