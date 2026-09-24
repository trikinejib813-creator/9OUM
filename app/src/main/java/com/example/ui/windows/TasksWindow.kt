package com.example.ui.windows

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskItem
import com.example.localization.AppLanguage
import com.example.localization.AppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksWindowContent(
  lang: AppLanguage,
  tasks: List<TaskItem>,
  onToggleComplete: (taskId: Long, completed: Boolean) -> Unit,
  onDeleteTask: (taskId: Long) -> Unit,
  onOpenTaskDetail: (taskId: Long) -> Unit,
  onAddNewTask: () -> Unit
) {
  var selectedFilter by remember { mutableStateOf("ALL") }

  val filteredTasks = remember(tasks, selectedFilter) {
    when (selectedFilter) {
      "PENDING" -> tasks.filter { !it.isCompleted }
      "COMPLETED" -> tasks.filter { it.isCompleted }
      "URGENT" -> tasks.filter { it.priority.equals("URGENT", ignoreCase = true) }
      else -> tasks
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      // Filter Chips
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 6.dp)
      ) {
        val filters = listOf(
          "ALL" to if (lang == AppLanguage.ARABIC) "الكل (${tasks.size})" else "All (${tasks.size})",
          "PENDING" to if (lang == AppLanguage.ARABIC) "قيد التنفيذ (${tasks.count { !it.isCompleted }})" else "Pending (${tasks.count { !it.isCompleted }})",
          "COMPLETED" to if (lang == AppLanguage.ARABIC) "المنجزة (${tasks.count { it.isCompleted }})" else "Done (${tasks.count { it.isCompleted }})",
          "URGENT" to if (lang == AppLanguage.ARABIC) "عاجل (${tasks.count { it.priority.equals("URGENT", true) }})" else "Urgent (${tasks.count { it.priority.equals("URGENT", true) }})"
        )
        items(filters) { (key, label) ->
          FilterChip(
            selected = selectedFilter == key,
            onClick = { selectedFilter = key },
            label = { Text(label) }
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      if (filteredTasks.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Tune,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
              modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = AppStrings.get("tasks_empty", lang),
              style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              ),
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          contentPadding = PaddingValues(bottom = 80.dp)
        ) {
          items(filteredTasks, key = { it.id }) { task ->
            TaskItemCard(
              task = task,
              lang = lang,
              onToggleComplete = { onToggleComplete(task.id, !task.isCompleted) },
              onDelete = { onDeleteTask(task.id) },
              onClick = { onOpenTaskDetail(task.id) }
            )
          }
        }
      }
    }

    // Floating Action Button
    FloatingActionButton(
      onClick = onAddNewTask,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(20.dp),
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
      Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
    }
  }
}

@Composable
fun TaskItemCard(
  task: TaskItem,
  lang: AppLanguage,
  onToggleComplete: () -> Unit,
  onDelete: () -> Unit,
  onClick: () -> Unit
) {
  val priorityColor = when (task.priority.uppercase()) {
    "URGENT" -> Color(0xFFEF4444)
    "HIGH" -> Color(0xFFF97316)
    "MEDIUM" -> Color(0xFF3B82F6)
    else -> Color(0xFF10B981)
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .clickable { onClick() },
    colors = CardDefaults.cardColors(
      containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
      else MaterialTheme.colorScheme.surface
    ),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (task.isCompleted) MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
      else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Checkbox
      IconButton(onClick = onToggleComplete) {
        Icon(
          imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
          contentDescription = "Toggle Complete",
          tint = if (task.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.width(6.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Priority badge
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = priorityColor.copy(alpha = 0.15f)
          ) {
            Text(
              text = task.priority,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              style = MaterialTheme.typography.labelSmall.copy(
                color = priorityColor,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
            )
          }

          // Category
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Text(
              text = task.category,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp
              )
            )
          }

          // AI Urgency Score Pill
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF6366F1).copy(alpha = 0.15f)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = null,
                tint = Color(0xFF818CF8),
                modifier = Modifier.size(10.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "AI ${task.aiUrgencyScore}%",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = Color(0xFF818CF8),
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 10.sp
                )
              )
            }
          }

          // Encrypted indicator
          Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Encrypted in DB",
            modifier = Modifier.size(12.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = task.title,
          style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.SemiBold,
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
            color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
          ),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        if (task.notes.isNotEmpty()) {
          Text(
            text = task.notes,
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      // Delete action
      IconButton(onClick = onDelete) {
        Icon(
          imageVector = Icons.Default.Delete,
          contentDescription = "Delete",
          tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}
