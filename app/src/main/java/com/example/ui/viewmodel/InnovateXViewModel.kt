@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.InnovationBadge
import com.example.data.model.InnovationCategory
import com.example.data.model.NotificationItem
import com.example.data.model.Project
import com.example.data.model.ProjectStatus
import com.example.data.model.User
import com.example.data.repository.AdminStats
import com.example.data.repository.InnovateXRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SubmissionFormState(
    val currentStep: Int = 1, // 1 to 4
    val projectId: String = "",
    val isEditMode: Boolean = false,
    // Step 1
    val title: String = "",
    val category: String = InnovationCategory.AGRICULTURE.title,
    val studentName: String = "",
    val school: String = "",
    val gradeClass: String = "",
    val cityState: String = "",
    // Step 2
    val problem: String = "",
    val solution: String = "",
    val working: String = "",
    val innovation: String = "",
    val components: String = "",
    val estimatedCost: String = "",
    // Step 3
    val photoUrls: List<String> = emptyList(),
    val videoUrl: String = "",
    val documentUrl: String = "",
    // Step 4
    val agreedToTerms: Boolean = false,
    val isSubmitting: Boolean = false,
    val uploadProgress: Float = 0f,
    val isAiAnalyzing: Boolean = false,
    val aiPreviewResult: com.example.data.ai.AiInnovationResult? = null,
    val errorMessage: String? = null,
    val isSubmittedSuccess: Boolean = false
)

class InnovateXViewModel(application: Application) : AndroidViewModel(application) {
    val repository = InnovateXRepository(application)

    val currentUser: StateFlow<User?> = repository.currentUser

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    // Explore / Filtering state
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow<String?>("All")
    val selectedSort = MutableStateFlow("Newest") // "Newest", "Most Popular", "Most Liked"

