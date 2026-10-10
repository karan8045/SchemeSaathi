package com.karan.schemesaathi.ui.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karan.schemesaathi.data.model.SchemeOpportunity
import com.karan.schemesaathi.data.model.UserProfile
import com.karan.schemesaathi.data.pref.UserPreferencesRepository
import com.karan.schemesaathi.data.repository.OpportunityRepository
import com.karan.schemesaathi.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ApplicationAssistantUiState(
    val opportunity: SchemeOpportunity? = null,
    val userProfile: UserProfile? = null,
    val prefilledName: String = "",
    val prefilledDob: String = "",
    val prefilledState: String = "",
    val prefilledEducation: String = "",
    val prefilledIncome: String = "",
    val prefilledCategory: String = "",
    val missingFields: List<String> = emptyList(),
    val isReviewedAndConfirmed: Boolean = false,
    val officialUrlToLaunch: String? = null
)

@HiltViewModel
class ApplicationAssistantViewModel @Inject constructor(
    private val opportunityRepository: OpportunityRepository,
    private val profileRepository: ProfileRepository,
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ApplicationAssistantUiState())
    val uiState: StateFlow<ApplicationAssistantUiState> = _uiState.asStateFlow()

    fun loadAssistant(opportunityId: String) {
        viewModelScope.launch {
            val opp = opportunityRepository.getOpportunityById(opportunityId)
            userPreferences.userIdFlow.collect { userId ->
                if (!userId.isNullOrBlank()) {
                    val profile = profileRepository.getProfile(userId)
                    val missing = mutableListOf<String>()

                    if (profile?.fullName.isNullOrBlank()) missing.add("Full Name")
                    if (profile?.dateOfBirth.isNullOrBlank()) missing.add("Date of Birth")
                    if (profile?.state.isNullOrBlank()) missing.add("State")
                    if (profile?.incomeRange.isNullOrBlank()) missing.add("Income Certificate / Range")

                    _uiState.value = ApplicationAssistantUiState(
                        opportunity = opp,
                        userProfile = profile,
                        prefilledName = profile?.fullName ?: "",
                        prefilledDob = profile?.dateOfBirth ?: "",
                        prefilledState = profile?.state ?: "",
                        prefilledEducation = profile?.educationLevel ?: "",
                        prefilledIncome = profile?.incomeRange ?: "",
                        prefilledCategory = profile?.socialCategory ?: "",
                        missingFields = missing,
                        officialUrlToLaunch = opp?.officialApplicationUrl
                    )
                }
            }
        }
    }

    fun updatePrefilledName(name: String) {
        _uiState.value = _uiState.value.copy(prefilledName = name)
    }

    fun updatePrefilledDob(dob: String) {
        _uiState.value = _uiState.value.copy(prefilledDob = dob)
    }

    fun toggleConfirmation(confirmed: Boolean) {
        _uiState.value = _uiState.value.copy(isReviewedAndConfirmed = confirmed)
    }
}
