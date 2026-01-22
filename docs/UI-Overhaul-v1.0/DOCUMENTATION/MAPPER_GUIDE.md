# Data Mapping Guide

> **Project:** Files Management Service - Android App  
> **Purpose:** Document the entity mapper pattern and best practices  
> **Last Updated:** January 22, 2026

---

## Overview

The mapper pattern centralizes all entity ↔ domain model conversions in dedicated mapper objects. This maintains Clean Architecture by keeping domain models pure Kotlin while providing a consistent conversion strategy across all entities.

## Why Mappers?

### The Problem
Without centralized mappers, conversion logic scattered throughout the codebase:
```kotlin
// ❌ BAD: Scattered mapping logic
class ActivityRepositoryImpl {
    fun getActivities(): Result<List<ActivityLog>> {
        return activityDao.getAll().map { 
            ActivityLog(
                id = it.id,
                operationId = it.operationId,
                timestamp = it.timestamp
            )
        }
    }
}

class HistoryViewModel {
    init {
        viewModelScope.launch {
            val activities = activityDao.getAll()
            // Duplicate mapping logic here too
            val logs = activities.map {
                ActivityLog(id = it.id, /* ... */)
            }
        }
    }
}
```

### The Solution
Centralize in mapper objects:
```kotlin
// ✅ GOOD: Centralized mapper
object ActivityLogMapper {
    fun ActivityLogEntity.toDomain(): ActivityLog = ActivityLog(
        id = this.id,
        operationId = this.operationId,
        timestamp = this.timestamp
    )
}

// Usage anywhere
class ActivityRepositoryImpl {
    fun getActivities(): Result<List<ActivityLog>> =
        activityDao.getAll().map { it.toDomain() }
}
```

## Mapper Architecture

### Location
```
app/src/main/java/com/example/conversion/data/local/mapper/
├── ActivityLogMapper.kt
├── TemplateMapper.kt
├── TagMapper.kt
└── OperationMapper.kt
```

### Structure
```kotlin
package com.example.conversion.data.local.mapper

import com.example.conversion.data.local.entity.ExampleEntity
import com.example.conversion.domain.model.ExampleModel

/**
 * Mapper for converting between ExampleModel and ExampleEntity.
 * 
 * Maps domain models to Room entities and vice versa.
 * Handles any necessary type conversions and validation.
 */
object ExampleMapper {
    
    /**
     * Converts ExampleEntity to domain ExampleModel.
     */
    fun ExampleEntity.toDomain(): ExampleModel {
        return ExampleModel(
            field1 = this.field1,
            field2 = this.field2,
            // etc.
        )
    }
    
    /**
     * Converts domain ExampleModel to ExampleEntity.
     */
    fun ExampleModel.toEntity(): ExampleEntity {
        return ExampleEntity(
            field1 = this.field1,
            field2 = this.field2,
            // etc.
        )
    }
}
```

## Implementation Patterns

### Pattern 1: Simple 1:1 Field Mapping

**When to use:** Most entities with straightforward field mappings (no complex types)

```kotlin
object TagMapper {
    
    fun TagEntity.toDomain(): FileTag {
        return FileTag(
            id = this.id,
            name = this.name,
            color = this.color,
            createdAt = this.createdAt
        )
    }
    
    fun FileTag.toEntity(): TagEntity {
        return TagEntity(
            id = this.id,
            name = this.name,
            color = this.color,
            createdAt = this.createdAt
        )
    }
}
```

### Pattern 2: Type Conversion (Strings ↔ Enums)

**When to use:** Entity stores string, domain uses enum

```kotlin
object OperationMapper {
    
    fun OperationEntity.toDomain(): RenameOperation {
        return RenameOperation(
            id = this.id,
            status = OperationStatus.valueOf(this.status),  // String → Enum
            filesProcessed = this.filesProcessed
        )
    }
    
    fun RenameOperation.toEntity(): OperationEntity {
        return OperationEntity(
            id = this.id,
            status = this.status.name,  // Enum → String
            filesProcessed = this.filesProcessed
        )
    }
}
```

### Pattern 3: Nested Objects (JSON Serialization)

**When to use:** Entity stores JSON, domain uses data class

```kotlin
object TemplateMapper {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    fun TemplateEntity.toDomain(): RenameTemplate {
        return RenameTemplate(
            id = this.id,
            name = this.name,
            config = json.decodeFromString<RenameConfig>(this.configJson),
            tags = json.decodeFromString<List<String>>(this.tagsJson)
        )
    }
    
    fun RenameTemplate.toEntity(): TemplateEntity {
        return TemplateEntity(
            id = this.id,
            name = this.name,
            configJson = json.encodeToString(this.config),
            tagsJson = json.encodeToString(this.tags)
        )
    }
}
```

### Pattern 4: Computed Fields

**When to use:** Domain model includes derived properties not stored in DB

