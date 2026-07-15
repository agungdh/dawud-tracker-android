package id.my.agungdh.dawudtracker.data.repository

import id.my.agungdh.dawudtracker.data.entity.UserProfile
import kotlinx.coroutines.flow.Flow

class UserProfileRepository(private val dao: id.my.agungdh.dawudtracker.data.dao.UserProfileDao) {

    fun getProfile(): Flow<UserProfile?> = dao.get()

    suspend fun getProfileOnce(): UserProfile? = dao.getOnce()

    suspend fun saveProfile(profile: UserProfile) {
        dao.upsert(profile.copy(id = 1))
    }
}
