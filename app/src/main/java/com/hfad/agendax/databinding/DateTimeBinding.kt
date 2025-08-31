package com.hfad.agendax.databinding

import android.graphics.Color
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.hfad.agendax.R
import com.hfad.agendax.util.getFormattedDate
import com.hfad.agendax.util.getFormattedTime12
import com.hfad.agendax.util.getFormattedTime24
import java.util.*



object DateTimeBinding {

    @JvmStatic
    @BindingAdapter(value = ["datePick"])
    fun setDatePick(view: TextView, date: Calendar?) {
        if(date != null){
            view.text = getFormattedDate(date)
        } else {
            view.text = ""
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["timePick", "set24Hour"], requireAll = false)
    fun setTimePick(view: TextView, time: Calendar?, hour24: Boolean = false) {
        if(time != null){
            if(!hour24)
                view.text = getFormattedTime12(time)
            else
                view.text = getFormattedTime24(time)
        } else {
            view.text = ""
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["currentDate", "dateSelected"])
    fun setDateCell(view: TextView, currentDate: Boolean = false, selectedDate: Boolean = false) {
        if(currentDate) {
            if (!selectedDate) {
                view.setTextColor(ContextCompat.getColor(view.context, R.color.color_primary))
            }
            else
                view.setTextColor(ContextCompat.getColor(view.context, R.color.week_cell))
        }

        if(selectedDate)
            view.setBackground(ContextCompat.getDrawable(view.context, R.drawable.calendar_date_selector))
        else{
            view.setBackground(null)
        }
    }




}