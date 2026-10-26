package com.example.ui.drawer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.design.VoidColors
import com.example.design.VoidDimensions
import com.example.design.VoidTypography
import com.example.ui.components.AppContextMenu
import com.example.ui.components.SearchField

@Composable
fun AppDrawerScreen(
    viewModel: AppDrawerViewModel,
    onBackToHome: () -> Unit,
    onOpenSettings: () -> Unit = {},
    autoFocusSearch: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    BackHandler {
        if (uiState.searchQuery.isNotEmpty()) {
            viewModel.clearSearch()
        } else {
            onBackToHome()
        }
    }

    LaunchedEffect(autoFocusSearch) {
        if (autoFocusSearch && uiState.preferences.autoFocusSearch) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
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

            // Header Bar matching user reference: "Applications"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackToHome,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("drawer_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Home",
                        tint = VoidColors.TextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(VoidDimensions.SpaceSmall))

                Text(
                    text = if (uiState.searchQuery.isEmpty()) "Applications" else "Search Results",
                    style = VoidTypography.headlineMedium.copy(fontSize = 24.sp),
                    color = VoidColors.TextPrimary,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "${uiState.filteredApps.size}",
                    style = VoidTypography.labelSmall,
                    color = VoidColors.TextDisabled
                )

                Spacer(modifier = Modifier.width(VoidDimensions.SpaceSmall))

                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("drawer_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = VoidColors.TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(VoidDimensions.SpaceMedium))

            // Search Field with "Find an application" placeholder
            SearchField(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.onQueryChange(it) },
                focusRequester = focusRequester,
                placeholder = "Find an application",
                modifier = Modifier.fillMaxWidth(),
                onSearchAction = {
                    keyboardController?.hide()
                    val topMatch = uiState.filteredApps.firstOrNull()
                    if (topMatch != null && uiState.filteredApps.size == 1) {
                        viewModel.launchApp(context, topMatch)
                    }
                }
            )

            Spacer(modifier = Modifier.height(VoidDimensions.SpaceLarge))

            // Apps Grid
            if (uiState.filteredApps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No applications found",
                            style = VoidTypography.titleMedium,
                            color = VoidColors.TextDisabled
                        )
                        Spacer(modifier = Modifier.height(VoidDimensions.SpaceExtraSmall))
                        Text(
                            text = "No matches for \"${uiState.searchQuery}\"",
                            style = VoidTypography.bodySmall,
                            color = VoidColors.TextSecondary
                        )
                    }
                }
            } else {
                val columns = uiState.preferences.gridColumns.coerceIn(3, 6)

                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("apps_grid"),
                    contentPadding = PaddingValues(bottom = VoidDimensions.SpaceExtraLarge),
                    verticalArrangement = Arrangement.spacedBy(VoidDimensions.SpaceLarge),
                    horizontalArrangement = Arrangement.spacedBy(VoidDimensions.SpaceMedium)
                ) {
                    items(uiState.filteredApps, key = { it.id }) { app ->
                        AppGridItem(
                            app = app,
                            iconRepository = viewModel.iconRepository,
                            iconStyle = uiState.preferences.iconStyle,
                            iconSizeDp = uiState.preferences.iconSizeDp,
                            showLabels = uiState.preferences.showAppLabels,
                            onClick = { viewModel.launchApp(context, app) },
                            onLongClick = { viewModel.openMenu(context, app) }
                        )
                    }
                }
            }
        }

        // Context Menu
        uiState.selectedMenuApp?.let { menuApp ->
            val isFavorite = uiState.preferences.favoriteIds.contains(menuApp.id)
            AppContextMenu(
                app = menuApp,
                isFavorite = isFavorite,
                isHomeScreenItem = false,
                onDismiss = { viewModel.closeMenu() },
                onLaunch = { viewModel.launchApp(context, menuApp) },
                onToggleFavorite = { viewModel.toggleFavorite(menuApp.id) },
                onAppInfo = { viewModel.openAppInfo(context, menuApp.packageName) },
                onHideApp = { viewModel.hideApp(menuApp.packageName) },
                onUninstall = { viewModel.uninstallApp(context, menuApp.packageName) }
            )
        }
    }
}
