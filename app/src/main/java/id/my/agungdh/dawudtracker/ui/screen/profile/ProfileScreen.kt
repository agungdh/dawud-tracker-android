package id.my.agungdh.dawudtracker.ui.screen.profile

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel()
) {
    val name by viewModel.name.collectAsState()
    val reason by viewModel.reason.collectAsState()
    val startDate by viewModel.startDate.collectAsState()
    val saved by viewModel.saved.collectAsState()

    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.title_profile),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { viewModel.updateName(it) },
                label = { Text(stringResource(R.string.label_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = reason,
                onValueChange = { viewModel.updateReason(it) },
                label = { Text(stringResource(R.string.label_reason)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = if (startDate != null) dateFormat.format(Date(startDate!!)) else "",
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.label_start_date)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                        if (startDate != null) {
                            cal.timeInMillis = startDate!!
                        }
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val selected = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                                    set(Calendar.YEAR, year)
                                    set(Calendar.MONTH, month)
                                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                    set(Calendar.HOUR_OF_DAY, 0)
                                    set(Calendar.MINUTE, 0)
                                    set(Calendar.SECOND, 0)
                                    set(Calendar.MILLISECOND, 0)
                                }
                                viewModel.updateStartDate(selected.timeInMillis)
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                trailingIcon = {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = stringResource(R.string.btn_pick_date)
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.save() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.btn_save))
            }

            if (saved) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.msg_profile_saved),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
