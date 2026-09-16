package com.example.aiplanner.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.aiplanner.data.local.dao.ScheduleDao
import com.example.aiplanner.data.local.dao.TaskDao
import com.example.aiplanner.data.local.entity.ScheduleEntity
import com.example.aiplanner.data.local.entity.TaskEntity

@Database(
    entities = [ScheduleEntity::class, TaskEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
    abstract fun taskDao(): TaskDao

    companion object {
        const val DATABASE_NAME = "ai_planner.db"
    }
}
