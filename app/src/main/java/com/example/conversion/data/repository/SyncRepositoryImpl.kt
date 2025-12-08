package com.example.conversion.data.repository

import com.example.conversion.domain.model.SyncStatus
import com.example.conversion.domain.model.UserPreferences
import com.example.conversion.domain.repository.SyncRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Mock implementation of SyncRepository for development
 * 
 * Simulates Firebase Firestore cloud sync behavior without requiring:
 * - Firebase setup and configuration
 * - Google Play Services
 * - Network connectivity
 * - Firebase Authentication
 * 
 * Features:
 * - Simulated network latency (500-1500ms)
 * - Configurable failure rate (10% by default)
 * - In-memory "cloud" storage
 * - Realistic sync status updates
 * - Conflict resolution simulation (last-write-wins)
 * 
 * **MOCK IMPLEMENTATION**: Replace with real Firebase Firestore in production
 * 
 * @see MOCK_IMPLEMENTATIONS.md for production upgrade path
 */
@Singleton
class SyncRepositoryImpl @Inject constructor() : SyncRepository {
    
    private val mutex = Mutex()
    
    // In-memory "cloud" storage
    private var cloudPreferences: UserPreferences? = null
    
    // Current sync status
    private val _syncStatus = MutableStateFlow(SyncStatus.IDLE)
    
    // Simulated failure rate (10%)
    private val failureRate = 0.1
    
    override suspend fun syncPreferences(): Result<Unit> = mutex.withLock {
        return try {
            // Update status: syncing started
            _syncStatus.value = SyncStatus.SYNCING
            
            // Simulate network latency
            delay(Random.nextLong(500, 1500))
            
            // Simulate occasional failures
            if (Random.nextDouble() < failureRate) {
                val errorMsg = "Network error: Unable to connect to sync service"
                _syncStatus.value = SyncStatus(
                    isSyncing = false,
                    lastSyncTime = _syncStatus.value.lastSyncTime,
                    error = errorMsg
                )
                return Result.failure(Exception(errorMsg))
            }
            
            // Simulate successful sync
            val now = System.currentTimeMillis()
            
            // In a real implementation, this would:
            // 1. Download cloud preferences
            // 2. Merge with local preferences (conflict resolution)
            // 3. Upload merged result
            
            // For mock: just update sync time
            _syncStatus.value = SyncStatus(
                isSyncing = false,
                lastSyncTime = now,
                error = null
            )
            
            Result.success(Unit)
        } catch (e: Exception) {
            _syncStatus.value = SyncStatus(
                isSyncing = false,
                lastSyncTime = _syncStatus.value.lastSyncTime,
                error = e.message ?: "Unknown error"
            )
            Result.failure(e)
        }
    }
    
    override suspend fun uploadPreferences(preferences: UserPreferences): Result<Unit> = mutex.withLock {
        return try {
            // Update status: syncing started
            _syncStatus.value = SyncStatus.SYNCING
            
            // Simulate network latency
            delay(Random.nextLong(500, 1500))
            
            // Simulate occasional failures
            if (Random.nextDouble() < failureRate) {
                val errorMsg = "Upload failed: Network timeout"
                _syncStatus.value = SyncStatus(
                    isSyncing = false,
                    lastSyncTime = _syncStatus.value.lastSyncTime,
                    error = errorMsg
                )
                return Result.failure(Exception(errorMsg))
            }
            
            // Store in "cloud"
            cloudPreferences = preferences.copy(
                lastSyncTimestamp = System.currentTimeMillis()
            )
            
            val now = System.currentTimeMillis()
            _syncStatus.value = SyncStatus(
                isSyncing = false,
                lastSyncTime = now,
                error = null
            )
            
            Result.success(Unit)
        } catch (e: Exception) {
            _syncStatus.value = SyncStatus(
                isSyncing = false,
                lastSyncTime = _syncStatus.value.lastSyncTime,
                error = e.message ?: "Unknown error"
            )
            Result.failure(e)
        }
    }
    
    override suspend fun downloadPreferences(): Result<UserPreferences> = mutex.withLock {
        return try {
            // Simulate network latency
            delay(Random.nextLong(300, 800))
            
            // Simulate occasional failures
            if (Random.nextDouble() < failureRate) {
                val errorMsg = "Download failed: Server unavailable"
                return Result.failure(Exception(errorMsg))
            }
            
            // Return cloud data or default preferences
            val prefs = cloudPreferences ?: UserPreferences()
            Result.success(prefs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun observeSyncStatus(): Flow<SyncStatus> {
        return _syncStatus.asStateFlow()
    }
    
    override suspend fun getSyncStatus(): Result<SyncStatus> {
        return Result.success(_syncStatus.value)
    }
}
