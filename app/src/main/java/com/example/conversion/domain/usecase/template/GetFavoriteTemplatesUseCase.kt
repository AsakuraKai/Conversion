package com.example.conversion.domain.usecase.template

import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.RenameTemplate
import com.example.conversion.domain.repository.TemplateRepository
import com.example.conversion.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Use case for retrieving favorite templates.
 * Returns only templates marked as favorites, sorted by last used date.
 *
 * Input: Unit - No parameters needed
 * Output: List<RenameTemplate> - Favorite templates
 */
class GetFavoriteTemplatesUseCase @Inject constructor(
    private val templateRepository: TemplateRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<Unit, List<RenameTemplate>>(dispatcher) {

    /**
     * Executes the use case to retrieve favorite templates.
     *
     * @param params Unit (no parameters)
     * @return List of favorite templates, sorted by last used date (most recent first)
     */
    override suspend fun execute(params: Unit): List<RenameTemplate> {
        return when (val result = templateRepository.getFavoriteTemplates()) {
            is Result.Success -> result.data.sortedByDescending { it.lastUsedAt ?: 0L }
            is Result.Error -> throw result.exception
            is Result.Loading -> throw IllegalStateException("Unexpected loading state")
        }
    }
}
