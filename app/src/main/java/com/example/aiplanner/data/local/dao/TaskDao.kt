package com.example.aiplanner.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.aiplanner.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY startTime ASC")
    fun observeTasksForDate(date: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE date = :date AND status = 'PENDING' ORDER BY startTime ASC")
    fun observePendingTasksForDate(date: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE date = :date AND status = 'COMPLETED' ORDER BY startTime ASC")
    fun observeCompletedTasksForDate(date: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: String): TaskEntity?

    @Query("DELETE FROM tasks WHERE scheduleId = :scheduleId")
    suspend fun deleteTasksForSchedule(scheduleId: String)

    @Query("DELETE FROM tasks WHERE date = :date")
    suspend fun deleteTasksForDate(date: String)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTask(id: String)

    @Query("SELECT COUNT(*) FROM tasks WHERE date = :date")
    suspend fun countTasksForDate(date: String): Int

    @Query("SELECT COUNT(*) FROM tasks WHERE date = :date AND status = 'COMPLETED'")
    suspend fun countCompletedForDate(date: String): Int
}
