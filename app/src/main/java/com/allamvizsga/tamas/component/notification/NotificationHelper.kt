package com.allamvizsga.tamas.component.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import com.allamvizsga.tamas.MainActivity
import com.allamvizsga.tamas.R

object NotificationHelper {

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    fun sendNotification(context: Context, notificationDetails: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(NotificationChannel(CHANNEL_ID, context.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT))
        }

        // Create an explicit content Intent that starts the main Activity.
        val notificationIntent = Intent(context.applicationContext, MainActivity::class.java)
        // Construct a task stack.
        val stackBuilder = TaskStackBuilder.create(context)
        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity::class.java)
        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent)

        // Get a PendingIntent containing the entire back stack.
        val notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentIntent(notificationPendingIntent)
                .setChannelId(CHANNEL_ID) // Used only on Android O
                .setAutoCancel(true)

        notificationManager.notify(0, builder.build())
    }

    private const val CHANNEL_ID = "allamvizsga_channel"
}