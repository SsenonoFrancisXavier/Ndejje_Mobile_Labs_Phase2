package com.ndejje.momocalc

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.ndejje.momocalc.ui.theme.Typography


val LightColorScheme = lightColorScheme(
    primary         = NavyBlue,
    onPrimary       = White,
    secondary       = BrandGold,
    onSecondary     = NavyBlueDark,
    background      = LightGrey,
    onBackground    = DarkSurface,
    surface         = White,
    onSurface       = DarkSurface,
    error           = ErrorRed,
    onError         = OnErrorWhite
)

val DarkColorScheme = darkColorScheme(
    primary         = BrandGold,        // gold becomes the hero in dark mode
    onPrimary       = NavyBlueDark,
    secondary       = NavyBlue,
    onSecondary     = White,
    background      = DarkBackground,
    onBackground    = OnDarkText,
    surface         = DarkSurface,
    onSurface       = OnDarkText,
    error           = ErrorRed,
    onError         = OnErrorWhite
)


@Composable
fun MoMoAppTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = MoMoTypography,   // from Module 5 Typography.kt
        shapes      = MoMoShapes,        // from Part D below
        content     = content
    )
}
@Composable
fun MoMoCalculatorAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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