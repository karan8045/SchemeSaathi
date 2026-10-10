package com.karan.schemesaathi.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karan.schemesaathi.data.model.SchemeOpportunity
import com.karan.schemesaathi.data.model.UserProfile
import com.karan.schemesaathi.data.pref.UserPreferencesRepository
import com.karan.schemesaathi.data.remote.GeminiAssistantManager
import com.karan.schemesaathi.data.repository.OpportunityRepository
import com.karan.schemesaathi.data.repository.ProfileRepository
import com.karan.schemesaathi.data.repository.UserDataRepository
import com.karan.schemesaathi.engine.EligibilityEngine
import com.karan.schemesaathi.engine.EligibilityResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val opportunity: SchemeOpportunity? = null,
    val userProfile: UserProfile? = null,
    val eligibilityResult: EligibilityResult? = null,
    val isSaved: Boolean = false,
    val geminiResponse: String? = null,
    val isGeminiLoading: Boolean = false,
    val selectedLanguage: String = "en"
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val opportunityRepository: OpportunityRepository,
    private val profileRepository: ProfileRepository,
    private val userDataRepository: UserDataRepository,
    private val userPreferences: UserPreferencesRepository,
    private val geminiManager: GeminiAssistantManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private var currentUserId: String = ""

    fun loadOpportunity(opportunityId: String) {
        viewModelScope.launch {
            val opp = opportunityRepository.getOpportunityById(opportunityId)
            userPreferences.userIdFlow.collect { userId ->
                if (!userId.isNullOrBlank()) {
                    currentUserId = userId
                    val profile = profileRepository.getProfile(userId)
                    val result = opp?.let { EligibilityEngine.evaluate(profile, it) }

                    userDataRepository.getSavedItemsFlow(userId).collect { savedItems ->
                        val isSaved = savedItems.any { it.itemId == opportunityId }
                        _uiState.value = _uiState.value.copy(
                            opportunity = opp,
                            userProfile = profile,
                            eligibilityResult = result,
                            isSaved = isSaved
                        )
                    }
                }
            }
        }
    }

    fun askGemini(question: String) {
        val state = _uiState.value
        val opp = state.opportunity ?: return
        _uiState.value = state.copy(isGeminiLoading = true)

        viewModelScope.launch {
            val result = geminiManager.askSaathi(
                prompt = question,
                opportunity = opp,
                userProfile = state.userProfile,
                language = state.selectedLanguage
            )
            _uiState.value = _uiState.value.copy(
                isGeminiLoading = false,
                geminiResponse = result.getOrNull() ?: "Unable to answer at this time. Please verify details on the official portal."
            )
        }
    }

    fun toggleSave() {
        val opp = _uiState.value.opportunity ?: return
        viewModelScope.launch {
            if (_uiState.value.isSaved) {
                userDataRepository.removeSavedItem(currentUserId, "opportunity", opp.id)
            } else {
                userDataRepository.saveItem(currentUserId, "opportunity", opp.id)
            }
            _uiState.value = _uiState.value.copy(isSaved = !_uiState.value.isSaved)
        }
    }

    fun addDeadlineReminder(reminderDate: String) {
        val opp = _uiState.value.opportunity ?: return
        viewModelScope.launch {
            userDataRepository.addDeadlineReminder(currentUserId, "opportunity", opp.id, reminderDate)
        }
    }
}
