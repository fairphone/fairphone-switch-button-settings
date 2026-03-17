/*
 * Copyright (C) 2025 FairPhone B.V.
 *
 * SPDX-FileCopyrightText: 2025. FairPhone B.V.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package com.fairphone.settings.switchbutton.action

import android.content.Context
import com.fairphone.settings.switchbutton.util.LauncherSwitcherService

object FairphoneMomentsSwitchActionHandler : SwitchActionHandler() {

    override suspend fun onSwitchStateUp(context: Context): Result<Unit> {
        return toggleFairphoneMoments(context, enable = false)
    }

    override suspend fun onSwitchStateDown(context: Context): Result<Unit> {
        return toggleFairphoneMoments(context, enable = true)
    }

    /**
     * Toggles between Fairphone Moments Launcher and the user's preferred launcher.
     *
     * This method invokes the appropriate launcher switching functionality based on the
     * `enable` parameter. If `enable` is `true`, it switches to the Fairphone Moments Launcher.
     * Otherwise, it reverts to the user's preferred launcher.
     *
     * @param context The application context used for executing the launcher switching operations.
     * @param enable A boolean indicating whether to enable the Fairphone Moments Launcher (`true`)
     *               or switch back to the user's preferred launcher (`false`).
     * @return A [Result] object encapsulating success or failure of the launcher switching operation.
     */
    private suspend fun toggleFairphoneMoments(context: Context, enable: Boolean): Result<Unit> {
        val launcherService = LauncherSwitcherService()
        val result = if (enable) {
            launcherService.switchToFairphoneMoments(context)
        } else {
            launcherService.switchToUserLauncher(context)
        }
        return result
    }
}
