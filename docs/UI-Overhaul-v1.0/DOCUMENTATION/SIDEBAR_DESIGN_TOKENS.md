# Sidebar Design Tokens Specification

**Version:** 1.0  
**Last Updated:** January 22, 2026  
**Platform:** Android (Jetpack Compose)  
**Design System:** Material 3

---

## Table of Contents

1. [Overview](#overview)
2. [Color Tokens](#color-tokens)
3. [Spacing Tokens](#spacing-tokens)
4. [Typography Tokens](#typography-tokens)
5. [Animation Tokens](#animation-tokens)
6. [Dimension Tokens](#dimension-tokens)
7. [Elevation Tokens](#elevation-tokens)
8. [Border Tokens](#border-tokens)
9. [Responsive Breakpoints](#responsive-breakpoints)
10. [Component-Specific Tokens](#component-specific-tokens)
11. [Usage Examples](#usage-examples)

---

## Overview

This document defines all design tokens used in the Collapsible Sidebar Navigation component. Design tokens ensure consistency across the application and enable theme switching (light/dark/dynamic).

### Token Naming Convention

```
{Component}_{Property}_{Variant}_{State}
```

**Examples:**
- `Sidebar_Background_Default`
- `NavigationItem_Background_Selected_Hover`
- `SidebarHeader_Text_Collapsed`

### Material 3 Integration

All tokens map to Material 3 color scheme roles for seamless theming:

```kotlin
MaterialTheme.colorScheme.{role}
```

---

## Color Tokens

### Background Colors

| Token Name | Material 3 Role | Light Theme | Dark Theme | Usage |
|------------|-----------------|-------------|------------|-------|
| `Sidebar_Background` | `surface` | `#FFFBFE` | `#1C1B1F` | Sidebar container background |
| `Sidebar_Surface` | `surfaceVariant` | `#E7E0EC` | `#49454F` | Elevated surface within sidebar |
| `Content_Background` | `background` | `#FFFBFE` | `#1C1B1F` | Main content area background |
| `Card_Background` | `surfaceContainer` | `#F3EDF7` | `#211F26` | Content card background |
| `Scrim_Overlay` | `scrim` (50% opacity) | `#000000` @ 0.5 | `#000000` @ 0.5 | Overlay when sidebar expanded |

**Code Implementation:**

```kotlin
object SidebarColors {
    val background @Composable get() = MaterialTheme.colorScheme.surface
    val surfaceVariant @Composable get() = MaterialTheme.colorScheme.surfaceVariant
    val contentBackground @Composable get() = MaterialTheme.colorScheme.background
    val cardBackground @Composable get() = MaterialTheme.colorScheme.surfaceContainer
    val scrimOverlay @Composable get() = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)
}
```

### Text Colors

| Token Name | Material 3 Role | Light Theme | Dark Theme | Usage |
|------------|-----------------|-------------|------------|-------|
| `Sidebar_Text_Primary` | `onSurface` | `#1C1B1F` | `#E6E1E5` | Primary text (labels, titles) |
| `Sidebar_Text_Secondary` | `onSurfaceVariant` | `#49454F` | `#CAC4D0` | Secondary text (date, hints) |
| `NavigationItem_Text_Selected` | `onSecondaryContainer` | `#1D192B` | `#E8DEF8` | Selected item text |
| `NavigationItem_Text_Unselected` | `onSurfaceVariant` | `#49454F` | `#CAC4D0` | Unselected item text |

**Code Implementation:**

```kotlin
object SidebarTextColors {
    val primary @Composable get() = MaterialTheme.colorScheme.onSurface
    val secondary @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
    val selectedItem @Composable get() = MaterialTheme.colorScheme.onSecondaryContainer
    val unselectedItem @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
}
```

### Interactive Colors

| Token Name | Material 3 Role | Light Theme | Dark Theme | Usage |
|------------|-----------------|-------------|------------|-------|
| `NavigationItem_Background_Selected` | `secondaryContainer` | `#E8DEF8` | `#4A4458` | Selected item background |
| `NavigationItem_Background_Hover` | `surfaceVariant` @ 0.08 | `#E7E0EC` @ 0.08 | `#49454F` @ 0.08 | Hover state overlay |
| `NavigationItem_Icon_Selected` | `onSecondaryContainer` | `#1D192B` | `#E8DEF8` | Selected item icon color |
| `NavigationItem_Icon_Unselected` | `onSurfaceVariant` | `#49454F` | `#CAC4D0` | Unselected item icon color |
| `Toggle_Button_Background` | `surfaceVariant` | `#E7E0EC` | `#49454F` | Collapse/expand button background |
| `Toggle_Button_Icon` | `onSurfaceVariant` | `#49454F` | `#CAC4D0` | Collapse/expand button icon |

**Code Implementation:**

```kotlin
object NavigationItemColors {
    val selectedBackground @Composable get() = MaterialTheme.colorScheme.secondaryContainer
    val hoverOverlay @Composable get() = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.08f)
    val selectedIcon @Composable get() = MaterialTheme.colorScheme.onSecondaryContainer
    val unselectedIcon @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
}
```

### Border & Outline Colors

| Token Name | Material 3 Role | Light Theme | Dark Theme | Usage |
|------------|-----------------|-------------|------------|-------|
| `Sidebar_Border` | `outlineVariant` | `#CAC4D0` | `#49454F` | Sidebar right border |
| `Card_Border` | `outlineVariant` | `#CAC4D0` | `#49454F` | Content card borders |
| `Divider` | `outlineVariant` @ 0.5 | `#CAC4D0` @ 0.5 | `#49454F` @ 0.5 | Section dividers |

**Code Implementation:**

```kotlin
object SidebarBorderColors {
    val border @Composable get() = MaterialTheme.colorScheme.outlineVariant
    val cardBorder @Composable get() = MaterialTheme.colorScheme.outlineVariant
    val divider @Composable get() = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
}
```

### Badge Colors

| Token Name | Material 3 Role | Light Theme | Dark Theme | Usage |
|------------|-----------------|-------------|------------|-------|
| `Badge_Background` | `error` | `#BA1A1A` | `#FFB4AB` | Badge background color |
| `Badge_Text` | `onError` | `#FFFFFF` | `#690005` | Badge text color |

**Code Implementation:**

```kotlin
object BadgeColors {
    val background @Composable get() = MaterialTheme.colorScheme.error
    val text @Composable get() = MaterialTheme.colorScheme.onError
}
```

---

## Spacing Tokens

### Layout Spacing

| Token Name | Value (dp) | Usage |
|------------|------------|-------|
| `Sidebar_Padding_Horizontal` | `16` | Left/right padding inside sidebar |
| `Sidebar_Padding_Vertical` | `8` | Top/bottom padding inside sidebar |
| `Content_Padding` | `24` | Main content area padding |
| `Card_Padding` | `16` | Content card internal padding |
| `Card_Spacing` | `16` | Space between content cards |

**Code Implementation:**

```kotlin
object SidebarSpacing {
    val horizontalPadding = 16.dp
    val verticalPadding = 8.dp
    val contentPadding = 24.dp
    val cardPadding = 16.dp
    val cardSpacing = 16.dp
}
```

### Component Spacing

| Token Name | Value (dp) | Usage |
|------------|------------|-------|
| `NavigationItem_Padding` | `16` | Navigation item internal padding |
| `NavigationItem_IconLabelGap` | `12` | Space between icon and label |
| `NavigationItem_VerticalSpacing` | `4` | Space between navigation items |
| `Header_Padding` | `16` | Sidebar header padding |
| `Header_Height` | `64` | Sidebar header height |
| `Badge_Offset_X` | `4` | Badge horizontal offset |
| `Badge_Offset_Y` | `-4` | Badge vertical offset |

**Code Implementation:**

```kotlin
object NavigationItemSpacing {
    val padding = 16.dp
    val iconLabelGap = 12.dp
    val verticalSpacing = 4.dp
}

object HeaderSpacing {
    val padding = 16.dp
    val height = 64.dp
}

object BadgeSpacing {
    val offsetX = 4.dp
    val offsetY = (-4).dp
}
```

---

## Typography Tokens

### Text Styles

| Token Name | Material 3 Style | Font Size | Line Height | Weight | Usage |
|------------|------------------|-----------|-------------|--------|-------|
| `Sidebar_Header_Date` | `titleMedium` | `16sp` | `24sp` | Medium (500) | Full date in header |
| `Sidebar_Header_Day` | `headlineMedium` | `28sp` | `36sp` | Regular (400) | Day number (collapsed) |
| `NavigationItem_Label` | `labelLarge` | `14sp` | `20sp` | Medium (500) | Navigation item label |
| `Card_Title` | `titleLarge` | `22sp` | `28sp` | Regular (400) | Content card title |
| `Card_Body` | `bodyMedium` | `14sp` | `20sp` | Regular (400) | Content card body text |
| `Badge_Text` | `labelSmall` | `11sp` | `16sp` | Medium (500) | Badge count text |

**Code Implementation:**

```kotlin
object SidebarTypography {
    val headerDate @Composable get() = MaterialTheme.typography.titleMedium
    val headerDay @Composable get() = MaterialTheme.typography.headlineMedium
    val navigationLabel @Composable get() = MaterialTheme.typography.labelLarge
    val cardTitle @Composable get() = MaterialTheme.typography.titleLarge
    val cardBody @Composable get() = MaterialTheme.typography.bodyMedium
    val badgeText @Composable get() = MaterialTheme.typography.labelSmall
}
```

---

## Animation Tokens

### Duration

| Token Name | Value (ms) | Usage |
|------------|------------|-------|
| `Sidebar_CollapseExpand_Duration` | `300` | Sidebar width transition |
| `Label_FadeInOut_Duration` | `200` | Label visibility transition |
| `Icon_Scale_Duration` | `150` | Icon scale animation |
| `Background_Transition_Duration` | `200` | Background color transition |
| `Scrim_FadeInOut_Duration` | `250` | Scrim overlay fade |

**Code Implementation:**

```kotlin
object SidebarAnimationDurations {
    const val collapseExpand = 300
    const val labelFade = 200
    const val iconScale = 150
    const val backgroundTransition = 200
    const val scrimFade = 250
}
```

### Easing

| Token Name | Easing Function | Usage |
|------------|-----------------|-------|
| `Sidebar_CollapseExpand_Easing` | `FastOutSlowInEasing` | Sidebar width animation |
| `Label_FadeInOut_Easing` | `LinearEasing` | Label fade animation |
| `Icon_Scale_Easing` | `FastOutSlowInEasing` | Icon scale animation |
| `Background_Transition_Easing` | `LinearEasing` | Background color transition |

**Code Implementation:**

```kotlin
object SidebarAnimationEasing {
    val collapseExpand = FastOutSlowInEasing
    val labelFade = LinearEasing
    val iconScale = FastOutSlowInEasing
    val backgroundTransition = LinearEasing
}
```

### Animation Specifications

```kotlin
object SidebarAnimations {
    val widthTransition = tween<Dp>(
        durationMillis = SidebarAnimationDurations.collapseExpand,
        easing = SidebarAnimationEasing.collapseExpand
    )
    
    val labelFade = tween<Float>(
        durationMillis = SidebarAnimationDurations.labelFade,
        easing = SidebarAnimationEasing.labelFade
    )
    
    val iconScale = tween<Float>(
        durationMillis = SidebarAnimationDurations.iconScale,
        easing = SidebarAnimationEasing.iconScale
    )
    
    val backgroundTransition = tween<Color>(
        durationMillis = SidebarAnimationDurations.backgroundTransition,
        easing = SidebarAnimationEasing.backgroundTransition
    )
}
```

---

## Dimension Tokens

### Sidebar Dimensions

| Token Name | Value (dp) | Usage |
|------------|------------|-------|
| `Sidebar_Width_Expanded` | `240` | Sidebar width when expanded |
| `Sidebar_Width_Collapsed` | `72` | Sidebar width when collapsed |
| `Sidebar_MinHeight` | `match_parent` | Sidebar minimum height |
| `Sidebar_Elevation` | `0` | Sidebar elevation (uses border instead) |

**Code Implementation:**

```kotlin
object SidebarDimensions {
    val expandedWidth = 240.dp
    val collapsedWidth = 72.dp
    val minHeight = Dp.Unspecified // match_parent
    val elevation = 0.dp
}
```

### Icon Dimensions

| Token Name | Value (dp) | Usage |
|------------|------------|-------|
| `NavigationItem_IconSize` | `24` | Navigation item icon size |
| `Toggle_Button_IconSize` | `24` | Collapse/expand button icon size |
| `Badge_MinSize` | `16` | Minimum badge size |
| `Badge_MaxDigits` | `2` | Max digits before showing "99+" |

**Code Implementation:**

```kotlin
object IconDimensions {
    val navigationIcon = 24.dp
    val toggleButton = 24.dp
    val badgeMin = 16.dp
    const val badgeMaxDigits = 2
}
```

### Touch Target Dimensions

| Token Name | Value (dp) | Usage |
|------------|------------|-------|
| `TouchTarget_MinSize` | `48` | Minimum touch target (WCAG) |
| `NavigationItem_MinHeight` | `48` | Navigation item minimum height |
| `Toggle_Button_MinSize` | `48` | Collapse button minimum size |

**Code Implementation:**

```kotlin
object TouchTargets {
    val minSize = 48.dp
    val navigationItemMinHeight = 48.dp
    val toggleButtonMinSize = 48.dp
}
```

---

## Elevation Tokens

### Component Elevation

| Token Name | Value (dp) | Usage |
|------------|------------|-------|
| `Sidebar_Elevation` | `0` | Sidebar uses border instead of elevation |
| `Card_Elevation` | `0` | Cards use border instead of elevation |
| `Scrim_Elevation` | `16` | Scrim overlay elevation (z-index) |
| `Tooltip_Elevation` | `8` | Tooltip elevation |

**Code Implementation:**

```kotlin
object SidebarElevation {
    val sidebar = 0.dp
    val card = 0.dp
    val scrim = 16.dp
    val tooltip = 8.dp
}
```

---

## Border Tokens

### Border Widths

| Token Name | Value (dp) | Usage |
|------------|------------|-------|
| `Sidebar_Border_Width` | `1` | Sidebar right border |
| `Card_Border_Width` | `1` | Content card border |
| `Divider_Width` | `1` | Section divider border |

**Code Implementation:**

```kotlin
object BorderWidths {
    val sidebar = 1.dp
    val card = 1.dp
    val divider = 1.dp
}
```

### Border Radius

| Token Name | Value (dp) | Usage |
|------------|------------|-------|
| `NavigationItem_CornerRadius` | `12` | Navigation item corner radius |
| `Card_CornerRadius` | `12` | Content card corner radius |
| `Badge_CornerRadius` | `8` | Badge corner radius |
| `Toggle_Button_CornerRadius` | `12` | Collapse button corner radius |

**Code Implementation:**

```kotlin
object BorderRadius {
    val navigationItem = 12.dp
    val card = 12.dp
    val badge = 8.dp
    val toggleButton = 12.dp
}
```

---

## Responsive Breakpoints

### Window Size Classes

| Token Name | Min Width (dp) | Max Width (dp) | Device Type | Sidebar Behavior |
|------------|----------------|----------------|-------------|------------------|
| `Compact` | `0` | `599` | Phone Portrait | Collapsible, auto-collapse enabled |
| `Medium` | `600` | `839` | Phone Landscape / Small Tablet | Collapsible, auto-collapse enabled |
| `Expanded` | `840` | `∞` | Tablet / Desktop | Always expanded, collapse disabled |

**Code Implementation:**

```kotlin
object ResponsiveBreakpoints {
    const val compactMaxWidth = 599
    const val mediumMinWidth = 600
    const val mediumMaxWidth = 839
    const val expandedMinWidth = 840
    
    @Composable
    fun isCompact(): Boolean {
        val windowSizeClass = calculateWindowSizeClass(LocalContext.current as ComponentActivity)
        return windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    }
    
    @Composable
    fun isMedium(): Boolean {
        val windowSizeClass = calculateWindowSizeClass(LocalContext.current as ComponentActivity)
        return windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium
    }
    
    @Composable
    fun isExpanded(): Boolean {
        val windowSizeClass = calculateWindowSizeClass(LocalContext.current as ComponentActivity)
        return windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
    }
}
```

### Orientation Breakpoints

| Token Name | Orientation | Device Type | Sidebar Behavior |
|------------|-------------|-------------|------------------|
| `Phone_Portrait` | Portrait | Phone | Collapsible |
| `Phone_Landscape` | Landscape | Phone | Collapsible |
| `Tablet_Portrait` | Portrait | Tablet (7"+) | Always expanded |
| `Tablet_Landscape` | Landscape | Tablet (7"+) | Always expanded |

---

## Component-Specific Tokens

### CollapsibleNavigationDrawer

```kotlin
object CollapsibleNavigationDrawerTokens {
    // Dimensions
    val expandedWidth = 240.dp
    val collapsedWidth = 72.dp
    
    // Colors
    val containerColor @Composable get() = MaterialTheme.colorScheme.surface
    val borderColor @Composable get() = MaterialTheme.colorScheme.outlineVariant
    
    // Spacing
    val horizontalPadding = 16.dp
    val verticalPadding = 8.dp
    
    // Animation
    val widthAnimationDuration = 300
    val widthAnimationEasing = FastOutSlowInEasing
    
    // Elevation
    val elevation = 0.dp
    val borderWidth = 1.dp
}
```

### SidebarHeader

```kotlin
object SidebarHeaderTokens {
    // Dimensions
    val height = 64.dp
    
    // Spacing
    val padding = 16.dp
    val iconLabelGap = 12.dp
    
    // Typography
    val dateTextStyle @Composable get() = MaterialTheme.typography.titleMedium
    val dayTextStyle @Composable get() = MaterialTheme.typography.headlineMedium
    
    // Colors
    val textColor @Composable get() = MaterialTheme.colorScheme.onSurface
    val toggleButtonBackground @Composable get() = MaterialTheme.colorScheme.surfaceVariant
    val toggleButtonIcon @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
    
    // Border Radius
    val toggleButtonCornerRadius = 12.dp
}
```

### NavigationItem

```kotlin
object NavigationItemTokens {
    // Dimensions
    val minHeight = 48.dp
    val iconSize = 24.dp
    
    // Spacing
    val padding = 16.dp
    val iconLabelGap = 12.dp
    val verticalSpacing = 4.dp
    
    // Typography
    val labelTextStyle @Composable get() = MaterialTheme.typography.labelLarge
    
    // Colors - Unselected
    val unselectedIconColor @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
    val unselectedTextColor @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
    val unselectedBackgroundColor = Color.Transparent
    
    // Colors - Selected
    val selectedIconColor @Composable get() = MaterialTheme.colorScheme.onSecondaryContainer
    val selectedTextColor @Composable get() = MaterialTheme.colorScheme.onSecondaryContainer
    val selectedBackgroundColor @Composable get() = MaterialTheme.colorScheme.secondaryContainer
    
    // Colors - Hover
    val hoverOverlayColor @Composable get() = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.08f)
    
    // Border Radius
    val cornerRadius = 12.dp
    
    // Animation
    val labelFadeDuration = 200
    val iconScaleDuration = 150
    val backgroundTransitionDuration = 200
}
```

### Badge

```kotlin
object BadgeTokens {
    // Dimensions
    val minSize = 16.dp
    val maxDigits = 2
    
    // Spacing
    val offsetX = 4.dp
    val offsetY = (-4).dp
    val padding = 4.dp
    
    // Typography
    val textStyle @Composable get() = MaterialTheme.typography.labelSmall
    
    // Colors
    val backgroundColor @Composable get() = MaterialTheme.colorScheme.error
    val textColor @Composable get() = MaterialTheme.colorScheme.onError
    
    // Border Radius
    val cornerRadius = 8.dp
}
```

### MainContentArea

```kotlin
object MainContentAreaTokens {
    // Spacing
    val padding = 24.dp
    val cardSpacing = 16.dp
    
    // Grid Layout
    val gridColumns = 2 // Responsive: 1 on phone, 2 on tablet
    
    // Colors
    val backgroundColor @Composable get() = MaterialTheme.colorScheme.background
}
```

### ContentCard

```kotlin
object ContentCardTokens {
    // Spacing
    val padding = 16.dp
    
    // Colors
    val backgroundColor @Composable get() = MaterialTheme.colorScheme.surfaceContainer
    val borderColor @Composable get() = MaterialTheme.colorScheme.outlineVariant
    
    // Border
    val borderWidth = 1.dp
    val cornerRadius = 12.dp
    
    // Elevation
    val elevation = 0.dp // Uses border instead
}
```

---

## Usage Examples

### Applying Color Tokens

```kotlin
@Composable
fun NavigationItem(/* ... */) {
    Surface(
        color = if (isSelected) {
            NavigationItemTokens.selectedBackgroundColor
        } else {
            NavigationItemTokens.unselectedBackgroundColor
        },
        shape = RoundedCornerShape(NavigationItemTokens.cornerRadius)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = NavigationItemTokens.minHeight)
                .padding(NavigationItemTokens.padding)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(NavigationItemTokens.iconSize),
                tint = if (isSelected) {
                    NavigationItemTokens.selectedIconColor
                } else {
                    NavigationItemTokens.unselectedIconColor
                }
            )
            
            AnimatedVisibility(visible = !isCollapsed) {
                Text(
                    text = label,
                    style = NavigationItemTokens.labelTextStyle,
                    color = if (isSelected) {
                        NavigationItemTokens.selectedTextColor
                    } else {
                        NavigationItemTokens.unselectedTextColor
                    }
                )
            }
        }
    }
}
```

### Applying Animation Tokens

```kotlin
@Composable
fun CollapsibleNavigationDrawer(/* ... */) {
    val animatedWidth by animateDpAsState(
        targetValue = if (isCollapsed) {
            CollapsibleNavigationDrawerTokens.collapsedWidth
        } else {
            CollapsibleNavigationDrawerTokens.expandedWidth
        },
        animationSpec = tween(
            durationMillis = CollapsibleNavigationDrawerTokens.widthAnimationDuration,
            easing = CollapsibleNavigationDrawerTokens.widthAnimationEasing
        ),
        label = "sidebar_width"
    )
    
    Surface(
        modifier = Modifier.width(animatedWidth),
        color = CollapsibleNavigationDrawerTokens.containerColor
    ) {
        // Content
    }
}
```

### Applying Responsive Tokens

```kotlin
@Composable
fun AdaptiveCollapsibleSidebar(/* ... */) {
    val isTablet = ResponsiveBreakpoints.isExpanded()
    
    val effectiveIsCollapsed = if (isTablet) {
        false // Always expanded on tablets
    } else {
        isCollapsed
    }
    
    CollapsibleSidebarLayout(
        isCollapsed = effectiveIsCollapsed,
        // ...
    )
}
```

---

## Token Maintenance

### Adding New Tokens

1. Define token in appropriate category
2. Add Material 3 mapping if applicable
3. Create Kotlin object/property
4. Document usage
5. Update this specification

### Modifying Existing Tokens

1. Update token value
2. Test across light/dark/dynamic themes
3. Verify responsive behavior
4. Update documentation
5. Review with design team

### Deprecating Tokens

1. Mark as deprecated in code
2. Document replacement token
3. Create migration guide
4. Remove after 2 release cycles

---

## References

- **Material 3 Design Tokens**: [Material Design 3](https://m3.material.io/foundations/design-tokens/overview)
- **Compose Material 3**: [Jetpack Compose Material 3](https://developer.android.com/jetpack/compose/themes/material3)
- **Color System**: [Material 3 Color System](https://m3.material.io/styles/color/the-color-system/overview)
- **Typography**: [Material 3 Typography](https://m3.material.io/styles/typography/overview)

---

**Document Version:** 1.0  
**Last Reviewed:** January 22, 2026  
**Next Review:** March 22, 2026
