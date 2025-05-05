package com.fairphone.settings.switchbutton.util

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import com.fairphone.settings.switchbutton.action.SwitchActionHandler
import com.fairphone.settings.switchbutton.data.model.SwitchButtonAction
import com.fairphone.settings.switchbutton.data.model.SwitchButtonActions
import com.fairphone.settings.switchbutton.data.model.SwitchButtonActionsFairphoneSprings
import com.fairphone.settings.switchbutton.data.model.SwitchState

object SwitchButtonSettingsUtils {

    /**
     * Global Settings used to store switch button related settings.
     */
    private const val SETTING_SWITCH_BUTTON_ACTION = Settings.Global.SWITCH_BUTTON_ACTION
    private const val SETTING_SWITCH_BUTTON_STATE = Settings.Global.SWITCH_BUTTON_STATE

    /**
     * @return a list of all available actions for the switch button.
     */
    fun getAllSwitchActions(context: Context): List<SwitchButtonAction> {
        return if (context.isFairphoneMomentsAvailable()) {
            SwitchButtonActionsFairphoneSprings
        } else {
            SwitchButtonActions
        }
    }

    /**
     * Get the action for the switch button.
     */
    fun getCurrentSwitchButtonAction(context: Context): SwitchButtonAction {
        val settingValue = Settings.Global.getInt(
            context.contentResolver,
            SETTING_SWITCH_BUTTON_ACTION,
            getDefaultSwitchButtonAction(context).settingValue
        )

        return SwitchButtonAction.fromSettingValue(settingValue)
            ?: throw IllegalStateException("Unknown switch button action")
    }

    /**
     * Set the action for the switch button.
     */
    fun setSwitchButtonAction(context: Context, action: SwitchButtonAction): Boolean {
        return Settings.Global.putInt(
            context.contentResolver,
            SETTING_SWITCH_BUTTON_ACTION,
            action.settingValue
        )
    }

    /**
     * Get the handler for the switch button action.
     */
    fun getSwitchButtonActionSettingHandler(context: Context): SwitchActionHandler {
        return getCurrentSwitchButtonAction(context).actionHandler
    }

    /**
     * Get the state of the switch button from the intent received by the system.
     */
    fun getSwitchState(intent: Intent): SwitchState? {
        val statusString = intent.getStringExtra(Constants.EXTRA_SWITCH_STATUS) ?: return null
        return try {
            SwitchState.valueOf(statusString)
        } catch (e: IllegalArgumentException) {
            Log.e("getSwitchState()", "Could not read switch status: $statusString", e)
            null
        }
    }

    /**
     * Get the default action for the switch button.
     */
    private fun getDefaultSwitchButtonAction(context: Context) =
        if (context.isFairphoneMomentsAvailable()) {
            SwitchButtonAction.FairphoneMoments
        } else {
            SwitchButtonAction.DoNotDisturb
        }

    /**
     * Get the state of the switch button, saved in a Global Setting.
     *
     * 1 means "UP" and 0 means "DOWN"
     */
    fun getLastKnownSwitchButtonStateFromSystem(context: Context): SwitchState {
        val settingValue = Settings.Global.getInt(
            context.contentResolver,
            SETTING_SWITCH_BUTTON_STATE,
            1
        )
        return if (settingValue == 1) {
            SwitchState.UP
        } else {
            SwitchState.DOWN
        }
    }
}
