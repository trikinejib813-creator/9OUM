package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Country
import com.example.data.CountryRepository
import com.example.data.db.AppDatabase
import com.example.data.db.JobApplicationEntity
import com.example.data.db.UserProfileEntity
import com.example.data.model.Artisan
import com.example.data.model.ArtisanQuoteRequest
import com.example.data.model.ArtisanSettings
import com.example.data.model.EmployerSettings
import com.example.data.model.GoumAppSettings
import com.example.data.model.HomeServiceChatMessage
import com.example.data.model.HomeServiceOrder
import com.example.data.model.HomeServiceSettings
import com.example.data.model.JobApplication
import com.example.data.model.JobContractType
import com.example.data.model.JobOffer
import com.example.data.model.JobSeekerSettings
import com.example.data.model.ServiceUrgency
import com.example.data.repository.GoumRepository
import com.example.localization.AppLanguage
import com.example.ui.windows.AppThemeMode
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class GoumTab {
  JOBS,
  OFFERS,
  ARTISANS,
  HOME_SERVICES
}

class GoumViewModel(application: Application) : AndroidViewModel(application) {
  private val database = AppDatabase.getDatabase(application)
  private val repository = GoumRepository(database)

  // Navigation & Splash
  private val _splashFinished = MutableStateFlow(false)
  val splashFinished: StateFlow<Boolean> = _splashFinished.asStateFlow()

  private val _activeTab = MutableStateFlow(GoumTab.JOBS)
  val activeTab: StateFlow<GoumTab> = _activeTab.asStateFlow()

  // App Theme & Appearance
  private val _themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
  val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

  private val _eyeComfortEnabled = MutableStateFlow(false)
  val eyeComfortEnabled: StateFlow<Boolean> = _eyeComfortEnabled.asStateFlow()

  private val _language = MutableStateFlow(AppLanguage.ARABIC)
  val language: StateFlow<AppLanguage> = _language.asStateFlow()

  // User Profile
  val userProfile: StateFlow<UserProfileEntity?> = database.userDao().getUserProfile()
    .stateIn(viewModelScope, SharingStarted.Eagerly, null)

  private val _selectedCountry = MutableStateFlow(CountryRepository.defaultCountry)
  val selectedCountry: StateFlow<Country> = _selectedCountry.asStateFlow()

  // Section Settings
  val sectionSettings: StateFlow<GoumAppSettings> = repository.settingsFlow
    .stateIn(viewModelScope, SharingStarted.Eagerly, GoumAppSettings())

  // --- JOBS TAB STATE ---
  val allJobs: StateFlow<List<JobOffer>> = repository.allJobs
    .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

  private val _jobsSearchQuery = MutableStateFlow("")
  val jobsSearchQuery: StateFlow<String> = _jobsSearchQuery.asStateFlow()

  private val _jobsSelectedCategory = MutableStateFlow("الكل")
  val jobsSelectedCategory: StateFlow<String> = _jobsSelectedCategory.asStateFlow()

  val filteredJobs: StateFlow<List<JobOffer>> = combine(
    allJobs,
    _jobsSearchQuery,
    _jobsSelectedCategory,
    sectionSettings
  ) { jobs, query, category, settings ->
    jobs.filter { job ->
      val matchesQuery = query.isBlank() ||
        job.title.contains(query, ignoreCase = true) ||
        job.company.contains(query, ignoreCase = true) ||
        job.location.contains(query, ignoreCase = true)

      val matchesCategory = category == "الكل" || job.category == category

      val matchesRemote = !settings.jobSeekerSettings.remoteJobsOnly || job.isRemote

      val matchesSalary = job.salaryMax >= settings.jobSeekerSettings.minTargetSalary

      matchesQuery && matchesCategory && matchesRemote && matchesSalary
    }
  }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

  val myApplications: StateFlow<List<JobApplicationEntity>> = repository.myApplications
    .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

  // --- OFFERS TAB (EMPLOYERS) STATE ---
  val myPostedJobs: StateFlow<List<JobOffer>> = repository.myPostedJobs
    .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

