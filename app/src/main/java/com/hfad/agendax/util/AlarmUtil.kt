package com.hfad.agendax.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.hfad.agendax.broadcasts.AlarmReceiver
import com.hfad.agendax.broadcasts.DailyReceiver
import com.hfad.agendax.vo.Task
import java.util.*

class AlarmUtil {
    companion object {
        fun setTaskAlarm(
            context: Context,
            taskUid: Int,
            title: String,
            details: String,
            timeInMillis: Long
        ) {
            if (Calendar.getInstance().timeInMillis <= timeInMillis) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(context, AlarmReceiver::class.java)
                intent.putExtra("title", title)
                intent.putExtra("details", details)
                intent.putExtra("taskUid", taskUid)
                val intentFlag: Int
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    intentFlag = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                } else {
                    intentFlag = PendingIntent.FLAG_UPDATE_CURRENT
                }
                val pendingIntent = PendingIntent.getBroadcast(context, taskUid, intent, intentFlag)
                alarmManager.setAlarmClock(
                    AlarmManager.AlarmClockInfo(timeInMillis, pendingIntent),
                    pendingIntent
                )
            }
        }

        fun setTaskAlarm(context: Context, task: Task) {
            setTaskAlarm(context, task.uid, task.title, task.details, task.calendar.timeInMillis)
        }

        fun cancelTaskAlarm(context: Context, taskUid: Int) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)
            val intentFlag: Int
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                intentFlag = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                intentFlag = PendingIntent.FLAG_UPDATE_CURRENT
            }
            val pendingIntent = PendingIntent.getBroadcast(context, taskUid, intent, intentFlag)
            alarmManager.cancel(pendingIntent)

        }

        fun setDayChangeAlarm(context: Context, calendar: Calendar) {
            val c = calendar.clone() as Calendar
            DateTime.stripSeconds(DateTime.stripTime(calendar))
            c.add(Calendar.DATE, 1)
            c.set(Calendar.HOUR_OF_DAY, 0)
            c.set(Calendar.MINUTE, 0)

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, DailyReceiver::class.java)

            val intentFlag: Int
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                intentFlag = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                intentFlag = PendingIntent.FLAG_UPDATE_CURRENT
            }

            val pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, intentFlag )

            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(c.timeInMillis, pendingIntent),
                pendingIntent
            )


        }
    }


}