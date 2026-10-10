package com.karan.schemesaathi.data.remote

import com.karan.schemesaathi.BuildConfig
import com.karan.schemesaathi.data.model.ApplicationTracking
import com.karan.schemesaathi.data.model.DeadlineReminder
import com.karan.schemesaathi.data.model.NotificationPreferences
import com.karan.schemesaathi.data.model.SavedItem
import com.karan.schemesaathi.data.model.TelegramConnection
import com.karan.schemesaathi.data.model.UserDocument
import com.karan.schemesaathi.data.model.UserProfile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseClientManager @Inject constructor() {

    // Configurable parameters sourced from BuildConfig
    var supabaseUrl: String = BuildConfig.SUPABASE_URL
    var supabaseAnonKey: String = BuildConfig.SUPABASE_ANON_KEY

    private var client: SupabaseClient? = null

    init {
        initClient()
    }

    private fun initClient() {
        try {
            client = createSupabaseClient(
                supabaseUrl = supabaseUrl,
                supabaseKey = supabaseAnonKey
            ) {
                install(io.github.jan.supabase.auth.Auth)
                install(io.github.jan.supabase.postgrest.Postgrest)
            }
        } catch (_: Exception) {
            client = null
        }
    }

    fun isConfigured(): Boolean {
        return !supabaseUrl.contains("xyzexample") && !supabaseAnonKey.contains("dummykey")
    }

    suspend fun sendEmailOtp(email: String): Result<Boolean> = withContext(Dispatchers.IO) {
        if (!isConfigured() || client == null) {
            // Demo mode fallback: simulate sending OTP successfully
            return@withContext Result.success(true)
        }
        return@withContext try {
            client!!.auth.signInWith(OTP) {
                this.email = email
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyEmailOtp(email: String, otp: String): Result<String> = withContext(Dispatchers.IO) {
        if (!isConfigured() || client == null) {
            // Demo mode fallback: if OTP is "123456" or any 6 digit string, return a deterministic demo user ID
            if (otp.length == 6) {
                val demoUserId = "demo_user_${email.replace("@", "_").replace(".", "_")}"
                return@withContext Result.success(demoUserId)
            } else {
                return@withContext Result.failure(Exception("Invalid OTP code. Please enter 123456 in demo mode."))
            }
        }
        return@withContext try {
            client!!.auth.verifyEmailOtp(
                type = io.github.jan.supabase.auth.OtpType.Email.EMAIL,
                email = email,
                token = otp
            )
            val sessionUser = client!!.auth.currentUserOrNull()
            if (sessionUser != null) {
                Result.success(sessionUser.id)
            } else {
                Result.failure(Exception("Failed to retrieve authenticated user session."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchProfile(userId: String): Result<UserProfile?> = withContext(Dispatchers.IO) {
        if (!isConfigured() || client == null) {
            return@withContext Result.success(null)
        }
        return@withContext try {
            val profile = client!!.postgrest["profiles"]
                .select { filter { eq("id", userId) } }
                .decodeSingleOrNull<UserProfile>()
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveProfile(profile: UserProfile): Result<Boolean> = withContext(Dispatchers.IO) {
        if (!isConfigured() || client == null) {
            return@withContext Result.success(true)
        }
        return@withContext try {
            client!!.postgrest["profiles"].upsert(profile)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProfile(userId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        if (!isConfigured() || client == null) {
            return@withContext Result.success(true)
        }
        return@withContext try {
            client!!.postgrest["profiles"].delete { filter { eq("id", userId) } }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchNotificationPreferences(userId: String): Result<NotificationPreferences?> = withContext(Dispatchers.IO) {
        if (!isConfigured() || client == null) return@withContext Result.success(null)
        return@withContext try {
            val pref = client!!.postgrest["notification_preferences"]
                .select { filter { eq("user_id", userId) } }
                .decodeSingleOrNull<NotificationPreferences>()
            Result.success(pref)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveNotificationPreferences(pref: NotificationPreferences): Result<Boolean> = withContext(Dispatchers.IO) {
        if (!isConfigured() || client == null) return@withContext Result.success(true)
        return@withContext try {
            client!!.postgrest["notification_preferences"].upsert(pref)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchTelegramConnection(userId: String): Result<TelegramConnection?> = withContext(Dispatchers.IO) {
        if (!isConfigured() || client == null) return@withContext Result.success(null)
        return@withContext try {
            val conn = client!!.postgrest["telegram_connections"]
                .select { filter { eq("user_id", userId) } }
                .decodeSingleOrNull<TelegramConnection>()
            Result.success(conn)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveTelegramConnection(conn: TelegramConnection): Result<Boolean> = withContext(Dispatchers.IO) {
        if (!isConfigured() || client == null) return@withContext Result.success(true)
        return@withContext try {
            client!!.postgrest["telegram_connections"].upsert(conn)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchSavedItems(userId: String): Result<List<SavedItem>> = withContext(Dispatchers.IO) {
        if (!isConfigured() || client == null) return@withContext Result.success(emptyList())
        return@withContext try {
            val items = client!!.postgrest["saved_items"]
                .select { filter { eq("user_id", userId) } }
                .decodeList<SavedItem>()
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveSavedItem(item: SavedItem): Result<Boolean> = withContext(Dispatchers.IO) {
        if (!isConfigured() || client == null) return@withContext Result.success(true)
        return@withContext try {
            client!!.postgrest["saved_items"].upsert(item)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchApplicationTracking(userId: String): Result<List<ApplicationTracking>> = withContext(Dispatchers.IO) {
        if (!isConfigured() || client == null) return@withContext Result.success(emptyList())
        return@withContext try {
            val list = client!!.postgrest["application_tracking"]
                .select { filter { eq("user_id", userId) } }
                .decodeList<ApplicationTracking>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveApplicationTracking(tracking: ApplicationTracking): Result<Boolean> = withContext(Dispatchers.IO) {
        if (!isConfigured() || client == null) return@withContext Result.success(true)
        return@withContext try {
            client!!.postgrest["application_tracking"].upsert(tracking)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchDeadlineReminders(userId: String): Result<List<DeadlineReminder>> = withContext(Dispatchers.IO) {
        if (!isConfigured() || client == null) return@withContext Result.success(emptyList())
        return@withContext try {
            val list = client!!.postgrest["deadline_reminders"]
                .select { filter { eq("user_id", userId) } }
                .decodeList<DeadlineReminder>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchUserDocuments(userId: String): Result<List<UserDocument>> = withContext(Dispatchers.IO) {
        if (!isConfigured() || client == null) return@withContext Result.success(emptyList())
        return@withContext try {
            val list = client!!.postgrest["user_documents"]
                .select { filter { eq("user_id", userId) } }
                .decodeList<UserDocument>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut(): Result<Boolean> = withContext(Dispatchers.IO) {
        if (!isConfigured() || client == null) return@withContext Result.success(true)
        return@withContext try {
            client!!.auth.signOut()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
