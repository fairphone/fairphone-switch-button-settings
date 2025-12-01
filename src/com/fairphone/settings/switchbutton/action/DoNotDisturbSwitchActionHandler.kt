/*
 * Copyright (C) 2025 FairPhone B.V.
 *
 * SPDX-FileCopyrightText: 2025. FairPhone B.V.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package com.fairphone.settings.switchbutton.action

import android.app.AutomaticZenRule
import android.content.ComponentName
import android.content.Context
import android.service.notification.Condition
import android.service.notification.ZenPolicy
import android.util.Log
import androidx.core.net.toUri
import com.fairphone.settings.switchbutton.SwitchButtonSettingsActivity
import com.fairphone.settings.switchbutton.data.model.SwitchState
import com.fairphone.settings.switchbutton.data.prefs.AppPrefs
import com.fairphone.settings.switchbutton.util.notificationManager

/**
 * This class is responsible for handling the "Do Not Disturb" (DND) switch action.
 * It extends [SwitchActionHandler] and implements the logic for starting and stopping DND mode
 * based on the state of the switch button.
 *
 * It utilizes the NotificationManager to manage automatic zen rules and AppPrefs to persist
 * the zen rule ID.
 */
object DoNotDisturbSwitchActionHandler : SwitchActionHandler() {

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
        val appPrefs = AppPrefs(context)

        if (context.notificationManager().getAutomaticZenRule(appPrefs.getSavedZenRuleId()) == null) {
            val zenRule = createDefaultZenRule(context)
            val zenRuleId = context.notificationManager().addAutomaticZenRule(zenRule)
            appPrefs.saveZenRuleId(zenRuleId)
        } else {
            val zenRuleId = appPrefs.getSavedZenRuleId()
            val zenRuleCondition = Condition(
                getZenRuleConditionUri(context),
                getZenRuleName(),
                Condition.STATE_TRUE,
            )
            context.notificationManager().setAutomaticZenRuleState(zenRuleId, zenRuleCondition)
        }
    }

    private suspend fun stopDND(context: Context) {
        val appPrefs = AppPrefs(context)
        val zenRuleId = appPrefs.getSavedZenRuleId()
        val zenRuleCondition = Condition(
            getZenRuleConditionUri(context),
            getZenRuleName(),
            Condition.STATE_FALSE,
        )
        context.notificationManager().setAutomaticZenRuleState(zenRuleId, zenRuleCondition)
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
