package com.karan.schemesaathi.ui.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karan.schemesaathi.data.model.Category
import com.karan.schemesaathi.data.model.NotificationPreferences
import com.karan.schemesaathi.data.model.SchemeOpportunity
import com.karan.schemesaathi.data.model.TelegramConnection
import com.karan.schemesaathi.data.pref.UserPreferencesRepository
import com.karan.schemesaathi.data.remote.TelegramBotManager
import com.karan.schemesaathi.data.repository.OpportunityRepository
import com.karan.schemesaathi.data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class AlertsUiState(
    val telegramConn: TelegramConnection? = null,
    val notificationPref: NotificationPreferences = NotificationPreferences(),
    val simulatedAlertMessage: String? = null,
    val isSimulating: Boolean = false
)

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val opportunityRepository: OpportunityRepository,
    private val telegramBotManager: TelegramBotManager,
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlertsUiState())
    val uiState: StateFlow<AlertsUiState> = _uiState.asStateFlow()

    private var currentUserId: String = ""

    init {
        viewModelScope.launch {
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
                userDataRepository.getTelegramConnFlow(userId),
                userDataRepository.getNotificationPrefFlow(userId)
            ) { conn, pref ->
                AlertsUiState(
                    telegramConn = conn,
                    notificationPref = pref ?: NotificationPreferences(userId = userId)
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun openConnectTelegram() {
        telegramBotManager.openTelegramConnect(currentUserId)
    }

    fun updateNotificationPref(pref: NotificationPreferences) {
        viewModelScope.launch {
            userDataRepository.saveNotificationPref(pref.copy(userId = currentUserId))
        }
    }

    fun simulateNewOpportunity() {
        _uiState.value = _uiState.value.copy(isSimulating = true, simulatedAlertMessage = null)
        viewModelScope.launch {
            val sampleId = "sim_${UUID.randomUUID().toString().take(6)}"
            val sampleOpportunity = SchemeOpportunity(
                id = sampleId,
                name = "PM Digital Internship Grant 2026 (Simulated Alert)",
                category = Category.INTERNSHIPS,
                description = "Newly announced government internship program with ₹6,000 monthly stipend.",
                plainLanguageSummary = "Simulated Opportunity: Paid 6-month digital skills internship for students & graduates.",
                minAge = 18,
                maxAge = 28,
                benefits = "₹6,000 / month stipend",
                officialApplicationUrl = "https://pminternship.mca.gov.in",
                sourceOrganization = "Ministry of Corporate Affairs",
                sourceUrl = "https://pminternship.mca.gov.in",
                lastVerifiedDate = "2026-10-10",
                deadline = "2026-12-31" // Added for formatting
            )

            // Save to DB
            opportunityRepository.saveSimulatedOpportunity(sampleOpportunity)

            // HARDCODING YOUR REAL TELEGRAM CHAT ID FOR TESTING: 8190362191
            val realChatId = "8190362191"

            // Simulate Telegram alert and dispatch real HTTP POST message
            val result = telegramBotManager.simulateOpportunityNotification(
                opportunity = sampleOpportunity, 
                userEmail = "user@schemesaathi.in",
                chatId = realChatId
            )
            
            _uiState.value = _uiState.value.copy(
                isSimulating = false,
                simulatedAlertMessage = result.getOrNull()
            )
        }
    }
}
