package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment_config")
data class PaymentConfig(
    @PrimaryKey
    val id: String = "default_payment_config",
    val scannerImageUri: String? = null,
    val accountTitle: String = "InnovateX Academy",
    val accountNumber: String = "0300-1234567",
    val paymentMethod: String = "EasyPaisa / JazzCash",
    val feeAmountPkr: Int = 250,
    val validityDays: Int = 10,
    val updatedTimestamp: Long = System.currentTimeMillis()
)
