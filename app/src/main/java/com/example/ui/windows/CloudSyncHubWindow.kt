package com.example.ui.windows

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CloudSyncHubWindowContent(
  lang: AppLanguage,
  isOnline: Boolean,
  unsyncedCount: Int,
  isSyncing: Boolean,
  lastSyncTimestamp: Long,
  onTriggerSync: () -> Unit
) {
  val dateFormatted = rememberDateFormatted(lastSyncTimestamp)

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Connection Status Card
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(
        containerColor = if (isOnline) Color(0xFF10B981).copy(alpha = 0.12f)
        else Color(0xFFEF4444).copy(alpha = 0.12f)
      ),
      border = androidx.compose.foundation.BorderStroke(
        1.5.dp,
        if (isOnline) Color(0xFF10B981).copy(alpha = 0.4f) else Color(0xFFEF4444).copy(alpha = 0.4f)
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
            .size(54.dp)
            .background(
              if (isOnline) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFFEF4444).copy(alpha = 0.2f),
              CircleShape
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
            contentDescription = null,
            tint = if (isOnline) Color(0xFF10B981) else Color(0xFFEF4444),
            modifier = Modifier.size(28.dp)
          )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
          Text(
            text = if (isOnline) AppStrings.get("connection_status_online", lang)
            else AppStrings.get("connection_status_offline", lang),
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = if (isOnline) Color(0xFF10B981) else Color(0xFFEF4444)
            )
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = if (isOnline) {
              if (lang == AppLanguage.ARABIC) "المزامنة المشفرة نشطة وتعمل في الخلفية" else "Encrypted real-time synchronization active"
            } else {
              AppStrings.get("offline_notice", lang)
            },
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          )
        }
      }
    }

    // 2. Sync Metrics Card
    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
      Column(
        modifier = Modifier.padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (lang == AppLanguage.ARABIC) "عناصر بانتظار المزامنة" else "Queued Sync Items",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
          )
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (unsyncedCount > 0) Color(0xFFF59E0B).copy(alpha = 0.2f) else Color(0xFF10B981).copy(alpha = 0.2f)
          ) {
            Text(
              text = "$unsyncedCount",
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (unsyncedCount > 0) Color(0xFFF59E0B) else Color(0xFF10B981)
              )
            )
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (lang == AppLanguage.ARABIC) "آخر مزامنة ناجحة" else "Last Successful Sync",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
          )
          Text(
            text = dateFormatted,
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (lang == AppLanguage.ARABIC) "بروتوكول حماية السحابة" else "Cloud Security Protocol",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "E2EE (AES-256)",
              style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
              )
            )
          }
        }
      }
    }

    // 3. Manual Sync Button
    Button(
      onClick = onTriggerSync,
      enabled = !isSyncing,
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp),
      shape = RoundedCornerShape(16.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary
      )
    ) {
      if (isSyncing) {
        CircularProgressIndicator(
          modifier = Modifier.size(22.dp),
          color = MaterialTheme.colorScheme.onPrimary,
          strokeWidth = 2.5.dp
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = AppStrings.get("sync_status_syncing", lang),
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
      } else {
        Icon(imageVector = Icons.Default.CloudSync, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = AppStrings.get("sync_now", lang),
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
      }
    }

    // Explanatory badge
    Surface(
      shape = RoundedCornerShape(14.dp),
      color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(14.dp),
        verticalAlignment = Alignment.Top
      ) {
        Icon(
          imageVector = Icons.Default.CheckCircle,
          contentDescription = null,
          tint = Color(0xFF10B981),
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = if (lang == AppLanguage.ARABIC) {
            "تم تصميم النظام وفق مبدأ Offline-First. عندما تكون غير متصل بالإنترنت، يتم الاحتفاظ بجميع التعديلات في قاعدة بيانات Room المشفرة محلياً، وبمجرد عودة الاتصال يقوم النظام بالمزامنة الفورية دون أي فقد للبيانات."
          } else {
            "Engineered with an Offline-First architecture. When disconnected, all mutations are securely stored in the encrypted local Room database and synchronized seamlessly the moment connectivity is restored."
          },
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            lineHeight = 18.sp
          )
        )
      }
    }
  }
}

@Composable
fun rememberDateFormatted(timestamp: Long): String {
  return androidx.compose.runtime.remember(timestamp) {
    if (timestamp == 0L) return@remember "N/A"
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    sdf.format(Date(timestamp))
  }
}
