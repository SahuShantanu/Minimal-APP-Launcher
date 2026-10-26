package com.example.domain

import com.example.data.LauncherApp

class ReconcileFavoritesUseCase {

    operator fun invoke(
        favoriteIds: List<String>,
        availableApps: List<LauncherApp>
    ): List<LauncherApp> {
        val appMap = availableApps.associateBy { it.id }
        return favoriteIds.mapNotNull { id -> appMap[id] }
    }

    fun getSuggestedFavorites(availableApps: List<LauncherApp>): List<String> {
        val preferredKeywords = listOf("phone", "dialer", "message", "sms", "browser", "chrome", "camera", "settings")
        val matchedIds = mutableListOf<String>()

        for (keyword in preferredKeywords) {
            val match = availableApps.firstOrNull { app ->
                !matchedIds.contains(app.id) &&
                    (app.packageName.lowercase().contains(keyword) || app.normalizedLabel.contains(keyword))
            }
            if (match != null) {
                matchedIds.add(match.id)
            }
            if (matchedIds.size >= 5) break
        }

        // Fallback: If fewer than 4 matched, take the first few available apps
        if (matchedIds.size < 4) {
            for (app in availableApps) {
                if (!matchedIds.contains(app.id)) {
                    matchedIds.add(app.id)
                }
                if (matchedIds.size >= 4) break
            }
        }
        return matchedIds
    }
}
