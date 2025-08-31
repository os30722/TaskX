package com.hfad.agendax.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.agendax.repository.TaskRepository
import com.hfad.agendax.util.DateTime
import com.hfad.agendax.vo.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject constructor(
    private val repository: TaskRepository
): ViewModel() {

    val currentMonth = MutableLiveData("")
    val currentYear = MutableLiveData("")
    val selectedDate = MutableLiveData(DateTime.stripTime(Calendar.getInstance()))
    var currentDate = MutableLiveData(DateTime.stripTime(Calendar.getInstance()))
    var tasksData: MutableLiveData<List<Task>> = MutableLiveData()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    var timeFormat24 = false

    fun fetchTasks(calendar: Calendar){
        viewModelScope.launch{
            repository.getTasks(calendar).collect { tasks ->
                isLoading.value = true
                tasksData.value = tasks
                isLoading.value = false
            }
        }

    }

    fun updateTaskCompletion(taskUid: Int, completed: Boolean){
        viewModelScope.launch {
            repository.updateTaskCompletion(taskUid, completed)
        }
    }

    suspend fun deleteTasks(taskUid: List<Long>){
         return repository.deleteTasks(taskUid)

    }
}