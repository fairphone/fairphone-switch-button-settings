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

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import com.fairphone.settings.switchbutton.model.SwitchState
import com.fairphone.settings.switchbutton.util.Constants

object SpringLauncherSwitchActionHandler : SwitchActionHandler() {
    override fun onSwitchButtonStateChanged(context: Context, state: SwitchState): Result<Unit> {
        return try {
            when (state) {
                SwitchState.UP -> {
                    sendSpringLauncherBroadcast(
                        context = context,
                        launcherAction = Constants.ACTION_STOP_SPRING_LAUNCHER
                    )
                }

                SwitchState.DOWN -> {
                    sendSpringLauncherBroadcast(
                        context = context,
                        launcherAction = Constants.ACTION_START_SPRING_LAUNCHER
                    )
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SpringLauncherSwitch", "Error", e)
            Result.failure(e)
        }
    }

    private fun sendSpringLauncherBroadcast(context: Context, launcherAction: String) {
        val intent = Intent(launcherAction).apply {
            action = launcherAction
            component = ComponentName(
                Constants.SPRING_LAUNCHER_CORE_PACKAGE_NAME,
                Constants.SPRING_LAUNCHER_CORE_RECEIVER,
            )
            setPackage(Constants.SPRING_LAUNCHER_CORE_PACKAGE_NAME)
            addFlags(Intent.FLAG_RECEIVER_INCLUDE_BACKGROUND)
        }
        context.sendBroadcast(intent)
    }
}
