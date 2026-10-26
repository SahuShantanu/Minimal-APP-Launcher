package com.example.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LauncherApp
import com.example.design.VoidColors
import com.example.design.VoidDimensions
import com.example.design.VoidTypography

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VoidColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(VoidDimensions.SpaceExtraLarge)
        ) {
            // Step Indicator Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VOID LAUNCHER",
                    style = VoidTypography.labelSmall,
                    color = VoidColors.TextSecondary,
                    letterSpacing = 1.6.sp
                )
                Text(
                    text = "STEP ${uiState.currentStep} OF 3",
                    style = VoidTypography.labelSmall,
                    color = VoidColors.TextDisabled
                )
            }

            Spacer(modifier = Modifier.height(VoidDimensions.SpaceSection))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (uiState.currentStep) {
                    1 -> Step1Intro()
                    2 -> Step2SelectFavorites(
                        apps = uiState.availableApps,
                        selectedIds = uiState.selectedAppIds,
                        onToggle = { viewModel.toggleAppSelection(it) }
                    )
                    3 -> Step3SetDefault(
                        onRequestDefault = { viewModel.requestDefaultLauncher(context) }
                    )
                }
            }

            // Bottom Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (uiState.currentStep > 1) {
                    TextButton(
                        onClick = { viewModel.prevStep() },
                        modifier = Modifier.testTag("onboarding_back_button")
                    ) {
                        Text("Back", color = VoidColors.TextSecondary)
                    }
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }

                if (uiState.currentStep < 3) {
                    Button(
                        onClick = { viewModel.nextStep() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VoidColors.Focus,
                            contentColor = VoidColors.Background
                        ),
                        shape = RoundedCornerShape(VoidDimensions.CornerMedium),
                        modifier = Modifier.testTag("onboarding_next_button")
                    ) {
                        Text("Continue", style = VoidTypography.titleMedium)
                    }
                } else {
                    Button(
                        onClick = { viewModel.completeOnboarding(onFinish) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VoidColors.Focus,
                            contentColor = VoidColors.Background
                        ),
                        shape = RoundedCornerShape(VoidDimensions.CornerMedium),
                        modifier = Modifier.testTag("onboarding_finish_button")
                    ) {
                        Text("Enter VOID", style = VoidTypography.titleMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun Step1Intro() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Nearly silent.\nInfinitely focused.",
            style = VoidTypography.displayMedium,
            color = VoidColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(VoidDimensions.SpaceExtraLarge))

        Text(
            text = "VOID replaces bright app badges, noisy widgets, and notification clutter with a calm text-only home screen.",
            style = VoidTypography.bodyLarge,
            color = VoidColors.TextSecondary
        )

        Spacer(modifier = Modifier.height(VoidDimensions.SpaceLarge))

        Column(verticalArrangement = Arrangement.spacedBy(VoidDimensions.SpaceMedium)) {
            FeatureItem(title = "Text-First Home", description = "Your favorite apps as simple typography without distracting icon badges.")
            FeatureItem(title = "Monochrome Drawer", description = "Clean grayscale app grid that reduces impulsive phone usage.")
            FeatureItem(title = "Instant Local Search", description = "Instant fuzzy app discovery offline with zero network latency.")
            FeatureItem(title = "Private & Light", description = "No accounts, no analytics, and zero background battery drain.")
        }
    }
}

@Composable
private fun FeatureItem(title: String, description: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(VoidColors.Focus)
        )
        Spacer(modifier = Modifier.width(VoidDimensions.SpaceMedium))
        Column {
            Text(text = title, style = VoidTypography.titleMedium, color = VoidColors.TextPrimary)
            Text(text = description, style = VoidTypography.bodySmall, color = VoidColors.TextSecondary)
        }
    }
}

@Composable
private fun Step2SelectFavorites(
    apps: List<LauncherApp>,
    selectedIds: Set<String>,
    onToggle: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Pick your essentials",
            style = VoidTypography.headlineMedium,
            color = VoidColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(VoidDimensions.SpaceExtraSmall))

        Text(
            text = "Select 3–6 apps you use daily to place on your home screen.",
            style = VoidTypography.bodyMedium,
            color = VoidColors.TextSecondary
        )

        Spacer(modifier = Modifier.height(VoidDimensions.SpaceMedium))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(VoidDimensions.SpaceExtraSmall)
        ) {
            items(apps, key = { it.id }) { app ->
                val isSelected = selectedIds.contains(app.id)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(VoidDimensions.CornerSmall))
                        .background(if (isSelected) VoidColors.SurfaceElevated else VoidColors.Surface)
                        .clickable { onToggle(app.id) }
                        .padding(horizontal = VoidDimensions.SpaceMedium, vertical = VoidDimensions.SpaceSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onToggle(app.id) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = VoidColors.Focus,
                            checkmarkColor = VoidColors.Background,
                            uncheckedColor = VoidColors.TextSecondary
                        )
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
}

@Composable
private fun Step3SetDefault(
    onRequestDefault: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Make VOID your Home",
            style = VoidTypography.headlineMedium,
            color = VoidColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(VoidDimensions.SpaceMedium))

        Text(
            text = "To enjoy a seamless minimal experience every time you press Home or swipe up to go home, set VOID as your default launcher.",
            style = VoidTypography.bodyLarge,
            color = VoidColors.TextSecondary
        )

        Spacer(modifier = Modifier.height(VoidDimensions.SpaceSection))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(VoidDimensions.CornerMedium))
                .background(VoidColors.Surface)
                .border(1.dp, VoidColors.Focus, RoundedCornerShape(VoidDimensions.CornerMedium))
                .clickable(onClick = onRequestDefault)
                .padding(VoidDimensions.SpaceLarge)
                .testTag("onboarding_set_default_card")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = VoidColors.Focus,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(VoidDimensions.SpaceLarge))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Choose Default Home App",
                        style = VoidTypography.titleMedium,
                        color = VoidColors.TextPrimary
                    )
                    Text(
                        text = "Select VOID Launcher and choose Always",
                        style = VoidTypography.bodySmall,
                        color = VoidColors.TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(VoidDimensions.SpaceSection))

        Text(
            text = "How to switch back anytime:",
            style = VoidTypography.titleMedium,
            color = VoidColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(VoidDimensions.SpaceSmall))
        Text(
            text = "Open Android Settings > Apps > Default apps > Home app, and choose your original launcher. You are always in full control.",
            style = VoidTypography.bodySmall,
            color = VoidColors.TextSecondary
        )
    }
}
