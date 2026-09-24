package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskItemEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val encryptedTitle: String,
  val encryptedNotes: String = "",
  val category: String = "عام",
  val priority: String = "MEDIUM", // URGENT, HIGH, MEDIUM, LOW
  val aiUrgencyScore: Int = 50,    // 0 - 100
  val aiFiltered: Boolean = false,
  val aiSummary: String = "",
  val dueDateMillis: Long = System.currentTimeMillis() + 86400000L,
  val isCompleted: Boolean = false,
  val createdAt: Long = System.currentTimeMillis(),
  val isSynced: Boolean = true
)
