/*
 * Copyright (C) 2025 FairPhone B.V.
 *
 * SPDX-FileCopyrightText: 2025. FairPhone B.V.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package com.fairphone.settings.switchbutton.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fairphone.settings.switchbutton.R
import com.fairphone.settings.switchbutton.ui.theme.SwitchButtonSettingsTheme
import com.fairphone.settings.switchbutton.ui.theme.prefSummaryTextStyle
import com.fairphone.settings.switchbutton.ui.theme.prefTitleTextStyle

@Composable
fun RadioButtonSetting(
    title: String,
    summary: String,
    selected: Boolean,
    enabled: Boolean = true,
    onRadioButtonClicked: () -> Unit,
    icon: ImageVector? = null,
    onIconClicked: (() -> Unit)? = null,
    options: List<String>? = null,
    switchUpSelectedOption: Int? = null,
    switchDownSelectedOption: Int? = null,
    onSwitchUpOptionSelected: ((Int) -> Unit)? = null,
    onSwitchDownOptionSelected: ((Int) -> Unit)? = null,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        RadioButton(
            enabled = enabled,
            selected = selected,
            onClick = onRadioButtonClicked,
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .padding(top = 8.dp)
                .weight(1f)
        ) {
            Text(
                text = title,
                style = prefTitleTextStyle,
                color = if (enabled) {
                    MaterialTheme.colorScheme.onBackground
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            Text(
                text = summary,
                style = prefSummaryTextStyle,
                color = if (enabled) {
                    MaterialTheme.colorScheme.onBackground
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            if (selected && !options.isNullOrEmpty()) {
                DropdownSetting(
                    title = stringResource(R.string.switch_up),
                    options = options,
                    selectedOption = switchUpSelectedOption,
                    modifier = Modifier.padding(top = 8.dp),
                    onOptionSelected = { onSwitchUpOptionSelected?.invoke(it) },
                )

                DropdownSetting(
                    title = stringResource(R.string.switch_down),
                    options = options,
                    selectedOption = switchDownSelectedOption,
                    modifier = Modifier.padding(top = 8.dp),
                    onOptionSelected = { onSwitchDownOptionSelected?.invoke(it) },
                )
            }
        }

        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onIconClicked?.invoke() }
                    .padding(16.dp)
            )
        }
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
fun RadioButtonSetting_Preview() {
    SwitchButtonSettingsTheme {
        Column {
            RadioButtonSetting(
                title = "Fairphone Moments",
                summary = "Turns on/off Fairphone Moments.\nYou can adjust those settings in the app",
                options = listOf(""),
                selected = true,
                enabled = true,
                onRadioButtonClicked = {},
                icon = null,
                onIconClicked = {},
                onSwitchUpOptionSelected = {},
                onSwitchDownOptionSelected = {},
            )
        }
    }

}