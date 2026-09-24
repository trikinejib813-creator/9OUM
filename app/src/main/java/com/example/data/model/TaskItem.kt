package com.example.data.model

data class TaskItem(
  val id: Long = 0,
  val title: String,
  val notes: String = "",
  val category: String = "عام",
  val priority: String = "MEDIUM",
  val aiUrgencyScore: Int = 50,
  val aiFiltered: Boolean = false,
  val aiSummary: String = "",
  val dueDateMillis: Long = System.currentTimeMillis() + 86400000L,
  val isCompleted: Boolean = false,
  val createdAt: Long = System.currentTimeMillis(),
  val isSynced: Boolean = true
)

data class UserProfile(
  val email: String,
  val countryCode: String,
  val countryName: String,
  val registeredAt: Long,
  val lastSyncAt: Long,
  val isSynced: Boolean,
  val encryptedEmailRaw: String
)
