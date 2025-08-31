package com.hfad.agendax.ui.newtask

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfad.agendax.repository.TaskRepository
import com.hfad.agendax.util.DateTime
import com.hfad.agendax.vo.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NewTaskViewModel
 @Inject constructor(
    private val repository: TaskRepository
): ViewModel() {

    private val _calendar: MutableLiveData<Calendar> = MutableLiveData(DateTime.stripSeconds(Calendar.getInstance()))
    val calendar: LiveData<Calendar> get() = _calendar

    var title = MutableLiveData("")
    var details = MutableLiveData("")
    var notification = MutableLiveData(true)
    private var taskUid = 0
    var timeFormat24 = false
    private var editMode: Boolean = false


    fun setDate(year: Int, month: Int, dayOfMonth: Int){
        _calendar.value = calendar.value?.clone() as Calendar
        _calendar.value?.apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
    }

    fun setCalendar(timeInMillis: Long){
        _calendar.value?.timeInMillis = timeInMillis
        DateTime.stripSeconds(_calendar.value!!)
    }

    fun setTime(hourOfDay: Int, minute: Int){
        _calendar.value = calendar.value?.clone() as Calendar
        _calendar.value?.apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }


    }

    fun setTask(task: Task) {
        editMode = true
        taskUid = task.uid
        title.value = task.title
        details.value = task.details
        _calendar.value = task.calendar
        notification.value = task.notification
    }

    suspend fun saveTask(): Int {
            if(!editMode) {
                val task = Task(title = title.value!!, details = details.value!!, calendar = calendar.value!!, notification = notification.value!!)
                return repository.saveTask(task).toInt()
            }
            else{
                val task = Task(uid = taskUid, title = title.value!!, details = details.value!!, calendar = calendar.value!!, notification = notification.value!!)
                repository.updateTask(task)
                return task.uid
            }

    }

  }