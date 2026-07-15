package id.my.agungdh.dawudtracker.ui.screen.settings

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.agungdh.dawudtracker.R

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val hour by viewModel.hour.collectAsState()
    val minute by viewModel.minute.collectAsState()
    val saved by viewModel.saved.collectAsState()

    val context = LocalContext.current

    val timeText = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.title_settings),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.label_notification),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = timeText,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.label_notif_time)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        TimePickerDialog(
                            context,
                            { _, h, m ->
                                viewModel.updateHour(h)
                                viewModel.updateMinute(m)
                            },
                            hour,
                            minute,
                            true
                        ).show()
                    },
                trailingIcon = {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = stringResource(R.string.btn_pick_time)
                    )
                },
                supportingText = {
                    Text(stringResource(R.string.desc_notif_time))
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.save(context) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.btn_save_settings))
            }

            if (saved) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.msg_settings_saved),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
