package com.example.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.design.VoidColors
import com.example.design.VoidDimensions
import com.example.design.VoidTypography
import com.example.ui.components.AppContextMenu
import com.example.ui.components.ClockDisplay
import com.example.ui.components.FloatingDockNavbar
import com.example.ui.components.ManageDockAppsDialog
import com.example.ui.components.ReorderFavoritesDialog
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenSearch: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val uiState by viewModel.uiState.collectAsState()

    val rowSpacingDp = when (uiState.preferences.rowSpacing) {
        "COMPACT" -> 6.dp
        "SPACIOUS" -> 20.dp
        else -> 12.dp
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VoidColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .pointerInput(Unit) {
                var totalDragX = 0f
                var totalDragY = 0f
                detectDragGestures(
                    onDragStart = {
                        totalDragX = 0f
                        totalDragY = 0f
                    },
                    onDragEnd = {
                        val absX = abs(totalDragX)
                        val absY = abs(totalDragY)
                        if (absX > absY && absX > 75f) {
                            if (totalDragX < 0) {
                                // Swiped left -> launch swipe left app
                                viewModel.launchSwipeLeft(context)
                            } else {
                                // Swiped right -> launch swipe right app
                                viewModel.launchSwipeRight(context)
                            }
                        } else if (totalDragY < -60f && absY > absX) {
                            // Swiped up -> open search / drawer
                            onOpenSearch()
                        }
                    },
                    onDragCancel = {
                        totalDragX = 0f
                        totalDragY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        totalDragX += dragAmount.x
                        totalDragY += dragAmount.y
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = VoidDimensions.SpaceExtraLarge)
        ) {
            Spacer(modifier = Modifier.height(VoidDimensions.SpaceMedium))

            // Clock & Date Section
            ClockDisplay(
                timeString = uiState.timeString,
                dateString = uiState.dateString,
                clockSize = uiState.preferences.clockSize,
                showDate = uiState.preferences.showDate,
                showSubtitle = uiState.preferences.showSubtitle,
                subtitleText = uiState.preferences.customSubtitle,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(VoidDimensions.SpaceLarge))

            // Favorites Heading & Reorder Shortcut
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "FAVORITES",
                    style = VoidTypography.labelSmall,
                    color = VoidColors.TextSecondary,
                    letterSpacing = 1.6.sp
                )

                if (uiState.favoriteApps.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.showReorderDialog() },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("home_reorder_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Reorder favorites",
                            tint = VoidColors.TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(VoidDimensions.SpaceSmall))

            // Favorites List (Text-only, no icons)
            if (uiState.favoriteApps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column {
                        Text(
                            text = "No favorites pinned.",
                            style = VoidTypography.titleLarge,
                            color = VoidColors.TextDisabled
                        )
                        Spacer(modifier = Modifier.height(VoidDimensions.SpaceSmall))
                        Text(
                            text = "Swipe up or tap below to search apps and pin your favorites.",
                            style = VoidTypography.bodyMedium,
                            color = VoidColors.TextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = VoidDimensions.SpaceSmall),
                    verticalArrangement = Arrangement.spacedBy(rowSpacingDp)
                ) {
                    itemsIndexed(uiState.favoriteApps, key = { _, app -> app.id }) { index, app ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(VoidDimensions.MinTouchTarget)
                                .combinedClickable(
                                    onClick = { viewModel.launchApp(context, app) },
                                    onLongClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.openMenu(context, app)
                                    }
                                )
                                .padding(vertical = VoidDimensions.SpaceSmall)
                                .testTag("favorite_item_${app.packageName}"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (uiState.preferences.showItemNumbers) {
                                Text(
                                    text = "${index + 1}. ",
                                    style = VoidTypography.titleLarge.copy(fontSize = 18.sp),
                                    color = VoidColors.TextSecondary,
                                    modifier = Modifier.width(32.dp)
                                )
                            }
                            Text(
                                text = app.label,
                                style = VoidTypography.titleLarge,
                                color = VoidColors.TextPrimary
                            )
                        }
                    }
                }
            }

            // Bottom Affordances: Search Bar above, Floating Dock Navbar below
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = VoidDimensions.SpaceSmall),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Search bar affordance (placed above the floating navbar)
                Surface(
                    onClick = onOpenSearch,
                    shape = RoundedCornerShape(VoidDimensions.CornerLarge),
                    color = VoidColors.Surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, VoidColors.Border),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("home_search_bar_affordance")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = VoidDimensions.SpaceLarge),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = VoidColors.TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(VoidDimensions.SpaceSmall))
                        Text(
                            text = "Search apps...",
                            style = VoidTypography.bodyMedium,
                            color = VoidColors.TextSecondary
                        )
                    }
                }

                // Floating Dock Navbar (placed at the bottom)
                if (uiState.preferences.showFloatingNavbar) {
                    Spacer(modifier = Modifier.height(VoidDimensions.SpaceMedium))
                    FloatingDockNavbar(
                        dockApps = uiState.dockApps,
                        onLaunchApp = { viewModel.launchApp(context, it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Contextual Menu
        uiState.selectedMenuApp?.let { menuApp ->
            val index = uiState.favoriteApps.indexOfFirst { it.id == menuApp.id }
            AppContextMenu(
                app = menuApp,
                isFavorite = true,
                isHomeScreenItem = true,
                canMoveUp = index > 0,
                canMoveDown = index in 0 until (uiState.favoriteApps.size - 1),
                onDismiss = { viewModel.closeMenu() },
                onLaunch = { viewModel.launchApp(context, menuApp) },
                onToggleFavorite = { viewModel.toggleFavorite(menuApp.id) },
                onMoveUp = { viewModel.moveFavoriteUp(index) },
                onMoveDown = { viewModel.moveFavoriteDown(index) },
                onAppInfo = { viewModel.openAppInfo(context, menuApp.packageName) },
                onHideApp = { viewModel.hideApp(menuApp.packageName) },
                onUninstall = { viewModel.uninstallApp(context, menuApp.packageName) }
            )
        }

        // Reorder Dialog
        if (uiState.isReorderDialogOpen) {
            ReorderFavoritesDialog(
                favoriteApps = uiState.favoriteApps,
                onMoveUp = { viewModel.moveFavoriteUp(it) },
                onMoveDown = { viewModel.moveFavoriteDown(it) },
                onRemove = { viewModel.removeFavorite(it) },
                onDismiss = { viewModel.hideReorderDialog() }
            )
        }

        // Manage Dock Apps Dialog
        if (uiState.isDockPickerOpen) {
            val allApps by viewModel.allApps.collectAsState()
            ManageDockAppsDialog(
                allApps = allApps,
                selectedAppIds = uiState.dockApps.map { it.id }.toSet(),
                onToggleApp = { viewModel.toggleAppInDock(it) },
                onDismiss = { viewModel.setDockPickerOpen(false) }
            )
        }
    }
}
