package com.example.aiplanner.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CalmTeal80,
    secondary = CalmGreenGrey80,
    tertiary = CalmAmber80
)

private val LightColorScheme = lightColorScheme(
    primary = CalmTeal40,
    secondary = CalmGreenGrey40,
    tertiary = CalmAmber40
)

/**
 * App-wide Material 3 theme. Dynamic color (Android 12+) is intentionally disabled by default
 * so the calming lifestyle palette stays consistent across devices; users can still get
 * dynamic color by passing `dynamicColor = true` where the theme is applied.
 */
@Composable
fun AiPlannerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
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
