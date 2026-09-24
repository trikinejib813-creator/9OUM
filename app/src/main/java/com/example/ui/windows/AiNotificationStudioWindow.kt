package com.example.ui.windows

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.notifications.AiFilterMode
import com.example.notifications.SmartNotificationManager

@Composable
fun AiNotificationStudioWindowContent(
  lang: AppLanguage,
  currentMode: AiFilterMode,
  onModeChange: (AiFilterMode) -> Unit
) {
  val context = LocalContext.current
  val notifManager = remember { SmartNotificationManager(context) }
  var testNotificationSent by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header Banner
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
      )
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(52.dp)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Psychology,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
          )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
          Text(
            text = AppStrings.get("smart_notifications_title", lang),
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold
            )
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = AppStrings.get("ai_filter_desc", lang),
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          )
        }
      }
    }

    // AI Filter Mode Selector
    Text(
      text = AppStrings.get("ai_filter_mode", lang),
      style = MaterialTheme.typography.titleSmall.copy(
        fontWeight = FontWeight.Bold
      )
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      AiFilterMode.entries.forEach { mode ->
        val isSelected = mode == currentMode
        val title = if (lang == AppLanguage.ARABIC) mode.titleAr else mode.titleEn
        val desc = when (mode) {
          AiFilterMode.HIGH_FOCUS -> if (lang == AppLanguage.ARABIC) "يتم كتم جميع التنبيهات الروتينية والسماح بالمهام الطارئة ذات النقاط > 70% فقط." else "Suppresses routine pings; only critical tasks with score > 70% notify you."
          AiFilterMode.BALANCED -> if (lang == AppLanguage.ARABIC) "توزيع ذكي بناءً على ساعات التركيز وتصنيف الأولويات لتفادي الإزعاج." else "Smart distribution based on productivity hours and priority analysis."
          AiFilterMode.ALL -> if (lang == AppLanguage.ARABIC) "إرسال كافة تنبيهات المهام المجدولة فوراً." else "Sends notifications for every scheduled task normally."
        }

        Surface(
          shape = RoundedCornerShape(16.dp),
          color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
          border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onModeChange(mode) }
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall.copy(
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              )
            }

            if (isSelected) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Active Mode",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
              )
            }
          }
        }
      }
    }

    // AI Stats Breakdown Card
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = if (lang == AppLanguage.ARABIC) "مؤشرات أداء الفلترة الذكية" else "AI Filtering Performance",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "88%",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            )
            Text(
              text = if (lang == AppLanguage.ARABIC) "دقة الفلترة" else "Accuracy",
              style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "14",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10B981)
              )
            )
            Text(
              text = if (lang == AppLanguage.ARABIC) "إشعارات هادئة" else "Filtered Quiet",
              style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "4",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEF4444)
              )
            )
            Text(
              text = if (lang == AppLanguage.ARABIC) "تنبيهات حرجة" else "Critical Alerts",
              style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(4.dp))

    // Interactive Test Notification Button
    Button(
      onClick = {
        val title = if (lang == AppLanguage.ARABIC) "تنبيه ذكي: تسليم التقرير السنوي" else "Smart Alert: Annual Report Review"
        val message = if (lang == AppLanguage.ARABIC) "قام محرك الذكاء الاصطناعي بترقية هذا الإشعار نظراً لأهميته وموعده القريب." else "AI filtered and prioritized this alert based on urgency score 92%."
        val sent = notifManager.sendNotification(1001, title, message, priorityScore = 92)
        testNotificationSent = true
        val toastMsg = if (sent) {
          AppStrings.get("notification_sent", lang)
        } else {
          if (lang == AppLanguage.ARABIC) "يرجى منح إذن الإشعارات من إعدادات النظام" else "Please grant notification permission in system settings"
        }
        Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp),
      shape = RoundedCornerShape(16.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary
      )
    ) {
      Icon(
        imageVector = Icons.Default.NotificationsActive,
        contentDescription = null,
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = AppStrings.get("test_ai_notification", lang),
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
      )
    }

    if (testNotificationSent) {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF10B981).copy(alpha = 0.15f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color(0xFF10B981),
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = AppStrings.get("notification_sent", lang),
            style = MaterialTheme.typography.bodySmall.copy(
              color = Color(0xFF10B981),
              fontWeight = FontWeight.SemiBold
            )
          )
        }
      }
    }
  }
}
