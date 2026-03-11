/*
 * Copyright (C) 2025 FairPhone B.V.
 *
 * SPDX-FileCopyrightText: 2025. FairPhone B.V.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package com.fairphone.settings.switchbutton

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.fairphone.settings.switchbutton.util.Constants
import com.fairphone.settings.switchbutton.util.SwitchButtonSettingsUtils
import com.fairphone.settings.switchbutton.util.SwitchEventHandler

class SwitchButtonActionReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "SwitchButtonReceiver"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Constants.ACTION_SWITCH_BUTTON) {
            Log.d(TAG, "Received action: ${intent?.action}")
            return
        }

        val switchState = SwitchButtonSettingsUtils.getSwitchState(intent) ?: run {
            Log.e(TAG, "Could not read switch status")
            return
        }
        SwitchEventHandler.onEventReceived(context, switchState)
    }
}
