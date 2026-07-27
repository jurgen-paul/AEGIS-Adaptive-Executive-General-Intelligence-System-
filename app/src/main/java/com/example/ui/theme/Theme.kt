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

private val AegisLightColorScheme = lightColorScheme(
    primary = SleekPrimary,
    onPrimary = SleekOnPrimary,
    primaryContainer = SleekPrimaryContainer,
    onPrimaryContainer = SleekOnPrimaryContainer,
    secondary = SleekSecurityGreen,
    onSecondary = Color.White,
    secondaryContainer = SleekPrimaryContainer,
    onSecondaryContainer = SleekOnPrimaryContainer,
    tertiary = SleekPrimary,
    onTertiary = Color.White,
    background = SleekBackground,
    onBackground = SleekTextPrimary,
    surface = SleekCardBg,
    onSurface = SleekTextPrimary,
    surfaceVariant = SleekCardBg,
    onSurfaceVariant = SleekTextSecondary,
    outline = SleekBorder,
    error = SleekThreatRed,
    onError = Color.White
)

private val AegisDarkColorScheme = darkColorScheme(
    primary = SleekPrimary,
    onPrimary = SleekOnPrimary,
    primaryContainer = SleekPrimaryContainer,
    onPrimaryContainer = SleekOnPrimaryContainer,
    secondary = SleekSecurityGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF28364D),
    onSecondaryContainer = SleekPrimaryContainer,
    tertiary = SleekPrimary,
    onTertiary = Color.White,
    background = Color(0xFF14171F),
    onBackground = Color(0xFFE1E2E9),
    surface = Color(0xFF1A1C1E),
    onSurface = Color(0xFFE1E2E9),
    surfaceVariant = Color(0xFF282B30),
    onSurfaceVariant = Color(0xFFA1A5B0),
    outline = Color(0xFF383C45),
    error = SleekThreatRed,
    onError = Color.White
)

@Composable
fun AegisTheme(
    darkTheme: Boolean = false, // Default to Sleek Interface light aesthetic
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> AegisDarkColorScheme
        else -> AegisLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    AegisTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}

