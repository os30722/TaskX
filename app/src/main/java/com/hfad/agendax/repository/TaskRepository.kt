package com.hfad.agendax.repository

import com.hfad.agendax.db.TaskDatabase
import com.hfad.agendax.vo.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.*

class TaskRepository (
    private val database: TaskDatabase
){

    suspend fun saveTask(task: Task): Long{
        return database.taskDao().insert(task)
    }

    fun getTasks(calendar: Calendar): Flow<List<Task>> {
        val date = Task.getTaskDate(calendar)
        return database.taskDao().getTasks(date).distinctUntilChanged()
    }

    fun getTask(uid: Int): Flow<Task> {
        return database.taskDao().getTask(uid).distinctUntilChanged()
    }

    suspend fun updateTask(task: Task) {
        return database.taskDao().updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        return database.taskDao().deleteTask(task)
    }

    suspend fun deleteTasks(taskUid: List<Long>){
        return database.taskDao().deleteTasks(taskUid)
    }

    suspend fun updateTaskCompletion(taskUid: Int, completed: Boolean){
        return database.taskDao().updateTaskCompletion(taskUid, completed)
    }


}