```kotlin
object ActivityLogMapper {
    
    fun ActivityLogEntity.toDomain(): ActivityLog {
        return ActivityLog(
            id = this.id,
            operationId = this.operationId,
            timestamp = this.timestamp,
            action = this.action,
            description = this.description,
            // Computed field (not in entity)
            formattedTime = SimpleDateFormat.getInstance().format(Date(this.timestamp))
        )
    }
    
    fun ActivityLog.toEntity(): ActivityLogEntity {
        // Note: formattedTime is not included (computed at display time)
        return ActivityLogEntity(
            id = this.id,
            operationId = this.operationId,
            timestamp = this.timestamp,
            action = this.action,
            description = this.description
        )
    }
}
```

### Pattern 5: Default Values

**When to use:** DB field is nullable, domain requires non-null default

```kotlin
fun ActivityLogEntity.toDomain(): ActivityLog {
    return ActivityLog(
        id = this.id,
        description = this.description ?: "No description",  // Default value
        timestamp = this.timestamp ?: System.currentTimeMillis()
    )
}
```

### Pattern 6: Collection Mapping

**When to use:** Entity contains List<String> or similar, domain uses specialized list

```kotlin
fun TemplateEntity.toDomain(): RenameTemplate {
    return RenameTemplate(
        // ...
        appliedTags = this.tagList.split(",")
            .filter { it.isNotBlank() }
            .map { it.trim() }
    )
}

fun RenameTemplate.toEntity(): TemplateEntity {
    return TemplateEntity(
        // ...
        tagList = this.appliedTags.joinToString(",")
    )
}
```

## Using Mappers

### In Repository Implementations

```kotlin
import com.example.conversion.data.local.mapper.ActivityLogMapper
import com.example.conversion.data.local.mapper.ActivityLogMapper.toDomain

class ActivityRepositoryImpl @Inject constructor(
    private val activityDao: ActivityDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ActivityRepository {
    
    override suspend fun getActivitiesForOperation(operationId: String): Result<List<ActivityLog>> =
        withContext(dispatcher) {
            resultOf {
                activityDao.getActivitiesForOperation(operationId)
                    .map { it.toDomain() }  // Extension function from mapper
            }
        }
    
    override suspend fun saveActivity(activity: ActivityLog): Result<Unit> =
        withContext(dispatcher) {
            resultOf {
                activityDao.insert(activity.toEntity())  // Extension function from mapper
            }
        }
}
```

### Single Entity Mapping

```kotlin
// Import the mapper object
import com.example.conversion.data.local.mapper.TagMapper
import com.example.conversion.data.local.mapper.TagMapper.toDomain

// Use extension functions
val entity = tagDao.getById(tagId)
val domainTag = entity.toDomain()
```

### Batch Mapping

```kotlin
import com.example.conversion.data.local.mapper.OperationMapper.toDomain

val entities = operationDao.getAllOperations()
val domainModels = entities.map { it.toDomain() }

// Or more explicitly
val domainModels: List<RenameOperation> = entities.map { entity ->
    entity.toDomain()
}
```

## When Adding a New Entity

Follow this checklist:

### Step 1: Create Domain Model
```kotlin
// domain/model/NewModel.kt
data class NewModel(
    val id: String,
    val name: String,
    val createdAt: Long
)
```

### Step 2: Create Entity
```kotlin
// data/local/entity/NewEntity.kt
@Entity(tableName = "new_table")
data class NewEntity(
    @PrimaryKey val id: String,
    val name: String,
    val createdAt: Long
)
```

### Step 3: Create Mapper
```kotlin
// data/local/mapper/NewMapper.kt
package com.example.conversion.data.local.mapper

import com.example.conversion.data.local.entity.NewEntity
import com.example.conversion.domain.model.NewModel

object NewMapper {
    
    fun NewEntity.toDomain(): NewModel {
        return NewModel(
            id = this.id,
            name = this.name,
            createdAt = this.createdAt
        )
    }
    
    fun NewModel.toEntity(): NewEntity {
        return NewEntity(
            id = this.id,
            name = this.name,
            createdAt = this.createdAt
        )
    }
}
```

### Step 4: Use in Repository
```kotlin
// data/repository/NewRepositoryImpl.kt
import com.example.conversion.data.local.mapper.NewMapper
import com.example.conversion.data.local.mapper.NewMapper.toDomain

class NewRepositoryImpl @Inject constructor(
    private val newDao: NewDao
) : NewRepository {
    
    override suspend fun getNew(): Result<NewModel> =
        resultOf {
            newDao.get().toDomain()
        }
}
```

## Common Patterns to Avoid

### ❌ Avoid: Mapping Logic in Repository
```kotlin
// BAD
class ActivityRepositoryImpl {
    override suspend fun getActivities(): Result<List<ActivityLog>> =
        resultOf {
            activityDao.getAll().map { entity ->
                ActivityLog(
                    id = entity.id,
                    operationId = entity.operationId,
                    timestamp = entity.timestamp,
                    action = entity.action
                )
            }
        }
}
```

