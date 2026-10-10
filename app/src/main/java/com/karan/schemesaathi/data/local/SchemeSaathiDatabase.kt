package com.karan.schemesaathi.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ProfileEntity::class,
        OpportunityEntity::class,
        SavedItemEntity::class,
        ApplicationTrackingEntity::class,
        DeadlineReminderEntity::class,
        UserDocumentEntity::class,
        NotificationPrefEntity::class,
        TelegramConnEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SchemeSaathiDatabase : RoomDatabase() {
    abstract fun dao(): SchemeSaathiDao
}
