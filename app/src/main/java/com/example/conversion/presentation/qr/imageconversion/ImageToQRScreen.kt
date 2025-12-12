package com.example.conversion.presentation.qr.imageconversion

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Image to QR Conversion Screen.
 * Encodes images as multiple QR codes for later reconstruction.
 * Part of P3 - Future Enhancements (Advanced QR Features)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageToQRScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Image ⇄ QR Conversion") },
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
        ) {
            // Tab Row
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Image → QR") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("QR → Image") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = null
                        )
                    }
                )
            }
            
            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = if (selectedTab == 0) Icons.Default.Image else Icons.Default.QrCode,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = if (selectedTab == 0) "Image to QR Codes" else "QR Codes to Image",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = if (selectedTab == 0) {
                        "Encode images into multiple QR codes for storage"
                    } else {
                        "Scan multiple QR codes to reconstruct an image"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (selectedTab == 0) "Encoding Process:" else "Decoding Process:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        if (selectedTab == 0) {
                            FeatureItem("1. Select image to encode")
                            FeatureItem("2. Compress image (max 100KB)")
                            FeatureItem("3. Convert to Base64")
                            FeatureItem("4. Split into QR-sized chunks")
                            FeatureItem("5. Generate multiple QR codes")
                            FeatureItem("6. Save QR sequence (1/N format)")
                        } else {
                            FeatureItem("1. Scan first QR code")
                            FeatureItem("2. Detect sequence info (1/N)")
                            FeatureItem("3. Scan remaining codes")
                            FeatureItem("4. Reassemble chunks")
                            FeatureItem("5. Decode Base64")
                            FeatureItem("6. Decompress and restore image")
                        }
                    }
                }
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "⚠️ Technical Limitations",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = "• QR codes limited to ~2KB each\n• Large images need multiple codes\n• Requires multi-QR encoding library",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
                
                Text(
                    text = "Status: Phase 3 - Future Enhancement",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
private fun FeatureItem(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}
