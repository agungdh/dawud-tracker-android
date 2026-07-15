package id.my.agungdh.dawudtracker.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import id.my.agungdh.dawudtracker.data.entity.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Upsert
    suspend fun upsert(profile: UserProfile)

    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun get(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getOnce(): UserProfile?
}