**Why:** Duplicates logic if called from multiple places

**Fix:** Move to mapper, use extension function

### ❌ Avoid: Mapping Logic in ViewModel
```kotlin
// BAD
class HistoryViewModel {
    fun loadActivities() {
        viewModelScope.launch {
            val entities = activityDao.getAll()
            val models = entities.map { entity ->
                ActivityLog(id = entity.id, /* ... */)
            }
        }
    }
}
```

**Why:** Violates Clean Architecture (presentation shouldn't do data layer work)

**Fix:** Keep mapping in data layer, use repository

### ❌ Avoid: Mixing Domain and Entity Properties
```kotlin
// BAD
data class ActivityLogEntity(
    @PrimaryKey val id: String,
    @ColumnInfo val operationId: String,
    val timestamp: Long,
    // ❌ Domain properties shouldn't be here
    val formattedTime: String,
    val isRecent: Boolean
)
```

**Why:** Entities should only have DB fields

**Fix:** Compute in mapper or domain model

## Testing Mappers

### Simple Unit Test

```kotlin
import com.example.conversion.data.local.entity.ActivityLogEntity
import com.example.conversion.data.local.mapper.ActivityLogMapper.toDomain
import com.example.conversion.data.local.mapper.ActivityLogMapper.toEntity
import com.example.conversion.domain.model.ActivityLog
import org.junit.Test
import kotlin.test.assertEquals

class ActivityLogMapperTest {
    
    @Test
    fun testEntityToDomain() {
        val entity = ActivityLogEntity(
            id = "123",
            operationId = "op-123",
            timestamp = 1000L,
            action = "RENAMED",
            description = "Renamed file.txt"
        )
        
        val domain = entity.toDomain()
        
        assertEquals("123", domain.id)
        assertEquals("op-123", domain.operationId)
        assertEquals(1000L, domain.timestamp)
    }
    
    @Test
    fun testDomainToEntity() {
        val domain = ActivityLog(
            id = "123",
            operationId = "op-123",
            timestamp = 1000L,
            action = "RENAMED",
            description = "Renamed file.txt"
        )
        
        val entity = domain.toEntity()
        
        assertEquals("123", entity.id)
        assertEquals("op-123", entity.operationId)
    }
    
    @Test
    fun testRoundTrip() {
        val original = ActivityLog(
            id = "123",
            operationId = "op-123",
            timestamp = 1000L,
            action = "RENAMED",
            description = "Renamed file.txt"
        )
        
        val roundTripped = original.toEntity().toDomain()
        
        assertEquals(original, roundTripped)
    }
}
```

## FAQ

### Q: Should I use mappers for responses from external APIs?

**A:** Yes, but create separate API mappers:
```kotlin
object OpenWeatherMapper {
    fun WeatherApiResponse.toDomain(): Weather = /* ... */
}
```

### Q: What if the entity and domain model are identical?

**A:** Still keep them separate! They may diverge later:
- Database schema changes
- New computed fields in domain
- Type conversions needed
- Default value handling

### Q: Can mappers do complex business logic?

**A:** No, keep mappers simple. Complex logic belongs in:
- **Use cases** (business rules)
- **Domain models** (computed properties)
- **Repositories** (data orchestration)

Mappers should only convert between types.

### Q: How do I handle optional fields?

**A:** Use Kotlin's null coalescing:
```kotlin
fun ActivityLogEntity.toDomain(): ActivityLog {
    return ActivityLog(
        id = this.id,
        description = this.description ?: "No description",
        metadata = this.metadata ?: emptyMap()
    )
}
```

### Q: Should mappers be object singletons or classes?

**A:** Use `object` singletons. They're stateless, thread-safe, and follow Kotlin idioms.

### Q: Where do I put complex mapping logic?

**A:** If mapping becomes complex (20+ lines), consider:
1. **Private helper functions** in the mapper
2. **Decompose into smaller mappers** for sub-objects
3. **Move validation to domain model** (not mapper's job)

Example:
```kotlin
object ConfigMapper {
    
    fun ConfigEntity.toDomain(): RenameConfig {
        return RenameConfig(
            prefix = this.prefix,
            rules = this.rules.map { mapRule(it) }
        )
    }
    
    private fun mapRule(entity: RuleEntity): RenameRule {
        return RenameRule(/* ... */)
    }
}
```

## Related Documentation

- [ADR 001: Clean Architecture](adr/001-clean-architecture.md#entity-mapping-pattern)
- [BACKEND_FRONTEND_CLEANUP_PLAN.md](UI-Overhaul-v1.0/ROADMAP/BACKEND_FRONTEND_CLEANUP_PLAN.md)
- [Repository Pattern (ADR 003)](adr/003-repository-pattern.md)

---

**Version:** 1.0  
**Last Updated:** January 22, 2026  
**Maintained By:** Kai (Backend Lead)
