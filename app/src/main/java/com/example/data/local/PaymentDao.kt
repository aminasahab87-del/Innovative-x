package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PaymentRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payment_requests WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    fun getLatestPaymentForUserFlow(userId: String): Flow<PaymentRequest?>

    @Query("SELECT * FROM payment_requests WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestPaymentForUserDirect(userId: String): PaymentRequest?

    @Query("SELECT * FROM payment_requests ORDER BY timestamp DESC")
    fun getAllPaymentRequestsFlow(): Flow<List<PaymentRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentRequest(request: PaymentRequest)

    @Update
    suspend fun updatePaymentRequest(request: PaymentRequest)

    @Query("UPDATE payment_requests SET status = :status, adminNotes = :adminNotes WHERE id = :requestId")
    suspend fun updateStatus(requestId: String, status: String, adminNotes: String)
}
