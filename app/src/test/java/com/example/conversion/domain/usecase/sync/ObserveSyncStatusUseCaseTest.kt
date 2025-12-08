package com.example.conversion.domain.usecase.sync

import com.example.conversion.domain.model.SyncStatus
import com.example.conversion.domain.repository.SyncRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ObserveSyncStatusUseCaseTest {
    
    private lateinit var syncRepository: SyncRepository
    private lateinit var useCase: ObserveSyncStatusUseCase
    
    @Before
    fun setup() {
        syncRepository = mockk()
        useCase = ObserveSyncStatusUseCase(syncRepository)
    }
    
    @Test
    fun `invoke should call repository observeSyncStatus`() = runTest {
        // Given
        val status = SyncStatus.IDLE
        every { syncRepository.observeSyncStatus() } returns flowOf(status)
        
        // When
        useCase().first()
        
        // Then
        verify(exactly = 1) { syncRepository.observeSyncStatus() }
    }
    
    @Test
    fun `invoke should return flow of sync status`() = runTest {
        // Given
        val status = SyncStatus(
            isSyncing = true,
            lastSyncTime = 123456789L,
            error = null
        )
        every { syncRepository.observeSyncStatus() } returns flowOf(status)
        
        // When
        val result = useCase().first()
        
        // Then
        assertEquals(status, result)
    }
    
    @Test
    fun `invoke should emit multiple status updates`() = runTest {
        // Given
        val status1 = SyncStatus.IDLE
        val status2 = SyncStatus.SYNCING
        val status3 = SyncStatus(
            isSyncing = false,
            lastSyncTime = 123456789L,
            error = null
        )
        every { syncRepository.observeSyncStatus() } returns flowOf(status1, status2, status3)
        
        // When
        val results = mutableListOf<SyncStatus>()
        useCase().collect { results.add(it) }
        
        // Then
        assertEquals(3, results.size)
        assertEquals(status1, results[0])
        assertEquals(status2, results[1])
        assertEquals(status3, results[2])
    }
}
