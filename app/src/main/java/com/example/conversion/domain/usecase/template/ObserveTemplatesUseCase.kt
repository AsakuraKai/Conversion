package com.example.conversion.domain.usecase.template

import com.example.conversion.domain.model.RenameTemplate
import com.example.conversion.domain.repository.TemplateRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing changes to the template list.
 * Returns a Flow that emits the current list of templates whenever it changes.
 *
 * Output: Flow<List<RenameTemplate>> - Stream of template lists
 */
class ObserveTemplatesUseCase @Inject constructor(
    private val templateRepository: TemplateRepository
) {

    /**
     * Executes the use case to observe templates.
     *
     * @return Flow emitting lists of templates sorted by creation date (newest first)
     */
    operator fun invoke(): Flow<List<RenameTemplate>> {
        return templateRepository.observeTemplates()
    }
}
