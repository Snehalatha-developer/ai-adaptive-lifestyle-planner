package com.example.aiplanner.di

import android.content.Context
import androidx.room.Room
import com.example.aiplanner.data.local.AppDatabase
import com.example.aiplanner.data.local.dao.ScheduleDao
import com.example.aiplanner.data.local.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME).build()

    @Provides
    fun provideScheduleDao(database: AppDatabase): ScheduleDao = database.scheduleDao()

    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDao = database.taskDao()
}
