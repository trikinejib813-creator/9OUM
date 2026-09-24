package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.db.ArtisanEntity
import com.example.data.db.ArtisanQuoteEntity
import com.example.data.db.HomeServiceMessageEntity
import com.example.data.db.HomeServiceOrderEntity
import com.example.data.db.JobApplicationEntity
import com.example.data.db.JobOfferEntity
import com.example.data.db.SectionSettingsEntity
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class GoumRepository(private val db: AppDatabase) {

  val allJobs: Flow<List<JobOffer>> = db.jobDao().getAllJobs().map { list ->
    list.map { it.toModel() }
  }

  val myPostedJobs: Flow<List<JobOffer>> = db.jobDao().getMyPostedJobs().map { list ->
    list.map { it.toModel() }
  }

  val myApplications: Flow<List<JobApplicationEntity>> = db.jobDao().getAllApplications()

  fun getApplicationsForJob(jobId: String): Flow<List<JobApplicationEntity>> =
    db.jobDao().getApplicationsForJob(jobId)

  suspend fun postNewJob(job: JobOffer) {
    db.jobDao().insertOrUpdateJob(JobOfferEntity.fromModel(job.copy(isOwner = true)))
  }

  suspend fun updateJobStatus(jobId: String, status: String) {
    db.jobDao().updateJobStatus(jobId, status)
  }

  suspend fun applyToJob(
    jobId: String,
    jobTitle: String,
    company: String,
    name: String,
    email: String,
    phone: String,
    experience: String,
    skills: String,
    note: String
  ) {
    val application = JobApplicationEntity(
      id = UUID.randomUUID().toString(),
      jobId = jobId,
      jobTitle = jobTitle,
      company = company,
      candidateName = name,
      candidateEmail = email,
      candidatePhone = phone,
      candidateExperience = experience,
      candidateSkills = skills,
      coverNote = note,
      appliedAt = System.currentTimeMillis(),
      status = "APPLIED"
    )
    db.jobDao().insertApplication(application)
    db.jobDao().markApplied(jobId, "APPLIED")
  }

  suspend fun updateApplicationStatus(appId: String, status: String) {
    db.jobDao().updateApplicationStatus(appId, status)
  }

  // --- ARTISANS ---
  val allArtisans: Flow<List<Artisan>> = db.artisanDao().getAllArtisans().map { list ->
    list.map { it.toModel() }
  }

  val allQuotes: Flow<List<ArtisanQuoteRequest>> = db.artisanDao().getAllQuotes().map { list ->
    list.map { it.toModel() }
  }

  suspend fun requestQuote(quote: ArtisanQuoteRequest) {
    db.artisanDao().insertQuote(ArtisanQuoteEntity.fromModel(quote))
  }

  suspend fun registerArtisan(artisan: Artisan) {
    db.artisanDao().insertOrUpdateArtisan(ArtisanEntity.fromModel(artisan.copy(isRegisteredByMe = true)))
  }

  suspend fun updateEmergencyAvailability(artisanId: String, available: Boolean) {
    db.artisanDao().updateEmergencyStatus(artisanId, available)
  }

  // --- HOME SERVICES ---
  val allOrders: Flow<List<HomeServiceOrder>> = db.homeServiceDao().getAllOrders().map { list ->
    list.map { it.toModel() }
  }

  suspend fun createHomeServiceOrder(order: HomeServiceOrder) {
    db.homeServiceDao().insertOrder(HomeServiceOrderEntity.fromModel(order))
  }

  suspend fun updateOrderStatus(orderId: String, status: String) {
    db.homeServiceDao().updateOrderStatus(orderId, status)
  }

  suspend fun cancelOrder(orderId: String) {
    db.homeServiceDao().deleteOrder(orderId)
  }

  fun getChatMessages(orderId: String): Flow<List<HomeServiceChatMessage>> =
    db.homeServiceDao().getMessagesForOrder(orderId).map { list ->
      list.map { it.toModel() }
    }

  suspend fun sendChatMessage(message: HomeServiceChatMessage) {
    db.homeServiceDao().insertMessage(HomeServiceMessageEntity.fromModel(message))
  }

  // --- SECTION SETTINGS ---
  val settingsFlow: Flow<GoumAppSettings> = db.settingsDao().getSettings().map { entity ->
    entity?.toModel() ?: GoumAppSettings()
  }

  suspend fun saveAllSettings(settings: GoumAppSettings) {
    db.settingsDao().saveSettings(SectionSettingsEntity.fromModel(settings))
  }

  suspend fun updateJobSeekerSettings(updater: (JobSeekerSettings) -> JobSeekerSettings) {
    val current = db.settingsDao().getSettingsOnce()?.toModel() ?: GoumAppSettings()
    val updated = current.copy(jobSeekerSettings = updater(current.jobSeekerSettings))
    db.settingsDao().saveSettings(SectionSettingsEntity.fromModel(updated))
  }

  suspend fun updateEmployerSettings(updater: (EmployerSettings) -> EmployerSettings) {
    val current = db.settingsDao().getSettingsOnce()?.toModel() ?: GoumAppSettings()
    val updated = current.copy(employerSettings = updater(current.employerSettings))
    db.settingsDao().saveSettings(SectionSettingsEntity.fromModel(updated))
  }

  suspend fun updateArtisanSettings(updater: (ArtisanSettings) -> ArtisanSettings) {
    val current = db.settingsDao().getSettingsOnce()?.toModel() ?: GoumAppSettings()
    val updated = current.copy(artisanSettings = updater(current.artisanSettings))
    db.settingsDao().saveSettings(SectionSettingsEntity.fromModel(updated))
  }

  suspend fun updateHomeServiceSettings(updater: (HomeServiceSettings) -> HomeServiceSettings) {
    val current = db.settingsDao().getSettingsOnce()?.toModel() ?: GoumAppSettings()
    val updated = current.copy(homeServiceSettings = updater(current.homeServiceSettings))
    db.settingsDao().saveSettings(SectionSettingsEntity.fromModel(updated))
  }

  // --- SEED INITIAL DATA IF EMPTY ---
  suspend fun seedInitialDataIfNeeded() {
    val currentSettings = db.settingsDao().getSettingsOnce()
    if (currentSettings == null) {
      db.settingsDao().saveSettings(SectionSettingsEntity())
    }

    val currentUser = db.userDao().getUserProfileOnce()
    if (currentUser == null) {
      val defaultUser = com.example.data.db.UserProfileEntity(
        id = 1,
        encryptedEmail = "demo.user@9goum.sa",
        countryCode = "SA",
        countryName = "المملكة العربية السعودية",
        registeredAt = System.currentTimeMillis(),
        lastSyncAt = System.currentTimeMillis(),
        isSynced = true
      )
      db.userDao().saveUserProfile(defaultUser)
    }

    val sampleJobs = listOf(
      JobOffer(
        title = "مطور تطبيقات Android و Flutter",
        company = "شركة النجم الرقمي المتقدم",
        location = "الرياض - حي الياسمين (متاح عن بُعد)",
        category = "تكنولوجيا ومعلومات",
        contractType = JobContractType.REMOTE,
        experienceLevel = "متوسط (3-5 سنوات)",
        salaryMin = 8000,
        salaryMax = 14000,
        currency = "SAR",
        description = "نبحث عن مطور تطبيقات خبير في بناء تطبيقات Jetpack Compose و Kotlin مع ربط واجهات API وFirebase وقواعد البيانات المحلية.",
        requirements = "خبرة موثوقة في Kotlin وCompose، فهم ممتاز للـ Clean Architecture و MVVM.",
        isRemote = true,
        contactPhone = "+966501122334",
        contactEmail = "careers@digistar.sa"
      ),
      JobOffer(
        title = "محاسب مالي وإداري معتمد",
        company = "مجموعة الأفق للاستشارات والتدقيق",
        location = "جدة - طريق الملك عبد العزيز",
        category = "محاسبة ومالية",
        contractType = JobContractType.FULL_TIME,
        experienceLevel = "خبير (+5 سنوات)",
        salaryMin = 6500,
        salaryMax = 9500,
        currency = "SAR",
        description = "إعداد التقارير المالية الدورية، تدقيق الحسابات، إدارة الضرائب والفوترة الإلكترونية، وإقفال الميزانيات الشهرية.",
        requirements = "بكالوريوس محاسبة، إتقان برامج ERP والمحاسبة السحابية، وشهادة SOCPA ميزة إضافية.",
        isRemote = false,
        contactPhone = "+966504455667",
        contactEmail = "hr@alofooq.sa"
      ),
      JobOffer(
        title = "مدير مبيعات وتسويق ميداني",
        company = "شركة البناء الحديث للتوريدات",
        location = "الدمام - المنطقة الشرقية",
        category = "تسويق ومبيعات",
        contractType = JobContractType.FULL_TIME,
        experienceLevel = "متوسط (2-4 سنوات)",
        salaryMin = 5500,
        salaryMax = 9000,
        currency = "SAR",
        description = "توسيع قاعدة العملاء، إدارة الصفقات مع المقاولين ومؤسسات البناء، وقيادة فريق المبيعات لتحقيق المستهدفات.",
        requirements = "رخصة قيادة سارية، مهارات إقناع وتفاوض متميزة، ومعرفة بقطاع مواد البناء والمقاولات.",
        isRemote = false,
        contactPhone = "+966506677889",
        contactEmail = "sales@modernbuild.sa"
      ),
      JobOffer(
        title = "مهندس مدني وإشراف مواقع",
        company = "دار الإعمار للهندسة والاستشارات",
        location = "المدينة المنورة ومكة المكرمة",
        category = "هندسة وتشييد",
        contractType = JobContractType.CONTRACT,
        experienceLevel = "متوسط (3-6 سنوات)",
        salaryMin = 7500,
        salaryMax = 12000,
        currency = "SAR",
        description = "إشراف هندسي ميداني على مشاريع الأبنية السكنية والتجارية، ومطابقة المواصفات الإنشائية واشتراطات الكود السعودي.",
        requirements = "شهادة هندسة مدنية معتمدة من هيئة المهندسين، خبرة في إدارة المخططات وبرامج الأوتوكاد.",
        isRemote = false,
        contactPhone = "+966508899001",
        contactEmail = "projects@daraleamar.sa"
      ),
      JobOffer(
        title = "أخصائي دعم فني وخدمة عملاء",
        company = "منصة تواصل كلاود",
        location = "عن بُعد (العمل من المنزل)",
        category = "خدمات عملاء واستقبال",
        contractType = JobContractType.PART_TIME,
        experienceLevel = "مبتدئ (1-2 سنة)",
        salaryMin = 3500,
        salaryMax = 5000,
        currency = "SAR",
        description = "الرد على استفسارات المشتركين عبر المحادثات الفورية والبريد، وحل مشاكل الحسابات التقنية بسرعة واحترافية.",
        requirements = "لباقة في الحديث، سرعة الكتابة، والقدرة على العمل في نوبات مرنة.",
        isRemote = true,
        contactPhone = "+966502233445",
        contactEmail = "support-jobs@tawasol.io"
      )
    )

    db.jobDao().insertAllJobs(sampleJobs.map { JobOfferEntity.fromModel(it) })

    val sampleArtisans = listOf(
      Artisan(
        name = "المعلم صالح النجار",
        trade = "نجارة وأثاث",
        city = "الرياض - شمال وشرق",
        rating = 4.9f,
        reviewsCount = 142,
        experienceYears = 14,
        hourlyRate = 80,
        dailyRate = 380,
        phone = "+966551234560",
        whatsapp = "+966551234560",
        bio = "متخصص في تفصيل وصيانة غرف النوم، مطابخ الألومنيوم والخشب، تركيب الأبواب والمفصلات الأوروبية، وإصلاح الكراسي والأثاث التالف.",
        portfolioHighlights = listOf("تفصيل دواليب مخفية", "ترميم أبواب خشبية أصلية", "تركيب غرف نوم إيطالية"),
        isVerified = true,
        isEmergencyAvailable = true,
        coverageRadiusKm = 35
      ),
      Artisan(
        name = "الأسطى رضوان للحدادة واللحام",
        trade = "حدادة ولحام",
        city = "جدة - حي الصفا والمروة",
        rating = 4.8f,
        reviewsCount = 98,
        experienceYears = 11,
        hourlyRate = 75,
        dailyRate = 350,
        phone = "+966552345671",
        whatsapp = "+966552345671",
        bio = "تصنيع وتركيب مظلات السيارات، دربزينات الاستيل والحديد المشغول، أبواب ليزر حديثة، ولحام وصيانة الهياكل المعدنية بأعلى متانة.",
        portfolioHighlights = listOf("مظلات وسواتر حديثة", "دربزين درج مودرن ليزر", "أبواب مداخل فلل"),
        isVerified = true,
        isEmergencyAvailable = true,
        coverageRadiusKm = 25
      ),
      Artisan(
        name = "فارس الدهانات والديكور العصري",
        trade = "دهانات وديكور",
        city = "الدمام والخبر",
        rating = 4.95f,
        reviewsCount = 176,
        experienceYears = 9,
        hourlyRate = 65,
        dailyRate = 320,
        phone = "+966553456782",
        whatsapp = "+966553456782",
        bio = "تنفيذ كافة أنواع الدهانات الداخلية والخارجية، ورق الجدران، بديل الرخام وبديل الخشب، ومعالجة الرطوبة والتشققات بضمان 5 سنوات.",
        portfolioHighlights = listOf("تشطيب فيلا مودرن جوتن", "تركيب بديل الرخام مع الإضاءة", "دهانات أوف وايت ومط"),
        isVerified = true,
        isEmergencyAvailable = false,
        coverageRadiusKm = 40
      ),
      Artisan(
        name = "المهندس عادل للألمنيوم والزجاج",
        trade = "ألومنيوم وزجاج",
        city = "مكة المكرمة",
        rating = 4.7f,
        reviewsCount = 64,
        experienceYears = 12,
        hourlyRate = 70,
        dailyRate = 340,
        phone = "+966554567893",
        whatsapp = "+966554567893",
        bio = "تركيب شبابيك دبل جلاس عازلة للصوت والحرارة، قطاعات سيكوريت، واجهات زجاجية للمحلات والمكاتب، وصيانة عاجلة للزجاج المكسور.",
        portfolioHighlights = listOf("شبابيك دبل جلاس عازلة", "شاورات حمام زجاج سيكوريت", "واجهات تجارية"),
        isVerified = true,
        isEmergencyAvailable = true,
        coverageRadiusKm = 30
      ),
      Artisan(
        name = "الأسطى ناصر للبناء والسيراميك",
        trade = "بناء وسيراميك",
        city = "الرياض - وسط وجنوب",
        rating = 4.85f,
        reviewsCount = 115,
        experienceYears = 16,
        hourlyRate = 85,
        dailyRate = 400,
        phone = "+966555678904",
        whatsapp = "+966555678904",
        bio = "تركيب بورسلان وسيراميك أرضيات وجدران بدقة ليزرية، بناء ملاحق وترميمات شاملة، وعزل حمامات وأسطح بأحدث المواد الإيبوكسية.",
        portfolioHighlights = listOf("أرضيات بورسلان ليزر 120x60", "عزل حمامات وأسطح", "بناء ملحق خارجي"),
        isVerified = true,
        isEmergencyAvailable = false,
        coverageRadiusKm = 50
      )
    )

    db.artisanDao().insertAllArtisans(sampleArtisans.map { ArtisanEntity.fromModel(it) })

    val sampleOrders = listOf(
      HomeServiceOrder(
        id = "order_electric_001",
        serviceCategory = "كهرباء منزلية",
        issueSubtype = "ماس كهربائي وانقطاع القاطع الرئيسي للفيلا",
        urgency = ServiceUrgency.EMERGENCY_30MIN,
        scheduledTime = "طوارئ فورية خلال 30 دقيقة",
        address = "حي النرجس، شارع رقم 42",
        clientName = "عبدالرحمن الشمري",
        clientPhone = "+966501199223",
        notes = "رائحة احتراق خفيفة قرب صندوق القواطع، يرجى إحضار قواطع بديلة أصلية.",
        estimatedCostMin = 150,
        estimatedCostMax = 280,
        orderStatus = "ON_THE_WAY",
        assignedTechnicianName = "الفني حسام الدين (فني معتمد)",
        assignedTechnicianPhone = "+966550011223"
      ),
      HomeServiceOrder(
        id = "order_plumbing_002",
        serviceCategory = "سباكة وصحي",
        issueSubtype = "تسريب مياه قوي تحت حوض المطبخ",
        urgency = ServiceUrgency.SAME_DAY,
        scheduledTime = "اليوم - الساعة 4:00 عصراً",
        address = "حي العقيق، مجمع واحة الرياض",
        clientName = "سارة التميمي",
        clientPhone = "+966504488331",
        notes = "التسريب يزداد عند فتح محبس المياه الساخنة.",
        estimatedCostMin = 90,
        estimatedCostMax = 180,
        orderStatus = "DISPATCHED",
        assignedTechnicianName = "الفني كمال السباك",
        assignedTechnicianPhone = "+966553322110"
      )
    )

    db.homeServiceDao().insertAllOrders(sampleOrders.map { HomeServiceOrderEntity.fromModel(it) })

    val sampleMessages = listOf(
      HomeServiceChatMessage(
        id = "msg_elec_1",
        orderId = "order_electric_001",
        senderName = "الفني حسام الدين",
        senderRole = "TECHNICIAN",
        text = "السلام عليكم ورحمة الله، تم استلام طلب الطوارئ وأنا في طريقي إليك الآن بحي النرجس.",
        timestamp = System.currentTimeMillis() - 15 * 60 * 1000,
        isFromMe = false
      ),
      HomeServiceChatMessage(
        id = "msg_elec_2",
        orderId = "order_electric_001",
        senderName = "عبدالرحمن الشمري",
        senderRole = "CLIENT",
        text = "وعليكم السلام، مرحباً فني حسام. القاطع الرئيسي ينفصل فوراً، هل أفصل الأجهزة الكهربائية؟",
        timestamp = System.currentTimeMillis() - 12 * 60 * 1000,
        isFromMe = true
      ),
      HomeServiceChatMessage(
        id = "msg_elec_3",
        orderId = "order_electric_001",
        senderName = "الفني حسام الدين",
        senderRole = "TECHNICIAN",
        text = "نعم فضلاً، يفضل فصل الأجهزة الثقيلة كالمكيفات والغسالة احتياطياً. سأصل خلال 10 دقائق ومعي جهاز فحص العزل وقواطع Schneider أصلية.",
        timestamp = System.currentTimeMillis() - 8 * 60 * 1000,
        isFromMe = false
      ),
      HomeServiceChatMessage(
        id = "msg_plumb_1",
        orderId = "order_plumbing_002",
        senderName = "الفني كمال السباك",
        senderRole = "TECHNICIAN",
        text = "أهلاً بك، بخصوص موعد صيانة التسريب، هل يمكن إرسال صورة للمحبس أسفل الحوض للتأكد من المقاس؟",
        timestamp = System.currentTimeMillis() - 40 * 60 * 1000,
        isFromMe = false
      ),
      HomeServiceChatMessage(
        id = "msg_plumb_2",
        orderId = "order_plumbing_002",
        senderName = "سارة التميمي",
        senderRole = "CLIENT",
        text = "أهلاً بك، تم إغلاق محبس التغذية الرئيسي والتسريب توقف حالياً بانتظار وصولك.",
        timestamp = System.currentTimeMillis() - 25 * 60 * 1000,
        isFromMe = true
      )
    )

    db.homeServiceDao().insertAllMessages(sampleMessages.map { HomeServiceMessageEntity.fromModel(it) })
  }
}
