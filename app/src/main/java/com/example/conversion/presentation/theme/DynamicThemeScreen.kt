package com.example.conversion.presentation.theme

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.conversion.R
import com.example.conversion.domain.model.ImagePalette
import com.example.conversion.presentation.theme.DynamicThemeContract.Action
import com.example.conversion.presentation.theme.DynamicThemeContract.Event
import com.example.conversion.ui.theme.ConversionTheme

/**
 * Screen for dynamic theming from images.
 * Allows users to select an image and extract colors for app theming.
 *
 * @param viewModel ViewModel managing the dynamic theme state
 * @param onNavigateBack Callback when user navigates back
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicThemeScreen(
    viewModel: DynamicThemeViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { viewModel.handleAction(Action.SelectImage(it)) }
    }
    
    // Collect events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is Event.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is Event.ThemeApplied -> {
                    // Theme applied successfully
                }
                is Event.ThemeReset -> {
                    // Theme reset successfully
                }
                is Event.NavigateBack -> {
                    onNavigateBack()
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dynamic Theme") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Text(
                text = "Create a custom theme from your favorite image",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Image selection card
            ImageSelectionCard(
                imageUri = state.selectedImageUri,
                isLoading = state.isLoading,
                onPickImage = {
                    viewModel.handleAction(Action.PickNewImage)
                    imagePickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )
            
            // Error message
            if (state.error != null) {
                ErrorCard(
                    message = state.error!!,
                    onDismiss = { viewModel.handleAction(Action.DismissError) }
                )
            }
            
            // Color palette preview
            if (state.hasPalette) {
                ColorPalettePreview(
                    palette = state.palette!!,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Action buttons
            if (state.hasPalette || state.isThemeApplied) {
                ActionButtons(
                    canApplyTheme = state.canApplyTheme,
                    canResetTheme = state.canResetTheme,
                    onApplyTheme = { viewModel.handleAction(Action.ApplyTheme) },
                    onResetTheme = { viewModel.handleAction(Action.ResetTheme) }
                )
            }
            
            // Instructions
            if (!state.hasPalette && !state.isLoading && state.selectedImageUri == null) {
                InstructionsCard()
            }
        }
    }
}

/**
 * Card for selecting an image.
 */
@Composable
private fun ImageSelectionCard(
    imageUri: Uri?,
    isLoading: Boolean,
    onPickImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Background Image",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (imageUri != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Selected image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                }
                
                OutlinedButton(
                    onClick = onPickImage,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Choose Different Image")
                }
            } else {
                Button(
                    onClick = onPickImage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Choose Image")
                }
            }
        }
    }
}

/**
 * Card displaying error messages.
 */
@Composable
private fun ErrorCard(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            
            TextButton(onClick = onDismiss) {
                Text("Dismiss")
            }
        }
    }
}

/**
 * Card displaying color palette preview.
 */
@Composable
fun ColorPalettePreview(
    palette: ImagePalette,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Extracted Colors",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Primary colors row
            palette.dominantColor?.let {
                ColorSwatch(
                    color = it,
                    label = "Dominant",
                    description = "Most common color in the image"
                )
            }
            
            palette.vibrantColor?.let {
                ColorSwatch(
                    color = it,
                    label = "Vibrant",
                    description = "Bright and saturated color"
                )
            }
            
            palette.mutedColor?.let {
                ColorSwatch(
                    color = it,
                    label = "Muted",
                    description = "Calm and desaturated color"
                )
            }
            
            // Additional colors if available
            if (palette.darkVibrantColor != null || 
                palette.lightVibrantColor != null || 
                palette.darkMutedColor != null || 
                palette.lightMutedColor != null) {
                
                Text(
                    text = "Variants",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                palette.darkVibrantColor?.let {
                    ColorSwatch(color = it, label = "Dark Vibrant")
                }
                
                palette.lightVibrantColor?.let {
                    ColorSwatch(color = it, label = "Light Vibrant")
                }
                
                palette.darkMutedColor?.let {
                    ColorSwatch(color = it, label = "Dark Muted")
                }
                
                palette.lightMutedColor?.let {
                    ColorSwatch(color = it, label = "Light Muted")
                }
            }
        }
    }
}

/**
 * Individual color swatch with label.
 */
@Composable
private fun ColorSwatch(
    color: Color,
    label: String,
    description: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                )
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Action buttons for applying or resetting theme.
 */
@Composable
private fun ActionButtons(
    canApplyTheme: Boolean,
    canResetTheme: Boolean,
    onApplyTheme: () -> Unit,
    onResetTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (canResetTheme) {
            OutlinedButton(
                onClick = onResetTheme,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Reset Theme")
            }
        }
        
        Button(
            onClick = onApplyTheme,
            modifier = Modifier.weight(1f),
            enabled = canApplyTheme
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(if (canResetTheme) "Update Theme" else "Apply Theme")
        }
    }
}

/**
 * Instructions card for first-time users.
 */
@Composable
private fun InstructionsCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "How it works",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Text(
                text = "1. Choose a background image\n" +
                      "2. We'll extract dominant colors\n" +
                      "3. Preview the color palette\n" +
                      "4. Apply to create your custom theme",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

// Previews

@Preview(showBackground = true)
@Composable
private fun DynamicThemeScreenPreview() {
    ConversionTheme {
        DynamicThemeScreen()
    }
}

@Preview(showBackground = true)
@Composable
private fun ColorPalettePreviewSample() {
    ConversionTheme {
        ColorPalettePreview(
            palette = ImagePalette(
                dominantColor = Color(0xFF6200EE),
                vibrantColor = Color(0xFF03DAC5),
                mutedColor = Color(0xFF757575),
                darkVibrantColor = Color(0xFF3700B3),
                lightVibrantColor = Color(0xFFBB86FC)
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
