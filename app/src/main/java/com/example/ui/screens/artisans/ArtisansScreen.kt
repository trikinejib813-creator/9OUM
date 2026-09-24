package com.example.ui.screens.artisans

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Artisan
import com.example.data.model.ArtisanSettings
import com.example.data.model.PREDEFINED_ARTISAN_TRADES
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.screens.settings.ArtisanSettingsDialog

@Composable
fun ArtisansScreen(
  lang: AppLanguage,
  artisans: List<Artisan>,
  searchQuery: String,
  selectedTrade: String,
  emergencyOnly: Boolean,
  settings: ArtisanSettings,
  isTopAreaVisible: Boolean = true,
  onTopBarVisibilityChange: (Boolean) -> Unit = {},
  onSearchChange: (String) -> Unit,
  onTradeSelect: (String) -> Unit,
  onEmergencyToggle: (Boolean) -> Unit,
  onRequestQuote: (artisan: Artisan, name: String, phone: String, title: String, desc: String, budget: String) -> Unit,
  onRegisterArtisan: (name: String, trade: String, city: String, hourlyRate: Int, phone: String, wa: String, bio: String) -> Unit,
  onUpdateSettings: (ArtisanSettings) -> Unit
) {
  var selectedArtisanForDetails by remember { mutableStateOf<Artisan?>(null) }
  var selectedArtisanForQuote by remember { mutableStateOf<Artisan?>(null) }
  var showRegisterDialog by remember { mutableStateOf(false) }
  var showSettingsDialog by remember { mutableStateOf(false) }

  val listState = rememberLazyListState()

  val nestedScrollConnection = remember {
    object : NestedScrollConnection {
      override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.y
        if (delta < -10f) {
          // User swiping up (scrolling down the list) -> hide top area to expand to full screen
          if (isTopAreaVisible) {
            onTopBarVisibilityChange(false)
          }
        } else if (delta > 10f) {
          // User swiping down (scrolling up toward top) -> bring top area back down
          if (!isTopAreaVisible) {
            onTopBarVisibilityChange(true)
          }
        }
        return Offset.Zero
      }
    }
  }

  LaunchedEffect(listState) {
    snapshotFlow { listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0 }
      .collect { isAtTop ->
        if (isAtTop && !isTopAreaVisible) {
          onTopBarVisibilityChange(true)
        }
      }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .nestedScroll(nestedScrollConnection)
      .padding(horizontal = 16.dp)
  ) {
    // Collapsible Top Area: Title, Register Button, Settings, Search, and Category Filters
    AnimatedVisibility(
      visible = isTopAreaVisible,
      enter = slideInVertically(initialOffsetY = { -it }) + expandVertically(),
      exit = slideOutVertically(targetOffsetY = { -it }) + shrinkVertically()
    ) {
      Column {
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
              text = AppStrings.get("artisans_title", lang),
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
            )
            Text(
              text = AppStrings.get("artisans_subtitle", lang),
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            // Register as Artisan Button
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFF0D9488),
              modifier = Modifier.clickable { showRegisterDialog = true }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "تسجيل كحرفي",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                )
              }
            }

            // Section Settings Button
            IconButton(
              onClick = { showSettingsDialog = true },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(
                Icons.Default.Tune,
                contentDescription = AppStrings.get("artisan_settings_title", lang),
                tint = Color(0xFF0D9488)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Box
        OutlinedTextField(
          value = searchQuery,
          onValueChange = onSearchChange,
          placeholder = { Text("بحث عن نجار، حداد، دهان، سباك بالاسم أو المدينة...") },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF0D9488))
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { onSearchChange("") }) {
                Icon(Icons.Default.Close, contentDescription = null)
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Trades Filter Chips & Emergency Filter
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Emergency toggle chip
          FilterChip(
            selected = emergencyOnly,
            onClick = { onEmergencyToggle(!emergencyOnly) },
            label = { Text("طوارئ فورية ⚡", style = MaterialTheme.typography.labelSmall) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = Color(0xFFEF4444),
              selectedLabelColor = Color.White
            )
          )

          Spacer(modifier = Modifier.width(8.dp))

          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
          ) {
            items(PREDEFINED_ARTISAN_TRADES) { trade ->
              val isSelected = selectedTrade == trade
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) Color(0xFF0D9488) else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.clickable { onTradeSelect(trade) }
              ) {
                Text(
                  text = trade,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                  ),
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "${artisans.size} حرفي معتمد متوفر حالياً",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // Full-Screen Mode Floating Pill (appears when top area collapses to inform the user)
    AnimatedVisibility(
      visible = !isTopAreaVisible,
      enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
      exit = fadeOut() + slideOutVertically(targetOffsetY = { -it })
    ) {
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0D9488),
        shadowElevation = 4.dp,
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp)
          .clickable { onTopBarVisibilityChange(true) }
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.Tune,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (selectedTrade == "الكل") "دليل الحرفيين (${artisans.size}) • شاشة كاملة" else "$selectedTrade (${artisans.size})",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
            )
          }

          Text(
            text = "المس أو مرر للأسفل للإظهار ⬇",
            style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.9f))
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    if (artisans.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(top = 40.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "لا يوجد حرفيون مطابقون لمعايير البحث في هذا التخصص.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    } else {
      LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(artisans, key = { it.id }) { artisan ->
          ArtisanCard(
            artisan = artisan,
            lang = lang,
            onClick = { selectedArtisanForDetails = artisan },
            onRequestQuote = { selectedArtisanForQuote = artisan }
          )
        }
        item {
          Spacer(modifier = Modifier.height(80.dp))
        }
      }
    }
  }

  // Artisan Details Dialog
  selectedArtisanForDetails?.let { artisan ->
    ArtisanDetailsDialog(
      artisan = artisan,
      lang = lang,
      onDismiss = { selectedArtisanForDetails = null },
      onRequestQuote = {
        selectedArtisanForDetails = null
        selectedArtisanForQuote = artisan
      }
    )
  }

  // Request Quote Dialog
  selectedArtisanForQuote?.let { artisan ->
    RequestQuoteDialog(
      artisan = artisan,
      lang = lang,
      onDismiss = { selectedArtisanForQuote = null },
      onSubmit = { name, phone, title, desc, budget ->
        onRequestQuote(artisan, name, phone, title, desc, budget)
        selectedArtisanForQuote = null
      }
    )
  }

  // Register Artisan Dialog
  if (showRegisterDialog) {
    RegisterArtisanDialog(
      lang = lang,
      onDismiss = { showRegisterDialog = false },
      onSubmit = { name, trade, city, hourlyRate, phone, wa, bio ->
        onRegisterArtisan(name, trade, city, hourlyRate, phone, wa, bio)
        showRegisterDialog = false
      }
    )
  }

  // Dedicated Settings Dialog
  if (showSettingsDialog) {
    ArtisanSettingsDialog(
      currentSettings = settings,
      lang = lang,
      onSave = onUpdateSettings,
      onDismiss = { showSettingsDialog = false }
    )
  }
}

