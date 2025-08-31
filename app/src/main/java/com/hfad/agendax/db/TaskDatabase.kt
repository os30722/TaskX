package com.hfad.agendax.db

import android.content.Context
import androidx.room.*
import com.hfad.agendax.vo.DateConverter
import com.hfad.agendax.vo.Task

@Database(
    entities = [Task::class],
    version = 1,
    exportSchema = true,
    autoMigrations = [
    ]
)
@TypeConverters(DateConverter::class)
abstract class TaskDatabase: RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {

        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getInstance(context: Context): TaskDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                TaskDatabase::class.java, "Tasks.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}