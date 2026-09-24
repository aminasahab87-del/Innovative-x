package com.example.data.repository

import android.content.Context
import com.example.data.firebase.FirebaseManager
import com.example.data.local.AppDatabase
import com.example.data.local.InitialData
import com.example.data.model.InnovationBadge
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.data.model.Project
import com.example.data.model.ProjectStatus
import com.example.data.model.Review
import com.example.data.model.User
import com.example.data.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class InnovateXRepository(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val userDao = database.userDao()
    private val projectDao = database.projectDao()
    private val notificationDao = database.notificationDao()
    private val reviewDao = database.reviewDao()
    private val liveClassDao = database.liveClassDao()
    private val paymentDao = database.paymentDao()
    private val aiService = com.example.data.ai.AiInnovationService()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isClassesUnlocked = MutableStateFlow<Boolean>(false)
    val isClassesUnlocked: StateFlow<Boolean> = _isClassesUnlocked.asStateFlow()

    val allLiveClasses: Flow<List<com.example.data.model.LiveClassSession>> = liveClassDao.getAllClassesFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                FirebaseManager.initialize(context)
                // Ensure initial seed data is populated if database is fresh
                val existingStudent = userDao.getUserByEmail("student@innovatex.edu")
                if (existingStudent == null) {
                    InitialData.seedDatabase(database)
                }
                // Automatic login removed: student must explicitly enter login or signup credentials to log in.
                _currentUser.value = null

                // Activate realtime sync listener from Firebase Realtime Database
                FirebaseManager.listenToAllProjectsRealtime { liveProjects ->
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            for (item in liveProjects) {
                                val id = item["id"] as? String ?: continue
                                val title = item["title"] as? String ?: continue
                                val existing = projectDao.getProjectDirect(id)
                                if (existing != null) {
                                    val status = item["status"] as? String ?: existing.status
                                    val likes = (item["likesCount"] as? Number)?.toInt() ?: existing.likesCount
                                    val views = (item["viewsCount"] as? Number)?.toInt() ?: existing.viewsCount
                                    val reviewerFeedback = item["reviewerFeedback"] as? String ?: existing.reviewerFeedback
                                    val reviewerName = item["reviewerName"] as? String ?: existing.reviewerName
                                    val reviewerId = item["reviewerId"] as? String ?: existing.reviewerId
                                    val privateNotes = item["privateReviewerNotes"] as? String ?: existing.privateReviewerNotes
                                    val badge = item["badgeAwarded"] as? String ?: existing.badgeAwarded
                                    val isFeatured = (item["isFeatured"] as? Boolean) ?: existing.isFeatured
                                    val aiScore = (item["aiInnovationScore"] as? Number)?.toInt() ?: existing.aiInnovationScore
                                    val aiVerdict = item["aiVerdict"] as? String ?: existing.aiVerdict
                                    val aiAnalysis = item["aiAnalysis"] as? String ?: existing.aiAnalysis
                                    val aiSuggestions = item["aiSuggestions"] as? String ?: existing.aiSuggestions
                                    val updatedAt = (item["updatedAt"] as? Number)?.toLong() ?: existing.updatedAt

                                    val updated = existing.copy(
                                        title = title,
                                        status = status,
                                        likesCount = likes,
                                        viewsCount = views,
                                        reviewerFeedback = reviewerFeedback,
                                        reviewerName = reviewerName,
                                        reviewerId = reviewerId,
                                        privateReviewerNotes = privateNotes,
                                        badgeAwarded = badge,
                                        isFeatured = isFeatured,
                                        aiInnovationScore = aiScore,
                                        aiVerdict = aiVerdict,
                                        aiAnalysis = aiAnalysis,
                                        aiSuggestions = aiSuggestions,
                                        updatedAt = updatedAt
                                    )
                                    if (updated != existing) {
                                        projectDao.updateProject(updated)
                                    }
                                } else {
                                    // Live new project submitted by a student
                                    val newProject = Project(
                                        id = id,
                                        ownerId = item["ownerId"] as? String ?: "student_user",
                                        ownerName = item["ownerName"] as? String ?: "Student Innovator",
                                        school = item["school"] as? String ?: "School of Innovation",
                                        gradeClass = item["gradeClass"] as? String ?: "Grade 10",
                                        cityState = item["cityState"] as? String ?: "",
                                        title = title,
                                        category = item["category"] as? String ?: "Technology",
                                        problem = item["problem"] as? String ?: "",
                                        solution = item["solution"] as? String ?: "",
                                        working = item["working"] as? String ?: "",
                                        innovation = item["innovation"] as? String ?: "",
                                        components = item["components"] as? String ?: "",
                                        estimatedCost = item["estimatedCost"] as? String ?: "",
                                        photoUrls = (item["photoUrls"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                                        videoUrl = item["videoUrl"] as? String ?: "",
                                        documentUrl = item["documentUrl"] as? String ?: "",
                                        status = item["status"] as? String ?: ProjectStatus.PENDING_REVIEW.name,
                                        isFeatured = (item["isFeatured"] as? Boolean) ?: false,
                                        reviewerId = item["reviewerId"] as? String,
                                        reviewerName = item["reviewerName"] as? String,
                                        reviewerFeedback = item["reviewerFeedback"] as? String,
                                        privateReviewerNotes = item["privateReviewerNotes"] as? String,
                                        badgeAwarded = item["badgeAwarded"] as? String,
                                        viewsCount = (item["viewsCount"] as? Number)?.toInt() ?: 0,
                                        likesCount = (item["likesCount"] as? Number)?.toInt() ?: 0,
                                        aiInnovationScore = (item["aiInnovationScore"] as? Number)?.toInt(),
                                        aiVerdict = item["aiVerdict"] as? String,
                                        aiAnalysis = item["aiAnalysis"] as? String,
                                        aiSuggestions = item["aiSuggestions"] as? String,
                                        createdAt = (item["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                                        updatedAt = (item["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
                                    )
                                    projectDao.insertProject(newProject)
                                }
                            }
                        } catch (e: Throwable) {
                            android.util.Log.w("InnovateXRepository", "Live sync processing warning: ${e.message}")
                        }
                    }
                }
            } catch (t: Throwable) {
                android.util.Log.e("InnovateXRepository", "Error during repository init: ${t.message}", t)
            }
        }
    }

    // ---------------- AUTHENTICATION & SECURITY ----------------

    suspend fun login(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByEmail(email.trim())
        if (user == null) {
            return@withContext Result.failure(Exception("No account found with this email address."))
        }
        if (user.passwordHash != password) {
            return@withContext Result.failure(Exception("Incorrect password. Please try again."))
        }
        _currentUser.value = user
        val latestPayment = paymentDao.getLatestPaymentForUserDirect(user.id)
        _isClassesUnlocked.value = user.isAdmin || (latestPayment?.status == com.example.data.model.PaymentStatus.APPROVED.statusKey)
        FirebaseManager.logLogin(user.id, user.role)
        Result.success(user)
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        school: String,
        gradeClass: String,
        city: String,
        state: String
    ): Result<User> = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim()
        if (userDao.getUserByEmail(trimmedEmail) != null) {
            return@withContext Result.failure(Exception("An account with this email already exists."))
        }

        // SECURITY: Role strictly forced to STUDENT.
        // Admin role can only be assigned in database/seed.
        val newUser = User(
            id = "user_" + UUID.randomUUID().toString().take(8),
            email = trimmedEmail,
            passwordHash = password,
            name = name.trim(),
            role = UserRole.STUDENT.roleKey,
            school = school.trim(),
            gradeClass = gradeClass.trim(),
            city = city.trim(),
            state = state.trim(),
            bio = "Student Innovator at ${school.trim()}",
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&auto=format&fit=crop&q=80",
            isPublicInfoVisible = true,
            createdAt = System.currentTimeMillis()
        )
        userDao.insertUser(newUser)
        _currentUser.value = newUser
        _isClassesUnlocked.value = false
        FirebaseManager.logSignUp(newUser.id, newUser.school, newUser.role)
        Result.success(newUser)
    }

    suspend fun switchUser(userId: String): Result<User> = withContext(Dispatchers.IO) {
        val user = userDao.findUserDirect(userId)
        if (user != null) {
            _currentUser.value = user
            val latestPayment = paymentDao.getLatestPaymentForUserDirect(user.id)
            _isClassesUnlocked.value = user.isAdmin || (latestPayment?.status == com.example.data.model.PaymentStatus.APPROVED.statusKey)
            Result.success(user)
        } else {
            Result.failure(Exception("User not found"))
        }
    }

    fun logout() {
        _currentUser.value = null
        _isClassesUnlocked.value = false
    }

    suspend fun updateProfile(user: User): Result<Unit> = withContext(Dispatchers.IO) {
        val active = _currentUser.value ?: return@withContext Result.failure(Exception("Not authenticated"))
        // Prevent changing own role via profile update!
        val safeUser = user.copy(role = active.role)
        userDao.updateUser(safeUser)
        _currentUser.value = safeUser
        Result.success(Unit)
    }

    // ---------------- PROJECT DISCOVERY ----------------

    fun getPublicProjects(): Flow<List<Project>> = projectDao.getAllPublicProjects()

    fun getFeaturedProjects(): Flow<List<Project>> = projectDao.getFeaturedProjects()

    fun getPopularProjects(): Flow<List<Project>> = projectDao.getPopularProjects()

    fun getProjectsByOwner(ownerId: String): Flow<List<Project>> = projectDao.getProjectsByOwner(ownerId)

    fun getProjectById(id: String): Flow<Project?> = projectDao.getProjectById(id)

    suspend fun likeProject(projectId: String) = withContext(Dispatchers.IO) {
        projectDao.incrementLikes(projectId)
        val proj = projectDao.getProjectDirect(projectId)
        proj?.let {
            FirebaseManager.incrementLikesInRealtime(projectId, it.likesCount)
        }
        FirebaseManager.logProjectInteraction(projectId, "", "like")
    }

    suspend fun viewProject(projectId: String) = withContext(Dispatchers.IO) {
        projectDao.incrementViews(projectId)
        FirebaseManager.logProjectInteraction(projectId, "", "view")
    }

    // ---------------- PROJECT SUBMISSION & STUDENT MANAGEMENT ----------------

    suspend fun submitProject(
        project: Project,
        asDraft: Boolean
    ): Result<Project> = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: return@withContext Result.failure(Exception("You must be logged in to submit a project."))

        val targetStatus = if (asDraft) ProjectStatus.DRAFT.name else ProjectStatus.PENDING_REVIEW.name
        val projectId = if (project.id.isBlank()) "proj_" + UUID.randomUUID().toString().take(8) else project.id

        // Trigger AI Innovation Analysis
        val aiResult = try {
            aiService.analyzeProject(
                title = project.title,
                category = project.category,
                problem = project.problem,
                solution = project.solution,
                working = project.working,
                innovation = project.innovation,
                components = project.components,
                cost = project.estimatedCost
            )
        } catch (t: Throwable) {
            null
        }

        val newProject = project.copy(
            id = projectId,
            ownerId = user.id,
            ownerName = user.name,
            school = if (project.school.isBlank()) user.school else project.school,
            gradeClass = if (project.gradeClass.isBlank()) user.gradeClass else project.gradeClass,
            cityState = if (project.cityState.isBlank()) "${user.city}, ${user.state}" else project.cityState,
            status = targetStatus,
            aiInnovationScore = aiResult?.score ?: project.aiInnovationScore,
            aiVerdict = aiResult?.verdict ?: project.aiVerdict,
            aiAnalysis = aiResult?.analysis ?: project.aiAnalysis,
            aiSuggestions = aiResult?.whatNewToAdd?.let { "• " + it.joinToString("\n• ") } ?: project.aiSuggestions,
            createdAt = if (project.createdAt == 0L) System.currentTimeMillis() else project.createdAt,
            updatedAt = System.currentTimeMillis()
        )

        projectDao.insertProject(newProject)
        FirebaseManager.syncProjectToRealtimeDatabase(newProject)
        FirebaseManager.logProjectSubmission(newProject.id, newProject.title, newProject.category, asDraft)

        if (!asDraft) {
            // Generate notification for submission received
            notificationDao.insertNotification(
                NotificationItem(
                    id = "notif_" + UUID.randomUUID().toString().take(8),
                    userId = user.id,
                    projectId = newProject.id,
                    projectTitle = newProject.title,
                    type = NotificationType.SUBMISSION_RECEIVED.name,
                    title = "Innovation Submitted for Review",
                    message = "Your innovation '${newProject.title}' was submitted successfully and is waiting for review.",
                    isRead = false,
                    createdAt = System.currentTimeMillis()
                )
            )

            if (aiResult != null) {
                notificationDao.insertNotification(
                    NotificationItem(
                        id = "notif_ai_" + UUID.randomUUID().toString().take(8),
                        userId = user.id,
                        projectId = newProject.id,
                        projectTitle = newProject.title,
                        type = NotificationType.AI_REVIEW_COMPLETED.name,
                        title = "🤖 AI Innovation Review: ${aiResult.verdict}",
                        message = "AI Score: ${aiResult.score}/100. AI analyzed your project and recommended ${aiResult.whatNewToAdd.size} new enhancements!",
                        isRead = false,
                        createdAt = System.currentTimeMillis() + 500
                    )
                )
            }
        }

        Result.success(newProject)
    }

    suspend fun analyzeInnovationPreview(
        title: String,
        category: String,
        problem: String,
        solution: String,
        working: String,
        innovation: String,
        components: String,
        cost: String
    ): Result<com.example.data.ai.AiInnovationResult> = withContext(Dispatchers.IO) {
        try {
            val res = aiService.analyzeProject(title, category, problem, solution, working, innovation, components, cost)
            Result.success(res)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    suspend fun reanalyzeProjectWithAi(projectId: String): Result<Project> = withContext(Dispatchers.IO) {
        val proj = projectDao.getProjectDirect(projectId) ?: return@withContext Result.failure(Exception("Project not found"))
        val aiRes = aiService.analyzeProject(
            title = proj.title,
            category = proj.category,
            problem = proj.problem,
            solution = proj.solution,
            working = proj.working,
            innovation = proj.innovation,
            components = proj.components,
            cost = proj.estimatedCost
        )
        val updated = proj.copy(
            aiInnovationScore = aiRes.score,
            aiVerdict = aiRes.verdict,
            aiAnalysis = aiRes.analysis,
            aiSuggestions = "• " + aiRes.whatNewToAdd.joinToString("\n• "),
            updatedAt = System.currentTimeMillis()
        )
        projectDao.updateProject(updated)
        FirebaseManager.syncProjectToRealtimeDatabase(updated)
        Result.success(updated)
    }

    suspend fun updateProject(
        project: Project,
        resubmitForReview: Boolean = false
    ): Result<Project> = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: return@withContext Result.failure(Exception("Not logged in"))
        val existing = projectDao.getProjectDirect(project.id) ?: return@withContext Result.failure(Exception("Project not found"))

        // Security check: Only owner or admin can update
        if (existing.ownerId != user.id && !user.isAdmin) {
            return@withContext Result.failure(Exception("Unauthorized: You can only edit your own project."))
        }

        // Students can only edit if DRAFT or CHANGES_REQUESTED
        if (!user.isAdmin && existing.status != ProjectStatus.DRAFT.name && existing.status != ProjectStatus.CHANGES_REQUESTED.name) {
            return@withContext Result.failure(Exception("Project cannot be edited in its current status (${existing.status})."))
        }

        val targetStatus = if (resubmitForReview) ProjectStatus.PENDING_REVIEW.name else existing.status

        val updated = project.copy(
            status = targetStatus,
            updatedAt = System.currentTimeMillis()
        )
        projectDao.updateProject(updated)
        FirebaseManager.syncProjectToRealtimeDatabase(updated)

        if (resubmitForReview) {
            notificationDao.insertNotification(
                NotificationItem(
                    id = "notif_" + UUID.randomUUID().toString().take(8),
                    userId = user.id,
                    projectId = updated.id,
                    projectTitle = updated.title,
                    type = NotificationType.SUBMISSION_RECEIVED.name,
                    title = "Changes Resubmitted",
                    message = "Your updated project '${updated.title}' has been resubmitted for review.",
                    isRead = false,
                    createdAt = System.currentTimeMillis()
                )
            )
        }

        Result.success(updated)
    }

    suspend fun deleteProject(projectId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: return@withContext Result.failure(Exception("Not logged in"))
        val existing = projectDao.getProjectDirect(projectId) ?: return@withContext Result.failure(Exception("Project not found"))

        if (existing.ownerId != user.id && !user.isAdmin) {
            return@withContext Result.failure(Exception("Unauthorized: Cannot delete this project."))
        }

        projectDao.deleteProjectById(projectId)
        Result.success(Unit)
    }

    // ---------------- ADMIN REVIEW & CONTROL ----------------

    suspend fun getAllProjectsForAdmin(): Result<Flow<List<Project>>> = withContext(Dispatchers.IO) {
        val user = _currentUser.value
        if (user == null || !user.isAdmin) {
            return@withContext Result.failure(Exception("Security Error: Only administrators can access this data."))
        }
        Result.success(projectDao.getAllProjects())
    }

    suspend fun getAdminCounts(): Result<AdminStats> = withContext(Dispatchers.IO) {
        val user = _currentUser.value
        if (user == null || !user.isAdmin) {
            return@withContext Result.failure(Exception("Security Error: Admin access required."))
        }

        // Calculate directly
        val projects = database.openHelper.readableDatabase
        // Use DAO or queries
        // We will fetch from DAO helper
        val allProj = projectDao.getProjectDirect("test") // simple query
        // Let's compute from list of all projects
        // We can query directly
        val totalProjCursor = database.query("SELECT COUNT(*) FROM projects", null)
        var totalProj = 0
        if (totalProjCursor.moveToFirst()) totalProj = totalProjCursor.getInt(0)
        totalProjCursor.close()

        val pendingCursor = database.query("SELECT COUNT(*) FROM projects WHERE status IN ('PENDING_REVIEW', 'UNDER_REVIEW')", null)
        var pending = 0
        if (pendingCursor.moveToFirst()) pending = pendingCursor.getInt(0)
        pendingCursor.close()

        val approvedCursor = database.query("SELECT COUNT(*) FROM projects WHERE status IN ('APPROVED', 'PUBLISHED')", null)
        var approved = 0
        if (approvedCursor.moveToFirst()) approved = approvedCursor.getInt(0)
        approvedCursor.close()

        val rejectedCursor = database.query("SELECT COUNT(*) FROM projects WHERE status = 'REJECTED'", null)
        var rejected = 0
        if (rejectedCursor.moveToFirst()) rejected = rejectedCursor.getInt(0)
        rejectedCursor.close()

        val changesCursor = database.query("SELECT COUNT(*) FROM projects WHERE status = 'CHANGES_REQUESTED'", null)
        var changes = 0
        if (changesCursor.moveToFirst()) changes = changesCursor.getInt(0)
        changesCursor.close()

        val usersCursor = database.query("SELECT COUNT(*) FROM users", null)
        var users = 0
        if (usersCursor.moveToFirst()) users = usersCursor.getInt(0)
        usersCursor.close()

        Result.success(
            AdminStats(
                totalUsers = users,
                totalProjects = totalProj,
                pendingReviews = pending,
                approvedProjects = approved,
                rejectedProjects = rejected,
                changesRequested = changes
            )
        )
    }

    suspend fun submitAdminReview(
        projectId: String,
        decision: String, // "APPROVED", "CHANGES_REQUESTED", "REJECTED", "PUBLISHED", "UNDER_REVIEW"
        studentFeedback: String,
        privateNotes: String,
        badgeAwarded: String? = null,
        featureProject: Boolean? = null
    ): Result<Project> = withContext(Dispatchers.IO) {
        val user = _currentUser.value
        if (user == null || !user.isAdmin) {
            return@withContext Result.failure(Exception("Security Exception: Only reviewers with the Admin role can make review decisions."))
        }

        val project = projectDao.getProjectDirect(projectId)
            ?: return@withContext Result.failure(Exception("Project not found"))

        val newStatus = when (decision.uppercase()) {
            "APPROVE", "APPROVED" -> ProjectStatus.APPROVED.name
            "PUBLISH", "PUBLISHED" -> ProjectStatus.PUBLISHED.name
            "REQUEST_CHANGES", "CHANGES_REQUESTED" -> ProjectStatus.CHANGES_REQUESTED.name
            "REJECT", "REJECTED" -> ProjectStatus.REJECTED.name
            "START_REVIEW", "UNDER_REVIEW" -> ProjectStatus.UNDER_REVIEW.name
            else -> project.status
        }

        val updatedProject = project.copy(
            status = newStatus,
            reviewerId = user.id,
            reviewerName = user.name,
            reviewerFeedback = studentFeedback.ifBlank { project.reviewerFeedback },
            privateReviewerNotes = privateNotes.ifBlank { project.privateReviewerNotes },
            badgeAwarded = badgeAwarded ?: project.badgeAwarded,
            isFeatured = featureProject ?: project.isFeatured,
            updatedAt = System.currentTimeMillis(),
            publishedAt = if (newStatus == ProjectStatus.PUBLISHED.name) System.currentTimeMillis() else project.publishedAt
        )

        projectDao.updateProject(updatedProject)
        FirebaseManager.updateProjectStatusInRealtime(updatedProject.id, newStatus)
        FirebaseManager.syncProjectToRealtimeDatabase(updatedProject)
        FirebaseManager.logReviewDecision(updatedProject.id, newStatus, user.id)

        // Save review log
        reviewDao.insertReview(
            Review(
                id = "rev_" + UUID.randomUUID().toString().take(8),
                projectId = projectId,
                reviewerId = user.id,
                reviewerName = user.name,
                action = newStatus,
                studentFeedback = studentFeedback,
                privateNotes = privateNotes,
                badgeAwarded = badgeAwarded,
                timestamp = System.currentTimeMillis()
            )
        )

        // Send corresponding notification to student
        val (notifType, notifTitle, notifMsg) = when (newStatus) {
            ProjectStatus.APPROVED.name -> Triple(
                NotificationType.PROJECT_APPROVED,
                "Project Approved!",
                "Great news! Your project '${project.title}' was approved by ${user.name}." +
                        (if (!studentFeedback.isBlank()) " Feedback: \"$studentFeedback\"" else "")
            )
            ProjectStatus.PUBLISHED.name -> Triple(
                NotificationType.PROJECT_APPROVED,
                "Project Published!",
                "Your project '${project.title}' is now live on the public InnovateX Explore feed."
            )
            ProjectStatus.CHANGES_REQUESTED.name -> Triple(
                NotificationType.CHANGES_REQUESTED,
                "Changes Requested on Your Submission",
                "Reviewer ${user.name} requested improvements: \"$studentFeedback\". Please update and resubmit."
            )
            ProjectStatus.REJECTED.name -> Triple(
                NotificationType.PROJECT_REJECTED,
                "Review Decision",
                "Your submission '${project.title}' was not approved at this time. Feedback: \"$studentFeedback\"."
            )
            ProjectStatus.UNDER_REVIEW.name -> Triple(
                NotificationType.PROJECT_UNDER_REVIEW,
                "Project is Under Review",
                "Reviewer ${user.name} has begun evaluating your project '${project.title}'."
            )
            else -> Triple(
                NotificationType.SUBMISSION_RECEIVED,
                "Status Updated",
                "Status changed to $newStatus."
            )
        }

        notificationDao.insertNotification(
            NotificationItem(
                id = "notif_" + UUID.randomUUID().toString().take(8),
                userId = project.ownerId,
                projectId = project.id,
                projectTitle = project.title,
                type = notifType.name,
                title = notifTitle,
                message = notifMsg,
                isRead = false,
                createdAt = System.currentTimeMillis()
            )
        )

        // If badge awarded, add badge notification!
        if (!badgeAwarded.isNullOrBlank()) {
            notificationDao.insertNotification(
                NotificationItem(
                    id = "notif_" + UUID.randomUUID().toString().take(8),
                    userId = project.ownerId,
                    projectId = project.id,
                    projectTitle = project.title,
                    type = NotificationType.BADGE_RECEIVED.name,
                    title = "Badge Awarded: $badgeAwarded",
                    message = "Congratulations! Your project received the '$badgeAwarded' distinction.",
                    isRead = false,
                    createdAt = System.currentTimeMillis() + 100
                )
            )
        }

        // If featured, add featured notification!
        if (featureProject == true && !project.isFeatured) {
            notificationDao.insertNotification(
                NotificationItem(
                    id = "notif_" + UUID.randomUUID().toString().take(8),
                    userId = project.ownerId,
                    projectId = project.id,
                    projectTitle = project.title,
                    type = NotificationType.PROJECT_FEATURED.name,
                    title = "Featured on Home!",
                    message = "Your innovation '${project.title}' has been featured on the InnovateX home page.",
                    isRead = false,
                    createdAt = System.currentTimeMillis() + 200
                )
            )
        }

        Result.success(updatedProject)
    }

    suspend fun toggleFeatured(projectId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        val user = _currentUser.value
        if (user == null || !user.isAdmin) {
            return@withContext Result.failure(Exception("Only admins can feature projects."))
        }
        val proj = projectDao.getProjectDirect(projectId)
            ?: return@withContext Result.failure(Exception("Project not found"))
        val next = !proj.isFeatured
        projectDao.updateProject(proj.copy(isFeatured = next))
        Result.success(next)
    }

    // ---------------- NOTIFICATIONS ----------------

    fun getNotificationsForUser(userId: String): Flow<List<NotificationItem>> =
        notificationDao.getNotificationsForUser(userId)

    fun getUnreadNotificationsCount(userId: String): Flow<Int> =
        notificationDao.getUnreadCount(userId)

    suspend fun markNotificationRead(id: String) = withContext(Dispatchers.IO) {
        notificationDao.markAsRead(id)
    }

    suspend fun markAllNotificationsRead(userId: String) = withContext(Dispatchers.IO) {
        notificationDao.markAllAsRead(userId)
    }

    suspend fun clearNotifications(userId: String) = withContext(Dispatchers.IO) {
        notificationDao.clearAllForUser(userId)
    }

    // ---------------- REVIEWS & STAR RATINGS ----------------

    fun getReviewsForProject(projectId: String): Flow<List<Review>> =
        reviewDao.getReviewsForProject(projectId)

    suspend fun submitStudentReview(
        projectId: String,
        rating: Int,
        studentFeedback: String,
        constructiveTip: String = ""
    ): Result<Review> = withContext(Dispatchers.IO) {
        val user = _currentUser.value
        val reviewerId = user?.id ?: "student_peer"
        val reviewerName = user?.name?.ifBlank { "Student Innovator" } ?: "Student Innovator"
        val reviewerRole = when {
            user?.isAdmin == true -> "Lead STEM Reviewer"
            !user?.gradeClass.isNullOrBlank() -> "Peer (${user.gradeClass})"
            else -> "Student Innovator"
        }

        if (studentFeedback.isBlank()) {
            return@withContext Result.failure(Exception("Please provide constructive feedback or comments for this prototype."))
        }

        val clampedRating = rating.coerceIn(1, 5)
        val reviewId = "rev_peer_" + UUID.randomUUID().toString().take(8)
        val review = Review(
            id = reviewId,
            projectId = projectId,
            reviewerId = reviewerId,
            reviewerName = reviewerName,
            action = "PEER_REVIEW",
            studentFeedback = studentFeedback.trim(),
            privateNotes = "",
            badgeAwarded = null,
            rating = clampedRating,
            reviewerRole = reviewerRole,
            constructiveTip = constructiveTip.trim(),
            timestamp = System.currentTimeMillis()
        )

        reviewDao.insertReview(review)

        // Notify project owner
        val project = projectDao.getProjectDirect(projectId)
        if (project != null && project.ownerId != reviewerId) {
            notificationDao.insertNotification(
                NotificationItem(
                    id = "notif_" + UUID.randomUUID().toString().take(8),
                    userId = project.ownerId,
                    projectId = project.id,
                    projectTitle = project.title,
                    type = NotificationType.PEER_REVIEW_RECEIVED.name,
                    title = "New $clampedRating★ Review Received!",
                    message = "$reviewerName rated your prototype '$clampedRating Stars': \"${studentFeedback.take(65)}\"",
                    isRead = false,
                    createdAt = System.currentTimeMillis()
                )
            )
        }

        Result.success(review)
    }

    suspend fun deleteReview(reviewId: String): Result<Unit> = withContext(Dispatchers.IO) {
        reviewDao.deleteReview(reviewId)
        Result.success(Unit)
    }

    // ---------------- LIVE CLASSES & PAYMENT LOCK ----------------

    fun getLatestPaymentForUser(userId: String): Flow<com.example.data.model.PaymentRequest?> =
        paymentDao.getLatestPaymentForUserFlow(userId)

    fun getAllPaymentRequests(): Flow<List<com.example.data.model.PaymentRequest>> =
        paymentDao.getAllPaymentRequestsFlow()

    suspend fun submitPaymentRequest(txnId: String): Result<com.example.data.model.PaymentRequest> = withContext(Dispatchers.IO) {
        if (txnId.isBlank()) {
            return@withContext Result.failure(Exception("Please enter payment Transaction ID / TRX #"))
        }

        val user = _currentUser.value
            ?: return@withContext Result.failure(Exception("User not logged in"))

        val req = com.example.data.model.PaymentRequest(
            id = "pay_" + UUID.randomUUID().toString().take(8),
            userId = user.id,
            userName = user.name,
            userEmail = user.email,
            transactionId = txnId,
            amountPkr = 250,
            status = com.example.data.model.PaymentStatus.PENDING.statusKey,
            timestamp = System.currentTimeMillis()
        )

        paymentDao.insertPaymentRequest(req)

        // Notify user
        notificationDao.insertNotification(
            NotificationItem(
                id = "notif_" + UUID.randomUUID().toString().take(8),
                userId = user.id,
                projectId = null,
                projectTitle = null,
                type = NotificationType.SYSTEM.name,
                title = "Payment Submitted for Verification ⏳",
                message = "Your payment of 250 PKR (Txn: $txnId) has been submitted to Admin. Classes will unlock once Admin approves.",
                isRead = false,
                createdAt = System.currentTimeMillis()
            )
        )

        Result.success(req)
    }

    suspend fun adminReviewPayment(requestId: String, approve: Boolean, adminNotes: String): Result<Boolean> = withContext(Dispatchers.IO) {
        val adminUser = _currentUser.value
        if (adminUser == null || !adminUser.isAdmin) {
            return@withContext Result.failure(Exception("Only admins can review payment requests."))
        }

        val nextStatus = if (approve) com.example.data.model.PaymentStatus.APPROVED.statusKey else com.example.data.model.PaymentStatus.REJECTED.statusKey
        paymentDao.updateStatus(requestId, nextStatus, adminNotes)

        // Get requests to notify user
        val allReqs = paymentDao.getAllPaymentRequestsFlow()
        // If approved, unlock current user if matching
        if (approve) {
            _isClassesUnlocked.value = true
        }

        Result.success(approve)
    }

    suspend fun unlockClassesAccessDirectly(): Result<Boolean> = withContext(Dispatchers.IO) {
        _isClassesUnlocked.value = true
        Result.success(true)
    }

    suspend fun createLiveClass(session: com.example.data.model.LiveClassSession): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            liveClassDao.insertClass(session)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteLiveClass(classId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            liveClassDao.deleteClass(classId)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getPaymentConfig(): Flow<com.example.data.model.PaymentConfig?> =
        paymentDao.getPaymentConfigFlow()

    suspend fun updatePaymentConfig(
        scannerImageUri: String?,
        accountTitle: String? = null,
        accountNumber: String? = null,
        feeAmountPkr: Int? = null
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        val current = paymentDao.getPaymentConfigDirect() ?: com.example.data.model.PaymentConfig()
        val updated = current.copy(
            scannerImageUri = scannerImageUri,
            accountTitle = accountTitle ?: current.accountTitle,
            accountNumber = accountNumber ?: current.accountNumber,
            feeAmountPkr = feeAmountPkr ?: current.feeAmountPkr,
            updatedTimestamp = System.currentTimeMillis()
        )
        paymentDao.savePaymentConfig(updated)
        Result.success(true)
    }

    suspend fun resetPaymentScannerToDefault(): Result<Boolean> = withContext(Dispatchers.IO) {
        val current = paymentDao.getPaymentConfigDirect() ?: com.example.data.model.PaymentConfig()
        val updated = current.copy(
            scannerImageUri = null,
            updatedTimestamp = System.currentTimeMillis()
        )
        paymentDao.savePaymentConfig(updated)
        Result.success(true)
    }
}

data class AdminStats(
    val totalUsers: Int = 0,
    val totalProjects: Int = 0,
    val pendingReviews: Int = 0,
    val approvedProjects: Int = 0,
    val rejectedProjects: Int = 0,
    val changesRequested: Int = 0
)
