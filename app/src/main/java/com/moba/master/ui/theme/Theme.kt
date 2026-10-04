package com.moba.master.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Dark MOBA-themed color scheme for the manager dashboard.
 */
private val DarkMobaColors = darkColorScheme(
    primary = Color(0xFF4FC3F7),        // Cyan accent
    onPrimary = Color(0xFF0D1117),
    secondary = Color(0xFF66BB6A),      // Green accent
    onSecondary = Color(0xFF0D1117),
    tertiary = Color(0xFFFF7043),       // Orange accent
    background = Color(0xFF0D1117),     // Very dark blue-black
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1A1F2E),        // Dark surface
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF252A3A),
    onSurfaceVariant = Color(0xFFB0B0B0),
    error = Color(0xFFEF5350),
    onError = Color(0xFFFFFFFF)
)

// Team colors
object TeamColors {
    val Blue = Color(0xFF2196F3)
    val BlueLight = Color(0xFF64B5F6)
    val Red = Color(0xFFE53935)
    val RedLight = Color(0xFFEF5350)
    val Gold = Color(0xFFFFD700)
    val HealthGreen = Color(0xFF4CAF50)
    val HealthRed = Color(0xFFF44336)
    val ManaBlue = Color(0xFF42A5F5)
    val NeonHighlight = Color(0xFF00E5FF)
}

@Composable
fun MobaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkMobaColors,
        content = content
    )
}
