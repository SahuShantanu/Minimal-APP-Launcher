package com.example.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.design.VoidColors
import com.example.design.VoidDimensions
import com.example.design.VoidTypography
import com.example.ui.components.ManageDockAppsDialog

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val allApps by viewModel.allApps.collectAsState()
    var appPickerForGesture by remember { mutableStateOf<String?>(null) }
    var showDockAppsDialog by remember { mutableStateOf(false) }

    BackHandler {
        onBackToHome()
    }

    LaunchedEffect(Unit) {
        viewModel.refreshDefaultLauncherStatus()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VoidColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = VoidDimensions.SpaceExtraLarge)
        ) {
            Spacer(modifier = Modifier.height(VoidDimensions.SpaceMedium))

            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackToHome,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("settings_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Home",
                        tint = VoidColors.TextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(VoidDimensions.SpaceSmall))

                Text(
                    text = "SETTINGS",
                    style = VoidTypography.labelSmall,
                    color = VoidColors.TextSecondary,
                    letterSpacing = 1.6.sp
                )
            }

            Spacer(modifier = Modifier.height(VoidDimensions.SpaceMedium))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(VoidDimensions.SpaceLarge)
            ) {
                // Default Home Status Card
                item {
                    DefaultLauncherCard(
                        isDefault = uiState.isDefaultLauncher,
                        onRequestDefault = { viewModel.requestDefaultLauncher(context) }
                    )
                }

                // Section 1: Appearance
                item {
                    SettingsSection(title = "APPEARANCE") {
                        SegmentedOptionRow(
                            label = "Clock Size",
                            options = listOf("SMALL", "MEDIUM", "LARGE"),
                            selected = uiState.preferences.clockSize,
                            onSelect = { viewModel.updateClockSize(it) }
                        )

                        SettingsToggleRow(
                            label = "24-Hour Clock",
                            description = "Use 24-hour military time format",
                            checked = uiState.preferences.is24HourClock,
                            onCheckedChange = { viewModel.update24HourClock(it) }
                        )

                        SettingsToggleRow(
                            label = "Show Date",
                            description = "Display current date above clock",
                            checked = uiState.preferences.showDate,
                            onCheckedChange = { viewModel.updateShowDate(it) }
                        )

                        SegmentedOptionRow(
                            label = "Icon Style",
                            options = listOf("GLYPHS", "MONO", "COLOR"),
                            selected = when (uiState.preferences.iconStyle) {
                                "MONOCHROME" -> "MONO"
                                "ORIGINAL" -> "COLOR"
                                else -> "GLYPHS"
                            },
                            onSelect = { option ->
                                val style = when (option) {
                                    "MONO" -> "MONOCHROME"
                                    "COLOR" -> "ORIGINAL"
                                    else -> "MINIMAL_GLYPHS"
                                }
                                viewModel.updateIconStyle(style)
                            }
                        )

                        SegmentedOptionRow(
                            label = "Icon Size",
                            options = listOf("48", "56", "64"),
                            selected = uiState.preferences.iconSizeDp.toString(),
                            onSelect = { viewModel.updateIconSizeDp(it.toInt()) }
                        )

                        SettingsToggleRow(
                            label = "Show App Labels",
                            description = "Display app names under icons in drawer",
                            checked = uiState.preferences.showAppLabels,
                            onCheckedChange = { viewModel.updateShowAppLabels(it) }
                        )
                    }
                }

                // Section 2: Home Screen
                item {
                    SettingsSection(title = "HOME SCREEN") {
                        SettingsToggleRow(
                            label = "Numbered Favorites",
                            description = "Display subtle item numbers (1, 2, 3...)",
                            checked = uiState.preferences.showItemNumbers,
                            onCheckedChange = { viewModel.updateShowItemNumbers(it) }
                        )

                        SegmentedOptionRow(
                            label = "Row Spacing",
                            options = listOf("COMPACT", "NORMAL", "SPACIOUS"),
                            selected = uiState.preferences.rowSpacing,
                            onSelect = { viewModel.updateRowSpacing(it) }
                        )

                        SettingsToggleRow(
                            label = "Show Focus Subtitle",
                            description = "Muted text quote or note under clock",
                            checked = uiState.preferences.showSubtitle,
                            onCheckedChange = { viewModel.updateShowSubtitle(it) }
                        )

                        if (uiState.preferences.showSubtitle) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setShowSubtitleDialog(true) }
                                    .padding(vertical = VoidDimensions.SpaceSmall),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Subtitle Text", style = VoidTypography.bodyLarge, color = VoidColors.TextPrimary)
                                    Text(uiState.preferences.customSubtitle, style = VoidTypography.bodySmall, color = VoidColors.TextSecondary)
                                }
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = VoidColors.TextSecondary, modifier = Modifier.size(18.dp))
                            }
                        }

                        SettingsToggleRow(
                            label = "Floating Navbar (Dock)",
                            description = "Horizontal scrolling app icons dock at bottom of home",
                            checked = uiState.preferences.showFloatingNavbar,
                            onCheckedChange = { viewModel.updateShowFloatingNavbar(it) }
                        )

                        if (uiState.preferences.showFloatingNavbar) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showDockAppsDialog = true }
                                    .padding(vertical = VoidDimensions.SpaceSmall),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = VoidDimensions.SpaceSmall)) {
                                    Text("Navbar Apps", style = VoidTypography.bodyLarge, color = VoidColors.TextPrimary)
                                    val countText = if (uiState.preferences.floatingNavbarAppIds.isEmpty()) "Default essentials (Phone, Messages, Chrome...)" else "${uiState.preferences.floatingNavbarAppIds.size} apps pinned"
                                    Text(countText, style = VoidTypography.bodySmall, color = VoidColors.TextSecondary)
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(VoidColors.Focus)
                                        .clickable { showDockAppsDialog = true }
                                        .padding(horizontal = VoidDimensions.SpaceMedium, vertical = 6.dp)
                                        .testTag("settings_add_dock_app_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add app",
                                            tint = VoidColors.Background,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "ADD APPS",
                                            style = VoidTypography.labelSmall,
                                            color = VoidColors.Background
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 3: App Drawer
                item {
                    SettingsSection(title = "APP DRAWER") {
                        SegmentedOptionRow(
                            label = "Grid Columns",
                            options = listOf("3", "4", "5"),
                            selected = uiState.preferences.gridColumns.toString(),
                            onSelect = { viewModel.updateGridColumns(it.toInt()) }
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setShowHiddenAppsDialog(true) }
                                .padding(vertical = VoidDimensions.SpaceSmall),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Hidden Applications", style = VoidTypography.bodyLarge, color = VoidColors.TextPrimary)
                                Text("${uiState.hiddenApps.size} apps hidden from drawer", style = VoidTypography.bodySmall, color = VoidColors.TextSecondary)
                            }
                            Text("MANAGE", style = VoidTypography.labelSmall, color = VoidColors.Focus)
                        }
                    }
                }

                // Section 4: Behavior & Gestures
                item {
                    SettingsSection(title = "BEHAVIOR & GESTURES") {
                        SegmentedOptionRow(
                            label = "Swipe Up Action",
                            options = listOf("SEARCH", "DRAWER"),
                            selected = uiState.preferences.swipeUpAction,
                            onSelect = { viewModel.updateSwipeUpAction(it) }
                        )

                        // Swipe Left Gesture App
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { appPickerForGesture = "LEFT" }
                                .padding(vertical = VoidDimensions.SpaceSmall),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Swipe Left Shortcut", style = VoidTypography.bodyLarge, color = VoidColors.TextPrimary)
                                val currentLeft = when (uiState.preferences.swipeLeftAppId) {
                                    "DEFAULT_CAMERA" -> "Camera (Default)"
                                    "DEFAULT_PHONE" -> "Phone (Default)"
                                    "NONE" -> "Disabled"
                                    else -> allApps.firstOrNull { it.id == uiState.preferences.swipeLeftAppId || it.packageName == uiState.preferences.swipeLeftAppId }?.label ?: uiState.preferences.swipeLeftAppId
                                }
                                Text(currentLeft, style = VoidTypography.bodySmall, color = VoidColors.TextSecondary)
                            }
                            Text("CHANGE", style = VoidTypography.labelSmall, color = VoidColors.Focus)
                        }

                        // Swipe Right Gesture App
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { appPickerForGesture = "RIGHT" }
                                .padding(vertical = VoidDimensions.SpaceSmall),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Swipe Right Shortcut", style = VoidTypography.bodyLarge, color = VoidColors.TextPrimary)
                                val currentRight = when (uiState.preferences.swipeRightAppId) {
                                    "DEFAULT_CAMERA" -> "Camera (Default)"
                                    "DEFAULT_PHONE" -> "Phone (Default)"
                                    "NONE" -> "Disabled"
                                    else -> allApps.firstOrNull { it.id == uiState.preferences.swipeRightAppId || it.packageName == uiState.preferences.swipeRightAppId }?.label ?: uiState.preferences.swipeRightAppId
                                }
                                Text(currentRight, style = VoidTypography.bodySmall, color = VoidColors.TextSecondary)
                            }
                            Text("CHANGE", style = VoidTypography.labelSmall, color = VoidColors.Focus)
                        }

                        SettingsToggleRow(
                            label = "Auto-Focus Search Keyboard",
                            description = "Instantly open keyboard when entering search",
                            checked = uiState.preferences.autoFocusSearch,
                            onCheckedChange = { viewModel.updateAutoFocusSearch(it) }
                        )

                        SettingsToggleRow(
                            label = "Haptic Feedback",
                            description = "Subtle tactile click on taps and long-presses",
                            checked = uiState.preferences.hapticFeedbackEnabled,
                            onCheckedChange = { viewModel.updateHapticFeedback(it) }
                        )
                    }
                }

                // Section 5: System & Reset
                item {
                    SettingsSection(title = "SYSTEM & PRIVACY") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setShowResetDialog(true) }
                                .padding(vertical = VoidDimensions.SpaceSmall),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Reset to Defaults", style = VoidTypography.bodyLarge, color = VoidColors.Danger)
                                Text("Restores all settings while preserving favorites", style = VoidTypography.bodySmall, color = VoidColors.TextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(VoidDimensions.SpaceSmall))

                        Column(modifier = Modifier.padding(vertical = VoidDimensions.SpaceSmall)) {
                            Text("VOID Launcher v1.0", style = VoidTypography.bodyMedium, color = VoidColors.TextPrimary)
                            Text("Distraction-free, offline, zero data collection.", style = VoidTypography.bodySmall, color = VoidColors.TextDisabled)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(VoidDimensions.SpaceSection))
                }
            }
        }

        // Subtitle Dialog
        if (uiState.showSubtitleDialog) {
            var tempSubtitle by remember { mutableStateOf(uiState.preferences.customSubtitle) }
            AlertDialog(
                onDismissRequest = { viewModel.setShowSubtitleDialog(false) },
                containerColor = VoidColors.SurfaceElevated,
                title = { Text("Edit Subtitle", style = VoidTypography.titleLarge, color = VoidColors.TextPrimary) },
                text = {
                    OutlinedTextField(
                        value = tempSubtitle,
                        onValueChange = { tempSubtitle = it },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VoidColors.Focus,
                            unfocusedBorderColor = VoidColors.Border,
                            focusedTextColor = VoidColors.TextPrimary,
                            unfocusedTextColor = VoidColors.TextPrimary
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("subtitle_input")
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.updateCustomSubtitle(tempSubtitle)
                        viewModel.setShowSubtitleDialog(false)
                    }) {
                        Text("Save", color = VoidColors.Focus)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.setShowSubtitleDialog(false) }) {
                        Text("Cancel", color = VoidColors.TextSecondary)
                    }
                }
            )
        }

        // Hidden Apps Dialog
        if (uiState.showHiddenAppsDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.setShowHiddenAppsDialog(false) },
                containerColor = VoidColors.SurfaceElevated,
                title = { Text("Hidden Applications", style = VoidTypography.titleLarge, color = VoidColors.TextPrimary) },
                text = {
                    if (uiState.hiddenApps.isEmpty()) {
                        Text("No applications are hidden.", style = VoidTypography.bodyMedium, color = VoidColors.TextSecondary)
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxWidth().height(260.dp)) {
                            items(uiState.hiddenApps.size) { i ->
                                val app = uiState.hiddenApps[i]
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = VoidDimensions.SpaceSmall),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(app.label, style = VoidTypography.bodyLarge, color = VoidColors.TextPrimary, modifier = Modifier.weight(1f))
                                    IconButton(
                                        onClick = { viewModel.unhideApp(app.packageName) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Visibility, contentDescription = "Unhide", tint = VoidColors.Focus)
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.setShowHiddenAppsDialog(false) }) {
                        Text("Done", color = VoidColors.Focus)
                    }
                }
            )
        }

        // Reset Confirmation Dialog
        if (uiState.showResetDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.setShowResetDialog(false) },
                containerColor = VoidColors.SurfaceElevated,
                title = { Text("Reset All Preferences?", style = VoidTypography.titleLarge, color = VoidColors.Danger) },
                text = { Text("This will reset all styling, grid, and layout options back to their original defaults.", style = VoidTypography.bodyMedium, color = VoidColors.TextSecondary) },
                confirmButton = {
                    TextButton(onClick = { viewModel.resetToDefaults() }) {
                        Text("Reset", color = VoidColors.Danger)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.setShowResetDialog(false) }) {
                        Text("Cancel", color = VoidColors.TextSecondary)
                    }
                }
            )
        }

        // Gesture App Picker Dialog
        appPickerForGesture?.let { gesture ->
            val isLeft = gesture == "LEFT"
            val title = if (isLeft) "Swipe Left Shortcut" else "Swipe Right Shortcut"
            val currentSelected = if (isLeft) uiState.preferences.swipeLeftAppId else uiState.preferences.swipeRightAppId

            AlertDialog(
                onDismissRequest = { appPickerForGesture = null },
                containerColor = VoidColors.SurfaceElevated,
                title = { Text(title, style = VoidTypography.titleLarge, color = VoidColors.TextPrimary) },
                text = {
                    LazyColumn(modifier = Modifier.fillMaxWidth().height(320.dp)) {
                        item {
                            val defaultId = if (isLeft) "DEFAULT_CAMERA" else "DEFAULT_PHONE"
                            val defaultLabel = if (isLeft) "Camera (Default System)" else "Phone (Default System)"
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(VoidDimensions.CornerSmall))
                                    .background(if (currentSelected == defaultId) VoidColors.Surface else androidx.compose.ui.graphics.Color.Transparent)
                                    .clickable {
                                        if (isLeft) viewModel.updateSwipeLeftAppId(defaultId)
                                        else viewModel.updateSwipeRightAppId(defaultId)
                                        appPickerForGesture = null
                                    }
                                    .padding(vertical = VoidDimensions.SpaceMedium, horizontal = VoidDimensions.SpaceSmall),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(defaultLabel, style = VoidTypography.bodyLarge, color = VoidColors.Focus, modifier = Modifier.weight(1f))
                                if (currentSelected == defaultId) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = VoidColors.Focus, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(VoidDimensions.CornerSmall))
                                    .background(if (currentSelected == "NONE") VoidColors.Surface else androidx.compose.ui.graphics.Color.Transparent)
                                    .clickable {
                                        if (isLeft) viewModel.updateSwipeLeftAppId("NONE")
                                        else viewModel.updateSwipeRightAppId("NONE")
                                        appPickerForGesture = null
                                    }
                                    .padding(vertical = VoidDimensions.SpaceMedium, horizontal = VoidDimensions.SpaceSmall),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Disabled (None)", style = VoidTypography.bodyLarge, color = VoidColors.TextSecondary, modifier = Modifier.weight(1f))
                                if (currentSelected == "NONE") {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = VoidColors.Focus, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        items(allApps.size) { i ->
                            val app = allApps[i]
                            val isSelected = currentSelected == app.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(VoidDimensions.CornerSmall))
                                    .background(if (isSelected) VoidColors.Surface else androidx.compose.ui.graphics.Color.Transparent)
                                    .clickable {
                                        if (isLeft) viewModel.updateSwipeLeftAppId(app.id)
                                        else viewModel.updateSwipeRightAppId(app.id)
                                        appPickerForGesture = null
                                    }
                                    .padding(vertical = VoidDimensions.SpaceSmall, horizontal = VoidDimensions.SpaceSmall),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(app.label, style = VoidTypography.bodyLarge, color = VoidColors.TextPrimary, modifier = Modifier.weight(1f))
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = VoidColors.Focus, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { appPickerForGesture = null }) {
                        Text("Cancel", color = VoidColors.TextSecondary)
                    }
                }
            )
        }

        // Manage Floating Navbar Dock Apps Dialog
        if (showDockAppsDialog) {
            ManageDockAppsDialog(
                allApps = allApps,
                selectedAppIds = uiState.preferences.floatingNavbarAppIds.toSet(),
                onToggleApp = { viewModel.toggleAppInDock(it) },
                onDismiss = { showDockAppsDialog = false }
            )
        }
    }
}

