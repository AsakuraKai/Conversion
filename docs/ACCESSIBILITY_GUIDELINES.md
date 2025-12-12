# Accessibility Guidelines

**Last Updated:** December 9, 2025  
**Status:** Production-Ready  
**Compliance:** WCAG 2.1 AA, Material Design Accessibility

---

## 📋 Overview

This document provides comprehensive accessibility guidelines for the Files Management application. Following these guidelines ensures the app is usable by everyone, including users with disabilities who rely on screen readers, voice control, or other assistive technologies.

---

## 🎯 Core Principles

### 1. Perceivable
Content must be presentable to all users.
- ✅ Text alternatives for non-text content
- ✅ Sufficient color contrast (4.5:1 for normal text)
- ✅ Resizable text up to 200%
- ✅ Content doesn't rely solely on color

### 2. Operable
Interface components must be operable.
- ✅ All functionality available via keyboard
- ✅ Minimum touch target size (48x48 dp)
- ✅ No timing requirements
- ✅ Clear focus indicators

### 3. Understandable
Information and operation must be understandable.
- ✅ Clear, predictable navigation
- ✅ Consistent UI patterns
- ✅ Error messages are descriptive
- ✅ Input assistance provided

### 4. Robust
Content must work with assistive technologies.
- ✅ Semantic HTML/Compose structure
- ✅ Proper ARIA roles (Compose semantics)
- ✅ Compatible with TalkBack
- ✅ Works with screen magnification

---

## 🛠️ Implementation Checklist

### Content Descriptions

**All Interactive Elements:**
```kotlin
// ✅ Good
IconButton(
    onClick = { },
    modifier = Modifier.semantics {
        contentDescription = "Settings"
        role = Role.Button
    }
) {
    Icon(
        imageVector = Icons.Default.Settings,
        contentDescription = null // Description on parent
    )
}

// ❌ Bad
IconButton(onClick = { }) {
    Icon(
        imageVector = Icons.Default.Settings,
        contentDescription = "" // Empty or missing
    )
}
```

**Decorative Images:**
```kotlin
// ✅ Good - Decorative image
Icon(
    imageVector = Icons.Default.Star,
    contentDescription = null
)

// ❌ Bad - Unnecessary description
Icon(
    imageVector = Icons.Default.Star,
    contentDescription = "Star decoration"
)
```

**Dynamic Content:**
```kotlin
// ✅ Good - State description
Card(
    modifier = Modifier.semantics {
        contentDescription = "File: ${file.name}"
        stateDescription = if (isSelected) "Selected" else "Not selected"
        role = Role.Checkbox
    }
)
```

### Touch Target Sizes

**Minimum Standards:**
- Standard: 48x48 dp
- Compact: 40x40 dp (use sparingly)
- Icon-only buttons: Always 48x48 dp

```kotlin
// ✅ Good
IconButton(
    onClick = { },
    modifier = Modifier.size(48.dp)
) { Icon(...) }

// ✅ Good - With padding
Icon(
    ...,
    modifier = Modifier
        .size(24.dp)
        .padding(12.dp) // Total 48dp
)

// ❌ Bad - Too small
Icon(
    ...,
    modifier = Modifier
        .size(20.dp)
        .clickable { }
)
```

### Semantic Roles

**Common Roles:**
```kotlin
// Button
Modifier.semantics { role = Role.Button }

// Checkbox
Modifier.semantics { role = Role.Checkbox }

// Radio button
Modifier.semantics { role = Role.RadioButton }

// Tab
Modifier.semantics { role = Role.Tab }

// Image
Modifier.semantics { role = Role.Image }
```

### State Descriptions

**Selection State:**
```kotlin
Modifier.semantics {
    stateDescription = if (isSelected) "Selected" else "Not selected"
}
```

**Loading State:**
```kotlin
Modifier.semantics {
    stateDescription = if (isLoading) "Loading" else "Loaded"
}
```

**Expanded State:**
```kotlin
Modifier.semantics {
    stateDescription = if (isExpanded) "Expanded" else "Collapsed"
}
```

**Progress State:**
```kotlin
Modifier.semantics {
    stateDescription = "Progress: $current of $total"
}
```

---

## 🎨 Color Contrast

