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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fairphone.settings.switchbutton.R
import com.fairphone.settings.switchbutton.data.model.SwitchButtonAction
import com.fairphone.settings.switchbutton.data.model.SwitchState
import com.fairphone.settings.switchbutton.data.prefs.AppPrefs
import com.fairphone.settings.switchbutton.ui.component.RadioButtonSetting
import com.fairphone.settings.switchbutton.ui.theme.SwitchButtonSettingsTheme
import com.fairphone.settings.switchbutton.ui.theme.prefScreenHeaderTextStyle
import com.fairphone.settings.switchbutton.ui.theme.prefSummaryTextStyle
import com.fairphone.settings.switchbutton.util.SwitchButtonSettingsUtils
import com.fairphone.settings.switchbutton.util.startFairphoneMomentsSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwitchButtonSettings(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            SwitchButtonSettingsScreen()
        }
    }
}

@Composable
fun SwitchButtonSettingsScreen() {
    val context = LocalContext.current.applicationContext
    val appPrefs = remember { AppPrefs(context) }

    var selectedSetting by remember {
        mutableStateOf(SwitchButtonSettingsUtils.getCurrentSwitchButtonAction(context))
    }
    val switchSettings by remember {
        mutableStateOf(SwitchButtonSettingsUtils.getAllSwitchActions(context))
    }
    val switchState by appPrefs.getLastKnownSwitchStateFlow().collectAsStateWithLifecycle(
        initialValue = SwitchState.UP,
        lifecycleOwner = LocalLifecycleOwner.current
    )

    SwitchButtonSettingsScreen(
        actions = switchSettings,
        selectedSetting = selectedSetting,
        enabled = switchState == SwitchState.UP,
        onSettingSelected = { action ->
            SwitchButtonSettingsUtils.setSwitchButtonAction(context, action)
            selectedSetting = action
        },
        onOpenActionConfig = { action ->
            if (action == SwitchButtonAction.FairphoneMoments) {
                context.startFairphoneMomentsSettings()
            }
        }
    )
}

@Composable
fun SwitchButtonSettingsScreen(
    actions: List<SwitchButtonAction>,
    selectedSetting: SwitchButtonAction,
    enabled: Boolean,
    onSettingSelected: (SwitchButtonAction) -> Unit,
    onOpenActionConfig: (SwitchButtonAction) -> Unit?,
) {
    val scrollState = rememberScrollState()

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 32.dp)

    ) {
        Text(
            text = stringResource(R.string.pref_title_switch_button),
            style = prefScreenHeaderTextStyle,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp),
        )

        if (!enabled) {
            Box(Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.disable_switch_to_change_setting_text),
                    style = prefSummaryTextStyle,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(10.dp))
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            actions.forEach { action ->
                RadioButtonSetting(
                    title = stringResource(action.titleResId),
                    summary = stringResource(action.summaryResId),
                    selected = selectedSetting.key == action.key,
                    enabled = enabled,
                    onRadioButtonClicked = { onSettingSelected(action) },
                    icon = action.icon,
                    onIconClicked = {
                        if (enabled) {
                            onOpenActionConfig.invoke(action)
                        }
                    },
                )
            }
        }
    }
}

@Composable
@Preview
fun SwitchButtonSettings_Preview() {
    SwitchButtonSettingsTheme {
        SwitchButtonSettingsScreen(
            actions = SwitchButtonSettingsUtils.getAllSwitchActions(LocalContext.current),
            selectedSetting = SwitchButtonAction.DoNotDisturb,
            enabled = true,
            onSettingSelected = {},
            onOpenActionConfig = {}
        )
    }
}
