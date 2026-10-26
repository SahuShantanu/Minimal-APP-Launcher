package com.example.ui.drawer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.LauncherApplication
import com.example.data.IconRepository
import com.example.data.LauncherApp
import com.example.data.LauncherPreferences
import com.example.util.HapticFeedbackUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AppDrawerUiState(
    val searchQuery: String = "",
    val filteredApps: List<LauncherApp> = emptyList(),
    val totalAppCount: Int = 0,
    val preferences: LauncherPreferences = LauncherPreferences(),
    val selectedMenuApp: LauncherApp? = null
)

class AppDrawerViewModel : ViewModel() {

    private val app = LauncherApplication.instance
    private val settingsRepo = app.settingsRepository
    private val appRepo = app.appRepository
    val iconRepository: IconRepository = app.iconRepository
    private val searchUseCase = app.searchAppsUseCase
    private val sortUseCase = app.sortAppsUseCase
    private val launchManager = app.appLaunchManager

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedMenuApp = MutableStateFlow<LauncherApp?>(null)
    val selectedMenuApp: StateFlow<LauncherApp?> = _selectedMenuApp.asStateFlow()

    val uiState: StateFlow<AppDrawerUiState> = combine(
        _searchQuery,
        appRepo.appsFlow,
        settingsRepo.preferencesFlow,
        _selectedMenuApp
    ) { query, allApps, prefs, menuApp ->
        val visibleApps = allApps.filterNot { prefs.hiddenPackageNames.contains(it.packageName) }
        val filtered = if (query.isBlank()) {
            sortUseCase(visibleApps)
        } else {
            searchUseCase(visibleApps, query)
        }

        AppDrawerUiState(
            searchQuery = query,
            filteredApps = filtered,
            totalAppCount = visibleApps.size,
            preferences = prefs,
            selectedMenuApp = menuApp
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppDrawerUiState()
    )

    fun onQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun launchApp(context: Context, targetApp: LauncherApp) {
        val haptics = uiState.value.preferences.hapticFeedbackEnabled
        HapticFeedbackUtil.performClick(context, haptics)
        launchManager.launchApp(context, targetApp)
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

    fun hideApp(packageName: String) {
        viewModelScope.launch {
            settingsRepo.hideApp(packageName)
        }
    }

    fun openAppInfo(context: Context, packageName: String) {
        launchManager.openAppInfo(context, packageName)
    }

    fun uninstallApp(context: Context, packageName: String) {
        launchManager.requestUninstall(context, packageName)
    }
}
