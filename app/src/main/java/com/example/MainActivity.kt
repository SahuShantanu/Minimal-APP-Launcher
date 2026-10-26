package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.design.VoidLauncherTheme
import com.example.ui.drawer.AppDrawerScreen
import com.example.ui.drawer.AppDrawerViewModel
import com.example.ui.home.HomeScreen
import com.example.ui.home.HomeViewModel
import com.example.ui.onboarding.OnboardingScreen
import com.example.ui.onboarding.OnboardingViewModel
import com.example.ui.settings.SettingsScreen
import com.example.ui.settings.SettingsViewModel

enum class LauncherScreen {
    HOME,
    DRAWER,
    SETTINGS,
    ONBOARDING
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VoidLauncherTheme {
                VoidLauncherApp()
            }
        }
    }
}

@Composable
fun VoidLauncherApp() {
    val homeViewModel: HomeViewModel = viewModel()
    val homeUiState by homeViewModel.uiState.collectAsState()

    val drawerViewModel: AppDrawerViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()
    val onboardingViewModel: OnboardingViewModel = viewModel()

    var currentScreen by remember { mutableStateOf(LauncherScreen.HOME) }
    var autoFocusSearchInDrawer by remember { mutableStateOf(false) }

    // If onboarding is not completed yet, show OnboardingScreen first
    val showOnboarding = !homeUiState.preferences.onboardingCompleted

    val activeScreen = if (showOnboarding) {
        LauncherScreen.ONBOARDING
    } else {
        currentScreen
    }

    Crossfade(
        targetState = activeScreen,
        animationSpec = tween(durationMillis = 180),
        modifier = Modifier.fillMaxSize()
    ) { screen ->
        when (screen) {
            LauncherScreen.ONBOARDING -> {
                OnboardingScreen(
                    viewModel = onboardingViewModel,
                    onFinish = {
                        currentScreen = LauncherScreen.HOME
                    }
                )
            }

            LauncherScreen.HOME -> {
                HomeScreen(
                    viewModel = homeViewModel,
                    onOpenSearch = {
                        autoFocusSearchInDrawer = false
                        currentScreen = LauncherScreen.DRAWER
                    },
                    onOpenSettings = {
                        currentScreen = LauncherScreen.SETTINGS
                    }
                )
            }

            LauncherScreen.DRAWER -> {
                AppDrawerScreen(
                    viewModel = drawerViewModel,
                    autoFocusSearch = autoFocusSearchInDrawer,
                    onBackToHome = {
                        autoFocusSearchInDrawer = false
                        currentScreen = LauncherScreen.HOME
                    },
                    onOpenSettings = {
                        currentScreen = LauncherScreen.SETTINGS
                    }
                )
            }

            LauncherScreen.SETTINGS -> {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onBackToHome = {
                        currentScreen = LauncherScreen.HOME
                    }
                )
            }
        }
    }
}
