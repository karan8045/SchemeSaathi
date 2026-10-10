package com.karan.schemesaathi.ui.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karan.schemesaathi.data.model.ApplicationStatus
import com.karan.schemesaathi.data.model.ApplicationTracking
import com.karan.schemesaathi.data.model.SchemeOpportunity
import com.karan.schemesaathi.data.pref.UserPreferencesRepository
import com.karan.schemesaathi.data.repository.OpportunityRepository
import com.karan.schemesaathi.data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SavedUiState(
    val savedOpportunities: List<SchemeOpportunity> = emptyList(),
    val applicationTrackingMap: Map<String, ApplicationTracking> = emptyMap(),
    val isLoading: Boolean = false
)

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val opportunityRepository: OpportunityRepository,
    private val userDataRepository: UserDataRepository,
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedUiState())
    val uiState: StateFlow<SavedUiState> = _uiState.asStateFlow()

    private var currentUserId: String = ""

    init {
        viewModelScope.launch {
            userPreferences.userIdFlow.collect { userId ->
                if (!userId.isNullOrBlank()) {
                    currentUserId = userId
                    observeSavedData(userId)
                }
            }
        }
    }

    private fun observeSavedData(userId: String) {
        viewModelScope.launch {
            combine(
                userDataRepository.getSavedItemsFlow(userId),
                userDataRepository.getApplicationTrackingFlow(userId),
                opportunityRepository.getAllOpportunitiesFlow()
            ) { savedItems, trackingList, allOpps ->
                val savedIds = savedItems.map { it.itemId }.toSet()
                val savedOpps = allOpps.filter { savedIds.contains(it.id) }
                val trackingMap = trackingList.associateBy { it.itemId }

                SavedUiState(
                    savedOpportunities = savedOpps,
                    applicationTrackingMap = trackingMap,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun removeSaved(opportunityId: String) {
        viewModelScope.launch {
            userDataRepository.removeSavedItem(currentUserId, "opportunity", opportunityId)
        }
    }

    fun updateStatus(opportunityId: String, newStatus: ApplicationStatus) {
        viewModelScope.launch {
            userDataRepository.updateApplicationStatus(currentUserId, "opportunity", opportunityId, newStatus)
        }
    }
}
