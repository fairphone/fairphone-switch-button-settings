/*
 * Copyright (c) 2025. Fairphone B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fairphone.settings.switchbutton

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.fairphone.settings.switchbutton.data.model.SwitchState
import com.fairphone.settings.switchbutton.data.prefs.AppPrefs
import com.fairphone.settings.switchbutton.data.prefs.appPrefs
import com.fairphone.settings.switchbutton.util.Constants
import com.fairphone.settings.switchbutton.util.SwitchButtonSettingsUtils
import com.fairphone.settings.switchbutton.util.isUserSetupComplete
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SwitchButtonActionReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "SwitchButtonReceiver"

        // How long to wait after the last switch flip before processing.
        private const val DEBOUNCE_DELAY_MS = 300L // 0.3 seconds

        // Handler for robust debouncing approach
        private val debounceHandler = Handler(Looper.getMainLooper())
        private var debounceRunnable: Runnable? = null
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

        handlerDebounce(context, intent)
    }

    /**
     * Uses a Handler to delay the execution of handleSwitchChanged.
     * If a new broadcast arrives before the delay expires, the previous
     * pending execution is cancelled, and a new delay starts.
     * Processes the *last* event after a quiet period.
     */
    private fun handlerDebounce(context: Context, intent: Intent) {
        // 1. Remove any previously posted (pending) runnable.
        debounceRunnable?.let { debounceHandler.removeCallbacks(it) }
        Log.d(TAG, "Removed previous debounce runnable (if any).")

        // 2. Create a new runnable to execute the actual work.
        debounceRunnable = Runnable {
            Log.d(TAG, "Debounce delay ended (Handler). Processing...")
            // Reset the runnable reference *before* executing the task
            // to allow new events to schedule immediately after processing starts.
            debounceRunnable = null

            val pendingResult: PendingResult? = goAsync()

            val switchState = getSwitchState(intent) ?: run {
                Log.e(TAG, "Could not read switch status")
                return@Runnable
            }
            handleSwitchEvent(context, switchState, pendingResult)
        }

        // 3. Post the new runnable with the specified delay.
        debounceHandler.postDelayed(debounceRunnable!!, DEBOUNCE_DELAY_MS)
        Log.d(TAG, "Posted new debounce runnable with ${DEBOUNCE_DELAY_MS}ms delay.")
    }

    private fun handleSwitchEvent(
        context: Context,
        state: SwitchState,
        pendingResult: PendingResult?
    ) {
        val appPrefs = AppPrefs(context.appPrefs)
        val switchButtonAction = SwitchButtonSettingsUtils.getCurrentSwitchButtonAction(context)
        val handler = switchButtonAction.actionHandler
        val jobScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        jobScope.launch {
            Log.d(TAG, "Handling switch event for action: $switchButtonAction")
            var currentState = if (state == SwitchState.UP) {
                SwitchState.PENDING_UP
            } else {
                SwitchState.PENDING_DOWN
            }
            // save pending state
            appPrefs.setLastKnownSwitchState(currentState)

            try {
                val result = handler.onSwitchButtonStateChanged(context, state)
                if (result.isFailure) {
                    // Save error state
                    appPrefs.setLastKnownSwitchState(SwitchState.ERROR)
                    Log.e(TAG, "Error handling action '$switchButtonAction': ${result.exceptionOrNull()?.message}", result.exceptionOrNull())
                } else {
                    currentState = if (state == SwitchState.UP) {
                        SwitchState.UP
                    } else {
                        SwitchState.DOWN
                    }
                    // save final state
                    appPrefs.setLastKnownSwitchState(currentState)
                    Log.d(TAG, "Switch event handled successfully.") // Log success
                }
            } catch (e: InstantiationException) {
                appPrefs.setLastKnownSwitchState(SwitchState.ERROR)
                Log.e(TAG, "Error instantiating action handler for '$switchButtonAction': ${e.message}", e)
            } finally {
                jobScope.cancel()
            }
        }.invokeOnCompletion {
            try {
                pendingResult?.finish()
            } catch (e: Exception) {
                Log.e(TAG, "Error finishing pending result", e)
            }
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