  // --- ARTISANS TAB STATE ---
  val allArtisans: StateFlow<List<Artisan>> = repository.allArtisans
    .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

  private val _artisanSearchQuery = MutableStateFlow("")
  val artisanSearchQuery: StateFlow<String> = _artisanSearchQuery.asStateFlow()

  private val _artisanSelectedTrade = MutableStateFlow("الكل")
  val artisanSelectedTrade: StateFlow<String> = _artisanSelectedTrade.asStateFlow()

  private val _artisanEmergencyOnly = MutableStateFlow(false)
  val artisanEmergencyOnly: StateFlow<Boolean> = _artisanEmergencyOnly.asStateFlow()

  val filteredArtisans: StateFlow<List<Artisan>> = combine(
    allArtisans,
    _artisanSearchQuery,
    _artisanSelectedTrade,
    _artisanEmergencyOnly
  ) { artisans, query, trade, emergencyOnly ->
    artisans.filter { art ->
      val matchesQuery = query.isBlank() ||
        art.name.contains(query, ignoreCase = true) ||
        art.trade.contains(query, ignoreCase = true) ||
        art.city.contains(query, ignoreCase = true)

      val matchesTrade = trade == "الكل" || art.trade == trade
      val matchesEmergency = !emergencyOnly || art.isEmergencyAvailable

      matchesQuery && matchesTrade && matchesEmergency
    }
  }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

  val allQuotes: StateFlow<List<ArtisanQuoteRequest>> = repository.allQuotes
    .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

  // --- HOME SERVICES TAB STATE ---
  val homeOrders: StateFlow<List<HomeServiceOrder>> = repository.allOrders
    .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

  // UI Toast / Event Bus
  private val _userMessage = MutableSharedFlow<String>()
  val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

  init {
    viewModelScope.launch {
      repository.seedInitialDataIfNeeded()
    }
  }

  fun finishSplash() {
    _splashFinished.value = true
  }

  fun setActiveTab(tab: GoumTab) {
    _activeTab.value = tab
  }

  fun setThemeMode(mode: AppThemeMode) {
    _themeMode.value = mode
  }

  fun setEyeComfort(enabled: Boolean) {
    _eyeComfortEnabled.value = enabled
  }

  fun setLanguage(lang: AppLanguage) {
    _language.value = lang
  }

  fun setCountry(country: Country) {
    _selectedCountry.value = country
  }

  fun saveUserProfile(email: String, country: Country) {
    viewModelScope.launch {
      val entity = UserProfileEntity(
        id = 1,
        encryptedEmail = email,
        countryCode = country.code,
        countryName = country.nameAr,
        registeredAt = System.currentTimeMillis(),
        lastSyncAt = System.currentTimeMillis(),
        isSynced = true
      )
      database.userDao().saveUserProfile(entity)
      _selectedCountry.value = country
      _userMessage.emit("مرحباً بك في منصة 9GOUM!")
    }
  }

  fun logout() {
    viewModelScope.launch {
      database.userDao().clearUserProfile()
    }
  }

  // --- JOBS ACTIONS ---
  fun setJobsSearchQuery(query: String) {
    _jobsSearchQuery.value = query
  }

  fun setJobsSelectedCategory(category: String) {
    _jobsSelectedCategory.value = category
  }

  fun applyToJob(
    job: JobOffer,
    candidateName: String,
    candidatePhone: String,
    candidateExp: String,
    coverNote: String
  ) {
    viewModelScope.launch {
      val email = userProfile.value?.encryptedEmail ?: "user@9goum.com"
      repository.applyToJob(
        jobId = job.id,
        jobTitle = job.title,
        company = job.company,
        name = candidateName,
        email = email,
        phone = candidatePhone,
        experience = candidateExp,
        skills = sectionSettings.value.jobSeekerSettings.resumeSkills,
        note = coverNote
      )
      _userMessage.emit("تم إرسال طلب التقديم إلى ${job.company} بنجاح!")
    }
  }

