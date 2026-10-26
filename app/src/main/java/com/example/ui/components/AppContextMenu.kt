package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.LauncherApp
import com.example.design.VoidColors
import com.example.design.VoidDimensions
import com.example.design.VoidTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContextMenu(
    app: LauncherApp,
    isFavorite: Boolean,
    isHomeScreenItem: Boolean = false,
    canMoveUp: Boolean = false,
    canMoveDown: Boolean = false,
    onDismiss: () -> Unit,
    onLaunch: () -> Unit,
    onToggleFavorite: () -> Unit,
    onMoveUp: (() -> Unit)? = null,
    onMoveDown: (() -> Unit)? = null,
    onAppInfo: () -> Unit,
    onHideApp: () -> Unit,
    onUninstall: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = VoidColors.SurfaceElevated,
        contentColor = VoidColors.TextPrimary,
        shape = RoundedCornerShape(topStart = VoidDimensions.CornerLarge, topEnd = VoidDimensions.CornerLarge)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = VoidDimensions.SpaceSection)
        ) {
            // App Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = VoidDimensions.SpaceExtraLarge, vertical = VoidDimensions.SpaceMedium)
            ) {
                Text(
                    text = app.label,
                    style = VoidTypography.titleLarge,
                    color = VoidColors.TextPrimary
                )
                Text(
                    text = app.packageName,
                    style = VoidTypography.bodySmall,
                    color = VoidColors.TextSecondary
                )
            }

            HorizontalDivider(color = VoidColors.Border, thickness = 1.dp)

            MenuItem(
                icon = Icons.Default.PlayArrow,
                label = "Open App",
                testTag = "menu_open_app",
                onClick = {
                    onDismiss()
                    onLaunch()
                }
            )

            MenuItem(
                icon = if (isFavorite) Icons.Default.StarBorder else Icons.Default.Star,
                label = if (isFavorite) "Remove from Favorites" else "Add to Favorites",
                testTag = "menu_toggle_favorite",
                onClick = {
                    onDismiss()
                    onToggleFavorite()
                }
            )

            if (isHomeScreenItem) {
                if (canMoveUp && onMoveUp != null) {
                    MenuItem(
                        icon = Icons.Default.KeyboardArrowUp,
                        label = "Move Up in Favorites",
                        testTag = "menu_move_up",
                        onClick = {
                            onDismiss()
                            onMoveUp()
                        }
                    )
                }
                if (canMoveDown && onMoveDown != null) {
                    MenuItem(
                        icon = Icons.Default.KeyboardArrowDown,
                        label = "Move Down in Favorites",
                        testTag = "menu_move_down",
                        onClick = {
                            onDismiss()
                            onMoveDown()
                        }
                    )
                }
            }

            MenuItem(
                icon = Icons.Default.Info,
                label = "App Info",
                testTag = "menu_app_info",
                onClick = {
                    onDismiss()
                    onAppInfo()
                }
            )

            MenuItem(
                icon = Icons.Default.VisibilityOff,
                label = "Hide from Drawer",
                testTag = "menu_hide_app",
                onClick = {
                    onDismiss()
                    onHideApp()
                }
            )

            if (!app.isSystemApp) {
                MenuItem(
                    icon = Icons.Default.Delete,
                    label = "Uninstall",
                    testTag = "menu_uninstall_app",
                    isDestructive = true,
                    onClick = {
                        onDismiss()
                        onUninstall()
                    }
                )
            }
        }
    }
}

@Composable
private fun MenuItem(
    icon: ImageVector,
    label: String,
    testTag: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(VoidDimensions.MinTouchTarget)
            .clickable(onClick = onClick)
            .padding(horizontal = VoidDimensions.SpaceExtraLarge)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isDestructive) VoidColors.Danger else VoidColors.TextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(VoidDimensions.SpaceLarge))
        Text(
            text = label,
            style = VoidTypography.bodyLarge,
            color = if (isDestructive) VoidColors.Danger else VoidColors.TextPrimary
        )
    }
}
