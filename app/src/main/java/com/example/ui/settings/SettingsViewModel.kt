package com.example.ui.settings

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.LauncherApplication
import com.example.data.LauncherApp
import com.example.data.LauncherPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val preferences: LauncherPreferences = LauncherPreferences(),
    val isDefaultLauncher: Boolean = false,
    val hiddenApps: List<LauncherApp> = emptyList(),
    val showResetDialog: Boolean = false,
    val showHiddenAppsDialog: Boolean = false,
    val showSubtitleDialog: Boolean = false
)

class SettingsViewModel : ViewModel() {

    private val app = LauncherApplication.instance
    private val settingsRepo = app.settingsRepository
    private val appRepo = app.appRepository
    private val homeRoleHandler = app.homeRoleHandler

    private val _isDefaultLauncher = MutableStateFlow(homeRoleHandler.isDefaultLauncher())
    private val _showResetDialog = MutableStateFlow(false)
    private val _showHiddenAppsDialog = MutableStateFlow(false)
    private val _showSubtitleDialog = MutableStateFlow(false)

    private val dialogsFlow = combine(
        _showResetDialog,
        _showHiddenAppsDialog,
        _showSubtitleDialog
    ) { resetDlg, hiddenDlg, subDlg ->
        Triple(resetDlg, hiddenDlg, subDlg)
    }

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepo.preferencesFlow,
        appRepo.appsFlow,
        _isDefaultLauncher,
        dialogsFlow
    ) { prefs, allApps, isDefault, dialogs ->
        val (resetDlg, hiddenDlg, subDlg) = dialogs
        val hiddenList = allApps.filter { prefs.hiddenPackageNames.contains(it.packageName) }
        SettingsUiState(
            preferences = prefs,
            isDefaultLauncher = isDefault,
            hiddenApps = hiddenList,
            showResetDialog = resetDlg,
            showHiddenAppsDialog = hiddenDlg,
            showSubtitleDialog = subDlg
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun refreshDefaultLauncherStatus() {
        _isDefaultLauncher.value = homeRoleHandler.isDefaultLauncher()
    }

    fun requestDefaultLauncher(context: Context) {
        homeRoleHandler.openDefaultLauncherSettings(context)
        refreshDefaultLauncherStatus()
    }

    fun updateClockSize(size: String) = viewModelScope.launch {
        settingsRepo.updateClockSize(size)
    }

    fun update24HourClock(enabled: Boolean) = viewModelScope.launch {
        settingsRepo.update24HourClock(enabled)
    }

    fun updateShowDate(show: Boolean) = viewModelScope.launch {
        settingsRepo.updateShowDate(show)
    }

    fun updateShowAppLabels(show: Boolean) = viewModelScope.launch {
        settingsRepo.updateShowAppLabels(show)
    }

    fun updateIconSizeDp(sizeDp: Int) = viewModelScope.launch {
        settingsRepo.updateIconSizeDp(sizeDp)
    }

    fun updateGridColumns(columns: Int) = viewModelScope.launch {
        settingsRepo.updateGridColumns(columns)
    }

    fun updateMonochromeIcons(monochrome: Boolean) = viewModelScope.launch {
        settingsRepo.updateMonochromeIcons(monochrome)
    }

    fun updateIconStyle(style: String) = viewModelScope.launch {
        settingsRepo.updateIconStyle(style)
    }

    fun updateShowItemNumbers(show: Boolean) = viewModelScope.launch {
        settingsRepo.updateShowItemNumbers(show)
    }

    fun updateShowSubtitle(show: Boolean) = viewModelScope.launch {
        settingsRepo.updateShowSubtitle(show)
    }

    fun updateCustomSubtitle(text: String) = viewModelScope.launch {
        settingsRepo.updateCustomSubtitle(text)
    }

    fun updateRowSpacing(spacing: String) = viewModelScope.launch {
        settingsRepo.updateRowSpacing(spacing)
    }

    fun updateHapticFeedback(enabled: Boolean) = viewModelScope.launch {
        settingsRepo.updateHapticFeedback(enabled)
    }

    fun updateAutoFocusSearch(enabled: Boolean) = viewModelScope.launch {
        settingsRepo.updateAutoFocusSearch(enabled)
    }

    val allApps: StateFlow<List<LauncherApp>> = appRepo.appsFlow

    fun updateSwipeUpAction(action: String) = viewModelScope.launch {
        settingsRepo.updateSwipeUpAction(action)
    }

    fun updateSwipeLeftAppId(appId: String) = viewModelScope.launch {
        settingsRepo.updateSwipeLeftAppId(appId)
    }

    fun updateSwipeRightAppId(appId: String) = viewModelScope.launch {
        settingsRepo.updateSwipeRightAppId(appId)
    }

    fun updateShowFloatingNavbar(show: Boolean) = viewModelScope.launch {
        settingsRepo.updateShowFloatingNavbar(show)
    }

    fun toggleAppInDock(appId: String) = viewModelScope.launch {
        val current = uiState.value.preferences.floatingNavbarAppIds
        if (current.contains(appId)) {
            settingsRepo.removeFloatingNavbarApp(appId)
        } else {
            settingsRepo.addFloatingNavbarApp(appId)
        }
    }

    fun unhideApp(packageName: String) = viewModelScope.launch {
        settingsRepo.unhideApp(packageName)
    }

    fun setShowResetDialog(show: Boolean) {
        _showResetDialog.value = show
    }

    fun setShowHiddenAppsDialog(show: Boolean) {
        _showHiddenAppsDialog.value = show
    }

    fun setShowSubtitleDialog(show: Boolean) {
        _showSubtitleDialog.value = show
    }

    fun resetToDefaults() = viewModelScope.launch {
        settingsRepo.resetDefaults()
        _showResetDialog.value = false
    }
}
