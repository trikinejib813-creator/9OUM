package com.example.data.model

import java.util.UUID

data class Artisan(
  val id: String = UUID.randomUUID().toString(),
  val name: String,
  val trade: String,
  val city: String,
  val rating: Float = 4.8f,
  val reviewsCount: Int = 12,
  val experienceYears: Int = 8,
  val hourlyRate: Int = 60,
  val dailyRate: Int = 300,
  val currency: String = "SAR",
  val phone: String,
  val whatsapp: String,
  val bio: String,
  val portfolioHighlights: List<String> = emptyList(),
  val isVerified: Boolean = true,
  val isEmergencyAvailable: Boolean = true,
  val isRegisteredByMe: Boolean = false,
  val coverageRadiusKm: Int = 30
)

data class ArtisanQuoteRequest(
  val id: String = UUID.randomUUID().toString(),
  val artisanId: String,
  val artisanName: String,
  val trade: String,
  val clientName: String,
  val clientPhone: String,
  val projectTitle: String,
  val description: String,
  val estimatedBudget: String,
  val requestedDate: Long = System.currentTimeMillis(),
  val status: String = "PENDING" // PENDING, ACCEPTED, DECLINED, COMPLETED
)

val PREDEFINED_ARTISAN_TRADES = listOf(
  "الكل",
  "نجارة وأثاث",
  "حدادة ولحام",
  "دهانات وديكور",
  "ألومنيوم وزجاج",
  "بناء وسيراميك",
  "جبس بورد وإضاءة",
  "كهرباء تأسيس وصيانة",
  "سباكة وتمديدات",
  "ميكانيك سيارات",
  "تنجيد وفرش",
  "صيانة أجهزة منزلية"
)
