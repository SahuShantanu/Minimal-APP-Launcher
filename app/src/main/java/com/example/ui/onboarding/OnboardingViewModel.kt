package com.example.ui.onboarding

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.LauncherApplication
import com.example.data.LauncherApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val currentStep: Int = 1,
    val availableApps: List<LauncherApp> = emptyList(),
    val selectedAppIds: Set<String> = emptySet(),
    val isDefaultLauncher: Boolean = false
)

class OnboardingViewModel : ViewModel() {

    private val app = LauncherApplication.instance
    private val settingsRepo = app.settingsRepository
    private val appRepo = app.appRepository
    private val homeRoleHandler = app.homeRoleHandler
    private val reconcileUseCase = app.reconcileFavoritesUseCase

    private val _currentStep = MutableStateFlow(1)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    private val _selectedAppIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedAppIds: StateFlow<Set<String>> = _selectedAppIds.asStateFlow()

    private val _isDefaultLauncher = MutableStateFlow(homeRoleHandler.isDefaultLauncher())

    val uiState: StateFlow<OnboardingUiState> = combine(
        _currentStep,
        appRepo.appsFlow,
        _selectedAppIds,
        _isDefaultLauncher
    ) { step, allApps, selectedIds, isDefault ->
        // On first initialization, if no selection, suggest default favorites
        if (selectedIds.isEmpty() && allApps.isNotEmpty()) {
            val suggestions = reconcileUseCase.getSuggestedFavorites(allApps).toSet()
            _selectedAppIds.value = suggestions
        }

        OnboardingUiState(
            currentStep = step,
            availableApps = allApps,
            selectedAppIds = if (selectedIds.isEmpty()) _selectedAppIds.value else selectedIds,
            isDefaultLauncher = isDefault
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = OnboardingUiState()
    )

    fun nextStep() {
        if (_currentStep.value < 3) {
            _currentStep.value += 1
        }
    }

    fun prevStep() {
        if (_currentStep.value > 1) {
            _currentStep.value -= 1
        }
    }

    fun toggleAppSelection(appId: String) {
        val current = _selectedAppIds.value
        _selectedAppIds.value = if (current.contains(appId)) {
            current - appId
        } else {
            current + appId
        }
    }

    fun requestDefaultLauncher(context: Context) {
        homeRoleHandler.openDefaultLauncherSettings(context)
    }

    fun completeOnboarding(onFinished: () -> Unit) {
        viewModelScope.launch {
            val finalFavorites = _selectedAppIds.value.toList()
            settingsRepo.setFavorites(finalFavorites)
            settingsRepo.setOnboardingCompleted(true)
            onFinished()
        }
    }
}
