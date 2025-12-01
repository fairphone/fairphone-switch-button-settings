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
import com.fairphone.settings.switchbutton.util.cameraManager

object TorchLightSwitchActionHandler : SwitchActionHandler() {
    override suspend  fun onSwitchButtonStateChanged(context: Context, state: SwitchState): Result<Unit> {
        return try {
            when (state) {
                SwitchState.UP -> stopTorchLight(context)
                SwitchState.DOWN -> startTorchLight(context)
                else -> Unit // ignore
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TorchLightSwitch", "Error", e)
            Result.failure(e)
        }
    }

    private fun startTorchLight(context: Context) {
        val camManager = context.cameraManager()
        val camId = camManager.cameraIdList[0]
        camManager.setTorchMode(camId, true)
    }

    private fun stopTorchLight(context: Context) {
        val camManager = context.cameraManager()
        val camId = camManager.cameraIdList[0]
        camManager.setTorchMode(camId, false)
    }
}
