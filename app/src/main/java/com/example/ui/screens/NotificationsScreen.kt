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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.ui.viewmodel.InnovateXViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationsScreen(
    viewModel: InnovateXViewModel,
    onNavigateToProject: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadNotifCount.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("notifications_screen")
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (unreadCount > 0) "$unreadCount unread updates" else "All caught up",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (notifications.isNotEmpty()) {
                Row {
                    IconButton(
                        onClick = { viewModel.markAllNotificationsRead() },
                        modifier = Modifier.testTag("mark_all_read_button")
                    ) {
                        Icon(imageVector = Icons.Default.DoneAll, contentDescription = "Mark All Read", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(
                        onClick = { viewModel.clearNotifications() },
                        modifier = Modifier.testTag("clear_notifications_button")
                    ) {
                        Icon(imageVector = Icons.Default.ClearAll, contentDescription = "Clear All", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "No Notifications",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "You'll be notified when your innovations are reviewed or receive distinctions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(notifications, key = { it.id }) { notif ->
                    NotificationItemCard(
                        notification = notif,
                        onClick = {
                            viewModel.markNotificationRead(notif.id)
                            notif.projectId?.let { onNavigateToProject(it) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationItemCard(
    notification: NotificationItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon: ImageVector, iconColor: Color) = when (notification.notificationType) {
        NotificationType.PROJECT_APPROVED -> Pair(Icons.Default.CheckCircle, Color(0xFF10B981))
        NotificationType.CHANGES_REQUESTED -> Pair(Icons.Default.ChangeCircle, Color(0xFFF97316))
        NotificationType.PROJECT_REJECTED -> Pair(Icons.Default.Cancel, Color(0xFFEF4444))
        NotificationType.PROJECT_UNDER_REVIEW -> Pair(Icons.Default.HourglassTop, Color(0xFF3B82F6))
        NotificationType.BADGE_RECEIVED -> Pair(Icons.Default.WorkspacePremium, Color(0xFFEAB308))
        NotificationType.PROJECT_FEATURED -> Pair(Icons.Default.Star, Color(0xFF8B5CF6))
        NotificationType.SUBMISSION_RECEIVED -> Pair(Icons.Default.Send, Color(0xFF6366F1))
        NotificationType.AI_REVIEW_COMPLETED -> Pair(Icons.Default.AutoAwesome, Color(0xFF9333EA))
        NotificationType.PEER_REVIEW_RECEIVED -> Pair(Icons.Default.Star, Color(0xFFF59E0B))
        NotificationType.SYSTEM, null -> Pair(Icons.Default.CheckCircle, Color(0xFF0284C7))
        else -> Pair(Icons.Default.CheckCircle, Color(0xFF0284C7))
    }

    val dateFormatter = remember { SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault()) }
    val formattedTime = remember(notification.createdAt) {
        dateFormatter.format(Date(notification.createdAt))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("notification_item_${notification.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.isRead) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (!notification.isRead) 2.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.SemiBold
                    )

                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
