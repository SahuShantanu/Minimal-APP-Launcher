package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "void_launcher_prefs")

class SettingsRepository(private val context: Context) {

    private object PreferencesKeys {
        val FAVORITE_IDS = stringPreferencesKey("favorite_ids_csv")
        val HIDDEN_PACKAGES = stringSetPreferencesKey("hidden_packages")
        val SHOW_DATE = booleanPreferencesKey("show_date")
        val IS_24_HOUR = booleanPreferencesKey("is_24_hour")
        val CLOCK_SIZE = stringPreferencesKey("clock_size")
        val SHOW_APP_LABELS = booleanPreferencesKey("show_app_labels")
        val ICON_SIZE_DP = intPreferencesKey("icon_size_dp")
        val GRID_COLUMNS = intPreferencesKey("grid_columns")
        val MONOCHROME_ICONS = booleanPreferencesKey("monochrome_icons")
        val ICON_STYLE = stringPreferencesKey("icon_style")
        val SHOW_ITEM_NUMBERS = booleanPreferencesKey("show_item_numbers")
        val SHOW_SUBTITLE = booleanPreferencesKey("show_subtitle")
        val CUSTOM_SUBTITLE = stringPreferencesKey("custom_subtitle")
        val ROW_SPACING = stringPreferencesKey("row_spacing")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val AUTO_FOCUS_SEARCH = booleanPreferencesKey("auto_focus_search")
        val SWIPE_UP_ACTION = stringPreferencesKey("swipe_up_action")
        val SWIPE_LEFT_APP = stringPreferencesKey("swipe_left_app")
        val SWIPE_RIGHT_APP = stringPreferencesKey("swipe_right_app")
        val SHOW_FLOATING_NAVBAR = booleanPreferencesKey("show_floating_navbar")
        val FLOATING_NAVBAR_APPS = stringPreferencesKey("floating_navbar_apps_csv")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    val preferencesFlow: Flow<LauncherPreferences> = context.dataStore.data.map { prefs ->
        val favoritesCsv = prefs[PreferencesKeys.FAVORITE_IDS] ?: ""
        val favoriteIds = if (favoritesCsv.isBlank()) {
            emptyList()
        } else {
            favoritesCsv.split("\n").filter { it.isNotBlank() }
        }

        val dockCsv = prefs[PreferencesKeys.FLOATING_NAVBAR_APPS] ?: ""
        val dockAppIds = if (dockCsv.isBlank()) {
            emptyList()
        } else {
            dockCsv.split("\n").filter { it.isNotBlank() }
        }

        LauncherPreferences(
            favoriteIds = favoriteIds,
            hiddenPackageNames = prefs[PreferencesKeys.HIDDEN_PACKAGES] ?: emptySet(),
            showDate = prefs[PreferencesKeys.SHOW_DATE] ?: true,
            is24HourClock = prefs[PreferencesKeys.IS_24_HOUR] ?: false,
            clockSize = prefs[PreferencesKeys.CLOCK_SIZE] ?: "LARGE",
            showAppLabels = prefs[PreferencesKeys.SHOW_APP_LABELS] ?: true,
            iconSizeDp = prefs[PreferencesKeys.ICON_SIZE_DP] ?: 56,
            gridColumns = prefs[PreferencesKeys.GRID_COLUMNS] ?: 4,
            monochromeIcons = prefs[PreferencesKeys.MONOCHROME_ICONS] ?: true,
            iconStyle = prefs[PreferencesKeys.ICON_STYLE] ?: "MINIMAL_GLYPHS",
            showItemNumbers = prefs[PreferencesKeys.SHOW_ITEM_NUMBERS] ?: false,
            showSubtitle = prefs[PreferencesKeys.SHOW_SUBTITLE] ?: false,
            customSubtitle = prefs[PreferencesKeys.CUSTOM_SUBTITLE] ?: "Quiet mind, focused day.",
            rowSpacing = prefs[PreferencesKeys.ROW_SPACING] ?: "NORMAL",
            hapticFeedbackEnabled = prefs[PreferencesKeys.HAPTIC_FEEDBACK] ?: true,
            autoFocusSearch = prefs[PreferencesKeys.AUTO_FOCUS_SEARCH] ?: true,
            swipeUpAction = prefs[PreferencesKeys.SWIPE_UP_ACTION] ?: "SEARCH",
            swipeLeftAppId = prefs[PreferencesKeys.SWIPE_LEFT_APP] ?: "DEFAULT_CAMERA",
            swipeRightAppId = prefs[PreferencesKeys.SWIPE_RIGHT_APP] ?: "DEFAULT_PHONE",
            showFloatingNavbar = prefs[PreferencesKeys.SHOW_FLOATING_NAVBAR] ?: true,
            floatingNavbarAppIds = dockAppIds,
            onboardingCompleted = prefs[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
        )
    }

    suspend fun setFavorites(favoriteIds: List<String>) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.FAVORITE_IDS] = favoriteIds.distinct().joinToString("\n")
        }
    }

    suspend fun addFavorite(appId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[PreferencesKeys.FAVORITE_IDS]?.split("\n")?.filter { it.isNotBlank() } ?: emptyList()
            if (!current.contains(appId)) {
                val updated = current + appId
                prefs[PreferencesKeys.FAVORITE_IDS] = updated.joinToString("\n")
            }
        }
    }

    suspend fun removeFavorite(appId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[PreferencesKeys.FAVORITE_IDS]?.split("\n")?.filter { it.isNotBlank() } ?: emptyList()
            val updated = current.filter { it != appId }
            prefs[PreferencesKeys.FAVORITE_IDS] = updated.joinToString("\n")
        }
    }

    suspend fun reorderFavorites(fromIndex: Int, toIndex: Int) {
        context.dataStore.edit { prefs ->
            val current = (prefs[PreferencesKeys.FAVORITE_IDS]?.split("\n")?.filter { it.isNotBlank() } ?: emptyList()).toMutableList()
            if (fromIndex in current.indices && toIndex in current.indices && fromIndex != toIndex) {
                val item = current.removeAt(fromIndex)
                current.add(toIndex, item)
                prefs[PreferencesKeys.FAVORITE_IDS] = current.joinToString("\n")
            }
        }
    }

    suspend fun hideApp(packageName: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[PreferencesKeys.HIDDEN_PACKAGES] ?: emptySet()
            prefs[PreferencesKeys.HIDDEN_PACKAGES] = current + packageName
        }
    }

    suspend fun unhideApp(packageName: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[PreferencesKeys.HIDDEN_PACKAGES] ?: emptySet()
            prefs[PreferencesKeys.HIDDEN_PACKAGES] = current - packageName
        }
    }

    suspend fun updateClockSize(size: String) {
        context.dataStore.edit { it[PreferencesKeys.CLOCK_SIZE] = size }
    }

    suspend fun update24HourClock(is24Hour: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.IS_24_HOUR] = is24Hour }
    }

    suspend fun updateShowDate(showDate: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SHOW_DATE] = showDate }
    }

    suspend fun updateShowAppLabels(showLabels: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SHOW_APP_LABELS] = showLabels }
    }

    suspend fun updateIconSizeDp(sizeDp: Int) {
        context.dataStore.edit { it[PreferencesKeys.ICON_SIZE_DP] = sizeDp }
    }

    suspend fun updateGridColumns(columns: Int) {
        context.dataStore.edit { it[PreferencesKeys.GRID_COLUMNS] = columns }
    }

    suspend fun updateMonochromeIcons(monochrome: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.MONOCHROME_ICONS] = monochrome }
    }

    suspend fun updateIconStyle(style: String) {
        context.dataStore.edit { it[PreferencesKeys.ICON_STYLE] = style }
    }

    suspend fun updateShowItemNumbers(showNumbers: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SHOW_ITEM_NUMBERS] = showNumbers }
    }

    suspend fun updateShowSubtitle(showSubtitle: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SHOW_SUBTITLE] = showSubtitle }
    }

    suspend fun updateCustomSubtitle(text: String) {
        context.dataStore.edit { it[PreferencesKeys.CUSTOM_SUBTITLE] = text }
    }

    suspend fun updateRowSpacing(spacing: String) {
        context.dataStore.edit { it[PreferencesKeys.ROW_SPACING] = spacing }
    }

    suspend fun updateHapticFeedback(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.HAPTIC_FEEDBACK] = enabled }
    }

    suspend fun updateAutoFocusSearch(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.AUTO_FOCUS_SEARCH] = enabled }
    }

    suspend fun updateSwipeUpAction(action: String) {
        context.dataStore.edit { it[PreferencesKeys.SWIPE_UP_ACTION] = action }
    }

    suspend fun updateSwipeLeftAppId(appId: String) {
        context.dataStore.edit { it[PreferencesKeys.SWIPE_LEFT_APP] = appId }
    }

    suspend fun updateSwipeRightAppId(appId: String) {
        context.dataStore.edit { it[PreferencesKeys.SWIPE_RIGHT_APP] = appId }
    }

    suspend fun updateShowFloatingNavbar(show: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SHOW_FLOATING_NAVBAR] = show }
    }

    suspend fun setFloatingNavbarAppIds(appIds: List<String>) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.FLOATING_NAVBAR_APPS] = appIds.distinct().joinToString("\n")
        }
    }

    suspend fun addFloatingNavbarApp(appId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[PreferencesKeys.FLOATING_NAVBAR_APPS]?.split("\n")?.filter { it.isNotBlank() } ?: emptyList()
            if (!current.contains(appId)) {
                val updated = current + appId
                prefs[PreferencesKeys.FLOATING_NAVBAR_APPS] = updated.joinToString("\n")
            }
        }
    }

    suspend fun removeFloatingNavbarApp(appId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[PreferencesKeys.FLOATING_NAVBAR_APPS]?.split("\n")?.filter { it.isNotBlank() } ?: emptyList()
            val updated = current.filter { it != appId }
            prefs[PreferencesKeys.FLOATING_NAVBAR_APPS] = updated.joinToString("\n")
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.ONBOARDING_COMPLETED] = completed }
    }

    suspend fun resetDefaults() {
        context.dataStore.edit { prefs ->
            val favorites = prefs[PreferencesKeys.FAVORITE_IDS]
            prefs.clear()
            if (favorites != null) {
                prefs[PreferencesKeys.FAVORITE_IDS] = favorites
            }
            prefs[PreferencesKeys.ONBOARDING_COMPLETED] = true
        }
    }
}
