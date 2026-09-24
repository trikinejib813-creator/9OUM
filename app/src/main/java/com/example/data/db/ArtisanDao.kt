package com.example.data.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.example.data.model.Artisan
import com.example.data.model.ArtisanQuoteRequest
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "artisans")
data class ArtisanEntity(
  @PrimaryKey val id: String,
  val name: String,
  val trade: String,
  val city: String,
  val rating: Float,
  val reviewsCount: Int,
  val experienceYears: Int,
  val hourlyRate: Int,
  val dailyRate: Int,
  val currency: String,
  val phone: String,
  val whatsapp: String,
  val bio: String,
  val highlightsJoined: String,
  val isVerified: Boolean,
  val isEmergencyAvailable: Boolean,
  val isRegisteredByMe: Boolean,
  val coverageRadiusKm: Int
) {
  fun toModel(): Artisan = Artisan(
    id = id,
    name = name,
    trade = trade,
    city = city,
    rating = rating,
    reviewsCount = reviewsCount,
    experienceYears = experienceYears,
    hourlyRate = hourlyRate,
    dailyRate = dailyRate,
    currency = currency,
    phone = phone,
    whatsapp = whatsapp,
    bio = bio,
    portfolioHighlights = if (highlightsJoined.isBlank()) emptyList() else highlightsJoined.split("||"),
    isVerified = isVerified,
    isEmergencyAvailable = isEmergencyAvailable,
    isRegisteredByMe = isRegisteredByMe,
    coverageRadiusKm = coverageRadiusKm
  )

  companion object {
    fun fromModel(model: Artisan): ArtisanEntity = ArtisanEntity(
      id = model.id,
      name = model.name,
      trade = model.trade,
      city = model.city,
      rating = model.rating,
      reviewsCount = model.reviewsCount,
      experienceYears = model.experienceYears,
      hourlyRate = model.hourlyRate,
      dailyRate = model.dailyRate,
      currency = model.currency,
      phone = model.phone,
      whatsapp = model.whatsapp,
      bio = model.bio,
      highlightsJoined = model.portfolioHighlights.joinToString("||"),
      isVerified = model.isVerified,
      isEmergencyAvailable = model.isEmergencyAvailable,
      isRegisteredByMe = model.isRegisteredByMe,
      coverageRadiusKm = model.coverageRadiusKm
    )
  }
}

@Entity(tableName = "artisan_quotes")
data class ArtisanQuoteEntity(
  @PrimaryKey val id: String,
  val artisanId: String,
  val artisanName: String,
  val trade: String,
  val clientName: String,
  val clientPhone: String,
  val projectTitle: String,
  val description: String,
  val estimatedBudget: String,
  val requestedDate: Long,
  val status: String
) {
  fun toModel(): ArtisanQuoteRequest = ArtisanQuoteRequest(
    id = id,
    artisanId = artisanId,
    artisanName = artisanName,
    trade = trade,
    clientName = clientName,
    clientPhone = clientPhone,
    projectTitle = projectTitle,
    description = description,
    estimatedBudget = estimatedBudget,
    requestedDate = requestedDate,
    status = status
  )

  companion object {
    fun fromModel(model: ArtisanQuoteRequest): ArtisanQuoteEntity = ArtisanQuoteEntity(
      id = model.id,
      artisanId = model.artisanId,
      artisanName = model.artisanName,
      trade = model.trade,
      clientName = model.clientName,
      clientPhone = model.clientPhone,
      projectTitle = model.projectTitle,
      description = model.description,
      estimatedBudget = model.estimatedBudget,
      requestedDate = model.requestedDate,
      status = model.status
    )
  }
}

@Dao
interface ArtisanDao {
  @Query("SELECT * FROM artisans ORDER BY rating DESC, reviewsCount DESC")
  fun getAllArtisans(): Flow<List<ArtisanEntity>>

  @Query("SELECT * FROM artisans WHERE isEmergencyAvailable = 1")
  fun getEmergencyArtisans(): Flow<List<ArtisanEntity>>

  @Query("SELECT * FROM artisans WHERE isRegisteredByMe = 1")
  fun getMyArtisanProfile(): Flow<List<ArtisanEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateArtisan(artisan: ArtisanEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllArtisans(artisans: List<ArtisanEntity>)

  @Query("UPDATE artisans SET isEmergencyAvailable = :available WHERE id = :id")
  suspend fun updateEmergencyStatus(id: String, available: Boolean)

  @Query("SELECT * FROM artisan_quotes ORDER BY requestedDate DESC")
  fun getAllQuotes(): Flow<List<ArtisanQuoteEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertQuote(quote: ArtisanQuoteEntity)

  @Query("UPDATE artisan_quotes SET status = :status WHERE id = :id")
  suspend fun updateQuoteStatus(id: String, status: String)
}
