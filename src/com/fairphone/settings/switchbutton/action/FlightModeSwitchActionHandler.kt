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
import com.fairphone.settings.switchbutton.util.connectivityManager

/**
 * Handler for actions related to the Flight Mode switch button.
 *
 * This class is responsible for managing the enabling and disabling of flight mode based on the
 * switch button state.
 *
 * It utilizes the ConnectivityManager to interact with the system's flight mode settings.
 *
 * @see SwitchActionHandler
 */
object FlightModeSwitchActionHandler : SwitchActionHandler() {
    override suspend fun onSwitchButtonStateChanged(context: Context, state: SwitchState): Result<Unit> {
        return try {
            when (state) {
                SwitchState.UP -> stopFlightMode(context)
                SwitchState.DOWN -> startFlightMode(context)
                else -> Unit // ignore
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
}
