package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.InnovationBadge
import com.example.ui.components.BadgeChip
import com.example.ui.components.StatusBadge
import com.example.ui.viewmodel.InnovateXViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProjectDetailScreen(
    projectId: String,
    viewModel: InnovateXViewModel,
    onNavigateBack: () -> Unit,
    onOpenAdminReview: (String) -> Unit,
    onEditProject: () -> Unit,
    modifier: Modifier = Modifier
) {
    val project by viewModel.selectedProject.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var selectedPhotoIndex by remember { mutableIntStateOf(0) }
    var isVideoPlaying by remember { mutableStateOf(false) }

    if (project == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .testTag("project_detail_loading"),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading project details...", style = MaterialTheme.typography.bodyMedium)
        }
        return
    }

    val currentProj = project!!
    val photos = currentProj.photoUrls.ifEmpty {
        listOf("https://images.unsplash.com/photo-1581092335397-9583fe92d232?w=800&auto=format&fit=crop&q=80")
    }

    val isOwner = currentUser?.id == currentProj.ownerId
    val isAdmin = currentUser?.isAdmin == true

    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val formattedDate = remember(currentProj.createdAt) {
        dateFormatter.format(Date(currentProj.createdAt))
    }

    Box(modifier = modifier.fillMaxSize().testTag("project_detail_screen")) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Media Hero Gallery
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    AsyncImage(
                        model = photos.getOrNull(selectedPhotoIndex) ?: photos.first(),
                        contentDescription = currentProj.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradient Scrim
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.5f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.75f)
                                    )
                                )
                            )
                    )

                    // Top Action Bar Overlay
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (isOwner && (currentProj.status == "DRAFT" || currentProj.status == "CHANGES_REQUESTED")) {
                                IconButton(
                                    onClick = onEditProject,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.5f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Project",
                                        tint = Color.White
                                    )
                                }
                            }

                            StatusBadge(status = currentProj.projectStatus)
                        }
                    }

                    // Bottom Carousel Thumbnail Selector
                    if (photos.size > 1) {
                        LazyRow(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(photos.indices.toList()) { idx ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        2.dp,
                                        if (selectedPhotoIndex == idx) MaterialTheme.colorScheme.primary else Color.Transparent
                                    ),
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedPhotoIndex = idx }
                                ) {
                                    AsyncImage(
                                        model = photos[idx],
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Title & Core Metadata
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = currentProj.category,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        if (!currentProj.badgeAwarded.isNullOrBlank()) {
                            BadgeChip(badgeTitle = currentProj.badgeAwarded!!)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentProj.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Creator info with privacy safety
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentProj.ownerName.take(1),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Column {
                            Text(
                                text = currentProj.ownerName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${currentProj.school} • ${currentProj.cityState}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Metrics row: Views, Likes, Date
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${currentProj.viewsCount} views", style = MaterialTheme.typography.bodySmall)
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.clickable { viewModel.likeProject(currentProj.id) }
                            ) {
                                Icon(imageVector = Icons.Default.Favorite, contentDescription = "Likes", modifier = Modifier.size(16.dp), tint = Color(0xFFEF4444))
                                Text("${currentProj.likesCount} likes", style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        Text(text = "Submitted $formattedDate", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // AI Innovation Review Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .testTag("ai_innovation_review_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFAF5FF)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        Color(0xFFC084FC).copy(alpha = 0.6f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFF7E22CE)
                                )
                                Text(
                                    text = "AI Innovation Review",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFF581C87)
                                )
                            }

                            if (currentProj.aiInnovationScore != null) {
                                Surface(
                                    color = Color(0xFF7E22CE),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "${currentProj.aiInnovationScore}/100",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (!currentProj.aiVerdict.isNullOrBlank() || !currentProj.aiAnalysis.isNullOrBlank()) {
                            if (!currentProj.aiVerdict.isNullOrBlank()) {
                                Surface(
                                    color = Color(0xFFE9D5FF),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = currentProj.aiVerdict!!,
                                        color = Color(0xFF6B21A8),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            if (!currentProj.aiAnalysis.isNullOrBlank()) {
                                Text(
                                    text = "Evaluation Summary:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF581C87)
                                )
                                Text(
                                    text = currentProj.aiAnalysis!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            if (!currentProj.aiSuggestions.isNullOrBlank()) {
                                Text(
                                    text = "💡 Is Mein Kya Naya Add Karna Hai (Suggested Enhancements):",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF7E22CE)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentProj.aiSuggestions!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 18.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = { viewModel.reanalyzeProjectWithAi(currentProj.id) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF7E22CE))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Re-Analyze with AI", fontSize = 12.sp, color = Color(0xFF7E22CE))
                            }
                        } else {
                            Text(
                                text = "Is project ka AI analysis abhi run nahi hua hai. Niche click karke AI se review aur naye enhancement ideas hasil karein.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { viewModel.reanalyzeProjectWithAi(currentProj.id) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E22CE)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Run AI Innovation Review & Suggestions")
                            }
                        }
                    }
                }
            }

            // Reviewer Feedback Section (If available)
            if (!currentProj.reviewerFeedback.isNullOrBlank()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                            .testTag("reviewer_feedback_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when (currentProj.status) {
                                "CHANGES_REQUESTED" -> Color(0xFFFFF7ED)
                                "APPROVED", "PUBLISHED" -> Color(0xFFECFDF5)
                                "REJECTED" -> Color(0xFFFEF2F2)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            when (currentProj.status) {
                                "CHANGES_REQUESTED" -> Color(0xFFF97316)
                                "APPROVED", "PUBLISHED" -> Color(0xFF10B981)
                                "REJECTED" -> Color(0xFFEF4444)
                                else -> MaterialTheme.colorScheme.outline
                            }.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Feedback,
                                    contentDescription = null,
                                    tint = when (currentProj.status) {
                                        "CHANGES_REQUESTED" -> Color(0xFFEA580C)
                                        "APPROVED", "PUBLISHED" -> Color(0xFF059669)
                                        "REJECTED" -> Color(0xFFDC2626)
                                        else -> MaterialTheme.colorScheme.primary
                                    }
                                )
                                Text(
                                    text = "Official Reviewer Evaluation",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }

                            if (!currentProj.reviewerName.isNullOrBlank()) {
                                Text(
                                    text = "Reviewed by ${currentProj.reviewerName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "“${currentProj.reviewerFeedback}”",
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
            }

            // Private Reviewer Notes (Visible ONLY to Admin)
            if (isAdmin && !currentProj.privateReviewerNotes.isNullOrBlank()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFFA5B4FC), modifier = Modifier.size(16.dp))
                                Text("Private Admin/Reviewer Notes", color = Color(0xFFA5B4FC), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = currentProj.privateReviewerNotes!!, color = Color.White, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            // Video Demo Player (Mock / Preview)
            if (currentProj.videoUrl.isNotBlank()) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                        Text(
                            text = "Project Demonstration Video",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { isVideoPlaying = !isVideoPlaying },
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = photos.firstOrNull(),
                                    contentDescription = "Video Thumbnail",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.PlayCircleFilled,
                                            contentDescription = "Play Video",
                                            tint = Color.White,
                                            modifier = Modifier.size(60.dp)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = if (isVideoPlaying) "Demo Video Streaming (Simulated)" else "Watch Prototype Demonstration",
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section: Problem Statement
            item {
                DetailContentSection(
                    title = "Problem Statement",
                    content = currentProj.problem
                )
            }

            // Section: Proposed Solution
            item {
                DetailContentSection(
                    title = "Proposed Solution",
                    content = currentProj.solution
                )
            }

            // Section: How It Works
            item {
                DetailContentSection(
                    title = "How the Project Works",
                    content = currentProj.working
                )
            }

            // Section: What is Innovative
            if (currentProj.innovation.isNotBlank()) {
                item {
                    DetailContentSection(
                        title = "What is Innovative About It",
                        content = currentProj.innovation
                    )
                }
            }

            // Section: Components & Cost
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    Text(
                        text = "Hardware & Implementation Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (currentProj.components.isNotBlank()) {
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Build, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                    Column {
                                        Text("Components & Materials", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                        Text(currentProj.components, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }

                            if (currentProj.estimatedCost.isNotBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.AttachMoney, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                                    Text("Approximate Cost: ${currentProj.estimatedCost}", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                                }
                            }

                            if (currentProj.documentUrl.isNotBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                                    Text("Attached File: ${currentProj.documentUrl}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button for Admins: "Review This Project"
        if (isAdmin) {
            FloatingActionButton(
                onClick = { onOpenAdminReview(currentProj.id) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
                    .testTag("admin_review_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.RateReview, contentDescription = "Review Project")
                    Text("Review Project", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DetailContentSection(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
        )
    }
}
