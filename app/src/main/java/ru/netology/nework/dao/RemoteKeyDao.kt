package ru.netology.nework.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nework.entity.RemoteKeyEntity

@Dao
interface RemoteKeyDao {

    @Query("SELECT COUNT(*) == 0 FROM RemoteKeyEntity")
    suspend fun isEmpty(): Boolean

    @Query("SELECT MAX(id) FROM RemoteKeyEntity")
    suspend fun max(): Long?

    @Query("SELECT MIN(id) FROM RemoteKeyEntity")
    suspend fun min(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(key: RemoteKeyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keys: List<RemoteKeyEntity>)

    @Query("DELETE FROM RemoteKeyEntity")
    suspend fun removeAll()
}