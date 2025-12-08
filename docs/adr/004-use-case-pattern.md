# ADR 004: Use Case Pattern for Business Logic

**Date:** December 8, 2025  
**Status:** Accepted  
**Deciders:** Kai (Backend)

## Context

Business logic needs to be:
- **Reusable**: Same logic used by multiple ViewModels
- **Testable**: Pure functions without Android dependencies
- **Maintainable**: Single responsibility, easy to understand
- **Discoverable**: Clear naming and organization
- **Composable**: Use cases can call other use cases

Traditional approach of putting logic in ViewModels leads to:
- Code duplication across ViewModels
- Difficult unit testing (need to mock Android context)
- Large ViewModels with multiple responsibilities
- Business logic tied to presentation layer

## Decision

We will implement **Use Case Pattern** (also known as Interactor pattern) with:
- **Single Responsibility**: Each use case does one thing
- **Base Class**: `BaseUseCase<Input, Output>` for consistency
- **Coroutines**: Proper dispatcher selection for different operations
- **Result Wrapper**: Type-safe success/error handling

## Architecture

```kotlin
// 1. Base Use Case (domain/common/)
abstract class BaseUseCase<in Input, out Output>(
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(input: Input): Result<Output> =
        withContext(dispatcher) {
            try {
                execute(input)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    
    @Throws(Exception::class)
    protected abstract suspend fun execute(input: Input): Result<Output>
}

// 2. Concrete Use Case (domain/usecase/)
class GenerateFilenameUseCase @Inject constructor() : 
    BaseUseCase<GenerateFilenameUseCase.Params, String>(Dispatchers.Default) {
    
    data class Params(
        val file: FileItem,
        val config: RenameConfig,
        val index: Int
    )
    
    override suspend fun execute(input: Params): Result<String> {
        val (file, config, index) = input
        
        // Validate input
        if (index < 0) {
            return Result.Error(Exception("Index must be non-negative"))
        }
        
        // Generate filename
        val paddedNumber = (config.startNumber + index)
            .toString()
            .padStart(config.digitCount, '0')
        
        val newName = buildString {
            if (config.prefix.isNotBlank()) {
                append(config.prefix)
                append("_")
            }
            append(paddedNumber)
        }
        
        val extension = if (config.preserveExtension) {
            file.name.substringAfterLast('.', "")
        } else {
            ""
        }
        
        val filename = if (extension.isNotBlank()) {
            "$newName.$extension"
        } else {
            newName
        }
        
        return Result.Success(filename)
    }
}

// 3. Usage in ViewModel
class RenameViewModel @Inject constructor(
    private val generateFilenameUseCase: GenerateFilenameUseCase,
    private val validateFilenameUseCase: ValidateFilenameUseCase
) : ViewModel() {
    
    fun generatePreview(files: List<FileItem>, config: RenameConfig) {
        viewModelScope.launch {
            val previews = files.mapIndexed { index, file ->
                val params = GenerateFilenameUseCase.Params(file, config, index)
                when (val result = generateFilenameUseCase(params)) {
                    is Result.Success -> {
                        val filename = result.data
                        val validation = validateFilenameUseCase(filename)
                        PreviewItem(file, filename, validation)
                    }
                    is Result.Error -> {
                        PreviewItem(file, "", hasError = true)
                    }
                    is Result.Loading -> { /* skip */ }
                }
            }
            _state.update { it.copy(previews = previews) }
        }
    }
}
```

## Use Case Types

### 1. **Query Use Cases** (Read Operations)
Return data without modifying state:

```kotlin
class GetMediaFilesUseCase @Inject constructor(
    private val repository: MediaRepository
) : BaseUseCase<FileFilter, List<FileItem>>(Dispatchers.IO) {
    
    override suspend fun execute(input: FileFilter): Result<List<FileItem>> {
        return repository.getMediaFiles(input)
    }
}

class GetTemplateByIdUseCase @Inject constructor(
    private val repository: TemplateRepository
) : BaseUseCase<String, RenameTemplate>(Dispatchers.IO) {
    
    override suspend fun execute(input: String): Result<RenameTemplate> {
        return repository.getTemplateById(input)
    }
}
```

