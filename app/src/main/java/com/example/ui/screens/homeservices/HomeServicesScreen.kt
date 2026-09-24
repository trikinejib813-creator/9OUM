package com.example.ui.screens.homeservices

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.HomeServiceCategory
import com.example.data.model.HomeServiceOrder
import com.example.data.model.HomeServiceSettings
import com.example.data.model.HomeWorkOffer
import com.example.data.model.PREDEFINED_HOME_OFFERS
import com.example.data.model.PREDEFINED_HOME_SERVICES
import com.example.data.model.ServiceUrgency
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.screens.settings.HomeServiceSettingsDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeServicesScreen(
  lang: AppLanguage,
  orders: List<HomeServiceOrder>,
  settings: HomeServiceSettings,
  onCreateOrder: (
    category: String,
    subtype: String,
    urgency: ServiceUrgency,
    time: String,
    address: String,
    name: String,
    phone: String,
    notes: String,
    costMin: Int,
    costMax: Int
  ) -> Unit,
  onUpdateOrderStatus: (orderId: String, status: String) -> Unit,
  onCancelOrder: (orderId: String) -> Unit,
  onUpdateSettings: (HomeServiceSettings) -> Unit,
  onOpenChat: (HomeServiceOrder) -> Unit,
  onOpenOfferChat: (HomeWorkOffer) -> Unit
) {
  val context = LocalContext.current
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Offers, 1: Orders & Chats, 2: Categories
  var selectedCategoryForOrder by remember { mutableStateOf<HomeServiceCategory?>(null) }
  var isEmergencyMode by remember { mutableStateOf(false) }
  var showSettingsDialog by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
  ) {
    // Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp, bottom = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = AppStrings.get("home_services_title", lang),
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
        )
        Text(
          text = AppStrings.get("home_services_subtitle", lang),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        IconButton(
          onClick = { showSettingsDialog = true },
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            Icons.Default.Tune,
            contentDescription = AppStrings.get("home_settings_title", lang),
            tint = Color(0xFFDC2626)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Interactive Tab Row
    androidx.compose.material3.TabRow(
      selectedTabIndex = selectedTab,
      containerColor = Color.Transparent,
      contentColor = MaterialTheme.colorScheme.primary,
      modifier = Modifier.fillMaxWidth()
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = {
          Text(
            text = "عروض الأشغال (${PREDEFINED_HOME_OFFERS.size})",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal)
          )
        },
        icon = {
          Icon(Icons.Default.LocalOffer, contentDescription = null, modifier = Modifier.size(18.dp))
        }
      )

      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = {
          Text(
            text = "طلباتي ومحادثاتي (${orders.size})",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal)
          )
        },
        icon = {
          Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
        }
      )

      Tab(
        selected = selectedTab == 2,
        onClick = { selectedTab = 2 },
        text = {
          Text(
            text = "حجز خدمة جديدة",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal)
          )
        },
        icon = {
          Icon(Icons.Default.Handyman, contentDescription = null, modifier = Modifier.size(18.dp))
        }
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Content based on selected tab
    when (selectedTab) {
      0 -> {
        // Tab 0: Home Work Offers list
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          item {
            // Promo Banner
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("💬", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "محادثة فورية مباشرة مع مقدمي العروض والورش",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                  )
                  Text(
                    text = "اضغط على أيقونة المحادثة في أي عرض لفتح صفحة محادثة خاصة والاستفسار مباشرة!",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }
          }

          items(PREDEFINED_HOME_OFFERS, key = { it.id }) { offer ->
            HomeOfferCard(
              offer = offer,
              onOpenChat = { onOpenOfferChat(offer) },
              onCall = {
                try {
                  val callIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${offer.phone}")
                  }
                  context.startActivity(callIntent)
                } catch (e: Exception) {
                  // safe fallback
                }
              }
            )
          }

          item {
            Spacer(modifier = Modifier.height(12.dp))
          }
        }
      }

      1 -> {
        // Tab 1: Orders and ongoing chats
        if (orders.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(24.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("📋", fontSize = 48.sp)
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "لا توجد طلبات صيانة نشطة حالياً",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "يمكنك استعراض عروض الأشغال المعتمدة أو حجز موعد جديد لبدء محادثة مباشرة مع الفني.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
              Spacer(modifier = Modifier.height(16.dp))
              Button(
                onClick = { selectedTab = 0 },
                shape = RoundedCornerShape(10.dp)
              ) {
                Icon(Icons.Default.LocalOffer, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("تصفح عروض الأشغال المتاحة")
              }
            }
          }
        } else {
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "طلبات الصيانة والمحادثات الجارية:",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  text = "${orders.size} طلب مسجل",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }

            items(orders, key = { it.id }) { order ->
              ActiveOrderCard(
                order = order,
                lang = lang,
                onOpenChat = { onOpenChat(order) },
                onAdvanceStatus = { nextStatus -> onUpdateOrderStatus(order.id, nextStatus) },
                onCancel = { onCancelOrder(order.id) }
              )
            }

            item {
              Spacer(modifier = Modifier.height(12.dp))
            }
          }
        }
      }

      2 -> {
        // Tab 2: New Service Booking
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          item {
            // 30-Minute Emergency Dispatch Banner
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = Color(0xFF7F1D1D)),
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  isEmergencyMode = true
                  selectedCategoryForOrder = PREDEFINED_HOME_SERVICES.firstOrNull()
                }
            ) {
              Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                      shape = CircleShape,
                      color = Color(0xFFDC2626),
                      modifier = Modifier.size(24.dp)
                    ) {
                      Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                      }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = AppStrings.get("emergency_30min_card", lang),
                      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                    )
                  }

                  Spacer(modifier = Modifier.height(4.dp))

                  Text(
                    text = AppStrings.get("emergency_desc", lang),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFFCA5A5)
                  )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                  onClick = {
                    isEmergencyMode = true
                    selectedCategoryForOrder = PREDEFINED_HOME_SERVICES.firstOrNull()
                  },
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626), contentColor = Color.White)
                ) {
                  Text(text = "طلب فوري", fontWeight = FontWeight.Bold)
                }
              }
            }
          }

          item {
            Text(
              text = "جميع مجالات الصيانة والأشغال المنزلية:",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
          }

          items(PREDEFINED_HOME_SERVICES.chunked(2)) { pair ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Box(modifier = Modifier.weight(1f)) {
                ServiceCategoryCard(
                  category = pair[0],
                  onClick = {
                    isEmergencyMode = false
                    selectedCategoryForOrder = pair[0]
                  }
                )
              }
              if (pair.size > 1) {
                Box(modifier = Modifier.weight(1f)) {
                  ServiceCategoryCard(
                    category = pair[1],
                    onClick = {
                      isEmergencyMode = false
                      selectedCategoryForOrder = pair[1]
                    }
                  )
                }
              } else {
                Spacer(modifier = Modifier.weight(1f))
              }
            }
          }

          item {
            Spacer(modifier = Modifier.height(14.dp))
          }
        }
      }
    }
  }

  // Book Service / Emergency Order Dialog
  selectedCategoryForOrder?.let { cat ->
    BookHomeServiceDialog(
      category = cat,
      lang = lang,
      defaultAddress = settings.defaultHomeAddress,
      isEmergencyInitial = isEmergencyMode,
      onDismiss = {
        selectedCategoryForOrder = null
        isEmergencyMode = false
      },
      onSubmit = { subtype, urgency, time, address, name, phone, notes, cMin, cMax ->
        onCreateOrder(cat.nameAr, subtype, urgency, time, address, name, phone, notes, cMin, cMax)
        selectedCategoryForOrder = null
        isEmergencyMode = false
        selectedTab = 1 // Switch to active orders so user can see it and chat!
      }
    )
  }

  // Dedicated Settings Dialog
  if (showSettingsDialog) {
    HomeServiceSettingsDialog(
      currentSettings = settings,
      lang = lang,
      onSave = onUpdateSettings,
      onDismiss = { showSettingsDialog = false }
    )
  }
}

