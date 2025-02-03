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

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import com.fairphone.settings.switchbutton.model.SwitchState
import com.fairphone.settings.switchbutton.util.connectivityManager

object FlightModeSwitchActionHandler : SwitchActionHandler() {
    override fun onSwitchButtonStateChanged(context: Context, state: SwitchState): Result<Unit> {
        return try {
            when (state) {
                SwitchState.UP -> stopFlightMode(context)
                SwitchState.DOWN -> startFlightMode(context)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FlightModeSwitch", "Error", e)
            Result.failure(e)
        }
    }

    private fun startFlightMode(context: Context) {
        context
            .connectivityManager()
            .setAirplaneMode(true)
    }

    private fun stopFlightMode(context: Context) {
        context
            .connectivityManager()
            .setAirplaneMode(false)
    }

    private fun setAirplaneModeSetting(context: Context, value: Boolean) {
        // Change the system setting
        Settings.Global.putInt(
            context.contentResolver,
            Settings.Global.AIRPLANE_MODE_ON,
            value.toInt()
        )

        // Post the intent
        val intent = Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        intent.putExtra("state", value)
        context.sendBroadcast(intent)
    }
}

fun Boolean.toInt() = if (this) 1 else 0
