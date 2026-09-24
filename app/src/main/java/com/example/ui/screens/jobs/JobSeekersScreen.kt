package com.example.ui.screens.jobs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.db.JobApplicationEntity
import com.example.data.db.UserProfileEntity
import com.example.data.model.JobOffer
import com.example.data.model.JobSeekerSettings
import com.example.data.model.PREDEFINED_JOB_CATEGORIES
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.screens.settings.JobSeekerSettingsDialog

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JobSeekersScreen(
  lang: AppLanguage,
  jobs: List<JobOffer>,
  searchQuery: String,
  selectedCategory: String,
  userProfile: UserProfileEntity?,
  myApplications: List<JobApplicationEntity>,
  settings: JobSeekerSettings,
  onSearchChange: (String) -> Unit,
  onCategorySelect: (String) -> Unit,
  onApplyJob: (job: JobOffer, name: String, phone: String, exp: String, note: String) -> Unit,
  onUpdateSettings: (JobSeekerSettings) -> Unit
) {
  var selectedJobForDetail by remember { mutableStateOf<JobOffer?>(null) }
  var selectedJobForApply by remember { mutableStateOf<JobOffer?>(null) }
  var showMyApplicationsDialog by remember { mutableStateOf(false) }
  var showSettingsDialog by remember { mutableStateOf(false) }

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
          text = AppStrings.get("jobs_title", lang),
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
        )
        Text(
          text = AppStrings.get("jobs_subtitle", lang),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        // My Applications Button
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier.clickable { showMyApplicationsDialog = true }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              Icons.Default.Description,
              contentDescription = null,
              tint = Color(0xFFF59E0B),
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${AppStrings.get("my_applications_btn", lang)} (${myApplications.size})",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
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
            contentDescription = AppStrings.get("job_settings_title", lang),
            tint = Color(0xFFF59E0B)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Search Box
    OutlinedTextField(
      value = searchQuery,
      onValueChange = onSearchChange,
      placeholder = { Text(AppStrings.get("search_placeholder", lang)) },
      leadingIcon = {
        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFF59E0B))
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

    // Categories Horizontal Chips
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(PREDEFINED_JOB_CATEGORIES) { cat ->
        val isSelected = selectedCategory == cat
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (isSelected) Color(0xFFF59E0B) else MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier.clickable { onCategorySelect(cat) }
        ) {
          Text(
            text = cat,
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              color = if (isSelected) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Jobs Count & Notice
    Text(
      text = "${jobs.size} فرصة عمل متوفرة حالياً",
      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(6.dp))

    // Jobs List
    if (jobs.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(top = 40.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "لا توجد وظائف مطابقة لبحثك الحالي أو لمعايير الراتب المختارة في الإعدادات.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    } else {
      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(jobs, key = { it.id }) { job ->
          JobCard(
            job = job,
            lang = lang,
            onClick = { selectedJobForDetail = job },
            onApply = { selectedJobForApply = job }
          )
        }
        item {
          Spacer(modifier = Modifier.height(80.dp))
        }
      }
    }
  }

  // Job Details Dialog
  selectedJobForDetail?.let { job ->
    JobDetailsDialog(
      job = job,
      lang = lang,
      onDismiss = { selectedJobForDetail = null },
      onApply = {
        selectedJobForDetail = null
        selectedJobForApply = job
      }
    )
  }

  // Job Apply Dialog
  selectedJobForApply?.let { job ->
    JobApplyDialog(
      job = job,
      lang = lang,
      userProfile = userProfile,
      defaultSkills = settings.resumeSkills,
      onDismiss = { selectedJobForApply = null },
      onSubmit = { name, phone, exp, note ->
        onApplyJob(job, name, phone, exp, note)
        selectedJobForApply = null
      }
    )
  }

  // My Applications Dialog
  if (showMyApplicationsDialog) {
    MyApplicationsDialog(
      applications = myApplications,
      lang = lang,
      onDismiss = { showMyApplicationsDialog = false }
    )
  }

  // Dedicated Job Seeker Settings Dialog
  if (showSettingsDialog) {
    JobSeekerSettingsDialog(
      currentSettings = settings,
      lang = lang,
      onSave = onUpdateSettings,
      onDismiss = { showSettingsDialog = false }
    )
  }
}

@Composable
fun JobCard(
  job: JobOffer,
  lang: AppLanguage,
  onClick: () -> Unit,
  onApply: () -> Unit
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
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = job.title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = job.company,
            style = MaterialTheme.typography.bodyMedium.copy(
              color = Color(0xFFF59E0B),
              fontWeight = FontWeight.SemiBold
            )
          )
        }

        // Remote badge or contract badge
        if (job.isRemote) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0x2610B981)
          ) {
            Text(
              text = AppStrings.get("remote_badge", lang),
              style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFF10B981),
                fontWeight = FontWeight.Bold
              ),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Location & Contract type
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            Icons.Default.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = job.location,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = MaterialTheme.colorScheme.surface
        ) {
          Text(
            text = job.contractType.localized(lang == AppLanguage.ARABIC),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Salary Range & Apply Action
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            Icons.Default.Payments,
            contentDescription = null,
            tint = Color(0xFF10B981),
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${job.salaryMin} - ${job.salaryMax} ${job.currency}",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
          )
        }

        if (job.isApplied) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0x2610B981)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = AppStrings.get("already_applied", lang),
                style = MaterialTheme.typography.labelSmall.copy(
                  color = Color(0xFF10B981),
                  fontWeight = FontWeight.Bold
                )
              )
            }
          }
        } else {
          Button(
            onClick = onApply,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFFF59E0B),
              contentColor = Color(0xFF0F172A)
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp)
          ) {
            Text(
              text = AppStrings.get("apply_now", lang),
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
          }
        }
      }
    }
  }
}

