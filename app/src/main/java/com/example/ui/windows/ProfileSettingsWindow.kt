package com.example.ui.windows

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Country
import com.example.data.CountryRepository
import com.example.data.model.UserProfile
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.screens.CountryPickerContent
import kotlinx.coroutines.launch

enum class AppThemeMode {
  DARK, LIGHT, SYSTEM
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsWindowContent(
  lang: AppLanguage,
  userProfile: UserProfile?,
  currentThemeMode: AppThemeMode,
  eyeComfortEnabled: Boolean,
  onThemeModeChange: (AppThemeMode) -> Unit,
  onEyeComfortChange: (Boolean) -> Unit,
  onLanguageChange: (AppLanguage) -> Unit,
  onCountryUpdated: (Country) -> Unit,
  onLogout: () -> Unit
) {
  var showCountryPicker by remember { mutableStateOf(false) }
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val scope = rememberCoroutineScope()

  val currentCountry = remember(userProfile?.countryCode) {
    CountryRepository.findByCode(userProfile?.countryCode ?: "SA") ?: CountryRepository.defaultCountry
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Account Info Card
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
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
            text = AppStrings.get("profile_info", lang),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "AES-GCM",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = MaterialTheme.colorScheme.primary,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                )
              )
            }
          }
        }

        // Email row
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Email,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = AppStrings.get("email_label", lang),
              style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Text(
              text = userProfile?.email ?: "name@example.com",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
          }
        }

        // Country row with Change button
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = currentCountry.flagEmoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = AppStrings.get("user_country", lang),
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
              )
              Text(
                text = "${currentCountry.localizedName(lang)} (${currentCountry.dialCode})",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
              )
            }
          }

          TextButton(onClick = { showCountryPicker = true }) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(AppStrings.get("edit", lang))
          }
        }
      }
    }

    // 2. Display Theme (Dark / Light / System)
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
      Column(
        modifier = Modifier.padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = AppStrings.get("theme_mode", lang),
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val modes = listOf(
            Triple(AppThemeMode.DARK, AppStrings.get("theme_dark", lang), Icons.Default.DarkMode),
            Triple(AppThemeMode.LIGHT, AppStrings.get("theme_light", lang), Icons.Default.LightMode),
            Triple(AppThemeMode.SYSTEM, AppStrings.get("theme_system", lang), Icons.Default.Brightness4)
          )

          modes.forEach { (mode, title, icon) ->
            val isSelected = currentThemeMode == mode
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
              border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
              modifier = Modifier
                .weight(1f)
                .clickable { onThemeModeChange(mode) }
            ) {
              Column(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Icon(
                  imageVector = icon,
                  contentDescription = null,
                  tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = when (mode) {
                    AppThemeMode.DARK -> if (lang == AppLanguage.ARABIC) "مظلم" else "Dark"
                    AppThemeMode.LIGHT -> if (lang == AppLanguage.ARABIC) "فاتح" else "Light"
                    AppThemeMode.SYSTEM -> if (lang == AppLanguage.ARABIC) "تلقائي" else "Auto"
                  },
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                  )
                )
              }
            }
          }
        }

        // Eye comfort warm tint toggle
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = AppStrings.get("eye_comfort_mode", lang),
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
              text = if (lang == AppLanguage.ARABIC) "يقلل من الضوء الأزرق لراحة العين أثناء الاستخدام الليلي" else "Filters blue light with a warm tint for nighttime reading",
              style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
          }

          Switch(
            checked = eyeComfortEnabled,
            onCheckedChange = onEyeComfortChange,
            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
          )
        }
      }
    }

    // 3. Language Selector
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
      Column(
        modifier = Modifier.padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = AppStrings.get("language_label", lang),
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        AppLanguage.entries.forEach { l ->
          val isSelected = l == lang
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onLanguageChange(l) }
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = l.displayName,
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              )
              if (isSelected) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary
                )
              }
            }
          }
        }
      }
    }

    // 4. Logout / Switch User Button
    Button(
      onClick = onLogout,
      colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
        contentColor = MaterialTheme.colorScheme.error
      ),
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
    ) {
      Icon(imageVector = Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = AppStrings.get("logout", lang),
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
      )
    }
  }

  // Country Picker Modal Sheet
  if (showCountryPicker) {
    ModalBottomSheet(
      onDismissRequest = { showCountryPicker = false },
      sheetState = sheetState,
      shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
      containerColor = MaterialTheme.colorScheme.surface
    ) {
      CountryPickerContent(
        currentLang = lang,
        selectedCountry = currentCountry,
        onSelectCountry = { country ->
          onCountryUpdated(country)
          scope.launch { sheetState.hide() }.invokeOnCompletion {
            showCountryPicker = false
          }
        }
      )
    }
  }
}
