package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.LauncherApp
import com.example.design.VoidColors
import com.example.design.VoidDimensions
import com.example.design.VoidTypography
import com.example.ui.drawer.AppGlyphMapper

@Composable
fun ManageDockAppsDialog(
    allApps: List<LauncherApp>,
    selectedAppIds: Set<String>,
    onToggleApp: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredApps = remember(allApps, searchQuery) {
        if (searchQuery.isBlank()) {
            allApps.sortedBy { it.label.lowercase() }
        } else {
            val q = searchQuery.trim().lowercase()
            allApps.filter { it.normalizedLabel.contains(q) || it.packageName.lowercase().contains(q) }
                .sortedBy { it.label.lowercase() }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = VoidColors.SurfaceElevated,
        title = {
            Text(
                text = "Floating Navbar Apps",
                style = VoidTypography.headlineMedium,
                color = VoidColors.TextPrimary
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Select apps to pin to the bottom floating navbar (${selectedAppIds.size} pinned).",
                    style = VoidTypography.bodySmall,
                    color = VoidColors.TextSecondary
                )

                Spacer(modifier = Modifier.height(VoidDimensions.SpaceSmall))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Filter apps...", color = VoidColors.TextDisabled) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VoidColors.Focus,
                        unfocusedBorderColor = VoidColors.Border,
                        focusedTextColor = VoidColors.TextPrimary,
                        unfocusedTextColor = VoidColors.TextPrimary
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dock_dialog_search_input")
                )

                Spacer(modifier = Modifier.height(VoidDimensions.SpaceSmall))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(VoidDimensions.SpaceExtraSmall)
                ) {
                    items(filteredApps, key = { it.id }) { app ->
                        val isSelected = selectedAppIds.contains(app.id)
                        val glyph = AppGlyphMapper.getGlyphForApp(app)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(VoidDimensions.CornerSmall))
                                .background(if (isSelected) VoidColors.Surface else Color.Transparent)
                                .clickable { onToggleApp(app.id) }
                                .padding(horizontal = VoidDimensions.SpaceSmall, vertical = VoidDimensions.SpaceSmall),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { onToggleApp(app.id) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = VoidColors.Focus,
                                    checkmarkColor = VoidColors.Background,
                                    uncheckedColor = VoidColors.TextSecondary
                                )
                            )
                            Spacer(modifier = Modifier.width(VoidDimensions.SpaceSmall))
                            Icon(
                                imageVector = glyph,
                                contentDescription = null,
                                tint = if (isSelected) VoidColors.Focus else VoidColors.TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(VoidDimensions.SpaceSmall))
                            Text(
                                text = app.label,
                                style = VoidTypography.bodyLarge,
                                color = VoidColors.TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dock_dialog_done_button")
            ) {
                Text("Done", color = VoidColors.Focus)
            }
        }
    )
}
