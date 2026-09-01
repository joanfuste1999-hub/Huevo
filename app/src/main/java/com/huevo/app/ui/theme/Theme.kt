package com.huevo.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = Color.White,
    primaryContainer = PeachSurface,
    onPrimaryContainer = BrownText,
    secondary = IndigoMuted,
    onSecondary = Color.White,
    secondaryContainer = MintAccent,
    onSecondaryContainer = BrownText,
    tertiary = MintAccent,
    onTertiary = BrownText,
    background = CreamBackground,
    onBackground = BrownText,
    surface = Color.White,
    onSurface = BrownText,
    surfaceVariant = PeachSurface,
    onSurfaceVariant = TextSecondary,
    error = Danger,
    onError = Color.White,
    outline = Divider
)

private val DarkColors = darkColorScheme(
    primary = OrangeSoft,
    onPrimary = Color(0xFF2E1B0B),
    primaryContainer = Color(0xFF4A3216),
    onPrimaryContainer = Color(0xFFFFE8D1),
    secondary = IndigoMuted,
    onSecondary = Color.White,
    background = Color(0xFF201812),
    onBackground = Color(0xFFF3E7D8),
    surface = Color(0xFF2A211A),
    onSurface = Color(0xFFF3E7D8),
    surfaceVariant = Color(0xFF3A2D22),
    onSurfaceVariant = Color(0xFFD8C3AC),
    error = Danger,
    outline = Color(0xFF4A3B2E)
)

@Composable
fun HuevoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = HuevoTypography,
        shapes = HuevoShapes,
        content = content
    )
}
