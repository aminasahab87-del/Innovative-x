package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ArduinoUnoBanner(
    onSubmitProject: () -> Unit,
    onExploreArduino: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "arduino_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    listOf(
                        Color(0xFF00979C),
                        Color(0xFF38BDF8),
                        Color(0xFF006468)
                    )
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .testTag("arduino_uno_hero_banner"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A1922)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF005C60).copy(alpha = 0.65f),
                            Color(0xFF0A1A24),
                            Color(0xFF051016)
                        ),
                        center = Offset(200f, 100f),
                        radius = 800f
                    )
                )
        ) {
            // Circuit traces background overlay
            Canvas(
                modifier = Modifier
                    .matchParentSize()
            ) {
                val traceColor = Color(0xFF00979C).copy(alpha = 0.18f)
                val dotColor = Color(0xFF38BDF8).copy(alpha = 0.25f)

                // Horizontal and diagonal circuit bus lines
                drawLine(traceColor, Offset(0f, 40f), Offset(size.width * 0.45f, 40f), strokeWidth = 2f)
                drawLine(traceColor, Offset(size.width * 0.45f, 40f), Offset(size.width * 0.55f, 80f), strokeWidth = 2f)
                drawLine(traceColor, Offset(size.width * 0.55f, 80f), Offset(size.width, 80f), strokeWidth = 2f)

                drawLine(traceColor, Offset(size.width * 0.2f, size.height), Offset(size.width * 0.4f, size.height - 50f), strokeWidth = 2f)
                drawLine(traceColor, Offset(size.width * 0.4f, size.height - 50f), Offset(size.width * 0.85f, size.height - 50f), strokeWidth = 2f)

                // Circuit solder pads
                drawCircle(dotColor, radius = 4f, center = Offset(size.width * 0.45f, 40f))
                drawCircle(dotColor, radius = 4f, center = Offset(size.width * 0.55f, 80f))
                drawCircle(dotColor, radius = 5f, center = Offset(size.width * 0.4f, size.height - 50f))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Top header: Arduino Uno Badge + Active LED indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFF00979C).copy(alpha = 0.25f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00979C).copy(alpha = 0.6f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeveloperBoard,
                                contentDescription = "Arduino Uno",
                                tint = Color(0xFF00D1D8),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "ARDUINO UNO R3 • HARDWARE LAB",
                                color = Color(0xFFE0F7FA),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    // Hardware Status LEDs (ON, TX/RX)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981).copy(alpha = pulseAlpha))
                            )
                            Text(
                                text = "ON",
                                color = Color(0xFF6EE7B7),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF59E0B))
                            )
                            Text(
                                text = "L13",
                                color = Color(0xFFFCD34D),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Main Title & Visual Microcontroller Representation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Arduino Uno Projects & Inventions",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Build obstacle-avoiding robots, smart agriculture nodes, home automation, and sensor circuits using ATmega328P.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFB0BEC5),
                            lineHeight = 18.sp
                        )
                    }

                    // Stylized ATmega328P DIP-28 Microcontroller Graphic
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF18262E),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00979C).copy(alpha = 0.5f)),
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = "ATmega328P",
                                tint = Color(0xFF00D1D8),
                                modifier = Modifier.size(26.dp)
                            )
                            Text(
                                text = "ATmega328P",
                                color = Color(0xFF80DEEA),
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "16 MHz",
                                color = Color(0xFF78909C),
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Hardware component feature pills
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HardwareSpecTag("HC-SR04 Ultrasonic")
                    HardwareSpecTag("DHT11 / DHT22")
                    HardwareSpecTag("L298N Motor Driver")
                    HardwareSpecTag("Servo SG90")
                    HardwareSpecTag("16x2 I2C LCD")
                    HardwareSpecTag("Arduino C++ Code")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Call to actions: Submit Arduino Project + Explore
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onSubmitProject,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00979C),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("submit_arduino_project_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Submit Arduino Project",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = onExploreArduino,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF38BDF8)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("explore_arduino_projects_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Browse",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HardwareSpecTag(label: String) {
    Surface(
        color = Color(0xFF132A36),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFF00979C).copy(alpha = 0.35f))
    ) {
        Text(
            text = label,
            color = Color(0xFFB2EBF2),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}
