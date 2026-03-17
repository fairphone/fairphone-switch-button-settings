/*
 * Copyright (C) 2025 FairPhone B.V.
 *
 * SPDX-FileCopyrightText: 2025. FairPhone B.V.
 *
 * SPDX-License-Identifier: EUPL-1.2
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fairphone.settings.switchbutton.R
import com.fairphone.settings.switchbutton.data.model.SoundProfile
import com.fairphone.settings.switchbutton.data.model.SwitchButtonAction
import com.fairphone.settings.switchbutton.data.model.SwitchState
import com.fairphone.settings.switchbutton.ui.component.RadioButtonSetting
import com.fairphone.settings.switchbutton.ui.theme.SwitchButtonSettingsTheme
import com.fairphone.settings.switchbutton.ui.theme.prefScreenHeaderTextStyle
import com.fairphone.settings.switchbutton.ui.theme.prefSummaryTextStyle
import com.fairphone.settings.switchbutton.util.SwitchButtonSettingsUtils

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
fun SwitchButtonSettingsScreen(viewModel: SwitchButtonSettingsViewModel = viewModel(factory = SwitchButtonSettingsViewModel.Factory)) {
    val context = LocalContext.current.applicationContext

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SwitchButtonSettingsScreen(
        actions = uiState.switchButtonActions,
        selectedSetting = uiState.selectedSwitchButtonAction,
        enabled = uiState.switchState == SwitchState.UP,
        onSettingSelected = { setting ->
            viewModel.onUiEvent(UiEvent.SwitchActionSelected(setting))
        },
        soundProfileOptions = uiState.soundProfileOptions,
        onSoundUpSelected = {
            viewModel.onUiEvent(UiEvent.SoundProfileUpSelected(it))
        },
        onSoundDownSelected = {
            viewModel.onUiEvent(UiEvent.SoundProfileDownSelected(it))
        }
    )
}

@Composable
fun SwitchButtonSettingsScreen(
    actions: List<SwitchActionSetting>,
    selectedSetting: String,
    enabled: Boolean,
    onSettingSelected: (SwitchActionSetting) -> Unit,
    soundProfileOptions: Pair<SoundProfile, SoundProfile>? = null,
    onSoundUpSelected: (SoundProfile) -> Unit,
    onSoundDownSelected: (SoundProfile) -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                        .background(
                            MaterialTheme.colorScheme.surfaceContainer,
                            RoundedCornerShape(10.dp)
                        )
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
                // Refactor this and make it more generic
                if (action.key == SwitchButtonAction.SoundProfiles.key) {
                    val soundProfiles = SoundProfile.entries.toTypedArray()
                    RadioButtonSetting(
                        title = stringResource(action.titleResId),
                        summary = stringResource(action.summaryResId),
                        selected = selectedSetting == action.key,
                        enabled = enabled,
                        onRadioButtonClicked = { onSettingSelected(action) },
                        options = soundProfiles.map { stringResource(it.titleResId) },
                        switchUpSelectedOption = soundProfileOptions?.first?.ordinal,
                        switchDownSelectedOption = soundProfileOptions?.second?.ordinal,
                        onSwitchUpOptionSelected = { onSoundUpSelected(soundProfiles[it]) },
                        onSwitchDownOptionSelected = { onSoundDownSelected(soundProfiles[it]) },
                    )
                } else {
                    RadioButtonSetting(
                        title = stringResource(action.titleResId),
                        summary = stringResource(action.summaryResId),
                        selected = selectedSetting == action.key,
                        enabled = enabled,
                        onRadioButtonClicked = { onSettingSelected(action) },
                        options = null,
                    )
                }
            }
        }
    }
}

@Composable
@Preview
@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
fun SwitchButtonSettings_Preview() {
    SwitchButtonSettingsTheme {
        val actions = SwitchButtonSettingsUtils.getAllSwitchActions(LocalContext.current).map {
            SwitchActionSetting(
                key = it.key,
                titleResId = it.titleResId,
                summaryResId = it.summaryResId,
            )
        }
        SwitchButtonSettingsScreen(
            actions = actions,
            selectedSetting = SwitchButtonAction.SoundProfiles.key,
            enabled = true,
            onSettingSelected = {},
            onSoundUpSelected = {},
            onSoundDownSelected = {}
        )
    }
}
