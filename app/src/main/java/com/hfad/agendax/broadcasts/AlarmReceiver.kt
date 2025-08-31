package com.hfad.agendax.broadcasts

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.hfad.agendax.TASK_CHANNEL_ID
import android.media.RingtoneManager
import android.net.Uri

import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import androidx.preference.PreferenceManager
import com.hfad.agendax.R
import com.hfad.agendax.services.TtsService
import com.hfad.agendax.ui.MainActivity


class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

//        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
//        val wakeLock = pm.newWakeLock(
//            PowerManager.PARTIAL_WAKE_LOCK or
//                    PowerManager.ACQUIRE_CAUSES_WAKEUP or
//                    PowerManager.ON_AFTER_RELEASE, "lock: lockk")
//        wakeLock.acquire()

        val title = intent.getStringExtra("title")
        val taskUid = intent.getIntExtra("taskUid", 0)

        val completedIntent = Intent(context, CompletedReceiver::class.java)
        completedIntent.putExtra("taskUid", taskUid)
        val completedPendingIntent = PendingIntent.getBroadcast(context, taskUid, completedIntent,  0)

        val bundle = bundleOf(
            "taskUid" to taskUid
        )

        val contentIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.main_nav)
            .setDestination(R.id.taskInfo)
            .setArguments(bundle)
            .setComponentName(MainActivity::class.java)
            .createPendingIntent()

        val builder = NotificationCompat.Builder(context.applicationContext, TASK_CHANNEL_ID)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setContentIntent(contentIntent)
            .setColor(ContextCompat.getColor(context, R.color.color_primary))
            .addAction(0, "Completed", completedPendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(intent.getIntExtra("taskUid", 0), builder.build())

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val voiceEnabled  = sharedPref.getBoolean("voice_enabled", true)

        if(voiceEnabled) {
            val speechRate = sharedPref.getInt("speech_rate", 0).toFloat() / 10
            val intent = Intent(context, TtsService::class.java)
            intent.putExtra("title", title)
            intent.putExtra("speech_rate", speechRate)

            context.startService(intent)
        }
//        wakeLock.release()
    }
}