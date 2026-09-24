package com.example.data.model

import java.util.UUID

enum class JobContractType(val arabicName: String, val englishName: String, val frenchName: String) {
  FULL_TIME("دوام كامل", "Full Time", "Temps Plein"),
  PART_TIME("دوام جزئي", "Part Time", "Temps Partiel"),
  REMOTE("عن بُعد / حر", "Remote / Freelance", "À distance / Freelance"),
  CONTRACT("عقد محدد", "Contract", "Contrat CDD");

  fun localized(isAr: Boolean): String = if (isAr) arabicName else englishName
}

data class JobOffer(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val company: String,
  val location: String,
  val category: String,
  val contractType: JobContractType = JobContractType.FULL_TIME,
  val experienceLevel: String = "متوسط (3-5 سنوات)",
  val salaryMin: Int = 4000,
  val salaryMax: Int = 8000,
  val currency: String = "SAR",
  val description: String,
  val requirements: String,
  val isRemote: Boolean = false,
  val postedAt: Long = System.currentTimeMillis(),
  val isApplied: Boolean = false,
  val applicationStatus: String = "NONE", // NONE, APPLIED, REVIEWING, INTERVIEW, ACCEPTED
  val status: String = "ACTIVE", // ACTIVE, PAUSED, CLOSED
  val isOwner: Boolean = false,
  val applicantsCount: Int = 0,
  val contactPhone: String = "",
  val contactEmail: String = ""
)

data class JobApplication(
  val id: String = UUID.randomUUID().toString(),
  val jobId: String,
  val jobTitle: String,
  val company: String,
  val candidateName: String,
  val candidateEmail: String,
  val candidatePhone: String,
  val candidateExperience: String,
  val candidateSkills: String,
  val coverNote: String,
  val appliedAt: Long = System.currentTimeMillis(),
  val status: String = "APPLIED" // APPLIED, REVIEWING, INTERVIEW, ACCEPTED, REJECTED
)

val PREDEFINED_JOB_CATEGORIES = listOf(
  "الكل",
  "تكنولوجيا ومعلومات",
  "هندسة وتشييد",
  "محاسبة ومالية",
  "تسويق ومبيعات",
  "إدارة وموارد بشرية",
  "خدمات عملاء واستقبال",
  "تعليم وتدريب",
  "صحة وطب",
  "لوجستيات وتوصيل"
)
