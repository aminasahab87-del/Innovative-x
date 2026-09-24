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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
    val isUnlocked by viewModel.isClassesUnlocked.collectAsStateWithLifecycle()
    val liveClasses by viewModel.allLiveClasses.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val latestPaymentReq by viewModel.latestUserPaymentRequest.collectAsStateWithLifecycle()

    val isActuallyUnlocked = isUnlocked || latestPaymentReq?.status == "APPROVED"

    var showCreateClassDialog by remember { mutableStateOf(false) }
    var txnIdInput by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var verificationError by remember { mutableStateOf<String?>(null) }

    // Dialog for creating/starting a Zoom Live class
    if (showCreateClassDialog) {
        CreateZoomClassDialog(
            onDismiss = { showCreateClassDialog = false },
            onCreate = { title, instructor, subject, dateTime, meetingId, password, link, desc ->
                viewModel.createLiveClass(
                    title, instructor, subject, dateTime, meetingId, password, link, desc
                ) { success, err ->
                    if (success) {
                        showCreateClassDialog = false
                        Toast.makeText(context, "Live Zoom Class Created!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, err ?: "Error creating class", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

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
                                    text = "250 PKR",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "/ 10 Days Access",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "✓ Full Access to All Zoom Live Classes & Recording Links\n✓ Direct Meeting ID & Password Access\n✓ Q&A with Instructors for 10 Days",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.95f),
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Payment Scanner QR Code Display Card (Official Uploaded Scanner)
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
                                    text = "Scan QR Code to Pay 250 PKR",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Payment Scanner Image (User Uploaded Official QR Scanner)
                            Image(
                                painter = painterResource(id = R.drawable.user_payment_qr_scanner),
                                contentDescription = "Official Payment QR Scanner Code",
                                modifier = Modifier
                                    .size(220.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(2.dp, MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Payment Account Details
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "EasyPaisa / JazzCash / Bank Account:",
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
                                            text = "0300-1234567 (InnovateX Classes)",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        IconButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString("03001234567"))
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

                            // Demo Direct Unlock Button
                            OutlinedButton(
                                onClick = {
                                    viewModel.unlockLiveClassesDirectly()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Direct Demo Unlock (Bypass Admin)", fontSize = 12.sp)
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
                                        text = "10 Days Access Active",
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

                            Button(
                                onClick = { showCreateClassDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Start Class", fontSize = 12.sp)
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
                                    text = "Click 'Start Class' above to schedule or share Zoom credentials.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    color = if (session.isLiveNow) Color(0xFFEF4444) else MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (session.isLiveNow) "• LIVE NOW" else session.subject,
                        color = if (session.isLiveNow) Color.White else MaterialTheme.colorScheme.onPrimaryContainer,
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
                    text = "Instructor: ${session.instructorName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = session.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D8CFF)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Icon(imageVector = Icons.Default.VideoCall, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Join Zoom Meeting", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CreateZoomClassDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, instructor: String, subject: String, dateTime: String, meetingId: String, password: String, link: String, desc: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var instructor by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var dateTimeText by remember { mutableStateOf("Today, 6:00 PM") }
    var zoomMeetingId by remember { mutableStateOf("") }
    var zoomPassword by remember { mutableStateOf("") }
    var zoomLink by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Start / Schedule Zoom Class") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Class Title / Topic") },
                        placeholder = { Text("e.g. Microcontroller Workshop") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = instructor,
                        onValueChange = { instructor = it },
                        label = { Text("Instructor Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = subject,
                            onValueChange = { subject = it },
                            label = { Text("Subject / Category") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = dateTimeText,
                            onValueChange = { dateTimeText = it },
                            label = { Text("Date & Time") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = zoomMeetingId,
                            onValueChange = { zoomMeetingId = it },
                            label = { Text("Zoom Meeting ID") },
                            placeholder = { Text("842 9102 5521") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = zoomPassword,
                            onValueChange = { zoomPassword = it },
                            label = { Text("Passcode") },
                            placeholder = { Text("innovate123") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = zoomLink,
                        onValueChange = { zoomLink = it },
                        label = { Text("Direct Zoom Link") },
                        placeholder = { Text("https://zoom.us/j/84291025521?pwd=...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description / What students will learn") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && zoomMeetingId.isNotBlank()) {
                        val finalLink = if (zoomLink.isBlank()) "https://zoom.us/j/${zoomMeetingId.replace(" ", "")}" else zoomLink
                        onCreate(title, instructor.ifBlank { "Instructor" }, subject.ifBlank { "STEM" }, dateTimeText, zoomMeetingId, zoomPassword, finalLink, description)
                    }
                }
            ) {
                Text("Publish Zoom Class")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
