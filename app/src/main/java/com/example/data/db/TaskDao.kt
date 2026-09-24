package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
  @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, aiUrgencyScore DESC, createdAt DESC")
  fun getAllTasks(): Flow<List<TaskItemEntity>>

  @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
  fun getTaskById(taskId: Long): Flow<TaskItemEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTask(task: TaskItemEntity): Long

  @Update
  suspend fun updateTask(task: TaskItemEntity)

  @Query("UPDATE tasks SET isCompleted = :completed, isSynced = 0 WHERE id = :taskId")
  suspend fun toggleTaskCompletion(taskId: Long, completed: Boolean)

  @Delete
  suspend fun deleteTask(task: TaskItemEntity)

  @Query("DELETE FROM tasks WHERE id = :taskId")
  suspend fun deleteTaskById(taskId: Long)

  @Query("SELECT * FROM tasks WHERE isSynced = 0")
  suspend fun getUnsyncedTasks(): List<TaskItemEntity>

  @Query("UPDATE tasks SET isSynced = 1")
  suspend fun markAllTasksSynced()
}
