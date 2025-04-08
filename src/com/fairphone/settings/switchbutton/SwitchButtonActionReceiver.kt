package com.fairphone.settings.switchbutton

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.fairphone.settings.switchbutton.model.SwitchState
import com.fairphone.settings.switchbutton.util.Constants
import com.fairphone.settings.switchbutton.util.SwitchButtonSettingsUtils
import com.fairphone.settings.switchbutton.util.isUserSetupComplete

class SwitchButtonActionReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "SwitchButtonReceiver"
        const val SWITCH_CHANGE_MIN_TIME_MS = 300
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Constants.ACTION_SWITCH_BUTTON) {
            Log.d(TAG, "Received action: ${intent?.action}")
            return
        }

        // Ignore if user device setup is not complete yet
        if (!context.isUserSetupComplete()) {
            Log.d(TAG, "User setup is not complete yet, ignoring broadcast")
            return
        }

        val state = getSwitchState(intent) ?: run {
            Log.e(TAG, "Could not read switch status")
            return
        }
        val pendingResult = goAsync()
        handleSwitchEvent(context, state)
        pendingResult.finish()
    }

    private fun handleSwitchEvent(context: Context, state: SwitchState): Boolean {
        val switchButtonAction = SwitchButtonSettingsUtils.getSwitchButtonActionSetting(context)
        val handler = SwitchButtonSettingsUtils.getSwitchButtonActionSettingHandler(context)

        Log.d(TAG, "Handling switch event for action: $switchButtonAction")
        return try {
            val result = handler?.onSwitchButtonStateChanged(context, state)
            if (result?.isFailure == true) {
                Log.e(
                    TAG,
                    "Error handling action '$switchButtonAction': ${result.exceptionOrNull()?.message}",
                    result.exceptionOrNull()
                ) // Include exception message
                false
            } else {
                Log.d(TAG, "Switch event handled successfully.") // Log success
                true
            }
        } catch (e: InstantiationException) {
            Log.e(
                TAG,
                "Error instantiating action handler for '$switchButtonAction': ${e.message}",
                e
            ) // Include exception message
            false
        }
    }

    private fun getSwitchState(intent: Intent): SwitchState? {
        val statusString = intent.getStringExtra(Constants.EXTRA_SWITCH_STATUS) ?: return null
        return try {
            SwitchState.valueOf(statusString)
        } catch (e: IllegalArgumentException) {
            Log.e("getSwitchState()", "Could not read switch status: $statusString", e)
            null
        }
    }
}
