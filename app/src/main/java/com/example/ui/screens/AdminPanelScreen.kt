package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.InnovationBadge
import com.example.data.model.Project
import com.example.data.model.ProjectStatus
import com.example.ui.components.BadgeChip
import com.example.ui.components.CategoryPill
import com.example.ui.components.StatusBadge
import com.example.ui.viewmodel.InnovateXViewModel

@Composable
fun AdminPanelScreen(
    viewModel: InnovateXViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToProjectDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val adminProjects by viewModel.adminProjects.collectAsStateWithLifecycle()
    val adminStats by viewModel.adminStats.collectAsStateWithLifecycle()
    val isAdminPasscodeUnlocked by viewModel.isAdminPasscodeUnlocked.collectAsStateWithLifecycle()
    val allPaymentRequests by viewModel.allPaymentRequests.collectAsStateWithLifecycle()

    var selectedStatusFilter by remember { mutableStateOf("Pending") }
    var activeReviewProject by remember { mutableStateOf<Project?>(null) }
    var inputPasscode by remember { mutableStateOf("") }
    var isGatePasscodeVisible by remember { mutableStateOf(false) }
    var passcodeError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.refreshAdminDashboard()
    }

    // Security Gate check
    if (currentUser?.isAdmin != true && !isAdminPasscodeUnlocked) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp)
                .testTag("admin_access_denied"),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF6366F1).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFF6366F1), modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Admin Passcode Required", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Enter the authorized 6-digit administrator code to unlock this Review Panel:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = inputPasscode,
                        onValueChange = {
                            if (it.length <= 8) {
                                inputPasscode = it
                                passcodeError = null
                            }
                        },
                        label = { Text("Admin Code") },
                        placeholder = { Text("••••••") },
                        singleLine = true,
                        isError = passcodeError != null,
                        visualTransformation = if (isGatePasscodeVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        trailingIcon = {
                            IconButton(onClick = { isGatePasscodeVisible = !isGatePasscodeVisible }) {
                                Icon(
                                    imageVector = if (isGatePasscodeVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (isGatePasscodeVisible) "Hide code" else "Show code"
                                )
                            }
                        },
                        supportingText = {
                            if (passcodeError != null) {
                                Text(passcodeError!!, color = MaterialTheme.colorScheme.error)
                            } else {
                                Text("Enter 6-digit security code", style = MaterialTheme.typography.labelSmall)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_gate_passcode_input")
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Back to Profile")
                        }
                        Button(
                            onClick = {
                                if (viewModel.verifyAndUnlockAdminPasscode(inputPasscode)) {
                                    passcodeError = null
                                } else {
                                    passcodeError = "Incorrect code! Access denied."
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_gate_unlock_btn")
                        ) {
                            Text("Unlock")
                        }
                    }
                }
            }
        }
        return
    }

    // Filter list
    val filteredProjects = remember(adminProjects, selectedStatusFilter) {
        when (selectedStatusFilter) {
            "Pending" -> adminProjects.filter { it.status == "PENDING_REVIEW" || it.status == "UNDER_REVIEW" }
            "Changes Req." -> adminProjects.filter { it.status == "CHANGES_REQUESTED" }
            "Approved" -> adminProjects.filter { it.status == "APPROVED" || it.status == "PUBLISHED" }
            "Rejected" -> adminProjects.filter { it.status == "REJECTED" }
            else -> adminProjects
        }
    }

    if (activeReviewProject != null) {
        AdminReviewDialog(
            project = activeReviewProject!!,
            onDismiss = { activeReviewProject = null },
            onSubmitDecision = { decision, feedback, notes, badge, feature ->
                viewModel.submitAdminReview(
                    projectId = activeReviewProject!!.id,
                    decision = decision,
                    studentFeedback = feedback,
                    privateNotes = notes,
                    badgeAwarded = badge,
                    featureProject = feature
                ) { success, _ ->
                    if (success) {
                        activeReviewProject = null
                    }
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_panel_screen")
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = "Admin Review Panel",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "InnovateX Reviewer Board • ${currentUser?.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Platform Overview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AdminStatCard(
                            label = "Pending Review",
                            count = adminStats?.pendingReviews ?: 0,
                            icon = Icons.Default.HourglassTop,
                            accentColor = Color(0xFFF59E0B),
                            modifier = Modifier.weight(1f)
                        )
                        AdminStatCard(
                            label = "Approved",
                            count = adminStats?.approvedProjects ?: 0,
                            icon = Icons.Default.AssignmentTurnedIn,
                            accentColor = Color(0xFF10B981),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AdminStatCard(
                            label = "Changes Req.",
                            count = adminStats?.changesRequested ?: 0,
                            icon = Icons.Default.ChangeCircle,
                            accentColor = Color(0xFFEA580C),
                            modifier = Modifier.weight(1f)
                        )
                        AdminStatCard(
                            label = "Total Submissions",
                            count = adminStats?.totalProjects ?: 0,
                            icon = Icons.Default.Assignment,
                            accentColor = Color(0xFF6366F1),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 💳 Zoom Live Classes Payment Approvals Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Zoom Classes Payment Approvals (${allPaymentRequests.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "${allPaymentRequests.count { it.status == "PENDING" }} Pending",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        if (allPaymentRequests.isEmpty()) {
                            Text(
                                text = "No student payment requests submitted yet.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            allPaymentRequests.take(5).forEach { req ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = req.userName,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Surface(
                                                color = when (req.status) {
                                                    "APPROVED" -> Color(0xFF10B981).copy(alpha = 0.15f)
                                                    "REJECTED" -> Color(0xFFEF4444).copy(alpha = 0.15f)
                                                    else -> Color(0xFFF59E0B).copy(alpha = 0.15f)
                                                },
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = req.status,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = when (req.status) {
                                                        "APPROVED" -> Color(0xFF10B981)
                                                        "REJECTED" -> Color(0xFFEF4444)
                                                        else -> Color(0xFFD97706)
                                                    },
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = "Email: ${req.userEmail} • Txn ID: ${req.transactionId}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        if (req.status == "PENDING") {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Button(
                                                    onClick = {
                                                        viewModel.adminReviewPayment(req.id, true, "Payment Verified by Admin")
                                                    },
                                                    modifier = Modifier.weight(1f).height(36.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Text("Approve & Unlock (250 PKR)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }

                                                OutlinedButton(
                                                    onClick = {
                                                        viewModel.adminReviewPayment(req.id, false, "Invalid TRX #")
                                                    },
                                                    modifier = Modifier.weight(1f).height(36.dp),
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Text("Reject", fontSize = 11.sp, color = Color(0xFFEF4444))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Filter Tabs
            item {
                Column {
                    Text(
                        text = "Submissions Queue (${filteredProjects.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val filters = listOf("Pending", "Changes Req.", "Approved", "Rejected", "All")
                        items(filters) { f ->
                            CategoryPill(
                                category = f,
                                isSelected = selectedStatusFilter == f,
                                onClick = { selectedStatusFilter = f }
                            )
                        }
                    }
                }
            }

            // Projects List
            if (filteredProjects.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No projects in '$selectedStatusFilter' queue.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(filteredProjects, key = { it.id }) { project ->
                    AdminProjectItemCard(
                        project = project,
                        onReviewClick = { activeReviewProject = project },
                        onViewDetail = { onNavigateToProjectDetail(project.id) },
                        onToggleFeatured = { viewModel.toggleFeatured(project.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminStatCard(
    label: String,
    count: Int,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(22.dp))
            }

            Column {
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AdminProjectItemCard(
    project: Project,
    onReviewClick: () -> Unit,
    onViewDetail: () -> Unit,
    onToggleFeatured: () -> Unit,
    modifier: Modifier = Modifier
) {
    val photo = project.photoUrls.firstOrNull()
        ?: "https://images.unsplash.com/photo-1581092335397-9583fe92d232?w=800&auto=format&fit=crop&q=80"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("admin_project_item_${project.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = onViewDetail)
                ) {
                    AsyncImage(
                        model = photo,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = project.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        StatusBadge(status = project.projectStatus)
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = project.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "By ${project.ownerName} • ${project.school}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Problem: ${project.problem}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (!project.badgeAwarded.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                BadgeChip(badgeTitle = project.badgeAwarded, compact = true)
            }

            if (project.aiInnovationScore != null || !project.aiVerdict.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = Color(0xFFF3E8FF),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF7E22CE),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "AI Review: ${project.aiInnovationScore ?: "--"}/100 • ${project.aiVerdict ?: "Assessed"}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF7E22CE),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onToggleFeatured) {
                        Icon(
                            imageVector = if (project.isFeatured) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Feature",
                            tint = if (project.isFeatured) Color(0xFFF59E0B) else MaterialTheme.colorScheme.outline
                        )
                    }
                    Text(
                        text = if (project.isFeatured) "Featured" else "Feature",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onViewDetail,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("View Details", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onReviewClick,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("review_button_${project.id}")
                    ) {
                        Icon(imageVector = Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Evaluate", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReviewDialog(
    project: Project,
    onDismiss: () -> Unit,
    onSubmitDecision: (decision: String, feedback: String, privateNotes: String, badge: String?, feature: Boolean) -> Unit
) {
    var studentFeedback by remember { mutableStateOf(project.reviewerFeedback ?: "") }
    var privateNotes by remember { mutableStateOf(project.privateReviewerNotes ?: "") }
    var selectedBadge by remember { mutableStateOf(project.badgeAwarded ?: "None") }
    var isFeatured by remember { mutableStateOf(project.isFeatured) }
    var showBadgeDropdown by remember { mutableStateOf(false) }

    val badgesList = listOf("None") + InnovationBadge.entries.map { it.title }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .testTag("admin_review_modal"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Evaluate Submission",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = project.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Creator: ${project.ownerName} • ${project.school}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // AI Innovation Review Insights (for Reviewer)
                if (project.aiInnovationScore != null || !project.aiVerdict.isNullOrBlank()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD8B4FE))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF7E22CE), modifier = Modifier.size(16.dp))
                                        Text("AI Innovation Evaluation", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge, color = Color(0xFF581C87))
                                    }
                                    if (project.aiInnovationScore != null) {
                                        Text("${project.aiInnovationScore}/100", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = Color(0xFF7E22CE))
                                    }
                                }

                                if (!project.aiVerdict.isNullOrBlank()) {
                                    Text(project.aiVerdict!!, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = Color(0xFF6B21A8))
                                }

                                if (!project.aiSuggestions.isNullOrBlank()) {
                                    Text("Suggested Enhancements:\n${project.aiSuggestions}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF374151), maxLines = 4, overflow = TextOverflow.Ellipsis)
                                }

                                TextButton(
                                    onClick = {
                                        val combined = buildString {
                                            if (studentFeedback.isNotBlank()) append(studentFeedback).append("\n\n")
                                            append("AI Evaluation: ").append(project.aiVerdict ?: "Assessed").append("\n")
                                            if (!project.aiSuggestions.isNullOrBlank()) {
                                                append("Recommended Enhancements to Add:\n").append(project.aiSuggestions)
                                            }
                                        }
                                        studentFeedback = combined
                                    },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF7E22CE))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Use AI Suggestions in Feedback", fontSize = 11.sp, color = Color(0xFF7E22CE))
                                }
                            }
                        }
                    }
                }

                // Student Feedback Field
                item {
                    OutlinedTextField(
                        value = studentFeedback,
                        onValueChange = { studentFeedback = it },
                        label = { Text("Feedback for Student *") },
                        placeholder = { Text("Provide encouraging feedback or specify required improvements...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_student_feedback_input"),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 3
                    )
                }

                // Private Reviewer Notes
                item {
                    OutlinedTextField(
                        value = privateNotes,
                        onValueChange = { privateNotes = it },
                        label = { Text("Private Reviewer Notes (Internal)") },
                        placeholder = { Text("Evaluation notes visible only to the review board...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_private_notes_input"),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 2
                    )
                }

                // Award Badge
                item {
                    ExposedDropdownMenuBox(
                        expanded = showBadgeDropdown,
                        onExpandedChange = { showBadgeDropdown = !showBadgeDropdown }
                    ) {
                        OutlinedTextField(
                            value = selectedBadge,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Award Distinction Badge") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showBadgeDropdown) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("admin_badge_dropdown"),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = showBadgeDropdown,
                            onDismissRequest = { showBadgeDropdown = false }
                        ) {
                            badgesList.forEach { badge ->
                                DropdownMenuItem(
                                    text = { Text(badge) },
                                    onClick = {
                                        selectedBadge = badge
                                        showBadgeDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Feature Toggle
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Feature on Home Feed", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                            Text("Spotlight this innovation on the main screen", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = isFeatured,
                            onCheckedChange = { isFeatured = it },
                            modifier = Modifier.testTag("admin_feature_switch")
                        )
                    }
                }
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Action Buttons Row 1: Approve & Publish
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val badge = if (selectedBadge == "None") null else selectedBadge
                            onSubmitDecision("APPROVED", studentFeedback, privateNotes, badge, isFeatured)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_approve_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Approve")
                    }

                    Button(
                        onClick = {
                            val badge = if (selectedBadge == "None") null else selectedBadge
                            onSubmitDecision("PUBLISHED", studentFeedback, privateNotes, badge, isFeatured)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_publish_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Publish")
                    }
                }

                // Action Buttons Row 2: Request Changes & Reject
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val badge = if (selectedBadge == "None") null else selectedBadge
                            onSubmitDecision("CHANGES_REQUESTED", studentFeedback, privateNotes, badge, isFeatured)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_request_changes_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Req. Changes", color = Color(0xFFEA580C))
                    }

                    OutlinedButton(
                        onClick = {
                            val badge = if (selectedBadge == "None") null else selectedBadge
                            onSubmitDecision("REJECTED", studentFeedback, privateNotes, badge, isFeatured)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_reject_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Reject", color = Color(0xFFDC2626))
                    }
                }
            }
        },
        dismissButton = {}
    )
}
