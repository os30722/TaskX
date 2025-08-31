package com.hfad.agendax.di

import android.content.Context
import com.hfad.agendax.db.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Database {

    @Provides
    @Singleton
    fun provideTaskDatabase(@ApplicationContext context: Context) = TaskDatabase.getInstance(context)
}