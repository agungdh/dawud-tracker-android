package id.my.agungdh.dawudtracker.ui.screen.settings

import android.app.Application
import android.content.Context
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

data class LanguageOption(val key: String, val labelRes: Int)

val languageOptions = listOf(
    LanguageOption("system", id.my.agungdh.dawudtracker.R.string.lang_system),
    LanguageOption("en", id.my.agungdh.dawudtracker.R.string.lang_en),
    LanguageOption("id", id.my.agungdh.dawudtracker.R.string.lang_id),
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repo = SettingsRepository(db.appSettingDao())

    private val _hour = MutableStateFlow(SettingsRepository.DEFAULT_NOTIFICATION_HOUR)
    val hour: StateFlow<Int> = _hour.asStateFlow()

    private val _minute = MutableStateFlow(SettingsRepository.DEFAULT_NOTIFICATION_MINUTE)
    val minute: StateFlow<Int> = _minute.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("system")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved.asStateFlow()

    private val prefs = application.getSharedPreferences("dawud_prefs", Context.MODE_PRIVATE)

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
        _selectedLanguage.value = prefs.getString("app_language", "system") ?: "system"
    }

    fun updateHour(value: Int) { _hour.value = value }
    fun updateMinute(value: Int) { _minute.value = value }
    fun updateLanguage(value: String) { _selectedLanguage.value = value }

    fun save(context: android.content.Context) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.setSetting(SettingsRepository.KEY_NOTIFICATION_HOUR, _hour.value.toString())
            repo.setSetting(SettingsRepository.KEY_NOTIFICATION_MINUTE, _minute.value.toString())
            FastingReminderReceiver.cancelReminder(context)
            FastingReminderReceiver.scheduleReminder(context, _hour.value, _minute.value)
            prefs.edit().putString("app_language", _selectedLanguage.value).apply()
            _saved.value = true
        }
    }
}
