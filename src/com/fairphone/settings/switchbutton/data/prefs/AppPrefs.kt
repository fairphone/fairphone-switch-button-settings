/*
 * Copyright (c) 2025. Fairphone B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fairphone.settings.switchbutton.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fairphone.settings.switchbutton.data.model.SwitchState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val APP_PREFS_DATASTORE = "app_prefs"
val Context.appPrefs: DataStore<Preferences> by preferencesDataStore(name = APP_PREFS_DATASTORE)

class AppPrefs(private val dataStore: DataStore<Preferences>) {

    companion object {
        val KEY_SWITCH_STATE = stringPreferencesKey("switch_state")
        val DEFAULT_SWITCH_STATE = SwitchState.DOWN.name
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
}
