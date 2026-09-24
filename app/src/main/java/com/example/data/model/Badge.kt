package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class InnovationBadge(
    val title: String,
    val description: String,
    val primaryColorHex: Long
) {
    INNOVATION_AWARD("Innovation Award", "Highest recognized excellence in scientific methodology", 0xFF6366F1),
    CREATIVE_IDEA("Creative Idea", "Out-of-the-box thinking and ingenious problem formulation", 0xFFEC4899),
    GREEN_INNOVATION("Green Innovation", "Outstanding commitment to environmental sustainability", 0xFF10B981),
    TECHNOLOGY_INNOVATION("Technology Innovation", "Cutting-edge hardware, sensor, or algorithmic innovation", 0xFF0EA5E9),
    YOUNG_INNOVATOR("Young Innovator", "Exceptional promise and execution by a rising student researcher", 0xFFF59E0B),
    FEATURED_INNOVATION("Featured Innovation", "Curated front-page spotlight chosen by the editorial board", 0xFF8B5CF6);

    companion object {
        fun fromTitle(title: String?): InnovationBadge? {
            if (title.isNullOrBlank()) return null
            return entries.find { it.title.equals(title, ignoreCase = true) }
        }
    }
}
