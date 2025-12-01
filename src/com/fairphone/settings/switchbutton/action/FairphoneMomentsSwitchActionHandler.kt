/*
 * Copyright (C) 2025 FairPhone B.V.
 *
 * SPDX-FileCopyrightText: 2025. FairPhone B.V.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package com.fairphone.settings.switchbutton.action

import android.content.Context
import android.util.Log
import com.fairphone.settings.switchbutton.data.model.SwitchState
import com.fairphone.settings.switchbutton.util.LauncherSwitcherService

object FairphoneMomentsSwitchActionHandler : SwitchActionHandler() {
    override suspend fun onSwitchButtonStateChanged(context: Context, state: SwitchState): Result<Unit> {
        return try {
            val launcherService = LauncherSwitcherService()
            when (state) {
                SwitchState.UP -> launcherService.switchToUserLauncher(context)
                SwitchState.DOWN -> launcherService.switchToFairphoneMoments(context)
                else -> Result.success(Unit)
            }
        } catch (e: Exception) {
            Log.e("SpringLauncherSwitch", "Error", e)
            Result.failure(e)
        }
    }
}
