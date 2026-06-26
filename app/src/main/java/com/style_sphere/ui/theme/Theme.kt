package com.style_sphere.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Purple,
    onPrimary = White,
    secondary = Yellow,
    onSecondary = Black,
    background = White,
    onBackground = Black,
    surface = White,
    onSurface = Black,
    tertiary = Pink
)

@Composable
fun StyleSphereTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}