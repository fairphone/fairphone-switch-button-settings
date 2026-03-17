/*
 * Copyright (C) 2025 FairPhone B.V.
 *
 * SPDX-FileCopyrightText: 2025. FairPhone B.V.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package com.fairphone.settings.switchbutton.ui.screen

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fairphone.settings.switchbutton.data.model.SoundProfile
import com.fairphone.settings.switchbutton.data.model.SwitchButtonAction
import com.fairphone.settings.switchbutton.data.model.SwitchState
import com.fairphone.settings.switchbutton.data.prefs.AppPrefs
import com.fairphone.settings.switchbutton.util.SwitchButtonSettingsUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SwitchButtonSettingsViewModel(
    context: Application,
    private val appPrefs: AppPrefs,
) : AndroidViewModel(context) {

    private val switchStateFlow = appPrefs.getLastKnownSwitchStateFlow()

    private val selectedSwitchActionFlow: MutableStateFlow<String> =
        MutableStateFlow(SwitchButtonSettingsUtils.getCurrentSwitchButtonAction(context).key)

    private val switchButtonActions: MutableStateFlow<List<SwitchActionSetting>> =
        MutableStateFlow(emptyList())

    private val soundProfileOptions = combine(
        appPrefs.getSoundProfileUpFlow(),
        appPrefs.getSoundProfileDownFlow()
    ) { up, down ->
        Pair(up, down)
    }

    val uiState = combine(
        switchStateFlow,
        switchButtonActions,
        selectedSwitchActionFlow,
        soundProfileOptions,
    ) { switchState, switchButtonActions, selectedSwitchAction, soundProfileOptions ->
        UiState(
            switchState = switchState,
            switchButtonActions = switchButtonActions,
            selectedSwitchButtonAction = selectedSwitchAction,
            soundProfileOptions = soundProfileOptions
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5000),
        initialValue = UiState(
            switchState = SwitchState.UP,
            switchButtonActions = switchButtonActions.value,
            selectedSwitchButtonAction = selectedSwitchActionFlow.value,
        )
    )

    private val context: Context
        get() = getApplication<Application>().applicationContext

    init {
        viewModelScope.launch {
            switchButtonActions.value = getSwitchButtonActions()
        }
    }

    private fun getSwitchButtonActions(): List<SwitchActionSetting> {
        return SwitchButtonSettingsUtils.getAllSwitchActions(context).map {
            SwitchActionSetting(
                key = it.key,
                titleResId = it.titleResId,
                summaryResId = it.summaryResId,
            )
        }
    }

    fun onUiEvent(event: UiEvent) = viewModelScope.launch {
        when (event) {
            is UiEvent.SwitchActionSelected -> {
                onSwitchActionSelected(event.setting)
            }

            is UiEvent.SoundProfileUpSelected -> {
                onSoundProfileUpSelected(event.profile)
            }

            is UiEvent.SoundProfileDownSelected -> {
                onSoundProfileDownSelected(event.profile)
            }
        }
    }

    private fun onSwitchActionSelected(setting: SwitchActionSetting) {
        selectedSwitchActionFlow.value = setting.key
        val action = SwitchButtonAction.entries.toTypedArray().find { it.key == setting.key }
            ?: throw IllegalStateException("Unknown switch button action")
        SwitchButtonSettingsUtils.setSwitchButtonAction(context, action)
    }

    private suspend fun onSoundProfileUpSelected(soundProfile: SoundProfile) {
        appPrefs.setSoundProfileUp(soundProfile)
    }

    private suspend fun onSoundProfileDownSelected(soundProfile: SoundProfile) {
        appPrefs.setSoundProfileDown(soundProfile)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                SwitchButtonSettingsViewModel(
                    application,
                    AppPrefs(application)
                )
            }
        }
    }
}

data class UiState(
    val switchState: SwitchState,
    val switchButtonActions: List<SwitchActionSetting>,
    val selectedSwitchButtonAction: String,
    val soundProfileOptions: Pair<SoundProfile, SoundProfile>? = null,
)

data class SwitchActionSetting(
    val key: String,
    val titleResId: Int,
    val summaryResId: Int,
)

sealed class UiEvent {
    data class SwitchActionSelected(val setting: SwitchActionSetting) : UiEvent()
    data class SoundProfileUpSelected(val profile: SoundProfile) : UiEvent()
    data class SoundProfileDownSelected(val profile: SoundProfile) : UiEvent()
}