  // --- EMPLOYER / OFFERS ACTIONS ---
  fun postNewJobOffer(
    title: String,
    company: String,
    location: String,
    category: String,
    contractType: JobContractType,
    salaryMin: Int,
    salaryMax: Int,
    description: String,
    requirements: String,
    isRemote: Boolean,
    contactPhone: String
  ) {
    viewModelScope.launch {
      val newJob = JobOffer(
        title = title,
        company = company,
        location = location,
        category = category,
        contractType = contractType,
        salaryMin = salaryMin,
        salaryMax = salaryMax,
        description = description,
        requirements = requirements,
        isRemote = isRemote,
        contactPhone = contactPhone,
        isOwner = true
      )
      repository.postNewJob(newJob)
      _userMessage.emit("تم نشر الشاغر الوظيفي '$title' بنجاح!")
    }
  }

  fun updateJobStatus(jobId: String, status: String) {
    viewModelScope.launch {
      repository.updateJobStatus(jobId, status)
      _userMessage.emit("تم تحديث حالة العرض")
    }
  }

  // --- ARTISANS ACTIONS ---
  fun setArtisanSearchQuery(query: String) {
    _artisanSearchQuery.value = query
  }

  fun setArtisanSelectedTrade(trade: String) {
    _artisanSelectedTrade.value = trade
  }

  fun setArtisanEmergencyOnly(emergencyOnly: Boolean) {
    _artisanEmergencyOnly.value = emergencyOnly
  }

  fun requestQuote(
    artisan: Artisan,
    clientName: String,
    clientPhone: String,
    projectTitle: String,
    projectDesc: String,
    estimatedBudget: String
  ) {
    viewModelScope.launch {
      val quote = ArtisanQuoteRequest(
        artisanId = artisan.id,
        artisanName = artisan.name,
        trade = artisan.trade,
        clientName = clientName,
        clientPhone = clientPhone,
        projectTitle = projectTitle,
        description = projectDesc,
        estimatedBudget = estimatedBudget
      )
      repository.requestQuote(quote)
      _userMessage.emit("تم إرسال طلب التسعير للحرفي ${artisan.name} بنجاح!")
    }
  }

  fun registerAsArtisan(
    name: String,
    trade: String,
    city: String,
    hourlyRate: Int,
    phone: String,
    whatsapp: String,
    bio: String
  ) {
    viewModelScope.launch {
      val artisan = Artisan(
        name = name,
        trade = trade,
        city = city,
        hourlyRate = hourlyRate,
        dailyRate = hourlyRate * 5,
        phone = phone,
        whatsapp = whatsapp,
        bio = bio,
        isVerified = true,
        isEmergencyAvailable = true,
        isRegisteredByMe = true
      )
      repository.registerArtisan(artisan)
      _userMessage.emit("تهانينا! تم تسجيل ملفك الحرفي في دليل 9GOUM.")
    }
  }

  fun toggleEmergencyAvailability(artisanId: String, available: Boolean) {
    viewModelScope.launch {
      repository.updateEmergencyAvailability(artisanId, available)
    }
  }

  // --- HOME SERVICES ACTIONS ---
  fun createHomeServiceOrder(
    serviceCategory: String,
    issueSubtype: String,
    urgency: ServiceUrgency,
    scheduledTime: String,
    address: String,
    clientName: String,
    clientPhone: String,
    notes: String,
    costMin: Int,
    costMax: Int
  ) {
    viewModelScope.launch {
      val order = HomeServiceOrder(
        serviceCategory = serviceCategory,
        issueSubtype = issueSubtype,
        urgency = urgency,
        scheduledTime = scheduledTime,
        address = address,
        clientName = clientName,
        clientPhone = clientPhone,
        notes = notes,
        estimatedCostMin = costMin,
        estimatedCostMax = costMax,
        orderStatus = if (urgency == ServiceUrgency.EMERGENCY_30MIN) "DISPATCHED" else "REQUESTED"
      )
      repository.createHomeServiceOrder(order)
      val msg = if (urgency == ServiceUrgency.EMERGENCY_30MIN)
        "تم توجيه فني طوارئ على الفور إلى موقعك!"
      else
        "تم تسجيل موعد الصيانة بنجاح."
      _userMessage.emit(msg)
    }
  }

