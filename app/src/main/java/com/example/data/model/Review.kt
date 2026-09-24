package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey
    val id: String,
    val projectId: String,
    val reviewerId: String,
    val reviewerName: String,
    val action: String = "PEER_REVIEW", // "APPROVED", "CHANGES_REQUESTED", "REJECTED", "PUBLISHED", "PEER_REVIEW"
    val studentFeedback: String,
    val privateNotes: String = "",
    val badgeAwarded: String? = null,
    val rating: Int = 5, // 1 to 5 stars
    val reviewerRole: String = "Student Innovator",
    val constructiveTip: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
