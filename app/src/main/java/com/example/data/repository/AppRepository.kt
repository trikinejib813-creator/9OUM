package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.db.TaskItemEntity
import com.example.data.db.UserProfileEntity
import com.example.data.model.TaskItem
import com.example.data.model.UserProfile
import com.example.security.CryptoManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppRepository(private val database: AppDatabase) {
  private val userDao = database.userDao()
  private val taskDao = database.taskDao()

  val userProfile: Flow<UserProfile?> = userDao.getUserProfile().map { entity ->
    entity?.let {
      UserProfile(
        email = CryptoManager.decrypt(it.encryptedEmail),
        countryCode = it.countryCode,
        countryName = it.countryName,
        registeredAt = it.registeredAt,
        lastSyncAt = it.lastSyncAt,
        isSynced = it.isSynced,
        encryptedEmailRaw = it.encryptedEmail
      )
    }
  }

  suspend fun getUserProfileOnce(): UserProfile? {
    val entity = userDao.getUserProfileOnce() ?: return null
    return UserProfile(
      email = CryptoManager.decrypt(entity.encryptedEmail),
      countryCode = entity.countryCode,
      countryName = entity.countryName,
      registeredAt = entity.registeredAt,
      lastSyncAt = entity.lastSyncAt,
      isSynced = entity.isSynced,
      encryptedEmailRaw = entity.encryptedEmail
    )
  }

  suspend fun saveUserProfile(email: String, countryCode: String, countryName: String) {
    val encryptedEmail = CryptoManager.encrypt(email.trim())
    val profile = UserProfileEntity(
      id = 1,
      encryptedEmail = encryptedEmail,
      countryCode = countryCode,
      countryName = countryName,
      registeredAt = System.currentTimeMillis(),
      lastSyncAt = System.currentTimeMillis(),
      isSynced = true
    )
    userDao.saveUserProfile(profile)
  }

  suspend fun clearUser() {
    userDao.clearUserProfile()
  }

  suspend fun updateSyncStatus(synced: Boolean) {
    userDao.updateSyncStatus(synced, System.currentTimeMillis())
  }

  val allTasks: Flow<List<TaskItem>> = taskDao.getAllTasks().map { list ->
    list.map { entity ->
      TaskItem(
        id = entity.id,
        title = CryptoManager.decrypt(entity.encryptedTitle),
        notes = CryptoManager.decrypt(entity.encryptedNotes),
        category = entity.category,
        priority = entity.priority,
        aiUrgencyScore = entity.aiUrgencyScore,
        aiFiltered = entity.aiFiltered,
        aiSummary = entity.aiSummary,
        dueDateMillis = entity.dueDateMillis,
        isCompleted = entity.isCompleted,
        createdAt = entity.createdAt,
        isSynced = entity.isSynced
      )
    }
  }

  fun getTaskById(taskId: Long): Flow<TaskItem?> = taskDao.getTaskById(taskId).map { entity ->
    entity?.let {
      TaskItem(
        id = it.id,
        title = CryptoManager.decrypt(it.encryptedTitle),
        notes = CryptoManager.decrypt(it.encryptedNotes),
        category = it.category,
        priority = it.priority,
        aiUrgencyScore = it.aiUrgencyScore,
        aiFiltered = it.aiFiltered,
        aiSummary = it.aiSummary,
        dueDateMillis = it.dueDateMillis,
        isCompleted = it.isCompleted,
        createdAt = it.createdAt,
        isSynced = it.isSynced
      )
    }
  }

  suspend fun saveTask(task: TaskItem): Long {
    val entity = TaskItemEntity(
      id = task.id,
      encryptedTitle = CryptoManager.encrypt(task.title),
      encryptedNotes = CryptoManager.encrypt(task.notes),
      category = task.category,
      priority = task.priority,
      aiUrgencyScore = task.aiUrgencyScore,
      aiFiltered = task.aiFiltered,
      aiSummary = task.aiSummary,
      dueDateMillis = task.dueDateMillis,
      isCompleted = task.isCompleted,
      createdAt = if (task.createdAt > 0) task.createdAt else System.currentTimeMillis(),
      isSynced = task.isSynced
    )
    return if (task.id == 0L) {
      taskDao.insertTask(entity)
    } else {
      taskDao.updateTask(entity)
      task.id
    }
  }

  suspend fun toggleTaskCompletion(taskId: Long, completed: Boolean) {
    taskDao.toggleTaskCompletion(taskId, completed)
  }

  suspend fun deleteTask(taskId: Long) {
    taskDao.deleteTaskById(taskId)
  }

  suspend fun getUnsyncedCount(): Int {
    return taskDao.getUnsyncedTasks().size
  }

  suspend fun markAllSynced() {
    taskDao.markAllTasksSynced()
    userDao.updateSyncStatus(true, System.currentTimeMillis())
  }
}
