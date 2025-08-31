package com.hfad.agendax.util

import android.util.Log
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class DateTime {

    companion object{
        fun stripTime(calendar: Calendar): Calendar {
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            return calendar
        }

        fun stripSeconds(calendar: Calendar): Calendar{
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar
        }

        fun equalDate(c1: Calendar, c2: Calendar): Boolean{
            val cal1 = c1.clone() as Calendar
            val cal2 = c2.clone() as Calendar
            return stripTime(cal1) == stripTime(cal2)
        }

        fun getDisplayDate(calendar: Calendar, timeHour24: Boolean = false): String{
            var hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val f: NumberFormat = DecimalFormat("00")
            if(!timeHour24){
                val meridian = if (hour >= 12)  "pm" else "am"
                if (hour > 12){
                    hour = hour - 12
                }
                if(hour == 0) {
                    hour = 12
                }
                return "${f.format(hour)}:${f.format(minute)} ${meridian}"
            } else{
                return "${f.format(hour)}:${f.format(minute)}"
            }
        }
    }


}