package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LauncherApp
import com.example.design.VoidColors
import com.example.design.VoidDimensions
import com.example.design.VoidTypography
import com.example.ui.drawer.AppGlyphMapper

@Composable
fun FloatingDockNavbar(
    dockApps: List<LauncherApp>,
    onLaunchApp: (LauncherApp) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("floating_navbar"),
        shape = RoundedCornerShape(26.dp),
        color = Color(0xFF141414).copy(alpha = 0.94f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF282828)),
        shadowElevation = 8.dp
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = VoidDimensions.SpaceMedium, vertical = 10.dp)
                .testTag("floating_dock_scroll_row"),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            if (dockApps.isEmpty()) {
                item {
                    Text(
                        text = "No apps in navbar (configure in Settings)",
                        style = VoidTypography.bodySmall,
                        color = VoidColors.TextDisabled,
                        modifier = Modifier.padding(vertical = VoidDimensions.SpaceSmall)
                    )
                }
            } else {
                items(dockApps, key = { it.id }) { app ->
                    DockItem(
                        app = app,
                        onClick = { onLaunchApp(app) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DockItem(
    app: LauncherApp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glyph = AppGlyphMapper.getGlyphForApp(app)

    Column(
        modifier = modifier
            .width(58.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp)
            .testTag("dock_app_${app.packageName}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Squircle Icon Container
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF202020))
                .border(1.dp, Color(0xFF2E2E2E), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = glyph,
                contentDescription = app.label,
                tint = Color(0xFFEEEEEE),
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = app.label,
            style = VoidTypography.bodySmall.copy(
                fontSize = 11.sp,
                color = VoidColors.TextSecondary
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
