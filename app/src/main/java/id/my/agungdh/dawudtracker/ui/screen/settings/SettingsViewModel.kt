package id.my.agungdh.dawudtracker.ui.screen.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.my.agungdh.dawudtracker.data.database.AppDatabase
import id.my.agungdh.dawudtracker.data.repository.SettingsRepository
import id.my.agungdh.dawudtracker.notification.FastingReminderReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repo = SettingsRepository(db.appSettingDao())

    private val _hour = MutableStateFlow(SettingsRepository.DEFAULT_NOTIFICATION_HOUR)
    val hour: StateFlow<Int> = _hour.asStateFlow()

    private val _minute = MutableStateFlow(SettingsRepository.DEFAULT_NOTIFICATION_MINUTE)
    val minute: StateFlow<Int> = _minute.asStateFlow()

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            val savedHour = repo.getSettingOnce(SettingsRepository.KEY_NOTIFICATION_HOUR)
            val savedMinute = repo.getSettingOnce(SettingsRepository.KEY_NOTIFICATION_MINUTE)
            _hour.value = savedHour?.value?.toIntOrNull() ?: SettingsRepository.DEFAULT_NOTIFICATION_HOUR
            _minute.value = savedMinute?.value?.toIntOrNull() ?: SettingsRepository.DEFAULT_NOTIFICATION_MINUTE
        }
    }

    fun updateHour(value: Int) { _hour.value = value }
    fun updateMinute(value: Int) { _minute.value = value }

    fun save(context: android.content.Context) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.setSetting(SettingsRepository.KEY_NOTIFICATION_HOUR, _hour.value.toString())
            repo.setSetting(SettingsRepository.KEY_NOTIFICATION_MINUTE, _minute.value.toString())

            FastingReminderReceiver.cancelReminder(context)
            FastingReminderReceiver.scheduleReminder(context, _hour.value, _minute.value)

            _saved.value = true
        }
    }
}
