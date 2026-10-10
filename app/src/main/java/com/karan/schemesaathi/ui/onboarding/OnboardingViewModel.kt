package com.karan.schemesaathi.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karan.schemesaathi.data.model.UserProfile
import com.karan.schemesaathi.data.pref.UserPreferencesRepository
import com.karan.schemesaathi.data.repository.ProfileRepository
import com.karan.schemesaathi.data.seed.DemoPersonas
import com.karan.schemesaathi.engine.AgeCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val currentStep: Int = 1,
    val totalSteps: Int = 4,
    val profile: UserProfile = UserProfile(),
    val calculatedAge: Int? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isOnboardingComplete: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private var userId: String = ""

    init {
        viewModelScope.launch {
            userPreferences.userIdFlow.collect { id ->
                if (!id.isNullOrBlank()) {
                    userId = id
                    val existing = profileRepository.getProfile(id)
                    if (existing != null) {
                        val age = AgeCalculator.calculateAge(existing.dateOfBirth)
                        _uiState.value = _uiState.value.copy(
                            profile = existing.copy(id = id),
                            calculatedAge = age
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            profile = _uiState.value.profile.copy(id = id)
                        )
                    }
                }
            }
        }
    }

    fun updateProfile(transform: (UserProfile) -> UserProfile) {
        val updated = transform(_uiState.value.profile)
        val age = AgeCalculator.calculateAge(updated.dateOfBirth)
        _uiState.value = _uiState.value.copy(
            profile = updated,
            calculatedAge = age,
            errorMessage = null
        )
    }

    fun applyDemoPersona(persona: UserProfile) {
        val configured = persona.copy(id = userId, profileComplete = true)
        val age = AgeCalculator.calculateAge(configured.dateOfBirth)
        _uiState.value = _uiState.value.copy(
            profile = configured,
            calculatedAge = age,
            errorMessage = null
        )
    }

    fun nextStep() {
        val state = _uiState.value
        // Validate required fields based on step
        when (state.currentStep) {
            1 -> {
                if (state.profile.fullName.isBlank()) {
                    _uiState.value = state.copy(errorMessage = "Please enter your Full Name.")
                    return
                }
                if (!AgeCalculator.isValidDob(state.profile.dateOfBirth)) {
                    _uiState.value = state.copy(errorMessage = "Please select a valid past Date of Birth.")
                    return
                }
            }
            2 -> {
                if (state.profile.state.isBlank()) {
                    _uiState.value = state.copy(errorMessage = "Please select your State.")
                    return
                }
            }
        }

        if (state.currentStep < state.totalSteps) {
            _uiState.value = state.copy(currentStep = state.currentStep + 1, errorMessage = null)
        } else {
            completeOnboarding()
        }
    }

    fun previousStep() {
        if (_uiState.value.currentStep > 1) {
            _uiState.value = _uiState.value.copy(currentStep = _uiState.value.currentStep - 1, errorMessage = null)
        }
    }

    fun completeOnboarding() {
        val finalProfile = _uiState.value.profile.copy(
            id = userId,
            profileComplete = true
        )

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = profileRepository.saveProfile(finalProfile)
            _uiState.value = _uiState.value.copy(isLoading = false)

            if (result.isSuccess) {
                userPreferences.setOnboarded(true)
                _uiState.value = _uiState.value.copy(isOnboardingComplete = true)
            } else {
                _uiState.value = _uiState.value.copy(errorMessage = "Failed to save profile. Please check network.")
            }
        }
    }
}
