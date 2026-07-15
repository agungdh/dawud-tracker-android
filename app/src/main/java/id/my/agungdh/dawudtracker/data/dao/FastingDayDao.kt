package id.my.agungdh.dawudtracker.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.my.agungdh.dawudtracker.data.entity.FastingDay
import kotlinx.coroutines.flow.Flow

@Dao
interface FastingDayDao {

    @Upsert
    suspend fun upsert(day: FastingDay)

    @Query("SELECT * FROM fasting_days WHERE date = :date")
    suspend fun getByDate(date: Long): FastingDay?

    @Query("SELECT * FROM fasting_days ORDER BY date DESC")
    fun getAll(): Flow<List<FastingDay>>

    @Query("SELECT * FROM fasting_days WHERE date BETWEEN :start AND :end ORDER BY date ASC")
    fun getByDateRange(start: Long, end: Long): Flow<List<FastingDay>>

    @Query("SELECT COUNT(*) FROM fasting_days WHERE status = 'FASTED'")
    fun getTotalFasted(): Flow<Int>

    @Query("SELECT COUNT(*) FROM fasting_days WHERE status = 'FASTED' AND date BETWEEN :start AND :end")
    suspend fun getMonthlyFastedCount(start: Long, end: Long): Int

    @Query("SELECT * FROM fasting_days ORDER BY date ASC")
    suspend fun getAllSorted(): List<FastingDay>

    @Query("DELETE FROM fasting_days WHERE date = :date")
    suspend fun delete(date: Long)
}
