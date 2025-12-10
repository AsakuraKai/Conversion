package com.example.conversion.ui.performance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Examples of optimized Compose UI patterns for file selection and display.
 * Demonstrates best practices for performance optimization in Jetpack Compose.
 *
 * @author Sokchea (UI/Frontend Specialist)
 */

/**
 * EXAMPLE 1: Optimized File List with proper keys and derived state
 */
@Composable
fun OptimizedFileList(
    state: OptimizedFileListState,
    onFileClick: (FileItem) -> Unit,
    modifier: Modifier = Modifier
) {
    // Track recomposition for debugging (remove in production)
    CompositionTracker.trackRecomposition("OptimizedFileList")
    
    // Derived state - only recalculates when state changes
    val filteredFiles by remember(state.files) {
        derivedStateOf {
            state.files.filter { it.name.isNotEmpty() }
        }
    }
    
    // Expensive calculation cached with remember
    val totalSize = remember(filteredFiles) {
        filteredFiles.sumOf { it.size }
    }
    
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header with total count
        item(key = "header") {
            FileListHeader(
                fileCount = filteredFiles.size,
                totalSize = totalSize,
                selectedCount = state.selectedCount
            )
        }
        
        // File items with stable keys and content types
        items(
            items = filteredFiles,
            key = { LazyListOptimization.itemKey(it) },
            contentType = { LazyListOptimization.contentType(it) }
        ) { file ->
            OptimizedFileItem(
                file = file,
                isSelected = state.selectedFiles.contains(file.id),
                onClick = { onFileClick(file) }
            )
        }
        
        // Empty state
        if (filteredFiles.isEmpty() && !state.isLoading) {
            item(key = "empty") {
                EmptyStateItem()
            }
        }
    }
}

/**
 * EXAMPLE 2: Optimized File Item with minimal recompositions
 */
@Composable
private fun OptimizedFileItem(
    file: FileItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Stable modifier - only changes when isSelected changes
    val backgroundColor by remember(isSelected) {
        derivedStateOf {
            if (isSelected) Color(0xFF1976D2).copy(alpha = 0.1f)
            else Color.Transparent
        }
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .background(backgroundColor)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Selection indicator - only animates when isSelected changes
            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = formatFileSize(file.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * EXAMPLE 3: File List Header with derived state
 */
@Composable
private fun FileListHeader(
    fileCount: Int,
    totalSize: Long,
    selectedCount: Int,
    modifier: Modifier = Modifier
) {
    // Expensive formatting cached
    val formattedSize = remember(totalSize) {
        formatFileSize(totalSize)
    }
    
    val headerText = remember(fileCount, selectedCount) {
        derivedStateOf {
            when {
                selectedCount > 0 -> "$selectedCount of $fileCount files selected"
                else -> "$fileCount files • $formattedSize"
            }
        }
    }.value
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Text(
            text = headerText,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
    }
}

/**
 * EXAMPLE 4: Empty State Component
 */
@Composable
private fun EmptyStateItem(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "No files found",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Select a folder to view files",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * EXAMPLE 5: Optimized Search Bar with debounced input
 */
@Composable
fun OptimizedSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Remember expensive state derivations
    val hasQuery by remember(query) {
        derivedStateOf { query.isNotEmpty() }
    }
    
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        placeholder = { Text("Search files...") },
        singleLine = true,
        trailingIcon = {
            AnimatedVisibility(visible = hasQuery) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search"
                    )
                }
            }
        }
    )
}

/**
 * EXAMPLE 6: Loading Skeleton with optimized animations
 */
@Composable
fun LoadingSkeleton(
    itemCount: Int = 5,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(count = itemCount, key = { "skeleton_$it" }) {
            SkeletonItem()
        }
    }
}

@Composable
private fun SkeletonItem(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Simulated shimmer effect
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.small
                    )
            )
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                            shape = MaterialTheme.shapes.small
                        )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(12.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                            shape = MaterialTheme.shapes.small
                        )
                )
            }
        }
    }
}

/**
 * Helper function to format file sizes
 */
private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
        else -> "${bytes / (1024 * 1024 * 1024)} GB"
    }
}

/**
 * Preview examples
 */
@Preview(showBackground = true)
@Composable
private fun PreviewOptimizedFileList() {
    MaterialTheme {
        OptimizedFileList(
            state = MockPerformanceData.createMockState(fileCount = 10),
            onFileClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLoadingSkeleton() {
    MaterialTheme {
        LoadingSkeleton(itemCount = 5)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewOptimizedSearchBar() {
    MaterialTheme {
        OptimizedSearchBar(
            query = "",
            onQueryChange = {}
        )
    }
}
