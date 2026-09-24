package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
  @PrimaryKey val id: Int = 1,
  val encryptedEmail: String,
  val countryCode: String,
  val countryName: String,
  val registeredAt: Long = System.currentTimeMillis(),
  val lastSyncAt: Long = System.currentTimeMillis(),
  val isSynced: Boolean = true
)
