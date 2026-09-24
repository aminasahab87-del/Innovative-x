package com.example.data.model

enum class InnovationCategory(val title: String, val iconName: String) {
    AGRICULTURE("Agriculture", "eco"),
    ARTIFICIAL_INTELLIGENCE("Artificial Intelligence", "psychology"),
    ROBOTICS("Robotics", "precision_manufacturing"),
    ELECTRONICS("Electronics", "memory"),
    ENVIRONMENT("Environment", "forest"),
    RENEWABLE_ENERGY("Renewable Energy", "solar_power"),
    SMART_DEVICES("Smart Devices", "devices"),
    HEALTHCARE("Healthcare", "health_and_safety"),
    SPACE_SCIENCE("Space & Science", "rocket_launch"),
    OTHER("Other", "lightbulb");

    companion object {
        fun allTitles(): List<String> = entries.map { it.title }
        fun fromTitle(title: String): InnovationCategory =
            entries.find { it.title.equals(title, ignoreCase = true) } ?: OTHER
    }
}
