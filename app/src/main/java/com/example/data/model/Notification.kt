package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class NotificationType(val title: String, val iconName: String, val colorHex: Long) {
    SUBMISSION_RECEIVED("Submission Received", "send", 0xFF6366F1),
    PROJECT_UNDER_REVIEW("Under Review", "find_in_page", 0xFF0284C7),
    PROJECT_APPROVED("Project Approved", "verified", 0xFF10B981),
    CHANGES_REQUESTED("Changes Requested", "edit_note", 0xFFF97316),
    PROJECT_REJECTED("Review Decision", "cancel", 0xFFEF4444),
    BADGE_RECEIVED("New Badge Awarded", "workspace_premium", 0xFFF59E0B),
    PROJECT_FEATURED("Project Featured", "auto_awesome", 0xFF8B5CF6),
    AI_REVIEW_COMPLETED("AI Review Completed", "smart_toy", 0xFF8B5CF6),
    SYSTEM("System Notice", "info", 0xFF0284C7);

    companion object {
        fun fromKey(key: String): NotificationType =
            entries.find { it.name.equals(key, ignoreCase = true) } ?: SUBMISSION_RECEIVED
    }
}

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey
    val id: String,
    val userId: String,
    val projectId: String?,
    val projectTitle: String?,
    val type: String,
    val title: String,
    val message: String,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    val notificationType: NotificationType
        get() = NotificationType.fromKey(type)
}
