package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val NexoraLightColorScheme = lightColorScheme(
    primary = NexoraCyan,
    onPrimary = Color.White,
    primaryContainer = NexoraSurfaceVariant,
    onPrimaryContainer = NexoraCyan,
    secondary = NexoraMagenta,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFDF2F8),
    onSecondaryContainer = NexoraPink,
    tertiary = NexoraPurple,
    onTertiary = Color.White,
    background = NexoraBackground,
    onBackground = NexoraTextPrimary,
    surface = NexoraSurface,
    onSurface = NexoraTextPrimary,
    surfaceVariant = NexoraSurfaceVariant,
    onSurfaceVariant = NexoraTextSecondary,
    outline = NexoraBorder,
    error = NexoraError,
    onError = Color.White
)

private val NexoraDarkColorScheme = androidx.compose.material3.darkColorScheme(
    primary = Color(0xFF38BDF8),
    onPrimary = Color(0xFF0B132B),
    primaryContainer = Color(0xFF1C2541),
    onPrimaryContainer = Color(0xFF38BDF8),
    secondary = Color(0xFFE879F9),
    onSecondary = Color(0xFF0B132B),
    secondaryContainer = Color(0xFF2E1065),
    onSecondaryContainer = Color(0xFFF0ABFC),
    tertiary = Color(0xFFC084FC),
    onTertiary = Color(0xFF0B132B),
    background = Color(0xFF0B132B),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1C2541),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF334155),
    error = Color(0xFFF87171),
    onError = Color(0xFF0B132B)
)

@Composable
fun NexoraTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val bgColor = if (darkTheme) Color(0xFF0B132B) else NexoraBackground
                window.statusBarColor = bgColor.toArgb()
                window.navigationBarColor = bgColor.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = if (darkTheme) NexoraDarkColorScheme else NexoraLightColorScheme,
        typography = Typography,
        content = content
    )
}

// Retain alias for any existing test references
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    NexoraTheme(darkTheme = darkTheme, content = content)
}

