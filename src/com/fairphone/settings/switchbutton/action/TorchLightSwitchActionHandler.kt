/*
 * Copyright (C) 2025 FairPhone B.V.
 *
 * SPDX-FileCopyrightText: 2025. FairPhone B.V.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package com.fairphone.settings.switchbutton.action

import android.content.Context
import com.fairphone.settings.switchbutton.util.cameraManager

object TorchLightSwitchActionHandler : SwitchActionHandler() {

    override suspend fun onSwitchStateUp(context: Context): Result<Unit> {
        return toggleTorchLight(context, enable = false)
    }

    override suspend fun onSwitchStateDown(context: Context): Result<Unit> {
        return toggleTorchLight(context, enable = true)
    }

    /**
     * Toggles the torch (flashlight) mode for the device's camera.
     *
     * @param context The context used to access the camera manager service.
     * @param enable A boolean flag indicating whether to enable or disable the torch mode.
     *               Set to `true` to turn on the torch mode or `false` to turn it off.
     * @return A [Result] containing a success status if the operation is completed
     *         without errors, or a failure status if an exception occurs.
     */
    private fun toggleTorchLight(context: Context, enable: Boolean): Result<Unit> {
        val camManager = context.cameraManager()
        val camId = camManager.cameraIdList[0]
        camManager.setTorchMode(camId, enable)

        return Result.success(Unit)
    }
}
