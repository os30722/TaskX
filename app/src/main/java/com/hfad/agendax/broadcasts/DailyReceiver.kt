package com.hfad.agendax.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hfad.agendax.services.DailyService
import com.hfad.agendax.util.AlarmUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

import android.os.Build
import com.hfad.agendax.widget.TaskListWidgetProvider


@AndroidEntryPoint
class DailyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val calendar = Calendar.getInstance()

        val dailyService = Intent(context, DailyService::class.java)
        dailyService.putExtra("time", calendar.timeInMillis)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(dailyService)
        } else {
            context.startService(dailyService)
        }

       AlarmUtil.setDayChangeAlarm(context,  calendar)
       TaskListWidgetProvider.sendRefreshBroadcast(context)
    }

}
