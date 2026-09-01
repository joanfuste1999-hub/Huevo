package com.huevo.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckInDao {
    @Insert
    suspend fun insert(entity: CheckInEntity)

    @Query("SELECT * FROM check_ins ORDER BY epochDay DESC")
    fun observeAll(): Flow<List<CheckInEntity>>

    @Query("SELECT * FROM check_ins ORDER BY epochDay DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<CheckInEntity>>

    @Query("DELETE FROM check_ins")
    suspend fun clearAll()
}

@Dao
interface CravingDao {
    @Insert
    suspend fun insert(entity: CravingEntity)

    @Query("SELECT * FROM cravings ORDER BY timestampMillis DESC")
    fun observeAll(): Flow<List<CravingEntity>>

    @Query("DELETE FROM cravings")
    suspend fun clearAll()
}