@Composable
fun HomeOfferCard(
  offer: HomeWorkOffer,
  onOpenChat: () -> Unit,
  onCall: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = MaterialTheme.colorScheme.surface,
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0x262563EB),
            modifier = Modifier.size(38.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(offer.iconEmoji, fontSize = 20.sp)
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = offer.title,
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              maxLines = 2
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = offer.providerName,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.width(4.dp))
              Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("⭐ ${offer.providerRating} (${offer.reviewsCount})", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold))
            }
          }
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = Color(0x26DC2626)
        ) {
          Text(
            text = offer.badgeText,
            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFDC2626), fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = offer.description,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 18.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = offer.price,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = Color(0xFF10B981))
          )
          offer.originalPrice?.let { orig ->
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = orig,
              style = MaterialTheme.typography.labelSmall.copy(
                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onCall,
            modifier = Modifier
              .size(36.dp)
              .background(Color(0x1A10B981), CircleShape)
          ) {
            Icon(Icons.Default.Phone, contentDescription = "اتصال", tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
          }

          // Dedicated Chat Button with Chat Icon
          Button(
            onClick = onOpenChat,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF2563EB),
              contentColor = Color.White
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            modifier = Modifier.testTag("offer_chat_button_${offer.id}")
          ) {
            Icon(
              Icons.AutoMirrored.Filled.Chat,
              contentDescription = "محادثة",
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text("محادثة واستفسار", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
          }
        }
      }
    }
  }
}

