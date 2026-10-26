package com.example

import com.example.data.LauncherApp
import com.example.domain.SortAppsUseCase
import org.junit.Assert.assertEquals
import org.junit.Test

class SortAppsUseCaseTest {

    @Test
    fun `apps are sorted alphabetically regardless of case`() {
        val sortUseCase = SortAppsUseCase()
        val apps = listOf(
            LauncherApp("com.z", "Act", "zebra", false),
            LauncherApp("com.a", "Act", "Apple", false),
            LauncherApp("com.m", "Act", "mango", false),
            LauncherApp("com.b", "Act", "Banana", false)
        )

        val sorted = sortUseCase(apps)
        assertEquals("Apple", sorted[0].label)
        assertEquals("Banana", sorted[1].label)
        assertEquals("mango", sorted[2].label)
        assertEquals("zebra", sorted[3].label)
    }
}
