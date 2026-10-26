package com.example.ui.drawer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.IconRepository
import com.example.data.LauncherApp
import com.example.design.VoidColors
import com.example.design.VoidDimensions
import com.example.design.VoidTypography

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppGridItem(
    app: LauncherApp,
    iconRepository: IconRepository,
    iconStyle: String,
    iconSizeDp: Int,
    showLabels: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    val squircleShape = RoundedCornerShape(18.dp)
    val tileBackground = Color(0xFF181818)
    val tileBorder = Color(0xFF282828)
    val iconTint = Color(0xFFEEEEEE)

    // Only load bitmap from disk/memory if not using minimal vector glyphs
    val loadBitmap = iconStyle != "MINIMAL_GLYPHS"
    val isMonochrome = iconStyle == "MONOCHROME"

    val iconBitmap = if (loadBitmap) {
        produceState<ImageBitmap?>(initialValue = null, key1 = app.id, key2 = iconStyle) {
            value = iconRepository.getAppIcon(
                packageName = app.packageName,
                activityName = app.activityName,
                monochrome = isMonochrome,
                targetSizePx = (iconSizeDp * 3)
            )
        }.value
    } else {
        null
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(VoidDimensions.CornerMedium))
            .combinedClickable(
                onClick = onClick,
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongClick()
                }
            )
            .padding(vertical = VoidDimensions.SpaceSmall, horizontal = 2.dp)
            .testTag("app_item_${app.packageName}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Uniform Squircle Tile (matching the reference design)
        Box(
            modifier = Modifier
                .size(iconSizeDp.dp)
                .clip(squircleShape)
                .background(tileBackground)
                .border(1.dp, tileBorder, squircleShape),
            contentAlignment = Alignment.Center
        ) {
            when {
                iconStyle == "MINIMAL_GLYPHS" || (loadBitmap && iconBitmap == null) -> {
                    // Pristine outline glyph matching the user screenshot
                    val glyph = AppGlyphMapper.getGlyphForApp(app)
                    Icon(
                        imageVector = glyph,
                        contentDescription = app.label,
                        tint = iconTint,
                        modifier = Modifier.size((iconSizeDp * 0.46f).dp)
                    )
                }

                iconBitmap != null -> {
                    // Real app icon centered neatly inside the squircle tile
                    Image(
                        bitmap = iconBitmap,
                        contentDescription = app.label,
                        modifier = Modifier
                            .size((iconSizeDp * 0.65f).dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                }
            }
        }

        if (showLabels) {
            Spacer(modifier = Modifier.height(VoidDimensions.SpaceSmall))
            Text(
                text = app.label,
                style = VoidTypography.bodySmall.copy(
                    fontSize = 12.sp,
                    color = VoidColors.TextSecondary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
