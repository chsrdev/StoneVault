package dev.chsr.stonevault.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD29BE0),
    onPrimary = Color(0xFF2A0E31),

    secondary = Color(0xFFBCA7C8),
    onSecondary = Color(0xFF231828),

    tertiary = Color(0xFF7DD3FC),
    onTertiary = Color(0xFF082433),

    background = Color(0xFF121014),
    onBackground = Color(0xFFF3EEF4),

    surface = Color(0xFF1B181D),
    onSurface = Color(0xFFF3EEF4),

    surfaceVariant = Color(0xFF2A2430),
    onSurfaceVariant = Color(0xFFD0C3D5),

    error = Color(0xFFFF6B6B),
    onError = Color(0xFF3B0A0A),

    primaryContainer = Color(0xFF5D356E),
    onPrimaryContainer = Color(0xFFF3DDF8),

    outline = Color(0xFF7B7082),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF8E44AD),
    onPrimary = Color.White,

    secondary = Color(0xFFB68CC7),
    onSecondary = Color(0xFF2F1B3A),

    tertiary = Color(0xFF4DA8DA),
    onTertiary = Color.White,

    background = Color(0xFFFAF7FC),
    onBackground = Color(0xFF1C1A1F),

    surface = Color(0xFFF3EDF7),
    onSurface = Color(0xFF1C1A1F),

    surfaceVariant = Color(0xFFE7DDEA),
    onSurfaceVariant = Color(0xFF5F5868),

    error = Color(0xFFD32F2F),
    onError = Color.White,

    primaryContainer = Color(0xFFEBDCF2),
    onPrimaryContainer = Color(0xFF341046),

    outline = Color(0xFF8A7F90),
)
@Composable
fun StoneVaultTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}