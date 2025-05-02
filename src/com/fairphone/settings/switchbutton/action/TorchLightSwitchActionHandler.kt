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
