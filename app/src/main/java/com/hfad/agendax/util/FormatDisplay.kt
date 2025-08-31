package com.hfad.agendax.util

import androidx.databinding.ObservableField
import java.text.SimpleDateFormat
import java.util.*

const val DATE_FORMAT = "dd MMMM yyyy"
const val TIME_FORMAT_12 = "hh : mm aa"
const val TIME_FORMAT_24 = "HH : mm"

fun getFormattedDate(calendar: Calendar): String {
    val dateFormatter = SimpleDateFormat(DATE_FORMAT)
    dateFormatter.timeZone = calendar.timeZone
    return dateFormatter.format(calendar.time)
}

fun getFormattedTime12(calendar: Calendar): String {
    val timeFormatter = SimpleDateFormat(TIME_FORMAT_12)
    timeFormatter.timeZone = calendar.timeZone
    return timeFormatter.format(calendar.time)
}

fun getFormattedTime24(calendar: Calendar): String {
    val timeFormatter = SimpleDateFormat(TIME_FORMAT_24)
    timeFormatter.timeZone = calendar.timeZone
    return timeFormatter.format(calendar.time)
}


fun getMonthWord(index: Int): String{
    return when(index) {
        0 -> "January"
        1 -> "February"
        2 -> "March"
        3 -> "April"
        4 -> "May"
        5 -> "June"
        6 -> "July"
        7 -> "August"
        8 -> "September"
        9 -> "October"
        10 -> "November"
        11 -> "December"
        else -> "Invalid"
    }
}

fun getDayWord(index: Int): String {
    return when(index) {
        1 -> "Sun"
        2 -> "Mon"
        3 -> "Tue"
        4 -> "Wed"
        5 -> "Thu"
        6 -> "Fri"
        7 -> "Sat"
        else -> "Invalid"
    }
}
fun <T> MutableList<T>.removeRange(from: Int, to: Int){
    for(i in from until to){
        this.removeAt(from)
    }
}