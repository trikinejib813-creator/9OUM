package com.example.data

import com.example.localization.AppLanguage

data class Country(
  val code: String,
  val nameAr: String,
  val nameEn: String,
  val nameFr: String,
  val flagEmoji: String,
  val dialCode: String,
  val region: String
) {
  fun localizedName(lang: AppLanguage): String = when (lang) {
    AppLanguage.ARABIC -> nameAr
    AppLanguage.ENGLISH -> nameEn
    AppLanguage.FRENCH -> nameFr
  }
}

object CountryRepository {
  val countries: List<Country> = listOf(
    Country("SA", "المملكة العربية السعودية", "Saudi Arabia", "Arabie Saoudite", "🇸🇦", "+966", "الشرق الأوسط"),
    Country("AE", "الإمارات العربية المتحدة", "United Arab Emirates", "Émirats Arabes Unis", "🇦🇪", "+971", "الشرق الأوسط"),
    Country("EG", "جمهورية مصر العربية", "Egypt", "Égypte", "🇪🇬", "+20", "شمال أفريقيا"),
    Country("DZ", "الجزائر", "Algeria", "Algérie", "🇩🇿", "+213", "شمال أفريقيا"),
    Country("MA", "المملكة المغربية", "Morocco", "Maroc", "🇲🇦", "+212", "شمال أفريقيا"),
    Country("TN", "تونس", "Tunisia", "Tunisie", "🇹🇳", "+216", "شمال أفريقيا"),
    Country("QA", "قطر", "Qatar", "Qatar", "🇶🇦", "+974", "الشرق الأوسط"),
    Country("KW", "الكويت", "Kuwait", "Koweït", "🇰🇼", "+965", "الشرق الأوسط"),
    Country("OM", "سلطنة عمان", "Oman", "Oman", "🇴🇲", "+968", "الشرق الأوسط"),
    Country("BH", "البحرين", "Bahrain", "Bahreïn", "🇧🇭", "+973", "الشرق الأوسط"),
    Country("JO", "المملكة الأردنية الهاشمية", "Jordan", "Jordanie", "🇯🇴", "+962", "الشرق الأوسط"),
    Country("IQ", "العراق", "Iraq", "Irak", "🇮🇶", "+964", "الشرق الأوسط"),
    Country("LB", "لبنان", "Lebanon", "Liban", "🇱🇧", "+961", "الشرق الأوسط"),
    Country("PS", "فلسطين", "Palestine", "Palestine", "🇵🇸", "+970", "الشرق الأوسط"),
    Country("LY", "ليبيا", "Libya", "Libye", "🇱🇾", "+218", "شمال أفريقيا"),
    Country("SD", "السودان", "Sudan", "Soudan", "🇸🇩", "+249", "أفريقيا"),
    Country("FR", "فرنسا", "France", "France", "🇫🇷", "+33", "أوروبا"),
    Country("US", "الولايات المتحدة", "United States", "États-Unis", "🇺🇸", "+1", "الأمريكتان"),
    Country("GB", "المملكة المتحدة", "United Kingdom", "Royaume-Uni", "🇬🇧", "+44", "أوروبا"),
    Country("DE", "ألمانيا", "Germany", "Allemagne", "🇩🇪", "+49", "أوروبا"),
    Country("CA", "كندا", "Canada", "Canada", "🇨🇦", "+1", "الأمريكتان"),
    Country("TR", "تركيا", "Turkey", "Turquie", "🇹🇷", "+90", "أوروبا / آسيا"),
    Country("ES", "إسبانيا", "Spain", "Espagne", "🇪🇸", "+34", "أوروبا"),
    Country("IT", "إيطاليا", "Italy", "Italie", "🇮🇹", "+39", "أوروبا"),
    Country("JP", "اليابان", "Japan", "Japon", "🇯🇵", "+81", "آسيا"),
    Country("MY", "ماليزيا", "Malaysia", "Malaisie", "🇲🇾", "+60", "آسيا"),
    Country("ID", "إندونيسيا", "Indonesia", "Indonésie", "🇮🇩", "+62", "آسيا"),
    Country("BR", "البرازيل", "Brazil", "Brésil", "🇧🇷", "+55", "الأمريكتان")
  )

  fun findByCode(code: String): Country? = countries.firstOrNull { it.code.equals(code, ignoreCase = true) }
  val defaultCountry: Country = countries[0] // Saudi Arabia
}
