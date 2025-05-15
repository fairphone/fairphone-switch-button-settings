package com.fairphone.settings.switchbutton.ui.component

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
import androidx.compose.ui.unit.dp
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
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable(
                enabled = enabled,
                onClick = onRadioButtonClicked
            )
    ) {
        RadioButton(
            enabled = enabled,
            selected = selected,
            onClick = onRadioButtonClicked,
            //colors = RadioButtonDefaults.colors(
            //    selectedColor = MaterialTheme.colorScheme.secondary,
            //)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(top = 8.dp).weight(1f)
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
