package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DelajBetDarkColorScheme = darkColorScheme(
    primary = DelajVoltGreen,
    onPrimary = Color(0xFF0C1017),
    primaryContainer = DelajGreenDark,
    onPrimaryContainer = DelajVoltGreen,
    secondary = DelajGold,
    onSecondary = Color(0xFF0C1017),
    secondaryContainer = Color(0xFF332900),
    onSecondaryContainer = DelajGoldGlow,
    tertiary = DelajCrimson,
    onTertiary = Color.White,
    background = DelajDarkBg,
    onBackground = DelajTextWhite,
    surface = DelajCardBg,
    onSurface = DelajTextWhite,
    surfaceVariant = DelajElevatedBg,
    onSurfaceVariant = DelajTextSecondary,
    outline = DelajSurfaceBorder
)

@Composable
fun DelajBetTheme(
    darkTheme: Boolean = true, // Force dark-themed UI as requested by user
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DelajBetDarkColorScheme,
        typography = Typography,
        content = content
    )
}
