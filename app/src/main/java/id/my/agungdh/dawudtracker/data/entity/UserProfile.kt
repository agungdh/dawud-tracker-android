package id.my.agungdh.dawudtracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Int = 1,
    val name: String = "",
    val reason: String = "",
    val startDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
