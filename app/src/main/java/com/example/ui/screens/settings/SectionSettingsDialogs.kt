package com.example.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ArtisanSettings
import com.example.data.model.EmployerSettings
import com.example.data.model.HomeServiceSettings
import com.example.data.model.JobSeekerSettings
import com.example.localization.AppLanguage
import com.example.localization.AppStrings

// ==========================================
// 1. DEDICATED SETTINGS FOR JOB SEEKERS
// ==========================================
@Composable
fun JobSeekerSettingsDialog(
  currentSettings: JobSeekerSettings,
  lang: AppLanguage,
  onSave: (JobSeekerSettings) -> Unit,
  onDismiss: () -> Unit
) {
  var jobAlerts by remember { mutableStateOf(currentSettings.jobAlertsEnabled) }
  var minSalary by remember { mutableFloatStateOf(currentSettings.minTargetSalary.toFloat()) }
  var remoteOnly by remember { mutableStateOf(currentSettings.remoteJobsOnly) }
  var hideProfile by remember { mutableStateOf(currentSettings.hideProfileFromEmployers) }
  var resumeSkills by remember { mutableStateOf(currentSettings.resumeSkills) }

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
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Work, contentDescription = null, tint = Color(0xFFF59E0B))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = AppStrings.get("job_settings_title", lang),
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = null)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Job alerts switch
        SettingsToggleRow(
          title = AppStrings.get("job_alerts_toggle", lang),
          subtitle = "إشعار عند توفر وظيفة تناسب تخصصك وسيرتك",
          checked = jobAlerts,
          onCheckedChange = { jobAlerts = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Remote only switch
        SettingsToggleRow(
          title = AppStrings.get("remote_only_toggle", lang),
          subtitle = "إخفاء الوظائف التي تتطلب الحضور المكتبي",
          checked = remoteOnly,
          onCheckedChange = { remoteOnly = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Hide profile switch
        SettingsToggleRow(
          title = AppStrings.get("hide_profile_toggle", lang),
          subtitle = "لا يمكن لأصحاب العمل رؤية اسمك إلا عند تقديمك المباشر",
          checked = hideProfile,
          onCheckedChange = { hideProfile = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Minimum salary slider
        Text(
          text = "${AppStrings.get("min_salary_label", lang)} ${minSalary.toInt()} SAR",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
        Slider(
          value = minSalary,
          onValueChange = { minSalary = it },
          valueRange = 2000f..20000f,
          steps = 17,
          colors = SliderDefaults.colors(
            thumbColor = Color(0xFFF59E0B),
            activeTrackColor = Color(0xFFF59E0B)
          )
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = resumeSkills,
          onValueChange = { resumeSkills = it },
          label = { Text("المهارات الأساسية في ملفك") },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = {
            onSave(
              currentSettings.copy(
                jobAlertsEnabled = jobAlerts,
                minTargetSalary = minSalary.toInt(),
                remoteJobsOnly = remoteOnly,
                hideProfileFromEmployers = hideProfile,
                resumeSkills = resumeSkills
              )
            )
            onDismiss()
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B), contentColor = Color(0xFF0F172A)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.Save, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text(AppStrings.get("save", lang), fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

// ==========================================
// 2. DEDICATED SETTINGS FOR EMPLOYERS / OFFERS
// ==========================================
@Composable
fun EmployerSettingsDialog(
  currentSettings: EmployerSettings,
  lang: AppLanguage,
  onSave: (EmployerSettings) -> Unit,
  onDismiss: () -> Unit
) {
  var companyName by remember { mutableStateOf(currentSettings.companyName) }
  var recruiterEmail by remember { mutableStateOf(currentSettings.recruiterEmail) }
  var recruiterPhone by remember { mutableStateOf(currentSettings.recruiterPhone) }
  var instantNotify by remember { mutableStateOf(currentSettings.applicantNotifyInstant) }
  var minExp by remember { mutableFloatStateOf(currentSettings.autoFilterMinExperience.toFloat()) }

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
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Business, contentDescription = null, tint = Color(0xFF2563EB))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = AppStrings.get("employer_settings_title", lang),
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = null)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
          value = companyName,
          onValueChange = { companyName = it },
          label = { Text(AppStrings.get("company_name_setting", lang)) },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = recruiterEmail,
          onValueChange = { recruiterEmail = it },
          label = { Text(AppStrings.get("recruiter_email_setting", lang)) },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = recruiterPhone,
          onValueChange = { recruiterPhone = it },
          label = { Text(AppStrings.get("recruiter_phone_setting", lang)) },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        SettingsToggleRow(
          title = AppStrings.get("instant_applicant_notify", lang),
          subtitle = "إشعار فوري عند تقديم أي مرشح جديد على إعلاناتك",
          checked = instantNotify,
          onCheckedChange = { instantNotify = it }
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "${AppStrings.get("auto_filter_exp_setting", lang)} ${minExp.toInt()} سنوات",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
        Slider(
          value = minExp,
          onValueChange = { minExp = it },
          valueRange = 0f..10f,
          steps = 9,
          colors = SliderDefaults.colors(
            thumbColor = Color(0xFF2563EB),
            activeTrackColor = Color(0xFF2563EB)
          )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = {
            onSave(
              currentSettings.copy(
                companyName = companyName,
                recruiterEmail = recruiterEmail,
                recruiterPhone = recruiterPhone,
                applicantNotifyInstant = instantNotify,
                autoFilterMinExperience = minExp.toInt()
              )
            )
            onDismiss()
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB), contentColor = Color.White),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.Save, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text(AppStrings.get("save", lang), fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

// ==========================================
// 3. DEDICATED SETTINGS FOR ARTISANS
// ==========================================
@Composable
fun ArtisanSettingsDialog(
  currentSettings: ArtisanSettings,
  lang: AppLanguage,
  onSave: (ArtisanSettings) -> Unit,
  onDismiss: () -> Unit
) {
  var emergencyAvail by remember { mutableStateOf(currentSettings.isAvailableForEmergency) }
  var publicPhone by remember { mutableStateOf(currentSettings.publicPhoneVisible) }
  var radius by remember { mutableFloatStateOf(currentSettings.coverageRadiusKm.toFloat()) }
  var hourlyRate by remember { mutableFloatStateOf(currentSettings.standardHourlyRate.toFloat()) }

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
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Construction, contentDescription = null, tint = Color(0xFF0D9488))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = AppStrings.get("artisan_settings_title", lang),
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = null)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsToggleRow(
          title = AppStrings.get("artisan_avail_toggle", lang),
          subtitle = "إظهار شارة (متاح للطوارئ) بجانب ملفك في الدليل",
          checked = emergencyAvail,
          onCheckedChange = { emergencyAvail = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        SettingsToggleRow(
          title = AppStrings.get("public_phone_toggle", lang),
          subtitle = "السماح بالاتصال الهاتفي والمحادثة عبر واتساب مباشرة",
          checked = publicPhone,
          onCheckedChange = { publicPhone = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "${AppStrings.get("coverage_radius_label", lang)} ${radius.toInt()} كم",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
        Slider(
          value = radius,
          onValueChange = { radius = it },
          valueRange = 5f..80f,
          steps = 14,
          colors = SliderDefaults.colors(
            thumbColor = Color(0xFF0D9488),
            activeTrackColor = Color(0xFF0D9488)
          )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "${AppStrings.get("standard_rate_label", lang)} ${hourlyRate.toInt()} ريال",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
        Slider(
          value = hourlyRate,
          onValueChange = { hourlyRate = it },
          valueRange = 30f..300f,
          steps = 26,
          colors = SliderDefaults.colors(
            thumbColor = Color(0xFF0D9488),
            activeTrackColor = Color(0xFF0D9488)
          )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = {
            onSave(
              currentSettings.copy(
                isAvailableForEmergency = emergencyAvail,
                publicPhoneVisible = publicPhone,
                coverageRadiusKm = radius.toInt(),
                standardHourlyRate = hourlyRate.toInt()
              )
            )
            onDismiss()
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488), contentColor = Color.White),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.Save, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text(AppStrings.get("save", lang), fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

// ==========================================
// 4. DEDICATED SETTINGS FOR HOME SERVICES
// ==========================================
@Composable
fun HomeServiceSettingsDialog(
  currentSettings: HomeServiceSettings,
  lang: AppLanguage,
  onSave: (HomeServiceSettings) -> Unit,
  onDismiss: () -> Unit
) {
  var address by remember { mutableStateOf(currentSettings.defaultHomeAddress) }
  var emergencyHorns by remember { mutableStateOf(currentSettings.emergencyHornsEnabled) }
  var warrantyParts by remember { mutableStateOf(currentSettings.requestOriginalPartsWithWarranty) }
  var paymentMethod by remember { mutableStateOf(currentSettings.preferredPaymentMethod) }

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
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.HomeRepairService, contentDescription = null, tint = Color(0xFFDC2626))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = AppStrings.get("home_settings_title", lang),
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = null)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
          value = address,
          onValueChange = { address = it },
          label = { Text(AppStrings.get("default_address_setting", lang)) },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        SettingsToggleRow(
          title = AppStrings.get("emergency_sound_setting", lang),
          subtitle = "تشغيل صوت تنبيه مميز عند وصول الفني لمنزلك",
          checked = emergencyHorns,
          onCheckedChange = { emergencyHorns = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        SettingsToggleRow(
          title = AppStrings.get("warranty_parts_setting", lang),
          subtitle = "إلزام الفنيين بتقديم فاتورة وضمان معتمد للأشغال",
          checked = warrantyParts,
          onCheckedChange = { warrantyParts = it }
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = paymentMethod,
          onValueChange = { paymentMethod = it },
          label = { Text(AppStrings.get("payment_pref_setting", lang)) },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = {
            onSave(
              currentSettings.copy(
                defaultHomeAddress = address,
                emergencyHornsEnabled = emergencyHorns,
                requestOriginalPartsWithWarranty = warrantyParts,
                preferredPaymentMethod = paymentMethod
              )
            )
            onDismiss()
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626), contentColor = Color.White),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.Save, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text(AppStrings.get("save", lang), fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun SettingsToggleRow(
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
    Spacer(modifier = Modifier.width(10.dp))
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = Color(0xFF0F172A),
        checkedTrackColor = Color(0xFFF59E0B)
      )
    )
  }
}
