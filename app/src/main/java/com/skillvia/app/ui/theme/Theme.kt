package com.skillvia.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SkillviaBlue,
    secondary = SkillviaBlue,
    tertiary = SkillviaOrange,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFE6E1E5),
    onSurface = Color(0xFFE6E1E5),
    primaryContainer = SkillviaBlueDark,
    onPrimaryContainer = Color.White,
    secondaryContainer = SkillviaBlueDark,
    onSecondaryContainer = Color.White,
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFE0E0E0)
)

private val LightColorScheme = lightColorScheme(
    primary = SkillviaBlue,
    secondary = SkillviaBlue,
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
    secondaryContainer = SkillviaBlueLight,
    onSecondaryContainer = SkillviaBlueDark,
    tertiaryContainer = SkillviaOrangeLight,
    onTertiaryContainer = SkillviaOrangeDark,
    surfaceVariant = SkillviaCardBackground
)

@Composable
fun SkillviaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}