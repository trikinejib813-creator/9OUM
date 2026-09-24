package com.example.ui.window

import androidx.compose.ui.graphics.vector.ImageVector

sealed class WindowType {
  data object Dashboard : WindowType()
  data object TaskManager : WindowType()
  data class TaskDetail(val taskId: Long) : WindowType()
  data class AddEditTask(val taskId: Long? = null) : WindowType()
  data object AiNotificationStudio : WindowType()
  data object CloudSyncHub : WindowType()
  data object SecurityVault : WindowType()
  data object ProfileSettings : WindowType()
  data object Statistics : WindowType()
}

data class WindowItem(
  val id: String,
  val type: WindowType,
  val titleKey: String,
  val isMinimized: Boolean = false,
  val isMaximized: Boolean = false,
  val openedAt: Long = System.currentTimeMillis()
)
