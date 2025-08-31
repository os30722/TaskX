package com.hfad.agendax.vo

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

const val DATE_FORMAT = "dd-MM-yyyy"

@Parcelize
@Entity(tableName = "tasks")
data class Task (
    @PrimaryKey(autoGenerate = true, )
    val uid: Int = 0,
    val title: String,
    val details: String,
    val calendar: Calendar,
    val notification: Boolean,
    val completed: Boolean = false
) : Parcelable {
    var date: String = ""

    init {
        val dateFormatter = SimpleDateFormat(DATE_FORMAT)
        date = dateFormatter.format(calendar.time)
    }

    companion object {

        fun getTaskDate(c: Calendar): String{
            val dateFormatter = SimpleDateFormat(DATE_FORMAT)
            return dateFormatter.format(c.timeInMillis)
        }
    }


}

class DateConverter {

    @TypeConverter
    fun toCalendar(l: Long): Calendar {
        val c = Calendar.getInstance()
        c.timeInMillis = l
        return c
    }

    @TypeConverter
    fun fromCalendar(c: Calendar): Long {
        return c.timeInMillis    }
}