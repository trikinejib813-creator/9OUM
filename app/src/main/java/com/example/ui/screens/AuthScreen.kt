package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.Country
import com.example.data.CountryRepository
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.components.GoumLogo

@Composable
fun AuthScreen(
  currentLang: AppLanguage,
  onLanguageChange: (AppLanguage) -> Unit,
  onSaveAndProceed: (email: String, country: Country) -> Unit
) {
  var email by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var selectedCountry by remember { mutableStateOf(CountryRepository.defaultCountry) }
  var showCountryPicker by remember { mutableStateOf(false) }
  var selectedRole by remember { mutableStateOf("JOB_SEEKER") }

  val focusManager = LocalFocusManager.current

  val isValidEmail = remember(email) {
    android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .statusBarsPadding()
      .navigationBarsPadding()
      .imePadding()
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Language Selector Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Language,
            contentDescription = null,
            tint = Color(0xFFF59E0B),
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = AppStrings.get("language_label", currentLang),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          AppLanguage.entries.forEach { lang ->
            val isSelected = currentLang == lang
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (isSelected) Color(0xFFF59E0B) else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier.clickable { onLanguageChange(lang) }
            ) {
              Text(
                text = lang.displayName,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = if (isSelected) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Logo & Brand
      GoumLogo(size = 90.dp)

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "9GOUM",
        style = MaterialTheme.typography.headlineMedium.copy(
          fontWeight = FontWeight.Black,
          fontSize = 28.sp,
          color = MaterialTheme.colorScheme.onBackground
        )
      )

      Text(
        text = AppStrings.get("app_tagline", currentLang),
        style = MaterialTheme.typography.bodyMedium.copy(
          color = Color(0xFFF59E0B),
          fontWeight = FontWeight.Medium
        ),
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Role Selection Cards
      Text(
        text = AppStrings.get("user_role_label", currentLang),
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        RoleCard(
          title = AppStrings.get("role_job_seeker", currentLang),
          icon = Icons.Default.Work,
          isSelected = selectedRole == "JOB_SEEKER",
          modifier = Modifier.weight(1f),
          onClick = { selectedRole = "JOB_SEEKER" }
        )
        RoleCard(
          title = AppStrings.get("role_employer", currentLang),
          icon = Icons.Default.Business,
          isSelected = selectedRole == "EMPLOYER",
          modifier = Modifier.weight(1f),
          onClick = { selectedRole = "EMPLOYER" }
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        RoleCard(
          title = AppStrings.get("role_artisan", currentLang),
          icon = Icons.Default.Construction,
          isSelected = selectedRole == "ARTISAN",
          modifier = Modifier.weight(1f),
          onClick = { selectedRole = "ARTISAN" }
        )
        RoleCard(
          title = AppStrings.get("role_client", currentLang),
          icon = Icons.Default.HomeRepairService,
          isSelected = selectedRole == "CLIENT",
          modifier = Modifier.weight(1f),
          onClick = { selectedRole = "CLIENT" }
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Email Input
      OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text(AppStrings.get("email_label", currentLang)) },
        placeholder = { Text(AppStrings.get("email_placeholder", currentLang)) },
        leadingIcon = {
          Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFFF59E0B))
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Email,
          imeAction = ImeAction.Next
        ),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = Color(0xFFF59E0B),
          unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Country Selector Box
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { showCountryPicker = true }
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = selectedCountry.flagEmoji,
              fontSize = 24.sp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = AppStrings.get("country_select_label", currentLang),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "${selectedCountry.localizedName(currentLang)} (${selectedCountry.dialCode})",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }

          Icon(
            imageVector = Icons.Default.Public,
            contentDescription = null,
            tint = Color(0xFFF59E0B)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Phone Input
      OutlinedTextField(
        value = phone,
        onValueChange = { phone = it },
        label = { Text(AppStrings.get("phone_label", currentLang)) },
        placeholder = { Text("50XXXXXXX") },
        leadingIcon = {
          Row(
            modifier = Modifier.padding(start = 12.dp, end = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = selectedCountry.dialCode, fontWeight = FontWeight.Bold)
          }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Phone,
          imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = Color(0xFFF59E0B),
          unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(28.dp))

      // Submit Button
      Button(
        onClick = {
          val cleanEmail = if (isValidEmail) email.trim() else "guest@9goum.com"
          onSaveAndProceed(cleanEmail, selectedCountry)
        },
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFFF59E0B),
          contentColor = Color(0xFF0F172A)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.CheckCircle, contentDescription = null)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = AppStrings.get("save_and_continue", currentLang),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      androidx.compose.material3.OutlinedButton(
        onClick = {
          onSaveAndProceed("demo@9goum.sa", selectedCountry)
        },
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(
          contentColor = Color(0xFFF59E0B)
        ),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF59E0B)),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
      ) {
        Text(
          text = if (currentLang == AppLanguage.ARABIC) "⚡ تجربة واستعراض المنصة مباشرة (وضع الزائر)" else "⚡ Instant Platform Preview (Guest Demo)",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }

  // Country Picker Dialog
  if (showCountryPicker) {
    CountryPickerDialog(
      currentLang = currentLang,
      selectedCountry = selectedCountry,
      onSelectCountry = {
        selectedCountry = it
        showCountryPicker = false
      },
      onDismiss = { showCountryPicker = false }
    )
  }
}

@Composable
fun RoleCard(
  title: String,
  icon: ImageVector,
  isSelected: Boolean,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = if (isSelected) Color(0x26F59E0B) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    border = androidx.compose.foundation.BorderStroke(
      width = if (isSelected) 2.dp else 1.dp,
      color = if (isSelected) Color(0xFFF59E0B) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    ),
    modifier = modifier.clickable { onClick() }
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isSelected) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
          color = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant
        ),
        maxLines = 1
      )
    }
  }
}

@Composable
fun CountryPickerDialog(
  currentLang: AppLanguage,
  selectedCountry: Country,
  onSelectCountry: (Country) -> Unit,
  onDismiss: () -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }

  val filteredList = remember(searchQuery, currentLang) {
    if (searchQuery.isBlank()) CountryRepository.countries
    else {
      CountryRepository.countries.filter {
        it.nameAr.contains(searchQuery, ignoreCase = true) ||
          it.nameEn.contains(searchQuery, ignoreCase = true) ||
          it.nameFr.contains(searchQuery, ignoreCase = true) ||
          it.dialCode.contains(searchQuery)
      }
    }
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 8.dp,
      modifier = Modifier
        .fillMaxWidth()
        .height(480.dp)
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = AppStrings.get("country_select_label", currentLang),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = null)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text(AppStrings.get("search_placeholder", currentLang)) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          items(filteredList) { country ->
            val isSelected = country.code == selectedCountry.code
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) Color(0x26F59E0B) else Color.Transparent,
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectCountry(country) }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(text = country.flagEmoji, fontSize = 22.sp)
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = country.localizedName(currentLang),
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                  )
                }

                Text(
                  text = country.dialCode,
                  style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFFF59E0B))
                )
              }
            }
          }
        }
      }
    }
  }
}
