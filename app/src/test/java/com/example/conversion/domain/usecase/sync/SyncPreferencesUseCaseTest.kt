package com.example.conversion.domain.usecase.sync

import com.example.conversion.domain.model.SyncStatus
import com.example.conversion.domain.repository.SyncRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SyncPreferencesUseCaseTest {
    
    private lateinit var syncRepository: SyncRepository
    private lateinit var useCase: SyncPreferencesUseCase
    
    @Before
    fun setup() {
        syncRepository = mockk()
        useCase = SyncPreferencesUseCase(syncRepository)
    }
    
    @Test
    fun `invoke should call repository syncPreferences`() = runTest {
        // Given
        coEvery { syncRepository.syncPreferences() } returns Result.success(Unit)
        
        // When
        val result = useCase()
        
        // Then
        coVerify(exactly = 1) { syncRepository.syncPreferences() }
        assertTrue(result.isSuccess)
    }
    
    @Test
    fun `invoke should return success when repository succeeds`() = runTest {
        // Given
        coEvery { syncRepository.syncPreferences() } returns Result.success(Unit)
        
        // When
        val result = useCase()
        
        // Then
        assertTrue(result.isSuccess)
    }
    
    @Test
    fun `invoke should return failure when repository fails`() = runTest {
        // Given
        val error = Exception("Network error")
        coEvery { syncRepository.syncPreferences() } returns Result.failure(error)
        
        // When
        val result = useCase()
        
        // Then
        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }
}
