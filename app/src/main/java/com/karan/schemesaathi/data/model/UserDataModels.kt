package com.karan.schemesaathi.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SavedItem(
    @SerialName("id") val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("item_type") val itemType: String = "", // scheme, job, etc.
    @SerialName("item_id") val itemId: String = "",
    @SerialName("notes") val notes: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class ApplicationTracking(
    @SerialName("id") val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("item_type") val itemType: String = "",
    @SerialName("item_id") val itemId: String = "",
    @SerialName("status") val status: String = ApplicationStatus.NOT_STARTED.name,
    @SerialName("notes") val notes: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class DeadlineReminder(
    @SerialName("id") val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("item_type") val itemType: String = "",
    @SerialName("item_id") val itemId: String = "",
    @SerialName("reminder_date") val reminderDate: String = "",
    @SerialName("is_enabled") val isEnabled: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class UserDocument(
    @SerialName("id") val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("document_type") val documentType: String = "",
    @SerialName("document_name") val documentName: String = "",
    @SerialName("status") val status: String = DocumentStatus.MISSING.name,
    @SerialName("expires_on") val expiresOn: String? = null,
    @SerialName("storage_path") val storagePath: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class NotificationPreferences(
    @SerialName("user_id") val userId: String = "",
    @SerialName("scholarships") val scholarships: Boolean = true,
    @SerialName("jobs") val jobs: Boolean = true,
    @SerialName("internships") val internships: Boolean = true,
    @SerialName("apprenticeships") val apprenticeships: Boolean = true,
    @SerialName("pensions") val pensions: Boolean = true,
    @SerialName("health_insurance") val healthInsurance: Boolean = true,
    @SerialName("farming") val farming: Boolean = true,
    @SerialName("education") val education: Boolean = true,
    @SerialName("housing") val housing: Boolean = true,
    @SerialName("skill_development") val skillDevelopment: Boolean = true,
    @SerialName("self_employment") val selfEmployment: Boolean = true,
    @SerialName("women_and_child_support") val womenAndChildSupport: Boolean = true,
    @SerialName("disability_support") val disabilitySupport: Boolean = true,
    @SerialName("frequency") val frequency: String = "instant", // instant, daily, weekly
    @SerialName("quiet_hours_start") val quietHoursStart: String? = "22:00",
    @SerialName("quiet_hours_end") val quietHoursEnd: String? = "07:00",
    @SerialName("preferred_language") val preferredLanguage: String = "en",
    @SerialName("enabled") val enabled: Boolean = true,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class TelegramConnection(
    @SerialName("user_id") val userId: String = "",
    @SerialName("telegram_chat_id") val telegramChatId: String = "",
    @SerialName("telegram_username") val telegramUsername: String? = null,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("connected_at") val connectedAt: String? = null,
    @SerialName("last_seen_at") val lastSeenAt: String? = null
)

@Serializable
data class AiConversation(
    @SerialName("id") val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("context_type") val contextType: String? = null,
    @SerialName("context_id") val contextId: String? = null,
    @SerialName("user_message") val userMessage: String = "",
    @SerialName("assistant_message") val assistantMessage: String = "",
    @SerialName("language") val language: String = "en",
    @SerialName("created_at") val createdAt: String? = null
)
