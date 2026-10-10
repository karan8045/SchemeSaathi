package com.karan.schemesaathi.data.repository

import com.karan.schemesaathi.data.local.ApplicationTrackingEntity
import com.karan.schemesaathi.data.local.DeadlineReminderEntity
import com.karan.schemesaathi.data.local.NotificationPrefEntity
import com.karan.schemesaathi.data.local.SavedItemEntity
import com.karan.schemesaathi.data.local.SchemeSaathiDao
import com.karan.schemesaathi.data.local.TelegramConnEntity
import com.karan.schemesaathi.data.local.UserDocumentEntity
import com.karan.schemesaathi.data.model.ApplicationStatus
import com.karan.schemesaathi.data.model.ApplicationTracking
import com.karan.schemesaathi.data.model.DeadlineReminder
import com.karan.schemesaathi.data.model.DocumentStatus
import com.karan.schemesaathi.data.model.NotificationPreferences
import com.karan.schemesaathi.data.model.SavedItem
import com.karan.schemesaathi.data.model.TelegramConnection
import com.karan.schemesaathi.data.model.UserDocument
import com.karan.schemesaathi.data.remote.SupabaseClientManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataRepository @Inject constructor(
    private val dao: SchemeSaathiDao,
    private val supabaseManager: SupabaseClientManager
) {

    // Saved Items
    fun getSavedItemsFlow(userId: String): Flow<List<SavedItem>> {
        return dao.getSavedItemsFlow(userId).map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun saveItem(userId: String, itemType: String, itemId: String, notes: String? = null) = withContext(Dispatchers.IO) {
        val id = UUID.randomUUID().toString()
        val item = SavedItem(id = id, userId = userId, itemType = itemType, itemId = itemId, notes = notes, createdAt = System.currentTimeMillis().toString())
        dao.saveSavedItem(item.toEntity())
        supabaseManager.saveSavedItem(item)
    }

    suspend fun removeSavedItem(userId: String, itemType: String, itemId: String) = withContext(Dispatchers.IO) {
        dao.removeSavedItem(userId, itemType, itemId)
    }

    // Application Tracking
    fun getApplicationTrackingFlow(userId: String): Flow<List<ApplicationTracking>> {
        return dao.getApplicationTrackingFlow(userId).map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun updateApplicationStatus(userId: String, itemType: String, itemId: String, status: ApplicationStatus, notes: String? = null) = withContext(Dispatchers.IO) {
        val id = UUID.randomUUID().toString()
        val tracking = ApplicationTracking(id = id, userId = userId, itemType = itemType, itemId = itemId, status = status.name, notes = notes, updatedAt = System.currentTimeMillis().toString())
        dao.saveApplicationTracking(tracking.toEntity())
        supabaseManager.saveApplicationTracking(tracking)
    }

    // Deadline Reminders
    fun getDeadlineRemindersFlow(userId: String): Flow<List<DeadlineReminder>> {
        return dao.getDeadlineRemindersFlow(userId).map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun addDeadlineReminder(userId: String, itemType: String, itemId: String, reminderDate: String) = withContext(Dispatchers.IO) {
        val id = UUID.randomUUID().toString()
        val reminder = DeadlineReminder(id = id, userId = userId, itemType = itemType, itemId = itemId, reminderDate = reminderDate, isEnabled = true, createdAt = System.currentTimeMillis().toString())
        dao.saveDeadlineReminder(reminder.toEntity())
    }

    suspend fun deleteDeadlineReminder(id: String) = withContext(Dispatchers.IO) {
        dao.deleteDeadlineReminder(id)
    }

    // User Documents
    fun getUserDocumentsFlow(userId: String): Flow<List<UserDocument>> {
        return dao.getUserDocumentsFlow(userId).map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun saveUserDocument(doc: UserDocument) = withContext(Dispatchers.IO) {
        dao.saveUserDocument(doc.toEntity())
    }

    suspend fun deleteUserDocument(documentId: String) = withContext(Dispatchers.IO) {
        dao.deleteUserDocument(documentId)
    }

    // Notification Preferences
    fun getNotificationPrefFlow(userId: String): Flow<NotificationPreferences?> {
        return dao.getNotificationPrefFlow(userId).map { it?.toDomain() }
    }

    suspend fun saveNotificationPref(pref: NotificationPreferences) = withContext(Dispatchers.IO) {
        dao.saveNotificationPref(pref.toEntity())
        supabaseManager.saveNotificationPreferences(pref)
    }

    // Telegram Connection
    fun getTelegramConnFlow(userId: String): Flow<TelegramConnection?> {
        return dao.getTelegramConnFlow(userId).map { it?.toDomain() }
    }

    suspend fun saveTelegramConn(conn: TelegramConnection) = withContext(Dispatchers.IO) {
        dao.saveTelegramConn(conn.toEntity())
        supabaseManager.saveTelegramConnection(conn)
    }

    suspend fun deleteTelegramConn(userId: String) = withContext(Dispatchers.IO) {
        dao.deleteTelegramConn(userId)
    }

    // Mappers
    private fun SavedItemEntity.toDomain() = SavedItem(id = id, userId = userId, itemType = itemType, itemId = itemId, notes = notes, createdAt = createdAt)
    private fun SavedItem.toEntity() = SavedItemEntity(id = id, userId = userId, itemType = itemType, itemId = itemId, notes = notes, createdAt = createdAt ?: System.currentTimeMillis().toString())

    private fun ApplicationTrackingEntity.toDomain() = ApplicationTracking(id = id, userId = userId, itemType = itemType, itemId = itemId, status = status, notes = notes, updatedAt = updatedAt)
    private fun ApplicationTracking.toEntity() = ApplicationTrackingEntity(id = id, userId = userId, itemType = itemType, itemId = itemId, status = status, notes = notes, updatedAt = updatedAt ?: System.currentTimeMillis().toString())

    private fun DeadlineReminderEntity.toDomain() = DeadlineReminder(id = id, userId = userId, itemType = itemType, itemId = itemId, reminderDate = reminderDate, isEnabled = isEnabled, createdAt = createdAt)
    private fun DeadlineReminder.toEntity() = DeadlineReminderEntity(id = id, userId = userId, itemType = itemType, itemId = itemId, reminderDate = reminderDate, isEnabled = isEnabled, createdAt = createdAt ?: System.currentTimeMillis().toString())

    private fun UserDocumentEntity.toDomain() = UserDocument(id = id, userId = userId, documentType = documentType, documentName = documentName, status = status, expiresOn = expiresOn, storagePath = storagePath, updatedAt = updatedAt)
    private fun UserDocument.toEntity() = UserDocumentEntity(id = id, userId = userId, documentType = documentType, documentName = documentName, status = status, expiresOn = expiresOn, storagePath = storagePath, updatedAt = updatedAt ?: System.currentTimeMillis().toString())

    private fun NotificationPrefEntity.toDomain() = NotificationPreferences(
        userId = userId, scholarships = scholarships, jobs = jobs, internships = internships, apprenticeships = apprenticeships, pensions = pensions, healthInsurance = healthInsurance, farming = farming, education = education, housing = housing, skillDevelopment = skillDevelopment, selfEmployment = selfEmployment, womenAndChildSupport = womenAndChildSupport, disabilitySupport = disabilitySupport, frequency = frequency, quietHoursStart = quietHoursStart, quietHoursEnd = quietHoursEnd, preferredLanguage = preferredLanguage, enabled = enabled
    )
    private fun NotificationPreferences.toEntity() = NotificationPrefEntity(
        userId = userId, scholarships = scholarships, jobs = jobs, internships = internships, apprenticeships = apprenticeships, pensions = pensions, healthInsurance = healthInsurance, farming = farming, education = education, housing = housing, skillDevelopment = skillDevelopment, selfEmployment = selfEmployment, womenAndChildSupport = womenAndChildSupport, disabilitySupport = disabilitySupport, frequency = frequency, quietHoursStart = quietHoursStart, quietHoursEnd = quietHoursEnd, preferredLanguage = preferredLanguage, enabled = enabled
    )

    private fun TelegramConnEntity.toDomain() = TelegramConnection(userId = userId, telegramChatId = telegramChatId, telegramUsername = telegramUsername, isActive = isActive, connectedAt = connectedAt)
    private fun TelegramConnection.toEntity() = TelegramConnEntity(userId = userId, telegramChatId = telegramChatId, telegramUsername = telegramUsername, isActive = isActive, connectedAt = connectedAt ?: System.currentTimeMillis().toString())
}
