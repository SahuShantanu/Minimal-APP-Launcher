package com.example.data

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.LruCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IconRepository(private val context: Context) {

    private val originalCache = LruCache<String, ImageBitmap>(200)
    private val monochromeCache = LruCache<String, ImageBitmap>(200)

    private val grayscaleMatrix = ColorMatrix().apply {
        setSaturation(0f)
        // Slightly enhance contrast and brightness for sleek dark mode readability
        val contrast = 1.15f
        val brightness = 15f
        val scaleMatrix = floatArrayOf(
            contrast, 0f, 0f, 0f, brightness,
            0f, contrast, 0f, 0f, brightness,
            0f, 0f, contrast, 0f, brightness,
            0f, 0f, 0f, 1f, 0f
        )
        postConcat(ColorMatrix(scaleMatrix))
    }
    private val grayscalePaint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
        colorFilter = ColorMatrixColorFilter(grayscaleMatrix)
    }

    suspend fun getAppIcon(
        packageName: String,
        activityName: String,
        monochrome: Boolean,
        targetSizePx: Int = 144
    ): ImageBitmap? = withContext(Dispatchers.IO) {
        val cacheKey = "$packageName/$activityName"

        if (monochrome) {
            monochromeCache.get(cacheKey)?.let { return@withContext it }
        } else {
            originalCache.get(cacheKey)?.let { return@withContext it }
        }

        val rawDrawable = loadDrawable(packageName, activityName) ?: return@withContext null

        val originalBitmap = drawableToBitmap(rawDrawable, targetSizePx) ?: return@withContext null
        val originalImageBitmap = originalBitmap.asImageBitmap()
        originalCache.put(cacheKey, originalImageBitmap)

        if (monochrome) {
            // Check if Android 13+ has a monochrome layer in AdaptiveIconDrawable
            val monoDrawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && rawDrawable is AdaptiveIconDrawable) {
                rawDrawable.monochrome
            } else null

            val monoBitmap = if (monoDrawable != null) {
                drawableToBitmap(monoDrawable, targetSizePx) ?: convertToGrayscale(originalBitmap)
            } else {
                convertToGrayscale(originalBitmap)
            }
            val monoImageBitmap = monoBitmap.asImageBitmap()
            monochromeCache.put(cacheKey, monoImageBitmap)
            return@withContext monoImageBitmap
        } else {
            return@withContext originalImageBitmap
        }
    }

    private fun loadDrawable(packageName: String, activityName: String): Drawable? {
        val pm = context.packageManager
        return try {
            val component = ComponentName(packageName, activityName)
            pm.getActivityIcon(component)
        } catch (e: Exception) {
            try {
                pm.getApplicationIcon(packageName)
            } catch (e2: Exception) {
                null
            }
        }
    }

    private fun drawableToBitmap(drawable: Drawable, targetSize: Int): Bitmap? {
        return try {
            val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else targetSize
            val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else targetSize
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    private fun convertToGrayscale(src: Bitmap): Bitmap {
        val grayBitmap = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(grayBitmap)
        canvas.drawBitmap(src, 0f, 0f, grayscalePaint)
        return grayBitmap
    }

    fun clearCache() {
        originalCache.evictAll()
        monochromeCache.evictAll()
    }
}