### WCAG AA Standards
- **Normal text:** 4.5:1 contrast ratio
- **Large text (18pt+):** 3:1 contrast ratio
- **UI components:** 3:1 contrast ratio

### Testing Tools
- Chrome DevTools Lighthouse
- Android Accessibility Scanner
- Online: WebAIM Contrast Checker

### Material 3 Colors
Our theme colors already meet WCAG AA standards:
- Primary on surface: 4.6:1
- OnPrimary on primary: 4.5:1
- Error on surface: 4.5:1

```kotlin
// ✅ Good - High contrast
Text(
    text = "Error message",
    color = MaterialTheme.colorScheme.error,
    style = MaterialTheme.typography.bodyMedium
)

// ⚠️ Check - May not have sufficient contrast
Text(
    text = "Subtle text",
    color = Color.Gray,
    style = MaterialTheme.typography.bodySmall
)
```

---

## 🌍 RTL Support

### Layout Best Practices

**Use Start/End Instead of Left/Right:**
```kotlin
// ✅ Good
Modifier.padding(start = 16.dp, end = 8.dp)

// ❌ Bad
Modifier.padding(left = 16.dp, right = 8.dp)
```

**RTL-Aware Arrangements:**
```kotlin
// ✅ Good - Automatically mirrors
Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
) {
    Text("Start") // Left in LTR, Right in RTL
    Text("End")   // Right in LTR, Left in RTL
}
```

**Detecting RTL:**
```kotlin
@Composable
fun MyComponent() {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    
    if (isRtl) {
        // RTL-specific layout
    } else {
        // LTR layout
    }
}
```

**Testing RTL:**
```kotlin
@Preview
@Preview(locale = "ar") // Arabic
@Composable
fun MyComponentPreview() {
    MyComponent()
}

// Or force RTL in code:
CompositionLocalProvider(
    LocalLayoutDirection provides LayoutDirection.Rtl
) {
    MyComponent()
}
```

### Icons That Mirror in RTL
- Arrow back/forward
- Chevron left/right
- Navigate previous/next
- List bullets

### Icons That Don't Mirror
- Close, Add, Remove
- Settings, Search
- Play, Pause, Stop
- Check, Error icons

---

## 📱 Font Scaling

### Support Dynamic Type
Test with large font sizes (up to 200%):
1. Settings → Display → Font size → Largest
2. Ensure text doesn't overflow
3. Use appropriate line heights

```kotlin
// ✅ Good - Scales automatically
Text(
    text = "File name",
    style = MaterialTheme.typography.bodyMedium,
    maxLines = 2,
    overflow = TextOverflow.Ellipsis
)

// ❌ Bad - Fixed size
Text(
    text = "File name",
    fontSize = 14.sp, // Won't scale
    maxLines = 1
)
```

### Typography Scale
Use Material 3 typography scale:
- `displayLarge` - Hero text
- `headlineMedium` - Screen titles
- `titleLarge` - Card titles
- `bodyLarge` - Emphasized body text
- `bodyMedium` - Standard body text
- `labelLarge` - Buttons, tabs

---

## 🧪 Testing

### TalkBack Testing

**Enable TalkBack:**
1. Settings → Accessibility → TalkBack → On
2. Navigate with swipe gestures
3. Activate with double-tap

**Test Checklist:**
- ✅ All buttons announce their purpose
- ✅ Selection state is announced
- ✅ Loading states are clear
- ✅ Error messages are read
- ✅ Navigation is logical
- ✅ No "unlabeled button" announcements

**Common Issues:**
- Missing content descriptions
- Decorative images with descriptions
- Redundant announcements
- Illogical reading order

### Accessibility Scanner

**Install Android Accessibility Scanner:**
1. Play Store → Accessibility Scanner
2. Enable in Settings → Accessibility
3. Tap blue button to scan screen

**Common Findings:**
- Touch target too small
- Low color contrast
- Missing content description
- Clickable element too close to another

### Manual Testing

**Color Blindness:**
- Test with grayscale display
- Use online simulators
- Ensure information isn't color-only

**Screen Magnification:**
- Enable magnification (3-finger tap)
- Test at 2x, 4x zoom
- Ensure layouts don't break

