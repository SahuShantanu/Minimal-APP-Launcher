package com.example.domain

import com.example.data.LauncherApp
import java.text.Collator
import java.util.Locale

class SearchAppsUseCase {

    private val collator = Collator.getInstance(Locale.getDefault()).apply {
        strength = Collator.PRIMARY
    }

    operator fun invoke(apps: List<LauncherApp>, query: String): List<LauncherApp> {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isEmpty()) {
            return apps.sortedWith { a, b -> collator.compare(a.label, b.label) }
        }

        val normalizedQuery = trimmedQuery.lowercase()

        // Categorize into the 4 ranking tiers
        val exactMatches = mutableListOf<LauncherApp>()
        val prefixMatches = mutableListOf<LauncherApp>()
        val wordPrefixMatches = mutableListOf<LauncherApp>()
        val substringMatches = mutableListOf<LauncherApp>()

        for (app in apps) {
            val label = app.normalizedLabel
            when {
                label == normalizedQuery -> {
                    exactMatches.add(app)
                }
                label.startsWith(normalizedQuery) -> {
                    prefixMatches.add(app)
                }
                hasWordPrefixMatch(label, normalizedQuery) -> {
                    wordPrefixMatches.add(app)
                }
                label.contains(normalizedQuery) -> {
                    substringMatches.add(app)
                }
            }
        }

        val comparator = Comparator<LauncherApp> { a, b -> collator.compare(a.label, b.label) }
        exactMatches.sortWith(comparator)
        prefixMatches.sortWith(comparator)
        wordPrefixMatches.sortWith(comparator)
        substringMatches.sortWith(comparator)

        return exactMatches + prefixMatches + wordPrefixMatches + substringMatches
    }

    private fun hasWordPrefixMatch(text: String, query: String): Boolean {
        var start = 0
        val len = text.length
        while (start < len) {
            // Skip non-alphanumeric delimiters
            while (start < len && !text[start].isLetterOrDigit()) {
                start++
            }
            if (start >= len) break
            if (text.startsWith(query, start)) {
                return true
            }
            // Advance to next word delimiter
            while (start < len && text[start].isLetterOrDigit()) {
                start++
            }
        }
        return false
    }
}
