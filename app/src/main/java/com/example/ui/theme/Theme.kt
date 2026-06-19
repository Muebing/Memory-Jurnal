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
    primary = NatDarkPrimary,
    onPrimary = Color(0xFF5D001F),
    primaryContainer = NatDarkPrimaryContainer,
    onPrimaryContainer = NatPrimaryContainer,
    secondary = NatDarkTextMuted,
    onSecondary = Color.Black,
    background = NatDarkBackground,
    surface = NatDarkSurface,
    onBackground = NatDarkTextLight,
    onSurface = NatDarkTextLight,
    surfaceVariant = NatDarkSurfaceVariant,
    onSurfaceVariant = NatDarkTextMuted,
    outline = NatDarkBorder,
    tertiary = AccentGold
)

private val LightColorScheme = lightColorScheme(
    primary = NatPrimary,
    onPrimary = Color.White,
    primaryContainer = NatPrimaryContainer,
    onPrimaryContainer = NatOnPrimaryContainer,
    background = NatBackground,
    surface = NatWhite,
    onBackground = NatTextDark,
    onSurface = NatTextDark,
    surfaceVariant = Color(0xFFFFF0F2), // Accentuated light-rose content area
    onSurfaceVariant = NatTextMuted,
    outline = NatBorderRosy,
    secondary = NatTextMuted,
    tertiary = AccentGold
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Keep romantic custom colors prioritized for the distinct thematic look
    dynamicColor: Boolean = false,
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
