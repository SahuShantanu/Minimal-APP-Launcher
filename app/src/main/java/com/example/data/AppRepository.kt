package com.example.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppRepository(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {
    private val _appsFlow = MutableStateFlow<List<LauncherApp>>(emptyList())
    val appsFlow: StateFlow<List<LauncherApp>> = _appsFlow.asStateFlow()

    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            refresh()
        }
    }

    init {
        registerPackageReceiver()
        refresh()
    }

    private fun registerPackageReceiver() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addDataScheme("package")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(packageReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(packageReceiver, filter)
        }
    }

    fun refresh() {
        scope.launch {
            val apps = queryInstalledApps()
            _appsFlow.value = apps
        }
    }

    private suspend fun queryInstalledApps(): List<LauncherApp> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val launcherIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.queryIntentActivities(
                    launcherIntent,
                    PackageManager.ResolveInfoFlags.of(0L)
                )
            } else {
                @Suppress("DEPRECATION")
                pm.queryIntentActivities(launcherIntent, 0)
            }
        } catch (e: Exception) {
            emptyList()
        }

        val ownPackage = context.packageName

        resolveInfos.mapNotNull { resolveInfo ->
            val activityInfo = resolveInfo.activityInfo ?: return@mapNotNull null
            val packageName = activityInfo.packageName ?: return@mapNotNull null
            val activityName = activityInfo.name ?: return@mapNotNull null

            // Exclude this launcher itself from the app drawer
            if (packageName == ownPackage) return@mapNotNull null

            val label = try {
                resolveInfo.loadLabel(pm)?.toString()?.trim() ?: packageName
            } catch (e: Exception) {
                packageName
            }

            val isSystemApp = try {
                val appInfo = activityInfo.applicationInfo
                (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            } catch (e: Exception) {
                false
            }

            LauncherApp(
                packageName = packageName,
                activityName = activityName,
                label = if (label.isNotBlank()) label else packageName,
                isSystemApp = isSystemApp
            )
        }
    }

    fun cleanup() {
        try {
            context.unregisterReceiver(packageReceiver)
        } catch (e: Exception) {
            // Receiver might not be registered or already unregistered
        }
    }
}
