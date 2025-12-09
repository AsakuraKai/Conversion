# Screenshot Gallery - Auto Rename File Service

**Last Updated:** December 9, 2025  
**Maintainer:** Sokchea (Frontend/UI Specialist)

---

## 📸 Overview

This document provides a comprehensive visual reference for all screens and major UI states in the Auto Rename File Service application. Screenshots are organized by feature and include both light and dark themes.

**Note:** Screenshots should be captured at 1080x2400 resolution (standard Android phone) and saved as PNG files for best quality.

---

## 🎨 Theme Showcase

### Light Theme
Location: `docs/screenshots/themes/light_theme.png`

**Captures:**
- System default light theme
- Material 3 color scheme
- Standard UI components

### Dark Theme
Location: `docs/screenshots/themes/dark_theme.png`

**Captures:**
- System default dark theme
- OLED-friendly blacks
- Adjusted contrast for readability

### Dynamic Theme (Image-Based)
Location: `docs/screenshots/themes/dynamic_theme_*.png`

**Captures:**
- Theme generated from image palette
- Color extraction from user photos
- Harmonious color application across UI

---

## 📱 Core Features

### 1. File Selection Screen

#### Normal State
**Location:** `docs/screenshots/file_selection/`

**light_mode.png**
- Grid layout with media thumbnails
- Multiple file types visible (images, videos)
- Bottom navigation bar
- Top app bar with title

**dark_mode.png**
- Same layout in dark theme
- Adjusted thumbnail borders for visibility
- Dark surface colors

#### With Selection
**Location:** `docs/screenshots/file_selection/with_selection.png`

**Captures:**
- Multiple files selected (checkmark overlay)
- Elevated card design for selected items
- Selection counter in top bar
- Floating action button visible

#### Empty State
**Location:** `docs/screenshots/file_selection/empty_state.png`

**Captures:**
- Large icon indicating no files
- Explanatory message
- Action button to grant permissions or browse files

#### Loading State
**Location:** `docs/screenshots/file_selection/loading.png`

**Captures:**
- Skeleton grid placeholders
- Shimmer animation effect
- Loading indicator

#### Error State
**Location:** `docs/screenshots/file_selection/error_state.png`

**Captures:**
- Error icon (red)
- Error title and message
- Retry button
- Optional secondary action

---

### 2. Rename Configuration Screen

#### Default View
**Location:** `docs/screenshots/rename_config/`

**normal_state.png**
- Input fields for rename pattern
  - Prefix text field
  - Start number picker
  - Digit count selector
- Sort strategy picker
- Live preview section
- Bottom action buttons (Cancel, Apply)

**with_metadata.png**
- Metadata variable chips displayed
- Inserted variables in pattern field
- Preview showing variable substitution

#### Validation States
**Location:** `docs/screenshots/rename_config/validation_*.png`

**validation_error.png**
- Invalid pattern highlighted
- Error message below input field
- Red color indication

**validation_success.png**
- Valid pattern with green checkmark
- Preview showing expected results

#### Sort Strategy Selection
**Location:** `docs/screenshots/rename_config/sort_picker.png`

**Captures:**
- Radio button list of sort strategies
  - Natural (alphanumeric)
  - Date Modified
  - File Size
  - Original Order
- Description for each strategy
- Selected option highlighted

---

### 3. Preview Screen

#### Normal Preview
**Location:** `docs/screenshots/preview/`

**normal_view.png**
- Before/after comparison list
- Original filename → New filename
- File thumbnails
- Warning icons for conflicts

**with_conflicts.png**
- Duplicate name warnings (yellow)
- Invalid character warnings (red)
- Suggested resolutions

**with_filters.png**
- Tag filter chips active
- Filtered results shown
- Filter count indicator

#### Empty State
**Location:** `docs/screenshots/preview/empty_state.png`

**Captures:**
- Message indicating no preview available
- Suggestion to configure rename pattern

---

### 4. Rename Progress Screen

#### In Progress
**Location:** `docs/screenshots/progress/`

**in_progress.png**
- Circular progress indicator
- Current file being renamed
- Statistics (X of Y files renamed)
- Elapsed time

**with_animation.png**
- Success checkmark animation
- File transition animation
- Progress bar filling

#### Completion States
**Location:** `docs/screenshots/progress/completion_*.png`

**success.png**
- Success icon (green checkmark)
- Completion message
- Summary statistics
- "Done" button

**partial_success.png**
- Mixed result icon
- Success count + error count
- "View Errors" expandable section
- "Done" and "Retry Failed" buttons

