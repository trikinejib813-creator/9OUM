package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity

enum class AiFilterMode(val titleAr: String, val titleEn: String) {
  HIGH_FOCUS("فلترة فائقة التركيز (المهام الحرجة)", "High Focus (Critical Only)"),
  BALANCED("تكيّف ذكي متوازن", "Balanced Adaptive"),
  ALL("السماح بجميع التنبيهات", "All Notifications")
}

data class AiNotificationDecision(
  val shouldNotify: Boolean,
  val priorityScore: Int, // 0 - 100
  val rationaleAr: String,
  val rationaleEn: String,
  val recommendedTimeMinutes: Int
)

class SmartNotificationManager(private val context: Context) {
  companion object {
    const val CHANNEL_ID = "aurasync_smart_channel"
    const val CHANNEL_NAME = "AuraSync Smart Notifications"
    const val CHANNEL_DESC = "Smart AI prioritized reminders and daily tasks"
  }

  init {
    createNotificationChannel()
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val importance = NotificationManager.IMPORTANCE_HIGH
      val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
        description = CHANNEL_DESC
        enableVibration(true)
        vibrationPattern = longArrayOf(0, 250, 150, 250)
        enableLights(true)
      }
      val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
      notificationManager?.createNotificationChannel(channel)
    }
  }

  fun evaluateTask(
    title: String,
    notes: String,
    priority: String,
    filterMode: AiFilterMode
  ): AiNotificationDecision {
    val text = (title + " " + notes).lowercase()

    val criticalKeywords = listOf(
      "عاجل", "ضروري", "هام", "طارئ", "تسليم", "موعد", "دفع", "صحة", "دواء", "طبيب", "اجتماع",
      "urgent", "critical", "deadline", "asap", "emergency", "payment", "doctor", "meeting", "important"
    )

    val mediumKeywords = listOf(
      "مراجعة", "تقرير", "شراء", "اتصال", "تذكير", "عمل", "دراسة",
      "review", "report", "buy", "call", "reminder", "work", "study"
    )

    var score = when (priority.uppercase()) {
      "URGENT" -> 85
      "HIGH" -> 70
      "MEDIUM" -> 45
      else -> 25
    }

    var keywordHits = 0
    for (kw in criticalKeywords) {
      if (text.contains(kw)) {
        score += 15
        keywordHits++
      }
    }
    for (kw in mediumKeywords) {
      if (text.contains(kw)) {
        score += 8
        keywordHits++
      }
    }

    score = score.coerceIn(5, 100)

    val shouldNotify = when (filterMode) {
      AiFilterMode.HIGH_FOCUS -> score >= 70
      AiFilterMode.BALANCED -> score >= 40
      AiFilterMode.ALL -> true
    }

    val (rationaleAr, rationaleEn) = when {
      score >= 80 -> Pair(
        "تمت ترقية التنبيه إلى أولوية حرجة بناءً على تحليل الكلمات المفتاحية ($keywordHits) وتحديد موعد حاسم.",
        "Upgraded to critical priority by AI filter based on detected urgency keywords ($keywordHits)."
      )
      score >= 50 -> Pair(
        "تم تصنيف التنبيه كمتوسط الأهمية، مناسب لجدولة تذكير في أوقات النشاط والإنتاجية.",
        "Classified as moderate urgency; scheduled for standard daytime delivery."
      )
      else -> Pair(
        "تم تصنيف التنبيه كمنخفض الإلحاح لتجنب التشتيت والمحافظة على تركيزك.",
        "Filtered as low urgency to maintain focus and prevent notification fatigue."
      )
    }

    return AiNotificationDecision(
      shouldNotify = shouldNotify,
      priorityScore = score,
      rationaleAr = rationaleAr,
      rationaleEn = rationaleEn,
      recommendedTimeMinutes = if (score >= 80) 15 else 60
    )
  }

  fun sendNotification(
    id: Int,
    title: String,
    message: String,
    priorityScore: Int = 80
  ): Boolean {
    // Check permission on Android 13+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      val hasPermission = ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.POST_NOTIFICATIONS
      ) == PackageManager.PERMISSION_GRANTED
      if (!hasPermission) {
        return false
      }
    }

    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(
      context,
      0,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val iconRes = android.R.drawable.ic_dialog_info

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(iconRes)
      .setContentTitle(title)
      .setContentText(message)
      .setStyle(NotificationCompat.BigTextStyle().bigText(message))
      .setPriority(
        if (priorityScore >= 75) NotificationCompat.PRIORITY_HIGH
        else NotificationCompat.PRIORITY_DEFAULT
      )
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)

    try {
      with(NotificationManagerCompat.from(context)) {
        notify(id, builder.build())
      }
      return true
    } catch (_: SecurityException) {
      return false
    }
  }
}
