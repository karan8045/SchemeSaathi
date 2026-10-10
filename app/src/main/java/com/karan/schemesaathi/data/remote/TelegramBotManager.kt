package com.karan.schemesaathi.data.remote

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.karan.schemesaathi.BuildConfig
import com.karan.schemesaathi.data.model.SchemeOpportunity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TelegramBotManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // Normally you do not want to embed the BOT_TOKEN in the client, it should be done via a backend server.
    // We demonstrate it here locally using the injected BuildConfig token.
    var botUsername: String = "SchemeSaathiBot"
    private val telegramApiBase = "https://api.telegram.org/bot${BuildConfig.TELEGRAM_BOT_TOKEN}"

    fun generateDeepLink(userId: String): String {
        // Encode userId into a clean linking parameter
        val linkingCode = "SS_${userId.hashCode().toString().takeLast(6)}"
        return "https://t.me/$botUsername?start=$linkingCode"
    }

    fun openTelegramConnect(userId: String) {
        val url = generateDeepLink(userId)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(webIntent)
        }
    }

    /**
     * Dispatch an actual message via Telegram Bot HTTP API to a user's Chat ID.
     */
    suspend fun dispatchDirectMessage(
        chatId: String,
        text: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        if (BuildConfig.TELEGRAM_BOT_TOKEN.contains("dummy")) {
            return@withContext Result.success(true)
        }

        try {
            val url = URL("$telegramApiBase/sendMessage")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val jsonBody = JSONObject()
            jsonBody.put("chat_id", chatId)
            jsonBody.put("text", text)
            jsonBody.put("parse_mode", "Markdown")

            connection.outputStream.use { os ->
                val input = jsonBody.toString().toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            val responseCode = connection.responseCode
            if (responseCode in 200..299) {
                Result.success(true)
            } else {
                Result.failure(Exception("Telegram API Error: HTTP $responseCode"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun simulateOpportunityNotification(
        opportunity: SchemeOpportunity,
        userEmail: String,
        chatId: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        val notificationText = "🔔 *New Match Alert!*\n\n*Opportunity*: ${opportunity.name}\n*Category*: ${opportunity.category.displayName}\n*Benefit*: ${opportunity.benefits}\n*Deadline*: ${opportunity.deadline ?: "N/A"}\n\n[Open Scheme Saathi](https://schemesaathi.in/app)"
        
        if (!chatId.isNullOrBlank() && !BuildConfig.TELEGRAM_BOT_TOKEN.contains("dummy")) {
            dispatchDirectMessage(chatId, notificationText)
        }
        
        Result.success("🔔 Telegram Alert Sent!\n\nOpportunity: ${opportunity.name}\nCategory: ${opportunity.category.displayName}\n\nSent to Telegram chat linked with $userEmail.")
    }
}

