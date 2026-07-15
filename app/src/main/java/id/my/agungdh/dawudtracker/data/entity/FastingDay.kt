package id.my.agungdh.dawudtracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fasting_days")
data class FastingDay(
    @PrimaryKey
    val date: Long,
    val status: FastingStatus
)

enum class FastingStatus {
    FASTED,
    NOT_FASTED
}
