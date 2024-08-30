package com.example.smartparent.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.smartparent.service.NotificationBroadcastReceiver
import com.example.smartparent.R

class Notification(private val context: Context) {

    private val notificationChannelId = "connectedDeviceChannel"

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "connectedDevice"
            val descriptionText = "the parent send request add"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(notificationChannelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(title: String, message: String, notificationID: Int) {
        createNotificationChannel()

        val acceptIntent = Intent(context, NotificationBroadcastReceiver::class.java).apply {
            action = "OK_Action"

        }
        val acceptPendingIntent =
            PendingIntent.getBroadcast(context, 0, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context, notificationChannelId)
            .setSmallIcon(R.drawable.notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .addAction(
                R.drawable.acctionadd,
                "OK",
                acceptPendingIntent
            )


        // Check for necessary permissions
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.VIBRATE
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.INTERNET
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        with(NotificationManagerCompat.from(context)) {
            notify(notificationID, builder.build())
        }
    }


}