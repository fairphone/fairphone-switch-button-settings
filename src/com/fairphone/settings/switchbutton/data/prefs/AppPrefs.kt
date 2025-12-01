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
        private val PREF_KEY_ZEN_RULE_ID = stringPreferencesKey("zen_rule_id")
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

    suspend fun saveZenRuleId(zenRuleId: String) {
        dataStore.edit { settings ->
            settings[PREF_KEY_ZEN_RULE_ID] = zenRuleId
        }
    }

    suspend fun getSavedZenRuleId(): String {
        return dataStore.data.map { preferences ->
            preferences[PREF_KEY_ZEN_RULE_ID] ?: ""
        }.first()
    }
}
