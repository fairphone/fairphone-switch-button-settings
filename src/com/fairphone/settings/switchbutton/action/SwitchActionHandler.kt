/*
 * Copyright (C) 2025 FairPhone B.V.
 *
 * SPDX-FileCopyrightText: 2025. FairPhone B.V.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package com.fairphone.settings.switchbutton.action

import android.content.Context
import com.fairphone.settings.switchbutton.data.model.SwitchState

abstract class SwitchActionHandler {
    abstract suspend fun onSwitchButtonStateChanged(context: Context, state: SwitchState): Result<Unit>
}
