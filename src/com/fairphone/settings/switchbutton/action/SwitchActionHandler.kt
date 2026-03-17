/*
 * Copyright (C) 2025 FairPhone B.V.
 *
 * SPDX-FileCopyrightText: 2025. FairPhone B.V.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package com.fairphone.settings.switchbutton.action

import android.content.Context
import android.util.Log
import com.fairphone.settings.switchbutton.data.model.SwitchState

abstract class SwitchActionHandler {
    suspend fun onSwitchButtonStateChanged(context: Context, state: SwitchState): Result<Unit> {
        return try {
            when (state) {
                SwitchState.UP -> onSwitchStateUp(context)
                SwitchState.DOWN -> onSwitchStateDown(context)
                else -> Result.success(Unit) // ignore
            }
        } catch (e: Exception) {
            Log.e(javaClass.simpleName, "Error handling switch state changed", e)
            Result.failure(e)
        }
    }

    abstract suspend fun onSwitchStateUp(context: Context): Result<Unit>
    abstract suspend fun onSwitchStateDown(context: Context): Result<Unit>
}

val emptyActionHandler = object: SwitchActionHandler() {
    override suspend fun onSwitchStateDown(context: Context): Result<Unit> = Result.success(Unit)
    override suspend fun onSwitchStateUp(context: Context): Result<Unit> = Result.success(Unit)
}