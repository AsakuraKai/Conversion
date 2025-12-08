package com.example.conversion.domain.usecase.template

import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.repository.TemplateRepository
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for toggling a template's favorite status.
 *
 * Input: String - The template ID
 * Output: Unit - Success or failure
 */
class ToggleFavoriteUseCase @Inject constructor(
    private val templateRepository: TemplateRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<String, Unit>(dispatcher) {

    /**
     * Executes the use case to toggle favorite status.
     *
     * @param params The template ID
     * @return Unit on success
     * @throws IllegalArgumentException if template ID is blank
     */
    override suspend fun execute(params: String): Unit {
        if (params.isBlank()) {
            throw IllegalArgumentException("Template ID cannot be blank")
        }

        return when (val result = templateRepository.toggleFavorite(params)) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
            is Result.Loading -> throw IllegalStateException("Unexpected loading state")
        }
    }
}
