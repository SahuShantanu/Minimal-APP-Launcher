package com.example.domain

import com.example.data.LauncherApp
import java.text.Collator
import java.util.Locale

class SortAppsUseCase {

    private val collator = Collator.getInstance(Locale.getDefault()).apply {
        strength = Collator.PRIMARY
    }

    operator fun invoke(apps: List<LauncherApp>): List<LauncherApp> {
        return apps.sortedWith { a, b -> collator.compare(a.label, b.label) }
    }
}
