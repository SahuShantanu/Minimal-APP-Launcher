package com.example

import com.example.data.LauncherApp
import com.example.domain.SearchAppsUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SearchAppsUseCaseTest {

    private lateinit var searchUseCase: SearchAppsUseCase
    private val testApps = listOf(
        LauncherApp("com.android.chrome", "MainActivity", "Chrome", false),
        LauncherApp("com.google.android.youtube", "HomeActivity", "YouTube", false),
        LauncherApp("com.google.android.apps.photos", "PhotosActivity", "Google Photos", false),
        LauncherApp("com.spotify.music", "MainActivity", "Spotify", false),
        LauncherApp("com.android.settings", "SettingsActivity", "Settings", true),
        LauncherApp("com.android.camera", "CameraActivity", "Camera", true)
    )

    @Before
    fun setUp() {
        searchUseCase = SearchAppsUseCase()
    }

    @Test
    fun `empty query returns all apps sorted alphabetically`() {
        val results = searchUseCase(testApps, "")
        assertEquals(testApps.size, results.size)
        assertEquals("Camera", results[0].label)
        assertEquals("Chrome", results[1].label)
    }

    @Test
    fun `exact match ranks first`() {
        val results = searchUseCase(testApps, "camera")
        assertTrue(results.isNotEmpty())
        assertEquals("Camera", results[0].label)
    }

    @Test
    fun `prefix match ranks before substring match`() {
        val apps = listOf(
            LauncherApp("pkg.myphoto", "Act", "My Photos App", false),
            LauncherApp("pkg.photos", "Act", "Photos", false)
        )
        val results = searchUseCase(apps, "phot")
        assertEquals(2, results.size)
        // "Photos" is a prefix match; "My Photos App" is a word-prefix match
        assertEquals("Photos", results[0].label)
        assertEquals("My Photos App", results[1].label)
    }

    @Test
    fun `word prefix match succeeds for multi-word labels`() {
        val results = searchUseCase(testApps, "photo")
        assertEquals(1, results.size)
        assertEquals("Google Photos", results[0].label)
    }

    @Test
    fun `no match returns empty list`() {
        val results = searchUseCase(testApps, "xyz123nonexistent")
        assertTrue(results.isEmpty())
    }
}
