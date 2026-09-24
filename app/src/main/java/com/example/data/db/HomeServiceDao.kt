package com.example.data.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.example.data.model.HomeServiceChatMessage
import com.example.data.model.HomeServiceOrder
import com.example.data.model.ServiceUrgency
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "home_service_messages")
data class HomeServiceMessageEntity(
  @PrimaryKey val id: String,
  val orderId: String,
  val senderName: String,
  val senderRole: String,
  val text: String,
  val timestamp: Long,
  val isFromMe: Boolean,
  val attachmentImageRes: String?
) {
  fun toModel(): HomeServiceChatMessage = HomeServiceChatMessage(
    id = id,
    orderId = orderId,
    senderName = senderName,
    senderRole = senderRole,
    text = text,
    timestamp = timestamp,
    isFromMe = isFromMe,
    attachmentImageRes = attachmentImageRes
  )

  companion object {
    fun fromModel(model: HomeServiceChatMessage): HomeServiceMessageEntity = HomeServiceMessageEntity(
      id = model.id,
      orderId = model.orderId,
      senderName = model.senderName,
      senderRole = model.senderRole,
      text = model.text,
      timestamp = model.timestamp,
      isFromMe = model.isFromMe,
      attachmentImageRes = model.attachmentImageRes
    )
  }
}

@Entity(tableName = "home_service_orders")
data class HomeServiceOrderEntity(
  @PrimaryKey val id: String,
  val serviceCategory: String,
  val issueSubtype: String,
  val urgency: String,
  val scheduledTime: String,
  val address: String,
  val clientName: String,
  val clientPhone: String,
  val notes: String,
  val estimatedCostMin: Int,
  val estimatedCostMax: Int,
  val currency: String,
  val orderStatus: String,
  val assignedTechnicianName: String,
  val assignedTechnicianPhone: String,
  val hasWarranty: Boolean,
  val createdAt: Long
) {
  fun toModel(): HomeServiceOrder = HomeServiceOrder(
    id = id,
    serviceCategory = serviceCategory,
    issueSubtype = issueSubtype,
    urgency = try { ServiceUrgency.valueOf(urgency) } catch (e: Exception) { ServiceUrgency.EMERGENCY_30MIN },
    scheduledTime = scheduledTime,
    address = address,
    clientName = clientName,
    clientPhone = clientPhone,
    notes = notes,
    estimatedCostMin = estimatedCostMin,
    estimatedCostMax = estimatedCostMax,
    currency = currency,
    orderStatus = orderStatus,
    assignedTechnicianName = assignedTechnicianName,
    assignedTechnicianPhone = assignedTechnicianPhone,
    hasWarranty = hasWarranty,
    createdAt = createdAt
  )

  companion object {
    fun fromModel(model: HomeServiceOrder): HomeServiceOrderEntity = HomeServiceOrderEntity(
      id = model.id,
      serviceCategory = model.serviceCategory,
      issueSubtype = model.issueSubtype,
      urgency = model.urgency.name,
      scheduledTime = model.scheduledTime,
      address = model.address,
      clientName = model.clientName,
      clientPhone = model.clientPhone,
      notes = model.notes,
      estimatedCostMin = model.estimatedCostMin,
      estimatedCostMax = model.estimatedCostMax,
      currency = model.currency,
      orderStatus = model.orderStatus,
      assignedTechnicianName = model.assignedTechnicianName,
      assignedTechnicianPhone = model.assignedTechnicianPhone,
      hasWarranty = model.hasWarranty,
      createdAt = model.createdAt
    )
  }
}

@Dao
interface HomeServiceDao {
  @Query("SELECT * FROM home_service_orders ORDER BY createdAt DESC")
  fun getAllOrders(): Flow<List<HomeServiceOrderEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrder(order: HomeServiceOrderEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllOrders(orders: List<HomeServiceOrderEntity>)

  @Query("UPDATE home_service_orders SET orderStatus = :status WHERE id = :id")
  suspend fun updateOrderStatus(id: String, status: String)

  @Query("DELETE FROM home_service_orders WHERE id = :id")
  suspend fun deleteOrder(id: String)

  @Query("SELECT * FROM home_service_messages WHERE orderId = :orderId ORDER BY timestamp ASC")
  fun getMessagesForOrder(orderId: String): Flow<List<HomeServiceMessageEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMessage(message: HomeServiceMessageEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllMessages(messages: List<HomeServiceMessageEntity>)

  @Query("SELECT COUNT(*) FROM home_service_messages WHERE orderId = :orderId")
  suspend fun getMessageCountForOrder(orderId: String): Int
}
