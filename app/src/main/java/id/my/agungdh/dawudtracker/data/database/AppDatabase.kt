package id.my.agungdh.dawudtracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import id.my.agungdh.dawudtracker.data.converter.Converters
import id.my.agungdh.dawudtracker.data.dao.AppSettingDao
import id.my.agungdh.dawudtracker.data.dao.FastingDayDao
import id.my.agungdh.dawudtracker.data.dao.UserProfileDao
import id.my.agungdh.dawudtracker.data.entity.AppSetting
import id.my.agungdh.dawudtracker.data.entity.FastingDay
import id.my.agungdh.dawudtracker.data.entity.UserProfile

@Database(
    entities = [FastingDay::class, UserProfile::class, AppSetting::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun fastingDayDao(): FastingDayDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun appSettingDao(): AppSettingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dawud_tracker.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