@Composable
fun ServiceCategoryCard(
  category: HomeServiceCategory,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0x26DC2626),
          modifier = Modifier.size(42.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(category.iconEmoji, fontSize = 22.sp)
          }
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = MaterialTheme.colorScheme.surface
        ) {
          Text(
            text = "من ${category.basePriceMin} ر.س",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF10B981)),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = category.nameAr,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
        maxLines = 1
      )

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = category.commonIssues.joinToString("، "),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 2
      )
    }
  }
}

@Composable
fun ActiveOrderCard(
  order: HomeServiceOrder,
  lang: AppLanguage,
  onOpenChat: () -> Unit,
  onAdvanceStatus: (String) -> Unit,
  onCancel: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = MaterialTheme.colorScheme.surface,
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "${order.serviceCategory} - ${order.issueSubtype}",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )

        val statusBadge = when (order.orderStatus) {
          "DISPATCHED" -> AppStrings.get("order_status_dispatched", lang)
          "ON_THE_WAY" -> AppStrings.get("order_status_ontheway", lang)
          "IN_PROGRESS" -> AppStrings.get("order_status_inprogress", lang)
          "COMPLETED" -> AppStrings.get("order_status_completed", lang)
          else -> order.orderStatus
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = Color(0x2610B981)
        ) {
          Text(
            text = statusBadge,
            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF10B981), fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "${AppStrings.get("technician_assigned", lang)} ${order.assignedTechnicianName}",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = Color(0xFFDC2626)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(14.dp))
      }

      Text(
        text = "العنوان: ${order.address}",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "التكلفة المتوقعة: ${order.estimatedCostMin}-${order.estimatedCostMax} ر.س",
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Action row: Primary Chat Button with Chat Icon + Advance Status & Cancel
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Chat button with Chat Icon
        Button(
          onClick = onOpenChat,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2563EB),
            contentColor = Color.White
          ),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          modifier = Modifier.testTag("chat_button_${order.id}")
        ) {
          Icon(
            Icons.AutoMirrored.Filled.Chat,
            contentDescription = "محادثة مع الفني",
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "محادثة الفني",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
          if (order.orderStatus != "COMPLETED") {
            Button(
              onClick = {
                val next = when (order.orderStatus) {
                  "DISPATCHED" -> "ON_THE_WAY"
                  "ON_THE_WAY" -> "IN_PROGRESS"
                  else -> "COMPLETED"
                }
                onAdvanceStatus(next)
              },
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981), contentColor = Color.White),
              contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text("تحديث المرحلة", style = MaterialTheme.typography.labelSmall)
            }
          }

          OutlinedButton(
            onClick = onCancel,
            shape = RoundedCornerShape(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text("إلغاء", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookHomeServiceDialog(
  category: HomeServiceCategory,
  lang: AppLanguage,
  defaultAddress: String,
  isEmergencyInitial: Boolean,
  onDismiss: () -> Unit,
  onSubmit: (
    subtype: String,
    urgency: ServiceUrgency,
    time: String,
    address: String,
    name: String,
    phone: String,
    notes: String,
    costMin: Int,
    costMax: Int
  ) -> Unit
) {
  var selectedSubtype by remember { mutableStateOf(category.commonIssues.firstOrNull() ?: "") }
  var isEmergency by remember { mutableStateOf(isEmergencyInitial) }
  var address by remember { mutableStateOf(defaultAddress) }
  var name by remember { mutableStateOf("سلطان الشهري") }
  var phone by remember { mutableStateOf("+966541234567") }
  var notes by remember { mutableStateOf("") }
  var scheduledTime by remember { mutableStateOf("خلال ساعتين") }

  var subtypeExpanded by remember { mutableStateOf(false) }

  val costMin = if (isEmergency) category.basePriceMin + 50 else category.basePriceMin
  val costMax = if (isEmergency) category.basePriceMax + 100 else category.basePriceMax

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = AppStrings.get("new_order_dialog_title", lang),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = null)
          }
        }

        Text(
          text = category.nameAr,
          style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFDC2626), fontWeight = FontWeight.SemiBold)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Urgency Toggle Banner
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (isEmergency) Color(0x33DC2626) else MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier
            .fillMaxWidth()
            .clickable { isEmergency = !isEmergency }
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.FlashOn,
                contentDescription = null,
                tint = if (isEmergency) Color(0xFFDC2626) else MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "طوارئ فورية خلال 30 دقيقة",
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  text = if (isEmergency) "فنيون جاهزون للتحرك الفوري" else "حجز بموعد عادي",
                  style = MaterialTheme.typography.labelSmall
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (isEmergency) Color(0xFFDC2626) else MaterialTheme.colorScheme.surface
            ) {
              Text(
                text = if (isEmergency) "مفعّل ⚡" else "تفعيل",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = if (isEmergency) Color.White else MaterialTheme.colorScheme.onSurface,
                  fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Subtype selector
        ExposedDropdownMenuBox(
          expanded = subtypeExpanded,
          onExpandedChange = { subtypeExpanded = it }
        ) {
          OutlinedTextField(
            value = selectedSubtype,
            onValueChange = {},
            readOnly = true,
            label = { Text(AppStrings.get("select_issue_label", lang)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subtypeExpanded) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .menuAnchor()
          )
          ExposedDropdownMenu(
            expanded = subtypeExpanded,
            onDismissRequest = { subtypeExpanded = false }
          ) {
            category.commonIssues.forEach { issue ->
              DropdownMenuItem(
                text = { Text(issue) },
                onClick = {
                  selectedSubtype = issue
                  subtypeExpanded = false
                }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = address,
          onValueChange = { address = it },
          label = { Text(AppStrings.get("address_label", lang)) },
          leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFDC2626)) },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("اسم العميل") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("رقم هاتف التواصل المباشر") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text(AppStrings.get("client_notes_label", lang)) },
          placeholder = { Text("مثال: تسريب مياه في المطبخ تحت الحوض") },
          minLines = 2,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Cost estimation notice
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.surfaceVariant
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = AppStrings.get("estimated_cost_range", lang),
              style = MaterialTheme.typography.labelSmall
            )
            Text(
              text = "$costMin - $costMax ر.س",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = {
            val urg = if (isEmergency) ServiceUrgency.EMERGENCY_30MIN else ServiceUrgency.SCHEDULED
            val time = if (isEmergency) "طوارئ فورية خلال 30 دقيقة" else scheduledTime
            onSubmit(selectedSubtype, urg, time, address, name, phone, notes, costMin, costMax)
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isEmergency) Color(0xFFDC2626) else Color(0xFFF59E0B),
            contentColor = if (isEmergency) Color.White else Color(0xFF0F172A)
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(if (isEmergency) Icons.Default.FlashOn else Icons.Default.Close, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text(AppStrings.get("confirm_order_btn", lang), fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
