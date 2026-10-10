package com.karan.schemesaathi.data.repository

import com.karan.schemesaathi.data.local.NotificationPrefEntity
import com.karan.schemesaathi.data.local.ProfileEntity
import com.karan.schemesaathi.data.local.SchemeSaathiDao
import com.karan.schemesaathi.data.local.TelegramConnEntity
import com.karan.schemesaathi.data.model.NotificationPreferences
import com.karan.schemesaathi.data.model.TelegramConnection
import com.karan.schemesaathi.data.model.UserProfile
import com.karan.schemesaathi.data.pref.UserPreferencesRepository
import com.karan.schemesaathi.data.remote.SupabaseClientManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val dao: SchemeSaathiDao,
    private val supabaseManager: SupabaseClientManager,
    private val userPreferences: UserPreferencesRepository
) {

    fun getProfileFlow(userId: String): Flow<UserProfile?> {
        return dao.getProfileFlow(userId).map { entity ->
            entity?.toDomainModel()
        }
    }

    suspend fun getProfile(userId: String): UserProfile? = withContext(Dispatchers.IO) {
        val cached = dao.getProfile(userId)
        if (cached != null) return@withContext cached.toDomainModel()

        val remoteResult = supabaseManager.fetchProfile(userId)
        val profile = remoteResult.getOrNull()
        if (profile != null) {
            dao.saveProfile(profile.toEntity())
        }
        profile
    }

    suspend fun saveProfile(profile: UserProfile): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            dao.saveProfile(profile.toEntity())
            userPreferences.setOnboarded(profile.profileComplete)
            supabaseManager.saveProfile(profile)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restoreUserDataFromCloud(userId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val profileRes = supabaseManager.fetchProfile(userId)
            val profile = profileRes.getOrNull()
            if (profile != null) {
                dao.saveProfile(profile.toEntity())
                userPreferences.setOnboarded(profile.profileComplete)
            }

            val notifRes = supabaseManager.fetchNotificationPreferences(userId)
            val notif = notifRes.getOrNull()
            if (notif != null) {
                dao.saveNotificationPref(notif.toEntity())
            }

            val tgRes = supabaseManager.fetchTelegramConnection(userId)
            val tg = tgRes.getOrNull()
            if (tg != null) {
                dao.saveTelegramConn(tg.toEntity())
            }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAccountAndData(userId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            supabaseManager.deleteProfile(userId)
            dao.clearAllProfiles()
            dao.clearAllSavedItems()
            dao.clearAllTracking()
            dao.clearAllReminders()
            dao.clearAllDocuments()
            userPreferences.clearSession()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun ProfileEntity.toDomainModel(): UserProfile {
        return UserProfile(
            id = id,
            fullName = fullName,
            dateOfBirth = dateOfBirth,
            gender = gender,
            state = state,
            district = district,
            locationType = locationType,
            occupation = occupation,
            educationLevel = educationLevel,
            skills = skillsJson.split(",").filter { it.isNotBlank() },
            employmentStatus = employmentStatus,
            incomeRange = incomeRange,
            socialCategory = socialCategory,
            disabilityStatus = disabilityStatus,
            studentStatus = studentStatus,
            farmerStatus = farmerStatus,
            preferredJobTypes = preferredJobTypesJson.split(",").filter { it.isNotBlank() },
            preferredWorkLocations = preferredWorkLocationsJson.split(",").filter { it.isNotBlank() },
            preferredLanguage = preferredLanguage,
            interestedCategories = interestedCategoriesJson.split(",").filter { it.isNotBlank() },
            profileComplete = profileComplete,
            updatedAt = updatedAt
        )
    }

    private fun UserProfile.toEntity(): ProfileEntity {
        return ProfileEntity(
            id = id,
            fullName = fullName,
            dateOfBirth = dateOfBirth,
            gender = gender,
            state = state,
            district = district,
            locationType = locationType,
            occupation = occupation,
            educationLevel = educationLevel,
            skillsJson = skills.joinToString(","),
            employmentStatus = employmentStatus,
            incomeRange = incomeRange,
            socialCategory = socialCategory,
            disabilityStatus = disabilityStatus,
            studentStatus = studentStatus,
            farmerStatus = farmerStatus,
            preferredJobTypesJson = preferredJobTypes.joinToString(","),
            preferredWorkLocationsJson = preferredWorkLocations.joinToString(","),
            preferredLanguage = preferredLanguage,
            interestedCategoriesJson = interestedCategories.joinToString(","),
            profileComplete = profileComplete,
            updatedAt = System.currentTimeMillis().toString()
        )
    }

    private fun NotificationPreferences.toEntity(): NotificationPrefEntity {
        return NotificationPrefEntity(
            userId = userId,
            scholarships = scholarships,
            jobs = jobs,
            internships = internships,
            apprenticeships = apprenticeships,
            pensions = pensions,
            healthInsurance = healthInsurance,
            farming = farming,
            education = education,
            housing = housing,
            skillDevelopment = skillDevelopment,
            selfEmployment = selfEmployment,
            womenAndChildSupport = womenAndChildSupport,
            disabilitySupport = disabilitySupport,
            frequency = frequency,
            quietHoursStart = quietHoursStart,
            quietHoursEnd = quietHoursEnd,
            preferredLanguage = preferredLanguage,
            enabled = enabled
        )
    }

    private fun TelegramConnection.toEntity(): TelegramConnEntity {
        return TelegramConnEntity(
            userId = userId,
            telegramChatId = telegramChatId,
            telegramUsername = telegramUsername,
            isActive = isActive,
            connectedAt = connectedAt ?: System.currentTimeMillis().toString()
        )
    }
}
