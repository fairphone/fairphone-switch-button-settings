/*
 * Copyright (C) 2026 FairPhone B.V.
 *
 * SPDX-FileCopyrightText: 2025. FairPhone B.V.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package com.fairphone.settings.switchbutton.action

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.fairphone.settings.switchbutton.util.Constants

object BlueLightFilterSwitchActionHandler : SwitchActionHandler() {

    const val SETTING_BLUE_FILTER = "night_display_activated"

    override suspend fun onSwitchStateUp(context: Context): Result<Unit> {
        return toggleBlueLightFilter(context, enable = false)
    }

    override suspend fun onSwitchStateDown(context: Context): Result<Unit> {
        return toggleBlueLightFilter(context, enable = true)
    }

    /**
     * Toggles the blue light filter on or off based on the provided state.
     *
     * This method modifies the system setting for the blue light filter using the content resolver.
     * It requires appropriate permissions to write to secure settings. In case of failure,
     * an exception or error message will be encapsulated in the returned [Result].
     *
     * @param context The context used to access the content resolver for system settings.
     * @param enable A boolean flag indicating whether to enable or disable the blue light filter.
     *               Set to `true` to activate the filter or `false` to deactivate it.
     * @return A [Result] containing a success status if the operation completes without errors,
     *         or a failure status with the relevant exception or error message in case of failure.
     */
    private fun toggleBlueLightFilter(context: Context, enable: Boolean): Result<Unit> {
        return try {
            val result = Settings.Secure.putInt(
                context.contentResolver,
                SETTING_BLUE_FILTER,
                if (enable) 1 else 0
            )
            if (result) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to set blue light filter"))
            }
        } catch (e: SecurityException) {
            Log.e(Constants.LOG_TAG, "Failed to set blue light filter", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG, "Unexpected error setting blue light filter", e)
            Result.failure(e)
        }
    }
}
