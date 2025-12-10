# Data Persistence Mocks - Mock Implementations

**Last Updated:** December 10, 2025  
**Category:** Data Layer - Persistence  
**Related Chunks:** 5, 7, 8, 13

---

## 📋 Overview

This group covers local data persistence implementations that enable rapid UI development without blocking on Room database setup. All implementations use in-memory storage with Flow-based reactive patterns and clear upgrade paths to Room.

**Implementations in this group:**
- **#5:** TemplateRepositoryImpl (In-Memory → Room)
- **#7:** HistoryRepositoryImpl (In-Memory → Room)
- **#8:** TagRepositoryImpl (In-Memory → Room)
- **#13:** ActivityRepositoryImpl (In-Memory → Room)

**Common theme:** In-memory storage with clean architecture and production upgrade to Room database

**Technology Stack:** Native Android (Room Database) - Zero external setup required

---

## 5️⃣ TemplateRepositoryImpl.kt

**Location:** `data/repository/TemplateRepositoryImpl.kt`  
**Chunk:** 12 (Pattern Templates)  
**Priority:** Medium

### Strategic Implementation
Uses in-memory storage (MutableStateFlow + HashMap) as a development-friendly approach that provides full CRUD functionality without requiring Room database setup. This enables immediate UI development for template management features while database architecture is prepared.

### Fully Functional Features
✅ Complete CRUD operations for templates  
✅ Flow-based reactive observation  
✅ Thread-safe with Mutex synchronization  
✅ Favorite template filtering  
✅ Usage tracking (lastUsedAt timestamps)  
✅ Template validation and error handling  
✅ Clean architecture with proper repository pattern  
✅ Comprehensive test coverage (50+ tests)

### Production Enhancements Needed
🔄 Migrate to Room database for persistent storage  
🔄 Add database migrations for schema changes  
🔄 Implement caching layer for performance  
🔄 Add query optimization for large template lists  
🔄 Support backup/restore functionality  
🔄 Add template import/export (JSON)

### Production Upgrade

