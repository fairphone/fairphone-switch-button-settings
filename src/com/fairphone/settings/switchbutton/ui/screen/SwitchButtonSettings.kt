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

package com.fairphone.settings.switchbutton.ui.screen

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fairphone.settings.switchbutton.R
import com.fairphone.settings.switchbutton.model.SwitchButtonSetting
import com.fairphone.settings.switchbutton.ui.component.RadioButtonSetting
import com.fairphone.settings.switchbutton.ui.theme.prefScreenHeaderTextStyle
import com.fairphone.settings.switchbutton.util.Constants
import com.fairphone.settings.switchbutton.util.Constants.KEY_SWITCH_SETTING_SPRING_LAUNCHER
import com.fairphone.settings.switchbutton.util.SwitchButtonSettingsUtils
import com.fairphone.settings.switchbutton.util.notificationManager
import com.fairphone.settings.switchbutton.util.startSpringLauncherSettings

@Composable
fun SwitchButtonSettings() {
    val context = LocalContext.current.applicationContext

    var selectedSetting by remember {
        mutableStateOf(SwitchButtonSettingsUtils.getSwitchButtonActionSetting(context))
    }
    val switchSettings = remember {
        SwitchButtonSettingsUtils.getAllSwitchActions(context)
    }

    SwitchButtonSettings(
        actions = switchSettings,
        selectedSetting = selectedSetting,
        onSettingSelected = { action ->
            if (action == Constants.KEY_SWITCH_SETTING_DO_NOT_DISTURB) {
                if (!context.notificationManager().isNotificationPolicyAccessGranted) {
                    context.startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
                }
            }
            SwitchButtonSettingsUtils.setSwitchButtonActionSetting(context, action)
            selectedSetting = action
        },
        onOpenActionConfig = { action ->
            if (action == KEY_SWITCH_SETTING_SPRING_LAUNCHER) {
                context.startSpringLauncherSettings()
            }
        }
    )
}

@Composable
fun SwitchButtonSettings(
    actions: List<SwitchButtonSetting>,
    selectedSetting: String,
    onSettingSelected: (String) -> Unit,
    onOpenActionConfig: (String) -> Unit?,
) {
    val scrollState = rememberScrollState()

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(vertical = 16.dp)

    ) {
        Text(
            text = stringResource(R.string.pref_screen_header_switch),
            style = prefScreenHeaderTextStyle,
            modifier = Modifier
                .padding(horizontal = 24.dp),
        )

        Image(
            painter = painterResource(R.drawable.switch_button_settings_header),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .aspectRatio(1.38f / 1f)
                .clip(RoundedCornerShape(20.dp))
                .height(320.dp)

        )

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            actions.forEach { action ->
                RadioButtonSetting(
                    title = stringResource(action.titleResId),
                    summary = stringResource(action.summaryResId),
                    selected = selectedSetting == action.key,
                    onRadioButtonClicked = { onSettingSelected(action.key) },
                    icon = action.icon,
                    onIconClicked = { onOpenActionConfig.invoke(action.key) }
                )
            }
        }
    }
}