**Large Fonts:**
- Set font to largest
- Verify text doesn't overflow
- Check button labels are visible

---

## 📚 Utilities Reference

### AccessibilityUtils
```kotlin
// Minimum touch targets
AccessibilityUtils.MIN_TOUCH_TARGET_SIZE // 48.dp
AccessibilityUtils.MIN_COMPACT_TARGET_SIZE // 40.dp

// Content descriptions
AccessibilityStrings.SELECT_FILE
AccessibilityStrings.NAVIGATE_BACK
AccessibilityStrings.LOADING

// State descriptions
StateDescriptions.fileSelection(isSelected)
StateDescriptions.filesSelected(count)
StateDescriptions.progress(current, total)
```

### AccessibleComponents
```kotlin
// Accessible icon button
AccessibleIconButton(
    onClick = { },
    contentDescription = "Settings",
    icon = Icons.Default.Settings
)

// Accessible clickable
Modifier.accessibleClickable(
    contentDescription = "Select file",
    role = Role.Checkbox,
    onClick = { }
)

// State modifiers
Modifier.selectableState(isSelected)
Modifier.loadingState(isLoading)
Modifier.expandableState(isExpanded)
```

### RtlSupport
```kotlin
// Check RTL
val isRtl = RtlSupport.isRtl()

// RTL-aware padding
RtlSupport.paddingValues(
    start = 16.dp,
    end = 8.dp
)
```

---

## ✅ Compliance Checklist

### Per Screen
- [ ] All interactive elements have content descriptions
- [ ] Decorative images marked with null description
- [ ] All buttons meet 48x48 dp minimum
- [ ] Color contrast meets WCAG AA (4.5:1)
- [ ] TalkBack navigation is logical
- [ ] Selection states are announced
- [ ] Loading states are clear
- [ ] Error messages are descriptive
- [ ] RTL layout tested (Arabic)
- [ ] Large font tested (200%)
- [ ] Screen magnification tested

### Per Component
- [ ] Semantic role assigned
- [ ] Content description provided
- [ ] State description for dynamic UI
- [ ] Touch target adequate
- [ ] Focus indicator visible
- [ ] Works with TalkBack
- [ ] RTL layout correct

---

## 🎯 Quick Reference

### When to Use Content Descriptions

**Always:**
- Icon-only buttons
- Interactive images
- Clickable cards
- Custom controls

**Never:**
- Decorative images
- Icons with adjacent text
- Purely visual elements

### Content Description Quality

**Good Examples:**
- "Settings"
- "Delete template"
- "Select file IMG_001.jpg"
- "Navigate back"

**Bad Examples:**
- "Button" (redundant)
- "Icon" (not descriptive)
- "Click here" (not specific)
- "" (empty)

### State Description Examples

**Selection:**
- "Selected" / "Not selected"
- "Checked" / "Unchecked"

**Progress:**
- "Progress: 5 of 10"
- "Uploading: 50%"

**Expansion:**
- "Expanded" / "Collapsed"
- "Showing details" / "Hiding details"

**Loading:**
- "Loading files"
- "Processing"
- "Loaded"

---

## 📖 Resources

### Android Documentation
- [Accessibility Principles](https://developer.android.com/guide/topics/ui/accessibility/principles)
- [Make apps accessible](https://developer.android.com/guide/topics/ui/accessibility/apps)
- [Compose Accessibility](https://developer.android.com/jetpack/compose/accessibility)
- [Testing Accessibility](https://developer.android.com/guide/topics/ui/accessibility/testing)

### WCAG Guidelines
- [WCAG 2.1 AA Guidelines](https://www.w3.org/WAI/WCAG21/quickref/)
- [Understanding WCAG](https://www.w3.org/WAI/WCAG21/Understanding/)

### Tools
- [Android Accessibility Scanner](https://play.google.com/store/apps/details?id=com.google.android.apps.accessibility.auditor)
- [Color Contrast Checker](https://webaim.org/resources/contrastchecker/)
- [Material Design Accessibility](https://m3.material.io/foundations/accessible-design/overview)

---

**Remember:** Accessibility is not optional. It's a fundamental requirement that makes your app usable by everyone. When in doubt, test with TalkBack and the Accessibility Scanner.
