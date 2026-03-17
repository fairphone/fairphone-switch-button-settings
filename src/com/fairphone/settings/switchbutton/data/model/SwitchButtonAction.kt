/*
 * Copyright (C) 2025 FairPhone B.V.
 *
 * SPDX-FileCopyrightText: 2025. FairPhone B.V.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package com.fairphone.settings.switchbutton.data.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.fairphone.settings.switchbutton.R
import com.fairphone.settings.switchbutton.action.BlueLightFilterSwitchActionHandler
import com.fairphone.settings.switchbutton.action.DarkModeModeSwitchActionHandler
import com.fairphone.settings.switchbutton.action.DoNotDisturbSwitchActionHandler
import com.fairphone.settings.switchbutton.action.FairphoneMomentsSwitchActionHandler
import com.fairphone.settings.switchbutton.action.FlightModeSwitchActionHandler
import com.fairphone.settings.switchbutton.action.PowerSaverModeSwitchActionHandler
import com.fairphone.settings.switchbutton.action.SoundProfilesSwitchActionHandler
import com.fairphone.settings.switchbutton.action.SwitchActionHandler
import com.fairphone.settings.switchbutton.action.TorchLightSwitchActionHandler
import com.fairphone.settings.switchbutton.action.WorkProfileSwitchActionHandler
import com.fairphone.settings.switchbutton.action.emptyActionHandler

const val KEY_SWITCH_SETTING_DO_NOT_DISTURB = "setting_switch_do_not_disturb"
const val KEY_SWITCH_SETTING_FLIGHT_MODE = "setting_switch_flight_mode"
const val KEY_SWITCH_SETTING_TORCH_LIGHT = "setting_switch_torch_light"
const val KEY_SWITCH_SETTING_DARK_MODE = "setting_switch_dark_mode"
const val KEY_SWITCH_SETTING_POWER_SAVER = "setting_switch_power_saver"
const val KEY_SWITCH_SETTING_DO_NOTHING = "setting_switch_do_nothing"
const val KEY_SWITCH_SETTING_FAIRPHONE_MOMENTS = "setting_switch_fairphone_moments"
const val KEY_SWITCH_SETTING_WORK_PROFILE = "setting_switch_work_profile"
const val KEY_SWITCH_SETTING_SOUND_PROFILES = "setting_switch_sound_profiles"
const val KEY_SWITCH_SETTING_BLUE_LIGHT_FILTER = "setting_switch_blue_light_filter"

enum class SwitchButtonAction(
    val key: String,
    val titleResId: Int,
    val summaryResId: Int,
    val icon: ImageVector? = null,
    val settingValue: Int,
    val actionHandler: SwitchActionHandler,
) {
    FairphoneMoments(
        key = KEY_SWITCH_SETTING_FAIRPHONE_MOMENTS,
        titleResId = R.string.pref_title_digital_detox,
        summaryResId = R.string.pref_summary_digital_detox,
        // TODO: Hide gear icon for now. See https://gitlab.fairphone.com/mobile-applications/fp6-apps/fp6-digital-detox/-/issues/126
        // icon = Icons.Default.Settings,
        settingValue = 1,
        actionHandler = FairphoneMomentsSwitchActionHandler,
    ),

    DoNotDisturb(
        key = KEY_SWITCH_SETTING_DO_NOT_DISTURB,
        titleResId = R.string.pref_title_do_not_disturb,
        summaryResId = R.string.pref_summary_do_not_disturb,
        settingValue = 2,
        actionHandler = DoNotDisturbSwitchActionHandler,
    ),

    FlightMode(
        key = KEY_SWITCH_SETTING_FLIGHT_MODE,
        titleResId = R.string.pref_title_flight_mode,
        summaryResId = R.string.pref_summary_flight_mode,
        settingValue = 3,
        actionHandler = FlightModeSwitchActionHandler,
    ),

    TorchLight(
        key = KEY_SWITCH_SETTING_TORCH_LIGHT,
        titleResId = R.string.pref_title_torch_light,
        summaryResId = R.string.pref_summary_torch_light,
        settingValue = 4,
        actionHandler = TorchLightSwitchActionHandler,
    ),

    DarkMode(
        key = KEY_SWITCH_SETTING_DARK_MODE,
        titleResId = R.string.pref_title_dark_mode,
        summaryResId = R.string.pref_summary_dark_mode,
        settingValue = 5,
        actionHandler = DarkModeModeSwitchActionHandler,
    ),

    PowerSaver(
        key = KEY_SWITCH_SETTING_POWER_SAVER,
        titleResId = R.string.pref_title_battery_saver_mode,
        summaryResId = R.string.pref_summary_battery_saver_mode,
        settingValue = 6,
        actionHandler = PowerSaverModeSwitchActionHandler,
    ),

    WorkProfile(
        key = KEY_SWITCH_SETTING_WORK_PROFILE,
        titleResId = R.string.pref_title_work_profile,
        summaryResId = R.string.pref_summary_work_profile,
        settingValue = 7,
        actionHandler = WorkProfileSwitchActionHandler,
    ),

    DoNothing(
        key = KEY_SWITCH_SETTING_DO_NOTHING,
        titleResId = R.string.pref_title_do_nothing,
        summaryResId = R.string.pref_summary_do_nothing,
        settingValue = 8,
        actionHandler = emptyActionHandler
    ),

    SoundProfiles(
        key = KEY_SWITCH_SETTING_SOUND_PROFILES,
        titleResId = R.string.pref_title_sound_profiles,
        summaryResId = R.string.pref_summary_sound_profiles,
        settingValue = 9,
        actionHandler = SoundProfilesSwitchActionHandler,
    ),

    BlueLightFilter(
        key = KEY_SWITCH_SETTING_BLUE_LIGHT_FILTER,
        titleResId = R.string.pref_title_blue_light_filter,
        summaryResId = R.string.pref_summary_blue_light_filter,
        settingValue = 10,
        actionHandler = BlueLightFilterSwitchActionHandler,
    );

    companion object {
        fun fromSettingValue(value: Int): SwitchButtonAction? = when (value) {
            1 -> FairphoneMoments
            2 -> DoNotDisturb
            3 -> FlightMode
            4 -> TorchLight
            5 -> DarkMode
            6 -> PowerSaver
            7 -> WorkProfile
            8 -> DoNothing
            9 -> SoundProfiles
            10 -> BlueLightFilter
            else -> null
        }
    }
}

val SwitchButtonActions: List<SwitchButtonAction> = listOf(
    SwitchButtonAction.DoNotDisturb,
    SwitchButtonAction.FlightMode,
    SwitchButtonAction.TorchLight,
    SwitchButtonAction.DarkMode,
    SwitchButtonAction.PowerSaver,
    SwitchButtonAction.WorkProfile,
    SwitchButtonAction.SoundProfiles,
    SwitchButtonAction.BlueLightFilter,
    SwitchButtonAction.DoNothing,
)

val SwitchButtonActionsFairphoneSprings: List<SwitchButtonAction> = listOf(
    SwitchButtonAction.FairphoneMoments,
    SwitchButtonAction.DoNotDisturb,
    SwitchButtonAction.FlightMode,
    SwitchButtonAction.TorchLight,
    SwitchButtonAction.DarkMode,
    SwitchButtonAction.PowerSaver,
    SwitchButtonAction.WorkProfile,
    SwitchButtonAction.SoundProfiles,
    SwitchButtonAction.BlueLightFilter,
    SwitchButtonAction.DoNothing,
)