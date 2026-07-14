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

private val DarkColorScheme =
  darkColorScheme(
    primary = NeonPurple,
    secondary = NeonCyan,
    tertiary = NeonCyan,
    background = CosmicBackground,
    surface = CosmicCard,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = CosmicTextPrimary,
    onSurface = CosmicTextPrimary
  )

private val LightColorScheme =
  darkColorScheme( // Use dark theme for light too, as requested by screenshots
    primary = NeonPurple,
    secondary = NeonCyan,
    tertiary = NeonCyan,
    background = CosmicBackground,
    surface = CosmicCard,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = CosmicTextPrimary,
    onSurface = CosmicTextPrimary
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
