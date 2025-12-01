/*
 * Copyright (C) 2025 FairPhone B.V.
 *
 * SPDX-FileCopyrightText: 2025. FairPhone B.V.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package com.fairphone.settings.switchbutton

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fairphone.settings.switchbutton.ui.screen.SwitchButtonSettings
import com.fairphone.settings.switchbutton.ui.theme.SwitchButtonSettingsTheme

class SwitchButtonSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SwitchButtonSettingsTheme {
                SwitchButtonSettings(
                    onNavigateBack = { finish() }
                )
            }
        }
    }
}
