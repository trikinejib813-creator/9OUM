package com.example.ui.screens.homeservices

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.HomeServiceChatMessage
import com.example.data.model.HomeServiceOrder
import com.example.data.model.ServiceUrgency
import com.example.localization.AppLanguage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeServiceChatScreen(
  order: HomeServiceOrder,
  messages: List<HomeServiceChatMessage>,
  isTechnicianTyping: Boolean,
  lang: AppLanguage,
  onSendMessage: (text: String) -> Unit,
  onBack: () -> Unit
) {
  val context = LocalContext.current
  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()

  var messageInput by remember { mutableStateOf("") }
  var showDetailsBanner by remember { mutableStateOf(true) }
  var showAttachDialog by remember { mutableStateOf(false) }

  // Quick suggestions
  val quickReplies = listOf(
    "أنا بانتظارك في الموقع 📍",
    "كم الوقت المتوقع للوصول؟ ⏱️",
    "هل أحتاج تجهيز أي أدوات؟ 🧰",
    "تم إغلاق المحبس الرئيسي احتياطاً 🚿",
    "يرجى التواصل هاتفياً عند وصولك 📞"
  )

  // Auto scroll to bottom when new messages arrive
  LaunchedEffect(messages.size, isTechnicianTyping) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size)
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface,
          titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("chat_back_button")
          ) {
            Icon(
              Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "رجوع"
            )
          }
        },
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            // Avatar with online pulse
            Box(contentAlignment = Alignment.BottomEnd) {
              Surface(
                shape = CircleShape,
                color = Color(0xFFDC2626).copy(alpha = 0.15f),
                modifier = Modifier.size(42.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(
                    text = when (order.serviceCategory) {
                      "كهرباء منزلية" -> "⚡"
                      "سباكة وصحي" -> "🔧"
                      "تكييف وتبريد" -> "❄️"
                      "تنظيف شامل وتعقيم" -> "✨"
                      else -> "🛠️"
                    },
                    fontSize = 20.sp
                  )
                }
              }

              // Green online indicator
              Box(
                modifier = Modifier
                  .size(12.dp)
                  .clip(CircleShape)
                  .background(Color(0xFF10B981))
                  .padding(1.5.dp)
              )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = order.assignedTechnicianName,
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  maxLines = 1
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                  Icons.Default.Verified,
                  contentDescription = "معتمد",
                  tint = Color(0xFF2563EB),
                  modifier = Modifier.size(16.dp)
                )
              }

              Text(
                text = if (isTechnicianTyping) "يكتب الآن... ✍️" else "متصل الآن • ${order.serviceCategory}",
                style = MaterialTheme.typography.labelSmall,
                color = if (isTechnicianTyping) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        },
        actions = {
          // Direct phone call button
          IconButton(
            onClick = {
              try {
                val callIntent = Intent(Intent.ACTION_DIAL).apply {
                  data = Uri.parse("tel:${order.assignedTechnicianPhone}")
                }
                context.startActivity(callIntent)
              } catch (e: Exception) {
                // handled safely
              }
            },
            modifier = Modifier.testTag("chat_call_button")
          ) {
            Icon(
              Icons.Default.Phone,
              contentDescription = "اتصال بالفني",
              tint = Color(0xFF10B981)
            )
          }

          // Toggle service info banner
          IconButton(onClick = { showDetailsBanner = !showDetailsBanner }) {
            Icon(
              Icons.Default.Info,
              contentDescription = "تفاصيل الطلب",
              tint = MaterialTheme.colorScheme.primary
            )
          }
        }
      )
    },
    bottomBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surface)
          .navigationBarsPadding()
          .imePadding()
      ) {
        // Quick Reply Chips
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          quickReplies.forEach { reply ->
            AssistChip(
              onClick = {
                onSendMessage(reply)
              },
              label = {
                Text(
                  text = reply,
                  style = MaterialTheme.typography.labelSmall
                )
              },
              colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
              ),
              shape = RoundedCornerShape(16.dp)
            )
          }
        }

        // Input bar
        Surface(
          tonalElevation = 2.dp,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Attach media button
            IconButton(
              onClick = { showAttachDialog = true },
              modifier = Modifier.size(40.dp)
            ) {
              Icon(
                Icons.Default.AddPhotoAlternate,
                contentDescription = "إرفاق صورة أو مستند",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            // Message text field
            OutlinedTextField(
              value = messageInput,
              onValueChange = { messageInput = it },
              placeholder = {
                Text(
                  text = "اكتب رسالتك للفني...",
                  style = MaterialTheme.typography.bodyMedium
                )
              },
              modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp)
                .testTag("chat_input_field"),
              shape = RoundedCornerShape(24.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
              ),
              maxLines = 4
            )

            // Send Button
            IconButton(
              onClick = {
                if (messageInput.isNotBlank()) {
                  val textToSend = messageInput
                  messageInput = ""
                  onSendMessage(textToSend)
                }
              },
              enabled = messageInput.isNotBlank(),
              modifier = Modifier
                .size(42.dp)
                .background(
                  color = if (messageInput.isNotBlank()) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f),
                  shape = CircleShape
                )
                .testTag("chat_send_button")
            ) {
              Icon(
                Icons.AutoMirrored.Filled.Send,
                contentDescription = "إرسال",
                tint = if (messageInput.isNotBlank()) MaterialTheme.colorScheme.onPrimary else Color.DarkGray
              )
            }
          }
        }
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // Collapsible Order Details Banner
      AnimatedVisibility(visible = showDetailsBanner) {
        OrderContextBanner(
          order = order,
          onClose = { showDetailsBanner = false }
        )
      }

      // Messages Timeline
      LazyColumn(
        state = listState,
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        item {
          Spacer(modifier = Modifier.height(8.dp))
          // Date separator
          Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
          ) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
              Text(
                text = "اليوم - محادثة مباشرة خاصة بطلب الخدمة",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(4.dp))
        }

        items(messages, key = { it.id }) { msg ->
          ChatMessageBubble(message = msg)
        }

        // Typing indicator row
        if (isTechnicianTyping) {
          item {
            TypingIndicatorBubble(technicianName = order.assignedTechnicianName)
          }
        }

        item {
          Spacer(modifier = Modifier.height(8.dp))
        }
      }
    }
  }

  // Quick Attachment Dialog
  if (showAttachDialog) {
    Dialog(onDismissRequest = { showAttachDialog = false }) {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth(0.92f)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "إرفاق للعطل والصيانة",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "اختر ما ترغب بإرساله إلى الفني لمعاينة العطل:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(14.dp))

          AttachmentOptionItem(
            iconEmoji = "📸",
            title = "إرسال صورة واضحة للعطل",
            subtitle = "صورة للمحبس، القاطع أو التسريب",
            onClick = {
              showAttachDialog = false
              onSendMessage("تم إرفاق صورة العطل للمعاينة المباشرة 📸")
            }
          )

          Spacer(modifier = Modifier.height(8.dp))

          AttachmentOptionItem(
            iconEmoji = "📍",
            title = "مشاركة الموقع الجغرافي الدقيق",
            subtitle = order.address,
            onClick = {
              showAttachDialog = false
              onSendMessage("موقعي الدقيق على الخريطة: ${order.address} 📍")
            }
          )

          Spacer(modifier = Modifier.height(8.dp))

          AttachmentOptionItem(
            iconEmoji = "📝",
            title = "إرسال ملاحظة أو تفاصيل إضافية",
            subtitle = order.notes.ifBlank { "تفاصيل وتوجيهات للوصول" },
            onClick = {
              showAttachDialog = false
              val note = order.notes.ifBlank { "العمارة على الشارع الرئيسي، يرجى الاتصال عند الباب" }
              onSendMessage("ملاحظة هامة: $note")
            }
          )
        }
      }
    }
  }
}

