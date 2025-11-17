package com.example.conversion.data.repository

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.conversion.domain.model.Permission
import com.example.conversion.domain.model.PermissionStatus
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for PermissionsManagerImpl.
 * Tests permission checking logic for different scenarios.
 */
class PermissionsManagerImplTest {

    private lateinit var context: Context
    private lateinit var permissionsManager: PermissionsManagerImpl

    @Before
    fun setup() {
        // Mock Context
        context = mockk(relaxed = true)
        
        // Mock ContextCompat for permission checks
        mockkStatic(ContextCompat::class)
        
        // Create instance
        permissionsManager = PermissionsManagerImpl(context)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `checkPermissions returns correct state when all permissions granted`() = runTest {
        // Given: All permissions are granted
        every { 
            ContextCompat.checkSelfPermission(context, any()) 
        } returns PackageManager.PERMISSION_GRANTED

        // Mock MANAGE_EXTERNAL_STORAGE for Android 11+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            mockkStatic(android.os.Environment::class)
            every { android.os.Environment.isExternalStorageManager() } returns true
        }

        // When: Check permissions
        val result = permissionsManager.checkPermissions()

        // Then: All applicable permissions should be granted
        Permission.getAllRequiredPermissions()
            .filter { it.isApplicable() }
            .forEach { permission ->
                if (permission == Permission.MANAGE_EXTERNAL_STORAGE && 
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    assertEquals(PermissionStatus.Granted, result.permissionStatuses[permission])
                } else if (permission.isApplicable()) {
                    assertEquals(
                        "Permission $permission should be granted",
                        PermissionStatus.Granted, 
                        result.permissionStatuses[permission]
                    )
                }
            }
        
        assertTrue("All granted should be true", result.allGranted)
    }

    @Test
    fun `checkPermissions returns denied when permissions not granted`() = runTest {
        // Given: All permissions are denied
        every { 
            ContextCompat.checkSelfPermission(context, any()) 
        } returns PackageManager.PERMISSION_DENIED

        // Mock MANAGE_EXTERNAL_STORAGE for Android 11+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            mockkStatic(android.os.Environment::class)
            every { android.os.Environment.isExternalStorageManager() } returns false
        }

        // When: Check permissions
        val result = permissionsManager.checkPermissions()

        // Then: All applicable permissions should be denied
        Permission.getAllRequiredPermissions()
            .filter { it.isApplicable() }
            .forEach { permission ->
                assertEquals(
                    "Permission $permission should be denied",
                    PermissionStatus.Denied,
                    result.permissionStatuses[permission]
                )
            }
        
        assertFalse("All granted should be false", result.allGranted)
    }

    @Test
    fun `isPermissionGranted returns true when permission granted`() = runTest {
        // Given: Permission is granted
        every { 
            ContextCompat.checkSelfPermission(context, any()) 
        } returns PackageManager.PERMISSION_GRANTED

        // When: Check specific permission
        val result = permissionsManager.isPermissionGranted(Permission.READ_IMAGES)

        // Then: Should return true
        assertTrue("READ_IMAGES should be granted", result)
    }

    @Test
    fun `isPermissionGranted returns false when permission denied`() = runTest {
        // Given: Permission is denied
        every { 
            ContextCompat.checkSelfPermission(context, any()) 
        } returns PackageManager.PERMISSION_DENIED

        // When: Check specific permission
        val result = permissionsManager.isPermissionGranted(Permission.READ_IMAGES)

        // Then: Should return false
        assertFalse("READ_IMAGES should be denied", result)
    }

    @Test
    fun `hasMediaAccess returns true when all media permissions granted`() = runTest {
        // Given: All permissions are granted
        every { 
            ContextCompat.checkSelfPermission(context, any()) 
        } returns PackageManager.PERMISSION_GRANTED

        // When: Check media access
        val result = permissionsManager.hasMediaAccess()

        // Then: Should return true
        assertTrue("Media access should be granted", result)
    }

    @Test
    fun `hasMediaAccess returns false when any media permission denied`() = runTest {
        // Given: All permissions are denied
        every { 
            ContextCompat.checkSelfPermission(context, any()) 
        } returns PackageManager.PERMISSION_DENIED

        // When: Check media access
        val result = permissionsManager.hasMediaAccess()

        // Then: Should return false
        assertFalse("Media access should be denied", result)
    }

    @Test
    fun `getRequiredPermissions returns only applicable permissions`() {
        // When: Get required permissions
        val result = permissionsManager.getRequiredPermissions()

        // Then: All returned permissions should be applicable
        result.forEach { permission ->
            assertTrue(
                "Permission $permission should be applicable",
                permission.isApplicable()
            )
        }
    }

    @Test
    fun `observePermissions emits state changes`() = runTest {
        // Given: Initial state with denied permissions
        every { 
            ContextCompat.checkSelfPermission(context, any()) 
        } returns PackageManager.PERMISSION_DENIED

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            mockkStatic(android.os.Environment::class)
            every { android.os.Environment.isExternalStorageManager() } returns false
        }

        // When: Check permissions (updates the flow)
        permissionsManager.checkPermissions()

        // Then: Flow should emit the new state
        val flowValue = permissionsManager.observePermissions().first()
        assertFalse("Flow should emit state with denied permissions", flowValue.allGranted)
    }

    @Test
    fun `refreshPermissions updates permission state`() = runTest {
        // Given: Initial state with denied permissions
        every { 
            ContextCompat.checkSelfPermission(context, any()) 
        } returns PackageManager.PERMISSION_DENIED

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            mockkStatic(android.os.Environment::class)
            every { android.os.Environment.isExternalStorageManager() } returns false
        }

        permissionsManager.checkPermissions()
        
        // When: Permissions are granted and we refresh
        every { 
            ContextCompat.checkSelfPermission(context, any()) 
        } returns PackageManager.PERMISSION_GRANTED

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            every { android.os.Environment.isExternalStorageManager() } returns true
        }

        permissionsManager.refreshPermissions()

        // Then: Flow should emit updated state
        val flowValue = permissionsManager.observePermissions().first()
        assertTrue("Flow should emit state with granted permissions", flowValue.allGranted)
    }

    @Test
    fun `non-applicable permissions return NotApplicable status`() = runTest {
        // Given: Mock all permissions as granted first
        every { 
            ContextCompat.checkSelfPermission(context, any()) 
        } returns PackageManager.PERMISSION_GRANTED

        // Mock MANAGE_EXTERNAL_STORAGE for Android 11+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            mockkStatic(android.os.Environment::class)
            every { android.os.Environment.isExternalStorageManager() } returns true
        }
        
        // When: Check permissions
        val result = permissionsManager.checkPermissions()

        // Then: Non-applicable permissions should have NotApplicable status
        val nonApplicablePermissions = Permission.getAllRequiredPermissions()
            .filter { !it.isApplicable() }
        
        nonApplicablePermissions.forEach { permission ->
            assertEquals(
                "Permission $permission should be NotApplicable",
                PermissionStatus.NotApplicable,
                result.permissionStatuses[permission]
            )
        }
    }
}