  fun updateOrderStatus(orderId: String, status: String) {
    viewModelScope.launch {
      repository.updateOrderStatus(orderId, status)
    }
  }

  fun cancelOrder(orderId: String) {
    viewModelScope.launch {
      repository.cancelOrder(orderId)
      _userMessage.emit("تم إلغاء الطلب")
    }
  }

  fun getChatMessages(orderId: String): kotlinx.coroutines.flow.Flow<List<HomeServiceChatMessage>> =
    repository.getChatMessages(orderId)

  private val _isTechnicianTyping = MutableStateFlow<Map<String, Boolean>>(emptyMap())
  val isTechnicianTyping: StateFlow<Map<String, Boolean>> = _isTechnicianTyping.asStateFlow()

  fun sendHomeServiceChatMessage(
    orderId: String,
    text: String,
    technicianName: String = "الفني المختص",
    category: String = ""
  ) {
    if (text.isBlank()) return
    viewModelScope.launch {
      val sender = "العميل"
      val userMsg = HomeServiceChatMessage(
        orderId = orderId,
        senderName = sender,
        senderRole = "CLIENT",
        text = text.trim(),
        isFromMe = true
      )
      repository.sendChatMessage(userMsg)

      // Simulate technician reply
      kotlinx.coroutines.delay(600)
      _isTechnicianTyping.value = _isTechnicianTyping.value + (orderId to true)
      kotlinx.coroutines.delay(1800)
      _isTechnicianTyping.value = _isTechnicianTyping.value - orderId

      val autoReplyText = when {
        text.contains("وقت") || text.contains("متى") || text.contains("تصل") ->
          "أنا الآن في الطريق، المسافة المتبقية حوالي 10 إلى 15 دقيقة فقط حسب حركة السير."
        text.contains("صورة") || text.contains("موقع") || text.contains("لوكيشن") ->
          "تم استلام تفاصيل الموقع والصورة بوضوح، القطع اللازمة متوفرة معي بالأدوات الأصلية."
        text.contains("محبس") || text.contains("قاطع") || text.contains("كهرباء") ->
          "ممتاز جداً تصرف سليم، يرجى إبقاء المكان مهيأ وسأبدأ الفحص الشامل فور وصولي."
        text.contains("تكلفة") || text.contains("سعر") || text.contains("خصم") ->
          "السعر يتم اعتماده بدقة حسب المقايسة المعروضة وبدون أي رسوم خفية مع ضمان رسمي."
        else ->
          "أهلاً بك! تم استلام رسالتك، لا تقلق كل الأمور تحت السيطرة وسأكون عندك في الموعد المحدد."
      }

      val techReply = HomeServiceChatMessage(
        orderId = orderId,
        senderName = technicianName,
        senderRole = "TECHNICIAN",
        text = autoReplyText,
        isFromMe = false
      )
      repository.sendChatMessage(techReply)
    }
  }

  // --- DEDICATED SECTION SETTINGS UPDATERS ---
  fun updateJobSeekerSettings(updater: (JobSeekerSettings) -> JobSeekerSettings) {
    viewModelScope.launch {
      repository.updateJobSeekerSettings(updater)
      _userMessage.emit("تم حفظ إعدادات الباحثين عن عمل")
    }
  }

  fun updateEmployerSettings(updater: (EmployerSettings) -> EmployerSettings) {
    viewModelScope.launch {
      repository.updateEmployerSettings(updater)
      _userMessage.emit("تم حفظ إعدادات عروض العمل")
    }
  }

  fun updateArtisanSettings(updater: (ArtisanSettings) -> ArtisanSettings) {
    viewModelScope.launch {
      repository.updateArtisanSettings(updater)
      _userMessage.emit("تم حفظ إعدادات الحرفيين")
    }
  }

  fun updateHomeServiceSettings(updater: (HomeServiceSettings) -> HomeServiceSettings) {
    viewModelScope.launch {
      repository.updateHomeServiceSettings(updater)
      _userMessage.emit("تم حفظ إعدادات الخدمات المنزلية")
    }
  }
}