@Composable
fun ArtisanCard(
  artisan: Artisan,
  lang: AppLanguage,
  onClick: () -> Unit,
  onRequestQuote: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF0D9488),
            modifier = Modifier.size(46.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(Icons.Default.Handyman, contentDescription = null, tint = Color.White)
            }
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = artisan.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
              if (artisan.isVerified) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                  Icons.Default.CheckCircle,
                  contentDescription = "معتمد",
                  tint = Color(0xFF10B981),
                  modifier = Modifier.size(16.dp)
                )
              }
            }
            Text(
              text = "${artisan.trade} • ${artisan.city}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        if (artisan.isEmergencyAvailable) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0x26EF4444)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(2.dp))
              Text(
                text = "طوارئ",
                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Rating & Experience
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "${artisan.rating} (${artisan.reviewsCount} تقييم)",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
          )
        }

        Text(
          text = "${artisan.experienceYears} سنوات خبرة",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
          text = "${artisan.hourlyRate} ريال / ساعة",
          style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF0D9488), fontWeight = FontWeight.Bold)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Actions: Request Quote
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = artisan.bio,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          modifier = Modifier.weight(1f)
        )

        Button(
          onClick = onRequestQuote,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488), contentColor = Color.White),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp)
        ) {
          Icon(Icons.Default.RequestQuote, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(AppStrings.get("request_quote_btn", lang), style = MaterialTheme.typography.labelSmall)
        }
      }
    }
  }
}

