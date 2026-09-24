package com.example.ui.screens.offers

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.db.JobApplicationEntity
import com.example.data.model.EmployerSettings
import com.example.data.model.JobContractType
import com.example.data.model.JobOffer
import com.example.data.model.PREDEFINED_JOB_CATEGORIES
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.screens.settings.EmployerSettingsDialog

@Composable
fun JobOffersScreen(
  lang: AppLanguage,
  myPostedJobs: List<JobOffer>,
  allJobs: List<JobOffer>,
  settings: EmployerSettings,
  myApplications: List<JobApplicationEntity>,
  onPostNewJob: (
    title: String,
    company: String,
    location: String,
    category: String,
    contractType: JobContractType,
    salaryMin: Int,
    salaryMax: Int,
    description: String,
    requirements: String,
    isRemote: Boolean,
    contactPhone: String
  ) -> Unit,
  onUpdateJobStatus: (jobId: String, status: String) -> Unit,
  onUpdateSettings: (EmployerSettings) -> Unit
) {
  var showPostJobDialog by remember { mutableStateOf(false) }
  var showSettingsDialog by remember { mutableStateOf(false) }
  var selectedJobForApplicants by remember { mutableStateOf<JobOffer?>(null) }
  var viewMode by remember { mutableStateOf(0) } // 0: My Posted Jobs, 1: All Market Offers

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
  ) {
    // Header Row with Title and Settings button
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp, bottom = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = AppStrings.get("offers_title", lang),
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
        )
        Text(
          text = AppStrings.get("offers_subtitle", lang),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        // Dedicated Section Settings Button
        IconButton(
          onClick = { showSettingsDialog = true },
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            Icons.Default.Tune,
            contentDescription = AppStrings.get("employer_settings_title", lang),
            tint = Color(0xFF2563EB)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Action banner: Post New Job Offer
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A8A)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "هل تبحث عن موظفين أو كفاءات؟",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
          )
          Text(
            text = "انشر عرض وظيفتك فوراً ليصل لآلاف الباحثين عن عمل",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF93C5FD)
          )
        }

        Button(
          onClick = { showPostJobDialog = true },
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B), contentColor = Color(0xFF0F172A))
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("نشر وظيفة", fontWeight = FontWeight.Bold)
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Segmented Switch: My Posted Jobs vs All Market Offers
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (viewMode == 0) Color(0xFF2563EB) else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
          .weight(1f)
          .clickable { viewMode = 0 }
      ) {
        Text(
          text = "${AppStrings.get("my_posted_jobs", lang)} (${myPostedJobs.size})",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = if (viewMode == 0) FontWeight.Bold else FontWeight.Normal,
            color = if (viewMode == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
          ),
          modifier = Modifier.padding(vertical = 10.dp),
          textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
      }

      Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (viewMode == 1) Color(0xFF2563EB) else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
          .weight(1f)
          .clickable { viewMode = 1 }
      ) {
        Text(
          text = "${AppStrings.get("all_market_offers", lang)} (${allJobs.size})",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = if (viewMode == 1) FontWeight.Bold else FontWeight.Normal,
            color = if (viewMode == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
          ),
          modifier = Modifier.padding(vertical = 10.dp),
          textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    val displayList = if (viewMode == 0) myPostedJobs else allJobs

    if (displayList.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(top = 40.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = if (viewMode == 0) "لم تقم بنشر أي عروض وظائف بعد. اضغط على 'نشر وظيفة' لتبدأ باستقبال المرشحين!" else "لا توجد عروض عمل في السوق حالياً.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
      }
    } else {
      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(displayList, key = { it.id }) { job ->
          EmployerJobCard(
            job = job,
            lang = lang,
            isMyJob = job.isOwner || viewMode == 0,
            onToggleStatus = { newStatus -> onUpdateJobStatus(job.id, newStatus) },
            onViewApplicants = { selectedJobForApplicants = job }
          )
        }
        item {
          Spacer(modifier = Modifier.height(80.dp))
        }
      }
    }
  }

  // Post Job Offer Dialog
  if (showPostJobDialog) {
    PostJobOfferDialog(
      lang = lang,
      defaultCompany = settings.companyName,
      defaultPhone = settings.recruiterPhone,
      onDismiss = { showPostJobDialog = false },
      onSubmit = { title, comp, loc, cat, contract, sMin, sMax, desc, req, isRem, phone ->
        onPostNewJob(title, comp, loc, cat, contract, sMin, sMax, desc, req, isRem, phone)
        showPostJobDialog = false
      }
    )
  }

  // View Applicants Dialog
  selectedJobForApplicants?.let { job ->
    JobApplicantsDialog(
      job = job,
      applications = myApplications.filter { it.jobId == job.id },
      lang = lang,
      onDismiss = { selectedJobForApplicants = null }
    )
  }

  // Dedicated Employer Settings Dialog
  if (showSettingsDialog) {
    EmployerSettingsDialog(
      currentSettings = settings,
      lang = lang,
      onSave = onUpdateSettings,
      onDismiss = { showSettingsDialog = false }
    )
  }
}

