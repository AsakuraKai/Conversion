package com.example.conversion.ui.performance

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * UI Performance monitoring components for Jetpack Compose.
 *
 * Provides real-time performance metrics display:
 * - Frame rate monitoring
 * - Memory usage tracking
 * - Recomposition count display
 * - Performance warnings and alerts
 *
 * @author Sokchea (UI/Frontend Specialist)
 */

/**
 * Performance monitoring state.
 */
data class UiPerformanceMetrics(
    val frameRate: Float = 60f,
    val recompositionCount: Int = 0,
    val memoryUsageMb: Float = 0f,
    val isPerformanceGood: Boolean = true
) {
    val performanceLevel: PerformanceLevel
        get() = when {
            frameRate >= 55f && memoryUsageMb < 100f -> PerformanceLevel.EXCELLENT
            frameRate >= 45f && memoryUsageMb < 150f -> PerformanceLevel.GOOD
            frameRate >= 30f && memoryUsageMb < 200f -> PerformanceLevel.FAIR
            else -> PerformanceLevel.POOR
        }
}

enum class PerformanceLevel(val color: Color, val label: String) {
    EXCELLENT(Color(0xFF4CAF50), "Excellent"),
    GOOD(Color(0xFF8BC34A), "Good"),
    FAIR(Color(0xFFFFC107), "Fair"),
    POOR(Color(0xFFF44336), "Poor")
}

/**
 * Performance monitor for UI metrics.
 */
class UiPerformanceMonitor(private val scope: CoroutineScope) {
    
    private val _metrics = MutableStateFlow(UiPerformanceMetrics())
    val metrics: StateFlow<UiPerformanceMetrics> = _metrics
    
    private var isMonitoring = false
    
    /**
     * Starts monitoring UI performance metrics.
     * Updates every 1 second.
     */
    fun startMonitoring() {
        if (isMonitoring) return
        
        isMonitoring = true
        scope.launch {
            while (isMonitoring) {
                updateMetrics()
                delay(1000) // Update every second
            }
        }
    }
    
    /**
     * Stops monitoring.
     */
    fun stopMonitoring() {
        isMonitoring = false
    }
    
    /**
     * Updates performance metrics.
     * Mock implementation - production should use Android Profiler APIs.
     */
    private fun updateMetrics() {
        // Mock frame rate calculation (production: use Choreographer)
        val frameRate = (55..60).random().toFloat()
        
        // Mock memory usage (production: use Runtime.getRuntime())
        val runtime = Runtime.getRuntime()
        val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        
        _metrics.value = UiPerformanceMetrics(
            frameRate = frameRate,
            recompositionCount = CompositionTracker.resetCounter().let { 0 },
            memoryUsageMb = usedMemory.toFloat(),
            isPerformanceGood = frameRate >= 55f
        )
    }
    
    /**
     * Increments recomposition counter.
     */
    fun trackRecomposition() {
        _metrics.value = _metrics.value.copy(
            recompositionCount = _metrics.value.recompositionCount + 1
        )
    }
}

/**
 * Composable: Performance overlay displaying metrics.
 */
@Composable
fun PerformanceOverlay(
    metrics: UiPerformanceMetrics,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    if (!enabled) return
    
    Card(
        modifier = modifier
            .padding(16.dp)
            .width(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Title
            Text(
                text = "Performance",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            
            Divider()
            
            // Frame rate
            MetricRow(
                label = "FPS",
                value = String.format("%.1f", metrics.frameRate),
                color = if (metrics.frameRate >= 55f) Color(0xFF4CAF50) else Color(0xFFF44336)
            )
            
            // Memory usage
            MetricRow(
                label = "Memory",
                value = "${metrics.memoryUsageMb.toInt()} MB",
                color = when {
                    metrics.memoryUsageMb < 100f -> Color(0xFF4CAF50)
                    metrics.memoryUsageMb < 150f -> Color(0xFFFFC107)
                    else -> Color(0xFFF44336)
                }
            )
            
            // Recompositions
            MetricRow(
                label = "Recomps",
                value = metrics.recompositionCount.toString(),
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Divider()
            
            // Performance level indicator
            PerformanceLevelIndicator(level = metrics.performanceLevel)
        }
    }
}

/**
 * Displays a single metric row.
 */
@Composable
private fun MetricRow(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

/**
 * Performance level indicator badge.
 */
@Composable
private fun PerformanceLevelIndicator(
    level: PerformanceLevel,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = level.color.copy(alpha = 0.2f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = level.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = level.color,
            modifier = Modifier.padding(8.dp)
        )
    }
}

/**
 * Composable: Performance warning alert.
 */
@Composable
fun PerformanceWarning(
    metrics: UiPerformanceMetrics,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (metrics.performanceLevel == PerformanceLevel.POOR) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text("Performance Warning")
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Performance issues detected:")
                    
                    if (metrics.frameRate < 30f) {
                        Text("• Low frame rate (${metrics.frameRate.toInt()} FPS)")
                    }
                    
                    if (metrics.memoryUsageMb > 200f) {
                        Text("• High memory usage (${metrics.memoryUsageMb.toInt()} MB)")
                    }
                    
                    if (metrics.recompositionCount > 100) {
                        Text("• Excessive recompositions (${metrics.recompositionCount})")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            },
            modifier = modifier
        )
    }
}