@Composable
fun ArtisanDetailsDialog(
  artisan: Artisan,
  lang: AppLanguage,
  onDismiss: () -> Unit,
  onRequestQuote: () -> Unit
) {
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
            text = artisan.name,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = null)
          }
        }

        Text(
          text = "${artisan.trade} • ${artisan.city}",
          style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF0D9488), fontWeight = FontWeight.SemiBold)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "نبذة عن الحرفي والخبرات:",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = artisan.bio,
          style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Rates
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text("سعر الساعة التقديري", style = MaterialTheme.typography.labelSmall)
              Text("${artisan.hourlyRate} ريال", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0D9488)))
            }
          }

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text("سعر اليومية الكاملة", style = MaterialTheme.typography.labelSmall)
              Text("${artisan.dailyRate} ريال", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0D9488)))
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Contact info
        Text(text = "رقم الهاتف: ${artisan.phone}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "واتساب: ${artisan.whatsapp}", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = onRequestQuote,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488), contentColor = Color.White),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.RequestQuote, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text(AppStrings.get("request_quote_btn", lang), fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun RequestQuoteDialog(
  artisan: Artisan,
  lang: AppLanguage,
  onDismiss: () -> Unit,
  onSubmit: (name: String, phone: String, title: String, desc: String, budget: String) -> Unit
) {
  var name by remember { mutableStateOf("محمد العتيبي") }
  var phone by remember { mutableStateOf("+966551234567") }
  var projectTitle by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var budget by remember { mutableStateOf("1500") }

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
            text = AppStrings.get("quote_dialog_title", lang),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = null)
          }
        }

        Text(
          text = "إلى الحرفي: ${artisan.name} (${artisan.trade})",
          style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF0D9488), fontWeight = FontWeight.SemiBold)
        )

        Spacer(modifier = Modifier.height(14.dp))

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
          label = { Text("رقم هاتف التواصل") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = projectTitle,
          onValueChange = { projectTitle = it },
          label = { Text(AppStrings.get("project_title_label", lang)) },
          placeholder = { Text("مثال: تصنيع وتركيب أبواب خشبية أو مظلة حديدية") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text(AppStrings.get("project_desc_label", lang)) },
          minLines = 3,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = budget,
          onValueChange = { budget = it },
          label = { Text(AppStrings.get("estimated_budget_label", lang)) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = {
            val title = if (projectTitle.isBlank()) "طلب تسعير مقايسة عمل" else projectTitle
            onSubmit(name, phone, title, description, budget)
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488), contentColor = Color.White),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.RequestQuote, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text(AppStrings.get("send_quote_request", lang), fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun RegisterArtisanDialog(
  lang: AppLanguage,
  onDismiss: () -> Unit,
  onSubmit: (name: String, trade: String, city: String, hourlyRate: Int, phone: String, wa: String, bio: String) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var trade by remember { mutableStateOf("نجارة وديكور خشب") }
  var city by remember { mutableStateOf("الرياض") }
  var hourlyRate by remember { mutableStateOf("90") }
  var phone by remember { mutableStateOf("+966509876543") }
  var wa by remember { mutableStateOf("+966509876543") }
  var bio by remember { mutableStateOf("حرفي متمكن بخبرة طويلة في التنفيذ والتشطيب.") }

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
            text = AppStrings.get("register_as_artisan_btn", lang),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = null)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("الاسم الكامل أو اسم الورشة") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = trade,
          onValueChange = { trade = it },
          label = { Text("المهنة أو التخصص") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = city,
          onValueChange = { city = it },
          label = { Text("المدينة") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = hourlyRate,
          onValueChange = { hourlyRate = it },
          label = { Text("سعر الساعة التقريبي (ريال)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("رقم الهاتف المباشر") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = bio,
          onValueChange = { bio = it },
          label = { Text("نبذة عن خبراتك وأعمالك السابقة") },
          minLines = 2,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = {
            val n = if (name.isBlank()) "حرفي مستقل" else name
            val rate = hourlyRate.toIntOrNull() ?: 80
            onSubmit(n, trade, city, rate, phone, wa, bio)
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488), contentColor = Color.White),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.CheckCircle, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text("تأكيد التسجيل في الدليل", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
