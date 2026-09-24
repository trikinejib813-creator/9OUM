package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Country
import com.example.data.CountryRepository
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.components.AuraIconLogo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginWindow(
  currentLang: AppLanguage,
  onLanguageChange: (AppLanguage) -> Unit,
  onSaveAndProceed: (email: String, country: Country) -> Unit
) {
  var email by remember { mutableStateOf("") }
  var emailError by remember { mutableStateOf<String?>(null) }
  var selectedCountry by remember { mutableStateOf(CountryRepository.defaultCountry) }
  var showCountryPicker by remember { mutableStateOf(false) }
  var isCardVisible by remember { mutableStateOf(false) }
  var showLanguageDialog by remember { mutableStateOf(false) }

  val focusManager = LocalFocusManager.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val scope = rememberCoroutineScope()

  LaunchedEffect(Unit) {
    isCardVisible = true
  }

  fun validateAndSubmit() {
    val trimmed = email.trim()
    val emailPattern = android.util.Patterns.EMAIL_ADDRESS
    if (trimmed.isEmpty() || !emailPattern.matcher(trimmed).matches()) {
      emailError = AppStrings.get("email_error_invalid", currentLang)
      return
    }
    emailError = null
    onSaveAndProceed(trimmed, selectedCountry)
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
          )
        )
      )
      .statusBarsPadding()
      .navigationBarsPadding()
      .imePadding()
  ) {
    // Top Bar with Language Selector
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        AuraIconLogo(size = 32.dp, assemblyProgress = 1f)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "AuraSync",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        )
      }

      // Language Switcher Chip
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = Modifier.clickable { showLanguageDialog = true }
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Language,
            contentDescription = "Language",
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = currentLang.displayName,
            style = MaterialTheme.typography.labelMedium.copy(
              color = MaterialTheme.colorScheme.onSurface
            )
          )
        }
      }
    }

    // Centered Fluid Login Card with Entrance Animation
    AnimatedVisibility(
      visible = isCardVisible,
      enter = slideInVertically(
        initialOffsetY = { 200 },
        animationSpec = spring(
          dampingRatio = Spring.DampingRatioLowBouncy,
          stiffness = Spring.StiffnessMediumLow
        )
      ) + fadeIn(),
      modifier = Modifier
        .align(Alignment.Center)
        .padding(horizontal = 20.dp)
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(28.dp)),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Card Header
          Box(
            modifier = Modifier
              .size(64.dp)
              .background(
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Shield,
              contentDescription = "Security",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(32.dp)
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = AppStrings.get("welcome_title", currentLang),
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 22.sp
            ),
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = AppStrings.get("welcome_subtitle", currentLang),
            style = MaterialTheme.typography.bodyMedium.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(24.dp))

          // 1. Email Input Only
          Column(modifier = Modifier.fillMaxWidth()) {
            Text(
              text = AppStrings.get("email_label", currentLang),
              style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium
              )
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
              value = email,
              onValueChange = {
                email = it
                if (emailError != null) emailError = null
              },
              modifier = Modifier.fillMaxWidth(),
              placeholder = { Text(AppStrings.get("email_placeholder", currentLang)) },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.Email,
                  contentDescription = "Email",
                  tint = MaterialTheme.colorScheme.primary
                )
              },
              isError = emailError != null,
              supportingText = emailError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
              singleLine = true,
              keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
              ),
              keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                validateAndSubmit()
              }),
              shape = RoundedCornerShape(16.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
              )
            )

            // Domain suggestions quick chips
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              val domains = listOf("@gmail.com", "@outlook.com", "@icloud.com")
              domains.forEach { domain ->
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                  modifier = Modifier.clickable {
                    val prefix = if (email.contains("@")) email.substringBefore("@") else email
                    if (prefix.isNotEmpty()) {
                      email = "$prefix$domain"
                      emailError = null
                    }
                  }
                ) {
                  Text(
                    text = domain,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = MaterialTheme.colorScheme.primary
                    )
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // 2. Country Selection Chain (سلسلة اختيار البلد)
          Column(modifier = Modifier.fillMaxWidth()) {
            Text(
              text = AppStrings.get("country_select_label", currentLang),
              style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium
              )
            )
            Spacer(modifier = Modifier.height(6.dp))

            Surface(
              shape = RoundedCornerShape(16.dp),
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
              ),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
              modifier = Modifier
                .fillMaxWidth()
                .clickable { showCountryPicker = true }
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = selectedCountry.flagEmoji,
                    fontSize = 24.sp
                  )
                  Spacer(modifier = Modifier.width(12.dp))
                  Column {
                    Text(
                      text = selectedCountry.localizedName(currentLang),
                      style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                      ),
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                    Text(
                      text = "${selectedCountry.dialCode} • ${selectedCountry.region}",
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    )
                  }
                }

                Icon(
                  imageVector = Icons.Default.KeyboardArrowDown,
                  contentDescription = "Expand",
                  tint = MaterialTheme.colorScheme.primary
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // 3. Security Badge
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Encrypted",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = AppStrings.get("data_secured_badge", currentLang),
                style = MaterialTheme.typography.labelSmall.copy(
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                  fontSize = 11.sp
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(24.dp))

          // 4. Save and Continue Button
          Button(
            onClick = {
              focusManager.clearFocus()
              validateAndSubmit()
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary
            )
          ) {
            Text(
              text = AppStrings.get("save_and_continue", currentLang),
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
              )
            )
          }
        }
      }
    }
  }

  // Country Selection Modal Sheet
  if (showCountryPicker) {
    ModalBottomSheet(
      onDismissRequest = { showCountryPicker = false },
      sheetState = sheetState,
      shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
      containerColor = MaterialTheme.colorScheme.surface
    ) {
      CountryPickerContent(
        currentLang = currentLang,
        selectedCountry = selectedCountry,
        onSelectCountry = { country ->
          selectedCountry = country
          scope.launch { sheetState.hide() }.invokeOnCompletion {
            showCountryPicker = false
          }
        }
      )
    }
  }

  // Language Picker Dialog
  if (showLanguageDialog) {
    androidx.compose.material3.AlertDialog(
      onDismissRequest = { showLanguageDialog = false },
      title = { Text(AppStrings.get("language_label", currentLang)) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          AppLanguage.entries.forEach { lang ->
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (lang == currentLang) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  onLanguageChange(lang)
                  showLanguageDialog = false
                }
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = lang.displayName,
                  style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (lang == currentLang) FontWeight.Bold else FontWeight.Normal
                  )
                )
                if (lang == currentLang) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                  )
                }
              }
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showLanguageDialog = false }) {
          Text(AppStrings.get("cancel", currentLang))
        }
      }
    )
  }
}