@Composable
fun EmployerJobCard(
  job: JobOffer,
  lang: AppLanguage,
  isMyJob: Boolean,
  onToggleStatus: (String) -> Unit,
  onViewApplicants: () -> Unit
) {
  val isActive = job.status == "ACTIVE"

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = job.title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = "${job.company} • ${job.location}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = if (isActive) Color(0x2610B981) else Color(0x26EF4444)
        ) {
          Text(
            text = if (isActive) "مفتوح للتقديم" else "معلق / مغلق",
            style = MaterialTheme.typography.labelSmall.copy(
              color = if (isActive) Color(0xFF10B981) else Color(0xFFEF4444),
              fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "المتقدمين لهذا الشاغر: ${job.applicantsCount} مترشح",
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = Color(0xFF2563EB))
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // View Applicants Button
        OutlinedButton(
          onClick = onViewApplicants,
          shape = RoundedCornerShape(8.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp)
        ) {
          Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(AppStrings.get("view_applicants", lang), style = MaterialTheme.typography.labelSmall)
        }

        if (isMyJob) {
          Button(
            onClick = { onToggleStatus(if (isActive) "PAUSED" else "ACTIVE") },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isActive) Color(0xFFEF4444) else Color(0xFF10B981),
              contentColor = Color.White
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp)
          ) {
            Icon(
              if (isActive) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (isActive) "إيقاف مؤقت" else "تفعيل العرض", style = MaterialTheme.typography.labelSmall)
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostJobOfferDialog(
  lang: AppLanguage,
  defaultCompany: String,
  defaultPhone: String,
  onDismiss: () -> Unit,
  onSubmit: (
    title: String,
    company: String,
    location: String,
    category: String,
    contractType: JobContractType,
    salaryMin: Int,
    salaryMax: Int,
    description: String,
    requirements: String,
    isRemote: Boolean,
    contactPhone: String
  ) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var company by remember { mutableStateOf(defaultCompany) }
  var location by remember { mutableStateOf("الرياض") }
  var category by remember { mutableStateOf("تكنولوجيا ومعلومات") }
  var contractType by remember { mutableStateOf(JobContractType.FULL_TIME) }
  var salaryMin by remember { mutableStateOf("5000") }
  var salaryMax by remember { mutableStateOf("9000") }
  var description by remember { mutableStateOf("") }
  var requirements by remember { mutableStateOf("") }
  var isRemote by remember { mutableStateOf(false) }
  var phone by remember { mutableStateOf(defaultPhone) }

  var categoryExpanded by remember { mutableStateOf(false) }

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
            text = AppStrings.get("post_new_job_btn", lang),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = null)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text(AppStrings.get("new_job_title_label", lang)) },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = company,
          onValueChange = { company = it },
          label = { Text(AppStrings.get("new_job_company_label", lang)) },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = location,
          onValueChange = { location = it },
          label = { Text(AppStrings.get("new_job_location_label", lang)) },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category dropdown
        ExposedDropdownMenuBox(
          expanded = categoryExpanded,
          onExpandedChange = { categoryExpanded = it }
        ) {
          OutlinedTextField(
            value = category,
            onValueChange = {},
            readOnly = true,
            label = { Text(AppStrings.get("new_job_category_label", lang)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .menuAnchor()
          )
          ExposedDropdownMenu(
            expanded = categoryExpanded,
            onDismissRequest = { categoryExpanded = false }
          ) {
            PREDEFINED_JOB_CATEGORIES.filter { it != "الكل" }.forEach { cat ->
              DropdownMenuItem(
                text = { Text(cat) },
                onClick = {
                  category = cat
                  categoryExpanded = false
                }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = salaryMin,
            onValueChange = { salaryMin = it },
            label = { Text(AppStrings.get("new_job_salary_min", lang)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = salaryMax,
            onValueChange = { salaryMax = it },
            label = { Text(AppStrings.get("new_job_salary_max", lang)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Remote work toggle
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("وظيفة عن بُعد (Remote)", style = MaterialTheme.typography.bodyMedium)
          Switch(
            checked = isRemote,
            onCheckedChange = { isRemote = it },
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color(0xFF0F172A),
              checkedTrackColor = Color(0xFF2563EB)
            )
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text(AppStrings.get("new_job_desc_label", lang)) },
          minLines = 3,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = requirements,
          onValueChange = { requirements = it },
          label = { Text(AppStrings.get("new_job_req_label", lang)) },
          minLines = 2,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("هاتف التواصل للتوظيف") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = {
            val min = salaryMin.toIntOrNull() ?: 4000
            val max = salaryMax.toIntOrNull() ?: 8000
            val desc = if (description.isBlank()) "تفاصيل الوظيفة حسب متطلبات جهة العمل." else description
            val req = if (requirements.isBlank()) "مؤهل مناسب وسيرة ذاتية سارية." else requirements
            onSubmit(
              if (title.isBlank()) "موظف متميز" else title,
              company,
              location,
              category,
              contractType,
              min,
              max,
              desc,
              req,
              isRemote,
              phone
            )
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB), contentColor = Color.White),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.PostAdd, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text(AppStrings.get("publish_offer_btn", lang), fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun JobApplicantsDialog(
  job: JobOffer,
  applications: List<JobApplicationEntity>,
  lang: AppLanguage,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .height(480.dp)
        .padding(4.dp)
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "المتقدمين للوظيفة (${applications.size})",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = job.title,
              style = MaterialTheme.typography.labelSmall,
              color = Color(0xFF2563EB)
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = null)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (applications.isEmpty()) {
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = AppStrings.get("no_applicants_yet", lang),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        } else {
          LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
          ) {
            items(applications) { candidate ->
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = candidate.candidateName,
                      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                      text = candidate.candidatePhone,
                      style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF2563EB))
                    )
                  }
                  Text(
                    text = "الخبرة: ${candidate.candidateExperience}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "رسالة التقديم: ${candidate.coverNote}",
                    style = MaterialTheme.typography.bodySmall
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
