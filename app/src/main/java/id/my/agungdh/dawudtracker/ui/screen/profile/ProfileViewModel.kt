package id.my.agungdh.dawudtracker.ui.screen.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.my.agungdh.dawudtracker.data.database.AppDatabase
import id.my.agungdh.dawudtracker.data.entity.UserProfile
import id.my.agungdh.dawudtracker.data.repository.UserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repo = UserProfileRepository(db.userProfileDao())

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _reason = MutableStateFlow("")
    val reason: StateFlow<String> = _reason.asStateFlow()

    private val _startDate = MutableStateFlow<Long?>(null)
    val startDate: StateFlow<Long?> = _startDate.asStateFlow()

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            val profile = repo.getProfileOnce()
            if (profile != null) {
                _name.value = profile.name
                _reason.value = profile.reason
                _startDate.value = profile.startDate
            }
        }
    }

    fun updateName(value: String) { _name.value = value }
    fun updateReason(value: String) { _reason.value = value }

    fun updateStartDate(timestamp: Long) {
        _startDate.value = timestamp
    }

    fun save() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.saveProfile(
                UserProfile(
                    id = 1,
                    name = _name.value,
                    reason = _reason.value,
                    startDate = _startDate.value
                )
            )
            _saved.value = true
        }
    }
}
