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
    primary = Color(0xFFA1C9FF),
    onPrimary = Color(0xFF00325A),
    primaryContainer = Color(0xFF284777),
    onPrimaryContainer = Color(0xFFD6E3FF),
    secondary = Color(0xFF78DC86),
    onSecondary = Color(0xFF00390F),
    secondaryContainer = Color(0xFF00531A),
    onSecondaryContainer = Color(0xFF94F9A0),
    tertiary = Color(0xFFA1C9FF),
    onTertiary = Color(0xFF00325A),
    background = Color(0xFF111318),
    onBackground = Color(0xFFE1E2E9),
    surface = Color(0xFF191C22),
    onSurface = Color(0xFFE1E2E9),
    surfaceVariant = Color(0xFF23262E),
    onSurfaceVariant = Color(0xFFA1A5B0),
    outline = Color(0xFF43474E),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
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

