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

package com.fairphone.settings.switchbutton.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.fairphone.settings.switchbutton.R
import com.fairphone.settings.switchbutton.util.Constants.KEY_SWITCH_SETTING_DARK_MODE
import com.fairphone.settings.switchbutton.util.Constants.KEY_SWITCH_SETTING_DO_NOT_DISTURB
import com.fairphone.settings.switchbutton.util.Constants.KEY_SWITCH_SETTING_FLIGHT_MODE
import com.fairphone.settings.switchbutton.util.Constants.KEY_SWITCH_SETTING_POWER_SAVER
import com.fairphone.settings.switchbutton.util.Constants.KEY_SWITCH_SETTING_SPRING_LAUNCHER
import com.fairphone.settings.switchbutton.util.Constants.KEY_SWITCH_SETTING_TORCH_LIGHT

data class SwitchButtonSetting(
    val key: String,
    val titleResId: Int,
    val summaryResId: Int,
    val icon: ImageVector? = null,
)

val DoNotDisturb = SwitchButtonSetting(
    key = KEY_SWITCH_SETTING_DO_NOT_DISTURB,
    titleResId = R.string.pref_title_do_not_disturb,
    summaryResId = R.string.pref_summary_do_not_disturb,
)

val FlightMode = SwitchButtonSetting(
    key = KEY_SWITCH_SETTING_FLIGHT_MODE,
    titleResId = R.string.pref_title_flight_mode,
    summaryResId = R.string.pref_summary_flight_mode,
)

val TorchLight = SwitchButtonSetting(
    key = KEY_SWITCH_SETTING_TORCH_LIGHT,
    titleResId = R.string.pref_title_torch_light,
    summaryResId = R.string.pref_summary_torch_light,
)

val DarkMode = SwitchButtonSetting(
    key = KEY_SWITCH_SETTING_DARK_MODE,
    titleResId = R.string.pref_title_dark_mode,
    summaryResId = R.string.pref_summary_dark_mode,
)

val PowerSaver = SwitchButtonSetting(
    key = KEY_SWITCH_SETTING_POWER_SAVER,
    titleResId = R.string.pref_title_battery_saver_mode,
    summaryResId = R.string.pref_summary_battery_saver_mode,
)

val SwitchButtonSettings: List<SwitchButtonSetting> = listOf(
    DoNotDisturb,
    FlightMode,
    TorchLight,
    DarkMode,
    PowerSaver,
)

val SpringLauncher = SwitchButtonSetting(
    key = KEY_SWITCH_SETTING_SPRING_LAUNCHER,
    titleResId = R.string.pref_title_digital_detox,
    summaryResId = R.string.pref_summary_digital_detox,
    icon = Icons.Default.Settings,
)

val SwitchButtonSettingsFairphoneSpring: List<SwitchButtonSetting> = listOf(
    SpringLauncher,
    DoNotDisturb,
    FlightMode,
    TorchLight,
    DarkMode,
    PowerSaver,
)