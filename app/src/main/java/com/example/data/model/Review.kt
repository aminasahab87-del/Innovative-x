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
    val action: String, // "APPROVED", "CHANGES_REQUESTED", "REJECTED", "PUBLISHED"
    val studentFeedback: String,
    val privateNotes: String,
    val badgeAwarded: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
