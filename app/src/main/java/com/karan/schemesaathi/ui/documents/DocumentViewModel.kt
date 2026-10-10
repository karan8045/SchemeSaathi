package com.karan.schemesaathi.ui.documents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karan.schemesaathi.data.model.DocumentStatus
import com.karan.schemesaathi.data.model.UserDocument
import com.karan.schemesaathi.data.pref.UserPreferencesRepository
import com.karan.schemesaathi.data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class DocumentUiState(
    val documents: List<UserDocument> = emptyList(),
    val standardChecklist: List<String> = listOf(
        "Aadhaar Card",
        "Class 10 Marksheet",
        "Class 12 Marksheet",
        "Income Certificate",
        "Domicile Certificate",
        "Caste Certificate",
        "Bank Passbook First Page",
        "Passport Size Photograph"
    ),
    val isLoading: Boolean = false
)

@HiltViewModel
class DocumentViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocumentUiState())
    val uiState: StateFlow<DocumentUiState> = _uiState.asStateFlow()

    private var currentUserId: String = ""

    init {
        viewModelScope.launch {
            userPreferences.userIdFlow.collect { userId ->
                if (!userId.isNullOrBlank()) {
                    currentUserId = userId
                    observeDocuments(userId)
                }
            }
        }
    }

    private fun observeDocuments(userId: String) {
        viewModelScope.launch {
            userDataRepository.getUserDocumentsFlow(userId).collect { docs ->
                _uiState.value = _uiState.value.copy(documents = docs)
            }
        }
    }

    fun addOrUpdateDocument(docName: String, status: DocumentStatus, expiresOn: String? = null, storagePath: String? = null) {
        viewModelScope.launch {
            val doc = UserDocument(
                id = UUID.randomUUID().toString(),
                userId = currentUserId,
                documentType = docName,
                documentName = docName,
                status = status.name,
                expiresOn = expiresOn,
                storagePath = storagePath
            )
            userDataRepository.saveUserDocument(doc)
        }
    }

    fun deleteDocument(id: String) {
        viewModelScope.launch {
            userDataRepository.deleteUserDocument(id)
        }
    }
}