### 2. **Command Use Cases** (Write Operations)
Modify state or perform actions:

```kotlin
class SaveTemplateUseCase @Inject constructor(
    private val repository: TemplateRepository
) : BaseUseCase<RenameTemplate, Unit>(Dispatchers.IO) {
    
    override suspend fun execute(input: RenameTemplate): Result<Unit> {
        // Validation
        if (input.name.isBlank()) {
            return Result.Error(Exception("Template name cannot be empty"))
        }
        
        // Save
        return repository.saveTemplate(input)
    }
}

class ExecuteBatchRenameUseCase @Inject constructor(
    private val fileRenameRepository: FileRenameRepository,
    private val historyRepository: HistoryRepository
) : BaseUseCase<ExecuteBatchRenameUseCase.Params, Flow<RenameProgress>>(Dispatchers.IO) {
    
    data class Params(
        val files: List<FileItem>,
        val config: RenameConfig,
        val cancellationToken: CancellationToken? = null
    )
    
    override suspend fun execute(input: Params): Result<Flow<RenameProgress>> {
        val (files, config, token) = input
        
        val progressFlow = flow {
            files.forEachIndexed { index, file ->
                // Check cancellation
                if (token?.isCancelled == true) {
                    throw OperationCancelledException("Rename cancelled by user")
                }
                
                // Generate filename
                val newName = generateFilename(file, config, index)
                
                // Rename file
                emit(RenameProgress(index, files.size, file, RenameStatus.InProgress))
                
                when (val result = fileRenameRepository.renameFile(file.uri, newName)) {
                    is Result.Success -> {
                        emit(RenameProgress(index, files.size, file, RenameStatus.Success))
                        historyRepository.saveOperation(
                            RenameOperation(file.uri, result.data, file.name, newName)
                        )
                    }
                    is Result.Error -> {
                        emit(RenameProgress(index, files.size, file, RenameStatus.Failed(result.message)))
                    }
                    is Result.Loading -> { /* skip */ }
                }
            }
        }
        
        return Result.Success(progressFlow)
    }
}
```

### 3. **Validation Use Cases**
Pure business logic validation:

```kotlin
class ValidateFilenameUseCase @Inject constructor() : 
    BaseUseCase<String, ValidateFilenameUseCase.ValidationResult>(Dispatchers.Default) {
    
    data class ValidationResult(
        val isValid: Boolean,
        val errors: List<String> = emptyList()
    )
    
    override suspend fun execute(input: String): Result<ValidationResult> {
        val errors = mutableListOf<String>()
        
        // Check length
        if (input.length > 255) {
            errors.add("Filename too long (max 255 characters)")
        }
        
        // Check illegal characters
        val illegalChars = listOf('/', '\\', ':', '*', '?', '"', '<', '>', '|')
        if (input.any { it in illegalChars }) {
            errors.add("Contains illegal characters: ${illegalChars.joinToString()}")
        }
        
        // Check reserved names (Windows)
        val reservedNames = listOf("CON", "PRN", "AUX", "NUL", "COM1", "LPT1")
        if (input.uppercase() in reservedNames) {
            errors.add("Cannot use reserved filename: $input")
        }
        
        return Result.Success(ValidationResult(errors.isEmpty(), errors))
    }
}
```

### 4. **Observation Use Cases** (Flow-based)
For reactive data streams:

```kotlin
class ObserveTemplatesUseCase @Inject constructor(
    private val repository: TemplateRepository
) {
    operator fun invoke(): Flow<List<RenameTemplate>> {
        return repository.observeTemplates()
    }
}

class ObserveMonitoringStatusUseCase @Inject constructor(
    private val repository: FolderMonitorRepository
) {
    operator fun invoke(monitorId: String): Flow<MonitoringStatus> {
        return repository.observeStatus(monitorId)
    }
}
```

## Benefits

