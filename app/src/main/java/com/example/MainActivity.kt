package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.GoumMainDashboard
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.EyeComfortWarmTint
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GoumViewModel
import com.example.ui.windows.AppThemeMode

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: GoumViewModel = viewModel()
      val language by viewModel.language.collectAsStateWithLifecycle()
      val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
      val eyeComfortEnabled by viewModel.eyeComfortEnabled.collectAsStateWithLifecycle()
      val splashFinished by viewModel.splashFinished.collectAsStateWithLifecycle()
      val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

      val isDark = when (themeMode) {
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
      }

      val layoutDirection = if (language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

      val context = LocalContext.current
      val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
      ) { _ -> }

      LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          if (ContextCompat.checkSelfPermission(
              context,
              Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
          ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
          }
        }
      }

      CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        MyApplicationTheme(darkTheme = isDark) {
          Box(modifier = Modifier.fillMaxSize()) {
            Crossfade(
              targetState = Pair(splashFinished, userProfile != null),
              label = "9GoumAppScreenTransition"
            ) { (isSplashDone, hasUser) ->
              when {
                !isSplashDone -> {
                  SplashScreen(
                    lang = language,
                    onSplashFinished = { viewModel.finishSplash() }
                  )
                }
                !hasUser -> {
                  AuthScreen(
                    currentLang = language,
                    onLanguageChange = { viewModel.setLanguage(it) },
                    onSaveAndProceed = { email, country ->
                      viewModel.saveUserProfile(email, country)
                    }
                  )
                }
                else -> {
                  GoumMainDashboard(
                    viewModel = viewModel,
                    currentLang = language,
                    themeMode = themeMode,
                    eyeComfortEnabled = eyeComfortEnabled,
                    userProfile = userProfile,
                    onThemeModeChange = { viewModel.setThemeMode(it) },
                    onEyeComfortChange = { viewModel.setEyeComfort(it) },
                    onLanguageChange = { viewModel.setLanguage(it) },
                    onLogout = { viewModel.logout() }
                  )
                }
              }
            }

            // Warm Eye Comfort Filter Overlay
            if (eyeComfortEnabled) {
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(EyeComfortWarmTint)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  MyApplicationTheme { Greeting("9GOUM") }
}
