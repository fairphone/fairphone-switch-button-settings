/*
 * Copyright (C) 2025 FairPhone B.V.
 *
 * SPDX-FileCopyrightText: 2025. FairPhone B.V.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package com.fairphone.settings.switchbutton.action

import android.app.UiModeManager
import android.content.Context
import com.fairphone.settings.switchbutton.util.uiModeManager

object DarkModeModeSwitchActionHandler : SwitchActionHandler() {

    override suspend fun onSwitchStateUp(context: Context): Result<Unit> {
        return toggleDarkMode(context, enable = false)
    }

    override suspend fun onSwitchStateDown(context: Context): Result<Unit> {
        return toggleDarkMode(context, enable = true)
    }

    /**
     * Toggles the dark mode for the device.
     *
     * This method enables or disables the dark mode by adjusting the `UiModeManager` night mode settings.
     *
     * @param context The context used to retrieve the system's `UiModeManager` service.
     * @param enable A boolean flag indicating whether to enable or disable dark mode.
     *               Set to `true` to enable dark mode or `false` to disable it.
     * @return A [Result] containing a success status if the operation completes
     *         without errors, or a failure status if an exception occurs.
     */
    private fun toggleDarkMode(context: Context, enable: Boolean): Result<Unit> {
        val uiModeManager = context.uiModeManager()
        uiModeManager.nightMode = if (enable) {
            UiModeManager.MODE_NIGHT_YES
        } else {
            UiModeManager.MODE_NIGHT_NO
        }

        return Result.success(Unit)
    }
}
