package com.example.data.model

import java.util.UUID

enum class ServiceUrgency(val titleAr: String, val subtitleAr: String, val badgeColorHex: Long) {
  EMERGENCY_30MIN("طوارئ عاجلة (30 دقيقة)", "تدخل فوري سريع للحالات الحرجة", 0xFFDC2626),
  SAME_DAY("اليوم (خلال ساعات)", "خدمة في نفس اليوم حسب جدول الفنيين", 0xFFD97706),
  SCHEDULED("حجز موعد مسبق", "تحديد يوم وتوقيت مناسب لراحتك", 0xFF2563EB)
}

data class HomeServiceCategory(
  val id: String,
  val nameAr: String,
  val nameEn: String,
  val iconEmoji: String,
  val basePriceMin: Int,
  val basePriceMax: Int,
  val commonIssues: List<String>
)

data class HomeServiceChatMessage(
  val id: String = UUID.randomUUID().toString(),
  val orderId: String,
  val senderName: String,
  val senderRole: String, // "CLIENT", "TECHNICIAN", "SUPPORT"
  val text: String,
  val timestamp: Long = System.currentTimeMillis(),
  val isFromMe: Boolean,
  val attachmentImageRes: String? = null
)

data class HomeWorkOffer(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val category: String,
  val providerName: String,
  val providerRating: Float = 4.9f,
  val reviewsCount: Int = 84,
  val price: String,
  val originalPrice: String? = null,
  val description: String,
  val location: String,
  val phone: String,
  val badgeText: String = "عرض معتمد",
  val iconEmoji: String = "🛠️"
)

data class HomeServiceOrder(
  val id: String = UUID.randomUUID().toString(),
  val serviceCategory: String,
  val issueSubtype: String,
  val urgency: ServiceUrgency = ServiceUrgency.EMERGENCY_30MIN,
  val scheduledTime: String = "الآن فوراً",
  val address: String,
  val clientName: String,
  val clientPhone: String,
  val notes: String = "",
  val estimatedCostMin: Int = 120,
  val estimatedCostMax: Int = 250,
  val currency: String = "SAR",
  val orderStatus: String = "REQUESTED", // REQUESTED, DISPATCHED, ON_THE_WAY, IN_PROGRESS, COMPLETED
  val assignedTechnicianName: String = "الفني محمد سالم (معتمد)",
  val assignedTechnicianPhone: String = "+966501234567",
  val hasWarranty: Boolean = true,
  val createdAt: Long = System.currentTimeMillis()
)

val PREDEFINED_HOME_SERVICES = listOf(
  HomeServiceCategory(
    id = "electric",
    nameAr = "كهرباء منزلية",
    nameEn = "Home Electricity",
    iconEmoji = "⚡",
    basePriceMin = 100,
    basePriceMax = 280,
    commonIssues = listOf(
      "ماس كهربائي أو انقطاع قاطع رئيسي",
      "تركيب ثريات وإنارة مخفية",
      "صيانة مفاتيح وقواطع وتأريض",
      "تمديدات كهربائية جديدة"
    )
  ),
  HomeServiceCategory(
    id = "plumbing",
    nameAr = "سباكة وصحي",
    nameEn = "Plumbing Services",
    iconEmoji = "🔧",
    basePriceMin = 90,
    basePriceMax = 250,
    commonIssues = listOf(
      "تسريب مياه في الحمام أو المطبخ",
      "تسليك انسداد بالوعات ومجاري",
      "تركيب وتصليح خلاطات ومحابس",
      "صيانة أو تركيب سخان مياه"
    )
  ),
  HomeServiceCategory(
    id = "hvac",
    nameAr = "تكييف وتبريد",
    nameEn = "HVAC & AC Repair",
    iconEmoji = "❄️",
    basePriceMin = 150,
    basePriceMax = 350,
    commonIssues = listOf(
      "المكيف لا يبرد (شحن فريون)",
      "تسريب ماء من الوحدة الداخلية",
      "صيانة دورية وتنظيف فلاتر عميق",
      "صوت مزعج أو عطل في الكومبريسور"
    )
  ),
  HomeServiceCategory(
    id = "cleaning",
    nameAr = "تنظيف شامل وتعقيم",
    nameEn = "Home Cleaning",
    iconEmoji = "✨",
    basePriceMin = 180,
    basePriceMax = 400,
    commonIssues = listOf(
      "تنظيف شقق وفلل بعد التشطيب",
      "غسيل كنب وسجاد بالبخار",
      "تنظيف خزانات مياه وتعقيم",
      "مكافحة حشرات ورش مبيدات"
    )
  ),
  HomeServiceCategory(
    id = "carpentry_locks",
    nameAr = "نجارة وأقفال أبواب",
    nameEn = "Carpentry & Locks",
    iconEmoji = "🚪",
    basePriceMin = 80,
    basePriceMax = 220,
    commonIssues = listOf(
      "فتح وتغيير أقفال أبواب طارئ",
      "تصليح مفصلات ودواليب مطبخ",
      "تركيب وتعديل أبواب خشبية",
      "تجميع وتركيب أثاث جديد"
    )
  ),
  HomeServiceCategory(
    id = "appliances",
    nameAr = "صيانة الأجهزة المنزلية",
    nameEn = "Appliance Repair",
    iconEmoji = "🧺",
    basePriceMin = 120,
    basePriceMax = 300,
    commonIssues = listOf(
      "عطل في الغسالة (لا تصرف أو تدور)",
      "عطل في الثلاجة (تبريد ضعيف)",
      "صيانة فرن وغاز سطحي",
      "صيانة ميكروويف وغسالة أطباق"
    )
  )
)