@Composable
fun JobDetailsDialog(
  job: JobOffer,
  lang: AppLanguage,
  onDismiss: () -> Unit,
  onApply: () -> Unit
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
            text = job.title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f)
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = null)
          }
        }

        Text(
          text = "${job.company} • ${job.location}",
          style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFF59E0B), fontWeight = FontWeight.SemiBold)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Tags
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
            Text(job.category, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall)
          }
          Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
            Text(job.contractType.localized(lang == AppLanguage.ARABIC), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall)
          }
          Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
            Text(job.experienceLevel, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "الوصف الوظيفي والمهام:",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = job.description,
          style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "المتطلبات والشروط:",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = job.requirements,
          style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "الراتب الشهري التقديري: ${job.salaryMin} - ${job.salaryMax} ${job.currency}",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (!job.isApplied) {
          Button(
            onClick = onApply,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B), contentColor = Color(0xFF0F172A)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Send, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text(AppStrings.get("apply_now", lang), fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun JobApplyDialog(
  job: JobOffer,
  lang: AppLanguage,
  userProfile: UserProfileEntity?,
  defaultSkills: String,
  onDismiss: () -> Unit,
  onSubmit: (name: String, phone: String, exp: String, note: String) -> Unit
) {
  var name by remember { mutableStateOf("عبدالله المحمد") }
  var phone by remember { mutableStateOf("+966501234567") }
  var experience by remember { mutableStateOf("3 سنوات في نفس التخصص") }
  var note by remember { mutableStateOf("يسعدني التقدم لهذه الوظيفة لما أملكه من خبرة في المجال.") }

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
            text = "تقديم سريع للوظيفة",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = null)
          }
        }

        Text(
          text = "${job.title} - ${job.company}",
          style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFF59E0B), fontWeight = FontWeight.SemiBold)
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text(AppStrings.get("applicant_name", lang)) },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text(AppStrings.get("applicant_phone", lang)) },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = experience,
          onValueChange = { experience = it },
          label = { Text(AppStrings.get("applicant_exp", lang)) },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = note,
          onValueChange = { note = it },
          label = { Text(AppStrings.get("applicant_note", lang)) },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = { onSubmit(name, phone, experience, note) },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B), contentColor = Color(0xFF0F172A)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.Send, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text(AppStrings.get("submit_application", lang), fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun MyApplicationsDialog(
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
          Text(
            text = AppStrings.get("my_applications_btn", lang),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
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
              text = "لم تقدم على أي وظيفة بعد. تصفح الفرص المتاحة وقدم بنقرة زر!",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        } else {
          LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
          ) {
            items(applications) { app ->
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
                      text = app.jobTitle,
                      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                      modifier = Modifier.weight(1f)
                    )
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = Color(0x2610B981)
                    ) {
                      Text(
                        text = "تم الإرسال ✓",
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = Color(0xFF10B981),
                          fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                  Text(
                    text = app.company,
                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFF59E0B))
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "ملاحظة التقديم: ${app.coverNote}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