```kotlin
// 1. Create Room Entity
@Entity(tableName = "templates")
data class TemplateEntity(
    @PrimaryKey val id: String,
    val name: String,
    val pattern: String,
    val configJson: String, // Serialize RenameConfig to JSON
    val isFavorite: Boolean,
    val createdAt: Long,
    val lastUsedAt: Long?
)

// Extension functions for mapping
fun TemplateEntity.toDomain(json: Json): RenameTemplate {
    val config = json.decodeFromString<RenameConfig>(configJson)
    return RenameTemplate(
        id = id,
        name = name,
        pattern = pattern,
        config = config,
        isFavorite = isFavorite,
        createdAt = Instant.ofEpochMilli(createdAt),
        lastUsedAt = lastUsedAt?.let { Instant.ofEpochMilli(it) }
    )
}

fun RenameTemplate.toEntity(json: Json): TemplateEntity {
    return TemplateEntity(
        id = id,
        name = name,
        pattern = pattern,
        configJson = json.encodeToString(config),
        isFavorite = isFavorite,
        createdAt = createdAt.toEpochMilli(),
        lastUsedAt = lastUsedAt?.toEpochMilli()
    )
}

// 2. Create DAO
@Dao
interface TemplateDao {
    @Query("SELECT * FROM templates ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<TemplateEntity>>
    
    @Query("SELECT * FROM templates WHERE isFavorite = 1 ORDER BY lastUsedAt DESC")
    fun observeFavorites(): Flow<List<TemplateEntity>>
    
    @Query("SELECT * FROM templates WHERE id = :id")
    suspend fun getById(id: String): TemplateEntity?
    
    @Query("SELECT * FROM templates WHERE name LIKE :query ORDER BY name ASC")
    suspend fun searchByName(query: String): List<TemplateEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(template: TemplateEntity)
    
    @Update
    suspend fun update(template: TemplateEntity)
    
    @Query("DELETE FROM templates WHERE id = :id")
    suspend fun deleteById(id: String): Int
    
    @Query("DELETE FROM templates")
    suspend fun deleteAll()
    
    @Query("SELECT COUNT(*) FROM templates")
    suspend fun count(): Int
}

// 3. Create Database
@Database(
    entities = [
        TemplateEntity::class,
        // Add other entities here (History, Tags, Activity)
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun templateDao(): TemplateDao
    abstract fun historyDao(): HistoryDao
    abstract fun tagDao(): TagDao
    abstract fun activityDao(): ActivityDao
}

// 4. Update Repository Implementation
@Singleton
class TemplateRepositoryImpl @Inject constructor(
    private val templateDao: TemplateDao,
    private val json: Json,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TemplateRepository {
    
    override suspend fun saveTemplate(template: RenameTemplate): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                val entity = template.toEntity(json)
                templateDao.insert(entity)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(Exception("Failed to save template", e))
            }
        }
    
    override fun observeTemplates(): Flow<List<RenameTemplate>> {
        return templateDao.observeAll()
            .map { entities -> entities.map { it.toDomain(json) } }
            .catch { e -> 
                emit(emptyList())
                Log.e(TAG, "Error observing templates", e)
            }
    }
    
    override suspend fun getTemplate(id: String): Result<RenameTemplate?> =
        withContext(ioDispatcher) {
            try {
                val entity = templateDao.getById(id)
                Result.Success(entity?.toDomain(json))
            } catch (e: Exception) {
                Result.Error(Exception("Failed to get template", e))
            }
        }
    
    override suspend fun deleteTemplate(id: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                val deleted = templateDao.deleteById(id)
                if (deleted > 0) {
                    Result.Success(Unit)
                } else {
                    Result.Error(Exception("Template not found"))
                }
            } catch (e: Exception) {
                Result.Error(Exception("Failed to delete template", e))
            }
        }
    
    override suspend fun updateLastUsed(id: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                val entity = templateDao.getById(id)
                    ?: return@withContext Result.Error(Exception("Template not found"))
                
                val updated = entity.copy(lastUsedAt = System.currentTimeMillis())
                templateDao.update(updated)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(Exception("Failed to update last used", e))
            }
        }
    
    override fun observeFavoriteTemplates(): Flow<List<RenameTemplate>> {
        return templateDao.observeFavorites()
            .map { entities -> entities.map { it.toDomain(json) } }
            .catch { e ->
                emit(emptyList())
                Log.e(TAG, "Error observing favorites", e)
            }
    }
    
    companion object {
        private const val TAG = "TemplateRepositoryImpl"
    }
}

// 5. Add to DI Module
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        )
        .addMigrations(/* future migrations */)
        .fallbackToDestructiveMigration() // For development only!
        .build()
    }
    
    @Provides
    fun provideTemplateDao(database: AppDatabase): TemplateDao {
        return database.templateDao()
    }
    
    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            prettyPrint = false
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }
}

// 6. Add Database Migrations (Example)
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE templates ADD COLUMN tags TEXT NOT NULL DEFAULT ''")
    }
}
```

### Trade-offs

**Current Implementation:**
- ✅ Instant setup, no database configuration needed
- ✅ Perfect for rapid UI development and testing
- ✅ Zero migration complexity during development
- ✅ Easy to debug and modify
- ⚠️ Data lost on app restart (acceptable for development)
- ⚠️ No query optimization for large datasets

**Production Implementation:**
- ✅ Persistent storage across app restarts
- ✅ Efficient querying for large datasets
- ✅ Database migrations for schema evolution
- ✅ Transaction support for data integrity
- ✅ Backup/restore capabilities
- ⚠️ Requires database setup and testing
- ⚠️ More complex error handling
- ⚠️ Migration strategy needed for updates

---

## 7️⃣ HistoryRepositoryImpl.kt

**Location:** `data/repository/HistoryRepositoryImpl.kt`  
**Chunk:** 14 (Undo/Redo System)  
**Priority:** Medium

