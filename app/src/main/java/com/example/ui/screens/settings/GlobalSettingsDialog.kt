package com.example.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.Country
import com.example.data.db.UserProfileEntity
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.windows.AppThemeMode

@Composable
fun GlobalSettingsDialog(
  currentLang: AppLanguage,
  themeMode: AppThemeMode,
  eyeComfortEnabled: Boolean,
  selectedCountry: Country,
  userProfile: UserProfileEntity?,
  onThemeModeChange: (AppThemeMode) -> Unit,
  onEyeComfortChange: (Boolean) -> Unit,
  onLanguageChange: (AppLanguage) -> Unit,
  onCountryChangeRequest: () -> Unit,
  onLogout: () -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 8.dp,
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
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFFF59E0B))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = AppStrings.get("settings_title", currentLang),
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = null)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Card
        userProfile?.let { prof ->
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF59E0B),
                modifier = Modifier.size(42.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF0F172A))
                }
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = prof.encryptedEmail,
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  text = "${prof.countryName} ${selectedCountry.flagEmoji}",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
        }

        // Language Switcher
        Text(
          text = AppStrings.get("language_label", currentLang),
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          AppLanguage.entries.forEach { lang ->
            val isSelected = currentLang == lang
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) Color(0xFFF59E0B) else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .weight(1f)
                .clickable { onLanguageChange(lang) }
            ) {
              Text(
                text = lang.displayName,
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = if (isSelected) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(vertical = 10.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display Theme
        Text(
          text = AppStrings.get("theme_mode", currentLang),
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          ThemeChoiceCard(
            label = "فاتح",
            icon = Icons.Default.LightMode,
            isSelected = themeMode == AppThemeMode.LIGHT,
            modifier = Modifier.weight(1f),
            onClick = { onThemeModeChange(AppThemeMode.LIGHT) }
          )
          ThemeChoiceCard(
            label = "داكن",
            icon = Icons.Default.DarkMode,
            isSelected = themeMode == AppThemeMode.DARK,
            modifier = Modifier.weight(1f),
            onClick = { onThemeModeChange(AppThemeMode.DARK) }
          )
          ThemeChoiceCard(
            label = "تلقائي",
            icon = Icons.Default.Settings,
            isSelected = themeMode == AppThemeMode.SYSTEM,
            modifier = Modifier.weight(1f),
            onClick = { onThemeModeChange(AppThemeMode.SYSTEM) }
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Eye comfort warm filter
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = AppStrings.get("eye_comfort_mode", currentLang),
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
              text = "طبقة كهرمانية دافئة لتقليل إجهاد العين أثناء الليل",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          Switch(
            checked = eyeComfortEnabled,
            onCheckedChange = onEyeComfortChange,
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color(0xFF0F172A),
              checkedTrackColor = Color(0xFFF59E0B)
            )
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Country switch button
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onCountryChangeRequest() }
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(selectedCountry.flagEmoji, fontSize = 20.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "${selectedCountry.localizedName(currentLang)} (${selectedCountry.dialCode})",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
              )
            }
            Text(
              text = "تغيير الدولة",
              style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Logout
        Button(
          onClick = {
            onLogout()
            onDismiss()
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.ExitToApp, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text("تسجيل الخروج وتبديل الحساب")
        }
      }
    }
  }
}

@Composable
fun ThemeChoiceCard(
  label: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  isSelected: Boolean,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = if (isSelected) Color(0xFFF59E0B) else MaterialTheme.colorScheme.surfaceVariant,
    modifier = modifier.clickable { onClick() }
  ) {
    Column(
      modifier = Modifier.padding(vertical = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isSelected) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
          color = if (isSelected) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurfaceVariant
        )
      )
    }
  }
}