**error_details.png**
- Expanded error list
- Each failed file with reason
- Retry individual or all option

---

### 5. Settings Screen

#### Main Settings
**Location:** `docs/screenshots/settings/`

**main_screen.png**
- Grouped preference sections
  - Theme Settings
  - File Operations
  - Advanced Options
- Toggle switches
- Navigation chevrons for nested settings

**theme_section.png**
- Theme mode selector
  - Light
  - Dark
  - System Default
- Dynamic color toggle
- Preview of selected theme

**file_operations.png**
- Default sort strategy
- Auto-backup toggle
- Conflict resolution preference
- File type filters

---

### 6. Template Management

#### Template List
**Location:** `docs/screenshots/templates/`

**template_list.png**
- Card list of saved templates
- Template name and pattern preview
- Edit and delete icons
- Add new template FAB

**empty_state.png**
- Empty state illustration
- "No templates saved" message
- "Create Template" button

#### Template Editor
**Location:** `docs/screenshots/templates/editor.png`

**Captures:**
- Template name input field
- Pattern configuration
- Variable insertion buttons
- Save and cancel actions

**save_dialog.png**
- Confirmation dialog before saving
- Template name and preview
- Save and cancel buttons

---

### 7. Tag Management

#### Tag List
**Location:** `docs/screenshots/tags/`

**tag_list.png**
- Tag chips in grid layout
- Tag name and file count
- Edit and delete actions
- Add tag button

**tag_editor.png**
- Tag name input
- Color picker for tag
- Associated files count
- Save button

**tag_filter_active.png**
- Tag filter chips in file selection
- Active filters highlighted
- File count per tag
- Clear all filters option

---

### 8. Advanced Features

#### Metadata Picker
**Location:** `docs/screenshots/metadata/`

**metadata_picker.png**
- Available metadata variables
- Variable chips with descriptions
- Preview of inserted variables
- Apply button

**metadata_preview.png**
- Preview card showing metadata extraction
- Date, time, location info
- Insert variable buttons

#### OCR Extraction
**Location:** `docs/screenshots/ocr/`

**ocr_button.png**
- OCR extract button on image
- Loading indicator during extraction
- Extracted text preview

**ocr_results.png**
- Extracted text in text field
- Edit and use options
- Confidence score indicator

#### QR Scanner
**Location:** `docs/screenshots/qr/`

**scanner_active.png**
- Camera viewfinder
- Scan area overlay
- Detected QR code highlight
- Extracted text display

---

### 9. Dynamic Theme Gallery

#### Theme Generation
**Location:** `docs/screenshots/dynamic_theme/`

**image_selection.png**
- Image picker for palette source
- Selected image thumbnail
- "Generate Theme" button

**color_extraction.png**
- Extracted color palette display
- Primary, secondary, tertiary swatches
- Apply theme button

**theme_applied.png**
- Full app with custom theme
- Multiple screens showing consistency
- Navigation bar with theme colors

---

### 10. Accessibility Features

#### TalkBack Mode
**Location:** `docs/screenshots/accessibility/`

**talkback_active.png**
- Screen with TalkBack overlay
- Focus indicators on elements
- Content description hints

**large_text.png**
- UI with 1.5x font scale
- Properly scaled components
- No text overflow

**high_contrast.png**
- High contrast mode enabled
- Enhanced borders and separators
- Improved readability

---

### 11. Error & Edge Cases

#### Permission Denied
**Location:** `docs/screenshots/errors/`

**permission_denied.png**
- Permission rationale dialog
- Explanation of why permission needed
- "Grant Permission" and "Deny" buttons

**permission_settings.png**
- Guide to app settings
- Instructions to enable permissions

#### Network Errors
**Location:** `docs/screenshots/errors/network_*.png`

**no_connection.png**
- No internet connection message
- Offline mode indication
- Retry button

**timeout.png**
- Request timeout error
- Retry and cancel options

#### Storage Full
**Location:** `docs/screenshots/errors/storage_full.png**

**Captures:**
- Storage full warning
- Available space indicator
- Clear cache option
- Manage storage button

---

## 📐 Capture Guidelines

### Device Specifications
- **Resolution:** 1080 x 2400 (19.5:9 aspect ratio)
- **DPI:** 420 (xhdpi)
- **Android Version:** Android 12+ (Material You support)

### Capture Settings
- **Clean State:** No personal data or identifiable information
- **Sample Data:** Use consistent mock data across screenshots
- **System UI:** Show status bar and navigation bar
- **Time:** Set to 10:30 AM for consistency
- **Battery:** Show 100% battery
- **Network:** Show full signal strength

### Naming Convention
```
{feature}_{state}_{theme}.png

