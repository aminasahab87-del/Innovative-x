package com.example.data.firebase

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.example.data.model.Project
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class FirebaseConfigData(
    val apiKey: String = "AIzaSyAIMUnieyGPA9AYAZ9gmQ7XIfxIRc3HvWk",
    val authDomain: String = "innovative-b2606.firebaseapp.com",
    val databaseUrl: String = "https://innovative-b2606-default-rtdb.firebaseio.com/",
    val projectId: String = "innovative-b2606",
    val storageBucket: String = "innovative-b2606.firebasestorage.app",
    val messagingSenderId: String = "955606077154",
    val appId: String = "1:955606077154:android:5f5a43ba7900c9c0b4d8bb",
    val webAppId: String = "1:955606077154:web:5f5a43ba7900c9c0b4d8bb",
    val measurementId: String = "G-B4W66FCEHQ"
)

object FirebaseManager {
    private const val TAG = "FirebaseManager"
    val config = FirebaseConfigData()

    private var analytics: FirebaseAnalytics? = null
    private var database: FirebaseDatabase? = null
    var isInitialized: Boolean = false
        private set

    val databaseReference: DatabaseReference?
        get() = database?.reference

    /**
     * Initializes Firebase App and activates Firebase Realtime Database
     */
    @Synchronized
    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApiKey(config.apiKey)
                    .setApplicationId(config.appId)
                    .setProjectId(config.projectId)
                    .setDatabaseUrl(config.databaseUrl)
                    .setStorageBucket(config.storageBucket)
                    .setGcmSenderId(config.messagingSenderId)
                    .build()

