package com.example.data

data class LauncherApp(
    val packageName: String,
    val activityName: String,
    val label: String,
    val isSystemApp: Boolean
) {
    val id: String = "$packageName/$activityName"
    val normalizedLabel: String = label.trim().lowercase()
}
