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
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.fairphone.settings.switchbutton.data.model.SwitchState
import com.fairphone.settings.switchbutton.data.prefs.AppPrefs
import com.fairphone.settings.switchbutton.util.Constants
import com.fairphone.settings.switchbutton.util.SwitchButtonSettingsUtils
import com.fairphone.settings.switchbutton.util.isUserSetupComplete
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SwitchButtonActionReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "SwitchButtonReceiver"

        // How long to wait after the last switch flip before processing.
        private const val DEBOUNCE_DELAY_MS = 150L // 0.15 seconds

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
        // TODO: Ignore if fairphone setup is not complete yet

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

            val switchState = SwitchButtonSettingsUtils.getSwitchState(intent) ?: run {
                Log.e(TAG, "Could not read switch status")
                return@Runnable
            }
            handleSwitchEvent(context, switchState)
        }

        // 3. Post the new runnable with the specified delay.
        debounceHandler.postDelayed(debounceRunnable!!, DEBOUNCE_DELAY_MS)
        Log.d(TAG, "Posted new debounce runnable with ${DEBOUNCE_DELAY_MS}ms delay.")
    }

    private fun handleSwitchEvent(
        context: Context,
        state: SwitchState,
    ) {
        val appPrefs = AppPrefs(context)
        val switchButtonAction = SwitchButtonSettingsUtils.getCurrentSwitchButtonAction(context)
        val handler = switchButtonAction.actionHandler
        val jobScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        jobScope.launch {
            val lastKnownSwitchState = appPrefs.getLastKnownSwitchStateFlow().first()
            if (lastKnownSwitchState == state) {
                Log.d(TAG, "Ignoring switch event with same state: $state")
                return@launch
            }

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
        }
    }
}