@Composable
private fun DefaultLauncherCard(
    isDefault: Boolean,
    onRequestDefault: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(VoidDimensions.CornerMedium))
            .background(VoidColors.Surface)
            .border(1.dp, if (isDefault) VoidColors.Border else VoidColors.Focus, RoundedCornerShape(VoidDimensions.CornerMedium))
            .clickable(onClick = onRequestDefault)
            .padding(VoidDimensions.SpaceLarge)
            .testTag("default_launcher_card")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = if (isDefault) VoidColors.Focus else VoidColors.TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(VoidDimensions.SpaceMedium))
                Column {
                    Text(
                        text = if (isDefault) "Default Launcher Active" else "Set as Default Launcher",
                        style = VoidTypography.bodyLarge,
                        color = VoidColors.TextPrimary
                    )
                    Text(
                        text = if (isDefault) "VOID Launcher is your primary home" else "Tap to choose VOID as home app",
                        style = VoidTypography.bodySmall,
                        color = VoidColors.TextSecondary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(VoidDimensions.CornerSmall))
                    .background(if (isDefault) VoidColors.SurfaceElevated else VoidColors.Focus)
                    .padding(horizontal = VoidDimensions.SpaceMedium, vertical = VoidDimensions.SpaceExtraSmall)
            ) {
                Text(
                    text = if (isDefault) "ACTIVE" else "SET",
                    style = VoidTypography.labelSmall,
                    color = if (isDefault) VoidColors.TextSecondary else VoidColors.Background
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = VoidTypography.labelSmall,
            color = VoidColors.TextSecondary,
            letterSpacing = 1.6.sp,
            modifier = Modifier.padding(vertical = VoidDimensions.SpaceSmall)
        )
        HorizontalDivider(color = VoidColors.Border, thickness = 1.dp)
        Spacer(modifier = Modifier.height(VoidDimensions.SpaceSmall))
        content()
    }
}

