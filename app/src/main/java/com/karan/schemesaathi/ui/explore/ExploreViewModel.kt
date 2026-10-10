package com.karan.schemesaathi.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karan.schemesaathi.data.model.Category
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

data class ExploreUiState(
    val selectedCategory: Category? = null,
    val searchQuery: String = "",
    val stateFilter: String? = null,
    val isCentralOnly: Boolean = false,
    val opportunities: List<Pair<SchemeOpportunity, EligibilityStatus>> = emptyList(),
    val savedItemIds: Set<String> = emptySet(),
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val opportunityRepository: OpportunityRepository,
    private val userDataRepository: UserDataRepository,
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    private var currentUserId: String = ""

    init {
        viewModelScope.launch {
            userPreferences.userIdFlow.collect { userId ->
                if (!userId.isNullOrBlank()) {
                    currentUserId = userId
                    observeOpportunities(userId)
                }
            }
        }
    }

    private fun observeOpportunities(userId: String) {
        viewModelScope.launch {
            combine(
                profileRepository.getProfileFlow(userId),
                opportunityRepository.getAllOpportunitiesFlow(),
                userDataRepository.getSavedItemsFlow(userId),
                _uiState
            ) { profile, allOpps, savedItems, state ->
                val savedIds = savedItems.map { it.itemId }.toSet()

                // Filter opportunities
                val filtered = allOpps.filter { opp ->
                    val categoryMatch = state.selectedCategory == null || opp.category == state.selectedCategory
                    val searchMatch = state.searchQuery.isBlank() ||
                            opp.name.contains(state.searchQuery, ignoreCase = true) ||
                            opp.description.contains(state.searchQuery, ignoreCase = true) ||
                            opp.plainLanguageSummary.contains(state.searchQuery, ignoreCase = true)
                    val stateMatch = state.stateFilter == null || opp.state.isNullOrBlank() || opp.state.equals("All India", ignoreCase = true) || opp.state.equals(state.stateFilter, ignoreCase = true)
                    val centralMatch = !state.isCentralOnly || opp.isCentralScheme

                    categoryMatch && searchMatch && stateMatch && centralMatch
                }.map { opp ->
                    opp to EligibilityEngine.evaluate(profile, opp).status
                }

                state.copy(
                    opportunities = filtered,
                    savedItemIds = savedIds,
                    userProfile = profile
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun selectCategory(category: Category?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun setStateFilter(state: String?) {
        _uiState.value = _uiState.value.copy(stateFilter = state)
    }

    fun toggleCentralOnly() {
        _uiState.value = _uiState.value.copy(isCentralOnly = !_uiState.value.isCentralOnly)
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
}
