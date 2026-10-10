package com.karan.schemesaathi.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karan.schemesaathi.data.model.EligibilityStatus
import com.karan.schemesaathi.data.model.SchemeOpportunity
import com.karan.schemesaathi.data.model.UserProfile
import com.karan.schemesaathi.data.pref.UserPreferencesRepository
import com.karan.schemesaathi.data.repository.OpportunityRepository
import com.karan.schemesaathi.data.repository.ProfileRepository
import com.karan.schemesaathi.data.repository.UserDataRepository
import com.karan.schemesaathi.engine.EligibilityEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val userProfile: UserProfile? = null,
    val bestMatches: List<Pair<SchemeOpportunity, EligibilityStatus>> = emptyList(),
    val newOpportunities: List<SchemeOpportunity> = emptyList(),
    val upcomingDeadlines: List<SchemeOpportunity> = emptyList(),
    val savedItemIds: Set<String> = emptySet(),
    val isTelegramConnected: Boolean = false,
    val selectedLanguage: String = "en",
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val opportunityRepository: OpportunityRepository,
    private val userDataRepository: UserDataRepository,
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentUserId: String = ""

    init {
        viewModelScope.launch {
            opportunityRepository.ensureSeeded()
            userPreferences.userIdFlow.collect { userId ->
                if (!userId.isNullOrBlank()) {
                    currentUserId = userId
                    observeData(userId)
                }
            }
        }
    }

    private fun observeData(userId: String) {
        viewModelScope.launch {
            combine(
                profileRepository.getProfileFlow(userId),
                opportunityRepository.getAllOpportunitiesFlow(),
                userDataRepository.getSavedItemsFlow(userId),
                userDataRepository.getTelegramConnFlow(userId),
                userPreferences.selectedLanguageFlow
            ) { profile, opportunities, savedItems, telegramConn, lang ->
                val savedIds = savedItems.map { it.itemId }.toSet()

                // Evaluate eligibility for all opportunities
                val evaluatedList = opportunities.map { opp ->
                    val result = EligibilityEngine.evaluate(profile, opp)
                    opp to result.status
                }

                val bestMatches = evaluatedList.filter {
                    it.second == EligibilityStatus.ELIGIBLE || it.second == EligibilityStatus.LIKELY_ELIGIBLE
                }.take(5)

                val upcomingDeadlines = opportunities.filter { !it.deadline.isNullOrBlank() }.take(5)

                HomeUiState(
                    userProfile = profile,
                    bestMatches = bestMatches,
                    newOpportunities = opportunities.take(8),
                    upcomingDeadlines = upcomingDeadlines,
                    savedItemIds = savedIds,
                    isTelegramConnected = telegramConn?.isActive == true,
                    selectedLanguage = lang,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleSaveOpportunity(opportunityId: String) {
        val isSaved = _uiState.value.savedItemIds.contains(opportunityId)
        viewModelScope.launch {
            if (isSaved) {
                userDataRepository.removeSavedItem(currentUserId, "opportunity", opportunityId)
            } else {
                userDataRepository.saveItem(currentUserId, "opportunity", opportunityId)
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }
}
