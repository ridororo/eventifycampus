package com.rido.eventifycampus

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object NotificationHelper {

    private const val CHANNEL_ID = "event_channel"
    private const val CHANNEL_NAME = "Event Notification"

    // 🔧 Buat channel (WAJIB Android 8+)
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi event kampus"
            }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    // 🔔 Tampil notif TANPA JAM
    fun showNotification(
        context: Context,
        title: String,
        message: String
    ) {
        val appContext = context.applicationContext

        // cek izin (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notif = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

            // 🔥 HILANGIN JAM
            .setShowWhen(false)
            .setWhen(0)
            .setUsesChronometer(false)

            .build()

        NotificationManagerCompat.from(appContext).notify(
            System.currentTimeMillis().toInt(),
            notif
        )
    }

    // ⏱ Reminder (delay)
    fun scheduleReminderSimple(
        context: Context,
        title: String,
        message: String,
        delayMillis: Long
    ) {
        val appContext = context.applicationContext

        Handler(Looper.getMainLooper()).postDelayed({
            showNotification(appContext, title, message)
        }, delayMillis)
    }
}