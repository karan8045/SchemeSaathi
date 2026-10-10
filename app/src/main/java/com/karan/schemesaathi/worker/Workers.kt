package com.karan.schemesaathi.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.karan.schemesaathi.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DeadlineReminderWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val title = inputData.getString("title") ?: "Scheme Saathi Deadline Alert"
        val message = inputData.getString("message") ?: "An application deadline is approaching soon."

        sendLocalNotification(title, message)
        return Result.success()
    }

    @Suppress("MissingPermission")
    private fun sendLocalNotification(title: String, message: String) {
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "scheme_saathi_reminders"

        val channel = NotificationChannel(
            channelId,
            "Scheme Saathi Reminders",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(appContext, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .build()

        try {
            notificationManager.notify(System.currentTimeMillis().toInt(), notification)
        } catch (_: SecurityException) {
            // Permission not granted on Android 13+
        }
    }
}

@HiltWorker
class OpportunitySyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // Perform background opportunity sync
        return Result.success()
    }
}
