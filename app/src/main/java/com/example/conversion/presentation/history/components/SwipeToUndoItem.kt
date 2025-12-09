package com.example.conversion.presentation.history.components

import android.content.res.Configuration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.conversion.domain.model.RenameOperation
import com.example.conversion.ui.theme.ConversionTheme
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * Swipeable history item that reveals undo action.
 * Swipe left to quickly undo an operation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToUndoItem(
    operation: RenameOperation,
    isCurrentOperation: Boolean,
    onUndo: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val swipeThreshold = with(density) { 80.dp.toPx() }
    val offsetX = remember { Animatable(0f) }
    
    var isRevealed by remember { mutableStateOf(false) }

    LaunchedEffect(isRevealed) {
        if (isRevealed) {
            offsetX.animateTo(
                targetValue = -swipeThreshold,
                animationSpec = tween(durationMillis = 200)
            )
        } else {
            offsetX.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 200)
            )
        }
    }

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Background with undo action
        UndoBackground(
            modifier = Modifier
                .matchParentSize()
                .align(Alignment.CenterEnd)
        )

        // Main content card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .clickable { 
                    if (isRevealed) {
                        isRevealed = false
                    } else {
                        onClick()
                    }
                },
            colors = CardDefaults.cardColors(
                containerColor = if (isCurrentOperation)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isCurrentOperation) 4.dp else 1.dp
            )
        ) {
            OperationContent(
                operation = operation,
                isCurrentOperation = isCurrentOperation,
                onSwipe = {
                    isRevealed = !isRevealed
                }
            )
        }
    }
}

@Composable
private fun UndoBackground(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            imageVector = Icons.Default.Undo,
            contentDescription = "Undo",
            tint = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun OperationContent(
    operation: RenameOperation,
    isCurrentOperation: Boolean,
    onSwipe: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }
    val formattedDate = remember(operation.timestamp) {
        dateFormat.format(Date(operation.timestamp))
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Operation indicator
            if (isCurrentOperation) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "CURRENT",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Original name
            Text(
                text = operation.originalName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (isCurrentOperation)
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            // Arrow
            Text(
                text = "↓",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // New name
            Text(
                text = operation.newName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (isCurrentOperation)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Timestamp
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodySmall,
                color = if (isCurrentOperation)
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }

        // Swipe hint
        IconButton(
            onClick = onSwipe
        ) {
            Icon(
                imageVector = Icons.Default.Undo,
                contentDescription = "Undo this operation",
                tint = if (isCurrentOperation)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// Simplified non-swipe version for easier implementation
@Composable
fun HistoryOperationItem(
    operation: RenameOperation,
    isCurrentOperation: Boolean,
    onUndo: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentOperation)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCurrentOperation) 4.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OperationInfo(
                operation = operation,
                isCurrentOperation = isCurrentOperation,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onUndo) {
                Icon(
                    imageVector = Icons.Default.Undo,
                    contentDescription = "Undo this operation",
                    tint = if (isCurrentOperation)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun OperationInfo(
    operation: RenameOperation,
    isCurrentOperation: Boolean,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }
    val formattedDate = remember(operation.timestamp) {
        dateFormat.format(Date(operation.timestamp))
    }

    Column(modifier = modifier) {
        // Current operation indicator
        if (isCurrentOperation) {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "CURRENT",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Original name
        Text(
            text = operation.originalName,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = if (isCurrentOperation)
                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            else
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        // Arrow
        Text(
            text = "↓",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )

        // New name
        Text(
            text = operation.newName,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = if (isCurrentOperation)
                MaterialTheme.colorScheme.onPrimaryContainer
            else
                MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Timestamp
        Text(
            text = formattedDate,
            style = MaterialTheme.typography.bodySmall,
            color = if (isCurrentOperation)
                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

// Preview
@Preview(name = "History Item - Light")
@Preview(name = "History Item - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HistoryOperationItemPreview() {
    ConversionTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HistoryOperationItem(
                    operation = RenameOperation.create(
                        id = "1",
                        originalUri = android.net.Uri.parse("file:///test_image.jpg"),
                        newUri = android.net.Uri.parse("file:///IMG_001.jpg"),
                        originalName = "test_image.jpg",
                        newName = "IMG_001.jpg"
                    ),
                    isCurrentOperation = true,
                    onUndo = {},
                    onClick = {}
                )

                HistoryOperationItem(
                    operation = RenameOperation.create(
                        id = "2",
                        originalUri = android.net.Uri.parse("file:///photo.jpg"),
                        newUri = android.net.Uri.parse("file:///IMG_002.jpg"),
                        originalName = "photo.jpg",
                        newName = "IMG_002.jpg"
                    ),
                    isCurrentOperation = false,
                    onUndo = {},
                    onClick = {}
                )
            }
        }
    }
}
