package com.example.data.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.example.data.model.ArtisanSettings
import com.example.data.model.EmployerSettings
import com.example.data.model.GoumAppSettings
import com.example.data.model.HomeServiceSettings
import com.example.data.model.JobSeekerSettings
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "section_settings")
data class SectionSettingsEntity(
  @PrimaryKey val id: Int = 1,
  // Job Seeker Settings
  val jobAlertsEnabled: Boolean = true,
  val minTargetSalary: Int = 3000,
  val remoteJobsOnly: Boolean = false,
  val hideProfileFromEmployers: Boolean = false,
  val preferredJobCategory: String = "الكل",
  val resumeName: String = "السيرة_الذاتية_المعتمدة.pdf",
  val resumeSkills: String = "إدارة مشاريع، برمجة وتطبيقات، خدمة عملاء",

  // Employer Settings
  val companyName: String = "مؤسسة 9GOUM للخدمات",
  val companyBio: String = "مؤسسة معتمدة لتوظيف الكفاءات وإدارة المشاريع",
  val recruiterEmail: String = "recruiting@9goum.com",
  val recruiterPhone: String = "+966509988776",
  val applicantNotifyInstant: Boolean = true,
  val autoFilterMinExperience: Int = 2,
  val autoArchiveDays: Int = 30,

  // Artisan Settings
  val isArtisanAvailableForEmergency: Boolean = true,
  val coverageRadiusKm: Int = 25,
  val standardHourlyRate: Int = 75,
  val publicPhoneVisible: Boolean = true,
  val instantQuoteAlerts: Boolean = true,
  val artisanTrade: String = "نجارة وأثاث",

  // Home Service Settings
  val defaultHomeAddress: String = "الرياض - حي الملقا، طريق الملك سلمان",
  val emergencyHornsEnabled: Boolean = true,
  val requestOriginalPartsWithWarranty: Boolean = true,
  val preferredPaymentMethod: String = "الدفع نقداً أو بطاقة بعد الإنجاز",
  val saveServiceInvoices: Boolean = true
) {
  fun toModel(): GoumAppSettings = GoumAppSettings(
    jobSeekerSettings = JobSeekerSettings(
      jobAlertsEnabled = jobAlertsEnabled,
      minTargetSalary = minTargetSalary,
      remoteJobsOnly = remoteJobsOnly,
      hideProfileFromEmployers = hideProfileFromEmployers,
      preferredCategory = preferredJobCategory,
      resumeName = resumeName,
      resumeSkills = resumeSkills
    ),
    employerSettings = EmployerSettings(
      companyName = companyName,
      companyBio = companyBio,
      recruiterEmail = recruiterEmail,
      recruiterPhone = recruiterPhone,
      applicantNotifyInstant = applicantNotifyInstant,
      autoFilterMinExperience = autoFilterMinExperience,
      autoArchiveDays = autoArchiveDays
    ),
    artisanSettings = ArtisanSettings(
      isAvailableForEmergency = isArtisanAvailableForEmergency,
      coverageRadiusKm = coverageRadiusKm,
      standardHourlyRate = standardHourlyRate,
      publicPhoneVisible = publicPhoneVisible,
      instantQuoteAlerts = instantQuoteAlerts,
      artisanTrade = artisanTrade
    ),
    homeServiceSettings = HomeServiceSettings(
      defaultHomeAddress = defaultHomeAddress,
      emergencyHornsEnabled = emergencyHornsEnabled,
      requestOriginalPartsWithWarranty = requestOriginalPartsWithWarranty,
      preferredPaymentMethod = preferredPaymentMethod,
      saveServiceInvoices = saveServiceInvoices
    )
  )

  companion object {
    fun fromModel(model: GoumAppSettings): SectionSettingsEntity = SectionSettingsEntity(
      id = 1,
      jobAlertsEnabled = model.jobSeekerSettings.jobAlertsEnabled,
      minTargetSalary = model.jobSeekerSettings.minTargetSalary,
      remoteJobsOnly = model.jobSeekerSettings.remoteJobsOnly,
      hideProfileFromEmployers = model.jobSeekerSettings.hideProfileFromEmployers,
      preferredJobCategory = model.jobSeekerSettings.preferredCategory,
      resumeName = model.jobSeekerSettings.resumeName,
      resumeSkills = model.jobSeekerSettings.resumeSkills,

      companyName = model.employerSettings.companyName,
      companyBio = model.employerSettings.companyBio,
      recruiterEmail = model.employerSettings.recruiterEmail,
      recruiterPhone = model.employerSettings.recruiterPhone,
      applicantNotifyInstant = model.employerSettings.applicantNotifyInstant,
      autoFilterMinExperience = model.employerSettings.autoFilterMinExperience,
      autoArchiveDays = model.employerSettings.autoArchiveDays,

      isArtisanAvailableForEmergency = model.artisanSettings.isAvailableForEmergency,
      coverageRadiusKm = model.artisanSettings.coverageRadiusKm,
      standardHourlyRate = model.artisanSettings.standardHourlyRate,
      publicPhoneVisible = model.artisanSettings.publicPhoneVisible,
      instantQuoteAlerts = model.artisanSettings.instantQuoteAlerts,
      artisanTrade = model.artisanSettings.artisanTrade,

      defaultHomeAddress = model.homeServiceSettings.defaultHomeAddress,
      emergencyHornsEnabled = model.homeServiceSettings.emergencyHornsEnabled,
      requestOriginalPartsWithWarranty = model.homeServiceSettings.requestOriginalPartsWithWarranty,
      preferredPaymentMethod = model.homeServiceSettings.preferredPaymentMethod,
      saveServiceInvoices = model.homeServiceSettings.saveServiceInvoices
    )
  }
}

@Dao
interface SettingsDao {
  @Query("SELECT * FROM section_settings WHERE id = 1 LIMIT 1")
  fun getSettings(): Flow<SectionSettingsEntity?>

  @Query("SELECT * FROM section_settings WHERE id = 1 LIMIT 1")
  suspend fun getSettingsOnce(): SectionSettingsEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveSettings(settings: SectionSettingsEntity)
}
