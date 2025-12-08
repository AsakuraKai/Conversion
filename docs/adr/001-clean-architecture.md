# ADR 001: Clean Architecture Implementation

**Date:** December 8, 2025  
**Status:** Accepted  
**Deciders:** Kai (Backend), Sokchea (UI)

## Context

The Auto Rename File Service app requires a robust, maintainable architecture that supports:
- Clear separation of concerns
- Testability at all layers
- Independent UI and business logic evolution
- Easy onboarding for new developers

## Decision

We will implement **Clean Architecture** with three distinct layers:

### 1. Domain Layer (`domain/`)
- **Pure Kotlin**: No Android dependencies
- **Models**: Data classes representing business entities
- **Repositories**: Interfaces defining data contracts
- **Use Cases**: Single-responsibility business logic operations
- **Common**: Shared types (Result, BaseUseCase)

### 2. Data Layer (`data/`)
- **Repository Implementations**: Concrete implementations of domain interfaces
- **Data Sources**: MediaStore, Room database, SharedPreferences
- **Managers**: Specialized operations (FileOperationsManager)
- **Entities**: Database-specific models with mapping to domain models

### 3. Presentation Layer (`presentation/`)
- **UI**: Jetpack Compose screens and components
- **ViewModels**: MVI state management with StateFlow
- **Navigation**: Compose Navigation with type-safe routes
- **Theme**: Material 3 theming and dynamic colors

## Rationale

**Benefits:**
- ✅ **Testability**: Pure domain layer enables fast unit tests without Android dependencies
- ✅ **Independence**: UI changes don't affect business logic and vice versa
- ✅ **Maintainability**: Clear file organization and responsibility boundaries
- ✅ **Scalability**: Easy to add new features without affecting existing code
- ✅ **Parallel Development**: Kai works on domain/data, Sokchea on presentation

**Trade-offs:**
- ⚠️ More boilerplate (interfaces, mappers, multiple models)
- ⚠️ Steeper learning curve for developers unfamiliar with Clean Architecture
- ✅ Mitigated by clear examples and comprehensive documentation

## Implementation Details

### Dependency Flow
```
Presentation → Domain ← Data
```

- **Presentation** depends on **Domain** (uses interfaces and models)
- **Data** depends on **Domain** (implements interfaces)
- **Domain** has **no dependencies** on other layers

### Example Structure
```
app/src/main/java/com/example/conversion/
├── domain/
│   ├── model/
│   │   ├── FileItem.kt
│   │   ├── RenameConfig.kt
│   │   └── Permission.kt
│   ├── repository/
│   │   ├── MediaRepository.kt
│   │   └── PermissionsRepository.kt
│   ├── usecase/
│   │   ├── rename/
│   │   │   ├── GenerateFilenameUseCase.kt
│   │   │   └── ExecuteBatchRenameUseCase.kt
│   │   └── permissions/
│   │       └── CheckPermissionsUseCase.kt
│   └── common/
│       ├── Result.kt
│       └── BaseUseCase.kt
├── data/
│   ├── repository/
│   │   ├── MediaRepositoryImpl.kt
│   │   └── PermissionsManagerImpl.kt
│   ├── source/
│   │   └── local/
│   │       └── MediaStoreDataSource.kt
│   └── manager/
│       └── FileOperationsManager.kt
└── presentation/
    ├── ui/
    │   ├── main/
    │   ├── fileselection/
    │   └── rename/
    └── viewmodel/
        └── FileSelectionViewModel.kt
```

## Consequences

### Positive
- Clear boundaries enable parallel development
- Easy to write comprehensive tests
- Business logic is portable and reusable
- Simplified code reviews (changes are localized)

### Negative
- More files and interfaces to maintain
- Need to map between data and domain models
- Requires discipline to maintain layer separation

## Related ADRs
- ADR 002: MVI Pattern
- ADR 003: Repository Pattern
- ADR 004: Use Case Pattern

## References
- [Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Android Clean Architecture Guide](https://developer.android.com/topic/architecture)
