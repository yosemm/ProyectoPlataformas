package com.uvg.mashoras.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.uvg.mashoras.R

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "activities_channel"

        fun createChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Actividades",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Notificaciones de actividades nuevas y finalizadas"
                }

                val manager =
                    context.getSystemService(NotificationManager::class.java) as NotificationManager
                manager.createNotificationChannel(channel)
            }
        }
    }

    private val manager = NotificationManagerCompat.from(context)

    fun showNotification(id: Int, title: String, text: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            // si no tienes un ícono, puedes usar ic_launcher_foreground
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        manager.notify(id, notification)
    }
}
