package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.InnovationBadge
import com.example.data.model.Review
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
    val context = LocalContext.current
    val project by viewModel.selectedProject.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val reviews by viewModel.selectedProjectReviews.collectAsStateWithLifecycle()

    var selectedPhotoIndex by remember { mutableIntStateOf(0) }
    var isVideoPlaying by remember { mutableStateOf(false) }
    var showFullScreenLightbox by remember { mutableStateOf(false) }
    var showWriteReviewDialog by remember { mutableStateOf(false) }

    // Ensure the project is loaded when entering this screen
    LaunchedEffect(projectId) {
        viewModel.selectProject(projectId)
    }

    // Loading State
    if (project == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .testTag("project_detail_loading"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp)
                )
                Text(
                    text = "Loading project details...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedButton(onClick = onNavigateBack) {
                    Text("Return to Gallery")
                }
            }
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

    val peerReviews = remember(reviews) {
        reviews.filter { it.action == "PEER_REVIEW" || it.rating > 0 }
    }
    val totalReviews = peerReviews.size
    val averageRating = remember(peerReviews) {
        if (peerReviews.isNotEmpty()) peerReviews.map { it.rating.toDouble() }.average() else 0.0
    }

    // Full-Screen Image Lightbox Modal
    if (showFullScreenLightbox) {
        Dialog(
            onDismissRequest = { showFullScreenLightbox = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                AsyncImage(
                    model = photos.getOrNull(selectedPhotoIndex) ?: photos.first(),
                    contentDescription = "Full Screen Project Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 60.dp)
                )

                // Top Controls Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "📸 Photo ${selectedPhotoIndex + 1} of ${photos.size}",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    IconButton(
                        onClick = { showFullScreenLightbox = false },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.65f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Fullscreen",
                            tint = Color.White
                        )
                    }
                }

                // Prev / Next Navigation Arrows
                if (photos.size > 1) {
                    if (selectedPhotoIndex > 0) {
                        IconButton(
                            onClick = { selectedPhotoIndex-- },
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(12.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.65f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.NavigateBefore,
                                contentDescription = "Previous Image",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    if (selectedPhotoIndex < photos.size - 1) {
                        IconButton(
                            onClick = { selectedPhotoIndex++ },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(12.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.65f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.NavigateNext,
                                contentDescription = "Next Image",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().testTag("project_detail_screen")) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            // ================= 1. MEDIA HERO GALLERY =================
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    AsyncImage(
                        model = photos.getOrNull(selectedPhotoIndex) ?: photos.first(),
                        contentDescription = currentProj.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { showFullScreenLightbox = true }
                    )

                    // Gradient Scrim for Top & Bottom readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.6f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.8f)
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
                                contentDescription = "Back to Gallery",
                                tint = Color.White
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Fullscreen zoom button
                            IconButton(
                                onClick = { showFullScreenLightbox = true },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.5f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fullscreen,
                                    contentDescription = "View Fullscreen Image",
                                    tint = Color.White
                                )
                            }

                            // Share Project Button
                            IconButton(
                                onClick = {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, currentProj.title)
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "Check out this student innovation on InnovateX!\n\n" +
                                            "Title: ${currentProj.title}\n" +
                                            "Category: ${currentProj.category}\n" +
                                            "Creator: ${currentProj.ownerName} (${currentProj.school})\n\n" +
                                            "Solution: ${currentProj.solution.take(200)}...\n\n" +
                                            "Explore more young innovator projects on InnovateX!"
                                        )
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Project"))
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.5f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share Project",
                                    tint = Color.White
                                )
                            }

                            // Owner Edit Action
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

                    // Bottom Image Counter & Thumbnail Carousel
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        if (photos.size > 1) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = Color.Black.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "${selectedPhotoIndex + 1} / ${photos.size} Images",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Text(
                                    text = "Tap to enlarge",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                itemsIndexed(photos) { idx, url ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(
                                            2.dp,
                                            if (selectedPhotoIndex == idx) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.4f)
                                        ),
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { selectedPhotoIndex = idx }
                                    ) {
                                        AsyncImage(
                                            model = url,
                                            contentDescription = "Thumbnail $idx",
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ================= 2. TITLE & CORE METADATA =================
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    // Category & Award Badges
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
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }

                        if (!currentProj.badgeAwarded.isNullOrBlank()) {
                            BadgeChip(badgeTitle = currentProj.badgeAwarded)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Project Title
                    Text(
                        text = currentProj.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 32.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Creator Info Card
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentProj.ownerName.take(1).uppercase(),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentProj.ownerName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${currentProj.school}${if (currentProj.gradeClass.isNotBlank()) " • Class ${currentProj.gradeClass}" else ""}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (currentProj.cityState.isNotBlank()) {
                                    Text(
                                        text = "📍 ${currentProj.cityState}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Metrics Row: Views, Likes, Date
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text("${currentProj.viewsCount} views", style = MaterialTheme.typography.bodySmall)
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { viewModel.likeProject(currentProj.id) }
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Likes",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFFEF4444)
                                )
                                Text(
                                    "${currentProj.likesCount} likes",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (totalReviews > 0) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { showWriteReviewDialog = true }
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Rating",
                                        modifier = Modifier.size(16.dp),
                                        tint = Color(0xFFF59E0B)
                                    )
                                    Text(
                                        "${String.format(Locale.getDefault(), "%.1f", averageRating)}★ ($totalReviews)",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFB45309)
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Submitted $formattedDate",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ================= 3. PROJECT PHOTO GALLERY SECTION =================
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
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
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Project Photo Gallery (${photos.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        TextButton(onClick = { showFullScreenLightbox = true }) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Full Screen", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(photos) { idx, url ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier
                                    .size(width = 160.dp, height = 110.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable {
                                        selectedPhotoIndex = idx
                                        showFullScreenLightbox = true
                                    }
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = "Project photo ${idx + 1}",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Surface(
                                        color = Color.Black.copy(alpha = 0.65f),
                                        shape = RoundedCornerShape(topStart = 8.dp),
                                        modifier = Modifier.align(Alignment.BottomEnd)
                                    ) {
                                        Text(
                                            text = "#${idx + 1}",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ================= 4. DETAILED PROJECT DESCRIPTION & SECTIONS =================

            // Problem Statement
            if (currentProj.problem.isNotBlank()) {
                item {
                    DetailContentCard(
                        title = "Problem Statement",
                        icon = Icons.Default.Description,
                        content = currentProj.problem,
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                }
            }

            // Proposed Solution
            if (currentProj.solution.isNotBlank()) {
                item {
                    DetailContentCard(
                        title = "Proposed Solution",
                        icon = Icons.Default.Lightbulb,
                        content = currentProj.solution,
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                }
            }

            // How the Project Works
            if (currentProj.working.isNotBlank()) {
                item {
                    DetailContentCard(
                        title = "How the Project Works (Mechanism)",
                        icon = Icons.Default.AssignmentTurnedIn,
                        content = currentProj.working,
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                }
            }

            // What is Innovative About It
            if (currentProj.innovation.isNotBlank()) {
                item {
                    DetailContentCard(
                        title = "What is Innovative About It",
                        icon = Icons.Default.AutoAwesome,
                        content = currentProj.innovation,
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                }
            }

            // Hardware & Implementation Details
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    Text(
                        text = "Hardware & Implementation Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            if (currentProj.components.isNotBlank()) {
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Build,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Components & Materials Used",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = currentProj.components,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            if (currentProj.estimatedCost.isNotBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AttachMoney,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Approximate Project Cost:",
                                            fontWeight = FontWeight.SemiBold,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Surface(
                                            color = Color(0xFF10B981).copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = currentProj.estimatedCost,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF047857),
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            if (currentProj.documentUrl.isNotBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Attached Documentation:",
                                            fontWeight = FontWeight.SemiBold,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                        Text(
                                            text = currentProj.documentUrl,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Video Demonstration Section
            if (currentProj.videoUrl.isNotBlank()) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                        Text(
                            text = "Demonstration Video",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp)
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
                                            modifier = Modifier.size(64.dp)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = if (isVideoPlaying) "Demo Video Streaming (Simulated)" else "Watch Prototype Demonstration",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // AI Innovation Review Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .testTag("ai_innovation_review_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                    border = BorderStroke(1.5.dp, Color(0xFFC084FC).copy(alpha = 0.6f))
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
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (!currentProj.aiVerdict.isNullOrBlank() || !currentProj.aiAnalysis.isNullOrBlank()) {
                            if (!currentProj.aiVerdict.isNullOrBlank()) {
                                Surface(
                                    color = Color(0xFFE9D5FF),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = currentProj.aiVerdict,
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
                                    text = currentProj.aiAnalysis,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            if (!currentProj.aiSuggestions.isNullOrBlank()) {
                                Text(
                                    text = "💡 Suggested Enhancements (Is Mein Naya Kya Add Karein):",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF7E22CE)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentProj.aiSuggestions,
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
                                text = "Run AI innovation analysis to evaluate technical novelty and get enhancement suggestions.",
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

            // Reviewer Feedback Section
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
                        border = BorderStroke(
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
                            Text(text = currentProj.privateReviewerNotes, color = Color.White, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            // ================= 5. STUDENT PEER REVIEWS & STAR-RATING SYSTEM =================
            item {
                StudentReviewsSection(
                    project = currentProj,
                    reviews = peerReviews,
                    averageRating = averageRating,
                    currentUserId = currentUser?.id,
                    isAdmin = isAdmin,
                    onOpenWriteReview = { showWriteReviewDialog = true },
                    onDeleteReview = { reviewId -> viewModel.deleteReview(reviewId) }
                )
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

        // Submit Review Modal Dialog
        if (showWriteReviewDialog) {
            SubmitReviewDialog(
                projectTitle = currentProj.title,
                ownerName = currentProj.ownerName,
                reviewerName = currentUser?.name ?: "Student Innovator",
                onDismiss = { showWriteReviewDialog = false },
                onSubmit = { rating, feedback, tip ->
                    viewModel.submitStudentReview(
                        projectId = currentProj.id,
                        rating = rating,
                        feedback = feedback,
                        constructiveTip = tip
                    ) { success, _ ->
                        if (success) {
                            showWriteReviewDialog = false
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun DetailContentCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: String,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun RatingStars(
    rating: Int,
    maxStars: Int = 5,
    starSize: androidx.compose.ui.unit.Dp = 18.dp,
    activeColor: Color = Color(0xFFF59E0B),
    inactiveColor: Color = Color(0xFFD1D5DB),
    isInteractive: Boolean = false,
    onRatingSelected: ((Int) -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        for (i in 1..maxStars) {
            val isFilled = i <= rating
            val icon = if (isFilled) Icons.Default.Star else Icons.Default.StarBorder
            val tint = if (isFilled) activeColor else inactiveColor
            Icon(
                imageVector = icon,
                contentDescription = "$i Stars",
                tint = tint,
                modifier = Modifier
                    .size(starSize)
                    .then(
                        if (isInteractive && onRatingSelected != null) {
                            Modifier
                                .clip(CircleShape)
                                .clickable { onRatingSelected(i) }
                                .padding(2.dp)
                        } else Modifier
                    )
            )
        }
    }
}

@Composable
fun StudentReviewsSection(
    project: com.example.data.model.Project,
    reviews: List<Review>,
    averageRating: Double,
    currentUserId: String?,
    isAdmin: Boolean,
    onOpenWriteReview: () -> Unit,
    onDeleteReview: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalReviews = reviews.size
    val ratingDistribution = remember(reviews) {
        (1..5).associateWith { star -> reviews.count { it.rating == star } }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("student_reviews_section")
    ) {
        // Section Header
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
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Peer Reviews & Ratings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = CircleShape
                ) {
                    Text(
                        text = "$totalReviews",
                        color = Color(0xFFB45309),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Button(
                onClick = onOpenWriteReview,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.testTag("rate_prototype_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Write Review", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Overall Rating Summary Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
            border = BorderStroke(1.dp, Color(0xFFFDE68A)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Column: Big score and star row
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text(
                            text = if (totalReviews > 0) String.format(Locale.getDefault(), "%.1f", averageRating) else "—",
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFB45309)
                        )
                        RatingStars(
                            rating = if (totalReviews > 0) averageRating.toInt().coerceIn(1, 5) else 0,
                            starSize = 16.dp,
                            activeColor = Color(0xFFF59E0B)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (totalReviews > 0) "$totalReviews reviews" else "No reviews yet",
                            fontSize = 11.sp,
                            color = Color(0xFF92400E)
                        )
                    }

                    // Divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(80.dp)
                            .background(Color(0xFFFDE68A))
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Right Column: Breakdown bars (5 stars down to 1)
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (star in 5 downTo 1) {
                            val count = ratingDistribution[star] ?: 0
                            val fraction = if (totalReviews > 0) count.toFloat() / totalReviews else 0f
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "${star}★",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E),
                                    modifier = Modifier.width(20.dp)
                                )
                                LinearProgressIndicator(
                                    progress = { fraction },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = Color(0xFFF59E0B),
                                    trackColor = Color(0xFFFDE68A).copy(alpha = 0.5f),
                                )
                                Text(
                                    text = "$count",
                                    fontSize = 11.sp,
                                    color = Color(0xFF92400E),
                                    modifier = Modifier.width(18.dp),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Community Constructive Feedback Callout
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.TipsAndUpdates,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Help student creators innovate! Test the prototype concept, share what worked, and provide constructive improvement suggestions.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Reviews List or Empty State
        if (reviews.isEmpty()) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RateReview,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "No Peer Reviews Yet",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Be the first student or peer to review '${project.title}' and provide constructive feedback!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = onOpenWriteReview,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add First Review")
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                reviews.forEach { rev ->
                    PeerReviewCard(
                        review = rev,
                        isOwnerOrAdmin = currentUserId == rev.reviewerId || isAdmin,
                        onDelete = { onDeleteReview(rev.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun PeerReviewCard(
    review: Review,
    isOwnerOrAdmin: Boolean,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    val dateText = remember(review.timestamp) { dateFormatter.format(Date(review.timestamp)) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Avatar, Name, Role, Rating, Date, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0E7FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = review.reviewerName.take(1).uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3730A3),
                            fontSize = 16.sp
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = review.reviewerName,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Surface(
                                color = Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = review.reviewerRole,
                                    fontSize = 10.sp,
                                    color = Color(0xFF475569),
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            RatingStars(
                                rating = review.rating,
                                starSize = 14.dp,
                                activeColor = Color(0xFFF59E0B)
                            )
                            Text(
                                text = "• $dateText",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (isOwnerOrAdmin) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete review",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Student Feedback
            Text(
                text = review.studentFeedback,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )

            // Constructive Tip / Improvement Suggestion Callout
            if (review.constructiveTip.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TipsAndUpdates,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "💡 Constructive Suggestion / Next Step:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFF92400E)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = review.constructiveTip,
                                fontSize = 12.sp,
                                color = Color(0xFF78350F),
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubmitReviewDialog(
    projectTitle: String,
    ownerName: String,
    reviewerName: String,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, feedback: String, constructiveTip: String) -> Unit
) {
    var rating by remember { mutableIntStateOf(5) }
    var feedback by remember { mutableStateOf("") }
    var tip by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val ratingLabels = mapOf(
        1 to "⭐ Needs Major Rework or Testing",
        2 to "⭐⭐ Promising Concept, Needs Notable Fixes",
        3 to "⭐⭐⭐ Good Prototype with Solid Foundations",
        4 to "⭐⭐⭐⭐ Very Impressive & Functional Prototype",
        5 to "⭐⭐⭐⭐⭐ Outstanding STEM Innovation! Ready to Showcase"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Rate & Review Prototype",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Giving peer feedback for $ownerName",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Star Rating Picker
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFFBEB))
                        .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tap to rate prototype:",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF92400E),
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    RatingStars(
                        rating = rating,
                        starSize = 34.dp,
                        activeColor = Color(0xFFF59E0B),
                        inactiveColor = Color(0xFFD1D5DB),
                        isInteractive = true,
                        onRatingSelected = { rating = it }
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = ratingLabels[rating] ?: "",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB45309),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Constructive Feedback Text Field
                OutlinedTextField(
                    value = feedback,
                    onValueChange = {
                        feedback = it
                        if (it.isNotBlank()) errorMessage = null
                    },
                    label = { Text("Constructive Feedback *") },
                    placeholder = { Text("What did you like? How does the design, hardware, or concept perform? Share honest feedback...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("review_feedback_input"),
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(12.dp),
                    isError = errorMessage != null
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Constructive Tip Field
                OutlinedTextField(
                    value = tip,
                    onValueChange = { tip = it },
                    label = { Text("💡 Suggestion for Next Step / Improvement (Optional)") },
                    placeholder = { Text("e.g. Add Bluetooth telemetry, add backup power, improve casing...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("review_tip_input"),
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Identity info note
                Text(
                    text = "Posting as: $reviewerName",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Dialog Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            if (feedback.isBlank()) {
                                errorMessage = "Please enter your constructive feedback."
                            } else {
                                onSubmit(rating, feedback, tip)
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        modifier = Modifier.testTag("submit_review_button")
                    ) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Submit Review")
                    }
                }
            }
        }
    }
}
