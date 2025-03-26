package com.fairphone.settings.switchbutton

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.fairphone.settings.switchbutton.model.SwitchState
import com.fairphone.settings.switchbutton.util.Constants
import com.fairphone.settings.switchbutton.util.SwitchButtonSettingsUtils
import com.fairphone.settings.switchbutton.util.isUserSetupCompleted

class SwitchButtonActionReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "SwitchButtonReceiver"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Constants.ACTION_SWITCH_BUTTON) {
            Log.d(TAG, "Received action: ${intent?.action}")
            return
        }

        // Ignore if user device setup is not complete yet
        if (!context.isUserSetupCompleted()) {
            Log.i(TAG, "User setup is not complete yet, ignoring broadcast")
            return
        }

        // TODO: Add logic to prevent fast switching

        val status = intent.getSwitchState() ?: run {
            Log.e(TAG, "Could not read switch status")
            return
        }
        val pendingResult = goAsync()
        onSwitchStateChanged(context, status)
        pendingResult.finish()
    }

    private fun onSwitchStateChanged(context: Context, state: SwitchState) {
        val switchButtonAction = SwitchButtonSettingsUtils.getSwitchButtonActionSetting(context)

        try {
            val handler = SwitchButtonSettingsUtils.getSwitchButtonActionSettingHandler(context)
            val result = handler?.onSwitchButtonStateChanged(context, state)
            if (result?.isFailure == true) {
                Log.e(TAG, "Error handling action $switchButtonAction", result.exceptionOrNull())
            }
        } catch (e: InstantiationException) {
            Log.e(TAG, "Error instantiating action handler for $switchButtonAction", e)
        }
    }
}

private fun Intent.getSwitchState(): SwitchState? {
    val statusString = getStringExtra(Constants.EXTRA_SWITCH_STATUS) ?: return null
    return try {
        SwitchState.valueOf(statusString)
    } catch (e: IllegalArgumentException) {
        Log.e("getSwitchState()", "Could not read switch status: $statusString", e)
        null
    }
}
