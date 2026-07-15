package id.my.agungdh.dawudtracker.data.converter

import androidx.room.TypeConverter
import id.my.agungdh.dawudtracker.data.entity.FastingStatus

class Converters {
    @TypeConverter
    fun fromFastingStatus(status: FastingStatus): String = status.name

    @TypeConverter
    fun toFastingStatus(value: String): FastingStatus = FastingStatus.valueOf(value)
}
