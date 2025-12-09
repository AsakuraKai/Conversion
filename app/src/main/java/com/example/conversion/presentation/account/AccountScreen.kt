package com.example.conversion.presentation.account

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.conversion.domain.model.SyncStatus
import com.example.conversion.ui.theme.ConversionTheme

/**
 * Account screen for managing user authentication and multi-device sync
 * 
 * Features:
 * - Account information display
 * - Manual sync trigger
 * - Sync status monitoring
 * - Synced data summary
 * - Sign in/out actions (mock)
 * 
 * @param viewModel ViewModel for account management
 * @param onNavigateBack Callback when user navigates back
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    viewModel: AccountViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Handle one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AccountEvent.ShowToast -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
                is AccountEvent.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = event.error,
                        duration = SnackbarDuration.Long
                    )
                }
                AccountEvent.NavigateToSignIn -> {
                    // Mock: Do nothing, user is always signed in
                }
                AccountEvent.SignOutCompleted -> {
                    snackbarHostState.showSnackbar("Signed out successfully")
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account & Sync") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.handleAction(AccountAction.RefreshData) },
                        enabled = !state.isLoading
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        AccountContent(
            state = state,
            onAction = viewModel::handleAction,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

/**
 * Main content for the Account screen
 */
@Composable
private fun AccountContent(
    state: AccountUiState,
    onAction: (AccountAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            if (state.isSignedIn) {
                // Account info card
                AccountInfoCard(
                    user = state.currentUser,
                    onSignOut = { onAction(AccountAction.SignOut) }
                )
                
                // Sync status card
                SyncStatusCard(
                    syncStatus = state.syncStatus,
                    lastSyncMessage = state.lastSyncMessage,
                    onManualSync = { onAction(AccountAction.SyncNow) }
                )
                
                // Synced data summary card
                SyncedDataCard(
                    templatesCount = state.syncedTemplatesCount,
                    tagsCount = state.syncedTagsCount,
                    settingsSynced = state.settingsSynced,
                    totalItems = state.totalSyncedItems
                )
                
                // Error display
                if (state.errorMessage != null) {
                    ErrorCard(
                        errorMessage = state.errorMessage,
                        onDismiss = { onAction(AccountAction.ClearError) }
                    )
                }
            } else {
                // Sign in prompt
                SignInPromptCard(
                    onSignIn = { onAction(AccountAction.SignIn) }
                )
            }
        }
    }
}

/**
 * Preview for Account screen in signed-in state
 */
@Preview(showBackground = true)
@Composable
private fun AccountScreenPreview() {
    ConversionTheme {
        AccountContent(
            state = AccountUiState(
                isSignedIn = true,
                currentUser = MockUser.DEFAULT,
                syncStatus = SyncStatus(
                    isSyncing = false,
                    lastSyncTime = System.currentTimeMillis() - 5 * 60 * 1000, // 5 minutes ago
                    error = null
                ),
                syncedTemplatesCount = 5,
                syncedTagsCount = 12,
                settingsSynced = true
            ),
            onAction = {}
        )
    }
}

/**
 * Preview for Account screen in syncing state
 */
@Preview(showBackground = true)
@Composable
private fun AccountScreenSyncingPreview() {
    ConversionTheme {
        AccountContent(
            state = AccountUiState(
                isSignedIn = true,
                syncStatus = SyncStatus(
                    isSyncing = true,
                    lastSyncTime = null,
                    error = null
                ),
                syncedTemplatesCount = 3,
                syncedTagsCount = 8
            ),
            onAction = {}
        )
    }
}

/**
 * Preview for Account screen with error
 */
@Preview(showBackground = true)
@Composable
private fun AccountScreenErrorPreview() {
    ConversionTheme {
        AccountContent(
            state = AccountUiState(
                isSignedIn = true,
                syncStatus = SyncStatus(
                    isSyncing = false,
                    lastSyncTime = System.currentTimeMillis() - 3600000,
                    error = "Network connection failed"
                ),
                errorMessage = "Sync failed: Network connection failed",
                syncedTemplatesCount = 2,
                syncedTagsCount = 5
            ),
            onAction = {}
        )
    }
}
