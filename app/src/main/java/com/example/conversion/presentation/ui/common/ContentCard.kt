package com.example.conversion.presentation.ui.common

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.conversion.ui.theme.ConversionTheme

/**
 * Content Card Component
 * 
 * A reusable card component with Material 3 styling and border for displaying
 * content in a consistent, elevated container.
 * 
 * @param modifier Modifier for customization
 * @param title Optional title text for the card
 * @param contentDescription Optional content description for accessibility
 * @param content The content to display inside the card
 */
@Composable
fun ContentCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    contentDescription: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription?.let {
                    this.contentDescription = it
                }
            },
        shape = CARD_SHAPE,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = CARD_ELEVATION
        ),
        border = BorderStroke(
            width = CARD_BORDER_WIDTH,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CARD_PADDING),
            verticalArrangement = Arrangement.spacedBy(CARD_CONTENT_SPACING)
        ) {
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            content()
        }
    }
}

// Design tokens
private val CARD_SHAPE = RoundedCornerShape(16.dp)
private val CARD_ELEVATION = 2.dp
private val CARD_BORDER_WIDTH = 1.dp
private val CARD_PADDING = 16.dp
private val CARD_CONTENT_SPACING = 12.dp

// Preview compositions
@Preview(name = "Content Card - Light", showBackground = true)
@Composable
private fun ContentCardPreview() {
    ConversionTheme {
        ContentCard(
            title = "Sample Card",
            contentDescription = "Sample content card"
        ) {
            Text("This is sample content inside the card.")
            Text("More content can be added here.")
        }
    }
}

@Preview(
    name = "Content Card - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ContentCardDarkPreview() {
    ConversionTheme {
        ContentCard(
            title = "Sample Card",
            contentDescription = "Sample content card"
        ) {
            Text("This is sample content inside the card.")
            Text("More content can be added here.")
        }
    }
}

@Preview(name = "Content Card - No Title", showBackground = true)
@Composable
private fun ContentCardNoTitlePreview() {
    ConversionTheme {
        ContentCard {
            Text("Content without a title")
        }
    }
}
