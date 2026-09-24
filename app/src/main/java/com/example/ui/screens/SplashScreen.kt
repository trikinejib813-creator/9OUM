package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.components.GoumLogo
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
  lang: AppLanguage,
  onSplashFinished: () -> Unit
) {
  var targetProgress by remember { mutableFloatStateOf(0f) }

  val animatedProgress by animateFloatAsState(
    targetValue = targetProgress,
    animationSpec = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
    label = "SplashProgress"
  )

  LaunchedEffect(Unit) {
    targetProgress = 0.35f
    delay(300)
    targetProgress = 0.70f
    delay(350)
    targetProgress = 1.0f
    delay(400)
    onSplashFinished()
  }

  val bgBrush = Brush.verticalGradient(
    colors = listOf(
      Color(0xFF090D16),
      Color(0xFF0F172A),
      Color(0xFF1E293B)
    )
  )

  val statusText = when {
    animatedProgress < 0.40f -> if (lang == AppLanguage.ARABIC) "تهيئة شبكة الوظائف والحرف..." else "Initializing employment & trades hub..."
    animatedProgress < 0.75f -> if (lang == AppLanguage.ARABIC) "تحميل خدمات الطوارئ المنزلية..." else "Connecting emergency home services..."
    else -> if (lang == AppLanguage.ARABIC) "مرحباً بك في منصة 9GOUM..." else "Welcome to 9GOUM..."
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(bgBrush)
      .statusBarsPadding()
      .navigationBarsPadding(),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.padding(24.dp)
    ) {
      GoumLogo(
        size = 150.dp,
        assemblyProgress = animatedProgress
      )

      Spacer(modifier = Modifier.height(28.dp))

      AnimatedVisibility(
        visible = animatedProgress > 0.3f,
        enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { 20 }
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "9GOUM",
            style = MaterialTheme.typography.displaySmall.copy(
              fontWeight = FontWeight.Black,
              fontSize = 34.sp,
              color = Color.White,
              letterSpacing = 2.sp
            ),
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = AppStrings.get("app_tagline", lang),
            style = MaterialTheme.typography.bodyMedium.copy(
              color = Color(0xFFFBBF24),
              fontSize = 14.sp,
              fontWeight = FontWeight.Medium
            ),
            textAlign = TextAlign.Center
          )
        }
      }

      Spacer(modifier = Modifier.height(32.dp))

      LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = Modifier
          .height(5.dp)
          .padding(horizontal = 48.dp),
        color = Color(0xFFF59E0B),
        trackColor = Color(0xFF1E293B),
        strokeCap = StrokeCap.Round
      )

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = statusText,
        style = MaterialTheme.typography.labelMedium.copy(
          color = Color(0xFF94A3B8),
          fontSize = 12.sp
        ),
        textAlign = TextAlign.Center
      )
    }

    TextButton(
      onClick = onSplashFinished,
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(16.dp),
      colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF94A3B8))
    ) {
      Text(
        text = AppStrings.get("skip_splash", lang),
        style = MaterialTheme.typography.labelMedium
      )
    }
  }
}
