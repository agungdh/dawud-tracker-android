package id.my.agungdh.dawudtracker.ui.screen.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.my.agungdh.dawudtracker.data.database.AppDatabase
import id.my.agungdh.dawudtracker.data.entity.FastingStatus
import id.my.agungdh.dawudtracker.data.repository.FastingRepository
import id.my.agungdh.dawudtracker.data.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val fastingRepo = FastingRepository(db.fastingDayDao())
    private val settingsRepo = SettingsRepository(db.appSettingDao())

    private val _currentMonth = MutableStateFlow(Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    })
    val currentMonth: StateFlow<Calendar> = _currentMonth.asStateFlow()

    private val _monthDays = MutableStateFlow<List<Int>>(emptyList())
    val monthDays: StateFlow<List<Int>> = _monthDays.asStateFlow()

    private val _daysInMonth: MutableStateFlow<Map<Long, FastingStatus>> = MutableStateFlow(emptyMap())
    val daysInMonth: StateFlow<Map<Long, FastingStatus>> = _daysInMonth.asStateFlow()

    val totalFasted = fastingRepo.getTotalFasted()

    private val _monthlyFasted = MutableStateFlow(0)
    val monthlyFasted: StateFlow<Int> = _monthlyFasted.asStateFlow()

    private val _longestStreak = MutableStateFlow(0)
    val longestStreak: StateFlow<Int> = _longestStreak.asStateFlow()

    private val _notificationTime = MutableStateFlow("20:00")
    val notificationTime: StateFlow<String> = _notificationTime.asStateFlow()

    init {
        loadCurrentMonth()
        loadSettings()
        observeStreak()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val hour = settingsRepo.getSettingOnce(SettingsRepository.KEY_NOTIFICATION_HOUR)?.value
                ?: SettingsRepository.DEFAULT_NOTIFICATION_HOUR.toString()
            val minute = settingsRepo.getSettingOnce(SettingsRepository.KEY_NOTIFICATION_MINUTE)?.value
                ?: SettingsRepository.DEFAULT_NOTIFICATION_MINUTE.toString()
            _notificationTime.value = "${hour.padStart(2, '0')}:${minute.padStart(2, '0')}"
        }
    }

    private fun observeStreak() {
        viewModelScope.launch(Dispatchers.IO) {
            fastingRepo.getAll().collect {
                _longestStreak.value = fastingRepo.getLongestStreak()
            }
        }
    }

    fun loadCurrentMonth() {
        viewModelScope.launch(Dispatchers.IO) {
            val monthCal = _currentMonth.value.clone() as Calendar

            val monthStart = monthCal.timeInMillis

            monthCal.set(Calendar.DAY_OF_MONTH, monthCal.getActualMaximum(Calendar.DAY_OF_MONTH))
            monthCal.set(Calendar.HOUR_OF_DAY, 23)
            monthCal.set(Calendar.MINUTE, 59)
            monthCal.set(Calendar.SECOND, 59)
            monthCal.set(Calendar.MILLISECOND, 999)
            val monthEnd = monthCal.timeInMillis

            val firstDayCal = _currentMonth.value.clone() as Calendar
            val daysInMonthCount = firstDayCal.getActualMaximum(Calendar.DAY_OF_MONTH)
            val firstDayOfWeek = firstDayCal.get(Calendar.DAY_OF_WEEK) - 1

            val days = mutableListOf<Int>()
            repeat(firstDayOfWeek) { days.add(0) }
            for (i in 1..daysInMonthCount) {
                days.add(i)
            }

            _monthDays.value = days

            fastingRepo.getByDateRange(monthStart, monthEnd).collect { fastingDays ->
                val map = mutableMapOf<Long, FastingStatus>()
                for (fd in fastingDays) {
                    map[fd.date] = fd.status
                }
                _daysInMonth.value = map

                monthlyFastedCount(monthStart, monthEnd)
            }
        }
    }

    private suspend fun monthlyFastedCount(start: Long, end: Long) {
        _monthlyFasted.value = fastingRepo.getMonthlyFastedCount(start, end)
    }

    fun navigateMonth(forward: Boolean) {
        val newMonth = _currentMonth.value.clone() as Calendar
        if (forward) {
            newMonth.add(Calendar.MONTH, 1)
        } else {
            newMonth.add(Calendar.MONTH, -1)
        }
        _currentMonth.value = newMonth
        loadCurrentMonth()
    }

    fun markToday(status: FastingStatus) {
        viewModelScope.launch {
            val today = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            fastingRepo.markDay(today, status)
            loadCurrentMonth()
        }
    }

    fun toggleDay(year: Int, month: Int, day: Int) {
        viewModelScope.launch {
            val dateCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val dateMs = dateCal.timeInMillis
            val existing = fastingRepo.getByDate(dateMs)
            when (existing?.status) {
                null -> fastingRepo.markDay(dateMs, FastingStatus.FASTED)
                FastingStatus.FASTED -> fastingRepo.markDay(dateMs, FastingStatus.NOT_FASTED)
                FastingStatus.NOT_FASTED -> fastingRepo.deleteDay(dateMs)
            }
            loadCurrentMonth()
        }
    }
}
