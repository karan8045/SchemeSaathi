package com.karan.schemesaathi.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SchemeSaathiDao {

    // Profile
    @Query("SELECT * FROM profile_cache WHERE id = :userId LIMIT 1")
    fun getProfileFlow(userId: String): Flow<ProfileEntity?>

    @Query("SELECT * FROM profile_cache WHERE id = :userId LIMIT 1")
    suspend fun getProfile(userId: String): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: ProfileEntity)

    @Query("DELETE FROM profile_cache WHERE id = :userId")
    suspend fun deleteProfile(userId: String)

    // Opportunities
    @Query("SELECT * FROM opportunity_cache")
    fun getAllOpportunitiesFlow(): Flow<List<OpportunityEntity>>

    @Query("SELECT * FROM opportunity_cache")
    suspend fun getAllOpportunities(): List<OpportunityEntity>

    @Query("SELECT * FROM opportunity_cache WHERE id = :id LIMIT 1")
    suspend fun getOpportunityById(id: String): OpportunityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveOpportunities(opportunities: List<OpportunityEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveOpportunity(opportunity: OpportunityEntity)

    // Saved Items
    @Query("SELECT * FROM saved_items_cache WHERE userId = :userId")
    fun getSavedItemsFlow(userId: String): Flow<List<SavedItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSavedItem(savedItem: SavedItemEntity)

    @Query("DELETE FROM saved_items_cache WHERE userId = :userId AND itemType = :itemType AND itemId = :itemId")
    suspend fun removeSavedItem(userId: String, itemType: String, itemId: String)

    @Query("DELETE FROM saved_items_cache WHERE userId = :userId")
    suspend fun clearSavedItems(userId: String)

    // Application Tracking
    @Query("SELECT * FROM application_tracking_cache WHERE userId = :userId")
    fun getApplicationTrackingFlow(userId: String): Flow<List<ApplicationTrackingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveApplicationTracking(tracking: ApplicationTrackingEntity)

    // Deadline Reminders
    @Query("SELECT * FROM deadline_reminders_cache WHERE userId = :userId")
    fun getDeadlineRemindersFlow(userId: String): Flow<List<DeadlineReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDeadlineReminder(reminder: DeadlineReminderEntity)

    @Query("DELETE FROM deadline_reminders_cache WHERE id = :id")
    suspend fun deleteDeadlineReminder(id: String)

    // User Documents
    @Query("SELECT * FROM user_documents_cache WHERE userId = :userId")
    fun getUserDocumentsFlow(userId: String): Flow<List<UserDocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserDocument(document: UserDocumentEntity)

    @Query("DELETE FROM user_documents_cache WHERE id = :documentId")
    suspend fun deleteUserDocument(documentId: String)

    // Notification Prefs
    @Query("SELECT * FROM notification_pref_cache WHERE userId = :userId LIMIT 1")
    fun getNotificationPrefFlow(userId: String): Flow<NotificationPrefEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNotificationPref(pref: NotificationPrefEntity)

    // Telegram Connection
    @Query("SELECT * FROM telegram_conn_cache WHERE userId = :userId LIMIT 1")
    fun getTelegramConnFlow(userId: String): Flow<TelegramConnEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTelegramConn(conn: TelegramConnEntity)

    @Query("DELETE FROM telegram_conn_cache WHERE userId = :userId")
    suspend fun deleteTelegramConn(userId: String)

    // Clear all user cache on sign out / account deletion
    @Query("DELETE FROM profile_cache")
    suspend fun clearAllProfiles()

    @Query("DELETE FROM saved_items_cache")
    suspend fun clearAllSavedItems()

    @Query("DELETE FROM application_tracking_cache")
    suspend fun clearAllTracking()

    @Query("DELETE FROM deadline_reminders_cache")
    suspend fun clearAllReminders()

    @Query("DELETE FROM user_documents_cache")
    suspend fun clearAllDocuments()
}
