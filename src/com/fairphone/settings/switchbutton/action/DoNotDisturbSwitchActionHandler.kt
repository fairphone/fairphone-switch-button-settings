/*
 * Copyright (C) 2025 Fairphone B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.fairphone.settings.switchbutton.action

import android.app.AutomaticZenRule
import android.content.ComponentName
import android.content.Context
import android.service.notification.Condition
import android.service.notification.ZenPolicy
import android.util.Log
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fairphone.settings.switchbutton.SwitchButtonSettingsActivity
import com.fairphone.settings.switchbutton.data.model.SwitchState
import com.fairphone.settings.switchbutton.util.notificationManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * This class is responsible for handling the "Do Not Disturb" (DND) switch action.
 * It extends [SwitchActionHandler] and implements the logic for starting and stopping DND mode
 * based on the state of the switch button.
 *
 * It utilizes the NotificationManager to manage automatic zen rules and DataStore to persist
 * the zen rule ID.
 */
object DoNotDisturbSwitchActionHandler : SwitchActionHandler() {

    private const val DND_PREFS_DATASTORE = "dnd_prefs"
    private val Context.dndPrefsDataStore: DataStore<Preferences> by preferencesDataStore(name = DND_PREFS_DATASTORE)
    private val PREF_KEY_ZEN_RULE_ID = stringPreferencesKey("zen_rule_id")

    override suspend fun onSwitchButtonStateChanged(
        context: Context,
        state: SwitchState
    ): Result<Unit> {
        return try {
            when (state) {
                SwitchState.UP -> stopDND(context)
                SwitchState.DOWN -> startDND(context)
                else -> Unit // ignore
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("DoNotDisturbSwitch", "Error", e)
            Result.failure(e)
        }
    }

    private fun getZenRuleName() = "Switch"
    private fun getZenRuleConditionUri(context: Context) = context.packageName.toUri()

    private suspend fun startDND(context: Context) {
        if (context.notificationManager().getAutomaticZenRule(getSavedZenRuleId(context)) == null) {
            val zenRule = createDefaultZenRule(context)
            val zenRuleId = context.notificationManager().addAutomaticZenRule(zenRule)
            saveZenRuleId(context, zenRuleId)
        } else {
            val zenRuleId = getSavedZenRuleId(context)
            val zenRuleCondition = Condition(
                getZenRuleConditionUri(context),
                getZenRuleName(),
                Condition.STATE_TRUE,
            )
            context.notificationManager().setAutomaticZenRuleState(zenRuleId, zenRuleCondition)
        }
    }

    private suspend fun stopDND(context: Context) {
        val zenRuleId = getSavedZenRuleId(context)
        val zenRuleCondition = Condition(
            getZenRuleConditionUri(context),
            getZenRuleName(),
            Condition.STATE_FALSE,
        )
        context.notificationManager().setAutomaticZenRuleState(zenRuleId, zenRuleCondition)
    }

    private suspend fun saveZenRuleId(context: Context, zenRuleId: String) {
        context.dndPrefsDataStore.edit { settings ->
            settings[PREF_KEY_ZEN_RULE_ID] = zenRuleId
        }
    }

    private suspend fun getSavedZenRuleId(context: Context): String {
        return context.dndPrefsDataStore.data.map { preferences ->
            preferences[PREF_KEY_ZEN_RULE_ID] ?: ""
        }.first()
    }

    /**
     * Create a new default zen rule.
     *
     */
    private fun createDefaultZenRule(context: Context): AutomaticZenRule {
        val configurationActivity = ComponentName(
            context.packageName,
            SwitchButtonSettingsActivity::class.java.packageName + ".SwitchButtonSettingsActivity"
        )
        return AutomaticZenRule.Builder(getZenRuleName(), getZenRuleConditionUri(context))
            .setEnabled(true)
            .setOwner(configurationActivity)
            .setConfigurationActivity(configurationActivity)
            .setZenPolicy(
                ZenPolicy.Builder()
                    .allowCalls(ZenPolicy.PEOPLE_TYPE_NONE)
                    .allowMessages(ZenPolicy.PEOPLE_TYPE_NONE)
                    .allowSystem(false)
                    .allowAlarms(true)
                    .build()
            )
            .build()
    }
}
