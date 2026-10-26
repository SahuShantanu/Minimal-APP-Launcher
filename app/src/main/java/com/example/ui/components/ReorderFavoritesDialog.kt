package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.LauncherApp
import com.example.design.VoidColors
import com.example.design.VoidDimensions
import com.example.design.VoidTypography

@Composable
fun ReorderFavoritesDialog(
    favoriteApps: List<LauncherApp>,
    onMoveUp: (Int) -> Unit,
    onMoveDown: (Int) -> Unit,
    onRemove: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = VoidColors.SurfaceElevated,
        title = {
            Text(
                text = "Reorder Favorites",
                style = VoidTypography.headlineMedium,
                color = VoidColors.TextPrimary
            )
        },
        text = {
            if (favoriteApps.isEmpty()) {
                Text(
                    text = "No favorites added yet.",
                    style = VoidTypography.bodyMedium,
                    color = VoidColors.TextSecondary
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(VoidDimensions.SpaceSmall)
                ) {
                    itemsIndexed(favoriteApps, key = { _, app -> app.id }) { index, app ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(VoidColors.Surface, RoundedCornerShape(VoidDimensions.CornerSmall))
                                .padding(horizontal = VoidDimensions.SpaceMedium, vertical = VoidDimensions.SpaceSmall),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${index + 1}.",
                                style = VoidTypography.bodyMedium,
                                color = VoidColors.TextSecondary,
                                modifier = Modifier.padding(end = VoidDimensions.SpaceSmall)
                            )
                            Text(
                                text = app.label,
                                style = VoidTypography.bodyLarge,
                                color = VoidColors.TextPrimary,
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = { onMoveUp(index) },
                                enabled = index > 0,
                                modifier = Modifier.size(36.dp).testTag("move_up_$index")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Move up",
                                    tint = if (index > 0) VoidColors.TextPrimary else VoidColors.TextDisabled
                                )
                            }

                            IconButton(
                                onClick = { onMoveDown(index) },
                                enabled = index < favoriteApps.size - 1,
                                modifier = Modifier.size(36.dp).testTag("move_down_$index")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Move down",
                                    tint = if (index < favoriteApps.size - 1) VoidColors.TextPrimary else VoidColors.TextDisabled
                                )
                            }

                            IconButton(
                                onClick = { onRemove(app.id) },
                                modifier = Modifier.size(36.dp).testTag("remove_fav_$index")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove favorite",
                                    tint = VoidColors.Danger
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dialog_done_button")
            ) {
                Text("Done", color = VoidColors.Focus)
            }
        }
    )
}
