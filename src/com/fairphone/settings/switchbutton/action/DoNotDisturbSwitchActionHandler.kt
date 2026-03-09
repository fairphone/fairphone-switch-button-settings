/*
 * Copyright (C) 2025 FairPhone B.V.
 *
 * SPDX-FileCopyrightText: 2025. FairPhone B.V.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package com.fairphone.settings.switchbutton.action

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.fairphone.settings.switchbutton.data.model.SwitchState
import com.fairphone.settings.switchbutton.util.notificationManager

/**
 * This class is responsible for handling the "Do Not Disturb" (DND) switch action.
 * It extends [SwitchActionHandler] and implements the logic for starting and stopping DND mode
 * based on the state of the switch button.
 *
 * It utilizes the NotificationManager to manage automatic zen rules and AppPrefs to persist
 * the zen rule ID.
 */
object DoNotDisturbSwitchActionHandler : SwitchActionHandler() {

    const val ZEN_MODE_REASON = "SwitchButton"

    override suspend fun onSwitchButtonStateChanged(
        context: Context,
        state: SwitchState
    ): Result<Unit> {
        return try {
            when (state) {
                SwitchState.UP -> stopDND(context)
                SwitchState.DOWN -> startDND(context)
                else -> Unit // ignore
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("DoNotDisturbSwitch", "Error", e)
            Result.failure(e)
        }
    }

    private fun startDND(context: Context) {
        context.notificationManager().setZenMode(
            Settings.Global.ZEN_MODE_IMPORTANT_INTERRUPTIONS,
            null,
            ZEN_MODE_REASON,
            true,
        )
    }

    private fun stopDND(context: Context) {
        context.notificationManager().setZenMode(
            Settings.Global.ZEN_MODE_OFF,
            null,
            ZEN_MODE_REASON,
            true,
        )
    }
}
