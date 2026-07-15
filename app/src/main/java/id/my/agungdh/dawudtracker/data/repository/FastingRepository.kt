package id.my.agungdh.dawudtracker.data.repository

import id.my.agungdh.dawudtracker.data.entity.FastingDay
import id.my.agungdh.dawudtracker.data.entity.FastingStatus
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class FastingRepository(private val dao: id.my.agungdh.dawudtracker.data.dao.FastingDayDao) {

    suspend fun markDay(date: Long, status: FastingStatus) {
        dao.upsert(FastingDay(date = startOfDay(date), status = status))
    }

    suspend fun deleteDay(date: Long) {
        dao.delete(startOfDay(date))
    }

    suspend fun getByDate(date: Long): FastingDay? = dao.getByDate(startOfDay(date))

    fun getAll(): Flow<List<FastingDay>> = dao.getAll()

    fun getByDateRange(start: Long, end: Long): Flow<List<FastingDay>> =
        dao.getByDateRange(startOfDay(start), startOfDay(end))

    fun getTotalFasted(): Flow<Int> = dao.getTotalFasted()

    suspend fun getMonthlyFastedCount(start: Long, end: Long): Int =
        dao.getMonthlyFastedCount(startOfDay(start), startOfDay(end))

    suspend fun getLongestStreak(): Int {
        val days = dao.getAllSorted()
        if (days.isEmpty()) return 0

        var maxStreak = 0
        var currentStreak = 0
        var lastDate = 0L
        val dayMs = TimeUnit.DAYS.toMillis(1)

        for (day in days) {
            if (day.status == FastingStatus.FASTED) {
                if (currentStreak == 0 || (day.date - lastDate) == dayMs) {
                    currentStreak++
                } else {
                    currentStreak = 1
                }
                lastDate = day.date
                if (currentStreak > maxStreak) {
                    maxStreak = currentStreak
                }
            } else {
                currentStreak = 0
            }
        }
        return maxStreak
    }

    private fun startOfDay(timestamp: Long): Long {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.timeInMillis = timestamp
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