✅ **Single Responsibility**: Each use case has one clear purpose  
✅ **Testability**: Pure functions, easy to unit test  
✅ **Reusability**: Same use case used by multiple ViewModels  
✅ **Composability**: Use cases can call other use cases  
✅ **Consistency**: Base class ensures uniform error handling  
✅ **Discoverability**: Organized by feature in `usecase/` folder

## Naming Conventions

### Command Use Cases (Actions)
- `Execute*UseCase`: Performs an action (ExecuteBatchRenameUseCase)
- `Save*UseCase`: Saves data (SaveTemplateUseCase)
- `Delete*UseCase`: Deletes data (DeleteTemplateUseCase)
- `Create*UseCase`: Creates entity (CreateTagUseCase)
- `Update*UseCase`: Updates entity (UpdatePreferencesUseCase)

### Query Use Cases (Reads)
- `Get*UseCase`: Fetches data once (GetMediaFilesUseCase)
- `Observe*UseCase`: Streams data (ObserveTemplatesUseCase)
- `Check*UseCase`: Validates state (CheckPermissionsUseCase)
- `Has*UseCase`: Boolean check (HasMediaAccessUseCase)

### Processing Use Cases
- `Generate*UseCase`: Creates derived data (GenerateFilenameUseCase)
- `Validate*UseCase`: Validates input (ValidateFilenameUseCase)
- `Parse*UseCase`: Parses data (ParseQRCodeUseCase)
- `Extract*UseCase`: Extracts information (ExtractMetadataUseCase)
- `Analyze*UseCase`: Analyzes content (AnalyzeImageUseCase)

## Testing

```kotlin
class GenerateFilenameUseCaseTest {
    private lateinit var useCase: GenerateFilenameUseCase
    
    @Before
    fun setup() {
        useCase = GenerateFilenameUseCase()
    }
    
    @Test
    fun `generates filename with prefix and padding`() = runTest {
        val file = FileItem(id = 1, name = "photo.jpg", ...)
        val config = RenameConfig(
            prefix = "IMG",
            startNumber = 1,
            digitCount = 3,
            preserveExtension = true
        )
        val params = GenerateFilenameUseCase.Params(file, config, index = 0)
        
        val result = useCase(params)
        
        assertTrue(result is Result.Success)
        assertEquals("IMG_001.jpg", (result as Result.Success).data)
    }
    
    @Test
    fun `returns error for negative index`() = runTest {
        val params = GenerateFilenameUseCase.Params(..., index = -1)
        
        val result = useCase(params)
        
        assertTrue(result is Result.Error)
    }
}
```

## Organization

```
domain/usecase/
├── permissions/
│   ├── CheckPermissionsUseCase.kt
│   ├── GetRequiredPermissionsUseCase.kt
│   └── HasMediaAccessUseCase.kt
├── fileselection/
│   └── GetMediaFilesUseCase.kt
├── rename/
│   ├── GenerateFilenameUseCase.kt
│   ├── ValidateFilenameUseCase.kt
│   └── ExecuteBatchRenameUseCase.kt
├── template/
│   ├── SaveTemplateUseCase.kt
│   ├── GetTemplatesUseCase.kt
│   ├── DeleteTemplateUseCase.kt
│   └── ObserveTemplatesUseCase.kt
├── history/
│   ├── UndoRenameUseCase.kt
│   ├── RedoRenameUseCase.kt
│   └── GetHistoryUseCase.kt
└── ai/
    ├── AnalyzeImageUseCase.kt
    └── GenerateSuggestionsUseCase.kt
```

## Trade-offs

### Benefits
- ✅ Clean separation of business logic
- ✅ Easy to test without ViewModels
- ✅ Reusable across multiple screens
- ✅ Easy to understand (single responsibility)

### Costs
- ⚠️ More classes (one per operation)
- ⚠️ Might feel like over-engineering for simple CRUD
- ✅ Worth it for complex business logic and long-term maintenance

## Related ADRs
- ADR 001: Clean Architecture
- ADR 003: Repository Pattern

## References
- [Use Case Pattern](https://www.baeldung.com/cs/use-case-pattern)
- [Android App Architecture Guide](https://developer.android.com/topic/architecture)
