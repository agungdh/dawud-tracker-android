package id.my.agungdh.dawudtracker.data.repository

import id.my.agungdh.dawudtracker.data.entity.AppSetting
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val dao: id.my.agungdh.dawudtracker.data.dao.AppSettingDao) {

    fun getSetting(key: String): Flow<AppSetting?> = dao.getFlow(key)

    suspend fun getSettingOnce(key: String): AppSetting? = dao.get(key)

    suspend fun setSetting(key: String, value: String) {
        dao.upsert(AppSetting(key = key, value = value))
    }

    companion object {
        const val KEY_NOTIFICATION_HOUR = "notification_hour"
        const val KEY_NOTIFICATION_MINUTE = "notification_minute"
        const val DEFAULT_NOTIFICATION_HOUR = 20
        const val DEFAULT_NOTIFICATION_MINUTE = 0
    }
}
