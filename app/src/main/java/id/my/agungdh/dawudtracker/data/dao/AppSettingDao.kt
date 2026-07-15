package id.my.agungdh.dawudtracker.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.my.agungdh.dawudtracker.data.entity.AppSetting
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSettingDao {

    @Upsert
    suspend fun upsert(setting: AppSetting)

    @Query("SELECT * FROM app_settings WHERE `key` = :key")
    suspend fun get(key: String): AppSetting?

    @Query("SELECT * FROM app_settings WHERE `key` = :key")
    fun getFlow(key: String): Flow<AppSetting?>
}
