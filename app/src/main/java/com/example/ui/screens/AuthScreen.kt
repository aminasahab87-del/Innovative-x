package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.InnovateXLogoHeader
import com.example.ui.viewmodel.InnovateXViewModel

@Composable
fun AuthScreen(
    viewModel: InnovateXViewModel,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Sign Up

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Sign Up fields
    var name by remember { mutableStateOf("") }
    var school by remember { mutableStateOf("") }
    var gradeClass by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotPasswordEmail by remember { mutableStateOf("") }
    var forgotPasswordSuccess by remember { mutableStateOf(false) }

    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = {
                showForgotPasswordDialog = false
                forgotPasswordSuccess = false
            },
            title = { Text("Reset Password") },
            text = {
                if (forgotPasswordSuccess) {
                    Text("Password reset instructions have been sent to $forgotPasswordEmail (simulated).")
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Enter your account email to receive a password reset link.")
                        OutlinedTextField(
                            value = forgotPasswordEmail,
                            onValueChange = { forgotPasswordEmail = it },
                            label = { Text("Email Address") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                if (forgotPasswordSuccess) {
                    Button(onClick = {
                        showForgotPasswordDialog = false
                        forgotPasswordSuccess = false
                    }) {
                        Text("Done")
                    }
                } else {
                    Button(
                        onClick = {
                            if (forgotPasswordEmail.isNotBlank()) {
                                forgotPasswordSuccess = true
                            }
                        }
                    ) {
                        Text("Send Link")
                    }
                }
            },
            dismissButton = {
                if (!forgotPasswordSuccess) {
                    TextButton(onClick = { showForgotPasswordDialog = false }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("auth_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))

                // Brand Logo Header
                InnovateXLogoHeader(
                    logoSize = 88.dp,
                    showTagline = true,
                    compactMode = false
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Student Innovation & Science Platform",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Tabs: Login / Sign Up
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.clip(RoundedCornerShape(14.dp)),
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            errorMessage = null
                        },
                        text = { Text("Log In", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("login_tab")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            errorMessage = null
                        },
                        text = { Text("Sign Up", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("signup_tab")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Error message
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }
            }

            // Form inputs
            if (selectedTab == 1) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name *") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signup_name_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                item {
                    OutlinedTextField(
                        value = school,
                        onValueChange = { school = it },
                        label = { Text("School Name *") },
                        leadingIcon = { Icon(imageVector = Icons.Default.School, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signup_school_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = gradeClass,
                            onValueChange = { gradeClass = it },
                            label = { Text("Grade / Class") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("City") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            item {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address *") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_email_input"),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password *") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_password_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            if (selectedTab == 0) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                forgotPasswordEmail = email
                                showForgotPasswordDialog = true
                            }
                        ) {
                            Text("Forgot Password?", fontSize = 12.sp)
                        }
                    }
                }
            } else {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "ℹ️ Note: New registered accounts default securely to the Student role. Reviewer/Admin access is granted by institutional invitation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        errorMessage = null
                        if (selectedTab == 0) {
                            if (email.isBlank() || password.isBlank()) {
                                errorMessage = "Please enter email and password."
                                return@Button
                            }
                            viewModel.login(email, password) { success, err ->
                                if (success) {
                                    onAuthSuccess()
                                } else {
                                    errorMessage = err ?: "Login failed"
                                }
                            }
                        } else {
                            if (name.isBlank() || email.isBlank() || password.isBlank() || school.isBlank()) {
                                errorMessage = "Please fill in all required fields marked with *."
                                return@Button
                            }
                            viewModel.register(
                                name = name,
                                email = email,
                                pass = password,
                                school = school,
                                gradeClass = gradeClass.ifBlank { "Student" },
                                city = city,
                                state = state
                            ) { success, err ->
                                if (success) {
                                    onAuthSuccess()
                                } else {
                                    errorMessage = err ?: "Registration failed"
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("auth_submit_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (selectedTab == 0) "Log In to InnovateX" else "Create Student Account",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
