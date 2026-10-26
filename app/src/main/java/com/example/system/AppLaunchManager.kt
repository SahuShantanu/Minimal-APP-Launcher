package com.example.system

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import com.example.data.LauncherApp

class AppLaunchManager {

    fun launchApp(context: Context, app: LauncherApp): Result<Unit> {
        return try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                component = ComponentName(app.packageName, app.activityName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            }
            context.startActivity(intent)
            Result.success(Unit)
        } catch (e: ActivityNotFoundException) {
            try {
                val fallbackIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                if (fallbackIntent != null) {
                    fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(fallbackIntent)
                    Result.success(Unit)
                } else {
                    Result.failure(e)
                }
            } catch (e2: Exception) {
                Result.failure(e2)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun launchAppById(context: Context, appId: String, allApps: List<LauncherApp>): Result<Unit> {
        if (appId.isBlank() || appId == "NONE") {
            return Result.success(Unit)
        }

        if (appId == "DEFAULT_CAMERA") {
            val cameraApp = allApps.firstOrNull {
                it.packageName.lowercase().contains("camera") || it.normalizedLabel.contains("camera")
            }
            if (cameraApp != null) {
                return launchApp(context, cameraApp)
            }
            return try {
                val cameraIntent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(cameraIntent)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        if (appId == "DEFAULT_PHONE") {
            val phoneApp = allApps.firstOrNull {
                it.packageName.lowercase().contains("dialer") ||
                    it.packageName.lowercase().contains("phone") ||
                    it.normalizedLabel.contains("phone")
            }
            if (phoneApp != null) {
                return launchApp(context, phoneApp)
            }
            return try {
                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(dialIntent)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        val targetApp = allApps.firstOrNull { it.id == appId || it.packageName == appId }
        if (targetApp != null) {
            return launchApp(context, targetApp)
        }

        // Try raw package launch
        val rawPkg = appId.substringBefore("/")
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage(rawPkg)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                Result.success(Unit)
            } else {
                Result.failure(Exception("App not found: $appId"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun openAppInfo(context: Context, packageName: String): Result<Unit> {
        return try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun requestUninstall(context: Context, packageName: String): Result<Unit> {
        return try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.fromParts("package", packageName, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
