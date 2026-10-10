package com.karan.schemesaathi.ui.profile

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

data class ProfileUiState(
    val userProfile: UserProfile = UserProfile(),
    val calculatedAge: Int? = null,
    val selectedLanguage: String = "en",
    val isDemoMode: Boolean = true,
    val isSaving: Boolean = false,
    val saveSuccessMessage: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var currentUserId: String = ""

    init {
        viewModelScope.launch {
            userPreferences.userIdFlow.collect { userId ->
                if (!userId.isNullOrBlank()) {
                    currentUserId = userId
                    loadProfile(userId)
                }
            }
        }
        viewModelScope.launch {
            userPreferences.selectedLanguageFlow.collect { lang ->
                _uiState.value = _uiState.value.copy(selectedLanguage = lang)
            }
        }
    }

    private fun loadProfile(userId: String) {
        viewModelScope.launch {
            profileRepository.getProfileFlow(userId).collect { profile ->
                if (profile != null) {
                    val age = AgeCalculator.calculateAge(profile.dateOfBirth)
                    _uiState.value = _uiState.value.copy(
                        userProfile = profile,
                        calculatedAge = age
                    )
                }
            }
        }
    }

    fun updateProfileField(transform: (UserProfile) -> UserProfile) {
        val updated = transform(_uiState.value.userProfile)
        val age = AgeCalculator.calculateAge(updated.dateOfBirth)
        _uiState.value = _uiState.value.copy(
            userProfile = updated,
            calculatedAge = age,
            saveSuccessMessage = null
        )
    }

    fun saveProfile() {
        _uiState.value = _uiState.value.copy(isSaving = true, saveSuccessMessage = null, errorMessage = null)
        viewModelScope.launch {
            val profile = _uiState.value.userProfile.copy(
                id = currentUserId,
                profileComplete = true
            )
            val result = profileRepository.saveProfile(profile)
            _uiState.value = _uiState.value.copy(isSaving = false)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(saveSuccessMessage = "Profile updated successfully!")
            } else {
                _uiState.value = _uiState.value.copy(errorMessage = "Failed to update profile.")
            }
        }
    }

    fun applyDemoPersona(persona: UserProfile) {
        val configured = persona.copy(id = currentUserId, profileComplete = true)
        val age = AgeCalculator.calculateAge(configured.dateOfBirth)
        _uiState.value = _uiState.value.copy(
            userProfile = configured,
            calculatedAge = age,
            saveSuccessMessage = "Applied ${persona.fullName}'s Demo Persona!"
        )
        saveProfile()
    }

    fun setLanguage(languageCode: String) {
        viewModelScope.launch {
            userPreferences.setSelectedLanguage(languageCode)
            _uiState.value = _uiState.value.copy(selectedLanguage = languageCode)
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            profileRepository.deleteAccountAndData(currentUserId)
        }
    }
}
