package com.example.localization

enum class AppLanguage(val code: String, val displayName: String, val isRtl: Boolean) {
  ARABIC("ar", "العربية", true),
  ENGLISH("en", "English", false),
  FRENCH("fr", "Français", false);

  companion object {
    fun fromCode(code: String): AppLanguage =
      entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: ARABIC
  }
}
