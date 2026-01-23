package com.example.conversion.data.repository

import com.example.conversion.data.local.dao.TemplateDao
import com.example.conversion.data.local.mapper.TemplateMapper.toDomain
import com.example.conversion.data.local.mapper.TemplateMapper.toEntity
import com.example.conversion.di.IoDispatcher
import com.example.conversion.domain.common.Result
import com.example.conversion.domain.model.RenameTemplate
import com.example.conversion.domain.repository.TemplateRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PRODUCTION IMPLEMENTATION: Room database template repository.
 * 
 * Upgraded from in-memory mock to persistent Room storage for production use.
 * All data is now persisted across app restarts.
 * 
 * **Features:**
 * ✅ Complete CRUD operations with Room
 * ✅ Flow-based reactive observation
 * ✅ Persistent storage with SQLite
 * ✅ Full error handling
 * ✅ Favorite filtering with queries
 * ✅ Usage tracking with timestamps
 * ✅ Thread-safe operations (Room handles this)
 * 
 * **Improvements over mock:**
 * ✅ Data persists across app restarts
 * ✅ Efficient database queries
 * ✅ Built-in thread safety from Room
 * ✅ Optimized Flow observations
 * 
 * Upgraded from: MOCK_IMPLEMENTATIONS.md - CHUNK 12
 */
@Singleton
class TemplateRepositoryImpl @Inject constructor(
    private val templateDao: TemplateDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TemplateRepository {

    override suspend fun saveTemplate(template: RenameTemplate): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                templateDao.insert(template.toEntity())
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getTemplates(): Result<List<RenameTemplate>> =
        withContext(ioDispatcher) {
            try {
                val entities = templateDao.getAll()
                Result.Success(entities.map { it.toDomain() })
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getTemplateById(id: String): Result<RenameTemplate?> =
        withContext(ioDispatcher) {
            try {
                val entity = templateDao.getById(id)
                Result.Success(entity?.toDomain())
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun deleteTemplate(id: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                templateDao.deleteById(id)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override fun observeTemplates(): Flow<List<RenameTemplate>> {
        return templateDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getFavoriteTemplates(): Result<List<RenameTemplate>> =
        withContext(ioDispatcher) {
            try {
                val entities = templateDao.getFavorites()
                Result.Success(entities.map { it.toDomain() })
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override fun observeFavoriteTemplates(): Flow<List<RenameTemplate>> {
        return templateDao.observeFavorites().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun markTemplateAsUsed(id: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                val entity = templateDao.getById(id)
                if (entity != null) {
                    val template = entity.toDomain().markAsUsed()
                    templateDao.update(template.toEntity())
                }
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun toggleFavorite(id: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                val entity = templateDao.getById(id)
                if (entity != null) {
                    val template = entity.toDomain().toggleFavorite()
                    templateDao.update(template.toEntity())
                }
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun clearAllTemplates(): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                templateDao.deleteAll()
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
}
