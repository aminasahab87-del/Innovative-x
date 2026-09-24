package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ProjectStatus(val displayName: String, val colorHex: Long) {
    DRAFT("Draft", 0xFF64748B),
    PENDING_REVIEW("Pending Review", 0xFFF59E0B),
    UNDER_REVIEW("Under Review", 0xFF0284C7),
    APPROVED("Approved", 0xFF10B981),
    CHANGES_REQUESTED("Changes Requested", 0xFFF97316),
    REJECTED("Rejected", 0xFFEF4444),
    PUBLISHED("Published", 0xFF6366F1);

    companion object {
        fun fromKey(key: String?): ProjectStatus {
            if (key == null) return DRAFT
            return entries.find { it.name.equals(key, ignoreCase = true) || it.displayName.equals(key, ignoreCase = true) }
                ?: DRAFT
        }
    }
}

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey
    val id: String,
    val ownerId: String,
    val ownerName: String,
    val school: String,
    val gradeClass: String,
    val cityState: String,
    val title: String,
    val category: String,
    val problem: String,
    val solution: String,
    val working: String,
    val innovation: String,
    val components: String,
    val estimatedCost: String,
    val photoUrls: List<String> = emptyList(),
    val videoUrl: String = "",
    val documentUrl: String = "",
    val status: String = ProjectStatus.PENDING_REVIEW.name,
    val isFeatured: Boolean = false,
    val reviewerId: String? = null,
    val reviewerName: String? = null,
    val reviewerFeedback: String? = null,
    val privateReviewerNotes: String? = null,
    val badgeAwarded: String? = null,
    val viewsCount: Int = 0,
    val likesCount: Int = 0,
    val aiInnovationScore: Int? = null,
    val aiVerdict: String? = null,
    val aiAnalysis: String? = null,
    val aiSuggestions: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val publishedAt: Long? = null
) {
    val projectStatus: ProjectStatus
        get() = ProjectStatus.fromKey(status)

    val isPubliclyVisible: Boolean
        get() = status == ProjectStatus.PUBLISHED.name || status == ProjectStatus.APPROVED.name
}