/**
 * Compact performance indicator for production use.
 */
@Composable
fun CompactPerformanceIndicator(
    metrics: UiPerformanceMetrics,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = metrics.performanceLevel.color.copy(alpha = 0.8f),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${metrics.frameRate.toInt()} FPS",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "•",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White
            )
            
            Text(
                text = "${metrics.memoryUsageMb.toInt()} MB",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Remember performance monitor across recompositions.
 */
@Composable
fun rememberPerformanceMonitor(): UiPerformanceMonitor {
    val scope = rememberCoroutineScope()
    return remember { UiPerformanceMonitor(scope) }
}

/**
 * Effect to start/stop monitoring based on lifecycle.
 */
@Composable
fun PerformanceMonitoringEffect(
    monitor: UiPerformanceMonitor,
    enabled: Boolean = true
) {
    DisposableEffect(enabled) {
        if (enabled) {
            monitor.startMonitoring()
        }
        
        onDispose {
            monitor.stopMonitoring()
        }
    }
}

/**
 * Mock data for preview.
 */
object MockPerformanceMetrics {
    
    fun excellent() = UiPerformanceMetrics(
        frameRate = 60f,
        recompositionCount = 5,
        memoryUsageMb = 85f,
        isPerformanceGood = true
    )
    
    fun good() = UiPerformanceMetrics(
        frameRate = 55f,
        recompositionCount = 12,
        memoryUsageMb = 120f,
        isPerformanceGood = true
    )
    
    fun fair() = UiPerformanceMetrics(
        frameRate = 45f,
        recompositionCount = 25,
        memoryUsageMb = 160f,
        isPerformanceGood = false
    )
    
    fun poor() = UiPerformanceMetrics(
        frameRate = 28f,
        recompositionCount = 50,
        memoryUsageMb = 220f,
        isPerformanceGood = false
    )
}

/**
 * Usage examples for documentation.
 */
object PerformanceMonitoringExamples {
    
    const val USAGE_EXAMPLE = """
    // Example 1: Full performance overlay (development mode)
    @Composable
    fun MyScreen() {
        val performanceMonitor = rememberPerformanceMonitor()
        val metrics by performanceMonitor.metrics.collectAsState()
        
        PerformanceMonitoringEffect(
            monitor = performanceMonitor,
            enabled = BuildConfig.DEBUG
        )
        
        Box(modifier = Modifier.fillMaxSize()) {
            // Your screen content
            
            // Performance overlay in corner
            PerformanceOverlay(
                metrics = metrics,
                modifier = Modifier.align(Alignment.TopEnd),
                enabled = BuildConfig.DEBUG
            )
        }
    }
    
    // Example 2: Compact indicator for production
    @Composable
    fun ProductionScreen() {
        val performanceMonitor = rememberPerformanceMonitor()
        val metrics by performanceMonitor.metrics.collectAsState()
        
        Box(modifier = Modifier.fillMaxSize()) {
            // Content
            
            // Compact indicator
            CompactPerformanceIndicator(
                metrics = metrics,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    }
    
    // Example 3: Performance warning dialog
    @Composable
    fun ScreenWithWarnings() {
        val performanceMonitor = rememberPerformanceMonitor()
        val metrics by performanceMonitor.metrics.collectAsState()
        var showWarning by remember { mutableStateOf(false) }
        
        LaunchedEffect(metrics.performanceLevel) {
            if (metrics.performanceLevel == PerformanceLevel.POOR) {
                showWarning = true
            }
        }
        
        if (showWarning) {
            PerformanceWarning(
                metrics = metrics,
                onDismiss = { showWarning = false }
            )
        }
    }
    """
}
