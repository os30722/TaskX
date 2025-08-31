package com.hfad.agendax

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Build.VERSION_CODES.O
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat.getSystemService
import androidx.hilt.work.HiltWorkerFactory
import androidx.preference.PreferenceManager
import androidx.work.Configuration
import com.hfad.agendax.services.DailyService
import com.hfad.agendax.util.AlarmUtil
import dagger.hilt.android.HiltAndroidApp
import java.util.*
import javax.inject.Inject


const val TASK_CHANNEL_ID = "taskId"

@HiltAndroidApp
class App: Application(){

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        createNotificationChannel()

    }


    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= O){
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(TASK_CHANNEL_ID, "Task Notification", importance)
            channel.description = "To show task notification"

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, audioAttributes)

            val notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(channel)


        }
    }

}