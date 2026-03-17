/*
 * Copyright (C) 2026 FairPhone B.V.
 *
 * SPDX-FileCopyrightText: 2025. FairPhone B.V.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package com.fairphone.settings.switchbutton.action

import android.content.Context
import com.fairphone.settings.switchbutton.util.userManager

object WorkProfileSwitchActionHandler : SwitchActionHandler() {

    override suspend fun onSwitchStateUp(context: Context): Result<Unit> {
        return toggleWorkProfile(context, enable = false)
    }

    override suspend fun onSwitchStateDown(context: Context): Result<Unit> {
        return toggleWorkProfile(context, enable = true)
    }

    /**
     * Toggles the quiet mode state for all managed profiles associated with the provided context.
     * If quiet mode is enabled, apps and notifications within the managed profile are suppressed,
     * conserving battery and data usage.
     *
     * @param context The context from which the user manager service is retrieved.
     * @param enable A boolean value indicating whether to enable or disable quiet mode.
     *               Set to `true` to enable or `false` to disable quiet mode for managed profiles.
     */
    private fun toggleWorkProfile(context: Context, enable: Boolean): Result<Unit> {
        val userManager = context.userManager()

        val workProfiles = userManager.userProfiles
            // We filter the managed profiles to get the 'work' profiles
            .filter { userManager.isManagedProfile(it.identifier) }

        val results = workProfiles.map { workProfile ->
            context.userManager().requestQuietModeEnabled(
                enable,
                workProfile
            )
        }
        return if (results.all { it }) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to set quiet mode state"))
        }
    }
}
