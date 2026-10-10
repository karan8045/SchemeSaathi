package com.karan.schemesaathi.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile_cache")
data class ProfileEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val dateOfBirth: String,
    val gender: String,
    val state: String,
    val district: String,
    val locationType: String,
    val occupation: String,
    val educationLevel: String,
    val skillsJson: String,
    val employmentStatus: String,
    val incomeRange: String,
    val socialCategory: String,
    val disabilityStatus: Boolean,
    val studentStatus: Boolean,
    val farmerStatus: Boolean,
    val preferredJobTypesJson: String,
    val preferredWorkLocationsJson: String,
    val preferredLanguage: String,
    val interestedCategoriesJson: String,
    val profileComplete: Boolean,
    val updatedAt: String
)

@Entity(tableName = "opportunity_cache")
data class OpportunityEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val description: String,
    val plainLanguageSummary: String,
    val minAge: Int?,
    val maxAge: Int?,
    val state: String?,
    val district: String?,
    val requiredEducation: String?,
    val incomeLimit: Long?,
    val targetOccupation: String?,
    val requiredSkillsJson: String,
    val benefits: String,
    val salaryOrStipend: String?,
    val duration: String?,
    val termsAndConditions: String,
    val requiredDocumentsJson: String,
    val deadline: String?,
    val officialApplicationUrl: String,
    val sourceOrganization: String,
    val sourceUrl: String,
    val lastVerifiedDate: String,
    val isCentralScheme: Boolean,
    val isRemote: Boolean,
    val targetGender: String?,
    val targetSocialCategory: String?,
    val requiresFarmerStatus: Boolean,
    val requiresStudentStatus: Boolean,
    val requiresDisabilityStatus: Boolean
)

@Entity(tableName = "saved_items_cache")
data class SavedItemEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val itemType: String,
    val itemId: String,
    val notes: String?,
    val createdAt: String
)

@Entity(tableName = "application_tracking_cache")
data class ApplicationTrackingEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val itemType: String,
    val itemId: String,
    val status: String,
    val notes: String?,
    val updatedAt: String
)

@Entity(tableName = "deadline_reminders_cache")
data class DeadlineReminderEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val itemType: String,
    val itemId: String,
    val reminderDate: String,
    val isEnabled: Boolean,
    val createdAt: String
)

@Entity(tableName = "user_documents_cache")
data class UserDocumentEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val documentType: String,
    val documentName: String,
    val status: String,
    val expiresOn: String?,
    val storagePath: String?,
    val updatedAt: String
)

@Entity(tableName = "notification_pref_cache")
data class NotificationPrefEntity(
    @PrimaryKey val userId: String,
    val scholarships: Boolean,
    val jobs: Boolean,
    val internships: Boolean,
    val apprenticeships: Boolean,
    val pensions: Boolean,
    val healthInsurance: Boolean,
    val farming: Boolean,
    val education: Boolean,
    val housing: Boolean,
    val skillDevelopment: Boolean,
    val selfEmployment: Boolean,
    val womenAndChildSupport: Boolean,
    val disabilitySupport: Boolean,
    val frequency: String,
    val quietHoursStart: String?,
    val quietHoursEnd: String?,
    val preferredLanguage: String,
    val enabled: Boolean
)

@Entity(tableName = "telegram_conn_cache")
data class TelegramConnEntity(
    @PrimaryKey val userId: String,
    val telegramChatId: String,
    val telegramUsername: String?,
    val isActive: Boolean,
    val connectedAt: String
)
