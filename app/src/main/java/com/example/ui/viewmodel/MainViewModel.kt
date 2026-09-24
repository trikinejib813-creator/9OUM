package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Country
import com.example.data.db.AppDatabase
import com.example.data.model.TaskItem
import com.example.data.model.UserProfile
import com.example.data.repository.AppRepository
import com.example.localization.AppLanguage
import com.example.network.NetworkMonitor
import com.example.notifications.AiFilterMode
import com.example.notifications.SmartNotificationManager
import com.example.ui.windows.AppThemeMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
  private val database = AppDatabase.getDatabase(application)
  private val repository = AppRepository(database)
  private val networkMonitor = NetworkMonitor(application)
  private val notificationManager = SmartNotificationManager(application)
  private val prefs = application.getSharedPreferences("aurasync_prefs", Context.MODE_PRIVATE)

  // Language state
  private val _language = MutableStateFlow(
    AppLanguage.fromCode(prefs.getString("pref_language", "ar") ?: "ar")
  )
  val language: StateFlow<AppLanguage> = _language.asStateFlow()

  // Theme mode
  private val _themeMode = MutableStateFlow(
    try {
      AppThemeMode.valueOf(prefs.getString("pref_theme", AppThemeMode.DARK.name) ?: AppThemeMode.DARK.name)
    } catch (_: Exception) {
      AppThemeMode.DARK
    }
  )
  val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

  // Eye comfort mode
  private val _eyeComfortEnabled = MutableStateFlow(
    prefs.getBoolean("pref_eye_comfort", false)
  )
  val eyeComfortEnabled: StateFlow<Boolean> = _eyeComfortEnabled.asStateFlow()

  // AI Filter mode
  private val _aiFilterMode = MutableStateFlow(
    try {
      AiFilterMode.valueOf(prefs.getString("pref_ai_mode", AiFilterMode.BALANCED.name) ?: AiFilterMode.BALANCED.name)
    } catch (_: Exception) {
      AiFilterMode.BALANCED
    }
  )
  val aiFilterMode: StateFlow<AiFilterMode> = _aiFilterMode.asStateFlow()

  // Splash screen state
  private val _splashFinished = MutableStateFlow(false)
  val splashFinished: StateFlow<Boolean> = _splashFinished.asStateFlow()

  // Sync state
  private val _isSyncing = MutableStateFlow(false)
  val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

  // Online / Offline State
  val isOnline: StateFlow<Boolean> = networkMonitor.isOnline
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), networkMonitor.checkCurrentConnectivity())

  // User Profile from Room
  val userProfile: StateFlow<UserProfile?> = repository.userProfile
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  // Tasks from Room
  val tasks: StateFlow<List<TaskItem>> = repository.allTasks
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  init {
    // Monitor online restoration to auto-sync offline changes
    viewModelScope.launch {
      var wasOffline = false
      isOnline.collect { online ->
        if (online && wasOffline) {
          triggerSync()
        }
        wasOffline = !online
      }
    }
  }

  fun finishSplash() {
    _splashFinished.value = true
  }

  fun saveUserProfile(email: String, country: Country) {
    viewModelScope.launch {
      repository.saveUserProfile(email, country.code, country.nameAr)
      // Provide sample starter tasks if database is brand new
      val initialTasks = repository.getUnsyncedCount()
      if (initialTasks == 0) {
        val sampleTask1 = TaskItem(
          title = if (_language.value == AppLanguage.ARABIC) "مرحباً بك في أورا سينك - استكشف النوافذ" else "Welcome to AuraSync - Explore Windows",
          notes = if (_language.value == AppLanguage.ARABIC) "يمكنك فتح عدة نوافذ وتصغيرها إلى الشريط السفلي." else "You can open multiple windows and minimize them to the dock.",
          category = if (_language.value == AppLanguage.ARABIC) "عام" else "General",
          priority = "HIGH",
          aiUrgencyScore = 80,
          aiFiltered = false
        )
        repository.saveTask(sampleTask1)
      }
    }
  }

  fun updateCountry(country: Country) {
    viewModelScope.launch {
      val current = userProfile.value ?: return@launch
      repository.saveUserProfile(current.email, country.code, country.nameAr)
    }
  }

  fun logout() {
    viewModelScope.launch {
      repository.clearUser()
    }
  }

  fun saveTask(task: TaskItem) {
    viewModelScope.launch {
      repository.saveTask(task)
      // If task is urgent, simulate smart alert
      if (task.priority.equals("URGENT", true) || task.aiUrgencyScore >= 75) {
        notificationManager.sendNotification(
          id = (System.currentTimeMillis() % 10000).toInt(),
          title = task.title,
          message = if (_language.value == AppLanguage.ARABIC) "تذكير ذكي لمهمة عاجلة تم جدولتها بنجاح" else "Smart reminder for critical task scheduled successfully",
          priorityScore = task.aiUrgencyScore
        )
      }
    }
  }

  fun deleteTask(taskId: Long) {
    viewModelScope.launch {
      repository.deleteTask(taskId)
    }
  }

  fun toggleTaskComplete(taskId: Long, completed: Boolean) {
    viewModelScope.launch {
      repository.toggleTaskCompletion(taskId, completed)
    }
  }

  fun triggerSync() {
    if (_isSyncing.value) return
    viewModelScope.launch {
      _isSyncing.value = true
      delay(1200) // Simulated secure cloud handshake & AES encrypted sync
      repository.markAllSynced()
      _isSyncing.value = false
    }
  }

  fun setAiFilterMode(mode: AiFilterMode) {
    _aiFilterMode.value = mode
    prefs.edit().putString("pref_ai_mode", mode.name).apply()
  }

  fun setLanguage(lang: AppLanguage) {
    _language.value = lang
    prefs.edit().putString("pref_language", lang.code).apply()
  }

  fun setThemeMode(mode: AppThemeMode) {
    _themeMode.value = mode
    prefs.edit().putString("pref_theme", mode.name).apply()
  }

  fun setEyeComfort(enabled: Boolean) {
    _eyeComfortEnabled.value = enabled
    prefs.edit().putBoolean("pref_eye_comfort", enabled).apply()
  }
}
