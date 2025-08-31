package com.hfad.agendax.di

import com.hfad.agendax.db.TaskDatabase
import com.hfad.agendax.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Repository {

    @Provides
    @Singleton
    fun provideTaskRepository(database: TaskDatabase) = TaskRepository(database)
}