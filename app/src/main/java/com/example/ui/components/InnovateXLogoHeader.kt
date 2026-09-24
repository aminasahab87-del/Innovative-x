package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@Composable
fun InnovateXLogoHeader(
    modifier: Modifier = Modifier,
    logoSize: Dp = 84.dp,
    showTagline: Boolean = true,
    compactMode: Boolean = false
) {
    Surface(
        modifier = modifier,
        color = Color.Transparent
    ) {
        if (compactMode) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_innovatex_logo),
                    contentDescription = "InnovateX Logo",
                    modifier = Modifier.size(logoSize)
                )

                Column {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color(0xFF0F2942), fontWeight = FontWeight.ExtraBold)) {
                                append("Innovate")
                            }
                            withStyle(
                                style = SpanStyle(
                                    brush = Brush.linearGradient(
                                        colors = listOf(Color(0xFF00A8FF), Color(0xFF8B5CF6))
                                    ),
                                    fontWeight = FontWeight.Black
                                )
                            ) {
                                append("X")
                            }
                        },
                        fontSize = 22.sp,
                        letterSpacing = (-0.5).sp
                    )

                    if (showTagline) {
                        Text(
                            text = "Your Idea. Your Innovation. Your Future.",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF475569),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                // Main Logo Icon
                Image(
                    painter = painterResource(id = R.drawable.ic_innovatex_logo),
                    contentDescription = "InnovateX Official Logo",
                    modifier = Modifier.size(logoSize)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // InnovateX Main Title
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = Color(0xFF0A2540),
                                fontWeight = FontWeight.Black,
                                fontSize = 32.sp
                            )
                        ) {
                            append("Innovate")
                        }
                        withStyle(
                            style = SpanStyle(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF0088FF), Color(0xFFA855F7))
                                ),
                                fontWeight = FontWeight.Black,
                                fontSize = 36.sp
                            )
                        ) {
                            append("X")
                        }
                    },
                    letterSpacing = (-0.8).sp
                )

                if (showTagline) {
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Your Idea. Your Innovation. Your Future.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF1E293B),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        letterSpacing = 0.2.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Decorative Underline with Graduation Cap Accent
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.width(180.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(Color.Transparent, Color(0xFF0088FF))
                                    )
                                )
                        )
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .size(12.dp)
                                .background(Color(0xFF1E3A8A), shape = RoundedCornerShape(2.dp))
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(Color(0xFFA855F7), Color.Transparent)
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}