@Composable
fun CountryPickerContent(
  currentLang: AppLanguage,
  selectedCountry: Country,
  onSelectCountry: (Country) -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedRegion by remember { mutableStateOf<String?>(null) }

  val allRegions = remember {
    listOf("الكل") + CountryRepository.countries.map { it.region }.distinct()
  }

  val filteredCountries = remember(searchQuery, selectedRegion) {
    CountryRepository.countries.filter { country ->
      val matchesSearch = searchQuery.isBlank() ||
          country.nameAr.contains(searchQuery, ignoreCase = true) ||
          country.nameEn.contains(searchQuery, ignoreCase = true) ||
          country.dialCode.contains(searchQuery)

      val matchesRegion = selectedRegion == null ||
          selectedRegion == "الكل" ||
          country.region.equals(selectedRegion, ignoreCase = true)

      matchesSearch && matchesRegion
    }
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .heightIn(max = 550.dp)
      .padding(horizontal = 20.dp, vertical = 8.dp)
  ) {
    Text(
      text = AppStrings.get("country_select_label", currentLang),
      style = MaterialTheme.typography.titleLarge.copy(
        fontWeight = FontWeight.Bold
      )
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Search bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      modifier = Modifier.fillMaxWidth(),
      placeholder = { Text(AppStrings.get("search_country", currentLang)) },
      leadingIcon = {
        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
      },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { searchQuery = "" }) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
          }
        }
      },
      singleLine = true,
      shape = RoundedCornerShape(16.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
      )
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Region Filter Chips
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
      items(allRegions) { region ->
        val isSelected = (selectedRegion == null && region == "الكل") || selectedRegion == region
        FilterChip(
          selected = isSelected,
          onClick = { selectedRegion = if (region == "الكل") null else region },
          label = { Text(region) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
          )
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Country List
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      items(filteredCountries, key = { it.code }) { country ->
        val isSelected = country.code == selectedCountry.code
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
          else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
          border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectCountry(country) }
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Text(
                text = country.flagEmoji,
                fontSize = 24.sp
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = country.localizedName(currentLang),
                  style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                  ),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  text = "${country.dialCode} • ${country.region}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                )
              }
            }

            if (isSelected) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary
              )
            }
          }
        }
      }
    }
  }
}
