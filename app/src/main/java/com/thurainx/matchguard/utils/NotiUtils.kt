package com.thurainx.matchguard.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.content.ContextCompat.getSystemService
import com.thurainx.matchguard.R

const val OWN_CHANNEL = "OWN_CHANNEL"

object NotiUtils {
    fun buildNotification(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +"://" + context.packageName + "/" + R.raw.marci_whistle);
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val notificationChannel = NotificationChannel(
                OWN_CHANNEL,
                "Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationChannel.setSound(soundUri,attributes)
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}