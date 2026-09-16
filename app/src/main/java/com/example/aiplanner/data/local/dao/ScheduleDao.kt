package com.example.aiplanner.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aiplanner.data.local.entity.ScheduleEntity

@Dao
interface ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: ScheduleEntity)

    @Query("SELECT * FROM schedules WHERE date = :date LIMIT 1")
    suspend fun getScheduleByDate(date: String): ScheduleEntity?

    @Query("SELECT * FROM schedules WHERE scheduleId = :scheduleId LIMIT 1")
    suspend fun getScheduleById(scheduleId: String): ScheduleEntity?

    @Query("DELETE FROM schedules WHERE date = :date")
    suspend fun deleteScheduleForDate(date: String)
}
