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
import com.fairphone.settings.switchbutton.util.powerManager

object PowerSaverModeSwitchActionHandler : SwitchActionHandler() {

    override suspend fun onSwitchStateUp(context: Context): Result<Unit> {
        return togglePowerSaverMode(context, enable = false)
    }

    override suspend fun onSwitchStateDown(context: Context): Result<Unit> {
        return togglePowerSaverMode(context, enable = true)
    }

    /**
     * Toggles the power saver mode on the device.
     *
     * This method enables or disables the power saver mode using the system's PowerManager.
     * The operation requires the relevant permissions to modify power saver settings.
     *
     * @param context The context used to retrieve the system's PowerManager service.
     * @param enable A boolean flag indicating whether to enable or disable power saver mode.
     *               Set to `true` to activate power saver mode or `false` to deactivate it.
     * @return A [Result] containing a success status if the operation completes without errors,
     *         or a failure status if the operation is unsuccessful.
     */
    @SuppressLint("MissingPermission")
    private fun togglePowerSaverMode(context: Context, enable: Boolean): Result<Unit> {
        val success = context
            .powerManager()
            .setPowerSaveModeEnabled(enable)
        return if (success) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to start battery saver mode"))
        }
    }
}
