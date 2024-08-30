package com.example.smartparent.service

import android.app.Application
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.smartparent.data.FirebaseRepositoryImp
import com.example.smartparent.utlity.NotificationHashCode

class NotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "OK_Action" -> {
                val application = context.applicationContext as Application
                val repository = FirebaseRepositoryImp(application)
                repository.getConnectedDeviceContact()
                Toast.makeText(context, "Notification accepted", Toast.LENGTH_SHORT).show()
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(NotificationHashCode.value)
            }
        }
    }
}