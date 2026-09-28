package com.example.coalguard.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Official Indian Government Portal Light Color Palette (CIL / DGMS / Ministry of Coal)
val GovtNavyPrimary = Color(0xFF002B49)
val GovtNavyDark = Color(0xFF001D33)
val GovtBlueAccent = Color(0xFF0284C7)
val GovtGoldAmber = Color(0xFFD97706)
val GovtGoldTint = Color(0xFFFEF3C7)
val GovtBgSlate = Color(0xFFF8FAFC)
val GovtSurfaceWhite = Color(0xFFFFFFFF)
val GovtCardBorder = Color(0xFFE2E8F0)
val GovtTextDark = Color(0xFF0F172A)
val GovtTextMuted = Color(0xFF475569)

private val GovtLightColorScheme = lightColorScheme(
    primary = GovtNavyPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = Color(0xFF334155),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF1F5F9),
    onSecondaryContainer = GovtTextDark,
    tertiary = GovtGoldAmber,
    onTertiary = Color.White,
    tertiaryContainer = GovtGoldTint,
    onTertiaryContainer = Color(0xFF92400E),
    background = GovtBgSlate,
    onBackground = GovtTextDark,
    surface = GovtSurfaceWhite,
    onSurface = GovtTextDark,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = GovtTextMuted,
    outline = GovtCardBorder
)

private val GovtDarkColorScheme = darkColorScheme(
    primary = Color(0xFF38BDF8),
    onPrimary = Color(0xFF0F172A),
    primaryContainer = GovtNavyPrimary,
    onPrimaryContainer = Color(0xFFBAE6FD),
    secondary = Color(0xFF94A3B8),
    onSecondary = Color(0xFF0F172A),
    tertiary = GovtGoldAmber,
    background = Color(0xFF070D18),
    onBackground = Color.White,
    surface = Color(0xFF0F172A),
    onSurface = Color.White
)

@Composable
fun CoalGuardTheme(
    darkTheme: Boolean = false, // Default to clean official Light Theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) GovtDarkColorScheme else GovtLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
