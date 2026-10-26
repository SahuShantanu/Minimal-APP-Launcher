package com.example

import com.example.data.LauncherApp
import com.example.domain.ReconcileFavoritesUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReconcileFavoritesUseCaseTest {

    private val reconcileUseCase = ReconcileFavoritesUseCase()

    @Test
    fun `reconcile removes apps that are no longer installed`() {
        val installed = listOf(
            LauncherApp("com.android.chrome", "MainActivity", "Chrome", false),
            LauncherApp("com.android.camera", "CameraActivity", "Camera", true)
        )

        val savedFavorites = listOf(
            "com.android.chrome/MainActivity",
            "com.uninstalled.app/OldActivity",
            "com.android.camera/CameraActivity"
        )

        val result = reconcileUseCase(savedFavorites, installed)
        assertEquals(2, result.size)
        assertEquals("Chrome", result[0].label)
        assertEquals("Camera", result[1].label)
    }

    @Test
    fun `getSuggestedFavorites picks core communication and tool apps`() {
        val apps = listOf(
            LauncherApp("com.android.dialer", "DialerActivity", "Phone", true),
            LauncherApp("com.google.android.apps.messaging", "ConversationListActivity", "Messages", false),
            LauncherApp("com.android.chrome", "MainActivity", "Chrome", false),
            LauncherApp("com.android.camera", "CameraActivity", "Camera", true),
            LauncherApp("com.game.flappy", "MainActivity", "Flappy Bird", false)
        )

        val suggestions = reconcileUseCase.getSuggestedFavorites(apps)
        assertTrue(suggestions.contains("com.android.dialer/DialerActivity"))
        assertTrue(suggestions.contains("com.google.android.apps.messaging/ConversationListActivity"))
        assertTrue(suggestions.contains("com.android.chrome/MainActivity"))
    }
}
