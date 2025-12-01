/*
 * Copyright (C) 2025 FairPhone B.V.
 *
 * SPDX-FileCopyrightText: 2025. FairPhone B.V.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package com.fairphone.settings.switchbutton.data.model

/**
 * The two states of the Switch.
 */
enum class SwitchState {
    UP,
    DOWN,
    PENDING_UP,
    PENDING_DOWN,
    ERROR,
}
