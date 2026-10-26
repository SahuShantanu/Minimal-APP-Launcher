package com.example.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.LauncherApplication
import com.example.data.LauncherApp
import com.example.data.LauncherPreferences
import com.example.system.TimeTickReceiver
import com.example.util.HapticFeedbackUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class HomeUiState(
    val timeString: String = "",
    val dateString: String = "",
    val favoriteApps: List<LauncherApp> = emptyList(),
    val dockApps: List<LauncherApp> = emptyList(),
    val preferences: LauncherPreferences = LauncherPreferences(),
    val selectedMenuApp: LauncherApp? = null,
    val isReorderDialogOpen: Boolean = false,
    val isDockPickerOpen: Boolean = false
)

private data class HomeDialogState(
    val menuApp: LauncherApp?,
    val reorderOpen: Boolean,
    val dockPickerOpen: Boolean
)

class HomeViewModel : ViewModel() {

    private val app = LauncherApplication.instance
    private val settingsRepo = app.settingsRepository
    private val appRepo = app.appRepository
    private val reconcileUseCase = app.reconcileFavoritesUseCase
    private val launchManager = app.appLaunchManager
    private val timeTickReceiver = TimeTickReceiver()

    val allApps: StateFlow<List<LauncherApp>> = appRepo.appsFlow

    private val _selectedMenuApp = MutableStateFlow<LauncherApp?>(null)
    private val _isReorderDialogOpen = MutableStateFlow(false)
    private val _isDockPickerOpen = MutableStateFlow(false)
    private val _currentTime = MutableStateFlow(Calendar.getInstance())

    init {
        viewModelScope.launch {
            timeTickReceiver.timeFlow(app.applicationContext).collect { cal ->
                _currentTime.value = cal
            }
        }
    }

    private val dialogsFlow = combine(
        _selectedMenuApp,
        _isReorderDialogOpen,
        _isDockPickerOpen
    ) { menuApp, reorderOpen, dockPickerOpen ->
        HomeDialogState(menuApp, reorderOpen, dockPickerOpen)
    }

    val uiState: StateFlow<HomeUiState> = combine(
        settingsRepo.preferencesFlow,
        appRepo.appsFlow,
        _currentTime,
        dialogsFlow
    ) { prefs, apps, time, dialogs ->
        val timeFormat = if (prefs.is24HourClock) {
            SimpleDateFormat("HH:mm", Locale.getDefault())
        } else {
            SimpleDateFormat("h:mm a", Locale.getDefault())
        }
        val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())

        val reconciledFavorites = reconcileUseCase(prefs.favoriteIds, apps)

        // Floating dock apps: if empty, suggest default core communication & utility apps
        val dockAppIds = if (prefs.floatingNavbarAppIds.isEmpty()) {
            reconcileUseCase.getSuggestedFavorites(apps)
        } else {
            prefs.floatingNavbarAppIds
        }
        val reconciledDock = reconcileUseCase(dockAppIds, apps)

        HomeUiState(
            timeString = timeFormat.format(time.time),
            dateString = dateFormat.format(time.time),
            favoriteApps = reconciledFavorites,
            dockApps = reconciledDock,
            preferences = prefs,
            selectedMenuApp = dialogs.menuApp,
            isReorderDialogOpen = dialogs.reorderOpen,
            isDockPickerOpen = dialogs.dockPickerOpen
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun launchApp(context: Context, targetApp: LauncherApp) {
        val haptics = uiState.value.preferences.hapticFeedbackEnabled
        HapticFeedbackUtil.performClick(context, haptics)
        launchManager.launchApp(context, targetApp)
    }

    fun launchSwipeLeft(context: Context) {
        val haptics = uiState.value.preferences.hapticFeedbackEnabled
        HapticFeedbackUtil.performClick(context, haptics)
        val allApps = appRepo.appsFlow.value
        launchManager.launchAppById(context, uiState.value.preferences.swipeLeftAppId, allApps)
    }

    fun launchSwipeRight(context: Context) {
        val haptics = uiState.value.preferences.hapticFeedbackEnabled
        HapticFeedbackUtil.performClick(context, haptics)
        val allApps = appRepo.appsFlow.value
        launchManager.launchAppById(context, uiState.value.preferences.swipeRightAppId, allApps)
    }

    fun openMenu(context: Context, targetApp: LauncherApp) {
        val haptics = uiState.value.preferences.hapticFeedbackEnabled
        HapticFeedbackUtil.performClick(context, haptics)
        _selectedMenuApp.value = targetApp
    }

    fun closeMenu() {
        _selectedMenuApp.value = null
    }

    fun toggleFavorite(appId: String) {
        viewModelScope.launch {
            val current = uiState.value.preferences.favoriteIds
            if (current.contains(appId)) {
                settingsRepo.removeFavorite(appId)
            } else {
                settingsRepo.addFavorite(appId)
            }
        }
    }

    fun moveFavoriteUp(index: Int) {
        if (index > 0) {
            viewModelScope.launch {
                settingsRepo.reorderFavorites(index, index - 1)
            }
        }
    }

    fun moveFavoriteDown(index: Int) {
        val maxIndex = uiState.value.favoriteApps.size - 1
        if (index < maxIndex) {
            viewModelScope.launch {
                settingsRepo.reorderFavorites(index, index + 1)
            }
        }
    }

    fun removeFavorite(appId: String) {
        viewModelScope.launch {
            settingsRepo.removeFavorite(appId)
        }
    }

    fun openAppInfo(context: Context, packageName: String) {
        launchManager.openAppInfo(context, packageName)
    }

    fun hideApp(packageName: String) {
        viewModelScope.launch {
            settingsRepo.hideApp(packageName)
        }
    }

    fun uninstallApp(context: Context, packageName: String) {
        launchManager.requestUninstall(context, packageName)
    }

    fun showReorderDialog() {
        _isReorderDialogOpen.value = true
    }

    fun hideReorderDialog() {
        _isReorderDialogOpen.value = false
    }

    fun setDockPickerOpen(open: Boolean) {
        _isDockPickerOpen.value = open
    }

    fun toggleAppInDock(appId: String) {
        viewModelScope.launch {
            val current = uiState.value.preferences.floatingNavbarAppIds.ifEmpty {
                uiState.value.dockApps.map { it.id }
            }
            if (current.contains(appId)) {
                settingsRepo.removeFloatingNavbarApp(appId)
            } else {
                settingsRepo.addFloatingNavbarApp(appId)
            }
        }
    }
}
