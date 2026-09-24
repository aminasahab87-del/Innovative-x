package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "live_classes")
data class LiveClassSession(
    @PrimaryKey
    val id: String,
    val title: String,
    val instructorName: String,
    val subject: String,
    val dateTimeText: String, // e.g., "Today, 5:00 PM"
    val zoomMeetingId: String,
    val zoomPassword: String,
    val zoomLink: String,
    val description: String = "",
    val priceText: String = "250 PKR / 10 Days",
    val isLiveNow: Boolean = false,
    val durationMinutes: Int = 60,
    val scheduledTimestamp: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)
