package com.karan.schemesaathi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = DeepBluePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD0E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = SaffronOrange,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDCC6),
    onSecondaryContainer = Color(0xFF301400),
    tertiary = SuccessGreen,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = Color(0xFF1B1B1F),
    surface = LightSurface,
    onSurface = Color(0xFF1B1B1F),
    error = ErrorRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = DeepBlue80,
    onPrimary = Color(0xFF003258),
    primaryContainer = DeepBlueSecondary,
    onPrimaryContainer = Color(0xFFD0E4FF),
    secondary = SaffronOrange,
    onSecondary = Color(0xFF4C1D00),
    tertiary = SuccessGreen,
    background = DarkBackground,
    onBackground = Color(0xFFE2E2E6),
    surface = DarkSurface,
    onSurface = Color(0xFFE2E2E6),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun SchemeSaathiTheme(
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
