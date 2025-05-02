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
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.fairphone.settings.switchbutton.util

object Constants {
    const val LOG_TAG = "SwitchButtonSettings"
    const val ACTION_SWITCH_BUTTON = "com.fairphone.action.SWITCH_STATE_CHANGED"
    const val ACTION_SPRING_LAUNCHER_SETTINGS = "com.fairphone.action.SPRING_LAUNCHER_SETTINGS"
    const val ACTION_START_SPRING_LAUNCHER = "com.fairphone.action.START_SPRING_LAUNCHER"
    const val ACTION_STOP_SPRING_LAUNCHER = "com.fairphone.action.STOP_SPRING_LAUNCHER"

    const val EXTRA_SWITCH_STATUS = "com.fairphone.extra.SWITCH_STATUS"



    const val FAIRPHONE_MOMENTS_PACKAGE_NAME = "com.fairphone.spring.launcher"
    const val FAIRPHONE_MOMENTS_HOME_ACTIVITY = "com.fairphone.spring.launcher.activity.SpringLauncherHomeActivity"
    const val FAIRPHONE_MOMENTS_SETTINGS_ACTIVITY = "com.fairphone.spring.launcher.activity.LauncherSettingsActivity"


    const val ACTION_SHOW_SWITCH_BUTTON_HINT = "com.fairphone.action.SHOW_SWITCH_BUTTON_HINT"


    const val STOCK_LAUNCHER_PACKAGE_NAME = "com.android.launcher3"
    const val STOCK_LAUNCHER_ACTIVITY = "com.android.searchlauncher.SearchLauncher"

    const val EXTRA_SHOW_OVERLAY = "com.fairphone.spring.launcher.extra.show_overlay"
    const val EXTRA_SWITCH_BUTTON_STATE = "com.fairphone.spring.launcher.extra.switch_button_state"
    const val EXTRA_SWITCH_BUTTON_STATE_ENABLED = "ENABLED"
    const val EXTRA_SWITCH_BUTTON_STATE_DISABLED = "DISABLED"

}
