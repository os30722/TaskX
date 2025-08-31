package com.hfad.agendax.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.hfad.agendax.util.AlarmUtil
import com.hfad.agendax.util.DateTime
import java.util.*

class TimeChangeReceiver: BroadcastReceiver(){

    interface DateChangeListener {
        fun onDateChange(calendar: Calendar)
    }

    private var mCallBack: DateChangeListener? = null

    fun registerDateChangeListener(callBack: DateChangeListener){
        mCallBack = callBack
    }

    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action == Intent.ACTION_DATE_CHANGED)
            mCallBack?.onDateChange(DateTime.stripTime(Calendar.getInstance()))


        val calendar = Calendar.getInstance()
        AlarmUtil.setDayChangeAlarm(context, calendar)


    }
}