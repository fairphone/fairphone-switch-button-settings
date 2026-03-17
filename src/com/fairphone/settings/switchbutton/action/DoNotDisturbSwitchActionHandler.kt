/*
 * Copyright (C) 2025 FairPhone B.V.
 *
 * SPDX-FileCopyrightText: 2025. FairPhone B.V.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package com.fairphone.settings.switchbutton.action

import android.content.Context
import android.provider.Settings
import com.fairphone.settings.switchbutton.util.notificationManager

/**
 * This class is responsible for handling the "Do Not Disturb" (DND) switch action.
 * It extends [SwitchActionHandler] and implements the logic for starting and stopping DND mode
 * based on the state of the switch button.
 *
 * It utilizes the NotificationManager to manage automatic zen rules and AppPrefs to persist
 * the zen rule ID.
 */
object DoNotDisturbSwitchActionHandler : SwitchActionHandler() {

    const val ZEN_MODE_REASON = "SwitchButton"

    override suspend fun onSwitchStateUp(context: Context): Result<Unit> {
        return toggleDoNotDisturb(context, false)
    }

    override suspend fun onSwitchStateDown(context: Context): Result<Unit> {
        return toggleDoNotDisturb(context, true)
    }

    /**
     * Toggles the "Do Not Disturb" mode for the device.
     *
     * This method enables or disables the "Do Not Disturb" mode by setting the system's zen mode.
     * It uses the `NotificationManager` to apply the changes.
     *
     * @param context The context used to access the system's `NotificationManager` service.
     * @param enable A boolean flag indicating whether to enable or disable the "Do Not Disturb" mode.
     *               Set to `true` to enable "Do Not Disturb", or `false` to disable it.
     * @return A [Result] containing a success status if the operation completes
     *         without errors, or a failure status if an exception occurs.
     */
    private fun toggleDoNotDisturb(context: Context, enable: Boolean): Result<Unit> {
        val zenMode = if (enable) {
            Settings.Global.ZEN_MODE_IMPORTANT_INTERRUPTIONS
        } else {
            Settings.Global.ZEN_MODE_OFF
        }
        context.notificationManager().setZenMode(
            zenMode,
            null,
            ZEN_MODE_REASON,
            true,
        )
        return Result.success(Unit)
    }
}
