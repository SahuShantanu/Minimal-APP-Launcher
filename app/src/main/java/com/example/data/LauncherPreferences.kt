package com.example.data

data class LauncherPreferences(
    val favoriteIds: List<String> = emptyList(),
    val hiddenPackageNames: Set<String> = emptySet(),
    val showDate: Boolean = true,
    val is24HourClock: Boolean = false,
    val clockSize: String = "LARGE",
    val showAppLabels: Boolean = true,
    val iconSizeDp: Int = 56,
    val gridColumns: Int = 4,
    val monochromeIcons: Boolean = true,
    val iconStyle: String = "MINIMAL_GLYPHS",
    val showItemNumbers: Boolean = false,
    val showSubtitle: Boolean = false,
    val customSubtitle: String = "Quiet mind, focused day.",
    val rowSpacing: String = "NORMAL",
    val hapticFeedbackEnabled: Boolean = true,
    val autoFocusSearch: Boolean = true,
    val swipeUpAction: String = "SEARCH",
    val swipeLeftAppId: String = "DEFAULT_CAMERA",
    val swipeRightAppId: String = "DEFAULT_PHONE",
    val showFloatingNavbar: Boolean = true,
    val floatingNavbarAppIds: List<String> = emptyList(),
    val onboardingCompleted: Boolean = false
)
