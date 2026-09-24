package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Country
import com.example.data.CountryRepository
import com.example.data.db.UserProfileEntity
import com.example.data.model.HomeServiceOrder
import com.example.data.model.ServiceUrgency
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.components.GoumLogo
import com.example.ui.screens.artisans.ArtisansScreen
import com.example.ui.screens.homeservices.HomeServiceChatScreen
import com.example.ui.screens.homeservices.HomeServicesScreen
import com.example.ui.screens.jobs.JobSeekersScreen
import com.example.ui.screens.offers.JobOffersScreen
import com.example.ui.screens.settings.GlobalSettingsDialog
import com.example.ui.viewmodel.GoumTab
import com.example.ui.viewmodel.GoumViewModel
import com.example.ui.windows.AppThemeMode
import kotlinx.coroutines.flow.collectLatest

@Composable
fun GoumMainDashboard(
  viewModel: GoumViewModel,
  currentLang: AppLanguage,
  themeMode: AppThemeMode,
  eyeComfortEnabled: Boolean,
  userProfile: UserProfileEntity?,
  onThemeModeChange: (AppThemeMode) -> Unit,
  onEyeComfortChange: (Boolean) -> Unit,
  onLanguageChange: (AppLanguage) -> Unit,
  onLogout: () -> Unit
) {
  val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
  val selectedCountry by viewModel.selectedCountry.collectAsStateWithLifecycle()
  val sectionSettings by viewModel.sectionSettings.collectAsStateWithLifecycle()

  // Jobs state
  val filteredJobs by viewModel.filteredJobs.collectAsStateWithLifecycle()
  val allJobs by viewModel.allJobs.collectAsStateWithLifecycle()
  val jobsSearchQuery by viewModel.jobsSearchQuery.collectAsStateWithLifecycle()
  val jobsCategory by viewModel.jobsSelectedCategory.collectAsStateWithLifecycle()
  val myApplications by viewModel.myApplications.collectAsStateWithLifecycle()

  // Offers state
  val myPostedJobs by viewModel.myPostedJobs.collectAsStateWithLifecycle()

  // Artisans state
  val filteredArtisans by viewModel.filteredArtisans.collectAsStateWithLifecycle()
  val artisanSearchQuery by viewModel.artisanSearchQuery.collectAsStateWithLifecycle()
  val artisanTrade by viewModel.artisanSelectedTrade.collectAsStateWithLifecycle()
  val artisanEmergencyOnly by viewModel.artisanEmergencyOnly.collectAsStateWithLifecycle()

  // Home services state
  val homeOrders by viewModel.homeOrders.collectAsStateWithLifecycle()

  var showGlobalSettings by remember { mutableStateOf(false) }
  var showCountryPicker by remember { mutableStateOf(false) }
  var activeChatOrder by remember { mutableStateOf<HomeServiceOrder?>(null) }
  var isArtisansTopBarVisible by remember { mutableStateOf(true) }

  LaunchedEffect(activeTab) {
    if (activeTab != GoumTab.ARTISANS) {
      isArtisansTopBarVisible = true
    }
  }

  val snackbarHostState = remember { SnackbarHostState() }

  // Listen to ViewModel toast events
  LaunchedEffect(Unit) {
    viewModel.userMessage.collectLatest { message ->
      snackbarHostState.showSnackbar(message)
    }
  }

  // Dedicated Chat Screen when an order or offer chat is opened
  val currentChatOrder = activeChatOrder
  if (currentChatOrder != null) {
    val chatMessages by viewModel.getChatMessages(currentChatOrder.id).collectAsStateWithLifecycle(emptyList())
    val typingMap by viewModel.isTechnicianTyping.collectAsStateWithLifecycle()
    val isTyping = typingMap[currentChatOrder.id] == true

    HomeServiceChatScreen(
      order = currentChatOrder,
      messages = chatMessages,
      isTechnicianTyping = isTyping,
      lang = currentLang,
      onSendMessage = { text ->
        viewModel.sendHomeServiceChatMessage(
          orderId = currentChatOrder.id,
          text = text,
          technicianName = currentChatOrder.assignedTechnicianName,
          category = currentChatOrder.serviceCategory
        )
      },
      onBack = { activeChatOrder = null }
    )
    return
  }

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      AnimatedVisibility(
        visible = if (activeTab == GoumTab.ARTISANS) isArtisansTopBarVisible else true,
        enter = slideInVertically(initialOffsetY = { -it }) + expandVertically(),
        exit = slideOutVertically(targetOffsetY = { -it }) + shrinkVertically()
      ) {
        GoumTopHeader(
          activeTab = activeTab,
          lang = currentLang,
          selectedCountry = selectedCountry,
          onOpenSettings = { showGlobalSettings = true },
          onOpenCountryPicker = { showCountryPicker = true }
        )
      }
    },
    bottomBar = {
      GoumBottomNavigation(
        activeTab = activeTab,
        lang = currentLang,
        onTabSelect = { viewModel.setActiveTab(it) }
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      Crossfade(
        targetState = activeTab,
        label = "GoumTabCrossfade"
      ) { tab ->
        when (tab) {
          GoumTab.JOBS -> {
            JobSeekersScreen(
              lang = currentLang,
              jobs = filteredJobs,
              searchQuery = jobsSearchQuery,
              selectedCategory = jobsCategory,
              userProfile = userProfile,
              myApplications = myApplications,
              settings = sectionSettings.jobSeekerSettings,
              onSearchChange = { viewModel.setJobsSearchQuery(it) },
              onCategorySelect = { viewModel.setJobsSelectedCategory(it) },
              onApplyJob = { job, name, phone, exp, note ->
                viewModel.applyToJob(job, name, phone, exp, note)
              },
              onUpdateSettings = { updated ->
                viewModel.updateJobSeekerSettings { updated }
              }
            )
          }

          GoumTab.OFFERS -> {
            JobOffersScreen(
              lang = currentLang,
              myPostedJobs = myPostedJobs,
              allJobs = allJobs,
              settings = sectionSettings.employerSettings,
              myApplications = myApplications,
              onPostNewJob = { t, c, l, cat, cont, sMin, sMax, d, r, isRem, p ->
                viewModel.postNewJobOffer(t, c, l, cat, cont, sMin, sMax, d, r, isRem, p)
              },
              onUpdateJobStatus = { id, st -> viewModel.updateJobStatus(id, st) },
              onUpdateSettings = { updated ->
                viewModel.updateEmployerSettings { updated }
              }
            )
          }

          GoumTab.ARTISANS -> {
            ArtisansScreen(
              lang = currentLang,
              artisans = filteredArtisans,
              searchQuery = artisanSearchQuery,
              selectedTrade = artisanTrade,
              emergencyOnly = artisanEmergencyOnly,
              settings = sectionSettings.artisanSettings,
              isTopAreaVisible = isArtisansTopBarVisible,
              onTopBarVisibilityChange = { isArtisansTopBarVisible = it },
              onSearchChange = { viewModel.setArtisanSearchQuery(it) },
              onTradeSelect = { viewModel.setArtisanSelectedTrade(it) },
              onEmergencyToggle = { viewModel.setArtisanEmergencyOnly(it) },
              onRequestQuote = { art, name, phone, title, desc, budget ->
                viewModel.requestQuote(art, name, phone, title, desc, budget)
              },
              onRegisterArtisan = { name, trade, city, rate, phone, wa, bio ->
                viewModel.registerAsArtisan(name, trade, city, rate, phone, wa, bio)
              },
              onUpdateSettings = { updated ->
                viewModel.updateArtisanSettings { updated }
              }
            )
          }

          GoumTab.HOME_SERVICES -> {
            HomeServicesScreen(
              lang = currentLang,
              orders = homeOrders,
              settings = sectionSettings.homeServiceSettings,
              onCreateOrder = { cat, sub, urg, tm, addr, n, p, nts, cMin, cMax ->
                viewModel.createHomeServiceOrder(cat, sub, urg, tm, addr, n, p, nts, cMin, cMax)
              },
              onUpdateOrderStatus = { id, st -> viewModel.updateOrderStatus(id, st) },
              onCancelOrder = { id -> viewModel.cancelOrder(id) },
              onUpdateSettings = { updated ->
                viewModel.updateHomeServiceSettings { updated }
              },
              onOpenChat = { order ->
                activeChatOrder = order
              },
              onOpenOfferChat = { offer ->
                val existingOrder = homeOrders.find { it.assignedTechnicianName == offer.providerName && it.issueSubtype == offer.title }
                if (existingOrder != null) {
                  activeChatOrder = existingOrder
                } else {
                  val newOrder = HomeServiceOrder(
                    id = "chat_offer_${offer.id}",
                    serviceCategory = offer.category,
                    issueSubtype = offer.title,
                    urgency = ServiceUrgency.SAME_DAY,
                    scheduledTime = "اليوم - استفسار وحجز",
                    address = sectionSettings.homeServiceSettings.defaultHomeAddress.ifBlank { "الرياض، حي النرجس" },
                    clientName = "العميل",
                    clientPhone = "+966500000000",
                    notes = "استفسار ومحادثة بخصوص عرض: ${offer.title} (${offer.price})",
                    estimatedCostMin = 100,
                    estimatedCostMax = 250,
                    orderStatus = "DISPATCHED",
                    assignedTechnicianName = offer.providerName,
                    assignedTechnicianPhone = offer.phone
                  )
                  viewModel.createHomeServiceOrder(
                    newOrder.serviceCategory,
                    newOrder.issueSubtype,
                    newOrder.urgency,
                    newOrder.scheduledTime,
                    newOrder.address,
                    newOrder.clientName,
                    newOrder.clientPhone,
                    newOrder.notes,
                    newOrder.estimatedCostMin,
                    newOrder.estimatedCostMax
                  )
                  activeChatOrder = newOrder
                }
              }
            )
          }
        }
      }
    }
  }

  // Global Settings Dialog
  if (showGlobalSettings) {
    GlobalSettingsDialog(
      currentLang = currentLang,
      themeMode = themeMode,
      eyeComfortEnabled = eyeComfortEnabled,
      selectedCountry = selectedCountry,
      userProfile = userProfile,
      onThemeModeChange = onThemeModeChange,
      onEyeComfortChange = onEyeComfortChange,
      onLanguageChange = onLanguageChange,
      onCountryChangeRequest = {
        showGlobalSettings = false
        showCountryPicker = true
      },
      onLogout = onLogout,
      onDismiss = { showGlobalSettings = false }
    )
  }

  // Country Picker Dialog
  if (showCountryPicker) {
    CountryPickerDialog(
      currentLang = currentLang,
      selectedCountry = selectedCountry,
      onSelectCountry = {
        viewModel.setCountry(it)
        showCountryPicker = false
      },
      onDismiss = { showCountryPicker = false }
    )
  }
}

