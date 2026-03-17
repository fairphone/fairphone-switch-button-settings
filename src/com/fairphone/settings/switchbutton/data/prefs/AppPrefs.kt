/*
 * Copyright (C) 2025 FairPhone B.V.
 *
 * SPDX-FileCopyrightText: 2025. FairPhone B.V.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package com.fairphone.settings.switchbutton.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fairphone.settings.switchbutton.data.model.SoundProfile
import com.fairphone.settings.switchbutton.data.model.SwitchState
import com.fairphone.settings.switchbutton.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

const val APP_PREFS_DATASTORE = "app_prefs"
val Context.appPrefs: DataStore<Preferences> by preferencesDataStore(name = APP_PREFS_DATASTORE)

class AppPrefs(context: Context) {

    private val dataStore: DataStore<Preferences> = context.appPrefs

    companion object {
        private val DEFAULT_SWITCH_STATE = SwitchState.UP.name
        private val KEY_SWITCH_STATE = stringPreferencesKey("switch_state")
        private val PREF_KEY_DEFAULT_HOME_APP = stringPreferencesKey("default_home_app")
        private val KEY_SOUND_PROFILE_UP = stringPreferencesKey("sound_profile_up")
        private val KEY_SOUND_PROFILE_DOWN = stringPreferencesKey("sound_profile_down")
        private val DEFAULT_SOUND_PROFILE_UP = SoundProfile.RING_AND_VIBRATE
        private val DEFAULT_SOUND_PROFILE_DOWN = SoundProfile.VIBRATE
    }

    fun getLastKnownSwitchStateFlow(): Flow<SwitchState> {
        return dataStore.data.map { prefs ->
            prefs[KEY_SWITCH_STATE] ?: DEFAULT_SWITCH_STATE
        }.map { SwitchState.valueOf(it) }
    }

    suspend fun setLastKnownSwitchState(state: SwitchState) {
        dataStore.edit { prefs ->
            prefs[KEY_SWITCH_STATE] = state.name
        }
    }

    /**
     * Get the saved default home app package name.
     */
    suspend fun getSavedHomeApp(): String {
        return dataStore.data.map { prefs ->
            prefs[PREF_KEY_DEFAULT_HOME_APP] ?: Constants.STOCK_ANDROID_LAUNCHER_PACKAGE_NAME
        }.first()
    }

    /**
     * Save the default home app package name.
     */
    suspend fun saveDefaultHomeApp(packageName: String) {
        dataStore.edit { prefs ->
            prefs[PREF_KEY_DEFAULT_HOME_APP] = packageName
        }
    }

    fun getSoundProfileUpFlow(): Flow<SoundProfile> {
        return dataStore.data.map { prefs ->
            prefs[KEY_SOUND_PROFILE_UP]
        }.map { it?.let { SoundProfile.valueOf(it) } ?: DEFAULT_SOUND_PROFILE_UP }
    }

    suspend fun setSoundProfileUp(profile: SoundProfile) {
        dataStore.edit { prefs ->
            prefs[KEY_SOUND_PROFILE_UP] = profile.name
        }
    }

    fun getSoundProfileDownFlow(): Flow<SoundProfile> {
        return dataStore.data.map { prefs ->
            prefs[KEY_SOUND_PROFILE_DOWN]
        }.map { it?.let { SoundProfile.valueOf(it) } ?: DEFAULT_SOUND_PROFILE_DOWN }
    }

    suspend fun setSoundProfileDown(profile: SoundProfile) {
        dataStore.edit { prefs ->
            prefs[KEY_SOUND_PROFILE_DOWN] = profile.name
        }
    }
}
