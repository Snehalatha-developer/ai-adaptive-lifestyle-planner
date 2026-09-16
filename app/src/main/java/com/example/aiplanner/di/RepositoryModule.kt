package com.example.aiplanner.di

import com.example.aiplanner.data.repository.ScheduleRepositoryImpl
import com.example.aiplanner.data.repository.UserPreferencesRepositoryImpl
import com.example.aiplanner.domain.repository.ScheduleRepository
import com.example.aiplanner.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindScheduleRepository(impl: ScheduleRepositoryImpl): ScheduleRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(impl: UserPreferencesRepositoryImpl): UserPreferencesRepository
}
