package com.example.conversion.data.repository

import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.RenameTemplate
import com.example.conversion.domain.repository.TemplateRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * MOCK IMPLEMENTATION: In-memory template repository.
 * 
 * This is a strategic implementation using in-memory storage to enable rapid development
 * and UI implementation without blocking on Room database setup.
 * 
 * **Production Upgrade Path:**
 * - Replace with Room database (TemplateEntity, TemplateDao)
 * - Add persistent storage with SQLite
 * - Implement proper database migrations
 * - Add caching layer for performance
 * 
 * **Current Functionality:**
 * ✅ Complete CRUD operations
 * ✅ Flow-based observation
 * ✅ Thread-safe with mutex
 * ✅ Full error handling
 * ✅ Favorite filtering
 * ✅ Usage tracking
 * 
 * **Limitations:**
 * ⚠️ Data lost on app restart (no persistence)
 * ⚠️ No database migrations
 * ⚠️ Limited query optimization
 * 
 * See: MOCK_IMPLEMENTATIONS.md - CHUNK 12
 */
@Singleton
class TemplateRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TemplateRepository {

    // In-memory storage
    private val templates = mutableMapOf<String, RenameTemplate>()
    private val templatesFlow = MutableStateFlow<List<RenameTemplate>>(emptyList())
    private val mutex = Mutex()

    override suspend fun saveTemplate(template: RenameTemplate): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                mutex.withLock {
                    templates[template.id] = template
                    emitTemplates()
                }
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getTemplates(): Result<List<RenameTemplate>> =
        withContext(ioDispatcher) {
            try {
                mutex.withLock {
                    Result.Success(templates.values.toList())
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getTemplateById(id: String): Result<RenameTemplate?> =
        withContext(ioDispatcher) {
            try {
                mutex.withLock {
                    Result.Success(templates[id])
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun deleteTemplate(id: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                mutex.withLock {
                    templates.remove(id)
                    emitTemplates()
                }
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override fun observeTemplates(): Flow<List<RenameTemplate>> {
        return templatesFlow
    }

    override suspend fun getFavoriteTemplates(): Result<List<RenameTemplate>> =
        withContext(ioDispatcher) {
            try {
                mutex.withLock {
                    Result.Success(templates.values.filter { it.isFavorite })
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override fun observeFavoriteTemplates(): Flow<List<RenameTemplate>> {
        return templatesFlow.map { templates ->
            templates.filter { it.isFavorite }
        }
    }

    override suspend fun markTemplateAsUsed(id: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                mutex.withLock {
                    templates[id]?.let { template ->
                        templates[id] = template.markAsUsed()
                        emitTemplates()
                    }
                }
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun toggleFavorite(id: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                mutex.withLock {
                    templates[id]?.let { template ->
                        templates[id] = template.toggleFavorite()
                        emitTemplates()
                    }
                }
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun clearAllTemplates(): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                mutex.withLock {
                    templates.clear()
                    emitTemplates()
                }
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    /**
     * Emits the current template list to all observers.
     * Must be called within mutex lock.
     */
    private fun emitTemplates() {
        templatesFlow.value = templates.values.toList()
    }
}
