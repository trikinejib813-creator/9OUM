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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskItem
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.notifications.AiFilterMode
import com.example.notifications.SmartNotificationManager

@Composable
fun AddEditTaskWindowContent(
  lang: AppLanguage,
  existingTask: TaskItem? = null,
  aiFilterMode: AiFilterMode = AiFilterMode.BALANCED,
  onSaveTask: (TaskItem) -> Unit,
  onCancel: () -> Unit
) {
  val context = LocalContext.current
  val notifManager = remember { SmartNotificationManager(context) }

  var title by remember { mutableStateOf(existingTask?.title ?: "") }
  var notes by remember { mutableStateOf(existingTask?.notes ?: "") }
  var category by remember { mutableStateOf(existingTask?.category ?: if (lang == AppLanguage.ARABIC) "عمل" else "Work") }
  var priority by remember { mutableStateOf(existingTask?.priority ?: "MEDIUM") }
  var titleError by remember { mutableStateOf(false) }

  // Live AI evaluation of task content & urgency
  val aiDecision by remember(title, notes, priority, aiFilterMode) {
    derivedStateOf {
      notifManager.evaluateTask(title, notes, priority, aiFilterMode)
    }
  }

  val categories = remember(lang) {
    if (lang == AppLanguage.ARABIC) {
      listOf("عمل", "شخصي", "صحة", "دراسة", "مالي", "هام")
    } else {
      listOf("Work", "Personal", "Health", "Study", "Finance", "Critical")
    }
  }

  val priorities = listOf("URGENT", "HIGH", "MEDIUM", "LOW")

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Task Title
    Column {
      Text(
        text = AppStrings.get("task_title", lang),
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
      )
      Spacer(modifier = Modifier.height(6.dp))
      OutlinedTextField(
        value = title,
        onValueChange = {
          title = it
          if (titleError) titleError = false
        },
        placeholder = { Text(if (lang == AppLanguage.ARABIC) "مثال: مراجعة العقد وتسليم التقرير السنوي" else "e.g. Complete quarterly report review") },
        modifier = Modifier.fillMaxWidth(),
        isError = titleError,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = MaterialTheme.colorScheme.primary
        )
      )
      if (titleError) {
        Text(
          text = AppStrings.get("required_field", lang),
          color = MaterialTheme.colorScheme.error,
          style = MaterialTheme.typography.labelSmall,
          modifier = Modifier.padding(start = 6.dp, top = 4.dp)
        )
      }
    }

    // 2. Category selection chips
    Column {
      Text(
        text = AppStrings.get("task_category", lang),
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
      )
      Spacer(modifier = Modifier.height(6.dp))
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { cat ->
          FilterChip(
            selected = category == cat,
            onClick = { category = cat },
            label = { Text(cat) }
          )
        }
      }
    }

    // 3. Priority selection
    Column {
      Text(
        text = AppStrings.get("task_priority", lang),
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
      )
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        priorities.forEach { p ->
          val isSelected = priority.equals(p, ignoreCase = true)
          val color = when (p) {
            "URGENT" -> Color(0xFFEF4444)
            "HIGH" -> Color(0xFFF97316)
            "MEDIUM" -> Color(0xFF3B82F6)
            else -> Color(0xFF10B981)
          }

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, color) else null,
            modifier = Modifier
              .weight(1f)
              .clickable { priority = p }
          ) {
            Text(
              text = p,
              modifier = Modifier.padding(vertical = 10.dp),
              textAlign = androidx.compose.ui.text.style.TextAlign.Center,
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
              )
            )
          }
        }
      }
    }

    // 4. Confidential Notes (Encrypted)
    Column {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = AppStrings.get("task_notes", lang),
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Icon(
          imageVector = Icons.Default.Lock,
          contentDescription = "Encrypted in Database",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(14.dp)
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      OutlinedTextField(
        value = notes,
        onValueChange = { notes = it },
        placeholder = { Text(if (lang == AppLanguage.ARABIC) "ملاحظات سرية مشفرة محلياً..." else "Confidential details encrypted at rest...") },
        modifier = Modifier
          .fillMaxWidth()
          .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        maxLines = 4
      )
    }

    // 5. Live AI Smart Notification Analysis Card
    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
      ),
      border = androidx.compose.foundation.BorderStroke(
        1.dp,
        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
      )
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = AppStrings.get("smart_notifications_title", lang),
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
          }

          Text(
            text = "${aiDecision.priorityScore}%",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LinearProgressIndicator(
          progress = { aiDecision.priorityScore / 100f },
          modifier = Modifier.fillMaxWidth().height(8.dp),
          color = if (aiDecision.priorityScore >= 70) Color(0xFFEF4444) else MaterialTheme.colorScheme.primary,
          trackColor = MaterialTheme.colorScheme.surfaceVariant,
          strokeCap = StrokeCap.Round
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = if (lang == AppLanguage.ARABIC) aiDecision.rationaleAr else aiDecision.rationaleEn,
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.NotificationsActive,
            contentDescription = null,
            tint = if (aiDecision.shouldNotify) Color(0xFF10B981) else Color(0xFFF59E0B),
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (aiDecision.shouldNotify) {
              if (lang == AppLanguage.ARABIC) "مؤهل للإرسال الفوري عبر الفلترة الذكية" else "Eligible for smart notification delivery"
            } else {
              if (lang == AppLanguage.ARABIC) "مدرج في التقرير الهادئ المجمع" else "Queued for quiet digest"
            },
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.SemiBold,
              color = if (aiDecision.shouldNotify) Color(0xFF10B981) else Color(0xFFF59E0B)
            )
          )
        }
      }
    }

    // 6. Action buttons
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      TextButton(
        onClick = onCancel,
        modifier = Modifier.weight(1f)
      ) {
        Text(AppStrings.get("cancel", lang))
      }

      Button(
        onClick = {
          if (title.isBlank()) {
            titleError = true
            return@Button
          }
          val updated = TaskItem(
            id = existingTask?.id ?: 0L,
            title = title.trim(),
            notes = notes.trim(),
            category = category,
            priority = priority,
            aiUrgencyScore = aiDecision.priorityScore,
            aiFiltered = !aiDecision.shouldNotify,
            aiSummary = if (lang == AppLanguage.ARABIC) aiDecision.rationaleAr else aiDecision.rationaleEn,
            dueDateMillis = System.currentTimeMillis() + 86400000L,
            isCompleted = existingTask?.isCompleted ?: false,
            createdAt = existingTask?.createdAt ?: System.currentTimeMillis(),
            isSynced = false
          )
          onSaveTask(updated)
        },
        modifier = Modifier.weight(1.5f),
        shape = RoundedCornerShape(14.dp)
      ) {
        Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(AppStrings.get("save", lang), fontWeight = FontWeight.Bold)
      }
    }
  }
}
