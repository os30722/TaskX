package com.hfad.agendax.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.hfad.agendax.R
import com.hfad.agendax.TASK_CHANNEL_ID
import com.hfad.agendax.broadcasts.AlarmReceiver
import com.hfad.agendax.repository.TaskRepository
import com.hfad.agendax.util.AlarmUtil
import com.hfad.agendax.vo.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


@AndroidEntryPoint
class DailyService: Service() {

    @Inject
    lateinit var repository: TaskRepository
    val job = SupervisorJob()
    val scope = CoroutineScope(Dispatchers.Default + job)

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {


        val builder = NotificationCompat.Builder(this, TASK_CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Receiving task")
            .setPriority(NotificationCompat.PRIORITY_HIGH)


        startForeground(1, builder.build())

        val timeInMillis = intent.getLongExtra("time", 0)

        scope.launch {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMillis
            repository.getTasks(calendar).collectLatest { tasks ->
                for(task: Task in tasks) {

                        AlarmUtil.setTaskAlarm(this@DailyService, task)

                    }

                stopSelf()
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}