package com.karan.schemesaathi.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karan.schemesaathi.data.model.AiConversation
import com.karan.schemesaathi.data.model.UserProfile
import com.karan.schemesaathi.data.pref.UserPreferencesRepository
import com.karan.schemesaathi.data.remote.GeminiAssistantManager
import com.karan.schemesaathi.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: String, // "user" or "assistant"
    val text: String,
    val timestamp: String = System.currentTimeMillis().toString()
)

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isThinking: Boolean = false,
    val selectedLanguage: String = "en",
    val userProfile: UserProfile? = null
)

@HiltViewModel
class AskSaathiViewModel @Inject constructor(
    private val geminiManager: GeminiAssistantManager,
    private val profileRepository: ProfileRepository,
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var currentUserId: String = ""

    init {
        // Welcome message from Saathi
        val initialMsg = ChatMessage(
            sender = "assistant",
            text = "Namaste! I am Saathi, your AI assistant for government schemes, scholarships, jobs, pensions, and farming grants. How can I help you today?"
        )
        _uiState.value = _uiState.value.copy(messages = listOf(initialMsg))

        viewModelScope.launch {
            userPreferences.userIdFlow.collect { userId ->
                if (!userId.isNullOrBlank()) {
                    currentUserId = userId
                    val profile = profileRepository.getProfile(userId)
                    _uiState.value = _uiState.value.copy(userProfile = profile)
                }
            }
        }
        viewModelScope.launch {
            userPreferences.selectedLanguageFlow.collect { lang ->
                _uiState.value = _uiState.value.copy(selectedLanguage = lang)
            }
        }
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        val userMsg = ChatMessage(sender = "user", text = userText)
        val updatedList = _uiState.value.messages + userMsg
        _uiState.value = _uiState.value.copy(messages = updatedList, isThinking = true)

        viewModelScope.launch {
            val response = geminiManager.askSaathi(
                prompt = userText,
                userProfile = _uiState.value.userProfile,
                language = _uiState.value.selectedLanguage
            )

            val replyText = response.getOrNull() ?: "Unable to answer right now. Please try again or check official portals."
            val assistantMsg = ChatMessage(sender = "assistant", text = replyText)

            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + assistantMsg,
                isThinking = false
            )
        }
    }
}
