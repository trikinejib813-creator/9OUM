package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [
    UserProfileEntity::class,
    TaskItemEntity::class,
    JobOfferEntity::class,
    JobApplicationEntity::class,
    ArtisanEntity::class,
    ArtisanQuoteEntity::class,
    HomeServiceOrderEntity::class,
    HomeServiceMessageEntity::class,
    SectionSettingsEntity::class
  ],
  version = 3,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun userDao(): UserDao
  abstract fun taskDao(): TaskDao
  abstract fun jobDao(): JobDao
  abstract fun artisanDao(): ArtisanDao
  abstract fun homeServiceDao(): HomeServiceDao
  abstract fun settingsDao(): SettingsDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "goum9_database"
        )
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