                FirebaseApp.initializeApp(context.applicationContext, options)
                Log.d(TAG, "FirebaseApp initialized for project: ${config.projectId}")
            } else {
                Log.d(TAG, "FirebaseApp already active for project: ${config.projectId}")
            }

            try {
                analytics = FirebaseAnalytics.getInstance(context.applicationContext)
                analytics?.setAnalyticsCollectionEnabled(true)
            } catch (t: Throwable) {
                Log.w(TAG, "FirebaseAnalytics unavailable: ${t.message}")
            }

            try {
                val db = FirebaseDatabase.getInstance(config.databaseUrl)
                try {
                    db.setPersistenceEnabled(true)
                } catch (_: Throwable) {
                    // Ignore if persistence is already configured
                }
                database = db
                Log.d(TAG, "Firebase Realtime Database active at: ${config.databaseUrl}")
            } catch (t: Throwable) {
                Log.w(TAG, "Firebase Realtime Database init warning: ${t.message}")
            }

            isInitialized = true
        } catch (t: Throwable) {
            Log.e(TAG, "Error initializing Firebase: ${t.message}", t)
        }
    }

    /**
     * Realtime Database: Sync or write a project to Realtime Database (/projects/{id})
     */
    fun syncProjectToRealtimeDatabase(project: Project) {
        try {
            val dbRef = database?.getReference("projects")?.child(project.id) ?: return
            val projectMap = hashMapOf<String, Any>(
                "id" to project.id,
                "title" to project.title,
                "category" to project.category,
                "problem" to project.problem,
                "solution" to project.solution,
                "working" to project.working,
                "innovation" to project.innovation,
                "components" to project.components,
                "estimatedCost" to project.estimatedCost,
                "gradeClass" to project.gradeClass,
                "school" to project.school,
                "cityState" to project.cityState,
                "ownerId" to project.ownerId,
                "ownerName" to project.ownerName,
                "status" to project.status,
                "photoUrls" to project.photoUrls,
                "videoUrl" to project.videoUrl,
                "documentUrl" to project.documentUrl,
                "likesCount" to project.likesCount,
                "viewsCount" to project.viewsCount,
                "isFeatured" to project.isFeatured,
                "createdAt" to project.createdAt,
                "updatedAt" to project.updatedAt
            )
            project.reviewerFeedback?.let { projectMap["reviewerFeedback"] = it }
            project.reviewerName?.let { projectMap["reviewerName"] = it }
            project.reviewerId?.let { projectMap["reviewerId"] = it }
            project.privateReviewerNotes?.let { projectMap["privateReviewerNotes"] = it }
            project.badgeAwarded?.let { projectMap["badgeAwarded"] = it }
            project.aiInnovationScore?.let { projectMap["aiInnovationScore"] = it }
            project.aiVerdict?.let { projectMap["aiVerdict"] = it }
            project.aiAnalysis?.let { projectMap["aiAnalysis"] = it }
            project.aiSuggestions?.let { projectMap["aiSuggestions"] = it }

            dbRef.setValue(projectMap).addOnFailureListener { err ->
                Log.w(TAG, "Realtime DB project sync error: ${err.message}")
            }
        } catch (t: Throwable) {
            Log.w(TAG, "Error syncing project to Realtime DB: ${t.message}")
        }
    }

    /**
     * Realtime Database: Update likes count in realtime
     */
    fun incrementLikesInRealtime(projectId: String, newLikesCount: Int) {
        try {
            val likesRef = database?.getReference("projects")?.child(projectId)?.child("likesCount")
            likesRef?.setValue(newLikesCount)
        } catch (t: Throwable) {
            Log.w(TAG, "Error updating likes in Realtime DB: ${t.message}")
        }
    }

    /**
     * Realtime Database: Update review decision / status in realtime
     */
    fun updateProjectStatusInRealtime(projectId: String, newStatus: String) {
        try {
            val projectRef = database?.getReference("projects")?.child(projectId)
            projectRef?.child("status")?.setValue(newStatus)
            projectRef?.child("updatedAt")?.setValue(System.currentTimeMillis())
        } catch (t: Throwable) {
            Log.w(TAG, "Error updating status in Realtime DB: ${t.message}")
        }
    }

    /**
     * Realtime Database: Listen for live changes on a specific project
     */
    fun listenToProjectRealtime(projectId: String, onUpdate: (Map<String, Any?>) -> Unit): ValueEventListener? {
        return try {
            val ref = database?.getReference("projects")?.child(projectId) ?: return null
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val map = snapshot.value as? Map<String, Any?>
                    if (map != null) {
                        onUpdate(map)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Realtime project listener cancelled: ${error.message}")
                }
            }
            ref.addValueEventListener(listener)
            listener
        } catch (t: Throwable) {
            Log.w(TAG, "Failed to attach Realtime listener: ${t.message}")
            null
        }
    }

    /**
     * Realtime Database: Listen to live project updates across the platform
     */
    fun listenToAllProjectsRealtime(onProjectListReceived: (List<Map<String, Any?>>) -> Unit): ValueEventListener? {
        return try {
            val ref = database?.getReference("projects") ?: return null
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Map<String, Any?>>()
                    for (child in snapshot.children) {
                        (child.value as? Map<String, Any?>)?.let { list.add(it) }
                    }
                    onProjectListReceived(list)
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Realtime projects listener cancelled: ${error.message}")
                }
            }
            ref.addValueEventListener(listener)
            listener
        } catch (t: Throwable) {
            Log.w(TAG, "Failed to attach all projects listener: ${t.message}")
            null
        }
    }

    /**
     * Realtime Database: Remove a listener
     */
    fun removeRealtimeListener(path: String, listener: ValueEventListener) {
        try {
            database?.getReference(path)?.removeEventListener(listener)
        } catch (t: Throwable) {
            Log.w(TAG, "Error removing Realtime listener: ${t.message}")
        }
    }

    // Analytics Events
    fun logLogin(userId: String, role: String) {
        try {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.METHOD, "email_password")
                putString("user_id", userId)
                putString("role", role)
            }
            analytics?.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
            analytics?.setUserId(userId)
            analytics?.setUserProperty("role", role)
            Log.d(TAG, "Logged Firebase Analytics login: $userId ($role)")
        } catch (t: Throwable) {
            Log.w(TAG, "Analytics event failed: ${t.message}")
        }
    }

    fun logSignUp(userId: String, school: String, role: String) {
        try {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.METHOD, "registration_form")
                putString("user_id", userId)
                putString("school", school)
                putString("role", role)
            }
            analytics?.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
            analytics?.setUserId(userId)
            analytics?.setUserProperty("role", role)
            Log.d(TAG, "Logged Firebase Analytics sign_up: $userId")
        } catch (t: Throwable) {
            Log.w(TAG, "Analytics event failed: ${t.message}")
        }
    }

    fun logProjectSubmission(projectId: String, title: String, category: String, asDraft: Boolean) {
        try {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, projectId)
                putString(FirebaseAnalytics.Param.ITEM_NAME, title)
                putString(FirebaseAnalytics.Param.ITEM_CATEGORY, category)
                putBoolean("is_draft", asDraft)
            }
            analytics?.logEvent("project_submission", bundle)
            Log.d(TAG, "Logged Firebase Analytics project_submission: $title (draft: $asDraft)")
        } catch (t: Throwable) {
            Log.w(TAG, "Analytics event failed: ${t.message}")
        }
    }

    fun logReviewDecision(projectId: String, decision: String, reviewerId: String) {
        try {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, projectId)
                putString("decision", decision)
                putString("reviewer_id", reviewerId)
            }
            analytics?.logEvent("review_decision", bundle)
            Log.d(TAG, "Logged Firebase Analytics review_decision: $decision for $projectId")
        } catch (t: Throwable) {
            Log.w(TAG, "Analytics event failed: ${t.message}")
        }
    }

    fun logProjectInteraction(projectId: String, title: String, action: String) {
        try {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, projectId)
                putString(FirebaseAnalytics.Param.ITEM_NAME, title)
                putString("action", action)
            }
            analytics?.logEvent("project_interaction", bundle)
        } catch (t: Throwable) {
            Log.w(TAG, "Analytics event failed: ${t.message}")
        }
    }
}
