/*
 * Copyright (C) 2025 Fairphone B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.fairphone.settings.switchbutton.action

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.fairphone.settings.switchbutton.model.SwitchState
import com.fairphone.settings.switchbutton.util.powerManager

object PowerSaverModeSwitchActionHandler : SwitchActionHandler() {
    override suspend fun onSwitchButtonStateChanged(context: Context, state: SwitchState): Result<Unit> {
        return try {
            when (state) {
                SwitchState.UP -> stopBatterySaverMode(context)
                SwitchState.DOWN -> startBatterySaverMode(context)
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
