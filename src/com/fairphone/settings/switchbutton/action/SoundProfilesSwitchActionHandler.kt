/*
 * Copyright (C) 2026 FairPhone B.V.
 *
 * SPDX-FileCopyrightText: 2025. FairPhone B.V.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package com.fairphone.settings.switchbutton.action

import android.content.Context
import android.media.AudioManager
import com.fairphone.settings.switchbutton.data.model.SoundProfile
import com.fairphone.settings.switchbutton.data.prefs.AppPrefs
import com.fairphone.settings.switchbutton.util.audioManager
import kotlinx.coroutines.flow.first

object SoundProfilesSwitchActionHandler : SwitchActionHandler() {

    override suspend fun onSwitchStateUp(context: Context): Result<Unit> {
        val appPrefs = AppPrefs(context)
        val soundProfileUp = appPrefs.getSoundProfileUpFlow().first()
        return setSoundProfile(context, soundProfileUp)
    }

    override suspend fun onSwitchStateDown(context: Context): Result<Unit> {
        val appPrefs = AppPrefs(context)
        val soundProfileDown = appPrefs.getSoundProfileDownFlow().first()
        return setSoundProfile(context, soundProfileDown)
    }

    private fun setSoundProfile(context: Context, soundProfile: SoundProfile): Result<Unit> {
        val ringerMode = when (soundProfile) {
            SoundProfile.SILENT -> AudioManager.RINGER_MODE_SILENT
            SoundProfile.VIBRATE -> AudioManager.RINGER_MODE_VIBRATE
            SoundProfile.RING_AND_VIBRATE -> AudioManager.RINGER_MODE_NORMAL
        }
        context.audioManager().ringerMode = ringerMode

        return Result.success(Unit)
    }
}
