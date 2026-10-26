package com.example.design

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val VoidDarkColorScheme = darkColorScheme(
    primary = VoidColors.Focus,
    onPrimary = VoidColors.Background,
    primaryContainer = VoidColors.SurfaceElevated,
    onPrimaryContainer = VoidColors.TextPrimary,
    secondary = VoidColors.TextSecondary,
    onSecondary = VoidColors.Background,
    background = VoidColors.Background,
    onBackground = VoidColors.TextPrimary,
    surface = VoidColors.Surface,
    onSurface = VoidColors.TextPrimary,
    surfaceVariant = VoidColors.SurfaceElevated,
    onSurfaceVariant = VoidColors.TextSecondary,
    outline = VoidColors.Border,
    outlineVariant = VoidColors.Border,
    error = VoidColors.Danger,
    onError = VoidColors.Background
)

@Composable
fun VoidLauncherTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = VoidDarkColorScheme,
        typography = VoidTypography,
        content = content
    )
}