### Strategic Implementation
Uses in-memory storage (MutableStateFlow + List) to provide complete undo/redo functionality without requiring Room database setup. Enables immediate UI development for history management features while database architecture is prepared.

### Fully Functional Features
✅ Complete undo/redo operations with MediaStore integration  
✅ Operation history tracking and state management  
✅ Flow-based reactive observation  
✅ Thread-safe with Mutex synchronization  
✅ Real file rename operations (undo/redo)  
✅ Operation validation and error handling  
✅ Recent operations filtering  
✅ Individual operation deletion  
✅ Clean architecture with proper repository pattern  
✅ Comprehensive test coverage (55+ tests)  
✅ **UI Complete:** History screen with undo/redo buttons (Dec 9, 2025)

### Production Enhancements Needed
🔄 Migrate to Room database for persistent storage  
🔄 Add database migrations for schema changes  
🔄 Implement operation expiry (auto-delete old operations)  
🔄 Add backup/restore functionality  
🔄 Support batch undo/redo operations  
🔄 Add operation search and filtering

### Production Upgrade

See FILE_SYSTEM_MOCKS.md for detailed Room implementation patterns. Key additions:

```kotlin
// Entity with undo state tracking
@Entity(tableName = "rename_operations")
data class OperationEntity(
    @PrimaryKey val id: String,
    val originalName: String,
    val newName: String,
    val originalUri: String,
    val newUri: String,
    val timestamp: Long,
    val isUndone: Boolean = false,
    val folderPath: String
)

// Automatic cleanup worker
@HiltWorker
class HistoryCleanupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val historyDao: HistoryDao
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            val cutoffTime = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)
            historyDao.deleteOlderThan(cutoffTime)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
```

### Trade-offs

**Current Implementation:**
- ✅ Instant setup, no database configuration needed
- ✅ Full undo/redo with real file operations
- ✅ Easy to debug and test
- ⚠️ History lost on app restart (acceptable for development)

**Production Implementation:**
- ✅ Persistent history across app restarts
- ✅ Automatic cleanup of old operations
- ✅ Database transactions for consistency
- ⚠️ More complex state synchronization

---

## 8️⃣ TagRepositoryImpl.kt

**Location:** `data/repository/TagRepositoryImpl.kt`  
**Chunk:** 16 (Tag System for Files)  
**Priority:** Medium

### Strategic Implementation
Uses in-memory storage (MutableStateFlow + HashMap) to provide complete tag management functionality without requiring Room database setup. Enables immediate UI development for file organization features while database architecture is prepared.

### Fully Functional Features
✅ Complete CRUD operations for tags  
✅ Many-to-many file-tag associations  
✅ Flow-based reactive observation  
✅ Thread-safe with Mutex synchronization  
✅ Tag validation and color management  
✅ Search and filtering by tag name  
✅ File queries by tag  
✅ Clean architecture with proper repository pattern  
✅ Comprehensive test coverage (70+ tests)

### Production Enhancements Needed
🔄 Migrate to Room database for persistent storage  
🔄 Implement proper many-to-many relationships with junction table  
🔄 Add complex JOIN queries for efficient file-tag lookups  
🔄 Integrate with MediaStore for real file metadata  
🔄 Add query optimization for large tag lists  
🔄 Support tag import/export functionality  
🔄 Add tag usage analytics and statistics

### Production Upgrade

Room implementation with many-to-many relationship:

```kotlin
// Junction table for many-to-many relationship
@Entity(
    tableName = "file_tag_cross_ref",
    primaryKeys = ["fileUriString", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tagId"), Index("fileUriString")]
)
data class FileTagCrossRef(
    val fileUriString: String,
    val tagId: String
)

// Complex JOIN query
@Transaction
@Query("""
    SELECT file_tags.* FROM file_tags
    INNER JOIN file_tag_cross_ref ON file_tags.id = file_tag_cross_ref.tagId
    WHERE file_tag_cross_ref.fileUriString = :fileUri
    ORDER BY file_tags.name ASC
""")
suspend fun getTagsForFile(fileUri: String): List<TagEntity>
```

