/*
 * Copyright (C) 2025 FairPhone B.V.
 *
 * SPDX-FileCopyrightText: 2025. FairPhone B.V.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package com.fairphone.settings.switchbutton.action

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.fairphone.settings.switchbutton.data.model.SwitchState
import com.fairphone.settings.switchbutton.util.powerManager

object PowerSaverModeSwitchActionHandler : SwitchActionHandler() {
    override suspend fun onSwitchButtonStateChanged(context: Context, state: SwitchState): Result<Unit> {
        return try {
            when (state) {
                SwitchState.UP -> stopBatterySaverMode(context)
                SwitchState.DOWN -> startBatterySaverMode(context)
                else -> Unit // ignore
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("BatterySaverMode", "Error", e)
            Result.failure(e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startBatterySaverMode(context: Context) {
        context
            .powerManager()
            .setPowerSaveModeEnabled(true)
    }

    @SuppressLint("MissingPermission")
    private fun stopBatterySaverMode(context: Context) {
        context
            .powerManager()
            .setPowerSaveModeEnabled(false)
    }
}
