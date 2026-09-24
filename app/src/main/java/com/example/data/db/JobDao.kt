package com.example.data.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.JobContractType
import com.example.data.model.JobOffer
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "jobs")
data class JobOfferEntity(
  @PrimaryKey val id: String,
  val title: String,
  val company: String,
  val location: String,
  val category: String,
  val contractType: String,
  val experienceLevel: String,
  val salaryMin: Int,
  val salaryMax: Int,
  val currency: String,
  val description: String,
  val requirements: String,
  val isRemote: Boolean,
  val postedAt: Long,
  val isApplied: Boolean,
  val applicationStatus: String,
  val status: String,
  val isOwner: Boolean,
  val applicantsCount: Int,
  val contactPhone: String,
  val contactEmail: String
) {
  fun toModel(): JobOffer = JobOffer(
    id = id,
    title = title,
    company = company,
    location = location,
    category = category,
    contractType = try { JobContractType.valueOf(contractType) } catch (e: Exception) { JobContractType.FULL_TIME },
    experienceLevel = experienceLevel,
    salaryMin = salaryMin,
    salaryMax = salaryMax,
    currency = currency,
    description = description,
    requirements = requirements,
    isRemote = isRemote,
    postedAt = postedAt,
    isApplied = isApplied,
    applicationStatus = applicationStatus,
    status = status,
    isOwner = isOwner,
    applicantsCount = applicantsCount,
    contactPhone = contactPhone,
    contactEmail = contactEmail
  )

  companion object {
    fun fromModel(model: JobOffer): JobOfferEntity = JobOfferEntity(
      id = model.id,
      title = model.title,
      company = model.company,
      location = model.location,
      category = model.category,
      contractType = model.contractType.name,
      experienceLevel = model.experienceLevel,
      salaryMin = model.salaryMin,
      salaryMax = model.salaryMax,
      currency = model.currency,
      description = model.description,
      requirements = model.requirements,
      isRemote = model.isRemote,
      postedAt = model.postedAt,
      isApplied = model.isApplied,
      applicationStatus = model.applicationStatus,
      status = model.status,
      isOwner = model.isOwner,
      applicantsCount = model.applicantsCount,
      contactPhone = model.contactPhone,
      contactEmail = model.contactEmail
    )
  }
}

@Entity(tableName = "job_applications")
data class JobApplicationEntity(
  @PrimaryKey val id: String,
  val jobId: String,
  val jobTitle: String,
  val company: String,
  val candidateName: String,
  val candidateEmail: String,
  val candidatePhone: String,
  val candidateExperience: String,
  val candidateSkills: String,
  val coverNote: String,
  val appliedAt: Long,
  val status: String
)

@Dao
interface JobDao {
  @Query("SELECT * FROM jobs ORDER BY postedAt DESC")
  fun getAllJobs(): Flow<List<JobOfferEntity>>

  @Query("SELECT * FROM jobs WHERE isOwner = 1 ORDER BY postedAt DESC")
  fun getMyPostedJobs(): Flow<List<JobOfferEntity>>

  @Query("SELECT * FROM jobs WHERE isApplied = 1 ORDER BY postedAt DESC")
  fun getAppliedJobs(): Flow<List<JobOfferEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateJob(job: JobOfferEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllJobs(jobs: List<JobOfferEntity>)

  @Query("UPDATE jobs SET isApplied = 1, applicationStatus = :status, applicantsCount = applicantsCount + 1 WHERE id = :jobId")
  suspend fun markApplied(jobId: String, status: String = "APPLIED")

  @Query("UPDATE jobs SET status = :status WHERE id = :jobId")
  suspend fun updateJobStatus(jobId: String, status: String)

  @Query("DELETE FROM jobs WHERE id = :jobId")
  suspend fun deleteJob(jobId: String)

  @Query("SELECT * FROM job_applications ORDER BY appliedAt DESC")
  fun getAllApplications(): Flow<List<JobApplicationEntity>>

  @Query("SELECT * FROM job_applications WHERE jobId = :jobId ORDER BY appliedAt DESC")
  fun getApplicationsForJob(jobId: String): Flow<List<JobApplicationEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertApplication(app: JobApplicationEntity)

  @Query("UPDATE job_applications SET status = :status WHERE id = :appId")
  suspend fun updateApplicationStatus(appId: String, status: String)
}
