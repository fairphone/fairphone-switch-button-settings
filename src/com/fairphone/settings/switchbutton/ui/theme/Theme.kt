/*
 * Copyright (C) 2025 FairPhone B.V.
 *
 * SPDX-FileCopyrightText: 2025. FairPhone B.V.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package com.fairphone.settings.switchbutton.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Color_FP_Brand_Primary,
    secondary = Color_FP_Brand_Accent_Dark,
    tertiary = Color_FP_Brand_Accent_Subtle,
    background = backgroundLight,
    surface = surfaceLight,
    surfaceContainer = containerLight,
    onSurface = onSurfaceLight,
    onSurfaceVariant = onSurfaceVariantLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = Color_FP_Brand_Accent,
    secondary = Color_FP_Brand_Accent,
    tertiary = Color_FP_Brand_Accent_Subtle,
    background = backgroundDark,
    surface = surfaceDark,
    surfaceContainer = containerDark,
    onSurface = onSurfaceDark,
    onSurfaceVariant = onSurfaceVariantDark,
)

@Composable
fun SwitchButtonSettingsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
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