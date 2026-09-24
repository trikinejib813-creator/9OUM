package com.example.data.model

data class JobSeekerSettings(
  val jobAlertsEnabled: Boolean = true,
  val minTargetSalary: Int = 3000,
  val remoteJobsOnly: Boolean = false,
  val hideProfileFromEmployers: Boolean = false,
  val preferredCategory: String = "الكل",
  val resumeName: String = "السيرة الذاتية المهنية.pdf",
  val resumeSkills: String = "إدارة، حاسوب، تواصل سريع"
)

data class EmployerSettings(
  val companyName: String = "مؤسسة الأعمال المتميزة",
  val companyBio: String = "مؤسسة رائدة في تقديم الحلول والخدمات الاحترافية",
  val recruiterEmail: String = "jobs@goum9.com",
  val recruiterPhone: String = "+966509988776",
  val applicantNotifyInstant: Boolean = true,
  val autoFilterMinExperience: Int = 2,
  val autoArchiveDays: Int = 30
)

data class ArtisanSettings(
  val isAvailableForEmergency: Boolean = true,
  val coverageRadiusKm: Int = 25,
  val standardHourlyRate: Int = 75,
  val publicPhoneVisible: Boolean = true,
  val instantQuoteAlerts: Boolean = true,
  val artisanTrade: String = "نجارة وأثاث"
)

data class HomeServiceSettings(
  val defaultHomeAddress: String = "الرياض - حي الياسمين، شارع أنس بن مالك",
  val emergencyHornsEnabled: Boolean = true,
  val requestOriginalPartsWithWarranty: Boolean = true,
  val preferredPaymentMethod: String = "الدفع نقداً أو عند المعاينة",
  val saveServiceInvoices: Boolean = true
)

data class GoumAppSettings(
  val jobSeekerSettings: JobSeekerSettings = JobSeekerSettings(),
  val employerSettings: EmployerSettings = EmployerSettings(),
  val artisanSettings: ArtisanSettings = ArtisanSettings(),
  val homeServiceSettings: HomeServiceSettings = HomeServiceSettings()
)
