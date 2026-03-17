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

    override suspend fun onSwitchStateUp(context: Context): Result<Unit> {
        return toggleFlightMode(context, enable = false)
    }

    override suspend fun onSwitchStateDown(context: Context): Result<Unit> {
        return toggleFlightMode(context, enable = true)
    }

    /**
     * Toggles the flight mode (airplane mode) on the device.
     *
     * This method enables or disables the flight mode using the system's `ConnectivityManager` service.
     * Requires appropriate permissions to modify airplane mode settings.
     *
     * @param context The context used to access the system's `ConnectivityManager` service.
     * @param enable A boolean flag indicating whether to enable or disable flight mode.
     *               Set to `true` to activate flight mode or `false` to deactivate it.
     * @return A [Result] containing a success status if the operation completes without errors,
     *         or a failure status if an exception occurs.
     */
    @SuppressLint("MissingPermission")
    private fun toggleFlightMode(context: Context, enable: Boolean): Result<Unit> {
        context
            .connectivityManager()
            .setAirplaneMode(enable)
        return Result.success(Unit)
    }
}
