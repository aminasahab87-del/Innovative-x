package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PaymentStatus(val statusKey: String, val displayName: String) {
    PENDING("PENDING", "Pending Admin Approval"),
    APPROVED("APPROVED", "Approved & Unlocked"),
    REJECTED("REJECTED", "Rejected");

    companion object {
        fun fromKey(key: String): PaymentStatus =
            entries.find { it.statusKey.equals(key, ignoreCase = true) } ?: PENDING
    }
}

@Entity(tableName = "payment_requests")
data class PaymentRequest(
    @PrimaryKey
    val id: String,
    val userId: String,
    val userName: String,
    val userEmail: String,
    val transactionId: String,
    val amountPkr: Int = 250,
    val status: String = PaymentStatus.PENDING.statusKey, // PENDING, APPROVED, REJECTED
    val adminNotes: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    val statusEnum: PaymentStatus
        get() = PaymentStatus.fromKey(status)
}
