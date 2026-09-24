package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = CyanPrimary,
  onPrimary = Color(0xFF032641),
  primaryContainer = Color(0xFF0C4A6E),
  onPrimaryContainer = Color(0xFFE0F2FE),
  secondary = VioletSecondary,
  onSecondary = Color(0xFF1E1B4B),
  secondaryContainer = Color(0xFF312E81),
  onSecondaryContainer = Color(0xFFEEF2FF),
  tertiary = EmeraldSuccess,
  background = NightBackground,
  onBackground = TextPrimaryDark,
  surface = NightSurface,
  onSurface = TextPrimaryDark,
  surfaceVariant = NightSurfaceVariant,
  onSurfaceVariant = TextSecondaryDark,
  outline = NightBorder,
  error = RoseError
)

private val LightColorScheme = lightColorScheme(
  primary = LightPrimary,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFE0F2FE),
  onPrimaryContainer = Color(0xFF0369A1),
  secondary = LightSecondary,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFEEF2FF),
  onSecondaryContainer = Color(0xFF4338CA),
  tertiary = EmeraldSuccess,
  background = LightBackground,
  onBackground = TextPrimaryLight,
  surface = LightSurface,
  onSurface = TextPrimaryLight,
  surfaceVariant = LightSurfaceVariant,
  onSurfaceVariant = TextSecondaryLight,
  outline = LightBorder,
  error = RoseError
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