Examples:
file_selection_normal_light.png
file_selection_with_selection_dark.png
rename_config_validation_error_light.png
progress_completion_success_dark.png
```

### Directory Structure
```
docs/screenshots/
├── themes/
│   ├── light_theme.png
│   ├── dark_theme.png
│   └── dynamic_theme_examples/
├── file_selection/
│   ├── normal_light.png
│   ├── normal_dark.png
│   ├── with_selection_light.png
│   ├── empty_state.png
│   ├── loading_state.png
│   └── error_state.png
├── rename_config/
│   ├── normal_state.png
│   ├── with_metadata.png
│   ├── validation_error.png
│   ├── validation_success.png
│   └── sort_picker.png
├── preview/
│   ├── normal_view.png
│   ├── with_conflicts.png
│   ├── with_filters.png
│   └── empty_state.png
├── progress/
│   ├── in_progress.png
│   ├── completion_success.png
│   ├── completion_partial.png
│   └── error_details.png
├── settings/
│   ├── main_screen.png
│   ├── theme_section.png
│   └── file_operations.png
├── templates/
│   ├── template_list.png
│   ├── empty_state.png
│   ├── editor.png
│   └── save_dialog.png
├── tags/
│   ├── tag_list.png
│   ├── tag_editor.png
│   └── tag_filter_active.png
├── metadata/
│   ├── metadata_picker.png
│   └── metadata_preview.png
├── ocr/
│   ├── ocr_button.png
│   └── ocr_results.png
├── qr/
│   └── scanner_active.png
├── dynamic_theme/
│   ├── image_selection.png
│   ├── color_extraction.png
│   └── theme_applied.png
├── accessibility/
│   ├── talkback_active.png
│   ├── large_text.png
│   └── high_contrast.png
└── errors/
    ├── permission_denied.png
    ├── permission_settings.png
    ├── no_connection.png
    ├── timeout.png
    └── storage_full.png
```

---

## 🎬 Animated Demonstrations

For complex interactions, consider creating short GIF animations:

### Suggested Animations
1. **File Selection Flow** (5 seconds)
   - Browse → Select files → Navigate to config
   
2. **Rename Configuration** (8 seconds)
   - Enter pattern → Preview updates → Apply changes

3. **Progress Animation** (10 seconds)
   - Start rename → Progress updates → Completion

4. **Theme Switching** (3 seconds)
   - Light → Dark → Dynamic theme transition

5. **Tag Filtering** (5 seconds)
   - Add tags → Filter files → Clear filters

### GIF Specifications
- **Frame Rate:** 30 FPS
- **Resolution:** 540 x 1080 (half size for smaller files)
- **Duration:** 3-10 seconds
- **Loop:** Continuous
- **Format:** GIF or MP4 (for higher quality)

---

## 📝 Screenshot Checklist

When capturing screenshots:

- [ ] Device in portrait orientation
- [ ] Clean status bar (10:30 AM, 100% battery, full signal)
- [ ] Consistent mock data across related screens
- [ ] Both light and dark theme versions
- [ ] No personal or sensitive information
- [ ] High-quality PNG format
- [ ] Proper file naming convention
- [ ] Organized in correct directory
- [ ] Updated this documentation with new screenshots

---

## 🔄 Update Schedule

**Regular Updates:**
- After major UI changes or redesigns
- When new features are added
- After significant bug fixes affecting UI
- Quarterly review for consistency

**Version Tracking:**
- Include app version in filename for major releases
- Maintain archive of previous versions
- Document changes in release notes

---

## 🎯 Usage

### For Documentation
Reference screenshots in:
- README.md feature showcase
- User guides and tutorials
- Bug reports and issue tickets
- Design reviews and feedback

### For Marketing
Use high-quality screenshots for:
- Google Play Store listing
- Website and landing pages
- Social media promotions
- Press kits and media releases

### For Development
Reference screenshots for:
- Design implementation verification
- UI/UX review sessions
- QA testing validation
- Regression testing baselines

---

## 📧 Contributing

To add new screenshots:

1. Follow capture guidelines above
2. Use consistent mock data
3. Capture both light and dark themes
4. Name files according to convention
5. Place in appropriate directory
6. Update this documentation
7. Submit PR with screenshots

**Questions?** Contact Sokchea (UI Specialist)

---

**Last Reviewed:** December 9, 2025  
**Next Review:** March 9, 2026 (Quarterly)

