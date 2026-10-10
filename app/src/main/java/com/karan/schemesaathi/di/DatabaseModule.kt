package com.karan.schemesaathi.di

import android.content.Context
import androidx.room.Room
import com.karan.schemesaathi.data.local.SchemeSaathiDao
import com.karan.schemesaathi.data.local.SchemeSaathiDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SchemeSaathiDatabase {
        return Room.databaseBuilder(
            context,
            SchemeSaathiDatabase::class.java,
            "scheme_saathi_database.db"
        ).fallbackToDestructiveMigration(true).build()
    }

    @Provides
    fun provideSchemeSaathiDao(database: SchemeSaathiDatabase): SchemeSaathiDao {
        return database.dao()
    }
}