@Composable
fun GoumTopHeader(
  activeTab: GoumTab,
  lang: AppLanguage,
  selectedCountry: Country,
  onOpenSettings: () -> Unit,
  onOpenCountryPicker: () -> Unit
) {
  val (tabTitle, tabColor) = when (activeTab) {
    GoumTab.JOBS -> AppStrings.get("tab_jobs", lang) to Color(0xFFF59E0B)
    GoumTab.OFFERS -> AppStrings.get("tab_offers", lang) to Color(0xFF2563EB)
    GoumTab.ARTISANS -> AppStrings.get("tab_artisans", lang) to Color(0xFF0D9488)
    GoumTab.HOME_SERVICES -> AppStrings.get("tab_home_services", lang) to Color(0xFFDC2626)
  }

  Surface(
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 2.dp,
    modifier = Modifier
      .fillMaxWidth()
      .statusBarsPadding()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        GoumLogo(size = 36.dp)

        Spacer(modifier = Modifier.width(10.dp))

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "9GOUM",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
              )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = tabColor.copy(alpha = 0.15f)
            ) {
              Text(
                text = tabTitle,
                style = MaterialTheme.typography.labelSmall.copy(
                  color = tabColor,
                  fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Text(
            text = AppStrings.get("app_tagline", lang),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        // Country Flag & Dial Code Pill
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
          modifier = Modifier.clickable { onOpenCountryPicker() }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = selectedCountry.flagEmoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = selectedCountry.code,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
          }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Settings Button
        IconButton(
          onClick = onOpenSettings,
          modifier = Modifier.size(38.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = AppStrings.get("settings_title", lang),
            tint = MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }
  }
}

@Composable
fun GoumBottomNavigation(
  activeTab: GoumTab,
  lang: AppLanguage,
  onTabSelect: (GoumTab) -> Unit
) {
  NavigationBar(
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 8.dp,
    modifier = Modifier.navigationBarsPadding()
  ) {
    // Tab 1: Jobs
    NavigationBarItem(
      selected = activeTab == GoumTab.JOBS,
      onClick = { onTabSelect(GoumTab.JOBS) },
      icon = {
        Icon(Icons.Default.Work, contentDescription = AppStrings.get("tab_jobs", lang))
      },
      label = {
        Text(AppStrings.get("tab_jobs", lang), fontWeight = if (activeTab == GoumTab.JOBS) FontWeight.Bold else FontWeight.Normal)
      },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = Color(0xFF0F172A),
        indicatorColor = Color(0xFFF59E0B)
      )
    )

    // Tab 2: Offers (Employers)
    NavigationBarItem(
      selected = activeTab == GoumTab.OFFERS,
      onClick = { onTabSelect(GoumTab.OFFERS) },
      icon = {
        Icon(Icons.Default.Business, contentDescription = AppStrings.get("tab_offers", lang))
      },
      label = {
        Text(AppStrings.get("tab_offers", lang), fontWeight = if (activeTab == GoumTab.OFFERS) FontWeight.Bold else FontWeight.Normal)
      },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = Color.White,
        indicatorColor = Color(0xFF2563EB)
      )
    )

    // Tab 3: Artisans
    NavigationBarItem(
      selected = activeTab == GoumTab.ARTISANS,
      onClick = { onTabSelect(GoumTab.ARTISANS) },
      icon = {
        Icon(Icons.Default.Construction, contentDescription = AppStrings.get("tab_artisans", lang))
      },
      label = {
        Text(AppStrings.get("tab_artisans", lang), fontWeight = if (activeTab == GoumTab.ARTISANS) FontWeight.Bold else FontWeight.Normal)
      },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = Color.White,
        indicatorColor = Color(0xFF0D9488)
      )
    )

    // Tab 4: Home Services
    NavigationBarItem(
      selected = activeTab == GoumTab.HOME_SERVICES,
      onClick = { onTabSelect(GoumTab.HOME_SERVICES) },
      icon = {
        Icon(Icons.Default.HomeRepairService, contentDescription = AppStrings.get("tab_home_services", lang))
      },
      label = {
        Text(AppStrings.get("tab_home_services", lang), fontWeight = if (activeTab == GoumTab.HOME_SERVICES) FontWeight.Bold else FontWeight.Normal)
      },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = Color.White,
        indicatorColor = Color(0xFFDC2626)
      )
    )
  }
}
