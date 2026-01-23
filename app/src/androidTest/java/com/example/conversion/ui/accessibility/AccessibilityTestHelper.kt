package com.example.conversion.ui.accessibility

import androidx.compose.ui.test.*

/**
 * Accessibility testing utilities for Compose UI tests.
 *
 * Provides helper functions for testing accessibility features:
 * - Content descriptions
 * - Semantic properties
 * - Touch target sizes
 * - TalkBack compatibility
 *
 * Usage in tests:
 * ```
 * @Test
 * fun button_hasAccessibleContentDescription() {
 *     composeTestRule.setContent {
 *         MyButton()
 *     }
 *
 *     composeTestRule
 *         .onNodeWithContentDescription("Click me")
 *         .assertExists()
 *         .assertIsDisplayed()
 * }
 * ```
 */
object AccessibilityTestHelper {
    
    /**
     * Assert that a node has the minimum touch target size (48dp).
     *
     * @param testTag Tag of the composable to test
     */
    fun SemanticsNodeInteractionsProvider.assertMinimumTouchTarget(
        testTag: String
    ): SemanticsNodeInteraction {
        return onNodeWithTag(testTag)
            .assertWidthIsAtLeast(48.dp)
            .assertHeightIsAtLeast(48.dp)
    }
    
    /**
     * Assert that a node has accessible content description.
     *
     * @param description Expected content description
     */
    fun SemanticsNodeInteractionsProvider.assertHasContentDescription(
        description: String
    ): SemanticsNodeInteraction {
        return onNodeWithContentDescription(description)
            .assertExists()
    }
    
    /**
     * Assert that all interactive elements have content descriptions.
     * Excludes decorative elements.
     */
    fun SemanticsNodeInteractionsProvider.assertAllClickableHaveDescriptions() {
        onAllNodes(hasClickAction())
            .fetchSemanticsNodes()
            .forEach { node ->
                val hasDescription = node.config.getOrNull(
                    SemanticsProperties.ContentDescription
                ) != null
                
                assert(hasDescription) {
                    "Clickable element without content description found"
                }
            }
    }
    
    /**
     * Common accessibility test scenarios.
     */
    object TestScenarios {
        
        /**
         * Test that button has proper accessibility.
         */
        fun SemanticsNodeInteractionsProvider.assertAccessibleButton(
            contentDescription: String,
            testTag: String? = null
        ) {
            val node = if (testTag != null) {
                onNodeWithTag(testTag)
            } else {
                onNodeWithContentDescription(contentDescription)
            }
            
            node
                .assertExists()
                .assertIsDisplayed()
                .assertHasClickAction()
                .assertWidthIsAtLeast(48.dp)
                .assertHeightIsAtLeast(48.dp)
        }
        
        /**
         * Test that icon button has proper accessibility.
         */
        fun SemanticsNodeInteractionsProvider.assertAccessibleIconButton(
            contentDescription: String
        ) {
            onNodeWithContentDescription(contentDescription)
                .assertExists()
                .assertIsDisplayed()
                .assertHasClickAction()
                .assertWidthIsAtLeast(48.dp)
                .assertHeightIsAtLeast(48.dp)
        }
        
        /**
         * Test that decorative image has empty content description.
         */
        fun SemanticsNodeInteractionsProvider.assertDecorativeImage(
            testTag: String
        ) {
            onNodeWithTag(testTag)
                .assertExists()
                .assert(
                    SemanticsMatcher("Has no content description") { node ->
                        val desc = node.config.getOrNull(
                            SemanticsProperties.ContentDescription
                        )
                        desc == null || desc.isEmpty()
                    }
                )
        }
    }
}

/**
 * Semantic matchers for common accessibility patterns.
 */
object AccessibilityMatchers {
    
    /**
     * Match nodes with specific role.
     */
    fun hasRole(role: androidx.compose.ui.semantics.Role): SemanticsMatcher {
        return SemanticsMatcher("Has role $role") { node ->
            node.config.getOrNull(SemanticsProperties.Role) == role
        }
    }
    
    /**
     * Match nodes with state description.
     */
    fun hasStateDescription(description: String): SemanticsMatcher {
        return SemanticsMatcher("Has state description '$description'") { node ->
            node.config.getOrNull(SemanticsProperties.StateDescription) == description
        }
    }
    
    /**
     * Match nodes that are selectable.
     */
    fun isSelectable(): SemanticsMatcher {
        return SemanticsMatcher("Is selectable") { node ->
            node.config.contains(SemanticsActions.OnClick) ||
            node.config.contains(SemanticsProperties.Selected)
        }
    }
}

/**
 * Extension functions for accessibility testing.
 */

/**
 * Assert that width is at least the specified dp value.
 */
fun SemanticsNodeInteraction.assertWidthIsAtLeast(
    minWidth: androidx.compose.ui.unit.Dp
): SemanticsNodeInteraction {
    val dpInPx = minWidth.value * fetchSemanticsNode().layoutInfo.density
    return assert(
        SemanticsMatcher("Width is at least $minWidth") { node ->
            node.layoutInfo.width >= dpInPx
        }
    )
}

/**
 * Assert that height is at least the specified dp value.
 */
fun SemanticsNodeInteraction.assertHeightIsAtLeast(
    minHeight: androidx.compose.ui.unit.Dp
): SemanticsNodeInteraction {
    val dpInPx = minHeight.value * fetchSemanticsNode().layoutInfo.density
    return assert(
        SemanticsMatcher("Height is at least $minHeight") { node ->
            node.layoutInfo.height >= dpInPx
        }
    )
}

/**
 * Best practices for accessibility testing:
 *
 * 1. Content Descriptions:
 *    - All interactive elements must have meaningful descriptions
 *    - Decorative images should have null or empty string
 *    - Avoid redundant text (e.g., "button" suffix)
 *
 * 2. Touch Targets:
 *    - Minimum 48x48 dp for all clickable elements
 *    - Test with assertMinimumTouchTarget()
 *    - Add padding if element is smaller
 *
 * 3. State Descriptions:
 *    - Dynamic UI elements need state descriptions
 *    - Selected/not selected, expanded/collapsed, etc.
 *    - Test with hasStateDescription()
 *
 * 4. Semantic Roles:
 *    - Use Role.Button, Role.Checkbox, etc.
 *    - Test with hasRole()
 *    - Helps screen readers announce element type
 *
 * 5. TalkBack Testing:
 *    - Enable TalkBack in device settings
 *    - Navigate through all screens
 *    - Verify announcements are clear and helpful
 *    - Check reading order is logical
 *
 * Example comprehensive test:
 * ```
 * @Test
 * fun fileGridItem_accessibilityComplete() {
 *     composeTestRule.setContent {
 *         FileGridItem(
 *             file = sampleFile,
 *             isSelected = true,
 *             onClick = {}
 *         )
 *     }
 *
 *     with(AccessibilityTestHelper.TestScenarios) {
 *         composeTestRule.assertAccessibleButton(
 *             contentDescription = "Select file sample.jpg",
 *             testTag = "file_grid_item"
 *         )
 *     }
 *
 *     composeTestRule
 *         .onNodeWithTag("file_grid_item")
 *         .assert(hasStateDescription("Selected"))
 * }
 * ```
 */