    // Base flows
    val publicProjects: StateFlow<List<Project>> = repository.getPublicProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredProjects: StateFlow<List<Project>> = repository.getFeaturedProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val popularProjects: StateFlow<List<Project>> = repository.getPopularProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered explore list
    val filteredExploreProjects: StateFlow<List<Project>> = combine(
        publicProjects,
        searchQuery,
        selectedCategory,
        selectedSort
    ) { projects, query, category, sort ->
        var list = projects
        if (!query.isBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                        it.problem.lowercase().contains(q) ||
                        it.solution.lowercase().contains(q) ||
                        it.category.lowercase().contains(q) ||
                        it.school.lowercase().contains(q) ||
                        it.components.lowercase().contains(q)
            }
        }
        if (!category.isNullOrBlank() && category != "All") {
            list = list.filter { it.category.equals(category, ignoreCase = true) }
        }
        when (sort) {
            "Most Popular" -> list.sortedByDescending { it.viewsCount }
            "Most Liked" -> list.sortedByDescending { it.likesCount }
            else -> list.sortedByDescending { it.createdAt }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // My Projects for currently logged-in student
    val myProjects: StateFlow<List<Project>> = currentUser.flatMapLatest { user ->
        if (user != null) {
            repository.getProjectsByOwner(user.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notifications
    val notifications: StateFlow<List<NotificationItem>> = currentUser.flatMapLatest { user ->
        if (user != null) {
            repository.getNotificationsForUser(user.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotifCount: StateFlow<Int> = currentUser.flatMapLatest { user ->
        if (user != null) {
            repository.getUnreadNotificationsCount(user.id)
        } else {
            flowOf(0)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Admin flows
    val isAdminPasscodeUnlocked = MutableStateFlow(false)

    fun verifyAndUnlockAdminPasscode(code: String): Boolean {
        return if (code.trim() == "123321") {
            isAdminPasscodeUnlocked.value = true
            if (currentUser.value?.isAdmin != true) {
                switchAccount("user_admin_1")
            } else {
                refreshAdminDashboard()
            }
            true
        } else {
            false
        }
    }

    fun lockAdminPasscode() {
        isAdminPasscodeUnlocked.value = false
    }

    private val _adminProjects = MutableStateFlow<List<Project>>(emptyList())
    val adminProjects: StateFlow<List<Project>> = _adminProjects.asStateFlow()

    private val _adminStats = MutableStateFlow<AdminStats?>(null)
    val adminStats: StateFlow<AdminStats?> = _adminStats.asStateFlow()

    // Selected project for detail view
    private val _selectedProjectId = MutableStateFlow<String?>(null)
    val selectedProject: StateFlow<Project?> = _selectedProjectId.flatMapLatest { id ->
        if (id != null) repository.getProjectById(id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Submission form wizard state
    private val _submissionForm = MutableStateFlow(SubmissionFormState())
    val submissionForm: StateFlow<SubmissionFormState> = _submissionForm.asStateFlow()

    init {
        // Observe currentUser to update admin data if admin
        viewModelScope.launch {
            currentUser.collect { user ->
                if (user?.isAdmin == true) {
                    refreshAdminDashboard()
                }
            }
        }
    }

    fun selectProject(projectId: String) {
        _selectedProjectId.value = projectId
        viewModelScope.launch {
            repository.viewProject(projectId)
        }
    }

    fun likeProject(projectId: String) {
        viewModelScope.launch {
            repository.likeProject(projectId)
            _userMessage.emit("Applauded innovation!")
        }
    }

    // ---------------- AUTH ACTIONS ----------------

    fun login(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val res = repository.login(email, pass)
            res.onSuccess {
                _userMessage.emit("Welcome back, ${it.name}!")
                onResult(true, null)
            }.onFailure {
                onResult(false, it.message)
            }
        }
    }

    fun register(
        name: String,
        email: String,
        pass: String,
        school: String,
        gradeClass: String,
        city: String,
        state: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.register(name, email, pass, school, gradeClass, city, state)
            res.onSuccess {
                _userMessage.emit("Welcome to InnovateX, ${it.name}!")
                onResult(true, null)
            }.onFailure {
                onResult(false, it.message)
            }
        }
    }

    fun switchAccount(userId: String) {
        viewModelScope.launch {
            val res = repository.switchUser(userId)
            res.onSuccess {
                _userMessage.emit("Switched profile to: ${it.name} (${if (it.isAdmin) "Admin" else "Student"})")
                if (it.isAdmin) {
                    refreshAdminDashboard()
                }
            }
        }
    }

    fun logout() {
        repository.logout()
        viewModelScope.launch {
            _userMessage.emit("Logged out")
        }
    }

    fun updateProfile(user: User, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val res = repository.updateProfile(user)
            res.onSuccess {
                _userMessage.emit("Profile updated successfully")
                onResult(true, null)
            }.onFailure {
                onResult(false, it.message)
            }
        }
    }

    // ---------------- SUBMISSION WIZARD ----------------

    fun startNewSubmission() {
        val user = currentUser.value
        _submissionForm.value = SubmissionFormState(
            currentStep = 1,
            studentName = user?.name ?: "",
            school = user?.school ?: "",
            gradeClass = user?.gradeClass ?: "",
            cityState = if (user != null) "${user.city}, ${user.state}" else "",
            photoUrls = listOf(
                "https://images.unsplash.com/photo-1581092335397-9583fe92d232?w=800&auto=format&fit=crop&q=80"
            )
        )
    }

    fun editExistingProject(project: Project) {
        _submissionForm.value = SubmissionFormState(
            currentStep = 1,
            projectId = project.id,
            isEditMode = true,
            title = project.title,
            category = project.category,
            studentName = project.ownerName,
            school = project.school,
            gradeClass = project.gradeClass,
            cityState = project.cityState,
            problem = project.problem,
            solution = project.solution,
            working = project.working,
            innovation = project.innovation,
            components = project.components,
            estimatedCost = project.estimatedCost,
            photoUrls = project.photoUrls.ifEmpty {
                listOf("https://images.unsplash.com/photo-1581092335397-9583fe92d232?w=800&auto=format&fit=crop&q=80")
            },
            videoUrl = project.videoUrl,
            documentUrl = project.documentUrl,
            agreedToTerms = true
        )
    }

    fun updateFormStep1(title: String, category: String, studentName: String, school: String, gradeClass: String, cityState: String) {
        _submissionForm.value = _submissionForm.value.copy(
            title = title,
            category = category,
            studentName = studentName,
            school = school,
            gradeClass = gradeClass,
            cityState = cityState,
            errorMessage = null
        )
    }

    fun updateFormStep2(problem: String, solution: String, working: String, innovation: String, components: String, estimatedCost: String) {
        _submissionForm.value = _submissionForm.value.copy(
            problem = problem,
            solution = solution,
            working = working,
            innovation = innovation,
            components = components,
            estimatedCost = estimatedCost,
            errorMessage = null
        )
    }

    fun updateFormStep3(photoUrls: List<String>, videoUrl: String, documentUrl: String) {
        _submissionForm.value = _submissionForm.value.copy(
            photoUrls = photoUrls,
            videoUrl = videoUrl,
            documentUrl = documentUrl,
            errorMessage = null
        )
    }

    fun setSubmissionTermsAgreed(agreed: Boolean) {
        _submissionForm.value = _submissionForm.value.copy(agreedToTerms = agreed)
    }

    fun goToNextStep(): Boolean {
        val s = _submissionForm.value
        when (s.currentStep) {
            1 -> {
                if (s.title.isBlank()) {
                    _submissionForm.value = s.copy(errorMessage = "Project title is required")
                    return false
                }
                if (s.school.isBlank()) {
                    _submissionForm.value = s.copy(errorMessage = "School name is required")
                    return false
                }
            }
            2 -> {
                if (s.problem.isBlank() || s.solution.isBlank()) {
                    _submissionForm.value = s.copy(errorMessage = "Problem statement and solution are required")
                    return false
                }
                if (s.working.isBlank()) {
                    _submissionForm.value = s.copy(errorMessage = "Explanation of how the project works is required")
                    return false
                }
            }
            3 -> {
                // Media validated
            }
        }
        _submissionForm.value = s.copy(currentStep = (s.currentStep + 1).coerceAtMost(4), errorMessage = null)
        return true
    }

    fun goToPreviousStep() {
        val s = _submissionForm.value
        _submissionForm.value = s.copy(currentStep = (s.currentStep - 1).coerceAtLeast(1), errorMessage = null)
    }

    fun submitProject(asDraft: Boolean = false, onComplete: (Boolean, String?) -> Unit) {
        val s = _submissionForm.value
        if (!asDraft && !s.agreedToTerms) {
            _submissionForm.value = s.copy(errorMessage = "Please accept the terms and student submission confirmation.")
            onComplete(false, "Please accept the confirmation checkbox.")
            return
        }

        viewModelScope.launch {
            _submissionForm.value = s.copy(isSubmitting = true, uploadProgress = 0.3f)
            kotlinx.coroutines.delay(400)
            _submissionForm.value = _submissionForm.value.copy(uploadProgress = 0.75f)
            kotlinx.coroutines.delay(300)
            _submissionForm.value = _submissionForm.value.copy(uploadProgress = 1.0f)

            val proj = Project(
                id = s.projectId,
                ownerId = currentUser.value?.id ?: "",
                ownerName = s.studentName.ifBlank { currentUser.value?.name ?: "Student" },
                school = s.school,
                gradeClass = s.gradeClass,
                cityState = s.cityState,
                title = s.title,
                category = s.category,
                problem = s.problem,
                solution = s.solution,
                working = s.working,
                innovation = s.innovation,
                components = s.components,
                estimatedCost = s.estimatedCost,
                photoUrls = s.photoUrls,
                videoUrl = s.videoUrl,
                documentUrl = s.documentUrl
            )

            val result = if (s.isEditMode) {
                repository.updateProject(proj, resubmitForReview = !asDraft)
            } else {
                repository.submitProject(proj, asDraft = asDraft)
            }

            result.onSuccess {
                _submissionForm.value = _submissionForm.value.copy(
                    isSubmitting = false,
                    isSubmittedSuccess = true
                )
                val msg = if (asDraft) "Draft saved successfully" else "Your innovation has been submitted successfully and is waiting for review."
                _userMessage.emit(msg)
                onComplete(true, null)
            }.onFailure {
                _submissionForm.value = _submissionForm.value.copy(
                    isSubmitting = false,
                    errorMessage = it.message
                )
                onComplete(false, it.message)
            }
        }
    }

    fun runAiAnalysisOnDraft() {
        val s = _submissionForm.value
        if (s.title.isBlank() || s.problem.isBlank() || s.solution.isBlank()) {
            _submissionForm.value = s.copy(errorMessage = "Please fill in the title, problem, and solution before requesting AI analysis.")
            return
        }
        viewModelScope.launch {
            _submissionForm.value = _submissionForm.value.copy(isAiAnalyzing = true, errorMessage = null)
            val result = repository.analyzeInnovationPreview(
                title = s.title,
                category = s.category,
                problem = s.problem,
                solution = s.solution,
                working = s.working,
                innovation = s.innovation,
                components = s.components,
                cost = s.estimatedCost
            )
            result.onSuccess { aiRes ->
                _submissionForm.value = _submissionForm.value.copy(
                    isAiAnalyzing = false,
                    aiPreviewResult = aiRes
                )
            }.onFailure { err ->
                _submissionForm.value = _submissionForm.value.copy(
                    isAiAnalyzing = false,
                    errorMessage = "AI Analysis note: ${err.message}"
                )
            }
        }
    }

    fun reanalyzeProjectWithAi(projectId: String, onComplete: (Boolean, String?) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            val res = repository.reanalyzeProjectWithAi(projectId)
            res.onSuccess { updated ->
                _userMessage.emit("🤖 AI Analysis updated: ${updated.aiVerdict ?: "Completed"}")
                refreshAdminDashboard()
                onComplete(true, null)
            }.onFailure { err ->
                _userMessage.emit("AI Analysis failed: ${err.message}")
                onComplete(false, err.message)
            }
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            val res = repository.deleteProject(projectId)
            res.onSuccess {
                _userMessage.emit("Project deleted")
                if (currentUser.value?.isAdmin == true) {
                    refreshAdminDashboard()
                }
            }.onFailure {
                _userMessage.emit(it.message ?: "Failed to delete project")
            }
        }
    }

    // ---------------- ADMIN PANEL ACTIONS ----------------

    fun refreshAdminDashboard() {
        viewModelScope.launch {
            val countsRes = repository.getAdminCounts()
            countsRes.onSuccess { _adminStats.value = it }

            val projRes = repository.getAllProjectsForAdmin()
            projRes.onSuccess { flow ->
                flow.collect { list ->
                    _adminProjects.value = list
                    val pending = list.count { it.status == ProjectStatus.PENDING_REVIEW.name || it.status == ProjectStatus.UNDER_REVIEW.name }
                    val approved = list.count { it.status == ProjectStatus.APPROVED.name || it.status == ProjectStatus.PUBLISHED.name }
                    val rejected = list.count { it.status == ProjectStatus.REJECTED.name }
                    val changes = list.count { it.status == ProjectStatus.CHANGES_REQUESTED.name }
                    val currentStats = _adminStats.value
                    _adminStats.value = (currentStats ?: AdminStats()).copy(
                        totalProjects = list.size,
                        pendingReviews = pending,
                        approvedProjects = approved,
                        rejectedProjects = rejected,
                        changesRequested = changes
                    )
                }
            }
        }
    }

    fun submitAdminReview(
        projectId: String,
        decision: String, // "APPROVED", "CHANGES_REQUESTED", "REJECTED", "PUBLISHED", "UNDER_REVIEW"
        studentFeedback: String,
        privateNotes: String,
        badgeAwarded: String?,
        featureProject: Boolean?,
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.submitAdminReview(
                projectId = projectId,
                decision = decision,
                studentFeedback = studentFeedback,
                privateNotes = privateNotes,
                badgeAwarded = badgeAwarded,
                featureProject = featureProject
            )
            res.onSuccess {
                _userMessage.emit("Review decision applied: ${it.status}")
                refreshAdminDashboard()
                onComplete(true, null)
            }.onFailure {
                onComplete(false, it.message)
            }
        }
    }

    fun toggleFeatured(projectId: String) {
        viewModelScope.launch {
            val res = repository.toggleFeatured(projectId)
            res.onSuccess { isFeatured ->
                val text = if (isFeatured) "Project is now featured!" else "Project unfeatured"
                _userMessage.emit(text)
                refreshAdminDashboard()
            }.onFailure {
                _userMessage.emit(it.message ?: "Error toggling featured status")
            }
        }
    }

    val isClassesUnlocked: StateFlow<Boolean> = repository.isClassesUnlocked
    val allLiveClasses: StateFlow<List<com.example.data.model.LiveClassSession>> = repository.allLiveClasses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val latestUserPaymentRequest: StateFlow<com.example.data.model.PaymentRequest?> = currentUser
        .flatMapLatest { user ->
            if (user != null) repository.getLatestPaymentForUser(user.id) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allPaymentRequests: StateFlow<List<com.example.data.model.PaymentRequest>> = repository.getAllPaymentRequests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun submitPaymentRequest(txnId: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val res = repository.submitPaymentRequest(txnId)
            res.onSuccess {
                _userMessage.emit("Payment Request Submitted! Pending Admin Approval. ⏳")
                onResult(true, null)
            }.onFailure {
                onResult(false, it.message ?: "Failed to submit payment request.")
            }
        }
    }

    fun adminReviewPayment(requestId: String, approve: Boolean, notes: String) {
        viewModelScope.launch {
            val res = repository.adminReviewPayment(requestId, approve, notes)
            res.onSuccess {
                val statusText = if (approve) "Approved & Unlocked!" else "Rejected"
                _userMessage.emit("Payment Request $statusText")
            }.onFailure {
                _userMessage.emit(it.message ?: "Error processing payment review")
            }
        }
    }

    fun unlockLiveClassesDirectly() {
        viewModelScope.launch {
            repository.unlockClassesAccessDirectly()
            _userMessage.emit("Classes Unlocked (Demo Mode) 🎉")
        }
    }

    fun createLiveClass(
        title: String,
        instructorName: String,
        subject: String,
        dateTimeText: String,
        zoomMeetingId: String,
        zoomPassword: String,
        zoomLink: String,
        description: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val newClass = com.example.data.model.LiveClassSession(
                id = "class_" + java.util.UUID.randomUUID().toString().take(8),
                title = title,
                instructorName = instructorName,
                subject = subject,
                dateTimeText = dateTimeText,
                zoomMeetingId = zoomMeetingId,
                zoomPassword = zoomPassword,
                zoomLink = zoomLink,
                description = description,
                isLiveNow = true
            )
            val res = repository.createLiveClass(newClass)
            res.onSuccess {
                _userMessage.emit("Zoom Live Class Created & Published!")
                onResult(true, null)
            }.onFailure {
                onResult(false, it.message ?: "Failed to create class.")
            }
        }
    }

    // ---------------- NOTIFICATIONS ----------------

    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsRead() {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.markAllNotificationsRead(user.id)
            _userMessage.emit("All notifications marked as read")
        }
    }

    fun clearNotifications() {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.clearNotifications(user.id)
            _userMessage.emit("Notification feed cleared")
        }
    }
}
