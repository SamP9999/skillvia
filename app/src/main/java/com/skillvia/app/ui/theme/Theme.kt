package com.skillvia.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SkillviaBlue80,
    secondary = SkillviaTeal80,
    tertiary = SkillviaOrange80,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFE6E1E5),
    onSurface = Color(0xFFE6E1E5)
)

private val LightColorScheme = lightColorScheme(
    primary = SkillviaBlue,
    secondary = SkillviaTeal,
    tertiary = SkillviaOrange,
    background = SkillviaBackground,
    surface = SkillviaSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = SkillviaOnSurface,
    onSurface = SkillviaOnSurface,
    onSurfaceVariant = SkillviaOnSurfaceVariant,
    primaryContainer = SkillviaBlueLight,
    onPrimaryContainer = SkillviaBlueDark,
    secondaryContainer = SkillviaTealLight,
    onSecondaryContainer = SkillviaTealDark,
    tertiaryContainer = SkillviaOrangeLight,
    onTertiaryContainer = SkillviaOrangeDark
)

@Composable
fun SkillviaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color disabled to maintain consistent brand identity
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Dynamic color disabled - using custom Skillvia brand colors
        // This ensures consistent branding regardless of system wallpaper
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}