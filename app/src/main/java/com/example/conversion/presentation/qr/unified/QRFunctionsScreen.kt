package com.example.conversion.presentation.qr.unified

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Unified QR Functions Screen.
 * Consolidates all QR-related features into a single dedicated screen:
 * - Generate QR codes (from preset, text, file link)
 * - Scan QR codes (import preset, generic scan, comparison)
 * - Image ⇄ QR conversion
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRFunctionsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToQRGenerate: () -> Unit,
    onNavigateToQRScan: () -> Unit,
    onNavigateToQRComparison: () -> Unit,
    onNavigateToImageToQR: () -> Unit,
    onNavigateToQRToImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("QR Functions") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "QR Code Tools",
                style = MaterialTheme.typography.headlineSmall
            )
            
            Text(
                text = "Generate, scan, and manage QR codes",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Generate QR Code Section
            SectionHeader(text = "Generate QR Codes")
            
            QRFunctionCard(
                title = "Generate from Preset",
                description = "Create QR codes from saved templates",
                icon = Icons.Default.QrCode,
                onClick = onNavigateToQRGenerate
            )
            
            QRFunctionCard(
                title = "Generate from Text",
                description = "Create QR code from custom text or URL",
                icon = Icons.Default.TextFields,
                onClick = onNavigateToQRGenerate
            )
            
            QRFunctionCard(
                title = "Generate from File Link",
                description = "Create QR code linking to a file",
                icon = Icons.Default.InsertDriveFile,
                onClick = onNavigateToQRGenerate
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Scan QR Code Section
            SectionHeader(text = "Scan QR Codes")
            
            QRFunctionCard(
                title = "Scan QR Code",
                description = "Scan and decode any QR code",
                icon = Icons.Default.CameraAlt,
                onClick = onNavigateToQRScan
            )
            
            QRFunctionCard(
                title = "Import Preset from QR",
                description = "Scan QR code to import rename preset",
                icon = Icons.Default.Download,
                onClick = onNavigateToQRScan
            )
            
            QRFunctionCard(
                title = "QR Comparison",
                description = "Compare and identify QR codes",
                icon = Icons.Default.Compare,
                onClick = onNavigateToQRComparison,
                badge = "New"
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Advanced Features Section
            SectionHeader(text = "Advanced Features")
            
            QRFunctionCard(
                title = "Image to QR",
                description = "Convert images into multiple QR codes",
                icon = Icons.Default.Image,
                onClick = onNavigateToImageToQR,
                badge = "Beta"
            )
            
            QRFunctionCard(
                title = "QR to Image",
                description = "Reconstruct image from QR codes",
                icon = Icons.Default.PhotoLibrary,
                onClick = onNavigateToQRToImage,
                badge = "Beta"
            )
        }
    }
}

@Composable
private fun SectionHeader(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun QRFunctionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    badge: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(56.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    if (badge != null) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Text(
                                text = badge,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
