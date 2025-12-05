package com.example.conversion.presentation.renameprogress

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.conversion.domain.model.FileItem
import com.example.conversion.domain.model.RenameConfig
import com.example.conversion.ui.theme.ConversionTheme

/**
 * Screen displaying rename progress with real-time updates.
 * 
 * Features:
 * - Circular and linear progress indicators
 * - Current file being processed
 * - Success/failure/skipped counts
 * - Cancellation support
 * - Completion summary
 * - Animated transitions
 */
@Composable
fun RenameProgressScreen(
    viewModel: RenameProgressViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is RenameProgressContract.Event.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is RenameProgressContract.Event.ShowCompletion -> {
                    // Completion is shown in the UI, no action needed
                }
                is RenameProgressContract.Event.NavigateBack -> {
                    onNavigateBack()
                }
                is RenameProgressContract.Event.OperationCancelled -> {
                    snackbarHostState.showSnackbar("Operation cancelled")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isComplete) {
                CompletionView(
                    state = state,
                    onDone = { viewModel.handleAction(RenameProgressContract.Action.AcknowledgeCompletion) },
                    onRetry = { viewModel.handleAction(RenameProgressContract.Action.RetryFailed) }
                )
            } else {
                ProgressView(
                    state = state,
                    onCancel = { viewModel.handleAction(RenameProgressContract.Action.CancelRename) }
                )
            }
        }
    }
}

/**
 * View showing progress during rename operation.
 */
@Composable
private fun ProgressView(
    state: RenameProgressContract.State,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Circular progress indicator
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(160.dp)
        ) {
            val animatedProgress by animateFloatAsState(
                targetValue = state.progressPercentage,
                animationSpec = tween(durationMillis = 300),
                label = "progress_animation"
            )
            
            CircularProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.size(160.dp),
                strokeWidth = 8.dp,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "${(state.progressPercentage * 100).toInt()}%",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Progress text
        Text(
            text = "Renaming files...",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = state.progressString,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Current file card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Current file:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = state.currentFileName.ifEmpty { "Processing..." },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = Icons.Default.CheckCircle,
                label = "Success",
                count = state.successCount,
                color = MaterialTheme.colorScheme.primary
            )
            StatItem(
                icon = Icons.Default.Error,
                label = "Failed",
                count = state.failedCount,
                color = MaterialTheme.colorScheme.error
            )
            StatItem(
                icon = Icons.Default.Warning,
                label = "Skipped",
                count = state.skippedCount,
                color = MaterialTheme.colorScheme.tertiary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Cancel button
        if (state.canCancel) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cancel")
            }
        }
    }
}

/**
 * View showing completion summary.
 */
@Composable
private fun CompletionView(
    state: RenameProgressContract.State,
    onDone: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Success icon with animation
        AnimatedVisibility(
            visible = !state.hasErrors,
            enter = scaleIn(animationSpec = tween(500)) + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(120.dp)
            )
        }

        AnimatedVisibility(
            visible = state.hasErrors,
            enter = scaleIn(animationSpec = tween(500)) + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Completed with errors",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(120.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (state.hasErrors) "Completed with issues" else "Rename complete!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = state.completionMessage,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Summary card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                SummaryRow("Total files", state.totalFiles.toString())
                SummaryRow("Successful", state.successCount.toString(), MaterialTheme.colorScheme.primary)
                if (state.failedCount > 0) {
                    SummaryRow("Failed", state.failedCount.toString(), MaterialTheme.colorScheme.error)
                }
                if (state.skippedCount > 0) {
                    SummaryRow("Skipped", state.skippedCount.toString(), MaterialTheme.colorScheme.tertiary)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action buttons
        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Done")
        }

        if (state.failedCount > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Retry Failed")
            }
        }
    }
}

/**
 * Stat item component showing icon, label, and count.
 */
@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    count: Int,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Summary row showing label and value.
 */
@Composable
private fun SummaryRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}

// ==================== Previews ====================

@Preview(name = "Progress - Light")
@Preview(name = "Progress - Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RenameProgressScreenPreview() {
    ConversionTheme {
        ProgressView(
            state = RenameProgressContract.State(
                successCount = 5,
                failedCount = 1,
                skippedCount = 2,
                isProcessing = true
            ),
            onCancel = {}
        )
    }
}

@Preview(name = "Completion - Success")
@Composable
private fun CompletionSuccessPreview() {
    ConversionTheme {
        CompletionView(
            state = RenameProgressContract.State(
                successCount = 10,
                failedCount = 0,
                skippedCount = 0,
                isComplete = true
            ),
            onDone = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Completion - With Errors")
@Composable
private fun CompletionWithErrorsPreview() {
    ConversionTheme {
        CompletionView(
            state = RenameProgressContract.State(
                successCount = 7,
                failedCount = 2,
                skippedCount = 1,
                isComplete = true
            ),
            onDone = {},
            onRetry = {}
        )
    }
}
