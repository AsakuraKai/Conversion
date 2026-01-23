package com.example.conversion.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.conversion.ui.theme.ConversionTheme

/**
 * Shimmer effect modifier for creating skeleton loading animations.
 *
 * Applies an animated gradient that moves across the composable to indicate loading state.
 */
fun Modifier.shimmerEffect(): Modifier = composed {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
    )
    
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )
    
    background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnimation - 1000f, translateAnimation - 1000f),
            end = Offset(translateAnimation, translateAnimation)
        )
    )
}

/**
 * Loading skeleton placeholder component for a single line of text.
 */
@Composable
fun TextLoadingSkeleton(
    width: Dp = 120.dp,
    height: Dp = 16.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(4.dp))
            .shimmerEffect()
    )
}

/**
 * Loading skeleton for circular/square images.
 */
@Composable
fun ImageLoadingSkeleton(
    size: Dp = 64.dp,
    shape: androidx.compose.ui.graphics.Shape = CircleShape,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .shimmerEffect()
    )
}

/**
 * Loading skeleton for rectangular content blocks.
 */
@Composable
fun BlockLoadingSkeleton(
    height: Dp = 100.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(8.dp))
            .shimmerEffect()
    )
}

/**
 * Loading skeleton for file list items.
 */
@Composable
fun FileItemLoadingSkeleton(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail
        ImageLoadingSkeleton(
            size = 56.dp,
            shape = RoundedCornerShape(8.dp)
        )
        
        // File info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextLoadingSkeleton(width = 180.dp, height = 18.dp)
            TextLoadingSkeleton(width = 100.dp, height = 14.dp)
        }
        
        // Checkbox placeholder
        ImageLoadingSkeleton(size = 24.dp)
    }
}

/**
 * Loading skeleton for grid items (thumbnails).
 */
@Composable
fun GridItemLoadingSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(4.dp)
    ) {
        ImageLoadingSkeleton(
            size = 120.dp,
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TextLoadingSkeleton(width = 120.dp, height = 14.dp)
    }
}

/**
 * Loading skeleton for card-based content.
 */
@Composable
fun CardLoadingSkeleton(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextLoadingSkeleton(width = 200.dp, height = 20.dp)
            TextLoadingSkeleton(width = 160.dp, height = 16.dp)
            BlockLoadingSkeleton(height = 80.dp)
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextLoadingSkeleton(width = 80.dp, height = 36.dp)
                TextLoadingSkeleton(width = 80.dp, height = 36.dp)
            }
        }
    }
}

/**
 * Loading skeleton for template list items.
 */
@Composable
fun TemplateItemLoadingSkeleton(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextLoadingSkeleton(width = 150.dp, height = 18.dp)
                ImageLoadingSkeleton(size = 20.dp)
            }
            
            TextLoadingSkeleton(width = 200.dp, height = 14.dp)
            TextLoadingSkeleton(width = 120.dp, height = 14.dp)
        }
    }
}

/**
 * Full screen loading skeleton with multiple file items.
 */
@Composable
fun FileListLoadingSkeleton(
    itemCount: Int = 5,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(itemCount) {
            FileItemLoadingSkeleton()
        }
    }
}

/**
 * Settings screen loading skeleton.
 */
@Composable
fun SettingsLoadingSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Section header
        TextLoadingSkeleton(width = 100.dp, height = 16.dp)
        
        // Settings items
        repeat(3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextLoadingSkeleton(width = 120.dp, height = 16.dp)
                    TextLoadingSkeleton(width = 180.dp, height = 14.dp)
                }
                ImageLoadingSkeleton(size = 40.dp)
            }
            
            if (it < 2) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingSkeletonPreviews() {
    ConversionTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextLoadingSkeleton()
            ImageLoadingSkeleton()
            BlockLoadingSkeleton()
            FileItemLoadingSkeleton()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FileListLoadingSkeletonPreview() {
    ConversionTheme {
        FileListLoadingSkeleton()
    }
}

@Preview(showBackground = true)
@Composable
private fun CardLoadingSkeletonPreview() {
    ConversionTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            CardLoadingSkeleton()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TemplateItemLoadingSkeletonPreview() {
    ConversionTheme {
        TemplateItemLoadingSkeleton()
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsLoadingSkeletonPreview() {
    ConversionTheme {
        SettingsLoadingSkeleton()
    }
}
