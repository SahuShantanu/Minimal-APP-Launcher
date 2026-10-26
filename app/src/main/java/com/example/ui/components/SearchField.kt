package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.design.VoidColors
import com.example.design.VoidDimensions
import com.example.design.VoidTypography

@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search apps...",
    focusRequester: FocusRequester? = null,
    onSearchAction: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(VoidColors.Surface, RoundedCornerShape(VoidDimensions.CornerMedium))
            .border(1.dp, VoidColors.Border, RoundedCornerShape(VoidDimensions.CornerMedium))
            .padding(horizontal = VoidDimensions.SpaceMedium),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = VoidColors.TextSecondary,
                modifier = Modifier.size(20.dp)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = VoidDimensions.SpaceSmall),
                contentAlignment = Alignment.CenterStart
            ) {
                if (query.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = VoidTypography.bodyMedium,
                        color = VoidColors.TextDisabled
                    )
                }

                val tfModifier = if (focusRequester != null) {
                    Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .testTag("search_text_input")
                } else {
                    Modifier
                        .fillMaxWidth()
                        .testTag("search_text_input")
                }

                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = tfModifier,
                    textStyle = VoidTypography.bodyLarge.copy(color = VoidColors.TextPrimary),
                    cursorBrush = SolidColor(VoidColors.Focus),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearchAction?.invoke() })
                )
            }

            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("clear_search_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear search",
                        tint = VoidColors.TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