val PREDEFINED_HOME_OFFERS = listOf(
  HomeWorkOffer(
    id = "offer_ac_cleaning",
    title = "عرض غسيل وصيانة 3 مكيفات سبليت + فحص فريون مجاني",
    category = "تكييف وتبريد",
    providerName = "شركة النسيم للتكييف والتبريد",
    providerRating = 4.9f,
    reviewsCount = 142,
    price = "199 ر.س",
    originalPrice = "300 ر.س",
    description = "تنظيف عميق بالضغط العالي والمواد المعقمة، تسليك مجاري التصريف وفحص ضغط غاز الفريون مع ضمان شهر.",
    location = "الرياض وجميع الأحياء",
    phone = "+966551122334",
    badgeText = "خصم 35%",
    iconEmoji = "❄️"
  ),
  HomeWorkOffer(
    id = "offer_plumbing_inspect",
    title = "كشف تسريبات المياه بجهاز النيتروجين الصوتي وإصلاح فوري",
    category = "سباكة وصحي",
    providerName = "مؤسسة الدرع للسباكة الذكية",
    providerRating = 4.8f,
    reviewsCount = 98,
    price = "150 ر.س",
    originalPrice = "220 ر.س",
    description = "تحديد موقع التسريب بدقة تحت البلاط والجدران دون تكسير، مع تقرير هندسي معتمد لشركة المياه الوطنية.",
    location = "جدة ومكة المكرمة",
    phone = "+966552233445",
    badgeText = "بدون تكسير",
    iconEmoji = "🔧"
  ),
  HomeWorkOffer(
    id = "offer_electrical_panel",
    title = "فحص وتأريض لوحة الكهرباء الرئيسية وتغيير القواطع التالفة",
    category = "كهرباء منزلية",
    providerName = "الفني المهندس للكهرباء المنزلية",
    providerRating = 4.95f,
    reviewsCount = 176,
    price = "120 ر.س",
    originalPrice = "180 ر.س",
    description = "موازنة الأحمال على الفازات الثلاثة، فحص القاطع التفاضلي للحماية من الصعق الكهربائي، وشهادة سلامة.",
    location = "الدمام والخبر",
    phone = "+966553344556",
    badgeText = "فحص شامل",
    iconEmoji = "⚡"
  ),
  HomeWorkOffer(
    id = "offer_deep_cleaning",
    title = "باقة التنظيف والتعقيم الشامل للشقق والفلل مع مكافحة الحشرات",
    category = "تنظيف شامل وتعقيم",
    providerName = "الريان لخدمات النظافة الفندقية",
    providerRating = 4.75f,
    reviewsCount = 89,
    price = "290 ر.س",
    originalPrice = "400 ر.س",
    description = "جلي وتلميع الأرضيات، غسيل الشبابيك والواجهات الداخلية، وتعقيم المطابخ والحمامات بمواد ألمانية.",
    location = "الرياض - تغطية كاملة",
    phone = "+966554455667",
    badgeText = "باقة مميزة",
    iconEmoji = "✨"
  )
)