@Composable
fun OrderContextBanner(
  order: HomeServiceOrder,
  onClose: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
    ),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (order.urgency == ServiceUrgency.EMERGENCY_30MIN) Color(0xFFDC2626) else Color(0xFF2563EB)
          ) {
            Text(
              text = if (order.urgency == ServiceUrgency.EMERGENCY_30MIN) "⚡ طوارئ 30 دقيقة" else "📅 موعد صيانة",
              style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Text(
            text = "${order.serviceCategory} • ${order.issueSubtype}",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            maxLines = 1
          )
        }

        Text(
          text = "${order.estimatedCostMin}-${order.estimatedCostMax} ر.س",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = order.address,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "ضمان رسمي 30 يوم",
            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF10B981), fontWeight = FontWeight.Medium)
          )
        }
      }
    }
  }
}

@Composable
fun ChatMessageBubble(message: HomeServiceChatMessage) {
  val isMe = message.isFromMe
  val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
  val formattedTime = timeFormat.format(Date(message.timestamp))

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
  ) {
    if (!isMe) {
      Surface(
        shape = CircleShape,
        color = Color(0xFFDC2626).copy(alpha = 0.15f),
        modifier = Modifier
          .size(32.dp)
          .align(Alignment.Bottom)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text("🛠️", fontSize = 14.sp)
        }
      }
      Spacer(modifier = Modifier.width(6.dp))
    }

    Column(
      horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
      modifier = Modifier.widthIn(max = 290.dp)
    ) {
      if (!isMe) {
        Text(
          text = message.senderName,
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFFDC2626)),
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }

      Surface(
        shape = if (isMe) {
          RoundedCornerShape(18.dp, 18.dp, 3.dp, 18.dp)
        } else {
          RoundedCornerShape(18.dp, 18.dp, 18.dp, 3.dp)
        },
        color = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = if (isMe) 2.dp else 1.dp
      ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
          Text(
            text = message.text,
            style = MaterialTheme.typography.bodyMedium.copy(
              color = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 20.sp
            )
          )

          Spacer(modifier = Modifier.height(4.dp))

          Row(
            modifier = Modifier.align(Alignment.End),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = formattedTime,
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                color = if (isMe) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
              )
            )

            if (isMe) {
              Spacer(modifier = Modifier.width(3.dp))
              Icon(
                Icons.Default.DoneAll,
                contentDescription = "تم القراءة",
                tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                modifier = Modifier.size(13.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun TypingIndicatorBubble(technicianName: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.Start,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Surface(
      shape = CircleShape,
      color = Color(0xFFDC2626).copy(alpha = 0.15f),
      modifier = Modifier.size(28.dp)
    ) {
      Box(contentAlignment = Alignment.Center) {
        Text("🛠️", fontSize = 12.sp)
      }
    }

    Spacer(modifier = Modifier.width(6.dp))

    Surface(
      shape = RoundedCornerShape(14.dp),
      color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "$technicianName يكتب الآن...",
          style = MaterialTheme.typography.labelSmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
          )
        )
      }
    }
  }
}

@Composable
fun AttachmentOptionItem(
  iconEmoji: String,
  title: String,
  subtitle: String,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(iconEmoji, fontSize = 22.sp)
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
        Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }
  }
}
