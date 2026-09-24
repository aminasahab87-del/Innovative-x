package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = PrimaryIndigoLight,
  onPrimary = Color.White,
  primaryContainer = PrimaryIndigoDark,
  onPrimaryContainer = Color(0xFFE0E7FF),
  secondary = SecondaryCyanLight,
  onSecondary = Color(0xFF0F172A),
  secondaryContainer = Color(0xFF0369A1),
  onSecondaryContainer = Color(0xFFE0F2FE),
  tertiary = TertiaryPurpleLight,
  onTertiary = Color.White,
  background = DarkBackground,
  onBackground = TextPrimaryDark,
  surface = DarkSurface,
  onSurface = TextPrimaryDark,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = TextSecondaryDark,
  outline = DarkCardBorder
)

private val LightColorScheme = lightColorScheme(
  primary = PrimaryIndigo,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFEEF2FF),
  onPrimaryContainer = PrimaryIndigoDark,
  secondary = SecondaryCyan,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFE0F2FE),
  onSecondaryContainer = SecondaryCyanDark,
  tertiary = TertiaryPurple,
  onTertiary = Color.White,
  background = LightBackground,
  onBackground = TextPrimaryLight,
  surface = LightSurface,
  onSurface = TextPrimaryLight,
  surfaceVariant = LightSurfaceVariant,
  onSurfaceVariant = TextSecondaryLight,
  outline = LightCardBorder
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our brand colors for cohesive identity
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
