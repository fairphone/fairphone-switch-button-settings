/*
 * Copyright (C) 2025 FairPhone B.V.
 *
 * SPDX-FileCopyrightText: 2025. FairPhone B.V.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package com.fairphone.settings.switchbutton.action

import android.app.UiModeManager
import android.content.Context
import android.util.Log
import com.fairphone.settings.switchbutton.data.model.SwitchState
import com.fairphone.settings.switchbutton.util.uiModeManager

object DarkModeModeSwitchActionHandler : SwitchActionHandler() {
    override suspend fun onSwitchButtonStateChanged(context: Context, state: SwitchState): Result<Unit> {
        return try {
            when (state) {
                SwitchState.UP -> stopDarkMode(context)
                SwitchState.DOWN -> startDarkMode(context)
                else -> Unit // ignore
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("DarkModeModeSwitch", "Error", e)
            Result.failure(e)
        }
    }

    private fun startDarkMode(context: Context) {
        context.uiModeManager().nightMode = UiModeManager.MODE_NIGHT_YES
    }

    private fun stopDarkMode(context: Context) {
        context.uiModeManager().nightMode = UiModeManager.MODE_NIGHT_NO
    }
}
