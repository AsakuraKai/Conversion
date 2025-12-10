package com.example.conversion.ui.accessibility

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * Accessible component extensions for Jetpack Compose.
 *
 * Pre-built accessible versions of common UI components that follow
 * accessibility best practices out of the box.
 *
 * Features:
 * - Proper content descriptions
 * - Minimum touch target sizes
 * - Semantic roles
 * - State descriptions
 */

/**
 * Accessible IconButton with proper content description and touch target.
 *
 * @param onClick Click handler
 * @param contentDescription Description for screen readers
 * @param icon Icon to display
 * @param modifier Optional modifier
 * @param enabled Whether button is enabled
 * @param tint Icon tint color
 */
@Composable
fun AccessibleIconButton(
    onClick: () -> Unit,
    contentDescription: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = Color.Unspecified
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(48.dp)
            .semantics {
                this.contentDescription = contentDescription
                role = Role.Button
            },
        enabled = enabled
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null, // Description on parent
            tint = tint
        )
    }
}

/**
 * Accessible Icon with content description.
 *
 * @param imageVector Icon vector
 * @param contentDescription Description for screen readers (null for decorative)
 * @param modifier Optional modifier
 * @param tint Icon tint color
 */
@Composable
fun AccessibleIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier.semantics {
            contentDescription?.let {
                this.contentDescription = it
            }
        },
        tint = tint
    )
}

/**
 * Clickable modifier with accessibility support.
 *
 * @param contentDescription Description for screen readers
 * @param role Semantic role (Button, Checkbox, etc.)
 * @param enabled Whether clickable is enabled
 * @param onClick Click handler
 */
fun Modifier.accessibleClickable(
    contentDescription: String,
    role: Role = Role.Button,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = this
    .semantics {
        this.contentDescription = contentDescription
        this.role = role
    }
    .clickable(
        enabled = enabled,
        role = role,
        onClick = onClick
    )

/**
 * Add state description to selectable items.
 *
 * @param isSelected Whether item is selected
 * @param selectedText Text to announce when selected
 * @param notSelectedText Text to announce when not selected
 */
fun Modifier.selectableState(
    isSelected: Boolean,
    selectedText: String = "Selected",
    notSelectedText: String = "Not selected"
): Modifier = this.semantics {
    this.contentDescription = if (isSelected) selectedText else notSelectedText
}

/**
 * Add loading state description.
 *
 * @param isLoading Whether currently loading
 * @param loadingText Text to announce when loading
 * @param loadedText Text to announce when loaded
 */
fun Modifier.loadingState(
    isLoading: Boolean,
    loadingText: String = "Loading",
    loadedText: String = "Loaded"
): Modifier = this.semantics {
    this.contentDescription = if (isLoading) loadingText else loadedText
}

/**
 * Add expanded/collapsed state description.
 *
 * @param isExpanded Whether currently expanded
 * @param expandedText Text to announce when expanded
 * @param collapsedText Text to announce when collapsed
 */
fun Modifier.expandableState(
    isExpanded: Boolean,
    expandedText: String = "Expanded",
    collapsedText: String = "Collapsed"
): Modifier = this.semantics {
    this.contentDescription = if (isExpanded) expandedText else collapsedText
}

/**
 * Ensure minimum interactive component size (48x48 dp).
 */
fun Modifier.minimumInteractiveSize(): Modifier = this.size(48.dp)

/**
 * Best practices for using accessible components:
 *
 * 1. Always provide meaningful content descriptions:
 *    ```
 *    AccessibleIconButton(
 *        onClick = { },
 *        contentDescription = "Settings",
 *        icon = Icons.Default.Settings
 *    )
 *    ```
 *
 * 2. Use semantic roles appropriately:
 *    ```
 *    Modifier.accessibleClickable(
 *        contentDescription = "Select file",
 *        role = Role.Checkbox,
 *        onClick = { }
 *    )
 *    ```
 *
 * 3. Add state descriptions for dynamic UI:
 *    ```
 *    Card(
 *        modifier = Modifier.selectableState(
 *            isSelected = true,
 *            selectedText = "File selected",
 *            notSelectedText = "File not selected"
 *        )
 *    )
 *    ```
 *
 * 4. Mark decorative images:
 *    ```
 *    AccessibleIcon(
 *        imageVector = Icons.Default.Star,
 *        contentDescription = null // Decorative
 *    )
 *    ```
 *
 * 5. Ensure minimum touch targets:
 *    ```
 *    Icon(
 *        ...,
 *        modifier = Modifier.minimumInteractiveSize()
 *    )
 *    ```
 */
