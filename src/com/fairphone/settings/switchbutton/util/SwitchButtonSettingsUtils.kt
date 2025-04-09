package com.fairphone.settings.switchbutton.util

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.provider.Settings
import com.fairphone.settings.switchbutton.action.DarkModeModeSwitchActionHandler
import com.fairphone.settings.switchbutton.action.DoNotDisturbSwitchActionHandler
import com.fairphone.settings.switchbutton.action.FairphoneMomentsSwitchActionHandler
import com.fairphone.settings.switchbutton.action.FlightModeSwitchActionHandler
import com.fairphone.settings.switchbutton.action.PowerSaverModeSwitchActionHandler
import com.fairphone.settings.switchbutton.action.SwitchActionHandler
import com.fairphone.settings.switchbutton.action.TorchLightSwitchActionHandler
import com.fairphone.settings.switchbutton.model.SwitchButtonSetting
import com.fairphone.settings.switchbutton.model.SwitchButtonSettings
import com.fairphone.settings.switchbutton.model.SwitchButtonSettingsFairphoneSpring
import com.fairphone.settings.switchbutton.util.Constants.KEY_SWITCH_SETTING_DARK_MODE
import com.fairphone.settings.switchbutton.util.Constants.KEY_SWITCH_SETTING_DO_NOT_DISTURB
import com.fairphone.settings.switchbutton.util.Constants.KEY_SWITCH_SETTING_FLIGHT_MODE
import com.fairphone.settings.switchbutton.util.Constants.KEY_SWITCH_SETTING_POWER_SAVER
import com.fairphone.settings.switchbutton.util.Constants.KEY_SWITCH_SETTING_SPRING_LAUNCHER
import com.fairphone.settings.switchbutton.util.Constants.KEY_SWITCH_SETTING_TORCH_LIGHT

object SwitchButtonSettingsUtils {

    private const val SWITCH_BUTTON_ACTION_SETTING = Settings.Global.SWITCH_BUTTON_ACTION

    private const val SWITCH_BUTTON_ACTION_SPRING_LAUNCHER = 1
    private const val SWITCH_BUTTON_ACTION_DO_NOT_DISTURB = 2
    private const val SWITCH_BUTTON_ACTION_FLIGHT_MODE = 3
    private const val SWITCH_BUTTON_ACTION_TORCH_LIGHT = 4
    private const val SWITCH_BUTTON_ACTION_DARK_MODE = 5
    private const val SWITCH_BUTTON_ACTION_POWER_SAVER = 6

    private val SWITCH_BUTTON_SETTING_MAP = mapOf(
        KEY_SWITCH_SETTING_POWER_SAVER to SWITCH_BUTTON_ACTION_POWER_SAVER,
        KEY_SWITCH_SETTING_DARK_MODE to SWITCH_BUTTON_ACTION_DARK_MODE,
        KEY_SWITCH_SETTING_DO_NOT_DISTURB to SWITCH_BUTTON_ACTION_DO_NOT_DISTURB,
        KEY_SWITCH_SETTING_FLIGHT_MODE to SWITCH_BUTTON_ACTION_FLIGHT_MODE,
        KEY_SWITCH_SETTING_SPRING_LAUNCHER to SWITCH_BUTTON_ACTION_SPRING_LAUNCHER,
        KEY_SWITCH_SETTING_TORCH_LIGHT to SWITCH_BUTTON_ACTION_TORCH_LIGHT,
    )

    private val SWITCH_BUTTON_SETTING_HANDLER_MAP = mapOf(
        KEY_SWITCH_SETTING_POWER_SAVER to PowerSaverModeSwitchActionHandler,
        KEY_SWITCH_SETTING_DARK_MODE to DarkModeModeSwitchActionHandler,
        KEY_SWITCH_SETTING_DO_NOT_DISTURB to DoNotDisturbSwitchActionHandler,
        KEY_SWITCH_SETTING_FLIGHT_MODE to FlightModeSwitchActionHandler,
        KEY_SWITCH_SETTING_SPRING_LAUNCHER to FairphoneMomentsSwitchActionHandler,
        KEY_SWITCH_SETTING_TORCH_LIGHT to TorchLightSwitchActionHandler,
    )

    fun getAllSwitchActions(context: Context): List<SwitchButtonSetting> {
        return if (context.isSpringLauncherAvailable()) {
            SwitchButtonSettingsFairphoneSpring
        } else {
            SwitchButtonSettings
        }
    }

    fun setSwitchButtonActionSetting(context: Context, action: String): Boolean {
        val settingValue = SWITCH_BUTTON_SETTING_MAP.getOrDefault(
            action,
            getDefaultSwitchButtonAction(context)
        )
        return Settings.Global.putInt(
            context.contentResolver,
            SWITCH_BUTTON_ACTION_SETTING,
            settingValue
        )
    }

    fun getSwitchButtonActionSetting(context: Context): String {
        val settingValue = Settings.Global.getInt(
            context.contentResolver,
            SWITCH_BUTTON_ACTION_SETTING,
            getDefaultSwitchButtonAction(context)
        )

        return when (settingValue) {
            SWITCH_BUTTON_ACTION_SPRING_LAUNCHER -> KEY_SWITCH_SETTING_SPRING_LAUNCHER
            SWITCH_BUTTON_ACTION_DO_NOT_DISTURB -> KEY_SWITCH_SETTING_DO_NOT_DISTURB
            SWITCH_BUTTON_ACTION_FLIGHT_MODE -> KEY_SWITCH_SETTING_FLIGHT_MODE
            SWITCH_BUTTON_ACTION_TORCH_LIGHT -> KEY_SWITCH_SETTING_TORCH_LIGHT
            SWITCH_BUTTON_ACTION_DARK_MODE -> KEY_SWITCH_SETTING_DARK_MODE
            SWITCH_BUTTON_ACTION_POWER_SAVER -> KEY_SWITCH_SETTING_POWER_SAVER
            else -> throw IllegalStateException("Unknown switch button action")
        }
    }

    fun getSwitchButtonActionSettingHandler(context: Context): SwitchActionHandler? {
        return SWITCH_BUTTON_SETTING_HANDLER_MAP[getSwitchButtonActionSetting(context)]
    }

    private fun getDefaultSwitchButtonAction(context: Context) =
        if (context.isSpringLauncherAvailable()) {
            SWITCH_BUTTON_ACTION_SPRING_LAUNCHER
        } else {
            SWITCH_BUTTON_ACTION_DO_NOT_DISTURB
        }
}

interface SettingsStateCallback {
    fun onChange(uri: Uri?)
}

class SettingsObserver(handler: Handler) : ContentObserver(handler) {
    var callback: SettingsStateCallback? = null

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        callback?.onChange(uri)
    }
}
