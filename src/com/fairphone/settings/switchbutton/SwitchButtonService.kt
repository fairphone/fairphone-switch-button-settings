/*
 * Copyright (C) 2025 FairPhone B.V.
 *
 * SPDX-FileCopyrightText: 2025. FairPhone B.V.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package com.fairphone.settings.switchbutton

import android.app.Service
import android.content.Intent
import android.hardware.input.ISwitchButtonService
import android.os.IBinder
import android.util.Log
import com.fairphone.settings.switchbutton.data.model.SwitchState
import com.fairphone.settings.switchbutton.util.SwitchEventHandler
import com.fairphone.settings.switchbutton.util.isUserSetupComplete

class SwitchButtonService : Service() {

    companion object {
        const val LOG_TAG = "SwitchButtonService"
    }

    private val binder = object : ISwitchButtonService.Stub() {
        override fun onSwitchStateChanged(state: String) {
            Log.d(LOG_TAG, "onSwitchStateChanged: $state")

            if (!isUserSetupComplete()) {
                Log.d(LOG_TAG, "User setup is not complete yet, ignoring.")
                return
            }

            val switchState = try {
                SwitchState.valueOf(state)
            } catch (e: IllegalArgumentException) {
                Log.e(LOG_TAG, "Invalid switch state received: $state")
                return
            }

            SwitchEventHandler.onEventReceived(applicationContext, switchState)
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }
}