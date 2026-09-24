package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EnhancedEncryption
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Country
import com.example.data.CountryRepository
import com.example.data.model.TaskItem
import com.example.data.model.UserProfile
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.notifications.AiFilterMode
import com.example.ui.components.AuraIconLogo
import com.example.ui.window.WindowFrame
import com.example.ui.window.WindowItem
import com.example.ui.window.WindowType
import com.example.ui.windows.AddEditTaskWindowContent
import com.example.ui.windows.AiNotificationStudioWindowContent
import com.example.ui.windows.AppThemeMode
import com.example.ui.windows.CloudSyncHubWindowContent
import com.example.ui.windows.ProfileSettingsWindowContent
import com.example.ui.windows.SecurityVaultWindowContent
import com.example.ui.windows.StatisticsWindowContent
import com.example.ui.windows.TaskDetailWindowContent
import com.example.ui.windows.TasksWindowContent
import kotlin.math.roundToInt

@Composable
fun MainDashboard(
  lang: AppLanguage,
  userProfile: UserProfile?,
  tasks: List<TaskItem>,
  isOnline: Boolean,
  unsyncedCount: Int,
  isSyncing: Boolean,
  aiFilterMode: AiFilterMode,
  currentThemeMode: AppThemeMode,
  eyeComfortEnabled: Boolean,
  onThemeModeChange: (AppThemeMode) -> Unit,
  onEyeComfortChange: (Boolean) -> Unit,
  onLanguageChange: (AppLanguage) -> Unit,
  onCountryUpdated: (Country) -> Unit,
  onAiFilterModeChange: (AiFilterMode) -> Unit,
  onTriggerSync: () -> Unit,
  onSaveTask: (TaskItem) -> Unit,
  onDeleteTask: (Long) -> Unit,
  onToggleTaskComplete: (Long, Boolean) -> Unit,
  onLogout: () -> Unit
) {
  // Multi-window state management
  val openWindows = remember { mutableStateListOf<WindowItem>() }
  var activeWindowId by remember { mutableStateOf<String?>(null) }

  // Scroll tracking to collapse top header when scrolling up
  // "و بعد التمرير للاعلى تختفي الاعدادات من فوق لتصبح الشاشة اوسع في التصفح"
  var isHeaderVisible by remember { mutableStateOf(true) }

  val nestedScrollConnection = remember {
    object : NestedScrollConnection {
      override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.y
        if (delta < -15f && isHeaderVisible) {
          // Scrolling down the page / upwards in content -> hide header
          isHeaderVisible = false
        } else if (delta > 15f && !isHeaderVisible) {
          // Scrolling up the page / downwards in content -> reveal header
          isHeaderVisible = true
        }
        return Offset.Zero
      }
    }
  }

  fun openWindow(type: WindowType, titleKey: String) {
    val existing = openWindows.firstOrNull { it.type == type }
    if (existing != null) {
      // Bring to front and un-minimize
      val index = openWindows.indexOf(existing)
      openWindows[index] = existing.copy(isMinimized = false)
      activeWindowId = existing.id
    } else {
      val newItem = WindowItem(
        id = "win_${System.currentTimeMillis()}_${openWindows.size}",
        type = type,
        titleKey = titleKey,
        isMinimized = false
      )
      openWindows.add(newItem)
      activeWindowId = newItem.id
    }
  }

  fun closeWindow(windowId: String) {
    openWindows.removeAll { it.id == windowId }
    if (activeWindowId == windowId) {
      activeWindowId = openWindows.lastOrNull { !it.isMinimized }?.id
    }
  }

  fun minimizeWindow(windowId: String) {
    val index = openWindows.indexOfFirst { it.id == windowId }
    if (index >= 0) {
      openWindows[index] = openWindows[index].copy(isMinimized = true)
      if (activeWindowId == windowId) {
        activeWindowId = openWindows.lastOrNull { !it.isMinimized }?.id
      }
    }
  }

  fun toggleMaximizeWindow(windowId: String) {
    val index = openWindows.indexOfFirst { it.id == windowId }
    if (index >= 0) {
      openWindows[index] = openWindows[index].copy(isMaximized = !openWindows[index].isMaximized)
    }
  }

  val activeWindow = openWindows.firstOrNull { it.id == activeWindowId && !it.isMinimized }
  val currentCountry = remember(userProfile?.countryCode) {
    CountryRepository.findByCode(userProfile?.countryCode ?: "SA") ?: CountryRepository.defaultCountry
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .statusBarsPadding()
      .navigationBarsPadding()
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // 1. Collapsing Top Settings Bar (Animated Hide/Show on Scroll)
      AnimatedVisibility(
        visible = isHeaderVisible,
        enter = expandVertically(animationSpec = tween(250)) + fadeIn(animationSpec = tween(250)),
        exit = shrinkVertically(animationSpec = tween(250)) + fadeOut(animationSpec = tween(250))
      ) {
        Surface(
          color = MaterialTheme.colorScheme.surface,
          shadowElevation = 3.dp,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            // App Branding & Logo
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.clickable {
                openWindow(WindowType.ProfileSettings, "settings_title")
              }
            ) {
              AuraIconLogo(size = 32.dp, assemblyProgress = 1f)
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "AuraSync",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                  )
                )
                Text(
                  text = "${currentCountry.flagEmoji} ${currentCountry.localizedName(lang)}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                )
              }
            }

            // Quick status actions: Sync badge + Notifications + Settings
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              // Online / Offline Indicator Pill
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isOnline) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f),
                modifier = Modifier.clickable {
                  openWindow(WindowType.CloudSyncHub, "window_sync")
                }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                    contentDescription = null,
                    tint = if (isOnline) Color(0xFF10B981) else Color(0xFFEF4444),
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = if (isOnline) (if (unsyncedCount > 0) "$unsyncedCount" else "Sync") else "Offline",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = if (isOnline) Color(0xFF10B981) else Color(0xFFEF4444),
                      fontSize = 11.sp
                    )
                  )
                }
              }

              // AI Notification Studio shortcut
              IconButton(onClick = { openWindow(WindowType.AiNotificationStudio, "window_ai_notif") }) {
                Icon(
                  imageVector = Icons.Default.NotificationsActive,
                  contentDescription = "Notifications",
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(20.dp)
                )
              }

              // Settings Window Shortcut
              IconButton(onClick = { openWindow(WindowType.ProfileSettings, "settings_title") }) {
                Icon(
                  imageVector = Icons.Default.Settings,
                  contentDescription = "Settings",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }
        }
      }

      // 2. Scrollable Dashboard Canvas with NestedScrollConnection
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .nestedScroll(nestedScrollConnection),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Welcome & Security Header Banner
        item {
          Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surface
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
          ) {
            Column(modifier = Modifier.padding(18.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = "${AppStrings.get("welcome_title", lang)},",
                    style = MaterialTheme.typography.bodyMedium.copy(
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  )
                  Text(
                    text = userProfile?.email?.substringBefore("@") ?: "User",
                    style = MaterialTheme.typography.titleLarge.copy(
                      fontWeight = FontWeight.Bold
                    )
                  )
                }

                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                  modifier = Modifier.clickable {
                    openWindow(WindowType.SecurityVault, "window_security")
                  }
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = Icons.Default.Lock,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "AES-GCM 256",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp
                      )
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              Text(
                text = AppStrings.get("scroll_hint", lang),
                style = MaterialTheme.typography.bodySmall.copy(
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 11.sp
                )
              )
            }
          }
        }

        // Quick Navigation Windows Grid (Windows Hub)
        item {
          Text(
            text = AppStrings.get("quick_windows", lang),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )

          Spacer(modifier = Modifier.height(8.dp))

          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              WindowLauncherCard(
                title = AppStrings.get("window_tasks", lang),
                subtitle = "${tasks.size} " + if (lang == AppLanguage.ARABIC) "مهمة" else "Tasks",
                icon = Icons.Default.Task,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
                onClick = { openWindow(WindowType.TaskManager, "window_tasks") }
              )
              WindowLauncherCard(
                title = AppStrings.get("window_ai_notif", lang),
                subtitle = if (lang == AppLanguage.ARABIC) "فلترة وتحكم" else "Smart Filter",
                icon = Icons.Default.Psychology,
                color = Color(0xFF818CF8),
                modifier = Modifier.weight(1f),
                onClick = { openWindow(WindowType.AiNotificationStudio, "window_ai_notif") }
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              WindowLauncherCard(
                title = AppStrings.get("window_sync", lang),
                subtitle = if (isOnline) AppStrings.get("connection_status_online", lang) else AppStrings.get("connection_status_offline", lang),
                icon = Icons.Default.CloudSync,
                color = if (isOnline) Color(0xFF10B981) else Color(0xFFEF4444),
                modifier = Modifier.weight(1f),
                onClick = { openWindow(WindowType.CloudSyncHub, "window_sync") }
              )
              WindowLauncherCard(
                title = AppStrings.get("window_security", lang),
                subtitle = AppStrings.get("encryption_algo", lang),
                icon = Icons.Default.EnhancedEncryption,
                color = Color(0xFF06B6D4),
                modifier = Modifier.weight(1f),
                onClick = { openWindow(WindowType.SecurityVault, "window_security") }
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              WindowLauncherCard(
                title = AppStrings.get("window_stats", lang),
                subtitle = "${tasks.count { it.isCompleted }}/${tasks.size} " + AppStrings.get("completed", lang),
                icon = Icons.Default.Assessment,
                color = Color(0xFFF59E0B),
                modifier = Modifier.weight(1f),
                onClick = { openWindow(WindowType.Statistics, "window_stats") }
              )
              WindowLauncherCard(
                title = AppStrings.get("window_profile", lang),
                subtitle = currentCountry.localizedName(lang),
                icon = Icons.Default.Settings,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f),
                onClick = { openWindow(WindowType.ProfileSettings, "settings_title") }
              )
            }
          }
        }

        // Recent Tasks Section with quick add button
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = AppStrings.get("window_tasks", lang),
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            TextButton(onClick = { openWindow(WindowType.AddEditTask(), "add_task") }) {
              Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(AppStrings.get("add_task", lang))
            }
          }

          if (tasks.isEmpty()) {
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                Text(
                  text = AppStrings.get("tasks_empty", lang),
                  style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                  textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
              }
            }
          } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              tasks.take(4).forEach { task ->
                Surface(
                  shape = RoundedCornerShape(14.dp),
                  color = MaterialTheme.colorScheme.surface,
                  border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { openWindow(WindowType.TaskDetail(task.id), "task_title") }
                ) {
                  Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    IconButton(
                      onClick = { onToggleTaskComplete(task.id, !task.isCompleted) },
                      modifier = Modifier.size(28.dp)
                    ) {
                      Icon(
                        imageVector = if (task.isCompleted) Icons.Default.Task else Icons.Default.Remove,
                        contentDescription = null,
                        tint = if (task.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                          fontWeight = FontWeight.SemiBold,
                          color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                      Text(
                        text = "${task.category} • AI ${task.aiUrgencyScore}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                      )
                    }
                    Icon(
                      imageVector = Icons.Default.OpenInNew,
                      contentDescription = "Open in Window",
                      tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                      modifier = Modifier.size(16.dp)
                    )
                  }
                }
              }
            }
          }
        }

        item {
          Spacer(modifier = Modifier.height(70.dp))
        }
      }

      // 3. Bottom Minimized Windows Dock / Taskbar
      if (openWindows.isNotEmpty()) {
        Surface(
          color = MaterialTheme.colorScheme.surface,
          shadowElevation = 8.dp,
          modifier = Modifier.fillMaxWidth()
        ) {
          LazyRow(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            items(openWindows, key = { it.id }) { win ->
              val isActive = win.id == activeWindowId && !win.isMinimized
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = if (isActive) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                modifier = Modifier.clickable {
                  val index = openWindows.indexOf(win)
                  openWindows[index] = win.copy(isMinimized = false)
                  activeWindowId = win.id
                }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = getIconForWindowType(win.type),
                    contentDescription = null,
                    tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = AppStrings.get(win.titleKey, lang),
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                      color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 1
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  IconButton(
                    onClick = { closeWindow(win.id) },
                    modifier = Modifier.size(18.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Remove,
                      contentDescription = "Close",
                      modifier = Modifier.size(12.dp),
                      tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              }
            }
          }
        }
      }
    }

    // 4. Active Window Container Overlay (WindowFrame)
    if (activeWindow != null) {
      WindowFrame(
        title = AppStrings.get(activeWindow.titleKey, lang),
        icon = getIconForWindowType(activeWindow.type),
        isMaximized = activeWindow.isMaximized,
        onMinimize = { minimizeWindow(activeWindow.id) },
        onToggleMaximize = { toggleMaximizeWindow(activeWindow.id) },
        onClose = { closeWindow(activeWindow.id) }
      ) {
        when (val type = activeWindow.type) {
          is WindowType.Dashboard -> {}
          is WindowType.TaskManager -> {
            TasksWindowContent(
              lang = lang,
              tasks = tasks,
              onToggleComplete = onToggleTaskComplete,
              onDeleteTask = onDeleteTask,
              onOpenTaskDetail = { taskId -> openWindow(WindowType.TaskDetail(taskId), "task_title") },
              onAddNewTask = { openWindow(WindowType.AddEditTask(), "add_task") }
            )
          }
          is WindowType.TaskDetail -> {
            val task = tasks.firstOrNull { it.id == type.taskId }
            TaskDetailWindowContent(
              lang = lang,
              task = task,
              onToggleComplete = { task?.let { onToggleTaskComplete(it.id, !it.isCompleted) } },
              onEdit = { openWindow(WindowType.AddEditTask(type.taskId), "edit") },
              onDelete = {
                task?.let { onDeleteTask(it.id) }
                closeWindow(activeWindow.id)
              }
            )
          }
          is WindowType.AddEditTask -> {
            val task = if (type.taskId != null) tasks.firstOrNull { it.id == type.taskId } else null
            AddEditTaskWindowContent(
              lang = lang,
              existingTask = task,
              aiFilterMode = aiFilterMode,
              onSaveTask = { newTask ->
                onSaveTask(newTask)
                closeWindow(activeWindow.id)
              },
              onCancel = { closeWindow(activeWindow.id) }
            )
          }
          is WindowType.AiNotificationStudio -> {
            AiNotificationStudioWindowContent(
              lang = lang,
              currentMode = aiFilterMode,
              onModeChange = onAiFilterModeChange
            )
          }
          is WindowType.CloudSyncHub -> {
            CloudSyncHubWindowContent(
              lang = lang,
              isOnline = isOnline,
              unsyncedCount = unsyncedCount,
              isSyncing = isSyncing,
              lastSyncTimestamp = userProfile?.lastSyncAt ?: 0L,
              onTriggerSync = onTriggerSync
            )
          }
          is WindowType.SecurityVault -> {
            SecurityVaultWindowContent(
              lang = lang,
              userProfile = userProfile
            )
          }
          is WindowType.ProfileSettings -> {
            ProfileSettingsWindowContent(
              lang = lang,
              userProfile = userProfile,
              currentThemeMode = currentThemeMode,
              eyeComfortEnabled = eyeComfortEnabled,
              onThemeModeChange = onThemeModeChange,
              onEyeComfortChange = onEyeComfortChange,
              onLanguageChange = onLanguageChange,
              onCountryUpdated = onCountryUpdated,
              onLogout = onLogout
            )
          }
          is WindowType.Statistics -> {
            StatisticsWindowContent(
              lang = lang,
              tasks = tasks
            )
          }
        }
      }
    }
  }
}

@Composable
fun WindowLauncherCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  color: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Card(
    modifier = modifier
      .clip(RoundedCornerShape(18.dp))
      .clickable { onClick() },
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .background(color.copy(alpha = 0.15f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        }

        Icon(
          imageVector = Icons.Default.OpenInNew,
          contentDescription = "Open Window",
          tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.size(16.dp)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium.copy(
          fontWeight = FontWeight.Bold
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

fun getIconForWindowType(type: WindowType): ImageVector {
  return when (type) {
    is WindowType.Dashboard -> Icons.Default.Task
    is WindowType.TaskManager -> Icons.Default.Task
    is WindowType.TaskDetail -> Icons.Default.Task
    is WindowType.AddEditTask -> Icons.Default.Add
    is WindowType.AiNotificationStudio -> Icons.Default.Psychology
    is WindowType.CloudSyncHub -> Icons.Default.CloudSync
    is WindowType.SecurityVault -> Icons.Default.EnhancedEncryption
    is WindowType.ProfileSettings -> Icons.Default.Settings
    is WindowType.Statistics -> Icons.Default.Assessment
  }
}
