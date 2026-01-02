package com.alertemedicaments.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Custom Colors
val Teal500 = Color(0xFF14B8A6)
val Teal600 = Color(0xFF0D9488)
val Cyan500 = Color(0xFF06B6D4)
val Cyan600 = Color(0xFF0891B2)

val StatusGreen = Color(0xFF10B981)
val StatusOrange = Color(0xFFF59E0B)
val StatusRed = Color(0xFFEF4444)
val StatusGray = Color(0xFF6B7280)

private val LightColorScheme = lightColorScheme(
    primary = Teal600,
    onPrimary = Color.White,
    primaryContainer = Teal500.copy(alpha = 0.1f),
    onPrimaryContainer = Teal600,
    secondary = Cyan600,
    onSecondary = Color.White,
    secondaryContainer = Cyan500.copy(alpha = 0.1f),
    onSecondaryContainer = Cyan600,
    tertiary = Color(0xFF7C3AED),
    onTertiary = Color.White,
    background = Color(0xFFF9FAFB),
    onBackground = Color(0xFF111827),
    surface = Color.White,
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = Color(0xFF6B7280),
    outline = Color(0xFFE5E7EB),
    error = StatusRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Teal500,
    onPrimary = Color.White,
    primaryContainer = Teal600.copy(alpha = 0.2f),
    onPrimaryContainer = Teal500,
    secondary = Cyan500,
    onSecondary = Color.White,
    secondaryContainer = Cyan600.copy(alpha = 0.2f),
    onSecondaryContainer = Cyan500,
    tertiary = Color(0xFFA78BFA),
    onTertiary = Color.White,
    background = Color(0xFF111827),
    onBackground = Color(0xFFF9FAFB),
    surface = Color(0xFF1F2937),
    onSurface = Color(0xFFF9FAFB),
    surfaceVariant = Color(0xFF374151),
    onSurfaceVariant = Color(0xFF9CA3AF),
    outline = Color(0xFF4B5563),
    error = StatusRed,
    onError = Color.White
)

@Composable
fun AlerteMedicamentsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

val Typography = Typography()
