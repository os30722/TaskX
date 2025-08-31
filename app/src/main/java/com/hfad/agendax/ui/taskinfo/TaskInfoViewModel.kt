package com.hfad.agendax.ui.taskinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.agendax.repository.TaskRepository
import com.hfad.agendax.vo.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskInfoViewModel
    @Inject constructor(
    private val repository: TaskRepository
): ViewModel() {

    private val _task: MutableLiveData<Task> = MutableLiveData(null)
    val task: LiveData<Task> get() = _task
    var timeFormat24 = false

    fun fetchTask(uid: Int) {
        viewModelScope.launch {
            repository.getTask(uid).collectLatest {
                _task.value = it
            }
        }
    }

    fun deleteTask(){
        viewModelScope.launch {
            repository.deleteTask(_task.value!!)
        }
    }


}