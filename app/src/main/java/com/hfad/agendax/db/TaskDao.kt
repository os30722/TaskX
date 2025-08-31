package com.hfad.agendax.db

import androidx.room.*
import com.hfad.agendax.vo.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task): Long

    @Query("Select * from tasks where date = :date order by completed, calendar")
    fun getTasks(date: String): Flow<List<Task>>

    @Query("Select * from tasks where date = :date and completed = 0")
    fun getPendingTasks(date: String): List<Task>

    @Query("select * from tasks where uid = :uid")
    fun getTask(uid: Int): Flow<Task>

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("delete from tasks where uid in (:uidList)")
    suspend fun deleteTasks(uidList: List<Long>)

    @Query("update tasks set completed = :completed where uid = :uid")
    suspend fun updateTaskCompletion(uid: Int, completed: Boolean)

}