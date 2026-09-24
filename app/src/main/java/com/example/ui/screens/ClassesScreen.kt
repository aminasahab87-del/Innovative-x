package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.BorderStroke
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.LiveClassSession
import com.example.ui.viewmodel.InnovateXViewModel

@Composable
fun ClassesScreen(
    viewModel: InnovateXViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val liveClasses by viewModel.allLiveClasses.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val latestPaymentReq by viewModel.latestUserPaymentRequest.collectAsStateWithLifecycle()
    val paymentConfig by viewModel.paymentConfig.collectAsStateWithLifecycle()

    // Strict Access Control: Only users with APPROVED payment (or Admin) can view scheduled classes and Zoom credentials
    val isAdmin = currentUser?.role == "ADMIN"
    val isPaymentApproved = latestPaymentReq != null && latestPaymentReq?.status == "APPROVED"
    val isActuallyUnlocked = isAdmin || isPaymentApproved

    var txnIdInput by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var verificationError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("classes_screen")
    ) {
        if (!isActuallyUnlocked) {
            // 🔒 PAYWALL / PAYMENT LOCK SCREEN
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // If Payment Request is PENDING approval
                if (latestPaymentReq?.status == "PENDING") {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Payment Submitted — Pending Admin Approval",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }

                                Text(
                                    text = "Transaction ID: ${latestPaymentReq?.transactionId}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = "Your payment of 250 PKR has been received and sent to Admin for review. Zoom Live Classes will automatically unlock as soon as Admin approves your request!",
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))

                    // Lock Icon Header
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Payment Locked",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Zoom Live Classes Locked",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "Join interactive live STEM & robotics sessions with expert instructors.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }

                // Pricing Banner Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "SPECIAL SUBSCRIPTION PLAN",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "${paymentConfig.feeAmountPkr} PKR",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "/ ${paymentConfig.validityDays} Days Access",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "✓ Full Access to All Zoom Live Classes & Recording Links\n✓ Direct Meeting ID & Password Access\n✓ Q&A with Instructors for ${paymentConfig.validityDays} Days",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.95f),
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Payment Scanner QR Code Display Card (Custom Gallery Scanner or Default)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Scan QR Code to Pay ${paymentConfig.feeAmountPkr} PKR",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Payment Scanner Image (Custom Gallery Scanner or Official Default)
                            if (!paymentConfig.scannerImageUri.isNullOrBlank()) {
                                AsyncImage(
                                    model = paymentConfig.scannerImageUri,
                                    contentDescription = "Payment QR Scanner Code",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(230.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(2.dp, MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.user_payment_qr_scanner),
                                    contentDescription = "Official Payment QR Scanner Code",
                                    modifier = Modifier
                                        .size(230.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(2.dp, MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Payment Account Details
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "${paymentConfig.paymentMethod} / Bank Account:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${paymentConfig.accountNumber} (${paymentConfig.accountTitle})",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        IconButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(paymentConfig.accountNumber))
                                                Toast.makeText(context, "Account Number Copied!", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "Copy",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Verify Payment Input Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Enter Payment TRX # to Send to Admin:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = txnIdInput,
                                onValueChange = {
                                    txnIdInput = it
                                    verificationError = null
                                },
                                label = { Text("Transaction ID / TRX #") },
                                placeholder = { Text("e.g. TRX98231024") },
                                isError = verificationError != null,
                                supportingText = {
                                    if (verificationError != null) {
                                        Text(verificationError!!, color = MaterialTheme.colorScheme.error)
                                    } else {
                                        Text("After scanning QR, enter Transaction ID above. Admin will verify and unlock your access.", style = MaterialTheme.typography.labelSmall)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_txn_id"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Button(
                                onClick = {
                                    if (txnIdInput.isBlank()) {
                                        verificationError = "Please enter Transaction ID or TRX #"
                                    } else {
                                        isSubmitting = true
                                        viewModel.submitPaymentRequest(txnIdInput) { success, msg ->
                                            isSubmitting = false
                                            if (!success) {
                                                verificationError = msg
                                            } else {
                                                txnIdInput = ""
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("verify_payment_btn"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Send Payment TRX to Admin for Approval")
                            }
                        }
                    }
                }
            }
        } else {
            // 🎉 UNLOCKED LIVE CLASSES HUB
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Subscription Active Header
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isAdmin) "Admin Access Active" else "10 Days Access Active",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Zoom Meeting ID & Passwords Unlocked!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }

                            if (isAdmin) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Admin Verified",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Upcoming & Live Zoom Sessions (${liveClasses.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (liveClasses.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No Live Classes Scheduled Yet", fontWeight = FontWeight.Bold)
                                Text(
                                    text = "Classes scheduled by Admin will appear here with live countdown and Zoom credentials.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(liveClasses) { session ->
                        LiveClassCard(
                            session = session,
                            onCopyMeetingId = {
                                clipboardManager.setText(AnnotatedString(session.zoomMeetingId))
                                Toast.makeText(context, "Meeting ID Copied!", Toast.LENGTH_SHORT).show()
                            },
                            onCopyPassword = {
                                clipboardManager.setText(AnnotatedString(session.zoomPassword))
                                Toast.makeText(context, "Password Copied!", Toast.LENGTH_SHORT).show()
                            },
                            onJoinZoom = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(session.zoomLink))
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    Toast.makeText(context, "Opening Zoom Link...", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LiveClassCard(
    session: LiveClassSession,
    onCopyMeetingId: () -> Unit,
    onCopyPassword: () -> Unit,
    onJoinZoom: () -> Unit
) {
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(session.scheduledTimestamp) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000)
        }
    }

    val remainingMillis = session.scheduledTimestamp - currentTime
    val isLiveNow = session.isLiveNow || (session.scheduledTimestamp > 0 && remainingMillis <= 0 && currentTime <= session.scheduledTimestamp + session.durationMinutes * 60 * 1000L)
    val isEnded = session.scheduledTimestamp > 0 && (currentTime > session.scheduledTimestamp + session.durationMinutes * 60 * 1000L)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                Surface(
                    color = if (isLiveNow) Color(0xFFEF4444) else MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isLiveNow) "• LIVE NOW" else session.subject,
                        color = if (isLiveNow) Color.White else MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = session.dateTimeText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = session.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Instructor: ${session.instructorName} • Duration: ${session.durationMinutes} Mins",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = session.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // ⏳ Real-Time Scheduled Class Countdown Timer Section
            if (isLiveNow) {
                Surface(
                    color = Color(0xFFEF4444).copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, Color(0xFFEF4444)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444))
                            )
                            Text(
                                text = "🔴 CLASS IS LIVE NOW!",
                                color = Color(0xFFDC2626),
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        }
                        Text(
                            text = "In Session",
                            color = Color(0xFFDC2626),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            } else if (remainingMillis > 0) {
                val totalSeconds = remainingMillis / 1000
                val days = totalSeconds / (24 * 3600)
                val hours = (totalSeconds % (24 * 3600)) / 3600
                val minutes = (totalSeconds % 3600) / 60
                val seconds = totalSeconds % 60

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Class Starts In (Live Countdown):",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (days > 0) {
                                LiveTimerBlock(value = String.format("%02d", days), label = "Days")
                                Text(":", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            }
                            LiveTimerBlock(value = String.format("%02d", hours), label = "Hours")
                            Text(":", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            LiveTimerBlock(value = String.format("%02d", minutes), label = "Mins")
                            Text(":", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            LiveTimerBlock(value = String.format("%02d", seconds), label = "Secs")
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Zoom room credentials unlocked • Join when timer finishes",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else if (isEnded) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Class Session Ended • Recording & Notes Available",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Zoom Credentials Unlocked Box
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Zoom Credentials (Unlocked):",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Meeting ID: ${session.zoomMeetingId}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        IconButton(onClick = onCopyMeetingId, modifier = Modifier.size(24.dp)) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy ID", modifier = Modifier.size(14.dp))
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Password: ${session.zoomPassword}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        IconButton(onClick = onCopyPassword, modifier = Modifier.size(24.dp)) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy Password", modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            Button(
                onClick = onJoinZoom,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLiveNow) Color(0xFFEF4444) else Color(0xFF2D8CFF)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Icon(
                    imageVector = if (isLiveNow) Icons.Default.Videocam else Icons.Default.VideoCall,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isLiveNow) "🚀 Join Live Zoom Class Now" else "Join Zoom Meeting",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun LiveTimerBlock(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.width(44.dp).height(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = value,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

