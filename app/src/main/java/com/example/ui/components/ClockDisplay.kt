package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.design.VoidColors
import com.example.design.VoidDimensions
import com.example.design.VoidTypography

@Composable
fun ClockDisplay(
    timeString: String,
    dateString: String,
    clockSize: String,
    showDate: Boolean,
    showSubtitle: Boolean,
    subtitleText: String,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start
) {
    val fontSize = when (clockSize) {
        "SMALL" -> 46.sp
        "MEDIUM" -> 58.sp
        else -> 68.sp
    }
    val lineHeight = when (clockSize) {
        "SMALL" -> 50.sp
        "MEDIUM" -> 62.sp
        else -> 72.sp
    }

    Column(
        modifier = modifier.padding(vertical = VoidDimensions.SpaceSmall),
        horizontalAlignment = horizontalAlignment
    ) {
        if (showDate && dateString.isNotBlank()) {
            Text(
                text = dateString.uppercase(),
                style = VoidTypography.labelSmall,
                color = VoidColors.TextSecondary,
                letterSpacing = 1.6.sp,
                modifier = Modifier.testTag("home_date_text")
            )
            Spacer(modifier = Modifier.height(VoidDimensions.SpaceExtraSmall))
        }

        Text(
            text = timeString,
            style = VoidTypography.displayLarge.copy(
                fontSize = fontSize,
                lineHeight = lineHeight,
                fontWeight = FontWeight.Light
            ),
            color = VoidColors.TextPrimary,
            modifier = Modifier.testTag("home_clock_text")
        )

        AnimatedVisibility(visible = showSubtitle && subtitleText.isNotBlank()) {
            Column {
                Spacer(modifier = Modifier.height(VoidDimensions.SpaceExtraSmall))
                Text(
                    text = subtitleText,
                    style = VoidTypography.bodyMedium,
                    color = VoidColors.TextSecondary,
                    modifier = Modifier.testTag("home_subtitle_text")
                )
            }
        }
    }
}
