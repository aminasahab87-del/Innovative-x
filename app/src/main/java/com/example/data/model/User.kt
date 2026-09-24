package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole(val roleKey: String, val displayName: String) {
    STUDENT("student", "Student"),
    ADMIN("admin", "Admin / Reviewer");

    companion object {
        fun fromKey(key: String): UserRole =
            entries.find { it.roleKey.equals(key, ignoreCase = true) } ?: STUDENT
    }
}

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String,
    val email: String,
    val passwordHash: String = "",
    val name: String,
    val role: String = UserRole.STUDENT.roleKey,
    val school: String = "",
    val gradeClass: String = "",
    val city: String = "",
    val state: String = "",
    val bio: String = "",
    val avatarUrl: String = "",
    val isPublicInfoVisible: Boolean = true,
    val firebaseUid: String? = null,
    val authProvider: String = "local", // "firebase_password", "google", "local"
    val createdAt: Long = System.currentTimeMillis()
) {
    val isAdmin: Boolean
        get() = role.equals(UserRole.ADMIN.roleKey, ignoreCase = true)

    val roleEnum: UserRole
        get() = UserRole.fromKey(role)
}