@Composable
private fun SettingsToggleRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = VoidDimensions.SpaceSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = VoidDimensions.SpaceMedium)) {
            Text(text = label, style = VoidTypography.bodyLarge, color = VoidColors.TextPrimary)
            Text(text = description, style = VoidTypography.bodySmall, color = VoidColors.TextSecondary)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = VoidColors.Background,
                checkedTrackColor = VoidColors.Focus,
                uncheckedThumbColor = VoidColors.TextSecondary,
                uncheckedTrackColor = VoidColors.SurfaceElevated,
                uncheckedBorderColor = VoidColors.Border
            ),
            modifier = Modifier.testTag("switch_$label")
        )
    }
}

@Composable
private fun SegmentedOptionRow(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = VoidDimensions.SpaceSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = VoidTypography.bodyLarge, color = VoidColors.TextPrimary)

        Row(
            modifier = Modifier
                .background(VoidColors.Surface, RoundedCornerShape(VoidDimensions.CornerSmall))
                .border(1.dp, VoidColors.Border, RoundedCornerShape(VoidDimensions.CornerSmall))
                .padding(2.dp)
        ) {
            options.forEach { option ->
                val isSelected = option.equals(selected, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(VoidDimensions.CornerSmall))
                        .background(if (isSelected) VoidColors.Focus else VoidColors.Surface)
                        .clickable { onSelect(option) }
                        .padding(horizontal = VoidDimensions.SpaceMedium, vertical = VoidDimensions.SpaceExtraSmall)
                        .testTag("segment_${label}_$option"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        style = VoidTypography.labelSmall,
                        color = if (isSelected) VoidColors.Background else VoidColors.TextSecondary
                    )
                }
            }
        }
    }
}