### Trade-offs

**Current Implementation:**
- ✅ Instant setup, no database configuration needed
- ✅ Full tag CRUD and file associations
- ✅ Easy to test many-to-many relationships
- ⚠️ Data lost on app restart (acceptable for development)

**Production Implementation:**
- ✅ Persistent storage across app restarts
- ✅ Efficient many-to-many relationships
- ✅ Complex JOIN queries for performance
- ✅ Real file metadata from MediaStore
- ⚠️ More complex query optimization needed

---

## 1️⃣3️⃣ ActivityRepositoryImpl.kt

**Location:** `data/repository/ActivityRepositoryImpl.kt`  
**Chunk:** 21 (Activity Log & Export)  
**Priority:** Medium

### Strategic Implementation
Uses in-memory storage (MutableList) to provide complete activity logging functionality without requiring Room database setup. This approach enables immediate development of activity logging UI and export features.

### Fully Functional Features
✅ Complete activity log tracking with unique IDs  
✅ Thread-safe operations with Mutex  
✅ Advanced filtering by date, status, and action  
✅ CSV export with proper escaping and formatting  
✅ JSON export with metadata and pretty printing  
✅ File export with FileProvider integration  
✅ Clean architecture with proper repository pattern  
✅ Comprehensive error handling  
✅ **UI Complete:** Activity log screen with filtering and export (Dec 9, 2025)

### Production Enhancements Needed
🔄 Upgrade to Room database for persistent storage  
🔄 Add background cleanup for old logs  
🔄 Implement pagination for large log sets  
🔄 Add database indices for efficient queries  
🔄 Implement log rotation and archival

### Production Upgrade

Room implementation with automatic cleanup:

```kotlin
// Automatic cleanup worker
@HiltWorker
class LogCleanupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val activityDao: ActivityDao
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            // Delete logs older than 90 days
            val cutoffTime = System.currentTimeMillis() - (90 * 24 * 60 * 60 * 1000L)
            activityDao.deleteOlderThan(cutoffTime)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}

// Efficient filtered queries
@Query("""
    SELECT * FROM activity_logs 
    WHERE (:startTime IS NULL OR timestamp >= :startTime)
    AND (:endTime IS NULL OR timestamp <= :endTime)
    AND (:status IS NULL OR status = :status)
    AND (:action IS NULL OR action = :action)
    ORDER BY timestamp DESC 
    LIMIT :limit
""")
suspend fun getFiltered(
    startTime: Long?,
    endTime: Long?,
    status: String?,
    action: String?,
    limit: Int
): List<ActivityEntity>
```

### Trade-offs

**Current Implementation:**
- ✅ Instant setup, no database configuration needed
- ✅ CSV/JSON export fully functional
- ✅ Complete filtering logic
- ⚠️ Logs lost on app restart (acceptable for development)

**Production Implementation:**
- ✅ Persistent logs across app restarts
- ✅ Automatic cleanup of old logs
- ✅ Efficient pagination for large datasets
- ⚠️ Requires database setup and indices

---

## 📚 Related Documentation

- **Main Index:** [MOCK_IMPLEMENTATIONS.md](../../MOCK_IMPLEMENTATIONS.md)
- **CHUNK 12 Completion:** [CHUNK_12_COMPLETION.md](../../CHUNK_12_COMPLETION.md) - Pattern Templates
- **CHUNK 14 Completion:** [CHUNK_14_COMPLETION.md](../../CHUNK_14_COMPLETION.md) - Undo/Redo System
- **CHUNK 16 Completion:** [CHUNK_16_COMPLETION.md](../../CHUNK_16_COMPLETION.md) - Tag System
- **CHUNK 21 Completion:** [CHUNK_21_COMPLETION.md](../../CHUNK_21_COMPLETION.md) - Activity Log
- **README Implementation Strategy:** [README.md - Group 1-3,7](../../README.md#group-1-3-7-native-android-components)

---

**Document Status:** Complete  
**Last Updated:** December 10, 2025  
**Prepared By:** Development Team
