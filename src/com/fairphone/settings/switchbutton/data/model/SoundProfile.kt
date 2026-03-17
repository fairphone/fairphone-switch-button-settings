/*
 * Copyright (C) 2026 FairPhone B.V.
 *
 * SPDX-FileCopyrightText: 2025. FairPhone B.V.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package com.fairphone.settings.switchbutton.data.model

import com.fairphone.settings.switchbutton.R

enum class SoundProfile(val titleResId: Int) {
    SILENT(R.string.sound_profile_silent),
    VIBRATE(R.string.sound_profile_vibrate),
    RING_AND_VIBRATE(R.string.sound_profile_loud),
}
